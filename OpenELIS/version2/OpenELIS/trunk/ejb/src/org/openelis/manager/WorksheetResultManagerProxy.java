
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

import javax.naming.InitialContext;

import org.openelis.domain.WorksheetResultDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.WorksheetResultLocal;
import org.openelis.manager.WorksheetResultManager;
import org.openelis.utilcommon.DataBaseUtil;

public class WorksheetResultManagerProxy {
    
    public WorksheetResultManager fetchByWorksheetAnalysisId(Integer id) throws Exception {
        int                            i;
        WorksheetResultManager       manager;
        ArrayList<WorksheetResultDO> results;
        
        results = local().fetchByWorksheetAnalysisId(id);
        manager = WorksheetResultManager.getInstance();
        manager.setWorksheetAnalysisId(id);
        for (i = 0; i < results.size(); i++)
            manager.addWorksheetResult(results.get(i));
        
        return manager;
    }
    
    public WorksheetResultManager add(WorksheetResultManager manager) throws Exception {
        int                    i;
        WorksheetResultLocal local;
        WorksheetResultDO    result;
        
        local = local();
        for (i = 0; i < manager.count(); i++) {
            result = manager.getWorksheetResultAt(i);
            result.setWorksheetAnalysisId(manager.getWorksheetAnalysisId());
            local.add(result);
        }
        
        return manager;
    }

    public WorksheetResultManager update(WorksheetResultManager manager) throws Exception {
        int                    i, j;
        WorksheetResultLocal local;
        WorksheetResultDO    result;
        
        local = local();
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

    public void validate(WorksheetResultManager manager) throws Exception {
        int                  i;
        ValidationErrorsList list;
        WorksheetResultLocal local;

        local = local();
        list  = new ValidationErrorsList();
        for (i = 0; i < manager.count(); i++) {
            try {
                local.validate(manager.getWorksheetResultAt(i));
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "itemTable", i);
            }
        }
        if (list.size() > 0)
            throw list;
    }

    private WorksheetResultLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (WorksheetResultLocal)ctx.lookup("openelis/WorksheetResultBean/local");
        } catch(Exception e) {
             System.out.println(e.getMessage());
             return null;
        }
    }
}
