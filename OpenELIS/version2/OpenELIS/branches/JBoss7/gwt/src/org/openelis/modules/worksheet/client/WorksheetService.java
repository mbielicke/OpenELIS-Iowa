package org.openelis.modules.worksheet.client;

import java.util.ArrayList;

import org.openelis.domain.WorksheetViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.WorksheetAnalysisManager;
import org.openelis.manager.WorksheetItemManager;
import org.openelis.manager.WorksheetManager;
import org.openelis.manager.WorksheetQcResultManager;
import org.openelis.manager.WorksheetResultManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class WorksheetService implements WorkSheetServiceInt, WorkSheetServiceIntAsync {
    
    static WorksheetService instance;
    
    WorkSheetServiceIntAsync service;
    
    public static WorksheetService get() {
        if(instance == null)
            instance = new WorksheetService();
        
        return instance;
    }
    
    private WorksheetService() {
        service = (WorkSheetServiceIntAsync)GWT.create(WorkSheetServiceInt.class);
    }

    @Override
    public void abortUpdate(Integer id, AsyncCallback<WorksheetManager> callback) {
        service.abortUpdate(id, callback);
    }

    @Override
    public void add(WorksheetManager manager, AsyncCallback<WorksheetManager> callback) {
        service.add(manager, callback);
    }

    @Override
    public void fetchByAnalysisId(Integer id, AsyncCallback<ArrayList<WorksheetViewDO>> callback) {
        service.fetchByAnalysisId(id, callback);
    }

    @Override
    public void fetchById(Integer id, AsyncCallback<WorksheetManager> callback) {
        service.fetchById(id, callback);
    }

    @Override
    public void fetchForUpdate(Integer id, AsyncCallback<WorksheetManager> callback) {
        service.fetchForUpdate(id, callback);
    }

    @Override
    public void fetchWithAllData(Integer id, AsyncCallback<WorksheetManager> callback) {
        service.fetchWithAllData(id, callback);
    }

    @Override
    public void fetchWithItems(Integer id, AsyncCallback<WorksheetManager> callback) {
        service.fetchWithItems(id, callback);
    }

    @Override
    public void fetchWithItemsAndNotes(Integer id, AsyncCallback<WorksheetManager> callback) {
        service.fetchWithItemsAndNotes(id, callback);
    }

    @Override
    public void fetchWithNotes(Integer id, AsyncCallback<WorksheetManager> callback) {
        service.fetchWithNotes(id, callback);
    }

    @Override
    public void fetchWorksheeetQcResultByWorksheetAnalysisId(Integer id,
                                                             AsyncCallback<WorksheetQcResultManager> callback) {
        service.fetchWorksheeetQcResultByWorksheetAnalysisId(id, callback);
    }

    @Override
    public void fetchWorksheeetResultByWorksheetAnalysisId(Integer id,
                                                           AsyncCallback<WorksheetResultManager> callback) {
        service.fetchWorksheeetResultByWorksheetAnalysisId(id, callback);
    }

    @Override
    public void fetchWorksheetAnalysisByWorksheetItemId(Integer id,
                                                        AsyncCallback<WorksheetAnalysisManager> callback) {
        service.fetchWorksheetAnalysisByWorksheetItemId(id, callback);
    }

    @Override
    public void fetchWorksheetItemByWorksheetId(Integer id,
                                                AsyncCallback<WorksheetItemManager> callback) {
        service.fetchWorksheetItemByWorksheetId(id, callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<WorksheetViewDO>> callback) {
        service.query(query, callback);
    }

    @Override
    public void update(WorksheetManager manager, AsyncCallback<WorksheetManager> callback) {
        service.update(manager, callback);
    }

    @Override
    public ArrayList<WorksheetViewDO> query(Query query) throws Exception {
        Callback<ArrayList<WorksheetViewDO>> callback;
        
        callback = new Callback<ArrayList<WorksheetViewDO>>();
        service.query(query, callback);
        return callback.getResult();
    }

    @Override
    public WorksheetManager fetchById(Integer id) throws Exception {
        Callback<WorksheetManager> callback;
        
        callback = new Callback<WorksheetManager>();
        service.fetchById(id, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<WorksheetViewDO> fetchByAnalysisId(Integer id) throws Exception {
        Callback<ArrayList<WorksheetViewDO>> callback;
        
        callback = new Callback<ArrayList<WorksheetViewDO>>();
        service.fetchByAnalysisId(id, callback);
        return callback.getResult();
    }

    @Override
    public WorksheetManager fetchWithItems(Integer id) throws Exception {
        Callback<WorksheetManager> callback;
        
        callback = new Callback<WorksheetManager>();
        service.fetchWithItems(id, callback);
        return callback.getResult();
    }

    @Override
    public WorksheetManager fetchWithNotes(Integer id) throws Exception {
        Callback<WorksheetManager> callback;
        
        callback = new Callback<WorksheetManager>();
        service.fetchWithNotes(id, callback);
        return callback.getResult();
    }

    @Override
    public WorksheetManager fetchWithItemsAndNotes(Integer id) throws Exception {
        Callback<WorksheetManager> callback;
        
        callback = new Callback<WorksheetManager>();
        service.fetchWithItemsAndNotes(id, callback);
        return callback.getResult();
    }

    @Override
    public WorksheetManager fetchWithAllData(Integer id) throws Exception {
        Callback<WorksheetManager> callback;
        
        callback = new Callback<WorksheetManager>();
        service.fetchWithAllData(id, callback);
        return callback.getResult();
    }

    @Override
    public WorksheetManager add(WorksheetManager manager) throws Exception {
        Callback<WorksheetManager> callback;
        
        callback = new Callback<WorksheetManager>();
        service.add(manager, callback);
        return callback.getResult();
    }

    @Override
    public WorksheetManager update(WorksheetManager manager) throws Exception {
        Callback<WorksheetManager> callback;
        
        callback = new Callback<WorksheetManager>();
        service.update(manager, callback);
        return callback.getResult();
    }

    @Override
    public WorksheetManager fetchForUpdate(Integer id) throws Exception {
        Callback<WorksheetManager> callback;
        
        callback = new Callback<WorksheetManager>();
        service.fetchForUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public WorksheetManager abortUpdate(Integer id) throws Exception {
        Callback<WorksheetManager> callback;
        
        callback = new Callback<WorksheetManager>();
        service.abortUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public WorksheetItemManager fetchWorksheetItemByWorksheetId(Integer id) throws Exception {
        Callback<WorksheetItemManager> callback;
        
        callback = new Callback<WorksheetItemManager>();
        service.fetchWorksheetItemByWorksheetId(id, callback);
        return callback.getResult();
    }

    @Override
    public WorksheetAnalysisManager fetchWorksheetAnalysisByWorksheetItemId(Integer id) throws Exception {
        Callback<WorksheetAnalysisManager> callback;
        
        callback = new Callback<WorksheetAnalysisManager>();
        service.fetchWorksheetAnalysisByWorksheetItemId(id, callback);
        return callback.getResult();
    }

    @Override
    public WorksheetResultManager fetchWorksheeetResultByWorksheetAnalysisId(Integer id) throws Exception {
        Callback<WorksheetResultManager> callback;
        
        callback = new Callback<WorksheetResultManager>();
        service.fetchWorksheeetResultByWorksheetAnalysisId(id, callback);
        return callback.getResult();
    }

    @Override
    public WorksheetQcResultManager fetchWorksheeetQcResultByWorksheetAnalysisId(Integer id) throws Exception {
        Callback<WorksheetQcResultManager> callback;
        
        callback = new Callback<WorksheetQcResultManager>();
        service.fetchWorksheeetQcResultByWorksheetAnalysisId(id, callback);
        return callback.getResult();
    }

}
