package org.openelis.modules.report.client;

import java.util.ArrayList;

import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.constants.Messages;
import org.openelis.gwt.screen.ScreenDef;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class TurnaroundReportScreen extends ReportScreen<Query> {

    public TurnaroundReportScreen(WindowInt window) throws Exception {
        setWindow(window);
        drawScreen(new ScreenDef());        
        setName(Messages.get().turnaround());
    }

    @Override
    protected ArrayList<Prompt> getPrompts() throws Exception {
        return TurnaroundReportService.get().getPrompts();
    }

    @Override
    public void runReport(Query query, AsyncCallback<ReportStatus> callback) {
        TurnaroundReportService.get().runReport(query, callback);
    }
    
    
}