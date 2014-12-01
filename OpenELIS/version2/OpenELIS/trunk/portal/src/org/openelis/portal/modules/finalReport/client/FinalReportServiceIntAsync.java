package org.openelis.portal.modules.finalReport.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleViewVO;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FinalReportServiceIntAsync {

    void runReportForWeb(Query query, AsyncCallback<ReportStatus> callback);

    void getProjectList(AsyncCallback<ArrayList<IdNameVO>> callback);

    void getSampleList(Query query, AsyncCallback<ArrayList<SampleViewVO>> callback);

    void getStatus(AsyncCallback<ReportStatus> callback);
}
