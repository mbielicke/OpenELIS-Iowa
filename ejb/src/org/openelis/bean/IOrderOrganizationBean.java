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
import org.openelis.domain.IOrderOrganizationDO;
import org.openelis.domain.IOrderOrganizationViewDO;
import org.openelis.entity.IOrderOrganization;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;

@Stateless
@SecurityDomain("openelis")
public class IOrderOrganizationBean {
    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;

    public ArrayList<IOrderOrganizationViewDO> fetchByIorderId(Integer orderId) throws Exception {
        List<IOrderOrganizationViewDO> returnList;
        Query query;

        query = manager.createNamedQuery("IOrderOrganization.FetchByIorderId");
        query.setParameter("id", orderId);
        returnList = query.getResultList();

        if (returnList.size() == 0)
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(returnList);
    }

    public ArrayList<IOrderOrganizationViewDO> fetchByIorderIds(ArrayList<Integer> orderIds) {
        Query query;

        query = manager.createNamedQuery("IOrderOrganization.FetchByIorderIds");
        query.setParameter("ids", orderIds);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public IOrderOrganizationDO add(IOrderOrganizationDO data) throws Exception {
        IOrderOrganization entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new IOrderOrganization();
        entity.setIorderId(data.getIorderId());
        entity.setOrganizationId(data.getOrganizationId());
        entity.setOrganizationAttention(data.getOrganizationAttention());
        entity.setTypeId(data.getTypeId());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public IOrderOrganizationDO update(IOrderOrganizationDO data) throws Exception {
        IOrderOrganization entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(IOrderOrganization.class, data.getId());
        entity.setIorderId(data.getIorderId());
        entity.setOrganizationId(data.getOrganizationId());
        entity.setOrganizationAttention(data.getOrganizationAttention());
        entity.setTypeId(data.getTypeId());

        return data;
    }

    public void delete(IOrderOrganizationDO data) throws Exception {
        IOrderOrganization entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(IOrderOrganization.class, data.getId());

        if (entity != null)
            manager.remove(entity);
    }

    public void validate(IOrderOrganizationDO data) throws Exception {
        Integer iorderId;
        ValidationErrorsList list;

        /*
         * for display
         */
        iorderId = data.getIorderId();
        if (iorderId == null)
            iorderId = 0;

        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getTypeId()))
            list.add(new FormErrorException(Messages.get()
                                                    .order_organizationTypeRequiredException(iorderId)));
        if (DataBaseUtil.isEmpty(data.getOrganizationId()))
            list.add(new FormErrorException(Messages.get()
                                                    .order_organizationRequiredException(iorderId)));

        if (list.size() > 0)
            throw list;
    }
}