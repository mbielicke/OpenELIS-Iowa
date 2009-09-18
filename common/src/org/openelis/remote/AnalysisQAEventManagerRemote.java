package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.manager.AnalysisQaEventManager;

@Remote
public interface AnalysisQAEventManagerRemote {
    public AnalysisQaEventManager fetchByAnalysisId(Integer analysisId) throws Exception;
}
