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
import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.OrganizationViewDO;
import org.openelis.entity.Organization;
import org.openelis.entity.Organization_;
import org.openelis.messages.Messages;
import org.openelis.meta.AddressMeta;
import org.openelis.meta.OrganizationMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.util.QueryUtil;

@Stateless
@SecurityDomain("openelis")

public class OrganizationBean {
    
    @PersistenceContext(unitName = "openelis")
    private EntityManager                 manager;

    @EJB
    private AddressBean                  address;
    
    @EJB
    private OrganizationParameterBean    organizationParameter;

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
    public ArrayList<OrganizationViewDO> fetchByIds(Collection<Integer> ids) {
        Query query;
        
        if (ids.size() == 0)
            return new ArrayList<OrganizationViewDO>();
        
        query = manager.createNamedQuery("Organization.FetchByIds");
        query.setParameter("ids", ids);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public OrganizationDO fetchActiveById(Integer id) throws Exception {
        Query query;

        query = manager.createNamedQuery("Organization.FetchActiveById");
        query.setParameter("id", id);

        try {
            return (OrganizationDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
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

//    @SuppressWarnings("unchecked")
//    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
//        Query query;
//        QueryBuilderV2 builder;
//        List list;
//
//        builder = new QueryBuilderV2();
//        builder.setMeta(meta);
//        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" + OrganizationMeta.getId() +
//                          ", " + OrganizationMeta.getName() + ") ");
//        builder.constructWhere(fields);
//        builder.setOrderBy(OrganizationMeta.getName());
//
//        query = manager.createQuery(builder.getEJBQL());
//        query.setMaxResults(first + max);
//        builder.setQueryParams(query, fields);
//
//        list = query.getResultList();
//        if (list.isEmpty())
//            throw new NotFoundException();
//        list = (ArrayList<IdNameVO>)DataBaseUtil.subList(list, first, max);
//        if (list == null)
//            throw new LastPageException();
//
//        return (ArrayList<IdNameVO>)list;
//    }
    
    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        TypedQuery<IdNameVO> query;
        List<IdNameVO> list;
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<IdNameVO> critQuery = builder.createQuery(IdNameVO.class);
        Root<Organization> org = critQuery.from(Organization.class);
        
        critQuery.select(builder.construct(IdNameVO.class,org.get(Organization_.id),org.get(Organization_.name)));
        critQuery.where(new QueryUtil(builder,critQuery,org).createQuery(fields));
        critQuery.orderBy(builder.asc(org.get(Organization_.name)));
        
        query = manager.createQuery(critQuery);
        query.setMaxResults(first + max);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        list = (ArrayList<IdNameVO>)DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();

        return (ArrayList<IdNameVO>)list;
    }

    public OrganizationDO add(OrganizationDO data) throws Exception {
        Organization entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        address.add(data.getAddress());        
        entity = new Organization();
        entity.setIsActive(data.getIsActive());
        //entity.setParentOrganizationId(data.getParentOrganizationId());
        entity.setName(data.getName());
        //entity.setAddressId(data.getAddress().getId());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public OrganizationDO update(OrganizationDO data) throws Exception {
        Organization entity;

        if (!data.isChanged() && !data.getAddress().isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(Organization.class, data.getId());
        entity.setIsActive(data.getIsActive());
        entity.setName(data.getName());
        entity.setParentOrganizationId(data.getParentOrganizationId());

        if (data.getAddress().isChanged())
            address.update(data.getAddress());
        
        return data;
    }

    public void validate(OrganizationDO data) throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getName()))
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(), OrganizationMeta.NAME));                    

        if (DataBaseUtil.isEmpty(data.getAddress().getStreetAddress()))
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             AddressMeta.STREET_ADDRESS));

        if (DataBaseUtil.isEmpty(data.getAddress().getCity()))
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             AddressMeta.CITY));

        if (list.size() > 0)
            throw list;
    }
    
    public boolean hasDontPrintFinalReport(Integer id) throws Exception {
        try {
            organizationParameter.fetchByOrgIdAndDictSystemName(id, "org_no_finalreport");
            return true;
        } catch (NotFoundException e) {
            return false;
        }        
    }
}
