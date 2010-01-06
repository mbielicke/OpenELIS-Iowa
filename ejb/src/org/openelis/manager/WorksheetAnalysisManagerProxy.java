
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

import org.openelis.domain.ReferenceTable;
import org.openelis.domain.WorksheetAnalysisDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.WorksheetAnalysisLocal;
import org.openelis.manager.WorksheetAnalysisManager;
import org.openelis.manager.WorksheetAnalysisManager.WorksheetAnalysisListItem;
import org.openelis.utilcommon.DataBaseUtil;

public class WorksheetAnalysisManagerProxy {
    
    public WorksheetAnalysisManager fetchByWorksheetItemId(Integer id) throws Exception {
        int                            i;
        WorksheetAnalysisManager       manager;
        ArrayList<WorksheetAnalysisDO> analyses;
        
        analyses = local().fetchByWorksheetItemId(id);
        manager = WorksheetAnalysisManager.getInstance();
        manager.setWorksheetItemId(id);
        for (i = 0; i < analyses.size(); i++)
            manager.addWorksheetAnalysis(analyses.get(i));
        
        return manager;
    }
    
    public WorksheetAnalysisManager add(WorksheetAnalysisManager manager) throws Exception {
        int                       i;
        WorksheetAnalysisListItem listItem;
        WorksheetAnalysisLocal    local;
        WorksheetAnalysisDO       analysis;
        
        local = local();
        for (i = 0; i < manager.count(); i++) {
            analysis = manager.getWorksheetAnalysisAt(i);
            analysis.setWorksheetItemId(manager.getWorksheetItemId());
            local.add(analysis);

            listItem = manager.getItemAt(i);
            if (analysis.getReferenceTableId() == ReferenceTable.ANALYSIS &&
                listItem.worksheetResult != null) {
                manager.getWorksheetResultAt(i).setWorksheetAnalysisId(analysis.getId());
                manager.getWorksheetResultAt(i).add();
            } else if (analysis.getReferenceTableId() == ReferenceTable.QC &&
                       listItem.worksheetQcResult != null) {
                manager.getWorksheetQcResultAt(i).setWorksheetAnalysisId(analysis.getId());
                manager.getWorksheetQcResultAt(i).add();
            }
        }
        
        return manager;
    }

    public WorksheetAnalysisManager update(WorksheetAnalysisManager manager) throws Exception {
        int                       i, j;
        WorksheetAnalysisListItem listItem;
        WorksheetAnalysisLocal    local;
        WorksheetAnalysisDO       analysis;
        
        local = local();
        for (j = 0; j < manager.deleteCount(); j++)
            local.delete(manager.getDeletedAt(j).worksheetAnalysis);
        
        for (i = 0; i < manager.count(); i++) {
            analysis = manager.getWorksheetAnalysisAt(i);
            
            if (analysis.getId() == null) {
                analysis.setWorksheetItemId(manager.getWorksheetItemId());
                local.add(analysis);
            } else {
                local.update(analysis);
            }
        
            listItem = manager.getItemAt(i);
            if (analysis.getReferenceTableId() == ReferenceTable.ANALYSIS &&
                listItem.worksheetResult != null) {
                manager.getWorksheetResultAt(i).setWorksheetAnalysisId(analysis.getId());
                manager.getWorksheetResultAt(i).update();
            } else if (analysis.getReferenceTableId() == ReferenceTable.QC &&
                listItem.worksheetQcResult != null) {
                manager.getWorksheetQcResultAt(i).setWorksheetAnalysisId(analysis.getId());
                manager.getWorksheetQcResultAt(i).update();
            }
        }

        return manager;
    }

    public void validate(WorksheetAnalysisManager manager) throws Exception {
        int                       i;
        ValidationErrorsList      list;
        WorksheetAnalysisListItem listItem;
        WorksheetAnalysisLocal    local;

        local = local();
        list  = new ValidationErrorsList();
        for (i = 0; i < manager.count(); i++) {
            try {
                local.validate(manager.getWorksheetAnalysisAt(i));
                
                listItem = manager.getItemAt(i);
                if (manager.getWorksheetAnalysisAt(i).getReferenceTableId() == ReferenceTable.ANALYSIS &&
                    listItem.worksheetResult != null)
                    manager.getWorksheetResultAt(i).validate();
                else if (manager.getWorksheetAnalysisAt(i).getReferenceTableId() == ReferenceTable.QC &&
                         listItem.worksheetQcResult != null)
                    manager.getWorksheetQcResultAt(i).validate();
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "itemTable", i);
            }
        }
        if (list.size() > 0)
            throw list;
    }

    private WorksheetAnalysisLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (WorksheetAnalysisLocal)ctx.lookup("openelis/WorksheetAnalysisBean/local");
        } catch(Exception e) {
             System.out.println(e.getMessage());
             return null;
        }
    }
}
