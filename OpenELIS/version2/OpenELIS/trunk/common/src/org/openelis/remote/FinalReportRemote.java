package org.openelis.remote;

import java.util.ArrayList;

import javax.ejb.Remote;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleEnvironmentalWebVO;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.report.Prompt;

@Remote
public interface FinalReportRemote {

    public ArrayList<Prompt> getPromptsForSingle() throws Exception;

    public ArrayList<Prompt> getPromptsForBatch() throws Exception;

    public ReportStatus runReportForSingle(ArrayList<QueryData> paramList) throws Exception;

    public ReportStatus runReportForPreview(ArrayList<QueryData> paramList) throws Exception;

    public ReportStatus runReportForBatch(ArrayList<QueryData> paramList) throws Exception;

    public ReportStatus runReportForWeb(ArrayList<QueryData> paramList) throws Exception;

    public ArrayList<SampleEnvironmentalWebVO> getSampleListForEnvironmental(ArrayList<QueryData> fields) throws Exception;

    public ArrayList<IdNameVO> getProjectList(ArrayList<QueryData> fields) throws Exception;
}
