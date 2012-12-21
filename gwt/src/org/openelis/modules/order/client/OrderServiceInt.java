package org.openelis.modules.order.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrderRecurrenceDO;
import org.openelis.domain.OrderViewDO;
import org.openelis.gwt.common.Prompt;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.OrderContainerManager;
import org.openelis.manager.OrderFillManager;
import org.openelis.manager.OrderItemManager;
import org.openelis.manager.OrderManager;
import org.openelis.manager.OrderOrganizationManager;
import org.openelis.manager.OrderReceiptManager;
import org.openelis.manager.OrderTestAnalyteManager;
import org.openelis.manager.OrderTestManager;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("order")
public interface OrderServiceInt extends RemoteService {

    OrderManager fetchById(Integer id) throws Exception;

    ArrayList<IdNameVO> fetchByDescription(String search) throws Exception;

    OrderViewDO fetchByShippingItemId(Integer id) throws Exception;

    OrderManager fetchWithOrganizations(Integer id) throws Exception;

    OrderManager fetchWithItems(Integer id) throws Exception;

    OrderManager fetchWithFills(Integer id) throws Exception;

    OrderManager fetchWithNotes(Integer id) throws Exception;

    OrderManager fetchWithTests(Integer id) throws Exception;

    OrderManager fetchWithContainers(Integer id) throws Exception;

    OrderManager fetchWithRecurring(Integer id) throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    ArrayList<OrderViewDO> queryOrderFill(Query query) throws Exception;

    OrderManager add(OrderManager man) throws Exception;

    OrderManager update(OrderManager man) throws Exception;

    OrderManager fetchForUpdate(Integer id) throws Exception;

    OrderManager abortUpdate(Integer id) throws Exception;

    OrderManager duplicate(Integer id) throws Exception;

    OrderOrganizationManager fetchOrganizationByOrderId(Integer id) throws Exception;

    OrderItemManager fetchItemByOrderId(Integer id) throws Exception;

    OrderFillManager fetchFillByOrderId(Integer id) throws Exception;

    OrderReceiptManager fetchReceiptByOrderId(Integer id) throws Exception;

    OrderTestManager fetchTestByOrderId(Integer id) throws Exception;

    OrderTestAnalyteManager fetchTestAnalyteByOrderTestId(Integer id) throws Exception;

    OrderTestAnalyteManager fetchTestAnalyteByTestId(Integer id) throws Exception;
    
    OrderTestAnalyteManager fetchMergedTestAnalyteByOrderTestId(Integer id) throws Exception;

    OrderContainerManager fetchContainerByOrderId(Integer id) throws Exception;

    OrderRecurrenceDO fetchRecurrenceByOrderId(Integer id) throws Exception;

    ArrayList<Prompt> getPrompts() throws Exception;

    void recurOrders() throws Exception;

}