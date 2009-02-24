package org.openelis.modules.organization.client;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.metamap.OrganizationMetaMap;

import com.google.gwt.xml.client.Node;

/**
 * Specific instance of Form for loading and unloading data for 
 * the Organization Screen.
 * @author tschmidt
 *
 */
public class OrganizationForm extends Form {

    private static final long serialVersionUID = 1L;
    
    public IntegerField id;
    public Integer addressId;
    public StringField name;
    public StringField street;
    public StringField multipleUnit;
    public StringField city;
    public StringField zipCode;
    public CheckField isActive;
    public DropDownField<Integer> parentOrg;
    public DropDownField<String> state;
    public DropDownField<String> country;
    public ContactsForm contacts;
    public NotesForm notes;
    public String orgTabPanel = "contactsTab"; 
    
    public OrganizationForm() {
        OrganizationMetaMap meta = new OrganizationMetaMap();
        fields.put(meta.getId(), id = new IntegerField());
        fields.put(meta.getName(),name = new StringField());
        fields.put(meta.ADDRESS.getStreetAddress(), street = new StringField());
        fields.put(meta.ADDRESS.getMultipleUnit(), multipleUnit = new StringField());
        fields.put(meta.ADDRESS.getCity(), city = new StringField());
        fields.put(meta.ADDRESS.getZipCode(), zipCode = new StringField());
        fields.put(meta.getIsActive(), isActive = new CheckField());
        fields.put(meta.PARENT_ORGANIZATION.getName(), parentOrg = new DropDownField<Integer>());
        fields.put(meta.ADDRESS.getState(), state = new DropDownField<String>());
        fields.put(meta.ADDRESS.getCountry(), country = new DropDownField<String>());
        fields.put("contacts", contacts = new ContactsForm());
        fields.put("notes", notes = new NotesForm());
    }
    
    public OrganizationForm(Node node) {
        this();
        createFields(node);
    }
    
    

}
