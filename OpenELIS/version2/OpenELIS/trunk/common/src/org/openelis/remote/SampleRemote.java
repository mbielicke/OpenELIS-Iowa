package org.openelis.remote;

import javax.ejb.Remote;

@Remote
public interface SampleRemote {
    public void validateAccessionNumber(Integer accessionNumber) throws Exception;
}
