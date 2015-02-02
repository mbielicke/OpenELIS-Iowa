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
import org.openelis.manager.OrderManager1;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

/**
 * ScreenServiceInt is a GWT RemoteService interface for the Screen Widget. GWT
 * RemoteServiceServlets that want to provide server side logic for Screens must
 * implement this interface.
 */
@RemoteServiceRelativePath("order1")
public interface OrderServiceInt1 extends XsrfProtectedService {

    public OrderManager1 getInstance(String type) throws Exception;

    public OrderManager1 fetchById(Integer orderId, OrderManager1.Load... elements) throws Exception;

    public ArrayList<OrderManager1> fetchByIds(ArrayList<Integer> orderIds,
                                               OrderManager1.Load... elements) throws Exception;

    public ArrayList<OrderManager1> fetchByQuery(ArrayList<QueryData> fields, int first, int max,
                                                 OrderManager1.Load... elements) throws Exception;

    public OrderManager1 fetchWith(OrderManager1 om, OrderManager1.Load... elements) throws Exception;

    public ArrayList<IdNameVO> query(Query query) throws Exception;

    public OrderManager1 fetchForUpdate(Integer orderId) throws Exception;

    public OrderManager1 fetchForUpdate(Integer orderId, OrderManager1.Load... elements) throws Exception;

    public ArrayList<OrderManager1> fetchForUpdate(ArrayList<Integer> orderIds,
                                                   OrderManager1.Load... elements) throws Exception;

    public OrderManager1 unlock(Integer orderId, OrderManager1.Load... elements) throws Exception;

    public ArrayList<OrderManager1> unlock(ArrayList<Integer> orderIds,
                                           OrderManager1.Load... elements) throws Exception;

    public OrderReturnVO duplicate(Integer id) throws Exception;

    public OrderManager1 update(OrderManager1 om, boolean ignoreWarnings) throws Exception;

    public ArrayList<IdNameVO> fetchByDescription(String search, int max) throws Exception;

    public OrderReturnVO addAuxGroups(OrderManager1 om, ArrayList<Integer> groupIds) throws Exception;

    public OrderManager1 removeAuxGroups(OrderManager1 om, ArrayList<Integer> groupIds) throws Exception;

    public OrderReturnVO addTest(OrderManager1 om, Integer id, boolean isTest, Integer index) throws Exception;

    public OrderManager1 removeTests(OrderManager1 om, ArrayList<Integer> ids) throws Exception;

}