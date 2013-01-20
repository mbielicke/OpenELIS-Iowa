package org.openelis.modules.standardnote.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.StandardNoteDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class StandardNoteService implements StandardNoteServiceInt, StandardNoteServiceIntAsync {
    
    static StandardNoteService instance;
    
    StandardNoteServiceIntAsync service;
    
    public static StandardNoteService get() {
        if(instance == null)
            instance = new StandardNoteService();
        
        return instance;
    }
    
    private StandardNoteService() {
        service = (StandardNoteServiceIntAsync)GWT.create(StandardNoteServiceInt.class);
    }

    @Override
    public void abortUpdate(Integer id, AsyncCallback<StandardNoteDO> callback) {
        service.abortUpdate(id, callback);
    }

    @Override
    public void add(StandardNoteDO data, AsyncCallback<StandardNoteDO> callback) {
        service.add(data, callback);
    }

    @Override
    public void delete(StandardNoteDO data, AsyncCallback<Void> callback) {
        service.delete(data, callback);
    }

    @Override
    public void fetchById(Integer id, AsyncCallback<StandardNoteDO> callback) {
        service.fetchById(id, callback);
    }

    @Override
    public void fetchByNameOrDescription(Query query,
                                         AsyncCallback<ArrayList<StandardNoteDO>> callback) {
        service.fetchByNameOrDescription(query, callback);
    }

    @Override
    public void fetchBySystemVariableName(String name, AsyncCallback<StandardNoteDO> callback) {
        service.fetchBySystemVariableName(name, callback);
    }

    @Override
    public void fetchByType(Integer typeId, AsyncCallback<ArrayList<StandardNoteDO>> callback) {
        service.fetchByType(typeId, callback);
    }

    @Override
    public void fetchForUpdate(Integer id, AsyncCallback<StandardNoteDO> callback) {
        service.fetchForUpdate(id, callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public void update(StandardNoteDO data, AsyncCallback<StandardNoteDO> callback) {
        service.update(data, callback);
    }

    @Override
    public StandardNoteDO fetchById(Integer id) throws Exception {
        Callback<StandardNoteDO> callback;
        
        callback = new Callback<StandardNoteDO>();
        service.fetchById(id, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<StandardNoteDO> fetchByNameOrDescription(Query query) throws Exception {
        Callback<ArrayList<StandardNoteDO>> callback;
        
        callback = new Callback<ArrayList<StandardNoteDO>>();
        service.fetchByNameOrDescription(query, callback);
        return callback.getResult();
    }

    @Override
    public StandardNoteDO fetchBySystemVariableName(String name) throws Exception {
        Callback<StandardNoteDO> callback;
        
        callback = new Callback<StandardNoteDO>();
        service.fetchBySystemVariableName(name, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<StandardNoteDO> fetchByType(Integer typeId) throws Exception {
        Callback<ArrayList<StandardNoteDO>> callback;
        
        callback = new Callback<ArrayList<StandardNoteDO>>();
        service.fetchByType(typeId, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<IdNameVO> query(Query query) throws Exception {
        Callback<ArrayList<IdNameVO>> callback;
        
        callback = new Callback<ArrayList<IdNameVO>>();
        service.query(query, callback);
        return callback.getResult();
    }

    @Override
    public StandardNoteDO add(StandardNoteDO data) throws Exception {
        Callback<StandardNoteDO> callback;
        
        callback = new Callback<StandardNoteDO>();
        service.add(data, callback);
        return callback.getResult();
    }

    @Override
    public StandardNoteDO update(StandardNoteDO data) throws Exception {
        Callback<StandardNoteDO> callback;
        
        callback = new Callback<StandardNoteDO>();
        service.update(data, callback);
        return callback.getResult();
    }

    @Override
    public StandardNoteDO fetchForUpdate(Integer id) throws Exception {
        Callback<StandardNoteDO> callback;
        
        callback = new Callback<StandardNoteDO>();
        service.fetchForUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public void delete(StandardNoteDO data) throws Exception {
        Callback<Void> callback;
        
        callback = new Callback<Void>();
        service.delete(data, callback);
    }

    @Override
    public StandardNoteDO abortUpdate(Integer id) throws Exception {
        Callback<StandardNoteDO> callback;
        
        callback = new Callback<StandardNoteDO>();
        service.abortUpdate(id, callback);
        return callback.getResult();
    }

}
