package org.openelis.remote;

import javax.ejb.Remote;

import org.openelis.manager.OrderItemManager;
import org.openelis.manager.OrderManager;

@Remote
public interface OrderManagerRemote {

    public OrderManager fetchById(Integer id) throws Exception;

    public OrderManager fetchWithItems(Integer id) throws Exception;

    public OrderManager fetchWithReceipts(Integer id) throws Exception;

    public OrderManager fetchWithNotes(Integer id) throws Exception;

    public OrderManager add(OrderManager man) throws Exception;

    public OrderManager update(OrderManager man) throws Exception;

    public OrderManager fetchForUpdate(Integer id) throws Exception;

    public OrderManager abortUpdate(Integer id) throws Exception;
    
    public OrderItemManager fetchItemByOrderId(Integer id) throws Exception;

}
