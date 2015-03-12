package org.openelis.manager;

import org.openelis.modules.provider.client.ProviderServiceImpl;

public class ProviderLocationManagerProxy {
    
    public ProviderLocationManagerProxy() {
    }

    public ProviderLocationManager fetchByProviderId(Integer orgId) throws Exception {
        return ProviderServiceImpl.INSTANCE.fetchLocationByProviderId(orgId);
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
