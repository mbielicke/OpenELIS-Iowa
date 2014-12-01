package org.openelis.portal.modules.dataView.client;

import java.util.ArrayList;

import org.openelis.domain.DataViewVO;
import org.openelis.domain.IdNameVO;
import org.openelis.ui.common.ReportStatus;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DataViewServiceIntAsync {

    void fetchAnalyteAndAuxField(DataViewVO data, AsyncCallback<DataViewVO> callback);

    void fetchProjectListForPortal(AsyncCallback<ArrayList<IdNameVO>> callback);

    void runReport(DataViewVO data, AsyncCallback<ReportStatus> callback);

    void runReportForPortal(DataViewVO data, AsyncCallback<ReportStatus> callback);

    void getStatus(AsyncCallback<ReportStatus> callback);

}
