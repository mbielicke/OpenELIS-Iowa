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
import org.openelis.constants.Messages;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.OrganizationViewDO;
import org.openelis.entity.Organization;
import org.openelis.meta.OrganizationMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.util.QueryBuilderV2;

@Stateless
@SecurityDomain("openelis")

public class OrganizationBean {
    
    @PersistenceContext(unitName = "openelis")
    private EntityManager                 manager;

    @EJB
    private AddressBean                  address;
    
    @EJB
    private OrganizationParameterBean    organizationParameter;

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
    public ArrayList<OrganizationViewDO> fetchByIds(ArrayList<Integer> ids) {
        Query query;
        List<OrganizationViewDO> o;
        ArrayList<Integer> r;
        
        if (ids.size() == 0)
            return new ArrayList<OrganizationViewDO>();
        
        query = manager.createNamedQuery("Organization.FetchByIds");
        o = new ArrayList<OrganizationViewDO>(); 
        r = DataBaseUtil.createSubsetRange(ids.size());
        for (int i = 0; i < r.size() - 1; i++ ) {
            query.setParameter("ids", ids.subList(r.get(i), r.get(i + 1)));
            o.addAll(query.getResultList());
        }

        return DataBaseUtil.toArrayList(o);
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

    @SuppressWarnings("unchecked")
    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" + OrganizationMeta.getId() +
                          ", " + OrganizationMeta.getName() + ") ");
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

    public OrganizationDO add(OrganizationDO data) throws Exception {
        Organization entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        address.add(data.getAddress());        
        entity = new Organization();
        entity.setIsActive(data.getIsActive());
        entity.setParentOrganizationId(data.getParentOrganizationId());
        entity.setName(data.getName());
        entity.setAddressId(data.getAddress().getId());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public OrganizationDO update(OrganizationDO data) throws Exception {
        Organization entity;

        if ( !data.isChanged() && !data.getAddress().isChanged())
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
        Integer oid;
        ValidationErrorsList list;

        list = new ValidationErrorsList();

        oid = data.getId();
        if (oid == null)
            oid = 0;

        if (DataBaseUtil.isEmpty(data.getName()))
            list.add(new FormErrorException(Messages.get().organization_nameRequiredException(oid)));

        if (DataBaseUtil.isEmpty(data.getAddress().getStreetAddress()))
            list.add(new FormErrorException(Messages.get()
                                                    .organization_addressRequiredException(oid)));

        if (DataBaseUtil.isEmpty(data.getAddress().getCity()))
            list.add(new FormErrorException(Messages.get().organization_cityRequiredException(oid)));
        
        if (DataBaseUtil.isEmpty(data.getAddress().getZipCode()))
            list.add(new FormErrorException(Messages.get().organization_zipCodeRequiredException(oid)));

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
