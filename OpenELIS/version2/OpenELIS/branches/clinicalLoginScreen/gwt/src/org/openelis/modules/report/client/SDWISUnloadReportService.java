package org.openelis.modules.report.client;

import java.util.ArrayList;

import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.services.TokenService;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

public class SDWISUnloadReportService implements SDWISUnloadReportServiceInt,
                                     SDWISUnloadReportServiceIntAsync {

    static SDWISUnloadReportService instance;
    
    SDWISUnloadReportServiceIntAsync service;
    
    public static SDWISUnloadReportService get() {
        if(instance == null)
            instance = new SDWISUnloadReportService();
        
        return instance;
    }
    
    private SDWISUnloadReportService() {
        service = (SDWISUnloadReportServiceIntAsync)GWT.create(SDWISUnloadReportServiceInt.class);
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }

    @Override
    public void getPrompts(AsyncCallback<ArrayList<Prompt>> callback) {
        service.getPrompts(callback);
    }

    @Override
    public void runReport(Query query, AsyncCallback<ReportStatus> callback) {
        service.runReport(query, callback);
    }

    @Override
    public ArrayList<Prompt> getPrompts() throws Exception {
        Callback<ArrayList<Prompt>> callback;
        
        callback = new Callback<ArrayList<Prompt>>();
        service.getPrompts(callback);
        return callback.getResult();
    }

    @Override
    public ReportStatus runReport(Query query) throws Exception {
        Callback<ReportStatus> callback;
        
        callback = new Callback<ReportStatus>();
        service.runReport(query, callback);
        return callback.getResult();
    }

}
