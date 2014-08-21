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

import java.util.ArrayList;

import org.openelis.domain.InventoryItemDO;
import org.openelis.domain.InventoryLocationViewDO;
import org.openelis.gwt.common.RPC;

public class InventoryTransferManager implements RPC {

    private static final long                                serialVersionUID = 1L;

    protected ArrayList<InventoryTransferDataBundle>         transfers;

    protected transient static InventoryTransferManagerProxy proxy;
    
    /**
     * This is a protected constructor. See the three static methods for
     * allocation.
     */
    protected InventoryTransferManager(){        
    }

    /**
     * Creates a new instance of this object
     */
    public static InventoryTransferManager getInstance() {               
        return new InventoryTransferManager();                
    }
    
    public InventoryTransferManager add() throws Exception {
        return proxy().add(this);
    }  
    
    public void validate() throws Exception {
        proxy().validate(this);
    }
    
    public int addTransfer() {
        if (transfers == null)
            transfers = new ArrayList<InventoryTransferDataBundle>();
        transfers.add(new InventoryTransferDataBundle());
        
        return count() - 1;
    } 
    
    public int addTransferAt(int i) {
        if (transfers == null)
            transfers = new ArrayList<InventoryTransferDataBundle>();
        transfers.add(i, new InventoryTransferDataBundle());
        
        return count() - 1;
    }
    
    public void removeTransferAt(int i) {        
        if (transfers == null || i >= transfers.size())
            return;
        
        transfers.remove(i);        
    }
    
    public InventoryItemDO getFromInventoryItemAt(int i) {
        return transfers.get(i).fromInventoryItem;
    }         
    
    public void setFromInventoryItemAt(InventoryItemDO fromInventoryItem, int i) {
        if (transfers == null)
            transfers = new ArrayList<InventoryTransferDataBundle>();
        transfers.get(i).fromInventoryItem = fromInventoryItem;
    }
    
    public InventoryLocationViewDO getFromInventoryLocationAt(int i) {
        return transfers.get(i).fromInventoryLocation;
    } 
    
    public void setFromInventoryLocationAt(InventoryLocationViewDO fromInventoryLocation, int i) {
        if (transfers == null)
            transfers = new ArrayList<InventoryTransferDataBundle>();
        transfers.get(i).fromInventoryLocation = fromInventoryLocation;
    }
    
    public String getAddtoExistingAt(int i) {
        return transfers.get(i).addtoExisting;
    } 
    
    public void setAddtoExistingAt(String addToExisting, int i) {
        if (transfers == null)
            transfers = new ArrayList<InventoryTransferDataBundle>();
        transfers.get(i).addtoExisting = addToExisting;
    }        
    
    public InventoryItemDO getToInventoryItemAt(int i) {
        return transfers.get(i).toInventoryItem;
    } 
    
    public void setToInventoryItemAt(InventoryItemDO toInventoryItem, int i) {
        if (transfers == null)
            transfers = new ArrayList<InventoryTransferDataBundle>();
        transfers.get(i).toInventoryItem = toInventoryItem;
    }   
    
    public InventoryLocationViewDO getToInventoryLocationAt(int i) {
        return transfers.get(i).toInventoryLocation;
    } 
    
    public void setToInventoryLocationAt(InventoryLocationViewDO toInventoryLocation, int i) {
        if (transfers == null)
            transfers = new ArrayList<InventoryTransferDataBundle>();
        transfers.get(i).toInventoryLocation = toInventoryLocation;
    }
    
    public Integer getQuantityAt(int i) {
        return transfers.get(i).quantity;
    } 
    
    public void setQuantityAt(Integer quantity, int i) {
        if (transfers == null)
            transfers = new ArrayList<InventoryTransferDataBundle>();
        transfers.get(i).quantity = quantity;
    }    
    
    public int count() {
        if (transfers == null)
            return 0;

        return transfers.size();
    }
    
    ArrayList<InventoryTransferDataBundle> getTransfers() {
        return transfers;
    }
    
    void setTransfers(ArrayList<InventoryTransferDataBundle> transfers) {
        this.transfers = transfers;
    } 
    
    private static InventoryTransferManagerProxy proxy() {
        if (proxy == null)
            proxy = new InventoryTransferManagerProxy();
    
        return proxy;
    }
    
    static class InventoryTransferDataBundle implements RPC {
        
        private static final long serialVersionUID = 1L;

        InventoryItemDO           fromInventoryItem, toInventoryItem;
        InventoryLocationViewDO   fromInventoryLocation, toInventoryLocation;
        String                    addtoExisting;
        Integer                   quantity;
            
        public InventoryTransferDataBundle() {
        }                
    }
}
