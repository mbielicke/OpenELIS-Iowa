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

import org.openelis.bean.InventoryLocationBean;
import org.openelis.domain.InventoryLocationViewDO;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.utils.EJBFactory;

public class InventoryLocationManagerProxy {

    public InventoryLocationManager fetchByInventoryItemId(Integer id) throws Exception {
        InventoryLocationManager lm;
        ArrayList<InventoryLocationViewDO> list;

        list = EJBFactory.getInventoryLocation().fetchByInventoryItemId(id);
        lm = InventoryLocationManager.getInstance();
        lm.setInventoryItemId(id);
        lm.setLocations(list);

        return lm;
    }

    public InventoryLocationManager add(InventoryLocationManager man) throws Exception {
        InventoryLocationBean cl;
        InventoryLocationViewDO data;

        cl = EJBFactory.getInventoryLocation();
        for (int i = 0; i < man.count(); i++ ) {
            data = man.getLocationAt(i);
            data.setInventoryItemId(man.getInventoryItemId());
            cl.add(data);
        }

        return man;
    }

    public InventoryLocationManager update(InventoryLocationManager man) throws Exception {
        int i;
        InventoryLocationBean cl;
        InventoryLocationViewDO location;

        cl = EJBFactory.getInventoryLocation();
        for (i = 0; i < man.deleteCount(); i++ )
            cl.delete(man.getDeletedAt(i));

        for (i = 0; i < man.count(); i++ ) {
            location = man.getLocationAt(i);

            if (location.getId() == null) {
                location.setInventoryItemId(man.getInventoryItemId());
                cl.add(location);
            } else {
                cl.update(location);
            }
        }

        return man;
    }
    
    public void validate(InventoryLocationManager man) throws Exception {
        ValidationErrorsList list;
        InventoryLocationBean cl;

        cl = EJBFactory.getInventoryLocation();
        list = new ValidationErrorsList();
        for (int i = 0; i < man.count(); i++ ) {
            try {
                cl.validate(man.getLocationAt(i));
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "locationTable", i);
            }
        }
        if (list.size() > 0)
            throw list;
    }
}
