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

import java.io.Serializable;

import org.openelis.domain.PanelDO;
import org.openelis.gwt.common.NotFoundException;

public class PanelManager implements Serializable {

    private static final long serialVersionUID = 1L;
    
    protected PanelDO panel;
    protected PanelItemManager items;    
    
    protected transient static PanelManagerProxy proxy;
    
    protected PanelManager() {
        panel = null;
        items = null;
    }
    
    /**
     * Creates a new instance of this object. A default Panel object is also
     * created.
     */
    public static PanelManager getInstance() {
        PanelManager manager;
        
        manager = new PanelManager();
        manager.panel = new PanelDO();
        
        return manager;
    }

    public PanelDO getPanel() {
        return panel;
    }

    public void setPanel(PanelDO panel) {
        this.panel = panel;
    }
    
    public static PanelManager fetchById(Integer id) throws Exception {
        return proxy().fetchById(id);
    }
    
    public static PanelManager fetchWithItems(Integer id) throws Exception {
        return proxy().fetchWithItems(id);
    }
    
    public PanelManager add() throws Exception {
        return proxy().add(this);
    }
    
    public PanelManager update() throws Exception {
        return proxy().update(this);
    }
    
    public void delete() throws Exception {
        proxy().delete(this);
    }
    
    public PanelManager fetchForUpdate() throws Exception {
        return proxy().fetchForUpdate(panel.getId());
    }
    
    public PanelManager abortUpdate() throws Exception {
        return proxy().abortUpdate(panel.getId());
    }
    
    public void validate() throws Exception {
        proxy().validate(this);
    }
    
    //
    // other managers
    //
    public PanelItemManager getItems() throws Exception {
        if(items == null) {
            if(panel.getId() != null) {
                try {
                    items = PanelItemManager.fetchByPanelId(panel.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (items == null)
                items = PanelItemManager.getInstance();
        }
        return items;
    }
    
    private static PanelManagerProxy proxy() {
        if(proxy == null)
            proxy = new PanelManagerProxy();
        
        return proxy;
    }
}
