
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

import java.util.ArrayList;

import org.openelis.domain.WorksheetQcResultViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.WorksheetQcResultLocal;
import org.openelis.manager.WorksheetQcResultManager;
import org.openelis.utils.EJBFactory;

public class WorksheetQcResultManagerProxy {
    
    public WorksheetQcResultManager fetchByWorksheetAnalysisId(Integer id) throws Exception {
        int                            i;
        WorksheetQcResultManager       manager;
        ArrayList<WorksheetQcResultViewDO> qcResults;
        
        qcResults = EJBFactory.getWorksheetQcResult().fetchByWorksheetAnalysisId(id);
        manager = WorksheetQcResultManager.getInstance();
        manager.setWorksheetAnalysisId(id);
        for (i = 0; i < qcResults.size(); i++)
            manager.addWorksheetQcResult(qcResults.get(i));
        
        return manager;
    }
    
    public WorksheetQcResultManager add(WorksheetQcResultManager manager) throws Exception {
        int                    i;
        WorksheetQcResultLocal local;
        WorksheetQcResultViewDO    qcResult;
        
        local = EJBFactory.getWorksheetQcResult();
        for (i = 0; i < manager.count(); i++) {
            qcResult = manager.getWorksheetQcResultAt(i);
            qcResult.setWorksheetAnalysisId(manager.getWorksheetAnalysisId());
            local.add(qcResult);
        }
        
        return manager;
    }

    public WorksheetQcResultManager update(WorksheetQcResultManager manager) throws Exception {
        int                    i, j;
        WorksheetQcResultLocal local;
        WorksheetQcResultViewDO    qcResult;
        
        local = EJBFactory.getWorksheetQcResult();
        for (j = 0; j < manager.deleteCount(); j++)
            local.delete(manager.getDeletedAt(j));
        
        for (i = 0; i < manager.count(); i++) {
            qcResult = manager.getWorksheetQcResultAt(i);
            
            if (qcResult.getId() == null) {
                qcResult.setWorksheetAnalysisId(manager.getWorksheetAnalysisId());
                local.add(qcResult);
            } else {
                local.update(qcResult);
            }
        }

        return manager;
    }

    public void validate(WorksheetQcResultManager manager, ValidationErrorsList errorList) {
        int                    i;
        WorksheetQcResultLocal local;

        local = EJBFactory.getWorksheetQcResult();
        for (i = 0; i < manager.count(); i++) {
            try {
                local.validate(manager.getWorksheetQcResultAt(i));
            } catch (Exception e) {
//                DataBaseUtil.mergeException(errorList, e, "itemTable", i);
                DataBaseUtil.mergeException(errorList, e);
            }
        }
    }
}
