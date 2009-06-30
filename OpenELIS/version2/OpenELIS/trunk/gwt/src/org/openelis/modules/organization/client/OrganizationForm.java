package org.openelis.modules.organization.client;

import com.google.gwt.xml.client.Node;

import org.openelis.domain.OrganizationAddressDO;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.CheckField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.IntegerField;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.StringObject;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.metamap.OrganizationMetaMap;

import java.io.Serializable;

/**
 * Specific instance of Form for loading and unloading data for 
 * the Organization Screen.
 * @author tschmidt
 *
 */
public class OrganizationForm extends Form<Integer> {

    private static final long serialVersionUID = 1L;
    
    public transient IntegerField id;  
    public transient StringField name;
    public transient StringField street;
    public transient StringField multipleUnit;
    public transient StringField city;
    public transient StringField zipCode;
    public transient CheckField isActive;
    public transient DropDownField<Integer> parentOrg;
    public transient DropDownField<String> state;
    public transient DropDownField<String> country;
    public ContactsForm contacts;
    public NotesForm notes;
    public String orgTabPanel = "contactsTab";
    public transient Integer addressId;
    
    
    public OrganizationAddressDO data;
    
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
    
    public void load(OrganizationForm form) {
        id.setValue(form.data.getOrganizationId());
        name.setValue(form.data.getName());
        isActive.setValue(form.data.getIsActive());
        //addressId = organizationDO.getAddressDO().getId();
        street.setValue(form.data.getAddressDO().getStreetAddress());
        multipleUnit.setValue(form.data.getAddressDO().getMultipleUnit());
        city.setValue(form.data.getAddressDO().getCity());
        zipCode.setValue(form.data.getAddressDO().getZipCode());
        state.setValue(new TableDataRow<String>(form.data.getAddressDO().getState()));
        country.setValue(new TableDataRow<String>(form.data.getAddressDO().getCountry()));
        
        //we need to create a dataset for the parent organization auto complete
        if(form.data.getParentOrganizationId() != null){
            TableDataModel<TableDataRow<Integer>> model = new TableDataModel<TableDataRow<Integer>>();
            model.add(new TableDataRow<Integer>(form.data.getParentOrganizationId(),new StringObject(form.data.getParentOrganization())));
            parentOrg.setModel(model);
            parentOrg.setValue(new TableDataRow<Integer>(form.data.getParentOrganizationId()));
        }
        addressId = form.data.getAddressDO().getId();
        if(form.contacts != null){
            contacts.load(form.contacts.data);
        }
        if(form.notes != null) {
            notes.load(form.notes.data);
        }
    }
    
    public void unload() {
        data = new OrganizationAddressDO();
        
        data.setOrganizationId(id.getValue());
        data.setName(name.getValue());
        data.setIsActive(isActive.getValue());
        data.setParentOrganizationId((Integer)parentOrg.getSelectedKey());      
        //newOrganizationDO.setParentOrganization()form.parentOrg.getTextValue());
        
        //organization address value
        data.getAddressDO().setId(addressId);
        data.getAddressDO().setMultipleUnit(multipleUnit.getValue());
        data.getAddressDO().setStreetAddress(street.getValue());
        data.getAddressDO().setCity(city.getValue());
        data.getAddressDO().setState((String)state.getSelectedKey());
        data.getAddressDO().setZipCode(zipCode.getValue());
        data.getAddressDO().setCountry((String)country.getSelectedKey());
        
    }
}
