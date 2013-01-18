package org.openelis.modules.report.client;

import org.openelis.gwt.screen.ScreenDef;
import org.openelis.gwt.services.ScreenService;

public class QASummaryReportScreen extends ReportScreen {

    public QASummaryReportScreen() throws Exception { 
        drawScreen(new ScreenDef());        
        setName(consts.get("QASummaryReport"));
        service = new ScreenService("controller?service=org.openelis.modules.report.server.QASummaryReportService");
    }
}