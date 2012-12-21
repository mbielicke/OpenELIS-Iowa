package org.openelis.modules.note.client;

import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.NoteManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class NoteService implements NoteServiceInt, NoteServiceIntAsync {
    
    static NoteService instance;
    
    NoteServiceIntAsync service;
    
    public static NoteService get() {
        if(instance == null)
            instance = new NoteService();
        
        return instance;
    }
    
    private NoteService() {
        service = (NoteServiceIntAsync)GWT.create(NoteServiceInt.class);
    }

    @Override
    public void fetchByRefTableRefIdIsExt(Query query, AsyncCallback<NoteManager> callback) {
        service.fetchByRefTableRefIdIsExt(query, callback);
    }

    @Override
    public NoteManager fetchByRefTableRefIdIsExt(Query query) throws Exception {
        Callback<NoteManager> callback;
        
        callback = new Callback<NoteManager>();
        service.fetchByRefTableRefIdIsExt(query, callback);
        return callback.getResult();
    }

}
