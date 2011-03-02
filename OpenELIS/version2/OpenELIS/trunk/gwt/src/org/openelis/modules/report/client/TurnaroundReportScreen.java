package org.openelis.modules.report.client;

import org.openelis.gwt.screen.ScreenDef;
import org.openelis.gwt.services.ScreenService;

public class TurnaroundReportScreen extends ReportScreen {

    public TurnaroundReportScreen() throws Exception { 
        drawScreen(new ScreenDef());        
        setName(consts.get("turnaround"));
        service = new ScreenService("controller?service=org.openelis.modules.report.server.TurnaroundReportService");
    }
}