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
import org.openelis.domain.QcLotDO;
import org.openelis.domain.QcLotViewDO;
import org.openelis.entity.QcLot;
import org.openelis.meta.QcMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.ui.common.ValidationErrorsList;

@Stateless
@SecurityDomain("openelis")

public class QcLotBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager       manager;
    
    @EJB
    private UserCacheBean     userCache;
    
    @EJB
    private WorksheetAnalysisBean worksheetAnalysis;

    public QcLotViewDO fetchById(Integer lotId) throws Exception {
        QcLotViewDO data;
        Query query;

        query = manager.createNamedQuery("QcLot.FetchById");
        query.setParameter("id", lotId);
        try {
            data = (QcLotViewDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }
    
    @SuppressWarnings("unchecked")
    public ArrayList<QcLotViewDO> fetchByIds(ArrayList<Integer> ids) {
        Query query;
        List<QcLotViewDO> q;
        ArrayList<Integer> r;

        query = manager.createNamedQuery("QcLot.FetchByIds");
        q = new ArrayList<QcLotViewDO>();
        r = DataBaseUtil.createSubsetRange(ids.size());
        for (int i = 0; i < r.size() - 1; i++ ) {
            query.setParameter("ids", ids.subList(r.get(i), r.get(i + 1)));
            q.addAll(query.getResultList());
        }

        return DataBaseUtil.toArrayList(q);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<QcLotViewDO> fetchByQcId(Integer id) throws Exception {
        Query query;
        List<QcLotViewDO> list;
        SystemUserVO user;

        query = manager.createNamedQuery("QcLot.FetchByQcId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        for (QcLotViewDO data : list) {
            if (data.getPreparedById() != null) {
                user = userCache.getSystemUser(data.getPreparedById());
                if (user != null)
                    data.setPreparedByName(user.getLoginName());
            }
        }
        return DataBaseUtil.toArrayList(list);
    }
    
    @SuppressWarnings("unchecked")
    public ArrayList<QcLotViewDO> fetchActiveByQcName(String qcName, int max) throws Exception {
        Query query;
        List<QcLotViewDO> list;
        SystemUserVO user;

        query = manager.createNamedQuery("QcLot.FetchActiveByQcName");
        query.setParameter("name", qcName);
        query.setMaxResults(max);

        list = query.getResultList();

        for (QcLotViewDO data : list) {
            if (data.getPreparedById() != null) {
                user = userCache.getSystemUser(data.getPreparedById());
                if (user != null)
                    data.setPreparedByName(user.getLoginName());               
            }
        }
        return DataBaseUtil.toArrayList(list);
    }
    
    public QcLotDO fetchByLotNumber(String lot) throws Exception {
        Query query;
        QcLotDO data;
        
        query = manager.createNamedQuery("QcLot.FetchByLotNumber");
        query.setParameter("lotNumber", lot);
        try {
            data = (QcLotDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    public QcLotDO add(QcLotDO data) throws Exception {
        QcLot entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new QcLot();
        entity.setQcId(data.getQcId());
        entity.setLotNumber(data.getLotNumber());
        entity.setLocationId(data.getLocationId());
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

    public QcLotDO update(QcLotDO data) throws Exception {
        QcLot entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(QcLot.class, data.getId());
        entity.setQcId(data.getQcId());
        entity.setLotNumber(data.getLotNumber());
        entity.setLocationId(data.getLocationId());
        entity.setPreparedDate(data.getPreparedDate());
        entity.setPreparedVolume(data.getPreparedVolume());
        entity.setPreparedUnitId(data.getPreparedUnitId());
        entity.setPreparedById(data.getPreparedById());
        entity.setUsableDate(data.getUsableDate());  
        entity.setExpireDate(data.getExpireDate());
        entity.setIsActive(data.getIsActive());

        return data;
    }

    public void delete(QcLotDO data) throws Exception {
        QcLot entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(QcLot.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validate(QcLotDO data) throws Exception {
        boolean validatePrep, validateExpire;
        ValidationErrorsList list;

        list = new ValidationErrorsList();

        if (data.getLotNumber() == null) 
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(), QcMeta.getQcLotLotNumber()));        

        validatePrep = true;
        validateExpire = true;
        if (data.getPreparedDate() == null) {
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(), QcMeta.getQcLotPreparedDate()));
            validatePrep = false;
        }

        if (data.getUsableDate() == null) {
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(), QcMeta.getQcLotUsableDate()));
            validatePrep = false;
            validateExpire = false;
        }

        if (data.getExpireDate() == null) {
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(), QcMeta.getQcLotExpireDate()));
            validateExpire = false;
        }

        if (validatePrep && DataBaseUtil.isAfter(data.getPreparedDate(), data.getUsableDate()))
            list.add(new FieldErrorException(Messages.get().usableBeforePrepException(), QcMeta.getQcLotUsableDate()));

        if (validateExpire && DataBaseUtil.isAfter(data.getUsableDate(), data.getExpireDate()))
            list.add(new FieldErrorException(Messages.get().expireBeforeUsableException(), QcMeta.getQcLotExpireDate()));

        if (list.size() > 0)
            throw list;
    }
    

    public void validateForDelete(QcLotDO data) throws Exception {        
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
        
        try {
            worksheetAnalysis.fetchByQcLotId(data.getId());
            list.add(new FieldErrorException(Messages.get().qcLotDeleteException(), null));
            throw list;
        } catch (NotFoundException e) {
            // ignore
        }
        
    }
}
