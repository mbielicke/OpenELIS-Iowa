package org.openelis.local;

import javax.ejb.Local;

import org.openelis.manager.OrderManager;

@Local
public interface OrderManagerLocal {    
    public OrderManager duplicate(Integer id) throws Exception;
    public OrderManager duplicateForRecurrence(Integer id) throws Exception;
}
