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
import org.openelis.domain.Constants;
import org.openelis.domain.EOrderDO;
import org.openelis.entity.EOrder;
import org.openelis.entity.EOrderBody;
import org.openelis.entity.EOrderLink;
import org.openelis.meta.EOrderMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;

@Stateless
@SecurityDomain("openelis")
public class EOrderBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;

    @EJB
    private LockBean      lock;

    public EOrderDO fetchById(Integer id) throws Exception {
        Query query;
        EOrderDO data;

        query = manager.createNamedQuery("EOrder.FetchById");
        query.setParameter("id", id);

        try {
            data = (EOrderDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<EOrderDO> fetchByIds(ArrayList<Integer> ids) {
        Query query;

        query = manager.createNamedQuery("EOrder.FetchByIds");
        query.setParameter("ids", ids);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    @SuppressWarnings("unchecked")
    public ArrayList<EOrderDO> fetchByPaperOrderValidator(String pov) {
        Query query;

        query = manager.createNamedQuery("EOrder.FetchByPaperOrderValidator");
        query.setParameter("pov", pov);

        return DataBaseUtil.toArrayList(query.getResultList());
    }

    public EOrderDO add(EOrderDO data) throws Exception {
        EOrder entity;

        validate(data);
        
        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new EOrder();
        entity.setEnteredDate(data.getEnteredDate());
        entity.setPaperOrderValidator(data.getPaperOrderValidator());
        entity.setDescription(data.getDescription());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public EOrderDO update(EOrderDO data) throws Exception {
        EOrder entity;

        if (!data.isChanged()) {
            lock.unlock(Constants.table().EORDER, data.getId());
            return data;
        }

        validate(data);

        lock.validateLock(Constants.table().EORDER, data.getId());

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(EOrder.class, data.getId());
        entity.setEnteredDate(data.getEnteredDate());
        entity.setPaperOrderValidator(data.getPaperOrderValidator());
        entity.setDescription(data.getDescription());

        lock.unlock(Constants.table().EORDER, data.getId());

        return data;
    }

    public EOrderDO fetchForUpdate(Integer id) throws Exception {
        try {
            lock.lock(Constants.table().EORDER, id);
            return fetchById(id);
        } catch (NotFoundException e) {
            throw new DatabaseException(e);
        }
    }

    public EOrderDO abortUpdate(Integer id) throws Exception {
        lock.unlock(Constants.table().EORDER, id);
        return fetchById(id);
    }

    public void delete(EOrderDO data) throws Exception {
        Query query;
        List<Long> list;
        EOrder entity;
        ValidationErrorsList errors;

        errors = new ValidationErrorsList();
        // reference check
        query = manager.createNamedQuery("EOrder.ReferenceCount");
        query.setParameter("id", data.getId());
        list = query.getResultList();
        for (Long i : list) {
            if (i > 0) {
                errors.add(new FormErrorException(Messages.get().eorder_deleteException()));
                throw errors;
            }
        }
        lock.validateLock(Constants.table().EORDER, data.getId());

        manager.setFlushMode(FlushModeType.COMMIT);
        entity = manager.find(EOrder.class, data.getId());
        if (entity != null)
            manager.remove(entity);

        lock.unlock(Constants.table().EORDER, data.getId());
    }

    public void validate(EOrderDO data) throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();

        if (DataBaseUtil.isEmpty(data.getEnteredDate()))
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             EOrderMeta.getEnteredDate()));

        if (DataBaseUtil.isEmpty(data.getPaperOrderValidator()))
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             EOrderMeta.getPaperOrderValidator()));

        if (DataBaseUtil.isEmpty(data.getDescription()))
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             EOrderMeta.getDescription()));

        if (list.size() > 0)
            throw list;
    }
}