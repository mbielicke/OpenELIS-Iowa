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
package org.openelis.modules.order1.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.OrderBean;
import org.openelis.bean.OrderManager1Bean;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrderTestReturnVO;
import org.openelis.gwt.server.RemoteServlet;
import org.openelis.manager.OrderManager1;
import org.openelis.manager.OrderManager1.Load;
import org.openelis.modules.order1.client.OrderServiceInt1;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;

@WebServlet("/openelis/order1")
public class OrderServlet1 extends RemoteServlet implements OrderServiceInt1 {

    private static final long serialVersionUID = 1L;

    @EJB
    private OrderManager1Bean orderManager1;

    @EJB
    private OrderBean         order;

    @Override
    public OrderManager1 getInstance(String type) throws Exception {
        return orderManager1.getInstance(type);
    }

    @Override
    public OrderManager1 fetchById(Integer orderId, Load... elements) throws Exception {
        return orderManager1.fetchById(orderId, elements);
    }

    @Override
    public ArrayList<OrderManager1> fetchByIds(ArrayList<Integer> orderIds, Load... elements) throws Exception {
        return orderManager1.fetchByIds(orderIds, elements);
    }

    @Override
    public ArrayList<OrderManager1> fetchByQuery(ArrayList<QueryData> fields, int first, int max,
                                                 Load... elements) throws Exception {
        return orderManager1.fetchByQuery(fields, first, max, elements);
    }

    @Override
    public OrderManager1 fetchWith(OrderManager1 om, Load... elements) throws Exception {
        return orderManager1.fetchWith(om, elements);
    }

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        return order.query(query.getFields(),
                           query.getPage() * query.getRowsPerPage(),
                           query.getRowsPerPage());
    }

    @Override
    public OrderManager1 fetchForUpdate(Integer orderId) throws Exception {
        return orderManager1.fetchForUpdate(orderId);
    }

    @Override
    public OrderManager1 fetchForUpdate(Integer orderId, Load... elements) throws Exception {
        return orderManager1.fetchForUpdate(orderId, elements);
    }

    @Override
    public ArrayList<OrderManager1> fetchForUpdate(ArrayList<Integer> orderIds, Load... elements) throws Exception {
        return orderManager1.fetchForUpdate(orderIds, elements);
    }

    @Override
    public OrderManager1 unlock(Integer orderId, Load... elements) throws Exception {
        return orderManager1.unlock(orderId, elements);
    }

    @Override
    public ArrayList<OrderManager1> unlock(ArrayList<Integer> orderIds, Load... elements) throws Exception {
        return orderManager1.unlock(orderIds, elements);
    }

    public OrderManager1 duplicate(Integer id) throws Exception {
        return orderManager1.duplicate(id);
    }

    @Override
    public OrderManager1 update(OrderManager1 om, boolean ignoreWarnings) throws Exception {
        return orderManager1.update(om, ignoreWarnings);
    }

    @Override
    public ArrayList<IdNameVO> fetchByDescription(String search, int max) throws Exception {
        return order.fetchByDescription(search, max);
    }

    public OrderManager1 addAuxGroups(OrderManager1 om, ArrayList<Integer> groupIds) throws Exception {
        return orderManager1.addAuxGroups(om, groupIds);
    }

    public OrderManager1 removeAuxGroups(OrderManager1 om, ArrayList<Integer> groupIds) throws Exception {
        return orderManager1.removeAuxGroups(om, groupIds);
    }

    @Override
    public OrderTestReturnVO addTest(OrderManager1 om, Integer id, boolean isTest, Integer index) throws Exception {
        return orderManager1.addTest(om, id, isTest, index);
    }

    @Override
    public OrderManager1 removeTests(OrderManager1 om, ArrayList<Integer> ids) throws Exception {
        return orderManager1.removeTests(om, ids);
    }
}