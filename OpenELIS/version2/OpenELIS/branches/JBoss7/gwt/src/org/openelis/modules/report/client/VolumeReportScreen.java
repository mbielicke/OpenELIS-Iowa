package org.openelis.modules.report.client;

import java.util.ArrayList;

import org.openelis.gwt.common.Prompt;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.ScreenDef;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class VolumeReportScreen extends ReportScreen {

    public VolumeReportScreen() throws Exception { 
        drawScreen(new ScreenDef());        
        setName(consts.get("volumeReport"));
    }

    @Override
    protected ArrayList<Prompt> getPrompts() throws Exception {
        return VolumeReportService.get().getPrompts();
    }

    @Override
    public void runReport(RPC rpc, AsyncCallback<ReportStatus> callback) {
        VolumeReportService.get().runReport((Query)rpc, callback);
    }
}