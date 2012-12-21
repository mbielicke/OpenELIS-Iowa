package org.openelis.modules.systemvariable.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.gwt.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SystemVariableServiceIntAsync {

    void abortUpdate(Integer id, AsyncCallback<SystemVariableDO> callback);

    void add(SystemVariableDO data, AsyncCallback<SystemVariableDO> callback);

    void delete(SystemVariableDO data, AsyncCallback<Void> callback);

    void fetchById(Integer id, AsyncCallback<SystemVariableDO> callback);

    void fetchByName(String name, AsyncCallback<ArrayList<SystemVariableDO>> callback);

    void fetchForUpdate(Integer id, AsyncCallback<SystemVariableDO> callback);

    void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback);

    void update(SystemVariableDO data, AsyncCallback<SystemVariableDO> callback);

}
