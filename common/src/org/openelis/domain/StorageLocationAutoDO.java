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
package org.openelis.domain;

import java.io.Serializable;

import org.openelis.util.DataBaseUtil;

public class StorageLocationAutoDO implements Serializable{

    private static final long serialVersionUID = 1L;
    protected Integer id;
    protected String name;
    protected String location;
    protected Integer qtyOnHand;
    
    public Integer getQtyOnHand() {
        return qtyOnHand;
    }

    public void setQtyOnHand(Integer qtyOnHand) {
        this.qtyOnHand = qtyOnHand;
    }

    public StorageLocationAutoDO(){
        
    }
    
    public StorageLocationAutoDO(Integer id, String name, String location, String storageUnitDescription){
        setId(id);
        setName(name);
        
        setLocation(name.trim()+", "+storageUnitDescription.trim()+" "+location.trim());
    }
    
    public StorageLocationAutoDO(Integer id, String name, String location, String storageUnitDescription, Integer qtyOnHand){
        setId(id);
        setName(name);
        
        setLocation(name.trim()+", "+storageUnitDescription.trim()+" "+location.trim());
        setQtyOnHand(qtyOnHand);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = DataBaseUtil.trim(location);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = DataBaseUtil.trim(name);
    }

}
