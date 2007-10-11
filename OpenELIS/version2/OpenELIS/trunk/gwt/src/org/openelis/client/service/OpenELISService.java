package org.openelis.client.service;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public class OpenELISService {
    private static OpenELISServiceIntAsync ttService = (OpenELISServiceIntAsync)GWT.create(OpenELISServiceInt.class);

    private static ServiceDefTarget target = (ServiceDefTarget)ttService;

    public static void init() {
        String url = GWT.getModuleBaseURL();
        url += "/OpenELISService";
        target.setServiceEntryPoint(url);
    }

    public static OpenELISServiceIntAsync getInstance() {
        return ttService;
    }
}
