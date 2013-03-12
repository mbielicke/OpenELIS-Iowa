package org.openelis.modules.report.qcChart.client;

import org.openelis.domain.QcChartReportViewVO;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface QcChartReportServiceIntAsync {

    void fetchForQcChart(Query query, AsyncCallback<QcChartReportViewVO> callback);

    void recompute(QcChartReportViewVO data, AsyncCallback<QcChartReportViewVO> callback);

    void runReport(QcChartReportViewVO data, AsyncCallback<ReportStatus> callback);

}
