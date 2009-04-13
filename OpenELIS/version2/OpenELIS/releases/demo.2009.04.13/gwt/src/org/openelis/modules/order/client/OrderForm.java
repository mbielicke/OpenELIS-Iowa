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

import com.google.gwt.xml.client.Node;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.metamap.OrderMetaMap;

public class OrderForm extends Form<Integer> {
    private static final long serialVersionUID = 1L;

    public IntegerField id;
    public IntegerField neededInDays;
    public DropDownField<Integer> statusId;
    public StringField orderedDate;
    public StringField requestedBy;
    public DropDownField<Integer> costCenterId;
    public DropDownField<Integer> shipFromId;
    public DropDownField<String> description;
    
    public DropDownField<Integer> organization;
    public StringField externalOrderNumber;
    public StringField multipleUnit;
    public StringField streetAddress;
    public StringField city;
    public StringField state;
    public StringField zipCode;
    
    public String orderTabPanel = "itemsTab";
    public String type;
    
    //sub forms
    public OrderShippingNoteForm shippingNotes;
    public OrderNoteForm customerNotes;
    public ItemsForm items;
    public ReceiptForm receipts;
    public ReportToBillToForm reportToBillTo;
    
    public TableDataModel<TableDataRow<Integer>> status;
    public TableDataModel<TableDataRow<Integer>> costCenters;
    public TableDataModel<TableDataRow<Integer>> shipFrom;
    
    public String orderType;
    public Integer originalStatus;

    
    public OrderForm() {
       OrderMetaMap meta = new OrderMetaMap();
       id = new IntegerField(meta.getId());
       neededInDays = new IntegerField(meta.getNeededInDays());
       statusId = new DropDownField<Integer>(meta.getStatusId());
       orderedDate = new StringField(meta.getOrderedDate());
       requestedBy = new StringField(meta.getRequestedBy());
       costCenterId = new DropDownField<Integer>(meta.getCostCenterId());
       externalOrderNumber = new StringField(meta.getExternalOrderNumber());
       shipFromId = new DropDownField<Integer>(meta.getShipFromId());
       description = new DropDownField<String>(meta.getDescription());
       organization = new DropDownField<Integer>(meta.ORDER_ORGANIZATION_META.getName());
       multipleUnit = new StringField(meta.ORDER_ORGANIZATION_META.ADDRESS.getMultipleUnit());
       streetAddress = new StringField(meta.ORDER_ORGANIZATION_META.ADDRESS.getStreetAddress());
       city = new StringField(meta.ORDER_ORGANIZATION_META.ADDRESS.getCity());
       state = new StringField(meta.ORDER_ORGANIZATION_META.ADDRESS.getState());
       zipCode = new StringField(meta.ORDER_ORGANIZATION_META.ADDRESS.getZipCode());
       shippingNotes = new OrderShippingNoteForm("shippingNote");
       customerNotes = new OrderNoteForm("custNote");
       items = new ItemsForm("items");
       receipts = new ReceiptForm("receipts");
       reportToBillTo = new ReportToBillToForm("reportToBillTo");
   }
   
   public OrderForm(Node node) {
       this();
       createFields(node);
   }
   
   public AbstractField[] getFields() {
       return new AbstractField[] {
                                     id,
                                     neededInDays,
                                     statusId,
                                     orderedDate,
                                     requestedBy,
                                     costCenterId,
                                     externalOrderNumber,
                                     shipFromId,
                                     description,
                                     organization,
                                     multipleUnit,
                                     streetAddress,
                                     city,
                                     state,
                                     zipCode,
                                     shippingNotes,
                                     customerNotes,
                                     items,
                                     receipts,
                                     reportToBillTo
       };
   }
}
