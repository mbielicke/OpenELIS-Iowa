/**
 * 
 */
package org.openelis.bean;

import javax.ejb.EJB;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.Service;
import org.openelis.domain.InventoryItemDO;
import org.openelis.local.InventoryItemCacheLocal;
import org.openelis.local.InventoryItemLocal;
import org.openelis.remote.InventoryItemCacheRemote;

/**
 * This class provides application level cache handling for inventory item
 */

@SecurityDomain("openelis")
@Service(objectName = "jboss:custom=InventoryCacheBean")
public class InventoryItemCacheBean implements InventoryItemCacheLocal, InventoryItemCacheRemote {

    @EJB
    private InventoryItemLocal item;

    private Cache              cache;

    public InventoryItemCacheBean() {
        CacheManager cm;

        cm = CacheManager.getInstance();
        cache = cm.getCache("inventory");
    }

    /*
     * InventoryItem Cache
     */
    public InventoryItemDO getInventoryItemById(Integer id) throws Exception {
        Element e;
        InventoryItemDO data;
        
        e = cache.get(id);
        if (e != null)
            return (InventoryItemDO) e.getValue();

        data = item.fetchById(id);
        cache.put(new Element(id, data));
        
        return data;
     }  

    public void evictInventoryItemCache(Integer id) {
        cache.remove(id);
    }
}
