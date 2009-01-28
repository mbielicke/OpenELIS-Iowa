package org.openelis.modules.organization.client;

import com.google.gwt.xml.client.Node;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.NumberField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.metamap.OrganizationMetaMap;

/**
 * Specific instance of Form for loading and unloading data for 
 * the Organization Screen.
 * @author tschmidt
 *
 */
public class OrganizationForm extends Form {

    private static final long serialVersionUID = 1L;
    
    public NumberField id;
    public Integer addressId;
    public StringField name;
    public StringField street;
    public StringField multipleUnit;
    public StringField city;
    public StringField zipCode;
    public CheckField isActive;
    public DropDownField parentOrg;
    public DropDownField state;
    public DropDownField country;
    public ContactsForm contacts;
    public NotesForm notes;
    public String orgTabPanel = "contactsTab"; 
    
    public OrganizationForm() {
        OrganizationMetaMap meta = new OrganizationMetaMap();
        fields.put(meta.getId(), id = new NumberField());
        fields.put(meta.getName(),name = new StringField());
        fields.put(meta.ADDRESS.getStreetAddress(), street = new StringField());
        fields.put(meta.ADDRESS.getMultipleUnit(), multipleUnit = new StringField());
        fields.put(meta.ADDRESS.getCity(), city = new StringField());
        fields.put(meta.ADDRESS.getZipCode(), zipCode = new StringField());
        fields.put(meta.getIsActive(), isActive = new CheckField());
        fields.put(meta.PARENT_ORGANIZATION.getName(), parentOrg = new DropDownField());
        fields.put(meta.ADDRESS.getState(), state = new DropDownField());
        fields.put(meta.ADDRESS.getCountry(), country = new DropDownField());
        fields.put("contacts", contacts = new ContactsForm());
        fields.put("notes", notes = new NotesForm());
    }
    
    public OrganizationForm(Node node) {
        this();
        createFields(node);
    }
    
    

}
