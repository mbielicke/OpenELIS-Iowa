package org.openelis.web.server;

import java.util.ArrayList;

import org.openelis.gwt.common.SystemUserVO;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.SystemUserPermissionProxyRemote;

public class SystemUserService {

    public ArrayList<SystemUserVO> fetchByLoginName(String search) throws Exception {
        return remote().fetchByLoginName(search + "%", 10);
    }

    private SystemUserPermissionProxyRemote remote() {
        return (SystemUserPermissionProxyRemote)EJBFactory.lookup("openelis/SystemUserPermissionProxyBean/remote");
    }
}
