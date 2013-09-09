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
package org.openelis.modules.worksheetBuilder.client;

import java.util.ArrayList;

import org.openelis.domain.AnalysisViewVO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.WorksheetAnalysisViewDO;
import org.openelis.domain.WorksheetQcChoiceVO;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.services.TokenService;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.WorksheetManager1;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

public class WorksheetBuilderService implements WorksheetBuilderServiceInt,
                                                 WorksheetBuilderServiceIntAsync {
    
    static WorksheetBuilderService instance;
    
    WorksheetBuilderServiceIntAsync service;
    
    public static WorksheetBuilderService get() {
        if(instance == null)
            instance = new WorksheetBuilderService();
        
        return instance;
    }
    
    private WorksheetBuilderService() {
        service = (WorksheetBuilderServiceIntAsync)GWT.create(WorksheetBuilderServiceInt.class);
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }

    @Override
    public void getColumnNames(Integer formatId, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.getColumnNames(formatId, callback);
    }

    @Override
    public ArrayList<IdNameVO> getColumnNames(Integer formatId) throws Exception {
        Callback<ArrayList<IdNameVO>> callback;
        
        callback = new Callback<ArrayList<IdNameVO>>();
        service.getColumnNames(formatId, callback);
        return callback.getResult();
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public ArrayList<IdNameVO> query(Query query) throws Exception {
        Callback<ArrayList<IdNameVO>> callback;
        
        callback = new Callback<ArrayList<IdNameVO>>();
        service.query(query, callback);
        return callback.getResult();
    }

    @Override
    public void fetchAnalysesByView(Query query, AsyncCallback<ArrayList<AnalysisViewVO>> callback) {
        service.fetchAnalysesByView(query, callback);
    }

    @Override
    public ArrayList<AnalysisViewVO> fetchAnalysesByView(Query query) throws Exception {
        Callback<ArrayList<AnalysisViewVO>> callback;
        
        callback = new Callback<ArrayList<AnalysisViewVO>>();
        service.fetchAnalysesByView(query, callback);
        return callback.getResult();
    }

    @Override
    public void fetchUnitsForWorksheetAutocomplete(Integer analysisId, String unitOfMeasure,
                                                   AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.fetchUnitsForWorksheetAutocomplete(analysisId, unitOfMeasure, callback);
    }

    @Override
    public ArrayList<IdNameVO> fetchUnitsForWorksheetAutocomplete(Integer analysisId,
                                                                  String unitOfMeasure) throws Exception {
        Callback<ArrayList<IdNameVO>> callback;
        
        callback = new Callback<ArrayList<IdNameVO>>();
        service.fetchUnitsForWorksheetAutocomplete(analysisId, unitOfMeasure, callback);
        return callback.getResult();
    }

    @Override
    public void fetchAnalytesByAnalysis(Integer analysisId, Integer testId, AsyncCallback<ArrayList<ResultViewDO>> callback) {
        service.fetchAnalytesByAnalysis(analysisId, testId, callback);
    }

    @Override
    public ArrayList<ResultViewDO> fetchAnalytesByAnalysis(Integer analysisId, Integer testId) throws Exception {
        Callback<ArrayList<ResultViewDO>> callback;
        
        callback = new Callback<ArrayList<ResultViewDO>>();
        service.fetchAnalytesByAnalysis(analysisId, testId, callback);
        return callback.getResult();
    }

    @Override
    public void loadTemplate(WorksheetManager1 wm, Integer testId, AsyncCallback<WorksheetQcChoiceVO> callback) {
        service.loadTemplate(wm, testId, callback);
    }

    @Override
    public WorksheetQcChoiceVO loadTemplate(WorksheetManager1 wm, Integer testId) throws Exception {
        Callback<WorksheetQcChoiceVO> callback;
        
        callback = new Callback<WorksheetQcChoiceVO>();
        service.loadTemplate(wm, testId, callback);
        return callback.getResult();
    }

    @Override
    public void initializeResults(WorksheetManager1 wm, ArrayList<WorksheetAnalysisViewDO> analyses,
                                  AsyncCallback<WorksheetManager1> callback) {
        service.initializeResults(wm, analyses, callback);
    }

    @Override
    public WorksheetManager1 initializeResults(WorksheetManager1 wm, ArrayList<WorksheetAnalysisViewDO> analyses) throws Exception {
        Callback<WorksheetManager1> callback;
        
        callback = new Callback<WorksheetManager1>();
        service.initializeResults(wm, analyses, callback);
        return callback.getResult();
    }

    @Override
    public void initializeResultsFromOther(WorksheetManager1 wm, ArrayList<WorksheetAnalysisViewDO> analyses,
                                           Integer fromWorksheetId, AsyncCallback<WorksheetManager1> callback) {
        service.initializeResultsFromOther(wm, analyses, fromWorksheetId, callback);
    }

    @Override
    public WorksheetManager1 initializeResultsFromOther(WorksheetManager1 wm, ArrayList<WorksheetAnalysisViewDO> analyses,
                                                        Integer fromWorksheetId) throws Exception {
        Callback<WorksheetManager1> callback;
        
        callback = new Callback<WorksheetManager1>();
        service.initializeResultsFromOther(wm, analyses, fromWorksheetId, callback);
        return callback.getResult();
    }

    @Override
    public void sortItems(WorksheetManager1 wm, ArrayList<Object> keys, int direction, AsyncCallback<WorksheetManager1> callback) {
        service.sortItems(wm, keys, direction, callback);
    }

    @Override
    public WorksheetManager1 sortItems(WorksheetManager1 wm, ArrayList<Object> keys, int direction) throws Exception {
        Callback<WorksheetManager1> callback;
        
        callback = new Callback<WorksheetManager1>();
        service.sortItems(wm, keys, direction, callback);
        return callback.getResult();
    }
}
