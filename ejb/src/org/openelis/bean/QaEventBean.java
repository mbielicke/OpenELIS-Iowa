package org.openelis.bean;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;

import org.openelis.domain.QaEventDO;
import org.openelis.remote.QaEventRemote;

@Stateless
public class QaEventBean implements QaEventRemote{

    public QaEventDO getQaEvent(Integer qaEventId, boolean unlock) {
        // TODO Auto-generated method stub
        return null;
    }

    public List getQaEventNameListByLetter(String letter, int startPos, int maxResults) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Object[]> getQaEventTypes() {
        // TODO Auto-generated method stub
        return null;
    }

    public QaEventDO getQaEventUpdate(Integer id) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Integer getSystemUserId() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Object[]> getTests(Integer Id) {
        // TODO Auto-generated method stub
        return null;
    }

    public List query(HashMap fields, int first, int max) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Integer updateQaEvent(QaEventDO qaEventDO) {
        // TODO Auto-generated method stub
        return null;
    }
 
}
