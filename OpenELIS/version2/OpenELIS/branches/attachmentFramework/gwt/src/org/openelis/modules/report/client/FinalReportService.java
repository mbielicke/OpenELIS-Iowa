package org.openelis.modules.report.client;

import java.util.ArrayList;

import org.openelis.gwt.screen.Callback;
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.services.TokenService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

public class FinalReportService implements FinalReportServiceInt, FinalReportServiceIntAsync {
    
    static FinalReportService instance;
    
    FinalReportServiceIntAsync service;
    
    public static FinalReportService get() {
        if(instance == null)
            instance = new FinalReportService();
        
        return instance;
    }
    
    private FinalReportService() {
        service = (FinalReportServiceIntAsync)GWT.create(FinalReportServiceInt.class);
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }

    @Override
    public void getPromptsForBatch(AsyncCallback<ArrayList<Prompt>> callback) {
        service.getPromptsForBatch(callback);
    }

    @Override
    public void getPromptsForBatchReprint(AsyncCallback<ArrayList<Prompt>> callback) {
        service.getPromptsForBatchReprint(callback);
    }

    @Override
    public void runReportForBatch(Query query, AsyncCallback<ReportStatus> callback) {
        service.runReportForBatch(query, callback);
    }

    @Override
    public void runReportForBatchReprint(Query query, AsyncCallback<ReportStatus> callback) {
        service.runReportForBatchReprint(query, callback);
    }

    @Override
    public void runReportForPreview(Query query, AsyncCallback<ReportStatus> callback) {
        service.runReportForPreview(query, callback);
    }

    @Override
    public void runReportForSingle(Query query, AsyncCallback<ReportStatus> callback) {
        service.runReportForSingle(query, callback);
    }

    @Override
    public void runReportForWeb(Query query, AsyncCallback<ReportStatus> callback) {
        service.runReportForWeb(query, callback);
    }

    @Override
    public ArrayList<Prompt> getPromptsForBatch() throws Exception {
        Callback<ArrayList<Prompt>> callback;
        
        callback = new Callback<ArrayList<Prompt>>();
        service.getPromptsForBatch(callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<Prompt> getPromptsForBatchReprint() throws Exception {
        Callback<ArrayList<Prompt>> callback;
        
        callback = new Callback<ArrayList<Prompt>>();
        service.getPromptsForBatchReprint(callback);
        return callback.getResult();
    }

    @Override
    public ReportStatus runReportForSingle(Query query) throws Exception {
        Callback<ReportStatus> callback;
        
        callback = new Callback<ReportStatus>();
        service.runReportForSingle(query, callback);
        return callback.getResult();
    }

    @Override
    public ReportStatus runReportForPreview(Query query) throws Exception {
        Callback<ReportStatus> callback;
        
        callback = new Callback<ReportStatus>();
        service.runReportForPreview(query, callback);
        return callback.getResult();
    }

    @Override
    public ReportStatus runReportForBatch(Query query) throws Exception {
        Callback<ReportStatus> callback;
        
        callback = new Callback<ReportStatus>();
        service.runReportForBatch(query, callback);
        return callback.getResult();
    }

    @Override
    public ReportStatus runReportForBatchReprint(Query query) throws Exception {
        Callback<ReportStatus> callback;
        
        callback = new Callback<ReportStatus>();
        service.runReportForBatchReprint(query, callback);
        return callback.getResult();
    }

    @Override
    public ReportStatus runReportForWeb(Query query) throws Exception {
        Callback<ReportStatus> callback;
        
        callback = new Callback<ReportStatus>();
        service.runReportForWeb(query, callback);
        return callback.getResult();
    }

}
