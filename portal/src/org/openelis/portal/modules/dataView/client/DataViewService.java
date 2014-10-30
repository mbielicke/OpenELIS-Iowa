package org.openelis.portal.modules.dataView.client;

import java.util.ArrayList;

import org.openelis.domain.DataViewVO;
import org.openelis.domain.IdNameVO;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.services.TokenService;
import org.openelis.ui.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

public class DataViewService implements DataViewServiceInt, DataViewServiceIntAsync {
    
    static DataViewService instance;
    
    DataViewServiceIntAsync service;
    
    public static DataViewService get() {
        if(instance == null)
            instance = new DataViewService();
        
        return instance;
    }
    
    private DataViewService() {
        service = (DataViewServiceIntAsync)GWT.create(DataViewServiceInt.class);
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }


    @Override
    public void fetchAnalyteAndAuxField(DataViewVO data, AsyncCallback<DataViewVO> callback) {
        service.fetchAnalyteAndAuxField(data, callback);
    }

    @Override
    public void fetchAnalyteAndAuxFieldForWebEnvironmental(DataViewVO data,
                                                           AsyncCallback<DataViewVO> callback) {
        service.fetchAnalyteAndAuxFieldForWebEnvironmental(data, callback);
    }

    @Override
    public void fetchEnvironmentalProjectListForWeb(AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.fetchEnvironmentalProjectListForWeb(callback);
    }

    @Override
    public void runReport(DataViewVO data, AsyncCallback<ReportStatus> callback) {
        service.runReport(data, callback);
    }

    @Override
    public void runReportForWebEnvironmental(DataViewVO data, AsyncCallback<ReportStatus> callback) {
        service.runReportForWebEnvironmental(data, callback);
    }

    @Override
    public ArrayList<IdNameVO> fetchEnvironmentalProjectListForWeb() throws Exception {
        Callback<ArrayList<IdNameVO>> callback;
        
        callback = new Callback<ArrayList<IdNameVO>>();
        service.fetchEnvironmentalProjectListForWeb(callback);
        return callback.getResult();
    }

    @Override
    public DataViewVO fetchAnalyteAndAuxField(DataViewVO data) throws Exception {
        Callback<DataViewVO> callback;
        
        callback = new Callback<DataViewVO>();
        service.fetchAnalyteAndAuxField(data, callback);
        return callback.getResult();  
    }

    @Override
    public DataViewVO fetchAnalyteAndAuxFieldForWebEnvironmental(DataViewVO data) throws Exception {
        Callback<DataViewVO> callback;
        
        callback = new Callback<DataViewVO>();
        service.fetchAnalyteAndAuxFieldForWebEnvironmental(data, callback);
        return callback.getResult();
    }

    @Override
    public ReportStatus runReport(DataViewVO data) throws Exception {
        Callback<ReportStatus> callback;
        
        callback = new Callback<ReportStatus>();
        service.runReport(data, callback);
        return callback.getResult();
    }

    @Override
    public ReportStatus runReportForWebEnvironmental(DataViewVO data) throws Exception {
        Callback<ReportStatus> callback;
        
        callback = new Callback<ReportStatus>();
        service.runReportForWebEnvironmental(data, callback);
        return callback.getResult();
    }

}
