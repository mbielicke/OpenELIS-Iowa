
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
import java.util.Date;
import java.util.HashMap;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.QcAnalyteViewDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.TestWorksheetAnalyteViewDO;
import org.openelis.domain.WorksheetAnalysisDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.domain.WorksheetQcResultViewDO;
import org.openelis.domain.WorksheetResultViewDO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.WorksheetAnalysisLocal;
import org.openelis.manager.WorksheetAnalysisManager;
import org.openelis.manager.WorksheetAnalysisManager.WorksheetAnalysisListItem;
import org.openelis.utils.EJBFactory;

public class WorksheetAnalysisManagerProxy {
    
    protected static Integer anLoggedInId, anInitiatedId, anCompletedId, anRequeueId,
                             anErrorInitiatedId, wsFailedRunId, wsVoidId;

    public WorksheetAnalysisManagerProxy() {
        DictionaryLocal l;

        if (anLoggedInId == null) {
            l = EJBFactory.getDictionary();

            try {
                anLoggedInId = l.fetchBySystemName("analysis_logged_in").getId();
                anInitiatedId = l.fetchBySystemName("analysis_initiated").getId();
                anCompletedId = l.fetchBySystemName("analysis_completed").getId();
                anRequeueId = l.fetchBySystemName("analysis_requeue").getId();
                anErrorInitiatedId = l.fetchBySystemName("analysis_error_initiated").getId();
                wsFailedRunId = l.fetchBySystemName("worksheet_failed").getId();
                wsVoidId = l.fetchBySystemName("worksheet_void").getId();
            } catch (Exception e) {
                e.printStackTrace();
                anLoggedInId = null;
            }
        }
    }
    
    public WorksheetAnalysisManager fetchByWorksheetItemId(Integer id) throws Exception {
        int                            i;
        ArrayList<WorksheetAnalysisDO> analyses;
        AnalysisViewDO                 aVDO;
        HashMap<Integer,SectionViewDO> sections;
        SectionViewDO                  sectionVDO;
        WorksheetViewDO                wVDO;
        WorksheetAnalysisDO            waDO;
        WorksheetAnalysisManager       waManager;
        WorksheetItemDO                wiDO;
        
        wiDO = EJBFactory.getWorksheetItem().fetchById(id);
        wVDO = EJBFactory.getWorksheet().fetchById(wiDO.getWorksheetId());
        analyses = EJBFactory.getWorksheetAnalysis().fetchByWorksheetItemId(id);
        sections = new HashMap<Integer,SectionViewDO>();
        waManager = WorksheetAnalysisManager.getInstance();
        waManager.setWorksheet(wVDO);
        waManager.setWorksheetItemId(id);
        for (i = 0; i < analyses.size(); i++) {
            waDO = analyses.get(i);
            waManager.addWorksheetAnalysis(waDO);
            if (waDO.getAnalysisId() != null) {
                aVDO = EJBFactory.getAnalysis().fetchById(waDO.getAnalysisId());
                sectionVDO = EJBFactory.getSection().fetchById(aVDO.getSectionId());
                if (!sections.containsKey(sectionVDO.getId()))
                    sections.put(sectionVDO.getId(), sectionVDO);
            }
        }
        waManager.setAnalysisSections(sections);
        
        return waManager;
    }
    
    public int add(WorksheetAnalysisManager manager, HashMap<Integer,Integer> idHash) throws Exception {
        boolean             unresolved;
        int                 i, numUnresolved;
        Integer             oldId, qcLinkId;
        WorksheetAnalysisDO analysis;

        numUnresolved = 0;
        for (i = 0; i < manager.count(); i++) {
            unresolved = false;
            analysis = manager.getWorksheetAnalysisAt(i);

            // try to resolve a negative related analysis id
            if (analysis.getWorksheetAnalysisId() != null && analysis.getWorksheetAnalysisId() < 0) {
                qcLinkId = idHash.get(analysis.getWorksheetAnalysisId());
                if (qcLinkId != null) {
                    analysis.setWorksheetAnalysisId(qcLinkId);
                } else {
                    unresolved = true;
                }
            }
            
            // if both the related analysis id is resolved, then add/update the
            // analysis and set the mappings in the hash, otherwise skip
            if (!unresolved) {
                if (!idHash.containsKey(analysis.getId())) {
                    if (analysis.getId() < 0) {
                        oldId = analysis.getId();
                        add(manager, analysis, i);
                        idHash.put(oldId, analysis.getId());
                    }
                    idHash.put(analysis.getId(), null);
                }
            } else {
                numUnresolved++;
            }
        }

        return numUnresolved;
    }

    public void add(WorksheetAnalysisManager manager, WorksheetAnalysisDO analysis, int i) throws Exception {
        boolean                   doBreak;
        int                       j, k;
        String                    accessionNumber;
        AnalysisViewDO            aVDO;
        AnalysisManager           aManager;
        AnalysisResultManager     arManager;
        QcManager                 qcManager;
        SampleDO                  sample;
        SampleItemManager         siManager;
        SampleManager             sManager;
        WorksheetAnalysisListItem listItem;
        WorksheetAnalysisLocal    local;
        WorksheetQcResultManager  wqrManager;
        WorksheetResultManager    wrManager;

        local = EJBFactory.getWorksheetAnalysis();

        if (analysis.getQcId() != null) {
            //
            // Rewrite temporary QC accession number
            //
            accessionNumber = analysis.getAccessionNumber();
            if (accessionNumber.startsWith("X."))
                analysis.setAccessionNumber(accessionNumber.replaceFirst("X", manager.getWorksheet().getId().toString()));
            
            //
            // We are only initiating records that were not added from another
            // worksheet
            //
            if ("N".equals(analysis.getIsFromOther())) {
                analysis.setQcStartedDate(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE));
                qcManager = QcManager.fetchById(analysis.getQcId());
                wqrManager = manager.getWorksheetQcResultAt(i);
                initializeWorksheetQcResults(qcManager, wqrManager);
            }
        } else if (analysis.getAnalysisId() != null) {
            //
            // Set Analysis status to Initiated and validate/update the sample
            //
            doBreak = false;
            //
            // Trim the 'D' off the front of the accession number for analyses
            // in a duplicate position on the worksheet
            //
            accessionNumber = analysis.getAccessionNumber();
            if (accessionNumber.startsWith("D"))
                accessionNumber = accessionNumber.substring(1);
            //
            // Keep a hash map of sample managers so we only allocate one per
            // sample to avoid update collisions for multiple analyses on the
            // same sample
            //
            sManager = manager.getLockedManagers().get(Integer.valueOf(accessionNumber));
            if (sManager == null) {
                sample = EJBFactory.getSample().fetchByAccessionNumber(Integer.valueOf(accessionNumber));
                sManager = EJBFactory.getSampleManager().fetchForUpdate(sample.getId());
                manager.getLockedManagers().put(sample.getAccessionNumber(), sManager);
                manager.getSampleManagers().put(sample.getAccessionNumber(), sManager);
            }
            siManager = sManager.getSampleItems();
            for (j = 0; j < siManager.count(); j++) {
                aManager = siManager.getAnalysisAt(j);
                for (k = 0; k < aManager.count(); k++) {
                    aVDO = aManager.getAnalysisAt(k);
                    if (analysis.getAnalysisId().equals(aVDO.getId())) {
                        //
                        // We are only initiating records that were not added from another
                        // worksheet
                        //
                        if ("N".equals(analysis.getIsFromOther())) {
                            arManager = aManager.getAnalysisResultAt(k);
                            // if the result records haven't been added via a full
                            // login, we need to add them now
                            if (arManager.rowCount() <= 0) {
                                try {
                                    arManager = AnalysisResultManager.fetchByTestId(aVDO.getTestId(), aVDO.getUnitOfMeasureId());
                                    aManager.setAnalysisResultAt(arManager, k);
                                } catch (NotFoundException nfE) {
                                    // ignore result not found error and leave the
                                    // empty AnalysisResultManager attached to the
                                    // AnalysisManger
                                }
                            }
                        
                            wrManager = manager.getWorksheetResultAt(i);
                            aManager.initiateAnalysisAt(k);
                            initializeWorksheetResults(aVDO, arManager, wrManager);
                        }
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

    public WorksheetAnalysisManager update(WorksheetAnalysisManager manager) throws Exception {
        boolean                   doBreak, clearDate;
        int                       i, k, l;
        Datetime                  createdDate, startedDate;
        String                    accessionNumber;
        AnalysisViewDO            aVDO;
        AnalysisManager           aManager;
        SampleDO                  sample;
        SampleItemManager         siManager;
        SampleManager             sManager;
        WorksheetAnalysisDO       analysis;
        WorksheetAnalysisListItem listItem;
        WorksheetAnalysisLocal    local;
        
        local = EJBFactory.getWorksheetAnalysis();
        for (i = 0; i < manager.count(); i++) {
            analysis = manager.getWorksheetAnalysisAt(i);

            if (analysis.getId() == null) {
                assert false : "not supported";
            } else {
                if (analysis.getAnalysisId() != null) {
                    //
                    // We are only updating records that were not added from another
                    // worksheet
                    //
                    if ("Y".equals(analysis.getIsFromOther()))
                        continue;
                    
                    doBreak = false;
                    //
                    // Trim the 'D' off the front of the accession number for analyses
                    // in a duplicate position on the worksheet
                    //
                    accessionNumber = analysis.getAccessionNumber();
                    if (accessionNumber.startsWith("D"))
                        accessionNumber = accessionNumber.substring(1);
                    //
                    // Keep a hash map of sample managers so we only allocate one per
                    // sample to avoid update collisions for multiple analyses on the
                    // same sample
                    //
                    sManager = manager.getLockedManagers().get(Integer.valueOf(accessionNumber));
                    if (sManager == null) {
                        sample = EJBFactory.getSample().fetchByAccessionNumber(Integer.valueOf(accessionNumber));
                        sManager = EJBFactory.getSampleManager().fetchForUpdate(sample.getId());
                        manager.getLockedManagers().put(sample.getAccessionNumber(), sManager);
                        manager.getSampleManagers().put(sample.getAccessionNumber(), sManager);
                    }
                    siManager = sManager.getSampleItems();
                    for (k = 0; k < siManager.count(); k++) {
                        aManager = siManager.getAnalysisAt(k);
                        for (l = 0; l < aManager.count(); l++) {
                            aVDO = aManager.getAnalysisAt(l);
                            if (analysis.getAnalysisId().equals(aVDO.getId())) {
                                if (wsFailedRunId.equals(manager.getWorksheet().getStatusId())) {
                                    if (anInitiatedId.equals(aVDO.getStatusId()) || anErrorInitiatedId.equals(aVDO.getStatusId()))
                                        aVDO.setStatusId(anRequeueId);
                                } else if (wsVoidId.equals(manager.getWorksheet().getStatusId())) {
                                    if (anInitiatedId.equals(aVDO.getStatusId()) || anErrorInitiatedId.equals(aVDO.getStatusId())) {
                                        createdDate = manager.getWorksheet().getCreatedDate();
                                        startedDate = aVDO.getStartedDate();
                                        //
                                        // only clear the started date if it was set
                                        // when this worksheet was created
                                        //
                                        if (startedDate != null &&
                                            (startedDate.equals(createdDate) || 
                                             (startedDate.after(createdDate) &&
                                              startedDate.before(new Date(manager.getWorksheet().getCreatedDate().getDate().getTime() + 60000)))))
                                            clearDate = true;
                                        else
                                            clearDate = false;
                                        aManager.unInitiateAnalysisAt(l, clearDate);
                                    }
                                } else {
                                    if (anLoggedInId.equals(aVDO.getStatusId()) ||
                                        anInitiatedId.equals(aVDO.getStatusId()) ||          
                                        anCompletedId.equals(aVDO.getStatusId())) {
                                        try {
                                            aManager.completeAnalysisAt(l);
                                        } catch (Exception ignE) {
                                            // ignoring errors cause by trying to complete
                                            // the analysis because they should not prevent
                                            // us from saving the record properly
                                        }
                                    }
                                }
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
        
        return manager;
    }

    public void validate(WorksheetAnalysisManager manager, ValidationErrorsList errorList) throws Exception {
        int                       i;
        WorksheetAnalysisDO       waDO;
        WorksheetAnalysisListItem listItem;
        WorksheetAnalysisLocal    local;

        local = EJBFactory.getWorksheetAnalysis();
        for (i = 0; i < manager.count(); i++) {
            waDO = manager.getWorksheetAnalysisAt(i);
            //
            // We are only validating records that were not added from another
            // worksheet
            //
            if ("Y".equals(waDO.getIsFromOther()))
                continue;
            
            try {
                local.validate(waDO);
            } catch (Exception e) {
                DataBaseUtil.mergeException(errorList, e);
            }
            
            listItem = manager.getItemAt(i);
            if (waDO.getAnalysisId() != null && listItem.worksheetResult != null)
                manager.getWorksheetResultAt(i).validate(errorList);
            else if (waDO.getQcId() != null && listItem.worksheetQcResult != null)
                manager.getWorksheetQcResultAt(i).validate(errorList);
        }
    }

    /**
     * Loads the WorksheetResultManager from the provided AnalysisManager. 
     */
    private void initializeWorksheetResults(AnalysisViewDO aVDO, AnalysisResultManager arManager,
                                            WorksheetResultManager wrManager) throws Exception {
        int                                         i;
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
            if (twAnalytes.size() == 0 || !twAnalytes.containsKey(result.getTestAnalyteId())) {
                wrVDO = new WorksheetResultViewDO();
                wrVDO.setTestAnalyteId(result.getTestAnalyteId());
                wrVDO.setResultRow(i);
                wrVDO.setAnalyteId(result.getAnalyteId());
                wrVDO.setTypeId(result.getTypeId());
                wrVDO.setAnalyteName(result.getAnalyte());
                wrManager.addWorksheetResult(wrVDO);
            }           
        }
    }

    /**
     * Loads the WorksheetResultManager from the provided AnalysisManager. 
     */
/*
    private void synchronizeWorksheetResults(AnalysisViewDO aVDO, AnalysisResultManager arManager,
                                             WorksheetAnalysisManager waManager) throws Exception {
        int                                         i, j;
        ArrayList<ResultViewDO>                     resultRow;
        ArrayList<ArrayList<ResultViewDO>>          results;
        HashMap<Integer,TestWorksheetAnalyteViewDO> twAnalytes;
        ResultViewDO                                result;
        TestWorksheetAnalyteViewDO                  twaVDO;
        TestWorksheetManager                        twManager;
        WorksheetResultManager                      wrManager, newManager;
        WorksheetResultViewDO                       wrVDO;

        twAnalytes = new HashMap<Integer,TestWorksheetAnalyteViewDO>();
        twManager = TestWorksheetManager.fetchByTestId(aVDO.getTestId());
        for (i = 0; i < twManager.analyteCount(); i++) {
            twaVDO = twManager.getAnalyteAt(i);
            twAnalytes.put(twaVDO.getTestAnalyteId(), twaVDO);
        }

        results = arManager.getResults();
        wrManager = waManager.getWorksheetResultAt(index);
        if (wrManager.count() <= 0) {
            for (i = 0; i < results.size(); i++) {
                resultRow = results.get(i);
                result = resultRow.get(0);
                
                if (twAnalytes.size() == 0 || !twAnalytes.containsKey(result.getTestAnalyteId())) {
                    wrVDO = new WorksheetResultViewDO();
                    wrVDO.setTestAnalyteId(result.getTestAnalyteId());
                    wrVDO.setResultRow(i);
                    wrVDO.setAnalyteId(result.getAnalyteId());
                    wrVDO.setTypeId(result.getTypeId());
                    wrVDO.setAnalyteName(result.getAnalyte());
                    wrManager.addWorksheetResult(wrVDO);
                }           
            }
        } else {
            i = 0;
            j = 0;
            newManager = WorksheetResultManager.getInstance();
            newManager.setWorksheetAnalysisId(wrManager.getWorksheetAnalysisId());
            while (i < results.size() || j < wrManager.count()) {
                resultRow = results.get(i);
                result = resultRow.get(0);
                wrVDO = wrManager.getWorksheetResultAt(j);
                
                
                
                if (twAnalytes.size() == 0 || twAnalytes.containsKey(result.getTestAnalyteId())) {
                    
                    wrVDO = new WorksheetResultViewDO();
                    wrVDO.setTestAnalyteId(result.getTestAnalyteId());
                    wrVDO.setResultRow(i);
                    wrVDO.setAnalyteId(result.getAnalyteId());
                    wrVDO.setTypeId(result.getTypeId());
                    wrVDO.setAnalyteName(result.getAnalyte());
                    wrManager.addWorksheetResult(wrVDO);
                }
            }
        }
    }
*/
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
            wqrVDO.setTypeId(qcaVDO.getTypeId());
            wqrVDO.setAnalyteId(qcaVDO.getAnalyteId());
            wqrVDO.setAnalyteName(qcaVDO.getAnalyteName());
            wqrManager.addWorksheetQcResult(wqrVDO);
        }
    }
}
