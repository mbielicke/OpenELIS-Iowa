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

import org.openelis.domain.SectionDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.gwt.services.ScreenService;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.user.client.Window;

/**
 * Class provides cache service handling for front end GWT classes. Cache
 * objects in GWT instance are cached for the duration of the session and are
 * not updated -- if objects in the back-end are updated, the user will need to
 * restart the session to get updated objects.
 */

public class SectionCache {
    protected ScreenService                                service;
    protected static HashMap<String, ArrayList<SectionViewDO>> nameList;
    protected static HashMap<Integer, SectionViewDO>       idList;
    
    protected static final String                          SECTION_CACHE_SERVICE_URL = "org.openelis.server.cache.SectionCacheService";
    private static SectionCache                            instance                  = new SectionCache();    

    protected SectionCache() {
        service = new ScreenService("controller?service=" + SECTION_CACHE_SERVICE_URL);

        nameList = new HashMap<String, ArrayList<SectionViewDO>>();
        OpenELIS.getCacheList().put("SectionsCache-name", nameList);

        idList = new HashMap<Integer, SectionViewDO>();
        OpenELIS.getCacheList().put("SectionsCache-id", idList);
    }
    
    public static ArrayList<SectionViewDO> getSectionList() {
        return instance.getSectionListInt();
    }

    public static SectionViewDO getSectionFromId(Integer id) throws Exception {
        return instance.getSectionFromIdInt(id);
    }

    protected ArrayList<SectionViewDO> getSectionListInt() {
        ArrayList<SectionViewDO> returnList;
        
        returnList = nameList.get("sections");

        if (returnList == null) {
            try {
                returnList = service.callList("fetchSectionList", "");

                if (returnList != null)
                    nameList.put("sections", returnList);
            } catch (Exception e) {
                Window.alert("SectionCache getSectionList error: " + e.getMessage());
            }
        }

        return returnList;
    }

    protected SectionViewDO getSectionFromIdInt(Integer id) throws Exception {
        SectionViewDO data;
        
        data = idList.get(id);

        if (data == null) {
            try {
                data = (SectionViewDO)service.call("fetchSectionById", id);

                if (data != null)
                    idList.put(data.getId(), data);

            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("SectionCache.getSectionFromId: id \"" + id +
                                    "\" not found in system.  Please call the system administrator.");
            }
        }

        return data;
    }
}