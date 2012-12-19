package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.manager.InventoryComponentManager;
import org.openelis.manager.InventoryItemManager;
import org.openelis.manager.InventoryLocationManager;

@Remote
public interface InventoryItemManagerRemote {

    public InventoryItemManager fetchById(Integer id) throws Exception;

    public InventoryItemManager fetchWithComponents(Integer id) throws Exception;

    public InventoryItemManager fetchWithLocations(Integer id) throws Exception;

    public InventoryItemManager fetchWithManufacturing(Integer id) throws Exception;

    public InventoryItemManager fetchWithNotes(Integer id) throws Exception;

    public InventoryItemManager add(InventoryItemManager man) throws Exception;

    public InventoryItemManager update(InventoryItemManager man) throws Exception;

    public InventoryItemManager fetchForUpdate(Integer id) throws Exception;

    public InventoryItemManager abortUpdate(Integer id) throws Exception;
    
    public InventoryComponentManager fetchComponentByInventoryItemId(Integer id) throws Exception;

    public InventoryLocationManager fetchLocationByInventoryItemId(Integer id) throws Exception;
}
