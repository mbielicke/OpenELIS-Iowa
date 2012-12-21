package org.openelis.modules.report.dataView.client;

import java.util.ArrayList;

import org.apache.commons.fileupload.FileItem;
import org.openelis.domain.DataViewVO;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.ReportStatus;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DataViewServiceIntAsync {

    void fetchAnalyteAndAuxField(DataViewVO data, AsyncCallback<DataViewVO> callback);

    void fetchAnalyteAndAuxFieldForWebEnvironmental(DataViewVO data,
                                                    AsyncCallback<DataViewVO> callback);

    void fetchEnvironmentalProjectListForWeb(AsyncCallback<ArrayList<IdNameVO>> callback);

    void openQuery(AsyncCallback<DataViewVO> callback);

    void runReport(DataViewVO data, AsyncCallback<ReportStatus> callback);

    void runReportForWebEnvironmental(DataViewVO data, AsyncCallback<ReportStatus> callback);

    void saveQuery(DataViewVO data, AsyncCallback<ReportStatus> callback);

}
