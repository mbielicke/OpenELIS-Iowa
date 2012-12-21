package org.openelis.modules.report.client;

import java.util.ArrayList;

import org.openelis.gwt.common.Prompt;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.ScreenDef;
import org.openelis.gwt.services.ScreenService;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class SampleInhouseReportScreen extends ReportScreen {

    public SampleInhouseReportScreen() throws Exception { 
        drawScreen(new ScreenDef());        
        setName(consts.get("sampleInhouseReport"));
        service = new ScreenService("controller?service=org.openelis.modules.report.server.SampleInhouseReportService");
    }

    @Override
    protected ArrayList<Prompt> getPrompts() throws Exception {
        return SampleInHouseReportService.get().getPrompts();
    }

    @Override
    public void runReport(RPC query, AsyncCallback<ReportStatus> callback) {
        SampleInHouseReportService.get().runReport((Query)query, callback);
        
    }
}