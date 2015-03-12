package org.openelis.modules.history.client;

import java.util.ArrayList;

import org.openelis.domain.HistoryVO;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.services.TokenService;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

public enum HistoryService implements HistoryServiceInt, HistoryServiceIntAsync {
    
    INSTANCE;
    
    HistoryServiceIntAsync service;
    
    private HistoryService() {
        service = (HistoryServiceIntAsync)GWT.create(HistoryServiceInt.class);
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
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
