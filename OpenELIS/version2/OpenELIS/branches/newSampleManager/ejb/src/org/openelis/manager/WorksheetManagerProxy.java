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

import org.openelis.bean.DictionaryBean;
import org.openelis.bean.LockBean;
import org.openelis.bean.SessionCacheBean;
import org.openelis.domain.Constants;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class WorksheetManagerProxy {

    public WorksheetManager fetchById(Integer id) throws Exception {
        SystemUserVO user;
        WorksheetManager manager;
        WorksheetViewDO data;

        data = EJBFactory.getWorksheet().fetchById(id);
        if (data != null && data.getSystemUserId() != null) {
            user = EJBFactory.getUserCache().getSystemUser(data.getSystemUserId());
            if (user != null)
                data.setSystemUser(user.getLoginName());
        }
        
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

    public WorksheetManager fetchWithAllData(Integer id) throws Exception {
        int i, j;
        WorksheetManager manager;
        WorksheetItemManager wiMan;
        WorksheetAnalysisManager waMan;

        manager = fetchById(id);
        wiMan = manager.getItems();
        for (i = 0; i < wiMan.count(); i++) {
            waMan = wiMan.getWorksheetAnalysisAt(i);
            for (j = 0; j < waMan.count(); j++) {
                if (waMan.getWorksheetAnalysisAt(j).getAnalysisId() != null) {
                    waMan.getBundleAt(j);
                    waMan.getWorksheetResultAt(j);
                }
                if (waMan.getWorksheetAnalysisAt(j).getQcLotId() != null)
                    waMan.getWorksheetQcResultAt(j);
            }
        }
        manager.getNotes();

        return manager;
    }

    public WorksheetManager add(WorksheetManager manager) throws Exception {
        Integer id;
        Iterator<SampleManager> iter;
        LockBean lock;
        SampleManager sManager;

        EJBFactory.getWorksheet().add(manager.getWorksheet());
        id = manager.getWorksheet().getId();

        lock = EJBFactory.getLock();
        if (manager.items != null) {
            manager.getItems().setWorksheet(manager.getWorksheet());
            manager.getItems().add();

            iter = manager.getSampleManagers().values().iterator();
            while (iter.hasNext()) {
                sManager = (SampleManager) iter.next();
                if (manager.getLockedManagers()
                           .containsKey(sManager.getSample().getAccessionNumber())) {
                    lock.validateLock(Constants.table().SAMPLE, sManager.getSample().getId());
                    sManager.update();
                    lock.unlock(Constants.table().SAMPLE, sManager.getSample().getId());
                    manager.getLockedManagers().remove(sManager.getSample()
                                                               .getAccessionNumber());
                }
            }
        }

        if (manager.notes != null) {
            manager.getNotes().setReferenceId(id);
            manager.getNotes().setReferenceTableId(Constants.table().WORKSHEET);
            manager.getNotes().add();
        }

        return manager;
    }

    public WorksheetManager update(WorksheetManager manager) throws Exception {
        int sManIndex, sManCount;
        Integer id;
        Iterator<SampleManager> iter;
        LockBean lock;
        ReportStatus status;
        SampleManager sManager;
        SessionCacheBean session;

        session = EJBFactory.getSessionCache();

        EJBFactory.getWorksheet().update(manager.getWorksheet());
        id = manager.getWorksheet().getId();

        lock = EJBFactory.getLock();
        if (manager.items != null) {
            manager.getItems().setWorksheet(manager.getWorksheet());
            manager.getItems().update();

            iter = manager.getSampleManagers().values().iterator();
            sManCount = manager.getSampleManagers().values().size();
            sManIndex = 0;
            status = (ReportStatus) session.getAttribute("WorksheetUpdateStatus");
            while (iter.hasNext()) {
                sManager = (SampleManager) iter.next();
                if (manager.getLockedManagers()
                           .containsKey(sManager.getSample().getAccessionNumber())) {
                    lock.validateLock(Constants.table().SAMPLE, sManager.getSample().getId());
                    sManager.update();
                    lock.unlock(Constants.table().SAMPLE, sManager.getSample().getId());
                    manager.getLockedManagers().remove(sManager.getSample()
                                                               .getAccessionNumber());
                }
                status.setPercentComplete((++sManIndex / sManCount) * 40 + 55);
                session.setAttribute("WorksheetUpdateStatus", status);
            }
        }

        if (manager.notes != null) {
            manager.getNotes().setReferenceId(id);
            manager.getNotes().setReferenceTableId(Constants.table().WORKSHEET);
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
        SampleManager sManager;

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