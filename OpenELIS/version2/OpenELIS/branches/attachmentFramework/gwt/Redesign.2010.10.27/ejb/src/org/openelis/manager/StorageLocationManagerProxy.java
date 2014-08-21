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

import javax.naming.InitialContext;

import org.openelis.domain.StorageLocationViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.StorageLocationLocal;

public class StorageLocationManagerProxy {

    public StorageLocationManager fetchById(Integer id) throws Exception {
        StorageLocationLocal sl;
        StorageLocationViewDO data;
        StorageLocationManager m;
        
        sl = local();
        data = sl.fetchById(id);
        m = StorageLocationManager.getInstance();
        
        m.setStorageLocation(data);
        m.setStorageLocationId(data.getId());
        
        return m;
    }
    
    public StorageLocationManager fetchWithChildren(Integer id) throws Exception {
        StorageLocationManager m;
        
        m = fetchById(id);
        m.getChildren();

        return m;
    }
    
    public StorageLocationManager add(StorageLocationManager man) throws Exception {
        Integer id;
        String name;
        StorageLocationLocal sl;
        
        sl = local();
        sl.add(man.getStorageLocation());
        id = man.getStorageLocation().getId();
        man.setStorageLocationId(id);        
        name = man.getStorageLocation().getName();
        
        if(man.children != null) {
            man.getChildren().setParentStorageLocationId(id);
            man.getChildren().setParentStorageLocationName(name);
            man.getChildren().add();
        }
     
        return man;
    }
    
    public StorageLocationManager update(StorageLocationManager man) throws Exception {
        Integer id;
        String name;
        StorageLocationLocal sl;
        
        sl = local();
        sl.update(man.getStorageLocation());
        id = man.getStorageLocation().getId();
        name = man.getStorageLocation().getName();
        
        if(man.children != null) {
            man.getChildren().setParentStorageLocationId(id);
            man.getChildren().setParentStorageLocationName(name);
            man.getChildren().update();
        }
     
        return man;
    }
    
    public StorageLocationManager fetchForUpdate(Integer id) throws Exception {
        assert false : "not supported";
        return null;
    }
    
    public StorageLocationManager abortUpdate(Integer id) throws Exception {
        assert false : "not supported";
        return null;
    }
    
    
    public void validate(StorageLocationManager man) throws Exception {
        ValidationErrorsList list;
        
        list = new ValidationErrorsList();
        
        try {
            local().validateParentStorageLocation(man.getStorageLocation());
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }
        try {
            if (man.children != null)
                man.getChildren().validate();
        } catch (Exception e) {
            DataBaseUtil.mergeException(list, e);
        }        
        
        if (list.size() > 0)
            throw list;
    }
    
    private StorageLocationLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (StorageLocationLocal)ctx.lookup("openelis/StorageLocationBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
