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

import org.openelis.domain.TestReflexViewDO;
import org.openelis.domain.TestResultViewDO;
import org.openelis.gwt.common.RPC;

public class TestReflexManager implements RPC {

    private static final long serialVersionUID = 1L;
    
    protected Integer testId;
    protected ArrayList<TestReflexViewDO> reflexes;
    protected ArrayList<TestReflexViewDO> deleted;
    
    protected transient static TestReflexManagerProxy proxy;
    
    protected TestReflexManager() {
    }
    
    /**
     * Creates a new instance of this object.
     */
    public static TestReflexManager getInstance() {        
        return new TestReflexManager();
    }
    
    public int count(){
        if(reflexes == null)
            return 0;
        
        return reflexes.size();
    }
    
    public TestReflexViewDO getReflexAt(int i) {
        return reflexes.get(i);
    }
    
    public void setReflexAt(TestReflexViewDO reflexTest, int i) {
        if(reflexes == null)
            reflexes = new ArrayList<TestReflexViewDO>();
        reflexes.set(i, reflexTest);
    }
    
    public void addReflex(TestReflexViewDO reflexTest) {
        if(reflexes == null)
            reflexes = new ArrayList<TestReflexViewDO>();        
        reflexes.add(reflexTest);
    }
    
    public void addReflexAt(TestReflexViewDO reflexTest, int i) {
        if(reflexes == null)
            reflexes = new ArrayList<TestReflexViewDO>();        
        reflexes.add(i,reflexTest);
    }
    
    public void removeReflexAt(int i) {
        TestReflexViewDO reflexTest;
     
        if (reflexes == null || i >= reflexes.size())
            return;

        reflexTest = reflexes.remove(i);
        if(reflexTest.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<TestReflexViewDO>();
            deleted.add(reflexTest);
        }        
    }
    
    public ArrayList<TestReflexViewDO> getReflexListByTestAnalyteIdTestResultId(Integer testAnalyteId,
                                                                                Integer testResultId) {
        int                         i;
        ArrayList<TestReflexViewDO> reflexList;
        TestReflexViewDO            reflexDO;
        
        reflexList = new ArrayList<TestReflexViewDO>();
        
        if (testResultId != null) {
            for (i = 0; i < count(); i++) {
                reflexDO = getReflexAt(i);
                if (testAnalyteId.equals(reflexDO.getTestAnalyteId()) &&
                    testResultId.equals(reflexDO.getTestResultId()))
                    reflexList.add(reflexDO);
            }
        }
        
        return reflexList;
    }
    
    public static TestReflexManager fetchByTestId(Integer testId) throws Exception {
        return proxy().fetchByTestId(testId);
    }
    
    public TestReflexManager add(HashMap<Integer,Integer> analyteMap,
                                 HashMap<Integer,Integer> resultMap) throws Exception {
        return proxy().add(this,analyteMap,resultMap);                
    }
    
    public TestReflexManager update(HashMap<Integer,Integer> analyteMap,
                                    HashMap<Integer,Integer> resultMap) throws Exception {
        return proxy().update(this,analyteMap,resultMap);                
    }
    
    public void validate(boolean analyteValid,boolean resultValid,
                         HashMap<Integer, Integer> anaResGrpMap,
                         HashMap<Integer, List<TestResultViewDO>> resGrpRsltMap) throws Exception{        
        proxy().validate(this,analyteValid,resultValid,anaResGrpMap,resGrpRsltMap);        
    }
    
    Integer getTestId() {
        return testId;
    }

    void setTestId(Integer testId) {
        this.testId = testId;
    }   

    ArrayList<TestReflexViewDO> getReflexes() {
        return reflexes;
    }
    
    void setReflexes(ArrayList<TestReflexViewDO> reflexes) {
        this.reflexes = reflexes;
    }
    
    int deleteCount(){
        if(deleted == null)
            return 0;
        
        return deleted.size();
    }
        
    TestReflexViewDO getDeletedAt(int i) {
        return deleted.get(i);
    }
    
    private static TestReflexManagerProxy proxy() {
        if(proxy == null)
            proxy = new TestReflexManagerProxy();
        
        return proxy;
    }
        
}