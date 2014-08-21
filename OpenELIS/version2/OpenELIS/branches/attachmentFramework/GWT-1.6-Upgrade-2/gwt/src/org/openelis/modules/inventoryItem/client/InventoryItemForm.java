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

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DoubleField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.metamap.InventoryItemMetaMap;

import com.google.gwt.xml.client.Node;

public class InventoryItemForm extends Form{
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
    
    public String itemTabPanel = "componentsTab";
    
    public InventoryItemForm() {
       InventoryItemMetaMap meta = new InventoryItemMetaMap();
       fields.put(meta.getId(), id = new IntegerField());
       fields.put(meta.getName(), name = new StringField());
       fields.put(meta.getDescription(), description = new StringField());
       fields.put(meta.getStoreId(), storeId = new DropDownField<Integer>());
       fields.put(meta.getCategoryId(), categoryId = new DropDownField<Integer>());
       fields.put(meta.getQuantityMinLevel(), quantityMinLevel = new IntegerField());
       fields.put(meta.getQuantityMaxLevel(), quantityMaxLevel = new IntegerField());
       fields.put(meta.getQuantityToReorder(), quantityToReorder = new IntegerField());
       fields.put(meta.getDispensedUnitsId(), dispensedUnitsId = new DropDownField<Integer>());
       fields.put(meta.getIsActive(), isActive = new CheckField());
       fields.put(meta.getIsReorderAuto(), isReorderAuto = new CheckField());
       fields.put(meta.getIsLotMaintained(), isLotMaintained = new CheckField());
       fields.put(meta.getIsSerialMaintained(), isSerialMaintained = new CheckField());
       fields.put(meta.getIsBulk(), isBulk = new CheckField());
       fields.put(meta.getIsNotForSale(), isNotForSale = new CheckField());
       fields.put(meta.getIsSubAssembly(), isSubAssembly = new CheckField());
       fields.put(meta.getIsLabor(), isLabor = new CheckField());
       fields.put(meta.getIsNoInventory(), isNoInventory = new CheckField());
       fields.put(meta.getProductUri(), productUri = new StringField());
       fields.put(meta.getAverageLeadTime(), averageLeadTime = new IntegerField());
       fields.put(meta.getAverageCost(), averageCost = new DoubleField());
       fields.put(meta.getAverageDailyUse(), averageDailyUse = new IntegerField());
       fields.put(meta.PARENT_INVENTORY_ITEM.getName(), parentInventoryItem = new DropDownField<Integer>());
       fields.put(meta.getParentRatio(), parentRatio = new IntegerField());
       
       fields.put("components", components = new InventoryComponentsForm());
       fields.put("locations", locations = new InventoryLocationsForm());
       fields.put("comments", comments = new InventoryCommentsForm());
   }
   
   public InventoryItemForm(Node node) {
       this();
       createFields(node);
   }
}
