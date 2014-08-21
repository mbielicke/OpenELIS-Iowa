package org.openelis.modules.provider.client;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.metamap.ProviderMetaMap;

import com.google.gwt.xml.client.Node;

public class ProviderForm extends Form {

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
    
    public ProviderForm() {
      ProviderMetaMap meta = new ProviderMetaMap();
      fields.put(meta.getId(), id = new IntegerField());
      fields.put(meta.getLastName(), lastName = new StringField());
      fields.put(meta.getFirstName(), firstName = new StringField());
      fields.put(meta.getNpi(),npi = new StringField());
      fields.put(meta.getMiddleName(), middleName = new StringField());
      fields.put(meta.getTypeId(), typeId = new DropDownField<Integer>());
      fields.put("addresses", addresses = new AddressesForm());     
      fields.put("notes", notes = new NotesForm());
    }
    
    public ProviderForm(Node node) {
        this();
        createFields(node);
    }
    
    
    

}
