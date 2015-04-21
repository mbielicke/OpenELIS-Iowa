package org.openelis.modules.report.dataView1.client;

import org.openelis.domain.DataView1VO;
import org.openelis.ui.common.ReportStatus;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DataViewServiceInt1Async {

    void fetchTestAnalyteAndAuxField(DataView1VO data, AsyncCallback<DataView1VO> callback);

    void runReport(DataView1VO data, AsyncCallback<ReportStatus> callback);

    void openQuery(AsyncCallback<DataView1VO> callback);

    void saveQuery(DataView1VO data, AsyncCallback<ReportStatus> callback);
}
