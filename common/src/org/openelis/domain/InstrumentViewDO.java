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

/**
 * The class extends the instrument DO and carries an additional scriptlet name
 * field. This additional fields is for read/display only and does not get
 * committed to the database. Note: isChanged will reflect any changes to
 * read/display fields.
 */

public class InstrumentViewDO extends InstrumentDO {

    private static final long serialVersionUID = 1L;

    protected String          scriptletName;

    public InstrumentViewDO() {
    }

    public InstrumentViewDO(Integer id, String name, String description, String modelNumber,
                                 String serialNumber, Integer typeId, String location,
                                 String isActive, Date activeBegin, Date activeEnd,
                                 Integer scriptletId, String scriptletName) {
        super(id, name, description, modelNumber, serialNumber, typeId, location, isActive,
              activeBegin, activeEnd, scriptletId);
        setScriptletName(scriptletName);
    }

    public String getScriptletName() {
        return scriptletName;
    }

    public void setScriptletName(String scriptletName) {
        this.scriptletName = scriptletName;
    }
}