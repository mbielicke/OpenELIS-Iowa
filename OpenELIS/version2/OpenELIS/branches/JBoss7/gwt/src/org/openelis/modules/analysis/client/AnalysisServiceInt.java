package org.openelis.modules.analysis.client;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.AnalysisQaEventManager;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("analysis")
public interface AnalysisServiceInt extends RemoteService {

    AnalysisManager fetchBySampleItemId(Integer sampleItemId) throws Exception;

    AnalysisViewDO fetchById(Integer analysisId) throws Exception;

    // qa method
    AnalysisQaEventManager fetchQaByAnalysisId(Integer analysisId) throws Exception;

}