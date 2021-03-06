package org.openelis.cache;

import java.util.ArrayList;

import org.openelis.domain.DictionaryDO;
import org.openelis.gwt.screen.Callback;
import org.openelis.ui.services.TokenService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

public class DictionaryCacheService implements DictionaryCacheServiceInt,
                                   DictionaryCacheServiceIntAsync {
    
    static DictionaryCacheService instance;
    
    DictionaryCacheServiceIntAsync service;
    
    public static DictionaryCacheService get() {
        if(instance == null)
            instance = new DictionaryCacheService();
        
        return instance;
    }
    
    private DictionaryCacheService() {
        service = (DictionaryCacheServiceIntAsync)GWT.create(DictionaryCacheServiceInt.class);
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }

    @Override
    public void getById(Integer id, AsyncCallback<DictionaryDO> callback) {
        service.getById(id, callback);
    }

    @Override
    public void getBySystemName(String systemName, AsyncCallback<DictionaryDO> callback) {
        service.getBySystemName(systemName, callback);
    }

    @Override
    public DictionaryDO getBySystemName(String systemName) throws Exception {
        Callback<DictionaryDO> callback;
        
        callback = new Callback<DictionaryDO>();
        service.getBySystemName(systemName, callback);
        return callback.getResult();
    }

    @Override
    public DictionaryDO getById(Integer id) throws Exception {
        Callback<DictionaryDO> callback;
        
        callback = new Callback<DictionaryDO>();
        service.getById(id, callback);
        return callback.getResult();
    }
    
    @Override
    public void getByIds(ArrayList<Integer> ids, AsyncCallback<ArrayList<DictionaryDO>> callback) {
        service.getByIds(ids, callback);
    }

    @Override
    public ArrayList<DictionaryDO> getByIds(ArrayList<Integer> ids) throws Exception {
        Callback<ArrayList<DictionaryDO>> callback;
        
        callback = new Callback<ArrayList<DictionaryDO>>();
        service.getByIds(ids, callback);
        return callback.getResult();
    }
}
