package org.openelis.modules.method.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.MethodDO;
import org.openelis.gwt.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MethodServiceIntAsync {

    void abortUpdate(Integer id, AsyncCallback<MethodDO> callback);

    void add(MethodDO data, AsyncCallback<MethodDO> callback);

    void fetchById(Integer id, AsyncCallback<MethodDO> callback);

    void fetchByName(String search, AsyncCallback<ArrayList<MethodDO>> callback);

    void fetchForUpdate(Integer id, AsyncCallback<MethodDO> callback);

    void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback);

    void update(MethodDO data, AsyncCallback<MethodDO> callback);

}
