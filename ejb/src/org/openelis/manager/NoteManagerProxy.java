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
package org.openelis.manager;

import java.util.ArrayList;

import javax.naming.InitialContext;

import org.openelis.domain.NoteViewDO;
import org.openelis.local.NoteLocal;

public class NoteManagerProxy {
    public NoteManager fetchByRefTableRefId(Integer tableId, Integer id) throws Exception {
        NoteManager m;
        ArrayList<NoteViewDO> notes;

        notes = local().fetchByRefTableRefId(tableId, id);

        m = NoteManager.getInstance();
        m.setNotes(notes);
        m.setReferenceId(id);
        m.setReferenceTableId(tableId);

        return m;
    }
    
    public NoteManager fetchByRefTableRefIdIsExt(Integer tableId, Integer id, String isExternal) throws Exception {
        NoteManager m;
        ArrayList<NoteViewDO> notes;

        notes = local().fetchByRefTableRefIdIsExt(tableId, id, isExternal);

        m = NoteManager.getInstance();
        m.setNotes(notes);
        m.setReferenceId(id);
        m.setReferenceTableId(tableId);

        return m;
    }

    public NoteManager add(NoteManager man) throws Exception {
        NoteViewDO note;

        if (man.count() > 0) {
            note = man.getNoteAt(0);
            note.setReferenceId(man.getReferenceId());
            note.setReferenceTableId(man.getReferenceTableId());

            local().add(note);
        }

        return man;
    }

    public NoteManager update(NoteManager man) throws Exception {
        NoteViewDO note;
        NoteLocal nl;

        nl = local();
        for (int j = 0; j < man.deleteCount(); j++)
            nl.delete(man.getDeletedAt(j));

        if (man.count() > 0) {
            note = man.getNoteAt(0);
            if (note.getId() == null) {
                note.setReferenceId(man.getReferenceId());
                note.setReferenceTableId(man.getReferenceTableId());
                nl.add(note);
            } else {
                nl.update(note);
            }
        }

        return man;
    }

    public void validate(NoteManager man) throws Exception {
    }

    private NoteLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (NoteLocal)ctx.lookup("openelis/NoteBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
