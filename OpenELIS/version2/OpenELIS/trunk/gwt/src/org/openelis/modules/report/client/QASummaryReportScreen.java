package org.openelis.modules.report.client;

import java.util.ArrayList;

import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.constants.Messages;
import org.openelis.gwt.screen.ScreenDef;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class QASummaryReportScreen extends ReportScreen<Query> {

    public QASummaryReportScreen(WindowInt window) throws Exception {
        setWindow(window);
        drawScreen(new ScreenDef());        
        setName(Messages.get().QASummaryReport());
    }

    @Override
    protected ArrayList<Prompt> getPrompts() throws Exception {
        return QASummaryReportService.get().getPrompts();
    }

    @Override
    public void runReport(Query query, AsyncCallback<ReportStatus> callback) {
        QASummaryReportService.get().runReport((Query)query, callback);
    }
}