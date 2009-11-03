package org.openelis.modules.organization.client;

import com.google.gwt.xml.client.Node;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.TableField;

/**
 * Specific instance of Form for loading and unloading the 
 * ContactsTab on the Organization Screen.
 * 
 * This is included as a Sub-Form in OrganizationForm
 * @author tschmidt
 *
 */
public class ContactsForm extends Form {
    
    private static final long serialVersionUID = 1L;
    
    public TableField<Contact> contacts;
    
    public ContactsForm() {
        fields.put("contactsTable",contacts = new TableField<Contact>());
    }
    
    public ContactsForm(Node node){
        this();
        createFields(node);
    }
    

}
