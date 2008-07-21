/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The University of Iowa.  All Rights Reserved.
*/
package org.openelis.remote;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

import org.openelis.domain.QaEventDO;

@Remote
public interface QaEventRemote {
   
    // method to return QaEvent 
    public QaEventDO getQaEvent(Integer qaEventId);
    
    public QaEventDO getQaEventAndUnlock(Integer qaEventId, String session);
    
    public QaEventDO getQaEventAndLock(Integer qaEventId, String session)throws Exception;    
     
    //  commit a change to QaEvent, or insert a new provider
    public Integer updateQaEvent(QaEventDO qaEventDO)throws Exception;
    
    //  method to query for QaEvent
    public List query(HashMap fields, int first, int max) throws Exception;
    
    //a way for the servlet to get the system user id
    public Integer getSystemUserId();
        
    //method to get all the tests for a given QaEvent
    public List<Object[]> getTestNames();
    
    public List validateForAdd(QaEventDO qaeDO);
    
    public List validateForUpdate(QaEventDO qaeDO);    
    
    
}
