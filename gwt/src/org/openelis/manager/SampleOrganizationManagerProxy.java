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

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.SampleOrganizationDO;
import org.openelis.gwt.common.FieldErrorWarning;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.FormErrorWarning;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.services.ScreenService;
import org.openelis.metamap.SampleEnvironmentalMetaMap;

public class SampleOrganizationManagerProxy {
    protected static final String SAMPLE_SERVICE_URL = "org.openelis.modules.sample.server.SampleService";
    protected ScreenService service;
    protected SampleEnvironmentalMetaMap META = new SampleEnvironmentalMetaMap();
    
    public SampleOrganizationManagerProxy(){
        service = new ScreenService("OpenELISServlet?service="+SAMPLE_SERVICE_URL);
    }
    
    public Integer getIdFromSystemName(String systemName){
        return DictionaryCache.getIdFromSystemName(systemName);
    }
    
    public SampleOrganizationManager fetchBySampleId(Integer sampleId) throws Exception {
        return service.call("fetchSampleOrganizationsBySampleId", sampleId);
    }
    
    
    public SampleOrganizationManager add(SampleOrganizationManager man) throws Exception {
        throw new UnsupportedOperationException();
    }
    
    
    public SampleOrganizationManager update(SampleOrganizationManager man) throws Exception {
        throw new UnsupportedOperationException();
    }
    
    public void validate(SampleOrganizationManager man, ValidationErrorsList errorsList) throws Exception {
        int numBillTo, numReportTo;
        
        numBillTo = 0;
        numReportTo = 0;
        for(int i=0; i<man.count(); i++){
            SampleOrganizationDO orgDO = man.getOrganizationAt(i);
            if(DictionaryCache.getIdFromSystemName("org_bill_to").equals(orgDO.getTypeId()))
                numBillTo++;
            
            if(DictionaryCache.getIdFromSystemName("org_report_to").equals(orgDO.getTypeId()))
                numReportTo++;
        }
        
        if(numBillTo > 1)
            errorsList.add(new FormErrorException("multipleBillToException"));
        
        if(numReportTo > 1)
            errorsList.add(new FormErrorException("multipleReportToException"));
        
        if(numReportTo == 0)
            errorsList.add(new FieldErrorWarning("multipleBillToException",META.SAMPLE.SAMPLE_ORGANIZATION.ORGANIZATION.getName()));
            
        if(numBillTo == 0)
            errorsList.add(new FormErrorWarning("multipleBillToException"));
    }
}
