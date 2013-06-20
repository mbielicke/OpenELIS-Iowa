package org.openelis.modules.report.client;

import java.util.ArrayList;

import org.openelis.gwt.common.Prompt;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TurnaroundReportServiceIntAsync {

    void getPrompts(AsyncCallback<ArrayList<Prompt>> callback);

    void runReport(Query query, AsyncCallback<ReportStatus> callback);

}