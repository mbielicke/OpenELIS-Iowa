package org.openelis.modules.report.client;

import java.util.ArrayList;

import org.openelis.gwt.common.Prompt;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.ScreenDef;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class TurnaroundReportScreen extends ReportScreen {

    public TurnaroundReportScreen() throws Exception { 
        drawScreen(new ScreenDef());        
        setName(consts.get("turnaround"));
    }

    @Override
    protected ArrayList<Prompt> getPrompts() throws Exception {
        return TurnaroundReportService.get().getPrompts();
    }

    @Override
    public void runReport(RPC query, AsyncCallback<ReportStatus> callback) {
        TurnaroundReportService.get().runReport((Query)query, callback);
    }
    
    
}