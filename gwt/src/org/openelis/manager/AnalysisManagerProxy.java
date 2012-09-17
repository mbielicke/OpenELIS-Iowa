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

import java.util.HashMap;

import org.openelis.cache.DictionaryCache;
import org.openelis.cache.SectionCache;
import org.openelis.cache.UserCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.FormErrorWarning;
import org.openelis.gwt.common.SystemUserPermission;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.screen.Calendar;
import org.openelis.gwt.services.ScreenService;
import org.openelis.manager.AnalysisManager.AnalysisListItem;

public class AnalysisManagerProxy {
    protected static final String ANALYSIS_SERVICE_URL = "org.openelis.modules.analysis.server.AnalysisService";
    protected ScreenService       service;
    protected static Integer      anLoggedInId, anInitiatedId, anCompletedId, anReleasedId,
                                  anInPrepId, anOnHoldId, anRequeueId, anCancelledId, anErrorLoggedInId,
                                  anErrorInitiatedId, anErrorInPrepId, anErrorCompletedId,
                                  sampleNotVerifiedId, sampleReleasedId;

    public AnalysisManagerProxy() {
        service = new ScreenService("controller?service=" + ANALYSIS_SERVICE_URL);

        if (anLoggedInId == null) {
            try {
                anLoggedInId = DictionaryCache.getIdBySystemName("analysis_logged_in");
                anInitiatedId = DictionaryCache.getIdBySystemName("analysis_initiated");
                anCompletedId = DictionaryCache.getIdBySystemName("analysis_completed");
                anReleasedId = DictionaryCache.getIdBySystemName("analysis_released");
                anInPrepId = DictionaryCache.getIdBySystemName("analysis_inprep");
                anOnHoldId = DictionaryCache.getIdBySystemName("analysis_on_hold");
                anRequeueId = DictionaryCache.getIdBySystemName("analysis_requeue");
                anCancelledId = DictionaryCache.getIdBySystemName("analysis_cancelled");
                anErrorLoggedInId = DictionaryCache.getIdBySystemName("analysis_error_logged_in");
                anErrorInitiatedId = DictionaryCache.getIdBySystemName("analysis_error_initiated");
                anErrorInPrepId = DictionaryCache.getIdBySystemName("analysis_error_inprep");
                anErrorCompletedId = DictionaryCache.getIdBySystemName("analysis_error_completed");
                sampleNotVerifiedId = DictionaryCache.getIdBySystemName("sample_not_verified");
                sampleReleasedId = DictionaryCache.getIdBySystemName("sample_released");
            } catch (Exception e) {
                e.printStackTrace();
                anLoggedInId = null;
            }
        }
    }

    public AnalysisManager fetchBySampleItemId(Integer sampleItemId) throws Exception {
        return service.call("fetchBySampleItemId", sampleItemId);
    }

    public int update(AnalysisManager man, HashMap<Integer, Integer> idHash) throws Exception {
        assert false : "not supported";
        return -1;
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
    
                // validate the sample type
                if (analysisDO.getTestId() != null &&
                    !testMan.getSampleTypes().hasType(sampleTypeId))
                    errorsList.add(new FormErrorWarning("sampleTypeInvalid", analysisDO.getTestName(),
                                                        analysisDO.getMethodName()));
    
                // if unit is not null, it needs to be validated
                if (analysisDO.getTestId() != null &&
                    analysisDO.getUnitOfMeasureId() != null &&
                    !testMan.getSampleTypes().hasUnit(analysisDO.getUnitOfMeasureId(), sampleTypeId))
                    errorsList.add(new FormErrorWarning("analysisUnitInvalid",
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
            // if the user didnt click on the tab
            if ( !quickEntry)
                man.getAnalysisResultAt(i).validate(analysisDO, errorsList);

            if ( !quickEntry && item.qaEvents != null)
                man.getQAEventAt(i).validate(errorsList);

            if ( !quickEntry && item.storages != null)
                man.getStorageAt(i).validate(errorsList);
                        
        }
    }

    protected SectionViewDO getSectionFromId(Integer sectionId) throws Exception {
        return SectionCache.getById(sectionId);
    }

    protected Datetime getCurrentDatetime(byte begin, byte end) throws Exception {
        return Calendar.getCurrentDatetime(begin, end);
    }

    protected SystemUserPermission getSystemUserPermission() {
        return UserCache.getPermission();
    }
}
