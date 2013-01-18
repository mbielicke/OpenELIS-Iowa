package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.manager.ProviderLocationManager;
import org.openelis.manager.ProviderManager;

@Remote
public interface ProviderManagerRemote {

    public ProviderManager fetchById(Integer id) throws Exception;

    public ProviderManager fetchWithLocations(Integer id) throws Exception;

    public ProviderManager fetchWithNotes(Integer id) throws Exception;

    public ProviderManager add(ProviderManager man) throws Exception;

    public ProviderManager update(ProviderManager man) throws Exception;

    public ProviderManager fetchForUpdate(Integer id) throws Exception;

    public ProviderManager abortUpdate(Integer id) throws Exception;
    
    public ProviderLocationManager fetchLocationByProviderId(Integer id) throws Exception;

}
