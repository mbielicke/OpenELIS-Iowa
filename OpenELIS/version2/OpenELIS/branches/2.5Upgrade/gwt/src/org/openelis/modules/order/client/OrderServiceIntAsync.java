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

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface OrderServiceIntAsync {

    void abortUpdate(Integer id, AsyncCallback<OrderManager> callback);

    void add(OrderManager man, AsyncCallback<OrderManager> callback);

    void duplicate(Integer id, AsyncCallback<OrderManager> callback);

    void fetchByDescription(String search, AsyncCallback<ArrayList<IdNameVO>> callback);

    void fetchById(Integer id, AsyncCallback<OrderManager> callback);

    void fetchByShippingItemId(Integer id, AsyncCallback<OrderViewDO> callback);

    void fetchContainerByOrderId(Integer id, AsyncCallback<OrderContainerManager> callback);

    void fetchFillByOrderId(Integer id, AsyncCallback<OrderFillManager> callback);

    void fetchForUpdate(Integer id, AsyncCallback<OrderManager> callback);

    void fetchItemByOrderId(Integer id, AsyncCallback<OrderItemManager> callback);

    void fetchOrganizationByOrderId(Integer id, AsyncCallback<OrderOrganizationManager> callback);

    void fetchReceiptByOrderId(Integer id, AsyncCallback<OrderReceiptManager> callback);

    void fetchRecurrenceByOrderId(Integer id, AsyncCallback<OrderRecurrenceDO> callback);

    void fetchTestAnalyteByOrderTestId(Integer id, AsyncCallback<OrderTestAnalyteManager> callback);

    void fetchTestAnalyteByTestId(Integer id, AsyncCallback<OrderTestAnalyteManager> callback);

    void fetchTestByOrderId(Integer id, AsyncCallback<OrderTestManager> callback);

    void fetchWithContainers(Integer id, AsyncCallback<OrderManager> callback);

    void fetchWithFills(Integer id, AsyncCallback<OrderManager> callback);

    void fetchWithItems(Integer id, AsyncCallback<OrderManager> callback);

    void fetchWithNotes(Integer id, AsyncCallback<OrderManager> callback);

    void fetchWithOrganizations(Integer id, AsyncCallback<OrderManager> callback);

    void fetchWithRecurring(Integer id, AsyncCallback<OrderManager> callback);

    void fetchWithTests(Integer id, AsyncCallback<OrderManager> callback);

    void getPrompts(AsyncCallback<ArrayList<Prompt>> callback);

    void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback);

    void queryOrderFill(Query query, AsyncCallback<ArrayList<OrderViewDO>> callback);

    void recurOrders(AsyncCallback<Void> callback);

    void update(OrderManager man, AsyncCallback<OrderManager> callback);

    void fetchMergedTestAnalyteByOrderTestId(Integer id,
                                             AsyncCallback<OrderTestAnalyteManager> callback);

}
