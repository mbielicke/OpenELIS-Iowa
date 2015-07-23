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
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.constants.Messages;
import org.openelis.domain.NoteDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.entity.Note;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.SystemUserVO;

@Stateless
@SecurityDomain("openelis")

public class NoteBean {

    @PersistenceContext(unitName = "openelis")
    private EntityManager          manager;
    
    @EJB
    private UserCacheBean          userCache; 
    
    public ArrayList<NoteViewDO> fetchById(Integer referenceId, Integer referenceTableId) throws Exception {
        Query query;
        NoteViewDO note;
        SystemUserVO user;
        ArrayList<NoteViewDO> list;

        // TODO
        // we are currently returning any requested note without checking to see
        // if the user have permission to this note -- we need to fix this
        //        
        query = manager.createNamedQuery("Note.FetchById");
        query.setParameter("id", referenceId);
        query.setParameter("tableId", referenceTableId);

        list = DataBaseUtil.toArrayList(query.getResultList());
        if (list.size() == 0)
            throw new NotFoundException();

        for (int i = 0; i < list.size(); i++ ) {
            note = list.get(i);

            if (note.getSystemUserId() != null) {
                user = userCache.getSystemUser(note.getSystemUserId());
                if (user != null)
                    note.setSystemUser(user.getLoginName());
            }
        }

        return list;
    }
    
    public ArrayList<NoteViewDO> fetchByIds(ArrayList<Integer> referenceIds, Integer referenceTableId) {
        int i;
        Query query;
        SystemUserVO u;
        NoteViewDO n;
        List<NoteViewDO> ns;
        ArrayList<Integer> r;

        // TODO
        // we are currently returning any requested note without checking to see
        // if the user have permission to this note -- we need to fix this
        //        
        query = manager.createNamedQuery("Note.FetchByIds");
        query.setParameter("tableId", referenceTableId);
        ns = new ArrayList<NoteViewDO>();        
        r = DataBaseUtil.createSubsetRange(referenceIds.size());
        for (i = 0; i < r.size() - 1; i++ ) {
            query.setParameter("ids", referenceIds.subList(r.get(i), r.get(i + 1)));
            ns.addAll(query.getResultList());
        }

        for (i = 0; i < ns.size(); i++ ) {
            n = ns.get(i);

            if (n.getSystemUserId() != null) {
                u = userCache.getSystemUser(n.getSystemUserId());
                if (u != null)
                    n.setSystemUser(u.getLoginName());
            }
        }

        return DataBaseUtil.toArrayList(ns);
    }
    
    public ArrayList<NoteViewDO> fetchByIdsAndTables(ArrayList<Integer> referenceIds, ArrayList<Integer> referenceTableIds) {
        int i;
        Query query;
        SystemUserVO u;
        NoteViewDO n;
        List<NoteViewDO> ns;
        ArrayList<Integer> r;

        // TODO
        // we are currently returning any requested note without checking to see
        // if the user have permission to this note -- we need to fix this
        //        
        query = manager.createNamedQuery("Note.FetchByIdsAndTables");
        query.setParameter("tableIds", referenceTableIds);        
        ns = new ArrayList<NoteViewDO>();        
        r = DataBaseUtil.createSubsetRange(referenceIds.size());
        for (i = 0; i < r.size() - 1; i++ ) {
            query.setParameter("ids", referenceIds.subList(r.get(i), r.get(i + 1)));
            ns.addAll(query.getResultList());
        }

        for (i = 0; i < ns.size(); i++ ) {
            n = ns.get(i);

            if (n.getSystemUserId() != null) {
                u = userCache.getSystemUser(n.getSystemUserId());
                if (u != null)
                    n.setSystemUser(u.getLoginName());
            }
        }

        return DataBaseUtil.toArrayList(ns);
    }

    public ArrayList<NoteViewDO> fetchByRefTableRefIdIsExt(Integer refTableId,
                                                           Integer refId,
                                                           String isExternal) throws Exception {
        Query query;
        NoteViewDO note;
        SystemUserVO user;
        ArrayList<NoteViewDO> list;

        // TODO
        // we are currently returning any requested note without checking to see
        // if the user have permission to this note -- we need to fix this
        //        
        query = manager.createNamedQuery("Note.FetchByRefTableRefIdIsExternal");
        query.setParameter("referenceTable", refTableId);
        query.setParameter("id", refId);
        query.setParameter("isExternal", isExternal);

        list = (ArrayList<NoteViewDO>)query.getResultList();
        if (list.size() == 0)
            throw new NotFoundException();

        for (int i = 0; i < list.size(); i++ ) {
            note = list.get(i);

            if (note.getSystemUserId() != null) {
                user = userCache.getSystemUser(note.getSystemUserId());
                if (user != null)
                    note.setSystemUser(user.getLoginName());
            }
        }

        return list;
    }

    public NoteDO add(NoteDO data) throws Exception {
        Note entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = new Note();
        entity.setIsExternal(data.getIsExternal());
        entity.setReferenceId(data.getReferenceId());
        entity.setReferenceTableId(data.getReferenceTableId());
        entity.setSubject(data.getSubject());
        entity.setSystemUserId(userCache.getId());
        entity.setText(data.getText());
        entity.setTimestamp(Datetime.getInstance());

        manager.persist(entity);
        data.setId(entity.getId());

        return data;
    }

    public NoteDO update(NoteDO data) throws Exception {
        Note entity;

        if ( !data.isChanged())
            return data;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(Note.class, data.getId());
        entity.setIsExternal(data.getIsExternal());
        entity.setReferenceId(data.getReferenceId());
        entity.setReferenceTableId(data.getReferenceTableId());
        entity.setSubject(data.getSubject());
        entity.setSystemUserId(userCache.getId());
        entity.setText(data.getText());
        entity.setTimestamp(Datetime.getInstance());

        return data;
    }

    public void delete(NoteDO data) throws Exception {
        Note entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(Note.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }

    public void validate(NoteDO data) throws Exception {
        if ("N".equals(data.getIsExternal())) {
            if (data.getId() < 0) {
                if (DataBaseUtil.isEmpty(data.getSubject()) || DataBaseUtil.isEmpty(data.getText()))
                    throw new FormErrorException(Messages.get().note_internalEmptyException());
            } else if (data.isChanged()) {
                throw new InconsistencyException("Internal note should not be updated");
            }
        }
    }
}