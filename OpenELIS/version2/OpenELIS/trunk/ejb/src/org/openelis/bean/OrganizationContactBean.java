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
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.OrganizationContactDO;
import org.openelis.entity.OrganizationContact;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.AddressLocal;
import org.openelis.local.OrganizationContactLocal;
import org.openelis.meta.OrganizationMeta;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("organization-select")
public class OrganizationContactBean implements OrganizationContactLocal {

    @PersistenceContext(unitName = "openelis")
    private EntityManager                    manager;

    @EJB
    private AddressLocal                     addressBean;

    @SuppressWarnings("unchecked")
    public ArrayList<OrganizationContactDO> fetchByOrganizationId(Integer id) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("OrganizationContact.FetchByOrganizationId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    public OrganizationContactDO add(OrganizationContactDO data) throws Exception {
        OrganizationContact entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        addressBean.add(data.getAddress());
        entity = new OrganizationContact();        
        entity.setOrganizationId(data.getOrganizationId());
        entity.setContactTypeId(data.getContactTypeId());
        entity.setName(data.getName());
        entity.setAddressId(data.getAddress().getId());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public OrganizationContactDO update(OrganizationContactDO data) throws Exception {
        OrganizationContact entity;

        if ( !data.isChanged() && !data.getAddress().isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(OrganizationContact.class, data.getId());
        entity.setContactTypeId(data.getContactTypeId());
        entity.setName(data.getName());

        if (data.getAddress().isChanged()) {
            entity.setAuditAddressId(true);
            addressBean.update(data.getAddress());
        }
        return data;
    }

    public void delete(OrganizationContactDO data) throws Exception {
        OrganizationContact entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        addressBean.delete(data.getAddress());
        entity = manager.find(OrganizationContact.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validate(OrganizationContactDO data) throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getContactTypeId()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             OrganizationMeta.getContactContactTypeId()));
        if (DataBaseUtil.isEmpty(data.getName()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             OrganizationMeta.getContactName()));
        
        if (list.size() > 0)
            throw list;
    }
}
