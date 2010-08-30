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

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.StandardNoteDO;
import org.openelis.entity.StandardNote;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.ModulePermission.ModuleFlags;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.LockLocal;
import org.openelis.meta.StandardNoteMeta;
import org.openelis.remote.StandardNoteRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utils.PermissionInterceptor;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("standardnote-select")
public class StandardNoteBean implements StandardNoteRemote {

    @PersistenceContext(unitName = "openelis")
    private EntityManager                 manager;

    @Resource
    private SessionContext                ctx;

    @EJB
    private LockLocal                     lockBean;

    private static final StandardNoteMeta meta = new StandardNoteMeta();

    public StandardNoteDO fetchById(Integer id) throws Exception {
        Query query;
        StandardNoteDO data;

        query = manager.createNamedQuery("StandardNote.FetchById");
        query.setParameter("id", id);
        try {
            data = (StandardNoteDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<StandardNoteDO> fetchByType(Integer typeId, int max) throws Exception {
        Query query;
        
        query = manager.createNamedQuery("StandardNote.FetchByType");
        query.setParameter("typeId", typeId);
        query.setMaxResults(max);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    @SuppressWarnings("unchecked")
    public ArrayList<StandardNoteDO> fetchByNameOrDescription(String name, String description, int max) throws Exception {
        Query query;
        
        query = manager.createNamedQuery("StandardNote.FetchByNameOrDescription");
        query.setParameter("name", name);
        query.setParameter("description", description);
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
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" + StandardNoteMeta.getId() + "," +
                          StandardNoteMeta.getName() + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(StandardNoteMeta.getName());

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

    public StandardNoteDO add(StandardNoteDO data) throws Exception {
        StandardNote entity;

        checkSecurity(ModuleFlags.ADD);

        validate(data);

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new StandardNote();
        entity.setDescription(data.getDescription());
        entity.setName(data.getName());
        entity.setText(data.getText());
        entity.setTypeId(data.getTypeId());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public StandardNoteDO update(StandardNoteDO data) throws Exception {
        StandardNote entity;

        checkSecurity(ModuleFlags.UPDATE);

        validate(data);

        lockBean.validateLock(ReferenceTable.STANDARD_NOTE, data.getId());

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(StandardNote.class, data.getId());
        entity.setDescription(data.getDescription());
        entity.setName(data.getName());
        entity.setText(data.getText());
        entity.setTypeId(data.getTypeId());

        lockBean.giveUpLock(ReferenceTable.STANDARD_NOTE, data.getId());

        return data;
    }

    public StandardNoteDO fetchForUpdate(Integer id) throws Exception {
        lockBean.getLock(ReferenceTable.STANDARD_NOTE, id);
        return fetchById(id);
    }

    public StandardNoteDO abortUpdate(Integer id) throws Exception {
        lockBean.giveUpLock(ReferenceTable.STANDARD_NOTE, id);
        return fetchById(id);
    }

    public void delete(StandardNoteDO data) throws Exception {
        StandardNote entity;

        checkSecurity(ModuleFlags.DELETE);

        lockBean.validateLock(ReferenceTable.STANDARD_NOTE, data.getId());

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(StandardNote.class, data);
        if (entity != null)
            manager.remove(entity);

        lockBean.giveUpLock(ReferenceTable.ANALYTE, data.getId());
    }

    private void validate(StandardNoteDO standardNoteDO) throws Exception {
        ValidationErrorsList list = new ValidationErrorsList();

        if (DataBaseUtil.isEmpty(standardNoteDO.getName()))
            list.add(new FieldErrorException("fieldRequiredException", meta.getName()));

        if (DataBaseUtil.isEmpty(standardNoteDO.getDescription()))
            list.add(new FieldErrorException("fieldRequiredException", meta.getDescription()));

        if (DataBaseUtil.isEmpty(standardNoteDO.getTypeId()))
            list.add(new FieldErrorException("fieldRequiredException", meta.getTypeId()));

        if (DataBaseUtil.isEmpty(standardNoteDO.getText()))
            list.add(new FieldErrorException("fieldRequiredException", meta.getText()));

        if (list.size() > 0)
            throw list;
    }

    private void checkSecurity(ModuleFlags flag) throws Exception {
        PermissionInterceptor.applyPermission("standardnote", flag);
    }
}
