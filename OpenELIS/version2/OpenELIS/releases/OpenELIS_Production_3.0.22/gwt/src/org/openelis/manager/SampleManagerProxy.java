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
import java.util.Date;

import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.SampleDO;
import org.openelis.gwt.services.CalendarService;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.sample.client.SampleService;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.FieldErrorWarning;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;

public class SampleManagerProxy {
    
    public SampleManagerProxy() {
    }

    public SampleManager fetchById(Integer sampleId) throws Exception {
        return SampleService.get().fetchById(sampleId);
    }

    public SampleManager fetchByAccessionNumber(Integer accessionNumber) throws Exception {
        return SampleService.get().fetchByAccessionNumber(accessionNumber);
    }

    public SampleManager fetchWithItemsAnalyses(Integer sampleId) throws Exception {
        return SampleService.get().fetchWithItemsAnalyses(sampleId);
    }

    public SampleManager fetchWithAllDataById(Integer sampleId) throws Exception {
        return SampleService.get().fetchWithAllDataById(sampleId);
    }

    public SampleManager fetchWithAllDataByAccessionNumber(Integer accessionNumber) throws Exception {
        return SampleService.get().fetchWithAllDataByAccessionNumber(accessionNumber);
    }

    public SampleManager add(SampleManager man) throws Exception {
        return SampleService.get().add(man);
    }

    public SampleManager update(SampleManager man) throws Exception {
        return SampleService.get().update(man);
    }

    public SampleManager fetchForUpdate(Integer sampleId) throws Exception {
        return SampleService.get().fetchForUpdate(sampleId);
    }

    public SampleManager abortUpdate(Integer sampleId) throws Exception {
        return SampleService.get().abortUpdate(sampleId);
    }

    public Datetime getCurrentDatetime(byte begin, byte end) throws Exception {
        return CalendarService.get().getCurrentDatetime(begin, end);
    }

    public void validate(SampleManager man, ValidationErrorsList errorsList) throws Exception {
        SampleDomainInt domMan;
        SampleDO data;
        NoteManager noteMan;
        Date collectionDate;
        Datetime collectionTime, collectionDateTime;

        data = man.getSample();

        // re-validate accession number
        validateAccessionNumber(data, errorsList);
        validateOrderId(man.getSample(), errorsList);

        // received date required
        if (data.getReceivedDate() == null || data.getReceivedDate().getDate() == null)
            errorsList.add(new FieldErrorWarning(Messages.get().receivedDateRequiredException(),
                                                 SampleMeta.getReceivedDate()));
        else if (data.getEnteredDate() != null &&
                 data.getReceivedDate().before(data.getEnteredDate().add( -180)))
            // received can't be more than 180 days before entered
            errorsList.add(new FieldErrorWarning(Messages.get().receivedTooOldWarning(),
                                                 SampleMeta.getReceivedDate()));

        collectionTime = data.getCollectionTime();
        if (data.getCollectionDate() != null) {
            collectionDate = (Date)data.getCollectionDate().getDate().clone();
            if (collectionTime != null) {
                collectionDate.setHours(collectionTime.getDate().getHours());
                collectionDate.setMinutes(collectionTime.getDate().getMinutes());
            }
            collectionDateTime = new Datetime(Datetime.YEAR, Datetime.MINUTE, collectionDate);
        } else {
            if (collectionTime != null) {
                errorsList.add(new FieldErrorException(Messages.get()
                                                               .collectedTimeWithoutDateError(),
                                                       SampleMeta.getCollectionTime()));
            }
            collectionDateTime = null;
        }
        
        if (collectionDateTime != null) {
            if (data.getEnteredDate() != null &&
                collectionDateTime.before(data.getEnteredDate().add( -180)))
                errorsList.add(new FieldErrorException(Messages.get().collectedTooOldWarning(),
                                                       SampleMeta.getCollectionDate()));

            if (data.getEnteredDate() != null &&
                collectionDateTime.compareTo(data.getEnteredDate()) == 1)
                errorsList.add(new FieldErrorException(Messages.get()
                                                               .collectedDateAfterEnteredError(),
                                                       SampleMeta.getCollectionDate()));

            if (data.getReceivedDate() != null &&
                collectionDateTime.compareTo(data.getReceivedDate()) == 1)
                errorsList.add(new FieldErrorException(Messages.get()
                                                               .collectedDateAfterReceivedError(),
                                                       SampleMeta.getReceivedDate()));
        }

        // every unreleased sample needs an internal comment describing the
        // reason
        if (man.unreleaseWithNotes) {
            noteMan = man.getInternalNotes();
            if ( (noteMan == null || noteMan.count() == 0) ||
                (noteMan != null && noteMan.count() > 0 && noteMan.getNoteAt(0).getId() != null)) {
                errorsList.add(new FormErrorException(Messages.get().unreleaseNoNoteException()));
            }
        }

        //
        // the domain manager can be null if the domain is quick entry
        //
        domMan = man.getDomainManager();
        if (domMan != null)
            domMan.validate(errorsList);

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
            SampleService.get().validateAccessionNumber(data);

        } catch (ValidationErrorsList e) {
            errors = e.getErrorList();

            for (int i = 0; i < errors.size(); i++ )
                errorsList.add(errors.get(i));
        }
    }

    private void validateOrderId(SampleDO data, ValidationErrorsList errorsList) throws Exception {
        IOrderManager man;

        if (data.getOrderId() == null)
            return;
        try {
            /*
             * only validate order for domains that use send-out orders
             */
            if (Constants.domain().ENVIRONMENTAL.equals(data.getDomain()) ||
                Constants.domain().PRIVATEWELL.equals(data.getDomain()) ||
                Constants.domain().SDWIS.equals(data.getDomain())) {
                man = IOrderManager.fetchById(data.getOrderId());
                if ( !IOrderManager.TYPE_SEND_OUT.equals(man.getIorder().getType()))
                    errorsList.add(new FieldErrorException(Messages.get().orderIdInvalidException(),
                                                           SampleMeta.getOrderId()));
            }
        } catch (NotFoundException e) {
            errorsList.add(new FieldErrorException(Messages.get().orderIdInvalidException(),
                                                   SampleMeta.getOrderId()));
        }

    }
}