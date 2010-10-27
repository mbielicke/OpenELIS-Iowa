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

import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrderViewDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.OrderContainerManager;
import org.openelis.manager.OrderFillManager;
import org.openelis.manager.OrderItemManager;
import org.openelis.manager.OrderManager;
import org.openelis.manager.OrderReceiptManager;
import org.openelis.manager.OrderTestManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.OrderManagerRemote;
import org.openelis.remote.OrderRemote;

public class OrderService {
    private static final int rowPP = 20;

    public OrderManager fetchById(Integer id) throws Exception {
        return remoteManager().fetchById(id);
    }
    
    public ArrayList<IdNameVO> fetchByDescription(String search) throws Exception {
        return remote().fetchByDescription(search + "%", 10);
    }
    
    public OrderViewDO fetchByShippingItemId(Integer id) throws Exception {
        return remote().fetchByShippingItemId(id);
    }

    public OrderManager fetchWithItems(Integer id) throws Exception {
        return remoteManager().fetchWithItems(id);
    }

    public OrderManager fetchWithFills(Integer id) throws Exception {
        return remoteManager().fetchWithFills(id);
    }

    public OrderManager fetchWithNotes(Integer id) throws Exception {
        return remoteManager().fetchWithNotes(id);
    }
    
    public OrderManager fetchWithTestsAndContainers(Integer id) throws Exception {
        return remoteManager().fetchWithTestsAndContainers(id);
    }   

    public ArrayList<IdNameVO> query(Query query) throws Exception {
        return remote().query(query.getFields(), query.getPage() * rowPP, rowPP);
    }
    
    public ArrayList<OrderViewDO> queryOrderFill(Query query) throws Exception {
        return remote().queryOrderFill(query.getFields());
    }

    public OrderManager add(OrderManager man) throws Exception {
        return remoteManager().add(man);
    }

    public OrderManager update(OrderManager man) throws Exception {
        return remoteManager().update(man);
    }

    public OrderManager fetchForUpdate(Integer id) throws Exception {
        return remoteManager().fetchForUpdate(id);
    }

    public OrderManager abortUpdate(Integer id) throws Exception {
        return remoteManager().abortUpdate(id);
    }

    //
    // support for OrderItemManager, OrderFillManager, OrderTestManager, OrderContainerManager
    //
    public OrderItemManager fetchItemByOrderId(Integer id) throws Exception {
        return remoteManager().fetchItemByOrderId(id);
    }

    public OrderFillManager fetchFillByOrderId(Integer id) throws Exception {
        return remoteManager().fetchFillByOrderId(id);
    }
    
    public OrderReceiptManager fetchReceiptByOrderId(Integer id) throws Exception {
        return remoteManager().fetchReceiptByOrderId(id);
    }
    
    public OrderTestManager fetchTestByOrderId(Integer id) throws Exception {
        return remoteManager().fetchTestByOrderId(id);
    }
    
    public OrderContainerManager fetchContainerByOrderId(Integer id) throws Exception {
        return remoteManager().fetchContainerByOrderId(id);
    }

    private OrderRemote remote() {
        return (OrderRemote)EJBFactory.lookup("openelis/OrderBean/remote");
    }

    private OrderManagerRemote remoteManager() {
        return (OrderManagerRemote)EJBFactory.lookup("openelis/OrderManagerBean/remote");
    }
}