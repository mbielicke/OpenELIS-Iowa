/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) OpenELIS.  All Rights Reserved.
*/
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
