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

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.IdNameDO;
import org.openelis.domain.OrganizationAddressDO;
import org.openelis.domain.OrganizationContactDO;
import org.openelis.entity.Organization;
import org.openelis.entity.OrganizationContact;
import org.openelis.exception.NotFoundException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.AddressLocal;
import org.openelis.local.LockLocal;
import org.openelis.local.OrganizationLocal;
import org.openelis.metamap.OrganizationMetaMap;
import org.openelis.remote.OrganizationRemote;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;
import org.openelis.utils.ReferenceTableCache;

@Stateless

@SecurityDomain("openelis")
@RolesAllowed("organization-select")
public class OrganizationBean implements OrganizationRemote, OrganizationLocal {

	@PersistenceContext(name = "openelis")
    private EntityManager manager;
	
	@EJB private AddressLocal addressBean;
    @EJB private LockLocal lockBean;
    
    private static int orgRefTableId;
    private static final OrganizationMetaMap OrgMeta = new OrganizationMetaMap();
    
    public OrganizationBean(){
        orgRefTableId = ReferenceTableCache.getReferenceTable("organization");
    }
    
	public OrganizationAddressDO fetchById(Integer organizationId) throws Exception {		
		Query query = manager.createNamedQuery("Organization.OrganizationAndAddress");
		query.setParameter("id", organizationId);
		OrganizationAddressDO orgAddressDO = (OrganizationAddressDO) query.getSingleResult();

        return orgAddressDO;
	}
	
	public ArrayList<IdNameDO> query(ArrayList<QueryData> fields,
                                     int first,
                                     int max) throws Exception {
        StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();

        qb.setMeta(OrgMeta);

        qb.setSelect("distinct new org.openelis.domain.IdNameDO(" + OrgMeta.getId()
                     + ", "
                     + OrgMeta.getName()
                     + ") ");

        // this method is going to throw an exception if a column doesnt match

        qb.addNewWhere(fields);

        qb.setOrderBy(OrgMeta.getName());

        sb.append(qb.getEJBQL());

        Query query = manager.createQuery(sb.toString());

        if (first > -1 && max > -1)
            query.setMaxResults(first + max);

        // ***set the parameters in the query
        qb.setNewQueryParams(query, fields);

        ArrayList<IdNameDO> returnList = (ArrayList<IdNameDO>)GetPage.getPage(query.getResultList(),
                                                                              first,
                                                                              max);

        if (returnList == null)
            throw new LastPageException();
        else
            return returnList;
    }
	
	public void add(OrganizationAddressDO organizationDO){
         manager.setFlushMode(FlushModeType.COMMIT);
         Organization organization = new Organization();
        
        //send the address to the update address bean
        addressBean.add(organizationDO.getAddressDO());
        
        //update organization
        organization.setAddressId(organizationDO.getAddressDO().getId());
        
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
        addressBean.update(organizationDO.getAddressDO());
        
        //update organization
        organization.setIsActive(organizationDO.getIsActive());
        organization.setName(organizationDO.getName());
        organization.setParentOrganizationId(organizationDO.getParentOrganizationId());
        
        lockBean.giveUpLock(orgRefTableId, organization.getId());                                  
    }
    
    public void addContact(OrganizationContactDO contactDO) throws Exception{
        System.out.println("inside add contact!!");
        manager.setFlushMode(FlushModeType.COMMIT);
        
        OrganizationContact orgContact = new OrganizationContact();
    
        //send the contact address to the address bean
        addressBean.add(contactDO.getAddressDO());
            
        orgContact.setContactTypeId(contactDO.getContactType());
        orgContact.setName(contactDO.getName());
        orgContact.setOrganizationId(contactDO.getOrganization());
        orgContact.setAddressId(contactDO.getAddressDO().getId());
        
        manager.persist(orgContact);
        contactDO.setId(orgContact.getId());
    }

    public void updateContact(OrganizationContactDO contactDO) throws Exception{
        System.out.println("inside org bean updateContact");
        manager.setFlushMode(FlushModeType.COMMIT);
        
        OrganizationContact orgContact = manager.find(OrganizationContact.class, contactDO.getId());

        //send the contact address to the address bean
        addressBean.update(contactDO.getAddressDO());
            
        orgContact.setContactTypeId(contactDO.getContactType());
        orgContact.setName(contactDO.getName());
        orgContact.setOrganizationId(contactDO.getOrganization());
        orgContact.setAddressId(contactDO.getAddressDO().getId());
        
    }
    
    public void deleteContact(OrganizationContactDO contactDO) throws Exception{
        manager.setFlushMode(FlushModeType.COMMIT);
        
        OrganizationContact orgContact = manager.find(OrganizationContact.class, contactDO.getId());
        
        addressBean.delete(contactDO.getAddressDO());
        
        if(orgContact != null)
            manager.remove(orgContact);
    }

	public List<OrganizationContactDO> fetchContactsById(Integer organizationId) throws Exception {
		Query query = manager.createNamedQuery("OrganizationContact.ContactsByOrgId");
		query.setParameter("id", organizationId);
		
		List contactsList = query.getResultList();
		
		if(contactsList.size() == 0)
		    throw new NotFoundException();
		
        return contactsList;
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
	
	public void validateOrganization(OrganizationAddressDO organizationDO, List contacts) throws Exception {
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
}