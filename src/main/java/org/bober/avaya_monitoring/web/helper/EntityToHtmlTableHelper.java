package org.bober.avaya_monitoring.web.helper;


import org.bober.avaya_monitoring.model.dao.iMonitoredEntityDao;
import org.bober.avaya_monitoring.model.entity.AbstractEntity;
import org.bober.avaya_monitoring.model.entity.AbstractMonitoredEntity;
import org.bober.avaya_monitoring.model.entity.CheckConfig;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class create html-table for any project entity.
 * Functional based on .toString method of entity object
 */
public class EntityToHtmlTableHelper {

    public static final String CFG_ROW_CELL_ID_ID = "cfgId";
    public static final String CFG_ROW_CELL_ID_ENTITY_ID = "cfgEntityIdDD";
    public static final String CFG_ROW_CELL_ID_ATTRIBUTES = "cfgAttributes";
    public static final String CFG_ROW_CELL_ID_FREQUENCY = "cfgFrequency";
    public static final String CFG_ROW_CELL_ID_ISDISABLED = "isDisabled";
    public static final String CFG_ROW_CELL_ID_DESCRIPTION = "cfgDescription";
    private final String simpleCellPattern = "<td>%s</td>";
    private final String tableRowPattern = "<tr id=\"%s\">";
    /* rowID must be compose from a entity.getDbTableName() and entity.getId() */
    private final String rowIdPattern = "%s___%s";

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

    /**
     * Method return html-code for one row of table with received cell values.
     * Every cell consist 'plain text' with obtained value.
     *
     * @param data list element fields values
     * @param rowId id of prepared row element. If rowId<0 then row doesn't have id property.
     * @return string with html-code of table row with obtained values
     */
    private String stringsToHtmlTableRow(List<String> data, int rowId) {
        StringBuilder result = new StringBuilder();

        result.append(
                (rowId<0)?String.format(tableRowPattern,rowId) :"<tr>"
        );

        for (String s : data) {
            result.append(String.format(simpleCellPattern , s ));
        }

        result.append("</tr>");

        return result.toString();
    }

    private String stringsToHtmlTableRow(List<String> data) {
        return stringsToHtmlTableRow(data, -1);
    }




    /**
     * Method return html-code for CheckBox element
     *
     * @param checkBoxId id of the new checkBox element
     * @param value current state
     * @return html-code of checkBox
     */
    private String getHtmlCheckBox(String checkBoxId, boolean value){
        String cellPattern = "<input id=\"%s\" type=\"checkbox\" %s/>";

        return String.format(cellPattern, checkBoxId, (value) ? "checked" : "");
    }

    /**
     * Method return html-code of Drop Down List that consist obtained elements
     *
     * @param dropDownListId id of new drop down list element
     * @param optValueList values list for all list options
     * @param optNameList names list for all list options
     * @param selectedId id of preselected option
     * @return html-code of drop down list
     */
    private String getHtmlDropDownList(String dropDownListId,
                                       List<Integer> optValueList,
                                       List<String> optNameList,
                                       int selectedId){
        StringBuilder result = new StringBuilder();

        if (dropDownListId==null || optValueList ==null || optNameList ==null
                || optValueList.size()!= optNameList.size())
            return "";

//      Example:  <select> <option value="saab" selected>Saab</option> </select>

        result.append(String.format("<select id=\"%s\">",dropDownListId));

        String optionPattern = "<option value=\"%s\" %s>%s</option>";

        int ddListSize = optValueList.size();

        for (int i=0; i<ddListSize; i++){
            final Integer optVal = optValueList.get(i);
            result.append(
                    String.format(
                            optionPattern,
                            optVal, (optVal == selectedId) ? "selected" : "", optNameList.get(i)
                    )
            );
        }

        result.append("</select>");

        return result.toString();
    }

    /**
     * Method return html-code of Drop Down List that consist obtained MonitoredEntity instances
     *
     * @param dropDownListId id of new drop down list element
     * @param monitoredEntityList list of all monitored entities
     * @param selectedEntityId id of preselected monitoredEntity
     * @return html-code of drop down list
     */
    public String getHtmlDropDownList(String dropDownListId,
                                       List<AbstractMonitoredEntity> monitoredEntityList,
                                       int selectedEntityId){
        if (monitoredEntityList==null) return "";
        List<Integer> optValueList = new ArrayList<>();
        List<String> optNameList = new ArrayList<>();
        for (AbstractMonitoredEntity entity : monitoredEntityList) {
            optValueList.add(entity.getId());
            optNameList.add(entity.getPrepareName());
        }
        return getHtmlDropDownList( dropDownListId, optValueList, optNameList, selectedEntityId);
    }


    /**
     * Method return html-code for one row of editable cells for obtained CheckConfig element.
     * Every cell consist '<input type="text" ... />' with obtained value than can be changed.
     *
     * @param checkConfig element that must be represented
     * @return string with html-code of table row with editable cells.
     */
    private String checkConfigToHtmlTableEditableRow(CheckConfig checkConfig,
                                                     List<AbstractMonitoredEntity> listOfAllMonitoredEntities,
                                                     String saveBtnOnClickLogic,
                                                     String cancelBtnOnClickLogic) {
        StringBuilder result = new StringBuilder();

        final String rowId = getRowId(checkConfig);

        result.append(
                String.format(tableRowPattern, rowId)
        );

        // <input type="text" size="10" value="Enter your name here!">
        final String
                editableCellPattern = "<td><input id=\"%s\" type=\"text\" size=\"%d\" value=\"%s\"></td>",
                cellPattern = "<td id=\"%s\">%s</td>",
                btnSaveId = "btnSaveEditRow",
                btnCancelId = "btnCancelEditRow",
                cellWithButtons = "<td><button id='" + btnSaveId + "'>save</button>" +
                        "<button id='" + btnCancelId + "'>cancel</button></td>",
                scriptTagWithButtonsOnClickLogic = "<script type=\"text/javascript\">" +
                        "$('#" + btnSaveId + "').click(function(){%s});$('#" + btnCancelId+"').click(function(){%s});" +
                        "</script>";

        result.append(String.format(cellPattern, CFG_ROW_CELL_ID_ID, checkConfig.getId() ));

        result.append(String.format(simpleCellPattern ,
                getHtmlDropDownList(CFG_ROW_CELL_ID_ENTITY_ID, listOfAllMonitoredEntities, checkConfig.getEntityId())
        ));

        result.append(String.format(editableCellPattern , CFG_ROW_CELL_ID_ATTRIBUTES, 10,
                (checkConfig.getAttributes()==null)?"":checkConfig.getAttributes()
        ));

        result.append(String.format(editableCellPattern , CFG_ROW_CELL_ID_FREQUENCY, 10,
                checkConfig.getFrequency()
        ));

        result.append(String.format(simpleCellPattern,
                        getHtmlCheckBox(CFG_ROW_CELL_ID_ISDISABLED, checkConfig.isDisabled()))
        );

        result.append(String.format(editableCellPattern , CFG_ROW_CELL_ID_DESCRIPTION, 45,
                (checkConfig.getDescription()==null)?"":checkConfig.getDescription()
        ));

        result.append(cellWithButtons);

        result.append(String.format(scriptTagWithButtonsOnClickLogic, saveBtnOnClickLogic, cancelBtnOnClickLogic));

        result.append("</tr>");

        return result.toString();
    }
    public String checkConfigToHtmlTableEditableRow(CheckConfig checkConfig,
                                                    List<AbstractMonitoredEntity> listOfAllMonitoredEntities) {
        final String
                rowId = getRowId(checkConfig),

                tableName_CheckConfigId = checkConfig.getDbTableName() + "/" + checkConfig.getId(),

                btnCancelLinkUrl = "configuration/getCheckConfigRow/" + tableName_CheckConfigId,

                btnSaveLinkUrlPattern = "configuration/updateCheckConfigEntity/" + tableName_CheckConfigId + "?",

                btnCancelPattern =
                        "replaceTagWithHtml('" + rowId + "', '" + btnCancelLinkUrl + "');" +
                                "enableButtonsInDivWithText('configurationPageLoadedContent' ,'edit');",
                btnSavePattern =
                        "var url= '" + btnSaveLinkUrlPattern + "' +getUrlParamsForEditorRow();" +
//                                "replaceTagWithHtml('" + rowId + "', url);" +
                                "$(this).closest('tr').load(url);"+
                                "enableButtonsInDivWithText('configurationPageLoadedContent' ,'edit');";

        return checkConfigToHtmlTableEditableRow(checkConfig,
                listOfAllMonitoredEntities,
                btnSavePattern,
                btnCancelPattern);
    }

    public String newCheckConfigToHtmlTableEditableRow(CheckConfig checkConfig,
                                                    List<AbstractMonitoredEntity> listOfAllMonitoredEntities) {
        final String
                rowId = getRowId(checkConfig),

                tableName_CheckConfigId = checkConfig.getDbTableName() + "/" + checkConfig.getId(),

                btnSaveLinkUrlPattern = "configuration/createCheckConfigEntity/" + tableName_CheckConfigId + "?",

                btnCancelPattern =  /* remove added edit-row from table and enable all 'edit'-buttons */
                        "$(this).closest('tr').remove();" +
                                "enableButtonsInDivWithText('configurationPageLoadedContent' ,'edit');",
                btnSavePattern =    /* reload table with checkConfigs and enable all 'edit'-buttons */
                        "var createNewCfgUrl = '" + btnSaveLinkUrlPattern + "' +getUrlParamsForEditorRow();"+
                        "$.get(createNewCfgUrl, function( data ) {alert( data );});"+
//                        "var srvResponse = $('" + btnSaveLinkUrlPattern + "' +getUrlParamsForEditorRow() ).responseText;"+
                        "var url = '/configuration/showTable/"+checkConfig.getDbTableName() + "';" +
//                                "alert(srvResponse);"+
                        "loadPage('configurationPageLoadedContent', url);" +
                        "enableButtonsInDivWithText('configurationPageLoadedContent' ,'edit');";
//                btnSavePattern =
//                        "var url= '" + btnSaveLinkUrlPattern + "' +getUrlParamsForEditorRow();" +
////                                "replaceTagWithHtml('" + rowId + "', url);" +
//                                "$(this).closest('tr').load(url);"+
//                                "enableButtonsInDivWithText('configurationPageLoadedContent' ,'edit');";

        return checkConfigToHtmlTableEditableRow(checkConfig,
                listOfAllMonitoredEntities,
                btnSavePattern,
                btnCancelPattern);
    }

    /**
     * Method return html-code for one row  with String content for obtained CheckConfig element.
     * Every cell consist 'plain text'.
     *
     * @param checkConfig element that must be represented
     * @return string with html-code of plain-text table row.
     */
    public String checkConfigToHtmlTableRow(CheckConfig checkConfig) {
        StringBuilder result = new StringBuilder();

        final String rowId = getRowId(checkConfig);

        result.append(String.format(tableRowPattern, rowId));

        final String btnEditLinkUrl = "configuration/getCheckConfigEditableRow/" +
                checkConfig.getDbTableName() + "/" + checkConfig.getId();
        String btnEditPattern =
                "<button onClick=" +
                        "\"replaceTagWithHtml('"+rowId+"', '" + btnEditLinkUrl + "');" +
                        "disableButtonsInDivWithText('configurationPageLoadedContent' ,'edit');" +
                        "\">edit</button>";
                /*"<button onClick=\"replaceTagWithHtml('"+rowId+"', '" + btnEditLinkUrl + "');" +
                        "disableButtonsInDivWithText( \"configurationPageLoadedContent\" ,\"edit\")\">edit</button>";*/

        result.append(String.format(simpleCellPattern, checkConfig.getId() ));

        result.append(String.format(simpleCellPattern, checkConfig.getEntity().getPrepareName() ));

        result.append(String.format(simpleCellPattern, checkConfig.getAttributes() ));

        result.append(String.format(simpleCellPattern, checkConfig.getFrequency() ));

        result.append(String.format(simpleCellPattern, checkConfig.isDisabled() ));

        result.append(String.format(simpleCellPattern, checkConfig.getDescription() ));

        result.append(String.format(simpleCellPattern, btnEditPattern ));

        result.append("</tr>");

        return result.toString();
    }


    public String getHtmlTableForCheckConfigList(List<CheckConfig> checkConfigList,
                                                 List<AbstractMonitoredEntity> listOfAllMonitoredEntities){
        if (checkConfigList==null || checkConfigList.isEmpty()) return "";

        StringBuilder result = new StringBuilder();


        for (CheckConfig checkConfig : checkConfigList) {
            result.append(checkConfigToHtmlTableRow(checkConfig));
//            result.append(checkConfigToHtmlTableEditableRow(checkConfig, listOfAllMonitoredEntities));
        }

        return result.toString();
    }

    private String getRowId(CheckConfig checkConfig) {
        return String.format(rowIdPattern,checkConfig.getDbTableName(), checkConfig.getId());
    }


    /* method for testing */
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
