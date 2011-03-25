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
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.OrderTestLocal;
import org.openelis.utils.EJBFactory;

public class OrderTestManagerProxy {
    
    public OrderTestManager fetchByOrderId(Integer id) throws Exception {
        OrderTestManager m;
        ArrayList<OrderTestViewDO> tests;
        
        tests = EJBFactory.getOrderTest().fetchByOrderId(id);
        m = OrderTestManager.getInstance();
        m.setOrderId(id);
        m.setTests(tests);

        return m;
    }
    
    public OrderTestManager add(OrderTestManager man) throws Exception {
        OrderTestLocal tl;
        OrderTestViewDO data;

        tl = EJBFactory.getOrderTest();
        for (int i = 0; i < man.count(); i++ ) {
            data = man.getTestAt(i);
            data.setSortOrder(i+1);
            data.setOrderId(man.getOrderId());
            tl.add(data);
        }

        return man;
    }

    public OrderTestManager update(OrderTestManager man) throws Exception {
        OrderTestLocal tl;
        OrderTestViewDO data;
        
        tl = EJBFactory.getOrderTest();
        for (int j = 0; j < man.deleteCount(); j++ )
            tl.delete(man.getDeletedAt(j));

        for (int i = 0; i < man.count(); i++ ) {
            data = man.getTestAt(i);
            data.setSortOrder(i+1);
            if (data.getId() == null) {
                data.setOrderId(man.getOrderId());
                tl.add(data);
            } else {
                tl.update(data);
            }
        }

        return man;
    }
    
    public void validate(OrderTestManager man) throws Exception {
        ValidationErrorsList list;
        OrderTestLocal tl;

        tl = EJBFactory.getOrderTest();
        list = new ValidationErrorsList();
        for (int i = 0; i < man.count(); i++ ) {
            try {
                tl.validate(man.getTestAt(i));
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "orderTestTable", i);
            }
        }
        
        if (list.size() > 0)
            throw list;
    }
}
