package org.openelis.modules.result.client;

import java.util.ArrayList;

import org.openelis.domain.AnalysisDO;
import org.openelis.domain.AnalyteDO;
import org.openelis.manager.AnalysisResultManager;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("result")
public interface ResultServiceInt extends XsrfProtectedService {

    AnalysisResultManager fetchByAnalysisIdForDisplay(Integer analysisId) throws Exception;

    AnalysisResultManager fetchByAnalysisId(Integer analysisId) throws Exception;

    AnalysisResultManager fetchByTestId(AnalysisDO anDO) throws Exception;

    AnalysisResultManager fetchByTestIdForOrderImport(AnalysisDO anDO) throws Exception;

    AnalysisResultManager merge(AnalysisResultManager manager) throws Exception;

    ArrayList<AnalyteDO> getAliasList(Query query) throws Exception;

}