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
import org.openelis.modules.auxiliary.client.AuxiliaryService;

public class AuxFieldGroupManagerProxy {
    
    public AuxFieldGroupManagerProxy(){
    }
    
    public AuxFieldGroupManager fetchById(Integer id) throws Exception {
        return AuxiliaryService.get().fetchGroupById(id);
    }
    
    public AuxFieldGroupManager fetchByIdWithFields(Integer id) throws Exception{
        return AuxiliaryService.get().fetchGroupByIdWithFields(id);
    }
    
    public AuxFieldGroupManager add(AuxFieldGroupManager man) throws Exception {
        return AuxiliaryService.get().add(man);
    }
    
    public AuxFieldGroupManager update(AuxFieldGroupManager man) throws Exception {
        return AuxiliaryService.get().update(man);
    }
    
    public AuxFieldGroupManager fetchForUpdate(Integer id) throws Exception {
        return AuxiliaryService.get().fetchForUpdate(id);
    }
    
    public AuxFieldGroupManager abortUpdate(Integer id) throws Exception {
        return AuxiliaryService.get().abortUpdate(id);
    }
    
    public void validate(AuxFieldGroupManager man, ValidationErrorsList errorsList) throws Exception {
        
    }
}
 