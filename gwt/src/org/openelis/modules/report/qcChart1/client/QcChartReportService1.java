package org.openelis.modules.report.qcChart1.client;

import org.openelis.domain.QcChartReportViewVO;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.services.TokenService;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

public class QcChartReportService1 implements QcChartReportServiceInt1, QcChartReportServiceInt1Async {
    
    static QcChartReportService1 instance;
    
    QcChartReportServiceInt1Async service;
    
    public static QcChartReportService1 get() {
        if(instance == null)
            instance = new QcChartReportService1();
        
        return instance;
    }
    
    private QcChartReportService1() {
        service = (QcChartReportServiceInt1Async)GWT.create(QcChartReportServiceInt1.class);
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }

    @Override
    public void fetchData(Query query, AsyncCallback<QcChartReportViewVO> callback) {
        service.fetchData(query, callback);
    }

    @Override
    public void runReport(QcChartReportViewVO data, AsyncCallback<ReportStatus> callback) {
        service.runReport(data, callback);
    }

    @Override
    public QcChartReportViewVO fetchData(Query query) throws Exception {
        Callback<QcChartReportViewVO> callback;
        
        callback = new Callback<QcChartReportViewVO>();
        service.fetchData(query, callback);
        return callback.getResult();
    }

    @Override
    public ReportStatus runReport(QcChartReportViewVO data) throws Exception {
        Callback<ReportStatus> callback;
        
        callback = new Callback<ReportStatus>();
        service.runReport(data, callback);
        return callback.getResult();
    }
}