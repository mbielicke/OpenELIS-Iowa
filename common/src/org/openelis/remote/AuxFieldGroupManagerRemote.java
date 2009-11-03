package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.manager.AuxFieldGroupManager;
import org.openelis.manager.AuxFieldManager;
import org.openelis.manager.AuxFieldValueManager;

@Remote
public interface AuxFieldGroupManagerRemote {
    public AuxFieldGroupManager fetchById(Integer id) throws Exception;
    
    public AuxFieldGroupManager fetchWithFields(Integer id) throws Exception;
    
    public AuxFieldGroupManager add(AuxFieldGroupManager man) throws Exception;

    public AuxFieldGroupManager update(AuxFieldGroupManager man) throws Exception;

    public AuxFieldGroupManager fetchForUpdate(Integer id) throws Exception;
    
    public AuxFieldGroupManager abortUpdate(Integer id) throws Exception;

    public AuxFieldManager fetchAuxFieldById(Integer id) throws Exception;
    
    public AuxFieldManager fetchByGroupIdWithValues(Integer groupId) throws Exception;
    
    public AuxFieldManager fetchByGroupId(Integer id) throws Exception;
    
    public AuxFieldValueManager fetchFieldValueByFieldId(Integer id) throws Exception;
}
