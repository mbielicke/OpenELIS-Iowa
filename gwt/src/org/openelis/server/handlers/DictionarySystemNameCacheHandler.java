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

import org.openelis.messages.DictionarySystemNameCacheMessage;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.persistence.MessageHandler;
import org.openelis.remote.CategoryRemote;

public class DictionarySystemNameCacheHandler implements MessageHandler<DictionarySystemNameCacheMessage> {
    
    static CategoryRemote remote = (CategoryRemote)EJBFactory.lookup("openelis/CategoryBean/remote");
    
    public static int version = 0;

    public void handle(DictionarySystemNameCacheMessage message) {
        CachingManager.remove("InitialData", "dictSystemNameValues");
        
    }
    
    public static Integer getIdFromSystemName(String systemName) {
        HashMap<String, Integer> tableHash = (HashMap<String, Integer>)CachingManager.getElement("InitialData", "dictSystemNameValues");
        HashMap<Integer, String> idHash = (HashMap<Integer, String>)CachingManager.getElement("InitialData", "dictIdValues");
        Integer id;
        if(tableHash == null){
            tableHash = new HashMap<String, Integer>();
            idHash = new HashMap<Integer, String>();
        }
        
        id = tableHash.get(systemName);
        if(id == null){
            id = remote.getEntryIdForSystemName(systemName);
            
            if(id != null){
                tableHash.put(systemName, id);
                idHash.put(id, systemName);
                CachingManager.putElement("InitialData", "dictSystemNameValues", tableHash);
                CachingManager.putElement("InitialData", "dictIdValues", idHash);
                version++;
            }
        }
        
        return id;
    }
    
    public static String getSystemNameFromId(Integer id) {
        HashMap<String, Integer> tableHash = (HashMap<String, Integer>)CachingManager.getElement("InitialData", "dictSystemNameValues");
        HashMap<Integer, String> idHash = (HashMap<Integer, String>)CachingManager.getElement("InitialData", "dictIdValues");
        String systemName;
        if(tableHash == null){
            tableHash = new HashMap<String, Integer>();
            idHash = new HashMap<Integer, String>();   
        }
        
        systemName = idHash.get(id);
        if(systemName == null){
            systemName = remote.getSystemNameForEntryId(id);
            
            if(systemName != null){
                tableHash.put(systemName, id);
                idHash.put(id, systemName);
                CachingManager.putElement("InitialData", "dictSystemNameValues", tableHash);
                CachingManager.putElement("InitialData", "dictIdValues", idHash);
                version++;
            }
        }
        
        return systemName;
    }
}