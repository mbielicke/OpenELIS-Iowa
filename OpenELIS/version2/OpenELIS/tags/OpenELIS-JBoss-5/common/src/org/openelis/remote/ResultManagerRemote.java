package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.manager.AnalysisResultManager;

@Remote
public interface ResultManagerRemote {
    public AnalysisResultManager fetchByAnalysisIdForDisplay(Integer analysisId) throws Exception;
    public AnalysisResultManager fetchForUpdateWithAnalysisId(Integer analysisId) throws Exception;
    public AnalysisResultManager fetchForUpdateWithTestId(Integer testId, Integer unitId) throws Exception;
    public AnalysisResultManager merge(AnalysisResultManager man) throws Exception;
}
