package org.openelis.modules.worksheetCreation.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.WorksheetCreationVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class WorksheetCreationService implements WorksheetCreationServiceInt,
                                                 WorksheetCreationServiceIntAsync {
    
    static WorksheetCreationService instance;
    
    WorksheetCreationServiceIntAsync service;
    
    public static WorksheetCreationService get() {
        if(instance == null)
            instance = new WorksheetCreationService();
        
        return instance;
    }
    
    private WorksheetCreationService() {
        service = (WorksheetCreationServiceIntAsync)GWT.create(WorksheetCreationServiceInt.class);
    }

    @Override
    public void getColumnNames(Integer formatId, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.getColumnNames(formatId, callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<WorksheetCreationVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public ArrayList<WorksheetCreationVO> query(Query query) throws Exception {
        Callback<ArrayList<WorksheetCreationVO>> callback;
        
        callback = new Callback<ArrayList<WorksheetCreationVO>>();
        service.query(query, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<IdNameVO> getColumnNames(Integer formatId) throws Exception {
        Callback<ArrayList<IdNameVO>> callback;
        
        callback = new Callback<ArrayList<IdNameVO>>();
        service.getColumnNames(formatId, callback);
        return callback.getResult();
    }

}
