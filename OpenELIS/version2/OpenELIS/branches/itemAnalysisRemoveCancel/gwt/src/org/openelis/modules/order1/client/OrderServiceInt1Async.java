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
import org.openelis.domain.OrderTestReturnVO;
import org.openelis.manager.OrderManager1;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * ScreenServiceIntAsync is the Asynchronous version of the ScreenServiceInt
 * interface.
 */
public interface OrderServiceInt1Async {

    public void getInstance(String type, AsyncCallback<OrderManager1> callback) throws Exception;

    public void fetchById(Integer orderId, OrderManager1.Load elements[],
                          AsyncCallback<OrderManager1> callback) throws Exception;

    public void fetchByIds(ArrayList<Integer> orderIds, OrderManager1.Load elements[],
                           AsyncCallback<ArrayList<OrderManager1>> callback) throws Exception;

    public void fetchByQuery(ArrayList<QueryData> fields, int first, int max,
                             OrderManager1.Load elements[],
                             AsyncCallback<ArrayList<OrderManager1>> callback) throws Exception;

    public void fetchWith(OrderManager1 om, OrderManager1.Load elements[],
                          AsyncCallback<OrderManager1> callback) throws Exception;

    public void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback) throws Exception;

    public void fetchForUpdate(Integer orderId, AsyncCallback<OrderManager1> callback) throws Exception;

    public void fetchForUpdate(Integer orderId, OrderManager1.Load elements[],
                               AsyncCallback<OrderManager1> callback) throws Exception;

    public void fetchForUpdate(ArrayList<Integer> orderIds, OrderManager1.Load elements[],
                               AsyncCallback<ArrayList<OrderManager1>> callback) throws Exception;

    public void unlock(Integer orderId, OrderManager1.Load elements[],
                       AsyncCallback<OrderManager1> callback) throws Exception;

    public void unlock(ArrayList<Integer> orderIds, OrderManager1.Load elements[],
                       AsyncCallback<ArrayList<OrderManager1>> callback) throws Exception;

    public void duplicate(Integer id, AsyncCallback<OrderManager1> callback) throws Exception;

    public void update(OrderManager1 om, boolean ignoreWarnings,
                       AsyncCallback<OrderManager1> callback) throws Exception;

    public void fetchByDescription(String search, int max,
                                   AsyncCallback<ArrayList<IdNameVO>> callback) throws Exception;

    public void addAuxGroups(OrderManager1 om, ArrayList<Integer> groupIds,
                             AsyncCallback<OrderManager1> callback) throws Exception;

    public void removeAuxGroups(OrderManager1 om, ArrayList<Integer> groupIds,
                                AsyncCallback<OrderManager1> callback) throws Exception;

    public void addTest(OrderManager1 om, Integer id, boolean isTest, Integer index,
                        AsyncCallback<OrderTestReturnVO> callback) throws Exception;

    public void removeTests(OrderManager1 om, ArrayList<Integer> ids, AsyncCallback<OrderManager1> callback) throws Exception;
}