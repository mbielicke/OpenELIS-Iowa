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

import javax.naming.InitialContext;

import org.openelis.domain.InventoryComponentViewDO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.local.InventoryComponentLocal;
import org.openelis.utilcommon.DataBaseUtil;

public class InventoryComponentManagerProxy {

    public InventoryComponentManager fetchByInventoryItemId(Integer id) throws Exception {
        InventoryComponentManager cm;
        ArrayList<InventoryComponentViewDO> components;

        components = local().fetchByInventoryItemId(id);
        cm = InventoryComponentManager.getInstance();
        cm.setInventoryItemId(id);
        cm.setComponents(components);

        return cm;
    }

    public InventoryComponentManager add(InventoryComponentManager man) throws Exception {
        InventoryComponentLocal cl;
        InventoryComponentViewDO component;

        cl = local();
        for (int i = 0; i < man.count(); i++ ) {
            component = man.getComponentAt(i);
            component.setInventoryItemId(man.getInventoryItemId());
            cl.add(component);
        }

        return man;
    }

    public InventoryComponentManager update(InventoryComponentManager man) throws Exception {
        InventoryComponentLocal cl;
        InventoryComponentViewDO component;

        cl = local();
        for (int j = 0; j < man.deleteCount(); j++ )
            cl.delete(man.getDeletedAt(j));

        for (int i = 0; i < man.count(); i++ ) {
            component = man.getComponentAt(i);

            if (component.getId() == null) {
                component.setInventoryItemId(man.getInventoryItemId());
                cl.add(component);
            } else {
                cl.update(component);
            }
        }

        return man;
    }
    
    public void validate(InventoryComponentManager man) throws Exception {
        ValidationErrorsList list;
        InventoryComponentLocal cl;

        cl = local();
        list = new ValidationErrorsList();
        for (int i = 0; i < man.count(); i++ ) {
            try {
                cl.validate(man.getComponentAt(i));
            } catch (Exception e) {
                DataBaseUtil.mergeException(list, e, "componentTable", i);
            }
        }
        if (list.size() > 0)
            throw list;
    }

    private InventoryComponentLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (InventoryComponentLocal)ctx.lookup("openelis/InventoryComponentBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
