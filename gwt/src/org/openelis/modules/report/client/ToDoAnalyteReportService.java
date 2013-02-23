package org.openelis.modules.report.client;

import java.util.ArrayList;

import org.openelis.gwt.common.Prompt;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ToDoAnalyteReportService implements ToDoAnalyteReportServiceInt,
                                     ToDoAnalyteReportServiceIntAsync {

    static ToDoAnalyteReportService instance;
    
    ToDoAnalyteReportServiceIntAsync service;
    
    public static ToDoAnalyteReportService get() {
        if(instance == null)
            instance = new ToDoAnalyteReportService();
        
        return instance;
    }
    
    private ToDoAnalyteReportService() {
        service = (ToDoAnalyteReportServiceIntAsync)GWT.create(ToDoAnalyteReportServiceInt.class);
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
