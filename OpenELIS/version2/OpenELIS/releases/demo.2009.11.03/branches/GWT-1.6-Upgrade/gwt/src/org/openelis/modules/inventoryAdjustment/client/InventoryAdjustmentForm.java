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

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableField;
import org.openelis.metamap.InventoryAdjustmentMetaMap;

import com.google.gwt.xml.client.Node;

public class InventoryAdjustmentForm extends Form{
    private static final long serialVersionUID = 1L;

    public IntegerField id;
    public StringField description;
    public DateField adjustmentDate;
    public StringField systemUser;
    public DropDownField<Integer> storeId;
    public TableField<Integer> adjustmentsTable;
    public Integer systemUserId;
    
    public InventoryAdjustmentForm() {
       InventoryAdjustmentMetaMap meta = new InventoryAdjustmentMetaMap();
       fields.put(meta.getId(), id = new IntegerField());
       fields.put(meta.getDescription(), description = new StringField());
       fields.put(meta.getAdjustmentDate(), adjustmentDate = new DateField());
       fields.put(meta.getSystemUserId(), systemUser = new StringField());
       fields.put(meta.TRANS_ADJUSTMENT_LOCATION_META.INVENTORY_LOCATION_META.INVENTORY_ITEM_META.getStoreId(), storeId = new DropDownField<Integer>());
       fields.put("adjustmentsTable", adjustmentsTable = new TableField<Integer>());
   }
   
   public InventoryAdjustmentForm(Node node) {
       this();
       createFields(node);
   }
}