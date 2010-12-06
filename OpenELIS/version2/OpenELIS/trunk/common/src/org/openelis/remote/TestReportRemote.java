package org.openelis.remote;

import java.util.ArrayList;

import javax.ejb.Remote;

import org.openelis.gwt.common.data.QueryData;
import org.openelis.report.Prompt;
import org.openelis.report.ReportStatus;

@Remote
public interface TestReportRemote {

    public ArrayList<Prompt> getPrompts() throws Exception;
    
    public ReportStatus runReport(ArrayList<QueryData> paramList) throws Exception;
}
