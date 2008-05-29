package org.openelis.modules.main.client;

import org.openelis.gwt.screen.AppScreenForm;
import org.openelis.modules.main.client.service.OpenELISServiceInt;
import org.openelis.modules.main.client.service.OpenELISServiceIntAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public class OpenELISScreenForm extends AppScreenForm {
    
    public OpenELISServiceIntAsync screenService = (OpenELISServiceIntAsync) GWT
    .create(OpenELISServiceInt.class);
    protected ServiceDefTarget target = (ServiceDefTarget) screenService; 
         
    public OpenELISScreenForm(String serviceClass, boolean withData){
        super();          
        String base = GWT.getModuleBaseURL();
        base += "OpenELISServlet?service="+serviceClass;        
        target.setServiceEntryPoint(base);
        service = screenService;
        formService = screenService;
        
        String nullString = null;
        if(withData)
            getXMLData();
        else{
            getXML();
        }
        
    }
    
    public OpenELISScreenForm(String serviceClass){
        super();          
        String base = GWT.getModuleBaseURL();
        base += "OpenELISServlet?service="+serviceClass;        
        target.setServiceEntryPoint(base);
        service = screenService;
        formService = screenService;       
    }
}
