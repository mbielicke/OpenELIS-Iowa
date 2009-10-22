package org.openelis.server;

import java.util.ArrayList;

import org.openelis.domain.SecuritySystemUserDO;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.SystemUserUtilRemote;

public class SystemUserService {
	
	public ArrayList<SecuritySystemUserDO> findSystemUserByLogin(String login) {
		return remote().findSystemUserByLogin(login,10);
		
	}
	
	private SystemUserUtilRemote remote() {
		return (SystemUserUtilRemote)EJBFactory.lookup("openelis/SystemUserUtilBean/remote");
	}
}
