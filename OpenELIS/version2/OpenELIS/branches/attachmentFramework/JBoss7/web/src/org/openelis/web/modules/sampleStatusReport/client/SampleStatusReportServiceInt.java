package org.openelis.web.modules.sampleStatusReport.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleStatusWebReportVO;
import org.openelis.gwt.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("sampleStatus")
public interface SampleStatusReportServiceInt extends RemoteService {

    ArrayList<SampleStatusWebReportVO> getSampleListForSampleStatusReport(Query query) throws Exception;

    ArrayList<IdNameVO> getSampleStatusProjectList() throws Exception;

}