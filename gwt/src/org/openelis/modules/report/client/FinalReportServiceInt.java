package org.openelis.modules.report.client;

import java.util.ArrayList;

import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("finalReport")
public interface FinalReportServiceInt extends XsrfProtectedService {

    ArrayList<Prompt> getPromptsForBatch() throws Exception;

    ArrayList<Prompt> getPromptsForBatchReprint() throws Exception;

    ReportStatus runReportForSingle(Query query) throws Exception;

    ReportStatus runReportForPreview(Query query) throws Exception;

    ReportStatus runReportForBatch(Query query) throws Exception;

    ReportStatus runReportForBatchReprint(Query query) throws Exception;

    ReportStatus runReportForWeb(Query query) throws Exception;

}