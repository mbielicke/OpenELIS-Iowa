/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.server.handlers;

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.domain.DictionaryCacheCategoryVO;
import org.openelis.domain.DictionaryCacheCategoryListVO;
import org.openelis.domain.DictionaryDO;
import org.openelis.messages.DictionaryCacheMessage;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.persistence.MessageHandler;
import org.openelis.remote.DictionaryRemote;

public class DictionaryCacheHandler implements MessageHandler<DictionaryCacheMessage> {

    protected static HashMap<Integer, DictionaryDO> idValues;
    protected static HashMap<String, DictionaryDO>  systemNameValues;
    protected static HashMap<String, ArrayList<DictionaryDO>> categoryNameListValues;

    static {
        idValues = new HashMap<Integer, DictionaryDO>();
        systemNameValues = new HashMap<String, DictionaryDO>();
        categoryNameListValues = new HashMap<String, ArrayList<DictionaryDO>>();
    }
    
    /*
     * Method is used to receive messages with updated dictionary information.
     * Currently, the method removes the entries from various cache lists --
     * subsequent requests will refresh the cache. 
     */
    public void handle(DictionaryCacheMessage message) {
        String categoryName;
        ArrayList<DictionaryDO> list;

        categoryName = message.getCatDO().getSystemName();

        list = categoryNameListValues.get(categoryName);
        if (list != null) {
            for (DictionaryDO data : list) { 
                systemNameValues.remove(data.getSystemName());
                idValues.remove(data.getId());
            }
            categoryNameListValues.remove(categoryName);
        }
    }

    public static DictionaryDO getDictionaryDOFromSystemName(String systemName) {
        DictionaryDO data;

        data = systemNameValues.get(systemName);
        if (data == null) {
            try {
                data = remote().fetchBySystemName(systemName);
                if (data != null) {
                    systemNameValues.put(systemName, data);
                    idValues.put(data.getId(), data);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return data;
    }

    public static DictionaryDO getDictionaryDOFromId(Integer id) {
        DictionaryDO data;

        data = idValues.get(id);
        if (data == null) {
            try {
                data = remote().fetchById(id);
                if (data != null) {
                    systemNameValues.put(data.getSystemName(), data);
                    idValues.put(id, data);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return data;
    }

    public static ArrayList<DictionaryDO> getListByCategorySystemName(String categoryName) {
        ArrayList<DictionaryDO> list;

        list = categoryNameListValues.get(categoryName);
        if (list == null) {
            try {
                list = remote().fetchByCategorySystemName(categoryName);
                if (list != null) {
                    categoryNameListValues.put(categoryName, list);
                    // we need to iterate through this list and insert the
                    // entries into the other 2 lists
                    for (DictionaryDO data : list) {
                        systemNameValues.put(data.getSystemName(), data);
                        idValues.put(data.getId(), data);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return list;
    }

    public static DictionaryCacheCategoryListVO preloadByCategorySystemNames(DictionaryCacheCategoryListVO cacheVO) {
        ArrayList<DictionaryCacheCategoryVO> currentList, tmpList;
        DictionaryCacheCategoryVO catVO;
        ArrayList<DictionaryDO> cacheList;
        DictionaryCacheCategoryListVO returnDO;
        ArrayList returnList;

        tmpList = cacheVO.getList();
        currentList = new ArrayList<DictionaryCacheCategoryVO>();
        for (int i = tmpList.size() - 1; i > -1; i-- ) {
            cacheList = categoryNameListValues.get(tmpList.get(i).getSystemName());
            if (cacheList != null) {
                catVO = tmpList.remove(i);
                catVO.setDictionaryList(cacheList);
                currentList.add(catVO);
            }
        }

        // current list now has already loaded VOs
        // tmp list now has needed to be loaded VOs
        try {
            if (tmpList.size() > 0)
                tmpList = remote().preLoadBySystemName(tmpList);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // initlize the return list
        returnDO = new DictionaryCacheCategoryListVO();
        returnDO.setList(currentList);

        // need to iterate through tmp list and insert into cache lists and add
        // them to the return list
        for (int j = 0; j < tmpList.size(); j++ ) {
            catVO = tmpList.get(j);
            cacheList = catVO.getDictionaryList();

            categoryNameListValues.put(catVO.getSystemName(), cacheList);

            // add this record to the return do
            returnDO.getList().add(catVO);

            // we need to iterate through this list and insert the entries into
            // the other 2 lists
            for (int i = 0; i < cacheList.size(); i++ ) {
                DictionaryDO dictDO = (DictionaryDO)cacheList.get(i);
                systemNameValues.put(dictDO.getSystemName(), dictDO);
                idValues.put(dictDO.getId(), dictDO);
            }
        }

        // put the lists back in the cache
        CachingManager.putElement("InitialData", "dictCategoryNameListValues",
                                  categoryNameListValues);
        CachingManager.putElement("InitialData", "dictSystemNameValues", systemNameValues);
        CachingManager.putElement("InitialData", "dictIdValues", idValues);

        return returnDO;
    }

    private static DictionaryRemote remote() {
        return (DictionaryRemote)EJBFactory.lookup("openelis/DictionaryBean/remote");
    }
}