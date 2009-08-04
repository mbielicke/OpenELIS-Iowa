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
package org.openelis.domain;

import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.RPC;
import org.openelis.utilcommon.DataBaseUtil;

public class BuildKitComponentDO implements RPC {

    private static final long serialVersionUID = 1L;
    protected Integer         inventoryItemId;
    protected String          component;
    protected Integer         locationId;
    protected String          location;
    protected String          lotNum;
    protected Datetime        expDate;
    protected Double          unit;
    protected Integer         total;
    protected Integer         qtyOnHand;
    protected Integer         orderItemId;
    protected Integer         inventoryXUseId;
    protected Integer         inventoryReceiptOrderItemId;

    public BuildKitComponentDO() {

    }

    public Integer getInventoryReceiptOrderItemId() {
        return inventoryReceiptOrderItemId;
    }

    public void setInventoryReceiptOrderItemId(Integer inventoryReceiptOrderItemId) {
        this.inventoryReceiptOrderItemId = inventoryReceiptOrderItemId;
    }

    public Integer getInventoryXUseId() {
        return inventoryXUseId;
    }

    public void setInventoryXUseId(Integer inventoryXUseId) {
        this.inventoryXUseId = inventoryXUseId;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = DataBaseUtil.trim(component);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = DataBaseUtil.trim(location);
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getLotNum() {
        return lotNum;
    }

    public void setLotNum(String lotNum) {
        this.lotNum = DataBaseUtil.trim(lotNum);
    }

    public Integer getQtyOnHand() {
        return qtyOnHand;
    }

    public void setQtyOnHand(Integer qtyOnHand) {
        this.qtyOnHand = qtyOnHand;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Double getUnit() {
        return unit;
    }

    public void setUnit(Double unit) {
        this.unit = unit;
    }

    public Integer getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Integer orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Integer getInventoryItemId() {
        return inventoryItemId;
    }

    public void setInventoryItemId(Integer inventoryItemId) {
        this.inventoryItemId = inventoryItemId;
    }

    public Datetime getExpDate() {
        return expDate;
    }

    public void setExpDate(Datetime expDate) {
        this.expDate = expDate;
    }
}
