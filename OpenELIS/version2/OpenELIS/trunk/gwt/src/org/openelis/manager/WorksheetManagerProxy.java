/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.manager;

import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.services.ScreenService;


public class WorksheetManagerProxy {

    protected static final String WORKSHEET_MANAGER_SERVICE_URL = "org.openelis.modules.worksheet.server.WorksheetService";
    protected ScreenService service;
    
    public WorksheetManagerProxy(){
        service = new ScreenService("OpenELISServlet?service="+WORKSHEET_MANAGER_SERVICE_URL);
    }
    
    public WorksheetManager fetchById(Integer worksheetId) throws Exception {
        return service.call("fetchById", worksheetId);
    }
    
    public WorksheetManager fetchWithItems(Integer worksheetId) throws Exception {
        return service.call("fetchWithItems", worksheetId);
    }
    
    public WorksheetManager fetchWithNotes(Integer worksheetId) throws Exception {
        return service.call("fetchWithNotes", worksheetId);
    }
    
    public WorksheetManager add(WorksheetManager man) throws Exception {
        return service.call("add",man);
    }

    public WorksheetManager update(WorksheetManager man) throws Exception {
        return service.call("update", man);
    }

    public WorksheetManager fetchForUpdate(Integer worksheetId) throws Exception {
        return service.call("fetchForUpdate", worksheetId);
    }
    
    public WorksheetManager abortUpdate(Integer worksheetId) throws Exception {
        return service.call("abortUpdate", worksheetId);
    }
    
    public void validate(WorksheetManager man, ValidationErrorsList errorList) throws Exception {
        
    }
}