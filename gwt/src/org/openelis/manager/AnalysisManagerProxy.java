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

import org.openelis.domain.AnalysisTestDO;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.services.ScreenService;

public class AnalysisManagerProxy {
    protected static final String ANALYSIS_SERVICE_URL = "org.openelis.modules.analysis.server.AnalysisService";
    protected ScreenService service;
    
    public AnalysisManagerProxy(){
        service = new ScreenService("OpenELISServlet?service="+ANALYSIS_SERVICE_URL);
    }
    public AnalysisManager fetchBySampleItemId(Integer sampleItemId) throws Exception {
        return service.call("fetchBySampleItemId", sampleItemId);
    }
    
    public AnalysisManager add(AnalysisManager man) throws Exception {
        throw new UnsupportedOperationException();
    }
    
    public AnalysisManager update(AnalysisManager man) throws Exception{
        throw new UnsupportedOperationException();
    }
    
    public void validate(AnalysisManager man, ValidationErrorsList errorsList) throws Exception {
        if(man.count() == 0)
            errorsList.add(new FormErrorException("minOneAnalysisException"));
        
        for(int i=0; i<man.count(); i++){
            AnalysisTestDO analysisDO = man.getAnalysisAt(i);
            
            if(analysisDO.getTestId() == null)
                errorsList.add(new FormErrorException("analysisTestIdMissing"));
            
            
            man.getStorageAt(i).validate(errorsList);
        }
    }
}
