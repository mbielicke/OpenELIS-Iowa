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

import org.openelis.common.AutocompleteRPC;
import org.openelis.domain.IdNameDO;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.manager.OrganizationContactManager;
import org.openelis.manager.OrganizationManager;
import org.openelis.modules.organization.client.OrgQuery;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.OrganizationManagerRemote;
import org.openelis.remote.OrganizationRemote;
import org.openelis.server.constants.Constants;
import org.openelis.util.SessionManager;
import org.openelis.util.UTFResource;

public class OrganizationService {

	private static final int rowPP = 18;
    
	private UTFResource openElisConstants= UTFResource.getBundle((String)SessionManager.getSession().getAttribute("locale"));
	
	public OrgQuery query(OrgQuery query) throws Exception {
        try {
            query.setResults(remote().query(query.getFields(), query.getPage() * rowPP, rowPP));
        } catch (LastPageException e) {
            throw new LastPageException(openElisConstants.getString("lastPageException"));
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return query;
    }

	public OrganizationManager fetch(Integer orgId) throws Exception {
        return remoteManager().fetch(orgId);
    }
    
    public OrganizationContactManager fetchContactById(Integer id) throws Exception{
        return null;
    }
    
    public OrganizationContactManager fetchContactByOrgId(Integer orgId) throws Exception {
        return remoteManager().fetchContactByOrgId(orgId);
    }
    
    public OrganizationManager add(OrganizationManager man) throws Exception {
        return remoteManager().add(man);
    }
    
    public OrganizationManager update(OrganizationManager man) throws Exception {
        return remoteManager().update(man);
        
    }
    
    public OrganizationManager fetchWithContacts(Integer orgId) throws Exception {
        return remoteManager().fetchWithContacts(orgId);
    }
    
    public OrganizationManager fetchWithNotes(Integer orgId) throws Exception {
        return remoteManager().fetchWithNotes(orgId);
    }
    
    public OrganizationManager fetchWithIdentifiers(Integer orgId) throws Exception {
        return remoteManager().fetchWithIdentifiers(orgId); 
    }
    
    public OrganizationManager fetchForUpdate(Integer orgId) throws Exception {
        return remoteManager().fetchForUpdate(orgId);
    }
    
    public OrganizationManager abort(Integer orgId) throws Exception {
        return remoteManager().abortUpdate(orgId);
    }
    
    public AutocompleteRPC getMatches(AutocompleteRPC rpc) {
        
    	try{
    		int id = Integer.parseInt(rpc.match); //this will throw an exception if it isnt an id
    		//lookup by id...should only bring back 1 result
    		rpc.model = (ArrayList)remote().autoCompleteLookupById(id);
    	}catch(NumberFormatException e){
    		rpc.model = (ArrayList)remote().autoCompleteLookupByName(rpc.match+"%", 10);
    	}
        
    	return rpc;		
    }
    
    private OrganizationRemote remote() {
        return (OrganizationRemote)EJBFactory.lookup("openelis/OrganizationBean/remote");
    }
    
    private OrganizationManagerRemote remoteManager() {
        return (OrganizationManagerRemote)EJBFactory.lookup("openelis/OrganizationManagerBean/remote");
    }
}
