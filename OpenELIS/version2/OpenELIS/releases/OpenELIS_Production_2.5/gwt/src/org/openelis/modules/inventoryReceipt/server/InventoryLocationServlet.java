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
package org.openelis.modules.inventoryReceipt.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.InventoryLocationBean;
import org.openelis.domain.InventoryLocationViewDO;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;
import org.openelis.modules.inventoryReceipt.client.InventoryLocationServiceInt;

@WebServlet("/openelis/inventoryLocation")
public class InventoryLocationServlet extends RemoteServlet implements InventoryLocationServiceInt {

    private static final long serialVersionUID = 1L;
    
    @EJB
    InventoryLocationBean inventoryLocation;

    public ArrayList<InventoryLocationViewDO> fetchByLocationNameInventoryItemId(Query query) throws Exception {
        Integer id;
        String search;
        
        search = query.getFields().get(0).getQuery();
        id = new Integer(query.getFields().get(1).getQuery());
        try {        
            return inventoryLocation.fetchByLocationNameInventoryItemId(search + "%", id,50);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }   
    
    public ArrayList<InventoryLocationViewDO> fetchByLocationNameInventoryItemIdStoreId(Query query) throws Exception {
        Integer itemId, storeId;
        String search;
        
        search = query.getFields().get(0).getQuery();
        itemId = new Integer(query.getFields().get(1).getQuery());
        storeId = new Integer(query.getFields().get(2).getQuery());
        try {        
            return inventoryLocation.fetchByLocationNameInventoryItemIdStoreId(search + "%", itemId, storeId, 50);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ArrayList<InventoryLocationViewDO> fetchByInventoryItemName(String search) throws Exception {
        try {        
            return inventoryLocation.fetchByInventoryItemName(search + "%", 50);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ArrayList<InventoryLocationViewDO> fetchByInventoryItemNameStoreId(Query query) throws Exception {
        Integer storeId;
        String search;
        
        search = query.getFields().get(0).getQuery();
        storeId = new Integer(query.getFields().get(1).getQuery());
        try {        
            return inventoryLocation.fetchByInventoryItemNameStoreId(search + "%", storeId, 50);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public InventoryLocationViewDO fetchById(Integer id) throws Exception {
        try {        
            return inventoryLocation.fetchById(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
}
