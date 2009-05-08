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
package org.openelis.modules.environmentalSampleLogin.client;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.metamap.SampleEnvironmentalMetaMap;

import com.google.gwt.xml.client.Node;

public class SampleLocationForm extends Form<Integer> {

    private static final long serialVersionUID = 1L;

    public StringField samplingLocation;
    public StringField multUnit;
    public StringField streetName;
    public StringField city;
    public DropDownField<String> state;
    public StringField zipCode;
    public DropDownField<String> country;
    
    public TableDataModel<TableDataRow<String>> states;
    public TableDataModel<TableDataRow<String>> countries;
    
    public SampleLocationForm() {
        SampleEnvironmentalMetaMap meta = new SampleEnvironmentalMetaMap();
        samplingLocation = new StringField(meta.getSamplingLocation());
        multUnit = new StringField(meta.ADDRESS.getMultipleUnit());
        streetName = new StringField(meta.ADDRESS.getStreetAddress());
        city = new StringField(meta.ADDRESS.getCity());
        state = new DropDownField<String>(meta.ADDRESS.getState());
        zipCode = new StringField(meta.ADDRESS.getZipCode());
        country = new DropDownField<String>(meta.ADDRESS.getCountry());
   }
   
   public SampleLocationForm(Node node) {
       this();
       createFields(node);
   }
   
   public SampleLocationForm(String key) {
       this();
       this.key = key;
   }
   
   public AbstractField[] getFields() {
       return new AbstractField[] {
                                   samplingLocation,
                                   multUnit,
                                   streetName,
                                   city,
                                   state,
                                   zipCode,
                                   country
       };
   }
}
