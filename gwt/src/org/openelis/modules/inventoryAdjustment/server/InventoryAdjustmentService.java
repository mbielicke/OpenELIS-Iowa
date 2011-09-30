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
package org.openelis.modules.inventoryAdjustment.server;

import java.util.ArrayList;

import org.openelis.domain.InventoryAdjustmentDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.manager.InventoryAdjustmentManager;
import org.openelis.manager.InventoryXAdjustManager;
import org.openelis.persistence.EJBFactory;
import org.openelis.remote.InventoryAdjustmentManagerRemote;
import org.openelis.remote.InventoryAdjustmentRemote;

public class InventoryAdjustmentService {
    public InventoryAdjustmentManager fetchWithAdjustments (Integer id) throws Exception {
        return remoteManager().fetchWithAdjustments(id);
    }
    
    public ArrayList<InventoryAdjustmentDO> query(Query query) throws Exception {
        return remote().query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
    }
    
    public InventoryAdjustmentManager add(InventoryAdjustmentManager man) throws Exception {
        return remoteManager().add(man);
    }
    
    public InventoryAdjustmentManager update(InventoryAdjustmentManager man) throws Exception {
        return remoteManager().update(man);
    }
    
    public InventoryAdjustmentManager fetchForUpdate(Integer id) throws Exception {
        return remoteManager().fetchForUpdate(id);
    }
    
    public InventoryAdjustmentManager abortUpdate(Integer id) throws Exception {
        return remoteManager().abortUpdate(id);
    }
    
    public InventoryXAdjustManager fetchAdjustmentByInventoryAdjustmentId(Integer id) throws Exception {
        return remoteManager().fetchAdjustmentByInventoryAdjustmentId(id);
    }
        
    private InventoryAdjustmentRemote remote() {
        return (InventoryAdjustmentRemote)EJBFactory.lookup("openelis/InventoryAdjustmentBean/remote");
    }
    
    private InventoryAdjustmentManagerRemote remoteManager() {
        return (InventoryAdjustmentManagerRemote)EJBFactory.lookup("openelis/InventoryAdjustmentManagerBean/remote"); 
    }
}