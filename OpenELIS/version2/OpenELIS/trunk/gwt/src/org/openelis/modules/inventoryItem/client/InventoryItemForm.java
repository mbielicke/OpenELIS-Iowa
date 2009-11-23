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
package org.openelis.modules.inventoryItem.client;

import org.openelis.gwt.common.data.deprecated.AbstractField;
import org.openelis.gwt.common.data.deprecated.CheckField;
import org.openelis.gwt.common.data.deprecated.DoubleField;
import org.openelis.gwt.common.data.deprecated.DropDownField;
import org.openelis.gwt.common.data.deprecated.IntegerField;
import org.openelis.gwt.common.data.deprecated.StringField;
import org.openelis.gwt.common.deprecated.Form;
import org.openelis.metamap.InventoryItemMetaMap;

import com.google.gwt.xml.client.Node;

public class InventoryItemForm extends Form<Integer> {
    private static final long serialVersionUID = 1L;

    public IntegerField id;
    public StringField name;
    public StringField description;
    public DropDownField<Integer> storeId;
    public DropDownField<Integer> categoryId;
    public IntegerField quantityMinLevel;
    public IntegerField quantityMaxLevel;
    public IntegerField quantityToReorder;
    public DropDownField<Integer> dispensedUnitsId;
    public CheckField isActive;
    public CheckField isReorderAuto;
    public CheckField isLotMaintained;
    public CheckField isSerialMaintained;
    public CheckField isBulk;
    public CheckField isNotForSale;
    public CheckField isSubAssembly;
    public CheckField isLabor;
    public CheckField isNoInventory;
    public StringField productUri;
    public IntegerField averageLeadTime;
    public DoubleField averageCost;
    public IntegerField averageDailyUse;
    public DropDownField<Integer> parentInventoryItem;
    public IntegerField parentRatio;
    
    public InventoryComponentsForm components;
    public InventoryLocationsForm locations;
    public InventoryCommentsForm comments;
    public InventoryManufacturingForm manufacturing;
    
    public String itemTabPanel = "componentsTab";
    
    public Integer componentId;
    public String descText;
    
    public InventoryItemForm() {
       InventoryItemMetaMap meta = new InventoryItemMetaMap();
       id = new IntegerField(meta.getId());
       name = new StringField(meta.getName());
       description = new StringField(meta.getDescription());
       storeId = new DropDownField<Integer>(meta.getStoreId());
       categoryId = new DropDownField<Integer>(meta.getCategoryId());
       quantityMinLevel = new IntegerField(meta.getQuantityMinLevel());
       quantityMaxLevel = new IntegerField(meta.getQuantityMaxLevel());
       quantityToReorder = new IntegerField(meta.getQuantityToReorder());
       dispensedUnitsId = new DropDownField<Integer>(meta.getDispensedUnitsId());
       isActive = new CheckField(meta.getIsActive());
       isReorderAuto = new CheckField(meta.getIsReorderAuto());
       isLotMaintained = new CheckField(meta.getIsLotMaintained());
       isSerialMaintained = new CheckField(meta.getIsSerialMaintained());
       isBulk = new CheckField(meta.getIsBulk());
       isNotForSale = new CheckField(meta.getIsNotForSale());
       isSubAssembly = new CheckField(meta.getIsSubAssembly());
       isLabor = new CheckField(meta.getIsLabor());
       isNoInventory = new CheckField(meta.getIsNotInventoried());
       productUri = new StringField(meta.getProductUri());
       averageLeadTime = new IntegerField(meta.getAverageLeadTime());
       averageCost = new DoubleField(meta.getAverageCost());
       averageDailyUse = new IntegerField(meta.getAverageDailyUse());
       parentInventoryItem = new DropDownField<Integer>(meta.PARENT_INVENTORY_ITEM.getName());
       parentRatio = new IntegerField(meta.getParentRatio());
       components = new InventoryComponentsForm("components");
       locations = new InventoryLocationsForm("locations");
       comments = new InventoryCommentsForm("comments");
       manufacturing = new InventoryManufacturingForm("manufacturing");
   }
   
   public InventoryItemForm(Node node) {
       this();
       createFields(node);
   }
   
   public AbstractField[] getFields() {
       return new AbstractField[] {
                                     id,
                                     name,
                                     description,
                                     storeId,
                                     categoryId,
                                     quantityMinLevel,
                                     quantityMaxLevel,
                                     quantityToReorder,
                                     dispensedUnitsId,
                                     isActive,
                                     isReorderAuto,
                                     isLotMaintained,
                                     isSerialMaintained,
                                     isBulk,
                                     isNotForSale,
                                     isSubAssembly,
                                     isLabor,
                                     isNoInventory,
                                     productUri,
                                     averageLeadTime,
                                     averageCost,
                                     averageDailyUse,
                                     parentInventoryItem,
                                     parentRatio,
                                     components,
                                     locations,
                                     comments,
                                     manufacturing
       };
   }
}
