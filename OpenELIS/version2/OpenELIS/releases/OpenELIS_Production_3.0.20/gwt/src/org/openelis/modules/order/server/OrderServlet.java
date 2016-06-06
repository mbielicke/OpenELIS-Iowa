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

import org.openelis.bean.IOrderBean;
import org.openelis.bean.IOrderManagerBean;
import org.openelis.bean.IOrderRecurrenceReportBean;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.IOrderRecurrenceDO;
import org.openelis.domain.IOrderViewDO;
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.server.RemoteServlet;
import org.openelis.manager.IOrderContainerManager;
import org.openelis.manager.IOrderFillManager;
import org.openelis.manager.IOrderItemManager;
import org.openelis.manager.IOrderManager;
import org.openelis.manager.IOrderOrganizationManager;
import org.openelis.manager.IOrderReceiptManager;
import org.openelis.manager.IOrderTestAnalyteManager;
import org.openelis.manager.IOrderTestManager;
import org.openelis.modules.order.client.OrderServiceInt;

@WebServlet("/openelis/order")
public class OrderServlet extends RemoteServlet implements OrderServiceInt {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    IOrderManagerBean          orderManager;
    
    @EJB
    IOrderBean                 order;
    
    @EJB
    IOrderRecurrenceReportBean orderRecurrenceReport;
    

    public IOrderManager fetchById(Integer id) throws Exception {
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
    
    public IOrderManager fetchByIorderItemId(Integer id) throws Exception {
        try {
            return orderManager.fetchByIorderItemId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public IOrderManager fetchWithOrganizations(Integer id) throws Exception {
        try {
            return orderManager.fetchWithOrganizations(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public IOrderManager fetchWithItems(Integer id) throws Exception {
        try {
            return orderManager.fetchWithItems(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public IOrderManager fetchWithFills(Integer id) throws Exception {
        try {
            return orderManager.fetchWithFills(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public IOrderManager fetchWithNotes(Integer id) throws Exception {
        try {
            return orderManager.fetchWithNotes(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public IOrderManager fetchWithTests(Integer id) throws Exception {
        try {
            return orderManager.fetchWithTests(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public IOrderManager fetchWithContainers(Integer id) throws Exception {
        try {
            return orderManager.fetchWithContainers(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public IOrderManager fetchWithRecurring(Integer id) throws Exception {
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
    
    public ArrayList<IOrderViewDO> queryOrderFill(Query query) throws Exception {
        try {
            return order.queryOrderFill(query.getFields());
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public IOrderManager add(IOrderManager man) throws Exception {
        try {
            return orderManager.add(man);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public IOrderManager update(IOrderManager man) throws Exception {
        try {
            return orderManager.update(man);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public IOrderManager fetchForUpdate(Integer id) throws Exception {
        try {
            return orderManager.fetchForUpdate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public IOrderManager abortUpdate(Integer id) throws Exception {
        try {
            return orderManager.abortUpdate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public IOrderManager duplicate(Integer id) throws Exception {
        try {
            return orderManager.duplicate(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    //
    // support for OrderItemManager, OrderFillManager, OrderTestManager, OrderContainerManager
    //
    
    public IOrderOrganizationManager fetchOrganizationByIorderId(Integer id) throws Exception {
        try {
            return orderManager.fetchOrganizationByIorderId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public IOrderItemManager fetchItemByIorderId(Integer id) throws Exception {
        try {
            return orderManager.fetchItemByIorderId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }

    public IOrderFillManager fetchFillByIorderId(Integer id) throws Exception {
        try {
            return orderManager.fetchFillByIorderId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public IOrderReceiptManager fetchReceiptByIorderId(Integer id) throws Exception {
        try {
            return orderManager.fetchReceiptByIorderId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public IOrderTestManager fetchTestByIorderId(Integer id) throws Exception {
        try {
            return orderManager.fetchTestByIorderId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public IOrderTestAnalyteManager fetchTestAnalyteByIorderTestId(Integer id) throws Exception {
        try {
            return orderManager.fetchTestAnalyteByIorderTestId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public IOrderTestAnalyteManager fetchMergedTestAnalyteByIorderTestId(Integer id) throws Exception {
        try {
            return orderManager.fetchMergedTestAnalyteByIorderTestId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public IOrderTestAnalyteManager fetchTestAnalyteByTestId(Integer id) throws Exception {
        try {
            return orderManager.fetchTestAnalyteByTestId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public IOrderContainerManager fetchContainerByOrderId(Integer id) throws Exception {
        try {
            return orderManager.fetchContainerByIorderId(id);
        } catch (Exception anyE) {
            throw serializeForGWT(anyE);
        }
    }
    
    public IOrderRecurrenceDO fetchRecurrenceByIorderId(Integer id) throws Exception {
        try {
            return orderManager.fetchRecurrenceByIorderId(id);
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
