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

import org.openelis.bean.OrderTestAnalyteBean;
import org.openelis.bean.OrderTestBean;
import org.openelis.domain.OrderTestViewDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.manager.OrderTestManager.OrderTestListItem;
import org.openelis.utils.EJBFactory;

public class OrderTestManagerProxy {
    
    public OrderTestManager fetchByOrderId(Integer id) throws Exception {
        OrderTestManager m;
        ArrayList<OrderTestViewDO> tests;
        
        tests = EJBFactory.getOrderTest().fetchByOrderId(id);
        m = OrderTestManager.getInstance();
        m.setOrderId(id);
        
        for (int i = 0; i < tests.size(); i++ )
            m.addTest(tests.get(i));

        return m;
    }
    
    public OrderTestManager add(OrderTestManager man) throws Exception {
        OrderTestBean tl;
        OrderTestViewDO data;
        OrderTestAnalyteManager anaMan;

        tl = EJBFactory.getOrderTest();
        for (int i = 0; i < man.count(); i++ ) {
            data = man.getTestAt(i);
            data.setSortOrder(i+1);
            data.setOrderId(man.getOrderId());
            tl.add(data);
            
            anaMan = man.getAnalytesAt(i);
            anaMan.setOrderTestId(data.getId());
            anaMan.add();
        }

        return man;
    }

    public OrderTestManager update(OrderTestManager man) throws Exception {
        OrderTestBean tl;
        OrderTestAnalyteBean al;
        OrderTestViewDO data;
        OrderTestAnalyteManager anaMan;
        OrderTestListItem item;
        
        tl = EJBFactory.getOrderTest();
        al = EJBFactory.getOrderTestAnalyte();
        
        for (int j = 0; j < man.deleteCount(); j++ ) {            
            item = man.getDeletedAt(j);
            /*
             * The analytes are deleted through the local interface and not the 
             * manager because the OrderTestManager may not have the analyte loaded
             * because the user never looked them up. Also some of the analytes 
             * may never have been marked as reportable, so they may not have any
             * ids.   
             */
            al.deleteByOrderTestId(item.test.getId());                       
            tl.delete(item.test);
        }

        for (int i = 0; i < man.count(); i++ ) {
            data = man.getTestAt(i);
            data.setSortOrder(i+1);
            if (data.getId() == null) {
                data.setOrderId(man.getOrderId());
                tl.add(data);
            } else {
                tl.update(data);
            }
            
            anaMan = man.getAnalytesAt(i);
            anaMan.setOrderTestId(data.getId());
            anaMan.update();
        }

        return man;
    }
    
    public void validate(OrderTestManager man) throws Exception {
        ValidationErrorsList list;
        OrderTestBean tl;
        OrderTestViewDO data;

        tl = EJBFactory.getOrderTest();
        list = new ValidationErrorsList();
        for (int i = 0; i < man.count(); i++ ) {
            data = man.getTestAt(i);
            try {
                tl.validate(data, i+1);
            } catch (Exception e) {
                list.add(e);
            }
            
            try {
                if (man.items.get(i).analytes != null)
                    man.getAnalytesAt(i).validate(data, i+1);
            } catch (Exception e) {
                list.add(e);
            }
        }
        
        if (list.size() > 0)
            throw list;
    }
}
