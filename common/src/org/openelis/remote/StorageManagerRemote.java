package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.gwt.common.data.Query;
import org.openelis.manager.StorageManager;

@Remote
public interface StorageManagerRemote {
    public StorageManager fetchById(Integer referenceTableId, Integer referenceId) throws Exception;
    
    public StorageManager fetchCurrentByLocationId(Integer id) throws Exception;

    public StorageManager fetchHistoryByLocationId(Integer id, int first, int max) throws Exception;
    
    public StorageManager update(StorageManager man) throws Exception;
}
