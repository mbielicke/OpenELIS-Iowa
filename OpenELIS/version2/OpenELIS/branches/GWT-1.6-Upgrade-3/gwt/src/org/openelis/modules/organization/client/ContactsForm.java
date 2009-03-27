package org.openelis.modules.organization.client;

import com.google.gwt.xml.client.Node;

import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.common.data.TableField;

/**
 * Specific instance of Form for loading and unloading the 
 * ContactsTab on the Organization Screen.
 * 
 * This is included as a Sub-Form in OrganizationForm
 * @author tschmidt
 *
 */
public class ContactsForm extends Form<Integer> {
    
    private static final long serialVersionUID = 1L;
    
    public TableField<TableDataRow<Contact>> contacts;
    
    public ContactsForm() {
        contacts = new TableField<TableDataRow<Contact>>("contactsTable");
    }
    
    public ContactsForm(String key) {
        this();
        this.key = key;
    }
    
    public ContactsForm(Node node){
        this();
        createFields(node);
    } 
    
    public AbstractField[] getFields() {
        return new AbstractField[] {
                                    contacts
        };
    }

}
