/**
 * 
 */
package org.openelis.remote;

import org.openelis.domain.InventoryItemDO;

public interface InventoryItemCacheRemote {

    public InventoryItemDO getInventoryItemById(Integer id) throws Exception;
}
