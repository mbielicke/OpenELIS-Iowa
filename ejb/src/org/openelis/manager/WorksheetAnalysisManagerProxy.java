
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
import org.openelis.domain.QcAnalyteViewDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.TestWorksheetAnalyteViewDO;
import org.openelis.domain.WorksheetAnalysisDO;
import org.openelis.domain.WorksheetQcResultViewDO;
import org.openelis.domain.WorksheetResultViewDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.SampleLocal;
import org.openelis.local.SampleManagerLocal;
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
    
    public WorksheetAnalysisManager add(WorksheetAnalysisManager manager, HashMap<Integer,Integer> idHash, HashMap<Integer,SampleManager> sManagers) throws Exception {
        int                 i;
        Integer             oldId, qcLinkId;
        WorksheetAnalysisDO analysis;
        
        manager.setNotDone(false);
        for (i = 0; i < manager.count(); i++) {
            analysis = manager.getWorksheetAnalysisAt(i);
            
            if (analysis.getWorksheetAnalysisId() == null) {
                if (!idHash.containsKey(analysis.getId())) {
                    oldId = analysis.getId();
                    add(manager, analysis, i, sManagers);
                    
                    idHash.put(oldId, analysis.getId());
                    idHash.put(analysis.getId(), null);
                }
            } else if (analysis.getWorksheetAnalysisId() < 0) {
                qcLinkId = idHash.get(analysis.getWorksheetAnalysisId());
                
                if (qcLinkId != null) {
                    oldId = analysis.getId();
                    analysis.setWorksheetAnalysisId(qcLinkId);
                    add(manager, analysis, i, sManagers);
                    
                    idHash.put(oldId, analysis.getId());
                    idHash.put(analysis.getId(), null);
                } else {
                    manager.setNotDone(true);
                }
            } else if (!idHash.containsKey(analysis.getId())) {
                qcLinkId = idHash.get(analysis.getWorksheetAnalysisId());
                
                if (qcLinkId == null) {
                    oldId = analysis.getId();
                    add(manager, analysis, i, sManagers);
                    
                    idHash.put(oldId, analysis.getId());
                    if (!oldId.equals(analysis.getId()))
                        idHash.put(analysis.getId(), null);
                }
            }
        }
        
        return manager;
    }

    public WorksheetAnalysisManager update(WorksheetAnalysisManager manager, HashMap<Integer,Integer> idHash, HashMap<Integer,SampleManager> sManagers) throws Exception {
        int                 i;
        Integer             oldId, qcLinkId;
        WorksheetAnalysisDO analysis;
        
        manager.setNotDone(false);
        for (i = 0; i < manager.count(); i++) {
            analysis = manager.getWorksheetAnalysisAt(i);
            
            if (analysis.getWorksheetAnalysisId() == null) {
                if (!idHash.containsKey(analysis.getId())) {
                    oldId = analysis.getId();
                    update(manager, analysis, i, sManagers);
                    
                    idHash.put(oldId, analysis.getId());
                    idHash.put(analysis.getId(), null);
                }
            } else if (analysis.getWorksheetAnalysisId() < 0) {
                qcLinkId = idHash.get(analysis.getWorksheetAnalysisId());
                
                if (qcLinkId != null) {
                    oldId = analysis.getId();
                    analysis.setWorksheetAnalysisId(qcLinkId);
                    update(manager, analysis, i, sManagers);
                    
                    idHash.put(oldId, analysis.getId());
                    idHash.put(analysis.getId(), null);
                } else {
                    manager.setNotDone(true);
                }
            } else if (!idHash.containsKey(analysis.getId())) {
                qcLinkId = idHash.get(analysis.getWorksheetAnalysisId());
                
                if (qcLinkId == null) {
                    oldId = analysis.getId();
                    update(manager, analysis, i, sManagers);
                    
                    idHash.put(oldId, analysis.getId());
                    if (!oldId.equals(analysis.getId()))
                        idHash.put(analysis.getId(), null);
                }
            }
        }
        
        return manager;
    }

    public void add(WorksheetAnalysisManager manager, WorksheetAnalysisDO analysis, int i, HashMap<Integer,SampleManager> sManagers) throws Exception {
        boolean                   doBreak;
        int                       j, k;
        String                    qcAccessionNumber;
        AnalysisViewDO            aVDO;
        AnalysisManager           aManager;
        AnalysisResultManager     arManager;
        DictionaryDO              newStatus;
        QcManager                 qcManager;
        SampleDO                  sample;
        SampleItemManager         siManager;
        SampleManager             sManager;
        WorksheetAnalysisListItem listItem;
        WorksheetAnalysisLocal    local;
        WorksheetQcResultManager  wqrManager;
        WorksheetResultManager    wrManager;
        
        local = local();

        if (analysis.getQcId() != null) {
            //
            // Rewrite temporary QC accession number
            //
            qcAccessionNumber = analysis.getAccessionNumber();
            if (qcAccessionNumber.startsWith("X."))
                analysis.setAccessionNumber(qcAccessionNumber.replaceFirst("X", manager.getWorksheetId().toString()));
            qcManager = QcManager.fetchById(analysis.getQcId());
            wqrManager = manager.getWorksheetQcResultAt(0);
            initializeWorksheetQcResults(qcManager, wqrManager);
        } else if (analysis.getAnalysisId() != null) {
            //
            // Set Analysis status to Initiated and validate/update the sample
            //
            doBreak = false;
            newStatus = dictionaryLocal().fetchBySystemName("analysis_initiated");
            sample = sampleLocal().fetchByAccessionNumber(Integer.valueOf(analysis.getAccessionNumber()));
            //
            // Keep a hash map of sample managers so we only allocate one per
            // sample to avoid update collisions for multiple analyses on the
            // same sample
            //
            if (sManagers.containsKey(sample.getId())) {
                sManager = sManagers.get(sample.getId());
            } else {
                sManager = sampleManagerLocal().fetchForUpdate(sample.getId());
                sManagers.put(sample.getId(), sManager);
            }
            siManager = sManager.getSampleItems();
            for (j = 0; j < siManager.count(); j++) {
                aManager = siManager.getAnalysisAt(j);
                for (k = 0; k < aManager.count(); k++) {
                    aVDO = aManager.getAnalysisAt(k);
                    if (analysis.getAnalysisId().equals(aVDO.getId())) {
                        aVDO.setStatusId(newStatus.getId());
                        arManager = aManager.getAnalysisResultAt(k);
                        wrManager = manager.getWorksheetResultAt(i);
                        initializeWorksheetResults(aVDO, arManager, wrManager);
                        doBreak = true;
                        break;
                    }
                }
                if (doBreak)
                    break;
            }
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

    public void update(WorksheetAnalysisManager manager, WorksheetAnalysisDO analysis, int i, HashMap<Integer,SampleManager> sManagers) throws Exception {
        boolean                   doBreak;
        int                       /*j, */k, l;
        AnalysisViewDO            aVDO;
        AnalysisManager           aManager;
        DictionaryDO              newStatus;
        SampleDO                  sample;
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
                sample = sampleLocal().fetchByAccessionNumber(Integer.valueOf(analysis.getAccessionNumber()));
                //
                // Keep a hash map of sample managers so we only allocate one per
                // sample to avoid update collisions for multiple analyses on the
                // same sample
                //
                if (sManagers.containsKey(sample.getId())) {
                    sManager = sManagers.get(sample.getId());
                } else {
                    sManager = sampleManagerLocal().fetchForUpdate(sample.getId());
                    sManagers.put(sample.getId(), sManager);
                }
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

    public void validate(WorksheetAnalysisManager manager, ValidationErrorsList errorList) throws Exception {
        int                       i;
        WorksheetAnalysisListItem listItem;
        WorksheetAnalysisLocal    local;

        local = local();
        for (i = 0; i < manager.count(); i++) {
            try {
                local.validate(manager.getWorksheetAnalysisAt(i));
            } catch (Exception e) {
//                DataBaseUtil.mergeException(errorList, e, "itemTable", i);
                DataBaseUtil.mergeException(errorList, e);
            }
            
            listItem = manager.getItemAt(i);
            if (manager.getWorksheetAnalysisAt(i).getAnalysisId() != null &&
                listItem.worksheetResult != null)
                manager.getWorksheetResultAt(i).validate(errorList);
            else if (manager.getWorksheetAnalysisAt(i).getQcId() != null &&
                     listItem.worksheetQcResult != null)
                manager.getWorksheetQcResultAt(i).validate(errorList);
        }
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

    private SampleLocal sampleLocal() {
        InitialContext ctx;
        
        try {
            ctx = new InitialContext();
            return (SampleLocal)ctx.lookup("openelis/SampleBean/local");
        } catch(Exception e) {
             System.out.println(e.getMessage());
             return null;
        }
    }

    private SampleManagerLocal sampleManagerLocal() {
        InitialContext ctx;
        
        try {
            ctx = new InitialContext();
            return (SampleManagerLocal)ctx.lookup("openelis/SampleManagerBean/local");
        } catch(Exception e) {
             System.out.println(e.getMessage());
             return null;
        }
    }

    /**
     * Loads the WorksheetResultManager from the provided AnalysisManager. 
     */
    private void initializeWorksheetResults(AnalysisViewDO aVDO, AnalysisResultManager arManager,
                                            WorksheetResultManager wrManager) throws Exception {
        int                                         i, j;
        ArrayList<ResultViewDO>                     resultRow;
        ArrayList<ArrayList<ResultViewDO>>          results;
        HashMap<Integer,TestWorksheetAnalyteViewDO> twAnalytes;
        ResultViewDO                                result;
        TestWorksheetAnalyteViewDO                  twaVDO;
        TestWorksheetManager                        twManager;
        WorksheetResultViewDO                       wrVDO;

        twAnalytes = new HashMap<Integer,TestWorksheetAnalyteViewDO>();
        twManager = TestWorksheetManager.fetchByTestId(aVDO.getTestId());
        for (i = 0; i < twManager.analyteCount(); i++) {
            twaVDO = twManager.getAnalyteAt(i);
            twAnalytes.put(twaVDO.getTestAnalyteId(), twaVDO);
        }

        results = arManager.getResults();
        for (i = 0; i < results.size(); i++) {
            resultRow = results.get(i);
            result = resultRow.get(0);
            if (twAnalytes.containsKey(result.getTestAnalyteId())) {
                for (j = 0; j < resultRow.size(); j++) {
                    result = resultRow.get(j);
                    wrVDO = new WorksheetResultViewDO();
                    wrVDO.setTestAnalyteId(result.getTestAnalyteId());
                    wrVDO.setIsColumn(result.getIsColumn());
                    wrVDO.setSortOrder(i+1);
                    wrVDO.setAnalyteId(result.getAnalyteId());
                    wrVDO.setTypeId(result.getTypeId());
                    wrVDO.setAnalyteName(result.getAnalyte());
                    wrManager.addWorksheetResult(wrVDO);
                }
            }           
        }
    }

    /**
     * Loads the WorksheetQcResultManager from the provided QcManager. 
     */
    private void initializeWorksheetQcResults(QcManager qcManager, WorksheetQcResultManager wqrManager) throws Exception {
        int                     i;
        QcAnalyteViewDO         qcaVDO;
        QcAnalyteManager        qcaManager;
        WorksheetQcResultViewDO wqrVDO;

        qcaManager = qcManager.getAnalytes();
        for (i = 0; i < qcaManager.count(); i++) {
            qcaVDO = qcaManager.getAnalyteAt(i);
            wqrVDO = new WorksheetQcResultViewDO();
            wqrVDO.setSortOrder(i+1);
            wqrVDO.setQcAnalyteId(qcaVDO.getId());
            wqrVDO.setAnalyteName(qcaVDO.getAnalyteName());
            wqrVDO.setTypeId(qcaVDO.getTypeId());
            wqrVDO.setValue(qcaVDO.getValue());
            wqrManager.addWorksheetQcResult(wqrVDO);
        }
    }
}
