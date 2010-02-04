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
package org.openelis.modules.storage.server;

import java.util.ArrayList;

import org.openelis.domain.StorageLocationVO;
import org.openelis.gwt.common.DatabaseException;
import org.openelis.manager.StorageManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.StorageLocationRemote;
import org.openelis.remote.StorageManagerRemote;
import org.openelis.gwt.common.data.Query;

public class StorageService {
    public StorageManager fetchById(Query query) throws Exception {
        try{
            return remote().fetchById(new Integer(query.getFields().get(0).query), 
                                      new Integer(query.getFields().get(1).query));
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
 
    public ArrayList<StorageLocationVO> fetchAvailableByName(String search) throws Exception {
        try{
            return storageLocationRemote().fetchAvailableByName(search+"%", 10);
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
    
    private StorageManagerRemote remote(){
        return (StorageManagerRemote)EJBFactory.lookup("openelis/StorageManagerBean/remote");
    }
    
    private StorageLocationRemote storageLocationRemote(){
        return (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
    }
}
