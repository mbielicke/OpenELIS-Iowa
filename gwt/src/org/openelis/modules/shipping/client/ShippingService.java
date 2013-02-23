package org.openelis.modules.shipping.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.ShippingViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.ShippingItemManager;
import org.openelis.manager.ShippingManager;
import org.openelis.manager.ShippingTrackingManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ShippingService implements ShippingServiceInt, ShippingServiceIntAsync {
    
    static ShippingService instance;
    
    ShippingServiceIntAsync service;
    
    public static ShippingService get() {
        if(instance == null)
            instance = new ShippingService();
        
        return instance;
    }
    
    private ShippingService() {
        service = (ShippingServiceIntAsync)GWT.create(ShippingServiceInt.class);
    }

    @Override
    public void abortUpdate(Integer id, AsyncCallback<ShippingManager> callback) {
        service.abortUpdate(id, callback);
    }

    @Override
    public void add(ShippingManager man, AsyncCallback<ShippingManager> callback) {
        service.add(man, callback);
    }

    @Override
    public void fetchById(Integer id, AsyncCallback<ShippingManager> callback) {
        service.fetchById(id, callback);
    }

    @Override
    public void fetchByOrderId(Integer id, AsyncCallback<ShippingViewDO> callback) {
        service.fetchByOrderId(id, callback);
    }

    @Override
    public void fetchForUpdate(Integer id, AsyncCallback<ShippingManager> callback) {
        service.fetchForUpdate(id, callback);
    }

    @Override
    public void fetchItemByShippingId(Integer id, AsyncCallback<ShippingItemManager> callback) {
        service.fetchItemByShippingId(id, callback);
    }

    @Override
    public void fetchTrackingByShippingId(Integer id,
                                          AsyncCallback<ShippingTrackingManager> callback) {
        service.fetchTrackingByShippingId(id, callback);
    }

    @Override
    public void fetchWithItemsAndTrackings(Integer id, AsyncCallback<ShippingManager> callback) {
        service.fetchWithItemsAndTrackings(id, callback);
    }

    @Override
    public void fetchWithNotes(Integer id, AsyncCallback<ShippingManager> callback) {
        service.fetchWithNotes(id, callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public void update(ShippingManager man, AsyncCallback<ShippingManager> callback) {
        service.update(man, callback);
    }

    @Override
    public ShippingManager fetchById(Integer id) throws Exception {
        Callback<ShippingManager> callback;
        
        callback = new Callback<ShippingManager>();
        service.fetchById(id, callback);
        return callback.getResult();
    }

    @Override
    public ShippingViewDO fetchByOrderId(Integer id) throws Exception {
        Callback<ShippingViewDO> callback;
        
        callback = new Callback<ShippingViewDO>();
        service.fetchByOrderId(id, callback);
        return callback.getResult();
    }

    @Override
    public ShippingManager fetchWithItemsAndTrackings(Integer id) throws Exception {
        Callback<ShippingManager> callback;
        
        callback = new Callback<ShippingManager>();
        service.fetchWithItemsAndTrackings(id, callback);
        return callback.getResult();
    }

    @Override
    public ShippingManager fetchWithNotes(Integer id) throws Exception {
        Callback<ShippingManager> callback;
        
        callback = new Callback<ShippingManager>();
        service.fetchWithNotes(id, callback);
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
    public ShippingManager add(ShippingManager man) throws Exception {
        Callback<ShippingManager> callback;
        
        callback = new Callback<ShippingManager>();
        service.add(man, callback);
        return callback.getResult();
    }

    @Override
    public ShippingManager update(ShippingManager man) throws Exception {
        Callback<ShippingManager> callback;
        
        callback = new Callback<ShippingManager>();
        service.update(man, callback);
        return callback.getResult();
    }

    @Override
    public ShippingManager fetchForUpdate(Integer id) throws Exception {
        Callback<ShippingManager> callback;
        
        callback = new Callback<ShippingManager>();
        service.fetchForUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public ShippingManager abortUpdate(Integer id) throws Exception {
        Callback<ShippingManager> callback;
        
        callback = new Callback<ShippingManager>();
        service.abortUpdate(id, callback);
        return callback.getResult();
    }

    @Override
    public ShippingItemManager fetchItemByShippingId(Integer id) throws Exception {
        Callback<ShippingItemManager> callback;
        
        callback = new Callback<ShippingItemManager>();
        service.fetchItemByShippingId(id, callback);
        return callback.getResult();
    }

    @Override
    public ShippingTrackingManager fetchTrackingByShippingId(Integer id) throws Exception {
        Callback<ShippingTrackingManager> callback;
        
        callback = new Callback<ShippingTrackingManager>();
        service.fetchTrackingByShippingId(id, callback);
        return callback.getResult();
    }

}
