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

import org.openelis.domain.TestPrepDO;
import org.openelis.gwt.common.RPC;

public class TestPrepManager implements RPC {

    private static final long serialVersionUID = 1L;
    
    protected Integer testId;
    protected ArrayList<TestPrepDO> preps;
    protected ArrayList<TestPrepDO> deletedPreps;
    
    protected transient static TestPrepManagerProxy proxy;
    
    protected TestPrepManager() {
        preps = null;
    }
    
    /**
     * Creates a new instance of this object.
     */
    public static TestPrepManager getInstance() {
        TestPrepManager tpm;
        
        tpm = new TestPrepManager();
        tpm.preps = new ArrayList<TestPrepDO>();
        
        return tpm;
    }
    
    public static TestPrepManager findByTestId(Integer testId) throws Exception {
        return proxy().fetchByTestId(testId);
    }
    
    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }
    
    public int count(){
        if(preps == null)
            return 0;
        
        return preps.size();
    }
    
    public TestPrepDO getPrepAt(int i) {
        return preps.get(i);
    } 
    
    public void setPrepAt(TestPrepDO prepTest, int i) {
        preps.set(i,prepTest);
    }
    
    public void addPrep(TestPrepDO prepTest) {
        if(preps == null) 
            preps = new ArrayList<TestPrepDO>();
    
        preps.add(prepTest);
    }
    
    public void addPrepAt(TestPrepDO prepTest, int i) {
        if(preps == null) 
            preps = new ArrayList<TestPrepDO>();
    
        preps.add(i,prepTest);
    }
    
    
    public void removePrepAt(int i) {
        TestPrepDO prepTest;
        if (preps == null || i >= preps.size())
            return;

        prepTest = preps.remove(i);
        if (prepTest.getId() != null) {
            if (deletedPreps == null)
                deletedPreps = new ArrayList<TestPrepDO>();
            deletedPreps.add(prepTest);
        }        
    }
    
    public TestPrepManager add() throws Exception {
        return proxy().add(this);                
    }
    
    public TestPrepManager update() throws Exception {
        return proxy().update(this);                
    }
    
    ArrayList<TestPrepDO> getPreps() {
        return preps;
    }
    
    void setPreps(ArrayList<TestPrepDO> prepTests) {
        this.preps = prepTests;
    }   
    
    int deleteCount(){
        if(deletedPreps == null)
            return 0;
        
        return deletedPreps.size();
    }
    
    TestPrepDO getDeletedAt(int i) {
        return deletedPreps.get(i);
    }
    
    private static TestPrepManagerProxy proxy() {
        if(proxy == null)
            proxy = new TestPrepManagerProxy();
        
        return proxy;
    } 

}
