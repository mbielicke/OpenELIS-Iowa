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

import java.util.HashMap;

import org.openelis.domain.DictionaryDO;
import org.openelis.gwt.services.ScreenService;

/**
 * Class provides cache service handling for front end GWT classes. Cache
 * objects in GWT instance are cached for the duration of the session and are
 * not updated -- if objects in the back-end are updated, the user will need to
 * restart the session to get updated objects.
 */

public class DictionaryCache {    
    protected static HashMap<Object, DictionaryDO> cache;
    protected static final String                  SERVICE_URL;
    protected static ScreenService                 service;
    
    static {
        cache = new HashMap<Object, DictionaryDO>();
        SERVICE_URL = "org.openelis.server.DictionaryCacheService";
        service = new ScreenService("controller?service=" + SERVICE_URL);
    }
    
    public static DictionaryDO getById(Integer id) throws Exception {
        DictionaryDO data;

        data = cache.get(id);
        if (data == null) {
            try {
                data = (DictionaryDO)service.call("getById", id);
                add(data);
            } catch (Exception e) {
                throw new Exception("DictionaryCache.getEntryFromId: id \"" + id +
                                    "\" not found in system.");
            }
        }

        return data;
    }    
    
    public static Integer getIdBySystemName(String systemName) throws Exception {
        DictionaryDO data;

        data = getBySystemName(systemName);
        if (data != null)
            return data.getId();

        return null;
    }
    
    public static DictionaryDO getBySystemName(String systemName) throws Exception {
        DictionaryDO data;

        data = cache.get(systemName);
        if (data != null)
            return data;

        try {
            data = (DictionaryDO)service.call("getBySystemName", systemName);
            add(data);
        } catch (Exception e) {
            throw new Exception("DictionaryCache.getBySystemNameInt: \"" + systemName +
                                "\" not found in system.");
        }

        return data;
    }  

    public static String getSystemNameById(Integer id) throws Exception {
        DictionaryDO data;

        data = getById(id);
        if (data != null)
            return data.getSystemName();

        return null;
    }   
    
    /**
     * Friendly method for other caches to add new entries to DictionaryCache
     */
    static void add(DictionaryDO data) {
        if (data != null) {
            cache.put(data.getId(), data);
            if (data.getSystemName() != null)
                cache.put(data.getSystemName(), data);
        }
    }
}