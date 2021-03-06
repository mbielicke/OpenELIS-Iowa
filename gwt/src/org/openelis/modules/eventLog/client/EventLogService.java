package org.openelis.modules.eventLog.client;

import java.util.ArrayList;

import org.openelis.domain.EventLogDO;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.services.TokenService;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

public class EventLogService implements EventLogServiceInt, EventLogServiceIntAsync {

    static EventLogService instance;
    
    EventLogServiceIntAsync service;
    
    public static EventLogService get() {
        if (instance == null)
            instance = new EventLogService();
        
        return instance;
    }
    
    private EventLogService() {
        service = (EventLogServiceIntAsync)GWT.create(EventLogServiceInt.class);
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<EventLogDO>> callback) {
        service.query(query, callback);
    }

    @Override
    public ArrayList<EventLogDO> query(Query query) throws Exception {
        Callback<ArrayList<EventLogDO>> callback;
        
        callback = new Callback<ArrayList<EventLogDO>>();
        service.query(query, callback);
        return callback.getResult();
    }

    @Override
    public void add(Integer typeId, String source, Integer referenceTableId, Integer referenceId,
                    Integer levelId, String text, AsyncCallback<EventLogDO> callback) {
        service.add(typeId, source, referenceTableId, referenceId, levelId, text, callback);
    }

    @Override
    public EventLogDO add(Integer typeId, String source, Integer referenceTableId,
                          Integer referenceId, Integer levelId, String text) throws Exception {
        Callback<EventLogDO> callback;
        
        callback = new Callback<EventLogDO>();
        service.add(typeId, source, referenceTableId, referenceId, levelId, text, callback);
        return callback.getResult();
    }
}