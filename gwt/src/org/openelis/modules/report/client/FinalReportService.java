package org.openelis.modules.report.client;

import java.util.ArrayList;

import org.openelis.domain.FinalReportWebVO;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.Prompt;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

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
    }

    @Override
    public void getEnvironmentalProjectList(AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.getEnvironmentalProjectList(callback);
    }

    @Override
    public void getPrivateWellProjectList(AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.getPrivateWellProjectList(callback);
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
    public void getPromptsForSingle(AsyncCallback<ArrayList<Prompt>> callback) {
        service.getPromptsForSingle(callback);
    }

    @Override
    public void getSDWISProjectList(AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.getSDWISProjectList(callback);
    }

    @Override
    public void getSampleEnvironmentalList(Query query,
                                           AsyncCallback<ArrayList<FinalReportWebVO>> callback) {
        service.getSampleEnvironmentalList(query, callback);
    }

    @Override
    public void getSamplePrivateWellList(Query query,
                                         AsyncCallback<ArrayList<FinalReportWebVO>> callback) {
        service.getSamplePrivateWellList(query, callback);
    }

    @Override
    public void getSampleSDWISList(Query query, AsyncCallback<ArrayList<FinalReportWebVO>> callback) {
        service.getSampleSDWISList(query, callback);
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
    public ArrayList<Prompt> getPromptsForSingle() throws Exception {
        Callback<ArrayList<Prompt>> callback;
        
        callback = new Callback<ArrayList<Prompt>>();
        service.getPromptsForSingle(callback);
        return callback.getResult();
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

    @Override
    public ArrayList<FinalReportWebVO> getSampleEnvironmentalList(Query query) throws Exception {
        Callback<ArrayList<FinalReportWebVO>> callback;
        
        callback = new Callback<ArrayList<FinalReportWebVO>>();
        service.getSampleEnvironmentalList(query, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<FinalReportWebVO> getSamplePrivateWellList(Query query) throws Exception {
        Callback<ArrayList<FinalReportWebVO>> callback;
        
        callback = new Callback<ArrayList<FinalReportWebVO>>();
        service.getSamplePrivateWellList(query, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<FinalReportWebVO> getSampleSDWISList(Query query) throws Exception {
        Callback<ArrayList<FinalReportWebVO>> callback;
        
        callback = new Callback<ArrayList<FinalReportWebVO>>();
        service.getSampleSDWISList(query, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<IdNameVO> getEnvironmentalProjectList() throws Exception {
        Callback<ArrayList<IdNameVO>> callback;
        
        callback = new Callback<ArrayList<IdNameVO>>();
        service.getEnvironmentalProjectList(callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<IdNameVO> getPrivateWellProjectList() throws Exception {
        Callback<ArrayList<IdNameVO>> callback;
        
        callback = new Callback<ArrayList<IdNameVO>>();
        service.getPrivateWellProjectList(callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<IdNameVO> getSDWISProjectList() throws Exception {
        Callback<ArrayList<IdNameVO>> callback;
        
        callback = new Callback<ArrayList<IdNameVO>>();
        service.getSDWISProjectList(callback);
        return callback.getResult();
    }

}
