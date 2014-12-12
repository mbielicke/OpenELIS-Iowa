package org.openelis.portal.modules.sampleStatus.client;

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleViewVO;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SampleStatusServiceIntAsync {

    void getSampleListForSampleStatusReport(Query query,
                                            AsyncCallback<ArrayList<SampleViewVO>> callback);

    void getProjectList(AsyncCallback<ArrayList<IdNameVO>> callback);

    void getSampleQaEvents(ArrayList<Integer> sampleIds,
                           AsyncCallback<HashMap<Integer, ArrayList<String>>> callback);

    void getAnalysisQaEvents(ArrayList<Integer> analysisIds,
                             AsyncCallback<HashMap<Integer, ArrayList<String>>> callback);

}
