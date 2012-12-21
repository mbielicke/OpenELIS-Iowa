package org.openelis.modules.report.qcChart.client;

import org.openelis.domain.QcChartReportViewVO;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("qcChartReport")
public interface QcChartReportServiceInt extends RemoteService {

    QcChartReportViewVO fetchForQcChart(Query query) throws Exception;

    QcChartReportViewVO recompute(QcChartReportViewVO data) throws Exception;

    ReportStatus runReport(QcChartReportViewVO data) throws Exception;

}