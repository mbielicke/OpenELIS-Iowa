package org.openelis.modules.report.client;

import java.util.ArrayList;

import org.openelis.gwt.common.Prompt;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SampleLoginLabelReportService implements SampleLoginLabelReportServiceInt,
                                          SampleLoginLabelReportServiceIntAsync {

    static SampleLoginLabelReportService instance;
    
    SampleLoginLabelReportServiceIntAsync service;
    
    public static SampleLoginLabelReportService get() {
        if(instance == null)
            instance = new SampleLoginLabelReportService();
        
        return instance;
    }
    
    private SampleLoginLabelReportService() {
        service = (SampleLoginLabelReportServiceIntAsync)GWT.create(SampleLoginLabelReportServiceInt.class);
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

    @Override
    public void getAdditionalPrompts(AsyncCallback<ArrayList<Prompt>> callback) {
        service.getAdditionalPrompts(callback);
    }

    @Override
    public void runAdditionalReport(Query query, AsyncCallback<ReportStatus> callback) {
        service.runAdditionalReport(query, callback);
    }

    @Override
    public ArrayList<Prompt> getAdditionalPrompts() throws Exception {
        Callback<ArrayList<Prompt>> callback;
        
        callback = new Callback<ArrayList<Prompt>>();
        service.getAdditionalPrompts(callback);
        return callback.getResult();
    }

    @Override
    public ReportStatus runAdditionalReport(Query query) throws Exception {
        Callback<ReportStatus> callback;
        
        callback = new Callback<ReportStatus>();
        service.runAdditionalReport(query, callback);
        return callback.getResult();
    }

}
