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
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.manager.InventoryComponentManager;
import org.openelis.manager.InventoryItemManager;
import org.openelis.manager.InventoryLocationManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.InventoryItemManagerRemote;
import org.openelis.remote.InventoryItemRemote;

public class InventoryItemService {

    private static final int rowPP = 23;

    public InventoryItemManager fetchById(Integer id) throws Exception {
        try {
            return remoteManager().fetchById(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public ArrayList<InventoryItemDO> fetchActiveByName(String search) throws Exception {
        return remote().fetchActiveByName(search+"%", 10);
    }

    public ArrayList<InventoryItemDO> fetchActiveByNameAndStore(Query query) throws Exception {
        Integer storeId = null;
        String name = null;
        
        // parse the query to find name and/or store
        for (QueryData field : query.getFields()) {
            if (field.key != null) {
                if (field.key.endsWith("name"))
                    name = field.query;
                else if (field.key.endsWith("storeId"))
                    storeId = Integer.valueOf(field.query);
            }
        }
        if (storeId == null)
            return remote().fetchActiveByName(name+"%", 10);
        else
            return remote().fetchActiveByNameAndStore(name+"%", storeId, 10);
    }

    public InventoryItemManager fetchWithComponents(Integer id) throws Exception {
        try {
            return remoteManager().fetchWithComponents(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public InventoryItemManager fetchWithLocations(Integer id) throws Exception {
        try {
            return remoteManager().fetchWithLocations(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public InventoryItemManager fetchWithManufacturing(Integer id) throws Exception {
        try {
            return remoteManager().fetchWithManufacturing(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public InventoryItemManager fetchWithNotes(Integer id) throws Exception {
        try {
            return remoteManager().fetchWithNotes(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public ArrayList<IdNameStoreVO> query(Query query) throws Exception {
        try {
            return remote().query(query.getFields(), query.getPage() * rowPP, rowPP);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public InventoryItemManager add(InventoryItemManager man) throws Exception {
        try {
            return remoteManager().add(man);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public InventoryItemManager update(InventoryItemManager man) throws Exception {
        try {
            return remoteManager().update(man);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }

    }

    public InventoryItemManager fetchForUpdate(Integer id) throws Exception {
        try {
            return remoteManager().fetchForUpdate(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    public InventoryItemManager abortUpdate(Integer id) throws Exception {
        try {
            return remoteManager().abortUpdate(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    //
    // support for InventoryComponentManager and InventoryLocationManager
    //
    public InventoryComponentManager fetchComponentByInventoryItemId(Integer id) throws Exception {
        try {
            return remoteManager().fetchComponentByInventoryItemId(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    public InventoryLocationManager fetchLocationByInventoryItemId(Integer id) throws Exception {
        try {
            return remoteManager().fetchLocationByInventoryItemId(id);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }

    private InventoryItemRemote remote() {
        return (InventoryItemRemote)EJBFactory.lookup("openelis/InventoryItemBean/remote");
    }

    private InventoryItemManagerRemote remoteManager() {
        return (InventoryItemManagerRemote)EJBFactory.lookup("openelis/InventoryItemManagerBean/remote");
    }
}