package org.openelis.modules.inventoryItem.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameStoreVO;
import org.openelis.domain.InventoryItemDO;
import org.openelis.domain.InventoryItemViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.InventoryComponentManager;
import org.openelis.manager.InventoryItemManager;
import org.openelis.manager.InventoryLocationManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class InventoryItemService implements InventoryItemServiceInt, InventoryItemServiceIntAsync {
    
    static InventoryItemService instance;
    
    InventoryItemServiceIntAsync service;
    
    public static InventoryItemService get() {
        if (instance == null)
            instance = new InventoryItemService();
        
        return instance;
    }
    
    private InventoryItemService() {
        service = (InventoryItemServiceIntAsync)GWT.create(InventoryItemServiceInt.class);
    }

    @Override
    public void abortUpdate(Integer id, AsyncCallback<InventoryItemManager> callback) {
        service.abortUpdate(id, callback);
    }

    @Override
    public void add(InventoryItemManager man, AsyncCallback<InventoryItemManager> callback) {
        service.add(man, callback);
    }

    @Override
    public void fetchActiveByName(String search, AsyncCallback<ArrayList<InventoryItemDO>> callback) {
        service.fetchActiveByName(search, callback);
    }

    @Override
    public void fetchActiveByNameAndStore(Query query,
                                          AsyncCallback<ArrayList<InventoryItemDO>> callback) {
        service.fetchActiveByNameAndStore(query, callback);
    }

    @Override
    public void fetchActiveByNameStoreAndParentInventoryItem(Query query,
                                                             AsyncCallback<ArrayList<InventoryItemDO>> callback) {
        service.fetchActiveByNameStoreAndParentInventoryItem(query, callback);
    }

    @Override
    public void fetchById(Integer id, AsyncCallback<InventoryItemManager> callback) {
        service.fetchById(id, callback);
    }

    @Override
    public void fetchComponentByInventoryItemId(Integer id,
                                                AsyncCallback<InventoryComponentManager> callback) {
        service.fetchComponentByInventoryItemId(id, callback);
    }

    @Override
    public void fetchForUpdate(Integer id, AsyncCallback<InventoryItemManager> callback) {
        service.fetchForUpdate(id, callback);
    }

    @Override
    public void fetchInventoryItemById(Integer id, AsyncCallback<InventoryItemViewDO> callback) {
        service.fetchInventoryItemById(id, callback);
    }

    @Override
    public void fetchLocationByInventoryItemId(Integer id,
                                               AsyncCallback<InventoryLocationManager> callback) {
        service.fetchLocationByInventoryItemId(id, callback);
    }

    @Override
    public void fetchWithComponents(Integer id, AsyncCallback<InventoryItemManager> callback) {
        service.fetchWithComponents(id, callback);
    }

    @Override
    public void fetchWithLocations(Integer id, AsyncCallback<InventoryItemManager> callback) {
        service.fetchWithLocations(id, callback);
    }

    @Override
    public void fetchWithManufacturing(Integer id, AsyncCallback<InventoryItemManager> callback) {
        service.fetchWithManufacturing(id, callback);
    }

    @Override
    public void fetchWithNotes(Integer id, AsyncCallback<InventoryItemManager> callback) {
        service.fetchWithNotes(id, callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdNameStoreVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public void update(InventoryItemManager man, AsyncCallback<InventoryItemManager> callback) {
        service.update(man, callback);
    }

    @Override
    public InventoryItemManager fetchById(Integer id) throws Exception {
        Callback<InventoryItemManager> callback;
        
        callback = new Callback<InventoryItemManager>();
        service.fetchById(id, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<InventoryItemDO> fetchActiveByName(String search) throws Exception {
        Callback<ArrayList<InventoryItemDO>> callback;
        
        callback = new Callback<ArrayList<InventoryItemDO>>();
        service.fetchActiveByName(search, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<InventoryItemDO> fetchActiveByNameAndStore(Query query) throws Exception {
        Callback<ArrayList<InventoryItemDO>> callback;
        
        callback = new Callback<ArrayList<InventoryItemDO>>();
        service.fetchActiveByNameAndStore(query, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<InventoryItemDO> fetchActiveByNameStoreAndParentInventoryItem(Query query) throws Exception {
        Callback<ArrayList<InventoryItemDO>> callback;
        
        callback = new Callback<ArrayList<InventoryItemDO>>();
        service.fetchActiveByNameStoreAndParentInventoryItem(query, callback);
        return callback.getResult();
    }

    @Override
    public InventoryItemViewDO fetchInventoryItemById(Integer id) throws Exception {
        Callback<InventoryItemViewDO> callback;
        
        callback = new Callback<InventoryItemViewDO>();
        service.fetchInventoryItemById(id, callback);
        return callback.getResult();
    }

    @Override
    public InventoryItemManager fetchWithComponents(Integer id) throws Exception {
        Callback<InventoryItemManager> callback;
        
        callback = new Callback<InventoryItemManager>();
        service.fetchWithComponents(id, callback);
        return callback.getResult();
    }

    @Override
    public InventoryItemManager fetchWithLocations(Integer id) throws Exception {
        Callback<InventoryItemManager> callback;
        
        callback = new Callback<InventoryItemManager>();
        service.fetchWithLocations(id, callback);
        return callback.getResult();
    }

    @Override
    public InventoryItemManager fetchWithManufacturing(Integer id) throws Exception {
        Callback<InventoryItemManager> callback;
        
        callback = new Callback<InventoryItemManager>();
        service.fetchWithManufacturing(id, callback);
        return callback.getResult();
    }

    @Override
    public InventoryItemManager fetchWithNotes(Integer id) throws Exception {
        Callback<InventoryItemManager> callback;
        
        callback = new Callback<InventoryItemManager>();
        service.fetchWithNotes(id, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<IdNameStoreVO> query(Query query) throws Exception {
        Callback<ArrayList<IdNameStoreVO>> callback;
        
        callback = new Callback<ArrayList<IdNameStoreVO>>();
        service.query(query, callback);
        return callback.getResult();
    }

    @Override
    public InventoryItemManager add(InventoryItemManager man) throws Exception {
        Callback<InventoryItemManager> callback;
        
        callback = new Callback<InventoryItemManager>();
        service.add(man, callback);
        return callback.getResult();
    }

    @Override
    public InventoryItemManager update(InventoryItemManager man) throws Exception {
        Callback<InventoryItemManager> callback;
        
        callback = new Callback<InventoryItemManager>();
        service.update(man, callback);
        return callback.getResult();
    }

    @Override
    public InventoryItemManager fetchForUpdate(Integer id) throws Exception {
        Callback<InventoryItemManager> callback;
        
        callback = new Callback<InventoryItemManager>();
        service.fetchForUpdate(id,callback);
        return callback.getResult();
    }

    @Override
    public InventoryItemManager abortUpdate(Integer id) throws Exception {
        Callback<InventoryItemManager> callback;
        
        callback = new Callback<InventoryItemManager>();
        service.abortUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public InventoryComponentManager fetchComponentByInventoryItemId(Integer id) throws Exception {
        Callback<InventoryComponentManager> callback;
        
        callback = new Callback<InventoryComponentManager>();
        service.fetchComponentByInventoryItemId(id, callback);
        return callback.getResult();
    }

    @Override
    public InventoryLocationManager fetchLocationByInventoryItemId(Integer id) throws Exception {
        Callback<InventoryLocationManager> callback;
        
        callback = new Callback<InventoryLocationManager>();
        service.fetchLocationByInventoryItemId(id, callback);
        return callback.getResult();
    }
    
    
}
