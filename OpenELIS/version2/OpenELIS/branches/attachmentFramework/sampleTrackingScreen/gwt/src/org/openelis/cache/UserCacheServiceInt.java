package org.openelis.cache;

import java.util.ArrayList;

import org.openelis.ui.common.SystemUserPermission;
import org.openelis.ui.common.SystemUserVO;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("userCache")
public interface UserCacheServiceInt extends XsrfProtectedService {

    SystemUserVO getSystemUser(Integer id) throws Exception;

    ArrayList<SystemUserVO> getSystemUsers(String name) throws Exception;

    ArrayList<SystemUserVO> getEmployees(String name) throws Exception;

    SystemUserPermission getPermission() throws Exception;

}