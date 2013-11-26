package org.openelis.web.modules.sampleStatusReport.client;

import java.util.ArrayList;

import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.domain.SampleStatusWebReportVO;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("sampleStatus")
public interface SampleStatusReportServiceInt extends XsrfProtectedService {

    ArrayList<SampleStatusWebReportVO> getSampleListForSampleStatusReport(Query query) throws Exception;

    ArrayList<IdNameVO> getSampleStatusProjectList() throws Exception;

    ArrayList<SampleQaEventViewDO> getSampleQaEventsBySampleId(Integer id) throws Exception;

    ArrayList<AnalysisQaEventViewDO> getAnalysisQaEventsByAnalysisId(Integer id) throws Exception;
}