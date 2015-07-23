package org.openelis.modules.order.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.IOrderRecurrenceDO;
import org.openelis.domain.IOrderViewDO;
import org.openelis.manager.IOrderContainerManager;
import org.openelis.manager.IOrderFillManager;
import org.openelis.manager.IOrderItemManager;
import org.openelis.manager.IOrderManager;
import org.openelis.manager.IOrderOrganizationManager;
import org.openelis.manager.IOrderReceiptManager;
import org.openelis.manager.IOrderTestAnalyteManager;
import org.openelis.manager.IOrderTestManager;
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.data.Query;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

@RemoteServiceRelativePath("order")
public interface OrderServiceInt extends XsrfProtectedService {

    IOrderManager fetchById(Integer id) throws Exception;

    ArrayList<IdNameVO> fetchByDescription(String search) throws Exception;

    IOrderManager fetchByIorderItemId(Integer id) throws Exception;

    IOrderManager fetchWithOrganizations(Integer id) throws Exception;

    IOrderManager fetchWithItems(Integer id) throws Exception;

    IOrderManager fetchWithFills(Integer id) throws Exception;

    IOrderManager fetchWithNotes(Integer id) throws Exception;

    IOrderManager fetchWithTests(Integer id) throws Exception;

    IOrderManager fetchWithContainers(Integer id) throws Exception;

    IOrderManager fetchWithRecurring(Integer id) throws Exception;

    ArrayList<IdNameVO> query(Query query) throws Exception;

    ArrayList<IOrderViewDO> queryOrderFill(Query query) throws Exception;

    IOrderManager add(IOrderManager man) throws Exception;

    IOrderManager update(IOrderManager man) throws Exception;

    IOrderManager fetchForUpdate(Integer id) throws Exception;

    IOrderManager abortUpdate(Integer id) throws Exception;

    IOrderManager duplicate(Integer id) throws Exception;

    IOrderOrganizationManager fetchOrganizationByIorderId(Integer id) throws Exception;

    IOrderItemManager fetchItemByIorderId(Integer id) throws Exception;

    IOrderFillManager fetchFillByIorderId(Integer id) throws Exception;

    IOrderReceiptManager fetchReceiptByIorderId(Integer id) throws Exception;

    IOrderTestManager fetchTestByIorderId(Integer id) throws Exception;

    IOrderTestAnalyteManager fetchTestAnalyteByIorderTestId(Integer id) throws Exception;

    IOrderTestAnalyteManager fetchTestAnalyteByTestId(Integer id) throws Exception;
    
    IOrderTestAnalyteManager fetchMergedTestAnalyteByIorderTestId(Integer id) throws Exception;

    IOrderContainerManager fetchContainerByOrderId(Integer id) throws Exception;

    IOrderRecurrenceDO fetchRecurrenceByIorderId(Integer id) throws Exception;

    ArrayList<Prompt> getPrompts() throws Exception;

}