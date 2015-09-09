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
package org.openelis.modules.worksheet1.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

import org.openelis.domain.AnalysisViewVO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.WorksheetAnalysisViewDO;
import org.openelis.domain.WorksheetQcChoiceVO;
import org.openelis.domain.WorksheetResultsTransferVO;
import org.openelis.domain.WorksheetViewDO;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.WorksheetManager1;
import org.openelis.manager.WorksheetManager1.Load;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.services.TokenService;

public class WorksheetService1 implements WorksheetServiceInt1, WorksheetServiceInt1Async {

    static WorksheetService1  instance;

    WorksheetServiceInt1Async service;

    public static WorksheetService1 get() {
        if (instance == null)
            instance = new WorksheetService1();

        return instance;
    }

    private WorksheetService1() {
        service = (WorksheetServiceInt1Async)GWT.create(WorksheetServiceInt1.class);
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }

    @Override
    public WorksheetManager1 getInstance() throws Exception {
        Callback<WorksheetManager1> callback;

        callback = new Callback<WorksheetManager1>();
        service.getInstance(callback);
        return callback.getResult();
    }

    @Override
    public void getInstance(AsyncCallback<WorksheetManager1> callback) {
        service.getInstance(callback);
    }

    @Override
    public WorksheetManager1 fetchById(Integer worksheetId, WorksheetManager1.Load... elements) throws Exception {
        Callback<WorksheetManager1> callback;

        callback = new Callback<WorksheetManager1>();
        service.fetchById(worksheetId, elements, callback);
        return callback.getResult();
    }

    @Override
    public void fetchById(Integer worksheetId, WorksheetManager1.Load elements[],
                          AsyncCallback<WorksheetManager1> callback) {
        service.fetchById(worksheetId, elements, callback);
    }

    @Override
    public ArrayList<WorksheetManager1> fetchByIds(ArrayList<Integer> worksheetIds, WorksheetManager1.Load... elements) throws Exception {
        Callback<ArrayList<WorksheetManager1>> callback;

        callback = new Callback<ArrayList<WorksheetManager1>>();
        service.fetchByIds(worksheetIds, elements, callback);
        return callback.getResult();
    }

    @Override
    public void fetchByIds(ArrayList<Integer> worksheetIds, WorksheetManager1.Load elements[],
                          AsyncCallback<ArrayList<WorksheetManager1>> callback) {
        service.fetchByIds(worksheetIds, elements, callback);
    }

    @Override
    public ArrayList<IdNameVO> query(Query query) throws Exception {
        Callback<ArrayList<IdNameVO>> callback;

        callback = new Callback<ArrayList<IdNameVO>>();
        service.query(query, callback);
        return callback.getResult();
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public ArrayList<WorksheetViewDO> queryForLookup(Query query) throws Exception {
        Callback<ArrayList<WorksheetViewDO>> callback;

        callback = new Callback<ArrayList<WorksheetViewDO>>();
        service.queryForLookup(query, callback);
        return callback.getResult();
    }

    @Override
    public void queryForLookup(Query query, AsyncCallback<ArrayList<WorksheetViewDO>> callback) {
        service.queryForLookup(query, callback);
    }

    @Override
    public WorksheetManager1 fetchForUpdate(Integer worksheetId) throws Exception {
        Callback<WorksheetManager1> callback;

        callback = new Callback<WorksheetManager1>();
        service.fetchForUpdate(worksheetId, callback);
        return callback.getResult();
    }

    @Override
    public void fetchForUpdate(Integer worksheetId, AsyncCallback<WorksheetManager1> callback) {
        service.fetchForUpdate(worksheetId, callback);
    }

    @Override
    public WorksheetResultsTransferVO fetchForTransfer(Integer worksheetId) throws Exception {
        Callback<WorksheetResultsTransferVO> callback;

        callback = new Callback<WorksheetResultsTransferVO>();
        service.fetchForTransfer(worksheetId, callback);
        return callback.getResult();
    }

    @Override
    public void fetchForTransfer(Integer worksheetId, AsyncCallback<WorksheetResultsTransferVO> callback) {
        service.fetchForTransfer(worksheetId, callback);
    }

    @Override
    public WorksheetManager1 unlock(Integer worksheetId, Load... elements) throws Exception {
        Callback<WorksheetManager1> callback;

        callback = new Callback<WorksheetManager1>();
        service.unlock(worksheetId, elements, callback);
        return callback.getResult();
    }

    @Override
    public void unlock(Integer worksheetId, Load[] elements,
                       AsyncCallback<WorksheetManager1> callback) {
        service.unlock(worksheetId, elements, callback);
    }

    @Override
    public WorksheetManager1 update(WorksheetManager1 wm, WorksheetManager1.ANALYSIS_UPDATE updateFlag) throws Exception {
        Callback<WorksheetManager1> callback;

        callback = new Callback<WorksheetManager1>();
        service.update(wm, updateFlag, callback);
        return callback.getResult();
    }

    @Override
    public void update(WorksheetManager1 wm, WorksheetManager1.ANALYSIS_UPDATE updateFlag,
                       AsyncCallback<WorksheetManager1> callback) {
        service.update(wm, updateFlag, callback);
    }

    @Override
    public WorksheetResultsTransferVO transferResults(WorksheetManager1 wm, ArrayList<WorksheetAnalysisViewDO> waVDOs,
                                                      ArrayList<SampleManager1> sampleMans,
                                                      boolean ignoreWarnings) throws Exception {
        Callback<WorksheetResultsTransferVO> callback;

        callback = new Callback<WorksheetResultsTransferVO>();
        service.transferResults(wm, waVDOs, sampleMans, ignoreWarnings, callback);
        return callback.getResult();
    }

    @Override
    public void transferResults(WorksheetManager1 wm, ArrayList<WorksheetAnalysisViewDO> waVDOs,
                                ArrayList<SampleManager1> sampleMans, boolean ignoreWarnings,
                                AsyncCallback<WorksheetResultsTransferVO> callback) {
        service.transferResults(wm, waVDOs, sampleMans, ignoreWarnings, callback);
    }

    @Override
    public ArrayList<AnalysisViewVO> fetchAnalysesByView(Query query) throws Exception {
        Callback<ArrayList<AnalysisViewVO>> callback;

        callback = new Callback<ArrayList<AnalysisViewVO>>();
        service.fetchAnalysesByView(query, callback);
        return callback.getResult();
    }

    @Override
    public void fetchAnalysesByView(Query query, AsyncCallback<ArrayList<AnalysisViewVO>> callback) {
        service.fetchAnalysesByView(query, callback);
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
    public void fetchUnitsForWorksheetAutocomplete(Integer analysisId, String unitOfMeasure,
                                                   AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.fetchUnitsForWorksheetAutocomplete(analysisId, unitOfMeasure, callback);
    }

    @Override
    public ArrayList<ResultViewDO> fetchAnalytesByAnalysis(Integer analysisId, Integer testId) throws Exception {
        Callback<ArrayList<ResultViewDO>> callback;

        callback = new Callback<ArrayList<ResultViewDO>>();
        service.fetchAnalytesByAnalysis(analysisId, testId, callback);
        return callback.getResult();
    }

    @Override
    public void fetchAnalytesByAnalysis(Integer analysisId, Integer testId,
                                        AsyncCallback<ArrayList<ResultViewDO>> callback) {
        service.fetchAnalytesByAnalysis(analysisId, testId, callback);
    }

    @Override
    public ArrayList<IdNameVO> getColumnNames(Integer formatId) throws Exception {
        Callback<ArrayList<IdNameVO>> callback;

        callback = new Callback<ArrayList<IdNameVO>>();
        service.getColumnNames(formatId, callback);
        return callback.getResult();
    }

    @Override
    public void getColumnNames(Integer formatId, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.getColumnNames(formatId, callback);
    }

    @Override
    public ArrayList<IdNameVO> getHeaderLabelsForScreen(WorksheetManager1 manager) throws Exception {
        Callback<ArrayList<IdNameVO>> callback;

        callback = new Callback<ArrayList<IdNameVO>>();
        service.getHeaderLabelsForScreen(manager, callback);
        return callback.getResult();
    }

    @Override
    public void getHeaderLabelsForScreen(WorksheetManager1 manager, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.getHeaderLabelsForScreen(manager, callback);
    }

    @Override
    public WorksheetQcChoiceVO loadTemplate(WorksheetManager1 wm, Integer testId) throws Exception {
        Callback<WorksheetQcChoiceVO> callback;

        callback = new Callback<WorksheetQcChoiceVO>();
        service.loadTemplate(wm, testId, callback);
        return callback.getResult();
    }

    @Override
    public void loadTemplate(WorksheetManager1 wm, Integer testId,
                             AsyncCallback<WorksheetQcChoiceVO> callback) {
        service.loadTemplate(wm, testId, callback);
    }

    @Override
    public WorksheetManager1 initializeResults(WorksheetManager1 wm,
                                               ArrayList<WorksheetAnalysisViewDO> analyses) throws Exception {
        Callback<WorksheetManager1> callback;

        callback = new Callback<WorksheetManager1>();
        service.initializeResults(wm, analyses, callback);
        return callback.getResult();
    }

    @Override
    public void initializeResults(WorksheetManager1 wm,
                                  ArrayList<WorksheetAnalysisViewDO> analyses,
                                  AsyncCallback<WorksheetManager1> callback) {
        service.initializeResults(wm, analyses, callback);
    }

    @Override
    public WorksheetManager1 initializeResultsFromOther(WorksheetManager1 wm,
                                                        ArrayList<WorksheetAnalysisViewDO> fromAnalyses,
                                                        ArrayList<WorksheetAnalysisViewDO> toAnalyses,
                                                        Integer fromWorksheetId) throws Exception {
        Callback<WorksheetManager1> callback;

        callback = new Callback<WorksheetManager1>();
        service.initializeResultsFromOther(wm, fromAnalyses, toAnalyses, fromWorksheetId, callback);
        return callback.getResult();
    }

    @Override
    public void initializeResultsFromOther(WorksheetManager1 wm,
                                           ArrayList<WorksheetAnalysisViewDO> fromAnalyses,
                                           ArrayList<WorksheetAnalysisViewDO> toAnalyses,
                                           Integer fromWorksheetId,
                                           AsyncCallback<WorksheetManager1> callback) {
        service.initializeResultsFromOther(wm, fromAnalyses, toAnalyses, fromWorksheetId, callback);
    }

    @Override
    public WorksheetManager1 sortItems(WorksheetManager1 wm, int col, int dir) throws Exception {
        Callback<WorksheetManager1> callback;

        callback = new Callback<WorksheetManager1>();
        service.sortItems(wm, col, dir, callback);
        return callback.getResult();
    }

    @Override
    public void sortItems(WorksheetManager1 wm, int col, int dir,
                          AsyncCallback<WorksheetManager1> callback) {
        service.sortItems(wm, col, dir, callback);
    }

    @Override
    public WorksheetManager1 exportToExcel(WorksheetManager1 wm) throws Exception {
        Callback<WorksheetManager1> callback;

        callback = new Callback<WorksheetManager1>();
        service.exportToExcel(wm, callback);
        return callback.getResult();
    }

    @Override
    public void exportToExcel(WorksheetManager1 wm, AsyncCallback<WorksheetManager1> callback) {
        service.exportToExcel(wm, callback);
    }

    @Override
    public WorksheetManager1 importFromExcel(WorksheetManager1 wm) throws Exception {
        Callback<WorksheetManager1> callback;

        callback = new Callback<WorksheetManager1>();
        service.importFromExcel(wm, callback);
        return callback.getResult();
    }

    @Override
    public void importFromExcel(WorksheetManager1 wm, AsyncCallback<WorksheetManager1> callback) {
        service.importFromExcel(wm, callback);
    }

    @Override
    public ReportStatus getExportToExcelStatus() throws Exception {
        Callback<ReportStatus> callback;

        callback = new Callback<ReportStatus>();
        service.getExportToExcelStatus(callback);
        return callback.getResult();
    }

    @Override
    public void getExportToExcelStatus(AsyncCallback<ReportStatus> callback) {
        service.getExportToExcelStatus(callback);
    }

    @Override
    public ReportStatus getImportFromExcelStatus() throws Exception {
        Callback<ReportStatus> callback;

        callback = new Callback<ReportStatus>();
        service.getImportFromExcelStatus(callback);
        return callback.getResult();
    }

    @Override
    public void getImportFromExcelStatus(AsyncCallback<ReportStatus> callback) {
        service.getImportFromExcelStatus(callback);
    }
}