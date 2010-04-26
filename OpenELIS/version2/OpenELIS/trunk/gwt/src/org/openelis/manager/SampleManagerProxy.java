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

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.SampleDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FieldErrorWarning;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.screen.Calendar;
import org.openelis.gwt.services.ScreenService;
import org.openelis.meta.SampleMeta;

public class SampleManagerProxy {
    protected static final String SAMPLE_SERVICE_URL = "org.openelis.modules.sample.server.SampleService";
    protected ScreenService service;
    
    public SampleManagerProxy(){
        service = new ScreenService("OpenELISServlet?service="+SAMPLE_SERVICE_URL);
    }
    
    public SampleManager fetchById(Integer sampleId) throws Exception {
        return service.call("fetchById", sampleId);
    }

    public SampleManager fetchByAccessionNumber(Integer accessionNumber) throws Exception {
        return service.call("fetchByAccessionNumber", accessionNumber);
    }

    public SampleManager fetchWithItemsAnalyses(Integer sampleId) throws Exception {
        try{
     return service.call("fetchWithItemsAnalyses", sampleId);
        }catch(Exception e){
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public SampleManager add(SampleManager man) throws Exception {
        return service.call("add", man);
    }

    public SampleManager update(SampleManager man) throws Exception {
        return service.call("update", man);
    }
    
    public SampleManager fetchForUpdate(Integer sampleId) throws Exception {
        return service.call("fetchForUpdate", sampleId);
    }
    
    public SampleManager abortUpdate(Integer sampleId) throws Exception {
        return service.call("abortUpdate", sampleId);
    }
    
    public Integer getIdFromSystemName(String systemName) throws Exception {
        return DictionaryCache.getIdFromSystemName(systemName);
    }
    
    public Datetime getCurrentDatetime(byte begin, byte end) throws Exception {
        return Calendar.getCurrentDatetime(begin, end);
    }

    public void validate(SampleManager man, ValidationErrorsList errorsList) throws Exception {
        //revalidate accession number
        validateAccessionNumber(man.getSample(), errorsList);
        
        //sample validate code
        SampleDO sampleDO = man.getSample();
        
        //validate the dates
        //recieved date required
        if(sampleDO.getReceivedDate() == null || sampleDO.getReceivedDate().getDate() == null)
            errorsList.add(new FieldErrorWarning("fieldRequiredException", SampleMeta.getReceivedDate()));
        else if(sampleDO.getEnteredDate() != null && sampleDO.getReceivedDate().before(sampleDO.getEnteredDate().add(-30)))
            //recieved cant be more than 30 days before entered
            errorsList.add(new FieldErrorWarning("receivedTooOldWarning", SampleMeta.getReceivedDate()));
            
       if(sampleDO.getEnteredDate() != null && sampleDO.getCollectionDate() != null && 
                       sampleDO.getCollectionDate().before(sampleDO.getEnteredDate().add(-364)))
               errorsList.add(new FieldErrorException("collectedTooOldException", SampleMeta.getCollectionDate()));
        
       if(sampleDO.getCollectionDate() == null)
           errorsList.add(new FieldErrorWarning("collectedDateMissingWarning", SampleMeta.getCollectionDate()));
       else if(sampleDO.getReceivedDate() != null){
            if(sampleDO.getCollectionDate().compareTo(sampleDO.getReceivedDate()) == 1)
                errorsList.add(new FieldErrorException("collectedDateInvalidError", SampleMeta.getReceivedDate()));
       }
        
       man.getDomainManager().validate(errorsList);
       
       if(man.sampleItems != null)
           man.getSampleItems().validate(errorsList);
       
       if(man.organizations != null)
           man.getOrganizations().validate(man.getSample().getDomain(), errorsList);
       
       if(man.projects != null)
           man.getProjects().validate(errorsList);
       
       if(man.qaEvents != null)
           man.getQaEvents().validate(errorsList);
       
       if(man.auxData != null)
           man.getAuxData().validate(errorsList);
    }
    
    private void validateAccessionNumber(SampleDO sampleDO, ValidationErrorsList errorsList) throws Exception {
        try{
            service.call("validateAccessionNumber", sampleDO);
    
        }catch(ValidationErrorsList e){
            ArrayList<Exception> errors = e.getErrorList();
            
            for(int i=0; i<errors.size(); i++)
                errorsList.add(errors.get(i));
        }
    }
}
