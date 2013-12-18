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

import java.io.Serializable;
import java.util.ArrayList;

import org.openelis.domain.InventoryLocationViewDO;

public class InventoryLocationManager implements Serializable {

    private static final long                                serialVersionUID = 1L;

    protected Integer                                        inventoryItemId;
    protected ArrayList<InventoryLocationViewDO>             locations, deleted;

    protected transient static InventoryLocationManagerProxy proxy;

    protected InventoryLocationManager() {
    }

    /**
     * Creates a new instance of this object.
     */
    public static InventoryLocationManager getInstance() {
        return new InventoryLocationManager();
    }

    public int count() {
        if (locations == null)
            return 0;
        return locations.size();
    }

    public InventoryLocationViewDO getLocationAt(int i) {
        return locations.get(i);
    }

    public void setLocationAt(InventoryLocationViewDO parameter, int i) {
        if (locations == null)
            locations = new ArrayList<InventoryLocationViewDO>();
        locations.set(i, parameter);
    }

    public void addLocation(InventoryLocationViewDO parameter) {
        if (locations == null)
            locations = new ArrayList<InventoryLocationViewDO>();
        locations.add(parameter);
    }

    public void addLocationAt(InventoryLocationViewDO parameter, int i) {
        if (locations == null)
            locations = new ArrayList<InventoryLocationViewDO>();
        locations.add(i, parameter);
    }

    public void removeLocationAt(int i) {
        InventoryLocationViewDO tmp;

        if (locations == null || i >= locations.size())
            return;

        tmp = locations.remove(i);
        if (tmp.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<InventoryLocationViewDO>();
            deleted.add(tmp);
        }
    }

    // service methods
    public static InventoryLocationManager fetchByInventoryItemId(Integer id) throws Exception {
        return proxy().fetchByInventoryItemId(id);
    }

    public InventoryLocationManager add() throws Exception {
        return proxy().add(this);
    }

    public InventoryLocationManager update() throws Exception {
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

    ArrayList<InventoryLocationViewDO> getLocations() {
        return locations;
    }

    void setLocations(ArrayList<InventoryLocationViewDO> locations) {
        this.locations = locations;
    }

    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }

    InventoryLocationViewDO getDeletedAt(int i) {
        return deleted.get(i);
    }

    private static InventoryLocationManagerProxy proxy() {
        if (proxy == null)
            proxy = new InventoryLocationManagerProxy();
        return proxy;
    }
}
