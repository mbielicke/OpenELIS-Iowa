package org.openelis.modules.label.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.LabelDO;
import org.openelis.domain.LabelViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class LabelService implements LabelServiceInt, LabelServiceIntAsync{
    
    static LabelService instance;
    
    LabelServiceIntAsync service;
    
    public static LabelService get() {
        if(instance == null)
            instance = new LabelService();
        
        return instance;
    }
    
    private LabelService() {
        service = (LabelServiceIntAsync)GWT.create(LabelServiceInt.class);
    }

    @Override
    public void abortUpdate(Integer id, AsyncCallback<LabelViewDO> callback) {
        service.abortUpdate(id, callback);
    }

    @Override
    public void add(LabelViewDO data, AsyncCallback<LabelViewDO> callback) {
        service.add(data, callback);
    }

    @Override
    public void delete(LabelViewDO data, AsyncCallback<Void> callback) {
        service.delete(data, callback);
    }

    @Override
    public void fetchById(Integer id, AsyncCallback<LabelViewDO> callback) {
        service.fetchById(id, callback);
    }

    @Override
    public void fetchByName(String search, AsyncCallback<ArrayList<LabelDO>> callback) {
        service.fetchByName(search, callback);
    }

    @Override
    public void fetchForUpdate(Integer id, AsyncCallback<LabelViewDO> callback) {
        service.fetchForUpdate(id, callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public void update(LabelViewDO data, AsyncCallback<LabelViewDO> callback) {
        service.update(data, callback);
    }

    @Override
    public LabelViewDO fetchById(Integer id) throws Exception {
        Callback<LabelViewDO> callback;
        
        callback = new Callback<LabelViewDO>();
        service.fetchById(id, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<LabelDO> fetchByName(String search) throws Exception {
        Callback<ArrayList<LabelDO>> callback;
        
        callback = new Callback<ArrayList<LabelDO>>();
        service.fetchByName(search, callback);
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
    public LabelViewDO add(LabelViewDO data) throws Exception {
        Callback<LabelViewDO> callback;
        
        callback = new Callback<LabelViewDO>();
        service.add(data, callback);
        return callback.getResult();
    }

    @Override
    public LabelViewDO update(LabelViewDO data) throws Exception {
        Callback<LabelViewDO> callback;
        
        callback = new Callback<LabelViewDO>();
        service.update(data, callback);
        return callback.getResult();
    }

    @Override
    public LabelViewDO fetchForUpdate(Integer id) throws Exception {
        Callback<LabelViewDO> callback;
        
        callback = new Callback<LabelViewDO>();
        service.fetchForUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public void delete(LabelViewDO data) throws Exception {
        Callback<Void> callback;
        
        callback = new Callback<Void>();
        service.delete(data, callback);
    }

    @Override
    public LabelViewDO abortUpdate(Integer id) throws Exception {
        Callback<LabelViewDO> callback;
        
        callback = new Callback<LabelViewDO>();
        service.abortUpdate(id, callback);
        return callback.getResult();
    }
    
}
