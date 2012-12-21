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

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.InventoryItemBean;
import org.openelis.bean.InventoryItemManagerBean;
import org.openelis.domain.IdNameStoreVO;
import org.openelis.domain.InventoryItemDO;
import org.openelis.domain.InventoryItemViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.server.AppServlet;
import org.openelis.manager.InventoryComponentManager;
import org.openelis.manager.InventoryItemManager;
import org.openelis.manager.InventoryLocationManager;
import org.openelis.modules.inventoryItem.client.InventoryItemServiceInt;

@WebServlet("/openelis/inventoryItem")
public class InventoryItemServlet extends AppServlet implements InventoryItemServiceInt {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    InventoryItemManagerBean inventoryItemManager;
    
    @EJB
    InventoryItemBean        inventoryItem;

    public InventoryItemManager fetchById(Integer id) throws Exception {
        return inventoryItemManager.fetchById(id);
    }

    public ArrayList<InventoryItemDO> fetchActiveByName(String search) throws Exception {
        return inventoryItem.fetchActiveByName(search+"%", 100);
    }

    public ArrayList<InventoryItemDO> fetchActiveByNameAndStore(Query query) throws Exception {
        Integer storeId;
        String name;

        
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

        if (storeId == null)
            return inventoryItem.fetchActiveByName(name+"%", 10);
        else
            return inventoryItem.fetchActiveByNameAndStore(name+"%", storeId, 10);
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
        
        return inventoryItem.fetchActiveByNameStoreAndParentInventoryItem(name+"%", parentInventoryItemId, 10);
    }
    
    public InventoryItemViewDO fetchInventoryItemById(Integer id) throws Exception {
        return inventoryItem.fetchById(id);
    }

    public InventoryItemManager fetchWithComponents(Integer id) throws Exception {
        return inventoryItemManager.fetchWithComponents(id);
    }

    public InventoryItemManager fetchWithLocations(Integer id) throws Exception {
        return inventoryItemManager.fetchWithLocations(id);
    }

    public InventoryItemManager fetchWithManufacturing(Integer id) throws Exception {
        return inventoryItemManager.fetchWithManufacturing(id);
    }

    public InventoryItemManager fetchWithNotes(Integer id) throws Exception {       
        return inventoryItemManager.fetchWithNotes(id);
    }

    public ArrayList<IdNameStoreVO> query(Query query) throws Exception {        
        return inventoryItem.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
    }

    public InventoryItemManager add(InventoryItemManager man) throws Exception {        
        return inventoryItemManager.add(man);
    }

    public InventoryItemManager update(InventoryItemManager man) throws Exception {       
        return inventoryItemManager.update(man);
    }

    public InventoryItemManager fetchForUpdate(Integer id) throws Exception {        
        return inventoryItemManager.fetchForUpdate(id);
    }

    public InventoryItemManager abortUpdate(Integer id) throws Exception {       
        return inventoryItemManager.abortUpdate(id);
    }

    //
    // support for InventoryComponentManager and InventoryLocationManager
    //
    public InventoryComponentManager fetchComponentByInventoryItemId(Integer id) throws Exception {        
        return inventoryItemManager.fetchComponentByInventoryItemId(id);
    }
    
    public InventoryLocationManager fetchLocationByInventoryItemId(Integer id) throws Exception {        
        return inventoryItemManager.fetchLocationByInventoryItemId(id);
    }
}