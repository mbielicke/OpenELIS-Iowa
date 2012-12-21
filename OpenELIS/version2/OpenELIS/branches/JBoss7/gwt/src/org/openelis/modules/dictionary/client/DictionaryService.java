package org.openelis.modules.dictionary.client;

import java.util.ArrayList;

import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.CategoryManager;
import org.openelis.manager.DictionaryManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DictionaryService implements DictionaryServiceInt, DictionaryServiceIntAsync {

    static DictionaryService instance;
    
    DictionaryServiceIntAsync service;
    
    public static DictionaryService get() {
        if (instance == null)
            instance = new DictionaryService();
        
        return instance;
    }
    
    private DictionaryService() {
        service = (DictionaryServiceIntAsync)GWT.create(DictionaryServiceInt.class);
    }

    @Override
    public void abortUpdate(Integer id, AsyncCallback<CategoryManager> callback) {
        service.abortUpdate(id, callback);
    }

    @Override
    public void add(CategoryManager man, AsyncCallback<CategoryManager> callback) {
        service.add(man, callback);
    }

    @Override
    public void fetchByCategoryName(String name, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.fetchByCategoryName(name, callback);
    }

    @Override
    public void fetchByEntry(Query query, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.fetchByEntry(query, callback);
    }

    @Override
    public void fetchByEntry(String entry, AsyncCallback<ArrayList<DictionaryViewDO>> callback) {
        service.fetchByEntry(entry, callback);
    }

    @Override
    public void fetchById(Integer id, AsyncCallback<CategoryManager> callback) {
        service.fetchById(id, callback);
    }

    @Override
    public void fetchEntryByCategoryId(Integer id, AsyncCallback<DictionaryManager> callback) {
        service.fetchEntryByCategoryId(id, callback);
    }

    @Override
    public void fetchForUpdate(Integer id, AsyncCallback<CategoryManager> callback) {
        service.fetchForUpdate(id, callback);
    }

    @Override
    public void fetchWithEntries(Integer id, AsyncCallback<CategoryManager> callback) {
        service.fetchWithEntries(id, callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public void update(CategoryManager man, AsyncCallback<CategoryManager> callback) {
        service.update(man, callback);
    }

    @Override
    public void validateForDelete(DictionaryViewDO data, AsyncCallback<DictionaryViewDO> callback) {
        service.validateForDelete(data, callback);
    }

    @Override
    public CategoryManager fetchById(Integer id) throws Exception {
        Callback<CategoryManager> callback;
        
        callback = new Callback<CategoryManager>();
        service.fetchById(id, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<IdNameVO> fetchByEntry(Query query) throws Exception {
        Callback<ArrayList<IdNameVO>> callback;
        
        callback = new Callback<ArrayList<IdNameVO>>();
        service.fetchByEntry(query, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<IdNameVO> fetchByCategoryName(String name) throws Exception {
        Callback<ArrayList<IdNameVO>> callback;
        
        callback = new Callback<ArrayList<IdNameVO>>();
        service.fetchByCategoryName(name, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<DictionaryViewDO> fetchByEntry(String entry) throws Exception {
        Callback<ArrayList<DictionaryViewDO>> callback;
        
        callback = new Callback<ArrayList<DictionaryViewDO>>();
        service.fetchByEntry(entry, callback);
        return callback.getResult();
    }

    @Override
    public CategoryManager fetchWithEntries(Integer id) throws Exception {
        Callback<CategoryManager> callback;
        
        callback = new Callback<CategoryManager>();
        service.fetchWithEntries(id, callback);
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
    public CategoryManager add(CategoryManager man) throws Exception {
        Callback<CategoryManager> callback;
        
        callback = new Callback<CategoryManager>();
        service.add(man, callback);
        return callback.getResult();
    }

    @Override
    public CategoryManager update(CategoryManager man) throws Exception {
        Callback<CategoryManager> callback;
        
        callback = new Callback<CategoryManager>();
        service.update(man, callback);
        return callback.getResult();
    }

    @Override
    public CategoryManager fetchForUpdate(Integer id) throws Exception {
        Callback<CategoryManager> callback;
        
        callback = new Callback<CategoryManager>();
        service.fetchForUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public CategoryManager abortUpdate(Integer id) throws Exception {
        Callback<CategoryManager> callback;
        
        callback = new Callback<CategoryManager>();
        service.abortUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public DictionaryManager fetchEntryByCategoryId(Integer id) throws Exception {
        Callback<DictionaryManager> callback;
        
        callback = new Callback<DictionaryManager>();
        service.fetchEntryByCategoryId(id, callback);
        return callback.getResult();
    }

    @Override
    public DictionaryViewDO validateForDelete(DictionaryViewDO data) throws Exception {
        Callback<DictionaryViewDO> callback;
        
        callback = new Callback<DictionaryViewDO>();
        service.validateForDelete(data, callback);
        return callback.getResult();
    }
    
    
}
