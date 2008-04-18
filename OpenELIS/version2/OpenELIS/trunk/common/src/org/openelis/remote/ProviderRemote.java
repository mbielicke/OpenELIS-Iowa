package org.openelis.remote;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

import org.openelis.domain.NoteDO;
import org.openelis.domain.ProviderAddressDO;
import org.openelis.domain.ProviderDO;

@Remote
public interface ProviderRemote {
    
    //  method to return provider 
    public ProviderDO getProvider(Integer providerId);
        
    public ProviderDO getProviderAndUnlock(Integer providerId);
    
    public ProviderDO getProviderAndLock(Integer providerId)throws Exception;
    
    //commit a change to provider, or insert a new provider
    public Integer updateProvider(ProviderDO providerDO, NoteDO noteDO, List addresses) throws Exception;
    
    //method to return just notes
    public List getProviderNotes(Integer providerId, boolean topLevel);
    
    //method to return just provider addresses
    public List getProviderAddresses(Integer providerId);
    
     //method to query for provider
     public List query(HashMap fields, int first, int max) throws Exception;
     
     //a way for the servlet to get the system user id
     public Integer getSystemUserId();
     
     //method to get all the dicntionary entries for providers
     public List<Object[]> getProviderTypes();
     
     public List getProviderNotes(Integer providerId);    
     
     //method to validate the fields before the backend updates it in the database
     public List validateForUpdate(ProviderDO providerDO, List<ProviderAddressDO> addresses);
     
     //method to validate the fields before the backend updates it in the database
     public List validateForAdd(ProviderDO providerDO, List<ProviderAddressDO> addresses);
}
