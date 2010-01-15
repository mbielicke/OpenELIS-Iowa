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
package org.openelis.server.handlers;

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.domain.DictionaryCacheCategoryVO;
import org.openelis.domain.DictionaryCacheVO;
import org.openelis.domain.DictionaryDO;
import org.openelis.messages.DictionaryCacheMessage;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.persistence.MessageHandler;
import org.openelis.remote.DictionaryRemote;

public class DictionaryCacheHandler implements MessageHandler<DictionaryCacheMessage> {
    
    public void handle(DictionaryCacheMessage message) {
        String catSystemName = message.getCatDO().getSystemName();
        HashMap<String, ArrayList> listHash = (HashMap<String, ArrayList>)CachingManager.getElement("InitialData", "dictCategoryNameListValues");
        HashMap<String, DictionaryDO> tableHash = (HashMap<String, DictionaryDO>)CachingManager.getElement("InitialData", "dictSystemNameValues");
        HashMap<Integer, DictionaryDO> idHash = (HashMap<Integer, DictionaryDO>)CachingManager.getElement("InitialData", "dictIdValues");
        
        if(listHash != null){
            if(tableHash != null && idHash != null){
                ArrayList catList = listHash.get(catSystemName);
                
                if(catList != null){
                    for(int i = 0; i < catList.size();i++){
                        DictionaryDO dictDO = (DictionaryDO)catList.get(i);
                        tableHash.remove(dictDO.getSystemName());
                        idHash.remove(dictDO.getId());
                    }
                }
            }
            
            listHash.remove(catSystemName);
        }
    }
    
    public static DictionaryDO getDictionaryDOFromSystemName(String systemName) {
        HashMap<String, DictionaryDO> tableHash = (HashMap<String, DictionaryDO>)CachingManager.getElement("InitialData","dictSystemNameValues");
        HashMap<Integer, DictionaryDO> idHash = (HashMap<Integer, DictionaryDO>)CachingManager.getElement("InitialData","dictIdValues");
        DictionaryDO dictDO;
        if (tableHash == null) {
            tableHash = new HashMap<String, DictionaryDO>();
            idHash = new HashMap<Integer, DictionaryDO>();
        }

        dictDO = tableHash.get(systemName);
        if (dictDO == null) {
            try {
                dictDO = remote().fetchBySystemName(systemName);

                if (dictDO != null) {
                    tableHash.put(systemName, dictDO);
                    idHash.put(dictDO.getId(), dictDO);
                    CachingManager.putElement("InitialData", "dictSystemNameValues", tableHash);
                    CachingManager.putElement("InitialData", "dictIdValues", idHash);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return dictDO;
    }
    
    public static DictionaryDO getDictionaryDOFromId(Integer id) {
        HashMap<String, DictionaryDO> tableHash = (HashMap<String, DictionaryDO>)CachingManager.getElement("InitialData", "dictSystemNameValues");
        HashMap<Integer, DictionaryDO> idHash = (HashMap<Integer, DictionaryDO>)CachingManager.getElement("InitialData", "dictIdValues");
        DictionaryDO dictDO;
        if(tableHash == null){
            tableHash = new HashMap<String, DictionaryDO>();
            idHash = new HashMap<Integer, DictionaryDO>();   
        }
        
        dictDO = idHash.get(id);
        if(dictDO == null){
            try {
                dictDO = remote().fetchById(id);
                
                if(dictDO != null){
                    tableHash.put(dictDO.getSystemName(), dictDO);
                    idHash.put(id, dictDO);
                    CachingManager.putElement("InitialData", "dictSystemNameValues", tableHash);
                    CachingManager.putElement("InitialData", "dictIdValues", idHash);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } 
        
        return dictDO;
    }
    
    public static ArrayList getListByCategorySystemName(String categoryName) {
        HashMap<String, ArrayList> listHash = (HashMap<String, ArrayList>)CachingManager.getElement("InitialData", "dictCategoryNameListValues");
        HashMap<String, DictionaryDO> tableHash = (HashMap<String, DictionaryDO>)CachingManager.getElement("InitialData", "dictSystemNameValues");
        HashMap<Integer, DictionaryDO> idHash = (HashMap<Integer, DictionaryDO>)CachingManager.getElement("InitialData", "dictIdValues");
        ArrayList returnList;
        
        if(listHash == null)
            listHash = new HashMap<String, ArrayList>();
        
        if(tableHash == null || idHash == null){
            tableHash = new HashMap<String, DictionaryDO>();
            idHash = new HashMap<Integer, DictionaryDO>();
        }
        
        returnList = listHash.get(categoryName);
        
        if(returnList == null){
            try {
                returnList = (ArrayList)remote().fetchByCategorySystemName(categoryName);
            
                if(returnList != null){
                    listHash.put(categoryName, returnList);
                
                    //we need to iterate through this list and insert the entries into the other 2 lists
                    for(int i=0; i<returnList.size(); i++){
                        DictionaryDO dictDO = (DictionaryDO)returnList.get(i);
                        tableHash.put(dictDO.getSystemName(), dictDO);
                        idHash.put(dictDO.getId(), dictDO);
                    }
                
                    CachingManager.putElement("InitialData", "dictCategoryNameListValues", listHash);
                    CachingManager.putElement("InitialData", "dictSystemNameValues", tableHash);
                    CachingManager.putElement("InitialData", "dictIdValues", idHash);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        return returnList;
    }
    
    public static DictionaryCacheVO preloadByCategorySystemNames(DictionaryCacheVO cacheVO) {
        ArrayList<DictionaryCacheCategoryVO> currentList, tmpList;
        DictionaryCacheCategoryVO catVO;
        ArrayList<DictionaryDO> cacheList;
        DictionaryCacheVO returnDO;
        
        HashMap<String, ArrayList> listHash = (HashMap<String, ArrayList>)CachingManager.getElement("InitialData", "dictCategoryNameListValues");
        HashMap<String, DictionaryDO> tableHash = (HashMap<String, DictionaryDO>)CachingManager.getElement("InitialData", "dictSystemNameValues");
        HashMap<Integer, DictionaryDO> idHash = (HashMap<Integer, DictionaryDO>)CachingManager.getElement("InitialData", "dictIdValues");
        ArrayList returnList;
        
        if(listHash == null)
            listHash = new HashMap<String, ArrayList>();
        
        if(tableHash == null || idHash == null){
            tableHash = new HashMap<String, DictionaryDO>();
            idHash = new HashMap<Integer, DictionaryDO>();
        }
        
        tmpList = cacheVO.getList();
        currentList = new ArrayList<DictionaryCacheCategoryVO>();
        for(int i=tmpList.size()-1; i>-1; i--){
            cacheList = listHash.get(tmpList.get(i).getSystemName());
            if(cacheList != null){
                catVO = tmpList.remove(i);
                catVO.setDictionaryList(cacheList);
                currentList.add(catVO);
            }
        }
        
        //current list now has already loaded VOs
        //tmp list now has needed to be loaded VOs
        try{
            if(tmpList.size() > 0)
                tmpList = remote().preLoadBySystemName(tmpList);
        
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
        
        //initlize the return list
        returnDO = new DictionaryCacheVO();
        returnDO.setList(currentList);
        
        //  need to iterate through tmp list and insert into cache lists and add them to the return list
        for(int j=0; j<tmpList.size(); j++){
            catVO = tmpList.get(j);
            cacheList = catVO.getDictionaryList();

            
            listHash.put(catVO.getSystemName(), cacheList);
            
            //add this record to the return do
            returnDO.getList().add(catVO);
        
            //we need to iterate through this list and insert the entries into the other 2 lists
            for(int i=0; i<cacheList.size(); i++){
                DictionaryDO dictDO = (DictionaryDO)cacheList.get(i);
                tableHash.put(dictDO.getSystemName(), dictDO);
                idHash.put(dictDO.getId(), dictDO);
            }
        }
        
        //put the lists back in the cache
        CachingManager.putElement("InitialData", "dictCategoryNameListValues", listHash);
        CachingManager.putElement("InitialData", "dictSystemNameValues", tableHash);
        CachingManager.putElement("InitialData", "dictIdValues", idHash);
        
        return returnDO;
    }
    
    private static DictionaryRemote remote(){
        return (DictionaryRemote)EJBFactory.lookup("openelis/DictionaryBean/remote");
    }
}