package org.openelis.modules.order.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.IOrderRecurrenceDO;
import org.openelis.domain.IOrderViewDO;
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.data.Query;
import org.openelis.manager.IOrderContainerManager;
import org.openelis.manager.IOrderFillManager;
import org.openelis.manager.IOrderItemManager;
import org.openelis.manager.IOrderManager;
import org.openelis.manager.IOrderOrganizationManager;
import org.openelis.manager.IOrderReceiptManager;
import org.openelis.manager.IOrderTestAnalyteManager;
import org.openelis.manager.IOrderTestManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface OrderServiceIntAsync {

    void abortUpdate(Integer id, AsyncCallback<IOrderManager> callback);

    void add(IOrderManager man, AsyncCallback<IOrderManager> callback);

    void duplicate(Integer id, AsyncCallback<IOrderManager> callback);

    void fetchByDescription(String search, AsyncCallback<ArrayList<IdNameVO>> callback);

    void fetchById(Integer id, AsyncCallback<IOrderManager> callback);

    void fetchByIorderItemId(Integer id, AsyncCallback<IOrderManager> callback);

    void fetchContainerByOrderId(Integer id, AsyncCallback<IOrderContainerManager> callback);

    void fetchFillByIorderId(Integer id, AsyncCallback<IOrderFillManager> callback);

    void fetchForUpdate(Integer id, AsyncCallback<IOrderManager> callback);

    void fetchItemByIorderId(Integer id, AsyncCallback<IOrderItemManager> callback);

    void fetchOrganizationByIorderId(Integer id, AsyncCallback<IOrderOrganizationManager> callback);

    void fetchReceiptByIorderId(Integer id, AsyncCallback<IOrderReceiptManager> callback);

    void fetchRecurrenceByIorderId(Integer id, AsyncCallback<IOrderRecurrenceDO> callback);

    void fetchTestAnalyteByIorderTestId(Integer id, AsyncCallback<IOrderTestAnalyteManager> callback);

    void fetchTestAnalyteByTestId(Integer id, AsyncCallback<IOrderTestAnalyteManager> callback);

    void fetchTestByIorderId(Integer id, AsyncCallback<IOrderTestManager> callback);

    void fetchWithContainers(Integer id, AsyncCallback<IOrderManager> callback);

    void fetchWithFills(Integer id, AsyncCallback<IOrderManager> callback);

    void fetchWithItems(Integer id, AsyncCallback<IOrderManager> callback);

    void fetchWithNotes(Integer id, AsyncCallback<IOrderManager> callback);

    void fetchWithOrganizations(Integer id, AsyncCallback<IOrderManager> callback);

    void fetchWithRecurring(Integer id, AsyncCallback<IOrderManager> callback);

    void fetchWithTests(Integer id, AsyncCallback<IOrderManager> callback);

    void getPrompts(AsyncCallback<ArrayList<Prompt>> callback);

    void query(Query query, AsyncCallback<ArrayList<IdNameVO>> callback);

    void queryOrderFill(Query query, AsyncCallback<ArrayList<IOrderViewDO>> callback);

    void update(IOrderManager man, AsyncCallback<IOrderManager> callback);

    void fetchMergedTestAnalyteByIorderTestId(Integer id,
                                             AsyncCallback<IOrderTestAnalyteManager> callback);

}
