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
import org.openelis.gwt.common.RPC;
import org.openelis.manager.proxy.NotesManagerProxy;


public class NotesManager implements RPC {

    private static final long serialVersionUID = 1L;
    protected boolean external;
    protected Integer referenceId;
    protected Integer referenceTableId;
    protected ArrayList<NoteDO> notes;
    public boolean cached = false;
    
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

    /**
      * Sets the behavior for allocating and storing notes.
      */
    public void setExternal(boolean flag) {
        external = flag;
    }

    /**
      * Returns the flag describing the behavior for allocating and storing notes.
      */
    public boolean getExternal() {
        return external;
    }
    
    /*
    public List<NoteDO> getNotes() {
       manager().fetch();
       return notes; 
    }
    */
    
  //service methods
    public NotesManager add(){
        return proxy().commitAdd(this);
    }
    
    public NotesManager update(){
        return proxy().commitUpdate(this);
    }
    
    /*
    public NotesManager fetch(){
        if (cached || !load)
            return this;

        cached = true;
        load = false;
        
        if(referenceId == null || referenceTableId == null)
            return null;
        
        return proxy().fetch(this);
    }*/
    
    private static NotesManagerProxy proxy(){
        if(proxy == null)
            proxy = new NotesManagerProxy();
        
        return proxy;
    }

    public ArrayList<NoteDO> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<NoteDO> notes) {
        this.notes = notes;
        cached = true;
    }     
}
