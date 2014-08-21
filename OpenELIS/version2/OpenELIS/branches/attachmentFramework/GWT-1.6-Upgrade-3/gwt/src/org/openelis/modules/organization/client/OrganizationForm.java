package org.openelis.modules.organization.client;

import com.google.gwt.xml.client.Node;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.metamap.OrganizationMetaMap;

/**
 * Specific instance of Form for loading and unloading data for 
 * the Organization Screen.
 * @author tschmidt
 *
 */
public class OrganizationForm extends Form<Integer> {

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
    
    public TableDataModel<TableDataRow<String>> countries;
    public TableDataModel<TableDataRow<String>> states;
    public TableDataModel<TableDataRow<Integer>> contactTypes;
    
    public OrganizationForm() {
        OrganizationMetaMap meta = new OrganizationMetaMap();
        id = new IntegerField(meta.getId());
        name = new StringField(meta.getName());
        street = new StringField(meta.ADDRESS.getStreetAddress());
        multipleUnit = new StringField(meta.ADDRESS.getMultipleUnit());
        city = new StringField(meta.ADDRESS.getCity());
        zipCode = new StringField(meta.ADDRESS.getZipCode());
        isActive = new CheckField(meta.getIsActive());
        parentOrg = new DropDownField<Integer>(meta.PARENT_ORGANIZATION.getName());
        state = new DropDownField<String>(meta.ADDRESS.getState());
        country = new DropDownField<String>(meta.ADDRESS.getCountry());
        contacts = new ContactsForm("contacts");
        notes = new NotesForm("notes");
    }
    
    public OrganizationForm(Node node) {
        this();
        createFields(node);
    }
    
    public AbstractField[] getFields() {
        return new AbstractField[] {
                                    id,
                                    name,
                                    street,
                                    multipleUnit,
                                    city,
                                    zipCode,
                                    isActive,
                                    parentOrg,
                                    state,
                                    country,
                                    contacts,
                                    notes
        };
    }
    
}
