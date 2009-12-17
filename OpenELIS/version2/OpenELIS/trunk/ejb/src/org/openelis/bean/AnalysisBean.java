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

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.entity.Analysis;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.AnalysisLocal;

@Stateless

@SecurityDomain("openelis")
public class AnalysisBean implements AnalysisLocal{

    @PersistenceContext(name = "openelis")
    private EntityManager manager;
   
    @Resource
    private SessionContext ctx;
    
    public List fetchBySampleItemId(Integer sampleItemId) throws Exception{
        Query query = manager.createNamedQuery("Analysis.AnalysisTestBySampleItemId");
        query.setParameter("id", sampleItemId);
        
        List returnList = query.getResultList();
        if(returnList.size() == 0)
            throw new NotFoundException();
        
        return returnList;
    }
    
    public AnalysisViewDO add(AnalysisViewDO data) {
        Analysis entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new Analysis();
        entity.setAvailableDate(data.getAvailableDate());
        entity.setCompletedDate(data.getCompletedDate());
        entity.setIsReportable(data.getIsReportable());
        entity.setParentAnalysisId(data.getParentAnalysisId());
        entity.setParentResultId(data.getParentResultId());
        entity.setPreAnalysisId(data.getPreAnalysisId());
        entity.setPrintedDate(data.getPrintedDate());
        entity.setReleasedDate(data.getReleasedDate());
        entity.setRevision(data.getRevision());
        entity.setSampleItemId(data.getSampleItemId());
        entity.setSectionId(data.getSectionId());
        entity.setStartedDate(data.getStartedDate());
        entity.setStatusId(data.getStatusId());
        entity.setTestId(data.getTestId());
        entity.setUnitOfMeasureId(data.getUnitOfMeasureId());
        
       manager.persist(entity);
       data.setId(entity.getId());
       
       return data;
    }

    public AnalysisViewDO update(AnalysisViewDO data) {
        Analysis entity;
        
        if ( !data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(Analysis.class, data.getId());
        entity.setAvailableDate(data.getAvailableDate());
        entity.setCompletedDate(data.getCompletedDate());
        entity.setIsReportable(data.getIsReportable());
        entity.setParentAnalysisId(data.getParentAnalysisId());
        entity.setParentResultId(data.getParentResultId());
        entity.setPreAnalysisId(data.getPreAnalysisId());
        entity.setPrintedDate(data.getPrintedDate());
        entity.setReleasedDate(data.getReleasedDate());
        entity.setRevision(data.getRevision());
        entity.setSampleItemId(data.getSampleItemId());
        entity.setSectionId(data.getSectionId());
        entity.setStartedDate(data.getStartedDate());
        entity.setStatusId(data.getStatusId());
        entity.setTestId(data.getTestId());
        entity.setUnitOfMeasureId(data.getUnitOfMeasureId());
        
        return data;
    }
    
    public void delete(AnalysisViewDO data) {
        Analysis entity;
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(Analysis.class, data.getId());
        
        if(entity != null)
            manager.remove(entity);
    }
}
