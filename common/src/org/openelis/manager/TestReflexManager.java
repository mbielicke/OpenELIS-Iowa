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

import org.openelis.domain.TestReflexDO;
import org.openelis.gwt.common.RPC;

public class TestReflexManager implements RPC {

    private static final long serialVersionUID = 1L;
    
    protected Integer testId;
    protected ArrayList<TestReflexDO> reflexes;
    protected ArrayList<TestReflexDO> deletedReflexes;
    
    protected transient static TestReflexManagerProxy proxy;
    
    protected TestReflexManager() {
        reflexes = null;
    }
    
    /**
     * Creates a new instance of this object.
     */
    public static TestReflexManager getInstance() {
        TestReflexManager trm;
        
        trm = new TestReflexManager();
        trm.reflexes = new ArrayList<TestReflexDO>();
        
        return trm;
    }
    
    public static TestReflexManager findByTestId(Integer testId) throws Exception {
        return proxy().fetchByTestId(testId);
    }
    
    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }
    
    public int count(){
        if(reflexes == null)
            return 0;
        
        return reflexes.size();
    } 

    public TestReflexDO getReflexAt(int i) {
        return reflexes.get(i);
    }
    
    public void setReflexAt(TestReflexDO reflexTest, int i) {
        reflexes.set(i, reflexTest);
    }
    
    public void addReflex(TestReflexDO reflexTest) {
        if(reflexes == null)
            reflexes = new ArrayList<TestReflexDO>();
        
        reflexes.add(reflexTest);
    }
    
    public void addReflexAt(TestReflexDO reflexTest, int i) {
        if(reflexes == null)
            reflexes = new ArrayList<TestReflexDO>();
        
        reflexes.add(i,reflexTest);
    }
    
    public void removeReflexAt(int i) {
        TestReflexDO reflexTest;
     
        if (reflexes == null || i >= reflexes.size())
            return;

        reflexTest = reflexes.remove(i);
        if(reflexTest.getId() != null) {
            if (deletedReflexes == null)
                deletedReflexes = new ArrayList<TestReflexDO>();
            deletedReflexes.add(reflexTest);
        }        
    }
    
    public TestReflexManager add(HashMap<Integer,Integer> analyteMap,
                                 HashMap<Integer,Integer> resultMap) throws Exception {
        return proxy().add(this,analyteMap,resultMap);                
    }
    
    public TestReflexManager update(HashMap<Integer,Integer> analyteMap,
                                    HashMap<Integer,Integer> resultMap) throws Exception {
        return proxy().add(this,analyteMap,resultMap);                
    }

    ArrayList<TestReflexDO> getReflexes() {
        return reflexes;
    }
    
    void setReflexes(ArrayList<TestReflexDO> reflexes) {
        this.reflexes = reflexes;
    }
    
    int deleteCount(){
        if(deletedReflexes == null)
            return 0;
        
        return deletedReflexes.size();
    }
        
    TestReflexDO getDeletedAt(int i) {
        return deletedReflexes.get(i);
    }
    
    private static TestReflexManagerProxy proxy() {
        if(proxy == null)
            proxy = new TestReflexManagerProxy();
        
        return proxy;
    }
        
}