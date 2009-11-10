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

import org.openelis.domain.WorksheetAnalysisDO;
import org.openelis.gwt.common.RPC;

public class WorksheetAnalysisManager implements RPC {
    
    private static final long                serialVersionUID = 1L;
    protected Integer                        worksheetItemId;
    protected ArrayList<WorksheetAnalysisDO> analyses, deleted;
    
    protected transient static WorksheetAnalysisManagerProxy proxy;
    
    protected WorksheetAnalysisManager() {
    }
    
    /**
     * Creates a new instance of this object.
     */
    public static WorksheetAnalysisManager getInstance() {
        return new WorksheetAnalysisManager();
    }
    
    public int count() {
        if (analyses == null)
            return 0;
        
        return analyses.size();
    }
    
    public WorksheetAnalysisDO getAnalysisAt(int i) {
        return analyses.get(i);
    }
    
    public void setAnalysisAt(WorksheetAnalysisDO analysis, int i) {
        if (analyses == null)
            analyses = new ArrayList<WorksheetAnalysisDO>();
        analyses.set(i, analysis);
    }
    
    public void addAnalysis(WorksheetAnalysisDO analysis) {
        if (analyses == null)
            analyses = new ArrayList<WorksheetAnalysisDO>();
        analyses.add(analysis);
    }
    
    public void addAnalysisAt(WorksheetAnalysisDO analysis, int i) {
        if (analyses == null)
            analyses = new ArrayList<WorksheetAnalysisDO>();
        analyses.add(i, analysis);
    }
    
    public void removeAnalysisAt(int i) {
        WorksheetAnalysisDO tmp;
        
        if (analyses == null || i >= analyses.size())
            return;
        
        tmp = analyses.remove(i);
        if (tmp.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<WorksheetAnalysisDO>();
            deleted.add(tmp);
        }
    }
    
    //service methods
    public static WorksheetAnalysisManager fetchByWorksheetItemId(Integer id) throws Exception {
        return proxy().fetchByWorksheetItemId(id);
    }

    public WorksheetAnalysisManager add() throws Exception {
        return proxy().add(this);
    }
    
    public WorksheetAnalysisManager update() throws Exception {
        return proxy().update(this);
    }
       
    public void validate() throws Exception {
        proxy().validate(this);
    }

    // friendly methods used by managers and proxies
    Integer getWorksheetItemId() {
        return worksheetItemId;
    }

    void setWorksheetItemId(Integer id) {
        worksheetItemId = id;
    }
    
    ArrayList<WorksheetAnalysisDO> getAnalyses() {
        return analyses;
    }

    void setAnalyses(ArrayList<WorksheetAnalysisDO> analyses) {
        this.analyses = analyses;
    }
    
    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }
    
    WorksheetAnalysisDO getDeletedAt(int i) {
        return deleted.get(i);
    }
    
    private static WorksheetAnalysisManagerProxy proxy() {
        if(proxy == null)
            proxy = new WorksheetAnalysisManagerProxy();
        return proxy;
    }
}
