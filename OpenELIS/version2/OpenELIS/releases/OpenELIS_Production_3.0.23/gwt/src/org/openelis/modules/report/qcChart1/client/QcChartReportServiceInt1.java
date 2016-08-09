package org.openelis.modules.report.qcChart1.client;

import org.openelis.domain.QcChartReportViewVO;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("qcChartReport1")
public interface QcChartReportServiceInt1 extends XsrfProtectedService {

    QcChartReportViewVO fetchData(Query query) throws Exception;

    ReportStatus runReport(QcChartReportViewVO data) throws Exception;

}