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

package org.openelis.modules.provider.client;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.metamap.ProviderMetaMap;

import com.google.gwt.xml.client.Node;

public class ProviderForm extends Form<Integer> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public IntegerField id;
    public StringField lastName;
    public StringField firstName;
    public StringField npi;
    public StringField middleName;
    public DropDownField<Integer> typeId; 
    public NotesForm notes;
    public AddressesForm addresses;
    public String provTabPanel = "addressesTab";
    
    public TableDataModel<TableDataRow<String>> countries;
    public TableDataModel<TableDataRow<String>> states;
    public TableDataModel<TableDataRow<Integer>> providerTypes;
    
    public ProviderForm() {
      ProviderMetaMap meta = new ProviderMetaMap();
      id = new IntegerField(meta.getId());
      lastName = new StringField(meta.getLastName());
      firstName = new StringField(meta.getFirstName());
      npi = new StringField(meta.getNpi());
      middleName = new StringField(meta.getMiddleName());
      typeId = new DropDownField<Integer>(meta.getTypeId());
      addresses = new AddressesForm("addresses");     
      notes = new NotesForm("notes");
    }
    
    public ProviderForm(Node node) {
        this();
        createFields(node);
    }
    
    public AbstractField[] getFields() {
        return new AbstractField[] {
                                   id,
                                   lastName,
                                   firstName,
                                   npi,
                                   middleName,
                                   typeId,
                                   addresses,
                                   notes
        };
    }
    
    
    

}
