/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.meta;

/**
 * Project META Data
 */

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.gwt.common.Meta;
import org.openelis.gwt.common.MetaMap;

public class ProjectMeta implements Meta, MetaMap {
    private static final String   ID = "_project.id",
                                  NAME = "_project.name",
                                  DESCRIPTION = "_project.description",
                                  STARTED_DATE = "_project.startedDate",
                                  COMPLETED_DATE = "_project.completedDate",
                                  IS_ACTIVE = "_project.isActive",
                                  REFERENCE_TO = "_project.referenceTo",
                                  OWNER_ID = "_project.ownerId",
                                  SCRIPTLET_ID = "_project.scriptletId",

                                  PARM_ID = "_projectParameter.id",
                                  PARM_PROJECT_ID = "_projectParameter.projectId",
                                  PARM_PARAMETER = "_projectParameter.parameter",
                                  PARM_OPERATION_ID = "_projectParameter.operationId",
                                  PARM_VALUE= "_projectParameter.value",
                                  
                                  SCRIPTLET_NAME = "_project.scriptlet.name";

    private static HashSet<String> names;

    static {
        names = new HashSet<String>(Arrays.asList(ID, NAME, DESCRIPTION,
                                                  STARTED_DATE, COMPLETED_DATE, IS_ACTIVE,
                                                  REFERENCE_TO, OWNER_ID, SCRIPTLET_ID,
                                                  PARM_ID, PARM_PROJECT_ID, PARM_PARAMETER,
                                                  PARM_OPERATION_ID, PARM_VALUE,
                                                  SCRIPTLET_NAME));
    }

    public static String getId() {
        return ID;
    }

    public static String getName() {
        return NAME;
    }

    public static String getDescription() {
        return DESCRIPTION;
    }

    public static String getStartedDate() {
        return STARTED_DATE;
    }

    public static String getCompletedDate() {
        return COMPLETED_DATE;
    }

    public static String getIsActive() {
        return IS_ACTIVE;
    }

    public static String getReferenceTo() {
        return REFERENCE_TO;
    }

    public static String getOwnerId() {
        return OWNER_ID;
    }

    public static String getScriptletId() {
        return SCRIPTLET_ID;
    }

    public static String getProjectParameterId() {
        return PARM_ID;
    }

    public static String getProjectParameterProjectId() {
        return PARM_PROJECT_ID;
    }

    public static String getProjectParameterParameter() {
        return PARM_PARAMETER;
    }

    public static String getProjectParameterOperationId() {
        return PARM_OPERATION_ID;
    }

    public static String getProjectParameterValue() {
        return PARM_VALUE;
    }

    public static String getScriptletName() {
        return SCRIPTLET_NAME;
    }

    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }

    public String buildFrom(String where) {
        String from;
        
        from = "Project _project ";
        if (where.indexOf("projectParameter.") > -1)
            from += ",IN (_project.projectParameter) _projectParameter ";

        return from;
    }
}