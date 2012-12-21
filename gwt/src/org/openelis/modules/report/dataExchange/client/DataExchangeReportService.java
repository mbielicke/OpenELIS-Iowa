package org.openelis.modules.report.dataExchange.client;

import java.util.ArrayList;

import org.openelis.gwt.common.Prompt;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DataExchangeReportService implements DataExchangeReportServiceInt,
                                      DataExchangeReportServiceIntAsync {

    static DataExchangeReportService instance;
    
    DataExchangeReportServiceIntAsync service;
    
    public static DataExchangeReportService get() {
        if(instance == null)
            instance = new DataExchangeReportService();
        
        return instance;
    }
    
    private DataExchangeReportService() {
        service = (DataExchangeReportServiceIntAsync)GWT.create(DataExchangeReportServiceInt.class);
    }
    
    @Override
    public void exportToLocation(Query query, AsyncCallback<ReportStatus> callback) {
        service.exportToLocation(query, callback);
    }

    @Override
    public void getPrompts(AsyncCallback<ArrayList<Prompt>> callback) {
        service.getPrompts(callback);
    }
    
    @Override
    public ArrayList<Prompt> getPrompts() throws Exception {
        Callback<ArrayList<Prompt>> callback;
        
        callback = new Callback<ArrayList<Prompt>>();
        service.getPrompts(callback);
        return callback.getResult();
    }

    @Override
    public ReportStatus exportToLocation(Query query) throws Exception {
        Callback<ReportStatus> callback;
        
        callback = new Callback<ReportStatus>();
        service.exportToLocation(query,callback);
        return callback.getResult();
    }

}
