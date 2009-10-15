package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.manager.AnalysisResultManager;

@Remote
public interface ResultManagerRemote {
    public AnalysisResultManager fetchByAnalysisId(Integer analysisId) throws Exception;
    
    public AnalysisResultManager fetchByTestId(Integer testId) throws Exception;
}
