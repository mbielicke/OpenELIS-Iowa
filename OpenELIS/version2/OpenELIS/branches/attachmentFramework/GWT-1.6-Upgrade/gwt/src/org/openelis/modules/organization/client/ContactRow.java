package org.openelis.modules.organization.client;

import org.openelis.gwt.common.data.DataSet;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.StringField;

public class ContactRow extends DataSet<Contact> {
    
    private static final long serialVersionUID = 1L;
    public DropDownField<Integer> type;
    public StringField name;
    public StringField multipleUnit;
    public StringField street;
    public StringField city;
    public DropDownField<String> state;
    public StringField zip;
    public DropDownField<String> country;
    public StringField workPhone;
    public StringField homePhone;
    public StringField cellPhone;
    public StringField faxPhone;
    public StringField email;
    
    public ContactRow() {
        add(type = new DropDownField<Integer>());
        add(name = new StringField());
        add(multipleUnit = new StringField());
        add(street = new StringField());
        add(city = new StringField());
        add(state = new DropDownField<String>());
        add(zip = new StringField());
        add(country = new DropDownField<String>());
        add(workPhone = new StringField());
        add(homePhone = new StringField());
        add(cellPhone = new StringField());
        add(faxPhone = new StringField());
        add(email = new StringField());
    }
}
