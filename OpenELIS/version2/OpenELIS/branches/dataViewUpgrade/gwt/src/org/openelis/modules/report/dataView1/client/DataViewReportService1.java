package org.openelis.modules.report.dataView1.client;

import org.openelis.domain.DataView1VO;
import org.openelis.gwt.screen.Callback;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.services.TokenService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

public class DataViewReportService1 implements DataViewReportServiceInt1, DataViewReportServiceInt1Async {

    static DataViewReportService1 instance;

    DataViewReportServiceInt1Async      service;

    public static DataViewReportService1 get() {
        if (instance == null)
            instance = new DataViewReportService1();

        return instance;
    }

    private DataViewReportService1() {
        service = (DataViewReportServiceInt1Async)GWT.create(DataViewReportServiceInt1.class);
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
    public void loadQuery(AsyncCallback<DataView1VO> callback) {
        service.loadQuery(callback);
    }

    @Override
    public DataView1VO loadQuery() throws Exception {
        Callback<DataView1VO> callback;

        callback = new Callback<DataView1VO>();
        service.loadQuery(callback);
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
    public void runReportForInternal(DataView1VO data, AsyncCallback<ReportStatus> callback) {
        service.runReportForInternal(data, callback);
    }

    @Override
    public ReportStatus runReportForInternal(DataView1VO data) throws Exception {
        Callback<ReportStatus> callback;

        callback = new Callback<ReportStatus>();
        service.runReportForInternal(data, callback);
        return callback.getResult();
    }

    @Override
    public void getStatus(AsyncCallback<ReportStatus> callback) {
        service.getStatus(callback);
    }

    @Override
    public ReportStatus getStatus() throws Exception {
        Callback<ReportStatus> callback;

        callback = new Callback<ReportStatus>();
        service.getStatus(callback);
        return callback.getResult();
    }

    @Override
    public void stopReport(AsyncCallback<Void> callback) {
        service.stopReport(callback);
    }

    @Override
    public void stopReport() {
        Callback<Void> callback;

        callback = new Callback<Void>();
        service.stopReport(callback);
    }
}