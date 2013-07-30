package org.openelis.modules.analysis.client;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.AnalysisQaEventManager;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("analysis")
public interface AnalysisServiceInt extends XsrfProtectedService {

    AnalysisManager fetchBySampleItemId(Integer sampleItemId) throws Exception;

    AnalysisViewDO fetchById(Integer analysisId) throws Exception;

    // qa method
    AnalysisQaEventManager fetchQaByAnalysisId(Integer analysisId) throws Exception;

}