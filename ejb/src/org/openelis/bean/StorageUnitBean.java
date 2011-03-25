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

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.StorageUnitDO;
import org.openelis.entity.StorageUnit;
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
import org.openelis.meta.StorageUnitMeta;
import org.openelis.remote.StorageUnitRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utils.EJBFactory;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("storageunit-select")
public class StorageUnitBean implements StorageUnitRemote {

    @PersistenceContext(unitName = "openelis")
    private EntityManager                   manager;

    @EJB
    private LockLocal                       lock;

    private static final StorageUnitMeta meta = new StorageUnitMeta();

    public StorageUnitBean() {
    }

    public StorageUnitDO fetchById(Integer id) throws Exception {
        Query query;
        StorageUnitDO data;

        query = manager.createNamedQuery("StorageUnit.FetchById");
        try {
            query.setParameter("id", id);
            data = (StorageUnitDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<IdNameVO> fetchByDescription(String desc, int max) throws Exception {
        Query query;

        query = manager.createNamedQuery("StorageUnit.FetchByDescription");
        query.setParameter("description", desc);
        query.setMaxResults(max);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    @SuppressWarnings("unchecked")
    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max)
                                                                                     throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" + StorageUnitMeta.getId() + ", " +
                          StorageUnitMeta.getDescription() + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(StorageUnitMeta.getDescription());

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

    public StorageUnitDO add(StorageUnitDO data) throws Exception {
        StorageUnit entity;

        checkSecurity(ModuleFlags.ADD);

        validate(data);

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new StorageUnit();
        entity.setCategory(data.getCategoryId());
        entity.setDescription(data.getDescription());
        entity.setIsSingular(data.getIsSingular());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public StorageUnitDO update(StorageUnitDO data) throws Exception {
        StorageUnit entity;

        if ( !data.isChanged()) {
            lock.unlock(ReferenceTable.STORAGE_UNIT, data.getId());
            return data;
        }
        checkSecurity(ModuleFlags.UPDATE);

        validate(data);

        lock.validateLock(ReferenceTable.STORAGE_UNIT, data.getId());

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(StorageUnit.class, data.getId());
        entity.setCategory(data.getCategoryId());
        entity.setDescription(data.getDescription());
        entity.setIsSingular(data.getIsSingular());

        lock.unlock(ReferenceTable.STORAGE_UNIT, data.getId());

        return data;
    }

    public StorageUnitDO fetchForUpdate(Integer id) throws Exception {
        try {
            lock.lock(ReferenceTable.STORAGE_UNIT, id);
            return fetchById(id);
        } catch (NotFoundException e) {
            throw new DatabaseException(e);
        }
    }

    public StorageUnitDO abortUpdate(Integer id) throws Exception {
        lock.unlock(ReferenceTable.STORAGE_UNIT, id);
        return fetchById(id);
    }

    public void delete(StorageUnitDO data) throws Exception {
        StorageUnit entity;

        checkSecurity(ModuleFlags.DELETE);

        validateForDelete(data);

        lock.validateLock(ReferenceTable.STORAGE_UNIT, data.getId());

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(StorageUnit.class, data.getId());
        if (entity != null)
            manager.remove(entity);

        lock.unlock(ReferenceTable.STORAGE_UNIT, data.getId());

    }

    private void validateForDelete(StorageUnitDO data) throws Exception {
        ValidationErrorsList list;
        List locations;
        Query query;

        query = manager.createNamedQuery("StorageUnit.ReferenceCheck");
        query.setParameter("id", data.getId());
        list = new ValidationErrorsList();
        locations = query.getResultList();

        if (locations.size() > 0) {
            list.add(new FormErrorException("storageUnitDeleteException"));        
            throw list;
        }
    }

    public void validate(StorageUnitDO data) throws Exception {
        ValidationErrorsList list;
        String desc;
        ArrayList<IdNameVO> dups;
        
        desc = data.getDescription();
        list = new ValidationErrorsList();

        if (DataBaseUtil.isEmpty(data.getCategoryId()))
            list.add(new FieldErrorException("fieldRequiredException", StorageUnitMeta.getCategoryId()));

        if (DataBaseUtil.isEmpty(desc)) {
            list.add(new FieldErrorException("fieldRequiredException", StorageUnitMeta.getDescription()));
        } else {
            dups = fetchByDescription(desc, 1);
            if(dups.size() > 0 && DataBaseUtil.isDifferent(dups.get(0).getId(), data.getId())) 
                list.add(new FieldErrorException("fieldUniqueException", StorageUnitMeta.getDescription()));            
        } 
            
        if (list.size() > 0)
            throw list;
    }

    private void checkSecurity(ModuleFlags flag) throws Exception {
        EJBFactory.getUserCache().applyPermission("storageunit", flag);
    }

}
