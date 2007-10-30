package org.openelis.client.main.service;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

import org.openelis.gwt.client.services.AppServiceInt;
import org.openelis.gwt.client.services.AppServiceIntAsync;

public class OpenELISService {
    private static OpenELISServiceIntAsync OpenElisService = (OpenELISServiceIntAsync)GWT.create(OpenELISServiceInt.class);
    private static AppServiceIntAsync AppElisService = (AppServiceIntAsync)GWT.create(AppServiceInt.class);
    
    private static ServiceDefTarget targetOpen = (ServiceDefTarget)OpenElisService;
    private static ServiceDefTarget targetApp = (ServiceDefTarget)AppElisService;

    public static void init() {
        String url = GWT.getModuleBaseURL();
        url += "/OpenELISService";
        targetOpen.setServiceEntryPoint(url);
        targetApp.setServiceEntryPoint(url);
    }

    public static OpenELISServiceIntAsync getInstance() {
        return OpenElisService;
    }
    
    public static AppServiceIntAsync getAppServInstance() {
        return AppElisService;
    }
}
