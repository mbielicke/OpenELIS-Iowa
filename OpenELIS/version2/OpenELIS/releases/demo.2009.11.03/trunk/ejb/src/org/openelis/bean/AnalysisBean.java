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
//@RolesAllowed("LOCKINGtest")
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
    
    public void add(AnalysisViewDO analysisDO) {
        manager.setFlushMode(FlushModeType.COMMIT);
        
        Analysis analysis = new Analysis();
        
        analysis.setAvailableDate(analysisDO.getAvailableDate());
        analysis.setCompletedDate(analysisDO.getCompletedDate());
        analysis.setIsReportable(analysisDO.getIsReportable());
        analysis.setParentAnalysisId(analysisDO.getParentAnalysisId());
        analysis.setParentResultId(analysisDO.getParentResultId());
        analysis.setPreAnalysisId(analysisDO.getPreAnalysisId());
        analysis.setPrintedDate(analysisDO.getPrintedDate());
        analysis.setReleasedDate(analysisDO.getReleasedDate());
        analysis.setRevision(analysisDO.getRevision());
        analysis.setSampleItemId(analysisDO.getSampleItemId());
        analysis.setSectionId(analysisDO.getSectionId());
        analysis.setStartedDate(analysisDO.getStartedDate());
        analysis.setStatusId(analysisDO.getStatusId());
        analysis.setTestId(analysisDO.getTestId());
        analysis.setUnitOfMeasureId(analysisDO.getUnitOfMeasureId());
        
       manager.persist(analysis);
       analysisDO.setId(analysis.getId());
    }

    public void update(AnalysisViewDO analysisDO) {
        manager.setFlushMode(FlushModeType.COMMIT);
        
        Analysis analysis = manager.find(Analysis.class, analysisDO.getId());
            
        analysis.setAvailableDate(analysisDO.getAvailableDate());
        analysis.setCompletedDate(analysisDO.getCompletedDate());
        analysis.setIsReportable(analysisDO.getIsReportable());
        analysis.setParentAnalysisId(analysisDO.getParentAnalysisId());
        analysis.setParentResultId(analysisDO.getParentResultId());
        analysis.setPreAnalysisId(analysisDO.getPreAnalysisId());
        analysis.setPrintedDate(analysisDO.getPrintedDate());
        analysis.setReleasedDate(analysisDO.getReleasedDate());
        analysis.setRevision(analysisDO.getRevision());
        analysis.setSampleItemId(analysisDO.getSampleItemId());
        analysis.setSectionId(analysisDO.getSectionId());
        analysis.setStartedDate(analysisDO.getStartedDate());
        analysis.setStatusId(analysisDO.getStatusId());
        analysis.setTestId(analysisDO.getTestId());
        analysis.setUnitOfMeasureId(analysisDO.getUnitOfMeasureId());
    }
    
    public void delete(AnalysisViewDO analysisDO) {
        manager.setFlushMode(FlushModeType.COMMIT);
        
        Analysis analysis = manager.find(Analysis.class, analysisDO.getId());
        
        if(analysis != null)
            manager.remove(analysis);
    }
}
