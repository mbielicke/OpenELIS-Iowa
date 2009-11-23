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

import org.openelis.domain.InventoryItemViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;

public class InventoryItemManager implements RPC, HasNotesInt {

    private static final long                            serialVersionUID = 1L;

    protected InventoryItemViewDO                        inventoryItem;
    protected InventoryComponentManager                  components;
    protected InventoryLocationManager                   locations;
    protected NoteManager                                manufacturing, notes;

    protected transient static InventoryItemManagerProxy proxy;

    /**
     * This is a protected constructor. See the three static methods for
     * allocation.
     */
    protected InventoryItemManager() {
        components = null;
        locations = null;
        manufacturing = null;
        notes = null;
        inventoryItem = null;
    }

    /**
     * Creates a new instance of this object. A default inventory item object is
     * also created.
     */
    public static InventoryItemManager getInstance() {
        InventoryItemManager manager;

        manager = new InventoryItemManager();
        manager.inventoryItem = new InventoryItemViewDO();

        return manager;
    }

    public InventoryItemViewDO getInventoryItem() {
        return inventoryItem;
    }

    public void setInventoryItem(InventoryItemViewDO inventoryItem) {
        this.inventoryItem = inventoryItem;
    }

    // service methods
    public static InventoryItemManager fetchById(Integer id) throws Exception {
        return proxy().fetchById(id);
    }

    public static InventoryItemManager fetchWithComponents(Integer id) throws Exception {
        return proxy().fetchWithComponents(id);
    }

    public static InventoryItemManager fetchWithLocations(Integer id) throws Exception {
        return proxy().fetchWithLocations(id);
    }

    public static InventoryItemManager fetchWithManufacturing(Integer id) throws Exception {
        return proxy().fetchWithManufacturing(id);
    }

    public static InventoryItemManager fetchWithNotes(Integer id) throws Exception {
        return proxy().fetchWithNotes(id);
    }

    public InventoryItemManager add() throws Exception {
        return proxy().add(this);
    }

    public InventoryItemManager update() throws Exception {
        return proxy().update(this);
    }

    public InventoryItemManager fetchForUpdate() throws Exception {
        return proxy().fetchForUpdate(inventoryItem.getId());
    }

    public InventoryItemManager abortUpdate() throws Exception {
        return proxy().abortUpdate(inventoryItem.getId());
    }

    public void validate() throws Exception {
        proxy().validate(this);
    }

    //
    // other managers
    //
    public InventoryComponentManager getComponents() throws Exception {
        if (components == null) {
            if (inventoryItem.getId() != null) {
                try {
                    components = InventoryComponentManager.fetchByInventoryItemId(inventoryItem.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (components == null)
                components = InventoryComponentManager.getInstance();
        }
        return components;
    }

    public InventoryLocationManager getLocations() throws Exception {
        if (locations == null) {
            if (inventoryItem.getId() != null) {
                try {
                    locations = InventoryLocationManager.fetchByInventoryItemId(inventoryItem.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (locations == null)
                locations = InventoryLocationManager.getInstance();
        }
        return locations;
    }

    public NoteManager getManufacturing() throws Exception {
        if (manufacturing == null) {
            if (inventoryItem.getId() != null) {
                try {
                    manufacturing = NoteManager.findByRefTableRefId(ReferenceTable.INVENTORY_ITEM_MANUFACTURING,
                                                                    inventoryItem.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (manufacturing == null)
                manufacturing = NoteManager.getInstance();
        }
        return manufacturing;
    }

    public NoteManager getNotes() throws Exception {
        if (notes == null) {
            if (inventoryItem.getId() != null) {
                try {
                    notes = NoteManager.findByRefTableRefId(ReferenceTable.INVENTORY_ITEM,
                                                            inventoryItem.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (notes == null)
                notes = NoteManager.getInstance();
        }
        return notes;
    }

    private static InventoryItemManagerProxy proxy() {
        if (proxy == null)
            proxy = new InventoryItemManagerProxy();

        return proxy;
    }
}
