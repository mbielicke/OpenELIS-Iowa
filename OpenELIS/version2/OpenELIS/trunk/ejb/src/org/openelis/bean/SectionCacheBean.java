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
package org.openelis.bean;

import java.util.ArrayList;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.Service;
import org.openelis.domain.SectionViewDO;
import org.openelis.local.SectionCacheLocal;
import org.openelis.remote.SectionCacheRemote;
import org.openelis.utils.EJBFactory;

/**
 * This class provides application level cache handling for section
 */

@SecurityDomain("openelis")
@Service(objectName = "jboss:custom=SectionCacheBean")
public class SectionCacheBean implements SectionCacheLocal, SectionCacheRemote {

    private Cache        cache;

    public SectionCacheBean() {
        CacheManager cm;

        cm = CacheManager.getInstance();
        cache = cm.getCache("section");
    }

    /*
     * Section Cache
     */
    public SectionViewDO getById(Integer id) throws Exception {
        Element e;
        
        e = cache.get(id);
        if (e != null)
            return (SectionViewDO) e.getValue();

        //
        // since section is a small list, we are going to load everything
        // rather than one lookup at a time.
        //
        getList();
        
        e = cache.get(id);
        if (e != null)
            return (SectionViewDO) e.getValue();

        return null;
    }  

    public ArrayList<SectionViewDO> getList() throws Exception {
        ArrayList<SectionViewDO> list;
        Element e;
        
        if (cache.getSize() == 0) {
            list = EJBFactory.getSection().fetchList();
            for (SectionViewDO data : list)
                cache.put(new Element(data.getId(), data));
            e = new Element("orderedList", list);
            cache.put(e);        
        } else {
            /*
             * It can happen that "cache" contains expired elements and one of
             * those elements could be the one with the key "orderedList".
             * If that's the case then getSize() will return a non-zero number
             * because it takes into consiration both expired and alive elements.
             * Thus we have to check to see if "orderedList" is alive before trying
             * to call getValue() on it and if it's not (e == null), put a new 
             * element with that key and the latest data from the database as the
             * value in "cache".
             */
            e = cache.get("orderedList");
            if (e == null) {                
                list = EJBFactory.getSection().fetchList(); 
                e = new Element("orderedList", list);
                cache.put(e);
            }
        } 
        return (ArrayList<SectionViewDO>)e.getValue();
    }
    
    public void evict() throws Exception {
        cache.removeAll();
    }
}
