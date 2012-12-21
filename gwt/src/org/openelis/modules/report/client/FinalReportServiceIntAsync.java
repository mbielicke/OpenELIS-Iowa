package org.openelis.modules.report.client;

import java.util.ArrayList;

import org.openelis.domain.FinalReportWebVO;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.Prompt;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FinalReportServiceIntAsync {

    void getEnvironmentalProjectList(AsyncCallback<ArrayList<IdNameVO>> callback);

    void getPrivateWellProjectList(AsyncCallback<ArrayList<IdNameVO>> callback);

    void getPromptsForBatch(AsyncCallback<ArrayList<Prompt>> callback);

    void getPromptsForBatchReprint(AsyncCallback<ArrayList<Prompt>> callback);

    void getPromptsForSingle(AsyncCallback<ArrayList<Prompt>> callback);

    void getSDWISProjectList(AsyncCallback<ArrayList<IdNameVO>> callback);

    void getSampleEnvironmentalList(Query query, AsyncCallback<ArrayList<FinalReportWebVO>> callback);

    void getSamplePrivateWellList(Query query, AsyncCallback<ArrayList<FinalReportWebVO>> callback);

    void getSampleSDWISList(Query query, AsyncCallback<ArrayList<FinalReportWebVO>> callback);

    void runReportForBatch(Query query, AsyncCallback<ReportStatus> callback);

    void runReportForBatchReprint(Query query, AsyncCallback<ReportStatus> callback);

    void runReportForPreview(Query query, AsyncCallback<ReportStatus> callback);

    void runReportForSingle(Query query, AsyncCallback<ReportStatus> callback);

    void runReportForWeb(Query query, AsyncCallback<ReportStatus> callback);

}
