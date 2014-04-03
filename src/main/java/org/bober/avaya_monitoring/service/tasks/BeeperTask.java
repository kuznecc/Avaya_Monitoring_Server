package org.bober.avaya_monitoring.service.tasks;

import org.bober.avaya_monitoring.model.entity.CheckResult;
import org.bober.avaya_monitoring.model.entity.Server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This task only for testing
 * After create new instance you must set up id of this beeper.
 * Then will be created dummy Server object with name-Beeper and ip=id
 *
 * You can set up sleeping time.
 *
 * Started task sleep and return 1.
 */
public class BeeperTask extends AbstractTask {

    {
        /* turn off fields checking and saving result to DB */
        enableCheckImportantFields = false;
        enableSaveTaskResultToDB = false;

        setMeasurementUnit("ok");
    }

    @Override
    public String getDescription(){
        return String.format(
                "проверочный таск"
        );
    }

    public void setId(int id) {
        setMonitoredEntity(new Server("Beeper", "" + id));
    }


    @Override
    protected List<CheckResult> childTaskRunLogic() {
        return new ArrayList<CheckResult>(){{
            add(new CheckResult(){{
                setDate( new Date() );
                setEntityId(1);
                setValue(1);
                setMeasurementUnit("beep");
            }});
        }};
    }

}
