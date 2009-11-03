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
package org.openelis.modules.inventoryReceipt.client;

import org.openelis.gwt.common.data.deprecated.AbstractField;
import org.openelis.gwt.common.data.deprecated.CheckField;
import org.openelis.gwt.common.data.deprecated.DateField;
import org.openelis.gwt.common.data.deprecated.DropDownField;
import org.openelis.gwt.common.data.deprecated.StringField;
import org.openelis.gwt.common.data.deprecated.TableDataModel;
import org.openelis.gwt.common.data.deprecated.TableDataRow;
import org.openelis.gwt.common.deprecated.Form;
import org.openelis.metamap.InventoryReceiptMetaMap;

import com.google.gwt.xml.client.Node;


public class InvReceiptItemInfoForm extends Form<Integer> {
    private static final long serialVersionUID = 1L;

    public DropDownField<Integer> storageLocationId;
    public StringField lotNumber;
    public DateField expirationDate;
    public CheckField addToExisting;
    
    public StringField description;
    public StringField storeId;
    public StringField dispensedUnits;
    public StringField multUnit;
    public StringField streetAddress;
    public StringField city;
    public StringField state;
    public StringField zipCode;
    public StringField toDescription;
    public StringField toStoreId;
    public StringField toDispensedUnits;
    public StringField toLotNumber;
    public StringField toExpDate;
    
    public boolean disableOrderId = false;
    public boolean disableInvItem = false;
    public boolean disableUpc = false;
    public boolean disableOrg = false;
    
    public Integer receiptId;
    public Integer orderItemId;
    
    public String itemIsBulk;
    public String itemIsLotMaintained;
    public String itemIsSerialMaintained;
    
    //used to unlock the rows
    public TableDataModel<TableDataRow<Integer>> lockedIds;
    
    public InvReceiptItemInfoForm() {
       InventoryReceiptMetaMap meta = new InventoryReceiptMetaMap();
       storageLocationId = new DropDownField<Integer>(meta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getStorageLocationId());
       lotNumber = new StringField(meta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getLotNumber());
       expirationDate = new DateField(meta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getExpirationDate());
       addToExisting = new CheckField("addToExisting");
       description = new StringField(meta.INVENTORY_ITEM_META.getDescription());
       storeId = new StringField(meta.INVENTORY_ITEM_META.getStoreId());
       dispensedUnits = new StringField(meta.INVENTORY_ITEM_META.getDispensedUnitsId());
       multUnit = new StringField(meta.ORGANIZATION_META.ADDRESS.getMultipleUnit());
       streetAddress = new StringField(meta.ORGANIZATION_META.ADDRESS.getStreetAddress());
       city = new StringField(meta.ORGANIZATION_META.ADDRESS.getCity());
       state = new StringField(meta.ORGANIZATION_META.ADDRESS.getState());
       zipCode = new StringField(meta.ORGANIZATION_META.ADDRESS.getZipCode());
       toDescription = new StringField("toDescription");
       toStoreId = new StringField("toStoreId");
       toDispensedUnits = new StringField("toDispensedUnits");
       toLotNumber = new StringField("toLotNumber");
       toExpDate = new StringField("toExpDate");
   }
   
   public InvReceiptItemInfoForm(Node node) {
       this();
       createFields(node);
   }
   
   public InvReceiptItemInfoForm(String key) {
       this();
       this.key = key;
   }
   
   public AbstractField[] getFields() {
       return new AbstractField[] {
                                   storageLocationId,
                                   lotNumber,
                                   expirationDate,
                                   addToExisting,
                                   description,
                                   storeId,
                                   dispensedUnits,
                                   multUnit,
                                   streetAddress,
                                   city,
                                   state,
                                   zipCode,
                                   toDescription,
                                   toStoreId,
                                   toDispensedUnits,
                                   toLotNumber,
                                   toExpDate
       };
   }
}