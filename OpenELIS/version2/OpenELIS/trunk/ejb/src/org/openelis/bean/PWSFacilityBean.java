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

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.PWSFacilityDO;
import org.openelis.entity.PWSFacility;
import org.openelis.entity.PWSFacility.PK;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.NotFoundException;

@Stateless
@SecurityDomain("openelis")
public class PWSFacilityBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;

    @SuppressWarnings("unchecked")
    public ArrayList<PWSFacilityDO> fetchByTinwsysIsNumber(Integer tinwsysIsNumber) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("PWSFacility.FetchByTinwsysIsNumber");
        query.setParameter("tinwsysIsNumber", tinwsysIsNumber);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<PWSFacilityDO> fetchAll() throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("PWSFacility.FetchAll");
        list = query.getResultList();

        return DataBaseUtil.toArrayList(list);
    }

    public PWSFacilityDO add(PWSFacilityDO data) throws Exception {
        PWSFacility entity;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = new PWSFacility();
        entity.setTinwsfIsNumber(data.getTinwsfIsNumber());
        entity.setTsasmpptIsNumber(data.getTsasmpptIsNumber());
        entity.setTinwsysIsNumber(data.getTinwsysIsNumber());
        entity.setName(data.getName());
        entity.setTypeCode(data.getTypeCode());
        entity.setStAsgnIdentCd(data.getStAsgnIdentCd());
        entity.setActivityStatusCd(data.getActivityStatusCd());
        entity.setWaterTypeCode(data.getWaterTypeCode());
        entity.setAvailabilityCode(data.getAvailabilityCode());
        entity.setIdentificationCd(data.getIdentificationCd());
        entity.setDescriptionText(data.getDescriptionText());
        entity.setSourceTypeCode(data.getSourceTypeCode());

        manager.persist(entity);

        return data;
    }

    public PWSFacilityDO update(PWSFacilityDO data) throws Exception {
        PWSFacility entity;
        PK pk;

        if ( !data.isChanged())
            return data;

        pk = new PK(data.getTinwsfIsNumber(), data.getTsasmpptIsNumber());
        entity = manager.find(PWSFacility.class, pk);
        entity.setTinwsysIsNumber(data.getTinwsysIsNumber());
        entity.setName(data.getName());
        entity.setTypeCode(data.getTypeCode());
        entity.setStAsgnIdentCd(data.getStAsgnIdentCd());
        entity.setActivityStatusCd(data.getActivityStatusCd());
        entity.setWaterTypeCode(data.getWaterTypeCode());
        entity.setAvailabilityCode(data.getAvailabilityCode());
        entity.setIdentificationCd(data.getIdentificationCd());
        entity.setDescriptionText(data.getDescriptionText());
        entity.setSourceTypeCode(data.getSourceTypeCode());
        return data;
    }

    public void delete(PWSFacilityDO data) throws Exception {
        PWSFacility entity;
        PK pk;

        manager.setFlushMode(FlushModeType.COMMIT);
        pk = new PK(data.getTinwsfIsNumber(), data.getTsasmpptIsNumber());
        entity = manager.find(PWSFacility.class, pk);

        if (entity != null)
            manager.remove(entity);
    }

}
