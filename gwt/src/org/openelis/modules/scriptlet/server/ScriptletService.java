package org.openelis.modules.scriptlet.server;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.ScriptletRemote;

public class ScriptletService {
	
	public ArrayList<IdNameVO> findByName(String name) {
        return remote().findByName(name, 10);
	}
	
	private ScriptletRemote remote() {
		return (ScriptletRemote)EJBFactory.lookup("openelis/ScriptletBean/remote");
	}

}
