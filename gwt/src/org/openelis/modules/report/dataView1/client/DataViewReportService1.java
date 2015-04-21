package org.openelis.modules.report.dataView1.client;

import org.openelis.domain.DataView1VO;
import org.openelis.gwt.screen.Callback;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.services.TokenService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

public class DataViewReportService1 implements DataViewServiceInt1, DataViewServiceInt1Async {
    
    static DataViewReportService1 instance;
    
    DataViewServiceInt1Async service;
    
    public static DataViewReportService1 get() {
        if(instance == null)
            instance = new DataViewReportService1();
        
        return instance;
    }
    
    private DataViewReportService1() {
        service = (DataViewServiceInt1Async)GWT.create(DataViewServiceInt1.class);
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }

    @Override
    public void fetchTestAnalyteAndAuxField(DataView1VO data, AsyncCallback<DataView1VO> callback) {
        service.fetchTestAnalyteAndAuxField(data, callback);
    }
    
    @Override
    public DataView1VO fetchTestAnalyteAndAuxField(DataView1VO data) throws Exception {
        Callback<DataView1VO> callback;
        
        callback = new Callback<DataView1VO>();
        service.fetchTestAnalyteAndAuxField(data, callback);
        return callback.getResult();  
    }

    @Override
    public void openQuery(AsyncCallback<DataView1VO> callback) {
        service.openQuery(callback);
    }

    @Override
    public DataView1VO openQuery() throws Exception {
        Callback<DataView1VO> callback;
        
        callback = new Callback<DataView1VO>();
        service.openQuery(callback);
        return callback.getResult();
    }

    @Override
    public void saveQuery(DataView1VO data, AsyncCallback<ReportStatus> callback) {
        service.saveQuery(data, callback);
    }

    @Override
    public ReportStatus saveQuery(DataView1VO data) throws Exception {
        Callback<ReportStatus> callback;
        
        callback = new Callback<ReportStatus>();
        service.saveQuery(data, callback);
        return callback.getResult();
    }

    @Override
    public void runReport(DataView1VO data, AsyncCallback<ReportStatus> callback) {
        service.runReport(data, callback);
    }

    @Override
    public ReportStatus runReport(DataView1VO data) throws Exception {
        Callback<ReportStatus> callback;
        
        callback = new Callback<ReportStatus>();
        service.runReport(data, callback);
        return callback.getResult();
    }
}