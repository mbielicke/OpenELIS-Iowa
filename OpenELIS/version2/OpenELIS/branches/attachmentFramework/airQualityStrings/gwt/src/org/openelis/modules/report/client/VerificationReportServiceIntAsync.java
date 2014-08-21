package org.openelis.modules.report.client;

import java.util.ArrayList;

import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface VerificationReportServiceIntAsync {

    void getPrompts(AsyncCallback<ArrayList<Prompt>> callback);

    void runReport(Query query, AsyncCallback<ReportStatus> callback);

}
