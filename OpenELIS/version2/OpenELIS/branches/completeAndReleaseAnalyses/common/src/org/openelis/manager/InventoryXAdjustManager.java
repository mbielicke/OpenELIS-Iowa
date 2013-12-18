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

import org.openelis.domain.InventoryXAdjustViewDO;

public class InventoryXAdjustManager implements Serializable {

    private static final long serialVersionUID = 1L;
    
    protected Integer         inventoryAdjustmentId;
    protected ArrayList<InventoryXAdjustViewDO> adjustments, deleted; 
    
    protected transient static InventoryXAdjustManagerProxy proxy;
    
    public InventoryXAdjustManager() {
    }
    
    /**
     * Creates a new instance of this object.
     */
    public static InventoryXAdjustManager getInstance() {
        return new InventoryXAdjustManager();
    }
    
    public InventoryXAdjustViewDO getAdjustmentAt(int i) {
        return adjustments.get(i);
    }

    public void setAdjustmentAt(InventoryXAdjustViewDO adjustment, int i) {
        if (adjustments == null)
            adjustments = new ArrayList<InventoryXAdjustViewDO>();
        adjustments.set(i, adjustment);
    }

    public int addAdjustment() {
        if (adjustments == null)
            adjustments = new ArrayList<InventoryXAdjustViewDO>();
        adjustments.add(new InventoryXAdjustViewDO());

        return count() - 1;
    }

    public int addAdjustmentAt(int i) {
        if (adjustments == null)
            adjustments = new ArrayList<InventoryXAdjustViewDO>();
        adjustments.add(i, new InventoryXAdjustViewDO());

        return i;
    }

    public void removeAdjustmentAt(int i) {
        InventoryXAdjustViewDO tmp;

        if (adjustments == null || i >= adjustments.size())
            return;

        tmp = adjustments.remove(i);
        if (tmp.getId() != null) {
            if (deleted == null)
                deleted = new ArrayList<InventoryXAdjustViewDO>();
            deleted.add(tmp);
        }
    }

    public int count() {
        if (adjustments == null)
            return 0;

        return adjustments.size();
    }

    // service methods
    public static InventoryXAdjustManager fetchByInventoryAdjustmentId(Integer id) throws Exception {
        return proxy().fetchByInventoryAdjustmentId(id);
    }

    public InventoryXAdjustManager add() throws Exception {
        return proxy().add(this);
    }

    public InventoryXAdjustManager update() throws Exception {
        return proxy().update(this);
    }

    public void validate() throws Exception {
        proxy().validate(this);
    }

    // friendly methods used by managers and proxies
    Integer getInventoryAdjustmentId() {
        return inventoryAdjustmentId;
    }

    void setInventoryAdjustmentId(Integer id) {
        inventoryAdjustmentId = id;
    }

    ArrayList<InventoryXAdjustViewDO> getAdjustments() {
        return adjustments;
    }

    void setAdjustments(ArrayList<InventoryXAdjustViewDO> adjustments) {
        this.adjustments = adjustments;
    }

    int deleteCount() {
        if (deleted == null)
            return 0;
        return deleted.size();
    }

    InventoryXAdjustViewDO getDeletedAt(int i) {
        return deleted.get(i);
    }

    private static InventoryXAdjustManagerProxy proxy() {
        if (proxy == null)
            proxy = new InventoryXAdjustManagerProxy();
        return proxy;
    }


}
