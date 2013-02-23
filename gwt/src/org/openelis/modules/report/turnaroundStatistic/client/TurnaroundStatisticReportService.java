package org.openelis.modules.report.turnaroundStatistic.client;

import org.openelis.domain.TurnAroundReportViewVO;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class TurnaroundStatisticReportService implements TurnaroundStatisticReportServiceInt,
                                             TurnaroundStatisticReportServiceIntAsync {
    
    static TurnaroundStatisticReportService instance;
    
    TurnaroundStatisticReportServiceIntAsync service;
    
    public static TurnaroundStatisticReportService get() {
        if(instance == null)
            instance = new TurnaroundStatisticReportService();
        
        return instance;
    }
    
    private TurnaroundStatisticReportService() {
        service = (TurnaroundStatisticReportServiceIntAsync)GWT.create(TurnaroundStatisticReportServiceInt.class);
    }

    @Override
    public void fetchForTurnaroundStatistic(Query query,
                                            AsyncCallback<TurnAroundReportViewVO> callback) {
        service.fetchForTurnaroundStatistic(query, callback);
    }

    @Override
    public void runReport(TurnAroundReportViewVO data, AsyncCallback<ReportStatus> callback) {
        service.runReport(data, callback);
    }

    @Override
    public TurnAroundReportViewVO fetchForTurnaroundStatistic(Query query) throws Exception {
        Callback<TurnAroundReportViewVO> callback;
        
        callback = new Callback<TurnAroundReportViewVO>();
        service.fetchForTurnaroundStatistic(query, callback);
        return callback.getResult();
    }

    @Override
    public ReportStatus runReport(TurnAroundReportViewVO data) throws Exception {
        Callback<ReportStatus> callback;
        
        callback = new Callback<ReportStatus>();
        service.runReport(data, callback);
        return callback.getResult();
    }

}
