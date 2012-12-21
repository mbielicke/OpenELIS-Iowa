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

import org.openelis.bean.OrderContainerBean;
import org.openelis.domain.OrderContainerDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class OrderContainerManagerProxy {
    
    public OrderContainerManager fetchByOrderId(Integer id) throws Exception {
        OrderContainerManager m;
        ArrayList<OrderContainerDO> data;

        data = EJBFactory.getOrderContainer().fetchByOrderId(id);
        m = OrderContainerManager.getInstance();
        m.setOrderId(id);
        m.setContainers(data);

        return m;
    }

    public OrderContainerManager add(OrderContainerManager man) throws Exception {
        OrderContainerBean cl;
        OrderContainerDO data;

        cl = EJBFactory.getOrderContainer();
        for (int i = 0; i < man.count(); i++ ) {
            data = man.getContainerAt(i);
            data.setOrderId(man.getOrderId());
            cl.add(data);
        }

        return man;
    }

    public OrderContainerManager update(OrderContainerManager man) throws Exception {
        OrderContainerBean cl;
        OrderContainerDO data;

        cl = EJBFactory.getOrderContainer();
        for (int j = 0; j < man.deleteCount(); j++ )
            cl.delete(man.getDeletedAt(j));

        for (int i = 0; i < man.count(); i++ ) {
            data = man.getContainerAt(i);

            if (data.getId() == null) {
                data.setOrderId(man.getOrderId());
                cl.add(data);
            } else {
                cl.update(data);
            }
        }

        return man;
    }
    
    public void validate(OrderContainerManager man) throws Exception {
        ValidationErrorsList list;
        OrderContainerBean cl;

        cl = EJBFactory.getOrderContainer();
        list = new ValidationErrorsList();
        
        for (int i = 0; i < man.count(); i++ ) {
            try {
                cl.validate(man.getContainerAt(i));
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "orderContainerTable", i);
            }
        }
        
        if (list.size() > 0)
            throw list;
    }
}
