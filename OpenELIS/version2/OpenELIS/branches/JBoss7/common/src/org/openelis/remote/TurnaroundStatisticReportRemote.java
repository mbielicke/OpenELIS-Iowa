package org.openelis.remote;

import java.util.ArrayList;

import javax.ejb.Remote;

import org.openelis.domain.TurnAroundReportViewVO;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.QueryData;

@Remote
public interface TurnaroundStatisticReportRemote {
    public TurnAroundReportViewVO fetchForTurnaroundStatistic(ArrayList<QueryData> paramList) throws Exception;
    public ReportStatus runReport(TurnAroundReportViewVO vo) throws Exception;
}
