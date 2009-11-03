package org.openelis.server;

import java.util.ArrayList;

import org.openelis.domain.SecuritySystemUserDO;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.SystemUserUtilRemote;

public class SystemUserService {
    public ArrayList<SecuritySystemUserDO> fetchByLogin(String search) {
		return remote().fetchByLogin(search + "%",10);
	}
	
	private SystemUserUtilRemote remote() {
		return (SystemUserUtilRemote)EJBFactory.lookup("openelis/SystemUserUtilBean/remote");
	}
}
