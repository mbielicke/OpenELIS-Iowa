package org.openelis.modules.report.client;

import java.util.ArrayList;

import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("holdRefuse")
public interface HoldRefuseOrganizationReportServiceInt extends RemoteService {

    ArrayList<Prompt> getPrompts() throws Exception;

    ReportStatus runReport(Query query) throws Exception;

}