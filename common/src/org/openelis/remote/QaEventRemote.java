package org.openelis.remote;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

import org.openelis.domain.QaEventDO;

@Remote
public interface QaEventRemote {
   
    // method to return QaEvent 
    public QaEventDO getQaEvent(Integer qaEventId);
    
    public QaEventDO getQaEventAndUnlock(Integer qaEventId);
    
    public QaEventDO getQaEventAndLock(Integer qaEventId)throws Exception;    
     
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
