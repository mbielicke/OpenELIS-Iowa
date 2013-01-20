package org.openelis.manager;

import org.openelis.modules.provider.client.ProviderService;

public class ProviderLocationManagerProxy {
    
    public ProviderLocationManagerProxy() {
    }

    public ProviderLocationManager fetchByProviderId(Integer orgId) throws Exception {
        return ProviderService.get().fetchLocationByProviderId(orgId);
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
