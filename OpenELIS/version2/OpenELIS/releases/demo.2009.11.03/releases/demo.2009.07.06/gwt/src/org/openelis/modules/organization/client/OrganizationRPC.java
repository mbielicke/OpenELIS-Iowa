package org.openelis.modules.organization.client;

import java.util.ArrayList;

import org.openelis.domain.OrganizationAddressDO;
import org.openelis.gwt.common.RPC;

public class OrganizationRPC implements RPC {

	private static final long serialVersionUID = 1L;
	
	// Main form DO for screen;
	public OrganizationAddressDO orgAddressDO = new OrganizationAddressDO();

	// RPC for loading of ContactsTab
	public ContactsRPC orgContacts;
	
	// RPC for loading of NotesTab;
	public NotesRPC notes;
    
	// AutoComplete RPC for Parent Organization when loaded with fetch
    public ParentOrgRPC parentOrgRPC;

    //Ordinal order of enums must match order in tab panel
    public enum Tabs {CONTACTS,IDENTIFIERS,NOTES};
    
    //The Tab that is selected on screen;
    public Tabs tab = Tabs.CONTACTS;
    
    //List of tabs that should be loaded on fetch
    public ArrayList<Tabs> loadTabs;
	

}
