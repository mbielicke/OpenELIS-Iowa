/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) OpenELIS.  All Rights Reserved.
*/
package org.openelis.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.NoteDO;
import org.openelis.domain.OrganizationAddressDO;
import org.openelis.domain.OrganizationContactDO;
import org.openelis.entity.Note;
import org.openelis.entity.Organization;
import org.openelis.entity.OrganizationContact;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.local.LockLocal;
import org.openelis.metamap.OrganizationMetaMap;
import org.openelis.remote.AddressLocal;
import org.openelis.remote.OrganizationRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.security.local.SystemUserUtilLocal;
import org.openelis.util.Datetime;
import org.openelis.util.NewQueryBuilder;
import org.openelis.utils.GetPage;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("organization-select")
public class OrganizationBean implements OrganizationRemote {

	@PersistenceContext(name = "openelis")
    private EntityManager manager;
    
	@EJB
	private SystemUserUtilLocal sysUser;
	
	@Resource
	private SessionContext ctx;
	
    private LockLocal lockBean;
    private AddressLocal addressBean;
    private static final OrganizationMetaMap OrgMeta = new OrganizationMetaMap();
    
    {
        try {
            InitialContext cont = new InitialContext();
            lockBean =  (LockLocal)cont.lookup("openelis/LockBean/local");
            addressBean =  (AddressLocal)cont.lookup("openelis/AddressBean/local");
        }catch(Exception e){
            
        }
    }
    
	public OrganizationAddressDO getOrganizationAddress(Integer organizationId) {		
		Query query = manager.createNamedQuery("Organization.OrganizationAndAddress");
		query.setParameter("id", organizationId);
		OrganizationAddressDO orgAddressContacts = (OrganizationAddressDO) query.getResultList().get(0);// getting organization with address and contacts

        return orgAddressContacts;
	}
	
	public OrganizationAddressDO getOrganizationAddressAndUnlock(Integer organizationId) {
		//unlock the entity
        Query unlockQuery = manager.createNamedQuery("getTableId");
        unlockQuery.setParameter("name", "organization");
        lockBean.giveUpLock((Integer)unlockQuery.getSingleResult(),organizationId);
       		
        return getOrganizationAddress(organizationId);
	}
	
	@RolesAllowed("organization-update")
    public OrganizationAddressDO getOrganizationAddressAndLock(Integer organizationId) throws Exception{
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "organization");
        lockBean.getLock((Integer)query.getSingleResult(),organizationId);
        
        return getOrganizationAddress(organizationId);
    }
	
	@RolesAllowed("organization-update")
    public Integer updateOrganization(OrganizationAddressDO organizationDO, NoteDO noteDO, List contacts) throws Exception{
	    //organization reference table id
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "organization");
        Integer organizationReferenceId = (Integer)query.getSingleResult();
        
        //organization contact reference table id
        query.setParameter("name", "organization_contact");
        Integer organizationContactReferenceId = (Integer)query.getSingleResult();
        
        if(organizationDO.getOrganizationId() != null){
            //we need to call lock one more time to make sure their lock didnt expire and someone else grabbed the record
            lockBean.getLock(organizationReferenceId,organizationDO.getOrganizationId());
        }
        
		 manager.setFlushMode(FlushModeType.COMMIT);
		 Organization organization = null;
    
         if (organizationDO.getOrganizationId() == null)
        	organization = new Organization();
        else
            organization = manager.find(Organization.class, organizationDO.getOrganizationId());

        //validate the organization record and its address
        List exceptionList = new ArrayList();
        validateOrganizationAndAddress(organizationDO, exceptionList);
        if(exceptionList.size() > 0){
        	throw (RPCException)exceptionList.get(0);
        }
        
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
        for (int i=0; i<contacts.size();i++) {
			OrganizationContactDO contactDO = (OrganizationContactDO) contacts.get(i);
            OrganizationContact orgContact = null;
            
            //validate the organization record and its address
            exceptionList = new ArrayList();
            validateContactAndAddress(contactDO, i, exceptionList);
            if(exceptionList.size() > 0){
            	throw (RPCException)exceptionList.get(0);
            }
            
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
        
        
        
//          update note
        Note note = null;
        //we need to make sure the note is filled out...
        if(noteDO.getText() != null || noteDO.getSubject() != null){
        	note = new Note();
            note.setIsExternal(noteDO.getIsExternal());
            note.setReferenceId(organization.getId());
            note.setReferenceTableId(organizationReferenceId);
            note.setSubject(noteDO.getSubject());
            note.setSystemUserId(getSystemUserId());
            note.setText(noteDO.getText());
        	note.setTimestamp(Datetime.getInstance());
    	}
        
//          insert into note table if necessary
        if(note != null && note.getId() == null){
        	manager.persist(note);
        }

        lockBean.giveUpLock(organizationReferenceId,organization.getId()); 
   
        return organization.getId();        
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

        return orgNotes;
	}

	public Integer getSystemUserId(){
	        try {
	            SystemUserDO systemUserDO = sysUser.getSystemUser(ctx.getCallerPrincipal()
	                                                                 .getName());
	            return systemUserDO.getId();
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }         
	    }

	public List query(HashMap fields, int first, int max) throws Exception{
//		organization reference table id
    	Query refIdQuery = manager.createNamedQuery("getTableId");
    	refIdQuery.setParameter("name", "organization");
        Integer organizationReferenceId = (Integer)refIdQuery.getSingleResult();
        
//      organization contact reference table id
        Integer organizationContactReferenceId = (Integer)refIdQuery.getSingleResult();
        
        StringBuffer sb = new StringBuffer();
        NewQueryBuilder qb = new NewQueryBuilder();

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
        
        List returnList = GetPage.getPage(query.getResultList(), first, max);
        
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

	public List validateForAdd(OrganizationAddressDO organizationDO, List contacts) {
		List exceptionList = new ArrayList();
		
		validateOrganizationAndAddress(organizationDO, exceptionList);
		
		for(int i=0; i<contacts.size();i++){			
			OrganizationContactDO contactDO = (OrganizationContactDO) contacts.get(i);
			
			validateContactAndAddress(contactDO, i, exceptionList);
		}
		
		return exceptionList;
	}

	public List validateForUpdate(OrganizationAddressDO organizationDO, List contacts) {
		List exceptionList = new ArrayList();
		
		validateOrganizationAndAddress(organizationDO, exceptionList);
		
		for(int i=0; i<contacts.size();i++){			
			OrganizationContactDO contactDO = (OrganizationContactDO) contacts.get(i);
			
			validateContactAndAddress(contactDO, i, exceptionList);
		}
		
		return exceptionList;
	}
	
	private void validateOrganizationAndAddress(OrganizationAddressDO organizationDO, List exceptionList){
		//name required
		if(organizationDO.getName() == null || "".equals(organizationDO.getName())){
			exceptionList.add(new FieldErrorException("fieldRequiredException",OrgMeta.getName()));
		}
		
		//street address required
		if(organizationDO.getAddressDO().getStreetAddress() == null || "".equals(organizationDO.getAddressDO().getStreetAddress())){
			exceptionList.add(new FieldErrorException("fieldRequiredException",OrgMeta.ADDRESS.getStreetAddress()));
		}

		//city required
		if(organizationDO.getAddressDO().getCity() == null || "".equals(organizationDO.getAddressDO().getCity())){
			exceptionList.add(new FieldErrorException("fieldRequiredException",OrgMeta.ADDRESS.getCity()));
		}

		//zipcode required
		if(organizationDO.getAddressDO().getZipCode() == null || "".equals(organizationDO.getAddressDO().getZipCode())){
			exceptionList.add(new FieldErrorException("fieldRequiredException",OrgMeta.ADDRESS.getZipCode()));
		}
		
		//country required
		if(organizationDO.getAddressDO().getCountry() == null || "".equals(organizationDO.getAddressDO().getCountry())){
			exceptionList.add(new FieldErrorException("fieldRequiredException",OrgMeta.ADDRESS.getCountry()));
		}		
	}
	
	private void validateContactAndAddress(OrganizationContactDO orgContactDO, int rowIndex, List exceptionList){
		//contact type required
		if(orgContactDO.getContactType() == null || "".equals(orgContactDO.getContactType())){
			exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, OrgMeta.ORGANIZATION_CONTACT.getContactTypeId()));
		}

		//name required
		if(orgContactDO.getName() == null || "".equals(orgContactDO.getName())){
			exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, OrgMeta.ORGANIZATION_CONTACT.getName()));
		}
		
		//street address required
		if(orgContactDO.getAddressDO().getStreetAddress() == null || "".equals(orgContactDO.getAddressDO().getStreetAddress())){
			exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, OrgMeta.ORGANIZATION_CONTACT.ADDRESS.getStreetAddress()));
		}
		
		//city required
		if(orgContactDO.getAddressDO().getCity() == null || "".equals(orgContactDO.getAddressDO().getCity())){
			exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, OrgMeta.ORGANIZATION_CONTACT.ADDRESS.getCity()));	
		}
		
		//zipcode required
		if(orgContactDO.getAddressDO().getZipCode() == null || "".equals(orgContactDO.getAddressDO().getZipCode())){
			exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, OrgMeta.ORGANIZATION_CONTACT.ADDRESS.getZipCode()));
		}
		
		//country required
		if(orgContactDO.getAddressDO().getCountry() == null || "".equals(orgContactDO.getAddressDO().getCountry())){
			exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, OrgMeta.ORGANIZATION_CONTACT.ADDRESS.getCountry()));
		}		
	}
}
