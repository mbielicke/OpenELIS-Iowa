package org.openelis.modules.report.client;

import java.util.ArrayList;

import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FinalReportServiceIntAsync {

    void getPromptsForBatch(AsyncCallback<ArrayList<Prompt>> callback);

    void getPromptsForBatchReprint(AsyncCallback<ArrayList<Prompt>> callback);

    void runReportForBatch(Query query, AsyncCallback<ReportStatus> callback);

    void runReportForBatchReprint(Query query, AsyncCallback<ReportStatus> callback);

    void runReportForPreview(Query query, AsyncCallback<ReportStatus> callback);

    void runReportForSingle(Query query, AsyncCallback<ReportStatus> callback);

    void runReportForWeb(Query query, AsyncCallback<ReportStatus> callback);

}
