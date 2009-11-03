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

import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ValidationErrorsList;

public class SampleQaEventManager implements RPC {
    
private static final long serialVersionUID = 1L;
    
    protected Integer                           sampleId;
    protected ArrayList<SampleQaEventViewDO>       items, deletedList;
    
    protected transient static SampleQAEventManagerProxy proxy;

    /**
     * Creates a new instance of this object.
     */
    public static SampleQaEventManager getInstance() {
        SampleQaEventManager sqam;

        sqam = new SampleQaEventManager();
        sqam.items = new ArrayList<SampleQaEventViewDO>();

        return sqam;
    }
    
    /**
     * Creates a new instance of this object with the specified sample id. Use this function to load an instance of this object from database.
     */
    public static SampleQaEventManager findBySampleId(Integer sampleId) throws Exception {
        return proxy().fetchBySampleId(sampleId);
    }
    
    public int count(){
        if(items == null)
            return 0;
        
        return items.size();
    }
    
    //getters/setters
    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }
    
    public SampleQaEventViewDO getSampleQAAt(int i) {
        return items.get(i);

    }

    public void setSampleQAAt(SampleQaEventViewDO sampleQA, int i) {
        items.set(i, sampleQA);
    }
    
    public void addSampleQA(SampleQaEventViewDO sampleQA){
        items.add(sampleQA);
    }
    
    public void removeSampleQAAt(int i){
        if(items == null || i >= items.size())
            return;
       
        SampleQaEventViewDO tmpQA = items.remove(i);
        
        if(deletedList == null)
            deletedList = new ArrayList<SampleQaEventViewDO>();
        
        if(tmpQA.getId() != null)
            deletedList.add(tmpQA);
    }
    
    /*
    public int getIndex(AnalysisTestDO aDO){
        for(int i=0; i<count(); i++)
            if(items.get(i).analysis == aDO)
                return i;
        
        return -1;
    }*/
    
    // service methods
    public SampleQaEventManager add() throws Exception {
        return proxy().add(this);
    }

    public SampleQaEventManager update() throws Exception {
        return proxy().update(this);
    }
    
    public void validate() throws Exception {
        ValidationErrorsList errorsList = new ValidationErrorsList();
        
        proxy().validate(this, errorsList);
        
        if(errorsList.size() > 0)
            throw errorsList;
    }
    
    public void validate(ValidationErrorsList errorsList) throws Exception {
        proxy().validate(this, errorsList);
    }
    
    private static SampleQAEventManagerProxy proxy() {
        if (proxy == null)
            proxy = new SampleQAEventManagerProxy();

        return proxy;
    }
    
    int deleteCount() {
        if (deletedList == null)
            return 0;

        return deletedList.size();
    }

    SampleQaEventViewDO getDeletedAt(int i) {
        return deletedList.get(i);
    }       
    
  //these are friendly methods so only managers and proxies can call this method
    ArrayList<SampleQaEventViewDO> getSampleQAs() {
        return items;
    }

    void setSampleQAEvents(ArrayList<SampleQaEventViewDO> sampleQas) {
        this.items = sampleQas;
    }

}