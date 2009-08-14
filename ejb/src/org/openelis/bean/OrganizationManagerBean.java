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
package org.openelis.bean;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.local.LockLocal;
import org.openelis.manager.OrganizationContactManager;
import org.openelis.manager.OrganizationManager;
import org.openelis.remote.OrganizationManagerRemote;
import org.openelis.utils.ReferenceTableCache;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)

@SecurityDomain("openelis")
@RolesAllowed("organization-select")
public class OrganizationManagerBean implements OrganizationManagerRemote {

    @PersistenceContext(name = "openelis")
    
    @Resource
    private SessionContext ctx;
    
    @EJB private LockLocal lockBean;
    
    private static int orgRefTableId;
    
    public OrganizationManagerBean(){
        orgRefTableId = ReferenceTableCache.getReferenceTable("organization");
    }
    
    public OrganizationManager add(OrganizationManager man) throws Exception {
        man.validate();
        
        UserTransaction ut = ctx.getUserTransaction();
        ut.begin();
        man.add();
        ut.commit();
        
        return man;
    }
    
    public OrganizationManager update(OrganizationManager man) throws Exception {
        man.validate();
        
        UserTransaction ut = ctx.getUserTransaction();
        ut.begin();
        man.update();
        ut.commit();
        
        return man;
    }
    
    public OrganizationManager fetch(Integer orgId) throws Exception {
        OrganizationManager man = OrganizationManager.findById(orgId);
        
        return man;
    }
    
    public OrganizationManager fetchWithContacts(Integer orgId) throws Exception {
        OrganizationManager man = OrganizationManager.findByIdWithContacts(orgId);
        
        return man;
    }
    
    public OrganizationManager fetchWithIdentifiers(Integer orgId) throws Exception {
        OrganizationManager man = OrganizationManager.findByIdWithIdentifiers(orgId);
        
        return man;
    }
    
    public OrganizationManager fetchWithNotes(Integer orgId) throws Exception { 
        OrganizationManager man = OrganizationManager.findByIdWithNotes(orgId);
        
        return man;
    }
    
    public OrganizationManager fetchForUpdate(Integer orgId) throws Exception {
        lockBean.getLock(orgRefTableId, orgId);
        
        return fetch(orgId);
    }
    
    public OrganizationManager abortUpdate(Integer orgId) throws Exception {
        lockBean.giveUpLock(orgRefTableId, orgId);
        
        return fetch(orgId);
    }
    
    public OrganizationContactManager fetchContactById(Integer id) throws Exception {
        return null;
    }
    
    public OrganizationContactManager fetchContactByOrgId(Integer orgId) throws Exception {
        OrganizationContactManager cm = OrganizationContactManager.findByOrganizationId(orgId);
        
        return cm;
    }
}