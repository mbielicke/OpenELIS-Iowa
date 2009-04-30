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
import org.openelis.domain.SampleOrganizationDO;
import org.openelis.entity.SampleOrganization;
import org.openelis.local.SampleOrganizationLocal;
import org.openelis.manager.SampleOrganizationsManager;
import org.openelis.remote.SampleOrganizationRemote;

@Stateless

@SecurityDomain("openelis")
//@RolesAllowed("LOCKINGtest")
public class SampleOrganizationBean implements SampleOrganizationRemote, SampleOrganizationLocal {
    @PersistenceContext(name = "openelis")
    private EntityManager manager;
   
    @Resource
    private SessionContext ctx;
    
    public List getOrganizationsBySampleId(Integer sampleId){
        Query query = manager.createNamedQuery("SampleOrg.SampleOrgBySampleId");
        query.setParameter("id", sampleId);
 
        return query.getResultList(); 
    }
    
    public void update(SampleOrganizationsManager sampleOrganizations){
        //validate orgs
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        for(int i=0; i<sampleOrganizations.count(); i++){
            //update the sample item
            SampleOrganizationDO orgDO = sampleOrganizations.getSampleOrganizationAt(i);
            SampleOrganization organization = null;
            if (orgDO.getId() == null)
                organization = new SampleOrganization();
            else
                organization = manager.find(SampleOrganization.class, orgDO.getId());
            
            organization.setOrganizationId(orgDO.getOrganizationId());
            organization.setSampleId(orgDO.getSampleId());
            organization.setTypeId(orgDO.getTypeId());
            
            if(organization.getId() == null)
                manager.persist(organization);
        }
    }
    
    private void validateOrgs() throws Exception {
        
    }
}
