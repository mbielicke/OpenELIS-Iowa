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

import org.openelis.domain.InventoryXPutViewDO;
import org.openelis.local.InventoryXPutLocal;

public class OrderReceiptManagerProxy {

    public OrderReceiptManager fetchByOrderId(Integer id) throws Exception {
        InventoryXPutLocal pl;
        OrderReceiptManager m;
        ArrayList<InventoryXPutViewDO> list;

        pl = local();
        list = pl.fetchByOrderId(id);
        m = OrderReceiptManager.getInstance();

        m.setReceipts(list);

        return m;
    }

    public OrderReceiptManager add(OrderReceiptManager man) throws Exception {
        InventoryXPutLocal pl;
        InventoryXPutViewDO receipt;        

        pl = local();
        for (int i = 0; i < man.count(); i++ ) {
            receipt = man.getReceiptAt(i);
            pl.add(receipt);
        }

        return man;
    }

    public OrderReceiptManager update(OrderReceiptManager man) throws Exception {
        InventoryXPutLocal pl;
        InventoryXPutViewDO receipt;        

        pl = local();
        
        for (int j = 0; j < man.deleteCount(); j++ )
            pl.delete(man.getDeletedAt(j));
        
        for (int i = 0; i < man.count(); i++ ) {
            receipt = man.getReceiptAt(i);
            if(receipt.getId() != null)
                pl.update(receipt);
            else
                pl.add(receipt);
        }

        return man;
    }

    public OrderReceiptManager fetchForUpdate(OrderReceiptManager man) throws Exception {
        assert false : "not supported";
        return null;
    }

    public OrderReceiptManager abortUpdate(Integer id) throws Exception {
        assert false : "not supported";
        return null;
    }

    public void validate(OrderReceiptManager man) throws Exception {
    
    }

    private InventoryXPutLocal local() {
        try {
            InitialContext ctx = new InitialContext();
            return (InventoryXPutLocal)ctx.lookup("openelis/InventoryXPutBean/local");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}