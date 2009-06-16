package org.openelis.modules.organization.client;

import com.google.gwt.xml.client.Node;

import org.openelis.domain.OrganizationContactDO;
import org.openelis.gwt.common.Form;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.data.AbstractField;
import org.openelis.gwt.common.data.DropDownField;
import org.openelis.gwt.common.data.TableDataModel;
import org.openelis.gwt.common.data.TableDataRow;
import org.openelis.gwt.common.data.TableField;

import java.util.ArrayList;
import java.util.List;

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
    
    public transient TableField<TableDataRow<Contact>> contacts;
    public transient Integer orgId;
    
    public ArrayList<OrganizationContactDO> data;
    
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
    
    public void load(ArrayList<OrganizationContactDO> data) {
        TableDataModel<TableDataRow<Contact>> model = new TableDataModel<TableDataRow<Contact>>();
        for(int iter = 0;iter < data.size(); iter++) {
            OrganizationContactDO contactRow = (OrganizationContactDO)data.get(iter);
        
           //TableDataRow<Contact> row = (TableDataRow<Contact>)contactsModel.createNewSet();
           ContactRow row = new ContactRow();
           Contact key = new Contact();
           key.orgId = contactRow.getId();
           key.addId = contactRow.getAddressDO().getId();
           row.key = key;
           
           row.contactType = contactRow.getContactType();
           row.name = (contactRow.getName());
           row.multipleUnit = (contactRow.getAddressDO().getMultipleUnit());
           row.streetAddress = (contactRow.getAddressDO().getStreetAddress());
           row.city = (contactRow.getAddressDO().getCity());          
           row.state= (contactRow.getAddressDO().getState());
           row.zipCode = (contactRow.getAddressDO().getZipCode());
           row.country = ((contactRow.getAddressDO().getCountry()));
           row.workPhone = (contactRow.getAddressDO().getWorkPhone());
           row.homePhone = (contactRow.getAddressDO().getHomePhone());
           row.cellPhone = (contactRow.getAddressDO().getCellPhone());
           row.faxPhone = (contactRow.getAddressDO().getFaxPhone());
           row.email = (contactRow.getAddressDO().getEmail());  
           orgId = contactRow.getOrganization();
           /*
           row.cells[0].setValue(new TableDataRow<Integer>(contactRow.getContactType()));
           row.cells[1].setValue(contactRow.getName());
           row.cells[2].setValue(contactRow.getAddressDO().getMultipleUnit());
           row.cells[3].setValue(contactRow.getAddressDO().getStreetAddress());
           row.cells[4].setValue(contactRow.getAddressDO().getCity());          
           row.cells[5].setValue(new TableDataRow<String>(contactRow.getAddressDO().getState()));
           row.cells[6].setValue(contactRow.getAddressDO().getZipCode());
           row.cells[7].setValue(new TableDataRow<String>(contactRow.getAddressDO().getCountry()));
           row.cells[8].setValue(contactRow.getAddressDO().getWorkPhone());
           row.cells[9].setValue(contactRow.getAddressDO().getHomePhone());
           row.cells[10].setValue(contactRow.getAddressDO().getCellPhone());
           row.cells[11].setValue(contactRow.getAddressDO().getFaxPhone());
           row.cells[12].setValue(contactRow.getAddressDO().getEmail());
           */
           model.add(row);
           
            }
        contacts.setValue(model);
        
    }

    public void unload() {
        data = new ArrayList<OrganizationContactDO>();
        List<TableDataRow<Contact>> deletedRows = contacts.getValue().getDeletions();
        
        for(int i=0; i< contacts.getValue().size(); i++){
            OrganizationContactDO contactDO = new OrganizationContactDO();
            //DataSet<Contact> row = contactsTable.get(i);
            ContactRow row = (ContactRow)contacts.getValue().get(i);
            if(row.key != null)
                contactDO.setId(row.key.orgId);
            
            contactDO.setOrganization(orgId);
            contactDO.setName(row.name);
            contactDO.getAddressDO().setId(row.key.addId);
            contactDO.setContactType(row.contactType);
            contactDO.getAddressDO().setMultipleUnit(row.multipleUnit);
            contactDO.getAddressDO().setStreetAddress(row.streetAddress);
            contactDO.getAddressDO().setCity(row.city);
            contactDO.getAddressDO().setState(row.state);
            contactDO.getAddressDO().setZipCode(row.zipCode);
            contactDO.getAddressDO().setCountry(row.country);
            contactDO.getAddressDO().setWorkPhone(row.workPhone);
            contactDO.getAddressDO().setHomePhone(row.homePhone);
            contactDO.getAddressDO().setCellPhone(row.cellPhone);
            contactDO.getAddressDO().setFaxPhone(row.faxPhone);
            contactDO.getAddressDO().setEmail(row.email);
            /*
            contactDO.setOrganization(orgId);
            contactDO.setName((String)row.cells[1].getValue());
            contactDO.getAddressDO().setId(row.key.addId);
            contactDO.setContactType((Integer)((DropDownField<Integer>)row.cells[0]).getSelectedKey());
            contactDO.getAddressDO().setMultipleUnit((String)row.cells[2].getValue());
            contactDO.getAddressDO().setStreetAddress((String)row.cells[3].getValue());
            contactDO.getAddressDO().setCity((String)row.cells[4].getValue());
            contactDO.getAddressDO().setState((String)((DropDownField<String>)row.cells[5]).getSelectedKey());
            contactDO.getAddressDO().setZipCode((String)row.cells[6].getValue());
            contactDO.getAddressDO().setCountry((String)((DropDownField<String>)row.cells[7]).getSelectedKey());
            contactDO.getAddressDO().setWorkPhone((String)row.cells[8].getValue());
            contactDO.getAddressDO().setHomePhone((String)row.cells[9].getValue());
            contactDO.getAddressDO().setCellPhone((String)row.cells[10].getValue());
            contactDO.getAddressDO().setFaxPhone((String)row.cells[11].getValue());
            contactDO.getAddressDO().setEmail((String)row.cells[12].getValue());
            */
            data.add(contactDO);    
        }
        
        if(deletedRows != null){
            for(int j=0; j<deletedRows.size(); j++){
                TableDataRow<Contact> deletedRow = deletedRows.get(j);
                if(deletedRow.key != null){
                    OrganizationContactDO contactDO = new OrganizationContactDO();
                    contactDO.setDelete(true);
                    contactDO.setId(deletedRow.key.orgId);
                    contactDO.getAddressDO().setId(deletedRow.key.addId);
                    
                    data.add(contactDO);
                }
            }
        }    
    }
}
