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

public class CategoryManagerProxy {
    protected static final String MANAGER_SERVICE_URL = "org.openelis.modules.dictionary.server.DictionaryService";
    protected ScreenService service;
    
    public CategoryManagerProxy() {
        service = new ScreenService("controller?service="+MANAGER_SERVICE_URL);
    }
    
    public CategoryManager fetchById(Integer id)throws Exception {
        return service.call("fetchById", id);
    }
    
    public CategoryManager fetchWithEntries(Integer id)throws Exception {
        return service.call("fetchWithEntries", id);
    }
    
    public CategoryManager add(CategoryManager man) throws Exception {
        return service.call("add",man);
    }
    
    public CategoryManager update(CategoryManager man) throws Exception {
        return service.call("update",man);
    }    
    
    public CategoryManager fetchForUpdate(Integer id) throws Exception {
        return service.call("fetchForUpdate",id);
    }
    
    public CategoryManager abortUpdate(Integer id) throws Exception {
        return service.call("abortUpdate", id);
    }
    
    public void validate(CategoryManager man) throws Exception {        
    }
}
