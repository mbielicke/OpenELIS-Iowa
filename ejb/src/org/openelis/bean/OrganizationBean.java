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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.ApplicationException;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.IdNameDO;
import org.openelis.domain.NoteDO;
import org.openelis.domain.OrganizationAddressDO;
import org.openelis.domain.OrganizationContactDO;
import org.openelis.entity.Note;
import org.openelis.entity.Organization;
import org.openelis.entity.OrganizationContact;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.SecurityModule.ModuleFlags;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.rewrite.QueryData;
import org.openelis.local.AddressLocal;
import org.openelis.local.LockLocal;
import org.openelis.local.OrganizationLocal;
import org.openelis.manager.OrganizationContactsManager;
import org.openelis.manager.OrganizationsManager;
import org.openelis.metamap.OrganizationMetaMap;
import org.openelis.remote.OrganizationRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.security.local.SystemUserLocal;
import org.openelis.util.Datetime;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;
import org.openelis.utils.ReferenceTableCache;
import org.openelis.utils.SecurityInterceptor;

@Stateless
/*@EJBs({
    @EJB(name="ejb/Lock",beanInterface=LockLocal.class),
    @EJB(name="ejb/Address",beanInterface=AddressLocal.class)
})*/
@SecurityDomain("openelis")
@RolesAllowed("organization-select")
public class OrganizationBean implements OrganizationRemote, OrganizationLocal {

	@PersistenceContext(name = "openelis")
    private EntityManager manager;
	
	@EJB private SystemUserLocal userLocal;
	
	@Resource
	private SessionContext ctx;
	
	@EJB private AddressLocal addressBean;
    @EJB private LockLocal lockBean;
    
    private static int orgRefTableId;
    private static final OrganizationMetaMap OrgMeta = new OrganizationMetaMap();
    
    public OrganizationBean(){
        orgRefTableId = ReferenceTableCache.getReferenceTable("organization");
    }
    
	public OrganizationAddressDO getOrganizationAddress(Integer organizationId) {		
		Query query = manager.createNamedQuery("Organization.OrganizationAndAddress");
		query.setParameter("id", organizationId);
		OrganizationAddressDO orgAddressContacts = (OrganizationAddressDO) query.getResultList().get(0);// getting organization with address and contacts

        return orgAddressContacts;
	}
	
	public OrganizationAddressDO getOrganizationAddressAndUnlock(Integer organizationId) {
		//unlock the entity
        lockBean.giveUpLock(orgRefTableId, organizationId);
       		
        return getOrganizationAddress(organizationId);
	}
	
	@RolesAllowed("organization-update")
    //@Interceptors(SecurityInterceptor.class)
    public OrganizationAddressDO getOrganizationAddressAndLock(Integer organizationId) throws Exception{
        SecurityInterceptor.applySecurity(ctx.getCallerPrincipal().getName(), "organization", ModuleFlags.UPDATE);
        lockBean.getLock(orgRefTableId, organizationId);
        
        return getOrganizationAddress(organizationId);
    }
	
	//@RolesAllowed("organization-update")
    //@Interceptors(SecurityInterceptor.class)
    public Integer updateOrganization(OrganizationAddressDO organizationDO, NoteDO noteDO, List<OrganizationContactDO> contacts) throws Exception{
        SecurityInterceptor.applySecurity(ctx.getCallerPrincipal().getName(),"organization", ModuleFlags.UPDATE);
	    //organization reference table id

        if(organizationDO.getOrganizationId() != null)
            lockBean.validateLock(orgRefTableId, organizationDO.getOrganizationId());
        
        //validate organization record and its contacts
        validateOrganization(organizationDO, contacts);

		 manager.setFlushMode(FlushModeType.COMMIT);
		 Organization organization = null;
    
         if (organizationDO.getOrganizationId() == null)
        	organization = new Organization();
        else
            organization = manager.find(Organization.class, organizationDO.getOrganizationId());

         //send the address to the update address bean
        Integer orgAddressId = addressBean.updateAddress(organizationDO.getAddressDO());
        
        //update organization
        organization.setAddressId(orgAddressId);
        
        organization.setIsActive(organizationDO.getIsActive());
        organization.setName(organizationDO.getName());
        organization.setParentOrganizationId(organizationDO.getParentOrganizationId());
                
        if (organization.getId() == null) {
        	manager.persist(organization);
        }
        
        //update contacts
        if(contacts != null) {
            for (OrganizationContactDO contactDO : contacts) {
                OrganizationContact orgContact = null;
            
                if (contactDO.getId() == null)
                    orgContact = new OrganizationContact();
                else
                    orgContact = manager.find(OrganizationContact.class, contactDO.getId());

                if(contactDO.getDelete() && orgContact.getId() != null){
                    //delete the contact record and the address record from the database
                    manager.remove(orgContact);
                    addressBean.deleteAddress(contactDO.getAddressDO());
            	
                }else{
//	            	send the contact address to the address bean
                    Integer contactAddressId = addressBean.updateAddress(contactDO.getAddressDO());
		                
                    orgContact.setContactTypeId(contactDO.getContactType());
                    orgContact.setName(contactDO.getName());
                    orgContact.setOrganizationId(organization.getId());
                    orgContact.setAddressId(contactAddressId);
		            
                    if (orgContact.getId() == null) {
                        manager.persist(orgContact);
                    }
                }
            }
        }
        
        
        
//          update note
        if(noteDO != null) {
            Note note = null;
            //  we need to make sure the note is filled out...
            if(noteDO.getText() != null || noteDO.getSubject() != null){
                note = new Note();
                note.setIsExternal(noteDO.getIsExternal());
                note.setReferenceId(organization.getId());
                note.setReferenceTableId(orgRefTableId);
                note.setSubject(noteDO.getSubject());
                note.setSystemUserId(lockBean.getSystemUserId());
                note.setText(noteDO.getText());
                note.setTimestamp(Datetime.getInstance());
            }
        
//         insert into note table if necessary
            if(note != null && note.getId() == null){
                manager.persist(note);
            }
        }

        lockBean.giveUpLock(orgRefTableId, organization.getId()); 
   
        return organization.getId();        
    }
    
    public void add(OrganizationAddressDO organizationDO){
         manager.setFlushMode(FlushModeType.COMMIT);
         Organization organization = new Organization();
        
        //send the address to the update address bean
        Integer orgAddressId = addressBean.updateAddress(organizationDO.getAddressDO());
        
        //update organization
        organization.setAddressId(orgAddressId);
        
        organization.setIsActive(organizationDO.getIsActive());
        organization.setName(organizationDO.getName());
        organization.setParentOrganizationId(organizationDO.getParentOrganizationId());
                
        manager.persist(organization);
        
        organizationDO.setOrganizationId(organization.getId());
    }
    
    public void update(OrganizationAddressDO organizationDO) throws Exception {
        lockBean.validateLock(orgRefTableId, organizationDO.getOrganizationId());
        
         manager.setFlushMode(FlushModeType.COMMIT);
         Organization organization = manager.find(Organization.class, organizationDO.getOrganizationId());

        //send the address to the update address bean
        addressBean.updateAddress(organizationDO.getAddressDO());
        
        //update organization
        organization.setIsActive(organizationDO.getIsActive());
        organization.setName(organizationDO.getName());
        organization.setParentOrganizationId(organizationDO.getParentOrganizationId());
        
        lockBean.giveUpLock(orgRefTableId, organization.getId());                                  
    }

	public List<OrganizationContactDO> getOrganizationContacts(Integer organizationId) {
		Query query = manager.createNamedQuery("Organization.Contacts");
		query.setParameter("id", organizationId);
		
		List orgAddressContacts = query.getResultList();// getting list of contacts from the org id
	
        return orgAddressContacts;
	}
	
	public List getOrganizationNotes(Integer organizationId) {
		Query query = null;
		
		query = manager.createNamedQuery("Organization.Notes");	
		query.setParameter("id", organizationId);
		
		List orgNotes = query.getResultList();// getting list of noteDOs from the org id

		for(int i=0; i<orgNotes.size(); i++){
	           NoteDO noteDO = (NoteDO)orgNotes.get(i);
	           SystemUserDO userDO = userLocal.getSystemUser(noteDO.getSystemUserId());
	           noteDO.setSystemUser(userDO.getLoginName());
	       }
		
        return orgNotes;
	}

	public ArrayList<IdNameDO> query(ArrayList<AbstractField> fields, int first, int max) throws Exception{
        StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();

        qb.setMeta(OrgMeta);
      
        qb.setSelect("distinct new org.openelis.domain.IdNameDO("+OrgMeta.getId()+", "+OrgMeta.getName()+") ");
       
        //this method is going to throw an exception if a column doesnt match
       
        qb.addWhere(fields);      

        qb.setOrderBy(OrgMeta.getName());
       
        sb.append(qb.getEJBQL());

        Query query = manager.createQuery(sb.toString());
        
        if(first > -1 && max > -1)
         query.setMaxResults(first+max);
        
//      ***set the parameters in the query
        qb.setQueryParams(query);
        
        ArrayList<IdNameDO> returnList = (ArrayList<IdNameDO>)GetPage.getPage(query.getResultList(), first, max);
        
        if(returnList == null)
         throw new LastPageException();
        else
         return returnList;
	}
	
	public List autoCompleteLookupById(Integer id){
		Query query = null;
		query = manager.createNamedQuery("Organization.AutoCompleteById");
		query.setParameter("id",id);
		return query.getResultList();
	}
	
	public List autoCompleteLookupByName(String orgName, int maxResults){
		Query query = null;
		query = manager.createNamedQuery("Organization.AutoCompleteByName");
		query.setParameter("name",orgName);
		query.setMaxResults(maxResults);
		return query.getResultList();
	}
	
	private void validateOrganization(OrganizationAddressDO organizationDO, List contacts) throws Exception {
	    ValidationErrorsList list = new ValidationErrorsList();
		//name required
		if(organizationDO.getName() == null || "".equals(organizationDO.getName())){
		    list.add(new FieldErrorException("fieldRequiredException",OrgMeta.getName()));
		}
		
		//street address required
		if(organizationDO.getAddressDO().getStreetAddress() == null || "".equals(organizationDO.getAddressDO().getStreetAddress())){
		    list.add(new FieldErrorException("fieldRequiredException",OrgMeta.ADDRESS.getStreetAddress()));
		}

		//city required
		if(organizationDO.getAddressDO().getCity() == null || "".equals(organizationDO.getAddressDO().getCity())){
		    list.add(new FieldErrorException("fieldRequiredException",OrgMeta.ADDRESS.getCity()));
		}

		//zipcode required
		if(organizationDO.getAddressDO().getZipCode() == null || "".equals(organizationDO.getAddressDO().getZipCode())){
		    list.add(new FieldErrorException("fieldRequiredException",OrgMeta.ADDRESS.getZipCode()));
		}
		
		//country required
		if(organizationDO.getAddressDO().getCountry() == null || "".equals(organizationDO.getAddressDO().getCountry())){
		    list.add(new FieldErrorException("fieldRequiredException",OrgMeta.ADDRESS.getCountry()));
		}	
		
		for(int i=0; i<contacts.size();i++)       
            validateContactAndAddress((OrganizationContactDO)contacts.get(i), i, list);
		
		if(list.size() > 0)
            throw list;
	}
	
	private void validateContactAndAddress(OrganizationContactDO orgContactDO, int rowIndex, ValidationErrorsList exceptionList){
		//contact type required
		if(orgContactDO.getContactType() == null || "".equals(orgContactDO.getContactType())){
			exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, OrgMeta.ORGANIZATION_CONTACT.getContactTypeId(),"contactsTable"));
		}

		//name required
		if(orgContactDO.getName() == null || "".equals(orgContactDO.getName())){
			exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, OrgMeta.ORGANIZATION_CONTACT.getName(),"contactsTable"));
		}
		
		//street address required
		if(orgContactDO.getAddressDO().getStreetAddress() == null || "".equals(orgContactDO.getAddressDO().getStreetAddress())){
			exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, OrgMeta.ORGANIZATION_CONTACT.ADDRESS.getStreetAddress(),"contactsTable"));
		}
		
		//city required
		if(orgContactDO.getAddressDO().getCity() == null || "".equals(orgContactDO.getAddressDO().getCity())){
			exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, OrgMeta.ORGANIZATION_CONTACT.ADDRESS.getCity(),"contactsTable"));	
		}
		
		//zipcode required
		if(orgContactDO.getAddressDO().getZipCode() == null || "".equals(orgContactDO.getAddressDO().getZipCode())){
			exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, OrgMeta.ORGANIZATION_CONTACT.ADDRESS.getZipCode(),"contactsTable"));
		}
		
		//country required
		if(orgContactDO.getAddressDO().getCountry() == null || "".equals(orgContactDO.getAddressDO().getCountry())){
			exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, OrgMeta.ORGANIZATION_CONTACT.ADDRESS.getCountry(),"contactsTable"));
		}		
	}

	public ArrayList<IdNameDO> newQuery(ArrayList<QueryData> fields, int first,
			int max) throws Exception {
        StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();

        qb.setMeta(OrgMeta);
      
        qb.setSelect("distinct new org.openelis.domain.IdNameDO("+OrgMeta.getId()+", "+OrgMeta.getName()+") ");
       
        //this method is going to throw an exception if a column doesnt match
       
        qb.addNewWhere(fields);      

        qb.setOrderBy(OrgMeta.getName());
       
        sb.append(qb.getEJBQL());

        Query query = manager.createQuery(sb.toString());
        
        if(first > -1 && max > -1)
         query.setMaxResults(first+max);
        
//      ***set the parameters in the query
        qb.setNewQueryParams(query,fields);
        
        ArrayList<IdNameDO> returnList = (ArrayList<IdNameDO>)GetPage.getPage(query.getResultList(), first, max);
        
        if(returnList == null)
         throw new LastPageException();
        else
         return returnList;
	}
	
	//
	//NEW METHODS FOR ORG MANAGER SCREEN
	//
	//fetch methods
	public OrganizationsManager fetch(Integer orgId) throws Exception {
	    OrganizationsManager man = OrganizationsManager.findById(orgId);
	    
	    return man;
	}
	
	public OrganizationsManager add(OrganizationsManager man) throws Exception {
	    man.validate();
	    man.add();
	    
	    return man;
	    
	}
	
	public OrganizationsManager fetchWithContacts(Integer orgId) {
	    return null;
	}
	
    public OrganizationsManager fetchWithIdentifiers(Integer orgId) {
        return null;
    }
    
    public OrganizationsManager fetchWithNotes(Integer orgId) { 
       return null;
    }
    
    public OrganizationsManager fetchForUpdate(Integer orgId) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public OrganizationsManager abortUpdate(Integer orgId){
        return null;
    }
 
    @ApplicationException(rollback=true)
    public class TestException extends Exception{
        
    }
}