package org.openelis.manager;

import org.openelis.modules.provider.client.ProviderService;

public class ProviderManagerProxy {

    public ProviderManagerProxy() {
    }

    public ProviderManager fetchById(Integer id) throws Exception {
        return ProviderService.get().fetchById(id);
    }

    public ProviderManager fetchWithLocations(Integer id) throws Exception {
        return ProviderService.get().fetchWithLocations(id);
    }

    public ProviderManager fetchWithNotes(Integer id) throws Exception {
        return ProviderService.get().fetchWithNotes(id);
    }

    public ProviderManager add(ProviderManager man) throws Exception {
        return ProviderService.get().add(man);
    }

    public ProviderManager update(ProviderManager man) throws Exception {
        return ProviderService.get().update(man);
    }

    public ProviderManager fetchForUpdate(Integer id) throws Exception {
        return ProviderService.get().fetchForUpdate(id);
    }

    public ProviderManager abortUpdate(Integer id) throws Exception {
        return ProviderService.get().abortUpdate(id);
    }

    public void validate(ProviderManager man) throws Exception {
    }
}
