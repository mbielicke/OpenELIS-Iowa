package org.openelis.portal.modules.finalReport.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleViewVO;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.screen.Callback;
import org.openelis.ui.services.TokenService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

public class FinalReportService implements FinalReportServiceInt, FinalReportServiceIntAsync {

    private static FinalReportService  instance;

    private FinalReportServiceIntAsync service;

    public static FinalReportService get() {
        if (instance == null)
            instance = new FinalReportService();

        return instance;
    }

    private FinalReportService() {
        service = (FinalReportServiceIntAsync)GWT.create(FinalReportServiceInt.class);
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }

    @Override
    public void getProjectList(AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.getProjectList(callback);
    }

    @Override
    public void getSampleList(Query query, AsyncCallback<ArrayList<SampleViewVO>> callback) {
        service.getSampleList(query, callback);
    }

    @Override
    public void runReportForWeb(Query query, AsyncCallback<ReportStatus> callback) {
        service.runReportForWeb(query, callback);
    }

    @Override
    public void getStatus(AsyncCallback<ReportStatus> callback) {
        service.getStatus(callback);
    }
    
    @Override
    public ReportStatus runReportForWeb(Query query) throws Exception {
        Callback<ReportStatus> callback;

        callback = new Callback<ReportStatus>();
        service.runReportForWeb(query, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<IdNameVO> getProjectList() throws Exception {
        Callback<ArrayList<IdNameVO>> callback;

        callback = new Callback<ArrayList<IdNameVO>>();
        service.getProjectList(callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<SampleViewVO> getSampleList(Query query) throws Exception {
        Callback<ArrayList<SampleViewVO>> callback;

        callback = new Callback<ArrayList<SampleViewVO>>();
        service.getSampleList(query, callback);
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
