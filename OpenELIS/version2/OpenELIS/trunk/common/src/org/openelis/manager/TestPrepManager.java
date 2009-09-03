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
    protected ArrayList<TestPrepDO> prepTests;
    protected ArrayList<TestPrepDO> deletedPrepTests;
    
    protected transient static TestPrepManagerProxy proxy;
    
    protected TestPrepManager() {
        prepTests = null;
    }
    
    /**
     * Creates a new instance of this object.
     */
    public static TestPrepManager getInstance() {
        TestPrepManager tpm;
        
        tpm = new TestPrepManager();
        tpm.prepTests = new ArrayList<TestPrepDO>();
        
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
        if(prepTests == null)
            return 0;
        
        return prepTests.size();
    }
    
    public TestPrepDO getPrepTestAt(int i) {
        return prepTests.get(i);
    } 
    
    public void setPrepTestAt(TestPrepDO prepTest, int i) {
        prepTests.set(i,prepTest);
    }
    
    public void addPrepTest(TestPrepDO prepTest) {
        if(prepTests == null) 
            prepTests = new ArrayList<TestPrepDO>();
    
        prepTests.add(prepTest);
    }
    
    public void addPrepTestAt(TestPrepDO prepTest, int i) {
        if(prepTests == null) 
            prepTests = new ArrayList<TestPrepDO>();
    
        prepTests.add(i,prepTest);
    }
    
    public TestPrepManager add() throws Exception {
        return proxy().add(this);                
    }
    
    public TestPrepManager update() throws Exception {
        return proxy().update(this);                
    }
    
    ArrayList<TestPrepDO> getPrepTests() {
        return prepTests;
    }
    
    int deleteCount(){
        if(deletedPrepTests == null)
            return 0;
        
        return deletedPrepTests.size();
    }
    
    void setPrepTests(ArrayList<TestPrepDO> prepTests) {
        this.prepTests = prepTests;
    }       
    
    TestPrepDO getDeletedAt(int i) {
        return deletedPrepTests.get(i);
    }
    
    private static TestPrepManagerProxy proxy() {
        if(proxy == null)
            proxy = new TestPrepManagerProxy();
        
        return proxy;
    } 

}
