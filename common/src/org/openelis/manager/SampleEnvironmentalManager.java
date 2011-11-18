/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.manager;

import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ValidationErrorsList;

public class SampleEnvironmentalManager implements RPC, SampleDomainInt {

    private static final long                           serialVersionUID = 1L;
    protected Integer                                   sampleId;
    protected SampleEnvironmentalDO                     environmental;

    protected transient static SampleEnvironmentalManagerProxy proxy;

    /**
     * Creates a new instance of this object.
     */
    public static SampleEnvironmentalManager getInstance() {
        SampleEnvironmentalManager sem;

        sem = new SampleEnvironmentalManager();
        sem.environmental = new SampleEnvironmentalDO();

        return sem;
    }

    public static SampleEnvironmentalManager fetchBySampleId(Integer sampleId) throws Exception {
        return proxy().fetchBySampleId(sampleId);
    }

    // setters/getters
    public SampleEnvironmentalDO getEnvironmental() {
        return environmental;
    }
    
    public void setEnvironmental(SampleEnvironmentalDO envDO) {
        environmental = envDO;
    }
    
    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    public String getDomainDescription() {
        return environmental.getDescription();
    }
    
    // manager methods
    public SampleEnvironmentalManager add() throws Exception {
        return proxy().add(this);
    }
    
    public SampleEnvironmentalManager update() throws Exception {
        return proxy().update(this);
    }
    
    public void delete() throws Exception {
        proxy().delete(this);
    }
    
    public void validate() throws Exception {
        
    }
    
    public void validate(ValidationErrorsList errorsList) throws Exception {
        
    }

    private static SampleEnvironmentalManagerProxy proxy(){
        if(proxy == null) 
            proxy = new SampleEnvironmentalManagerProxy();
        
        return proxy;
    }    
}
