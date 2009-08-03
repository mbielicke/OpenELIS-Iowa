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
package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.domain.NoteDO;
import org.openelis.exception.MultipleNoteException;
import org.openelis.gwt.common.RPC;

public class NotesManager implements RPC {

    private static final long serialVersionUID = 1L;
    protected Integer referenceId;
    protected Integer referenceTableId;
    protected ArrayList<NoteDO> notes;
    
    protected transient static NotesManagerProxy proxy;
    
    protected NotesManager() {
        referenceId = null;
        referenceTableId = null;
        notes = null;
    }
    
    /**
     * Creates a new instance of this object.
     */
    public static NotesManager getInstance() {
        NotesManager nm;

        nm = new NotesManager();
        nm.notes = new ArrayList<NoteDO>();

        return nm;
    }
    
    public int count(){
        if(notes == null)
            return 0;
        
        return notes.size();
    }
    
    public NoteDO getNoteAt(int i) {
        return notes.get(i);
    }
    
    public NoteDO getInternalEditingNote() {
        if(count() == 0 || notes.get(0).getId() != null){
            NoteDO note = new NoteDO();
            note.setIsExternal("N");
            
            try{
                addNote(note);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
        return getNoteAt(0);
    }
    
    public NoteDO getExternalEditingNote() {
        if(count() == 0){
            NoteDO note = new NoteDO();
            note.setIsExternal("Y");
            
            try{
                addNote(note);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
        return getNoteAt(0);
    }
    
    public void addNote(NoteDO note) throws Exception {
        //we are only going to allow 1 external note.  External notes can be modified
        //so there is no reason to have more than 1.
        if("Y".equals(note.getIsExternal()) && count() > 0)
            throw new MultipleNoteException();
        
        //you can only add 1 internal note at a time.  This checks to see if we 
        //already have an uncommited internal note.
        for(int i=0; i<count(); i++){
            NoteDO noteDO = getNoteAt(i);
            
            if(noteDO.getId() == null)
                throw new MultipleNoteException();
        }
        
        if(notes == null)
            notes = new ArrayList<NoteDO>();
        
        notes.add(0, note);
    }
    
    public static NotesManager findByRefTableRefId(Integer tableId, Integer id) throws Exception {
        return proxy().fetch(tableId, id);
    }
        
    public void setReferenceTableId(Integer referenceTableId) {
        this.referenceTableId = referenceTableId;
    }
    
    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
    }
    
    public Integer getReferenceId() {
        return referenceId;
    }

    public Integer getReferenceTableId() {
        return referenceTableId;
    }

    //service methods
    public NotesManager add() throws Exception {
        return proxy().add(this);
    }
    
    public NotesManager update() throws Exception {
        return proxy().update(this);
    }
    
    public void validate() throws Exception {
        
    }
        
    private static NotesManagerProxy proxy(){
        if(proxy == null)
            proxy = new NotesManagerProxy();
        
        return proxy;
    }

    //these are friendly methods so only managers and proxies can call this method
    ArrayList<NoteDO> getNotes() {
        return notes;
    }

    void setNotes(ArrayList<NoteDO> notes) {
        this.notes = notes;
    }
}
