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

import java.io.Serializable;
import java.util.ArrayList;

import org.openelis.domain.IOrderTestAnalyteViewDO;
import org.openelis.domain.IOrderTestViewDO;
import org.openelis.ui.common.NotFoundException;

public class IOrderTestManager implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    protected Integer                       iorderId;
    protected ArrayList<IOrderTestListItem>  items, deleted;

    protected transient static IOrderTestManagerProxy proxy;
    
    protected IOrderTestManager() {        
    }
    
    /**
     * Creates a new instance of this object.
     */
    public static IOrderTestManager getInstance() {
        return new IOrderTestManager();
    }
    
    public IOrderTestViewDO getTestAt(int i) {
        return items.get(i).test;
    }
    
    public void setTestAt(IOrderTestViewDO test, int i) {
        if(items == null)
            items = new ArrayList<IOrderTestListItem>();
        items.get(i).test = test;        
    }
    
    public void addTest() {
        IOrderTestListItem item;
        
        if(items == null)
            items = new ArrayList<IOrderTestListItem>();
        item = new IOrderTestListItem();        
        item.test = new IOrderTestViewDO();
        items.add(item);
    }
    
    public void addTest(IOrderTestViewDO test) {
        IOrderTestListItem item;
        
        if(items == null)
            items = new ArrayList<IOrderTestListItem>();
        item = new IOrderTestListItem();        
        item.test = test;
        items.add(item);
    }
    
    public void addTestAt(int i) {
        IOrderTestListItem item;
        
        if(items == null)
            items = new ArrayList<IOrderTestListItem>();
        item = new IOrderTestListItem();        
        item.test = new IOrderTestViewDO();
        items.add(i, item);
    }
    
    public void removeTestAt(int i) {
        IOrderTestListItem tmp;
        
        if(items == null || i >= items.size())
            return;
        
        tmp = items.remove(i);
        if (tmp.test.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<IOrderTestListItem>();
            deleted.add(tmp);        
        }
    }
    
    public void removeNotReportableAnalytesAt(int i) { 
        IOrderTestAnalyteManager aman;
        IOrderTestAnalyteViewDO ana;
        
        if(items == null || i >= items.size())
            return;
        
        aman = items.get(i).analytes;
        if (aman == null)
            return;
        
        for (int j = 0; j < aman.count(); j++) {
            ana = aman.getAnalyteAt(j);
            if ("N".equals(ana.getTestAnalyteIsReportable()))
                aman.removeAnalyteAt(j--);
        }
    }
    
    public int count() {
        if (items == null)
            return 0;

        return items.size();
    }
    
    // service methods
    public static IOrderTestManager fetchByIorderId(Integer id) throws Exception {
        return proxy().fetchByIorderId(id);
    }
    
    public IOrderTestManager add() throws Exception {
        return proxy().add(this);
    }        
    
    public IOrderTestManager update() throws Exception {
        return proxy().update(this);
    } 
    
    public void validate() throws Exception {
        proxy().validate(this);
    }
    
    public IOrderTestAnalyteManager getAnalytesAt(int i) throws Exception {
        IOrderTestListItem item;
        
        item = items.get(i);
        if (item.analytes == null) {
            if (item.test != null && item.test.getId() != null) {
                try {
                    item.analytes = IOrderTestAnalyteManager.fetchByIorderTestId(item.test.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
        }

        if (item.analytes == null)
            item.analytes = IOrderTestAnalyteManager.getInstance();

        return item.analytes;
    }
    
    public IOrderTestAnalyteManager getMergedAnalytesAt(int i) throws Exception {
        IOrderTestListItem item;

        item = items.get(i);
        if (item.analytes == null) {
            if (item.test != null) {
                if (item.test.getId() != null) {
                    try {
                        item.analytes = IOrderTestAnalyteManager.fetchMergedByIorderTestId(item.test.getId());
                    } catch (NotFoundException e) {
                        // ignore
                    } catch (Exception e) {
                        throw e;
                    }
                } else if (item.test.getTestId() != null) {
                    try {
                        item.analytes = IOrderTestAnalyteManager.fetchByTestId(item.test.getTestId());
                    } catch (NotFoundException e) {
                        // ignore
                    } catch (Exception e) {
                        throw e;
                    }
                }

            }
        }

        if (item.analytes == null)
            item.analytes = IOrderTestAnalyteManager.getInstance();

        return item.analytes;
    }
    
    public void refreshAnalytesByTestAt(int i) throws Exception {
        IOrderTestListItem item;
        IOrderTestAnalyteManager newMan;
        
        /*
         * done to make sure that the analytes for the old test are present before
         * merging them with those of the new one 
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
            newMan = IOrderTestAnalyteManager.getInstance();
        } else {
            /*
             * fetch the analytes for the new test associated with this order
             * test and merge them with the analytes from the previous test
             */
            try {
                newMan = IOrderTestAnalyteManager.fetchByTestId(item.test.getTestId());
            } catch (NotFoundException e) {
                newMan = IOrderTestAnalyteManager.getInstance();
            } catch (Exception e) {
                throw e;
            }
        }
        newMan.mergeAnalytes(item.analytes);
        item.analytes = newMan;        
    }

    // friendly methods used by managers and proxies
    Integer getIorderId() {
        return iorderId;
    }

    void setIorderId(Integer id) {
        iorderId = id;
    }
    
    void setTests(ArrayList<IOrderTestListItem> tests) {
        this.items = tests;
    }
    
    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }
    
    IOrderTestListItem getDeletedAt(int i) {
        return deleted.get(i);
    }
    
    private static IOrderTestManagerProxy proxy() {
        if (proxy == null)
            proxy = new IOrderTestManagerProxy();
        return proxy;
    }
    
    static class IOrderTestListItem implements Serializable {
        private static final long serialVersionUID = 1L;
        
        IOrderTestViewDO         test;
        IOrderTestAnalyteManager analytes;
    }
}
