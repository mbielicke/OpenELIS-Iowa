package org.openelis.modules.analysis.client;

import java.util.ArrayList;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AnalysisViewVO;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.AnalysisQaEventManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AnalysisServiceIntAsync {

    void fetchById(Integer analysisId, AsyncCallback<AnalysisViewDO> callback);

    void fetchBySampleItemId(Integer sampleItemId, AsyncCallback<AnalysisManager> callback);

    void fetchByPatientId(Integer patientId, AsyncCallback<ArrayList<AnalysisViewVO>> callback);

    void fetchQaByAnalysisId(Integer analysisId, AsyncCallback<AnalysisQaEventManager> callback);

}
