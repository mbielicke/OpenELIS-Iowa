package org.openelis.modules.analysis.client;

import java.util.ArrayList;

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.SampleAnalysisVO;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AnalysisServiceIntAsync {

    void fetchById(Integer analysisId, AsyncCallback<AnalysisViewDO> callback);

    void fetchByPatientId(Integer patientId, AsyncCallback<ArrayList<SampleAnalysisVO>> callback);
}
