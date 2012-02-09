package org.openelis.manager;

import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ValidationErrorsList;

public interface SampleDomainInt {
    
    public void setSampleId(Integer sampleId);
    public RPC add() throws Exception;
    public RPC update() throws Exception;
    public void delete() throws Exception;
    public void validate() throws Exception;
    public void validate(ValidationErrorsList errorsList) throws Exception;
}
