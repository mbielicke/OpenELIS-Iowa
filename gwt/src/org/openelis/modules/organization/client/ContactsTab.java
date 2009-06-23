package org.openelis.modules.organization.client;

import java.util.ArrayList;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.OrganizationContactDO;
import org.openelis.gwt.common.rewrite.data.TableDataRow;
import org.openelis.gwt.event.CommandListenerCollection;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.DataChangeHandler;
import org.openelis.gwt.event.HasDataChangeHandlers;
import org.openelis.gwt.event.HasStateChangeHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.event.StateChangeHandler;
import org.openelis.gwt.screen.rewrite.Screen;
import org.openelis.gwt.screen.rewrite.ScreenDef;
import org.openelis.gwt.screen.rewrite.ScreenEventHandler;
import org.openelis.gwt.screen.rewrite.Screen.State;
import org.openelis.gwt.widget.rewrite.AppButton;
import org.openelis.gwt.widget.HandlesEvents;
import org.openelis.gwt.widget.rewrite.Dropdown;
import org.openelis.gwt.widget.table.rewrite.TableModel;
import org.openelis.gwt.widget.table.rewrite.TableWidget;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

public class ContactsTab extends Screen {
	
	private ScreenDef def;
	private ContactsRPC rpc;
	private TableWidget table;

	public ContactsTab(ScreenDef def) {
		this.def = def;
		setHandlers();
	}
	
	private void setHandlers() {
		table = (TableWidget)def.getWidget("contactsTable");
		addScreenHandler(table,new ScreenEventHandler<ArrayList<TableDataRow>>() {
			public void onDataChange(DataChangeEvent event) {
				table.model.load(getTableModel());
			}
			public void onValueChange(ValueChangeEvent<ArrayList<TableDataRow>> event) {
				rpc.orgContacts = getContacts();
			}
			public void onStateChange(StateChangeEvent<State> event) {
				if(event.getState() == State.ADD || event.getState() == State.UPDATE) {
					table.enabled(true);
					table.model.enableAutoAdd(true);
				}else if(event.getState() == State.QUERY) {
					table.enabled(true);
				}else{
					table.enabled(false);
					table.model.enableAutoAdd(false);
				}
			}
		});
    	final AppButton removeContact = (AppButton)def.getWidget("removeContactButton");
    	addScreenHandler(removeContact,new ScreenEventHandler<Object>() {
    		public void onStateChange(StateChangeEvent<State> event) {
    			if(event.getState() == State.QUERY)
    				removeContact.changeState(AppButton.ButtonState.DISABLED);
    		}
    		
    	});
    	removeContact.addClickListener(new ClickListener() {
    		public void onClick(Widget sender) {
    	        int selectedRow = table.model.getSelectedIndex();
    	        if (selectedRow > -1 && table.model.numRows() > 0) {
    	            table.model.deleteRow(selectedRow);
    	        }
    		}
    	});
	}
	
	private ArrayList<TableDataRow> getTableModel() {
		ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
		if(rpc.orgContacts == null)
			return model;
    	try 
        {	
    		for(int iter = 0;iter < rpc.orgContacts.size();iter++) {
    			OrganizationContactDO contactRow = (OrganizationContactDO)rpc.orgContacts.get(iter);
    		
               //TableDataRow<Contact> row = (TableDataRow<Contact>)table.model.createRow();
    		   TableDataRow row = new TableDataRow(13);
               Contact key = new Contact();
               key.orgId = contactRow.getId();
               key.addId = contactRow.getAddressDO().getId();
               row.key = key;
               /*
               row.contactType.setValue(new DataSet<Integer>(contactRow.getContactType()));
               row.name.setValue(contactRow.getName());
               row.multipleUnit.setValue(contactRow.getAddressDO().getMultipleUnit());
               row.streetAddress.setValue(contactRow.getAddressDO().getStreetAddress());
               row.city.setValue(contactRow.getAddressDO().getCity());          
               row.state.setValue(new DataSet<String>(contactRow.getAddressDO().getState()));
               row.zipCode.setValue(contactRow.getAddressDO().getZipCode());
               row.country.setValue(new DataSet<String>(contactRow.getAddressDO().getCountry()));
               row.workPhone.setValue(contactRow.getAddressDO().getWorkPhone());
               row.homePhone.setValue(contactRow.getAddressDO().getHomePhone());
               row.cellPhone.setValue(contactRow.getAddressDO().getCellPhone());
               row.faxPhone.setValue(contactRow.getAddressDO().getFaxPhone());
               row.email.setValue(contactRow.getAddressDO().getEmail());     
               */
               row.cells[0] = contactRow.getContactType();
               row.cells[1] = contactRow.getName();
               row.cells[2] = contactRow.getAddressDO().getMultipleUnit();
               row.cells[3] = contactRow.getAddressDO().getStreetAddress();
               row.cells[4] = contactRow.getAddressDO().getCity();          
               row.cells[5] = contactRow.getAddressDO().getState();
               row.cells[6] = contactRow.getAddressDO().getZipCode();
               row.cells[7] = contactRow.getAddressDO().getCountry();
               row.cells[8] = contactRow.getAddressDO().getWorkPhone();
               row.cells[9] = contactRow.getAddressDO().getHomePhone();
               row.cells[10] = contactRow.getAddressDO().getCellPhone();
               row.cells[11] = contactRow.getAddressDO().getFaxPhone();
               row.cells[12] = contactRow.getAddressDO().getEmail();
               model.add(row);
                }
    		
        } catch (Exception e) {
    
            e.printStackTrace();
            return null;
        }		
    	
    	return model;
	}
	
	private ArrayList<OrganizationContactDO> getContacts() {
		ArrayList<OrganizationContactDO> organizationContacts = new ArrayList<OrganizationContactDO>();
        ArrayList<TableDataRow> deletedRows = ((TableModel)table.model).deleted;
        
		for(int i=0; i < table.model.getData().size(); i++){
			OrganizationContactDO contactDO = new OrganizationContactDO();
			//DataSet<Contact> row = contactsTable.get(i);
            TableDataRow row = (TableDataRow)table.model.getRow(i);
			if(row.key != null)
				contactDO.setId(((Contact)row.key).orgId);
            /*
			contactDO.setOrganization(orgId);
            contactDO.setName(row.name.getValue());
            contactDO.getAddressDO().setId(row.key.addId);
            contactDO.setContactType((Integer)row.contactType.getSelectedKey());
            contactDO.getAddressDO().setMultipleUnit(row.multipleUnit.getValue());
            contactDO.getAddressDO().setStreetAddress(row.streetAddress.getValue());
            contactDO.getAddressDO().setCity(row.city.getValue());
            contactDO.getAddressDO().setState((String)row.state.getSelectedKey());
            contactDO.getAddressDO().setZipCode(row.zipCode.getValue());
            contactDO.getAddressDO().setCountry((String)row.country.getSelectedKey());
            contactDO.getAddressDO().setWorkPhone(row.workPhone.getValue());
            contactDO.getAddressDO().setHomePhone(row.homePhone.getValue());
            contactDO.getAddressDO().setCellPhone(row.cellPhone.getValue());
            contactDO.getAddressDO().setFaxPhone(row.faxPhone.getValue());
            contactDO.getAddressDO().setEmail(row.email.getValue());
            */
            contactDO.setOrganization(rpc.orgId);
            contactDO.setName((String)row.cells[1]);
            contactDO.getAddressDO().setId(((Contact)row.key).addId);
            contactDO.setContactType((Integer)row.cells[0]);
            contactDO.getAddressDO().setMultipleUnit((String)row.cells[2]);
            contactDO.getAddressDO().setStreetAddress((String)row.cells[3]);
            contactDO.getAddressDO().setCity((String)row.cells[4]);
            contactDO.getAddressDO().setState((String)row.cells[5]);
            contactDO.getAddressDO().setZipCode((String)row.cells[6]);
            contactDO.getAddressDO().setCountry((String)row.cells[7]);
            contactDO.getAddressDO().setWorkPhone((String)row.cells[8]);
            contactDO.getAddressDO().setHomePhone((String)row.cells[9]);
            contactDO.getAddressDO().setCellPhone((String)row.cells[10]);
            contactDO.getAddressDO().setFaxPhone((String)row.cells[11]);
            contactDO.getAddressDO().setEmail((String)row.cells[12]);
			organizationContacts.add(contactDO);	
		}
        
		if(deletedRows != null){
            for(int j=0; j<deletedRows.size(); j++){
                TableDataRow deletedRow = deletedRows.get(j);
                if(deletedRow.key != null){
                    OrganizationContactDO contactDO = new OrganizationContactDO();
                    contactDO.setDelete(true);
                    contactDO.setId(((Contact)deletedRow.key).orgId);
                    contactDO.getAddressDO().setId(((Contact)deletedRow.key).addId);
                    
                    organizationContacts.add(contactDO);
                }
            }
		}
		
		return organizationContacts;
	}
	
	private void setContactTypes() {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for(IdNameDO resultDO :  rpc.contactTypes){
            model.add(new TableDataRow(resultDO.getId(),resultDO.getName()));
        } 
        ((Dropdown)table.columns.get(0).getColumnWidget()).setModel(model);
        rpc.contactTypes = null;
	}
	
	public void setRPC(ContactsRPC rpc) {
		if(rpc == null) {
			rpc = new ContactsRPC();
		}
		this.rpc = rpc;
		if(rpc.contactTypes != null) {
		    ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
		    model.add(new TableDataRow(null,""));
		    for(IdNameDO cdo : rpc.contactTypes) {
		        model.add(new TableDataRow(cdo.getId(),cdo.getName()));
		    }
		    ((Dropdown)table.columns.get(0).getColumnWidget()).setModel(model);
		}                
		DataChangeEvent.fire(this);
	}

}
