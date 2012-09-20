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

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.domain.AnalysisReportFlagsDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SectionViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.SystemUserPermission;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.DictionaryLocal;
import org.openelis.manager.AnalysisManager.AnalysisListItem;
import org.openelis.utils.EJBFactory;

public class AnalysisManagerProxy {
    protected static Integer anLoggedInId, anInitiatedId, anCompletedId, anReleasedId,
                             anInPrepId, anOnHoldId, anRequeueId, anCancelledId,
                             anErrorLoggedInId, anErrorInitiatedId, anErrorInPrepId,
                             anErrorCompletedId, sampleNotVerifiedId, sampleReleasedId;

    public AnalysisManagerProxy() {
        DictionaryLocal l;

        if (anLoggedInId == null) {
            l = EJBFactory.getDictionary();

            try {
                anLoggedInId = l.fetchBySystemName("analysis_logged_in").getId();
                anInitiatedId = l.fetchBySystemName("analysis_initiated").getId();
                anCompletedId = l.fetchBySystemName("analysis_completed").getId();
                anReleasedId = l.fetchBySystemName("analysis_released").getId();
                anInPrepId = l.fetchBySystemName("analysis_inprep").getId();
                anOnHoldId = l.fetchBySystemName("analysis_on_hold").getId();
                anRequeueId = l.fetchBySystemName("analysis_requeue").getId();
                anCancelledId = l.fetchBySystemName("analysis_cancelled").getId();
                anErrorLoggedInId = l.fetchBySystemName("analysis_error_logged_in").getId();
                anErrorInitiatedId = l.fetchBySystemName("analysis_error_initiated").getId();
                anErrorInPrepId = l.fetchBySystemName("analysis_error_inprep").getId();
                anErrorCompletedId = l.fetchBySystemName("analysis_error_completed").getId();
                sampleNotVerifiedId = l.fetchBySystemName("sample_not_verified").getId();
                sampleReleasedId = l.fetchBySystemName("sample_released").getId();
            } catch (Exception e) {
                e.printStackTrace();
                anLoggedInId = null;
            }
        }
    }
    
    public AnalysisManager fetchBySampleItemId(Integer sampleItemId) throws Exception {
        AnalysisViewDO anDO;
        ArrayList<AnalysisViewDO> analyses;
        AnalysisManager man;

        analyses = EJBFactory.getAnalysis().fetchBySampleItemId(sampleItemId);
        man = AnalysisManager.getInstance();

        for (int i = 0; i < analyses.size(); i++ ) {
            anDO = analyses.get(i);
            man.addAnalysis(anDO);
        }

        man.setSampleItemId(sampleItemId);

        return man;
    }

    public int update(AnalysisManager man, HashMap<Integer, Integer> idHash) throws Exception {
        boolean        unresolved;
        int            i, numUnresolved;
        Integer        oldId, parentId, prepId;
        AnalysisViewDO analysisDO;

        numUnresolved = 0;
        for (i = 0; i < man.count(); i++ ) {
            unresolved = false;
            analysisDO = man.getAnalysisAt(i);

            // try to resolve a negative prep analysis id
            if (analysisDO.getPreAnalysisId() != null && analysisDO.getPreAnalysisId() < 0) {
                prepId = idHash.get(analysisDO.getPreAnalysisId());
                if (prepId != null) {
                    analysisDO.setPreAnalysisId(prepId);
                } else {
                    unresolved = true;
                }
            }

            // try to resolve a negative parent analysis id
            if (analysisDO.getParentAnalysisId() != null && analysisDO.getParentAnalysisId() < 0) {
                parentId = idHash.get(analysisDO.getParentAnalysisId());
                if (parentId != null) {
                    analysisDO.setParentAnalysisId(parentId);
                } else {
                    unresolved = true;
                }
            }
            
            // if both the prep and parent ids are resolved, then add/update the
            // analysis and set the mappings in the hash, otherwise skip
            if (!unresolved) {
                if (!idHash.containsKey(analysisDO.getId())) {
                    oldId = analysisDO.getId();
                    if (oldId < 0) {
                        add(man, analysisDO, i);
                        idHash.put(oldId, analysisDO.getId());
                    } else {
                        update(man, analysisDO, i);
                    }
                    idHash.put(analysisDO.getId(), null);
                }
            } else {
                numUnresolved++;
            }
        }
        
        return numUnresolved;
    }

    private void add(AnalysisManager man, AnalysisViewDO analysisDO, int i) throws Exception {
        AnalysisListItem      item;
        AnalysisReportFlagsDO arfDO;

        analysisDO.setSampleItemId(man.getSampleItemId());
        EJBFactory.getAnalysis().add(analysisDO);

        arfDO = new AnalysisReportFlagsDO(analysisDO.getId(), "N", "N", null, 0, "N");
        EJBFactory.getAnalysisReportFlags().add(arfDO);
        
        item = man.getItemAt(i);

        if (item.analysisUsers != null) {
            man.getAnalysisUserAt(i).setAnalysisId(analysisDO.getId());
            man.getAnalysisUserAt(i).add();
        }

        if (item.analysisResult != null) {
            man.getAnalysisResultAt(i).setAnalysisId(analysisDO.getId());
            man.getAnalysisResultAt(i).add();
        }

        if (item.qaEvents != null) {
            man.getQAEventAt(i).setAnalysisId(analysisDO.getId());
            man.getQAEventAt(i).add();
        }

        if (item.analysisInternalNotes != null) {
            man.getInternalNotesAt(i).setReferenceId(analysisDO.getId());
            man.getInternalNotesAt(i).setReferenceTableId(ReferenceTable.ANALYSIS);
            man.getInternalNotesAt(i).add();
        }

        if (item.analysisExternalNote != null) {
            man.getExternalNoteAt(i).setReferenceId(analysisDO.getId());
            man.getExternalNoteAt(i).setReferenceTableId(ReferenceTable.ANALYSIS);
            man.getExternalNoteAt(i).add();
        }

        if (item.storages != null) {
            man.getStorageAt(i).setReferenceId(analysisDO.getId());
            man.getStorageAt(i).setReferenceTableId(ReferenceTable.ANALYSIS);
            man.getStorageAt(i).add();
        }
    }

    private void update(AnalysisManager man, AnalysisViewDO analysisDO, int i) throws Exception {
        Integer analysisRefId;
        AnalysisListItem item;

        analysisRefId = ReferenceTable.ANALYSIS;

        if (analysisDO.getId() == null) {
            analysisDO.setSampleItemId(man.getSampleItemId());
            EJBFactory.getAnalysis().add(analysisDO);
        } else {
            //
            // if the analysis was moved to a different sample item, we need to
            // update the sample_item_id
            //
            if (!man.getSampleItemId().equals(analysisDO.getSampleItemId()))
                analysisDO.setSampleItemId(man.getSampleItemId());
            //
            // we delay setting the released date until after the validation, 
            // to detect newly released analyses 
            //
            if (anReleasedId.equals(analysisDO.getStatusId()) && analysisDO.getReleasedDate() == null)
                analysisDO.setReleasedDate(getCurrentDatetime(Datetime.YEAR, Datetime.MINUTE));   
            EJBFactory.getAnalysis().update(analysisDO);
        }
        
        item = man.getItemAt(i);

        if (item.analysisUsers != null) {
            man.getAnalysisUserAt(i).setAnalysisId(analysisDO.getId());
            man.getAnalysisUserAt(i).update();
        }

        if (item.analysisResult != null) {
            man.getAnalysisResultAt(i).setAnalysisId(analysisDO.getId());
            man.getAnalysisResultAt(i).update();
        }

        if (item.qaEvents != null) {
            man.getQAEventAt(i).setAnalysisId(analysisDO.getId());
            man.getQAEventAt(i).update();
        }

        if (item.analysisInternalNotes != null) {
            man.getInternalNotesAt(i).setReferenceId(analysisDO.getId());
            man.getInternalNotesAt(i).setReferenceTableId(analysisRefId);
            man.getInternalNotesAt(i).update();
        }

        if (item.analysisExternalNote != null) {
            man.getExternalNoteAt(i).setReferenceId(analysisDO.getId());
            man.getExternalNoteAt(i).setReferenceTableId(analysisRefId);
            man.getExternalNoteAt(i).update();
        }

        if (item.storages != null) {
            man.getStorageAt(i).setReferenceId(analysisDO.getId());
            man.getStorageAt(i).setReferenceTableId(analysisRefId);
            man.getStorageAt(i).update();
        }
    }

    public void validate(AnalysisManager man, ValidationErrorsList errorsList) throws Exception {
        validate(man, null, null, null, errorsList);
    }

    public void validate(AnalysisManager man,
                         String sampleItemSequence,
                         Integer sampleTypeId,
                         String sampleDomain,
                         ValidationErrorsList errorsList) throws Exception {
        AnalysisListItem item;
        AnalysisViewDO analysisDO;
        TestManager testMan;
        boolean quickEntry;

        quickEntry = SampleManager.QUICK_ENTRY.equals(sampleDomain);

        for (int i = 0; i < man.count(); i++ ) {
            analysisDO = man.getAnalysisAt(i);
            testMan = man.getTestAt(i);

            //
            // We do NOT need to validate analyses that are in cancelled or released
            // status 
            //
            if (anCancelledId.equals(analysisDO.getStatusId()) ||
                (anReleasedId.equals(analysisDO.getStatusId()) && analysisDO.getReleasedDate() != null))
                continue;
                            
            //
            // Only validate the analysis if it has changed, so we don't cause
            // problems when a user updates one analysis on a sample and another
            // one has unrelated validation errors
            //
            if (analysisDO.isChanged()) {
                if (analysisDO.getTestId() == null)
                    errorsList.add(new FormErrorException("analysisTestIdMissing", sampleItemSequence));
    
                if (analysisDO.getTestId() != null && analysisDO.getSectionId() == null)
                    errorsList.add(new FormErrorException("analysisSectionIdMissing",
                                                          analysisDO.getTestName(),
                                                          analysisDO.getMethodName()));
    
                // if unit is null, check the test definition to see if all sample types
                // have units defined. if so, require the user to enter units
                if (analysisDO.getTestId() != null &&
                    analysisDO.getUnitOfMeasureId() == null &&
                    !testMan.getSampleTypes().hasEmptyUnit())
                    errorsList.add(new FormErrorException("analysisUnitRequired",
                                                          analysisDO.getTestName(),
                                                          analysisDO.getMethodName()));
    
                if (analysisDO.getStartedDate() != null && analysisDO.getCompletedDate() != null &&
                    analysisDO.getStartedDate().compareTo(analysisDO.getCompletedDate()) == 1)
                    errorsList.add(new FormErrorException("startedDateInvalidError",
                                                          analysisDO.getTestName(),
                                                          analysisDO.getMethodName()));
            }
            
            item = man.getItemAt(i);
            // validate the children

            // we want to always run through validate so it loads the data
            // if the user didn't click on the tab
            if ( !quickEntry)
                man.getAnalysisResultAt(i).validate(analysisDO, errorsList);

            if ( !quickEntry && item.qaEvents != null)
                man.getQAEventAt(i).validate(errorsList);

            if ( !quickEntry && item.storages != null)
                man.getStorageAt(i).validate(errorsList);
        }
    }

    public SectionViewDO getSectionFromId(Integer sectionId) throws Exception {
        return EJBFactory.getSection().fetchById(sectionId);
    }

    protected Datetime getCurrentDatetime(byte begin, byte end) throws Exception {
        return Datetime.getInstance(begin, end);
    }

    protected SystemUserPermission getSystemUserPermission() throws Exception {
        return EJBFactory.getUserCache().getPermission();
    }
}
