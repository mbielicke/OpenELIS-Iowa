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

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.StorageLocationViewDO;
import org.openelis.entity.StorageLocation;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.StorageLocationLocal;
import org.openelis.metamap.StorageLocationMetaMap;
import org.openelis.remote.StorageLocationRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("storagelocation-select")
public class StorageLocationBean implements StorageLocationRemote, StorageLocationLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager                       manager;

    private static final StorageLocationMetaMap meta = new StorageLocationMetaMap();

    public StorageLocationBean() {
    }

    public StorageLocationViewDO fetchById(Integer id) throws Exception {
        Query query;
        StorageLocationViewDO data;

        query = manager.createNamedQuery("StorageLocation.FetchById");
        query.setParameter("id", id);
        try {
            data = (StorageLocationViewDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<StorageLocationViewDO> fetchByParentStorageLocationId(Integer id)
                                                                                      throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("StorageLocation.FetchByParentStorageLocationId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    public ArrayList<StorageLocationViewDO> fetchByName(String name) {
        Query query = null;
        query = manager.createNamedQuery("StorageLocation.FetchByName");
        query.setParameter("name", name);
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
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" + meta.getId() + ", " +
                          meta.getName() + ") ");
        builder.constructWhere(fields);
        builder.addWhere(meta.getParentStorageLocationId() + " is null");
        builder.setOrderBy(meta.getName());

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

    public StorageLocationViewDO add(StorageLocationViewDO data) throws Exception {
        StorageLocation entity;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = new StorageLocation();
        entity.setSortOrder(data.getSortOrder());
        entity.setName(data.getName());
        entity.setLocation(data.getLocation());
        entity.setStorageUnitId(data.getStorageUnitId());
        entity.setParentStorageLocationId(data.getParentStorageLocationId());
        entity.setIsAvailable(data.getIsAvailable());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public StorageLocationViewDO update(StorageLocationViewDO data) throws Exception {
        StorageLocation entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(StorageLocation.class, data.getId());
        entity.setSortOrder(data.getSortOrder());
        entity.setName(data.getName());
        entity.setLocation(data.getLocation());
        entity.setStorageUnitId(data.getStorageUnitId());
        entity.setParentStorageLocationId(data.getParentStorageLocationId());
        entity.setIsAvailable(data.getIsAvailable());

        return data;
    }

    public void delete(StorageLocationViewDO data) throws Exception {
        StorageLocation entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(StorageLocation.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validateParentStorageLocation(StorageLocationViewDO data) throws Exception {
        ValidationErrorsList list;
        ArrayList<StorageLocationViewDO> dups;

        list = new ValidationErrorsList();

        // name required
        if (DataBaseUtil.isEmpty(data.getName())) {
            list.add(new FieldErrorException("fieldRequiredException", meta.getName()));
        } else {
            // no name duplicates
            dups = fetchByName(data.getName());
            if (dups.size() > 0 && DataBaseUtil.isDifferent(dups.get(0).getId(), data.getId()) &&
                dups.get(0).getParentStorageLocationId() == null) {
                list.add(new FieldErrorException("fieldUniqueException", meta.getName()));
            }

        }

        // storage unit required
        if (DataBaseUtil.isEmpty(data.getStorageUnitId()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             meta.STORAGE_UNIT_META.getDescription()));

        // location required
        if (DataBaseUtil.isEmpty(data.getLocation()))
            list.add(new FieldErrorException("fieldRequiredException", meta.getLocation()));

        if (list.size() > 0)
            throw list;

    }

    public void validateChildStorageLocation(StorageLocationViewDO storageLocationDO)
                                                                                     throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();

        if (DataBaseUtil.isEmpty(storageLocationDO.getStorageUnitId()))
            list.add(new FieldErrorException(
                                             "fieldRequiredException",
                                             meta.CHILD_STORAGE_LOCATION_META.STORAGE_UNIT_META.getDescription()));

        // location required
        if (DataBaseUtil.isEmpty(storageLocationDO.getLocation()))
            list.add(new FieldErrorException("fieldRequiredException",
                                             meta.CHILD_STORAGE_LOCATION_META.getLocation()));

        if (list.size() > 0)
            throw list;
    }

    public void validateForDelete(StorageLocationViewDO data) throws Exception {
        ValidationErrorsList list;
        Query query;
        List result;

        list = new ValidationErrorsList();
        query = manager.createNamedQuery("StorageLocation.ReferenceCheck");
        query.setParameter("id", data.getId());
        result = query.getResultList();

        if (result.size() > 0) {
            list.add(new FieldErrorException("storageLocationDeleteException", null));
            throw list;
        }
    }
    
 //
 // this code is here so that in the future when the service that uses it is
 // rewritten, it will be available
 //   
    
 /*public ArrayList<StorageLocationVO> fetchByName(String name, int maxResults) {
    Query query = null;
    query = manager.createNamedQuery("StorageLocation.FetchByName");
    query.setParameter("name",name);
    query.setParameter("loc",name);
    query.setParameter("desc",name);
    query.setMaxResults(maxResults);
    return DataBaseUtil.toArrayList(query.getResultList());
}*/

}
