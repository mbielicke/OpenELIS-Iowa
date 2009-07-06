package org.openelis.modules.organization.client;

import java.util.ArrayList;

import org.openelis.domain.OrganizationAutoDO;
import org.openelis.gwt.common.RPC;

public class ParentOrgRPC implements RPC {
	
	public String match;
	
	public ArrayList<OrganizationAutoDO> model;
	
}
