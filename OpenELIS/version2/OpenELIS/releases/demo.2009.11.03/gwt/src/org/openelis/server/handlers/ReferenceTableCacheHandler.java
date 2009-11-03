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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openelis.domain.IdNameDO;
import org.openelis.messages.ReferenceTableCacheMessage;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.persistence.MessageHandler;
import org.openelis.remote.ReferenceTableRemote;

public class ReferenceTableCacheHandler implements MessageHandler<ReferenceTableCacheMessage> {
    
    public void handle(ReferenceTableCacheMessage message) {
    }
    
    public static IdNameDO getDOFromTableName(String tableName) {
        HashMap<String, IdNameDO> hash = (HashMap<String, IdNameDO>)CachingManager.getElement("InitialData", "referenceTableHash");
        ArrayList<IdNameDO> tableArray = (ArrayList<IdNameDO>)CachingManager.getElement("InitialData", "referenceTableList");

        //if the hash is null then load it from the database
        if(hash == null){
            ReferenceTableRemote remote = (ReferenceTableRemote)EJBFactory.lookup("openelis/ReferenceTableBean/remote");
            hash = new HashMap<String, IdNameDO>();
            List<IdNameDO> referenceTables = remote.GetAllReferenceTables();
            tableArray = (ArrayList<IdNameDO>)referenceTables;
            if(referenceTables != null){
                for(int i=0; i<referenceTables.size(); i++){
                    IdNameDO tableDO = (IdNameDO)referenceTables.get(i);
                    hash.put(tableDO.getName(), tableDO);
                }
                CachingManager.putElement("InitialData", "referenceTableValues", hash);
                CachingManager.putElement("InitialData", "referenceTableList", tableArray);
            }
        }
            
        return hash.get(tableName);
    }
    
    public static ArrayList getTableList() {
        HashMap<String, IdNameDO> hash = (HashMap<String, IdNameDO>)CachingManager.getElement("InitialData", "referenceTableHash");
        ArrayList<IdNameDO> tableArray = (ArrayList<IdNameDO>)CachingManager.getElement("InitialData", "referenceTableList");

        //if the hash is null then load it from the database
        if(hash == null){
            ReferenceTableRemote remote = (ReferenceTableRemote)EJBFactory.lookup("openelis/ReferenceTableBean/remote");
            hash = new HashMap<String, IdNameDO>();
            List<IdNameDO> referenceTables = remote.GetAllReferenceTables();
            tableArray = (ArrayList<IdNameDO>)referenceTables;
            if(referenceTables != null){
                for(int i=0; i<referenceTables.size(); i++){
                    IdNameDO tableDO = (IdNameDO)referenceTables.get(i);
                    hash.put(tableDO.getName(), tableDO);
                }
                CachingManager.putElement("InitialData", "referenceTableValues", hash);
                CachingManager.putElement("InitialData", "referenceTableList", tableArray);
            }
        }
            
        return tableArray;
    }
}