package org.openelis.portal.modules.sampleStatus.client;

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleViewVO;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("sampleStatus")
public interface SampleStatusServiceInt extends XsrfProtectedService {

    ArrayList<SampleViewVO> getSampleListForSampleStatusReport(Query query) throws Exception;

    ArrayList<IdNameVO> getProjectList() throws Exception;

    HashMap<Integer, ArrayList<String>> getSampleQaEvents(ArrayList<Integer> sampleIds) throws Exception;

    HashMap<Integer, ArrayList<String>> getAnalysisQaEvents(ArrayList<Integer> analysisIds) throws Exception;

}