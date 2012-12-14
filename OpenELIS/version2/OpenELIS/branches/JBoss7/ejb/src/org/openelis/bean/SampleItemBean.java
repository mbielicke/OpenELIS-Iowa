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
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.SampleItemDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.entity.SampleItem;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.SampleItemLocal;

@Stateless
@SecurityDomain("openelis")

public class SampleItemBean implements SampleItemLocal {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;
   
    public SampleItemViewDO fetchById(Integer id) throws Exception {
        Query query;
        SampleItemViewDO data;
        
        query = manager.createNamedQuery("SampleItem.FetchById");
        query.setParameter("id", id);
        try {
            data = (SampleItemViewDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }
    
    public ArrayList<SampleItemViewDO> fetchBySampleId(Integer sampleId) throws Exception {
        List<SampleItemViewDO> returnList;
        Query query;
        
        query = manager.createNamedQuery("SampleItem.FetchBySampleId");
        query.setParameter("id", sampleId);
 
        returnList = query.getResultList();
        
        if(returnList.size() == 0)
            throw new NotFoundException();
        
        return DataBaseUtil.toArrayList(returnList);
    }
    
    public ArrayList<SampleItemViewDO> fetchBySampleIds(ArrayList<Integer> sampleIds) {
        Query query;
        
        query = manager.createNamedQuery("SampleItem.FetchBySampleIds");
        query.setParameter("ids", sampleIds);
 
        return DataBaseUtil.toArrayList(query.getResultList());
    }
    
    /**
     * This method returns all of the sample items belonging to the samples that
     * have the analyses with these ids and not just the sample items that are
     * linked to those analysis
     */
    public ArrayList<SampleItemViewDO> fetchByAnalysisIds(ArrayList<Integer> analysisIds) {
        Query query;
        
        query = manager.createNamedQuery("SampleItem.FetchByAnalysisIds");
        query.setParameter("ids", analysisIds);
 
        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public SampleItemDO add(SampleItemDO data){
        SampleItem entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new SampleItem();
        entity.setSampleId(data.getSampleId());
        entity.setSampleItemId(data.getSampleItemId());
        entity.setItemSequence(data.getItemSequence());
        entity.setTypeOfSampleId(data.getTypeOfSampleId());
        entity.setSourceOfSampleId(data.getSourceOfSampleId());
        entity.setSourceOther(data.getSourceOther());
        entity.setContainerId(data.getContainerId());
        entity.setContainerReference(data.getContainerReference());
        entity.setQuantity(data.getQuantity());
        entity.setUnitOfMeasureId(data.getUnitOfMeasureId());
        
       manager.persist(entity);
       data.setId(entity.getId());
       
       return data;
    }
    
    public SampleItemDO update(SampleItemDO data){
        SampleItem entity;
        
        if (!data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(SampleItem.class, data.getId());
        entity.setSampleId(data.getSampleId());
        entity.setSampleItemId(data.getSampleItemId());
        entity.setItemSequence(data.getItemSequence());
        entity.setTypeOfSampleId(data.getTypeOfSampleId());
        entity.setSourceOfSampleId(data.getSourceOfSampleId());
        entity.setSourceOther(data.getSourceOther());
        entity.setContainerId(data.getContainerId());
        entity.setContainerReference(data.getContainerReference());
        entity.setQuantity(data.getQuantity());
        entity.setUnitOfMeasureId(data.getUnitOfMeasureId());
        
        return data;
    }
    
    public void delete(SampleItemDO data){
        SampleItem entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(SampleItem.class, data.getId());
        
        if(entity != null)
            manager.remove(entity);
    }
    
    public void validate() throws Exception {
        
    }
}
