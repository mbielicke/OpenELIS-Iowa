package org.openelis.modules.report.client;

import java.util.ArrayList;

import org.openelis.gwt.screen.Callback;
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.services.TokenService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

public class AirQualityReportService implements AirQualityReportServiceInt,
                                     AirQualityReportServiceIntAsync {

    private static AirQualityReportService  instance;
    private AirQualityReportServiceIntAsync service;

    public static AirQualityReportService get() {
        if (instance == null)
            instance = new AirQualityReportService();
        return instance;
    }

    private AirQualityReportService() {
        service = (AirQualityReportServiceIntAsync)GWT.create(AirQualityReportServiceInt.class);
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }

    @Override
    public ArrayList<Prompt> getPrompts() throws Exception {
        Callback<ArrayList<Prompt>> callback;

        callback = new Callback<ArrayList<Prompt>>();
        service.getPrompts(callback);
        return callback.getResult();
    }

    @Override
    public void getPrompts(AsyncCallback<ArrayList<Prompt>> callback) throws Exception {
        service.getPrompts(callback);
    }

    @Override
    public void runReport(Query query, AsyncCallback<ReportStatus> callback) {
        service.runReport(query, callback);
    }

    @Override
    public ReportStatus runReport(Query query) throws Exception {
        Callback<ReportStatus> callback;

        callback = new Callback<ReportStatus>();
        service.runReport(query, callback);
        return callback.getResult();
    }

}
