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

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.domain.DictionaryDO;
import org.openelis.gwt.common.data.IntegerObject;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.modules.main.client.ScreenCache;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.SyncCallback;

public class DictionaryCache extends ScreenCache {
    HashMap<String, DictionaryDO> systemNameList;
    HashMap<Integer, DictionaryDO> idList;
    HashMap<String, ArrayList<DictionaryDO>> categoryNameList;
    private Object returnObj = null;
    private static DictionaryCache instance;
    
    public static Integer getIdFromSystemName(String systemName){
        if(instance == null)
            instance = new DictionaryCache();
        
        return instance.getIdFromSystemNameInt(systemName);
    }
    
    public static String getSystemNameFromId(Integer id){
        if(instance == null)
            instance = new DictionaryCache();
        
        return instance.getSystemNameFromIdInt(id);
    }
    
    
    public static ArrayList getListByCategorySystemName(String systemName){
       if(instance == null)
            instance = new DictionaryCache();
        
       return instance.getListFromCategorySystemName(systemName);
    }
    
    public DictionaryCache(){
        super("org.openelis.cache.server.DictionaryCacheService");
        systemNameList = (HashMap<String, DictionaryDO>)OpenELIS.getCacheList().get("DictSystemNameCache-systemName");
        idList = (HashMap<Integer, DictionaryDO>)OpenELIS.getCacheList().get("DictSystemNameCache-id");
        categoryNameList = (HashMap<String, ArrayList<DictionaryDO>>)OpenELIS.getCacheList().get("DictSystemNameCache-categoryName");
        
        if(systemNameList == null){
            systemNameList = new HashMap<String, DictionaryDO>();
            OpenELIS.getCacheList().put("DictSystemNameCache-systemName", systemNameList);
        }
        
        if(idList == null){
            idList = new HashMap<Integer, DictionaryDO>();
            OpenELIS.getCacheList().put("DictSystemNameCache-id", idList);
        }
        
        if(categoryNameList == null){
            categoryNameList = new HashMap<String, ArrayList<DictionaryDO>>();
            OpenELIS.getCacheList().put("DictSystemNameCache-categoryName", categoryNameList);
        }
    }
    
    protected Integer getIdFromSystemNameInt(final String systemName){
        returnObj = systemNameList.get(systemName);
        
        if(returnObj == null){
            screenService.call("getIdBySystemName", new StringObject(systemName), new SyncCallback<ArrayList>() {
                public void onSuccess(ArrayList result) {
                    if(result != null && result.size() == 1){
                        DictionaryDO dictDO = (DictionaryDO)result.get(0);
                        returnObj = dictDO;
                        
                        systemNameList.put(dictDO.getSystemName(), dictDO);
                        idList.put(dictDO.getId(), dictDO);
                    }
                }
                
                public void onFailure(Throwable caught){
                    Window.alert("FAIL");
                }
            });
        }
        
        if(returnObj != null)
            return ((DictionaryDO)returnObj).getId();
        else
            return null;
    }
    
    
    protected String getSystemNameFromIdInt(final Integer id){
        returnObj = idList.get(id);
        
        if(returnObj == null){
            screenService.call("getSystemNameById", new IntegerObject(id), new SyncCallback<ArrayList>() {
                public void onSuccess(ArrayList result) {
                    if(result != null && result.size() == 1){
                        DictionaryDO dictDO = (DictionaryDO)result.get(0);
                        returnObj = dictDO;
                        
                        systemNameList.put(dictDO.getSystemName(), dictDO);
                        idList.put(dictDO.getId(), dictDO);
                    }
                }
                public void onFailure(Throwable caught){
                }
            });
        }
        
        if(returnObj != null)
            return ((DictionaryDO)returnObj).getSystemName();
        else 
            return null;
    }
    
    
    protected ArrayList getListFromCategorySystemName(final String systemName) {
        returnObj = categoryNameList.get(systemName);
        
        if(returnObj == null){
             screenService.call("getListByCategorySystemName", new StringObject(systemName), new SyncCallback<ArrayList>() {
                public void onSuccess(ArrayList result) {
                    if(result != null && result.size() > 0){
                        returnObj = result;
                        categoryNameList.put(systemName, result);
                        
                        //iterate through the results and insert them into the other lists
                        for(int i=0; i<result.size(); i++){
                            DictionaryDO dictDO = (DictionaryDO)result.get(i);
                            
                            systemNameList.put(dictDO.getSystemName(), dictDO);
                            idList.put(dictDO.getId(), dictDO);
                        }
                    }
                }
                public void onFailure(Throwable caught){
                    Window.alert("cache error: "+caught.getMessage());
                }
            });
        }
        
        return (ArrayList)returnObj;
    }
}
