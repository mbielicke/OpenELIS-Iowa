package org.openelis.cache;

import java.util.ArrayList;

import org.openelis.gwt.common.SystemUserPermission;
import org.openelis.gwt.common.SystemUserVO;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("userCache")
public interface UserCacheServiceInt extends RemoteService {

    SystemUserVO getSystemUser(Integer id) throws Exception;

    ArrayList<SystemUserVO> getSystemUsers(String name) throws Exception;

    ArrayList<SystemUserVO> getEmployees(String name) throws Exception;

    SystemUserPermission getPermission() throws Exception;

}