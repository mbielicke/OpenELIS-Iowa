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
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.SampleOrganizationDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.entity.SampleOrganization;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.NotFoundException;

@Stateless
@SecurityDomain("openelis")
public class SampleOrganizationBean {
    @PersistenceContext(unitName = "openelis")
    private EntityManager    manager;

    public ArrayList<SampleOrganizationViewDO> fetchBySampleId(Integer sampleId) throws Exception {
        List<SampleOrganizationViewDO> returnList;
        Query query;

        query = manager.createNamedQuery("SampleOrganization.FetchBySampleId");
        query.setParameter("id", sampleId);
        returnList = query.getResultList();

        if (returnList.size() == 0)
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(returnList);
    }

    public ArrayList<SampleOrganizationViewDO> fetchBySampleIds(ArrayList<Integer> sampleIds) {
        Query query;
        List<SampleOrganizationViewDO> o;
        ArrayList<Integer> r;

        query = manager.createNamedQuery("SampleOrganization.FetchBySampleIds");
        o = new ArrayList<SampleOrganizationViewDO>();
        r = DataBaseUtil.createSubsetRange(sampleIds.size());
        for (int i = 0; i < r.size() - 1; i++ ) {
            query.setParameter("ids", sampleIds.subList(r.get(i), r.get(i + 1)));
            o.addAll(query.getResultList());
        }

        return DataBaseUtil.toArrayList(o);
    }

    public SampleOrganizationViewDO fetchReportToBySampleId(Integer sampleId) throws Exception {
        Query query;

        query = manager.createNamedQuery("SampleOrganization.FetchReportToBySampleId");
        query.setParameter("id", sampleId);
        try {
            return (SampleOrganizationViewDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException(Messages.get().noRecordsFound());
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    public SampleOrganizationDO add(SampleOrganizationDO data) throws Exception {
        SampleOrganization entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new SampleOrganization();
        entity.setSampleId(data.getSampleId());
        entity.setOrganizationId(data.getOrganizationId());
        entity.setOrganizationAttention(data.getOrganizationAttention());
        entity.setTypeId(data.getTypeId());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public SampleOrganizationDO update(SampleOrganizationDO data) throws Exception {
        SampleOrganization entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(SampleOrganization.class, data.getId());
        entity.setSampleId(data.getSampleId());
        entity.setOrganizationId(data.getOrganizationId());
        entity.setOrganizationAttention(data.getOrganizationAttention());
        entity.setTypeId(data.getTypeId());

        return data;
    }

    public void delete(SampleOrganizationDO data) throws Exception {
        SampleOrganization entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(SampleOrganization.class, data.getId());

        if (entity != null)
            manager.remove(entity);
    }
}