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

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.LabelDO;
import org.openelis.domain.LabelViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.entity.Label;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.FormErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.ModulePermission.ModuleFlags;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.LockLocal;
import org.openelis.meta.LabelMeta;
import org.openelis.remote.LabelRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utils.EJBFactory;

@Stateless
@SecurityDomain("openelis")

public class LabelBean implements LabelRemote {

    @PersistenceContext(unitName = "openelis")
    private EntityManager             manager;

    @EJB
    private LockLocal                 lock;

    private static final LabelMeta    meta = new LabelMeta();

    public LabelBean() {
    }

    public LabelViewDO fetchById(Integer id) throws Exception {
        Query query;
        LabelViewDO data;

        query = manager.createNamedQuery("Label.FetchById");
        query.setParameter("id", id);

        try {
            data = (LabelViewDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<LabelDO> fetchByName(String match, int max) throws Exception {
        Query query;

        query = manager.createNamedQuery("Label.FetchByName");
        query.setParameter("name", match);
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
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" + LabelMeta.getId() + ", " +
                          LabelMeta.getName() + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(LabelMeta.getName());

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

    public LabelViewDO add(LabelViewDO data) throws Exception {
        Label entity;

        checkSecurity(ModuleFlags.ADD);

        validate(data);

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new Label();
        entity.setName(data.getName());
        entity.setDescription(data.getDescription());
        entity.setPrinterTypeId(data.getPrinterTypeId());
        entity.setScriptletId(data.getScriptletId());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public LabelViewDO update(LabelViewDO data) throws Exception {
        Label entity;

        if ( !data.isChanged()) {
            lock.unlock(ReferenceTable.LABEL, data.getId());
            return data;
        }
        checkSecurity(ModuleFlags.UPDATE);

        validate(data);

        lock.validateLock(ReferenceTable.LABEL, data.getId());

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(Label.class, data.getId());
        entity.setName(data.getName());
        entity.setDescription(data.getDescription());
        entity.setPrinterTypeId(data.getPrinterTypeId());
        entity.setScriptletId(data.getScriptletId());

        lock.unlock(ReferenceTable.LABEL, data.getId());

        return data;
    }

    public LabelViewDO fetchForUpdate(Integer id) throws Exception {
        try {
            lock.lock(ReferenceTable.LABEL, id);
            return fetchById(id);
        } catch (NotFoundException e) {
            throw new DatabaseException(e);
        }
    }

    public LabelViewDO abortUpdate(Integer id) throws Exception {
        lock.unlock(ReferenceTable.LABEL, id);
        return fetchById(id);
    }

    public void delete(LabelViewDO data) throws Exception {
        Label entity;

        checkSecurity(ModuleFlags.DELETE);

        validateForDelete(data.getId());

        lock.validateLock(ReferenceTable.LABEL, data.getId());

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(Label.class, data.getId());
        if (entity != null)
            manager.remove(entity);

        lock.unlock(ReferenceTable.LABEL, data.getId());
    }

    public void validateForDelete(Integer id) throws Exception {
        Query query;
        ValidationErrorsList list;
        List result;

        list = new ValidationErrorsList();

        query = manager.createNamedQuery("Label.ReferenceCheck");
        query.setParameter("id", id);
        result = query.getResultList();

        if (result.size() > 0) {
            list.add(new FormErrorException("labelDeleteException"));
            throw list;
        }
    }

    public void validate(LabelViewDO data) throws Exception {
        ValidationErrorsList list;
        List<LabelDO> dups;

        list = new ValidationErrorsList();

        if (DataBaseUtil.isEmpty(data.getName())) {
            list.add(new FieldErrorException("fieldRequiredException", meta.getName()));
        } else {
            dups = fetchByName(data.getName(), 1);
            if (dups.size() > 0 && ! dups.get(0).getId().equals(data.getId()))
                list.add(new FieldErrorException("fieldUniqueException", meta.getName()));
        }

        if (DataBaseUtil.isEmpty(data.getPrinterTypeId()))
            list.add(new FieldErrorException("fieldRequiredException", meta.getPrinterTypeId()));

        if (list.size() > 0)
            throw list;
    }

    private void checkSecurity(ModuleFlags flag) throws Exception {
        EJBFactory.getUserCache().applyPermission("label", flag);
    }

}
