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

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.entity.Analysis;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.AnalysisLocal;
import org.openelis.remote.AnalysisRemote;

@Stateless

@SecurityDomain("openelis")
public class AnalysisBean implements AnalysisLocal, AnalysisRemote {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;
   
    public ArrayList<AnalysisViewDO> fetchBySampleId(Integer sampleId) throws Exception {
        List returnList;
        Query query;
        
        query = manager.createNamedQuery("Analysis.FetchBySampleId");
        query.setParameter("id", sampleId);
        
        returnList = query.getResultList();
        if(returnList.size() == 0)
            throw new NotFoundException();
        
        return DataBaseUtil.toArrayList(returnList);
    }
    
    public ArrayList<AnalysisViewDO> fetchBySampleItemId(Integer sampleItemId) throws Exception{
        List returnList;
        Query query;
        
        query = manager.createNamedQuery("Analysis.FetchBySampleItemId");
        query.setParameter("id", sampleItemId);
        
        returnList = query.getResultList();
        if(returnList.size() == 0)
            throw new NotFoundException();
        
        return DataBaseUtil.toArrayList(returnList);
    }
    
    public AnalysisViewDO fetchById(Integer id) throws Exception {
        Query query;
        AnalysisViewDO data;
        
        query = manager.createNamedQuery("Analysis.FetchById");
        query.setParameter("id", id);
        try {
            data = (AnalysisViewDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }
    
    public AnalysisViewDO add(AnalysisViewDO data) throws Exception{
        Analysis entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new Analysis();
        entity.setSampleItemId(data.getSampleItemId());
        entity.setRevision(data.getRevision());
        entity.setTestId(data.getTestId());
        entity.setSectionId(data.getSectionId());
        entity.setPreAnalysisId(data.getPreAnalysisId());
        entity.setParentAnalysisId(data.getParentAnalysisId());
        entity.setParentResultId(data.getParentResultId());
        entity.setIsReportable(data.getIsReportable());
        entity.setUnitOfMeasureId(data.getUnitOfMeasureId());
        entity.setStatusId(data.getStatusId());
        entity.setAvailableDate(data.getAvailableDate());        
        entity.setStartedDate(data.getStartedDate());
        entity.setCompletedDate(data.getCompletedDate());
        entity.setReleasedDate(data.getReleasedDate());
        entity.setPrintedDate(data.getPrintedDate());
        
        manager.persist(entity);
        data.setId(entity.getId());
       
        return data;
    }

    public AnalysisViewDO update(AnalysisViewDO data) throws Exception {
        Analysis entity;
        
        if ( !data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(Analysis.class, data.getId());
        entity.setSampleItemId(data.getSampleItemId());
        entity.setRevision(data.getRevision());
        entity.setTestId(data.getTestId());
        entity.setSectionId(data.getSectionId());
        entity.setPreAnalysisId(data.getPreAnalysisId());
        entity.setParentAnalysisId(data.getParentAnalysisId());
        entity.setParentResultId(data.getParentResultId());
        entity.setIsReportable(data.getIsReportable());
        entity.setUnitOfMeasureId(data.getUnitOfMeasureId());
        entity.setStatusId(data.getStatusId());
        entity.setAvailableDate(data.getAvailableDate());
        entity.setStartedDate(data.getStartedDate());
        entity.setCompletedDate(data.getCompletedDate());
        entity.setReleasedDate(data.getReleasedDate());
        entity.setPrintedDate(data.getPrintedDate());
        
        return data;
    }
    
    public void updatePrintedDate(Integer id, Datetime timeStamp) throws Exception {
        Analysis entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(Analysis.class, id);
        entity.setPrintedDate(timeStamp);
    }
    
    public void delete(AnalysisViewDO data) {
        Analysis entity;
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(Analysis.class, data.getId());
        
        if(entity != null)
            manager.remove(entity);
    }
}
