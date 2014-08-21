package org.openelis.modules.report.turnaroundStatistic.client;

import org.openelis.domain.TurnAroundReportViewVO;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("turnaroundStatisticReport")
public interface TurnaroundStatisticReportServiceInt extends XsrfProtectedService {

    TurnAroundReportViewVO fetchForTurnaroundStatistic(Query query) throws Exception;

    ReportStatus runReport(TurnAroundReportViewVO data) throws Exception;

}