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
 * The class extends inventory item DO and carries parent inventory item name.
 * The additional field is for read/display only and do not get committed to the
 * database. Note: isChanged will reflect any changes to read/display fields.
 */

public class InventoryItemViewDO extends InventoryItemDO {

    private static final long serialVersionUID = 1L;

    protected String          parentInventoryItemName;

    public InventoryItemViewDO() {
    }

    public InventoryItemViewDO(Integer id, String name, String description, Integer categoryId,
                               Integer storeId, Integer quantityMinLevel, Integer quantityMaxLevel,
                               Integer quantityToReorder, Integer dispensedUnitsId,
                               String isReorderAuto, String isLotMaintained,
                               String isSerialMaintained, String isActive, String isBulk,
                               String isNotForSale, String isSubAssembly, String isLabor,
                               String isNotInventoried, String productUri, Integer aveLeadTime,
                               Double aveCost, Integer aveDailyUse, Integer parentInventoryItemId,
                               Integer parentRatio, String parentInventoryItemName) {

        super(id, name, description, categoryId, storeId, quantityMinLevel, quantityMaxLevel,
              quantityToReorder, dispensedUnitsId, isReorderAuto, isLotMaintained,
              isSerialMaintained, isActive, isBulk, isNotForSale, isSubAssembly, isLabor,
              isNotInventoried, productUri, aveLeadTime, aveCost, aveDailyUse,
              parentInventoryItemId, parentRatio);

        setParentInventoryItemName(parentInventoryItemName);
    }

    public String getParentInventoryItemName() {
        return parentInventoryItemName;
    }

    public void setParentInventoryItemName(String parentInventoryItemName) {
        this.parentInventoryItemName = DataBaseUtil.trim(parentInventoryItemName);
    }
}