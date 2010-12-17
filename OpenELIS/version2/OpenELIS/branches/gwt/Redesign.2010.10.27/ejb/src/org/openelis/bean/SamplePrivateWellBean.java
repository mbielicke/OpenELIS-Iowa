/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.bean;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.entity.SamplePrivateWell;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.AddressLocal;
import org.openelis.local.OrganizationLocal;
import org.openelis.local.SamplePrivateWellLocal;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("sampleprivatewell-select")
public class SamplePrivateWellBean implements SamplePrivateWellLocal {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;
    
    @EJB private AddressLocal addressBean;
    
    @EJB private OrganizationLocal organizationBean;

    public SamplePrivateWellViewDO fetchBySampleId(Integer sampleId) throws Exception {
        Query query;
        SamplePrivateWellViewDO data;

        query = manager.createNamedQuery("SamplePrivateWell.FetchBySampleId");
        query.setParameter("id", sampleId);
        try {
            data = (SamplePrivateWellViewDO)query.getSingleResult();
            if (data.getOrganizationId() != null)
                data.setOrganization(organizationBean.fetchById(data.getOrganizationId()));
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

        return data;
    }

    public SamplePrivateWellViewDO add(SamplePrivateWellViewDO data) throws Exception {
        SamplePrivateWell entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new SamplePrivateWell();

        // add the location address and set the id
        if (addressBean.isEmpty(data.getLocationAddress())) 
            data.getLocationAddress().setId(null);
        else 
            addressBean.add(data.getLocationAddress());        
        
        // only one of organization or report to address can be specified  
        if (data.getOrganizationId() == null) {
            if (addressBean.isEmpty(data.getReportToAddress())) 
                data.getReportToAddress().setId(null);
           else 
                addressBean.add(data.getReportToAddress());           
        }

        entity.setSampleId(data.getSampleId()); 
        entity.setOrganizationId(data.getOrganizationId());
        entity.setReportToName(data.getReportToName());
        entity.setReportToAttention(data.getReportToAttention());
        entity.setReportToAddressId(data.getReportToAddress().getId()); 
        entity.setLocation(data.getLocation());
        entity.setLocationAddressId(data.getLocationAddress().getId());
        entity.setOwner(data.getOwner());
        entity.setCollector(data.getCollector());
        entity.setWellNumber(data.getWellNumber());

        manager.persist(entity);

        data.setId(entity.getId());

        return data;
    }

    public SamplePrivateWellViewDO update(SamplePrivateWellViewDO data) throws Exception {
        SamplePrivateWell entity;

        if ( !data.isChanged() && !data.getLocationAddress().isChanged() &&
            !data.getReportToAddress().isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(SamplePrivateWell.class, data.getId());

        if (addressBean.isEmpty(data.getLocationAddress())) {
            if (data.getLocationAddress().getId() != null) {
                addressBean.delete(data.getLocationAddress());             
                data.getLocationAddress().setId(null);
            }
        } else {
            if (data.getLocationAddress().isChanged()) {
                entity.setAuditLocationAddressId(true);
                addressBean.update(data.getLocationAddress());
            }
        }

        //
        // Only one of organization or report to address can be specified.
        // Clean up previous address if needed
        //
        if (data.getOrganizationId() != null) {
            if (entity.getReportToAddressId() != null)
                addressBean.delete(entity.getReportToAddressId());
            data.setReportToName(null);
            data.getReportToAddress().setId(null);            
        } else {
            if (data.getReportToAddress().getId() == null) {
                if ( !addressBean.isEmpty(data.getReportToAddress())) 
                    addressBean.add(data.getReportToAddress());                                    
            } else {
                if (data.getReportToAddress().isChanged()) {
                    entity.setAuditReportToAddressId(true);
                    addressBean.update(data.getReportToAddress());
                }
            }
        }

        entity.setSampleId(data.getSampleId()); 
        entity.setOrganizationId(data.getOrganizationId());
        entity.setReportToName(data.getReportToName());
        entity.setReportToAttention(data.getReportToAttention());
        entity.setReportToAddressId(data.getReportToAddress().getId()); 
        entity.setLocation(data.getLocation());
        entity.setLocationAddressId(data.getLocationAddress().getId());
        entity.setOwner(data.getOwner());
        entity.setCollector(data.getCollector());
        entity.setWellNumber(data.getWellNumber());

        return data;
    }
}
