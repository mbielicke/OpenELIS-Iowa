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

import org.openelis.domain.IdNameDO;
import org.openelis.gwt.services.ScreenService;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.user.client.Window;

public class ReferenceTableCache {
    protected static final String REFERENCETABLE_CACHE_SERVICE_URL = "org.openelis.cache.server.ReferenceTableCacheService";
    protected ScreenService service;
    HashMap<String, Integer> tableList;
    private static ReferenceTableCache instance;
    
    public static Integer getIdFromTableName(String tableName){
        if(instance == null)
            instance = new ReferenceTableCache();
        
        return instance.getIdFromTableNameInt(tableName);
    }
    
    public ReferenceTableCache(){
        service = new ScreenService("OpenELISServlet?service="+REFERENCETABLE_CACHE_SERVICE_URL);
        
        tableList = (HashMap<String, Integer>)OpenELIS.getCacheList().get("ReferenceTableCache");
        
        if(tableList == null){
            tableList = new HashMap<String, Integer>();
            OpenELIS.getCacheList().put("ReferenceTableCache", tableList);
        }
    }
    
    protected Integer getIdFromTableNameInt(final String tableName){
        Integer tableId = tableList.get(tableName);
        if(tableId == null){
            try{
                ReferenceTableCacheRPC rpc = service.call("getReferenceTableList", "");
                
                if(rpc.list != null){
                    for(int i=0; i<rpc.list.size(); i++){
                        IdNameDO tableDO = (IdNameDO)rpc.list.get(i);
                        tableList.put(tableName, tableDO.getId());
                    }
                    
                    tableId = tableList.get(tableName);
                }
            }catch(Exception e){
                Window.alert("ReferenceTableCache getIdFromTableName error: "+e.getMessage());    
            }
        }
        
        return tableId;
    }
}