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
import org.openelis.constants.Messages;
import org.openelis.domain.ProviderAnalyteDO;
import org.openelis.domain.ProviderAnalyteViewDO;
import org.openelis.domain.ProviderLocationDO;
import org.openelis.entity.ProviderAnalyte;
import org.openelis.meta.ProviderMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;

@Stateless
@SecurityDomain("openelis")

public class ProviderAnalyteBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager       manager;

    @SuppressWarnings("unchecked")
    public ArrayList<ProviderAnalyteViewDO> fetchByProviderId(Integer id) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("ProviderAnalyte.FetchByProviderId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<ProviderAnalyteViewDO> fetchByProviderIds(ArrayList<Integer> ids) throws Exception {
        Query query;
        List<ProviderAnalyteViewDO> p;
        ArrayList<Integer> r;

        query = manager.createNamedQuery("ProviderAnalyte.FetchByProviderIds");
        p = new ArrayList<ProviderAnalyteViewDO>();
        r = DataBaseUtil.createSubsetRange(ids.size());
        for (int i = 0; i < r.size() - 1; i++ ) {
            query.setParameter("ids", ids.subList(r.get(i), r.get(i + 1)));
            p.addAll(query.getResultList());
        }

        return DataBaseUtil.toArrayList(p);
    }

    public ProviderAnalyteDO add(ProviderAnalyteDO data) throws Exception {
        ProviderAnalyte entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new ProviderAnalyte();
        entity.setProviderId(data.getProviderId());
        entity.setSortOrder(data.getSortOrder());
        entity.setAnalyteId(data.getAnalyteId());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public ProviderAnalyteDO update(ProviderAnalyteDO data) throws Exception {
        ProviderAnalyte entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(ProviderAnalyte.class, data.getId());
        entity.setProviderId(data.getProviderId());
        entity.setSortOrder(data.getSortOrder());
        entity.setAnalyteId(data.getAnalyteId());

        return data;
    }

    public void delete(ProviderAnalyteDO data) throws Exception {
        ProviderAnalyte entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(ProviderAnalyte.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validate(ProviderAnalyteDO data) throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getAnalyteId()))
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             ProviderMeta.getProviderAnalyteId()));
        if (list.size() > 0)
            throw list;
    }
}
