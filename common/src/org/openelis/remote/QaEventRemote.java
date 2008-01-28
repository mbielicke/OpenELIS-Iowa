package org.openelis.remote;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

import org.openelis.domain.QaEventDO;

@Remote
public interface QaEventRemote {
    // method to return list of QaEvent ids and QaEvent names by the letter they start with
    public List getQaEventNameListByLetter(String letter, int startPos, int maxResults);
   
    // method to return QaEvent 
    public QaEventDO getQaEvent(Integer qaEventId, boolean unlock);
    
    //  update initial call for QaEvent
    public QaEventDO getQaEventUpdate(Integer id) throws Exception;
     
    //  commit a change to QaEvent, or insert a new provider
    public Integer updateQaEvent(QaEventDO qaEventDO);
    
    //  method to query for QaEvent
    public List query(HashMap fields, int first, int max) throws Exception;
    
    //a way for the servlet to get the system user id
    public Integer getSystemUserId();
    
    //method to get all the dictionary entries for QaEvents
    public List<Object[]> getQaEventTypes();
    
    //method to get all the tests for a given QaEvent
    public List<Object[]> getTests(Integer Id);
    
    
}
