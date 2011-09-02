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

import java.util.Iterator;

import org.openelis.domain.InstrumentLogDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.InstrumentLogLocal;
import org.openelis.local.LockLocal;
import org.openelis.utils.EJBFactory;

public class WorksheetManagerProxy {
    protected static Integer instrumentLogPendingId, instrumentLogCompletedId, statusCompleteId;
    
    public WorksheetManagerProxy() {
        DictionaryLocal l;

        if (instrumentLogPendingId == null) {
            l = EJBFactory.getDictionary();

            try {
                instrumentLogPendingId = l.fetchBySystemName("instrument_log_pending").getId();
                instrumentLogCompletedId = l.fetchBySystemName("instrument_log_completed").getId();
                statusCompleteId = l.fetchBySystemName("worksheet_complete").getId();
            } catch (Exception e) {
                e.printStackTrace();
                instrumentLogPendingId = null;
            }
        }
    }
    
    public WorksheetManager fetchById(Integer id) throws Exception {
        WorksheetManager manager;
        WorksheetViewDO  data;

        data    = EJBFactory.getWorksheet().fetchById(id);
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

    public WorksheetManager fetchWithItemsAndNotes(Integer id) throws Exception {
        WorksheetManager manager;

        manager = fetchById(id);
        manager.getItems();
        manager.getNotes();

        return manager;
    }

    public WorksheetManager add(WorksheetManager manager) throws Exception {
        Integer                 id;
        Iterator<SampleManager> iter;
        InstrumentLogDO         ilDO;
        InstrumentLogLocal      il;
        LockLocal               lock;
        SampleManager           sManager;

        EJBFactory.getWorksheet().add(manager.getWorksheet());
        id = manager.getWorksheet().getId();

        lock = EJBFactory.getLock();
        if (manager.items != null) {
            manager.getItems().setWorksheetId(id);
            manager.getItems().add();
            
            iter = manager.getSampleManagers().values().iterator();
            while (iter.hasNext()) {
                sManager = (SampleManager) iter.next();
                if (manager.getLockedManagers().containsKey(sManager.getSample().getAccessionNumber())) {
                    lock.validateLock(ReferenceTable.SAMPLE, sManager.getSample().getId());
                    sManager.update();
                    lock.unlock(ReferenceTable.SAMPLE, sManager.getSample().getId());  
                    manager.getLockedManagers().remove(sManager.getSample().getAccessionNumber());
                }
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
        Integer                 id;
        Iterator<SampleManager> iter;
        LockLocal               lock;
        SampleManager           sManager;

        EJBFactory.getWorksheet().update(manager.getWorksheet());
        id = manager.getWorksheet().getId();
        
        lock = EJBFactory.getLock();
        if (manager.items != null) {
            manager.getItems().setWorksheetId(id);
            manager.getItems().update();
            
            iter = manager.getSampleManagers().values().iterator();
            while (iter.hasNext()) {
                sManager = (SampleManager) iter.next();
                if (manager.getLockedManagers().containsKey(sManager.getSample().getAccessionNumber())) {
                    lock.validateLock(ReferenceTable.SAMPLE, sManager.getSample().getId());
                    sManager.update();
                    lock.unlock(ReferenceTable.SAMPLE, sManager.getSample().getId());  
                    manager.getLockedManagers().remove(sManager.getSample().getAccessionNumber());
                }
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
        Iterator<SampleManager> iter;
        SampleManager           sManager;

        try {
            EJBFactory.getWorksheet().validate(manager.getWorksheet());
        } catch (Exception e) {
            DataBaseUtil.mergeException(errorList, e);
        }

        if (manager.items != null) {
            manager.getItems().validate(errorList);

            iter = manager.getSampleManagers().values().iterator();
            while (iter.hasNext()) {
                sManager = (SampleManager) iter.next();
                try {
                    sManager.validate();
                } catch (Exception e) {
                    DataBaseUtil.mergeException(errorList, e);
                }
            }
        }
    }
}
