package org.openelis.modules.testTrailer.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.TestTrailerDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class TestTrailerService implements TestTrailerServiceInt, TestTrailerServiceIntAsync {
    
    static TestTrailerService instance;
    
    TestTrailerServiceIntAsync service;
    
    public static TestTrailerService get() {
        if(instance == null)
            instance = new TestTrailerService();
        
        return instance;
    }
    
    private TestTrailerService() {
        service = (TestTrailerServiceIntAsync)GWT.create(TestTrailerServiceInt.class);
    }

    @Override
    public void abortUpdate(Integer id, AsyncCallback<TestTrailerDO> callback) {
        service.abortUpdate(id, callback);
    }

    @Override
    public void add(TestTrailerDO data, AsyncCallback<TestTrailerDO> callback) {
        service.add(data, callback);
    }

    @Override
    public void delete(TestTrailerDO data, AsyncCallback<Void> callback) {
        service.delete(data, callback);
    }

    @Override
    public void fetchById(Integer id, AsyncCallback<TestTrailerDO> callback) {
        service.fetchById(id, callback);
    }

    @Override
    public void fetchByName(String search, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.fetchByName(search, callback);
    }

    @Override
    public void fetchForUpdate(Integer id, AsyncCallback<TestTrailerDO> callback) {
        service.fetchForUpdate(id, callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public void update(TestTrailerDO data, AsyncCallback<TestTrailerDO> callback) {
        service.update(data, callback);
    }

    @Override
    public TestTrailerDO fetchById(Integer id) throws Exception {
        Callback<TestTrailerDO> callback;
        
        callback = new Callback<TestTrailerDO>();
        service.fetchById(id, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<IdNameVO> fetchByName(String search) throws Exception {
        Callback<ArrayList<IdNameVO>> callback;
        
        callback = new Callback<ArrayList<IdNameVO>>();
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
    public TestTrailerDO add(TestTrailerDO data) throws Exception {
        Callback<TestTrailerDO> callback;
        
        callback = new Callback<TestTrailerDO>();
        service.add(data, callback);
        return callback.getResult();
    }

    @Override
    public TestTrailerDO update(TestTrailerDO data) throws Exception {
        Callback<TestTrailerDO> callback;
        
        callback = new Callback<TestTrailerDO>();
        service.update(data, callback);
        return callback.getResult();
    }

    @Override
    public TestTrailerDO fetchForUpdate(Integer id) throws Exception {
        Callback<TestTrailerDO> callback;
        
        callback = new Callback<TestTrailerDO>();
        service.fetchForUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public void delete(TestTrailerDO data) throws Exception {
        Callback<Void> callback;
        
        callback = new Callback<Void>();
        service.delete(data, callback);
    }

    @Override
    public TestTrailerDO abortUpdate(Integer id) throws Exception {
        Callback<TestTrailerDO> callback;
        
        callback = new Callback<TestTrailerDO>();
        service.abortUpdate(id, callback);
        return callback.getResult();
    }

}
