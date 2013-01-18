package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.manager.SampleQaEventManager;

@Remote
public interface SampleQAEventManagerRemote {

    public SampleQaEventManager fetchBySampleId(Integer sampleId) throws Exception;
}
