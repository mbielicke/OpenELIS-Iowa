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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.NoteDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.entity.Note;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.SystemUserVO;

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
        Query query;
        NoteViewDO data;
        SystemUserVO user;
        ArrayList<NoteViewDO> list;

        // TODO
        // we are currently returning any requested note without checking to see
        // if the user have permission to this note -- we need to fix this
        //        
        query = manager.createNamedQuery("Note.FetchByIds");
        query.setParameter("ids", referenceIds);
        query.setParameter("tableId", referenceTableId);

        list = DataBaseUtil.toArrayList(query.getResultList());

        for (int i = 0; i < list.size(); i++ ) {
            data = list.get(i);

            if (data.getSystemUserId() != null) {
                user = userCache.getSystemUser(data.getSystemUserId());
                if (user != null)
                    data.setSystemUser(user.getLoginName());
            }
        }

        return list;
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
}
