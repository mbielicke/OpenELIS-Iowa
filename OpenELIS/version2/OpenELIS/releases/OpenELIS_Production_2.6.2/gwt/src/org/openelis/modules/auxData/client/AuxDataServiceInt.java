package org.openelis.modules.auxData.client;

import java.util.ArrayList;

import org.openelis.domain.AuxDataDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.IdVO;
import org.openelis.manager.AuxDataManager;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("auxData")
public interface AuxDataServiceInt extends XsrfProtectedService {

    AuxDataManager fetchById(AuxDataDO auxData) throws Exception;

    ArrayList<AuxDataViewDO> fetchByRefId(AuxDataDO auxData) throws Exception;

    IdVO getAuxGroupIdFromSystemVariable(String sysVariableKey) throws Exception;

}