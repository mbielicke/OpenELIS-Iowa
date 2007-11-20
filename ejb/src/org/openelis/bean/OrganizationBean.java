package org.openelis.bean;

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
import org.openelis.local.LockLocal;
import org.openelis.remote.OrganizationRemote;
import org.openelis.util.Datetime;

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
            //organization.setParentOrganization(organizationDO.getParentOrganization());
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
            
            
            
//          update note
            Note note = null;
            //we need to make sure the note is filled out...
            System.out.println("before the note if...");
            System.out.println("text:["+noteDO.getText()+"]   subject:["+noteDO.getSubject()+"]");
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

	private Integer getSystemUserId(){
	   // log.debug(ctx.getCallerPrincipal().getName()+" in LockBean "+ctx.toString());
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
	
}
