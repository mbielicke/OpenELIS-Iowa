
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
package org.openelis.manager.proxy;

import java.util.ArrayList;

import javax.naming.InitialContext;

import org.openelis.domain.OrganizationContactDO;
import org.openelis.local.OrganizationLocal;
import org.openelis.manager.OrganizationContactsManager;

public class OrganizationContactsManagerProxy {
    
    public OrganizationContactsManager commitAdd(OrganizationContactsManager man) {
        return null;
    }

    public OrganizationContactsManager commitUpdate(OrganizationContactsManager man) {
        return null;
    }

    public OrganizationContactsManager fetch(Integer id) throws Exception {
        OrganizationLocal ol = getOrganizationLocal();
     //   man.setContacts((ArrayList)ol.getOrganizationContacts(man.getOrganizationId()));
        
        return null;
    }
    
    public OrganizationContactsManager fetchByOrgId(Integer orgId) throws Exception {
        OrganizationLocal ol = getOrganizationLocal();
        ArrayList<OrganizationContactDO> contacts = (ArrayList<OrganizationContactDO>)ol.getOrganizationContacts(orgId);
        
        OrganizationContactsManager cm = OrganizationContactsManager.getInstance();
        cm.setContacts(contacts);
        cm.setOrganizationId(orgId);
        
        return cm;
    }
    
    private OrganizationLocal getOrganizationLocal(){
        try{
            InitialContext ctx = new InitialContext();
            return (OrganizationLocal)ctx.lookup("openelis/OrganizationBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }
    }
}
