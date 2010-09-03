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
package org.openelis.cache;

import java.util.HashMap;

import org.openelis.domain.InventoryItemDO;
import org.openelis.gwt.services.ScreenService;
import org.openelis.modules.main.client.openelis.OpenELIS;

public class InventoryItemCache {
    protected static final String               INVENTORY_ITEM_CACHE_SERVICE_URL = "org.openelis.server.cache.InventoryItemCacheService";
    protected ScreenService                     service;
    protected HashMap<Integer, InventoryItemDO> idList;
    private static InventoryItemCache           instance = new InventoryItemCache();  
    
    protected InventoryItemCache() {
        service = new ScreenService("OpenELISServlet?service="+INVENTORY_ITEM_CACHE_SERVICE_URL);
        
        idList = new HashMap<Integer, InventoryItemDO>();
        OpenELIS.getCacheList().put("InventoryItemsCache-id", idList);
    }
    
    public static InventoryItemDO getActiveInventoryItemFromId(Integer id) throws Exception {
        return instance.getActiveInventoryItemFromIdInt(id);
    }   

    protected InventoryItemDO getActiveInventoryItemFromIdInt(Integer id) throws Exception {
        InventoryItemDO data;
        
        data = idList.get(id);
        if(data == null){
            try{
                data = (InventoryItemDO)service.call("fetchActiveInventoryItemById", id);
                
                if(data != null)
                    idList.put(data.getId(), data);
                
            } catch(Exception e){
                e.printStackTrace();
                throw new Exception("InventoryItemCache.getActiveInventoryItemFromId: id \""+id+"\" not found in system.  Please call the system administrator.");    
            }
        }
        
        return data;
    }
}
