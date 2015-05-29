package org.openelis.modules.report.dataView1.client;

import org.openelis.domain.DataView1VO;
import org.openelis.ui.common.ReportStatus;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("dataViewReport1")
public interface DataViewServiceInt1 extends XsrfProtectedService {

    public DataView1VO fetchTestAnalyteAndAuxField(DataView1VO data) throws Exception;

    public ReportStatus runReport(DataView1VO data) throws Exception;

    public DataView1VO openQuery() throws Exception;

    public ReportStatus saveQuery(DataView1VO data) throws Exception;
    
    public ReportStatus getStatus() throws Exception;
    
    public void stopReport();
}