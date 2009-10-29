package org.openelis.modules.scriptlet.server;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.ScriptletRemote;

public class ScriptletService {
	
	public ArrayList<IdNameVO> fetchByName(String search)throws Exception {
        try {
            return remote().findByName(search+"%", 10);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
	}
	
	private ScriptletRemote remote() {
		return (ScriptletRemote)EJBFactory.lookup("openelis/ScriptletBean/remote");
	}

}
