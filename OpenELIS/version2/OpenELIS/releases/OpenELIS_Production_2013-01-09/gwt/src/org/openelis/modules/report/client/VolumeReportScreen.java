package org.openelis.modules.report.client;

import org.openelis.gwt.screen.ScreenDef;
import org.openelis.gwt.services.ScreenService;

public class VolumeReportScreen extends ReportScreen {

    public VolumeReportScreen() throws Exception { 
        drawScreen(new ScreenDef());        
        setName(consts.get("volumeReport"));
        service = new ScreenService("controller?service=org.openelis.modules.report.server.VolumeReportService");
    }
}