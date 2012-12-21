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

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import org.openelis.bean.InventoryAdjustmentBean;
import org.openelis.bean.InventoryAdjustmentManagerBean;
import org.openelis.domain.InventoryAdjustmentDO;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.server.AppServlet;
import org.openelis.manager.InventoryAdjustmentManager;
import org.openelis.manager.InventoryXAdjustManager;
import org.openelis.modules.inventoryAdjustment.client.InventoryAdjustmentServiceInt;

@WebServlet("/openelis/inventoryAdjustment")
public class InventoryAdjustmentServlet extends AppServlet implements InventoryAdjustmentServiceInt {
    
    private static final long serialVersionUID = 1L;

    @EJB
    InventoryAdjustmentManagerBean inventoryAdjustmentManager;
    
    @EJB
    InventoryAdjustmentBean        inventoryAdjustment;
    
    public InventoryAdjustmentManager fetchWithAdjustments (Integer id) throws Exception {
        return inventoryAdjustmentManager.fetchWithAdjustments(id);
    }
    
    public ArrayList<InventoryAdjustmentDO> query(Query query) throws Exception {
        return inventoryAdjustment.query(query.getFields(), query.getPage() * query.getRowsPerPage(), query.getRowsPerPage());
    }
    
    public InventoryAdjustmentManager add(InventoryAdjustmentManager man) throws Exception {
        return inventoryAdjustmentManager.add(man);
    }
    
    public InventoryAdjustmentManager update(InventoryAdjustmentManager man) throws Exception {
        return inventoryAdjustmentManager.update(man);
    }
    
    public InventoryAdjustmentManager fetchForUpdate(Integer id) throws Exception {
        return inventoryAdjustmentManager.fetchForUpdate(id);
    }
    
    public InventoryAdjustmentManager abortUpdate(Integer id) throws Exception {
        return inventoryAdjustmentManager.abortUpdate(id);
    }
    
    public InventoryXAdjustManager fetchAdjustmentByInventoryAdjustmentId(Integer id) throws Exception {
        return inventoryAdjustmentManager.fetchAdjustmentByInventoryAdjustmentId(id);
    }
        
}