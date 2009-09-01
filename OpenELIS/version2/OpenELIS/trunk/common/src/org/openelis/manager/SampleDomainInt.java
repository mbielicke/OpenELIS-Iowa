package org.openelis.manager;

import org.openelis.gwt.common.RPC;

public interface SampleDomainInt {
    
    public void setSampleId(Integer sampleId);
    public RPC add() throws Exception;
    public RPC update() throws Exception;
}
