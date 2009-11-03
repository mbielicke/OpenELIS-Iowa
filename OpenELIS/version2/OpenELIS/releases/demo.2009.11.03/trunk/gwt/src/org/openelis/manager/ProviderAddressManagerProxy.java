package org.openelis.manager;

import org.openelis.gwt.services.ScreenService;

public class ProviderAddressManagerProxy {
    
	protected static final String PROVIDER_MANAGER_SERVICE_URL = "org.openelis.modules.provider.server.ProviderService";
    protected ScreenService       service;

    public ProviderAddressManagerProxy() {
        service = new ScreenService("controller?service=" + PROVIDER_MANAGER_SERVICE_URL);
    }

    public ProviderAddressManager fetchByProviderId(Integer orgId) throws Exception {
        return service.call("fetchAddressByProviderId", orgId);
    }

    public ProviderAddressManager add(ProviderAddressManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    public ProviderAddressManager update(ProviderAddressManager man) throws Exception {
        assert false : "not supported";
        return null;
    }
    
    public void validate(ProviderAddressManager man) throws Exception {
    }
}
