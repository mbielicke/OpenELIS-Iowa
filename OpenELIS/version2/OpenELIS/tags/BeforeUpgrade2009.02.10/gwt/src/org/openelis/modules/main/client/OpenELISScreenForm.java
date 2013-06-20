/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.modules.main.client;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.data.Data;
import org.openelis.gwt.common.data.DataModel;
import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.screen.AppScreenForm;
import org.openelis.modules.main.client.service.OpenELISServiceInt;
import org.openelis.modules.main.client.service.OpenELISServiceIntAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public class OpenELISScreenForm<ScreenRPC extends RPC<Display,Data>,Display extends Form> extends AppScreenForm<ScreenRPC,DataModel<DataSet>,Display> {
    
    public OpenELISServiceIntAsync<ScreenRPC,DataModel<DataSet>> screenService = (OpenELISServiceIntAsync<ScreenRPC,DataModel<DataSet>>)GWT.create(OpenELISServiceInt.class);
    public ServiceDefTarget target = (ServiceDefTarget)screenService;
    
         
    public OpenELISScreenForm(String serviceClass, boolean withData,ScreenRPC rpc){
        super();          
        target.setServiceEntryPoint(target.getServiceEntryPoint()+"?service="+serviceClass);
        service = screenService;
        formService = screenService;
     
        if(withData)
            getXMLData(rpc);
        else{
            getXML(rpc);
        }
        
    }
    
    public OpenELISScreenForm(String serviceClass){
        super();              
        target.setServiceEntryPoint(target.getServiceEntryPoint()+"?service="+serviceClass);
        service = screenService;
        formService = screenService;
    }
    
}