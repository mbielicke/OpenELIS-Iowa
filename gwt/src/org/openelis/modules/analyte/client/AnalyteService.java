package org.openelis.modules.analyte.client;

import java.util.ArrayList;

import org.openelis.domain.AnalyteDO;
import org.openelis.domain.AnalyteViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AnalyteService implements AnalyteServiceInt, AnalyteServiceIntAsync {

    static AnalyteService instance;
    
    AnalyteServiceIntAsync service;
    
    public static AnalyteService get() {
        if (instance == null)
            instance = new AnalyteService();
        
        return instance;
    }
    
    private AnalyteService() {
        service = (AnalyteServiceIntAsync)GWT.create(AnalyteServiceInt.class);
    }

    @Override
    public void abortUpdate(Integer id, AsyncCallback<AnalyteViewDO> callback) {
        service.abortUpdate(id, callback);
    }

    @Override
    public void add(AnalyteViewDO data, AsyncCallback<AnalyteDO> callback) {
        service.add(data, callback);        
    }

    @Override
    public void delete(AnalyteDO data, AsyncCallback<Void> callback) {
        service.delete(data, callback);
    }

    @Override
    public void fetchById(Integer id, AsyncCallback<AnalyteViewDO> callback) {
        service.fetchById(id, callback);
    }

    @Override
    public void fetchByName(String search, AsyncCallback<ArrayList<AnalyteDO>> callback) {
        service.fetchByName(search, callback);
    }

    @Override
    public void fetchForUpdate(Integer id, AsyncCallback<AnalyteViewDO> callback) {
        service.fetchForUpdate(id, callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public void update(AnalyteViewDO data, AsyncCallback<AnalyteDO> callback) {
        service.update(data, callback);
    }

    @Override
    public AnalyteViewDO fetchById(Integer id) throws Exception {
        Callback<AnalyteViewDO> callback;
        
        callback = new Callback<AnalyteViewDO>();
        service.fetchById(id, callback);
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
    public AnalyteDO add(AnalyteViewDO data) throws Exception {
        Callback<AnalyteDO> callback;
        
        callback = new Callback<AnalyteDO>();
        service.add(data, callback);
        return callback.getResult();
    }

    @Override
    public AnalyteDO update(AnalyteViewDO data) throws Exception {
        Callback<AnalyteDO> callback;
        
        callback = new Callback<AnalyteDO>();
        service.update(data, callback);
        return callback.getResult();
    }

    @Override
    public AnalyteViewDO fetchForUpdate(Integer id) throws Exception {
        Callback<AnalyteViewDO> callback;
        
        callback = new Callback<AnalyteViewDO>();
        service.fetchForUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public void delete(AnalyteDO data) throws Exception {
        Callback<Void> callback;
        
        callback = new Callback<Void>();
        service.delete(data,callback);
    }

    @Override
    public AnalyteViewDO abortUpdate(Integer id) throws Exception {
        Callback<AnalyteViewDO> callback;
        
        callback = new Callback<AnalyteViewDO>();
        service.abortUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<AnalyteDO> fetchByName(String search) throws Exception {
        Callback<ArrayList<AnalyteDO>> callback;
        
        callback = new Callback<ArrayList<AnalyteDO>>();
        service.fetchByName(search, callback);
        return callback.getResult();
    }
}
