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
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.SampleEnvironmentalDO;
import org.openelis.entity.SampleEnvironmental;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.NotFoundException;

@Stateless
@SecurityDomain("openelis")

public class SampleEnvironmentalBean  {
    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;

    @EJB
    private AddressBean  addressBean;

    public SampleEnvironmentalDO fetchBySampleId(Integer id) throws Exception {
        Query query;
        SampleEnvironmentalDO data;

        query = manager.createNamedQuery("SampleEnvironmental.FetchBySampleId");
        query.setParameter("id", id);
        try {
            data = (SampleEnvironmentalDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

        return data;
    }
    
    public ArrayList<SampleEnvironmentalDO> fetchBySampleIds(ArrayList<Integer> sampleIds) {
        Query query;
        List<SampleEnvironmentalDO> e;
        ArrayList<Integer> r;

        query = manager.createNamedQuery("SampleEnvironmental.FetchBySampleIds");
        e = new ArrayList<SampleEnvironmentalDO>();        
        r = DataBaseUtil.createSubsetRange(sampleIds.size());
        for (int i = 0; i < r.size() - 1; i++ ) {
            query.setParameter("ids", sampleIds.subList(r.get(i), r.get(i + 1)));
            e.addAll(query.getResultList());
        }

        return DataBaseUtil.toArrayList(e);
    }

    public SampleEnvironmentalDO add(SampleEnvironmentalDO data) throws Exception {
        SampleEnvironmental entity;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = new SampleEnvironmental();
        
        // first insert the address so we can reference its id        
        if (addressBean.isEmpty(data.getLocationAddress()))     
            data.getLocationAddress().setId(null);                                       
        else  
            addressBean.add(data.getLocationAddress());                    
        
        entity.setCollector(data.getCollector());
        entity.setCollectorPhone(data.getCollectorPhone());
        entity.setDescription(data.getDescription());
        entity.setIsHazardous(data.getIsHazardous());
        entity.setPriority(data.getPriority());
        entity.setSampleId(data.getSampleId());
        entity.setLocation(data.getLocation());
        entity.setLocationAddressId(data.getLocationAddress().getId());
        
        manager.persist(entity);
        data.setId(entity.getId());
        
        return data;
    }

    public SampleEnvironmentalDO update(SampleEnvironmentalDO data) throws Exception {
        SampleEnvironmental entity;

        if (! data.isChanged() && !data.getLocationAddress().isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(SampleEnvironmental.class, data.getId());
        if (addressBean.isEmpty(data.getLocationAddress())) {
            if (data.getLocationAddress().getId() != null) {
                addressBean.delete(data.getLocationAddress());                
                data.getLocationAddress().setId(null);
            }
        } else {
            if (data.getLocationAddress().isChanged()) {
                if (data.getLocationAddress().getId() != null)
                    addressBean.update(data.getLocationAddress());
                else
                    addressBean.add(data.getLocationAddress());
            }                       
        }
        
        entity.setCollector(data.getCollector());
        entity.setCollectorPhone(data.getCollectorPhone());
        entity.setDescription(data.getDescription());
        entity.setIsHazardous(data.getIsHazardous());
        entity.setPriority(data.getPriority());
        entity.setSampleId(data.getSampleId());
        entity.setLocation(data.getLocation());
        entity.setLocationAddressId(data.getLocationAddress().getId());
        return data;
    }

    public void delete(SampleEnvironmentalDO data) throws Exception {
        SampleEnvironmental entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = manager.find(SampleEnvironmental.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }
}