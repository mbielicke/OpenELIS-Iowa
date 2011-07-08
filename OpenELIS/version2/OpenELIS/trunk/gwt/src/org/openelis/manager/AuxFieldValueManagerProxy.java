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
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.services.ScreenService;

public class AuxFieldValueManagerProxy {
    protected static final String AUXILIARY_SERVICE_URL = "org.openelis.modules.auxiliary.server.AuxiliaryService";
    protected ScreenService service;
    
    public AuxFieldValueManagerProxy(){
        service = new ScreenService("controller?service="+AUXILIARY_SERVICE_URL);
    }
    
    public AuxFieldValueManager fetchByFieldId(Integer auxFieldId) throws Exception {
        return service.call("fetchFieldValueByFieldId", auxFieldId);
    }
    
    public AuxFieldValueManager add(AuxFieldValueManager man) throws Exception {
        throw new UnsupportedOperationException();
    }
    
    public AuxFieldValueManager update(AuxFieldValueManager man) throws Exception {
        throw new UnsupportedOperationException();    
    }
    
    public void validate(AuxFieldValueManager man, ValidationErrorsList errorsList) throws Exception {
        
    }
    
    public Integer getIdFromSystemName(String systemName) throws Exception {
        return DictionaryCache.getIdBySystemName(systemName);
    }
}
