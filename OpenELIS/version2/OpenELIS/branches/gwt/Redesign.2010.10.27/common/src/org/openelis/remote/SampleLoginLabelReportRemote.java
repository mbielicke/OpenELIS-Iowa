package org.openelis.remote;

import java.util.ArrayList;

import javax.ejb.Remote;

import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.report.Prompt;

@Remote
public interface SampleLoginLabelReportRemote {

    public ArrayList<Prompt> getPrompts() throws Exception;
    
    public ReportStatus runReport(ArrayList<QueryData> paramList) throws Exception;

    public ArrayList<Prompt> getReprintPrompts() throws Exception;
    
    public ReportStatus runReprintReport(ArrayList<QueryData> paramList) throws Exception;
}
