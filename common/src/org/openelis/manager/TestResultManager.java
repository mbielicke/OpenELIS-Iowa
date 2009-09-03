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

import org.openelis.domain.TestResultDO;
import org.openelis.gwt.common.RPC;


public class TestResultManager implements RPC {

    private static final long serialVersionUID = 1L;
    
    protected Integer testId;
    protected ArrayList<ArrayList<TestResultDO>> testResults;    
    protected ArrayList<TestResultDO> deletedTestResults;   
    
    protected transient static TestResultManagerProxy proxy;
    
    protected TestResultManager() {
        testResults = null;
    }

    /**
     * Creates a new instance of this object.
     */
    public static TestResultManager getInstance() {
        TestResultManager trm;
        
        trm = new TestResultManager();
        trm.testResults = new ArrayList<ArrayList<TestResultDO>>();
        
        return trm;
    }
    
    public static TestResultManager findByTestId(Integer testId) throws Exception {
        return proxy().fetchByTestId(testId);
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }    
    
    public int groupCount(){
        if(testResults == null)
            return 0;
        
        return testResults.size();
    }
    
    public TestResultDO getTestResultAt(int group, int row) {
        ArrayList<TestResultDO> list;
        
        if(group <= 0 || row < 0 || group-1 >= testResults.size())
            return null;
        
        list = testResults.get(group-1);
        
        if(row < list.size())
            return list.get(row);
        
        return null;
    }
    
    public int getResultGroupSize(int group) {        
        if(testResults == null || group <= 0 || group-1 >= testResults.size())
            return 0;
        
        return testResults.get(group-1).size();
    }
    
    public void addTestResult(int group) {      
        if(testResults == null || group <= 0 || group-1 >= testResults.size())
            return;
   
        testResults.get(group-1).add(new TestResultDO());
    }
    
    public void addTestResultAt(int group, int row) {
        ArrayList<TestResultDO> list;
        
        if(row < 0 || testResults == null || group <= 0 || group-1 >= testResults.size())
            return;
        
        list = testResults.get(group-1);
        
        if (row < list.size()) {        
            list.add(row, new TestResultDO());
        } else {
            list.add(new TestResultDO());
        }            
    }
    
    public void addResultGroup() {        
        if(testResults == null)
            testResults = new ArrayList<ArrayList<TestResultDO>>();
        
        testResults.add(new ArrayList<TestResultDO>());
    }
    
    public void removeTestResultAt(int group, int row) {
        ArrayList<TestResultDO> list;
        TestResultDO testResult;
        
        if(row < 0 || testResults == null || group <= 0 || group-1 >= testResults.size())
            return;
        
        list = testResults.get(group-1);
        
        if(row >= list.size())
            return;
        
        testResult = list.remove(row);
        
        if(testResult.getId() != null) {
            if(deletedTestResults == null)
                deletedTestResults = new ArrayList<TestResultDO>();
            
            deletedTestResults.add(testResult);
        }
    }
    
    public void removeResultGroup(int group) {
        if(testResults == null || group <= 0 || group-1 >= testResults.size())
            return;
        
        testResults.remove(group-1);
    }
    
    public TestResultManager add() throws Exception {
        return proxy().add(this);
    }
    
    public TestResultManager update() throws Exception {
        return proxy().update(this);
    }

    ArrayList<ArrayList<TestResultDO>> getTestResults() {
        return testResults;
    }

    void setTestResults(ArrayList<ArrayList<TestResultDO>> testResults) {
        this.testResults = testResults;
    }
    
    int deleteCount(){
        if(deletedTestResults == null)
            return 0;
        
        return deletedTestResults.size();
    }
    
    TestResultDO getDeletedAt(int i) {
        return deletedTestResults.get(i);
    }
    
    private static TestResultManagerProxy proxy() {
        if(proxy == null)
            proxy = new TestResultManagerProxy();
        
        return proxy;
    }
    
}
