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

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.EOrderDO;
import org.openelis.entity.EOrder;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.NotFoundException;

@Stateless
@SecurityDomain("openelis")
public class EOrderBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;

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

    public EOrderDO add(EOrderDO data) throws Exception {
        EOrder entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new EOrder();
        entity.setEnteredDate(data.getEnteredDate());
        entity.setPaperOrderValidator(data.getPaperOrderValidator());
        entity.setDescription(data.getDescription());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }
}
