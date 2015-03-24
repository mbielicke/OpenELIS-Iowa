/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.modules.order1.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrderReturnVO;
import org.openelis.gwt.screen.Callback;
import org.openelis.manager.OrderManager1;
import org.openelis.manager.OrderManager1.Load;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.services.TokenService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;

/**
 * This class implements and provides clients with the standard Sync and Async
 * service calls
 */
public class OrderService1 implements OrderServiceInt1, OrderServiceInt1Async {
    /**
     * GWT.created service to make calls to the server
     */
    private OrderServiceInt1Async service;

    private static OrderService1  instance;

    public static OrderService1 get() {
        if (instance == null)
            instance = new OrderService1();

        return instance;
    }

    private OrderService1() {
        service = (OrderServiceInt1Async)GWT.create(OrderServiceInt1.class);
        ((HasRpcToken)service).setRpcToken(TokenService.getToken());
    }

    @Override
    public OrderManager1 getInstance(String type) throws Exception {
        Callback<OrderManager1> callback;

        callback = new Callback<OrderManager1>();
        service.getInstance(type, callback);
        return callback.getResult();
    }

    @Override
    public void getInstance(String type, AsyncCallback<OrderManager1> callback) {
        service.getInstance(type, callback);
    }

    @Override
    public OrderManager1 fetchById(Integer orderId, Load... elements) throws Exception {
        Callback<OrderManager1> callback;

        callback = new Callback<OrderManager1>();
        service.fetchById(orderId, elements, callback);
        return callback.getResult();
    }

    @Override
    public void fetchById(Integer orderId, Load[] elements, AsyncCallback<OrderManager1> callback) {
        service.fetchById(orderId, elements, callback);
    }

    @Override
    public ArrayList<OrderManager1> fetchByIds(ArrayList<Integer> orderIds, Load... elements) throws Exception {
        Callback<ArrayList<OrderManager1>> callback;

        callback = new Callback<ArrayList<OrderManager1>>();
        service.fetchByIds(orderIds, elements, callback);
        return callback.getResult();
    }

    @Override
    public void fetchByIds(ArrayList<Integer> orderIds, Load[] elements,
                           AsyncCallback<ArrayList<OrderManager1>> callback) {
        service.fetchByIds(orderIds, elements, callback);
    }

    @Override
    public ArrayList<OrderManager1> fetchByQuery(ArrayList<QueryData> fields, int first, int max,
                                                 Load... elements) throws Exception {
        Callback<ArrayList<OrderManager1>> callback;

        callback = new Callback<ArrayList<OrderManager1>>();
        service.fetchByQuery(fields, first, max, elements, callback);
        return callback.getResult();
    }

    @Override
    public void fetchByQuery(ArrayList<QueryData> fields, int first, int max, Load[] elements,
                             AsyncCallback<ArrayList<OrderManager1>> callback) {
        service.fetchByQuery(fields, first, max, elements, callback);
    }

    @Override
    public OrderManager1 fetchWith(OrderManager1 om, Load... elements) throws Exception {
        Callback<OrderManager1> callback;

        callback = new Callback<OrderManager1>();
        service.fetchWith(om, elements, callback);
        return callback.getResult();
    }

    @Override
    public void fetchWith(OrderManager1 om, Load[] elements, AsyncCallback<OrderManager1> callback) {
        service.fetchWith(om, elements, callback);
    }

    @Override
    public ArrayList<IdNameVO> query(Query query) throws Exception {
        Callback<ArrayList<IdNameVO>> callback;

        callback = new Callback<ArrayList<IdNameVO>>();
        service.query(query, callback);
        return callback.getResult();
    }

    @Override
    public void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.query(query, callback);
    }

    @Override
    public OrderManager1 fetchForUpdate(Integer orderId) throws Exception {
        Callback<OrderManager1> callback;

        callback = new Callback<OrderManager1>();
        service.fetchForUpdate(orderId, callback);
        return callback.getResult();
    }

    @Override
    public void fetchForUpdate(Integer orderId, AsyncCallback<OrderManager1> callback) {
        service.fetchForUpdate(orderId, callback);
    }

    @Override
    public OrderManager1 fetchForUpdate(Integer orderId, Load... elements) throws Exception {
        Callback<OrderManager1> callback;

        callback = new Callback<OrderManager1>();
        service.fetchForUpdate(orderId, elements, callback);
        return callback.getResult();
    }

    @Override
    public void fetchForUpdate(Integer orderId, Load[] elements,
                               AsyncCallback<OrderManager1> callback) {
        service.fetchForUpdate(orderId, elements, callback);
    }

    @Override
    public ArrayList<OrderManager1> fetchForUpdate(ArrayList<Integer> orderIds, Load... elements) throws Exception {
        Callback<ArrayList<OrderManager1>> callback;

        callback = new Callback<ArrayList<OrderManager1>>();
        service.fetchForUpdate(orderIds, elements, callback);
        return callback.getResult();
    }

    @Override
    public void fetchForUpdate(ArrayList<Integer> orderIds, Load[] elements,
                               AsyncCallback<ArrayList<OrderManager1>> callback) {
        service.fetchForUpdate(orderIds, elements, callback);
    }

    @Override
    public OrderManager1 unlock(Integer orderId, Load... elements) throws Exception {
        Callback<OrderManager1> callback;

        callback = new Callback<OrderManager1>();
        service.unlock(orderId, elements, callback);
        return callback.getResult();
    }

    @Override
    public void unlock(Integer orderId, Load[] elements, AsyncCallback<OrderManager1> callback) {
        service.unlock(orderId, elements, callback);
    }

    @Override
    public ArrayList<OrderManager1> unlock(ArrayList<Integer> orderIds, Load... elements) throws Exception {
        Callback<ArrayList<OrderManager1>> callback;

        callback = new Callback<ArrayList<OrderManager1>>();
        service.unlock(orderIds, elements, callback);
        return callback.getResult();
    }

    @Override
    public void unlock(ArrayList<Integer> orderIds, Load[] elements,
                       AsyncCallback<ArrayList<OrderManager1>> callback) {
        service.unlock(orderIds, elements, callback);
    }

    @Override
    public OrderReturnVO duplicate(Integer id) throws Exception {
        Callback<OrderReturnVO> callback;

        callback = new Callback<OrderReturnVO>();
        service.duplicate(id, callback);
        return callback.getResult();
    }

    @Override
    public void duplicate(Integer id, AsyncCallback<OrderReturnVO> callback) {
        service.duplicate(id, callback);
    }

    @Override
    public OrderManager1 update(OrderManager1 om, boolean ignoreWarnings) throws Exception {
        Callback<OrderManager1> callback;

        callback = new Callback<OrderManager1>();
        service.update(om, ignoreWarnings, callback);
        return callback.getResult();
    }

    @Override
    public void update(OrderManager1 om, boolean ignoreWarnings,
                       AsyncCallback<OrderManager1> callback) {
        service.update(om, ignoreWarnings, callback);
    }

    @Override
    public ArrayList<IdNameVO> fetchByDescription(String search, int max) throws Exception {
        Callback<ArrayList<IdNameVO>> callback;

        callback = new Callback<ArrayList<IdNameVO>>();
        service.fetchByDescription(search, max, callback);
        return callback.getResult();
    }

    @Override
    public void fetchByDescription(String search, int max,
                                   AsyncCallback<ArrayList<IdNameVO>> callback) {
        service.fetchByDescription(search, max, callback);
    }

    @Override
    public OrderReturnVO addAuxGroups(OrderManager1 om, ArrayList<Integer> groupIds) throws Exception {
        Callback<OrderReturnVO> callback;

        callback = new Callback<OrderReturnVO>();
        service.addAuxGroups(om, groupIds, callback);
        return callback.getResult();
    }

    @Override
    public void addAuxGroups(OrderManager1 om, ArrayList<Integer> groupIds,
                             AsyncCallback<OrderReturnVO> callback) {
        service.addAuxGroups(om, groupIds, callback);
    }

    @Override
    public OrderManager1 removeAuxGroups(OrderManager1 om, ArrayList<Integer> groupIds) throws Exception {
        Callback<OrderManager1> callback;

        callback = new Callback<OrderManager1>();
        service.removeAuxGroups(om, groupIds, callback);
        return callback.getResult();
    }

    @Override
    public void removeAuxGroups(OrderManager1 om, ArrayList<Integer> groupIds,
                                AsyncCallback<OrderManager1> callback) {
        service.removeAuxGroups(om, groupIds, callback);
    }

    @Override
    public OrderReturnVO addTest(OrderManager1 om, Integer id, boolean isTest, Integer index) throws Exception {
        Callback<OrderReturnVO> callback;

        callback = new Callback<OrderReturnVO>();
        service.addTest(om, id, isTest, index, callback);
        return callback.getResult();
    }

    @Override
    public void addTest(OrderManager1 om, Integer id, boolean isTest, Integer index,
                        AsyncCallback<OrderReturnVO> callback) {
        service.addTest(om, id, isTest, index, callback);
    }

    @Override
    public OrderManager1 removeTests(OrderManager1 om, ArrayList<Integer> ids) throws Exception {
        Callback<OrderManager1> callback;

        callback = new Callback<OrderManager1>();
        service.removeTests(om, ids, callback);
        return callback.getResult();
    }

    @Override
    public void removeTests(OrderManager1 om, ArrayList<Integer> ids,
                            AsyncCallback<OrderManager1> callback) {
        service.removeTests(om, ids, callback);
    }
}