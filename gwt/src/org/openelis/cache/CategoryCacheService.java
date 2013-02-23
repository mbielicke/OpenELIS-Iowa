package org.openelis.cache;

import java.util.ArrayList;

import org.openelis.domain.CategoryCacheVO;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CategoryCacheService implements CategoryCacheServiceInt, CategoryCacheServiceIntAsync {
    
    static CategoryCacheService instance;
    
    CategoryCacheServiceIntAsync service;
    
    public static CategoryCacheService get() {
        if(instance == null)
            instance = new CategoryCacheService();
        
        return instance;
    }
    
    private CategoryCacheService() {
        service = (CategoryCacheServiceIntAsync)GWT.create(CategoryCacheServiceInt.class);
    }

    @Override
    public void getBySystemName(String systemName, AsyncCallback<CategoryCacheVO> callback) {
        service.getBySystemName(systemName, callback);
    }

    @Override
    public void getBySystemNames(String[] systemNames,
                                 AsyncCallback<ArrayList<CategoryCacheVO>> callback) {
        service.getBySystemNames(systemNames, callback);
    }

    @Override
    public CategoryCacheVO getBySystemName(String systemName) throws Exception {
        Callback<CategoryCacheVO> callback;
        
        callback = new Callback<CategoryCacheVO>();
        service.getBySystemName(systemName, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<CategoryCacheVO> getBySystemNames(String... systemNames) throws Exception {
        Callback<ArrayList<CategoryCacheVO>> callback;
        
        callback = new Callback<ArrayList<CategoryCacheVO>>();
        service.getBySystemNames(systemNames, callback);
        return callback.getResult();
    }

}
