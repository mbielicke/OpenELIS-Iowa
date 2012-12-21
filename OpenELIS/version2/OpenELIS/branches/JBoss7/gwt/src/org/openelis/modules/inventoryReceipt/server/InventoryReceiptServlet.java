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
package org.openelis.modules.inventoryReceipt.server;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.InventoryReceiptBean;
import org.openelis.bean.InventoryReceiptManagerBean;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.server.AppServlet;
import org.openelis.manager.InventoryReceiptManager;
import org.openelis.modules.inventoryReceipt.client.InventoryReceiptServiceInt;

@WebServlet("/openelis/inventoryReceipt")
public class InventoryReceiptServlet extends AppServlet implements InventoryReceiptServiceInt {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    InventoryReceiptManagerBean inventoryReceiptManager;
    
    @EJB
    InventoryReceiptBean        inventoryReceipt;

    public ArrayList<IdNameVO> fetchByUpc(String search) throws Exception {
        return inventoryReceipt.fetchByUpc(search + "%", 10);
    }
    
    public ArrayList<InventoryReceiptManager> query(Query query) throws Exception {
        return inventoryReceipt.query(query.getFields());
    }
    
    public InventoryReceiptManager add(InventoryReceiptManager man) throws Exception {
        return inventoryReceiptManager.add(man);
    }
    
    public InventoryReceiptManager update(InventoryReceiptManager man) throws Exception {
        return inventoryReceiptManager.update(man);
    }
    
    public InventoryReceiptManager fetchForUpdate(InventoryReceiptManager man) throws Exception {
        return inventoryReceiptManager.fetchForUpdate(man);
    }
    
    public InventoryReceiptManager abortUpdate(InventoryReceiptManager man) throws Exception {
        return inventoryReceiptManager.abortUpdate(man);
    }
    
}