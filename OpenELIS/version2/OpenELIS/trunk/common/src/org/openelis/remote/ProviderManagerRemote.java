package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.manager.ProviderAddressManager;
import org.openelis.manager.ProviderManager;

@Remote
public interface ProviderManagerRemote {

    public ProviderManager fetchById(Integer id) throws Exception;

    public ProviderManager fetchWithAddresses(Integer id) throws Exception;

    public ProviderManager fetchWithNotes(Integer id) throws Exception;

    public ProviderManager add(ProviderManager man) throws Exception;

    public ProviderManager update(ProviderManager man) throws Exception;

    public ProviderManager fetchForUpdate(Integer id) throws Exception;

    public ProviderManager abortUpdate(Integer id) throws Exception;
    
    public ProviderAddressManager fetchAddressByProviderId(Integer id) throws Exception;

}
