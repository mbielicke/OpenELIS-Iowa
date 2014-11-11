package org.openelis.portal.modules.dataView.client;

import java.util.ArrayList;

import org.openelis.domain.DataViewVO;
import org.openelis.domain.IdNameVO;
import org.openelis.ui.common.ReportStatus;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("dataView")
public interface DataViewServiceInt extends XsrfProtectedService {

    ArrayList<IdNameVO> fetchProjectListForPortal() throws Exception;

    DataViewVO fetchAnalyteAndAuxField(DataViewVO data) throws Exception;

    ReportStatus runReport(DataViewVO data) throws Exception;

    ReportStatus runReportForPortal(DataViewVO data) throws Exception;

}