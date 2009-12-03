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

import org.openelis.domain.SectionDO;
import org.openelis.gwt.services.ScreenService;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.user.client.Window;

public class SectionCache {
    protected static final String SECTION_CACHE_SERVICE_URL = "org.openelis.server.cache.SectionCacheService";
    protected ScreenService service;
    HashMap<String, ArrayList<SectionDO>> sectionList;
    private static SectionCache instance;
    
    public SectionCache() {
        service = new ScreenService("OpenELISServlet?service="+SECTION_CACHE_SERVICE_URL);
        
        sectionList = (HashMap<String, ArrayList<SectionDO>>)OpenELIS.getCacheList().get("SectionsCache");
        
        if(sectionList == null){
            sectionList = new HashMap<String, ArrayList<SectionDO>>();
            OpenELIS.getCacheList().put("SectionsCache", sectionList);
        }
    }
    
    public static ArrayList<SectionDO> getSectionList() {
        if(instance == null)
            instance = new SectionCache();
        
        return instance.getSectionListInt();
    }
    
    protected ArrayList<SectionDO> getSectionListInt() {
        ArrayList<SectionDO> returnList = sectionList.get("sections");
        
        if(returnList == null){
            try{
                SectionCacheRPC rpc = service.call("getSectionList", "");
                
                if(rpc.list != null){
                    sectionList.put("sections", rpc.list);
                    returnList = rpc.list;
                }
            }catch(Exception e){
                Window.alert("SectionCache getSectionList error: "+e.getMessage());   
            }
        }
        
        return returnList;   
    }
}
