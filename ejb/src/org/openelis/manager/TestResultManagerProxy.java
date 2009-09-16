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
package org.openelis.manager;

import java.util.ArrayList;
import java.util.HashMap;

import javax.naming.InitialContext;

import org.openelis.domain.TestResultDO;
import org.openelis.local.TestLocal;

public class TestResultManagerProxy {
    
    public TestResultManager add(TestResultManager man,HashMap<Integer,Integer> idMap) throws Exception {
        TestLocal tl;              
        TestResultDO testResult;
        int i,j,size,negId;
        
        tl = getTestLocal();        
        
        for(i = 0; i < man.groupCount(); i++){
            size = man.getResultGroupSize(i+1);             
            for(j = 0; j < size; j++) {                
                testResult = man.getResultAt(i+1, j);
                negId = testResult.getId();
                testResult.setTestId(man.getTestId());
                testResult.setResultGroup(i+1);
                testResult.setSortOrder(j);
                
                tl.addTestResult(testResult);
                idMap.put(negId, testResult.getId());
            }
        }
        return man;
    }
        
    public TestResultManager update(TestResultManager man,HashMap<Integer,Integer> idMap) throws Exception {
        TestLocal tl;              
        TestResultDO testResult;
        int i,j,size,negId;
        
        tl = getTestLocal();
        
        try {
            for(i = 0; i < man.deleteCount(); i++) 
                tl.deleteTestResult(man.getDeletedAt(i));            
            
            for(i = 0; i < man.groupCount(); i++){
                size = man.getResultGroupSize(i+1);             
                for(j = 0; j < size; j++) {
                    testResult = man.getResultAt(i+1, j);                
                    testResult.setResultGroup(i+1);
                    testResult.setSortOrder(j);
                    negId = testResult.getId();
                    if(negId < 0) {                    
                        testResult.setTestId(man.getTestId());                    
                        tl.addTestResult(testResult);
                        idMap.put(negId, testResult.getId());
                    } else {
                        tl.updateTestResult(testResult);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        return man;
    }
    
    public TestResultManager fetchByTestId(Integer testId) throws Exception { 
        TestLocal tl;
        TestResultManager trm;
        ArrayList<ArrayList<TestResultDO>> results;
        
        tl = getTestLocal();
        results = tl.fetchTestResultsById(testId);
        trm = TestResultManager.getInstance();
        trm.setTestId(testId);
        trm.setResults(results);
        
        return trm;
    }
        
    private TestLocal getTestLocal(){
        try{
            InitialContext ctx = new InitialContext();
            return (TestLocal)ctx.lookup("openelis/TestBean/local");
        }catch(Exception e){
             System.out.println(e.getMessage());
             return null;
        }
    }
    
}
