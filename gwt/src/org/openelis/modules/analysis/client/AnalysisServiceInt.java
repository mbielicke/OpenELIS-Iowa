package org.openelis.modules.analysis.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.SampleAnalysisVO;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.AnalysisQaEventManager;

@RemoteServiceRelativePath("analysis")
public interface AnalysisServiceInt extends XsrfProtectedService {

    AnalysisManager fetchBySampleItemId(Integer sampleItemId) throws Exception;

    AnalysisViewDO fetchById(Integer analysisId) throws Exception;

    ArrayList<SampleAnalysisVO> fetchByPatientId(Integer patientId) throws Exception;

    // qa method
    AnalysisQaEventManager fetchQaByAnalysisId(Integer analysisId) throws Exception;

}