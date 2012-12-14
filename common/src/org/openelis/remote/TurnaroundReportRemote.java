package org.openelis.remote;

import java.util.ArrayList;

import javax.ejb.Remote;

import org.openelis.domain.Prompt;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.QueryData;

@Remote
public interface TurnaroundReportRemote {

    public ArrayList<Prompt> getPrompts() throws Exception;

    public ReportStatus runReport(ArrayList<QueryData> paramList) throws Exception;
}
