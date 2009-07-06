package org.openelis.modules.organization.client;

import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.FieldType;
import org.openelis.gwt.common.data.StringField;
import org.openelis.gwt.common.data.TableDataRow;

import java.util.Arrays;
import java.util.List;

public class ContactRow extends TableDataRow<Contact> {

    private static final long serialVersionUID = 1L;
    
    public Integer contactType;
    public String name;
    public String multipleUnit;
    public String streetAddress;
    public String city;
    public String state;
    public String zipCode;
    public String country;
    public String workPhone;
    public String homePhone;
    public String cellPhone;
    public String faxPhone;
    public String email;
    
    
    public ContactRow() {
        /*
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
        */
    }
    
    public ContactRow(ContactRow orig){
        /*
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
        */
    }
    
    public Object clone() {
        return new ContactRow(this);
    }
    
    public List getCells() {
        DropDownField<Integer> contact = new DropDownField<Integer>();
        contact.setValue(new TableDataRow<Integer>(contactType));
        DropDownField<String> stateD = new DropDownField<String>();
        stateD.setValue(new TableDataRow<String>(state));
        DropDownField<String> countryD = new DropDownField<String>();
        countryD.setValue(new TableDataRow<String>(country));
        return Arrays.asList(new Object[] {
                                              contact,
                                              name,
                                              multipleUnit,
                                              streetAddress,
                                              city,
                                              stateD,
                                              zipCode,
                                              countryD,
                                              workPhone,
                                              homePhone,
                                              cellPhone,
                                              faxPhone,
                                              email
            
                             }
            
        );
    }
    

}
