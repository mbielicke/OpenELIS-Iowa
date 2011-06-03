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
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.PWSDO;
import org.openelis.domain.SampleSDWISViewDO;
import org.openelis.entity.SampleSDWIS;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.PWSLocal;
import org.openelis.local.SampleSDWISLocal;
import org.openelis.meta.SampleMeta;

@Stateless

@SecurityDomain("openelis")
@RolesAllowed("samplesdwis-select")
public class SampleSDWISBean implements SampleSDWISLocal {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;
    
    @EJB
    private PWSLocal pws;
    
    public SampleSDWISViewDO fetchBySampleId(Integer sampleId) throws Exception {
        Query query;
        SampleSDWISViewDO data;
        PWSDO pwsData;
        
        query = manager.createNamedQuery("SampleSDWIS.FetchBySampleId");
        query.setParameter("id", sampleId);

        try{
            data = (SampleSDWISViewDO) query.getSingleResult();
            pwsData = pws.fetchById(data.getPwsId());
            data.setPwsName(pwsData.getName());
            
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        
        return data;
    }
    
    public SampleSDWISViewDO add(SampleSDWISViewDO data) throws Exception {
        SampleSDWIS entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new SampleSDWIS();
        
        entity.setSampleId(data.getSampleId());
        entity.setPwsId(data.getPwsId());
        entity.setStateLabId(data.getStateLabId());
        entity.setFacilityId(data.getFacilityId());
        entity.setSampleTypeId(data.getSampleTypeId());
        entity.setSampleCategoryId(data.getSampleCategoryId());
        entity.setSamplePointId(data.getSamplePointId());
        entity.setLocation(data.getLocation());
        entity.setCollector(data.getCollector());
        
        manager.persist(entity);
        
        data.setId(entity.getId());
        
        return data;
    }

    public SampleSDWISViewDO update(SampleSDWISViewDO data) throws Exception {
        SampleSDWIS entity;
        
        if (!data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(SampleSDWIS.class, data.getId());
        
        entity.setSampleId(data.getSampleId());
        entity.setPwsId(data.getPwsId());
        entity.setStateLabId(data.getStateLabId());
        entity.setFacilityId(data.getFacilityId());
        entity.setSampleTypeId(data.getSampleTypeId());
        entity.setSampleCategoryId(data.getSampleCategoryId());
        entity.setSamplePointId(data.getSamplePointId());
        entity.setLocation(data.getLocation());
        entity.setCollector(data.getCollector());
        
        return data;
    }

    public void validate(SampleSDWISViewDO data) throws Exception {
        ValidationErrorsList list;
             
        list = new ValidationErrorsList();
        try {
            pws.fetchById(data.getPwsId());
        } catch (NotFoundException e) {            
            list.add(new FieldErrorException("invalidPwsException",
                                                   SampleMeta.getSDWISPwsId()));
        } 
         
        if (list.size() > 0)
            throw list;
    }
}
