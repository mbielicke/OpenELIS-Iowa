package org.openelis.modules.auxData.client;

import java.util.ArrayList;

import org.openelis.domain.AuxDataDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.IdVO;
import org.openelis.manager.AuxDataManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AuxDataServiceIntAsync {

    void fetchById(AuxDataDO auxData, AsyncCallback<AuxDataManager> callback);

    void fetchByRefId(AuxDataDO auxData, AsyncCallback<ArrayList<AuxDataViewDO>> callback);

    void getAuxGroupIdFromSystemVariable(String sysVariableKey, AsyncCallback<IdVO> callback);

}
