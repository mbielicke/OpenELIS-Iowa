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
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.entity.AnalysisQaevent;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.AnalysisQAEventLocal;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless

@SecurityDomain("openelis")
public class AnalysisQAEventBean implements AnalysisQAEventLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager manager;
    
    public ArrayList<AnalysisQaEventViewDO> fetchByAnalysisId(Integer analysisId) throws Exception {
        Query query;
        List returnList;
        
        query = manager.createNamedQuery("AnalysisQaevent.FetchByAnalysisId");
        query.setParameter("id", analysisId);
        returnList = query.getResultList();
        
        if(returnList.size() == 0)
            throw new NotFoundException();
        
        return DataBaseUtil.toArrayList(returnList);
    }
    
    public AnalysisQaEventViewDO add(AnalysisQaEventViewDO data) {
        AnalysisQaevent entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new AnalysisQaevent();
        entity.setAnalysisId(data.getAnalysisId());
        entity.setQaeventId(data.getQaEventId());
        entity.setTypeId(data.getTypeId());
        entity.setIsBillable(data.getIsBillable());
        
       manager.persist(entity);
       data.setId(entity.getId());
       
       return data;
        
    }

    public AnalysisQaEventViewDO update(AnalysisQaEventViewDO data) {
        AnalysisQaevent entity;
        
        if ( !data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(AnalysisQaevent.class, data.getId());
        entity.setAnalysisId(data.getAnalysisId());
        entity.setQaeventId(data.getQaEventId());
        entity.setTypeId(data.getTypeId());
        entity.setIsBillable(data.getIsBillable());
        
        return data;
    }
    
    public void delete(AnalysisQaEventViewDO data) {
        AnalysisQaevent entity;
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(AnalysisQaevent.class, data.getId());
        
        if(entity != null)
            manager.remove(entity);
    }
}
