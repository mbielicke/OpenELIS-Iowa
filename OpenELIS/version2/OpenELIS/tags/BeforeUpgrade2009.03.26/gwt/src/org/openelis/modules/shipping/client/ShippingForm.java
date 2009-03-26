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

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.DateField;
import org.openelis.gwt.common.data.DoubleField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.metamap.ShippingMetaMap;

import com.google.gwt.xml.client.Node;

public class ShippingForm extends Form{
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
    
    //sub forms
    public ShippingItemsForm shippingItemsForm;
    public ShippingNotesForm shippingNotesForm;
    
    
    public ShippingForm() {
       ShippingMetaMap meta = new ShippingMetaMap();
       fields.put(meta.getId(), id = new IntegerField());
       fields.put(meta.getStatusId(),statusId = new DropDownField<Integer>());
       fields.put(meta.getNumberOfPackages(), numberOfPackages = new IntegerField());
       fields.put(meta.getCost(), cost = new DoubleField());
       fields.put(meta.getShippedFromId(), shippedFromId = new DropDownField<Integer>());
       fields.put(meta.ORGANIZATION_META.getName(), organization = new DropDownField<Integer>());
       fields.put(meta.getProcessedDate(), processedDate = new DateField());
       fields.put(meta.getProcessedById(), processedBy = new StringField());
       fields.put(meta.getShippedDate(), shippedDate = new DateField());
       fields.put(meta.getShippedMethodId(), shippedMethodId = new DropDownField<Integer>());
       fields.put(meta.ORGANIZATION_META.ADDRESS.getMultipleUnit(), multipleUnit = new StringField());
       fields.put(meta.ORGANIZATION_META.ADDRESS.getStreetAddress(), streetAddress = new StringField());
       fields.put(meta.ORGANIZATION_META.ADDRESS.getCity(), city = new StringField());
       fields.put(meta.ORGANIZATION_META.ADDRESS.getState(), state = new StringField());
       fields.put(meta.ORGANIZATION_META.ADDRESS.getZipCode(), zipcode = new StringField());

       fields.put("shippingItems", shippingItemsForm = new ShippingItemsForm());
       fields.put("orderShippingNotes", shippingNotesForm = new ShippingNotesForm());
   }
   
   public ShippingForm(Node node) {
       this();
       createFields(node);
   }
}