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
import org.openelis.domain.ProviderAddressDO;
import org.openelis.domain.ProviderDO;
import org.openelis.entity.Note;
import org.openelis.entity.Provider;
import org.openelis.entity.ProviderAddress;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.RPCException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.local.AddressLocal;
import org.openelis.local.LockLocal;
import org.openelis.metamap.ProviderMetaMap;
import org.openelis.remote.ProviderRemote;
import org.openelis.util.Datetime;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
@EJBs({
    @EJB(name="ejb/Lock",beanInterface=LockLocal.class),
    @EJB(name="ejb/Address",beanInterface=AddressLocal.class)
})
@SecurityDomain("openelis")
@RolesAllowed("provider-select")
public class ProviderBean implements ProviderRemote {

    @PersistenceContext(name = "openelis")
    private EntityManager manager;
    
    @Resource
    private SessionContext ctx;
    
    private LockLocal lockBean;
    private AddressLocal addressBean;
    
    private static final ProviderMetaMap ProvMeta = new ProviderMetaMap(); 
    
    @PostConstruct
    private void init()
    {
        lockBean =  (LockLocal)ctx.lookup("ejb/Lock");
        addressBean =  (AddressLocal)ctx.lookup("ejb/Address");
    }
    
    public ProviderDO getProvider(Integer providerId) {                 
        
        Query query = manager.createNamedQuery("Provider.Provider");
        query.setParameter("id", providerId);
        ProviderDO provider = (ProviderDO) query.getSingleResult();// getting provider 

        return provider;
    }

    public List getProviderAddresses(Integer providerId) {
        Query query = manager.createNamedQuery("Provider.Addresses");
        query.setParameter("id", providerId);
        
        List providerAddresses = query.getResultList();// getting list of addresses from the 
    
        return providerAddresses;
    }


    public List getProviderNotes(Integer providerId) {
       Query query = null;
        
       query = manager.createNamedQuery("Provider.Notes");
       query.setParameter("id", providerId);
        
       List provNotes = query.getResultList(); // getting list of noteDOs from the provider id

       return provNotes;
    }

    public List query(HashMap fields, int first, int max) throws Exception {
        
        Query refIdQuery = manager.createNamedQuery("getTableId");
        refIdQuery.setParameter("name", "provider");
        //Integer providerReferenceId = (Integer)refIdQuery.getSingleResult();
               
        
        StringBuffer sb = new StringBuffer();
        QueryBuilder qb = new QueryBuilder();
         
       //ProviderMeta providerMeta = ProviderMeta.getInstance();
       //ProviderAddressMeta providerAddressMeta = ProviderAddressMeta.getInstance();
       //ProviderAddressAddressMeta providerAddressAddressMeta = ProviderAddressAddressMeta.getInstance();
       //ProviderNoteMeta providerNoteMeta = ProviderNoteMeta.getInstance();
       
       //qb.addMeta(new Meta[]{providerMeta, providerAddressMeta, providerAddressAddressMeta, providerNoteMeta});
       qb.setMeta(ProvMeta);
        
       qb.setSelect("distinct new org.openelis.domain.IdLastNameFirstNameDO("+ProvMeta.getId()+", "+ProvMeta.getLastName()+", "+ProvMeta.getFirstName() + ") ");
       //qb.addTable(providerMeta);
       
       
       //this method is going to throw an exception if a column doesnt match
       qb.addWhere(fields);   
       
       qb.setOrderBy(ProvMeta.getLastName()+", "+ProvMeta.getFirstName());
       
      // if(qb.hasTable(providerAddressAddressMeta.getTable()))
       //    qb.addTable(providerAddressMeta);
          
       //if(qb.hasTable(providerNoteMeta.getTable())){
       //    qb.addWhere(ProviderNoteMeta.REFERENCE_TABLE+" = "+providerReferenceId+" or "+ProviderNoteMeta.REFERENCE_TABLE+" is null");
       //   }
          
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
        ProviderAddress provAdd = null;
            
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
            
            provider.setFirstName(providerDO.getFirstName());
            provider.setLastName(providerDO.getLastName());
            provider.setMiddleName(providerDO.getMiddleName());
            provider.setNpi(providerDO.getNpi());
            provider.setTypeId(providerDO.getTypeId());
            
            if (provider.getId() == null) {
                manager.persist(provider);
            }
            
           try{
            int index = 0 ;
            for (Iterator iter = addresses.iterator(); iter.hasNext();) {
                ProviderAddressDO provAddDO = (ProviderAddressDO)iter.next();                
                                
                exceptionList = new ArrayList<Exception>();
                validateProviderAddress(provAddDO,index,exceptionList);
                if(exceptionList.size() > 0){
                    throw (RPCException)exceptionList.get(0);
                }
                
                if (provAddDO.getId() == null){
                    provAdd = new ProviderAddress();
                } 
                else{
                    provAdd = manager.find(ProviderAddress.class, provAddDO.getId());
                }
                                
                  if(provAddDO.getDelete() && provAddDO.getId() != null){
                      manager.remove(provAdd);                     
                      addressBean.deleteAddress(provAddDO.getAddressDO());
                    
                  }else{   
                      Integer addressId = addressBean.updateAddress(provAddDO.getAddressDO());                                                                                      
                      provAdd.setExternalId(provAddDO.getExternalId());
                      provAdd.setLocation(provAddDO.getLocation());
                      provAdd.setProviderId(provider.getId());
                      provAdd.setAddressId(addressId);
                                                             
                      if(provAdd.getId()==null){
                         manager.persist(provAdd);                          
                      }
                  } 
              index++;
            }
            
            //update note
            Note note = null;
            //we need to make sure the note is filled out...
            if(noteDO.getText() != null || noteDO.getSubject() != null){
                note = new Note();                
                note.setIsExternal(noteDO.getIsExternal());
                note.setReferenceId(provider.getId());
                note.setReferenceTableId(providerReferenceId);
                note.setSubject(noteDO.getSubject());
                note.setSystemUserId(lockBean.getSystemUserId());
                note.setText(noteDO.getText());
                note.setTimestamp(Datetime.getInstance());                 
            }
            
            //insert into note table if necessary
            if(note != null){
                if(note.getId() == null){
                manager.persist(note);                 
             }
            }
           }catch(Exception ex){
               ex.printStackTrace();
               throw ex;
           } 
        lockBean.giveUpLock(providerReferenceId,provider.getId()); 
            
        return provider.getId();
    }

    @RolesAllowed("provider-update")
    public ProviderDO getProviderAndLock(Integer providerId, String session) throws Exception{
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "provider");
        lockBean.getLock((Integer)query.getSingleResult(),providerId);         
        return getProvider(providerId);
    }

    public ProviderDO getProviderAndUnlock(Integer providerId, String session) {
        Query query = manager.createNamedQuery("getTableId");
        query.setParameter("name", "provider");
        lockBean.giveUpLock((Integer)query.getSingleResult(),providerId);
        return getProvider(providerId);
    }

   private List validateProvider(ProviderDO providerDO,List<Exception> exceptionList){            
       if("".equals(providerDO.getLastName())){           
           exceptionList.add(new FieldErrorException("fieldRequiredException",ProvMeta.getLastName()));
          }
       if(providerDO.getTypeId()==null){           
           exceptionList.add(new FieldErrorException("fieldRequiredException",ProvMeta.getTypeId()));
          }
       return exceptionList;
    }
   
   private List validateProviderAddress(ProviderAddressDO provAddDO,int rowIndex,List<Exception> exceptionList){
       String location = provAddDO.getLocation();  
       String city = provAddDO.getAddressDO().getCity();
       String state = provAddDO.getAddressDO().getState();
       String zipcode = provAddDO.getAddressDO().getZipCode();      
       if(!provAddDO.getDelete()) {
        if(location == null || "".equals(location)){            
           exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, ProvMeta.getProviderAddress().getLocation()));
         }
        if(state == null || "".equals(state)){            
           exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex, ProvMeta.getProviderAddress().getAddress().getState()));
         }
        if(city == null || "".equals(city)){            
           exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex,ProvMeta.getProviderAddress().getAddress().getCity()));
         }
        if(zipcode == null || "".equals(zipcode)){            
           exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex,ProvMeta.getProviderAddress().getAddress().getZipCode()));
         }
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
