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
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.openelis.domain.HistoryVO;
import org.openelis.entity.History;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.SystemUserVO;
import org.openelis.local.HistoryLocal;
import org.openelis.remote.HistoryRemote;
import org.openelis.utils.EJBFactory;

@Stateless
@SecurityDomain("openelis")

public class HistoryBean implements HistoryRemote, HistoryLocal {

    @PersistenceContext(unitName = "openelis")
    EntityManager               manager;

    @SuppressWarnings("unchecked")
    public ArrayList<HistoryVO> fetchByReferenceIdAndTable(Integer referenceId, Integer referenceTableId) throws Exception{
        Query query;
        SystemUserVO user;
        List<HistoryVO> list;

        query = manager.createNamedQuery("History.FetchByReferenceIdAndTable");
        query.setParameter("referenceId", referenceId);
        query.setParameter("referenceTableId", referenceTableId);

        list = query.getResultList();
        for (HistoryVO h : list) {
            if (h.getSystemUserId() != null) {
                user = EJBFactory.getUserCache().getSystemUser(h.getSystemUserId());
                if (user != null)
                    h.setSystemUserLoginName(user.getLoginName());
            }
        }
        return DataBaseUtil.toArrayList(list);
    }

    public HistoryVO add(HistoryVO data) {
        History entity;
        
        manager.setFlushMode(FlushModeType.COMMIT);
        
        entity = new History();
        entity.setReferenceId(data.getReferenceId());
        entity.setReferenceTableId(data.getReferenceTableId());
        entity.setTimestamp(data.getTimestamp());
        entity.setActivityId(data.getActivityId());
        entity.setSystemUserId(data.getSystemUserId());
        entity.setChanges(data.getChanges());

        manager.persist(entity);
        data.setId(entity.getId());
        
        return data;
    }
}