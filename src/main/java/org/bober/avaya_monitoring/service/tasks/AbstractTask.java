package org.bober.avaya_monitoring.service.tasks;

import org.bober.avaya_monitoring.model.helper.DateHelper;
import org.bober.avaya_monitoring.model.dao.iCheckResultDao;
import org.bober.avaya_monitoring.model.dao.impl.EntityServerDaoJdbc;
import org.bober.avaya_monitoring.model.entity.AbstractMonitoredEntity;
import org.bober.avaya_monitoring.model.entity.CheckConfig;
import org.bober.avaya_monitoring.model.entity.CheckResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract class for all tasks.
 * Consist all common logic and methods
 */
public abstract class AbstractTask implements Runnable {

    /**
     * Default frequency execution of all tasks.
     * Can be used like a default value in CheckConfiguration instances
     */
    public static final int DEFAULT_FREQUENCY_OF_TASK_EXECUTION = 60000; // 10min in ms.

    /* If in run() method checking important fields is not required than set to false */
    protected boolean enableCheckImportantFields = true;

    /* If in run() method saving task result in the DB is not required than set to false */
    protected boolean enableSaveTaskResultToDB = true;


    Logger logger = LoggerFactory.getLogger("avayaMonTask");

    protected void logAnError(String message) {
        logAnError(message,null);
    }
    protected void logAnError(String message, Exception e) {
        logger.error(String.format("%s Error - %s - %s - %s",
                this.getClass().getSimpleName(),
                getMonitoredEntity().getPrepareName(),
                message,
                (e!=null) ? e.getClass().getName() : ""
        ));

        if (e!=null)
            System.err.println(e);
    }


    /**
     * This method return description of child task.
     * (task goals and common logic)
     */
    public abstract String getDescription();


    public AbstractTask() {
        taskCreationTime = new Date();
    }


    private int period = 1000;

    public int getPeriod() {
        return period;
    }

    /**
     * Set execution frequency of this task instance
     * @param period in microseconds(us)
     */
    public void setPeriod(int period) {
        this.period = period;
    }


    private String measurementUnit = "measurementUnitNotDefined".intern();

    /**
     * This method set measurement unit of specified task type.
     * Method must be used in initializer block of child class
     * @param measurementUnits abbr (for example : ms,%,units...)
     */
    protected void setMeasurementUnit(String measurementUnits) {
        this.measurementUnit = measurementUnits;
    }

    public String getMeasurementUnit() {
        return measurementUnit;
    }


    private iCheckResultDao checkResultDao;

    /**
     * This property CheckResultDao unique for all child task and
     * must bu applied in the Spring context file
     * @param checkResultDao - specified dao-bean
     */
    public void setCheckResultDao(iCheckResultDao checkResultDao) {
        this.checkResultDao = checkResultDao;
    }

    public iCheckResultDao getCheckResultDao() {
        return checkResultDao;
    }


    /* autowire EntityServerDaoJdbc bean for access to servers table */
    @Resource(name = "serverDao")
    private EntityServerDaoJdbc serverDao;

    protected EntityServerDaoJdbc getServerDao() {
        return serverDao;
    }


    private AbstractMonitoredEntity monitoredEntity;

    /**
     * Set monitored entity object that will be checked in specified task.
     * @param monitoredEntity AbstractMonitoredEntity instance
     */
    protected void setMonitoredEntity(AbstractMonitoredEntity monitoredEntity) {
        this.monitoredEntity = monitoredEntity;
    }

    public AbstractMonitoredEntity getMonitoredEntity() {
        return monitoredEntity;
    }

    /**
     * If this task currently disabled the it shell not run
     */
    private boolean disabled;

    public boolean isDisabled() {
        return (monitoredEntity.isDeleted() || disabled);
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    protected CheckConfig checkConfig;

    public CheckConfig getCheckConfig() {
        return checkConfig;
    }

    /**
     * This method receive checkConfig, load needed monitoredEntity from specified DAO-instance
     * and set in to this task instance fields.
     *
     * <p/>
     * This method can be Override, if it need.
     * For tasks, where checkConfig keep entityId of another entity-type (for example: AvayaParameter) 
     * we must override this method and use specified dao.
     *
     * @param checkConfig - configuration of this task
     */
    public void setCheckCfg(CheckConfig checkConfig) {
        this.checkConfig = checkConfig;
        monitoredEntity = serverDao.get(checkConfig.getEntityId());
        setPeriod(checkConfig.getFrequency());
        disabled = checkConfig.isDisabled();
    }


    @Override
    public final void run() {
        try {
            /* If this task is disabled */
            if (isDisabled()) {
                return;
            }
            /* Checking important fields that they assigned right */
            if (enableCheckImportantFields && !isImportantFieldsAssignedRight()) {
                return;
            }

            newIterationInvoked();

            /* Receive result from child task implementation */
            lastIterationResult = childTaskRunLogic();

            /* Save child task result to the DB*/
            if (enableSaveTaskResultToDB && lastIterationResult != null) {
                saveResultToDb(lastIterationResult);
            }

            runFinishingOperations(lastIterationResult);
        } catch (Exception e) {
            logAnError("Can't execute right run() method.", e);
        }
    }



    /**
     * Verification method that checking data presence in important class fields which
     * used in the main task logic
     */
    protected boolean isImportantFieldsAssignedRight() {
        if (getMonitoredEntity() == null || getCheckResultDao() == null || getServerDao() == null) {
            StringBuilder sb = new StringBuilder("Important variable is null! (");
            if (getMonitoredEntity() == null) sb.append("getMonitoredEntity() == null;");
            if (getCheckResultDao() == null) sb.append("getCheckResultDao() == null;");
            if (getServerDao() == null) sb.append("getServerDao() == null;");
            sb.append(")");

            logger.error(sb.toString());
            return false;
        }
        return true;
    }

    /**
     * This method save to DB received result list by injected DAO
     *
     * @param resultList prepared list of CheckResult
     */
    private void saveResultToDb(List<CheckResult> resultList) {
        if (resultList == null) throw new IllegalArgumentException("resultList can't be null");

        setLastIterationEndingTimeAndDuration();

        try {
            getCheckResultDao().create(resultList);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            /* throw when we try save to DB not unique CheckResult object.
            * For example db-table 'avaya_parameter_telnet_bcms_vdn' configured
            * for unique column set : date + entityId + attributes
            * */
            logger.warn(String.format("%s Warning - %s - saveResultToDb() - %s",
                    this.getClass().getSimpleName(),
                    getMonitoredEntity().getPrepareName(),
                    e.getClass().getName()
            ));
        } catch (Exception e) {
            logAnError("saveResultToDb()", e);
        }
        logger.debug("Saved to DB");
    }

    /**
     * This method must be executed at the last time in the run() method
     * for logging or something else.
     *
     * @param resultList list of CheckResult
     */
    private void runFinishingOperations(List<CheckResult> resultList) {

        setLastIterationEndingTimeAndDuration();
        logger.info(
                String.format(
                        "%-30s - %-30s = %-10s (execution time %s ms)",
                        this.getClass().getSimpleName(),
                        getMonitoredEntity().getPrepareName(),
                        (resultList == null)
                                ? "null"
                                : (resultList.size() == 1)
                                ? resultList.get(0).getValue() + " " + getMeasurementUnit()
                                : resultList.size() + " values",
                        getLastIterationOfTaskDuration()
                )
        );
    }


    /**
     * This method consist specific logic of child task
     *
     * @return prepared list of CheckResult
     */
    abstract protected List<CheckResult> childTaskRunLogic();


    /*
    *           ===============================================
    *           ==== Helper methods for child task classes ====
    *           ===============================================
    */

    /**
     * Helper method receive int value, and create list of CheckResult with one entry
     *
     * @param value - result of child task execution
     * @return list of CheckResults with one entry
     */
    protected List<CheckResult> getTaskResultList(Integer value) {
        Map<AbstractMonitoredEntity, Integer> resultMap = new HashMap<>();
        resultMap.put(getMonitoredEntity(), value);
        return getTaskResultList(resultMap);
    }

    /**
     * Helper method receive map of monitoredEntity-value, and create list of CheckResult
     *
     * @param resultMap - map of monitoredEntity-value pairs from child task
     * @return list of CheckResults with entry's for each resultMap pair
     */
    protected List<CheckResult> getTaskResultList(Map<AbstractMonitoredEntity, Integer> resultMap) {
        List<CheckResult> checkResultList = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date now = new Date(cal.getTimeInMillis());

        for (AbstractMonitoredEntity monitoredEntity : resultMap.keySet()) {
            CheckResult result =
                    new CheckResult()
                            .setEntityId(monitoredEntity.getId())
                            .setDate(now)
                            .setEntity(monitoredEntity)
                            .setValue(resultMap.get(monitoredEntity));

            checkResultList.add(result);
        }

        return checkResultList;
    }

    /**
     * Finding regExp groups in received telnet output. Method return list of strings from regExp groups.
     *
     * @param telnetOutput - telnet output which we need to parse
     * @param regExp       - regExp, that must have least 1 group
     * @param groupsCount  - count of regExp groups, that will be parsed
     * @return list of founded groups
     */
    protected List<String> parseTelnetOutputByRegexp(StringBuilder telnetOutput, String regExp, int groupsCount) {
        List<String> result = new ArrayList<>();

        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(telnetOutput);

        if (telnetOutput == null || regExp == null || regExp.isEmpty() || groupsCount <= 0
                || !matcher.find())
            return result;

        for (int i = 1; i <= groupsCount; i++) {
            result.add(matcher.group(i));
        }

        return result;
    }

    /**
     * Overloaded method that you can use when you need to parse only one regExp group
     *
     * @param telnetOutput - telnet output which we need to parse
     * @param regExp       - regExp, that must have least 1 group
     * @return list of founded groups
     */
    protected List<String> parseTelnetOutputByRegexp(StringBuilder telnetOutput, String regExp) {
        return parseTelnetOutputByRegexp(telnetOutput, regExp, 1);
    }

    /**
     * Method prepare CheckResult instance object by received telnet output string and regExp with ine group.
     *
     * @param date - date of check
     * @param entityId - entityId of monitored entity
     * @param telnetOutput - telnet output which we need to parse
     * @param regExpWithOneGroup - regExp with one group, that will be used like a CheckResult value
     * @return prepared object
     */
    protected CheckResult getResultForId(final Date date, final int entityId, String regExpWithOneGroup,
                                         StringBuilder telnetOutput) {
        Pattern pattern = Pattern.compile(regExpWithOneGroup);
        final Matcher matcher = pattern.matcher(telnetOutput);
        return
                new CheckResult(
                        date,
                        entityId,
                        ((matcher.find()) ? Integer.parseInt(matcher.group(1)) : null)
                );
    }

    /**
     * Return list with all matches
     *
     * @param str - string that will be parsed
     * @param regExpt - pattern
     * @return - list of all matches
     */
    protected List<String> getAllMatches (String str, String regExpt){
        List<String> allMatches = new ArrayList<String>();

        Matcher m = Pattern.compile(regExpt).matcher(str);

        while (m.find()) {
            allMatches.add(m.group());
        }

        return allMatches;
    }


    /*
    *           ======================================
    *           ==== Maintenance methods & fields ====
    *           ======================================
    */

    /**
     * When this task was created
     */
    private Date taskCreationTime;

    public Date getTaskCreationTime() {
        return taskCreationTime;
    }

    /**
     * ScheduledFuture instance from taskPool class
     */
    private ScheduledFuture future;

    public ScheduledFuture getFuture() {
        return future;
    }

    public void setFuture(ScheduledFuture future) {
        this.future = future;
    }

    /**
     * How many times this task was executed
     */
    private long iterationsCount;
    /**
     * Time when the last iteration of task was started
     */
    private Date lastIterationStartTime = null;
    /**
     * Time when the last iteration of task was ended
     */
    private Date lastIterationEndTime = null;
    /**
     * Duration of the last iteration of task
     */
    private Integer lastIterationDuration = null; //milli seconds
    /**
     * Result of last iteration of the task
     */
    private List<CheckResult> lastIterationResult = null;

    private void newIterationInvoked() {
        iterationsCount++;
        lastIterationStartTime = new Date();
        lastIterationEndTime = null;
        lastIterationDuration = null;
    }

    public long getIterationsCount() {
        return iterationsCount;
    }

    public Date getLastIterationStartTime() {
        return lastIterationStartTime;
    }

    protected void setLastIterationEndingTimeAndDuration() {
        lastIterationEndTime = new Date();
        lastIterationDuration = (int) (lastIterationEndTime.getTime() - lastIterationStartTime.getTime());
    }

    public Date getLastIterationEndTime() {
        return lastIterationEndTime;
    }

    public Integer getLastIterationOfTaskDuration() {
        return lastIterationDuration;
    }

    public List<CheckResult> getLastIterationResult() {
        return lastIterationResult;
    }

    /**
     * Return string with json-formatted data about task parameters and state
     * That need for statistics.
     *
     * @return string with task-status
     */
    public String getTaskStatus() {
        StringBuilder sb = new StringBuilder();

        String lResult =
                (getLastIterationResult() == null || getLastIterationResult().isEmpty())
                        ? "-null-"
                        : (getLastIterationResult().size() == 1)
                        ? getLastIterationResult().get(0).getValue() + " " + getMeasurementUnit()
                        : getLastIterationResult().size() + " values";

        String taskAttr = getCheckConfig().getAttributes();
        if (taskAttr == null || taskAttr.isEmpty()) {
            taskAttr = "";
        }

        return String.format(
                "TasksStatus{Task=%s(%s%s), period=%s, counter=%s, lastStart=%s" +
                        ", lastEnd=%s, lDuration=%s, lResult=%s" +
                        ", isDisabled=%s, isDone=%s, IsCanceled=%s}"
                , getClass().getSimpleName()
                , getMonitoredEntity().getName()
                , taskAttr
                , getPeriod()
                , getIterationsCount()
                , getObjectToString(getLastIterationStartTime())
                , getObjectToString(getLastIterationEndTime())
                , getObjectToString(getLastIterationOfTaskDuration())
                , lResult
                , isDisabled()
                , getFuture().isDone()
                , getFuture().isCancelled()
        );
    }

    private String getObjectToString(Object val) {
        if (val == null) {
            return "-";
        }
        if (val instanceof Date) {
            return DateHelper.dateToSqlFormat((Date) val);
        }
        return val.toString();
    }
}
