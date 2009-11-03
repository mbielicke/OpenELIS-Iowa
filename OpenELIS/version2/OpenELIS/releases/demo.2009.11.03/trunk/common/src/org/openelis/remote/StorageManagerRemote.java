package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.manager.StorageManager;

@Remote
public interface StorageManagerRemote {
    public StorageManager fetch(Integer referenceTableId, Integer referenceId) throws Exception;
}
