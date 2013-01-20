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

import org.openelis.bean.InventoryComponentBean;
import org.openelis.domain.InventoryComponentViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class InventoryComponentManagerProxy {

    public InventoryComponentManager fetchByInventoryItemId(Integer id) throws Exception {
        InventoryComponentManager cm;
        ArrayList<InventoryComponentViewDO> list;

        list = EJBFactory.getInventoryComponent().fetchByInventoryItemId(id);
        cm = InventoryComponentManager.getInstance();
        cm.setInventoryItemId(id);
        cm.setComponents(list);

        return cm;
    }

    public InventoryComponentManager add(InventoryComponentManager man) throws Exception {
        InventoryComponentBean cl;
        InventoryComponentViewDO data;

        cl = EJBFactory.getInventoryComponent();
        for (int i = 0; i < man.count(); i++ ) {
            data = man.getComponentAt(i);
            data.setInventoryItemId(man.getInventoryItemId());
            cl.add(data);
        }

        return man;
    }

    public InventoryComponentManager update(InventoryComponentManager man) throws Exception {
        int i;
        InventoryComponentBean cl;
        InventoryComponentViewDO data;

        cl = EJBFactory.getInventoryComponent();
        for (i = 0; i < man.deleteCount(); i++ )
            cl.delete(man.getDeletedAt(i));

        for (i = 0; i < man.count(); i++ ) {
            data = man.getComponentAt(i);

            if (data.getId() == null) {
                data.setInventoryItemId(man.getInventoryItemId());
                cl.add(data);
            } else {
                cl.update(data);
            }
        }

        return man;
    }
    
    public void validate(InventoryComponentManager man, Integer inventoryItemStoreId) throws Exception {
        ValidationErrorsList list;
        InventoryComponentBean cl;

        cl = EJBFactory.getInventoryComponent();
        list = new ValidationErrorsList();

        for (int i = 0; i < man.count(); i++ ) {
            try {
                cl.validate(man.getComponentAt(i), inventoryItemStoreId);
            } catch (ValidationErrorsList e) {
                DataBaseUtil.mergeException(list, e, "componentTable", i);
            }
        }
        if (list.size() > 0)
            throw list;
    }
}
