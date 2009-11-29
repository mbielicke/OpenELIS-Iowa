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
package org.openelis.modules.inventoryAdjustment.client;


public class InventoryAdjustmentForm {
}
/*
    private static final long serialVersionUID = 1L;

    public IntegerField id;
    public StringField description;
    public DateField adjustmentDate;
    public StringField systemUser;
    public DropDownField<Integer> storeId;
    public TableField<TableDataRow<Integer>> adjustmentsTable;
    public Integer systemUserId;
    
    public Integer storeIdKey;
    public Integer locId;
    public TableDataModel<TableDataRow<Integer>> invItemModel;
    public String storageLocation;
    public Integer qtyOnHand;
    public Integer oldLocId;
    public TableDataModel<TableDataRow<Integer>> lockedIds;
    
    public InventoryAdjustmentForm() {
       InventoryAdjustmentMetaMap meta = new InventoryAdjustmentMetaMap();
       id = new IntegerField(meta.getId());
       description = new StringField(meta.getDescription());
       adjustmentDate = new DateField(meta.getAdjustmentDate());
       systemUser = new StringField(meta.getSystemUserId());
       storeId = new DropDownField<Integer>(meta.TRANS_ADJUSTMENT_LOCATION_META.INVENTORY_LOCATION_META.INVENTORY_ITEM_META.getStoreId());
       adjustmentsTable = new TableField<TableDataRow<Integer>>("adjustmentsTable");
   }
   
   public InventoryAdjustmentForm(Node node) {
       this();
       createFields(node);
   }
   
   public AbstractField[] getFields() {
       return new AbstractField[] {
                                   id,
                                   description,
                                   adjustmentDate,
                                   systemUser,
                                   storeId,
                                   adjustmentsTable
       };
   }
*/