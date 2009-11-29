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

import org.openelis.manager.StorageManager;
import org.openelis.modules.storage.client.StorageServiceParams;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.StorageManagerRemote;

public class StorageService {
    public StorageManager fetch(StorageServiceParams params) throws Exception {
        StorageManagerRemote remote = (StorageManagerRemote)EJBFactory.lookup("openelis/StorageManagerBean/remote");
        
        return remote.fetch(params.referenceTableId, params.referenceId);
    }
/*    
    public AutocompleteRPC getStorageMatches(AutocompleteRPC rpc) throws Exception {
        StorageLocationRemote remote = (StorageLocationRemote)EJBFactory.lookup("openelis/StorageLocationBean/remote");
        
        rpc.model = remote.autoCompleteLookupByName(rpc.match+"%", 10);
        
        return rpc;
    }
*/    
}
