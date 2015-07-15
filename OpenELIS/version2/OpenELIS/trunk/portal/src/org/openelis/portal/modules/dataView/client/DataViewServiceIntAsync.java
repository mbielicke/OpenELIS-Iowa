package org.openelis.portal.modules.dataView.client;

import java.util.ArrayList;

import org.openelis.domain.DataView1VO;
import org.openelis.domain.IdNameVO;
import org.openelis.ui.common.ReportStatus;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DataViewServiceIntAsync {

    void fetchAnalyteAndAuxField(DataView1VO data, AsyncCallback<DataView1VO> callback);

    void fetchProjectListForPortal(AsyncCallback<ArrayList<IdNameVO>> callback);

    void runReportForPortal(DataView1VO data, AsyncCallback<ReportStatus> callback);

    void getStatus(AsyncCallback<ReportStatus> callback);

}
