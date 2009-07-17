/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.modules.organization.server;

import java.util.ArrayList;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.NoteDO;
import org.openelis.domain.OrganizationAddressDO;
import org.openelis.domain.OrganizationAutoDO;
import org.openelis.domain.OrganizationContactDO;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.ScreenServiceInt;
import org.openelis.gwt.services.rewrite.AutoCompleteServiceInt;
import org.openelis.manager.OrganizationContactsManager;
import org.openelis.manager.OrganizationsManager;
import org.openelis.metamap.OrganizationMetaMap;
import org.openelis.modules.organization.client.ContactsRPC;
import org.openelis.modules.organization.client.NotesRPC;
import org.openelis.modules.organization.client.OrgQuery;
import org.openelis.modules.organization.client.OrganizationRPC;
import org.openelis.modules.organization.client.ParentOrgRPC;
import org.openelis.modules.organization.client.Organization.Tabs;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.OrganizationManagerRemote;
import org.openelis.remote.OrganizationRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class Organization implements ScreenServiceInt, AutoCompleteServiceInt<ParentOrgRPC> {

	private static final int leftTableRowsPerPage = 18;
    
    private static final OrganizationMetaMap OrgMeta = new OrganizationMetaMap();
    
	private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
	
	public OrgQuery query(OrgQuery query) throws RPCException {

	    OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");

	    try{	
	        query.results = new ArrayList<IdNameDO>();
	        ArrayList<IdNameDO> results = (ArrayList<IdNameDO>)remote.newQuery(query.fields,query.page*leftTableRowsPerPage,leftTableRowsPerPage);
	        for(IdNameDO result : results) {
	            query.results.add(result);
	        }
	    }catch(LastPageException e) {
	        throw new LastPageException(openElisConstants.getString("lastPageException"));
	    }catch(Exception e){
	        throw new RPCException(e.getMessage());
	    }
	    return query;
    }

    public OrganizationRPC add(OrganizationRPC rpc) throws RPCException {
        //		remote interface to call the organization bean
        OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
        Integer orgId;
        NoteDO note = null;
        if(rpc.notes != null) {
            if(rpc.notes.notes != null)
                note = rpc.notes.notes.get(0);
        }
        ArrayList<OrganizationContactDO> contacts = null;
        if(rpc.orgContacts != null){
            contacts = rpc.orgContacts.orgContacts;
            contacts.get(0).setName(null);
        }
        try{
            orgId = (Integer)remote.updateOrganization(rpc.orgAddressDO, note, contacts);
        }catch(ValidationErrorsList e) {
            throw e;
        }catch(Exception e){
            throw new RPCException(e.getMessage());
        }
        rpc.orgAddressDO.setOrganizationId(orgId);
        return rpc;
    }

    public OrganizationRPC update(OrganizationRPC rpc) throws RPCException {
        //		remote interface to call the organization bean
        OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
        NoteDO note = null;
        if(rpc.notes != null) {
            if(rpc.notes.notes != null)
                note = rpc.notes.notes.get(0);
        }
        ArrayList<OrganizationContactDO> contacts = null;
        if(rpc.orgContacts != null){
            contacts = rpc.orgContacts.orgContacts;
            contacts.get(0).setName(null);
        }
        //		send the changes to the database
        try{
            remote.updateOrganization(rpc.orgAddressDO, note, contacts);
        }catch(ValidationErrorsList e) {
            throw e;
        }catch(Exception e){
            throw new RPCException(e.getMessage());
        }
        return rpc;
    }

    public OrganizationRPC abort(OrganizationRPC rpc) throws RPCException {
        //		remote interface to call the organization bean
        return null;
        /*OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");

        rpc.orgAddressDO = remote.getOrganizationAddressAndUnlock(rpc.orgAddressDO.getOrganizationId());
        
        setRPC(rpc);
        
        return rpc;
*/
    }

    public OrganizationsManager fetch(Integer orgId) throws Exception {
        //remote interface to call the organization bean
        OrganizationManagerRemote remote = (OrganizationManagerRemote)EJBFactory.lookup("openelis/OrganizationManagerBean/remote");
        
        return remote.fetch(orgId);
    }
    
    /*public OrganizationContactsManager fetchContactById(Integer id) throws Exception{
        OrganizationManagerRemote remote = (OrganizationManagerRemote)EJBFactory.lookup("openelis/OrganizationManagerBean/remote");
        
    }*/
    
    public OrganizationContactsManager fetchContactByOrgId(Integer orgId) throws Exception {
        OrganizationManagerRemote remote = (OrganizationManagerRemote)EJBFactory.lookup("openelis/OrganizationManagerBean/remote");
        
        return remote.fetchContactByOrgId(orgId);
    }
    
    public OrganizationsManager add(OrganizationsManager man) throws Exception {
        OrganizationManagerRemote remote = (OrganizationManagerRemote)EJBFactory.lookup("openelis/OrganizationManagerBean/remote");

        return remote.add(man);
    }
    
    public OrganizationsManager update(OrganizationsManager man) throws Exception {
        OrganizationManagerRemote remote = (OrganizationManagerRemote)EJBFactory.lookup("openelis/OrganizationManagerBean/remote");
        
        return remote.update(man);
        
    }
    
    public OrganizationsManager fetchWithContacts(Integer orgId) throws Exception {
        //remote interface to call the organization bean
        OrganizationManagerRemote remote = (OrganizationManagerRemote)EJBFactory.lookup("openelis/OrganizationManagerBean/remote");
        
        OrganizationsManager man = remote.fetchWithContacts(orgId);
        
        return man;
    }
    
    public OrganizationsManager fetchWithNotes(Integer orgId) throws Exception {
        //remote interface to call the organization bean
        OrganizationManagerRemote remote = (OrganizationManagerRemote)EJBFactory.lookup("openelis/OrganizationManagerBean/remote");
        
        OrganizationsManager man = remote.fetchWithNotes(orgId);
        
        return man;
    }
    
    public OrganizationsManager fetchForUpdate(Integer orgId) throws Exception {
        //remote interface to call the organization bean
        OrganizationManagerRemote remote = (OrganizationManagerRemote)EJBFactory.lookup("openelis/OrganizationManagerBean/remote");
        
        OrganizationsManager man = remote.fetch(orgId);
        
        return man;
    }
    
    public OrganizationRPC fetch(OrganizationRPC rpc) throws RPCException {
        return null;
/*		//remote interface to call the organization bean
        OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
		
        rpc.orgAddressDO = remote.getOrganizationAddress(rpc.orgAddressDO.getOrganizationId());
        
        setRPC(rpc);
        
        return rpc;
        */
    }
    
    public OrganizationRPC fetchForUpdate(OrganizationRPC rpc) throws RPCException {

        //      remote interface to call the organization bean 
        OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");

        OrganizationAddressDO organizationDO = new OrganizationAddressDO();
        try{
            rpc.orgAddressDO = remote.getOrganizationAddressAndLock(rpc.orgAddressDO.getOrganizationId());
        }catch(Exception e){
            throw new RPCException(e.getMessage());
        }

        setRPC(rpc);

        return rpc;

    }
    
    private void setRPC(OrganizationRPC rpc) {
     
        if(rpc.orgAddressDO.getParentOrganizationId() != null) {
        	rpc.parentOrgRPC = new ParentOrgRPC();
        	rpc.parentOrgRPC.match = rpc.orgAddressDO.getOrganizationId().toString();
        	rpc.parentOrgRPC = getMatches(rpc.parentOrgRPC);
        }
 
      /*  if(rpc.tab == Tabs.CONTACTS){
            rpc.orgContacts = new ContactsRPC();
            getContacts(rpc.orgAddressDO.getOrganizationId(),rpc.orgContacts);
        }else
            rpc.orgContacts =  null;
       
        if(rpc.tab == Tabs.NOTES){
        	rpc.notes = new NotesRPC();
            getNotes(rpc.orgAddressDO.getOrganizationId(), rpc.notes);
        }else
            rpc.notes = null;
            */
     
	}
    
    public ContactsRPC loadContacts(ContactsRPC rpc) throws RPCException {
        getContacts(rpc.orgId,rpc);
        return rpc;
    }

    
    public NotesRPC loadNotes(NotesRPC rpc) throws RPCException {
        getNotes(rpc.key,rpc);
        return rpc;
    }

    public String getScreen() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/organizationRewrite.xsl");      
    }

    public void getNotes(Integer key, NotesRPC rpc){
        //remote interface to call the organization bean
        OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
    
        //gets the whole notes list now
        rpc.notes = (ArrayList<NoteDO>)remote.getOrganizationNotes(key);   
    }
    
    public void getContacts(Integer orgId, ContactsRPC rpc){
        /*
        OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
        rpc.orgContacts = (ArrayList<OrganizationContactDO>)remote.getOrganizationContacts(orgId);
*/
    }

    //autocomplete textbox method
    //match is what they typed
    public ParentOrgRPC getMatches(ParentOrgRPC rpc) {
    	OrganizationRemote remote = (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
    
    	try{
    		int id = Integer.parseInt(rpc.match); //this will throw an exception if it isnt an id
    		//lookup by id...should only bring back 1 result
    		rpc.model = (ArrayList<OrganizationAutoDO>)remote.autoCompleteLookupById(id);
    		
    	}catch(NumberFormatException e){
    		//it isnt an id
    		//lookup by name
    		rpc.model = (ArrayList<OrganizationAutoDO>)remote.autoCompleteLookupByName(rpc.match+"%", 10);
    	}
        
    	return rpc;		
    }
    
	public <T extends RPC> T callScreen(String method, T rpc) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

    public <T extends RPC> T call(String method, Integer param) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public <T extends RPC> T call(String method, RPC param) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public <T extends RPC> T call(String method, String param) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

}
