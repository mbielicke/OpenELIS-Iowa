package org.openelis.manager;

import org.openelis.gwt.services.ScreenService;

public class ProviderLocationManagerProxy {
    
	protected static final String SERVICE_URL = "org.openelis.modules.provider.server.ProviderService";
    protected ScreenService       service;

    public ProviderLocationManagerProxy() {
        service = new ScreenService("controller?service=" + SERVICE_URL);
    }

    public ProviderLocationManager fetchByProviderId(Integer orgId) throws Exception {
        return service.call("fetchLocationByProviderId", orgId);
    }

    public ProviderLocationManager add(ProviderLocationManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    public ProviderLocationManager update(ProviderLocationManager man) throws Exception {
        assert false : "not supported";
        return null;
    }
    
    public void validate(ProviderLocationManager man) throws Exception {
    }
}
