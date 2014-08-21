/**
 * Exhibit A - UIRF Open-source Based Public Software License.+
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
import org.openelis.domain.EventLogDO;
import org.openelis.domain.IdNameVO;
import org.openelis.entity.EventLog;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.meta.EventLogMeta;
import org.openelis.util.QueryBuilderV2;

@Stateless
@SecurityDomain("openelis")
public class EventLogBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager             manager;

    @EJB
    private UserCacheBean                     userCache;
    
    private static final EventLogMeta         meta = new EventLogMeta();
    
    public EventLogDO fetchById(Integer id) throws Exception { 
        Query query;
        EventLogDO data;

        query = manager.createNamedQuery("EventLog.FetchById");
        query.setParameter("id", id);
        try {
            data = (EventLogDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    public ArrayList<EventLogDO> fetchByRefTableIdRefId(Integer refTableId, Integer refId) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("EventLog.FetchByRefTableIdRefId");
        query.setParameter("refTableId", refTableId);
        query.setParameter("refId", refId);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    public ArrayList<EventLogDO> fetchByRefTableIdRefId(Integer refTableId,
                                                        Integer refId, int max) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("EventLog.FetchByRefTableIdRefId");
        query.setParameter("refTableId", refTableId);
        query.setParameter("refId", refId);
        query.setMaxResults(max);

        list = query.getResultList();

        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<EventLogDO> query(ArrayList<QueryData> fields, int first, int max) throws Exception {
        Query query;
        QueryBuilderV2 builder;
        List list;

        builder = new QueryBuilderV2();
        builder.setMeta(meta);
        builder.setSelect("new org.openelis.domain.EventLogDO(" + EventLogMeta.getId() +
                          ", " + EventLogMeta.getTypeId() + ", " +
                          EventLogMeta.getSource() + ", " +
                          EventLogMeta.getReferenceTableId() + ", " +
                          EventLogMeta.getReferenceId() + ", " +
                          EventLogMeta.getLevelId() + ", " +
                          EventLogMeta.getSystemUserId() + ", " +
                          EventLogMeta.getTimeStamp() + ", " + EventLogMeta.getText() +
                          ") ");
        builder.constructWhere(fields);
        builder.setOrderBy(EventLogMeta.getTimeStamp() + " DESC");

        query = manager.createQuery(builder.getEJBQL());
        query.setMaxResults(first + max);
        builder.setQueryParams(query, fields);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();
        list = (ArrayList<IdNameVO>)DataBaseUtil.subList(list, first, max);
        if (list == null)
            throw new LastPageException();

        return (ArrayList<EventLogDO>)list;
    }

    public EventLogDO add(EventLogDO data) throws Exception {
        EventLog entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new EventLog();
        entity.setTypeId(data.getTypeId());
        entity.setSource(data.getSource());
        entity.setReferenceTableId(data.getReferenceTableId());
        entity.setReferenceId(data.getReferenceId());
        entity.setLevelId(data.getLevelId());
        entity.setSystemUserId(data.getSystemUserId());
        entity.setTimeStamp(data.getTimeStamp());
        entity.setText(data.getText());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public void add(Integer typeId, String source, Integer referenceTableId,
                    Integer referenceId, Integer levelId, String text) throws Exception {
        EventLog entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new EventLog();
        entity.setTypeId(typeId);
        entity.setSource(source);
        entity.setReferenceTableId(referenceTableId);
        entity.setReferenceId(referenceId);
        entity.setLevelId(levelId);
        entity.setSystemUserId(userCache.getId());
        entity.setTimeStamp(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE));
        entity.setText(text);

        manager.persist(entity);
    }

    public void delete(EventLogDO data) throws Exception {
        EventLog entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(EventLog.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }
}