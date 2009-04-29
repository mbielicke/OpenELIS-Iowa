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

public class SampleEnvironmentalManager implements RPC, SampleDomainInt {

    private static final long                           serialVersionUID = 1L;
    protected boolean                                   cached;
    protected Integer                                   sampleId;
    protected SampleEnvironmentalDO                     environmental;

    protected transient SampleEnvironmentalManagerIOInt manager;

    /**
     * Creates a new instance of this object.
     */
    public static SampleEnvironmentalManager getInstance() {
        SampleEnvironmentalManager sem;

        sem = new SampleEnvironmentalManager();
        sem.environmental = new SampleEnvironmentalDO();

        return sem;
    }

    public static SampleEnvironmentalManager findBySampleId(Integer sampleId) {
        SampleEnvironmentalManager sem;

        sem = new SampleEnvironmentalManager();
        sem.environmental = new SampleEnvironmentalDO();
        sem.setSampleId(sampleId);

        sem.fetch();

        return sem;
    }

    // setters/getters
    public void setSampleId(Integer id) {
        sampleId = id;
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public SampleEnvironmentalDO getEnvironmental() {
        fetch();
        
        return environmental;
    }

    // manager methods
    public Integer update() {
        Integer newId = manager().update(this);
        environmental.setId(newId);

        return newId;
    }

    private void fetch() {
        if (cached)
            return;

        cached = true;
        
        environmental = manager().fetch(sampleId);
    }

    private SampleEnvironmentalManagerIOInt manager() {
        if (manager == null)
            manager = ManagerFactory.getSampleEnvironmentalManagerIO();

        return manager;
    }
}
