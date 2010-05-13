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

import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.WorksheetAnalysisDO;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ValidationErrorsList;

public class WorksheetAnalysisManager implements RPC {
    
    private static final long                      serialVersionUID = 1L;
    boolean                                        notDone;
    protected Integer                              worksheetId, worksheetItemId;
    protected ArrayList<WorksheetAnalysisListItem> analyses, deleted;
    
    protected transient static WorksheetAnalysisManagerProxy proxy;
    
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
    
    public WorksheetAnalysisDO getWorksheetAnalysisAt(int i) {
        return analyses.get(i).worksheetAnalysis;
    }
    
    public void setWorksheetAnalysisAt(WorksheetAnalysisDO analysis, int i) {
        if (analyses == null)
            analyses = new ArrayList<WorksheetAnalysisListItem>();
        analyses.get(i).worksheetAnalysis = analysis;
    }
    
    public void addWorksheetAnalysis(WorksheetAnalysisDO analysis) {
        WorksheetAnalysisListItem listItem;
        
        if (analyses == null)
            analyses = new ArrayList<WorksheetAnalysisListItem>();
        listItem = new WorksheetAnalysisListItem();
        listItem.worksheetAnalysis = analysis;
        analyses.add(listItem);
    }
    
    public void removeWorksheetAnalysisAt(int i) {
        WorksheetAnalysisListItem tmp;
        
        if (analyses == null || i >= analyses.size())
            return;
        
        tmp = analyses.remove(i);
        if (tmp.worksheetAnalysis.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<WorksheetAnalysisListItem>();
            deleted.add(tmp);
        }
    }
    
    public WorksheetResultManager getWorksheetResultAt(int i) throws Exception {
        WorksheetAnalysisListItem analysis = analyses.get(i);

        if (analysis.worksheetResult == null) {
            if (analysis.worksheetAnalysis != null && analysis.worksheetAnalysis.getId() != null) {
                try {
                    analysis.worksheetResult = WorksheetResultManager.fetchByWorksheetAnalysisId(analysis.worksheetAnalysis.getId());
                } catch (NotFoundException e) {
                    //ignore
                } catch (Exception e) {
                    throw e;
                }
            }
        }
            
        if (analysis.worksheetResult == null)
            analysis.worksheetResult = WorksheetResultManager.getInstance();
    
        return analysis.worksheetResult;
    }

    public void setWorksheetResultAt(WorksheetResultManager result, int i) {
        analyses.get(i).worksheetResult = result;
    }

    public WorksheetQcResultManager getWorksheetQcResultAt(int i) throws Exception {
        WorksheetAnalysisListItem analysis = analyses.get(i);

        if (analysis.worksheetQcResult == null) {
            if (analysis.worksheetAnalysis != null && analysis.worksheetAnalysis.getId() != null) {
                try {
                    analysis.worksheetQcResult = WorksheetQcResultManager.fetchByWorksheetAnalysisId(analysis.worksheetAnalysis.getId());
                } catch (NotFoundException e) {
                    //ignore
                } catch (Exception e) {
                    throw e;
                }
            }
        }
            
        if (analysis.worksheetQcResult == null)
            analysis.worksheetQcResult = WorksheetQcResultManager.getInstance();
    
        return analysis.worksheetQcResult;
    }

    public void setWorksheetQcResultAt(WorksheetQcResultManager qcResult, int i) {
        analyses.get(i).worksheetQcResult = qcResult;
    }

    public SampleDataBundle getBundleAt(int i) throws Exception {
        int                       j, k;
        SampleManager             sManager;
        SampleItemManager         siManager;
        AnalysisManager           aManager;
        AnalysisViewDO            aVDO;
        WorksheetAnalysisDO       waDO;
        WorksheetAnalysisListItem analysis = analyses.get(i);

        if (analysis.bundle == null) {
            waDO = analysis.worksheetAnalysis;
            if (waDO != null && waDO.getId() != null) {
                try {
                    sManager = SampleManager.fetchByAccessionNumber(Integer.valueOf(waDO.getAccessionNumber()));
                    siManager = sManager.getSampleItems();
                    for (j = 0; j < siManager.count(); j++) {
                        aManager = siManager.getAnalysisAt(j);
                        for (k = 0; k < aManager.count(); k++) {
                            aVDO = aManager.getAnalysisAt(k);
                            if (waDO.getAnalysisId().equals(aVDO.getId())) {
                                analysis.bundle = aManager.getBundleAt(k);
                                break;
                            }
                        }
                        if (analysis.bundle != null)
                            break;
                    }
                } catch (NotFoundException e) {
                    //ignore
                } catch (Exception e) {
                    throw e;
                }
            }
        }
            
        return analysis.bundle;
    }

    public WorksheetAnalysisListItem getItemAt(int i) {
        return (WorksheetAnalysisListItem)analyses.get(i);
    }
        
    //service methods
    public static WorksheetAnalysisManager fetchByWorksheetItemId(Integer id) throws Exception {
        return proxy().fetchByWorksheetItemId(id);
    }

    public WorksheetAnalysisManager add(HashMap<Integer,Integer> idHash, HashMap<Integer,SampleManager> sManagers) throws Exception {
        return proxy().add(this, idHash, sManagers);
    }
    
    public WorksheetAnalysisManager update(HashMap<Integer,Integer> idHash, HashMap<Integer,SampleManager> sManagers) throws Exception {
        return proxy().update(this, idHash, sManagers);
    }
       
    public void validate(ValidationErrorsList errorList) throws Exception {
        proxy().validate(this, errorList);
    }

    // friendly methods used by managers and proxies
    Integer getWorksheetId() {
        return worksheetId;
    }

    void setWorksheetId(Integer id) {
        worksheetId = id;
    }
    
    Integer getWorksheetItemId() {
        return worksheetItemId;
    }

    void setWorksheetItemId(Integer id) {
        worksheetItemId = id;
    }
    
    boolean getNotDone() {
        return notDone;
    }
     
    void setNotDone(boolean nd) {
        notDone = nd;
    }
    
    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }
    
    WorksheetAnalysisListItem getDeletedAt(int i) {
        return deleted.get(i);
    }
    
    private static WorksheetAnalysisManagerProxy proxy() {
        if(proxy == null)
            proxy = new WorksheetAnalysisManagerProxy();
        return proxy;
    }
    
    static class WorksheetAnalysisListItem implements RPC {
        private static final long serialVersionUID = 1L;

        WorksheetAnalysisDO      worksheetAnalysis;
        WorksheetResultManager   worksheetResult;
        WorksheetQcResultManager worksheetQcResult;
        SampleDataBundle         bundle;
    }
}
