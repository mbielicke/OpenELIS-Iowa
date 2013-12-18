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
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.PWSViolationDO;
import org.openelis.entity.PWSViolation;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.NotFoundException;

@Stateless
@SecurityDomain("openelis")
public class PWSViolationBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;

    @SuppressWarnings("unchecked")
    public ArrayList<PWSViolationDO> fetchByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("PWSViolation.FetchByTinwsysIsNumber");
        query.setParameter("tinwsysIsNumber", tinwsysIsNumber);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    @SuppressWarnings("unchecked")
    public PWSViolationDO fetchByFacilityIdAndSeries(String facilityId, String series,
                                                     Integer tinwsysIsNumber, Date startTime,
                                                     Date endTime) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("PWSViolation.FetchByFacilityIdAndSeries");
        query.setParameter("tinwsysIsNumber", tinwsysIsNumber);
        query.setParameter("facilityId", facilityId);
        query.setParameter("series", series);
        query.setParameter("startTime", startTime);
        query.setParameter("endTime", endTime);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return (PWSViolationDO)list.get(0);
    }

    public ArrayList<PWSViolationDO> fetchAll() throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("PWSViolation.FetchAll");
        list = query.getResultList();

        return DataBaseUtil.toArrayList(list);
    }

    public PWSViolationDO add(PWSViolationDO data) throws Exception {
        PWSViolation entity;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = new PWSViolation();
        entity.setId(data.getId());
        entity.setTinwsysIsNumber(data.getTinwsysIsNumber());
        entity.setFacilityId(data.getFacilityId());
        entity.setSeries(data.getSeries());
        entity.setViolationDate(data.getViolationDate());
        entity.setSampleId(data.getSampleId());

        manager.persist(entity);

        return data;
    }

    public PWSViolationDO update(PWSViolationDO data) throws Exception {
        PWSViolation entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(PWSViolation.class, data.getId());
        entity.setId(data.getId());
        entity.setTinwsysIsNumber(data.getTinwsysIsNumber());
        entity.setFacilityId(data.getFacilityId());
        entity.setSeries(data.getSeries());
        entity.setViolationDate(data.getViolationDate());

        return data;
    }

    public void delete(PWSViolationDO data) throws Exception {
        PWSViolation entity;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(PWSViolation.class, data.getId());

        if (entity != null)
            manager.remove(entity);
    }

    public void deleteList(ArrayList<Integer> deleteList) throws Exception {
        Query query;

        query = manager.createNamedQuery("PWSViolation.DeleteList");
        query.setParameter("deleteList", deleteList);

        query.executeUpdate();
    }
}