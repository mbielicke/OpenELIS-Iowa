package org.openelis.modules.scriptlet.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

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

    
}
