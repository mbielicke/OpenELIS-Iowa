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

import java.io.Serializable;
import java.util.ArrayList;

import org.openelis.domain.WorksheetResultViewDO;
import org.openelis.gwt.common.ValidationErrorsList;

public class WorksheetResultManager implements Serializable {
    
    private static final long                  serialVersionUID = 1L;
    protected Integer                          worksheetAnalysisId;
    protected ArrayList<WorksheetResultViewDO> results, deleted;
    
    protected transient static WorksheetResultManagerProxy proxy;
    
    /**
     * Creates a new instance of this object.
     */
    public static WorksheetResultManager getInstance() {
        return new WorksheetResultManager();
    }
    
    public int count() {
        if (results == null)
            return 0;
        
        return results.size();
    }
    
    public WorksheetResultViewDO getWorksheetResultAt(int i) {
        return results.get(i);
    }
    
    public void setWorksheetResultAt(WorksheetResultViewDO result, int i) {
        if (results == null)
            results = new ArrayList<WorksheetResultViewDO>();
        results.set(i, result);
    }
    
    public void addWorksheetResult(WorksheetResultViewDO result) {
        if (results == null)
            results = new ArrayList<WorksheetResultViewDO>();
        results.add(result);
    }
    
    public void addWorksheetResultAt(WorksheetResultViewDO result, int i) {
        if (results == null)
            results = new ArrayList<WorksheetResultViewDO>();
        results.add(i, result);
    }
    
    public void removeItemAt(int i) {
        WorksheetResultViewDO tmp;
        
        if (results == null || i >= results.size())
            return;
        
        tmp = results.remove(i);
        if (tmp.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<WorksheetResultViewDO>();
            deleted.add(tmp);
        }
    }
    
    //service methods
    public static WorksheetResultManager fetchByWorksheetAnalysisId(Integer id) throws Exception {
        return proxy().fetchByWorksheetAnalysisId(id);
    }

    public WorksheetResultManager add() throws Exception {
        return proxy().add(this);
    }
    
    public WorksheetResultManager update() throws Exception {
        return proxy().update(this);
    }
    
    public void validate(ValidationErrorsList errorList) {
        proxy().validate(this, errorList);
    }
       
    // friendly methods used by managers and proxies
    Integer getWorksheetAnalysisId() {
        return worksheetAnalysisId;
    }

    void setWorksheetAnalysisId(Integer id) {
        worksheetAnalysisId = id;
    }
    
    ArrayList<WorksheetResultViewDO> getWorksheetResults() {
        return results;
    }
    
    void setWorksheetResults(ArrayList<WorksheetResultViewDO> results) {
        this.results = results;
    }
    
    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }
    
    WorksheetResultViewDO getDeletedAt(int i) {
        return deleted.get(i);
    }
    
    private static WorksheetResultManagerProxy proxy() {
        if(proxy == null)
            proxy = new WorksheetResultManagerProxy();
        return proxy;
    }
}
