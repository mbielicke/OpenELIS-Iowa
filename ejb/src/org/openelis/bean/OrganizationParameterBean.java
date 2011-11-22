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

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.entity.OrganizationParameter;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.OrganizationParameterLocal;
import org.openelis.meta.OrganizationMeta;

@Stateless
@SecurityDomain("openelis")

public class OrganizationParameterBean implements OrganizationParameterLocal {

    @PersistenceContext(unitName = "openelis")
    private EntityManager                    manager;

    public ArrayList<OrganizationParameterDO> fetchByOrganizationId(Integer id) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("OrganizationParameter.FetchByOrganizationId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    public OrganizationParameterDO add(OrganizationParameterDO data) throws Exception {
        OrganizationParameter entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new OrganizationParameter();
        entity.setOrganizationId(data.getOrganizationId());
        entity.setTypeId(data.getTypeId());
        entity.setValue(data.getValue());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public OrganizationParameterDO update(OrganizationParameterDO data) throws Exception {
        OrganizationParameter entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(OrganizationParameter.class, data.getId());
        entity.setTypeId(data.getTypeId());
        entity.setValue(data.getValue());

        return data;
    }

    public void delete(OrganizationParameterDO data) throws Exception {
        OrganizationParameter entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(OrganizationParameter.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validate(OrganizationParameterDO data) throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getTypeId()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             OrganizationMeta.getOrganizationParameterTypeId()));
        if (DataBaseUtil.isEmpty(data.getValue()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             OrganizationMeta.getOrganizationParameterValue()));
        if (list.size() > 0)
            throw list;
    }
}
