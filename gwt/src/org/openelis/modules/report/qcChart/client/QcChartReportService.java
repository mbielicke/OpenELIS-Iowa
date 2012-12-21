package org.openelis.modules.report.qcChart.client;

import org.openelis.domain.QcChartReportViewVO;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class QcChartReportService implements QcChartReportServiceInt, QcChartReportServiceIntAsync {
    
    static QcChartReportService instance;
    
    QcChartReportServiceIntAsync service;
    
    public static QcChartReportService get() {
        if(instance == null)
            instance = new QcChartReportService();
        
        return instance;
    }
    
    private QcChartReportService() {
        service = (QcChartReportServiceIntAsync)GWT.create(QcChartReportServiceInt.class);
    }

    @Override
    public void fetchForQcChart(Query query, AsyncCallback<QcChartReportViewVO> callback) {
        service.fetchForQcChart(query, callback);
    }

    @Override
    public void recompute(QcChartReportViewVO data, AsyncCallback<QcChartReportViewVO> callback) {
        service.recompute(data, callback);
    }

    @Override
    public void runReport(QcChartReportViewVO data, AsyncCallback<ReportStatus> callback) {
        service.runReport(data, callback);
    }

    @Override
    public QcChartReportViewVO fetchForQcChart(Query query) throws Exception {
        Callback<QcChartReportViewVO> callback;
        
        callback = new Callback<QcChartReportViewVO>();
        service.fetchForQcChart(query, callback);
        return callback.getResult();
    }

    @Override
    public QcChartReportViewVO recompute(QcChartReportViewVO data) throws Exception {
        Callback<QcChartReportViewVO> callback;
        
        callback = new Callback<QcChartReportViewVO>();
        service.recompute(data, callback);
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
