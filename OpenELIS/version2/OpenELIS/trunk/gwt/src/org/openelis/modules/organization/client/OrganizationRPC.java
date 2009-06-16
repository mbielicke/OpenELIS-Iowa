package org.openelis.modules.organization.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.NoteDO;
import org.openelis.domain.OrganizationAddressDO;
import org.openelis.domain.OrganizationContactDO;
import org.openelis.gwt.common.RPC;

public class OrganizationRPC implements RPC {

	private static final long serialVersionUID = 1L;

	public String xml;
  
	public OrganizationAddressDO orgAddressDO = new OrganizationAddressDO();
	public ContactsRPC orgContacts;
	public NotesRPC notes;
    
    public ArrayList<IdNameDO> countries;
    public ArrayList<IdNameDO> states;

    public String orgTabPanel = "contactsTab";
	

}
