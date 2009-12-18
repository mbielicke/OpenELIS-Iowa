package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.manager.AnalysisManager;

@Remote
public interface AnalysisManagerRemote {
    public AnalysisManager fetchBySampleItemId(Integer sampleItemId) throws Exception;
    public AnalysisManager fetchBySampleItemIdForUpdate(Integer sampleItemId) throws Exception;
}
