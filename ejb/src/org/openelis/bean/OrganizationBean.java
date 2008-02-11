package org.openelis.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

import org.openelis.domain.NoteDO;
import org.openelis.domain.OrganizationAddressDO;
import org.openelis.domain.OrganizationContactDO;
import org.openelis.domain.OrganizationTableRowDO;
import org.openelis.entity.Note;
import org.openelis.entity.Organization;
import org.openelis.entity.OrganizationContact;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.data.CollectionField;
import org.openelis.gwt.common.data.QueryNumberField;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.local.LockLocal;
import org.openelis.remote.AddressLocal;
import org.openelis.remote.OrganizationRemote;
import org.openelis.util.Datetime;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

import edu.uiowa.uhl.security.domain.SystemUserDO;
import edu.uiowa.uhl.security.local.SystemUserUtilLocal;

@Stateless
public class OrganizationBean implements OrganizationRemote {

	@PersistenceContext(name = "openelis")
    private EntityManager manager;
    //private String className = this.getClass().getName();
   // private Logger log = Logger.getLogger(className);
    
	@EJB
	private SystemUserUtilLocal sysUser;
	
	@Resource
	private SessionContext ctx;
	
    private LockLocal lockBean;
    private AddressLocal addressBean;
    
    {
        try {
            InitialContext cont = new InitialContext();
            lockBean =  (LockLocal)cont.lookup("openelis/LockBean/local");
            addressBean =  (AddressLocal)cont.lookup("openelis/AddressBean/local");
        }catch(Exception e){
            
        }
    }
    
	public OrganizationAddressDO getOrganizationAddress(Integer organizationId, boolean unlock) {
		if(unlock){
            Query query = manager.createNamedQuery("getTableId");
            query.setParameter("name", "organization");
            lockBean.giveUpLock((Integer)query.getSingleResult(),organizationId);
        }
		
		Query query = manager.createNamedQuery("getOrganizationAndAddress");
		query.setParameter("id", organizationId);
		OrganizationAddressDO orgAddressContacts = (OrganizationAddressDO) query.getResultList().get(0);// getting organization with address and contacts

        return orgAddressContacts;
	}
	
	@RolesAllowed("organization-update")
    public OrganizationAddressDO getOrganizationAddressUpdate(Integer id) throws Exception{
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "organization");
        lockBean.getLock((Integer)query.getSingleResult(),id); 
        return getOrganizationAddress(id, false);
    }
	
	@RolesAllowed("organization-update")
    public Integer updateOrganization(OrganizationAddressDO organizationDO, NoteDO noteDO, List contacts) {
//        log.debug("Entering updateProject");
		 manager.setFlushMode(FlushModeType.COMMIT);
		 Organization organization = null;
        try {
        	//organization reference table id
        	Query query = manager.createNamedQuery("getTableId");
            query.setParameter("name", "organization");
            Integer organizationReferenceId = (Integer)query.getSingleResult();
            
//          organization contact reference table id
            query.setParameter("name", "organization_contact");
            Integer organizationContactReferenceId = (Integer)query.getSingleResult();
            
            if (organizationDO.getOrganizationId() == null)
            	organization = new Organization();
            else
                organization = manager.find(Organization.class, organizationDO.getOrganizationId());

            //send the address to the update address bean
            Integer orgAddressId = addressBean.updateAddress(organizationDO.getAddressDO());
            
            //update organization
            organization.setAddress(orgAddressId);
            
            organization.setIsActive(organizationDO.getIsActive());
            organization.setName(organizationDO.getName());
            organization.setParentOrganization(organizationDO.getParentOrganization());
                    
	        if (organization.getId() == null) {
	        	manager.persist(organization);
            }
            
            //update contacts
            for (Iterator contactsItr = contacts.iterator(); contactsItr.hasNext();) {
				OrganizationContactDO contactDO = (OrganizationContactDO) contactsItr.next();
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
			                
		            orgContact.setContactType(contactDO.getContactType());
			        orgContact.setName(contactDO.getName());
			        orgContact.setOrganization(organization.getId());
			        orgContact.setAddress(contactAddressId);
			            
			        if (orgContact.getId() == null) {
			            manager.persist(orgContact);
			        }
	           }
			}
            
            
            
//          update note
            Note note = null;
            //we need to make sure the note is filled out...
	        if(!("".equals(noteDO.getText())) ||   !("".equals(noteDO.getSubject()))){
	        	note = new Note();
	            note.setIsExternal(noteDO.getIsExternal());
	            note.setReferenceId(organization.getId());
	            note.setReferenceTable(organizationReferenceId);
	            note.setSubject(noteDO.getSubject());
	            note.setSystemUser(getSystemUserId());
	            note.setText(noteDO.getText());
            	note.setTimestamp(Datetime.getInstance());
        	}
            
//          insert into note table if necessary
            if(note != null && note.getId() == null){
            	manager.persist(note);
            }

            lockBean.giveUpLock(organizationReferenceId,organization.getId()); 
        } catch (Exception e) {
            //log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            //log.debug("Exiting updateProject");
        }
        return organization.getId();        
    }

	public List<OrganizationContactDO> getOrganizationContacts(Integer organizationId) {
		Query query = manager.createNamedQuery("getOrganizationContacts");
		query.setParameter("id", organizationId);
		
		List orgAddressContacts = query.getResultList();// getting list of contacts from the org id
	
        return orgAddressContacts;
	}
	
	public List getOrganizationNotes(Integer organizationId) {
		Query query = null;
		
		query = manager.createNamedQuery("getOrganizationNotesTopLevel");
		//}else{
		//	query = manager.createNamedQuery("getOrganizationNotesSecondLevel");
		//}
		
		query.setParameter("id", organizationId);
		
		List orgNotes = query.getResultList();// getting list of notes from the org id

        return orgNotes;
	}
	
	public List<OrganizationTableRowDO> getOrganizationNameListByLetter(String letter, int startPos, int maxResults){
		Query query = manager.createNamedQuery("getOrganizationNameRowsByLetter");
		query.setParameter("letter", letter);
		
		if(maxResults > 0){
			query.setFirstResult(startPos);
			query.setMaxResults(maxResults);
		}
		
		List orgList = query.getResultList();// getting a list of organizations
		
		return orgList;
	}

	public Integer getSystemUserId(){
	        try {
	            SystemUserDO systemUserDO = sysUser.getSystemUser(ctx.getCallerPrincipal()
	                                                                 .getName());
	            return systemUserDO.getId();
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        } finally {
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
        
        sb.append("select distinct o.id,o.name " + "from Organization o left join o.orgNote n " +
        		" left join o.organizationContact oc left join oc.orgContactaddress oca" +
				" where " +
				" (n.referenceTable = "+organizationReferenceId+" or n.referenceTable is null) "
        		);
         //***append the abstract fields to the string buffer
//       org elements
         if(fields.containsKey("orgId"))
        	 sb.append(QueryBuilder.getQuery((QueryNumberField)fields.get("orgId"), "o.id"));
         if(fields.containsKey("orgName"))
        	 sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("orgName"), "o.name"));
         if(fields.containsKey("streetAddress"))
        	 sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("streetAddress"), "o.orgAddress.streetAddress"));
         if(fields.containsKey("multUnit"))
        	 sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("multUnit"), "o.orgAddress.multipleUnit"));
         if(fields.containsKey("city"))
        	 sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("city"),"o.orgAddress.city"));         
         if(fields.containsKey("state") && ((ArrayList)((CollectionField)fields.get("state")).getValue()).size()>0 &&
        		 !(((ArrayList)((CollectionField)fields.get("state")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("state")).getValue()).get(0))))
        	 sb.append(QueryBuilder.getQuery((CollectionField)fields.get("state"),"o.orgAddress.state"));        
         if(fields.containsKey("zipCode"))
        	 sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("zipCode"),"o.orgAddress.zipCode"));         
         if(fields.containsKey("country") && ((ArrayList)((CollectionField)fields.get("country")).getValue()).size()>0 &&
        		 !(((ArrayList)((CollectionField)fields.get("country")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("country")).getValue()).get(0))))
        	 sb.append(QueryBuilder.getQuery((CollectionField)fields.get("country"),"o.orgAddress.country"));        
         if(fields.containsKey("parentOrg"))
        	 sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("parentOrg"),"o.parentOrg.name")); 
         if(fields.containsKey("isActive") && ((ArrayList)((CollectionField)fields.get("isActive")).getValue()).size()>0 &&
        		 !(((ArrayList)((CollectionField)fields.get("isActive")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("isActive")).getValue()).get(0))))
        	 sb.append(QueryBuilder.getQuery((CollectionField)fields.get("isActive"), "o.isActive"));         
         //org contact elements
         if(fields.containsKey("contactType") && ((ArrayList)((CollectionField)fields.get("contactType")).getValue()).size()>0 &&
        		 !(((ArrayList)((CollectionField)fields.get("contactType")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("contactType")).getValue()).get(0))))
        	 sb.append(QueryBuilder.getQuery((CollectionField)fields.get("contactType"), "oc.contactType"));         
         if(fields.containsKey("contactName") && ((QueryStringField)fields.get("contactName")).getComparator() != null)
        	 sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("contactName"), "oc.name"));
         if(fields.containsKey("contactMultUnit") && ((QueryStringField)fields.get("contactMultUnit")).getComparator() != null)
         	sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("contactMultUnit"), "oca.multipleUnit"));
         if(fields.containsKey("contactStreetAddress") && ((QueryStringField)fields.get("contactStreetAddress")).getComparator() != null)
        	 sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("contactStreetAddress"), "oca.streetAddress"));
         if(fields.containsKey("contactCity") && ((QueryStringField)fields.get("contactCity")).getComparator() != null)
        	 sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("contactCity"), "oca.city"));
         if(fields.containsKey("contactState") && ((ArrayList)((CollectionField)fields.get("contactState")).getValue()).size()>0 &&
        		 !(((ArrayList)((CollectionField)fields.get("contactState")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("contactState")).getValue()).get(0))))
        	 sb.append(QueryBuilder.getQuery((CollectionField)fields.get("contactState"), "oca.state"));    
         if(fields.containsKey("contactZipCode") && ((QueryStringField)fields.get("contactZipCode")).getComparator() != null)
        	 sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("contactZipCode"), "oca.zipCode"));
         if(fields.containsKey("contactWorkPhone") && ((QueryStringField)fields.get("contactWorkPhone")).getComparator() != null)
        	 sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("contactWorkPhone"), "oca.workPhone"));
         if(fields.containsKey("contactHomePhone") && ((QueryStringField)fields.get("contactHomePhone")).getComparator() != null)
        	 sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("contactHomePhone"), "oca.homePhone"));
         if(fields.containsKey("contactCellPhone") && ((QueryStringField)fields.get("contactCellPhone")).getComparator() != null)
        	 sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("contactCellPhone"), "oca.cellPhone"));
         if(fields.containsKey("contactFaxPhone") && ((QueryStringField)fields.get("contactFaxPhone")).getComparator() != null)
        	 sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("contactFaxPhone"), "oca.faxPhone"));
         if(fields.containsKey("contactEmail") && ((QueryStringField)fields.get("contactEmail")).getComparator() != null)
        	 sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("contactEmail"), "oca.email"));
         if(fields.containsKey("contactCountry") && ((ArrayList)((CollectionField)fields.get("contactCountry")).getValue()).size()>0 &&
        		 !(((ArrayList)((CollectionField)fields.get("contactCountry")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("contactCountry")).getValue()).get(0))))
        	 sb.append(QueryBuilder.getQuery((CollectionField)fields.get("contactCountry"), "oca.country"));         
         
         //org notes
         if(fields.containsKey("usersSubject"))
        	 sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("usersSubject"),"n.subject"));	
   
         Query query = manager.createQuery(sb.toString()+" order by o.name");
         
        // if(first > -1)
        	// query.setFirstResult(first);
 
         if(first > -1 && max > -1)
        	 query.setMaxResults(first+max);
             
//       ***set the parameters in the query
//       org elements
         if(fields.containsKey("orgId"))
        	 QueryBuilder.setParameters((QueryNumberField)fields.get("orgId"), "o.id", query);
         if(fields.containsKey("orgName"))
        	 QueryBuilder.setParameters((QueryStringField)fields.get("orgName"), "o.name", query);
         if(fields.containsKey("streetAddress"))
        	 QueryBuilder.setParameters((QueryStringField)fields.get("streetAddress"), "o.orgAddress.streetAddress", query);
         if(fields.containsKey("multUnit"))
        	 QueryBuilder.setParameters((QueryStringField)fields.get("multUnit"), "o.orgAddress.multipleUnit", query);
         if(fields.containsKey("city"))
        	 QueryBuilder.setParameters((QueryStringField)fields.get("city"), "o.orgAddress.city", query);  
         if(fields.containsKey("state") && ((ArrayList)((CollectionField)fields.get("state")).getValue()).size()>0 &&
        		 !(((ArrayList)((CollectionField)fields.get("state")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("state")).getValue()).get(0))))
        	 QueryBuilder.setParameters((CollectionField)fields.get("state"), "o.orgAddress.state", query);         
         if(fields.containsKey("zipCode"))
        	 QueryBuilder.setParameters((QueryStringField)fields.get("zipCode"), "o.orgAddress.zipCode", query);
         if(fields.containsKey("country") && ((ArrayList)((CollectionField)fields.get("country")).getValue()).size()>0 &&
        		 !(((ArrayList)((CollectionField)fields.get("country")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("country")).getValue()).get(0))))
        	QueryBuilder.setParameters((CollectionField)fields.get("country"), "o.orgAddress.country", query); 
         if(fields.containsKey("parentOrg"))       	 
        	 QueryBuilder.setParameters((QueryStringField)fields.get("parentOrg"), "o.parentOrg.name", query);        
         if(fields.containsKey("isActive") && ((ArrayList)((CollectionField)fields.get("isActive")).getValue()).size()>0 &&
        		 !(((ArrayList)((CollectionField)fields.get("isActive")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("isActive")).getValue()).get(0))))
        	 QueryBuilder.setParameters((CollectionField)fields.get("isActive"), "o.isActive", query);      
         //       org contact elements
         if(fields.containsKey("contactType") && ((ArrayList)((CollectionField)fields.get("contactType")).getValue()).size()>0 &&
        		 !(((ArrayList)((CollectionField)fields.get("contactType")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("contactType")).getValue()).get(0))))
        	 QueryBuilder.setParameters((CollectionField)fields.get("contactType"), "oc.contactType", query);
         if(fields.containsKey("contactName") && ((QueryStringField)fields.get("contactName")).getComparator() != null)
        	 QueryBuilder.setParameters((QueryStringField)fields.get("contactName"), "oc.name", query);
         if(fields.containsKey("contactMultUnit") && ((QueryStringField)fields.get("contactMultUnit")).getComparator() != null)
        	 QueryBuilder.setParameters((QueryStringField)fields.get("contactMultUnit"), "oca.multipleUnit", query);
         if(fields.containsKey("contactStreetAddress") && ((QueryStringField)fields.get("contactStreetAddress")).getComparator() != null)
        	 QueryBuilder.setParameters((QueryStringField)fields.get("contactStreetAddress"), "oca.streetAddress", query);
         if(fields.containsKey("contactCity") && ((QueryStringField)fields.get("contactCity")).getComparator() != null)
        	 QueryBuilder.setParameters((QueryStringField)fields.get("contactCity"), "oca.city", query);
         if(fields.containsKey("contactState") && ((ArrayList)((CollectionField)fields.get("contactState")).getValue()).size()>0 &&
        		 !(((ArrayList)((CollectionField)fields.get("contactState")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("contactState")).getValue()).get(0))))
        	 QueryBuilder.setParameters((CollectionField)fields.get("contactState"), "oca.state", query);
         if(fields.containsKey("contactZipCode") && ((QueryStringField)fields.get("contactZipCode")).getComparator() != null)
        	 QueryBuilder.setParameters((QueryStringField)fields.get("contactZipCode"), "oca.zipCode", query);
         if(fields.containsKey("contactWorkPhone") && ((QueryStringField)fields.get("contactWorkPhone")).getComparator() != null)
        	 QueryBuilder.setParameters((QueryStringField)fields.get("contactWorkPhone"), "oca.workPhone", query);
         if(fields.containsKey("contactHomePhone") && ((QueryStringField)fields.get("contactHomePhone")).getComparator() != null)
        	 QueryBuilder.setParameters((QueryStringField)fields.get("contactHomePhone"), "oca.homePhone", query);
         if(fields.containsKey("contactCellPhone") && ((QueryStringField)fields.get("contactCellPhone")).getComparator() != null)
        	 QueryBuilder.setParameters((QueryStringField)fields.get("contactCellPhone"), "oca.cellPhone", query);
         if(fields.containsKey("contactFaxPhone") && ((QueryStringField)fields.get("contactFaxPhone")).getComparator() != null)
        	 QueryBuilder.setParameters((QueryStringField)fields.get("contactFaxPhone"), "oca.faxPhone", query);
         if(fields.containsKey("contactEmail") && ((QueryStringField)fields.get("contactEmail")).getComparator() != null)
        	 QueryBuilder.setParameters((QueryStringField)fields.get("contactEmail"), "oca.email", query);
         if(fields.containsKey("contactCountry") && ((ArrayList)((CollectionField)fields.get("contactCountry")).getValue()).size()>0 &&
        		 !(((ArrayList)((CollectionField)fields.get("contactCountry")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("contactCountry")).getValue()).get(0))))
        	 QueryBuilder.setParameters((CollectionField)fields.get("contactCountry"), "oca.country", query);
//       org notes
         if(fields.containsKey("usersSubject"))
        	 QueryBuilder.setParameters((QueryStringField)fields.get("usersSubject"), "n.subject", query);
         List returnList = GetPage.getPage(query.getResultList(), first, max);
         
         if(returnList == null)
        	 throw new LastPageException();
         else
        	 return returnList;
         //return query.getResultList();
	}
	
	public List autoCompleteLookupById(Integer id){
		Query query = null;
		query = manager.createNamedQuery("getOrganizationAutoCompleteById");
		query.setParameter("id",id);
		return query.getResultList();
	}
	
	public List autoCompleteLookupByName(String orgName, int maxResults){
		Query query = null;
		query = manager.createNamedQuery("getOrganizationAutoCompleteByName");
		query.setParameter("name",orgName);
		query.setMaxResults(maxResults);
		return query.getResultList();
	}
	
}
