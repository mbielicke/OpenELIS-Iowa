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
import org.openelis.gwt.common.data.StringField;
import org.openelis.metamap.OrderMetaMap;

import com.google.gwt.xml.client.Node;

public class ReportToBillToForm extends Form{
    private static final long serialVersionUID = 1L;
    
    public DropDownField<Integer> reportTo;
    public DropDownField<Integer> billTo;
    
    //report to address
    public StringField reportToMultUnit;
    public StringField reportToStreetAddress;
    public StringField reportToCity;
    public StringField reportToState;
    public StringField reportToZipCode;
    
    //bill to address
    public StringField billToMultUnit;
    public StringField billToStreetAddress;
    public StringField billToCity;
    public StringField billToState;
    public StringField billToZipCode;
    
    public ReportToBillToForm() {
        OrderMetaMap meta = new OrderMetaMap();
        
        fields.put(meta.ORDER_REPORT_TO_META.getName(), reportTo = new DropDownField<Integer>());
        fields.put(meta.ORDER_BILL_TO_META.getName(), billTo = new DropDownField<Integer>());
        
        fields.put(meta.ORDER_REPORT_TO_META.ADDRESS.getMultipleUnit(), reportToMultUnit = new StringField());
        fields.put(meta.ORDER_REPORT_TO_META.ADDRESS.getStreetAddress(), reportToStreetAddress = new StringField());
        fields.put(meta.ORDER_REPORT_TO_META.ADDRESS.getCity(), reportToCity = new StringField());
        fields.put(meta.ORDER_REPORT_TO_META.ADDRESS.getState(), reportToState = new StringField());
        fields.put(meta.ORDER_REPORT_TO_META.ADDRESS.getZipCode(), reportToZipCode = new StringField());
        
        fields.put(meta.ORDER_BILL_TO_META.ADDRESS.getMultipleUnit(), billToMultUnit = new StringField());
        fields.put(meta.ORDER_BILL_TO_META.ADDRESS.getStreetAddress(), billToStreetAddress = new StringField());
        fields.put(meta.ORDER_BILL_TO_META.ADDRESS.getCity(), billToCity = new StringField());
        fields.put(meta.ORDER_BILL_TO_META.ADDRESS.getState(), billToState = new StringField());
        fields.put(meta.ORDER_BILL_TO_META.ADDRESS.getZipCode(), billToZipCode = new StringField());
    }
    
    public ReportToBillToForm(Node node){
        this();
        createFields(node);
    }
    
}
