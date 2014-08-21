package org.openelis.modules.analysisUser.client;

import org.openelis.manager.AnalysisUserManager;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("analysisUser")
public interface AnalysisUserServiceInt extends XsrfProtectedService {
    
    public AnalysisUserManager fetchByAnalysisId(Integer analysisId) throws Exception;
}