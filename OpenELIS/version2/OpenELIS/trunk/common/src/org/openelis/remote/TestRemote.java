package org.openelis.remote;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

import org.openelis.domain.TestDetailsDO;
import org.openelis.domain.TestIdNameMethodIdDO;

@Remote
public interface TestRemote {

    public TestIdNameMethodIdDO getTestIdNameMethod(Integer testId);
    
    public TestIdNameMethodIdDO getTestIdNameMethodAndUnlock(Integer testId, String session);
    
    public TestIdNameMethodIdDO getTestIdNameMethodLock(Integer testId, String session) throws Exception;
        
    public TestDetailsDO getTestDetails(Integer testId);
    
    public Integer updateTest(TestIdNameMethodIdDO testIdNameMethod,TestDetailsDO testDetails) ;
    
    public List query(HashMap fields, int first, int max) throws Exception;
    
    public Integer getSystemUserId();
    
    public List validateForUpdate(TestIdNameMethodIdDO testIdNameMethod,TestDetailsDO testDetails);
    
    public List validateForAdd(TestIdNameMethodIdDO testIdNameMethod,TestDetailsDO testDetails);
}
