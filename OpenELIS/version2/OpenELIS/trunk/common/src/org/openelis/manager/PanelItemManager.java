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
import java.util.ArrayList;

import org.openelis.domain.PanelItemDO;


public class PanelItemManager implements Serializable {

    private static final long serialVersionUID = 1L;
    
    protected Integer panelId;
    protected ArrayList<PanelItemDO> items, deleted;
    
    protected transient static PanelItemManagerProxy proxy;
    
    protected PanelItemManager() {
    }

    public static PanelItemManager getInstance() {
        return new PanelItemManager();
    }
    
    public int count() {
        if(items == null)
            return 0;
        return items.size();
    }
    
    public PanelItemDO getItemAt(int i) {
        return items.get(i);
    }
    
    public void setItemAt(PanelItemDO panelItem, int i) {
        items().set(i, panelItem);
    }
    
    public void addItem(PanelItemDO panelItem) {
        items().add(panelItem);
    }
    
    public void addItemAt(PanelItemDO panelItem, int i) {
        items().add(i, panelItem);
    }
    
    public void remoteItemAt(int i) {
        PanelItemDO tmp;
        
        if(items == null || i >= items().size())
            return;
        
        tmp = items().remove(i);
        if(tmp.getId() != null) {
            if(deleted == null)
                deleted = new ArrayList<PanelItemDO>();            
            deleted.add(tmp);
        }
    }
    
    public void moveItem(int oldIndex, int newIndex) {
        PanelItemDO item;
        
        if(items == null)
            return;
        
        item = items.remove(oldIndex);

        if (newIndex >= count())
            addItem(item);
        else
            addItemAt(item, newIndex);
    }
    
    // service methods
    public static PanelItemManager fetchByPanelId(Integer id) throws Exception {
        return proxy().fetchByPanelId(id);
    }
    
    public PanelItemManager add() throws Exception {
        return proxy().add(this);
    }
    
    public PanelItemManager update() throws Exception {
        return proxy().update(this);
    }
    
    public void delete() throws Exception {
        proxy().delete(this);            
    }
    
    public void validate() throws Exception {
        proxy().validate(this);
    }
    
    // friendly methods used by managers and proxies
    Integer getPanelId() {
        return panelId;
    }

    void setPanelId(Integer panelId) {
        this.panelId = panelId;
    }
    
    ArrayList<PanelItemDO> getItems() {
        return items;
    }

    void setItems(ArrayList<PanelItemDO> items) {
        this.items = items;
    }
    
    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }
    
    PanelItemDO getDeletedAt(int i) {
        return deleted.get(i);
    }
    
    private static PanelItemManagerProxy proxy() {
        if (proxy == null)
            proxy = new PanelItemManagerProxy();
        return proxy;        
    }
        
    
    private ArrayList<PanelItemDO> items() {
        if(items == null)
            items = new ArrayList<PanelItemDO>();
        return items;
    }

}
