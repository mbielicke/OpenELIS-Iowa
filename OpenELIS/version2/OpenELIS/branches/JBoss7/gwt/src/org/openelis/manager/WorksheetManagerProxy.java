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
import org.openelis.modules.worksheet.client.WorksheetService;


public class WorksheetManagerProxy {

    
    public WorksheetManagerProxy(){
    }
    
    public WorksheetManager fetchById(Integer worksheetId) throws Exception {
        return WorksheetService.get().fetchById(worksheetId);
    }
    
    public WorksheetManager fetchWithItems(Integer worksheetId) throws Exception {
        return WorksheetService.get().fetchWithItems(worksheetId);
    }
    
    public WorksheetManager fetchWithNotes(Integer worksheetId) throws Exception {
        return WorksheetService.get().fetchWithNotes(worksheetId);
    }
    
    public WorksheetManager fetchWithItemsAndNotes(Integer worksheetId) throws Exception {
        return WorksheetService.get().fetchWithItemsAndNotes(worksheetId);
    }
    
    public WorksheetManager fetchWithAllData(Integer worksheetId) throws Exception {
        return WorksheetService.get().fetchWithAllData(worksheetId);
    }
    
    public WorksheetManager add(WorksheetManager man) throws Exception {
        return WorksheetService.get().add(man);
    }

    public WorksheetManager update(WorksheetManager man) throws Exception {
        return WorksheetService.get().update(man);
    }

    public WorksheetManager fetchForUpdate(Integer worksheetId) throws Exception {
        return WorksheetService.get().fetchForUpdate(worksheetId);
    }
    
    public WorksheetManager abortUpdate(Integer worksheetId) throws Exception {
        return WorksheetService.get().abortUpdate(worksheetId);
    }
    
    public void validate(WorksheetManager man, ValidationErrorsList errorList) throws Exception {
        
    }
}