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

import org.openelis.modules.panel.client.PanelService;

public class PanelManagerProxy {
    
    public PanelManagerProxy() {
    }
    
    public PanelManager fetchById(Integer id) throws Exception {
        return PanelService.get().fetchById(id);
    }
    
    public PanelManager fetchWithItems(Integer id) throws Exception {
        return PanelService.get().fetchWithItems(id);
    }
    
    public PanelManager add(PanelManager man) throws Exception {
        return PanelService.get().add(man);
    }
    
    public PanelManager update(PanelManager man) throws Exception {
        return PanelService.get().update(man);
    }
    
    public void delete(PanelManager man) throws Exception {
        PanelService.get().delete(man);
    }
    
    public PanelManager fetchForUpdate(Integer id) throws Exception {
        return PanelService.get().fetchForUpdate(id);
    }
    
    public PanelManager abortUpdate(Integer id) throws Exception {
        return PanelService.get().abortUpdate(id);
    }
    
    @SuppressWarnings("unused")
    public void validate(PanelManager man) throws Exception {
    }
}
