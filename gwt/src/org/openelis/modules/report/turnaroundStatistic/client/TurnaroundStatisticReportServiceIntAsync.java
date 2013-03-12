package org.openelis.modules.report.turnaroundStatistic.client;

import org.openelis.domain.TurnAroundReportViewVO;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TurnaroundStatisticReportServiceIntAsync {

    void fetchForTurnaroundStatistic(Query query, AsyncCallback<TurnAroundReportViewVO> callback);

    void runReport(TurnAroundReportViewVO data, AsyncCallback<ReportStatus> callback);

}
