package org.openelis.manager;

import org.openelis.modules.provider.client.ProviderService;
import org.openelis.modules.provider.client.ProviderServiceImpl;

public class ProviderManagerProxy {

    public ProviderManagerProxy() {
    }

    public ProviderManager fetchById(Integer id) throws Exception {
        return ProviderServiceImpl.INSTANCE.fetchById(id);
    }

    public ProviderManager fetchWithLocations(Integer id) throws Exception {
        return ProviderServiceImpl.INSTANCE.fetchWithLocations(id);
    }

    public ProviderManager fetchWithNotes(Integer id) throws Exception {
        return ProviderServiceImpl.INSTANCE.fetchWithNotes(id);
    }

    public ProviderManager add(ProviderManager man) throws Exception {
        return ProviderServiceImpl.INSTANCE.add(man);
    }

    public ProviderManager update(ProviderManager man) throws Exception {
        return ProviderServiceImpl.INSTANCE.update(man);
    }

    public ProviderManager fetchForUpdate(Integer id) throws Exception {
        return ProviderServiceImpl.INSTANCE.fetchForUpdate(id);
    }

    public ProviderManager abortUpdate(Integer id) throws Exception {
        return ProviderServiceImpl.INSTANCE.abortUpdate(id);
    }

    public void validate(ProviderManager man) throws Exception {
    }
}
