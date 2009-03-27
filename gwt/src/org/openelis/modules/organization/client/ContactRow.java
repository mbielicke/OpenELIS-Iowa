package org.openelis.modules.organization.client;

import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableDataRow;

import java.util.Arrays;
import java.util.List;

public class ContactRow extends TableDataRow<Contact> {

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
        contactType = new DropDownField<Integer>();
        name = new StringField();
        multipleUnit = new StringField();
        streetAddress = new StringField();
        city = new StringField();
        state = new DropDownField<String>();
        zipCode = new StringField();
        country = new DropDownField<String>();
        workPhone = new StringField();
        homePhone = new StringField();
        cellPhone = new StringField();
        faxPhone = new StringField();
        email = new StringField();
    }
    
    public ContactRow(ContactRow orig){
        contactType = (DropDownField<Integer>)orig.contactType.clone();
        name = (StringField)orig.name.clone();
        multipleUnit = (StringField)orig.multipleUnit.clone();
        streetAddress = (StringField)orig.streetAddress.clone();
        city = (StringField)orig.city.clone();
        state = (DropDownField<String>)orig.state.clone();
        zipCode = (StringField)orig.zipCode.clone();
        country = (DropDownField<String>)orig.country.clone();
        workPhone = (StringField)orig.workPhone.clone();
        homePhone = (StringField)orig.homePhone.clone();
        cellPhone = (StringField)orig.cellPhone.clone();
        faxPhone = (StringField)orig.faxPhone.clone();
        email = (StringField)orig.email.clone();
    }
    
    public Object clone() {
        return new ContactRow(this);
    }
    
    public List<FieldType> getCells() {
        return Arrays.asList(new FieldType[] {
                                              contactType,
                                              name,
                                              multipleUnit,
                                              streetAddress,
                                              city,
                                              state,
                                              zipCode,
                                              country,
                                              workPhone,
                                              homePhone,
                                              cellPhone,
                                              faxPhone,
                                              email
            
                             }
            
        );
    }
    

}
