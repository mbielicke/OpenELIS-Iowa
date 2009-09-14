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
package org.openelis.domain;

import java.util.Date;
import org.openelis.utilcommon.DataBaseUtil;

/**
 * The class extends the project DO and carries additional scriptlet name field.
 * This additional fields is for read/display only and does not get committed to
 * the database. Note: isChanged will reflect any changes to read/display
 * fields.
 */

public class ProjectScriptletDO extends ProjectDO {

    private static final long serialVersionUID = 1L;

    protected String          scriptletName;

    public ProjectScriptletDO() {
    }

    public ProjectScriptletDO(Integer id, String name, String description, Date startedDate,
                              Date completedDate, String isActive, String referenceTo,
                              Integer ownerId, Integer scriptletId, String scriptletName) {
        super(id, name, description, startedDate, completedDate, isActive, referenceTo, ownerId, scriptletId);
        setScriptletName(scriptletName);
    }
    
    public String getScriptletName() {
        return scriptletName;
    }

    public void setScriptletName(String scriptletName) {
        this.scriptletName = DataBaseUtil.trim(scriptletName);
    }
}