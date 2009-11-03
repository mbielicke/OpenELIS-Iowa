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

import org.openelis.domain.SampleDO;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FieldErrorWarning;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.services.ScreenService;
import org.openelis.metamap.SampleMetaMap;

public class SampleManagerProxy {
    protected static final String SAMPLE_SERVICE_URL = "org.openelis.modules.sample.server.SampleService";
    protected ScreenService service;
    
    public SampleManagerProxy(){
        service = new ScreenService("OpenELISServlet?service="+SAMPLE_SERVICE_URL);
    }
    
    public SampleManager add(SampleManager man) throws Exception {
        return service.call("add", man);
    }

    public SampleManager update(SampleManager man) throws Exception {
        return service.call("update", man);
    }
    
    public SampleManager fetch(Integer sampleId) throws Exception {
        return service.call("fetch", sampleId);
    }
    
    public SampleManager fetchWithItemsAnalyses(Integer sampleId) throws Exception {
     return service.call("fetchWithItemsAnalyses", sampleId);   
    }
    
    public SampleManager fetchByAccessionNumber(Integer accessionNumber) throws Exception {
        return service.call("fetchByAccessionNumber", accessionNumber);
    }
    
    public SampleManager fetchForUpdate(Integer sampleId) throws Exception {
        return service.call("fetchForUpdate", sampleId);
    }
    
    public SampleManager abort(Integer sampleId) throws Exception {
        return service.call("abort", sampleId);
    }
    
    public void validate(SampleManager man, ValidationErrorsList errorsList) throws Exception {
        //FIXME revalidate accession num
        
        SampleMetaMap meta = new SampleMetaMap("sample.");
        
        //sample validate code
        SampleDO sampleDO = man.getSample();
        //validate the dates
        if(sampleDO.getCollectionDate() != null && sampleDO.getReceivedDate() != null)
            if(sampleDO.getCollectionDate().compareTo(sampleDO.getReceivedDate()) == 1)
                errorsList.add(new FieldErrorException("collectedDateInvalidError", meta.getReceivedDate()));
        
        if(sampleDO.getCollectionDate() == null)
            errorsList.add(new FieldErrorWarning("collectedDateMissingWarning", meta.getCollectionDate()));
        
        man.getSampleItems().validate(errorsList);
        man.getOrganizations().validate(errorsList);
        man.getProjects().validate(errorsList);
        
        
    }
}
