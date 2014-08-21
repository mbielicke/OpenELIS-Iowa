package org.openelis.manager;

import org.openelis.ui.common.ValidationErrorsList;

public interface SampleDomainInt {
    
    public void setSampleId(Integer sampleId);
    public SampleDomainInt add() throws Exception;
    public SampleDomainInt update() throws Exception;
    public void delete() throws Exception;
    public void validate() throws Exception;
    public void validate(ValidationErrorsList errorsList) throws Exception;
}
