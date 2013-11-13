package org.openelis.modules.scriptlet.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.ScriptletDO;
import org.openelis.gwt.screen.Callback;
import org.openelis.scriptlet.ScriptletObject;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.services.TokenService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

public class ScriptletService implements ScriptletServiceInt, ScriptletServiceIntAsync {
    
    static ScriptletService instance;
    
    ScriptletServiceIntAsync service;
    
    public static ScriptletService get() {
        if(instance == null)
            instance = new ScriptletService();
        
        return instance;
    }
    
    private ScriptletService() {
        service = (ScriptletServiceIntAsync)GWT.create(ScriptletServiceInt.class);
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }

    @Override
    public void fetchByName(String search, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.fetchByName(search, callback);
    }

    @Override
    public ArrayList<IdNameVO> fetchByName(String search) throws Exception {
        Callback<ArrayList<IdNameVO>> callback;
        
        callback = new Callback<ArrayList<IdNameVO>>();
        service.fetchByName(search, callback);
        return callback.getResult();
    }

    @Override
    public void run(ScriptletObject so, AsyncCallback<ScriptletObject> callback) {
        service.run(so,callback);
    }

    @Override
    public ScriptletObject run(ScriptletObject so) throws Exception {
        Callback<ScriptletObject> callback;
        
        callback = new Callback<ScriptletObject>();
        service.run(so, callback);
        return callback.getResult();
    }

    @Override
    public void fetchById(Integer id, AsyncCallback<ScriptletDO> callback) {
        service.fetchById(id, callback);
    }

    @Override
    public ScriptletDO fetchById(Integer id) throws Exception {
        Callback<ScriptletDO> callback;
        
        callback = new Callback<ScriptletDO>();
        service.fetchById(id, callback);
        return callback.getResult();
    }

    @Override
    public void fetchForUpdate(Integer id, AsyncCallback<ScriptletDO> callback) {
        service.fetchForUpdate(id, callback);
    }

    @Override
    public ScriptletDO fetchForUpdate(Integer id) throws Exception {
        Callback<ScriptletDO> callback;
        
        callback = new Callback<ScriptletDO>();
        service.fetchForUpdate(id,callback);
        return callback.getResult();
    }

    @Override
    public void add(ScriptletDO data, AsyncCallback<ScriptletDO> callback) {
        service.add(data, callback);
    }

    @Override
    public ScriptletDO add(ScriptletDO data) throws Exception {
        Callback<ScriptletDO> callback;
        
        callback = new Callback<ScriptletDO>();
        service.add(data, callback);
        return callback.getResult();
    }

    @Override
    public void update(ScriptletDO data, AsyncCallback<ScriptletDO> callback) {
        service.update(data, callback);
    }

    @Override
    public ScriptletDO update(ScriptletDO data) throws Exception {
        Callback<ScriptletDO> callback;
        
        callback = new Callback<ScriptletDO>();
        service.update(data, callback);
        return callback.getResult();
    }

    @Override
    public void abortUpdate(Integer id, AsyncCallback<ScriptletDO> callback) {
        service.abortUpdate(id, callback);
    }

    @Override
    public ScriptletDO abortUpdate(Integer id) throws Exception {
        Callback<ScriptletDO> callback;
        
        callback = new Callback<ScriptletDO>();
        service.abortUpdate(id, callback);
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

    
}
