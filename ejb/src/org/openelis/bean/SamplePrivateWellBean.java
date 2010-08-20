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
import org.openelis.domain.AddressDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.entity.SamplePrivateWell;
import org.openelis.local.AddressLocal;
import org.openelis.local.SamplePrivateWellLocal;

@Stateless

@SecurityDomain("openelis")
@RolesAllowed("sampleprivatewell-select")
public class SamplePrivateWellBean implements SamplePrivateWellLocal {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;
    
    @EJB private AddressLocal addressBean;

    public SamplePrivateWellViewDO fetchBySampleId(Integer sampleId) throws Exception {
        Query query;
        SamplePrivateWellViewDO result;
        
        query = manager.createNamedQuery("SamplePrivateWell.FetchBySampleId");
        query.setParameter("id", sampleId);

        result = (SamplePrivateWellViewDO)query.getSingleResult();
        
        return result;
    }
    
    public void add(SamplePrivateWellViewDO data) throws Exception {
        SamplePrivateWell entity;
        AddressDO adDO;
        
        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new SamplePrivateWell();
        
        //add the location address and set the id
        adDO = addressBean.add(data.getLocationAddressDO());
        data.setLocationAddressId(adDO.getId());
        
        entity.setCollector(data.getCollector());
        entity.setLocation(data.getLocation());
        entity.setLocationAddressId(data.getLocationAddressId());
        entity.setOrganizationId(data.getOrganizationId());
        entity.setOwner(data.getOwner());
        entity.setReportToAddressId(data.getReportToAddressId());
        entity.setReportToName(data.getReportToName());
        entity.setReportToAttention(data.getReportToAttention());
        entity.setSampleId(data.getSampleId());
        entity.setWellNumber(data.getWellNumber());
        
        manager.persist(entity);
        
        data.setId(entity.getId());
        
    }

    public void update(SamplePrivateWellViewDO data) throws Exception {
        SamplePrivateWell entity;
        
        if (!data.isChanged())
            return;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(SamplePrivateWell.class, data.getId());
        
        addressBean.update(data.getLocationAddressDO());
        
        entity.setCollector(data.getCollector());
        entity.setLocation(data.getLocation());
        entity.setLocationAddressId(data.getLocationAddressId());
        entity.setOrganizationId(data.getOrganizationId());
        entity.setOwner(data.getOwner());
        entity.setReportToAddressId(data.getReportToAddressId());
        entity.setReportToName(data.getReportToName());
        entity.setReportToAttention(data.getReportToAttention());
        entity.setSampleId(data.getSampleId());
        entity.setWellNumber(data.getWellNumber());
    }
}
