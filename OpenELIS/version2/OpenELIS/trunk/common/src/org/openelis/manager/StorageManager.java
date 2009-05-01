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

import org.openelis.gwt.common.RPC;

public class StorageManager implements RPC {

    private static final long serialVersionUID = 1L;
    
    protected Integer  referenceId, referenceTableId;
    protected ArrayList<Item>  storageList;
    protected boolean cached;
    protected transient StorageManagerIOInt manager;
    
    class Item implements Serializable {
        private static final long serialVersionUID = 1L;
        //storageDO
    }

    /**
     * Creates a new instance of this object. A default Specimen object is also created.
     */
    public static StorageManager getInstance() {
        StorageManager sm;

        sm = new StorageManager();

        return sm;
    }
    
    public StorageManagerIOInt getManager() {
        return manager;
    }

    public void setManager(StorageManagerIOInt manager) {
        this.manager = manager;
    }

    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
    }

    public Integer getReferenceTableId() {
        return referenceTableId;
    }

    public void setReferenceTableId(Integer referenceTableId) {
        this.referenceTableId = referenceTableId;
    }
}
