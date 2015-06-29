package org.openelis.modules.report.dataView1.client;

import org.openelis.domain.DataView1VO;
import org.openelis.ui.common.ReportStatus;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DataViewReportServiceInt1Async {

    public void fetchTestAnalyteAndAuxField(DataView1VO data, AsyncCallback<DataView1VO> callback);

    public void runReport(DataView1VO data, AsyncCallback<ReportStatus> callback);

    public void openQuery(AsyncCallback<DataView1VO> callback);

    public void saveQuery(DataView1VO data, AsyncCallback<ReportStatus> callback);

    public void getStatus(AsyncCallback<ReportStatus> callback);

    public void stopReport(AsyncCallback<Void> callback);
}
