package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.manager.AuxDataManager;

@Remote
public interface AuxDataManagerRemote {
    public AuxDataManager fetchById(Integer referenceId, Integer referenceTableId) throws Exception;
    
    public AuxDataManager fetchByIdForUpdate(Integer referenceId, Integer referenceTableId) throws Exception;
}
