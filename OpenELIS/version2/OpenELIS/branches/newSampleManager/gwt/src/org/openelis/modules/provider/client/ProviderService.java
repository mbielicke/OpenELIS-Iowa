package org.openelis.modules.provider.client;

import java.util.ArrayList;

import org.openelis.domain.IdFirstLastNameVO;
import org.openelis.domain.ProviderDO;
import org.openelis.ui.common.data.Query;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.ProviderLocationManager;
import org.openelis.manager.ProviderManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ProviderService implements ProviderServiceInt, ProviderServiceIntAsync {
    
    static ProviderService instance;
    
    ProviderServiceIntAsync service;
    
    public static ProviderService get() {
        if(instance == null)
            instance = new ProviderService();
        
        return instance;
    }
    
    private ProviderService() {
        service = (ProviderServiceIntAsync)GWT.create(ProviderServiceInt.class);
    }
    
    @Override
    public ProviderManager fetchById(Integer id) throws Exception {
        Callback<ProviderManager> callback;
        
        callback = new Callback<ProviderManager>();
        service.fetchById(id, callback);
        return callback.getResult();
    }

    @Override
    public void fetchById(Integer id, AsyncCallback<ProviderManager> callback) {
        service.fetchById(id, callback);    
    }

    @Override
    public void fetchByLastName(String lastName, AsyncCallback<ArrayList<ProviderDO>> callback) {
        service.fetchByLastName(lastName, callback);    
    }

    @Override
    public ArrayList<ProviderDO> fetchByLastName(String lastName) throws Exception {
        Callback<ArrayList<ProviderDO>> callback;
        
        callback = new Callback<ArrayList<ProviderDO>>();
        service.fetchByLastName(lastName, callback);
        return callback.getResult();
    }

    @Override
    public ProviderManager fetchWithLocations(Integer id) throws Exception {
        Callback<ProviderManager> callback;
        
        callback = new Callback<ProviderManager>();
        service.fetchWithLocations(id, callback);
        return callback.getResult();
    }

    @Override
    public void fetchWithNotes(Integer id, AsyncCallback<ProviderManager> callback) {
        service.fetchWithNotes(id, callback);
    }

    @Override
    public ProviderManager fetchWithNotes(Integer id) throws Exception {
        Callback<ProviderManager> callback;
        
        callback = new Callback<ProviderManager>();
        service.fetchWithNotes(id, callback);
        return callback.getResult();
    }

    @Override
    public void fetchWithLocations(Integer id, AsyncCallback<ProviderManager> callback) {
        service.fetchWithLocations(id, callback);
    }

    @Override
    public ArrayList<IdFirstLastNameVO> query(Query query) throws Exception {
        Callback<ArrayList<IdFirstLastNameVO>> callback;
        
        callback = new Callback<ArrayList<IdFirstLastNameVO>>();
        service.query(query, callback);
        return callback.getResult();
    }
    
    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdFirstLastNameVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public ProviderManager add(ProviderManager man) throws Exception {
        Callback<ProviderManager> callback;
        
        callback = new Callback<ProviderManager>();
        service.add(man, callback);
        return callback.getResult();
    }

    @Override
    public void add(ProviderManager man, AsyncCallback<ProviderManager> callback) {
        service.add(man, callback);
    }

    @Override
    public ProviderManager update(ProviderManager man) throws Exception {
        Callback<ProviderManager> callback;
        
        callback = new Callback<ProviderManager>();
        service.update(man, callback);
        return callback.getResult();
    }

    @Override
    public void update(ProviderManager man, AsyncCallback<ProviderManager> callback) {
        service.update(man, callback);
    }

    @Override
    public void fetchForUpdate(Integer id, AsyncCallback<ProviderManager> callback) {
        service.fetchForUpdate(id, callback);
    }

    @Override
    public ProviderManager fetchForUpdate(Integer id) throws Exception {
        Callback<ProviderManager> callback;
        
        callback = new Callback<ProviderManager>();
        service.fetchForUpdate(id, callback);
        return callback.getResult();
    }
    
    @Override
    public ProviderManager abortUpdate(Integer id) throws Exception {
        Callback<ProviderManager> callback;
        
        callback = new Callback<ProviderManager>();
        service.abortUpdate(id, callback);
        return callback.getResult();
    }
    
    @Override
    public void abortUpdate(Integer id, AsyncCallback<ProviderManager> callback) {
        service.abortUpdate(id, callback);
    }

    @Override
    public void fetchLocationByProviderId(Integer id,
                                          AsyncCallback<ProviderLocationManager> callback) {
        service.fetchLocationByProviderId(id, callback);
    }

    @Override
    public ProviderLocationManager fetchLocationByProviderId(Integer id) throws Exception {
        Callback<ProviderLocationManager> callback;
        
        callback = new Callback<ProviderLocationManager>();
        service.fetchLocationByProviderId(id, callback);
        return callback.getResult();
    }
}