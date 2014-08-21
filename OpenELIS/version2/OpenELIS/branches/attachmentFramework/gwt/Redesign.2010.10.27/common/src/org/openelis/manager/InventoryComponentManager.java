/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.domain.InventoryComponentViewDO;
import org.openelis.gwt.common.RPC;

public class InventoryComponentManager implements RPC {

    private static final long                                 serialVersionUID = 1L;

    protected Integer                                         inventoryItemId;
    protected ArrayList<InventoryComponentViewDO>             components, deleted;

    protected transient static InventoryComponentManagerProxy proxy;

    protected InventoryComponentManager() {
    }

    /**
     * Creates a new instance of this object.
     */
    public static InventoryComponentManager getInstance() {
        return new InventoryComponentManager();
    }

    public int count() {
        if (components == null)
            return 0;
        return components.size();
    }

    public InventoryComponentViewDO getComponentAt(int i) {
        return components.get(i);
    }

    public void setComponentAt(InventoryComponentViewDO parameter, int i) {
        if (components == null)
            components = new ArrayList<InventoryComponentViewDO>();
        components.set(i, parameter);
    }

    public void addComponent(InventoryComponentViewDO parameter) {
        if (components == null)
            components = new ArrayList<InventoryComponentViewDO>();
        components.add(parameter);
    }

    public void addComponentAt(InventoryComponentViewDO parameter, int i) {
        if (components == null)
            components = new ArrayList<InventoryComponentViewDO>();
        components.add(i, parameter);
    }

    public void removeComponentAt(int i) {
        InventoryComponentViewDO tmp;

        if (components == null || i >= components.size())
            return;

        tmp = components.remove(i);
        if (tmp.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<InventoryComponentViewDO>();
            deleted.add(tmp);
        }
    }

    // service methods
    public static InventoryComponentManager fetchByInventoryItemId(Integer id) throws Exception {
        return proxy().fetchByInventoryItemId(id);
    }

    public InventoryComponentManager add() throws Exception {
        return proxy().add(this);
    }

    public InventoryComponentManager update() throws Exception {
        return proxy().update(this);
    }

    public void validate() throws Exception {
        proxy().validate(this);
    }

    // friendly methods used by managers and proxies
    Integer getInventoryItemId() {
        return inventoryItemId;
    }

    void setInventoryItemId(Integer id) {
        inventoryItemId = id;
    }

    ArrayList<InventoryComponentViewDO> getComponents() {
        return components;
    }

    void setComponents(ArrayList<InventoryComponentViewDO> components) {
        this.components = components;
    }

    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }

    InventoryComponentViewDO getDeletedAt(int i) {
        return deleted.get(i);
    }

    private static InventoryComponentManagerProxy proxy() {
        if (proxy == null)
            proxy = new InventoryComponentManagerProxy();
        return proxy;
    }
}
