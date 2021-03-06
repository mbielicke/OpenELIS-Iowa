package org.openelis.modules.report.client;

import java.util.ArrayList;

import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("chlGcToCDCExport")
public interface ChlGcToCDCExportServiceInt extends XsrfProtectedService {

    ArrayList<Prompt> getPrompts() throws Exception;

    ReportStatus runReport(Query query) throws Exception;

    ReportStatus getStatus() throws Exception;

}