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
package org.openelis.modules.inventoryItem.server;

import java.util.ArrayList;

import org.openelis.domain.IdNameStoreVO;
import org.openelis.domain.InventoryItemDO;
import org.openelis.domain.InventoryItemViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.manager.InventoryComponentManager;
import org.openelis.manager.InventoryItemManager;
import org.openelis.manager.InventoryLocationManager;
import org.openelis.remote.InventoryItemRemote;
import org.openelis.server.EJBFactory;

public class InventoryItemService {
    public InventoryItemManager fetchById(Integer id) throws Exception {
        return EJBFactory.getInventoryItemManager().fetchById(id);
    }

    public ArrayList<InventoryItemDO> fetchActiveByName(String search) throws Exception {
        return EJBFactory.getInventoryItem().fetchActiveByName(search+"%", 100);
    }

    public ArrayList<InventoryItemDO> fetchActiveByNameAndStore(Query query) throws Exception {
        Integer storeId;
        String name;
        InventoryItemRemote remote;
        
        storeId = null;
        name = null;
        // parse the query to find name and/or store
        for (QueryData field : query.getFields()) {
            if (field.key != null) {
                if (field.key.endsWith("name"))
                    name = field.query;
                else if (field.key.endsWith("storeId"))
                    storeId = Integer.valueOf(field.query);
            }
        }
        remote = EJBFactory.getInventoryItem();
        if (storeId == null)
            return remote.fetchActiveByName(name+"%", 10);
        else
            return remote.fetchActiveByNameAndStore(name+"%", storeId, 10);
    }
    
    public ArrayList<InventoryItemDO> fetchActiveByNameStoreAndParentInventoryItem(Query query) throws Exception {
        Integer parentInventoryItemId;
        String name;
        
        //storeId = null;
        name = null;
        parentInventoryItemId = null;
        
        // parse the query to find name and/or store
        for (QueryData field : query.getFields()) {
            if (field.key != null) {
                if (field.key.endsWith("name"))
                    name = field.query;
                //else if (field.key.endsWith("storeId"))
                  //  storeId = Integer.valueOf(field.query);
                else if (field.key.endsWith("parentInventoryItemId"))
                    parentInventoryItemId = Integer.valueOf(field.query);
            }
        }
        
        return EJBFactory.getInventoryItem().fetchActiveByNameStoreAndParentInventoryItem(name+"%", parentInventoryItemId, 10);
    }
    
    public InventoryItemViewDO fetchInventoryItemById(Integer id) throws Exception {
        return EJBFactory.getInventoryItem().fetchById(id);
    }

    public InventoryItemManager fetchWithComponents(Integer id) throws Exception {
        return EJBFactory.getInventoryItemManager().fetchWithComponents(id);
    }

    public InventoryItemManager fetchWithLocations(Integer id) throws Exception {
        return EJBFactory.getInventoryItemManager().fetchWithLocations(id);
    }

    public InventoryItemManager fetchWithManufacturing(Integer id) throws Exception {
        return EJBFactory.getInventoryItemManager().fetchWithManufacturing(id);
    }

    public InventoryItemManager fetchWithNotes(Integer id) throws Exception {       
        return EJBFactory.getInventoryItemManager().fetchWithNotes(id);
    }

    public ArrayList<IdNameStoreVO> query(Query query) throws Exception {        
        return EJBFactory.getInventoryItem().query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
    }

    public InventoryItemManager add(InventoryItemManager man) throws Exception {        
        return EJBFactory.getInventoryItemManager().add(man);
    }

    public InventoryItemManager update(InventoryItemManager man) throws Exception {       
        return EJBFactory.getInventoryItemManager().update(man);
    }

    public InventoryItemManager fetchForUpdate(Integer id) throws Exception {        
        return EJBFactory.getInventoryItemManager().fetchForUpdate(id);
    }

    public InventoryItemManager abortUpdate(Integer id) throws Exception {       
        return EJBFactory.getInventoryItemManager().abortUpdate(id);
    }

    //
    // support for InventoryComponentManager and InventoryLocationManager
    //
    public InventoryComponentManager fetchComponentByInventoryItemId(Integer id) throws Exception {        
        return EJBFactory.getInventoryItemManager().fetchComponentByInventoryItemId(id);
    }
    
    public InventoryLocationManager fetchLocationByInventoryItemId(Integer id) throws Exception {        
        return EJBFactory.getInventoryItemManager().fetchLocationByInventoryItemId(id);
    }
}