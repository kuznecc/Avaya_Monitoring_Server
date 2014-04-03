package org.bober.avaya_monitoring.service.tasks.util;

/**
 * Helper class for all loggers
 */
public class LoggerHelper {


    /**
     * return name of class that they invoke this method
     */
    public static String getCurrentClassName() {
        try {
            throw new RuntimeException();
        } catch (Exception e){
            return e.getStackTrace()[1].getClassName();
        }
    }

    public static String getCurrentClassSimpleName() {
        try {
            throw new RuntimeException();
        } catch (Exception e){
            return e.getStackTrace()[1].getClassName();
        }
    }
}
