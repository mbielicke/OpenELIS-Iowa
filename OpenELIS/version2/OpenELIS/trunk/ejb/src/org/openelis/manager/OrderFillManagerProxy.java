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

import org.openelis.domain.InventoryXUseViewDO;
import org.openelis.local.InventoryXUseLocal;

public class OrderFillManagerProxy {

    public OrderFillManager fetchByOrderId(Integer id) throws Exception {
        InventoryXUseLocal ul;
        OrderFillManager m;
        ArrayList<InventoryXUseViewDO> list;

        ul = local();
        list = ul.fetchByOrderId(id);
        m = OrderFillManager.getInstance();

        m.setFills(list);

        return m;
    }

    public OrderFillManager add(OrderFillManager man) throws Exception {
        Integer id;
        InventoryXUseLocal ul;

        // FIXME
        return man;
    }

    public OrderFillManager update(OrderFillManager man) throws Exception {
        Integer id;
        InventoryXUseLocal ul;

        // FIXME
        return man;
    }

    public OrderFillManager fetchForUpdate(OrderFillManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    public OrderFillManager abortUpdate(Integer id) throws Exception {
        assert false : "not supported";
        return null;
    }

    public void validate(OrderFillManager man) throws Exception {
    }

    private InventoryXUseLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (InventoryXUseLocal)ctx.lookup("openelis/InventoryXUseBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}