package org.openelis.modules.organization.client;

import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.StringField;

public class ContactRow extends DataSet<Contact> {

    private static final long serialVersionUID = 1L;
    
    public DropDownField<Integer> contactType;
    public StringField name;
    public StringField multipleUnit;
    public StringField streetAddress;
    public StringField city;
    public DropDownField<String> state;
    public StringField zipCode;
    public DropDownField<String> country;
    public StringField workPhone;
    public StringField homePhone;
    public StringField cellPhone;
    public StringField faxPhone;
    public StringField email;
    
    public ContactRow() {
        add(contactType = new DropDownField<Integer>());
        add(name = new StringField());
        add(multipleUnit = new StringField());
        add(streetAddress = new StringField());
        add(city = new StringField());
        add(state = new DropDownField<String>());
        add(zipCode = new StringField());
        add(country = new DropDownField<String>());
        add(workPhone = new StringField());
        add(homePhone = new StringField());
        add(cellPhone = new StringField());
        add(faxPhone = new StringField());
        add(email = new StringField());
    }
    
    public ContactRow(ContactRow orig){
        add(contactType = (DropDownField<Integer>)orig.contactType.clone());
        add(name = (StringField)orig.name.clone());
        add(multipleUnit = (StringField)orig.multipleUnit.clone());
        add(streetAddress = (StringField)orig.streetAddress.clone());
        add(city = (StringField)orig.city.clone());
        add(state = (DropDownField<String>)orig.state.clone());
        add(zipCode = (StringField)orig.zipCode.clone());
        add(country = (DropDownField<String>)orig.country.clone());
        add(workPhone = (StringField)orig.workPhone.clone());
        add(homePhone = (StringField)orig.homePhone.clone());
        add(cellPhone = (StringField)orig.cellPhone.clone());
        add(faxPhone = (StringField)orig.faxPhone.clone());
        add(email = (StringField)orig.email.clone());
    }
    
    public Object clone() {
        return new ContactRow(this);
    }

}
