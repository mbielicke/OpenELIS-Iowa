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
package org.openelis.modules.order.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.OrderBean;
import org.openelis.bean.OrderManagerBean;
import org.openelis.bean.OrderRecurrenceReportBean;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrderRecurrenceDO;
import org.openelis.domain.OrderViewDO;
import org.openelis.gwt.common.Prompt;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.server.RemoteServlet;
import org.openelis.manager.OrderContainerManager;
import org.openelis.manager.OrderFillManager;
import org.openelis.manager.OrderItemManager;
import org.openelis.manager.OrderManager;
import org.openelis.manager.OrderOrganizationManager;
import org.openelis.manager.OrderReceiptManager;
import org.openelis.manager.OrderTestAnalyteManager;
import org.openelis.manager.OrderTestManager;
import org.openelis.modules.order.client.OrderServiceInt;

@WebServlet("/openelis/order")
public class OrderServlet extends RemoteServlet implements OrderServiceInt {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    OrderManagerBean          orderManager;
    
    @EJB
    OrderBean                 order;
    
    @EJB
    OrderRecurrenceReportBean orderRecurrenceReport;
    

    public OrderManager fetchById(Integer id) throws Exception {
        return orderManager.fetchById(id);
    }
    
    public ArrayList<IdNameVO> fetchByDescription(String search) throws Exception {
        return order.fetchByDescription(search + "%", 10);
    }
    
    public OrderViewDO fetchByShippingItemId(Integer id) throws Exception {
        return order.fetchByShippingItemId(id);
    }
    
    public OrderManager fetchWithOrganizations(Integer id) throws Exception {
        return orderManager.fetchWithOrganizations(id);
    }

    public OrderManager fetchWithItems(Integer id) throws Exception {
        return orderManager.fetchWithItems(id);
    }

    public OrderManager fetchWithFills(Integer id) throws Exception {
        return orderManager.fetchWithFills(id);
    }

    public OrderManager fetchWithNotes(Integer id) throws Exception {
        return orderManager.fetchWithNotes(id);
    }
    
    public OrderManager fetchWithTests(Integer id) throws Exception {
        return orderManager.fetchWithTests(id);
    }
    
    public OrderManager fetchWithContainers(Integer id) throws Exception {
        return orderManager.fetchWithContainers(id);
    }
    
    public OrderManager fetchWithRecurring(Integer id) throws Exception {
        return orderManager.fetchWithRecurring(id);
    }  

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        return order.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
    }
    
    public ArrayList<OrderViewDO> queryOrderFill(Query query) throws Exception {
        return order.queryOrderFill(query.getFields());
    }

    public OrderManager add(OrderManager man) throws Exception {
        return orderManager.add(man);
    }

    public OrderManager update(OrderManager man) throws Exception {
        return orderManager.update(man);
    }

    public OrderManager fetchForUpdate(Integer id) throws Exception {
        return orderManager.fetchForUpdate(id);
    }

    public OrderManager abortUpdate(Integer id) throws Exception {
        return orderManager.abortUpdate(id);
    }
    
    public OrderManager duplicate(Integer id) throws Exception {
        return orderManager.duplicate(id);
    }

    //
    // support for OrderItemManager, OrderFillManager, OrderTestManager, OrderContainerManager
    //
    
    public OrderOrganizationManager fetchOrganizationByOrderId(Integer id) throws Exception {
        return orderManager.fetchOrganizationByOrderId(id);
    }
    
    public OrderItemManager fetchItemByOrderId(Integer id) throws Exception {
        return orderManager.fetchItemByOrderId(id);
    }

    public OrderFillManager fetchFillByOrderId(Integer id) throws Exception {
        return orderManager.fetchFillByOrderId(id);
    }
    
    public OrderReceiptManager fetchReceiptByOrderId(Integer id) throws Exception {
        return orderManager.fetchReceiptByOrderId(id);
    }
    
    public OrderTestManager fetchTestByOrderId(Integer id) throws Exception {
        return orderManager.fetchTestByOrderId(id);
    }
    
    public OrderTestAnalyteManager fetchTestAnalyteByOrderTestId(Integer id) throws Exception {
        return orderManager.fetchTestAnalyteByOrderTestId(id);
    }
    
    public OrderTestAnalyteManager fetchMergedTestAnalyteByOrderTestId(Integer id) throws Exception {
        return orderManager.fetchMergedTestAnalyteByOrderTestId(id);
    }
    
    public OrderTestAnalyteManager fetchTestAnalyteByTestId(Integer id) throws Exception {
        return orderManager.fetchTestAnalyteByTestId(id);
    }
    
    public OrderContainerManager fetchContainerByOrderId(Integer id) throws Exception {
        return orderManager.fetchContainerByOrderId(id);
    }
    
    public OrderRecurrenceDO fetchRecurrenceByOrderId(Integer id) throws Exception {
        return orderManager.fetchRecurrenceByOrderId(id);
    }
    
    public ArrayList<Prompt> getPrompts() throws Exception {
        return orderRecurrenceReport.getPrompts();
    }
    
    public void recurOrders() throws Exception {
        orderRecurrenceReport.recurOrders();
    }
}