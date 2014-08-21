package org.openelis.modules.analysisUser.client;

import org.openelis.manager.AnalysisUserManager;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("analysisUser")
public interface AnalysisUserServiceInt extends RemoteService{
    
    public AnalysisUserManager fetchByAnalysisId(Integer analysisId) throws Exception;
}