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

import java.util.ArrayList;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.SampleNeonatalDO;
import org.openelis.entity.SampleNeonatal;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.NotFoundException;

@Stateless
@SecurityDomain("openelis")

public class SampleNeonatalBean {
    
    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;
    
    public SampleNeonatalDO fetchBySampleId(Integer id) throws Exception {
        Query query;
        SampleNeonatalDO data;
     
        query = manager.createNamedQuery("SampleNeonatal.FetchBySampleId");
        query.setParameter("id", id);
        try {
            data = (SampleNeonatalDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

        return data;
    }
    
    public ArrayList<SampleNeonatalDO> fetchBySampleIds(ArrayList<Integer> sampleIds) {
        Query query;

        query = manager.createNamedQuery("SampleNeonatal.FetchBySampleIds");
        query.setParameter("ids", sampleIds);

        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    public SampleNeonatalDO add(SampleNeonatalDO data) throws Exception {
        SampleNeonatal entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new SampleNeonatal();
        entity.setSampleId(data.getSampleId());
        entity.setPatientId(data.getPatientId());
        entity.setNextOfKinId(data.getNextOfKinId());
        entity.setNextOfKinRelationId(data.getNextOfKinRelationId());
        entity.setIsNicu(data.getIsNicu());
        entity.setBirthOrderId(data.getBirthOrderId());
        entity.setGestationalAge(data.getGestationalAge());
        entity.setFeedingId(data.getFeedingId());
        entity.setWeight(data.getWeight());
        entity.setIsTransfused(data.getIsTransfused());
        entity.setTransfusionDate(data.getTransfusionDate());
        entity.setTransfusionAge(data.getTransfusionAge());
        entity.setIsRepeat(data.getIsRepeat());
        entity.setCollectionAge(data.getCollectionAge());
        entity.setIsCollectionValid(data.getIsCollectionValid());
        entity.setProviderId(data.getProviderId());
        entity.setBarcodeNumber(data.getBarcodeNumber());
        
        manager.persist(entity);
        
        data.setId(entity.getId());
        
        return data;
    }
    
    public SampleNeonatalDO update(SampleNeonatalDO data) throws Exception {
        SampleNeonatal entity;
        
        if (!data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(SampleNeonatal.class, data.getId());
        entity.setSampleId(data.getSampleId());
        entity.setPatientId(data.getPatientId());
        entity.setNextOfKinId(data.getNextOfKinId());
        entity.setNextOfKinRelationId(data.getNextOfKinRelationId());
        entity.setIsNicu(data.getIsNicu());
        entity.setBirthOrderId(data.getBirthOrderId());
        entity.setGestationalAge(data.getGestationalAge());
        entity.setFeedingId(data.getFeedingId());
        entity.setWeight(data.getWeight());
        entity.setIsTransfused(data.getIsTransfused());
        entity.setTransfusionDate(data.getTransfusionDate());
        entity.setTransfusionAge(data.getTransfusionAge());
        entity.setIsRepeat(data.getIsRepeat());
        entity.setCollectionAge(data.getCollectionAge());
        entity.setIsCollectionValid(data.getIsCollectionValid());
        entity.setProviderId(data.getProviderId());
        entity.setBarcodeNumber(data.getBarcodeNumber());
        
        return data;
    }
    
    public void delete(SampleNeonatalDO data) throws Exception {
        SampleNeonatal entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(SampleNeonatal.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }
    
    public void validate(SampleNeonatalDO data) throws Exception {
        //TODO add logic for validation
    }
}