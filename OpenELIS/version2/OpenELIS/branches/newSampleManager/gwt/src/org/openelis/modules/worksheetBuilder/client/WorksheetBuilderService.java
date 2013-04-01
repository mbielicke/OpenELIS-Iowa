package org.openelis.modules.worksheetBuilder.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.WorksheetBuilderVO;
import org.openelis.ui.common.data.Query;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

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
    public void lookupAnalyses(Query query, AsyncCallback<ArrayList<WorksheetBuilderVO>> callback) {
        service.lookupAnalyses(query, callback);
    }

    @Override
    public ArrayList<WorksheetBuilderVO> lookupAnalyses(Query query) throws Exception {
        Callback<ArrayList<WorksheetBuilderVO>> callback;
        
        callback = new Callback<ArrayList<WorksheetBuilderVO>>();
        service.lookupAnalyses(query, callback);
        return callback.getResult();
    }
}
