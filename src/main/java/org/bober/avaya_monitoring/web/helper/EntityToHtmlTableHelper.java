package org.bober.avaya_monitoring.web.helper;


import org.bober.avaya_monitoring.model.dao.iMonitoredEntityDao;
import org.bober.avaya_monitoring.model.entity.AbstractEntity;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class create html-table for any project entity.
 * Functional based on .toString method of entity object
 */
public class EntityToHtmlTableHelper {

    @Resource(name = "serverDao")
    private iMonitoredEntityDao serverDao;


    /* return header for table in html */
    private String getHtmlTableHeader(AbstractEntity entity){
        return getHtmlTableHeader(entity.toString());
    }
    private String getHtmlTableHeader(String entity) {
        if (entity == null) {
            return "";
        }

        // Create header for table. 'Server{' ==> '<H1>Server</H1><br>'
        return "<h2>Table - " + entity.split("\\{")[0] + "</h2>";
    }

    /* return header for table columns in html */
    public String getHtmlTableRowWithColumnHeaders(AbstractEntity entity){
        return getHtmlTableRowWithColumnHeaders(entity.toString());
    }
    public String getHtmlTableRowWithColumnHeaders(String entity) {
        if (entity == null) {
            return "";
        }

        List<String> listOfColumnHeaders = new ArrayList<String>();
        for (String[] property : entityToStringMap(entity)) {
            listOfColumnHeaders.add(property[0]);
        }

        return "<b>"+stringsToHtmlTableRow(listOfColumnHeaders)+"</b>";
    }

    /* return one table row with data from received entity */
    public String getHtmlTableRow(AbstractEntity entity){
        return getHtmlTableRow(entity.toString());
    }
    public String getHtmlTableRow(String entity) {
        if (entity == null) {
            return "";
        }

        List<String> listOfValues = new ArrayList<String>();
        for (String[] property : entityToStringMap(entity)) {
            listOfValues.add(property[1]);
        }

        return stringsToHtmlTableRow(listOfValues);
    }

    /* return list of String pairs that consist 'property name' and 'property value' */
    private List<String[]> entityToStringMap(AbstractEntity entity) {
        return entityToStringMap(entity.toString());
    }
    private List<String[]> entityToStringMap(String entity) {
        /* Example of entity.toString => Entity{"id=" + id + ", date=" + date + '}'; */

        // every property of entity put to the List like array {key,value}
        List<String[]> result = new ArrayList<String[]>();

        /* Remove last symbol of string. In the .toString() output it is a '}' */
        entity = entity.substring(0,entity.length()-1);

        Pattern pattern = Pattern.compile("(\\{|,\\s)(\\w+)=");
        Matcher matcher = pattern.matcher( entity );

        if (matcher.find()){
            String[] values = entity.split("(\\{|,\\s)(\\w+)=");

            for (int i=1;;i++){
                result.add(new String[]{matcher.group(2),values[i]});
                if (!matcher.find()) break;
            }
        }

        return result;
    }

    /* return html-code for one row of table with received strings */
    private String stringsToHtmlTableRow(List<String> data) {
        StringBuilder result = new StringBuilder();

        result.append("<tr>");

        for (String s : data) {
            result.append("<td>");
            result.append(s);
            result.append("</td>");
        }

        result.append("</tr>");

        return result.toString();
    }


    public static void main(String[] args) {
        // CheckConfig{id=2, entityId=5, attributes='98200', frequency=1800000, disabled=false, description='0800506800'}
        String testToString = "CheckConfig{id=2, entityId=5, attributes='98200', frequency=1800000, disabled=false, description='0800506800'}";

        /* Remove last symbol of string. In the .toString() output it is a '}' */
        testToString = testToString.substring(0,testToString.length()-2);

        String regex = "(\\{|,\\s)([\\w]+)=";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher( testToString );

        if (matcher.find()){
            String[] values = testToString.split(regex);
            System.out.println("className : " + values[0]);

            for (int i=1;;i++){
                System.out.println("pair => " + matcher.group(2)+ " = " + values[i]);
                if (!matcher.find()) break;
            }
        }

    }

}
