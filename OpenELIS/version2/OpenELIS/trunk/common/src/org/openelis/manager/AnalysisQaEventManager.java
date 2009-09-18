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

import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ValidationErrorsList;

public class AnalysisQaEventManager implements RPC {

    private static final long serialVersionUID = 1L;
    
    protected Integer                           analysisId;
    protected ArrayList<AnalysisQaEventViewDO>       items, deletedList;
    
    protected transient static AnalysisQAEventManagerProxy proxy;

    /**
     * Creates a new instance of this object.
     */
    public static AnalysisQaEventManager getInstance() {
        AnalysisQaEventManager aqam;

        aqam = new AnalysisQaEventManager();
        aqam.items = new ArrayList<AnalysisQaEventViewDO>();

        return aqam;
    }
    
    /**
     * Creates a new instance of this object with the specified sample id. Use this function to load an instance of this object from database.
     */
    public static AnalysisQaEventManager findByAnalysisId(Integer analysisId) throws Exception {
        return proxy().fetchByAnalysisId(analysisId);
    }
    
    public int count(){
        if(items == null)
            return 0;
        
        return items.size();
    }
    
  //getters/setters
    public Integer getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Integer analysisId) {
        this.analysisId = analysisId;
    }
    
    public AnalysisQaEventViewDO getAnalysisQAAt(int i) {
        return items.get(i);

    }

    public void setAnalysisQAAt(AnalysisQaEventViewDO analysisQA, int i) {
        items.set(i, analysisQA);
    }
    
    public void addAnalysisQA(AnalysisQaEventViewDO analysisQA){
        items.add(analysisQA);
    }
    
    public void removeAnalysisQAAt(int i){
        if(items == null || i >= items.size())
            return;
       
        AnalysisQaEventViewDO tmpQA = items.remove(i);
        
        if(deletedList == null)
            deletedList = new ArrayList<AnalysisQaEventViewDO>();
        
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
    public AnalysisQaEventManager add() throws Exception {
        return proxy().add(this);
    }

    public AnalysisQaEventManager update() throws Exception {
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
    
    private static AnalysisQAEventManagerProxy proxy() {
        if (proxy == null)
            proxy = new AnalysisQAEventManagerProxy();

        return proxy;
    }
    
    int deleteCount() {
        if (deletedList == null)
            return 0;

        return deletedList.size();
    }

    AnalysisQaEventViewDO getDeletedAt(int i) {
        return deletedList.get(i);
    }       
    
  //these are friendly methods so only managers and proxies can call this method
    ArrayList<AnalysisQaEventViewDO> getAnalysisQAs() {
        return items;
    }

    void setAnalysisQAEvents(ArrayList<AnalysisQaEventViewDO> analysisQas) {
        this.items = analysisQas;
    }
}
