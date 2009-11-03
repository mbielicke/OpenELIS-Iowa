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

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.metamap.InventoryReceiptMetaMap;

import com.google.gwt.xml.client.Node;


public class InvReceiptItemInfoForm extends Form{
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
    
    public InvReceiptItemInfoForm() {
       InventoryReceiptMetaMap meta = new InventoryReceiptMetaMap();
       fields.put(meta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getStorageLocationId(), storageLocationId = new DropDownField<Integer>());
       fields.put(meta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getLotNumber(), lotNumber = new StringField());
       fields.put(meta.TRANS_RECEIPT_LOCATION_META.INVENTORY_LOCATION_META.getExpirationDate(), expirationDate = new DateField());
       fields.put("addToExisiting", addToExisting = new CheckField());
       fields.put(meta.INVENTORY_ITEM_META.getDescription(), description = new StringField());
       fields.put(meta.INVENTORY_ITEM_META.getStoreId(), storeId = new StringField());
       fields.put(meta.INVENTORY_ITEM_META.getDispensedUnitsId(), dispensedUnits = new StringField());
       fields.put(meta.ORGANIZATION_META.ADDRESS.getMultipleUnit(), multUnit = new StringField());
       fields.put(meta.ORGANIZATION_META.ADDRESS.getStreetAddress(), streetAddress = new StringField());
       fields.put(meta.ORGANIZATION_META.ADDRESS.getCity(), city = new StringField());
       fields.put(meta.ORGANIZATION_META.ADDRESS.getState(), state = new StringField());
       fields.put(meta.ORGANIZATION_META.ADDRESS.getZipCode(), zipCode = new StringField());
   }
   
   public InvReceiptItemInfoForm(Node node) {
       this();
       createFields(node);
   }
}