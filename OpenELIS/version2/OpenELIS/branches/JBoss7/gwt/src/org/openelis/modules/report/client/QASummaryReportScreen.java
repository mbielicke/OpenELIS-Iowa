package org.openelis.modules.report.client;

import java.util.ArrayList;

import org.openelis.gwt.common.Prompt;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.ScreenDef;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class QASummaryReportScreen extends ReportScreen {

    public QASummaryReportScreen() throws Exception { 
        drawScreen(new ScreenDef());        
        setName(consts.get("QAByOrganization"));
    }

    @Override
    protected ArrayList<Prompt> getPrompts() throws Exception {
        return QASummaryReportService.get().getPrompts();
    }

    @Override
    public void runReport(RPC query, AsyncCallback<ReportStatus> callback) {
        QASummaryReportService.get().runReport((Query)query, callback);
    }
}