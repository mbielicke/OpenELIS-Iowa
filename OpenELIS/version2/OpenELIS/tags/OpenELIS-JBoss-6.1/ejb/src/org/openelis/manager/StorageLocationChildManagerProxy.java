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

import org.openelis.domain.StorageLocationViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.StorageLocationLocal;
import org.openelis.utils.EJBFactory;

public class StorageLocationChildManagerProxy {
    
    public StorageLocationChildManager fetchByParentStorageLocationId(Integer id) throws Exception {
        StorageLocationChildManager cm;
        ArrayList<StorageLocationViewDO> list;
        
        list = EJBFactory.getStorageLocation().fetchByParentStorageLocationId(id);
        cm = StorageLocationChildManager.getInstance();
        cm.setParentStorageLocationId(id);
        cm.setChildren(list);
        
        return cm;
    }
    
    public StorageLocationChildManager add(StorageLocationChildManager man) throws Exception {
        StorageLocationLocal sl;
        StorageLocationViewDO data;
        
        sl = EJBFactory.getStorageLocation();
        for (int i = 0; i < man.count(); i++ ) {
            data = man.getChildAt(i);
            data.setParentStorageLocationId(man.getParentStorageLocationId());
            data.setName(man.getParentStorageLocationName());
            data.setSortOrder(i+1);            
            sl.add(data);
        }
        
        return man;
    }
    
    public StorageLocationChildManager update(StorageLocationChildManager man) throws Exception {
        StorageLocationLocal sl;
        StorageLocationViewDO data;
        
        sl = EJBFactory.getStorageLocation();
        for (int j = 0; j < man.deleteCount(); j++ )
            sl.delete(man.getDeletedAt(j));
     
        for (int i = 0; i < man.count(); i++ ) {
            data = man.getChildAt(i);
            data.setName(man.getParentStorageLocationName());
            data.setSortOrder(i+1);            
            if(data.getId() == null) {
                data.setParentStorageLocationId(man.getParentStorageLocationId());
                sl.add(data);
            }  else {
                sl.update(data);
            }
        }

        return man;                        
    }
    
    
    public void validate(StorageLocationChildManager man) throws Exception {
        int i;
        ValidationErrorsList list;
        StorageLocationLocal sl;        
        StorageLocationViewDO data;
        
        sl = EJBFactory.getStorageLocation();
        list = new ValidationErrorsList();
        
        for(i = 0;  i < man.deleteCount(); i++) {
            data = man.getDeletedAt(i);
            try {
                sl.validateForDelete(data);
            } catch(Exception e) {
                DataBaseUtil.mergeException(list, e);                
            }
        }
        
        for (i = 0; i < man.count(); i++ ) {
            try {
                sl.validateChildStorageLocation(man.getChildAt(i));
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "childStorageLocsTable", i);
            }
        }
        if (list.size() > 0)
            throw list;
    }
}
