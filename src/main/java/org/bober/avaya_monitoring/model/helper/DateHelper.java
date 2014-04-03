package org.bober.avaya_monitoring.model.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class with methods for work with date
 */
public class DateHelper {

    private static final SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat MY_JS_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    public static String dateToSqlFormat(Date date){
        return SQL_DATE_FORMAT.format(date);
    }

    public static String getCurrentDateInSqlFormat(){
        Date currentDate = new Date();

        return dateToSqlFormat(currentDate);
    }

    /**
     *  Parse date from sql dataTime format to Data
     *  source string example - '2014-02-13 00:00:00'
     */
    public static Date dateParseFromSqlFormat(String sqlDate) throws ParseException {
        /* if string '2014-02-13 00:00' then change it to '2014-02-13 00:00:00'*/
        if (sqlDate.split(" ")[1].split(":").length==2){
            sqlDate +=":00";
        }

        return SQL_DATE_FORMAT.parse(sqlDate);
    }

    /**
     *  Parse date from my string format to Data
     *  source string example - '2014-02-13_00.00.00'
     */
    public static Date dateParseFromMyJsFormat(String jsDate) throws ParseException {
        /* if string '2014-02-13_00.00' then change it to '2014-02-13_00.00.00'*/

        if (jsDate.split("_")[1].length()==5){
            jsDate +=".00";
        }

        return MY_JS_DATE_FORMAT.parse(jsDate);
    }
}
