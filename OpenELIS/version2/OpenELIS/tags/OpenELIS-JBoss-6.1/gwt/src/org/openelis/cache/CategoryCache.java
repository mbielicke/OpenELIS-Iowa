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

import org.openelis.domain.CategoryCacheVO;
import org.openelis.domain.DictionaryDO;
import org.openelis.gwt.services.ScreenService;

import com.google.gwt.user.client.Window;

/**
 * Class provides cache service handling for front end GWT classes. Cache
 * objects in GWT instance are cached for the duration of the session and are
 * not updated -- if objects in the back-end are updated, the user will need to
 * restart the session to get updated objects.
 */

public class CategoryCache {    
    protected static HashMap<String, CategoryCacheVO> cache;
    protected static final String                     SERVICE_URL;
    protected static ScreenService                    service;
    
    static {
        cache = new HashMap<String, CategoryCacheVO>();
        SERVICE_URL = "org.openelis.server.CategoryCacheService";
        service = new ScreenService("controller?service=" + SERVICE_URL);
    }

    public static ArrayList<CategoryCacheVO> getBySystemNames(String... systemNames) throws Exception {
        int i, j;
        ArrayList<CategoryCacheVO> partial, full;
        String partialNames[];
        CategoryCacheVO entry;

        //
        // make a list of category system names that we don't have in the
        // cache
        full = new ArrayList<CategoryCacheVO>();
        partialNames = new String[systemNames.length];
        for (i = 0, j = 0; i < systemNames.length; i++ ) {
            entry = cache.get(systemNames[i]);
            if (entry == null)             
                partialNames[j++] = systemNames[i];
            else
                full.add(entry);
        }
        try {
            if (j > 0) {
                partial = service.callList("getBySystemNames", partialNames);

                for (CategoryCacheVO cat : partial) {
                    full.add(cat);
                    cache.put(cat.getSystemName(), cat);                    
                    for (DictionaryDO data : cat.getDictionaryList()) 
                        DictionaryCache.add(data);                    
                }
            }
        } catch (Exception e) {
            throw new Exception("CategoryCache.getBySystemNamesInt error." + e.getMessage());
        }

        return full;
    }

    public static ArrayList<DictionaryDO> getBySystemName(String systemName) {
        CategoryCacheVO cat;

        cat = cache.get(systemName);
        if (cat == null) {
            try {
                cat = service.call("getBySystemName", systemName);
                cache.put(systemName, cat);
                for (DictionaryDO entry : cat.getDictionaryList())
                    DictionaryCache.add(entry);
            } catch (Exception e) {
                Window.alert(e.getMessage());
                return null;
            }
        } 
        return cat.getDictionaryList();
    }
}
