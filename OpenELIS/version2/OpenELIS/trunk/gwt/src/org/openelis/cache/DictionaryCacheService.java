package org.openelis.cache;

import org.openelis.domain.DictionaryDO;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

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

}
