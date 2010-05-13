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

import java.util.HashMap;
import java.util.Iterator;

import javax.naming.InitialContext;

import org.openelis.domain.ReferenceTable;
import org.openelis.domain.WorksheetDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.WorksheetLocal;
import org.openelis.utilcommon.DataBaseUtil;

public class WorksheetManagerProxy {

    public WorksheetManager fetchById(Integer id) throws Exception {
        WorksheetLocal   local;
        WorksheetManager manager;
        WorksheetDO      data;

        local   = local();
        data    = local.fetchById(id);
        manager = WorksheetManager.getInstance();

        manager.setWorksheet(data);

        return manager;
    }

    public WorksheetManager fetchWithItems(Integer id) throws Exception {
        WorksheetManager manager;

        manager = fetchById(id);
        manager.getItems();

        return manager;
    }

    public WorksheetManager fetchWithNotes(Integer id) throws Exception {
        WorksheetManager manager;

        manager = fetchById(id);
        manager.getNotes();

        return manager;
    }

    public WorksheetManager add(WorksheetManager manager) throws Exception {
        int                            i;
        Integer                        id;
        Iterator<SampleManager>        iter;
        HashMap<Integer,SampleManager> sManagers;
        SampleManager                  sManager;
        WorksheetLocal                 local;

        local = local();
        local.add(manager.getWorksheet());
        id = manager.getWorksheet().getId();

        if (manager.items != null) {
            sManagers = new HashMap<Integer,SampleManager>();

            manager.getItems().setWorksheetId(id);
            manager.getItems().add(sManagers);
            
            iter = sManagers.values().iterator();
            while (iter.hasNext()) {
                sManager = (SampleManager) iter.next();
                sManager.validate();
                sManager.update();
            }
        }
        
        if (manager.notes != null) {
            manager.getNotes().setReferenceId(id);
            manager.getNotes().setReferenceTableId(ReferenceTable.WORKSHEET);
            manager.getNotes().add();
        }

        return manager;
    }

    public WorksheetManager update(WorksheetManager manager) throws Exception {
        int                            i;
        Integer                        id;
        Iterator<SampleManager>        iter;
        HashMap<Integer,SampleManager> sManagers;
        SampleManager                  sManager;
        WorksheetLocal                 local;

        local = local();
        local.update(manager.getWorksheet());
        id = manager.getWorksheet().getId();
        
        if (manager.items != null) {
            sManagers = new HashMap<Integer,SampleManager>();

            manager.getItems().setWorksheetId(id);
            manager.getItems().update(sManagers);
            
            iter = sManagers.values().iterator();
            while (iter.hasNext()) {
                sManager = (SampleManager) iter.next();
                sManager.validate();
                sManager.update();
            }
        }
        
        if (manager.notes != null) {
            manager.getNotes().setReferenceId(id);
            manager.getNotes().setReferenceTableId(ReferenceTable.WORKSHEET);
            manager.getNotes().update();
        }
        
        return manager;
    }

    public WorksheetManager fetchForUpdate(WorksheetManager manager) throws Exception {
        assert false : "not supported";
        return null;
    }

    public WorksheetManager abortUpdate(Integer id) throws Exception {
        assert false : "not supported";
        return null;
    }

    public void validate(WorksheetManager manager, ValidationErrorsList errorList) throws Exception {
        try {
            local().validate(manager.getWorksheet());
        } catch (Exception e) {
            DataBaseUtil.mergeException(errorList, e);
        }

        if (manager.items != null)
            manager.getItems().validate(errorList);
    }

    private WorksheetLocal local(){
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
