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
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.entity.WorksheetItem;
import org.openelis.gwt.common.FieldErrorException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.WorksheetItemLocal;
import org.openelis.meta.WorksheetCompletionMeta;
import org.openelis.utilcommon.DataBaseUtil;

@Stateless
@SecurityDomain("openelis")
@RolesAllowed("worksheet-select")
public class WorksheetItemBean implements WorksheetItemLocal {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;

    @SuppressWarnings("unchecked")
    public ArrayList<WorksheetItemDO> fetchByWorksheetId(Integer id) throws Exception {
        Query query;
        List list;

        query = manager.createNamedQuery("WorksheetItem.FetchByWorksheetId");
        query.setParameter("id", id);

        list = query.getResultList();
        if (list.isEmpty())
            throw new NotFoundException();

        return DataBaseUtil.toArrayList(list);
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
            list.add(new FieldErrorException("fieldRequiredException",
                                             WorksheetCompletionMeta.getWorksheetItemPosition()));
        
        if (list.size() > 0)
            throw list;
    }
}
