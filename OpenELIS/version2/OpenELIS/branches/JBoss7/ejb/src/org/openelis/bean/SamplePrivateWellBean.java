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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.OrganizationViewDO;
import org.openelis.domain.SamplePrivateWellDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.entity.SamplePrivateWell;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.NotFoundException;

@Stateless
@SecurityDomain("openelis")

public class SamplePrivateWellBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;
    
    @EJB
    private AddressBean address;
    
    @EJB
    private OrganizationBean organization;

    public SamplePrivateWellViewDO fetchBySampleId(Integer sampleId) throws Exception {
        Query query;
        SamplePrivateWellViewDO data;

        query = manager.createNamedQuery("SamplePrivateWell.FetchBySampleId");
        query.setParameter("id", sampleId);
        try {
            data = (SamplePrivateWellViewDO)query.getSingleResult();
            if (data.getOrganizationId() != null)
                data.setOrganization(organization.fetchById(data.getOrganizationId()));
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

        return data;
    }
    
    public ArrayList<SamplePrivateWellViewDO> fetchBySampleIds(ArrayList<Integer> sampleIds) {
        Query query;
        Integer orgId;
        List<SamplePrivateWellViewDO> results;
        ArrayList<SamplePrivateWellViewDO> list;
        ArrayList<OrganizationViewDO> orgs;
        HashMap<Integer, ArrayList<SamplePrivateWellViewDO>> map;

        query = manager.createNamedQuery("SamplePrivateWell.FetchBySampleIds");
        query.setParameter("ids", sampleIds);
        
        results = query.getResultList();

        map = new HashMap<Integer, ArrayList<SamplePrivateWellViewDO>>();
        /*
         * creating the following mapping allows us to fetch in one query all of
         * the primary report to organizations linked to these private well records
         * and put them in the appropriate SamplePrivateWellViewDOs
         */
        for (SamplePrivateWellViewDO data : results) {
            orgId = data.getOrganizationId();
            
            if (orgId == null)
                continue;
            
            list = map.get(orgId);
            if (map.get(orgId) == null) {
                list = new ArrayList<SamplePrivateWellViewDO>();
                map.put(orgId, list);
            } 
            list.add(data);
        }
        
        if (map.size() > 0) {
            orgs = organization.fetchByIds(map.keySet());

            for (OrganizationViewDO org : orgs) {
                list = map.get(org.getId());
                if (list != null) {
                    /*
                     * set the OrganizationViewDOs for the primary report to in the 
                     * SamplePrivateWellViewDOs that link to them
                     */
                    for (SamplePrivateWellViewDO well : list)
                        well.setOrganization(org);                    
                }
            }
        }
        
        return DataBaseUtil.toArrayList(results);
    }

    public SamplePrivateWellDO add(SamplePrivateWellDO data) throws Exception {
        SamplePrivateWell entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new SamplePrivateWell();

        // add the location address and set the id
        if (address.isEmpty(data.getLocationAddress())) 
            data.getLocationAddress().setId(null);
        else 
            address.add(data.getLocationAddress());        
        
        // only one of organization or report to address can be specified  
        if (data.getOrganizationId() == null) {
            if (address.isEmpty(data.getReportToAddress())) 
                data.getReportToAddress().setId(null);
           else 
                address.add(data.getReportToAddress());           
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

    public SamplePrivateWellDO update(SamplePrivateWellDO data) throws Exception {
        SamplePrivateWell entity;

        if ( !data.isChanged() && !data.getLocationAddress().isChanged() &&
            !data.getReportToAddress().isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(SamplePrivateWell.class, data.getId());

        if (address.isEmpty(data.getLocationAddress())) {
            if (data.getLocationAddress().getId() != null) {
                address.delete(data.getLocationAddress());             
                data.getLocationAddress().setId(null);
            }
        } else {
            if (data.getLocationAddress().isChanged()) {
                if (data.getLocationAddress().getId() != null)
                    address.update(data.getLocationAddress());
                else
                    address.add(data.getLocationAddress());
            }
        }

        //
        // Only one of organization or report to address can be specified.
        // Clean up previous address if needed
        //
        if (data.getOrganizationId() != null) {
            if (entity.getReportToAddressId() != null)
                address.delete(entity.getReportToAddressId());
            data.setReportToName(null);
            data.getReportToAddress().setId(null);            
        } else {
            if (address.isEmpty(data.getReportToAddress())) {
                if (entity.getReportToAddressId() != null) {
                    address.delete(entity.getReportToAddressId());             
                    data.getReportToAddress().setId(null);
                }
            } else {
                if (data.getReportToAddress().isChanged()) {
                    if (data.getReportToAddress().getId() != null)
                        address.update(data.getReportToAddress());
                    else
                        address.add(data.getReportToAddress());
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

    public void delete(SamplePrivateWellDO data) throws Exception {
        SamplePrivateWell entity;

        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(SamplePrivateWell.class, data.getId());
        
        if (entity != null) {
            if (data.getLocationAddress().getId() != null)
                address.delete(data.getLocationAddress());
            if (data.getReportToAddress().getId() != null)
                address.delete(data.getReportToAddress());
            manager.remove(entity);
        }              
    }
}