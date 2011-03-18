package org.openelis.remote;

import java.util.ArrayList;

import javax.ejb.Remote;

import org.openelis.gwt.common.SystemUserPermission;
import org.openelis.gwt.common.SystemUserVO;

@Remote
public interface UserCacheRemote {

    public SystemUserPermission login();

    public void logout();

    public ArrayList<SystemUserVO> getSystemUsers(String name, int max) throws Exception;

    public SystemUserPermission getPermission() throws Exception;
}