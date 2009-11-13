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

import org.openelis.cache.server.DictionaryCacheRPC;
import org.openelis.domain.DictionaryDO;
import org.openelis.gwt.services.ScreenService;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.user.client.Window;

public class DictionaryCache {
    protected static final String DICTIONARY_CACHE_SERVICE_URL = "org.openelis.cache.server.DictionaryCacheService";
    protected ScreenService service;
    
    HashMap<String, DictionaryDO> systemNameList;
    HashMap<Integer, DictionaryDO> idList;
    HashMap<String, ArrayList<DictionaryDO>> categoryNameList;
    private static DictionaryCache instance;
    
    public static Integer getIdFromSystemName(String systemName) throws Exception {
        if(instance == null)
            instance = new DictionaryCache();
        
        return instance.getIdFromSystemNameInt(systemName);
    }
    
    public static String getSystemNameFromId(Integer id) throws Exception {
        if(instance == null)
            instance = new DictionaryCache();
        
        return instance.getSystemNameFromIdInt(id);
    }
    
    
    public static ArrayList<DictionaryDO> getListByCategorySystemName(String systemName) {
       if(instance == null)
            instance = new DictionaryCache();
        
       return instance.getListFromCategorySystemName(systemName);
    }
    
    public static DictionaryDO getEntryFromId(Integer id) throws Exception {
        if(instance == null)
            instance = new DictionaryCache();
        
       return instance.getEntryFromIdInt(id);
    }
    
    public DictionaryCache(){
        service = new ScreenService("OpenELISServlet?service="+DICTIONARY_CACHE_SERVICE_URL);
        
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
    
    protected Integer getIdFromSystemNameInt(final String systemName) throws Exception {
        DictionaryDO dictDO = systemNameList.get(systemName);
        
        if(dictDO == null){
            try{
                dictDO = (DictionaryDO)service.call("getIdBySystemName", systemName);
                
                if(dictDO != null){
                    systemNameList.put(dictDO.getSystemName(), dictDO);
                    idList.put(dictDO.getId(), dictDO);
                }
            }catch(Exception e){
                throw new Exception("DictionaryCache.getIdFromSystemName: \""+systemName+"\" not found in system.  Please call the system administrator.");    
            }
        }
        
        return dictDO.getId();
    }
    
    
    protected String getSystemNameFromIdInt(final Integer id) throws Exception {
        DictionaryDO dictDO = idList.get(id);
        
        if(dictDO == null){
            try{
                dictDO = (DictionaryDO)service.call("getSystemNameById", id);
                
                if(dictDO != null){
                    systemNameList.put(dictDO.getSystemName(), dictDO);
                    idList.put(dictDO.getId(), dictDO);
                }
            }catch(Exception e){
                throw new Exception("DictionaryCache getSystemNameFromId: id \""+id+"\" not found in system.  Please call the system administrator.");
            }
        }
        
        return dictDO.getSystemName();
    }
    
    protected DictionaryDO getEntryFromIdInt(final Integer id) throws Exception {
        DictionaryDO dictDO = idList.get(id);
        
        if(dictDO == null){
            try{
                dictDO = (DictionaryDO)service.call("getSystemNameById", id);
                
                if(dictDO != null){
                    systemNameList.put(dictDO.getSystemName(), dictDO);
                    idList.put(dictDO.getId(), dictDO);
                }
            }catch(Exception e){
                throw new Exception("DictionaryCache.getEntryFromId: id \""+id+"\" not found in system.  Please call the system administrator.");    
            }
        }
        
        return dictDO;
    }
    
    
    protected ArrayList<DictionaryDO> getListFromCategorySystemName(final String systemName) {
        ArrayList<DictionaryDO> list = categoryNameList.get(systemName);
        
        if(list == null){
            try{
                list = service.callList("getListByCategorySystemName", systemName);
            }catch(Exception e ){
                Window.alert(e.getMessage());
            }
            
            if(list != null){
                categoryNameList.put(systemName, list);
            
                //iterate through the results and insert them into the other lists
                for(int i=0; i<list.size(); i++){
                    DictionaryDO dictDO = (DictionaryDO)list.get(i);
                    
                    systemNameList.put(dictDO.getSystemName(), dictDO);
                    idList.put(dictDO.getId(), dictDO);
                }
            }
        }
        
        return list;
    }
}
