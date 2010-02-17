package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.manager.AnalysisResultManager;

@Remote
public interface ResultManagerRemote {
    public AnalysisResultManager fetchByAnalysisIdForDisplay(Integer analysisId) throws Exception;
    public AnalysisResultManager fetchByAnalysisId(Integer analysisId, Integer testId) throws Exception;
    public AnalysisResultManager fetchByTestId(Integer testId) throws Exception;
    public AnalysisResultManager merge(AnalysisResultManager man) throws Exception;
}
