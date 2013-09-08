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
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;
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
        try {
            return orderManager.fetchById(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ArrayList<IdNameVO> fetchByDescription(String search) throws Exception {
        try {
            return order.fetchByDescription(search + "%", 10);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public OrderManager fetchByOrderItemId(Integer id) throws Exception {
        try {
            return orderManager.fetchByOrderItemId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public OrderManager fetchWithOrganizations(Integer id) throws Exception {
        try {
            return orderManager.fetchWithOrganizations(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public OrderManager fetchWithItems(Integer id) throws Exception {
        try {
            return orderManager.fetchWithItems(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public OrderManager fetchWithFills(Integer id) throws Exception {
        try {
            return orderManager.fetchWithFills(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public OrderManager fetchWithNotes(Integer id) throws Exception {
        try {
            return orderManager.fetchWithNotes(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public OrderManager fetchWithTests(Integer id) throws Exception {
        try {
            return orderManager.fetchWithTests(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public OrderManager fetchWithContainers(Integer id) throws Exception {
        try {
            return orderManager.fetchWithContainers(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public OrderManager fetchWithRecurring(Integer id) throws Exception {
        try {
            return orderManager.fetchWithRecurring(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }  

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        try {
            return order.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ArrayList<OrderViewDO> queryOrderFill(Query query) throws Exception {
        try {
            return order.queryOrderFill(query.getFields());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public OrderManager add(OrderManager man) throws Exception {
        try {
            return orderManager.add(man);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public OrderManager update(OrderManager man) throws Exception {
        try {
            return orderManager.update(man);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public OrderManager fetchForUpdate(Integer id) throws Exception {
        try {
            return orderManager.fetchForUpdate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public OrderManager abortUpdate(Integer id) throws Exception {
        try {
            return orderManager.abortUpdate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public OrderManager duplicate(Integer id) throws Exception {
        try {
            return orderManager.duplicate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    //
    // support for OrderItemManager, OrderFillManager, OrderTestManager, OrderContainerManager
    //
    
    public OrderOrganizationManager fetchOrganizationByOrderId(Integer id) throws Exception {
        try {
            return orderManager.fetchOrganizationByOrderId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public OrderItemManager fetchItemByOrderId(Integer id) throws Exception {
        try {
            return orderManager.fetchItemByOrderId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public OrderFillManager fetchFillByOrderId(Integer id) throws Exception {
        try {
            return orderManager.fetchFillByOrderId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public OrderReceiptManager fetchReceiptByOrderId(Integer id) throws Exception {
        try {
            return orderManager.fetchReceiptByOrderId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public OrderTestManager fetchTestByOrderId(Integer id) throws Exception {
        try {
            return orderManager.fetchTestByOrderId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public OrderTestAnalyteManager fetchTestAnalyteByOrderTestId(Integer id) throws Exception {
        try {
            return orderManager.fetchTestAnalyteByOrderTestId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public OrderTestAnalyteManager fetchMergedTestAnalyteByOrderTestId(Integer id) throws Exception {
        try {
            return orderManager.fetchMergedTestAnalyteByOrderTestId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public OrderTestAnalyteManager fetchTestAnalyteByTestId(Integer id) throws Exception {
        try {
            return orderManager.fetchTestAnalyteByTestId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public OrderContainerManager fetchContainerByOrderId(Integer id) throws Exception {
        try {
            return orderManager.fetchContainerByOrderId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public OrderRecurrenceDO fetchRecurrenceByOrderId(Integer id) throws Exception {
        try {
            return orderManager.fetchRecurrenceByOrderId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public ArrayList<Prompt> getPrompts() throws Exception {
        try {
            return orderRecurrenceReport.getPrompts();
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
}
