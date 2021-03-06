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

import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.meta.StorageMeta;
import org.openelis.modules.storage.client.StorageService;

public class StorageManagerProxy {
    
    public StorageManagerProxy(){
    }
    
    public StorageManager fetchById(Integer tableId, Integer id) throws Exception {
        Query query;
        QueryData field;
        ArrayList<QueryData> fields;

        fields = new ArrayList<QueryData>();
        query = new Query();

        field = new QueryData();
        field.setQuery(tableId.toString());
        fields.add(field);

        field = new QueryData();
        field.setQuery(id.toString());
        fields.add(field);

        query.setFields(fields);

        return StorageService.get().fetchById(query);
    }
    
    public StorageManager fetchCurrentByLocationId(Integer id) throws Exception {         
        return StorageService.get().fetchCurrentByLocationId(id);
    }
    
    public StorageManager fetchHistoryByLocationId(Integer id, int first, int max) throws Exception {
        Query query;
        QueryData field;
        
        query = new Query();
        
        field = new QueryData();
        field.setKey(StorageMeta.getStorageLocationId());
        field.setQuery(id.toString());
        field.setType(QueryData.Type.INTEGER);            
        query.setFields(field);
                     
        query.setRowsPerPage(max);
        query.setPage(first * max);  
        return StorageService.get().fetchHistoryByLocationId(query);
    }
    
    public StorageManager add(StorageManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    public StorageManager update(StorageManager man) throws Exception {
        return StorageService.get().update(man);
    }

    public void validate(StorageManager man, ValidationErrorsList errorsList) throws Exception {
        
    }
}
