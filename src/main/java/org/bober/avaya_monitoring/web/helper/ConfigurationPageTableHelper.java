package org.bober.avaya_monitoring.web.helper;

import org.bober.avaya_monitoring.model.entity.*;

import java.util.ArrayList;
import java.util.List;

import static org.bober.avaya_monitoring.web.helper.UrlHelper.*;

/**
 * This helper class consist for constructing and modification
 * html-table with received entities on the Configuration Page
 */
public class ConfigurationPageTableHelper {

    public static final String
            URL_GET_TABLE_PATTERN = getUrlPattern(URL_CONFIG_PAGE_ROOT + URL_CONFIG_PAGE_GET_TABLE),
            URL_GET_VIEW_ROW_PATTERN = getUrlPattern(URL_CONFIG_PAGE_ROOT + URL_CONFIG_PAGE_GET_VIEW_ROW),
            URL_GET_EDIT_ROW_PATTERN = getUrlPattern(URL_CONFIG_PAGE_ROOT + URL_CONFIG_PAGE_GET_EDIT_ROW),
            URL_ADD_NEW_ROW_PATTERN = getUrlPattern(URL_CONFIG_PAGE_ROOT + URL_CONFIG_PAGE_ADD_NEW_ROW),

            PARENT_DIV_ID = CONFIGURATION_PAGE_DIV_LOADED_CONTENT_ID,

            TR_ID_NEW_ROW = "newRowId",
            TD_ID_ID = "cfgId",
            TD_ID_ENTITY_ID = "cfgEntityIdDD",
            TD_ID_ATTRIBUTES = "cfgAttributes",
            TD_ID_FREQUENCY = "cfgFrequency",
            TD_ID_DISABLED = "isDisabled",
            TD_ID_DELETED = "isDeleted",
            TD_ID_DESCRIPTION = "cfgDescription",
            TD_ID_NAME = "monEntityName",
            TD_ID_SRV_IP = "srvIp",
            TD_ID_SRV_SNMP_COMMUNITY = "srvSnmpCommunity",
            TD_ID_SRV_OS_TYPE = "srvOsType",
            TD_ID_SERVER_ID = "serverId",
            TD_ID_SUBSYSTEM = "subsystem",

            TD_PATTERN = "<td id=%s>%s</td>",
            TD_PATTERN_SIMPLE = "<td>%s</td>",
            TD_PATTERN_SIMPLE_RED = "<td><font color=\"red\">%s</font></td>",
            TD_PATTERN_INPUT_TEXT_EDIT = "<td><input id=\"%s\" type=\"text\" size=\"%d\" value=\"%s\"></td>",

            BTN_ADD_ROW_ID = "btnAddRow",
            BTN_ADD_ROW_TEXT = "Add new",
            BTN_EDIT_TEXT = "edit",
            BTN_SAVE_ID = "btnSaveEditRow",
            BTN_SAVE_TEXT = "save",
            BTN_CANCEL_ID = "btnCancelEditRow",
            BTN_CANCEL_TEXT = "cancel",

            DIV_ID_CACHE = "divEditorRowCache",

            JS_FUNC_URL_PARAMS_PATTERN = "%s : $('#%s').%s";

    /**
     * This method prepare html-code with table that consist viewRow's for each entity from obtained entityList.
     *
     * @param tableName - name of table from DB that consist received entities
     * @param entityList - list of entities that will be shown
     * @return string with html-code of table
     */
    public static <T extends AbstractEntity> String getTable(String tableName, List<T> entityList){
        StringBuilder result = new StringBuilder();

        /* adding table name */
        result.append( String.format("<b>Table - %s</b><br>", tableName) );

        /* adding <table> */
        result.append( String.format("<table id='%s' border=1>", getTableId(tableName)) );

        if (entityList ==null || entityList.isEmpty()){
            return "<tr><td> empty </td></tr>";
        }

        /* adding <tr> for each entity*/
        if (entityList.size()>0){
            result.append(getHeadersRow(entityList.get(0)));

            for (T entity : entityList) {
                result.append( getViewRow(entity) ).append("\n");
            }
        }

        /* add 'add'-button  */
        result.append( getAddNewRowBtnHtmlCode(tableName, entityList) );

        /* adding </table> */
        result.append("</table>");


        /* add div for data cache */
        result.append("<div id='"+ DIV_ID_CACHE +"' style=\"display: none;\"/>\n");

        /* return table pattern + table rows */
        return result.toString() ;
    }

    /**
     * Prepare html-code of row with button 'add new row'.
     *  This was check entity type and load corresponded editRow
     *
     * @param tableName name of dbTable which records will be showed
     * @param entityList list of entities that represented in the table
     * @return string with html-code of table row with button and logic
     */
    // TODO: provide adding new row to the empty table
    private static String getAddNewRowBtnHtmlCode(String tableName, List<? extends AbstractEntity> entityList) {
        StringBuilder result = new StringBuilder();

        result.append("<tr><td><button id='"+BTN_ADD_ROW_ID+"' >"+BTN_ADD_ROW_TEXT+"</button></td>");

        final String disableAddBtnJsCode = "$(\"#" + BTN_ADD_ROW_ID + "\").attr('disabled', true);";

        /* if entityList is empty than return disabled button */
        if (entityList==null || entityList.isEmpty() ){
            result.append("<script type=\"text/javascript\">" + disableAddBtnJsCode + "</script></tr>");
            return result.toString();
        }

        String entityType;
        final Class<? extends AbstractEntity> entityClass = entityList.get(0).getClass();
        /* prepare url part for new entity particular class*/
        if (entityClass == CheckConfig.class ||
                entityClass == Server.class ||
                entityClass == AvayaParameter.class ){
            entityType = entityClass.getSimpleName();
        } else {
            entityType = "UnknownType";
        }

        String addBtnLogicUrl = String.format(URL_ADD_NEW_ROW_PATTERN, entityType, tableName),
                addBtnLogic = "<script type=\"text/javascript\">$('#" + BTN_ADD_ROW_ID + "').click(function(){" +
                        "$(\"#"+ DIV_ID_CACHE +"\").load('" + addBtnLogicUrl + "', function(data){" +
                        "$(\"#" + getTableId(tableName) + " tr:last\").before('<tr id=" + TR_ID_NEW_ROW + "></tr>');" +
                        "$(\"#" + TR_ID_NEW_ROW + "\").replaceWith(data);" +
                        disableAddBtnJsCode +
                        "disableButtonsInDivWithText('" + PARENT_DIV_ID + "' ,'"+BTN_EDIT_TEXT+"');});});</script>";

        result.append(addBtnLogic)
                .append("</tr>");

        return result.toString();
    }

    /**
     * Prepare normalized ID of table
     * @param tableName name of dbTable which records will be showed
     * @return string with id
     */
    private static String getTableId(String tableName) {
        final String TABLE_ID_PATTERN = "table___%s";
        return String.format(TABLE_ID_PATTERN, tableName);
    }




    /**
     * This method prepare html-code of table row with headers. Headers sequence for each entity type is unique.
     * Each column header in whole headers sequence is 'hardcoded'.
     *
     * @param entity some valid entity
     * @param <T> child of AbstractEntity
     * @return string with html-code of table headers row
     */
    public static <T extends AbstractEntity> String getHeadersRow(T entity){
        StringBuilder result = new StringBuilder();

        /* adding tr-tag with id */
        result.append("<tr>");

        /* AbstractEntity fields preparing */
        result.append("<td>id</td>");

        /* CheckConfig fields preparing */
        if (entity instanceof CheckConfig) {
            result.append("<td>monEntity</td>")
                    .append("<td>attributes</td>")
                    .append("<td>frequency</td>")
                    .append("<td>isDisabled</td>")
                    .append("<td>description</td>");
        }

        /* AbstractMonitoringEntity fields preparing */
        if (entity instanceof AbstractMonitoredEntity) {
            result.append("<td>name</td>")
                    .append("<td>isDeleted</td>");

            /* Server fields preparing */
            if (entity instanceof Server) {
                result.append("<td>ip</td>")
                        .append("<td>snmpCommunity</td>")
                        .append("<td>osType</td>");
            /* AvayaParameter fields preparing */
            } else if (entity instanceof AvayaParameter){
                result.append("<td>serverId</td>")
                        .append("<td>subSystem</td>");
            }

            result.append("<td>description</td>");
        }

        result.append("<td>controls</td>");

        result.append("</tr>");

        return result.toString();
    }

    /**
     * This method prepare html-code of one table row with plain text cells and 'edit' button for one entity.
     *
     * Button 'edit' just replace html code of this TR-tag to editableRow-code that will be loaded from controller.
     * onClick script use entityTableName and id.
     *
     * @param entity some valid entity
     * @param <T> child of AbstractEntity
     * @return string with html-code of table row
     */
    public static <T extends AbstractEntity> String getViewRow(T entity){
        StringBuilder result = new StringBuilder();

        /* adding tr-tag with id */
        String rowId = getRowId(entity);
        result.append(String.format("<tr id=\"%s\">", rowId));

        /* AbstractEntity fields preparing */
        result.append(String.format(TD_PATTERN_SIMPLE, entity.getId() ));

        /* CheckConfig fields preparing */
        if (entity instanceof CheckConfig) {
            CheckConfig checkConfig = (CheckConfig) entity;
            result.append(String.format(TD_PATTERN_SIMPLE, checkConfig.getEntity().getPrepareName() ));
            result.append(String.format(TD_PATTERN_SIMPLE, checkConfig.getAttributes() ));
            result.append(String.format(TD_PATTERN_SIMPLE, checkConfig.getFrequency() ));
            result.append(String.format(TD_PATTERN_SIMPLE, checkConfig.isDisabled() ));
            result.append(String.format(TD_PATTERN_SIMPLE, checkConfig.getDescription() ));
        }

        /* AbstractMonitoringEntity fields preparing */
        if (entity instanceof AbstractMonitoredEntity) {
            AbstractMonitoredEntity abstractMonitoredEntity = (AbstractMonitoredEntity)entity;
            result.append(String.format(TD_PATTERN_SIMPLE, abstractMonitoredEntity.getName() ));
            result.append(String.format(TD_PATTERN_SIMPLE, abstractMonitoredEntity.isDeleted() ));

            /* Server fields preparing */
            if (entity instanceof Server) {
                Server server = (Server)entity;
                result.append(String.format(TD_PATTERN_SIMPLE, server.getIp() ));
                result.append(String.format(TD_PATTERN_SIMPLE, server.getSnmpCommunity() ));
                result.append(String.format(TD_PATTERN_SIMPLE, server.getOsType() ));
            /* AvayaParameter fields preparing */
            } else if (entity instanceof AvayaParameter){
                AvayaParameter avayaParameter = (AvayaParameter)entity;
                if (avayaParameter.getServer()==null){
                    result.append(String.format(TD_PATTERN_SIMPLE, avayaParameter.getServerId() ));
                } else {
                    result.append(String.format(TD_PATTERN_SIMPLE, avayaParameter.getServer().getPrepareName() ));
                }
                result.append(String.format(TD_PATTERN_SIMPLE, avayaParameter.getSubSystem() ));
            }

            result.append(String.format(TD_PATTERN_SIMPLE, abstractMonitoredEntity.getDescription() ));
        }

        /* Edit button fields preparing */
        String btnEditLinkUrl = String.format(URL_GET_EDIT_ROW_PATTERN,
                entity.getDbTableName(), entity.getId());

        String btnEditPattern =
                String.format("<button onClick=\"replaceTagWithHtml('%s', '%s');", rowId, btnEditLinkUrl) +
                        String.format("disableButtonsInDivWithText('%s' ,'%s');", PARENT_DIV_ID, BTN_ADD_ROW_TEXT) +
                        String.format("disableButtonsInDivWithText('%s' ,'%s');", PARENT_DIV_ID, BTN_EDIT_TEXT) +
                        String.format("\">%s</button>",BTN_EDIT_TEXT);

        result.append(String.format(TD_PATTERN_SIMPLE, btnEditPattern ));

        result.append("</tr>");

        return result.toString();
    }

    /**
     * This method prepare table row with editable cell's that consist properties of received entity and control
     * buttons. In case when ary important parameter was 'incorrect' than method show it as is and disable 'save'
     * button.
     *
     *
     * Button 'save' will send request to controller, that consist entityTableName, id and established values of
     * all parameters. onClick script of this button is unique for each entity type as whole editable row.
     *
     * Button 'cancel' just replace html-code in this TR-tag to viewRow-code that will be requested from controller.
     * onClick script use tableName of this entity and id.
     *
     *
     * @param entity entity that will be showed
     * @param monitoredEntityList list of all monitored entities, that can be used in entity configuration
     * @param saveBtnOnClickLogic logic that will be applied to 'save' button
     * @param cancelBtnOnClickLogic logic that will be applied to 'cancel' button
     * @param <T> obtained entities can be instance of any child class of AbstractEntity
     * @return string with html-code of table row with editable cells
     */
    private static <T extends AbstractEntity> String getEditRow(T entity,
                                                               List<? extends AbstractMonitoredEntity> monitoredEntityList,
                                                               String saveBtnOnClickLogic,
                                                               String cancelBtnOnClickLogic){
        StringBuilder result = new StringBuilder();

        /* This var consist common pattern for JS function that
        *   reading values of all cells in editRow and return string of parameters for url
        *
        *   for example :
        *           function getUrlParamsFromEditRow(){
		*		        return $.param({
		*				        id 			: $("#cfgId").text(),
		*				        monEntityId	: $('#cfgEntityIdDD option:selected').val(),
		*				        frequency 	: $("#cfgFrequency").val(),
		*				        asDisabled 	: $('#isDisabled').prop('checked'),
		*			    });
		*	        }       ==>         id=0&monEntityId=0&frequency=0&asDisabled=false
        */
        String jsFuncUrlParamsPreparingPattern =
                "\n<script type=\"text/javascript\">function getUrlParamsForEditorRow(){" +
                "return $.param({ %s });}</script>";
        /*
        *  Below in the IF-blocks that builds cells for editRow we must add to this var js-code for parsing it.
        */
        StringBuilder jsFuncUrlParamsPreparingRows = new StringBuilder();


        /* if some data in the row cells is incorrect
        *   than we must set this value to true */
        boolean saveBtnWillBeDisabled = false;

        /* adding tr-tag with id */
        String rowId = getRowId(entity);
        result.append(String.format("<tr id=\"%s\">", rowId));

        /* AbstractEntity fields preparing */
        result.append(String.format(TD_PATTERN, TD_ID_ID, entity.getId() ));

        /* CheckConfig fields preparing */
        if (entity instanceof CheckConfig) {
            CheckConfig checkConfig = (CheckConfig) entity;

            /* DropDown list with monitoredEntities */
            String tdEntityIdContent;
            if (monitoredEntityList==null){
                final String monitoredEntityId = "--" + checkConfig.getEntityId() + "--";
                tdEntityIdContent = String.format(TD_PATTERN_SIMPLE_RED, monitoredEntityId);
                saveBtnWillBeDisabled = true;
            } else {
                final String dropDownList =
                        getHtmlDropDownList(TD_ID_ENTITY_ID, monitoredEntityList, checkConfig.getEntityId());
                tdEntityIdContent = String.format(TD_PATTERN_SIMPLE, dropDownList);
            }

            jsFuncUrlParamsPreparingRows.append( String.format(JS_FUNC_URL_PARAMS_PATTERN,
                    URL_PROPERTY_MON_ENTITY_ID, TD_ID_ENTITY_ID+" option:selected","val()"));

            result.append(tdEntityIdContent);

            /* Text edit field with attributes */
            final String entityAttributes =
                    (checkConfig.getAttributes() == null) ? "" : checkConfig.getAttributes();

            jsFuncUrlParamsPreparingRows.append(",").append(String.format(JS_FUNC_URL_PARAMS_PATTERN,
                    URL_PROPERTY_ATTRIBUTES, TD_ID_ATTRIBUTES, "val()"));
            result.append(String.format(TD_PATTERN_INPUT_TEXT_EDIT, TD_ID_ATTRIBUTES, 10, entityAttributes));

            /* Text edit field with frequency */
            final int entityFrequency = checkConfig.getFrequency();
            jsFuncUrlParamsPreparingRows.append(",").append(String.format(JS_FUNC_URL_PARAMS_PATTERN,
                    URL_PROPERTY_FREQUENCY, TD_ID_FREQUENCY, "val()"));
            result.append(String.format(TD_PATTERN_INPUT_TEXT_EDIT, TD_ID_FREQUENCY, 10, entityFrequency));

            /* CheckBox with isDisabled */
            final String isDisabledCheckBox = getHtmlCheckBox(TD_ID_DISABLED, checkConfig.isDisabled());
            jsFuncUrlParamsPreparingRows.append(",").append(String.format(JS_FUNC_URL_PARAMS_PATTERN,
                    URL_PROPERTY_DISABLED, TD_ID_DISABLED, "prop('checked')"));
            result.append(String.format(TD_PATTERN_SIMPLE, isDisabledCheckBox));

            /* Text edit field with description */
            final String entityDescription =
                    (checkConfig.getDescription() == null) ? "" : checkConfig.getDescription();
            jsFuncUrlParamsPreparingRows.append(",").append(String.format(JS_FUNC_URL_PARAMS_PATTERN,
                    URL_PROPERTY_DESCRIPTION, TD_ID_DESCRIPTION, "val()"));
            result.append(String.format(TD_PATTERN_INPUT_TEXT_EDIT, TD_ID_DESCRIPTION, 45, entityDescription));
        }

        /* AbstractMonitoringEntity fields preparing */
        if (entity instanceof AbstractMonitoredEntity) {
            AbstractMonitoredEntity abstractMonitoredEntity = (AbstractMonitoredEntity)entity;

            /* Text edit field with entityName */
            final String entityName = abstractMonitoredEntity.getName();

            jsFuncUrlParamsPreparingRows.append(String.format(JS_FUNC_URL_PARAMS_PATTERN,
                    URL_PROPERTY_NAME, TD_ID_NAME, "val()"));
            result.append(String.format(TD_PATTERN_INPUT_TEXT_EDIT, TD_ID_NAME, 20, entityName));

            /* CheckBox with isDeleted */
            final boolean isDeleted = abstractMonitoredEntity.isDeleted();
            final String isDeletedCheckBox = getHtmlCheckBox(TD_ID_DELETED, isDeleted);
            jsFuncUrlParamsPreparingRows.append(",").append(String.format(JS_FUNC_URL_PARAMS_PATTERN,
                    URL_PROPERTY_DELETED, TD_ID_DELETED, "prop('checked')"));
            result.append(String.format(TD_PATTERN_SIMPLE, isDeletedCheckBox));

            /* Server fields preparing */
            if (entity instanceof Server) {
                Server server = (Server)entity;

                /* Text edit field with serverIp */
                jsFuncUrlParamsPreparingRows.append(",").append(String.format(JS_FUNC_URL_PARAMS_PATTERN,
                        URL_PROPERTY_SERVER_IP, TD_ID_SRV_IP, "val()"));
                result.append(String.format(TD_PATTERN_INPUT_TEXT_EDIT, TD_ID_SRV_IP, 16, server.getIp()));


                /* Text edit field with serverSnmpCommunity */
                jsFuncUrlParamsPreparingRows.append(",").append(String.format(JS_FUNC_URL_PARAMS_PATTERN,
                        URL_PROPERTY_SERVER_SNMP_COMMUNITY, TD_ID_SRV_SNMP_COMMUNITY, "val()"));
                result.append(String.format(TD_PATTERN_INPUT_TEXT_EDIT, TD_ID_SRV_SNMP_COMMUNITY, 25, server.getSnmpCommunity()));


                /* DropDown list with serverOsTypes */
                final String dropDownList =
                        getHtmlDropDownList(TD_ID_SRV_OS_TYPE, Server.OS_TYPES, server.getOsType());
                String tdSrvOsTypesDdList = String.format(TD_PATTERN_SIMPLE, dropDownList);
                result.append(tdSrvOsTypesDdList);
                jsFuncUrlParamsPreparingRows.append(",").append(String.format(JS_FUNC_URL_PARAMS_PATTERN,
                        URL_PROPERTY_SERVER_OS_TYPE, TD_ID_SRV_OS_TYPE, "val()"));

            /* AvayaParameter fields preparing */
            } else if (entity instanceof AvayaParameter){
                AvayaParameter avayaParameter = (AvayaParameter)entity;

                /* DropDown list with serverList */
                String tdServerIdContent;
                if (monitoredEntityList==null){
                    final String monitoredEntityId = "--" + avayaParameter.getServerId() + "--";
                    tdServerIdContent = String.format(TD_PATTERN_SIMPLE_RED, monitoredEntityId);
                    saveBtnWillBeDisabled = true;
                } else {
                    final String dropDownList =
                            getHtmlDropDownList(TD_ID_SERVER_ID, monitoredEntityList, avayaParameter.getServerId());
                    tdServerIdContent = String.format(TD_PATTERN_SIMPLE, dropDownList);
                }
                jsFuncUrlParamsPreparingRows.append(",").append(String.format(JS_FUNC_URL_PARAMS_PATTERN,
                        URL_PROPERTY_SERVER_ID, TD_ID_SERVER_ID, "val()"));
                result.append( tdServerIdContent );

                /* Text edit field with subsystem name */
                jsFuncUrlParamsPreparingRows.append(",").append(String.format(JS_FUNC_URL_PARAMS_PATTERN,
                        URL_PROPERTY_SUBSYSTEM, TD_ID_SUBSYSTEM, "val()"));
                result.append(String.format(TD_PATTERN_INPUT_TEXT_EDIT, TD_ID_SUBSYSTEM, 10, avayaParameter.getSubSystem()));
            }

            /* Text edit field with description */
            final String entityDescription = (abstractMonitoredEntity.getDescription() == null)
                            ? "" : abstractMonitoredEntity.getDescription();
            jsFuncUrlParamsPreparingRows.append(",").append(String.format(JS_FUNC_URL_PARAMS_PATTERN,
                    URL_PROPERTY_DESCRIPTION, TD_ID_DESCRIPTION, "val()"));
            result.append(String.format(TD_PATTERN_INPUT_TEXT_EDIT, TD_ID_DESCRIPTION, 45, entityDescription));
        }

        /* add <button> 'save' & 'cancel' */
        result.append("\n");
        final String cellWithButtons = String.format(
                "<button id='%s'>%s</button><button id='%s'>%s</button>",
                BTN_SAVE_ID, BTN_SAVE_TEXT, BTN_CANCEL_ID, BTN_CANCEL_TEXT
                );
        result.append(String.format(TD_PATTERN_SIMPLE, cellWithButtons ));


        /* add <script> with buttons logic */
        final String scriptTagWithButtonsOnClickLogic =
                "<script type=\"text/javascript\">$('#%s').click(function(){%s});$('#%s').click(function(){%s});</script>";
        result.append(
                String.format(
                        scriptTagWithButtonsOnClickLogic,
                        BTN_SAVE_ID, saveBtnOnClickLogic,
                        BTN_CANCEL_ID, cancelBtnOnClickLogic
                )
        );

        /* add <script> with function for parsing cell values */
        result.append(
                String.format(jsFuncUrlParamsPreparingPattern, jsFuncUrlParamsPreparingRows)
        );

        /* add <script> with code for disabling saveBtn if we  need it */
        if (saveBtnWillBeDisabled){
            final String scriptForDisablingSaveBtn = String.format(
                    "<script type=\"text/javascript\">$(\"#%s\").attr(\"disabled\", \"disabled\");</script>",
                    BTN_SAVE_ID);
            result.append(scriptForDisablingSaveBtn);
        }

        result.append("</tr>");

        return result.toString();
    }

    /**
     * This overloaded method return html-code of table row with editable cell's that consist properties of
     * received entity and control buttons.
     * Also this method prepare scripts for buttons logic.
     *
     * @param entity entity that will be showed
     * @param monitoredEntitiesList list of all monitored entities, that can be used in entity configuration
     * @param <T> obtained entities can be instance of any child class of AbstractEntity
     * @return string with html-code of table row with editable cells
     */
    public static <T extends AbstractEntity> String getEditRow(T entity,
                                                    List<? extends AbstractMonitoredEntity> monitoredEntitiesList) {
        final String
                btnCancelLinkUrl = String.format(URL_GET_VIEW_ROW_PATTERN, entity.getDbTableName(), entity.getId()),
                btnCancelPattern =
                        String.format("replaceTagWithHtml('%s', '%s');", getRowId(entity) ,btnCancelLinkUrl) +
                        String.format("enableButtonsInDivWithText('%s' ,'%s');", PARENT_DIV_ID, BTN_ADD_ROW_TEXT) +
                        String.format("enableButtonsInDivWithText('%s' ,'%s');", PARENT_DIV_ID, BTN_EDIT_TEXT);

        String saveBtnUrlPattern = null;
        if (entity.getClass() == Server.class) {
            saveBtnUrlPattern = getUrlPattern(URL_CONFIG_PAGE_UPDATE_SERVER);
        } else if (entity.getClass() == AvayaParameter.class) {
            saveBtnUrlPattern = getUrlPattern(URL_CONFIG_PAGE_UPDATE_AVAYA_PARAMETER);
        } else if (entity.getClass() == CheckConfig.class) {
            saveBtnUrlPattern = getUrlPattern(URL_CONFIG_PAGE_UPDATE_CHECK_CONFIG);
        }

        String btnSaveLinkUrlPattern = "";

        if (saveBtnUrlPattern != null) {
            btnSaveLinkUrlPattern = String.format(URL_CONFIG_PAGE_ROOT + saveBtnUrlPattern,
                    entity.getDbTableName(), entity.getId());
        }

                /* reload table with checkConfigs and enable all 'edit'-buttons */
        String btnSavePattern =
                String.format("var url = '%s?' + getUrlParamsForEditorRow();", btnSaveLinkUrlPattern) +
                String.format("$.get(url, function( data ) { $('#%s').replaceWith(data); } );", getRowId(entity)) +
                String.format("enableButtonsInDivWithText('%s' ,'%s');", PARENT_DIV_ID, BTN_ADD_ROW_TEXT) +
                String.format("enableButtonsInDivWithText('%s' ,'%s');", PARENT_DIV_ID, BTN_EDIT_TEXT);

        return getEditRow(entity, monitoredEntitiesList, btnSavePattern, btnCancelPattern);
    }

    /**
     * This overloaded method will be used for entities that haven't any monitoredEntity parameter.
     *
     * @param entity entity that will be showed
     * @param <T> obtained entities can be instance of any child class of AbstractEntity
     * @return html-code of table row with editable cell's that consist properties of received entity and control
     * buttons.
     */
    public static <T extends AbstractEntity> String getEditRow(T entity) {
        return getEditRow(entity, null);
    }


    /**
     * This method :
     *      - obtained new instance of entity that have been just created in the controller method
     *      - prepare save & cancel button scripts
     *              save: create new record in DB and reload table
     *              cancel : remove added editable row from table
     *      - invoke .getEditRow(entity, monitoredEntitiesList, btnSavePattern, btnCancelPattern)
     * End up method return html-code of table row with editable cell's that consist properties of received newEntity
     *  and control buttons.
     *
     * @param entity entity that will be showed
     * @param monitoredEntitiesList list of all monitored entities, that can be used in entity configuration
     * @param <T> obtained entities can be instance of any child class of AbstractEntity
     * @return string with html-code of table row with editable cells
     */
    public static <T extends AbstractEntity> String getAddNewEntityEditRow(T entity,
                                                                           List<? extends AbstractMonitoredEntity> monitoredEntitiesList) {
        // TODO: add separate saveBtn url's for each entity type
        final String
                /* remove added edit-row from table and enable all 'edit'-buttons */
                btnCancelPattern = "$(this).closest('tr').remove();" +
                        String.format("enableButtonsInDivWithText('%s' ,'%s');", PARENT_DIV_ID, BTN_ADD_ROW_TEXT) +
                        String.format("enableButtonsInDivWithText('%s' ,'%s');", PARENT_DIV_ID, BTN_EDIT_TEXT);

        String saveBtnUrlPattern = null;
        if (entity.getClass() == Server.class) {
            saveBtnUrlPattern = getUrlPattern(URL_CONFIG_PAGE_CREATE_SERVER);
        } else if (entity.getClass() == AvayaParameter.class) {
            saveBtnUrlPattern = getUrlPattern(URL_CONFIG_PAGE_CREATE_AVAYA_PARAMETER);
        } else if (entity.getClass() == CheckConfig.class) {
            saveBtnUrlPattern = getUrlPattern(URL_CONFIG_PAGE_CREATE_CHECK_CONFIG);
        }

        String btnSaveLinkUrlPattern = "";

        if (saveBtnUrlPattern != null) {
            btnSaveLinkUrlPattern = String.format(URL_CONFIG_PAGE_ROOT + saveBtnUrlPattern, entity.getDbTableName());
        }

                /* reload table with checkConfigs and enable all 'edit'-buttons */
        String getTableUrl = String.format(URL_GET_TABLE_PATTERN, entity.getDbTableName()),
                btnSavePattern =
                        String.format("var url = '%s?' +getUrlParamsForEditorRow();", btnSaveLinkUrlPattern) +
                        "$.get(url, function( data ) {alert( data );});" +
                        String.format("var url = '%s';", getTableUrl) +
                        String.format("loadPage('%s', url);", PARENT_DIV_ID) +
                        String.format("enableButtonsInDivWithText('%s' ,'%s');", PARENT_DIV_ID, BTN_ADD_ROW_TEXT) +
                        String.format("enableButtonsInDivWithText('%s' ,'%s');", PARENT_DIV_ID, BTN_EDIT_TEXT);

        return getEditRow(entity, monitoredEntitiesList, btnSavePattern, btnCancelPattern);
    }

    /**
     * Prepare normalized ID of table row with entity
     * @param entity some entity
     * @param <T> child of AbstractEntity
     * @return string with id
     */
    private static <T extends AbstractEntity> String getRowId(T entity) {
        final String ROW_ID_PATTERN = "%s___%s";
        return String.format(ROW_ID_PATTERN, entity.getDbTableName(), entity.getId());
    }

    /**
     * Method return html-code of Drop Down List that consist obtained MonitoredEntity instances
     *
     * @param dropDownListId id of new drop down list element
     * @param monitoredEntityList list of all monitored entities
     * @param selectedEntityId id of preselected monitoredEntity
     * @return html-code of drop down list
     */
    private static String getHtmlDropDownList(String dropDownListId,
                                      List<? extends AbstractMonitoredEntity> monitoredEntityList,
                                      int selectedEntityId){
        if (monitoredEntityList==null) return "";
        List<String> optValueList = new ArrayList<>();
        List<String> optNameList = new ArrayList<>();
        for (AbstractMonitoredEntity entity : monitoredEntityList) {
            optValueList.add(""+entity.getId());
            optNameList.add(entity.getPrepareName());
        }
        return getHtmlDropDownList( dropDownListId, optValueList, optNameList, ""+selectedEntityId);
    }

    private static String getHtmlDropDownList(String dropDownListId,
                                              List<String> valuesList,
                                              String selectedValue){
        if (valuesList==null || valuesList.isEmpty()) return "";

        return getHtmlDropDownList( dropDownListId, valuesList, valuesList, selectedValue);
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
    private static String getHtmlDropDownList(String dropDownListId,
                                       List<String> optValueList,
                                       List<String> optNameList,
                                       String selectedId){
        StringBuilder result = new StringBuilder();

        if (dropDownListId==null || optValueList ==null || optNameList ==null
                || optValueList.size()!= optNameList.size())
            return "";

//      Example:  <select> <option value="saab" selected>Saab</option> </select>

        result.append(String.format("<select id=\"%s\">",dropDownListId));

        int ddListSize = optValueList.size();

        for (int i=0; i<ddListSize; i++){
            final String optVal = optValueList.get(i);
            result.append( String.format("<option value=\"%s\" %s>%s</option>",
                    optVal,
                    (optVal.equals(selectedId))
                            ? "selected"
                            : "", optNameList.get(i)
                    )
            );
        }

        result.append("</select>");

        return result.toString();
    }

    /**
     * Method return html-code for CheckBox element
     *
     * @param checkBoxId id of the new checkBox element
     * @param value current state
     * @return html-code of checkBox
     */
    private static String getHtmlCheckBox(String checkBoxId, boolean value){
        return String.format("<input id=\"%s\" type=\"checkbox\" %s/>",
                checkBoxId, (value) ? "checked" : "");
    }

}
