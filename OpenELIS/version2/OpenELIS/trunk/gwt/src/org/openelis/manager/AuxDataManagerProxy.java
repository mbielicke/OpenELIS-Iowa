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

import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.services.ScreenService;
import org.openelis.modules.auxiliary.client.AuxiliaryServiceParams;

public class AuxDataManagerProxy {
    protected static final String AUXILIARY_SERVICE_URL = "org.openelis.modules.auxData.server.AuxDataService";
    protected ScreenService service;
    
    public AuxDataManagerProxy(){
        service = new ScreenService("OpenELISServlet?service="+AUXILIARY_SERVICE_URL);
    }
    
    public AuxDataManager fetchById(Integer referenceId, Integer referenceTableId) throws Exception {
        AuxiliaryServiceParams p = new AuxiliaryServiceParams();
        p.referenceId = referenceId;
        p.referenceTableId = referenceTableId;
        
        return service.call("fetchById", p);
    }
    
    public AuxDataManager fetchByIdForUpdate(Integer referenceId, Integer referenceTableId) throws Exception {
        AuxiliaryServiceParams p = new AuxiliaryServiceParams();
        p.referenceId = referenceId;
        p.referenceTableId = referenceTableId;
        
        return service.call("fetchByIdForUpdate", p);
    }
    
    public AuxDataManager add(AuxDataManager man) throws Exception {
        assert false : "not supported";
        return null;
    }
    
    public AuxDataManager update(AuxDataManager man) throws Exception {
        assert false : "not supported";
        return null;
    }
    
    public void validate(AuxDataManager man, ValidationErrorsList errorsList) throws Exception {
        
    }
}
