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
import org.openelis.domain.CategoryCacheVO;
import org.openelis.domain.DictionaryDO;
import org.openelis.local.CategoryCacheLocal;
import org.openelis.remote.CategoryCacheRemote;
import org.openelis.utils.EJBFactory;

/**
 * This class provides application level cache handling for category
 */

@SecurityDomain("openelis")
@Service(objectName = "jboss:custom=CategoryCacheBean")

public class CategoryCacheBean implements CategoryCacheLocal, CategoryCacheRemote {

    private Cache           cache, dictCache;

    public CategoryCacheBean() {
        CacheManager cm;

        cm = CacheManager.getInstance();
        cache = cm.getCache("category");
        dictCache = cm.getCache("dictionary");
    }

    /*
     * Category Cache
     */
    public CategoryCacheVO getBySystemName(String systemName) throws Exception {
        Element e;
        ArrayList<DictionaryDO> list;
        CategoryCacheVO cat;

        e = cache.get(systemName);
        if (e != null)
            return (CategoryCacheVO)e.getValue();
        list = EJBFactory.getDictionary().fetchByCategorySystemName(systemName);
        cat = new CategoryCacheVO();
        if (list != null) {            
            cat.setSystemName(systemName);
            cat.setDictionaryList(list);
            cache.put(new Element(systemName, cat));
            //
            // add it to dictionary cache
            //
            for (DictionaryDO data : list) {
                dictCache.put(new Element(data.getId(), data));
                if (data.getSystemName() != null)
                    dictCache.put(new Element(data.getSystemName(), data));
            }
        }

        return cat;
    }

    public ArrayList<CategoryCacheVO> getBySystemNames(String systemNames[]) throws Exception {
        ArrayList<CategoryCacheVO> list;

        list = new ArrayList<CategoryCacheVO>();

        for (String name : systemNames) 
            list.add(getBySystemName(name));        

        return list;
    }

    public void evict(String systemName) throws Exception {
        cache.remove(systemName);
    }
}
