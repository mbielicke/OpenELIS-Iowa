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
package org.openelis.cache;

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.domain.DictionaryCacheCategoryVO;
import org.openelis.domain.DictionaryCacheCategoryListVO;
import org.openelis.domain.DictionaryDO;
import org.openelis.gwt.services.ScreenService;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.user.client.Window;

/**
 * Class provides cache service handling for front end GWT classes. Cache
 * objects in GWT instance are cached for the duration of the session and are
 * not updated -- if objects in the back-end are updated, the user will need to
 * restart the session to get updated objects.
 */

public class DictionaryCache {
    protected ScreenService                            service;
    protected HashMap<String, DictionaryDO>            systemNameList;
    protected HashMap<Integer, DictionaryDO>           idList;
    protected HashMap<String, ArrayList<DictionaryDO>> categoryNameList;

    protected static final String                      DICTIONARY_CACHE_SERVICE_URL = "org.openelis.server.cache.DictionaryCacheService";
    private static DictionaryCache                     instance                     = new DictionaryCache();

    protected DictionaryCache() {
        service = new ScreenService("OpenELISServlet?service=" + DICTIONARY_CACHE_SERVICE_URL);

        systemNameList = new HashMap<String, DictionaryDO>();
        OpenELIS.getCacheList().put("DictSystemNameCache-systemName", systemNameList);

        idList = new HashMap<Integer, DictionaryDO>();
        OpenELIS.getCacheList().put("DictSystemNameCache-id", idList);

        categoryNameList = new HashMap<String, ArrayList<DictionaryDO>>();
        OpenELIS.getCacheList().put("DictSystemNameCache-categoryName", categoryNameList);
    }

    public static Integer getIdFromSystemName(String systemName) throws Exception {
        return instance.getIdFromSystemNameInt(systemName);
    }

    public static String getSystemNameFromId(Integer id) throws Exception {
        return instance.getSystemNameFromIdInt(id);
    }

    public static void preloadByCategorySystemNames(String... systemNames) throws Exception {
        instance.preloadByCategorySystemNamesInt(systemNames);
    }

    public static ArrayList<DictionaryDO> getListByCategorySystemName(String systemName) {
        return instance.getListFromCategorySystemName(systemName);
    }

    public static DictionaryDO getEntryFromId(Integer id) throws Exception {
        return instance.getEntryFromIdInt(id);
    }

    protected Integer getIdFromSystemNameInt(final String systemName) throws Exception {
        DictionaryDO data;

        data = systemNameList.get(systemName);
        if (data == null) {
            try {
                data = (DictionaryDO)service.call("getIdBySystemName", systemName);

                if (data != null) {
                    systemNameList.put(data.getSystemName(), data);
                    idList.put(data.getId(), data);
                }
            } catch (Exception e) {
                throw new Exception("DictionaryCache.getIdFromSystemName: \"" + systemName +
                                    "\" not found in system.");
            }
        }

        return data.getId();
    }

    protected String getSystemNameFromIdInt(final Integer id) throws Exception {
        DictionaryDO data;

        data = idList.get(id);
        if (data == null) {
            try {
                data = (DictionaryDO)service.call("getSystemNameById", id);

                if (data != null) {
                    systemNameList.put(data.getSystemName(), data);
                    idList.put(data.getId(), data);
                }
            } catch (Exception e) {
                throw new Exception("DictionaryCache getSystemNameFromId: id \"" + id +
                                    "\" not found in system.");
            }
        }

        return data.getSystemName();
    }

    protected DictionaryDO getEntryFromIdInt(final Integer id) throws Exception {
        DictionaryDO data;

        data = idList.get(id);
        if (data == null) {
            try {
                data = (DictionaryDO)service.call("getSystemNameById", id);

                if (data != null) {
                    systemNameList.put(data.getSystemName(), data);
                    idList.put(data.getId(), data);
                }
            } catch (Exception e) {
                throw new Exception("DictionaryCache.getEntryFromId: id \"" + id +
                                    "\" not found in system.");
            }
        }

        return data;
    }

    protected void preloadByCategorySystemNamesInt(final String... systemNames) throws Exception {
        DictionaryCacheCategoryListVO cats;

        try {
            //
            // make a list of category system names that we don't have in the
            // cache
            cats = new DictionaryCacheCategoryListVO();
            cats.setList(new ArrayList<DictionaryCacheCategoryVO>());

            for (int i = 0; i < systemNames.length; i++ ) {
                if ( !categoryNameList.containsKey(systemNames[i])) {
                    DictionaryCacheCategoryVO cat;

                    cat = new DictionaryCacheCategoryVO();
                    cat.setSystemName(systemNames[i]);
                    cats.getList().add(cat);
                }
            }

            if (cats.getList().size() > 0)
                cats = service.call("preloadByCategorySystemNames", cats);

            // put the new values in the screen cache
            for (DictionaryCacheCategoryVO cat : cats.getList()) {
                categoryNameList.put(cat.getSystemName(), cat.getDictionaryList());

                // iterate through the results and insert them into the other
                // lists
                for (DictionaryDO data : cat.getDictionaryList()) {
                    systemNameList.put(data.getSystemName(), data);
                    idList.put(data.getId(), data);
                }
            }
        } catch (Exception e) {
            throw new Exception("DictionaryCache.preloadByCategorySystemNamesInt error."+ e.getMessage());
        }
    }

    protected ArrayList<DictionaryDO> getListFromCategorySystemName(String systemName) {
        ArrayList<DictionaryDO> list;

        list = categoryNameList.get(systemName);
        if (list == null) {
            try {
                list = service.callList("getListByCategorySystemName", systemName);
            } catch (Exception e) {
                Window.alert(e.getMessage());
            }

            if (list != null) {
                categoryNameList.put(systemName, list);

                // iterate through the results and insert them into the other
                // lists
                for (DictionaryDO data : list) {
                    systemNameList.put(data.getSystemName(), data);
                    idList.put(data.getId(), data);
                }
            }
        }

        return list;
    }
}