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
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.entity.SampleQaevent;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.SampleQAEventLocal;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless

@SecurityDomain("openelis")
public class SampleQaEventBean implements SampleQAEventLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager manager;
    
    public ArrayList<SampleQaEventViewDO> fetchBySampleId(Integer sampleId) throws Exception {
        Query query;
        List returnList;
        
        query = manager.createNamedQuery("SampleQaevent.FetchBySampleId");
        query.setParameter("id", sampleId);
        returnList = query.getResultList();
        
        if(returnList.size() == 0)
            throw new NotFoundException();
        
        return DataBaseUtil.toArrayList(returnList);
    }
    
    public void add(SampleQaEventViewDO data) {
        SampleQaevent entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new SampleQaevent();
        entity.setSampleId(data.getSampleId());
        entity.setQaeventId(data.getQaEventId());
        entity.setTypeId(data.getTypeId());
        entity.setIsBillable(data.getIsBillable());
        
       manager.persist(entity);
       data.setId(entity.getId());
    }

    public void update(SampleQaEventViewDO data) {
        SampleQaevent entity;
        
        if (!data.isChanged())
            return;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(SampleQaevent.class, data.getId());
        entity.setSampleId(data.getSampleId());
        entity.setQaeventId(data.getQaEventId());
        entity.setTypeId(data.getTypeId());
        entity.setIsBillable(data.getIsBillable());
    }
    
    public void delete(SampleQaEventViewDO data) {
        SampleQaevent entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(SampleQaevent.class, data.getId());
        
        if(entity != null)
            manager.remove(entity);
    }
}
