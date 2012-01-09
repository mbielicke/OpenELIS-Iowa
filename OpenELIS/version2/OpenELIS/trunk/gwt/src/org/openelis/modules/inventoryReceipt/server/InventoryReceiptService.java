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

import org.openelis.domain.IdNameVO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.InventoryReceiptManager;
import org.openelis.server.EJBFactory;
import org.openelis.remote.InventoryReceiptManagerRemote;
import org.openelis.remote.InventoryReceiptRemote;

public class InventoryReceiptService {
    
    public ArrayList<IdNameVO> fetchByUpc(String search) throws Exception {
        return remote().fetchByUpc(search + "%", 10);
    }
    
    public ArrayList<InventoryReceiptManager> query(Query query) throws Exception {
        return remote().query(query.getFields());
    }
    
    public InventoryReceiptManager add(InventoryReceiptManager man) throws Exception {
        return remoteManager().add(man);
    }
    
    public InventoryReceiptManager update(InventoryReceiptManager man) throws Exception {
        return remoteManager().update(man);
    }
    
    public InventoryReceiptManager fetchForUpdate(InventoryReceiptManager man) throws Exception {
        return remoteManager().fetchForUpdate(man);
    }
    
    public InventoryReceiptManager abortUpdate(InventoryReceiptManager man) throws Exception {
        return remoteManager().abortUpdate(man);
    }
    
    private InventoryReceiptRemote remote() {
        return EJBFactory.getInventoryReceipt();
    }
    
    private InventoryReceiptManagerRemote remoteManager() {
        return EJBFactory.getInventoryReceiptManager();
    }
}