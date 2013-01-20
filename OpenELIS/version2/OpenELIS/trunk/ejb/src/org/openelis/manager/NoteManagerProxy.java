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

import org.openelis.bean.NoteBean;
import org.openelis.domain.NoteViewDO;
import org.openelis.utils.EJBFactory;

public class NoteManagerProxy {
    public NoteManager fetchByRefTableRefIdIsExt(Integer tableId, Integer id, String isExternal) throws Exception {
        NoteManager m;
        ArrayList<NoteViewDO> notes;

        notes = EJBFactory.getNote().fetchByRefTableRefIdIsExt(tableId, id, isExternal);

        m = NoteManager.getInstance();
        m.setIsExternal("Y".equals(isExternal));
        m.setNotes(notes);
        m.setReferenceId(id);
        m.setReferenceTableId(tableId);

        return m;
    }

    public NoteManager add(NoteManager man) throws Exception {
        NoteViewDO data;

        if (man.count() > 0) {
            data = man.getNoteAt(0);
            data.setReferenceId(man.getReferenceId());
            data.setReferenceTableId(man.getReferenceTableId());
            if (data.getSubject() != null || data.getText() != null) 
                EJBFactory.getNote().add(data);
            else
                /*
                 * since the returned manager's lists are not created from scratch
                 * but only have the changes made in the beans, we need to make
                 * sure that all new empty notes get removed because they never
                 * got added to the database to begin with  
                 */
                man.removeNoteAt(0);
        }

        return man;
    }

    public NoteManager update(NoteManager man) throws Exception {
        NoteViewDO data;
        NoteBean nl;

        nl = EJBFactory.getNote();
        for (int j = 0; j < man.deleteCount(); j++)
            nl.delete(man.getDeletedAt(j));

        if (man.count() > 0) {
            data = man.getNoteAt(0);
            if (data.getId() == null) {
                if (data.getSubject() != null || data.getText() != null) {
                    data.setReferenceId(man.getReferenceId());
                    data.setReferenceTableId(man.getReferenceTableId());                
                    nl.add(data);
                } else {
                    /*
                     * since the returned manager's lists are not created from 
                     * scratch but only have the changes made in the beans, we
                     * need to make sure that all new empty notes get removed
                     * because they never got added to the database to begin with  
                     */
                    man.removeNoteAt(0);
                }
            } else {
                nl.update(data);
            }
        }

        return man;
    }

    public void validate(NoteManager man) throws Exception {
    }
}