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
package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.domain.OrderContainerDO;
import org.openelis.gwt.common.RPC;

public class OrderContainerManager implements RPC {

    private static final long                             serialVersionUID = 1L;

    protected Integer                                     orderId;
    protected ArrayList<OrderContainerDO>                 containers, deleted;

    protected transient static OrderContainerManagerProxy proxy;

    protected OrderContainerManager() {
    }

    /**
     * Creates a new instance of this object.
     */
    public static OrderContainerManager getInstance() {
        return new OrderContainerManager();
    }

    public OrderContainerDO getContainerAt(int i) {
        return containers.get(i);
    }

    public void setContainerAt(OrderContainerDO container, int i) {
        if (containers == null)
            containers = new ArrayList<OrderContainerDO>();
        containers.set(i, container);
    }

    public int addContainer() {
        if (containers == null)
            containers = new ArrayList<OrderContainerDO>();
        containers.add(new OrderContainerDO());   
        
        return count() - 1;
    }
    
    public int addContainerAt(int i) {
        if (containers == null)
            containers = new ArrayList<OrderContainerDO>();
        containers.add(i, new OrderContainerDO()); 
        
        return count() - 1;
    }
    
    public int addContainer(OrderContainerDO container) {
        if (containers == null)
            containers = new ArrayList<OrderContainerDO>();
        containers.add(container);   
        
        return count() - 1;
    }

    public void removeContainerAt(int i) {
        OrderContainerDO tmp;

        if (containers == null || i >= containers.size())
            return;

        tmp = containers.remove(i);
        if (tmp.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<OrderContainerDO>();
            deleted.add(tmp);
        }
    }

    public int count() {
        if (containers == null)
            return 0;

        return containers.size();
    }

    // service methods
    public static OrderContainerManager fetchByOrderId(Integer id) throws Exception {
        return proxy().fetchByOrderId(id);
    }

    public OrderContainerManager add() throws Exception {
        return proxy().add(this);
    }

    public OrderContainerManager update() throws Exception {
        return proxy().update(this);
    }

    public void validate() throws Exception {
        proxy().validate(this);
    }

    // friendly methods used by managers and proxies
    Integer getOrderId() {
        return orderId;
    }

    void setOrderId(Integer id) {
        orderId = id;
    }

    ArrayList<OrderContainerDO> getContainers() {
        return containers;
    }

    void setContainers(ArrayList<OrderContainerDO> containers) {
        this.containers = containers;
    }

    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }

    OrderContainerDO getDeletedAt(int i) {
        return deleted.get(i);
    }

    private static OrderContainerManagerProxy proxy() {
        if (proxy == null)
            proxy = new OrderContainerManagerProxy();
        return proxy;
    }

}
