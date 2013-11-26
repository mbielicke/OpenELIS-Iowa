package org.openelis.modules.analysisUser.client;

import org.openelis.manager.AnalysisUserManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AnalysisUserServiceIntAsync {

    void fetchByAnalysisId(Integer analysisId, AsyncCallback<AnalysisUserManager> callback);

}
