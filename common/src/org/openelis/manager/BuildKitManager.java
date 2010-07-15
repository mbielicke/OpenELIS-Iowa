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

import org.openelis.domain.InventoryReceiptViewDO;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;
import org.openelis.utilcommon.DataBaseUtil;

public class BuildKitManager implements RPC {

    private static final long                       serialVersionUID = 1L;

    protected Integer                               inventoryItemId;
    protected InventoryItemManager                  inventoryItem;
    protected InventoryReceiptViewDO                inventoryReceipt;

    protected transient static BuildKitManagerProxy proxy;         
    /**
     * This is a protected constructor. See the static methods for allocation.
     */
    protected BuildKitManager() {        
        inventoryItemId = null;
        inventoryItem = null;
        inventoryReceipt = null;
    }
    
    /**
     * Creates a new instance of this object. A default inventory receipt object is
     * also created.
     */
    public static BuildKitManager getInstance() { 
        BuildKitManager man;
        
        man = new BuildKitManager();
        man.inventoryReceipt = new InventoryReceiptViewDO();             
        return man;
    }
    
    public Integer getInventoryItemId() {
        return inventoryItemId;
    }

    public void setInventoryItemId(Integer inventoryItemId) {
        if(DataBaseUtil.isDifferent(this.inventoryItemId, inventoryItemId)) {
            this.inventoryItemId = inventoryItemId;
            inventoryItem = null;
        }
    }
    
    public InventoryReceiptViewDO getInventoryReceipt() {
        return inventoryReceipt;
    }

    public void setInventoryReceipt(InventoryReceiptViewDO inventoryReceipt) {
        this.inventoryReceipt = inventoryReceipt;
    }
    
    public BuildKitManager add() throws Exception {
        return proxy().add(this);
    }    
    
    public void validate() throws Exception {
        proxy().validate(this);
    }
    
    //
    // other managers
    //
    public InventoryItemManager getInventoryItem() throws Exception {
        if (inventoryItem == null) {
            if (inventoryItemId != null) {
                try {
                    inventoryItem = InventoryItemManager.fetchById(inventoryItemId);
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
        }

        return inventoryItem;
    }
    
    private static BuildKitManagerProxy proxy() {
        if (proxy == null)
            proxy = new BuildKitManagerProxy();
        
        return proxy;
    }
}