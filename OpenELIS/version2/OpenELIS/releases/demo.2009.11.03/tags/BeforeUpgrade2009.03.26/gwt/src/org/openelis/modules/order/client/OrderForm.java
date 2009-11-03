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
package org.openelis.modules.order.client;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.metamap.OrderMetaMap;

import com.google.gwt.xml.client.Node;

public class OrderForm extends Form{
    private static final long serialVersionUID = 1L;

    public IntegerField id;
    public IntegerField neededInDays;
    public DropDownField<Integer> statusId;
    public StringField orderedDate;
    public StringField requestedBy;
    public DropDownField<Integer> costCenterId;
    public DropDownField<Integer> shipFromId;
    public DropDownField<String> description;
    
    public DropDownField<OrderOrgKey> organization;
    public StringField externalOrderNumber;
    public StringField multipleUnit;
    public StringField streetAddress;
    public StringField city;
    public StringField state;
    public StringField zipCode;
    
    public String orderTabPanel = "itemsTab";
    
    //sub forms
    public OrderShippingNoteForm shippingNotes;
    public OrderNoteForm customerNotes;
    public ItemsForm items;
    public ReceiptForm receipts;
    public ReportToBillToForm reportToBillTo;
   
    public OrderForm() {
       OrderMetaMap meta = new OrderMetaMap();
       fields.put(meta.getId(), id = new IntegerField());
       fields.put(meta.getNeededInDays(),neededInDays = new IntegerField());
       fields.put(meta.getStatusId(), statusId = new DropDownField<Integer>());
       fields.put(meta.getOrderedDate(), orderedDate = new StringField());
       fields.put(meta.getRequestedBy(), requestedBy = new StringField());
       fields.put(meta.getCostCenterId(), costCenterId = new DropDownField<Integer>());
       fields.put(meta.getExternalOrderNumber(), externalOrderNumber = new StringField());
       fields.put(meta.getShipFromId(), shipFromId = new DropDownField<Integer>());
       fields.put(meta.getDescription(), description = new DropDownField<String>());
       
       fields.put(meta.ORDER_ORGANIZATION_META.getName(), organization = new DropDownField<OrderOrgKey>());
       fields.put(meta.ORDER_ORGANIZATION_META.ADDRESS.getMultipleUnit(), multipleUnit = new StringField());
       fields.put(meta.ORDER_ORGANIZATION_META.ADDRESS.getStreetAddress(), streetAddress = new StringField());
       fields.put(meta.ORDER_ORGANIZATION_META.ADDRESS.getCity(), city = new StringField());
       fields.put(meta.ORDER_ORGANIZATION_META.ADDRESS.getState(), state = new StringField());
       fields.put(meta.ORDER_ORGANIZATION_META.ADDRESS.getZipCode(), zipCode = new StringField());
       
       fields.put("shippingNote", shippingNotes = new OrderShippingNoteForm());
       fields.put("custNote", customerNotes = new OrderNoteForm());
       fields.put("items", items = new ItemsForm());
       fields.put("receipts", receipts = new ReceiptForm());
       fields.put("reportToBillTo", reportToBillTo = new ReportToBillToForm());
   }
   
   public OrderForm(Node node) {
       this();
       createFields(node);
   }
}
