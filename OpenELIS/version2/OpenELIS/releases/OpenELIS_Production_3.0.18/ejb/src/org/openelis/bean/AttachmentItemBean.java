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

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.AttachmentItemDO;
import org.openelis.domain.AttachmentItemViewDO;
import org.openelis.entity.AttachmentItem;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.NotFoundException;

@Stateless
@SecurityDomain("openelis")
public class AttachmentItemBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager manager;

    @SuppressWarnings("unchecked")
    public ArrayList<AttachmentItemDO> fetchById(Integer referenceId, Integer referenceTableId) throws Exception {
        Query query;
        ArrayList<AttachmentItemDO> list;

        query = manager.createNamedQuery("AttachmentItem.FetchById");
        query.setParameter("id", referenceId);
        query.setParameter("tableId", referenceTableId);

        list = DataBaseUtil.toArrayList(query.getResultList());
        if (list.size() == 0)
            throw new NotFoundException();

        return list;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<AttachmentItemViewDO> fetchByIds(ArrayList<Integer> referenceIds,
                                                  Integer referenceTableId) {
        Query query;
        List<AttachmentItemViewDO> a;
        ArrayList<Integer> r;

        query = manager.createNamedQuery("AttachmentItem.FetchByIds");
        query.setParameter("tableId", referenceTableId);
        a = new ArrayList<AttachmentItemViewDO>();
        r = DataBaseUtil.createSubsetRange(referenceIds.size());
        for (int i = 0; i < r.size() - 1; i++ ) {
            query.setParameter("ids", referenceIds.subList(r.get(i), r.get(i + 1)));
            a.addAll(query.getResultList());
        }

        return DataBaseUtil.toArrayList(a);
    }
    
    @SuppressWarnings("unchecked")
    public ArrayList<AttachmentItemViewDO> fetchByAttachmentIds(ArrayList<Integer> attachmentIds) {
        Query query;
        List<AttachmentItemViewDO> a;
        ArrayList<Integer> r;

        query = manager.createNamedQuery("AttachmentItem.FetchByAttachmentIds");
        a = new ArrayList<AttachmentItemViewDO>();
        r = DataBaseUtil.createSubsetRange(attachmentIds.size());
        for (int i = 0; i < r.size() - 1; i++ ) {
            query.setParameter("ids", attachmentIds.subList(r.get(i), r.get(i + 1)));
            a.addAll(query.getResultList());
        }

        return DataBaseUtil.toArrayList(a);
    }

    public AttachmentItemDO add(AttachmentItemDO data) throws Exception {
        AttachmentItem entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new AttachmentItem();
        entity.setReferenceId(data.getReferenceId());
        entity.setReferenceTableId(data.getReferenceTableId());
        entity.setAttachmentId(data.getAttachmentId());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public AttachmentItemDO update(AttachmentItemDO data) throws Exception {
        AttachmentItem entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(AttachmentItem.class, data.getId());
        entity.setReferenceId(data.getReferenceId());
        entity.setReferenceTableId(data.getReferenceTableId());
        entity.setAttachmentId(data.getAttachmentId());

        return data;
    }

    public void delete(AttachmentItemDO data) throws Exception {
        AttachmentItem entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(AttachmentItem.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }
}