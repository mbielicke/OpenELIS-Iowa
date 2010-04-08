/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.gwt.common.RPC;

public class TestTypeOfSampleManager implements RPC {

    private static final long                               serialVersionUID = 1L;

    protected Integer                                       testId;
    protected ArrayList<TestTypeOfSampleDO>                 types;
    protected ArrayList<TestTypeOfSampleDO>                 deleted;

    protected transient static TestTypeOfSampleManagerProxy proxy;

    protected TestTypeOfSampleManager() {
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
    
    public int count() {
        if (types == null)
            return 0;

        return types.size();
    }
    
    public TestTypeOfSampleDO getTypeAt(int i) {
        return types.get(i);
    }
    
    public void setTypeAt(TestTypeOfSampleDO sampleType, int i) {
        if (types == null)
            types = new ArrayList<TestTypeOfSampleDO>();
        types.set(i, sampleType);
    }

    public void addType(TestTypeOfSampleDO sampleType) {
        if (types == null)
            types = new ArrayList<TestTypeOfSampleDO>();
        types.add(sampleType);
    }

    public void addTypeAt(TestTypeOfSampleDO sampleType, int i) {
        if (types == null)
            types = new ArrayList<TestTypeOfSampleDO>();
        types.add(i, sampleType);
    }

    public void removeTypeAt(int i) {
        TestTypeOfSampleDO sampleType;
        
        if (types == null || i >= types.size())
            return;

        sampleType = types.remove(i);
        if (sampleType.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<TestTypeOfSampleDO>();
            deleted.add(sampleType);
        }
    }
    
    public ArrayList<TestTypeOfSampleDO> getTypesBySampleType(Integer type){
        ArrayList<TestTypeOfSampleDO> returnList;
        TestTypeOfSampleDO typeDO;
        
        returnList = new ArrayList<TestTypeOfSampleDO>();
        
        if(type != null){
            for(int i=0; i<count(); i++){
                typeDO = types.get(i); 
        
                if (type.equals(typeDO.getTypeOfSampleId()))
                    returnList.add(typeDO);
            }
        }
        
        return returnList;
        
    }

    public boolean hasType(Integer type){
        TestTypeOfSampleDO typeDO;
        for(int i=0; i<count(); i++){
            typeDO = getTypeAt(i);
            
            if(typeDO.getTypeOfSampleId().equals(type))
                return true;
        }
        
        return false;
    }
    
    public boolean hasUnit(Integer unitId, Integer typeId){
        TestTypeOfSampleDO typeDO;
        for(int i=0; i<count(); i++){
            typeDO = getTypeAt(i);
            
            if(typeDO.getTypeOfSampleId().equals(typeId) && typeDO.getUnitOfMeasureId().equals(unitId))
                return true;
        }
        
        return false;
    }
    
    public static TestTypeOfSampleManager fetchByTestId(Integer testId) throws Exception {
        return proxy().fetchByTestId(testId);
    }
    
    public TestTypeOfSampleManager add() throws Exception {
        return proxy().add(this);
    }

    public TestTypeOfSampleManager update() throws Exception {
        return proxy().update(this);
    }
    
    public void validate() throws Exception {
        proxy().validate(this);
    }

    Integer getTestId() {
        return testId;
    }

    void setTestId(Integer testId) {
        this.testId = testId;
    }

    ArrayList<TestTypeOfSampleDO> getTypes() {
        return types;
    }

    void setTypes(ArrayList<TestTypeOfSampleDO> types) {
        this.types = types;
    }

    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }

    TestTypeOfSampleDO getDeletedAt(int i) {
        return deleted.get(i);
    }

    private static TestTypeOfSampleManagerProxy proxy() {
        if (proxy == null)
            proxy = new TestTypeOfSampleManagerProxy();

        return proxy;
    }

}
