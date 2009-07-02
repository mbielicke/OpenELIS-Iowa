package org.openelis.modules.organization.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.OrganizationContactDO;
import org.openelis.gwt.common.RPC;

public class ContactsRPC implements RPC {

	private static final long serialVersionUID = 1L;
	
	public Integer orgId;
	public ArrayList<OrganizationContactDO> orgContacts;
}
