package org.openelis.modules.result.client;

import java.util.ArrayList;

import org.openelis.domain.AnalysisDO;
import org.openelis.domain.AnalyteDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.AnalysisResultManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ResultServiceIntAsync {

    void fetchByAnalysisId(Integer analysisId, AsyncCallback<AnalysisResultManager> callback);

    void fetchByAnalysisIdForDisplay(Integer analysisId,
                                     AsyncCallback<AnalysisResultManager> callback);

    void fetchByTestId(AnalysisDO anDO, AsyncCallback<AnalysisResultManager> callback);

    void fetchByTestIdForOrderImport(AnalysisDO anDO, AsyncCallback<AnalysisResultManager> callback);

    void getAliasList(Query query, AsyncCallback<ArrayList<AnalyteDO>> callback);

    void merge(AnalysisResultManager manager, AsyncCallback<AnalysisResultManager> callback);

}
