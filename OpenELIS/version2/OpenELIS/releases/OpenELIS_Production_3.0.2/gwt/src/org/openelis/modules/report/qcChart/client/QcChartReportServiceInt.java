package org.openelis.modules.report.qcChart.client;

import org.openelis.domain.QcChartReportViewVO;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("qcChartReport")
public interface QcChartReportServiceInt extends XsrfProtectedService {

    QcChartReportViewVO fetchForQcChart(Query query) throws Exception;

    QcChartReportViewVO recompute(QcChartReportViewVO data) throws Exception;

    ReportStatus runReport(QcChartReportViewVO data) throws Exception;

}