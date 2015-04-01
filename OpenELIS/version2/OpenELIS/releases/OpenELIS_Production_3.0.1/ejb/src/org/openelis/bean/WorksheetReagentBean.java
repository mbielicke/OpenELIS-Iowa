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
import org.openelis.domain.WorksheetReagentDO;
import org.openelis.domain.WorksheetReagentViewDO;
import org.openelis.entity.WorksheetReagent;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.SystemUserVO;

@Stateless
@SecurityDomain("openelis")

public class WorksheetReagentBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager       manager;
    
    @EJB
    private UserCacheBean     userCache;
    
    public WorksheetReagentViewDO fetchById(Integer id) throws Exception {
        Query query;
        SystemUserVO user;
        WorksheetReagentViewDO data;

        query = manager.createNamedQuery("WorksheetReagent.FetchById");
        query.setParameter("id", id);
        try {
            data = (WorksheetReagentViewDO)query.getSingleResult();
            if (data.getPreparedById() != null) {
                user = userCache.getSystemUser(data.getPreparedById());
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
    
    @SuppressWarnings("unchecked")
    public ArrayList<WorksheetReagentViewDO> fetchByWorksheetId(Integer id) throws Exception {
        List<WorksheetReagentViewDO> list;
        Query query;
        SystemUserVO user;

        query = manager.createNamedQuery("WorksheetReagent.FetchByWorksheetId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        for (WorksheetReagentViewDO data : list) {
            if (data.getPreparedById() != null) {
                user = userCache.getSystemUser(data.getPreparedById());
                if (user != null)
                    data.setPreparedByName(user.getLoginName());
            }
        }
        return DataBaseUtil.toArrayList(list);
    }
    
    @SuppressWarnings("unchecked")
    public ArrayList<WorksheetReagentViewDO> fetchByWorksheetIds(ArrayList<Integer> ids) throws Exception {
        List<WorksheetReagentViewDO> list;
        Query query;
        SystemUserVO user;

        query = manager.createNamedQuery("WorksheetReagent.FetchByWorksheetIds");
        query.setParameter("ids", ids);

        list = query.getResultList();
        if (!list.isEmpty()) {
            for (WorksheetReagentViewDO data : list) {
                if (data.getPreparedById() != null) {
                    user = userCache.getSystemUser(data.getPreparedById());
                    if (user != null)
                        data.setPreparedByName(user.getLoginName());
                }
            }
        }
        return DataBaseUtil.toArrayList(list);
    }
    
    public WorksheetReagentDO add(WorksheetReagentDO data) throws Exception {
        WorksheetReagent entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new WorksheetReagent();
        entity.setWorksheetId(data.getWorksheetId());
        entity.setSortOrder(data.getSortOrder());
        entity.setQcLotId(data.getQcLotId());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public WorksheetReagentDO update(WorksheetReagentDO data) throws Exception {
        WorksheetReagent entity;

        if (!data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(WorksheetReagent.class, data.getId());
        entity.setWorksheetId(data.getWorksheetId());
        entity.setSortOrder(data.getSortOrder());
        entity.setQcLotId(data.getQcLotId());

        return data;
    }

    public void delete(WorksheetReagentDO data) throws Exception {
        WorksheetReagent entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(WorksheetReagent.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validate(WorksheetReagentDO data) throws Exception {
    }
}