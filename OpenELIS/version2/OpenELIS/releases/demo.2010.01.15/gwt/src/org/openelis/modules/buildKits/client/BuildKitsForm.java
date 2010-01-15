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
 package org.openelis.modules.buildKits.client;


public class BuildKitsForm {
}
/*
    private static final long serialVersionUID = 1L;

    public CheckField addToExisiting;
    public DropDownField<Integer> inventoryItem;
    public IntegerField numRequested;
    public DropDownField<Integer> storageLocation;
    public StringField lotNumber;
    public DateField expirationDate;
    public TableField<TableDataRow<Integer>> subItemsTable;
    
    public Integer kitId;
    public TableDataModel<TableDataRow<Integer>> subItemsModel;
    public Integer locId;
    public Integer lastLocId;
    public Integer qtyOnHand;
    
    public BuildKitsForm() {
       InventoryItemMetaMap meta = new InventoryItemMetaMap();
       addToExisiting = new CheckField("addToExisting");
       inventoryItem = new DropDownField<Integer>(meta.getName());
       numRequested = new IntegerField("numRequested");
       storageLocation = new DropDownField<Integer>(meta.INVENTORY_LOCATION.INVENTORY_LOCATION_STORAGE_LOCATION.getLocation());
       lotNumber = new StringField(meta.INVENTORY_LOCATION.getLotNumber());
       expirationDate = new DateField(meta.INVENTORY_LOCATION.getExpirationDate());
       subItemsTable = new TableField<TableDataRow<Integer>>("subItemsTable");
   }
   
   public BuildKitsForm(Node node) {
       this();
       createFields(node);
   }
   
   public AbstractField[] getFields() {
       return new AbstractField[] {
                                   addToExisiting,
                                   inventoryItem,
                                   numRequested,
                                   storageLocation,
                                   lotNumber,
                                   expirationDate,
                                   subItemsTable
       };
   }
   */
