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


public class OrganizationsManagerProxy {

    protected static final String ORG_MANAGER_SERVICE_URL = "org.openelis.modules.organization.server.OrganizationService";
    protected ScreenService service;
    
    public OrganizationsManagerProxy(){
        service = new ScreenService("OpenELISServlet?service="+ORG_MANAGER_SERVICE_URL);
    }
    
    public OrganizationsManager add(OrganizationsManager man) throws Exception {
        return service.call("add",man);
    }

    public OrganizationsManager update(OrganizationsManager man) throws Exception {
        return service.call("update", man);
    }

    public OrganizationsManager fetch(Integer orgId) throws Exception {
        return service.call("fetch", orgId);
    }
    
    public OrganizationsManager fetchWithContacts(Integer orgId) throws Exception {
        return service.call("fetchWithContacts", orgId);
    }
    
    public OrganizationsManager fetchWithNotes(Integer orgId) throws Exception {
        return service.call("fetchWithNotes", orgId);
    }
    
    public OrganizationsManager fetchWithIdentifiers(Integer orgId) throws Exception {
        return service.call("fetchWithIdentifiers", orgId);
    }

    public OrganizationsManager fetchForUpdate(Integer orgId) throws Exception {
        return service.call("fetchForUpdate", orgId);
    }
    
    public OrganizationsManager abort(Integer orgId) throws Exception {
        return service.call("abort", orgId);
    }
    
    public void validate(OrganizationsManager man) throws Exception {
        
    }
}