package org.openelis.modules.report.client;

import java.util.ArrayList;

import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("buildKitsReport")
public interface BuildKitsReportServiceInt extends RemoteService {
    
    public ArrayList<Prompt> getPrompts() throws Exception;
    
    public ReportStatus runReport(Query query) throws Exception;

}
