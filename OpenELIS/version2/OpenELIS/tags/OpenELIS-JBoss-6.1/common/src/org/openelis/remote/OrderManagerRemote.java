package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.domain.OrderRecurrenceDO;
import org.openelis.manager.OrderContainerManager;
import org.openelis.manager.OrderFillManager;
import org.openelis.manager.OrderItemManager;
import org.openelis.manager.OrderManager;
import org.openelis.manager.OrderOrganizationManager;
import org.openelis.manager.OrderReceiptManager;
import org.openelis.manager.OrderTestAnalyteManager;
import org.openelis.manager.OrderTestManager;

@Remote
public interface OrderManagerRemote {

    public OrderManager fetchById(Integer id) throws Exception;
    
    public OrderManager fetchWithOrganizations(Integer id) throws Exception;

    public OrderManager fetchWithItems(Integer id) throws Exception;

    public OrderManager fetchWithFills(Integer id) throws Exception;

    public OrderManager fetchWithNotes(Integer id) throws Exception;
    
    public OrderManager fetchWithTests(Integer id) throws Exception;

    public OrderManager fetchWithContainers(Integer id) throws Exception;

    public OrderManager fetchWithRecurring(Integer id) throws Exception;
    
    public OrderManager duplicate(Integer id) throws Exception;

    public OrderManager add(OrderManager man) throws Exception;

    public OrderManager update(OrderManager man) throws Exception;

    public OrderManager fetchForUpdate(Integer id) throws Exception;

    public OrderManager abortUpdate(Integer id) throws Exception;
    
    public OrderOrganizationManager fetchOrganizationByOrderId(Integer id) throws Exception;
    
    public OrderItemManager fetchItemByOrderId(Integer id) throws Exception;

    public OrderFillManager fetchFillByOrderId(Integer id) throws Exception;
    
    public OrderReceiptManager fetchReceiptByOrderId(Integer id) throws Exception;
    
    public OrderTestManager fetchTestByOrderId(Integer id) throws Exception;
    
    public OrderTestAnalyteManager fetchTestAnalyteByOrderTestId(Integer id) throws Exception;
    
    public OrderTestAnalyteManager fetchMergedTestAnalyteByOrderTestId(Integer id) throws Exception;

    public OrderTestAnalyteManager fetchTestAnalyteByTestId(Integer id) throws Exception;
    
    public OrderContainerManager fetchContainerByOrderId(Integer id) throws Exception;
    
    public OrderRecurrenceDO fetchRecurrenceByOrderId(Integer id) throws Exception;
}