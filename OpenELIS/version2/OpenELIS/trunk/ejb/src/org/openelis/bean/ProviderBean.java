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
        QueryBuilder qb = new QueryBuilder();
         
       ProviderMeta providerMeta = ProviderMeta.getInstance();
       ProviderAddressMeta providerAddressMeta = ProviderAddressMeta.getInstance();
       ProviderAddressAddressMeta providerAddressAddressMeta = ProviderAddressAddressMeta.getInstance();
       ProviderNoteMeta providerNoteMeta = ProviderNoteMeta.getInstance();
       
       qb.addMeta(new Meta[]{providerMeta, providerAddressMeta, providerAddressAddressMeta, providerNoteMeta});
       
       qb.setSelect("distinct "+providerMeta.ID+", "+providerMeta.LAST_NAME+", "+providerMeta.FIRST_NAME);
       qb.addTable(providerMeta);
       
       //this method is going to throw an exception if a column doesnt match
       qb.addWhere(fields);
       //System.out.println("{"+fields+"}");
       
       qb.setOrderBy(providerMeta.LAST_NAME+", "+providerMeta.FIRST_NAME);
       
       if(qb.hasTable(providerAddressAddressMeta.getTable()))
           qb.addTable(providerAddressMeta);
          
       if(qb.hasTable(providerNoteMeta.getTable())){
           qb.addWhere(providerNoteMeta.REFERENCE_TABLE+" = "+providerReferenceId+" or "+providerNoteMeta.REFERENCE_TABLE+" is null");
          }
          
       sb.append(qb.getEJBQL());       
       System.out.println("sb "+ "{"+sb+"}" );
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
            if(providerDO.getTypeId()!=null){
                provider.setType(providerDO.getTypeId());
               }else{
                   throw new Exception("Type must be specified for a provider"); 
               }
            
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
                      addressId =  addressBean.updateAddress(provAddDO.getAddressDO());
                      
                  String location = provAddDO.getLocation();                                                    
                  
                  if(!location.trim().equals("")){ 
                    provAdd.setExternalId(provAddDO.getExternalId());
                    provAdd.setLocation(provAddDO.getLocation());
                    provAdd.setProvider(provider.getId());
                    provAdd.setAddressId(addressId);
                                                           
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

   
}
