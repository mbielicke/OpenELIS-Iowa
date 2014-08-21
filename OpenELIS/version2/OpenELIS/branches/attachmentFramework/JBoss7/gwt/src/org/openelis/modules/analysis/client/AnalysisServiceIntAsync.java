package org.openelis.modules.analysis.client;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.AnalysisQaEventManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AnalysisServiceIntAsync {

    void fetchById(Integer analysisId, AsyncCallback<AnalysisViewDO> callback);

    void fetchBySampleItemId(Integer sampleItemId, AsyncCallback<AnalysisManager> callback);

    void fetchQaByAnalysisId(Integer analysisId, AsyncCallback<AnalysisQaEventManager> callback);

}
