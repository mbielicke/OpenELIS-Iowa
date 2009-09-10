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

import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.gwt.common.RPC;


public class TestTypeOfSampleManager implements RPC {

    private static final long serialVersionUID = 1L;
    
    protected Integer testId;
    protected ArrayList<TestTypeOfSampleDO> types;
    protected ArrayList<TestTypeOfSampleDO> deletedTypes;
    
    protected transient static TestTypeOfSampleManagerProxy proxy;
    
    protected TestTypeOfSampleManager() {
        types = null;
    }
    
    /**
     * Creates a new instance of this object.
     */
    public static TestTypeOfSampleManager getInstance() {
        TestTypeOfSampleManager ttsm;
        
        ttsm = new TestTypeOfSampleManager();
        ttsm.types = new ArrayList<TestTypeOfSampleDO>();
        
        return ttsm;
    }
    
    public static TestTypeOfSampleManager findByTestId(Integer testId) throws Exception {
        return proxy().fetchByTestId(testId);
    }
    
    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }
    
    public int count(){
        if(types == null)
            return 0;
        
        return types.size();
    }
    
    public TestTypeOfSampleDO getTypeAt(int i) {
        return types.get(i);
    }
    
    public void setTypeAt(TestTypeOfSampleDO sampleType, int i) {
        types.set(i, sampleType);
    }
    
    public void addType(TestTypeOfSampleDO sampleType) {
        if(types == null)
            types = new ArrayList<TestTypeOfSampleDO>();
        
        types.add(sampleType);
    }
    
    public void addTypeAt(TestTypeOfSampleDO sampleType,int i) {
        if(types == null)
            types = new ArrayList<TestTypeOfSampleDO>();
        
        types.add(i,sampleType);
    }
    
    public void removeTypeAt(int i) {
        TestTypeOfSampleDO sampleType;
        if (types == null || i >= types.size())
            return;

        sampleType = types.remove(i);
        if (sampleType.getId() != null) {
            if (deletedTypes == null)
                deletedTypes = new ArrayList<TestTypeOfSampleDO>();
            deletedTypes.add(sampleType);
        }        
    }
    
    public TestTypeOfSampleManager add() throws Exception {
        return proxy().add(this);
    }
    
    public TestTypeOfSampleManager update() throws Exception {
        return proxy().update(this);
    } 
    
    ArrayList<TestTypeOfSampleDO> getTypes() {
        return types;
    }
    
    void setTypes(ArrayList<TestTypeOfSampleDO> types) {
        this.types = types;
    }
    
    int deleteCount(){
        if(deletedTypes == null)
            return 0;
        
        return deletedTypes.size();
    }
    
    TestTypeOfSampleDO getDeletedAt(int i) {
        return deletedTypes.get(i);
    }
    
    private static TestTypeOfSampleManagerProxy proxy() {
        if(proxy == null)
            proxy = new TestTypeOfSampleManagerProxy();
            
        return proxy;
    }


}
