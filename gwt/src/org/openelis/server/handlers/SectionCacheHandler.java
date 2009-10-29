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

import org.openelis.domain.SectionDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.messages.SectionCacheMessage;
import org.openelis.persistence.CachingManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.persistence.MessageHandler;
import org.openelis.remote.SectionRemote;


public class SectionCacheHandler implements MessageHandler<SectionCacheMessage> {

    public void handle(SectionCacheMessage message) {  
        ArrayList<SectionViewDO> sectList;
        SectionViewDO msecDO,lsecDO;
        
        sectList = (ArrayList<SectionViewDO>)CachingManager.getElement("InitialData", "sectionList");        
        
        if(sectList !=null) 
            CachingManager.remove("InitialData", "sectionList");
        
    }
    
    public static ArrayList<SectionDO>  getSectionList() {
        SectionRemote remote;
        ArrayList<SectionDO> sectList; 
        
        sectList = (ArrayList<SectionDO>)CachingManager.getElement("InitialData", "sectionList");
        if(sectList == null) {
            remote = (SectionRemote)EJBFactory.lookup("openelis/SectionBean/remote");
            try {
                sectList = (ArrayList<SectionDO>)remote.fetchList();
            } catch (Exception e) {
                e.printStackTrace();
            }
            CachingManager.putElement("InitialData", "sectionList", sectList);
        }       
        return sectList;
    }
  
}
