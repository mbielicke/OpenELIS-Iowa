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

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.entity.SampleProject;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.SampleProjectLocal;

@Stateless
@SecurityDomain("openelis")

public class SampleProjectBean implements SampleProjectLocal {
    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;
   
    public ArrayList<SampleProjectViewDO> fetchBySampleId(Integer sampleId) throws Exception {
        Query query;
        List<SampleProjectViewDO> returnList;
        
        query = manager.createNamedQuery("SampleProject.FetchBySampleId");
        query.setParameter("id", sampleId);
        returnList = query.getResultList();
        
        if(returnList.size() == 0)
            throw new NotFoundException();
        
        return DataBaseUtil.toArrayList(returnList);
    }
    
    public ArrayList<SampleProjectViewDO> fetchPermanentBySampleId(Integer sampleId) throws Exception {
        Query query;
        List<SampleProjectViewDO> returnList;
        
        query = manager.createNamedQuery("SampleProject.FetchPermanentBySampleId");
        query.setParameter("id", sampleId);
        returnList = query.getResultList();
        
        if(returnList.size() == 0)
            throw new NotFoundException();
        
        return DataBaseUtil.toArrayList(returnList);
    }
    
    public ArrayList<IdNameVO> fetchPermanentProjectList() throws Exception {
        Query query;
        
        query = manager.createNamedQuery("SampleProject.FetchPermanentProjectList");
        
        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public SampleProjectViewDO add(SampleProjectViewDO data) {
        SampleProject entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new SampleProject();
        entity.setSampleId(data.getSampleId());
        entity.setProjectId(data.getProjectId());
        entity.setIsPermanent(data.getIsPermanent());
        
        manager.persist(entity);
        data.setId(entity.getId());
        
        return data;
    }

    public SampleProjectViewDO update(SampleProjectViewDO data) {
        SampleProject entity;
        
        if (!data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(SampleProject.class, data.getId());
        entity.setSampleId(data.getSampleId());
        entity.setProjectId(data.getProjectId());
        entity.setIsPermanent(data.getIsPermanent());
        
        return data;
    }

    public void delete(SampleProjectViewDO data) {
        SampleProject entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(SampleProject.class, data.getId());
        
        if(entity != null)
            manager.remove(entity);
    }
}
