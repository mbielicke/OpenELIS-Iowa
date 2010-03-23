
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
import java.util.HashMap;

import javax.naming.InitialContext;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.WorksheetAnalysisDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.DictionaryLocal;
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
    
    public WorksheetAnalysisManager add(WorksheetAnalysisManager manager, HashMap<Integer,Integer> idHash) throws Exception {
        int                 i;
        Integer             oldId, qcLinkId;
        WorksheetAnalysisDO analysis;
        
        manager.setNotDone(false);
        for (i = 0; i < manager.count(); i++) {
            analysis = manager.getWorksheetAnalysisAt(i);
            
            if (analysis.getWorksheetAnalysisId() == null) {
                if (!idHash.containsKey(analysis.getId())) {
                    oldId = analysis.getId();
                    add(manager, analysis, i);
                    
                    idHash.put(oldId, analysis.getId());
                    idHash.put(analysis.getId(), null);
                }
            } else if (analysis.getWorksheetAnalysisId() < 0) {
                qcLinkId = idHash.get(analysis.getWorksheetAnalysisId());
                
                if (qcLinkId != null) {
                    oldId = analysis.getId();
                    analysis.setWorksheetAnalysisId(qcLinkId);
                    add(manager, analysis, i);
                    
                    idHash.put(oldId, analysis.getId());
                    idHash.put(analysis.getId(), null);
                } else {
                    manager.setNotDone(true);
                }
            } else if (!idHash.containsKey(analysis.getId())) {
                qcLinkId = idHash.get(analysis.getWorksheetAnalysisId());
                
                if (qcLinkId == null) {
                    oldId = analysis.getId();
                    add(manager, analysis, i);
                    
                    idHash.put(oldId, analysis.getId());
                    if (!oldId.equals(analysis.getId()))
                        idHash.put(analysis.getId(), null);
                }
            }
        }
        
        return manager;
    }

    public WorksheetAnalysisManager update(WorksheetAnalysisManager manager, HashMap<Integer,Integer> idHash) throws Exception {
        int                 i;
        Integer             oldId, qcLinkId;
        WorksheetAnalysisDO analysis;
        
        manager.setNotDone(false);
        for (i = 0; i < manager.count(); i++) {
            analysis = manager.getWorksheetAnalysisAt(i);
            
            if (analysis.getWorksheetAnalysisId() == null) {
                if (!idHash.containsKey(analysis.getId())) {
                    oldId = analysis.getId();
                    update(manager, analysis, i);
                    
                    idHash.put(oldId, analysis.getId());
                    idHash.put(analysis.getId(), null);
                }
            } else if (analysis.getWorksheetAnalysisId() < 0) {
                qcLinkId = idHash.get(analysis.getWorksheetAnalysisId());
                
                if (qcLinkId != null) {
                    oldId = analysis.getId();
                    analysis.setWorksheetAnalysisId(qcLinkId);
                    update(manager, analysis, i);
                    
                    idHash.put(oldId, analysis.getId());
                    idHash.put(analysis.getId(), null);
                } else {
                    manager.setNotDone(true);
                }
            } else if (!idHash.containsKey(analysis.getId())) {
                qcLinkId = idHash.get(analysis.getWorksheetAnalysisId());
                
                if (qcLinkId == null) {
                    oldId = analysis.getId();
                    update(manager, analysis, i);
                    
                    idHash.put(oldId, analysis.getId());
                    if (!oldId.equals(analysis.getId()))
                        idHash.put(analysis.getId(), null);
                }
            }
        }
        
        return manager;
    }

    public void add(WorksheetAnalysisManager manager, WorksheetAnalysisDO analysis, int i) throws Exception {
        boolean                   doBreak;
        int                       j, k;
        String                    qcAccessionNumber;
        AnalysisViewDO            aVDO;
        AnalysisManager           aManager;
        DictionaryDO              newStatus;
        SampleItemManager         siManager;
        SampleManager             sManager;
        WorksheetAnalysisListItem listItem;
        WorksheetAnalysisLocal    local;
        
        local = local();

        if (analysis.getQcId() != null) {
            //
            // Rewrite temporary QC accession number
            //
            qcAccessionNumber = analysis.getAccessionNumber();
            if (qcAccessionNumber.startsWith("X.")) {
                analysis.setAccessionNumber(qcAccessionNumber.replaceFirst("X", manager.getWorksheetId().toString()));
            }
        } else if (analysis.getAnalysisId() != null) {
            //
            // Set Analysis status to Initiated and validate/update the sample
            //
            doBreak = false;
            newStatus = dictionaryLocal().fetchBySystemName("analysis_initiated");
            sManager = SampleManager.fetchByAccessionNumber(Integer.valueOf(analysis.getAccessionNumber()));
            siManager = sManager.getSampleItems();
            for (j = 0; j < siManager.count(); j++) {
                aManager = siManager.getAnalysisAt(j);
                for (k = 0; k < aManager.count(); k++) {
                    aVDO = aManager.getAnalysisAt(k);
                    if (analysis.getAnalysisId().equals(aVDO.getId())) {
                        aVDO.setStatusId(newStatus.getId());
                        doBreak = true;
                        break;
                    }
                }
                if (doBreak)
                    break;
            }
            sManager.validate();
            sManager.update();
        }
            
        analysis.setWorksheetItemId(manager.getWorksheetItemId());
        local.add(analysis);

        listItem = manager.getItemAt(i);
        if (analysis.getAnalysisId() != null && listItem.worksheetResult != null) {
            manager.getWorksheetResultAt(i).setWorksheetAnalysisId(analysis.getId());
            manager.getWorksheetResultAt(i).add();
        } else if (analysis.getQcId() != null && listItem.worksheetQcResult != null) {
            manager.getWorksheetQcResultAt(i).setWorksheetAnalysisId(analysis.getId());
            manager.getWorksheetQcResultAt(i).add();
        }
    }

    public void update(WorksheetAnalysisManager manager, WorksheetAnalysisDO analysis, int i) throws Exception {
        boolean                   doBreak;
        int                       /*j, */k, l;
        AnalysisViewDO            aVDO;
        AnalysisManager           aManager;
        DictionaryDO              newStatus;
        SampleItemManager         siManager;
        SampleManager             sManager;
        WorksheetAnalysisListItem listItem;
        WorksheetAnalysisLocal    local;
        
        local = local();
//        for (j = 0; j < manager.deleteCount(); j++)
//            local.delete(manager.getDeletedAt(j).worksheetAnalysis);
        
        if (analysis.getId() == null) {
            assert false : "not supported";
        } else {
            if (analysis.getAnalysisId() != null) {
                doBreak = false;
                newStatus = dictionaryLocal().fetchBySystemName("analysis_completed");
                sManager = SampleManager.fetchByAccessionNumber(Integer.valueOf(analysis.getAccessionNumber()));
                siManager = sManager.getSampleItems();
                for (k = 0; k < siManager.count(); k++) {
                    aManager = siManager.getAnalysisAt(k);
                    for (l = 0; l < aManager.count(); l++) {
                        aVDO = aManager.getAnalysisAt(l);
                        if (analysis.getAnalysisId().equals(aVDO.getId())) {
                            aVDO.setStatusId(newStatus.getId());
                            doBreak = true;
                            break;
                        }
                    }
                    if (doBreak)
                        break;
                }
                sManager.validate();
                sManager.update();
            }
                
            local.update(analysis);
        }
    
        listItem = manager.getItemAt(i);
        if (analysis.getAnalysisId() != null && listItem.worksheetResult != null) {
            manager.getWorksheetResultAt(i).setWorksheetAnalysisId(analysis.getId());
            manager.getWorksheetResultAt(i).update();
        } else if (analysis.getQcId() != null && listItem.worksheetQcResult != null) {
            manager.getWorksheetQcResultAt(i).setWorksheetAnalysisId(analysis.getId());
            manager.getWorksheetQcResultAt(i).update();
        }
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
                if (manager.getWorksheetAnalysisAt(i).getAnalysisId() != null &&
                    listItem.worksheetResult != null)
                    manager.getWorksheetResultAt(i).validate();
                else if (manager.getWorksheetAnalysisAt(i).getQcId() != null &&
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

    private DictionaryLocal dictionaryLocal() {
        InitialContext ctx;
        
        try {
            ctx = new InitialContext();
            return (DictionaryLocal)ctx.lookup("openelis/DictionaryBean/local");
        } catch(Exception e) {
             System.out.println(e.getMessage());
             return null;
        }
    }
}
