package org.bober.avaya_monitoring.service;


import org.bober.avaya_monitoring.service.tasks.AbstractTask;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Scheduled task pool can execute periodically received task
 */
public class TaskPool {

    // count of threads that can be used at the same time
    private final int COUNT_OF_THREADS_THAT_CAN_BE_USED = 90;

    private final ScheduledThreadPoolExecutor executor =
            (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(COUNT_OF_THREADS_THAT_CAN_BE_USED);


    /* List of all received tasks and they ScheduledFuture instances */
    private List<AbstractTask> allTaskList = new ArrayList<>();

    public List<AbstractTask> getAllTaskList() {
        return allTaskList;
    }

    /**
     * Adding new task to the executor
     */
    public void add(AbstractTask task) {
        if (task.getMonitoredEntity().isDeleted()) {
            return;
        }

        /*      Schedule received task
           We add rnd initial delay for executing task not in the same time */
        ScheduledFuture scheduledFuture = executor.scheduleAtFixedRate(
                task,
                (long) (Math.random() * 60000),  // initialDelay
                task.getPeriod(),  // period of restarting task
                MILLISECONDS
        );
        task.setFuture(scheduledFuture);
        if (task.isDisabled()) {
            scheduledFuture.cancel(true);
        }
        allTaskList.add(task);

    }



    /*
    *           ======================================
    *           ==== Maintenance methods & fields ====
    *           ======================================
    */

    public void cancelAllTasks() {
        for (AbstractTask task : allTaskList) {
            task.getFuture().cancel(true);
        }

        allTaskList.clear();
        executor.getQueue().clear();
    }

    /**
     * Method return map of important taskpool parameters and his values
     *
     * @return map of pool parameters and values
     */

    public Map<String, String> getStatus() {
        Map<String, String> result = new HashMap<>();

        result.put("getActiveCount()", "" + executor.getActiveCount());
        result.put("getCompletedTaskCount()", "" + executor.getCompletedTaskCount());
        result.put("getCorePoolSize()", "" + executor.getCorePoolSize());
        result.put("getLargestPoolSize()", "" + executor.getLargestPoolSize());
        result.put("getMaximumPoolSize()", "" + executor.getMaximumPoolSize());
        result.put("getPoolSize()", "" + executor.getPoolSize());
        result.put("getTaskCount()", "" + executor.getTaskCount());
        result.put("isShutdown()", "" + executor.isShutdown());
        result.put("isTerminated()", "" + executor.isTerminated());
        result.put("isTerminating()", "" + executor.isTerminating());
        result.put(".getQueue()", executor.getQueue().toString());

        return result;
    }

    /**
     * Return status of all scheduled tasks
     * @return list of scheduled task statuses
     */
    public List<String> getTasksStatus() {
        Map<String, AbstractTask> sortedTaskMap = new TreeMap<>();
        List<String> result = new ArrayList<>();

        /* create sorted set by name&entity of all tasks */
        for (AbstractTask task : allTaskList) {
            String key =
                    String.format("%s%s%s",
                            task.getClass().getSimpleName(),
                            task.getMonitoredEntity().getName(),
                            task.getCheckConfig().getAttributes());

            sortedTaskMap.put(key, task);
        }

        for (String key : sortedTaskMap.keySet()) {
            result.add(sortedTaskMap.get(key).getTaskStatus());
        }


        return result;
    }

    /**
     * Shutdown pool
     */
    public void shutdown() {
        executor.shutdown();
    }

    public void purge() {
        executor.purge();
    }

}
