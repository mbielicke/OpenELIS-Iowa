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

import java.io.Serializable;

import org.openelis.domain.InventoryAdjustmentViewDO;
import org.openelis.gwt.common.NotFoundException;

public class InventoryAdjustmentManager implements Serializable {

    private static final long                                  serialVersionUID = 1L;

    protected InventoryAdjustmentViewDO                        inventoryAdjustment;
    protected InventoryXAdjustManager                          adjustments;

    protected transient static InventoryAdjustmentManagerProxy proxy;
    
    /**
     * This is a protected constructor. See the three static methods for
     * allocation.
     */
    protected InventoryAdjustmentManager() {
        inventoryAdjustment = null;
        adjustments = null;
    }
    
    /**
     * Creates a new instance of this object. A default inventory adjustment
     * object is also created.
     */
    public static InventoryAdjustmentManager getInstance() {
        InventoryAdjustmentManager manager;
        
        manager = new InventoryAdjustmentManager();
        manager.inventoryAdjustment = new InventoryAdjustmentViewDO();
        
        return manager;
    }

    public InventoryAdjustmentViewDO getInventoryAdjustment() {
        return inventoryAdjustment;
    }

    public void setInventoryAdjustment(InventoryAdjustmentViewDO inventoryAdjustment) {
        this.inventoryAdjustment = inventoryAdjustment;
    }
    
    // service methods
    public static InventoryAdjustmentManager fetchById(Integer id) throws Exception {
        return proxy().fetchById(id);
    }

    public static InventoryAdjustmentManager fetchWithAdjustments(Integer id) throws Exception {
        return proxy().fetchWithAdjustments(id);
    }

    public InventoryAdjustmentManager add() throws Exception {
        return proxy().add(this);
    }

    public InventoryAdjustmentManager update() throws Exception {
        return proxy().update(this);
    }

    public InventoryAdjustmentManager fetchForUpdate() throws Exception {
        return proxy().fetchForUpdate(inventoryAdjustment.getId());
    }

    public InventoryAdjustmentManager abortUpdate() throws Exception {
        return proxy().abortUpdate(inventoryAdjustment.getId());
    }

    public void validate() throws Exception {
        proxy().validate(this);
    }

    //
    // other managers
    //
    public InventoryXAdjustManager getAdjustments() throws Exception {
        if (adjustments == null) {
            if (inventoryAdjustment.getId() != null) {
                try {
                    adjustments = InventoryXAdjustManager.fetchByInventoryAdjustmentId(inventoryAdjustment.getId());
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
            if (adjustments == null)
                adjustments = InventoryXAdjustManager.getInstance();
        }
        return adjustments;
    }
    
    private static InventoryAdjustmentManagerProxy proxy() {
        if (proxy == null)
            proxy = new InventoryAdjustmentManagerProxy();

        return proxy;
    }

}
