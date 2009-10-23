package org.openelis.manager;

import org.openelis.gwt.services.ScreenService;

public class ProviderManagerProxy {


    protected static final String PROVIDER_MANAGER_SERVICE_URL = "org.openelis.modules.provider.server.ProviderService";
    protected ScreenService       service;

    public ProviderManagerProxy() {
        service = new ScreenService("controller?service=" + PROVIDER_MANAGER_SERVICE_URL);
    }

    public ProviderManager fetchById(Integer id) throws Exception {
        return service.call("fetchById", id);
    }

    public ProviderManager fetchWithAddresses(Integer id) throws Exception {
        return service.call("fetchWithAddresses", id);
    }

    public ProviderManager fetchWithNotes(Integer id) throws Exception {
        return service.call("fetchWithNotes", id);
    }

    public ProviderManager add(ProviderManager man) throws Exception {
        return service.call("add", man);
    }

    public ProviderManager update(ProviderManager man) throws Exception {
        return service.call("update", man);
    }

    public ProviderManager fetchForUpdate(Integer id) throws Exception {
        return service.call("fetchForUpdate", id);
    }

    public ProviderManager abortUpdate(Integer id) throws Exception {
        return service.call("abortUpdate", id);
    }

    public void validate(ProviderManager man) throws Exception {
    }
}
