package org.openelis.portal.modules.finalReport.client;

import java.util.ArrayList;

import org.openelis.domain.FinalReportWebVO;
import org.openelis.domain.IdNameVO;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("finalReport")
public interface FinalReportServiceInt extends XsrfProtectedService {

    ReportStatus runReportForWeb(Query query) throws Exception;

    ArrayList<IdNameVO> getProjectList() throws Exception;

    ArrayList<FinalReportWebVO> getSampleList(Query query) throws Exception;

}