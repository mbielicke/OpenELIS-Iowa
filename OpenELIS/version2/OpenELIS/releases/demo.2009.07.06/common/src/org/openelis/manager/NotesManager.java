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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

import org.openelis.domain.NoteDO;


public class NotesManager implements Serializable{

    private static final long serialVersionUID = 1L;
     
    protected boolean external, loaded;
    protected Integer referenceId;
    protected Integer referenceTableId;
    protected ArrayList<NoteDO> notes;
    
    @Transient
    protected NotesManagerIOInt manager;
    
    /**
     * Creates a new instance of this object.
     */
    public static NotesManager getInstance() {
        NotesManager nm;

        nm = new NotesManager();
        nm.notes = new ArrayList<NoteDO>();

        return nm;
    }
    
    //FIXME cleanup this code...
    public NoteDO add(){
         boolean needOne = false;
         NoteDO noteDO = null;

         manager().fetch();
         if (notes.size() == 0) {
             needOne = true;
         } else {
             noteDO = notes.get(0);
             if (!external && noteDO.getId() != null)
                 needOne = true;
         }
         if (needOne) {
             noteDO = new NoteDO();
             notes.add(0,noteDO);
         }

         return noteDO; 
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
    
    public List<NoteDO> getNotes() {
       manager().fetch();
       return notes; 
    }
    
   
     private NotesManagerIOInt manager() {
         //manager = ManagerFactory.getNotesManager();
         return manager;
     }     
}
