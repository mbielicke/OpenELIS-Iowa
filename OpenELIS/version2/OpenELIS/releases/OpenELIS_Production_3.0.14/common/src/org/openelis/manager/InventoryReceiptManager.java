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
import java.util.ArrayList;

import org.openelis.domain.InventoryReceiptViewDO;
import org.openelis.ui.common.NotFoundException;

public class InventoryReceiptManager implements Serializable {

    private static final long                               serialVersionUID = 1L;

    protected ArrayList<InventoryReceiptViewDO>             receipts, deleted;
    protected IOrderManager                                  iorder;

    protected transient static InventoryReceiptManagerProxy proxy;
    
    /**
     * This is a protected constructor. See the three static methods for
     * allocation.
     */
    protected InventoryReceiptManager(){
    }
    
    /**
     * Creates a new instance of this object
     */
    public static InventoryReceiptManager getInstance() {               
        return new InventoryReceiptManager();                
    }
    
    public InventoryReceiptManager add() throws Exception {
        return proxy().add(this);
    }    
    
    public InventoryReceiptManager update() throws Exception {
        return proxy().update(this);
    }
    
    public InventoryReceiptManager fetchForUpdate() throws Exception {        
        Integer iorderId;
        InventoryReceiptManager manager;
        InventoryReceiptViewDO data;
        
        iorderId = null;       
        manager = null;        
        if (count() > 0) {
            data = getReceiptAt(0);
            iorderId = data.getIorderItemIorderId();
        }
        
        //
        // if iorderId is not null, then this manager refers to an existing iorder
        // and thus we need to lock that iorder; on the other hand if iorderId is 
        // null then this manager refers to some inventory receipt records that
        // aren't linked to any iorder and thus we need to lock them individually 
        //
        if (iorderId == null) {
            try {
                manager = proxy().fetchForUpdate(this);
            } catch (NotFoundException e) {
                // ignore
            } catch (Exception e) {
                throw e;
            }
        } else {
            if(iorder == null) {
                iorder = IOrderManager.getInstance();
                iorder.getIorder().setId(iorderId);
            }
            try {
                iorder = iorder.fetchForUpdate();
                manager = this;
            } catch (NotFoundException e) {
                // ignore
            } catch (Exception e) {
                throw e;
            }
        }
    
        return manager;
    }  
            
    public InventoryReceiptManager abortUpdate() throws Exception {        
        if(iorder != null && iorder.getIorder().getId() != null) 
            iorder = iorder.abortUpdate();
                
        return this;
    }
    
    public void validate() throws Exception {
        proxy().validate(this);
    }

    public InventoryReceiptViewDO getReceiptAt(int i) {
        return receipts.get(i);
    }
    
    public void setReceiptAt(InventoryReceiptViewDO receipt, int i) {
        if(receipts == null)
            receipts = new ArrayList<InventoryReceiptViewDO>();
        receipts.set(i, receipt);
    }
    
    public int addReceipt(InventoryReceiptViewDO receipt) {
        if(receipts == null)
            receipts = new ArrayList<InventoryReceiptViewDO>();
        receipts.add(receipt);
        
        return count() - 1;
    }
    
    public int addReceiptAt(int i) {
        if(receipts == null)
            receipts = new ArrayList<InventoryReceiptViewDO>();
        receipts.add(i, new InventoryReceiptViewDO());
        
        return count() - 1;
    }
    
    public void removeReceiptAt(int i) {
        InventoryReceiptViewDO tmp;
        
        if(receipts == null || i >= receipts.size())
            return;
        
        tmp = receipts.remove(i);
        if(tmp.getId() != null) {
            if(deleted == null)
                deleted = new ArrayList<InventoryReceiptViewDO>();
            deleted.add(tmp);
        }
    }
    
    public int count() {
        if (receipts == null)
            return 0;

        return receipts.size();
    }
    
    //
    // other managers
    //    
    public IOrderManager getIorder() throws Exception {
        InventoryReceiptViewDO data;
        Integer iorderId;
        
        if (iorder == null && count() > 0) {
            data = getReceiptAt(0);
            iorderId = data.getIorderItemIorderId();
            if (iorderId != null) {
                try {
                    iorder = IOrderManager.fetchById(iorderId);
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    throw e;
                }
            }
        }
        return iorder;
    }       
    
    ArrayList<InventoryReceiptViewDO> getReceipts() {
        return receipts;
    }

    void setReceipts(ArrayList<InventoryReceiptViewDO> receipts) {
        this.receipts = receipts;
    }
    
    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }
    
    InventoryReceiptViewDO getDeletedAt(int i) {
        return deleted.get(i);
    }
        
    private static InventoryReceiptManagerProxy proxy() {
        if (proxy == null)
            proxy = new InventoryReceiptManagerProxy();
        
        return proxy;
    }
}
