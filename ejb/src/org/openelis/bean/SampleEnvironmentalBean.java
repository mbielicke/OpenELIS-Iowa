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

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.entity.SampleEnvironmental;
import org.openelis.local.AddressLocal;
import org.openelis.local.SampleEnvironmentalLocal;

@Stateless

@SecurityDomain("openelis")
@RolesAllowed("sampleenvironmental-select")
public class SampleEnvironmentalBean implements SampleEnvironmentalLocal {
    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;
    
    @EJB private AddressLocal addressBean;
    
    public SampleEnvironmentalDO fetchBySampleId(Integer id) throws Exception {
        Query query;
        
        query = manager.createNamedQuery("SampleEnvironmental.FetchBySampleId");
        query.setParameter("id", id);

        return (SampleEnvironmentalDO) query.getSingleResult();
    }

    public void add(SampleEnvironmentalDO data) throws Exception {
        SampleEnvironmental entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new SampleEnvironmental();
        
        addressBean.add(data.getLocationAddressDO());
        
        entity.setLocationAddressId(data.getLocationAddressDO().getId());
        entity.setCollector(data.getCollector());
        entity.setCollectorPhone(data.getCollectorPhone());
        entity.setDescription(data.getDescription());
        entity.setIsHazardous(data.getIsHazardous());
        entity.setPriority(data.getPriority());
        entity.setSampleId(data.getSampleId());
        entity.setLocation(data.getLocation());
        
        manager.persist(entity);
        
        data.setId(entity.getId());
    }

    public void update(SampleEnvironmentalDO data) throws Exception {
        SampleEnvironmental entity;
        
        if (!data.isChanged())
            return;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(SampleEnvironmental.class, data.getId());
        
        addressBean.update(data.getLocationAddressDO());
        
        entity.setLocationAddressId(data.getLocationAddressDO().getId());
        entity.setCollector(data.getCollector());
        entity.setCollectorPhone(data.getCollectorPhone());
        entity.setDescription(data.getDescription());
        entity.setIsHazardous(data.getIsHazardous());
        entity.setPriority(data.getPriority());
        entity.setSampleId(data.getSampleId());
        entity.setLocation(data.getLocation());
    }

    public void validate() throws Exception {
    }
}
