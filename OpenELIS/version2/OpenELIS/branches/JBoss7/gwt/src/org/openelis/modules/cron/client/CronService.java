package org.openelis.modules.cron.client;

import java.util.ArrayList;

import org.openelis.domain.CronDO;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CronService implements CronServiceInt, CronServiceIntAsync {
    
    static CronService instance;
    
    CronServiceIntAsync service;
    
    public static CronService get() {
        if (instance == null)
            instance = new CronService();
        
        return instance;
    }
    
    private CronService() {
        service = (CronServiceIntAsync)GWT.create(CronServiceInt.class);
    }

    @Override
    public void abortUpdate(Integer id, AsyncCallback<CronDO> callback) {
        service.abortUpdate(id, callback);
    }

    @Override
    public void add(CronDO data, AsyncCallback<CronDO> callback) {
        service.add(data, callback);
    }

    @Override
    public void delete(CronDO data, AsyncCallback<Void> callback) {
        service.delete(data, callback);
    }

    @Override
    public void fetchById(Integer id, AsyncCallback<CronDO> callback) {
        service.fetchById(id, callback);
    }

    @Override
    public void fetchForUpdate(Integer id, AsyncCallback<CronDO> callback) {
        service.fetchForUpdate(id, callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public void update(CronDO data, AsyncCallback<CronDO> callback) {
        service.update(data, callback);
    }

    @Override
    public CronDO fetchById(Integer id) throws Exception {
        Callback<CronDO> callback;
        
        callback = new Callback<CronDO>();
        service.fetchById(id, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<IdNameVO> query(Query query) throws Exception {
        Callback<ArrayList<IdNameVO>> callback;
        
        callback = new Callback<ArrayList<IdNameVO>>();
        service.query(query, callback);
        return callback.getResult();    }

    @Override
    public CronDO add(CronDO data) throws Exception {
        Callback<CronDO> callback;
        
        callback = new Callback<CronDO>();
        service.add(data, callback);
        return callback.getResult();
    }

    @Override
    public CronDO update(CronDO data) throws Exception {
        Callback<CronDO> callback;
        
        callback = new Callback<CronDO>();
        service.update(data, callback);
        return callback.getResult();
    }

    @Override
    public CronDO fetchForUpdate(Integer id) throws Exception {
        Callback<CronDO> callback;
        
        callback = new Callback<CronDO>();
        service.fetchForUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public void delete(CronDO data) throws Exception {
        Callback<Void> callback;
        
        callback = new Callback<Void>();
        service.delete(data, callback);
    }

    @Override
    public CronDO abortUpdate(Integer id) throws Exception {
        Callback<CronDO> callback;
        
        callback = new Callback<CronDO>();
        service.abortUpdate(id, callback);
        return callback.getResult();
    }
    
    
    

}
