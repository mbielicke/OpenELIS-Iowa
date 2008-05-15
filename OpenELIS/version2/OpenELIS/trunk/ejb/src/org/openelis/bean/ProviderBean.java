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

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.NoteDO;
import org.openelis.domain.ProviderAddressDO;
import org.openelis.domain.ProviderDO;
import org.openelis.entity.Note;
import org.openelis.entity.Provider;
import org.openelis.entity.ProviderAddress;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.local.LockLocal;
import org.openelis.meta.ProviderAddressAddressMeta;
import org.openelis.meta.ProviderAddressMeta;
import org.openelis.meta.ProviderMeta;
import org.openelis.meta.ProviderNoteMeta;
import org.openelis.remote.AddressLocal;
import org.openelis.remote.ProviderRemote;
import org.openelis.util.Datetime;
import org.openelis.util.Meta;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

import edu.uiowa.uhl.security.domain.SystemUserDO;
import edu.uiowa.uhl.security.local.SystemUserUtilLocal;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("provider-select")
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


    public List getProviderNotes(Integer providerId) {
       Query query = null;
        
       query = manager.createNamedQuery("getProviderNotes");
       query.setParameter("id", providerId);
        
       List provNotes = query.getResultList(); // getting list of noteDOs from the provider id

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
        QueryBuilder qb = new QueryBuilder();
         
       ProviderMeta providerMeta = ProviderMeta.getInstance();
       ProviderAddressMeta providerAddressMeta = ProviderAddressMeta.getInstance();
       ProviderAddressAddressMeta providerAddressAddressMeta = ProviderAddressAddressMeta.getInstance();
       ProviderNoteMeta providerNoteMeta = ProviderNoteMeta.getInstance();
       
       qb.addMeta(new Meta[]{providerMeta, providerAddressMeta, providerAddressAddressMeta, providerNoteMeta});
       
       qb.setSelect("distinct new org.openelis.domain.IdLastNameFirstNameDO("+ProviderMeta.ID+", "+ProviderMeta.LAST_NAME+", "+ProviderMeta.FIRST_NAME + ") ");
       qb.addTable(providerMeta);
       
       //this method is going to throw an exception if a column doesnt match
       qb.addWhere(fields);   
       
       qb.setOrderBy(ProviderMeta.LAST_NAME+", "+ProviderMeta.FIRST_NAME);
       
       if(qb.hasTable(providerAddressAddressMeta.getTable()))
           qb.addTable(providerAddressMeta);
          
       if(qb.hasTable(providerNoteMeta.getTable())){
           qb.addWhere(ProviderNoteMeta.REFERENCE_TABLE+" = "+providerReferenceId+" or "+ProviderNoteMeta.REFERENCE_TABLE+" is null");
          }
          
       sb.append(qb.getEJBQL());       
       Query query = manager.createQuery(sb.toString());
       
       if(first > -1 && max > -1)
         query.setMaxResults(first+max);
       
      //***set the parameters in the query
       qb.setQueryParams(query);
       
       List returnList = GetPage.getPage(query.getResultList(), first, max);
         if(returnList == null)
        	 throw new LastPageException();
         else
        	 return returnList;
        
    }

    @RolesAllowed("provider-update")
    public Integer updateProvider(ProviderDO providerDO, NoteDO noteDO,List addresses) throws Exception{
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "provider");
        Integer providerReferenceId = (Integer)query.getSingleResult();
        
        if(providerDO.getId() != null){
            //we need to call lock one more time to make sure their lock didnt expire and someone else grabbed the record
            lockBean.getLock(providerReferenceId, providerDO.getId());
        }
        
        manager.setFlushMode(FlushModeType.COMMIT);
        Provider provider = null;
            
            if (providerDO.getId() == null){
                provider = new Provider();
            } 
            else{
                provider = manager.find(Provider.class, providerDO.getId());
            }
            
            List<Exception> exceptionList = new ArrayList<Exception>();
            validateProvider(providerDO,exceptionList);  
            if(exceptionList.size() > 0){
                throw (RPCException)exceptionList.get(0);
            }
            
            ArrayList<ProviderAddressDO> updateList = null;
            ArrayList<ProviderAddressDO> deleteList = null;
            int index = 0 ;
            for (Iterator iter = addresses.iterator(); iter.hasNext();) {
                ProviderAddressDO provAddDO = (ProviderAddressDO)iter.next();                
                boolean checkForUpdate =  false;
                
                exceptionList = new ArrayList<Exception>();
                validateProviderAddress(provAddDO,index,exceptionList);
                if(exceptionList.size() > 0){
                    throw (RPCException)exceptionList.get(0);
                }
                                
                  if(new Boolean(true).equals(provAddDO.getDelete())){
                     if(deleteList == null){
                         deleteList = new ArrayList<ProviderAddressDO>();
                     } 
                      deleteList.add(provAddDO);
                    
                  }else{   
                      checkForUpdate = true;
                  } 
                                             
              
              if(checkForUpdate){   
                 if(updateList==null){
                     updateList =  new ArrayList<ProviderAddressDO>();
                 } 
                  updateList.add(provAddDO);                                                         
                } 
              index++;
            }
            
            provider.setFirstName(providerDO.getFirstName());
            provider.setLastName(providerDO.getLastName());
            provider.setMiddleName(providerDO.getMiddleName());
            provider.setNpi(providerDO.getNpi());
            provider.setType(providerDO.getTypeId());            
            
            if (provider.getId() == null) {
                manager.persist(provider);
            }
                        
           if(deleteList!=null){
            for (Iterator iter = deleteList.iterator(); iter.hasNext();){
                ProviderAddressDO provAddDO = (ProviderAddressDO)iter.next();
                ProviderAddress provAdd = null;                
                                                                
                if(provAddDO.getId() != null){                    
                    //delete the contact record and the address record from the database 
                    provAdd = manager.find(ProviderAddress.class, provAddDO.getId());
                     manager.remove(provAdd);                     
                     addressBean.deleteAddress(provAddDO.getAddressDO());
                     }
            }
           } 
           
          if(updateList!=null){  
            for (Iterator iter = updateList.iterator(); iter.hasNext();) {
                ProviderAddressDO provAddDO = (ProviderAddressDO)iter.next();
                Integer addressId = addressBean.updateAddress(provAddDO.getAddressDO());
                ProviderAddress provAdd = null;
                
                if (provAddDO.getId() == null){
                    provAdd = new ProviderAddress();
                } 
                else{
                    provAdd = manager.find(ProviderAddress.class, provAddDO.getId());
                }
                
                provAdd.setExternalId(provAddDO.getExternalId());
                provAdd.setLocation(provAddDO.getLocation());
                provAdd.setProvider(provider.getId());
                provAdd.setAddressId(addressId);
                                                       
                if(provAdd.getId()==null){
                   manager.persist(provAdd);                          
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
            
        return provider.getId();
    }

    public List<Object[]> getProviderTypes() {
        Query query = manager.createNamedQuery("getProviderTypes");        
        return query.getResultList();
    }

    @RolesAllowed("provider-update")
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

   private List validateProvider(ProviderDO providerDO,List<Exception> exceptionList){
       if("".equals(providerDO.getLastName())){           
           exceptionList.add(new FieldErrorException("fieldRequiredException",ProviderMeta.LAST_NAME));
          }
       if(providerDO.getTypeId()==null){           
           exceptionList.add(new FieldErrorException("fieldRequiredException",ProviderMeta.TYPE));
          }
       return exceptionList;
    }
   
   private List validateProviderAddress(ProviderAddressDO provAddDO,int rowIndex,List<Exception> exceptionList){
       String location = provAddDO.getLocation();  
       String city = provAddDO.getAddressDO().getCity();
       String state = provAddDO.getAddressDO().getState();
       String zipcode = provAddDO.getAddressDO().getZipCode();      
       if(location == null || "".equals(location)){            
           exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, ProviderAddressMeta.LOCATION));
         }
       if(state == null || "".equals(state)){            
           exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, ProviderAddressAddressMeta.STATE));
         }
       if(city == null || "".equals(city)){            
           exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex,ProviderAddressAddressMeta.CITY));
         }
       if(zipcode == null || "".equals(zipcode)){            
           exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex,ProviderAddressAddressMeta.ZIP_CODE));
         }
       
       return  exceptionList;
      }

     public List validateForAdd(ProviderDO providerDO, List<ProviderAddressDO> addresses) {
         List<Exception> exceptionList = new ArrayList<Exception>();
         validateProvider(providerDO,exceptionList);
         for(int iter = 0; iter < addresses.size(); iter++){
             ProviderAddressDO provAddDO = (ProviderAddressDO)addresses.get(iter);
             validateProviderAddress(provAddDO,iter, exceptionList);
         }
         return exceptionList;
     }

    public List validateForUpdate(ProviderDO providerDO, List<ProviderAddressDO> addresses) {
        List<Exception> exceptionList = new ArrayList<Exception>();
        validateProvider(providerDO,exceptionList);
       
        for(int iter = 0; iter < addresses.size(); iter++){
            ProviderAddressDO provAddDO = (ProviderAddressDO)addresses.get(iter);
            validateProviderAddress(provAddDO,iter, exceptionList);
        }
        return exceptionList;
     } 
   
}
