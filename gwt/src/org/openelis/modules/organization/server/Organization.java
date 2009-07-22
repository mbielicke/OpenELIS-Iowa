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
import org.openelis.domain.OrganizationAutoDO;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.services.rewrite.AutoCompleteServiceInt;
import org.openelis.manager.OrganizationContactsManager;
import org.openelis.manager.OrganizationsManager;
import org.openelis.metamap.OrganizationMetaMap;
import org.openelis.modules.organization.client.OrgQuery;
import org.openelis.modules.organization.client.ParentOrgRPC;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.OrganizationManagerRemote;
import org.openelis.remote.OrganizationRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class Organization implements AutoCompleteServiceInt<ParentOrgRPC> {

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

    public OrganizationsManager fetch(Integer orgId) throws Exception {
        OrganizationManagerRemote remote = (OrganizationManagerRemote)EJBFactory.lookup("openelis/OrganizationManagerBean/remote");
        
        return remote.fetch(orgId);
    }
    
    public OrganizationContactsManager fetchContactById(Integer id) throws Exception{
        OrganizationManagerRemote remote = (OrganizationManagerRemote)EJBFactory.lookup("openelis/OrganizationManagerBean/remote");
        return null;
    }
    
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
        OrganizationManagerRemote remote = (OrganizationManagerRemote)EJBFactory.lookup("openelis/OrganizationManagerBean/remote");
        OrganizationsManager man = remote.fetchWithContacts(orgId);
        
        return man;
    }
    
    public OrganizationsManager fetchWithNotes(Integer orgId) throws Exception {
        OrganizationManagerRemote remote = (OrganizationManagerRemote)EJBFactory.lookup("openelis/OrganizationManagerBean/remote");
        OrganizationsManager man = remote.fetchWithNotes(orgId);
        
        return man;
    }
    
    public OrganizationsManager fetchWithIdentifiers(Integer orgId) throws Exception {
        OrganizationManagerRemote remote = (OrganizationManagerRemote)EJBFactory.lookup("openelis/OrganizationManagerBean/remote");
        OrganizationsManager man = remote.fetchWithIdentifiers(orgId);
        
        return man;
    }
    
    public OrganizationsManager fetchForUpdate(Integer orgId) throws Exception {
        //remote interface to call the organization bean
        OrganizationManagerRemote remote = (OrganizationManagerRemote)EJBFactory.lookup("openelis/OrganizationManagerBean/remote");
        
        OrganizationsManager man = remote.fetch(orgId);
        
        return man;
    }
    
    public String getScreen() throws RPCException {
        return ServiceUtils.getXML(Constants.APP_ROOT+"/Forms/organizationRewrite.xsl");      
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
}
