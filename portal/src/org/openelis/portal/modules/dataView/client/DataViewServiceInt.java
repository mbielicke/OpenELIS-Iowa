package org.openelis.portal.modules.dataView.client;

import java.util.ArrayList;

import org.openelis.domain.DataView1VO;
import org.openelis.domain.IdNameVO;
import org.openelis.ui.common.ReportStatus;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("dataView")
public interface DataViewServiceInt extends XsrfProtectedService {

    ArrayList<IdNameVO> fetchProjectListForPortal() throws Exception;

    DataView1VO fetchAnalyteAndAuxField(DataView1VO data) throws Exception;

    ReportStatus runReportForPortal(DataView1VO data) throws Exception;

    ReportStatus getStatus() throws Exception;

}