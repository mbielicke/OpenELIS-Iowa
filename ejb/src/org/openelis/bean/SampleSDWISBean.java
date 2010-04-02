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
package org.openelis.bean;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.SampleSDWISViewDO;
import org.openelis.entity.SampleSDWIS;
import org.openelis.local.SampleSDWISLocal;

@Stateless

@SecurityDomain("openelis")
@RolesAllowed("samplesdwis-select")
public class SampleSDWISBean implements SampleSDWISLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager manager;
    
    public SampleSDWISViewDO fetchBySampleId(Integer sampleId) throws Exception {
        Query query;
        
        query = manager.createNamedQuery("SampleSDWIS.FetchBySampleId");
        query.setParameter("id", sampleId);

        return (SampleSDWISViewDO) query.getSingleResult();
    }
    
    public void add(SampleSDWISViewDO data) throws Exception {
        SampleSDWIS entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new SampleSDWIS();
        
        entity.setSampleId(data.getSampleId());
        entity.setPwsId(data.getPwsId());
        entity.setStateLabId(data.getStateLabId());
        entity.setFacilityId(data.getFacilityId());
        entity.setSampleTypeId(data.getSampleTypeId());
        entity.setSampleCategoryId(data.getSampleCategoryId());
        entity.setPbSampleTypeId(data.getLeadSampleTypeId());
        entity.setSamplePointId(data.getSamplePointId());
        entity.setLocation(data.getLocation());
        entity.setCollector(data.getCollector());
        entity.setOriginalSampleNumber(data.getOriginalSampleNumber());
        entity.setRepeatCodeId(data.getRepeatCodeId());
        entity.setCompositeIndicator(data.getCompositeIndicator());
        entity.setCompositeSampleNumber(data.getCompositeSampleNumber());
        entity.setCompositeDate(data.getCompositeDate());
        entity.setCompositeSequence(data.getCompositeSequence());
        
        manager.persist(entity);
        
        data.setId(entity.getId());
    }

    public void update(SampleSDWISViewDO data) throws Exception {
        SampleSDWIS entity;
        
        if (!data.isChanged())
            return;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(SampleSDWIS.class, data.getId());
        
        entity.setSampleId(data.getSampleId());
        entity.setPwsId(data.getPwsId());
        entity.setStateLabId(data.getStateLabId());
        entity.setFacilityId(data.getFacilityId());
        entity.setSampleTypeId(data.getSampleTypeId());
        entity.setSampleCategoryId(data.getSampleCategoryId());
        entity.setPbSampleTypeId(data.getLeadSampleTypeId());
        entity.setSamplePointId(data.getSamplePointId());
        entity.setLocation(data.getLocation());
        entity.setCollector(data.getCollector());
        entity.setOriginalSampleNumber(data.getOriginalSampleNumber());
        entity.setRepeatCodeId(data.getRepeatCodeId());
        entity.setCompositeIndicator(data.getCompositeIndicator());
        entity.setCompositeSampleNumber(data.getCompositeSampleNumber());
        entity.setCompositeDate(data.getCompositeDate());
        entity.setCompositeSequence(data.getCompositeSequence());
    }
}
