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

import org.openelis.bean.IOrderContainerBean;
import org.openelis.domain.IOrderContainerDO;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class IOrderContainerManagerProxy {
    
    public IOrderContainerManager fetchByIorderId(Integer id) throws Exception {
        IOrderContainerManager m;
        ArrayList<IOrderContainerDO> data;

        data = EJBFactory.getIOrderContainer().fetchByIorderId(id);
        m = IOrderContainerManager.getInstance();
        m.setOrderId(id);
        m.setContainers(data);

        return m;
    }

    public IOrderContainerManager add(IOrderContainerManager man) throws Exception {
        IOrderContainerBean cl;
        IOrderContainerDO data;

        cl = EJBFactory.getIOrderContainer();
        for (int i = 0; i < man.count(); i++ ) {
            data = man.getContainerAt(i);
            data.setIorderId(man.getOrderId());
            cl.add(data);
        }

        return man;
    }

    public IOrderContainerManager update(IOrderContainerManager man) throws Exception {
        IOrderContainerBean cl;
        IOrderContainerDO data;

        cl = EJBFactory.getIOrderContainer();
        for (int j = 0; j < man.deleteCount(); j++ )
            cl.delete(man.getDeletedAt(j));

        for (int i = 0; i < man.count(); i++ ) {
            data = man.getContainerAt(i);

            if (data.getId() == null) {
                data.setIorderId(man.getOrderId());
                cl.add(data);
            } else {
                cl.update(data);
            }
        }

        return man;
    }
    
    public void validate(IOrderContainerManager man) throws Exception {
        ValidationErrorsList list;
        IOrderContainerBean cl;

        cl = EJBFactory.getIOrderContainer();
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
