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
package org.openelis.modules.report.client;

import org.openelis.gwt.screen.ScreenDef;
import org.openelis.gwt.services.ScreenService;

import com.google.gwt.user.client.Window;

public class OrderRecurrenceReportScreen extends ReportScreen {
    
    public OrderRecurrenceReportScreen() throws Exception { 
        drawScreen(new ScreenDef());      
        setRunReportInterface("recurOrders");   
        setName(consts.get("orderRecurrence"));
        service = new ScreenService("controller?service=org.openelis.modules.order.server.OrderService");
    }
    
    protected void runReport() {
        window.setBusy(consts.get("genReportMessage"));
        try {
            service.call(runReportInterface);
            window.setDone(consts.get("recurredOrders"));
        } catch (Exception e) {
            window.setError("Failed");
            Window.alert(e.getMessage());
        }
    }
}

