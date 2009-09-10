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

import org.openelis.gwt.services.ScreenService;

public class TestManagerProxy {

    protected static final String TEST_MANAGER_SERVICE_URL = "org.openelis.modules.test.server.TestService";
    protected ScreenService service;
    
    public TestManagerProxy() {
        service = new ScreenService("OpenELISServlet?service="+TEST_MANAGER_SERVICE_URL);
    }
    
    public TestManager add(TestManager man) throws Exception {
        return service.call("add",man);
    }
    
    public TestManager update(TestManager man) throws Exception {
        try{
            return service.call("update",man);
        } catch(Exception e) {
            throw e;
        }
    }
    
    public TestManager fetch(Integer testId) throws Exception {
        return service.call("fetch", testId);
    }
    
    public TestManager fetchWithSampleTypes(Integer testId) throws Exception {
        return service.call("fetchWithSampleTypes", testId);
    }
    
    public TestManager fetchWithAnalytesAndResults(Integer testId) throws Exception {
        return service.call("fetchWithAnalytesAndResults", testId);
    }
    
    public TestManager fetchForUpdate(Integer testId) throws Exception {
        return service.call("fetchForUpdate",testId);
    }
    
    public TestManager abort(Integer testId) throws Exception {
        return service.call("abort", testId);
    }
    
    public void validate(TestManager man) throws Exception {
        
    }
}
