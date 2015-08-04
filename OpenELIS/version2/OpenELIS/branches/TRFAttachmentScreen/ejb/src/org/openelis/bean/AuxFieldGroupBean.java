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
import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.domain.IdNameVO;
import org.openelis.entity.AuxFieldGroup;
import org.openelis.meta.AuxFieldGroupMeta;
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
public class AuxFieldGroupBean { // implements AuxFieldGroupRemote,
    // AuxFieldGroupLocal {

    @PersistenceContext(unitName = "openelis")
    private EntityManager     manager;

    private AuxFieldGroupMeta meta = new AuxFieldGroupMeta();

    public ArrayList<AuxFieldGroupDO> fetchActive() {
        Query query = manager.createNamedQuery("AuxFieldGroup.FetchActive");

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public AuxFieldGroupDO fetchById(Integer id) throws Exception {
        Query query;
        AuxFieldGroupDO data;

        query = manager.createNamedQuery("AuxFieldGroup.FetchById");
        query.setParameter("id", id);
        try {
            data = (AuxFieldGroupDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    public ArrayList<AuxFieldGroupDO> fetchByIds(ArrayList<Integer> ids) {
        Query query;

        query = manager.createNamedQuery("AuxFieldGroup.FetchByIds");
        query.setParameter("ids", ids);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    @SuppressWarnings("unchecked")
    public ArrayList<AuxFieldGroupDO> fetchByName(String name) throws Exception {
        Query query;

        query = manager.createNamedQuery("AuxFieldGroup.FetchByName");
        query.setParameter("name", name);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public AuxFieldGroupDO fetchActiveByName(String name) throws Exception {
        Query query;
        AuxFieldGroupDO data;

        query = manager.createNamedQuery("AuxFieldGroup.FetchActiveByName");
        query.setParameter("name", name);

        try {
            data = (AuxFieldGroupDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {

        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" + AuxFieldGroupMeta.getId() +
                          ", " + AuxFieldGroupMeta.getName() + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(AuxFieldGroupMeta.getName());

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

    public AuxFieldGroupDO add(AuxFieldGroupDO data) throws Exception {
        AuxFieldGroup entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new AuxFieldGroup();
        entity.setName(data.getName());
        entity.setDescription(data.getDescription());
        entity.setIsActive(data.getIsActive());
        entity.setActiveBegin(data.getActiveBegin());
        entity.setActiveEnd(data.getActiveEnd());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public AuxFieldGroupDO update(AuxFieldGroupDO data) throws Exception {
        AuxFieldGroup entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(AuxFieldGroup.class, data.getId());
        entity.setName(data.getName());
        entity.setDescription(data.getDescription());
        entity.setIsActive(data.getIsActive());
        entity.setActiveBegin(data.getActiveBegin());
        entity.setActiveEnd(data.getActiveEnd());

        return data;
    }

    public void validate(AuxFieldGroupDO data) throws Exception {
        boolean checkDuplicate, overlap;
        Integer gid;
        ValidationErrorsList list;
        ArrayList<AuxFieldGroupDO> groups;
        AuxFieldGroupDO group;

        list = new ValidationErrorsList();
        checkDuplicate = true;

        gid = data.getId();
        if (gid == null)
            gid = 0;

        if (DataBaseUtil.isEmpty(data.getName())) {
            list.add(new FormErrorException(Messages.get().aux_nameRequiredException(gid)));
            checkDuplicate = false;
        }

        if (DataBaseUtil.isEmpty(data.getDescription())) {
            list.add(new FormErrorException(Messages.get().aux_descriptionRequiredException(gid)));
            checkDuplicate = false;
        }

        if (DataBaseUtil.isEmpty(data.getActiveBegin())) {
            list.add(new FormErrorException(Messages.get().aux_activeBeginRequiredException(gid)));
            checkDuplicate = false;
        }

        if (DataBaseUtil.isEmpty(data.getActiveEnd())) {
            list.add(new FormErrorException(Messages.get().aux_activeEndRequiredException(gid)));
            checkDuplicate = false;
        }

        if (checkDuplicate) {
            if (DataBaseUtil.isAfter(data.getActiveBegin(), data.getActiveEnd())) {
                list.add(new FormErrorException(Messages.get().aux_endDateAfterBeginDateException()));
                checkDuplicate = false;
            }
        }

        if (checkDuplicate) {
            groups = fetchByName(data.getName());

            for (int i = 0; i < groups.size(); i++ ) {
                overlap = false;
                group = (AuxFieldGroupDO)groups.get(i);
                if (DataBaseUtil.isDifferent(group.getId(), data.getId())) {
                    if (DataBaseUtil.isSame(group.getIsActive(), data.getIsActive()) &&
                        DataBaseUtil.isSame("Y", data.getIsActive())) {
                        list.add(new FormErrorException(Messages.get()
                                                                .aux_fieldGroupActiveException()));
                        break;
                    }

                    if (DataBaseUtil.isAfter(data.getActiveEnd(), group.getActiveBegin()) &&
                        DataBaseUtil.isAfter(group.getActiveEnd(), data.getActiveBegin())) {
                        overlap = true;
                    } else if (DataBaseUtil.isAfter(data.getActiveBegin(), group.getActiveBegin()) &&
                               DataBaseUtil.isAfter(group.getActiveEnd(), data.getActiveEnd())) {
                        overlap = true;
                    } else if (DataBaseUtil.isAfter(data.getActiveEnd(), group.getActiveEnd()) &&
                               DataBaseUtil.isAfter(group.getActiveBegin(), data.getActiveBegin())) {
                        overlap = true;
                    } else if (DataBaseUtil.isAfter(group.getActiveEnd(), data.getActiveEnd()) &&
                               DataBaseUtil.isAfter(data.getActiveBegin(), group.getActiveBegin())) {
                        overlap = true;
                    } else if ( !DataBaseUtil.isDifferentYD(group.getActiveBegin(),
                                                            data.getActiveEnd()) ||
                               !DataBaseUtil.isDifferentYD(group.getActiveEnd(),
                                                           data.getActiveBegin())) {
                        overlap = true;
                    } else if ( !DataBaseUtil.isDifferentYD(group.getActiveBegin(),
                                                            data.getActiveBegin()) ||
                               ( !DataBaseUtil.isDifferentYD(group.getActiveEnd(),
                                                             data.getActiveEnd()))) {
                        overlap = true;
                    }

                    if (overlap)
                        list.add(new FormErrorException(Messages.get()
                                                                .aux_fieldGroupTimeOverlapException()));

                }
            }
        }

        if (list.size() > 0)
            throw list;
    }

}
