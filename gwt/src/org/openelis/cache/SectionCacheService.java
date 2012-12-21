package org.openelis.cache;

import java.util.ArrayList;

import org.openelis.domain.SectionViewDO;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SectionCacheService implements SectionCacheServiceInt, SectionCacheServiceIntAsync {
    
    static SectionCacheService instance;
    
    SectionCacheServiceIntAsync service;
    
    public static SectionCacheService get() {
        if(instance == null)
            instance = new SectionCacheService();
        
        return instance;
    }
    
    private SectionCacheService() {
        service = (SectionCacheServiceIntAsync)GWT.create(SectionCacheServiceInt.class);
    }

    @Override
    public void getById(Integer id, AsyncCallback<SectionViewDO> callback) {
        service.getById(id, callback);
    }

    @Override
    public void getList(String name, AsyncCallback<ArrayList<SectionViewDO>> callback) {
        service.getList(name, callback);
    }

    @Override
    public SectionViewDO getById(Integer id) throws Exception {
        Callback<SectionViewDO> callback;
        
        callback = new Callback<SectionViewDO>();
        service.getById(id, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<SectionViewDO> getList(String name) throws Exception {
        Callback<ArrayList<SectionViewDO>> callback;
        
        callback = new Callback<ArrayList<SectionViewDO>>();
        service.getList(name, callback);
        return callback.getResult();
    }

}
