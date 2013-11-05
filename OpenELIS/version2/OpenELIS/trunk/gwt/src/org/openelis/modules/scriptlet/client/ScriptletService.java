package org.openelis.modules.scriptlet.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.gwt.screen.Callback;
import org.openelis.scriptlet.ScriptletObject;
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

    
}
