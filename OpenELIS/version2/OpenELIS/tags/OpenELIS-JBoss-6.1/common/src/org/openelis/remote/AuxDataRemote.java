package org.openelis.remote;

import java.util.ArrayList;

import javax.ejb.Remote;

import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.IdVO;

@Remote
public interface AuxDataRemote {
    public ArrayList<AuxDataViewDO> fetchById(Integer referenceId, Integer referenceTableId) throws Exception;
    
    public IdVO fetchGroupIdBySystemVariable(String sysVariableKey) throws Exception;
}
