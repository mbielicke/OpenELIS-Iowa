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
import java.util.List;

import org.openelis.domain.TestResultViewDO;
import org.openelis.gwt.common.RPC;


public class TestResultManager implements RPC {

    private static final long serialVersionUID = 1L;
    
    protected Integer testId;
    protected ArrayList<ArrayList<TestResultViewDO>> results;    
    protected ArrayList<TestResultViewDO> deletedResults;   
    
    protected transient static TestResultManagerProxy proxy;
    
    protected TestResultManager() {
    }

    /**
     * Creates a new instance of this object.
     */
    public static TestResultManager getInstance() {
        TestResultManager trm;
        
        trm = new TestResultManager();
        trm.results = new ArrayList<ArrayList<TestResultViewDO>>();
        
        return trm;
    }
    
    /**
     * This method returns the number of result groups maintained by this manager.
     */
    public int groupCount(){
        if(results == null)
            return 0;
        
        return results.size();
    }
    
    /**
     * This method returns the size of the result group for the index "group". 
     * As noted earlier the index "group" starts at one.
     */
    public int getResultGroupSize(int group) {        
        if(results == null || group <= 0 || group-1 >= results.size())
            return 0;
        
        return results.get(group-1).size();
    }
    
    /**
     * This method returns the TestResultViewDO stored in the result group 
     * specified by the argument "group" and at the index "row" in the group.
     * It should be noted that "group" starts at one and NOT zero as it mirrors
     * the data as seen on the screen by the user and not how it is maintained
     * by the manager. When result groups are created or assigned to test analytes,
     * on the Test screen, they numbering always starts with one.
     * The argument "row" however starts at zero as this index is always hidden
     * from the user.
     */
    public TestResultViewDO getResultAt(int group, int row) {
        ArrayList<TestResultViewDO> list;
        
        if(group <= 0 || row < 0 || group-1 >= results.size())
            return null;
        
        list = results.get(group-1);
        
        if(row < list.size())
            return list.get(row);
        
        return null;
    }   
    
    /**
     * This method adds a TestResultViewDO at the end of the result group specified 
     * by the argument "group" and sets the id of the DO to the argument "id".
     * The argument "group" is an index that begins at one. 
     */
    public TestResultViewDO addResult(int group,Integer id) {
        TestResultViewDO result;
        if(results == null || group <= 0 || group-1 >= results.size())
            return null;
   
        result = new TestResultViewDO();
        result.setId(id);
        result.setResultGroup(group);
        results.get(group-1).add(result);
        
        return result;
    }
    
    /**
     * This method adds a TestResultViewDO at the index "row" of the result group specified 
     * by the argument "group" and sets the id of the DO to the argument "id".
     * The argument "group" is an index that begins at one. 
     */
    public TestResultViewDO addResultAt(int group,int row,Integer id) {
        ArrayList<TestResultViewDO> list;
        TestResultViewDO result;
        
        if(row < 0 || results == null || group <= 0 || group-1 >= results.size())
            return null;
        
        list = results.get(group-1);
        result = new TestResultViewDO();
        result.setId(id);
        result.setResultGroup(group);
        if (row < list.size())         
            list.add(row, result);
        else 
            list.add(result);                   
        
        return result;
    }
    
    public void addResultGroup() {        
        if(results == null)
            results = new ArrayList<ArrayList<TestResultViewDO>>();
        
        results.add(new ArrayList<TestResultViewDO>());
    }
    
    public TestResultViewDO removeResultAt(int group, int row) {
        ArrayList<TestResultViewDO> list;
        TestResultViewDO result;
        
        if(row < 0 || results == null || group <= 0 || group-1 >= results.size())
            return null;
        
        list = results.get(group-1);
        
        if(row >= list.size())
            return null;
        
        result = list.remove(row);
        
        if(result.getId() > 0) {
            if(deletedResults == null)
                deletedResults = new ArrayList<TestResultViewDO>();
            
            deletedResults.add(result);
        }
        
        return result;
    }
    
    public void removeResultGroup(int group) {
        if(results == null || group <= 0 || group-1 >= results.size())
            return;
        
        results.remove(group-1);
    }
    
    public static TestResultManager fetchByTestId(Integer testId) throws Exception {
        return proxy().fetchByTestId(testId);
    }
    
    public TestResultManager add(HashMap<Integer,Integer> idMap) throws Exception {
        return proxy().add(this,idMap);
    }
    
    public TestResultManager update(HashMap<Integer,Integer> idMap) throws Exception {
        return proxy().update(this,idMap);
    }
    
    public void validate(TestTypeOfSampleManager ttsm,HashMap<Integer,
                         List<TestResultViewDO>> resGrpRsltMap) throws Exception {
        proxy().validate(this,ttsm,resGrpRsltMap);
    }

    Integer getTestId() {
        return testId;
    }

    void setTestId(Integer testId) {
        this.testId = testId;
    }       

    ArrayList<ArrayList<TestResultViewDO>> getResults() {
        return results;
    }

    void setResults(ArrayList<ArrayList<TestResultViewDO>> results) {
        this.results = results;
    }
    
    int deleteCount(){
        if(deletedResults == null)
            return 0;
        
        return deletedResults.size();
    }
    
    TestResultViewDO getDeletedAt(int i) {
        return deletedResults.get(i);
    }
    
    private static TestResultManagerProxy proxy() {
        if(proxy == null)
            proxy = new TestResultManagerProxy();
        
        return proxy;
    }
    
}
