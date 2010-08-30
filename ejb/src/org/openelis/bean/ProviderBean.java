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

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.IdFirstLastNameVO;
import org.openelis.domain.ProviderDO;
import org.openelis.entity.Provider;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.ProviderLocal;
import org.openelis.meta.ProviderMeta;
import org.openelis.remote.ProviderRemote;
import org.openelis.util.QueryBuilderV2;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("provider-select")
public class ProviderBean implements ProviderRemote, ProviderLocal {

    @PersistenceContext(unitName = "openelis")
    private EntityManager             manager;

    private static final ProviderMeta meta = new ProviderMeta();

    public ProviderDO fetchById(Integer providerId) throws Exception {
        Query query;
        ProviderDO data;

        query = manager.createNamedQuery("Provider.FetchById");
        query.setParameter("id", providerId);
        try {
            data = (ProviderDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<IdFirstLastNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdFirstLastNameVO(" +
                          ProviderMeta.getId() + "," + ProviderMeta.getLastName() + "," +
                          ProviderMeta.getFirstName() + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(ProviderMeta.getLastName() + "," + ProviderMeta.getFirstName());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        list = (ArrayList<IdFirstLastNameVO>)DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();

        return (ArrayList<IdFirstLastNameVO>)list;
    }

    public ProviderDO add(ProviderDO data) throws Exception {
        Provider entity;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = new Provider();

        entity.setFirstName(data.getFirstName());
        entity.setLastName(data.getLastName());
        entity.setMiddleName(data.getMiddleName());
        entity.setNpi(data.getNpi());
        entity.setTypeId(data.getTypeId());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public ProviderDO update(ProviderDO data) throws Exception {
        Provider entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(Provider.class, data.getId());
        entity.setFirstName(data.getFirstName());
        entity.setLastName(data.getLastName());
        entity.setMiddleName(data.getMiddleName());
        entity.setNpi(data.getNpi());
        entity.setTypeId(data.getTypeId());

        return data;
    }

    public void validate(ProviderDO providerDO) throws ValidationErrorsList {
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(providerDO.getLastName()))
            list.add(new FieldErrorException("fieldRequiredException", ProviderMeta.getLastName()));

        if (DataBaseUtil.isEmpty(providerDO.getTypeId()))
            list.add(new FieldErrorException("fieldRequiredException", ProviderMeta.getTypeId()));

        if (list.size() > 0)
            throw list;
    }
}
