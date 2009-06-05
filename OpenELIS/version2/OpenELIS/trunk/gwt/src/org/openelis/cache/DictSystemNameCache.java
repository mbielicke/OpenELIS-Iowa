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

import org.openelis.gwt.common.data.IntegerObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.modules.main.client.ScreenCache;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.user.client.rpc.SyncCallback;

public class DictSystemNameCache extends ScreenCache {
    HashMap<String, Integer> systemNameList, categoryNameList;
    HashMap<Integer, String> idList;
    private Object returnObj = null;
    private static DictSystemNameCache instance;
    
    public static Integer getIdFromSystemName(String systemName){
        if(instance == null)
            instance = new DictSystemNameCache();
        
        return instance.getIdFromSystemNameInt(systemName);
    }
    
    public static String getSystemNameFromId(Integer id){
        if(instance == null)
            instance = new DictSystemNameCache();
        
        return instance.getSystemNameFromIdInt(id);
    }
    
    /*
    public static void loadCacheByCategory(String systemName){
        if(instance == null)
            instance = new DictSystemNameCache();
        
        instance.loadCacheByCategoryInt(systemName);
    }*/
    
    public DictSystemNameCache(){
        super("org.openelis.cache.server.DictSystemNameCacheService");
        
        systemNameList = (HashMap<String, Integer>)OpenELIS.getCacheList().get("DictSystemNameCache-systemName");
        idList = (HashMap<Integer, String>)OpenELIS.getCacheList().get("DictSystemNameCache-id");
        categoryNameList = (HashMap<String, Integer>)OpenELIS.getCacheList().get("DictSystemNameCache-categoryName");
        
        if(systemNameList == null){
            systemNameList = new HashMap<String, Integer>();
            OpenELIS.getCacheList().put("DictSystemNameCache-systemName", systemNameList);
        }
        
        if(idList == null){
            idList = new HashMap<Integer, String>();
            OpenELIS.getCacheList().put("DictSystemNameCache-id", idList);
        }
        
        if(categoryNameList == null){
            categoryNameList = new HashMap<String, Integer>();
            OpenELIS.getCacheList().put("DictSystemNameCache-categoryName", categoryNameList);
        }
    }
    
    protected Integer getIdFromSystemNameInt(final String systemName){
        returnObj = systemNameList.get(systemName);
        
        if(returnObj == null){
            screenService.call("getIdBySystemName", new StringObject(systemName), new SyncCallback<IntegerObject>() {
                public void onSuccess(IntegerObject result) {
                    if(result != null){
                        returnObj = result.getValue();
                        systemNameList.put(systemName, (Integer)returnObj);
                        idList.put((Integer)returnObj, systemName);
                    }
                }
                public void onFailure(Throwable caught){
                }
            });
        }
        
        return (Integer)returnObj;
    }
    
    
    protected String getSystemNameFromIdInt(final Integer id){
        returnObj = idList.get(id);
        
        if(returnObj == null){
            screenService.call("getSystemNameById", new IntegerObject(id), new SyncCallback<StringObject>() {
                public void onSuccess(StringObject result) {
                    if(result != null){
                        returnObj = result.getValue();
                        systemNameList.put((String)returnObj, id);
                        idList.put(id, (String)returnObj);
                    }
                }
                public void onFailure(Throwable caught){
                }
            });
        }
        
        return (String)returnObj;
    }
    
    /*
    protected void loadCacheByCategoryInt(String systemName) {
        screenService.call("getSystemNameById", new IntegerObject(id), new SyncCallback<StringObject>() {
            public void onSuccess(StringObject result) {
                if(result != null){
                    returnObj = result.getValue();
                    systemNameList.put((String)returnObj, id);
                    idList.put(id, (String)returnObj);
                }
            }
            public void onFailure(Throwable caught){
            }
        });
    }*/
}
