package org.openelis.local;

import javax.ejb.Local;

import org.openelis.manager.OrderManager;

@Local
public interface OrderManagerLocal {    
    public OrderManager duplicate(Integer orderId) throws Exception;
    public void recur(Integer orderId) throws Exception;
}
