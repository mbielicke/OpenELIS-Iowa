package org.openelis.modules.instrument.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.InstrumentViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.InstrumentLogManager;
import org.openelis.manager.InstrumentManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class InstrumentService implements InstrumentServiceInt, InstrumentServiceIntAsync {
    
    static InstrumentService instance;
    
    InstrumentServiceIntAsync service;
    
    public static InstrumentService get() {
        if (instance == null)
            instance = new InstrumentService();
        
        return instance;
    }
    
    private InstrumentService() {
        service = (InstrumentServiceIntAsync)GWT.create(InstrumentServiceInt.class);
    }

    @Override
    public void abortUpdate(Integer id, AsyncCallback<InstrumentManager> callback) {
        service.abortUpdate(id, callback);
    }

    @Override
    public void add(InstrumentManager man, AsyncCallback<InstrumentManager> callback) {
        service.add(man, callback);
    }

    @Override
    public void fetchActiveByName(String name, AsyncCallback<ArrayList<InstrumentViewDO>> callback) {
        service.fetchActiveByName(name, callback);
    }

    @Override
    public void fetchById(Integer id, AsyncCallback<InstrumentManager> callback) {
        service.fetchById(id, callback);
    }

    @Override
    public void fetchByName(String name, AsyncCallback<ArrayList<InstrumentViewDO>> callback) {
        service.fetchByName(name, callback);
    }

    @Override
    public void fetchForUpdate(Integer id, AsyncCallback<InstrumentManager> callback) {
        service.fetchForUpdate(id, callback);
    }

    @Override
    public void fetchLogByInstrumentId(Integer id, AsyncCallback<InstrumentLogManager> callback) {
        service.fetchLogByInstrumentId(id, callback);
    }

    @Override
    public void fetchWithLogs(Integer id, AsyncCallback<InstrumentManager> callback) {
        service.fetchWithLogs(id, callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public void update(InstrumentManager man, AsyncCallback<InstrumentManager> callback) {
        service.update(man, callback);
    }

    @Override
    public InstrumentManager fetchById(Integer id) throws Exception {
        Callback<InstrumentManager> callback;
        
        callback = new Callback<InstrumentManager>();
        service.fetchById(id, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<InstrumentViewDO> fetchByName(String name) throws Exception {
        Callback<ArrayList<InstrumentViewDO>> callback;
        
        callback = new Callback<ArrayList<InstrumentViewDO>>();
        service.fetchByName(name, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<InstrumentViewDO> fetchActiveByName(String name) throws Exception {
        Callback<ArrayList<InstrumentViewDO>> callback;
        
        callback = new Callback<ArrayList<InstrumentViewDO>>();
        service.fetchActiveByName(name, callback);
        return callback.getResult();
    }

    @Override
    public InstrumentManager fetchWithLogs(Integer id) throws Exception {
        Callback<InstrumentManager> callback;
        
        callback = new Callback<InstrumentManager>();
        service.fetchWithLogs(id, callback);
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
    public InstrumentManager add(InstrumentManager man) throws Exception {
        Callback<InstrumentManager> callback;
        
        callback = new Callback<InstrumentManager>();
        service.add(man, callback);
        return callback.getResult();
    }

    @Override
    public InstrumentManager update(InstrumentManager man) throws Exception {
        Callback<InstrumentManager> callback;
        
        callback = new Callback<InstrumentManager>();
        service.update(man, callback);
        return callback.getResult();  
    }

    @Override
    public InstrumentManager fetchForUpdate(Integer id) throws Exception {
        Callback<InstrumentManager> callback;
        
        callback = new Callback<InstrumentManager>();
        service.fetchForUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public InstrumentManager abortUpdate(Integer id) throws Exception {
        Callback<InstrumentManager> callback;
        
        callback = new Callback<InstrumentManager>();
        service.abortUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public InstrumentLogManager fetchLogByInstrumentId(Integer id) throws Exception {
        Callback<InstrumentLogManager> callback;
        
        callback = new Callback<InstrumentLogManager>();
        service.fetchLogByInstrumentId(id, callback);
        return callback.getResult();
    }
    
    

}
