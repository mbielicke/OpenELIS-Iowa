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

public class ExchangeCriteriaManagerProxy {

    protected static final String MANAGER_SERVICE_URL = "org.openelis.modules.exchangeDataSelection.server.ExchangeDataSelectionService";
    protected ScreenService       service;

    public ExchangeCriteriaManagerProxy() {
        service = new ScreenService("controller?service=" + MANAGER_SERVICE_URL);
    }

    public ExchangeCriteriaManager fetchById(Integer id) throws Exception {
        return service.call("fetchById", id);
    }
    
    public ExchangeCriteriaManager fetchByName(String name) throws Exception {
        return service.call("fetchByName", name);
    }

    public ExchangeCriteriaManager fetchWithProfiles(Integer id) throws Exception {
        return service.call("fetchWithProfiles", id);
    }
    
    public ExchangeCriteriaManager fetchWithProfilesByName(String name) throws Exception {
        return service.call("fetchWithProfilesByName", name);
    }

    public ExchangeCriteriaManager add(ExchangeCriteriaManager man) throws Exception {
        return service.call("add", man);
    }

    public ExchangeCriteriaManager update(ExchangeCriteriaManager man) throws Exception {
        return service.call("update", man);
    }
    
    public void delete(ExchangeCriteriaManager man) throws Exception {
        service.call("delete", man);
    }

    public ExchangeCriteriaManager fetchForUpdate(Integer id) throws Exception {
        return service.call("fetchForUpdate", id);
    }

    public ExchangeCriteriaManager abortUpdate(Integer id) throws Exception {
        return service.call("abortUpdate", id);
    }

    public void validate(ExchangeCriteriaManager man) throws Exception {
    }
}