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
import org.openelis.domain.SampleProjectDO;
import org.openelis.entity.SampleProject;
import org.openelis.local.SampleProjectLocal;
import org.openelis.manager.SampleProjectsManager;
import org.openelis.remote.SampleProjectRemote;

@Stateless

@SecurityDomain("openelis")
//@RolesAllowed("LOCKINGtest")
public class SampleProjectBean implements SampleProjectRemote, SampleProjectLocal {
    @PersistenceContext(name = "openelis")
    private EntityManager manager;
   
    @Resource
    private SessionContext ctx;
    
    public List getProjectsBySampleId(Integer sampleId) {
        Query query = manager.createNamedQuery("SampleProject.SampleProjectBySampleId");
        query.setParameter("id", sampleId);
 
        return query.getResultList(); 
    }

    public void update(SampleProjectsManager sampleProjects) {
        //validate projects
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        for(int i=0; i<sampleProjects.count(); i++){
            //update the sample item
            SampleProjectDO projDO = sampleProjects.getSampleProjectAt(i);
            SampleProject project = null;
            if (projDO.getId() == null)
                project = new SampleProject();
            else
                project = manager.find(SampleProject.class, projDO.getId());
            
            project.setIsPermanent(projDO.getIsPermanent());
            project.setProjectId(projDO.getProjectId());
            project.setSampleId(projDO.getSampleId());
            
            if(project.getId() == null)
                manager.persist(project);
        }
    }
    
    public List autoCompleteLookupByName(String projectName, Integer maxResults) {
        Query query = manager.createNamedQuery("Project.ProjectByName");
        query.setParameter("name", projectName);
        query.setMaxResults(maxResults);
 
        return query.getResultList(); 
    }
    
    private void validateProjects() throws Exception {
        
    }    
}
