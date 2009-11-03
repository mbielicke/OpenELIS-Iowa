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
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.entity.SampleOrganization;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.SampleOrganizationLocal;

@Stateless

@SecurityDomain("openelis")
//@RolesAllowed("LOCKINGtest")
public class SampleOrganizationBean implements SampleOrganizationLocal {
    @PersistenceContext(name = "openelis")
    private EntityManager manager;
   
    public List<SampleOrganizationViewDO> fetchBySampleId(Integer sampleId) throws Exception {
        Query query = manager.createNamedQuery("SampleOrg.SampleOrgBySampleId");
        query.setParameter("id", sampleId);
 
        List<SampleOrganizationViewDO> returnList = query.getResultList();
        
        if(returnList.size() == 0)
            throw new NotFoundException();
        
        return returnList;
    }
    
    public void add(SampleOrganizationViewDO sampleOrgDO) {
        manager.setFlushMode(FlushModeType.COMMIT);
        
        SampleOrganization organization = new SampleOrganization();
        
        organization.setOrganizationId(sampleOrgDO.getOrganizationId());
        organization.setSampleId(sampleOrgDO.getSampleId());
        organization.setTypeId(sampleOrgDO.getTypeId());
        
        manager.persist(organization);
        sampleOrgDO.setId(organization.getId());
    }

    public void update(SampleOrganizationViewDO sampleOrgDO) {
        manager.setFlushMode(FlushModeType.COMMIT);
        
        SampleOrganization organization = manager.find(SampleOrganization.class, sampleOrgDO.getId());
        
        organization.setOrganizationId(sampleOrgDO.getOrganizationId());
        organization.setSampleId(sampleOrgDO.getSampleId());
        organization.setTypeId(sampleOrgDO.getTypeId());
    }
    
    public void delete(SampleOrganizationViewDO sampleOrgDO) {
        manager.setFlushMode(FlushModeType.COMMIT);
        
        SampleOrganization organization = manager.find(SampleOrganization.class, sampleOrgDO.getId());
        
        if(organization != null)
            manager.remove(organization);
    }    
}
