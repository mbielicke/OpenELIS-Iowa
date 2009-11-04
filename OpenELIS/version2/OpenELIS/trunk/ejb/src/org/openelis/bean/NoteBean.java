/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.bean;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.security.SecurityDomain;
import org.openelis.domain.NoteViewDO;
import org.openelis.entity.Note;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.local.LoginLocal;
import org.openelis.local.NoteLocal;
import org.openelis.remote.NoteRemote;

@Stateless

@SecurityDomain("openelis")
public class NoteBean implements NoteRemote, NoteLocal {

    @PersistenceContext(name = "openelis")
    private EntityManager manager;
    
    @EJB LoginLocal login;
    
    public ArrayList<NoteViewDO> getNotes(Integer refTableId, Integer refId) throws Exception {
        
        Query query = manager.createNamedQuery("Note.Notes");
        query.setParameter("referenceTable", refTableId);
        query.setParameter("id", refId);
        
        ArrayList<NoteViewDO> list = (ArrayList<NoteViewDO>)query.getResultList();
        
        if(list.size() == 0)
            throw new NotFoundException();
        
        return list;
    }
    
    public void add(NoteViewDO noteDO) throws Exception {
        manager.setFlushMode(FlushModeType.COMMIT);
        
        Note note = new Note();
        
        note.setIsExternal(noteDO.getIsExternal());
        note.setReferenceId(noteDO.getReferenceId());
        note.setReferenceTableId(noteDO.getReferenceTableId());
        note.setSubject(noteDO.getSubject());
        note.setSystemUserId(login.getSystemUserId());
        note.setText(noteDO.getText());
        note.setTimestamp(Datetime.getInstance());
        
        manager.persist(note);
        
        noteDO.setId(note.getId());
    }

    public void update(NoteViewDO noteDO) throws Exception {
        if (! noteDO.isChanged())
            return;

        manager.setFlushMode(FlushModeType.COMMIT);
        
        Note note = manager.find(Note.class, noteDO.getId());

        note.setIsExternal(noteDO.getIsExternal());
        note.setReferenceId(noteDO.getReferenceId());
        note.setReferenceTableId(noteDO.getReferenceTableId());
        note.setSubject(noteDO.getSubject());
        note.setSystemUserId(login.getSystemUserId());
        note.setText(noteDO.getText());
        note.setTimestamp(Datetime.getInstance());
    }
    
    public void delete(NoteViewDO data) throws Exception {
        Note entity;

        manager.setFlushMode(FlushModeType.COMMIT);

        entity = manager.find(Note.class, data.getId());
        if (entity != null)
            manager.remove(entity);
    }
}
