package org.openelis.modules.order.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrderRecurrenceDO;
import org.openelis.domain.OrderViewDO;
import org.openelis.gwt.common.Prompt;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.OrderContainerManager;
import org.openelis.manager.OrderFillManager;
import org.openelis.manager.OrderItemManager;
import org.openelis.manager.OrderManager;
import org.openelis.manager.OrderOrganizationManager;
import org.openelis.manager.OrderReceiptManager;
import org.openelis.manager.OrderTestAnalyteManager;
import org.openelis.manager.OrderTestManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class OrderService implements OrderServiceInt, OrderServiceIntAsync {
    
    static OrderService instance;
    
    OrderServiceIntAsync service;
    
    public static OrderService get() {
        if(instance == null)
            instance = new OrderService();
        
        return instance;
    }
    
    private OrderService() {
        service = (OrderServiceIntAsync)GWT.create(OrderServiceInt.class);
    }

    @Override
    public void abortUpdate(Integer id, AsyncCallback<OrderManager> callback) {
        service.abortUpdate(id, callback);
    }

    @Override
    public void add(OrderManager man, AsyncCallback<OrderManager> callback) {
        service.add(man, callback);
    }

    @Override
    public void duplicate(Integer id, AsyncCallback<OrderManager> callback) {
        service.duplicate(id, callback);
    }

    @Override
    public void fetchByDescription(String search, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.fetchByDescription(search, callback);
    }

    @Override
    public void fetchById(Integer id, AsyncCallback<OrderManager> callback) {
        service.fetchById(id, callback);
    }

    @Override
    public void fetchByShippingItemId(Integer id, AsyncCallback<OrderViewDO> callback) {
        service.fetchByShippingItemId(id, callback);
    }

    @Override
    public void fetchContainerByOrderId(Integer id, AsyncCallback<OrderContainerManager> callback) {
        service.fetchContainerByOrderId(id, callback);
    }

    @Override
    public void fetchFillByOrderId(Integer id, AsyncCallback<OrderFillManager> callback) {
        service.fetchFillByOrderId(id, callback);
    }

    @Override
    public void fetchForUpdate(Integer id, AsyncCallback<OrderManager> callback) {
        service.fetchForUpdate(id, callback);
    }

    @Override
    public void fetchItemByOrderId(Integer id, AsyncCallback<OrderItemManager> callback) {
        service.fetchItemByOrderId(id, callback);
    }

    @Override
    public void fetchOrganizationByOrderId(Integer id,
                                           AsyncCallback<OrderOrganizationManager> callback) {
        service.fetchOrganizationByOrderId(id, callback);
    }

    @Override
    public void fetchReceiptByOrderId(Integer id, AsyncCallback<OrderReceiptManager> callback) {
        service.fetchReceiptByOrderId(id, callback);
    }

    @Override
    public void fetchRecurrenceByOrderId(Integer id, AsyncCallback<OrderRecurrenceDO> callback) {
        service.fetchRecurrenceByOrderId(id, callback);
    }

    @Override
    public void fetchTestAnalyteByOrderTestId(Integer id,
                                              AsyncCallback<OrderTestAnalyteManager> callback) {
        service.fetchTestAnalyteByOrderTestId(id, callback);
    }

    @Override
    public void fetchTestAnalyteByTestId(Integer id, AsyncCallback<OrderTestAnalyteManager> callback) {
        service.fetchTestAnalyteByTestId(id, callback);
    }

    @Override
    public void fetchTestByOrderId(Integer id, AsyncCallback<OrderTestManager> callback) {
        service.fetchTestByOrderId(id, callback);
    }

    @Override
    public void fetchWithContainers(Integer id, AsyncCallback<OrderManager> callback) {
        service.fetchWithContainers(id, callback);
    }

    @Override
    public void fetchWithFills(Integer id, AsyncCallback<OrderManager> callback) {
        service.fetchWithFills(id, callback);
    }

    @Override
    public void fetchWithItems(Integer id, AsyncCallback<OrderManager> callback) {
        service.fetchWithItems(id, callback);
    }

    @Override
    public void fetchWithNotes(Integer id, AsyncCallback<OrderManager> callback) {
        service.fetchWithNotes(id, callback);
    }

    @Override
    public void fetchWithOrganizations(Integer id, AsyncCallback<OrderManager> callback) {
        service.fetchWithOrganizations(id, callback);
    }

    @Override
    public void fetchWithRecurring(Integer id, AsyncCallback<OrderManager> callback) {
        service.fetchWithRecurring(id, callback);
    }

    @Override
    public void fetchWithTests(Integer id, AsyncCallback<OrderManager> callback) {
        service.fetchWithTests(id, callback);
    }

    @Override
    public void getPrompts(AsyncCallback<ArrayList<Prompt>> callback) {
        service.getPrompts(callback);
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public void queryOrderFill(Query query, AsyncCallback<ArrayList<OrderViewDO>> callback) {
        service.queryOrderFill(query, callback);
    }

    @Override
    public void recurOrders(AsyncCallback<Void> callback) {
        service.recurOrders(callback);
    }

    @Override
    public void update(OrderManager man, AsyncCallback<OrderManager> callback) {
        service.update(man, callback);
    }

    @Override
    public OrderManager fetchById(Integer id) throws Exception {
        Callback<OrderManager> callback;
        
        callback = new Callback<OrderManager>();
        service.fetchById(id, callback);
        return callback.getResult();
    }

    @Override
    public ArrayList<IdNameVO> fetchByDescription(String search) throws Exception {
        Callback<ArrayList<IdNameVO>> callback;
        
        callback = new Callback<ArrayList<IdNameVO>>();
        service.fetchByDescription(search, callback);
        return callback.getResult();

    }

    @Override
    public OrderViewDO fetchByShippingItemId(Integer id) throws Exception {
        Callback<OrderViewDO> callback;
        
        callback = new Callback<OrderViewDO>();
        service.fetchByShippingItemId(id, callback);
        return callback.getResult();

    }

    @Override
    public OrderManager fetchWithOrganizations(Integer id) throws Exception {
        Callback<OrderManager> callback;
        
        callback = new Callback<OrderManager>();
        service.fetchWithOrganizations(id, callback);
        return callback.getResult();

    }

    @Override
    public OrderManager fetchWithItems(Integer id) throws Exception {
        Callback<OrderManager> callback;
        
        callback = new Callback<OrderManager>();
        service.fetchWithItems(id, callback);
        return callback.getResult();
    }

    @Override
    public OrderManager fetchWithFills(Integer id) throws Exception {
        Callback<OrderManager> callback;
        
        callback = new Callback<OrderManager>();
        service.fetchWithFills(id, callback);
        return callback.getResult();

    }

    @Override
    public OrderManager fetchWithNotes(Integer id) throws Exception {
        Callback<OrderManager> callback;
        
        callback = new Callback<OrderManager>();
        service.fetchWithNotes(id, callback);
        return callback.getResult();

    }

    @Override
    public OrderManager fetchWithTests(Integer id) throws Exception {
        Callback<OrderManager> callback;
        
        callback = new Callback<OrderManager>();
        service.fetchWithTests(id, callback);
        return callback.getResult();

    }

    @Override
    public OrderManager fetchWithContainers(Integer id) throws Exception {
        Callback<OrderManager> callback;
        
        callback = new Callback<OrderManager>();
        service.fetchWithContainers(id, callback);
        return callback.getResult();

    }

    @Override
    public OrderManager fetchWithRecurring(Integer id) throws Exception {
        Callback<OrderManager> callback;
        
        callback = new Callback<OrderManager>();
        service.fetchWithRecurring(id, callback);
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
    public ArrayList<OrderViewDO> queryOrderFill(Query query) throws Exception {
        Callback<ArrayList<OrderViewDO>> callback;
        
        callback = new Callback<ArrayList<OrderViewDO>>();
        service.queryOrderFill(query, callback);
        return callback.getResult();

    }

    @Override
    public OrderManager add(OrderManager man) throws Exception {
        Callback<OrderManager> callback;
        
        callback = new Callback<OrderManager>();
        service.add(man, callback);
        return callback.getResult();

    }

    @Override
    public OrderManager update(OrderManager man) throws Exception {
        Callback<OrderManager> callback;
        
        callback = new Callback<OrderManager>();
        service.update(man, callback);
        return callback.getResult();

    }

    @Override
    public OrderManager fetchForUpdate(Integer id) throws Exception {
        Callback<OrderManager> callback;
        
        callback = new Callback<OrderManager>();
        service.fetchForUpdate(id, callback);
        return callback.getResult();

    }

    @Override
    public OrderManager abortUpdate(Integer id) throws Exception {
        Callback<OrderManager> callback;
        
        callback = new Callback<OrderManager>();
        service.abortUpdate(id, callback);
        return callback.getResult();

    }

    @Override
    public OrderManager duplicate(Integer id) throws Exception {
        Callback<OrderManager> callback;
        
        callback = new Callback<OrderManager>();
        service.duplicate(id, callback);
        return callback.getResult();

    }

    @Override
    public OrderOrganizationManager fetchOrganizationByOrderId(Integer id) throws Exception {
        Callback<OrderOrganizationManager> callback;
        
        callback = new Callback<OrderOrganizationManager>();
        service.fetchOrganizationByOrderId(id, callback);
        return callback.getResult();

    }

    @Override
    public OrderItemManager fetchItemByOrderId(Integer id) throws Exception {
        Callback<OrderItemManager> callback;
        
        callback = new Callback<OrderItemManager>();
        service.fetchItemByOrderId(id, callback);
        return callback.getResult();

    }

    @Override
    public OrderFillManager fetchFillByOrderId(Integer id) throws Exception {
        Callback<OrderFillManager> callback;
        
        callback = new Callback<OrderFillManager>();
        service.fetchFillByOrderId(id, callback);
        return callback.getResult();

    }

    @Override
    public OrderReceiptManager fetchReceiptByOrderId(Integer id) throws Exception {
        Callback<OrderReceiptManager> callback;
        
        callback = new Callback<OrderReceiptManager>();
        service.fetchReceiptByOrderId(id, callback);
        return callback.getResult();

    }

    @Override
    public OrderTestManager fetchTestByOrderId(Integer id) throws Exception {
        Callback<OrderTestManager> callback;
        
        callback = new Callback<OrderTestManager>();
        service.fetchTestByOrderId(id, callback);
        return callback.getResult();

    }

    @Override
    public OrderTestAnalyteManager fetchTestAnalyteByOrderTestId(Integer id) throws Exception {
        Callback<OrderTestAnalyteManager> callback;
        
        callback = new Callback<OrderTestAnalyteManager>();
        service.fetchTestAnalyteByOrderTestId(id, callback);
        return callback.getResult();

    }

    @Override
    public OrderTestAnalyteManager fetchTestAnalyteByTestId(Integer id) throws Exception {
        Callback<OrderTestAnalyteManager> callback;
        
        callback = new Callback<OrderTestAnalyteManager>();
        service.fetchTestAnalyteByTestId(id, callback);
        return callback.getResult();
    }

    @Override
    public OrderContainerManager fetchContainerByOrderId(Integer id) throws Exception {
        Callback<OrderContainerManager> callback;
        
        callback = new Callback<OrderContainerManager>();
        service.fetchContainerByOrderId(id, callback);
        return callback.getResult();

    }

    @Override
    public OrderRecurrenceDO fetchRecurrenceByOrderId(Integer id) throws Exception {
        Callback<OrderRecurrenceDO> callback;
        
        callback = new Callback<OrderRecurrenceDO>();
        service.fetchRecurrenceByOrderId(id, callback);
        return callback.getResult();

    }

    @Override
    public ArrayList<Prompt> getPrompts() throws Exception {
        Callback<ArrayList<Prompt>> callback;
        
        callback = new Callback<ArrayList<Prompt>>();
        service.getPrompts(callback);
        return callback.getResult();

    }

    @Override
    public void recurOrders() throws Exception {
        Callback<Void> callback;
        
        callback = new Callback<Void>();
        service.recurOrders(callback);
    }

    @Override
    public void fetchMergedTestAnalyteByOrderTestId(Integer id,
                                                    AsyncCallback<OrderTestAnalyteManager> callback) {
        service.fetchMergedTestAnalyteByOrderTestId(id, callback);
        
    }

    @Override
    public OrderTestAnalyteManager fetchMergedTestAnalyteByOrderTestId(Integer id) throws Exception {
        Callback<OrderTestAnalyteManager> callback;
        
        callback = new Callback<OrderTestAnalyteManager>();
        service.fetchMergedTestAnalyteByOrderTestId(id, callback);
        return callback.getResult();

    }

}
