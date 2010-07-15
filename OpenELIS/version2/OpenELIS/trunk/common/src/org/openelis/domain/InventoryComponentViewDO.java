/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.domain;

import org.openelis.utilcommon.DataBaseUtil;

/**
 * The class extends inventory component DO and adds several commonly used
 * fields such as component name and description. The additional field is for
 * read/display only and does not get committed to the database. Note: isChanged
 * will reflect any changes to read/display fields.
 */

public class InventoryComponentViewDO extends InventoryComponentDO {

    private static final long serialVersionUID = 1L;

    protected String          componentName, componentDescription, inventoryLocationLotNumber,
                              inventoryLocationStorageLocationName,
                              inventoryLocationStorageLocationUnitDescription,
                              inventoryLocationStorageLocationLocation;
    protected Integer         componentDispensedUnitsId, inventoryLocationId, inventoryLocationQuantityOnhand, total;

    public InventoryComponentViewDO() {
    }

    public InventoryComponentViewDO(Integer id, Integer inventoryItemId, Integer componentId,
                                    Integer quantity, String componentName,
                                    String componentDescription, Integer componentDispensedUnitsId) {
        super(id, inventoryItemId, componentId, quantity);
        setComponentName(componentName);
        setComponentDescription(componentDescription);
        setComponentDispensedUnitsId(componentDispensedUnitsId);
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = DataBaseUtil.trim(componentName);
    }

    public String getComponentDescription() {
        return componentDescription;
    }

    public void setComponentDescription(String componentDescription) {
        this.componentDescription = DataBaseUtil.trim(componentDescription);
    }

    public Integer getComponentDispensedUnitsId() {
        return componentDispensedUnitsId;
    }

    public void setComponentDispensedUnitsId(Integer componentDispensedUnitsId) {
        this.componentDispensedUnitsId = componentDispensedUnitsId;
    }

    public Integer getInventoryLocationId() {
        return inventoryLocationId;
    }

    public void setInventoryLocationId(Integer inventoryLocationId) {
        this.inventoryLocationId = inventoryLocationId;
    }
    
    public String getInventoryLocationLotNumber() {
        return inventoryLocationLotNumber;
    }

    public void setInventoryLocationLotNumber(String inventoryLocationLotNumber) {
        this.inventoryLocationLotNumber = DataBaseUtil.trim(inventoryLocationLotNumber);
    }

    public String getInventoryLocationStorageLocationName() {
        return inventoryLocationStorageLocationName;
    }

    public void setInventoryLocationStorageLocationName(String inventoryLocationStorageLocationName) {
        this.inventoryLocationStorageLocationName = DataBaseUtil.trim(inventoryLocationStorageLocationName);
    }

    public String getInventoryLocationStorageLocationUnitDescription() {
        return inventoryLocationStorageLocationUnitDescription;
    }

    public void setInventoryLocationStorageLocationUnitDescription(String inventoryLocationStorageLocationUnitDescription) {
        this.inventoryLocationStorageLocationUnitDescription = DataBaseUtil.trim(inventoryLocationStorageLocationUnitDescription);
    }

    public String getInventoryLocationStorageLocationLocation() {
        return inventoryLocationStorageLocationLocation;
    }

    public void setInventoryLocationStorageLocationLocation(String inventoryLocationStorageLocationLocation) {
        this.inventoryLocationStorageLocationLocation = inventoryLocationStorageLocationLocation;
    }

    public Integer getInventoryLocationQuantityOnhand() {
        return inventoryLocationQuantityOnhand;
    }

    public void setInventoryLocationQuantityOnhand(Integer inventoryLocationQuantityOnhand) {
        this.inventoryLocationQuantityOnhand = inventoryLocationQuantityOnhand;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
    
    
}
