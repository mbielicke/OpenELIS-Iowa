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

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.SampleDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FieldErrorWarning;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.screen.Calendar;
import org.openelis.gwt.services.ScreenService;
import org.openelis.meta.SampleMeta;

public class SampleManagerProxy {
    protected static final String SAMPLE_SERVICE_URL = "org.openelis.modules.sample.server.SampleService";
    protected ScreenService       service;
    protected static Integer      anLoggedInId, anInitiatedId, anCompletedId, anReleasedId,
                                  anInPrepId, anOnHoldId, anRequeueId, anCancelledId, anErrorLoggedInId,
                                  anErrorInitiatedId, anErrorInPrepId, anErrorCompletedId, samNotVerifiedId,
                                  samLoggedInId, samCompletedId, samReleasedId, samErrorId;

    public SampleManagerProxy() {
        service = new ScreenService("controller?service=" + SAMPLE_SERVICE_URL);

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
                samNotVerifiedId = DictionaryCache.getIdBySystemName("sample_not_verified");
                samLoggedInId = DictionaryCache.getIdBySystemName("sample_logged_in");
                samCompletedId = DictionaryCache.getIdBySystemName("sample_completed");
                samReleasedId = DictionaryCache.getIdBySystemName("sample_released");
                samErrorId = DictionaryCache.getIdBySystemName("sample_error");
            } catch (Exception e) {
                e.printStackTrace();
                anLoggedInId = null;
            }
        }
    }

    public SampleManager fetchById(Integer sampleId) throws Exception {
        return service.call("fetchById", sampleId);
    }

    public SampleManager fetchByAccessionNumber(Integer accessionNumber) throws Exception {
        return service.call("fetchByAccessionNumber", accessionNumber);
    }

    public SampleManager fetchWithItemsAnalyses(Integer sampleId) throws Exception {
        return service.call("fetchWithItemsAnalyses", sampleId);
    }
    
    public SampleManager fetchWithAllData(Integer sampleId) throws Exception {        
        return service.call("fetchWithAllData", sampleId);
    }

    public SampleManager add(SampleManager man) throws Exception {
        return service.call("add", man);
    }

    public SampleManager update(SampleManager man) throws Exception {
        return service.call("update", man);
    }
    
    public void updateCache(SampleManager man) {      
    }
    
    public SampleManager fetchForUpdate(Integer sampleId) throws Exception {
        return service.call("fetchForUpdate", sampleId);
    }

    public SampleManager abortUpdate(Integer sampleId) throws Exception {
        return service.call("abortUpdate", sampleId);
    }

    public Datetime getCurrentDatetime(byte begin, byte end) throws Exception {
        return Calendar.getCurrentDatetime(begin, end);
    }

    public void validate(SampleManager man, ValidationErrorsList errorsList) throws Exception {
        SampleDO data;
        NoteManager noteMan;

        data = man.getSample();

        // re-validate accession number
        validateAccessionNumber(data, errorsList);
        validateOrderId(man.getSample(), errorsList);

        // received date required
        if (data.getReceivedDate() == null || data.getReceivedDate().getDate() == null)
            errorsList.add(new FieldErrorWarning("receivedDateRequiredException",
                                                 SampleMeta.getReceivedDate()));
        else if (data.getEnteredDate() != null &&
                 data.getReceivedDate().before(data.getEnteredDate().add( -180)))
            // received can't be more than 180 days before entered
            errorsList.add(new FieldErrorWarning("receivedTooOldWarning",
                                                 SampleMeta.getReceivedDate()));

        if (data.getEnteredDate() != null && data.getCollectionDate() != null &&
            data.getCollectionDate().before(data.getEnteredDate().add( -180)))
            errorsList.add(new FieldErrorException("collectedTooOldException",
                                                   SampleMeta.getCollectionDate()));

        if (data.getCollectionDate() != null && data.getReceivedDate() != null &&
            data.getCollectionDate().compareTo(data.getReceivedDate()) == 1)
                errorsList.add(new FieldErrorException("collectedDateInvalidError",
                                                       SampleMeta.getReceivedDate()));
        
        // every unreleased sample needs an internal comment describing the reason
        if (man.unreleaseWithNotes) {
            noteMan = man.getInternalNotes();
            if (noteMan == null || noteMan.count() == 0)
                errorsList.add(new FormErrorException("unreleaseNoNoteException"));
        }

        man.getDomainManager().validate(errorsList);

        if (man.sampleItems != null)
            man.getSampleItems().validate(errorsList);

        if (man.organizations != null)
            man.getOrganizations().validate(man.getSample().getDomain(), errorsList);

        if (man.projects != null)
            man.getProjects().validate(errorsList);

        if (man.qaEvents != null)
            man.getQaEvents().validate(errorsList);

        if (man.auxData != null)
            man.getAuxData().validate(errorsList);
    }

    private void validateAccessionNumber(SampleDO data, ValidationErrorsList errorsList) throws Exception {
        ArrayList<Exception> errors;
        try {
            service.call("validateAccessionNumber", data);

        } catch (ValidationErrorsList e) {
            errors = e.getErrorList();

            for (int i = 0; i < errors.size(); i++ )
                errorsList.add(errors.get(i));
        }
    }
    
    private void validateOrderId(SampleDO data, ValidationErrorsList errorsList) throws Exception {
        OrderManager man;
        
        if (data.getOrderId() == null)
            return;
        try {
            man = OrderManager.fetchById(data.getOrderId());
            if (!OrderManager.TYPE_SEND_OUT.equals(man.getOrder().getType()))
                errorsList.add(new FieldErrorException("orderIdInvalidException",
                                                       SampleMeta.getOrderId()));
        } catch (NotFoundException e) {
            errorsList.add(new FieldErrorException("orderIdInvalidException",
                                                   SampleMeta.getOrderId()));
        }
        
    }
}
