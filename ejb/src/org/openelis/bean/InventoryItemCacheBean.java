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

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.InventoryItemDO;
import org.openelis.utils.EJBFactory;

/**
 * This class provides application level cache handling for inventory item
 */

@SecurityDomain("openelis")
@Singleton
@Lock(LockType.READ)

public class InventoryItemCacheBean {

    private Cache              cache;

    @PostConstruct
    public void init() {
        CacheManager cm;

        cm = CacheManager.getInstance();
        cache = cm.getCache("inventory");
    }

    /*
     * InventoryItem Cache
     */
    public InventoryItemDO getById(Integer id) throws Exception {
        Element e;
        InventoryItemDO data;
        
        e = cache.get(id);
        if (e != null)
            return (InventoryItemDO) e.getValue();

        data = EJBFactory.getInventoryItem().fetchById(id);
        cache.put(new Element(id, data));
        
        return data;
     }  

    public void evict(Integer id) {
        cache.remove(id);
    }
}
