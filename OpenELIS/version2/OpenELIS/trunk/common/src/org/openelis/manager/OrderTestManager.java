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
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;

public class OrderTestManager implements RPC {
    
    private static final long serialVersionUID = 1L;
    
    protected Integer                       orderId;
    protected ArrayList<OrderTestListItem>  items, deleted;

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
        return items.get(i).test;
    }
    
    public void setTestAt(OrderTestViewDO test, int i) {
        if(items == null)
            items = new ArrayList<OrderTestListItem>();
        items.get(i).test = test;        
    }
    
    public void addTest() {
        OrderTestListItem item;
        
        if(items == null)
            items = new ArrayList<OrderTestListItem>();
        item = new OrderTestListItem();        
        item.test = new OrderTestViewDO();
        items.add(item);
    }
    
    public void addTest(OrderTestViewDO test) {
        OrderTestListItem item;
        
        if(items == null)
            items = new ArrayList<OrderTestListItem>();
        item = new OrderTestListItem();        
        item.test = test;
        items.add(item);
    }
    
    public void addTestAt(int i) {
        OrderTestListItem item;
        
        if(items == null)
            items = new ArrayList<OrderTestListItem>();
        item = new OrderTestListItem();        
        item.test = new OrderTestViewDO();
        items.add(i, item);
    }
    
    public void removeTestAt(int i) {
        OrderTestListItem tmp;
        
        if(items == null || i >= items.size())
            return;
        
        tmp = items.remove(i);
        if (tmp.test.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<OrderTestListItem>();
            deleted.add(tmp);        
        }
    }
    
    public ArrayList<OrderTestListItem> getTests(){
        return items;
    }
    
    public int count() {
        if (items == null)
            return 0;

        return items.size();
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
    
    public OrderTestAnalyteManager getAnalytesAt(int i) throws Exception {
        OrderTestListItem item;
        
        item = items.get(i);
        if (item.analytes == null) {
            if (item.test != null && item.test.getId() != null) {
                try {
                    item.analytes = OrderTestAnalyteManager.fetchByOrderTestId(item.test.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
        }

        if (item.analytes == null)
            item.analytes = OrderTestAnalyteManager.getInstance();

        return item.analytes;
    }
    
    public OrderTestAnalyteManager getMergedAnalytesAt(int i) throws Exception {
        OrderTestListItem item;

        item = items.get(i);
        if (item.analytes == null) {
            if (item.test != null) {
                if (item.test.getId() != null) {
                    try {
                        item.analytes = OrderTestAnalyteManager.fetchMergedByOrderTestId(item.test.getId());
                    } catch (NotFoundException e) {
                        // ignore
                    } catch (Exception e) {
                        throw e;
                    }
                } else if (item.test.getTestId() != null) {
                    try {
                        item.analytes = OrderTestAnalyteManager.fetchByTestId(item.test.getTestId());
                    } catch (NotFoundException e) {
                        // ignore
                    } catch (Exception e) {
                        throw e;
                    }
                }

            }
        }

        if (item.analytes == null)
            item.analytes = OrderTestAnalyteManager.getInstance();

        return item.analytes;
    }
    
    public void refreshAnalytesByTestAt(int i) throws Exception {
        OrderTestListItem item;
        OrderTestAnalyteManager newMan;
        
        /*
         * done to make sure that the the analytes for the old test are present 
         * before merging them with those of the new one 
         */
        getMergedAnalytesAt(i);
        
        /*
         * if the test id is null then the OrderTestAnalyteManager for this 
         * order test is assigned to an empty one because the analytes need to
         * be preserved for the next time a test id gets set so that these analytes
         * can be included with the ones for the new test and get committed correctly  
         */
        item = items.get(i);
        if (item.test == null || item.test.getTestId() == null) {
            newMan = OrderTestAnalyteManager.getInstance();
        } else {
            /*
             * fetch the analytes for the new test associated with this order
             * test and merge them with the analytes from the previous test
             */
            try {
                newMan = OrderTestAnalyteManager.fetchByTestId(item.test.getTestId());
            } catch (NotFoundException e) {
                newMan = OrderTestAnalyteManager.getInstance();
            } catch (Exception e) {
                throw e;
            }
        }
        newMan.mergeAnalytes(item.analytes);
        item.analytes = newMan;        
    }

    // friendly methods used by managers and proxies
    Integer getOrderId() {
        return orderId;
    }

    void setOrderId(Integer id) {
        orderId = id;
    }
    
    void setTests(ArrayList<OrderTestListItem> tests) {
        this.items = tests;
    }
    
    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }
    
    OrderTestListItem getDeletedAt(int i) {
        return deleted.get(i);
    }
    
    private static OrderTestManagerProxy proxy() {
        if (proxy == null)
            proxy = new OrderTestManagerProxy();
        return proxy;
    }
    
    static class OrderTestListItem implements RPC {
        private static final long serialVersionUID = 1L;
        
        OrderTestViewDO         test;
        OrderTestAnalyteManager analytes;
    }
}
