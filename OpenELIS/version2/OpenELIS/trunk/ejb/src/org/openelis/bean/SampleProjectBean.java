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

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.entity.SampleProject;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.SampleProjectLocal;

@Stateless

@SecurityDomain("openelis")
public class SampleProjectBean implements SampleProjectLocal {
    @PersistenceContext(name = "openelis")
    private EntityManager manager;
   
    public List<SampleProjectViewDO> fetchBySampleId(Integer sampleId) throws Exception {
        Query query = manager.createNamedQuery("SampleProject.SampleProjectBySampleId");
        query.setParameter("id", sampleId);
        List<SampleProjectViewDO> returnList = query.getResultList();
        
        if(returnList.size() == 0)
            throw new NotFoundException();
        
        return returnList;
    }

    public void add(SampleProjectViewDO sampleProjectDO) {
        manager.setFlushMode(FlushModeType.COMMIT);
        
        SampleProject project = new SampleProject();
        
        project.setIsPermanent(sampleProjectDO.getIsPermanent());
        project.setProjectId(sampleProjectDO.getProjectId());
        project.setSampleId(sampleProjectDO.getSampleId());
        
        manager.persist(project);
        sampleProjectDO.setId(project.getId());
    }

    public void update(SampleProjectViewDO sampleProjectDO) {
        manager.setFlushMode(FlushModeType.COMMIT);
        
        SampleProject project = manager.find(SampleProject.class, sampleProjectDO.getId());
        
        project.setIsPermanent(sampleProjectDO.getIsPermanent());
        project.setProjectId(sampleProjectDO.getProjectId());
        project.setSampleId(sampleProjectDO.getSampleId());
    }

    public void delete(SampleProjectViewDO sampleProjectDO) {
        manager.setFlushMode(FlushModeType.COMMIT);
        
        SampleProject project = manager.find(SampleProject.class, sampleProjectDO.getId());
        
        if(project != null)
            manager.remove(project);
    }
}
