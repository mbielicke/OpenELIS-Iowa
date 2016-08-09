package org.openelis.portal.modules.dataView.client;

import java.util.ArrayList;

import org.openelis.domain.DataView1VO;
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
    public void fetchAnalyteAndAuxField(DataView1VO data, AsyncCallback<DataView1VO> callback) {
        service.fetchAnalyteAndAuxField(data, callback);
    }

    @Override
    public void fetchProjectListForPortal(AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.fetchProjectListForPortal(callback);
    }

    @Override
    public void runReportForPortal(DataView1VO data, AsyncCallback<ReportStatus> callback) {
        service.runReportForPortal(data, callback);
    }

    @Override
    public void getStatus(AsyncCallback<ReportStatus> callback) {
        service.getStatus(callback);
    }

    @Override
    public ArrayList<IdNameVO> fetchProjectListForPortal() throws Exception {
        Callback<ArrayList<IdNameVO>> callback;

        callback = new Callback<ArrayList<IdNameVO>>();
        service.fetchProjectListForPortal(callback);
        return callback.getResult();
    }

    @Override
    public DataView1VO fetchAnalyteAndAuxField(DataView1VO data) throws Exception {
        Callback<DataView1VO> callback;

        callback = new Callback<DataView1VO>();
        service.fetchAnalyteAndAuxField(data, callback);
        return callback.getResult();
    }

    @Override
    public ReportStatus runReportForPortal(DataView1VO data) throws Exception {
        Callback<ReportStatus> callback;

        callback = new Callback<ReportStatus>();
        service.runReportForPortal(data, callback);
        return callback.getResult();
    }

    @Override
    public ReportStatus getStatus() throws Exception {
        Callback<ReportStatus> callback;

        callback = new Callback<ReportStatus>();
        service.getStatus(callback);
        return callback.getResult();
    }

}
