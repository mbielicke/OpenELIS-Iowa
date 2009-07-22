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
package org.openelis.manager;

import javax.naming.InitialContext;
import javax.persistence.EntityNotFoundException;

import org.openelis.domain.OrganizationAddressDO;
import org.openelis.local.OrganizationLocal;
import org.openelis.utils.ReferenceTableCache;

public class OrganizationsManagerProxy {
    
    public OrganizationsManager add(OrganizationsManager man) throws Exception {
        Integer orgId, orgRefId;
        OrganizationLocal ol = getOrganizationLocal();
        ol.add(man.getOrganizationAddress());
        
        orgId = man.getOrganizationAddress().getOrganizationId();
        orgRefId = man.getOrganizationReferenceTable();
        
        man.getContacts().setOrganizationId(orgId);
        man.getContacts().add();
        
        man.getNotes().setReferenceId(orgId);
        man.getNotes().setReferenceTableId(orgRefId);
        man.getNotes().add();
        
        return man;
    }

    public OrganizationsManager update(OrganizationsManager man) throws Exception {
        Integer orgId, orgRefId;
        OrganizationLocal ol = getOrganizationLocal();
        ol.update(man.getOrganizationAddress());
        
        orgId = man.getOrganizationAddress().getOrganizationId();
        orgRefId = man.getOrganizationReferenceTable();
        
        man.getContacts().setOrganizationId(orgId);
        man.getContacts().update();
        
        man.getNotes().setReferenceId(orgId);
        man.getNotes().setReferenceTableId(orgRefId);
        man.getNotes().update();
        
        return man;
    }

    public OrganizationsManager fetch(Integer orgId) throws Exception {
        OrganizationLocal ol = getOrganizationLocal();
        OrganizationAddressDO orgDO = ol.getOrganizationAddress(orgId);
        OrganizationsManager m = OrganizationsManager.getInstance();
        m.setOrganizationAddress(orgDO);
        m.setOrganizationReferenceTable(ReferenceTableCache.getReferenceTable("organization"));
        
        return m;
    }
    
    public OrganizationsManager fetchWithContacts(Integer orgId) throws Exception {
        OrganizationsManager m = fetch(orgId);
        m.setOrganizationReferenceTable(ReferenceTableCache.getReferenceTable("organization"));
        try{
            m.getContacts();
        }catch(EntityNotFoundException ignE){}
        
        return m;
    }
    
    public OrganizationsManager fetchWithNotes(Integer orgId) throws Exception {
        OrganizationsManager m = fetch(orgId);
        m.setOrganizationReferenceTable(ReferenceTableCache.getReferenceTable("organization"));
        System.out.println("in notes fetch ["+m.getOrganizationAddress().getOrganizationId()+"] ["+m.getOrganizationReferenceTable()+"]");
        try{
            m.getNotes();
        }catch(EntityNotFoundException ignE){}
        
        return m;
    }
    
    public OrganizationsManager fetchWithIdentifiers(Integer orgId) throws Exception {
        return null;
    }

    public OrganizationsManager fetchForUpdate(OrganizationsManager man) throws Exception {
        OrganizationLocal ol = getOrganizationLocal();
        man.setOrganizationAddress(ol.getOrganizationAddressAndLock(man.getOrganizationAddress().getOrganizationId()));
        
        return man;
    }
    
    public OrganizationsManager abort(OrganizationsManager man) throws Exception {
        OrganizationLocal ol = getOrganizationLocal();
        man.setOrganizationAddress(ol.getOrganizationAddressAndUnlock(man.getOrganizationAddress().getOrganizationId()));
        
        return man;
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
    
    public void validate(OrganizationsManager man) throws Exception {
        
    }
}
