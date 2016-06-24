package org.openelis.modules.analysis.client;

import java.util.ArrayList;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.SampleAnalysisVO;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("analysis")
public interface AnalysisServiceInt extends XsrfProtectedService {

    AnalysisViewDO fetchById(Integer analysisId) throws Exception;

    ArrayList<SampleAnalysisVO> fetchByPatientId(Integer patientId) throws Exception;
}