package org.openelis.remote;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

import org.openelis.domain.NoteDO;
import org.openelis.domain.ProviderDO;

@Remote
public interface ProviderRemote {
    
    //  method to return list of provider ids and provider names by what letter they start with
    public List getProviderNameListByLetter(String letter, int startPos, int maxResults);
    
    //  method to return org name, address
    public ProviderDO getProvider(Integer providerId, boolean unlock);
    
    //update initial call for provider
    public ProviderDO getProviderUpdate(Integer id) throws Exception;
    
    //commit a change to org, or insert a new provider
    public Integer updateProvider(ProviderDO providerDO, NoteDO noteDO, List addresses);
    
    //method to return just notes
    public List getProviderNotes(Integer providerId, boolean topLevel);
    
    //method to return just provider addresses
    public List getProviderAddresses(Integer providerId);
    
     //method to query for provider
     public List query(HashMap fields, int first, int max) throws RemoteException;
     
     //a way for the servlet to get the system user id
     public Integer getSystemUserId();
     
     //method to get all the dicntionary entries for providers
     public List<Object[]> getProviderTypes();
}
