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

import org.openelis.domain.SectionViewDO;
import org.openelis.messages.SectionCacheMessage;
import org.openelis.persistence.EJBFactory;
import org.openelis.persistence.MessageHandler;
import org.openelis.remote.SectionRemote;

public class SectionCacheHandler implements MessageHandler<SectionCacheMessage> {

    protected static ArrayList<SectionViewDO> sectionList;
    protected static HashMap<Integer, SectionViewDO> idValues;
    
    static {
        sectionList = new ArrayList<SectionViewDO>();
        idValues = new HashMap<Integer, SectionViewDO>();
    }
    
    public void handle(SectionCacheMessage message) {  
        SectionViewDO data;
        Integer id;
        
        id = message.getSectionDO().getId();
        data = idValues.get(id);
        if(data != null)
            idValues.remove(data);
        sectionList.clear();     
    }
    
    public static ArrayList<SectionViewDO> getSectionList() {
        if (sectionList.size() == 0) {
            try {
                sectionList = (ArrayList<SectionViewDO>)remote().fetchList();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sectionList;
    }

    public static SectionViewDO getSectionDOFromId(Integer id) {       
        SectionViewDO data;
        
        data = idValues.get(id);
        if(data == null){
            try {
                data = remote().fetchById(id);                
                if(data != null)
                    idValues.put(id, data);
                
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } 
        
        return data;
    }
  
    private static SectionRemote remote(){
        return (SectionRemote)EJBFactory.lookup("openelis/SectionBean/remote");
    }
}
