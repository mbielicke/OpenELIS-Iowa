package org.openelis.modules.report.client;

import org.openelis.gwt.screen.ScreenDef;
import org.openelis.gwt.services.ScreenService;

public class ClientNotificationReleasedReportScreen extends ReportScreen {

    public ClientNotificationReleasedReportScreen() throws Exception { 
        drawScreen(new ScreenDef());        
        setName(consts.get("organizationRelRef"));
        service = new ScreenService("controller?service=org.openelis.modules.report.server.ClientNotificationReleasedReportService");
    }
}