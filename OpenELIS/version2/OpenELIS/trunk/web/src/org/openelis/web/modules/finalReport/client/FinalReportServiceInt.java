package org.openelis.web.modules.finalReport.client;

import java.util.ArrayList;

import org.openelis.domain.FinalReportWebVO;
import org.openelis.domain.IdNameVO;
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("finalReport")
public interface FinalReportServiceInt extends XsrfProtectedService {

    ArrayList<Prompt> getPromptsForSingle() throws Exception;

    ArrayList<Prompt> getPromptsForBatch() throws Exception;

    ArrayList<Prompt> getPromptsForBatchReprint() throws Exception;

    ReportStatus runReportForSingle(Query query) throws Exception;

    ReportStatus runReportForPreview(Query query) throws Exception;

    ReportStatus runReportForBatch(Query query) throws Exception;

    ReportStatus runReportForBatchReprint(Query query) throws Exception;

    ReportStatus runReportForWeb(Query query) throws Exception;

    ArrayList<FinalReportWebVO> getSampleEnvironmentalList(Query query) throws Exception;

    ArrayList<FinalReportWebVO> getSamplePrivateWellList(Query query) throws Exception;

    ArrayList<FinalReportWebVO> getSampleSDWISList(Query query) throws Exception;

    ArrayList<IdNameVO> getEnvironmentalProjectList() throws Exception;

    ArrayList<IdNameVO> getPrivateWellProjectList() throws Exception;

    ArrayList<IdNameVO> getSDWISProjectList() throws Exception;

}