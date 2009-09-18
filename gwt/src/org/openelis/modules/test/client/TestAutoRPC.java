package org.openelis.modules.test.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.TestMethodViewDO;
import org.openelis.gwt.common.RPC;

public class TestAutoRPC implements RPC {
	
    private static final long serialVersionUID = 1L;

    public String match;
	
	public ArrayList<IdNameDO> idNameList;
	public ArrayList<TestMethodViewDO> testMethodList;
	
}
