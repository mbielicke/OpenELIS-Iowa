package org.openelis.remote;

import java.util.ArrayList;

import javax.ejb.Remote;

import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.report.Prompt;

@Remote
public interface FinalReportRemote {
    
    public ArrayList<Prompt> getPromptsForSingle() throws Exception;
    
    public ReportStatus runReportForSingle(ArrayList<QueryData> paramList) throws Exception;

    public ReportStatus runReportForPreview(ArrayList<QueryData> paramList) throws Exception;
    
    public ReportStatus runReportForBatch(ArrayList<QueryData> paramList) throws Exception;
}
