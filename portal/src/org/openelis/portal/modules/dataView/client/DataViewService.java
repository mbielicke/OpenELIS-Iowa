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

    private static DataViewService  instance;

    private DataViewServiceIntAsync service;

    public static DataViewService get() {
        if (instance == null)
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
    public void fetchProjectListForPortal(AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.fetchProjectListForPortal(callback);
    }

    @Override
    public void runReport(DataViewVO data, AsyncCallback<ReportStatus> callback) {
        service.runReport(data, callback);
    }

    @Override
    public void runReportForPortal(DataViewVO data, AsyncCallback<ReportStatus> callback) {
        service.runReportForPortal(data, callback);
    }

    @Override
    public ArrayList<IdNameVO> fetchProjectListForPortal() throws Exception {
        Callback<ArrayList<IdNameVO>> callback;

        callback = new Callback<ArrayList<IdNameVO>>();
        service.fetchProjectListForPortal(callback);
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
    public ReportStatus runReport(DataViewVO data) throws Exception {
        Callback<ReportStatus> callback;

        callback = new Callback<ReportStatus>();
        service.runReport(data, callback);
        return callback.getResult();
    }

    @Override
    public ReportStatus runReportForPortal(DataViewVO data) throws Exception {
        Callback<ReportStatus> callback;

        callback = new Callback<ReportStatus>();
        service.runReportForPortal(data, callback);
        return callback.getResult();
    }

}
