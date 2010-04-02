/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.domain.OrderTestViewDO;
import org.openelis.gwt.common.RPC;

public class OrderTestManager implements RPC {
    
    private static final long serialVersionUID = 1L;
    
    protected Integer         orderId;
    protected ArrayList<OrderTestViewDO> tests, deleted;
    
    protected transient static OrderTestManagerProxy proxy;
    
    protected OrderTestManager() {        
    }
    
    /**
     * Creates a new instance of this object.
     */
    public static OrderTestManager getInstance() {
        return new OrderTestManager();
    }
    
    public OrderTestViewDO getTestAt(int i) {
        return tests.get(i);
    }
    
    public void setTestAt(OrderTestViewDO test, int i) {
        if(tests == null)
            tests = new ArrayList<OrderTestViewDO>();
        tests.set(i, test);        
    }
    
    public void addTest() {
        if(tests == null)
            tests = new ArrayList<OrderTestViewDO>();
        tests.add(new OrderTestViewDO());        
    }
    
    public void addTestAt(int i) {
        if(tests == null)
            tests = new ArrayList<OrderTestViewDO>();
        tests.add(i, new OrderTestViewDO());        
    }
    
    public void removeTestAt(int i) {
        OrderTestViewDO tmp;
        
        if(tests == null || i >= tests.size())
            return;
        
        tmp = tests.remove(i);
        if (tmp.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<OrderTestViewDO>();
            deleted.add(tmp);        
        }
    }
    
    public int count() {
        if (tests == null)
            return 0;

        return tests.size();
    }
    
    // service methods
    public static OrderTestManager fetchByOrderId(Integer id) throws Exception {
        return proxy().fetchByOrderId(id);
    }
    
    public OrderTestManager add() throws Exception {
        return proxy().add(this);
    }        
    
    public OrderTestManager update() throws Exception {
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
    
    ArrayList<OrderTestViewDO> getTests(){
        return tests;
    }
    
    void setTests(ArrayList<OrderTestViewDO> tests) {
        this.tests = tests;
    }
    
    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }
    
    OrderTestViewDO getDeletedAt(int i) {
        return deleted.get(i);
    }
    
    private static OrderTestManagerProxy proxy() {
        if (proxy == null)
            proxy = new OrderTestManagerProxy();
        return proxy;
    }
}
