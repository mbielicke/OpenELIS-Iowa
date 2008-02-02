package org.openelis.modules.main.client.service;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

import org.openelis.gwt.client.services.AppServiceInt;
import org.openelis.gwt.client.services.AppServiceIntAsync;

public class OpenELISService {
    private static OpenELISServiceIntAsync OpenElisService = (OpenELISServiceIntAsync)GWT.create(OpenELISServiceInt.class);
    
    private static ServiceDefTarget targetOpen = (ServiceDefTarget)OpenElisService;

    public static void init() {
        String url = GWT.getModuleBaseURL();
        url += "/OpenELISService";
        targetOpen.setServiceEntryPoint(url);
    }

    public static OpenELISServiceIntAsync getInstance() {
        return OpenElisService;
    }
}
