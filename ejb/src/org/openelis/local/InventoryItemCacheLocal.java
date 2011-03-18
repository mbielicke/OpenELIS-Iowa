/**
 * 
 */
package org.openelis.local;

import org.openelis.domain.InventoryItemDO;

public interface InventoryItemCacheLocal {

    public InventoryItemDO getInventoryItemById(Integer id) throws Exception;

    public void evictInventoryItemCache(Integer id);
}
