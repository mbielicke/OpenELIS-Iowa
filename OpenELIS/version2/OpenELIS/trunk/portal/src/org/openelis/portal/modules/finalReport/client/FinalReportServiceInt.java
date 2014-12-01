package org.openelis.portal.modules.finalReport.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleViewVO;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("finalReport")
public interface FinalReportServiceInt extends XsrfProtectedService {

    ReportStatus runReportForWeb(Query query) throws Exception;

    ArrayList<IdNameVO> getProjectList() throws Exception;

    ArrayList<SampleViewVO> getSampleList(Query query) throws Exception;

    ReportStatus getStatus() throws Exception;

}