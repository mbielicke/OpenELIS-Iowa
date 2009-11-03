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
package org.openelis.modules.shipping.client;

import org.openelis.gwt.common.data.deprecated.AbstractField;
import org.openelis.gwt.common.data.deprecated.DateField;
import org.openelis.gwt.common.data.deprecated.DoubleField;
import org.openelis.gwt.common.data.deprecated.DropDownField;
import org.openelis.gwt.common.data.deprecated.IntegerField;
import org.openelis.gwt.common.data.deprecated.StringField;
import org.openelis.gwt.common.data.deprecated.TableDataModel;
import org.openelis.gwt.common.data.deprecated.TableDataRow;
import org.openelis.gwt.common.deprecated.Form;
import org.openelis.metamap.ShippingMetaMap;

import com.google.gwt.xml.client.Node;

public class ShippingForm extends Form<Integer>{
    private static final long serialVersionUID = 1L;

    public IntegerField id;
    public DropDownField<Integer> statusId;
    public IntegerField numberOfPackages;
    public DoubleField cost;
    public DropDownField<Integer> shippedFromId;
    public DropDownField<Integer> organization;
    public DateField processedDate;
    public StringField processedBy;
    public DateField shippedDate;
    public DropDownField<Integer> shippedMethodId;
    public StringField multipleUnit;
    public StringField streetAddress;
    public StringField city;
    public StringField state;
    public StringField zipcode;
    
    public String shippingTabPanel = "itemsTab";
    public Integer systemUserId;

    public TableDataModel<TableDataRow<Integer>> checkedOrderIds;
    
    //sub forms
    public ShippingItemsForm shippingItemsForm;
    public ShippingNotesForm shippingNotesForm;
    
    
    public ShippingForm() {
       ShippingMetaMap meta = new ShippingMetaMap();
       id = new IntegerField(meta.getId());
       statusId = new DropDownField<Integer>(meta.getStatusId());
       numberOfPackages = new IntegerField(meta.getNumberOfPackages());
       cost = new DoubleField(meta.getCost());
       shippedFromId = new DropDownField<Integer>(meta.getShippedFromId());
       organization = new DropDownField<Integer>(meta.ORGANIZATION_META.getName());
       processedDate = new DateField(meta.getProcessedDate());
       processedBy = new StringField(meta.getProcessedById());
       shippedDate = new DateField(meta.getShippedDate());
       shippedMethodId = new DropDownField<Integer>(meta.getShippedMethodId());
       multipleUnit = new StringField(meta.ORGANIZATION_META.ADDRESS.getMultipleUnit());
       streetAddress = new StringField(meta.ORGANIZATION_META.ADDRESS.getStreetAddress());
       city = new StringField(meta.ORGANIZATION_META.ADDRESS.getCity());
       state = new StringField(meta.ORGANIZATION_META.ADDRESS.getState());
       zipcode = new StringField(meta.ORGANIZATION_META.ADDRESS.getZipCode());
       shippingItemsForm = new ShippingItemsForm("shippingItems");
       shippingNotesForm = new ShippingNotesForm("orderShippingNotes");
   }
   
   public ShippingForm(Node node) {
       this();
       createFields(node);
   }
   
   public AbstractField[] getFields() {
       return new AbstractField[] {
                                     id,
                                     statusId,
                                     numberOfPackages,
                                     cost,
                                     shippedFromId,
                                     organization,
                                     processedDate,
                                     processedBy,
                                     shippedDate,
                                     shippedMethodId,
                                     multipleUnit,
                                     streetAddress,
                                     city,
                                     state,
                                     zipcode,
                                     shippingItemsForm,
                                     shippingNotesForm
       };
   }
}