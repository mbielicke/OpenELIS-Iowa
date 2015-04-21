package org.openelis.modules.report.dataView1.client;

import org.openelis.domain.DataView1VO;
import org.openelis.ui.common.ReportStatus;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("dataViewReport1")
public interface DataViewServiceInt1 extends XsrfProtectedService {

    DataView1VO fetchTestAnalyteAndAuxField(DataView1VO data) throws Exception;

    ReportStatus runReport(DataView1VO data) throws Exception;

    DataView1VO openQuery() throws Exception;

    ReportStatus saveQuery(DataView1VO data) throws Exception;
}