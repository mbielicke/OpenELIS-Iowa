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
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.OrganizationViewDO;
import org.openelis.entity.Organization;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.AddressLocal;
import org.openelis.local.OrganizationLocal;
import org.openelis.meta.OrganizationMeta;
import org.openelis.remote.OrganizationRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("organization-select")
public class OrganizationBean implements OrganizationRemote, OrganizationLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager                    manager;

    @EJB
    private AddressLocal                     addressBean;

    private static final OrganizationMeta meta = new OrganizationMeta();

    public OrganizationViewDO fetchById(Integer id) throws Exception {
        Query query;
        OrganizationViewDO data;
        
        query = manager.createNamedQuery("Organization.FetchById");
        query.setParameter("id", id);
        try {
            data = (OrganizationViewDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<OrganizationViewDO> fetchByIds(Integer... ids) {
        Query query;
        
        query = manager.createNamedQuery("Organization.FetchByIds");
        query.setParameter("ids", ids);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public OrganizationDO fetchActiveById(Integer id) throws Exception {
        Query query;
        
        query = manager.createNamedQuery("Organization.FetchActiveById");
        query.setParameter("id", id);

        try {
            return (OrganizationDO) query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        }
    }

    @SuppressWarnings("unchecked")
    public ArrayList<OrganizationDO> fetchActiveByName(String name, int max) {
        Query query;
        
        query = manager.createNamedQuery("Organization.FetchActiveByName");
        query.setParameter("name", name);
        query.setMaxResults(max);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    @SuppressWarnings("unchecked")
    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" + 
                          OrganizationMeta.getId() + ", " +
                          OrganizationMeta.getName() + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(OrganizationMeta.getName());

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        list = (ArrayList<IdNameVO>)DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();

        return (ArrayList<IdNameVO>)list;
    }


    public OrganizationViewDO add(OrganizationViewDO data) throws Exception {
        Organization entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        // first insert the address so we can reference its id
        addressBean.add(data.getAddress());
        entity = new Organization();
        entity.setAddressId(data.getAddress().getId());
        entity.setIsActive(data.getIsActive());
        entity.setName(data.getName());
        entity.setParentOrganizationId(data.getParentOrganizationId());

        manager.persist(entity);
        data.setId(entity.getId());
        
        return data;
    }

    public OrganizationViewDO update(OrganizationViewDO data) throws Exception {
        Organization entity;
        
        if (!data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        addressBean.update(data.getAddress());
        entity = manager.find(Organization.class, data.getId());
        entity.setIsActive(data.getIsActive());
        entity.setName(data.getName());
        entity.setParentOrganizationId(data.getParentOrganizationId());

        return data;
    }

    public void validate(OrganizationViewDO data) throws Exception {
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getName()))
            list.add(new FieldErrorException("fieldRequiredException", OrganizationMeta.getName()));

        if (DataBaseUtil.isEmpty(data.getAddress().getStreetAddress()))
            list.add(new FieldErrorException("fieldRequiredException", OrganizationMeta.getAddressStreetAddress()));

        if (DataBaseUtil.isEmpty(data.getAddress().getCity()))
            list.add(new FieldErrorException("fieldRequiredException", OrganizationMeta.getAddressCity()));

        if (DataBaseUtil.isEmpty(data.getAddress().getZipCode()))
            list.add(new FieldErrorException("fieldRequiredException", OrganizationMeta.getAddressZipCode()));

        if (list.size() > 0)
            throw list;
    }
}