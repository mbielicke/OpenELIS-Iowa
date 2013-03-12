package org.openelis.modules.history.client;

import java.util.ArrayList;

import org.openelis.domain.HistoryVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class HistoryService implements HistoryServiceInt, HistoryServiceIntAsync {
    
    static HistoryService instance;
    
    HistoryServiceIntAsync service;
    
    public static HistoryService get() {
        if (instance == null)
            instance = new HistoryService();
        
        return instance;
    }
    
    private HistoryService() {
        service = (HistoryServiceIntAsync)GWT.create(HistoryServiceInt.class);
    }

    @Override
    public void fetchByReferenceIdAndTable(Query query, AsyncCallback<ArrayList<HistoryVO>> callback) {
        service.fetchByReferenceIdAndTable(query, callback);
    }

    @Override
    public ArrayList<HistoryVO> fetchByReferenceIdAndTable(Query query) throws Exception {
        Callback<ArrayList<HistoryVO>> callback;
        
        callback = new Callback<ArrayList<HistoryVO>>();
        service.fetchByReferenceIdAndTable(query, callback);
        return callback.getResult();
    }
    
    

}
