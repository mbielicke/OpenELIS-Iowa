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

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.entity.WorksheetItem;
import org.openelis.meta.WorksheetCompletionMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.DatabaseException;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.ValidationErrorsList;

@Stateless
@SecurityDomain("openelis")
public class WorksheetItemBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;

    public WorksheetItemDO fetchById(Integer id) throws Exception {
        Query query;
        WorksheetItemDO data;

        query = manager.createNamedQuery("WorksheetItem.FetchById");
        query.setParameter("id", id);
        try {
            data = (WorksheetItemDO)query.getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<WorksheetItemDO> fetchByWorksheetId(Integer id) throws Exception {
        Query query;
        List<WorksheetItemDO> list;

        query = manager.createNamedQuery("WorksheetItem.FetchByWorksheetId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<WorksheetItemDO> fetchByWorksheetIds(ArrayList<Integer> ids) throws Exception {
        Query query;
        List<WorksheetItemDO> w;
        ArrayList<Integer> r;

        query = manager.createNamedQuery("WorksheetItem.FetchByWorksheetIds");
        w = new ArrayList<WorksheetItemDO>();
        r = DataBaseUtil.createSubsetRange(ids.size());
        for (int i = 0; i < r.size() - 1; i++ ) {
            query.setParameter("ids", ids.subList(r.get(i), r.get(i + 1)));
            w.addAll(query.getResultList());
        }

        return DataBaseUtil.toArrayList(w);
    }

    public WorksheetItemDO add(WorksheetItemDO data) throws Exception {
        WorksheetItem entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new WorksheetItem();
        entity.setWorksheetId(data.getWorksheetId());
        entity.setPosition(data.getPosition());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public WorksheetItemDO update(WorksheetItemDO data) throws Exception {
        WorksheetItem entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(WorksheetItem.class, data.getId());
        entity.setWorksheetId(data.getWorksheetId());
        entity.setPosition(data.getPosition());

        return data;
    }

    public void delete(WorksheetItemDO data) throws Exception {
        WorksheetItem entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(WorksheetItem.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validate(WorksheetItemDO data) throws Exception {
        ValidationErrorsList list;

        list = new ValidationErrorsList();
        if (DataBaseUtil.isEmpty(data.getPosition()))
            list.add(new FieldErrorException(Messages.get().fieldRequiredException(),
                                             WorksheetCompletionMeta.getWorksheetItemPosition()));
        
        if (list.size() > 0)
            throw list;
    }
}
