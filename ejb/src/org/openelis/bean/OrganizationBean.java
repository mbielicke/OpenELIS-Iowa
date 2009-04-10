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
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.SecurityModule.ModuleFlags;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.local.AddressLocal;
import org.openelis.local.LockLocal;
import org.openelis.metamap.OrganizationMetaMap;
import org.openelis.persistence.CachingManager;
import org.openelis.remote.OrganizationRemote;
import org.openelis.util.Datetime;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;
import org.openelis.utils.SecurityInterceptor;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.EJBs;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
@EJBs({
    @EJB(name="ejb/Lock",beanInterface=LockLocal.class),
    @EJB(name="ejb/Address",beanInterface=AddressLocal.class)
})
@SecurityDomain("openelis")
@RolesAllowed("organization-select")
public class OrganizationBean implements OrganizationRemote {

	@PersistenceContext(name = "openelis")
    private EntityManager manager;
	
	@Resource
	private SessionContext ctx;
	
    private LockLocal lockBean;
    private AddressLocal addressBean;
    private static final OrganizationMetaMap OrgMeta = new OrganizationMetaMap();
    
    @PostConstruct
    private void init() 
    {
        lockBean =  (LockLocal)ctx.lookup("ejb/Lock");
        addressBean =  (AddressLocal)ctx.lookup("ejb/Address");
    }
    
	public OrganizationAddressDO getOrganizationAddress(Integer organizationId) {		
		Query query = manager.createNamedQuery("Organization.OrganizationAndAddress");
		query.setParameter("id", organizationId);
		OrganizationAddressDO orgAddressContacts = (OrganizationAddressDO) query.getResultList().get(0);// getting organization with address and contacts

        return orgAddressContacts;
	}
	
	public OrganizationAddressDO getOrganizationAddressAndUnlock(Integer organizationId, String session) {
		//unlock the entity
        Query unlockQuery = manager.createNamedQuery("getTableId");
        unlockQuery.setParameter("name", "organization");
        lockBean.giveUpLock((Integer)unlockQuery.getSingleResult(),organizationId);
       		
        return getOrganizationAddress(organizationId);
	}
	
	@RolesAllowed("organization-update")
    //@Interceptors(SecurityInterceptor.class)
    public OrganizationAddressDO getOrganizationAddressAndLock(Integer organizationId, String session) throws Exception{
        System.out.println("local cache alive "+CachingManager.isAlive("security"));
        SecurityInterceptor.applySecurity(ctx.getCallerPrincipal().getName(), "organization", ModuleFlags.UPDATE);
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "organization");
        lockBean.getLock((Integer)query.getSingleResult(),organizationId);
        
        return getOrganizationAddress(organizationId);
    }
	
	//@RolesAllowed("organization-update")
    //@Interceptors(SecurityInterceptor.class)
    public Integer updateOrganization(OrganizationAddressDO organizationDO, NoteDO noteDO, List<OrganizationContactDO> contacts) throws Exception{
        SecurityInterceptor.applySecurity(ctx.getCallerPrincipal().getName(),"organization", ModuleFlags.UPDATE);
	    //organization reference table id
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "organization");
        Integer organizationReferenceId = (Integer)query.getSingleResult();
        
        //organization contact reference table id
        query.setParameter("name", "organization_contact");
        Integer organizationContactReferenceId = (Integer)query.getSingleResult();
        
        if(organizationDO.getOrganizationId() != null)
            lockBean.validateLock(organizationReferenceId,organizationDO.getOrganizationId());
        
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
                note.setReferenceTableId(organizationReferenceId);
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

	public List query(ArrayList<AbstractField> fields, int first, int max) throws Exception{
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
