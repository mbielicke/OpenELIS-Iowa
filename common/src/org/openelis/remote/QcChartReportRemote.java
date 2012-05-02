package org.openelis.remote;

import java.util.ArrayList;

import javax.ejb.Remote;

import org.openelis.domain.QcChartReportViewVO;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.QueryData;

@Remote
public interface QcChartReportRemote {
    public QcChartReportViewVO fetchForQcChart(ArrayList<QueryData> paramList) throws Exception;
    public QcChartReportViewVO recompute(QcChartReportViewVO vo) throws Exception;
    public ReportStatus runReport(QcChartReportViewVO vo) throws Exception;
}
