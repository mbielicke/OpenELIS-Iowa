/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
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

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.QcDO;
import org.openelis.domain.QcViewDO;
import org.openelis.entity.Qc;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.SystemUserVO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.QcLocal;
import org.openelis.meta.QcMeta;
import org.openelis.remote.QcRemote;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utils.PermissionInterceptor;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("qc-select")
public class QcBean implements QcRemote, QcLocal {

    @PersistenceContext(unitName = "openelis")
    private EntityManager                    manager;

    private static final QcMeta meta = new QcMeta();

    public QcViewDO fetchById(Integer id) throws Exception {
        Query query;
        QcViewDO data;
        SystemUserVO user;
        
        query = manager.createNamedQuery("Qc.FetchById");
        query.setParameter("id", id);
        try {
            data = (QcViewDO)query.getSingleResult();
            if (data.getPreparedById() != null) {
                user = PermissionInterceptor.getSystemUser(data.getPreparedById());
                if (user != null)
                    data.setPreparedByName(user.getLoginName());
            }
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    public QcDO fetchByLotNumber(String lot) throws Exception {
        Query query;
        QcDO data;
        
        query = manager.createNamedQuery("Qc.FetchByLotNumber");
        query.setParameter("lotNumber", lot);
        try {
            data = (QcDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<QcDO> fetchByName(String name, int max) {
        Query query;
        
        query = manager.createNamedQuery("Qc.FetchByName");
        query.setParameter("name", name);
        query.setMaxResults(max);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    @SuppressWarnings("unchecked")
    public ArrayList<QcDO> fetchActiveByName(String name, int max) {
        Query query;
        
        query = manager.createNamedQuery("Qc.FetchActiveByName");
        query.setParameter("name", name);
        query.setMaxResults(max);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    @SuppressWarnings("unchecked")
    public ArrayList<QcDO> fetchActiveByName(ArrayList<QueryData> fields) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.QcDO(" + 
                          QcMeta.getId() + "," + QcMeta.getName() + "," + QcMeta.getTypeId() + "," +
                          QcMeta.getInventoryItemId() + "," + QcMeta.getSource() + "," +
                          QcMeta.getLotNumber() + "," + QcMeta.getPreparedDate() + "," +
                          QcMeta.getPreparedVolume() + "," + QcMeta.getPreparedUnitId() + "," +
                          QcMeta.getPreparedById() + "," + QcMeta.getUsableDate() + "," +
                          QcMeta.getExpireDate() + "," + QcMeta.getIsActive() + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(QcMeta.getName() + "," + QcMeta.getLotNumber());

        query = manager.createQuery(builder.getEJBQL());
        builder.setQueryParams(query, fields);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<IdNameVO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("distinct new org.openelis.domain.IdNameVO(" + 
                          QcMeta.getId() + "," + QcMeta.getName() + "," +
                          QcMeta.getLotNumber() + ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(QcMeta.getName() + "," + QcMeta.getLotNumber());

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

    public QcViewDO add(QcViewDO data) throws Exception {
        Qc entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new Qc();
        entity.setName(data.getName());
        entity.setTypeId(data.getTypeId());
        entity.setInventoryItemId(data.getInventoryItemId());
        entity.setSource(data.getSource());
        entity.setLotNumber(data.getLotNumber());
        entity.setPreparedDate(data.getPreparedDate());
        entity.setPreparedVolume(data.getPreparedVolume());
        entity.setPreparedUnitId(data.getPreparedUnitId());
        entity.setPreparedById(data.getPreparedById());
        entity.setUsableDate(data.getUsableDate());  
        entity.setExpireDate(data.getExpireDate());
        entity.setIsActive(data.getIsActive());

        manager.persist(entity);
        data.setId(entity.getId());
        
        return data;
    }

    public QcViewDO update(QcViewDO data) throws Exception {
        Qc entity;
        
        if (!data.isChanged())
            return data;
        
        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(Qc.class, data.getId());
        entity.setName(data.getName());
        entity.setTypeId(data.getTypeId());
        entity.setInventoryItemId(data.getInventoryItemId());
        entity.setSource(data.getSource());
        entity.setLotNumber(data.getLotNumber());
        entity.setPreparedDate(data.getPreparedDate());
        entity.setPreparedVolume(data.getPreparedVolume());
        entity.setPreparedUnitId(data.getPreparedUnitId());
        entity.setPreparedById(data.getPreparedById());
        entity.setUsableDate(data.getUsableDate());  
        entity.setExpireDate(data.getExpireDate());
        entity.setIsActive(data.getIsActive());

        return data;
    }

    public void validate(QcViewDO data) throws Exception {
        QcDO dup;
        ValidationErrorsList list;
        Double prepVolume;
        
        prepVolume = data.getPreparedVolume();
        
        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getName()))
            list.add(new FieldErrorException("fieldRequiredException", QcMeta.getName()));

        if (DataBaseUtil.isEmpty(data.getSource()))
            list.add(new FieldErrorException("fieldRequiredException", QcMeta.getSource()));

        if (DataBaseUtil.isEmpty(data.getPreparedDate()))
            list.add(new FieldErrorException("fieldRequiredException", QcMeta.getPreparedDate()));
        
        if (DataBaseUtil.isEmpty(data.getUsableDate()))
            list.add(new FieldErrorException("fieldRequiredException", QcMeta.getUsableDate()));

        if (DataBaseUtil.isEmpty(data.getExpireDate()))
            list.add(new FieldErrorException("fieldRequiredException", QcMeta.getExpireDate()));

        if (DataBaseUtil.isAfter(data.getPreparedDate(), data.getUsableDate()))
            list.add(new FieldErrorException("usableBeforePrepException",null));

        if (DataBaseUtil.isAfter(data.getUsableDate(), data.getExpireDate()))
            list.add(new FieldErrorException("expireBeforeUsableException",null));       

        //
        // check for duplicate lot #
        //
        if (DataBaseUtil.isEmpty(data.getLotNumber())) {
            list.add(new FieldErrorException("fieldRequiredException", QcMeta.getLotNumber()));
        } else {
            try {
                dup = fetchByLotNumber(data.getLotNumber());
                if (DataBaseUtil.isDifferent(dup.getId(), data.getId()))
                    list.add(new FieldErrorException("fieldUniqueException", QcMeta.getLotNumber()));
            } catch (NotFoundException e) {
                // ignore
            }
        }
        
        if (list.size() > 0)
            throw list;
    }
}
