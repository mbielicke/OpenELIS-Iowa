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
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.ProviderAddressDO;
import org.openelis.domain.ProviderDO;
import org.openelis.entity.Note;
import org.openelis.entity.Provider;
import org.openelis.entity.ProviderAddress;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.TableFieldErrorException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.SecurityModule.ModuleFlags;
import org.openelis.gwt.common.data.deprecated.AbstractField;
import org.openelis.local.AddressLocal;
import org.openelis.local.LockLocal;
import org.openelis.local.ProviderLocal;
import org.openelis.metamap.ProviderMetaMap;
import org.openelis.remote.ProviderRemote;
import org.openelis.security.domain.SystemUserDO;
import org.openelis.security.local.SystemUserLocal;
import org.openelis.util.QueryBuilder;
import org.openelis.utils.GetPage;
import org.openelis.utils.ReferenceTableCache;
import org.openelis.utils.SecurityInterceptor;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("provider-select")
public class ProviderBean implements ProviderRemote, ProviderLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager manager;
    
    @Resource
    private SessionContext ctx;
    
    @EJB
    private SystemUserLocal userLocal;
    
    @EJB
    private LockLocal lockBean;
    
    @EJB
    private AddressLocal addressBean;
    
    private static final ProviderMetaMap ProvMeta = new ProviderMetaMap(); 
    
    private static Integer providerRefTableId;
    
    public ProviderBean() {
        providerRefTableId = ReferenceTableCache.getReferenceTable("provider");
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
       Query query = manager.createNamedQuery("Note.Notes");
       query.setParameter("referenceTable", providerRefTableId);
       query.setParameter("id", providerId);
        
       List provNotes = query.getResultList(); // getting list of noteDOs from the provider id
       
       for(int i=0; i<provNotes.size(); i++){
           NoteViewDO noteDO = (NoteViewDO)provNotes.get(i);
           SystemUserDO userDO = userLocal.getSystemUser(noteDO.getSystemUserId());
           noteDO.setSystemUser(userDO.getLoginName());
       }
       
       return provNotes;
    }

    public List query(ArrayList<AbstractField> fields, int first, int max) throws Exception {                         
        
       StringBuffer sb = new StringBuffer();
       QueryBuilder qb = new QueryBuilder();
                
       qb.setMeta(ProvMeta);
        
       qb.setSelect("distinct new org.openelis.domain.IdLastNameFirstNameDO("+ProvMeta.getId()+", "+ProvMeta.getLastName()+", "+ProvMeta.getFirstName() + ") ");
       
       
       //this method is going to throw an exception if a column doesnt match
       qb.addWhere(fields);   
       
       qb.setOrderBy(ProvMeta.getLastName()+", "+ProvMeta.getFirstName());       
          
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
    public Integer updateProvider(ProviderDO providerDO, NoteViewDO noteDO,List addresses) throws Exception{
        SecurityInterceptor.applySecurity(ctx.getCallerPrincipal().getName(), "provider", ModuleFlags.UPDATE);
        Integer providerId = providerDO.getId();
        
        if(providerId != null){
            //we need to call lock one more time to make sure their lock didnt expire and someone else grabbed the record
            lockBean.validateLock(providerRefTableId,providerId);
        }
        
        validateProvider(providerDO,addresses);
        
        manager.setFlushMode(FlushModeType.COMMIT);
        Provider provider = null;
        ProviderAddress provAdd = null;
            
        if (providerId == null){
            provider = new Provider();
        } 
        else{
            provider = manager.find(Provider.class, providerDO.getId());
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
                                                
                if (provAddDO.getId() == null){
                    provAdd = new ProviderAddress();
                } else{
                    provAdd = manager.find(ProviderAddress.class, provAddDO.getId());
                }
                     
                //this is commented out because the DO changed
                //when the screen is rewritten this will be fixed
      //          if(provAddDO.getDelete() && provAddDO.getId() != null){
      //              manager.remove(provAdd);                     
      //              addressBean.delete(provAddDO.getAddressDO());                    
      //          }else if(!provAddDO.getDelete()){   
                    if(provAddDO.getAddressDO() == null)
                        addressBean.add(provAddDO.getAddressDO());
                    else
                        addressBean.update(provAddDO.getAddressDO());
                                                                                     
                    provAdd.setExternalId(provAddDO.getExternalId());
                    provAdd.setLocation(provAddDO.getLocation());
                    provAdd.setProviderId(provider.getId());
                    provAdd.setAddressId(provAddDO.getAddressDO().getId());
                    
                    if(provAdd.getId()==null){
                        manager.persist(provAdd);                          
                    }
                } 
                index++;
   //         }
            
            //update note
            Note note = null;
            //we need to make sure the note is filled out...
            if(noteDO.getText() != null || noteDO.getSubject() != null){
                note = new Note();                
                note.setIsExternal(noteDO.getIsExternal());
                note.setReferenceId(provider.getId());
                note.setReferenceTableId(providerRefTableId);
                note.setSubject(noteDO.getSubject());
                note.setSystemUserId(lockBean.getSystemUserId());
                note.setText(noteDO.getText());
                note.setTimestamp(Datetime.getInstance());                 
            }
            
            //insert into note table if necessary
            if(note != null && note.getId() == null){
                manager.persist(note);                              
            }
        }catch(Exception ex){
            ex.printStackTrace();
            throw ex;
        } 
        lockBean.giveUpLock(providerRefTableId,provider.getId()); 
            
        return provider.getId();
    }

    @RolesAllowed("provider-update")
    public ProviderDO getProviderAndLock(Integer providerId, String session) throws Exception{
        SecurityInterceptor.applySecurity(ctx.getCallerPrincipal().getName(), "provider", ModuleFlags.UPDATE);
        lockBean.getLock(providerRefTableId,providerId);         
        return getProvider(providerId);
    }

    public ProviderDO getProviderAndUnlock(Integer providerId, String session) {        
        lockBean.giveUpLock(providerRefTableId,providerId);
        return getProvider(providerId);
    }

   private void validateProvider(ProviderDO providerDO,ValidationErrorsList exceptionList){            
       if("".equals(providerDO.getLastName())){           
           exceptionList.add(new FieldErrorException("fieldRequiredException",ProvMeta.getLastName()));
       }
       if(providerDO.getTypeId()==null){           
           exceptionList.add(new FieldErrorException("fieldRequiredException",ProvMeta.getTypeId()));
       }      
   }
   
   private void validateProviderAddress(ProviderAddressDO provAddDO,int rowIndex,ValidationErrorsList exceptionList){
       String location = provAddDO.getLocation();  
       String city = provAddDO.getAddressDO().getCity();
       String state = provAddDO.getAddressDO().getState();
       String zipcode = provAddDO.getAddressDO().getZipCode();
       String country = provAddDO.getAddressDO().getCountry();
   //    if(!provAddDO.getDelete()) {
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
        
        if(country == null || "".equals(country)){            
            exceptionList.add(new TableFieldErrorException("fieldRequiredException", rowIndex,ProvMeta.getProviderAddress().getAddress().getCountry()));
         }
     //  }
   }
    
    private void validateProvider(ProviderDO providerDO, List<ProviderAddressDO> addresses) throws Exception{
        ValidationErrorsList exceptionList = new ValidationErrorsList();
        validateProvider(providerDO,exceptionList);
       
        for(int iter = 0; iter < addresses.size(); iter++){
            ProviderAddressDO provAddDO = (ProviderAddressDO)addresses.get(iter);
            validateProviderAddress(provAddDO,iter, exceptionList);
        }

        if(exceptionList.size() > 0) 
            throw exceptionList;
     }
   
}
