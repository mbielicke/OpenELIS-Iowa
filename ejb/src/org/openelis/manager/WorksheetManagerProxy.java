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
package org.openelis.manager;

import javax.naming.InitialContext;

import org.openelis.domain.WorksheetViewDO;
import org.openelis.local.WorksheetLocal;
import org.openelis.utils.ReferenceTableCache;

public class WorksheetManagerProxy {

    public WorksheetManager add(WorksheetManager worksheetManager) throws Exception {
        Integer worksheetId, worksheetRefId;
        WorksheetLocal worksheetLocal;

        worksheetId    = worksheetManager.getWorksheet().getId();
        worksheetRefId = ReferenceTableCache.getReferenceTable("worksheet");
        worksheetLocal = getWorksheetLocal();

//        worksheetManager.getItems().setWorksheetId(worksheetId);
//        worksheetManager.getItems().add();

        worksheetManager.getNotes().setReferenceId(worksheetId);
        worksheetManager.getNotes().setReferenceTableId(worksheetRefId);
        worksheetManager.getNotes().add();

        return worksheetManager;
    }

    public WorksheetManager update(WorksheetManager worksheetManager) throws Exception {
        Integer worksheetId, worksheetRefId;
        WorksheetLocal worksheetLocal;

        worksheetLocal = getWorksheetLocal();

        worksheetId = worksheetManager.getWorksheet().getId();
        worksheetRefId = worksheetManager.getWorksheetReferenceTable();

//        worksheetManager.getItems().setWorksheetId(worksheetId);
//        worksheetManager.getItems().update();

        worksheetManager.getNotes().setReferenceId(worksheetId);
        worksheetManager.getNotes().setReferenceTableId(worksheetRefId);
        worksheetManager.getNotes().update();

        return worksheetManager;
    }

    public WorksheetManager fetch(Integer worksheetId) throws Exception {
        WorksheetLocal worksheetLocal;
        WorksheetManager worksheetManager;
        WorksheetViewDO worksheetDO;

        worksheetLocal = getWorksheetLocal();
        worksheetDO = worksheetLocal.fetchById(worksheetId);

        worksheetManager = WorksheetManager.getInstance();
        worksheetManager.setWorksheet(worksheetDO);
        worksheetManager.setWorksheetReferenceTable(ReferenceTableCache.getReferenceTable("worksheet"));

        return worksheetManager;
    }

    public WorksheetManager fetchWithItems(Integer worksheetId) throws Exception {
        WorksheetManager worksheetManager;

        worksheetManager = fetch(worksheetId);
        worksheetManager.setWorksheetReferenceTable(ReferenceTableCache.getReferenceTable("worksheet"));

//        worksheetManager.getItems();

        return worksheetManager;
    }

    public WorksheetManager fetchWithNotes(Integer worksheetId) throws Exception {
        WorksheetManager worksheetManager;

        worksheetManager = fetch(worksheetId);
        worksheetManager.setWorksheetReferenceTable(ReferenceTableCache.getReferenceTable("worksheet"));

        worksheetManager.getNotes();

        return worksheetManager;
    }

    public WorksheetManager fetchWithIdentifiers(Integer worksheetId) throws Exception {
        return null;
    }

    public WorksheetManager fetchForUpdate(WorksheetManager man) throws Exception {
        throw new UnsupportedOperationException();
    }

    public WorksheetManager abort(Integer worksheetId) throws Exception {
        throw new UnsupportedOperationException();
    }

    public void validate(WorksheetManager worksheetManager) throws Exception {
        WorksheetLocal worksheetLocal;

        worksheetLocal = getWorksheetLocal();
//        worksheetLocal.validateWorksheet(worksheetManager.getWorksheet(),
//                                         worksheetManager.getItems().getItems());
    }

    private WorksheetLocal getWorksheetLocal(){
        InitialContext ctx;
        
        try {
            ctx = new InitialContext();
            return (WorksheetLocal)ctx.lookup("openelis/WorksheetBean/local");
        } catch(Exception e) {
             System.out.println(e.getMessage());
             return null;
        }
    }
}
