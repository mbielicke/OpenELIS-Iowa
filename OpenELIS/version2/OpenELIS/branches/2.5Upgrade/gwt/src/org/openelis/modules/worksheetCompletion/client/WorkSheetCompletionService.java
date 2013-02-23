package org.openelis.modules.worksheetCompletion.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.WorksheetManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class WorkSheetCompletionService implements WorksheetCompletionServiceInt,
                                                   WorksheetCompletionServiceIntAsync {
    
    static WorkSheetCompletionService instance;
    
    WorksheetCompletionServiceIntAsync service;
    
    public static WorkSheetCompletionService get() {
        if(instance == null)
            instance = new WorkSheetCompletionService();
        
        return instance;
    }
    
    private WorkSheetCompletionService() {
        service = (WorksheetCompletionServiceIntAsync)GWT.create(WorksheetCompletionServiceInt.class);
    }

    @Override
    public void getHeaderLabelsForScreen(WorksheetManager manager,
                                         AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.getHeaderLabelsForScreen(manager, callback);
    }

    @Override
    public void getUpdateStatus(AsyncCallback<ReportStatus> callback) {
        service.getUpdateStatus(callback);
    }

    @Override
    public void loadFromEdit(WorksheetManager manager, AsyncCallback<WorksheetManager> callback) {
        service.loadFromEdit(manager, callback);
    }

    @Override
    public void saveForEdit(WorksheetManager manager, AsyncCallback<WorksheetManager> callback) {
        service.saveForEdit(manager, callback);
    }

    @Override
    public WorksheetManager saveForEdit(WorksheetManager manager) throws Exception {
        Callback<WorksheetManager> callback;
        
        callback = new Callback<WorksheetManager>();
        service.saveForEdit(manager, callback);
        return callback.getResult();
    }

    @Override
    public WorksheetManager loadFromEdit(WorksheetManager manager) throws Exception {
        Callback<WorksheetManager> callback;
        
        callback = new Callback<WorksheetManager>();
        service.loadFromEdit(manager, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<IdNameVO> getHeaderLabelsForScreen(WorksheetManager manager) throws Exception {
        Callback<ArrayList<IdNameVO>> callback;
        
        callback = new Callback<ArrayList<IdNameVO>>();
        service.getHeaderLabelsForScreen(manager, callback);
        return callback.getResult();
    }

    @Override
    public ReportStatus getUpdateStatus() {
        Callback<ReportStatus> callback;
        
        callback = new Callback<ReportStatus>();
        service.getUpdateStatus(callback);
        try {
            return callback.getResult();
        }catch(Exception e) {
            return null;
        }
    }

}
