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

import org.openelis.domain.TestSectionDO;
import org.openelis.gwt.common.RPC;

public class TestSectionManager implements RPC {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    protected Integer testId;
    protected ArrayList<TestSectionDO> testSections;
    protected ArrayList<TestSectionDO> deletedTestSections;
    
    protected transient static TestSectionManagerProxy proxy;
    
    protected TestSectionManager() {
        testSections = null;
    }
    
    /**
     * Creates a new instance of this object.
     */
    public static TestSectionManager getInstance() {
        TestSectionManager tsm;
        
        tsm = new TestSectionManager();
        tsm.testSections = new ArrayList<TestSectionDO>();
        
        return tsm;
    }
    
    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }       
   
    
    public int count(){
        if(testSections == null)
            return 0;
        
        return testSections.size();
    }
    
    public ArrayList<TestSectionDO> getTestSections() {
        return testSections;
    }

    public void setTestSections(ArrayList<TestSectionDO> testSections) {
        this.testSections = testSections;
    }
    
    public TestSectionDO getTestSectionAt(int i) {
        return testSections.get(i);
    }
    
    public void setTestSectionAt(TestSectionDO section, int i) {
        testSections.set(i, section);
    }
    
    public void addTestSection(TestSectionDO section){
        if(testSections == null)
            testSections = new ArrayList<TestSectionDO>();
        
        testSections.add(section);
    }
    
    public void addTestSectionAt(TestSectionDO section, int i){
        if(testSections == null)
            testSections = new ArrayList<TestSectionDO>();
        
        testSections.add(i, section);
    }
    
    public void removeTestSectionAt(int i){
        if(testSections == null || i >= testSections.size())
            return;
        
        TestSectionDO tmpDO = testSections.remove(i);
        
        if(deletedTestSections == null)
            deletedTestSections = new ArrayList<TestSectionDO>();
        
        deletedTestSections.add(tmpDO);
    }
    
    //service methods
    public TestSectionManager add() throws Exception {
        return proxy().add(this);
    }
    
    public TestSectionManager update() throws Exception {
        return proxy().update(this);
    }
       
    private static TestSectionManagerProxy proxy(){
        if(proxy == null)
            proxy = new TestSectionManagerProxy();
        
        return proxy;
    }
    
    int deleteCount(){
        if(deletedTestSections == null)
            return 0;
        
        return deletedTestSections.size();
    }
    
    TestSectionDO getDeletedAt(int i){
        return deletedTestSections.get(i);
    }
}
