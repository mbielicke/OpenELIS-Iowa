package org.openelis.modules.report.turnaroundStatistic.client;

import org.openelis.domain.TurnAroundReportViewVO;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("turnaroundStatisticReport")
public interface TurnaroundStatisticReportServiceInt extends RemoteService {

    TurnAroundReportViewVO fetchForTurnaroundStatistic(Query query) throws Exception;

    ReportStatus runReport(TurnAroundReportViewVO data) throws Exception;

}