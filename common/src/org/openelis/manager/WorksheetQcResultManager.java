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

import org.openelis.domain.WorksheetQcResultDO;
import org.openelis.gwt.common.RPC;

public class WorksheetQcResultManager implements RPC {
    
    private static final long                serialVersionUID = 1L;
    protected Integer                        worksheetAnalysisId;
    protected ArrayList<WorksheetQcResultDO> qcResults, deleted;
    
    protected transient static WorksheetQcResultManagerProxy proxy;
    
    /**
     * Creates a new instance of this object.
     */
    public static WorksheetQcResultManager getInstance() {
        return new WorksheetQcResultManager();
    }
    
    public int count() {
        if (qcResults == null)
            return 0;
        
        return qcResults.size();
    }
    
    public WorksheetQcResultDO getWorksheetQcResultAt(int i) {
        return qcResults.get(i);
    }
    
    public void setWorksheetQcResultAt(WorksheetQcResultDO qcResult, int i) {
        if (qcResults == null)
            qcResults = new ArrayList<WorksheetQcResultDO>();
        qcResults.set(i, qcResult);
    }
    
    public void addWorksheetQcResult(WorksheetQcResultDO qcResult) {
        if (qcResults == null)
            qcResults = new ArrayList<WorksheetQcResultDO>();
        qcResults.add(qcResult);
    }
    
    public void addWorksheetQcResultAt(WorksheetQcResultDO qcResult, int i) {
        if (qcResults == null)
            qcResults = new ArrayList<WorksheetQcResultDO>();
        qcResults.add(i, qcResult);
    }
    
    public void removeItemAt(int i) {
        WorksheetQcResultDO tmp;
        
        if (qcResults == null || i >= qcResults.size())
            return;
        
        tmp = qcResults.remove(i);
        if (tmp.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<WorksheetQcResultDO>();
            deleted.add(tmp);
        }
    }
    
    //service methods
    public static WorksheetQcResultManager fetchByWorksheetAnalysisId(Integer id) throws Exception {
        return proxy().fetchByWorksheetAnalysisId(id);
    }

    public WorksheetQcResultManager add() throws Exception {
        return proxy().add(this);
    }
    
    public WorksheetQcResultManager update() throws Exception {
        return proxy().update(this);
    }
    
    public void validate() throws Exception {
        proxy().validate(this);
    }
       
    // friendly methods used by managers and proxies
    Integer getWorksheetAnalysisId() {
        return worksheetAnalysisId;
    }

    void setWorksheetAnalysisId(Integer id) {
        worksheetAnalysisId = id;
    }
    
    ArrayList<WorksheetQcResultDO> getWorksheetQcResults() {
        return qcResults;
    }
    
    void setWorksheetQcResults(ArrayList<WorksheetQcResultDO> qcResults) {
        this.qcResults = qcResults;
    }
    
    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }
    
    WorksheetQcResultDO getDeletedAt(int i) {
        return deleted.get(i);
    }
    
    private static WorksheetQcResultManagerProxy proxy() {
        if(proxy == null)
            proxy = new WorksheetQcResultManagerProxy();
        return proxy;
    }
}
