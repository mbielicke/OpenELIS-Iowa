package org.openelis.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.openelis.domain.AddressDO;
import org.openelis.domain.NoteDO;
import org.openelis.domain.ProviderAddressDO;
import org.openelis.domain.ProviderDO;
import org.openelis.entity.Note;
import org.openelis.entity.Provider;
import org.openelis.entity.ProviderAddress;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.data.CollectionField;
import org.openelis.gwt.common.data.QueryNumberField;
import org.openelis.gwt.common.data.QueryStringField;
import org.openelis.local.LockLocal;
import org.openelis.remote.AddressLocal;
import org.openelis.remote.ProviderRemote;
import org.openelis.util.Datetime;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

import edu.uiowa.uhl.security.domain.SystemUserDO;
import edu.uiowa.uhl.security.local.SystemUserUtilLocal;

@Stateless
public class ProviderBean implements ProviderRemote {

    @PersistenceContext(name = "openelis")
    private EntityManager manager;
    
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
            e.printStackTrace();
        }
    }
    
    public ProviderDO getProvider(Integer providerId) {                 
        
        Query query = manager.createNamedQuery("getProvider");
        query.setParameter("id", providerId);
        ProviderDO provider = (ProviderDO) query.getSingleResult();// getting provider 

        return provider;
    }

    public List getProviderAddresses(Integer providerId) {
        Query query = manager.createNamedQuery("getProviderAddresses");
        query.setParameter("id", providerId);
        
        List providerAddresses = query.getResultList();// getting list of addresses from the 
    
        return providerAddresses;
    }


    public List getProviderNotes(Integer providerId, boolean topLevel) {
       Query query = null;
        
        if(topLevel){
            query = manager.createNamedQuery("getProviderNotesTopLevel");
        }else{
            query = manager.createNamedQuery("getProviderNotesSecondLevel");
        }
        
        query.setParameter("id", providerId);
        
        List provNotes = query.getResultList();// getting list of notes from the provider id

        return provNotes;
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

    public List query(HashMap fields, int first, int max) throws Exception {
        
        Query refIdQuery = manager.createNamedQuery("getTableId");
        refIdQuery.setParameter("name", "provider");
        Integer providerReferenceId = (Integer)refIdQuery.getSingleResult();
               
        
        StringBuffer sb = new StringBuffer();
        sb.append("select distinct p.id, p.lastName, p.firstName from Provider p left join p.provNote n left join p.providerAddress pa left join pa.provAddress a where " +
                " (n.referenceTable = "+providerReferenceId+" or n.referenceTable is null) "
                );
        if(fields.containsKey("providerId"))
            sb.append(QueryBuilder.getQuery((QueryNumberField)fields.get("providerId"), "p.id"));
        if(fields.containsKey("lastName"))
         sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("lastName"), "p.lastName"));
        if(fields.containsKey("firstName"))
         sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("firstName"), "p.firstName"));
        if(fields.containsKey("npi"))
         sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("npi"), "p.npi"));
        if(fields.containsKey("middleName"))
         sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("middleName"), "p.middleName"));
        if(fields.containsKey("providerType")&& ((ArrayList)((CollectionField)fields.get("providerType")).getValue()).size()>0 && 
                        !(((ArrayList)((CollectionField)fields.get("providerType")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("providerType")).getValue()).get(0))))
            sb.append(QueryBuilder.getQuery((CollectionField)fields.get("providerType"), "p.type"));
        
         if(fields.containsKey("location") && ((QueryStringField)fields.get("location")).getComparator() != null)
                 sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("location"), "pa.location"));
         if(fields.containsKey("externalId") && ((QueryStringField)fields.get("externalId")).getComparator() != null)
             sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("externalId"), "pa.externalId"));
           if(fields.containsKey("multiUnit") && ((QueryStringField)fields.get("multiUnit")).getComparator() != null)
                    sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("multiUnit"), "a.multipleUnit"));
                if(fields.containsKey("streetAddress") && ((QueryStringField)fields.get("streetAddress")).getComparator() != null)
                 sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("streetAddress"), "a.streetAddress"));
                if(fields.containsKey("city") && ((QueryStringField)fields.get("city")).getComparator() != null)
                 sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("city"), "a.city"));
                if(fields.containsKey("state") && ((ArrayList)((CollectionField)fields.get("state")).getValue()).size()>0 &&
                   !(((ArrayList)((CollectionField)fields.get("state")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("state")).getValue()).get(0))))
                    sb.append(QueryBuilder.getQuery((CollectionField)fields.get("state"), "a.state"));                                   

                if(fields.containsKey("country") && ((ArrayList)((CollectionField)fields.get("country")).getValue()).size()>0 &&
                      !(((ArrayList)((CollectionField)fields.get("country")).getValue()).size()== 1 && "".equals(((ArrayList)((CollectionField)fields.get("country")).getValue()).get(0))))
                    sb.append(QueryBuilder.getQuery((CollectionField)fields.get("country"), "a.country"));                                     
 
                if(fields.containsKey("zipCode") && ((QueryStringField)fields.get("zipCode")).getComparator() != null)
                 sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("zipCode"), "a.zipCode"));
                if(fields.containsKey("workPhone") && ((QueryStringField)fields.get("workPhone")).getComparator() != null)
                 sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("workPhone"), "a.workPhone"));
                if(fields.containsKey("homePhone") && ((QueryStringField)fields.get("homePhone")).getComparator() != null)
                 sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("homePhone"), "a.homePhone"));
                if(fields.containsKey("cellPhone") && ((QueryStringField)fields.get("cellPhone")).getComparator() != null)
                 sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("cellPhone"), "a.cellPhone"));
                if(fields.containsKey("faxPhone") && ((QueryStringField)fields.get("faxPhone")).getComparator() != null)
                 sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("faxPhone"), "a.faxPhone"));
                if(fields.containsKey("email") && ((QueryStringField)fields.get("email")).getComparator() != null)
                 sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("email"), "a.email"));
                if(fields.containsKey("usersSubject"))
                 sb.append(QueryBuilder.getQuery((QueryStringField)fields.get("usersSubject"),"n.subject"));
                                
                
        Query query = manager.createQuery(sb.toString()+" order by p.lastName, p.firstName ");
        
//      if(first > -1)
    	// query.setFirstResult(first);

        if(first > -1 && max > -1)
        	query.setMaxResults(first+max);
        
        if(fields.containsKey("providerId"))
            QueryBuilder.setParameters((QueryNumberField)fields.get("providerId"), "p.id",query);
        if(fields.containsKey("lastName"))
            QueryBuilder.setParameters((QueryStringField)fields.get("lastName"), "p.lastName",query);
        if(fields.containsKey("firstName"))
            QueryBuilder.setParameters((QueryStringField)fields.get("firstName"), "p.firstName",query);
        if(fields.containsKey("npi"))
            QueryBuilder.setParameters((QueryStringField)fields.get("npi"), "p.npi",query);
        if(fields.containsKey("middleName"))
            QueryBuilder.setParameters((QueryStringField)fields.get("middleName"), "p.middleName",query);
        if(fields.containsKey("providerType")&& ((ArrayList)((CollectionField)fields.get("providerType")).getValue()).size()>0 &&
                     !(((ArrayList)((CollectionField)fields.get("providerType")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("providerType")).getValue()).get(0))))
            QueryBuilder.setParameters((CollectionField)fields.get("providerType"), "p.type",query);
        
        if(fields.containsKey("location")&& ((QueryStringField)fields.get("location")).getComparator() != null)
           QueryBuilder.setParameters((QueryStringField)fields.get("location"), "pa.location",query);
        
        if(fields.containsKey("externalId")&&  ((QueryStringField)fields.get("externalId")).getComparator() != null) 
           QueryBuilder.setParameters((QueryStringField)fields.get("externalId"), "pa.externalId",query);
    
         if(fields.containsKey("multiUnit") && ((QueryStringField)fields.get("multiUnit")).getComparator() != null)
            QueryBuilder.setParameters((QueryStringField)fields.get("multiUnit"), "a.multipleUnit",query);
         
         if(fields.containsKey("streetAddress") && ((QueryStringField)fields.get("streetAddress")).getComparator() != null)
             QueryBuilder.setParameters((QueryStringField)fields.get("streetAddress"), "a.streetAddress",query);
         
         if(fields.containsKey("city") && ((QueryStringField)fields.get("city")).getComparator() != null)
             QueryBuilder.setParameters((QueryStringField)fields.get("city"), "a.city",query);
         
         if(fields.containsKey("state")&& ((ArrayList)((CollectionField)fields.get("state")).getValue()).size()>0 &&
                         !(((ArrayList)((CollectionField)fields.get("state")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("state")).getValue()).get(0))))
             QueryBuilder.setParameters((CollectionField)fields.get("state"), "a.state",query);
         
         if(fields.containsKey("country") && ((ArrayList)((CollectionField)fields.get("country")).getValue()).size()>0 &&
                         !(((ArrayList)((CollectionField)fields.get("country")).getValue()).size() == 1 && "".equals(((ArrayList)((CollectionField)fields.get("country")).getValue()).get(0))))
             QueryBuilder.setParameters((CollectionField)fields.get("country"), "a.country",query);
         
         if(fields.containsKey("zipCode") && ((QueryStringField)fields.get("zipCode")).getComparator() != null)
             QueryBuilder.setParameters((QueryStringField)fields.get("zipCode"), "a.zipCode",query);
         
         if(fields.containsKey("workPhone") && ((QueryStringField)fields.get("workPhone")).getComparator() != null)
             QueryBuilder.setParameters((QueryStringField)fields.get("workPhone"), "a.workPhone",query);
         
         if(fields.containsKey("homePhone") && ((QueryStringField)fields.get("homePhone")).getComparator() != null) 
             QueryBuilder.setParameters((QueryStringField)fields.get("homePhone"), "a.homePhone",query);
         
         if(fields.containsKey("cellPhone") && ((QueryStringField)fields.get("cellPhone")).getComparator() != null)
             QueryBuilder.setParameters((QueryStringField)fields.get("cellPhone"), "a.cellPhone",query);
         
         if(fields.containsKey("faxPhone")&& ((QueryStringField)fields.get("faxPhone")).getComparator() != null)
             QueryBuilder.setParameters((QueryStringField)fields.get("faxPhone"), "a.faxPhone",query);
         
         if(fields.containsKey("email") && ((QueryStringField)fields.get("email")).getComparator() != null)
             QueryBuilder.setParameters((QueryStringField)fields.get("email"), "a.email",query);
                
         
         if(fields.containsKey("usersSubject"))
             QueryBuilder.setParameters((QueryStringField)fields.get("usersSubject"), "n.subject", query);
         
        
         List returnList = GetPage.getPage(query.getResultList(), first, max);
         if(returnList == null)
        	 throw new LastPageException();
         else
        	 return returnList;
         //return query.getResultList();
        
    }

    public Integer updateProvider(ProviderDO providerDO, NoteDO noteDO,List addresses) throws Exception{
        manager.setFlushMode(FlushModeType.COMMIT);
        Provider provider = null;
        try{
            
            Query query = manager.createNamedQuery("getTableId");
            query.setParameter("name", "provider");
            Integer providerReferenceId = (Integer)query.getSingleResult();
            
            if (providerDO.getId() == null){
                provider = new Provider();
            } 
            else{
                provider = manager.find(Provider.class, providerDO.getId());
            }
            
            provider.setFirstName(providerDO.getFirstName());
            provider.setLastName(providerDO.getLastName());
            provider.setMiddleName(providerDO.getMiddleName());
            provider.setNpi(providerDO.getNpi());
            provider.setType(providerDO.getTypeId());
            
            if (provider.getId() == null) {
                manager.persist(provider);
            }
            
            for (Iterator iter = addresses.iterator(); iter.hasNext();) {
                ProviderAddressDO provAddDO = (ProviderAddressDO)iter.next();
                ProviderAddress provAdd = null;
                boolean checkForUpdate =  false;
                
                if (provAddDO.getId() == null){
                    provAdd = new ProviderAddress();
                } 
                else{
                    provAdd = manager.find(ProviderAddress.class, provAddDO.getId());
                }
                if(provAddDO.getDelete()!=null){ 
                  if(provAddDO.getDelete()){
                    if(provAdd.getId() != null){
                  
                    //delete the contact record and the address record from the database                    
                     manager.remove(provAdd);                     
                     addressBean.deleteAddress(provAddDO.getAddressDO());
                     }
                  }else{   
                      checkForUpdate = true;
                  } 
                }else{ 
                    checkForUpdate = true; 
                                   
                }
                
               
              
              if(checkForUpdate){
                  Integer addressId = null;                                   
                  
                  if(updateAddress(provAddDO.getAddressDO())){ 
                      addressId =  addressBean.updateAddress(provAddDO.getAddressDO());
                  }
                  
                  String location = provAddDO.getLocation();                                  
                  System.out.println("location "+ location);
                  
                  if(!location.trim().equals("")){ 
                    provAdd.setExternalId(provAddDO.getExternalId());
                    provAdd.setLocation(provAddDO.getLocation());
                    provAdd.setProvider(provider.getId());
                    provAdd.setAddress(addressId);
                                                           
                    if(provAdd.getId()==null){
                       manager.persist(provAdd);                          
                    }
                  }else{
                      throw new Exception("Location must be specified for each address");
                  }     
                } 
              
            }
                             
            //update note
            Note note = null;
            //we need to make sure the note is filled out...
            if(!("".equals(noteDO.getText())) ||   !("".equals(noteDO.getSubject()))){
                note = new Note();
                note.setIsExternal(noteDO.getIsExternal());
                note.setReferenceId(provider.getId());
                note.setReferenceTable(providerReferenceId);
                note.setSubject(noteDO.getSubject());
                note.setSystemUser(getSystemUserId());
                note.setText(noteDO.getText());
                note.setTimestamp(Datetime.getInstance());
            }
            
            //insert into note table if necessary
            if(note != null){
                if(note.getId() == null){
                manager.persist(note);               
             }
            }
            lockBean.giveUpLock(providerReferenceId,provider.getId()); 
            
        }catch(Exception ex){
           ex.printStackTrace();
           throw ex;
        }
        
        return provider.getId();
    }

    public List<Object[]> getProviderTypes() {
        Query query = manager.createNamedQuery("getProviderTypes");        
        return query.getResultList();
    }

    public List getProviderNotes(Integer providerId) {
       Query query = null;
       query = manager.createNamedQuery("getProviderNotesTopLevel");
       
       query.setParameter("id", providerId);
        
       List provNotes = query.getResultList();// getting list of notes from the provider id

       return provNotes;
    }



    public ProviderDO getProviderAndLock(Integer providerId) throws Exception{
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "provider");
        lockBean.getLock((Integer)query.getSingleResult(),providerId);         
        return getProvider(providerId);
    }

    public ProviderDO getProviderAndUnlock(Integer providerId) {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "provider");
        lockBean.giveUpLock((Integer)query.getSingleResult(),providerId);
        return getProvider(providerId);
    }

    private boolean updateAddress(AddressDO addressDO){
        int numFieldsBlank = 0; 
        if(addressDO.getCity().trim().equals("")){
            numFieldsBlank++; 
        }
        if(addressDO.getCellPhone().trim().equals("")){
            numFieldsBlank++; 
        }
        if(addressDO.getCountry().trim().equals("")){
            numFieldsBlank++; 
        }
        if(addressDO.getEmail().trim().equals("")){
            numFieldsBlank++; 
        }
        if(addressDO.getFaxPhone().trim().equals("")){
            numFieldsBlank++; 
        }
        if(addressDO.getHomePhone().trim().equals("")){
            numFieldsBlank++; 
        }
        if(addressDO.getMultipleUnit().trim().equals("")){
            numFieldsBlank++; 
        }
        if(addressDO.getState().trim().equals("")){
            numFieldsBlank++; 
        }
        if(addressDO.getStreetAddress().trim().equals("")){
            numFieldsBlank++; 
        }
        if(addressDO.getWorkPhone().trim().equals("")){
            numFieldsBlank++; 
        }
        if(addressDO.getZipCode().trim().equals("")){
            numFieldsBlank++; 
        }
        
        if(numFieldsBlank < 11){
            return true;
        }
       return false; 
    }

}
