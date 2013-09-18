package org.openelis.modules.report.client;

import org.openelis.gwt.screen.ScreenDef;
import org.openelis.gwt.services.ScreenService;

public class SampleInhouseReportScreen extends ReportScreen {

    public SampleInhouseReportScreen() throws Exception { 
        drawScreen(new ScreenDef());        
        setName(consts.get("sampleInhouseReport"));
        service = new ScreenService("controller?service=org.openelis.modules.report.server.SampleInhouseReportService");
    }
}