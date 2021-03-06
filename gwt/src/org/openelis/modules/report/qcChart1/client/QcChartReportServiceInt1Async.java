package org.openelis.modules.report.qcChart1.client;

import org.openelis.domain.QcChartReportViewVO;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface QcChartReportServiceInt1Async {

    void fetchData(Query query, AsyncCallback<QcChartReportViewVO> callback);

    void runReport(QcChartReportViewVO data, AsyncCallback<ReportStatus> callback);

}
