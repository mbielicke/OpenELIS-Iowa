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

import org.openelis.domain.TestResultDO;
import org.openelis.gwt.common.RPC;


public class TestResultManager implements RPC {

    private static final long serialVersionUID = 1L;
    
    protected Integer testId;
    protected ArrayList<ArrayList<TestResultDO>> results;    
    protected ArrayList<TestResultDO> deletedResults;   
    
    protected transient static TestResultManagerProxy proxy;
    
    protected TestResultManager() {
        results = null;
    }

    /**
     * Creates a new instance of this object.
     */
    public static TestResultManager getInstance() {
        TestResultManager trm;
        
        trm = new TestResultManager();
        trm.results = new ArrayList<ArrayList<TestResultDO>>();
        
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
        if(results == null)
            return 0;
        
        return results.size();
    }
    
    public TestResultDO getResultAt(int group, int row) {
        ArrayList<TestResultDO> list;
        
        if(group <= 0 || row < 0 || group-1 >= results.size())
            return null;
        
        list = results.get(group-1);
        
        if(row < list.size())
            return list.get(row);
        
        return null;
    }
    
    public int getResultGroupSize(int group) {        
        if(results == null || group <= 0 || group-1 >= results.size())
            return 0;
        
        return results.get(group-1).size();
    }
    
    public void addResult(int group,Integer id) {
        TestResultDO result;
        if(results == null || group <= 0 || group-1 >= results.size())
            return;
   
        result = new TestResultDO();
        result.setId(id);
        results.get(group-1).add(result);
    }
    
    public void addResultAt(int group,int row,Integer id) {
        ArrayList<TestResultDO> list;
        TestResultDO result;
        
        if(row < 0 || results == null || group <= 0 || group-1 >= results.size())
            return;
        
        list = results.get(group-1);
        result = new TestResultDO();
        result.setId(id);
        if (row < list.size()) {        
            list.add(row, result);
        } else {
            list.add(result);
        }            
    }
    
    public void addResultGroup() {        
        if(results == null)
            results = new ArrayList<ArrayList<TestResultDO>>();
        
        results.add(new ArrayList<TestResultDO>());
    }
    
    public void removeResultAt(int group, int row) {
        ArrayList<TestResultDO> list;
        TestResultDO testResult;
        
        if(row < 0 || results == null || group <= 0 || group-1 >= results.size())
            return;
        
        list = results.get(group-1);
        
        if(row >= list.size())
            return;
        
        testResult = list.remove(row);
        
        if(testResult.getId() > 0) {
            if(deletedResults == null)
                deletedResults = new ArrayList<TestResultDO>();
            
            deletedResults.add(testResult);
        }
    }
    
    public void removeResultGroup(int group) {
        if(results == null || group <= 0 || group-1 >= results.size())
            return;
        
        results.remove(group-1);
    }
    
    public TestResultManager add(HashMap<Integer,Integer> idMap) throws Exception {
        return proxy().add(this,idMap);
    }
    
    public TestResultManager update(HashMap<Integer,Integer> idMap) throws Exception {
        return proxy().update(this,idMap);
    }

    ArrayList<ArrayList<TestResultDO>> getResults() {
        return results;
    }

    void setResults(ArrayList<ArrayList<TestResultDO>> testResults) {
        this.results = testResults;
    }
    
    int deleteCount(){
        if(deletedResults == null)
            return 0;
        
        return deletedResults.size();
    }
    
    TestResultDO getDeletedAt(int i) {
        return deletedResults.get(i);
    }
    
    private static TestResultManagerProxy proxy() {
        if(proxy == null)
            proxy = new TestResultManagerProxy();
        
        return proxy;
    }
    
}
