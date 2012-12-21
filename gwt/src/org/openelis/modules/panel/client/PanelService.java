package org.openelis.modules.panel.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.IdVO;
import org.openelis.domain.TestMethodVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.PanelItemManager;
import org.openelis.manager.PanelManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PanelService implements PanelServiceInt, PanelServiceIntAsync {
    
    static PanelService instance;
    
    PanelServiceIntAsync service;
    
    public static PanelService get() {
        if(instance == null)
            instance = new PanelService();
        
        return instance;
    }
    
    private PanelService() {
        service = (PanelServiceIntAsync)GWT.create(PanelServiceInt.class);
    }

    @Override
    public void abortUpdate(Integer id, AsyncCallback<PanelManager> callback) {
        service.abortUpdate(id, callback);
    }

    @Override
    public void add(PanelManager man, AsyncCallback<PanelManager> callback) {
        service.add(man, callback);
    }

    @Override
    public void delete(PanelManager man, AsyncCallback<Void> callback) {
        service.delete(man, callback);
    }

    @Override
    public void fetchAuxIdsByPanelId(Integer panelId, AsyncCallback<ArrayList<IdVO>> callback) {
        service.fetchAuxIdsByPanelId(panelId, callback);
    }

    @Override
    public void fetchById(Integer id, AsyncCallback<PanelManager> callback) {
        service.fetchById(id, callback);
    }

    @Override
    public void fetchByNameSampleTypeWithTests(Query query,
                                               AsyncCallback<ArrayList<TestMethodVO>> callback) {
        service.fetchByNameSampleTypeWithTests(query, callback);
    }

    @Override
    public void fetchByNameWithTests(String name, AsyncCallback<ArrayList<TestMethodVO>> callback) {
        service.fetchByNameWithTests(name, callback);
    }

    @Override
    public void fetchForUpdate(Integer id, AsyncCallback<PanelManager> callback) {
        service.fetchForUpdate(id, callback);
    }

    @Override
    public void fetchItemByPanelId(Integer id, AsyncCallback<PanelItemManager> callback) {
        service.fetchItemByPanelId(id, callback);
    }

    @Override
    public void fetchTestIdsByPanelId(Integer panelId, AsyncCallback<ArrayList<IdVO>> callback) {
        service.fetchTestIdsByPanelId(panelId, callback);
    }

    @Override
    public void fetchWithItems(Integer id, AsyncCallback<PanelManager> callback) {
        service.fetchWithItems(id, callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public void update(PanelManager man, AsyncCallback<PanelManager> callback) {
        service.update(man, callback);
    }

    @Override
    public PanelManager fetchById(Integer id) throws Exception {
        Callback<PanelManager> callback;
        
        callback = new Callback<PanelManager>();
        service.fetchById(id, callback);
        return callback.getResult();
    }

    @Override
    public PanelManager fetchWithItems(Integer id) throws Exception {
        Callback<PanelManager> callback;
        
        callback = new Callback<PanelManager>();
        service.fetchWithItems(id, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<TestMethodVO> fetchByNameWithTests(String name) throws Exception {
        Callback<ArrayList<TestMethodVO>> callback;
        
        callback = new Callback<ArrayList<TestMethodVO>>();
        service.fetchByNameWithTests(name, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<TestMethodVO> fetchByNameSampleTypeWithTests(Query query) throws Exception {
        Callback<ArrayList<TestMethodVO>> callback;
        
        callback = new Callback<ArrayList<TestMethodVO>>();
        service.fetchByNameSampleTypeWithTests(query, callback);
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
    public PanelManager add(PanelManager man) throws Exception {
        Callback<PanelManager> callback;
        
        callback = new Callback<PanelManager>();
        service.add(man, callback);
        return callback.getResult();
    }

    @Override
    public PanelManager update(PanelManager man) throws Exception {
        Callback<PanelManager> callback;
        
        callback = new Callback<PanelManager>();
        service.update(man, callback);
        return callback.getResult();
    }

    @Override
    public void delete(PanelManager man) throws Exception {
        Callback<Void> callback;
        
        callback = new Callback<Void>();
        service.delete(man, callback);
    }

    @Override
    public PanelManager fetchForUpdate(Integer id) throws Exception {
        Callback<PanelManager> callback;
        
        callback = new Callback<PanelManager>();
        service.fetchForUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public PanelManager abortUpdate(Integer id) throws Exception {
        Callback<PanelManager> callback;
        
        callback = new Callback<PanelManager>();
        service.abortUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public PanelItemManager fetchItemByPanelId(Integer id) throws Exception {
        Callback<PanelItemManager> callback;
        
        callback = new Callback<PanelItemManager>();
        service.fetchItemByPanelId(id, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<IdVO> fetchTestIdsByPanelId(Integer panelId) throws Exception {
        Callback<ArrayList<IdVO>> callback;
        
        callback = new Callback<ArrayList<IdVO>>();
        service.fetchTestIdsByPanelId(panelId, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<IdVO> fetchAuxIdsByPanelId(Integer panelId) throws Exception {
        Callback<ArrayList<IdVO>> callback;
        
        callback = new Callback<ArrayList<IdVO>>();
        service.fetchAuxIdsByPanelId(panelId, callback);
        return callback.getResult();
    }

}
