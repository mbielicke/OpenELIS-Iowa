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
package org.openelis.server.handlers;

import java.util.HashMap;
import java.util.List;

import org.openelis.domain.IdNameDO;
import org.openelis.messages.ReferenceTableCacheMessage;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.persistence.MessageHandler;
import org.openelis.remote.ReferenceTableRemote;

public class ReferenceTableCacheHandler implements MessageHandler<ReferenceTableCacheMessage> {
    
    static ReferenceTableRemote remote = (ReferenceTableRemote)EJBFactory.lookup("openelis/ReferenceTableBean/remote");
    
    public static int version = 0;

    public void handle(ReferenceTableCacheMessage message) {
        CachingManager.remove("InitialData", "referenceTableValues");
        
    }
    
    public static HashMap<String, Integer> getReferenceTableId(String referenceTableName) {
        HashMap<String, Integer> tableHash = (HashMap<String, Integer>)CachingManager.getElement("InitialData", "referenceTableValues");
        if(tableHash == null) {
            tableHash = new HashMap<String, Integer>();
            List<IdNameDO> tables = remote.GetAllReferenceTables();
        
            for(IdNameDO resultDO :  tables)
                tableHash.put(resultDO.getName(), resultDO.getId());
            
            CachingManager.putElement("InitialData", "referenceTableValues", tableHash);
            version++;
        }
        return tableHash;
        
    }
}
