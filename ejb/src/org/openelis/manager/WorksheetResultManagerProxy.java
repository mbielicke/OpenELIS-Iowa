
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

import org.openelis.bean.WorksheetResultBean;
import org.openelis.domain.WorksheetResultViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class WorksheetResultManagerProxy {
    
    public WorksheetResultManager fetchByWorksheetAnalysisId(Integer id) throws Exception {
        int                            i;
        WorksheetResultManager       manager;
        ArrayList<WorksheetResultViewDO> results;
        
        results = EJBFactory.getWorksheetResult().fetchByWorksheetAnalysisId(id);
        manager = WorksheetResultManager.getInstance();
        manager.setWorksheetAnalysisId(id);
        for (i = 0; i < results.size(); i++)
            manager.addWorksheetResult(results.get(i));
        
        return manager;
    }
    
    public WorksheetResultManager add(WorksheetResultManager manager) throws Exception {
        int                    i;
        WorksheetResultBean local;
        WorksheetResultViewDO    result;
        
        local = EJBFactory.getWorksheetResult();
        for (i = 0; i < manager.count(); i++) {
            result = manager.getWorksheetResultAt(i);
            result.setWorksheetAnalysisId(manager.getWorksheetAnalysisId());
            local.add(result);
        }
        
        return manager;
    }

    public WorksheetResultManager update(WorksheetResultManager manager) throws Exception {
        int                    i, j;
        WorksheetResultBean local;
        WorksheetResultViewDO    result;
        
        local = EJBFactory.getWorksheetResult();
        for (j = 0; j < manager.deleteCount(); j++)
            local.delete(manager.getDeletedAt(j));
        
        for (i = 0; i < manager.count(); i++) {
            result = manager.getWorksheetResultAt(i);
            
            if (result.getId() == null) {
                result.setWorksheetAnalysisId(manager.getWorksheetAnalysisId());
                local.add(result);
            } else {
                local.update(result);
            }
        }

        return manager;
    }

    public void validate(WorksheetResultManager manager, ValidationErrorsList errorList) {
        int                  i;
        WorksheetResultBean local;

        local = EJBFactory.getWorksheetResult();
        for (i = 0; i < manager.count(); i++) {
            try {
                local.validate(manager.getWorksheetResultAt(i));
            } catch (Exception e) {
//                DataBaseUtil.mergeException(errorList, e, "itemTable", i);
                DataBaseUtil.mergeException(errorList, e);
            }
        }
    }
}
