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

import org.openelis.domain.SectionViewDO;
import org.openelis.gwt.services.ScreenService;

import com.google.gwt.user.client.Window;

/**
 * Class provides cache service handling for front end GWT classes. Cache
 * objects in GWT instance are cached for the duration of the session and are
 * not updated -- if objects in the back-end are updated, the user will need to
 * restart the session to get updated objects.
 */

public class SectionCache {
    protected static HashMap<Integer, SectionViewDO>  cache;
    protected static ArrayList<SectionViewDO>         list;    

    static {
        cache = new HashMap<Integer, SectionViewDO>();
    }
    
    public static SectionViewDO getById(Integer id) throws Exception {
        SectionViewDO data;
        
        data = cache.get(id);
        if (data == null) {
            try {
                refreshList();
                data = cache.get(id);

            } catch (Exception e) {
                throw new Exception("SectionCache.getById: id \"" + id +
                                    "\" not found in system.  Please call the system administrator.");
            }
        }

        return data;
    }
    
    public static ArrayList<SectionViewDO> getList() {        
        try {
            if (cache.size() == 0) 
                list = refreshList();             
        } catch (Exception e) {
            Window.alert("SectionCache getList error: " + e.getMessage());
        }

        return list;
    } 
    
    protected static ArrayList<SectionViewDO> refreshList() throws Exception {
        ArrayList<SectionViewDO> list;
        
        list = SectionCacheService.get().getList("");
                    
        for (SectionViewDO data: list) 
            cache.put(data.getId(), data);                            
         
        return list; 
    }        

}
