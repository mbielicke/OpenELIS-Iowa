package org.openelis.cache;

import java.util.ArrayList;

import org.openelis.ui.common.SystemUserPermission;
import org.openelis.ui.common.SystemUserVO;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserCacheServiceIntAsync {

    void getEmployees(String name, AsyncCallback<ArrayList<SystemUserVO>> callback);

    void getPermission(AsyncCallback<SystemUserPermission> callback);

    void getSystemUser(Integer id, AsyncCallback<SystemUserVO> callback);

    void validateSystemUsers(ArrayList<String> names,
                             AsyncCallback<ArrayList<SystemUserVO>> callback);
}
