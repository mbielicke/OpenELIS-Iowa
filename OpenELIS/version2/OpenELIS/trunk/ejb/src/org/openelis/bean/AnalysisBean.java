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
import org.openelis.domain.AnalysisTestDO;
import org.openelis.entity.Analysis;
import org.openelis.local.AnalysisLocal;
import org.openelis.manager.AnalysesManager;
import org.openelis.remote.AnalysisRemote;

@Stateless

@SecurityDomain("openelis")
//@RolesAllowed("LOCKINGtest")
public class AnalysisBean implements AnalysisRemote, AnalysisLocal{

    @PersistenceContext(name = "openelis")
    private EntityManager manager;
   
    @Resource
    private SessionContext ctx;
    
    public List getAnalysisTestsBySampleItemId(Integer sampleItemId) {
        Query query = manager.createNamedQuery("Analysis.AnalysisTestBySampleItemId");
        query.setParameter("id", sampleItemId);
 
        return query.getResultList();
    }

    public void update(AnalysesManager analyses) {
        //validate analyses
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        for(int i=0; i<analyses.count(); i++){
            //update the sample item
            AnalysisTestDO analysisDO = analyses.getAnalysisAt(i);
            Analysis analysis = null;
            if (analysisDO.getId() == null)
                analysis = new Analysis();
            else
                analysis = manager.find(Analysis.class, analysisDO.getId());
            
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
            
            if(analysis.getId() == null)
                manager.persist(analysis);
        }
    }
}
