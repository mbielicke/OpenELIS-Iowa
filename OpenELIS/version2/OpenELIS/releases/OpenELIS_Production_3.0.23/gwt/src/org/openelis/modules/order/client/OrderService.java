package org.openelis.modules.order.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.IOrderRecurrenceDO;
import org.openelis.domain.IOrderViewDO;
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.services.TokenService;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.IOrderContainerManager;
import org.openelis.manager.IOrderFillManager;
import org.openelis.manager.IOrderItemManager;
import org.openelis.manager.IOrderManager;
import org.openelis.manager.IOrderOrganizationManager;
import org.openelis.manager.IOrderReceiptManager;
import org.openelis.manager.IOrderTestAnalyteManager;
import org.openelis.manager.IOrderTestManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

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
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }

    @Override
    public void abortUpdate(Integer id, AsyncCallback<IOrderManager> callback) {
        service.abortUpdate(id, callback);
    }

    @Override
    public void add(IOrderManager man, AsyncCallback<IOrderManager> callback) {
        service.add(man, callback);
    }

    @Override
    public void duplicate(Integer id, AsyncCallback<IOrderManager> callback) {
        service.duplicate(id, callback);
    }

    @Override
    public void fetchByDescription(String search, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.fetchByDescription(search, callback);
    }

    @Override
    public void fetchById(Integer id, AsyncCallback<IOrderManager> callback) {
        service.fetchById(id, callback);
    }

    @Override
    public void fetchByIorderItemId(Integer id, AsyncCallback<IOrderManager> callback) {
        service.fetchByIorderItemId(id, callback);
    }

    @Override
    public void fetchContainerByOrderId(Integer id, AsyncCallback<IOrderContainerManager> callback) {
        service.fetchContainerByOrderId(id, callback);
    }

    @Override
    public void fetchFillByIorderId(Integer id, AsyncCallback<IOrderFillManager> callback) {
        service.fetchFillByIorderId(id, callback);
    }

    @Override
    public void fetchForUpdate(Integer id, AsyncCallback<IOrderManager> callback) {
        service.fetchForUpdate(id, callback);
    }

    @Override
    public void fetchItemByIorderId(Integer id, AsyncCallback<IOrderItemManager> callback) {
        service.fetchItemByIorderId(id, callback);
    }

    @Override
    public void fetchOrganizationByIorderId(Integer id,
                                           AsyncCallback<IOrderOrganizationManager> callback) {
        service.fetchOrganizationByIorderId(id, callback);
    }

    @Override
    public void fetchReceiptByIorderId(Integer id, AsyncCallback<IOrderReceiptManager> callback) {
        service.fetchReceiptByIorderId(id, callback);
    }

    @Override
    public void fetchRecurrenceByIorderId(Integer id, AsyncCallback<IOrderRecurrenceDO> callback) {
        service.fetchRecurrenceByIorderId(id, callback);
    }

    @Override
    public void fetchTestAnalyteByIorderTestId(Integer id,
                                              AsyncCallback<IOrderTestAnalyteManager> callback) {
        service.fetchTestAnalyteByIorderTestId(id, callback);
    }

    @Override
    public void fetchTestAnalyteByTestId(Integer id, AsyncCallback<IOrderTestAnalyteManager> callback) {
        service.fetchTestAnalyteByTestId(id, callback);
    }

    @Override
    public void fetchTestByIorderId(Integer id, AsyncCallback<IOrderTestManager> callback) {
        service.fetchTestByIorderId(id, callback);
    }

    @Override
    public void fetchWithContainers(Integer id, AsyncCallback<IOrderManager> callback) {
        service.fetchWithContainers(id, callback);
    }

    @Override
    public void fetchWithFills(Integer id, AsyncCallback<IOrderManager> callback) {
        service.fetchWithFills(id, callback);
    }

    @Override
    public void fetchWithItems(Integer id, AsyncCallback<IOrderManager> callback) {
        service.fetchWithItems(id, callback);
    }

    @Override
    public void fetchWithNotes(Integer id, AsyncCallback<IOrderManager> callback) {
        service.fetchWithNotes(id, callback);
    }

    @Override
    public void fetchWithOrganizations(Integer id, AsyncCallback<IOrderManager> callback) {
        service.fetchWithOrganizations(id, callback);
    }

    @Override
    public void fetchWithRecurring(Integer id, AsyncCallback<IOrderManager> callback) {
        service.fetchWithRecurring(id, callback);
    }

    @Override
    public void fetchWithTests(Integer id, AsyncCallback<IOrderManager> callback) {
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
    public void queryOrderFill(Query query, AsyncCallback<ArrayList<IOrderViewDO>> callback) {
        service.queryOrderFill(query, callback);
    }

    @Override
    public void update(IOrderManager man, AsyncCallback<IOrderManager> callback) {
        service.update(man, callback);
    }

    @Override
    public IOrderManager fetchById(Integer id) throws Exception {
        Callback<IOrderManager> callback;
        
        callback = new Callback<IOrderManager>();
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
    public IOrderManager fetchByIorderItemId(Integer id) throws Exception {
        Callback<IOrderManager> callback;
        
        callback = new Callback<IOrderManager>();
        service.fetchByIorderItemId(id, callback);
        return callback.getResult();

    }

    @Override
    public IOrderManager fetchWithOrganizations(Integer id) throws Exception {
        Callback<IOrderManager> callback;
        
        callback = new Callback<IOrderManager>();
        service.fetchWithOrganizations(id, callback);
        return callback.getResult();

    }

    @Override
    public IOrderManager fetchWithItems(Integer id) throws Exception {
        Callback<IOrderManager> callback;
        
        callback = new Callback<IOrderManager>();
        service.fetchWithItems(id, callback);
        return callback.getResult();
    }

    @Override
    public IOrderManager fetchWithFills(Integer id) throws Exception {
        Callback<IOrderManager> callback;
        
        callback = new Callback<IOrderManager>();
        service.fetchWithFills(id, callback);
        return callback.getResult();

    }

    @Override
    public IOrderManager fetchWithNotes(Integer id) throws Exception {
        Callback<IOrderManager> callback;
        
        callback = new Callback<IOrderManager>();
        service.fetchWithNotes(id, callback);
        return callback.getResult();

    }

    @Override
    public IOrderManager fetchWithTests(Integer id) throws Exception {
        Callback<IOrderManager> callback;
        
        callback = new Callback<IOrderManager>();
        service.fetchWithTests(id, callback);
        return callback.getResult();

    }

    @Override
    public IOrderManager fetchWithContainers(Integer id) throws Exception {
        Callback<IOrderManager> callback;
        
        callback = new Callback<IOrderManager>();
        service.fetchWithContainers(id, callback);
        return callback.getResult();

    }

    @Override
    public IOrderManager fetchWithRecurring(Integer id) throws Exception {
        Callback<IOrderManager> callback;
        
        callback = new Callback<IOrderManager>();
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
    public ArrayList<IOrderViewDO> queryOrderFill(Query query) throws Exception {
        Callback<ArrayList<IOrderViewDO>> callback;
        
        callback = new Callback<ArrayList<IOrderViewDO>>();
        service.queryOrderFill(query, callback);
        return callback.getResult();

    }

    @Override
    public IOrderManager add(IOrderManager man) throws Exception {
        Callback<IOrderManager> callback;
        
        callback = new Callback<IOrderManager>();
        service.add(man, callback);
        return callback.getResult();

    }

    @Override
    public IOrderManager update(IOrderManager man) throws Exception {
        Callback<IOrderManager> callback;
        
        callback = new Callback<IOrderManager>();
        service.update(man, callback);
        return callback.getResult();

    }

    @Override
    public IOrderManager fetchForUpdate(Integer id) throws Exception {
        Callback<IOrderManager> callback;
        
        callback = new Callback<IOrderManager>();
        service.fetchForUpdate(id, callback);
        return callback.getResult();

    }

    @Override
    public IOrderManager abortUpdate(Integer id) throws Exception {
        Callback<IOrderManager> callback;
        
        callback = new Callback<IOrderManager>();
        service.abortUpdate(id, callback);
        return callback.getResult();

    }

    @Override
    public IOrderManager duplicate(Integer id) throws Exception {
        Callback<IOrderManager> callback;
        
        callback = new Callback<IOrderManager>();
        service.duplicate(id, callback);
        return callback.getResult();

    }

    @Override
    public IOrderOrganizationManager fetchOrganizationByIorderId(Integer id) throws Exception {
        Callback<IOrderOrganizationManager> callback;
        
        callback = new Callback<IOrderOrganizationManager>();
        service.fetchOrganizationByIorderId(id, callback);
        return callback.getResult();

    }

    @Override
    public IOrderItemManager fetchItemByIorderId(Integer id) throws Exception {
        Callback<IOrderItemManager> callback;
        
        callback = new Callback<IOrderItemManager>();
        service.fetchItemByIorderId(id, callback);
        return callback.getResult();

    }

    @Override
    public IOrderFillManager fetchFillByIorderId(Integer id) throws Exception {
        Callback<IOrderFillManager> callback;
        
        callback = new Callback<IOrderFillManager>();
        service.fetchFillByIorderId(id, callback);
        return callback.getResult();

    }

    @Override
    public IOrderReceiptManager fetchReceiptByIorderId(Integer id) throws Exception {
        Callback<IOrderReceiptManager> callback;
        
        callback = new Callback<IOrderReceiptManager>();
        service.fetchReceiptByIorderId(id, callback);
        return callback.getResult();

    }

    @Override
    public IOrderTestManager fetchTestByIorderId(Integer id) throws Exception {
        Callback<IOrderTestManager> callback;
        
        callback = new Callback<IOrderTestManager>();
        service.fetchTestByIorderId(id, callback);
        return callback.getResult();

    }

    @Override
    public IOrderTestAnalyteManager fetchTestAnalyteByIorderTestId(Integer id) throws Exception {
        Callback<IOrderTestAnalyteManager> callback;
        
        callback = new Callback<IOrderTestAnalyteManager>();
        service.fetchTestAnalyteByIorderTestId(id, callback);
        return callback.getResult();

    }

    @Override
    public IOrderTestAnalyteManager fetchTestAnalyteByTestId(Integer id) throws Exception {
        Callback<IOrderTestAnalyteManager> callback;
        
        callback = new Callback<IOrderTestAnalyteManager>();
        service.fetchTestAnalyteByTestId(id, callback);
        return callback.getResult();
    }

    @Override
    public IOrderContainerManager fetchContainerByOrderId(Integer id) throws Exception {
        Callback<IOrderContainerManager> callback;
        
        callback = new Callback<IOrderContainerManager>();
        service.fetchContainerByOrderId(id, callback);
        return callback.getResult();

    }

    @Override
    public IOrderRecurrenceDO fetchRecurrenceByIorderId(Integer id) throws Exception {
        Callback<IOrderRecurrenceDO> callback;
        
        callback = new Callback<IOrderRecurrenceDO>();
        service.fetchRecurrenceByIorderId(id, callback);
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
    public void fetchMergedTestAnalyteByIorderTestId(Integer id,
                                                    AsyncCallback<IOrderTestAnalyteManager> callback) {
        service.fetchMergedTestAnalyteByIorderTestId(id, callback);
        
    }

    @Override
    public IOrderTestAnalyteManager fetchMergedTestAnalyteByIorderTestId(Integer id) throws Exception {
        Callback<IOrderTestAnalyteManager> callback;
        
        callback = new Callback<IOrderTestAnalyteManager>();
        service.fetchMergedTestAnalyteByIorderTestId(id, callback);
        return callback.getResult();

    }

}
