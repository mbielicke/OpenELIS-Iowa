package org.openelis.bean;

import java.rmi.RemoteException;
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
import org.openelis.entity.Address;
import org.openelis.entity.Note;
import org.openelis.entity.Organization;
import org.openelis.entity.OrganizationContact;
import org.openelis.gwt.common.OptionItem;
import org.openelis.gwt.common.QueryNumberField;
import org.openelis.gwt.common.QueryOptionField;
import org.openelis.gwt.common.QueryStringField;
import org.openelis.local.LockLocal;
import org.openelis.remote.OrganizationRemote;
import org.openelis.util.Datetime;
import org.openelis.util.QueryBuilder;

import edu.uiowa.uhl.security.domain.SystemUserDO;
import edu.uiowa.uhl.security.local.SystemUserUtilLocal;

@Stateless
//@SecurityDomain("security")
//@RolesAllowed("organization-select")
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
    
    {
        try {
            InitialContext cont = new InitialContext();
            lockBean =  (LockLocal)cont.lookup("openelis/LockBean/local");
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

	public List<OrganizationTableRowDO> getOrganizationNameList(int startPos, int maxResults) {
		Query query = manager.createNamedQuery("getOrganizationNameRows");
		
		if(maxResults > 0){
			query.setFirstResult(startPos);
			query.setMaxResults(maxResults);
		}
		
		List  orgList = query.getResultList();// getting a list of organizations
		
        return orgList;

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
            
            Address orgAddress = null;
            if (organizationDO.getAddressId() == null)
            	orgAddress = new Address();
            else
            	orgAddress = manager.find(Address.class, organizationDO.getAddressId());
            
            //update organization
            if(orgAddress.getId() != null)
            	organization.setAddress(organizationDO.getAddressId());
            
            organization.setIsActive(organizationDO.getIsActive());
            organization.setName(organizationDO.getName());
            organization.setParentOrganization(organizationDO.getParentOrganization());
            //update organization address
            //orgAddress.setType(type) FIXME need to set this to business (or org)
            if(organization.getId() != null)
            	orgAddress.setReferenceId(organization.getId());
            else
            	orgAddress.setReferenceId(0);
            
            orgAddress.setReferenceTable(organizationReferenceId);
            orgAddress.setMultipleUnit(organizationDO.getMultipleUnit());
            orgAddress.setStreetAddress(organizationDO.getStreetAddress());
            orgAddress.setCity(organizationDO.getCity());
            orgAddress.setState(organizationDO.getState());
            orgAddress.setZipCode(organizationDO.getZipCode());
            orgAddress.setCountry(organizationDO.getCountry());
            
            if (orgAddress.getId() == null) {
                manager.persist(orgAddress);
                organization.setAddress(orgAddress.getId());
	            
	            if (organization.getId() == null) {
	                manager.persist(organization);
	                orgAddress.setReferenceId(organization.getId());
	            }
            }
            
            //update contacts
            for (Iterator contactsItr = contacts.iterator(); contactsItr.hasNext();) {
				OrganizationContactDO contactDO = (OrganizationContactDO) contactsItr.next();
				Address contactAddress = null;
	            OrganizationContact orgContact = null;
	            
	            if (contactDO.getId() == null)
	            	orgContact = new OrganizationContact();
	            else
	            	orgContact = manager.find(OrganizationContact.class, contactDO.getId());
	       
	            if (contactDO.getAddressId() == null)
	            	contactAddress = new Address();
	            else
	            	contactAddress = manager.find(Address.class, contactDO.getAddressId());
	            
	            if(contactDO.getDelete() && orgContact.getId() != null && contactAddress != null){
	            	//delete the contact record and the address record from the database
	            	manager.remove(orgContact);
	            	manager.remove(contactAddress);
	            	
	            }else{
		            if(orgContact.getId() != null)
		            	contactAddress.setReferenceId(orgContact.getId());
		            else
		            	contactAddress.setReferenceId(0);
		            
		            contactAddress.setReferenceTable(organizationContactReferenceId);
		            contactAddress.setType(contactDO.getType());
		            contactAddress.setMultipleUnit(contactDO.getMultipleUnit());
		            contactAddress.setStreetAddress(contactDO.getStreetAddress());
		            contactAddress.setCity(contactDO.getCity());
		            contactAddress.setState(contactDO.getState());
		            contactAddress.setZipCode(contactDO.getZipCode());
		            contactAddress.setWorkPhone(contactDO.getWorkPhone());
		            contactAddress.setHomePhone(contactDO.getHomePhone());
		            contactAddress.setCellPhone(contactDO.getCellPhone());
		            contactAddress.setFaxPhone(contactDO.getFaxPhone());
		            contactAddress.setEmail(contactDO.getEmail());
		            contactAddress.setCountry(contactDO.getCountry());          
	
		            if (contactAddress.getId() == null) {
		                manager.persist(contactAddress);
		                
		                orgContact.setContactType(contactDO.getContactType());
			            orgContact.setName(contactDO.getName());
			            orgContact.setOrganization(organization.getId());
			            orgContact.setAddress(contactAddress.getId());
			            
			            if (orgContact.getId() == null) {
			                manager.persist(orgContact);
			                contactAddress.setReferenceId(orgContact.getId());
			            }
		            }else{
		            	orgContact.setContactType(contactDO.getContactType());
			            orgContact.setName(contactDO.getName());
			            orgContact.setOrganization(organization.getId());
			            orgContact.setAddress(contactAddress.getId());
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
	
	public List getOrganizationNotes(Integer organizationId, boolean topLevel) {
		Query query = null;
		
		if(topLevel){
			query = manager.createNamedQuery("getOrganizationNotesTopLevel");
		}else{
			query = manager.createNamedQuery("getOrganizationNotesSecondLevel");
		}
		
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

	public List query(HashMap fields, int first, int max) throws RemoteException{
	
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
         if(fields.containsKey("state") && ((QueryOptionField)fields.get("state")).getSelections().size()>0 && 
        		 !(((QueryOptionField)fields.get("state")).getSelections().size() == 1 && " ".equals(((OptionItem)((QueryOptionField)fields.get("state")).getSelections().get(0)).display)))
        	 sb.append(QueryBuilder.getQuery((QueryOptionField)fields.get("state"),"o.orgAddress.state"));
         if(fields.containsKey("zipCode"))
        	 sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("zipCode"),"o.orgAddress.zipCode"));
         if(fields.containsKey("country") && ((QueryOptionField)fields.get("country")).getSelections().size()>0 && 
        		 !(((QueryOptionField)fields.get("country")).getSelections().size() == 1 && " ".equals(((OptionItem)((QueryOptionField)fields.get("country")).getSelections().get(0)).display)))
        	 sb.append(QueryBuilder.getQuery((QueryOptionField)fields.get("country"),"o.orgAddress.country"));
         if(fields.containsKey("parentOrg"))
        	 sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("parentOrg"),"o.parentOrg.name"));
         if(fields.containsKey("isActive") && ((QueryOptionField)fields.get("isActive")).getSelections().size()>0 && 
        		 !(((QueryOptionField)fields.get("isActive")).getSelections().size() == 1 && " ".equals(((OptionItem)((QueryOptionField)fields.get("isActive")).getSelections().get(0)).display)))
        	 sb.append(QueryBuilder.getQuery((QueryOptionField)fields.get("isActive"), "o.isActive"));
  
         //org contact elements
         if(fields.containsKey("contactName") && ((QueryStringField)fields.get("contactName")).getComparator() != null)
        	 sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("contactName"), "oc.name"));
         if(fields.containsKey("contactMultUnit") && ((QueryStringField)fields.get("contactMultUnit")).getComparator() != null)
         	sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("contactMultUnit"), "oca.multipleUnit"));
         if(fields.containsKey("contactStreetAddress") && ((QueryStringField)fields.get("contactStreetAddress")).getComparator() != null)
        	 sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("contactStreetAddress"), "oca.streetAddress"));
         if(fields.containsKey("contactCity") && ((QueryStringField)fields.get("contactCity")).getComparator() != null)
        	 sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("contactCity"), "oca.city"));
         if(fields.containsKey("contactState") && ((QueryOptionField)fields.get("contactState")).getSelections().size()>0 && 
        		 !(((QueryOptionField)fields.get("contactState")).getSelections().size() == 1 && " ".equals(((OptionItem)((QueryOptionField)fields.get("contactState")).getSelections().get(0)).display)))
        	 sb.append(QueryBuilder.getQuery((QueryOptionField)fields.get("contactState"), "oca.state"));
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
         if(fields.containsKey("contactCountry") && ((QueryOptionField)fields.get("contactCountry")).getSelections().size()>0 && 
        		 !(((QueryOptionField)fields.get("contactCountry")).getSelections().size() == 1 && " ".equals(((OptionItem)((QueryOptionField)fields.get("contactCountry")).getSelections().get(0)).display)))
        	 sb.append(QueryBuilder.getQuery((QueryOptionField)fields.get("contactCountry"), "oca.country"));         
         
         //org notes
         if(fields.containsKey("usersSubject"))
        	 sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("usersSubject"),"n.subject"));	
   
         Query query = manager.createQuery(sb.toString()+" order by o.name");
         
         if(first > -1)
        	 query.setFirstResult(first);
         if(max > -1)
        	 query.setMaxResults(max);
         
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
         if(fields.containsKey("state") && ((QueryOptionField)fields.get("state")).getSelections().size()>0 && 
        		 !(((QueryOptionField)fields.get("state")).getSelections().size() == 1 && " ".equals(((OptionItem)((QueryOptionField)fields.get("state")).getSelections().get(0)).display)))
        	 QueryBuilder.setParameters((QueryOptionField)fields.get("state"), "o.orgAddress.state", query);
         if(fields.containsKey("zipCode"))
        	 QueryBuilder.setParameters((QueryStringField)fields.get("zipCode"), "o.orgAddress.zipCode", query);
         if(fields.containsKey("country") && ((QueryOptionField)fields.get("country")).getSelections().size()>0 && 
        		 !(((QueryOptionField)fields.get("country")).getSelections().size() == 1 && " ".equals(((OptionItem)((QueryOptionField)fields.get("country")).getSelections().get(0)).display)))
        	QueryBuilder.setParameters((QueryOptionField)fields.get("country"), "o.orgAddress.country", query);
         if(fields.containsKey("parentOrg"))       	 
        	 QueryBuilder.setParameters((QueryStringField)fields.get("parentOrg"), "o.parentOrg.name", query);
         if(fields.containsKey("isActive") && ((QueryOptionField)fields.get("isActive")).getSelections().size()>0 && 
        		 !(((QueryOptionField)fields.get("isActive")).getSelections().size() == 1 && " ".equals(((OptionItem)((QueryOptionField)fields.get("isActive")).getSelections().get(0)).display)))
        	 QueryBuilder.setParameters((QueryOptionField)fields.get("isActive"), "o.isActive", query);
//       org contact elements
         if(fields.containsKey("contactName") && ((QueryStringField)fields.get("contactName")).getComparator() != null)
        	 QueryBuilder.setParameters((QueryStringField)fields.get("contactName"), "oc.name", query);
         if(fields.containsKey("contactMultUnit") && ((QueryStringField)fields.get("contactMultUnit")).getComparator() != null)
        	 QueryBuilder.setParameters((QueryStringField)fields.get("contactMultUnit"), "oca.multipleUnit", query);
         if(fields.containsKey("contactStreetAddress") && ((QueryStringField)fields.get("contactStreetAddress")).getComparator() != null)
        	 QueryBuilder.setParameters((QueryStringField)fields.get("contactStreetAddress"), "oca.streetAddress", query);
         if(fields.containsKey("contactCity") && ((QueryStringField)fields.get("contactCity")).getComparator() != null)
        	 QueryBuilder.setParameters((QueryStringField)fields.get("contactCity"), "oca.city", query);
         if(fields.containsKey("contactState") && ((QueryOptionField)fields.get("contactState")).getSelections().size()>0 && 
        		 !(((QueryOptionField)fields.get("contactState")).getSelections().size() == 1 && " ".equals(((OptionItem)((QueryOptionField)fields.get("contactState")).getSelections().get(0)).display)))
        	 QueryBuilder.setParameters((QueryStringField)fields.get("contactState"), "oca.state", query);
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
         if(fields.containsKey("contactCountry") && ((QueryOptionField)fields.get("contactCountry")).getSelections().size()>0 && 
        		 !(((QueryOptionField)fields.get("contactCountry")).getSelections().size() == 1 && " ".equals(((OptionItem)((QueryOptionField)fields.get("contactCountry")).getSelections().get(0)).display)))
        	 QueryBuilder.setParameters((QueryStringField)fields.get("contactCountry"), "oca.country", query);
//       org notes
         if(fields.containsKey("usersSubject"))
        	 QueryBuilder.setParameters((QueryStringField)fields.get("usersSubject"), "n.subject", query);
        
         return query.getResultList();
	}
	
	public List autoCompleteLookupById(Integer id){
		Query query = null;
		query = manager.createNamedQuery("getAutoCompleteById");
		query.setParameter("id",id);
		return query.getResultList();
	}
	
	public List autoCompleteLookupByName(String orgName, int maxResults){
		Query query = null;
		query = manager.createNamedQuery("getAutoCompleteByName");
		query.setParameter("name",orgName);
		query.setMaxResults(maxResults);
		return query.getResultList();
	}
	
}
