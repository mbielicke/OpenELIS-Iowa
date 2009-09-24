package org.openelis.modules.organization.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OrganizationContactDO;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.OrganizationManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;

public class ContactsTab extends Screen {

	private OrganizationManager manager;
	private TableWidget table;
	private boolean dropdownsInited, loaded;

	public ContactsTab(ScreenDefInt def) {
	    setDef(def);
		initialize();
	}
	
	private void initialize() {
		table = (TableWidget)def.getWidget("contactsTable");
		addScreenHandler(table,new ScreenEventHandler<ArrayList<TableDataRow>>() {
			public void onDataChange(DataChangeEvent event) {
				table.load(getTableModel());
			}
			public void onStateChange(StateChangeEvent<State> event) {
			    table.enable(EnumSet.of(State.ADD,State.UPDATE,State.QUERY).contains(event.getState()));
			    table.setQueryMode(event.getState() == State.QUERY);
			}
		});
		
		table.addCellEditedHandler(new CellEditedHandler(){
            public void onCellUpdated(CellEditedEvent event) {
            	if(state == State.QUERY)
            		return;
                int row,col;
                row = event.getRow();
                col = event.getCell();
                OrganizationContactDO contactDO;
                TableDataRow tableRow = table.getRow(row);
                try{
                    contactDO = manager.getContacts().getContactAt(row);
                }catch(Exception e){
                    Window.alert(e.getMessage());
                    return;
                }
                    
                Object val = tableRow.cells.get(col).value;
                
                switch (col){
                    case 0:
                            contactDO.setContactTypeId((Integer)val);
                            break;
                    case 1:
                            contactDO.setName((String)val);
                            break;
                    case 2:
                            contactDO.getAddressDO().setMultipleUnit((String)val);
                            break;
                    case 3:
                            contactDO.getAddressDO().setStreetAddress((String)val);
                            break;
                    case 4:
                            contactDO.getAddressDO().setCity((String)val);
                            break;
                    case 5:
                            contactDO.getAddressDO().setState((String)val);
                            break;
                    case 6:
                            contactDO.getAddressDO().setZipCode((String)val);
                            break;
                    case 7:
                            contactDO.getAddressDO().setCountry((String)val);
                            break;
                    case 8:
                            contactDO.getAddressDO().setWorkPhone((String)val);
                            break;
                    case 9:
                            contactDO.getAddressDO().setHomePhone((String)val);
                            break;
                    case 10:
                            contactDO.getAddressDO().setCellPhone((String)val);
                            break;
                    case 11:
                        contactDO.getAddressDO().setFaxPhone((String)val);
                            break;
                    case 12:
                            contactDO.getAddressDO().setEmail((String)val);
                            break;
                }
            }
		});
		
		table.addRowAddedHandler(new RowAddedHandler(){
            public void onRowAdded(RowAddedEvent event) {
                try{
                    manager.getContacts().addContact(new OrganizationContactDO());
                }catch(Exception e){
                    Window.alert(e.getMessage());
                }
                
            }
		});
		
		table.addRowDeletedHandler(new RowDeletedHandler(){
            public void onRowDeleted(RowDeletedEvent event) {
                try{
                    manager.getContacts().removeContactAt(event.getIndex());
                }catch(Exception e){
                    Window.alert(e.getMessage());
                }
                
            }
		});
		
		final AppButton removeContact = (AppButton)def.getWidget("removeContactButton");
    	addScreenHandler(removeContact,new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int selectedRow = table.getSelectedIndex();
                if (selectedRow > -1 && table.numRows() > 0) {
                    table.deleteRow(selectedRow);
                }
            }
    		public void onStateChange(StateChangeEvent<State> event) {
    			if(event.getState() == State.ADD || event.getState() == State.UPDATE)
    			    removeContact.enable(true);
    			else
    				removeContact.enable(false);
    		}
    		
    	});
    	
    	final AppButton addContact = (AppButton)def.getWidget("addContactButton");
        addScreenHandler(addContact,new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                table.addRow();
                table.selectRow(table.numRows()-1);
                table.scrollToSelection();
                table.startEditing(table.numRows()-1, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if(event.getState() == State.ADD || event.getState() == State.UPDATE)
                    addContact.enable(true);
                else
                    addContact.enable(false);
            }
            
        });
	}
	
	private ArrayList<TableDataRow> getTableModel() {
		ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
		
		if(manager == null)
		    return model;
		
		try 
        {	
    		for(int iter = 0;iter < manager.getContacts().count();iter++) {
    			OrganizationContactDO contactRow = (OrganizationContactDO)manager.getContacts().getContactAt(iter);
    		
    		   TableDataRow row = new TableDataRow(13);
               Contact key = new Contact();
               key.orgId = contactRow.getId();
               key.addId = contactRow.getAddressDO().getId();
               row.key = key;

               row.cells.get(0).value = contactRow.getContactTypeId();
               row.cells.get(1).value = contactRow.getName();
               row.cells.get(2).value = contactRow.getAddressDO().getMultipleUnit();
               row.cells.get(3).value = contactRow.getAddressDO().getStreetAddress();
               row.cells.get(4).value = contactRow.getAddressDO().getCity();          
               row.cells.get(5).value = contactRow.getAddressDO().getState();
               row.cells.get(6).value = contactRow.getAddressDO().getZipCode();
               row.cells.get(7).value = contactRow.getAddressDO().getCountry();
               row.cells.get(8).value = contactRow.getAddressDO().getWorkPhone();
               row.cells.get(9).value = contactRow.getAddressDO().getHomePhone();
               row.cells.get(10).value = contactRow.getAddressDO().getCellPhone();
               row.cells.get(11).value = contactRow.getAddressDO().getFaxPhone();
               row.cells.get(12).value = contactRow.getAddressDO().getEmail();
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
        
		for(int i=0; i < table.getData().size(); i++){
			OrganizationContactDO contactDO = new OrganizationContactDO();
			//DataSet<Contact> row = contactsTable.get(i);
            TableDataRow row = table.getRow(i);
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
            contactDO.setOrganizationId(contactDO.getOrganizationId());
            contactDO.setName((String)row.cells.get(1).value);
            contactDO.getAddressDO().setId(((Contact)row.key).addId);
            contactDO.setContactTypeId((Integer)row.cells.get(0).value);
            contactDO.getAddressDO().setMultipleUnit((String)row.cells.get(2).value);
            contactDO.getAddressDO().setStreetAddress((String)row.cells.get(3).value);
            contactDO.getAddressDO().setCity((String)row.cells.get(4).value);
            contactDO.getAddressDO().setState((String)row.cells.get(5).value);
            contactDO.getAddressDO().setZipCode((String)row.cells.get(6).value);
            contactDO.getAddressDO().setCountry((String)row.cells.get(7).value);
            contactDO.getAddressDO().setWorkPhone((String)row.cells.get(8).value);
            contactDO.getAddressDO().setHomePhone((String)row.cells.get(9).value);
            contactDO.getAddressDO().setCellPhone((String)row.cells.get(10).value);
            contactDO.getAddressDO().setFaxPhone((String)row.cells.get(11).value);
            contactDO.getAddressDO().setEmail((String)row.cells.get(12).value);
			organizationContacts.add(contactDO);	
		}
        
		return organizationContacts;
	}
	
	private void setContactTypes(ArrayList<DictionaryDO> list) {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for(DictionaryDO resultDO :  list){
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        } 
        ((Dropdown<Integer>)table.columns.get(0).getColumnWidget()).setModel(model);
	}
	
    public void setCountriesModel(ArrayList<DictionaryDO> list) {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for(DictionaryDO resultDO :  list){
            model.add(new TableDataRow(resultDO.getEntry(),resultDO.getEntry()));
        } 
        ((Dropdown<String>)table.columns.get(7).getColumnWidget()).setModel(model);
    }
    
    public void setStatesModel(ArrayList<DictionaryDO> list) {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for(DictionaryDO resultDO :  list){
            model.add(new TableDataRow(resultDO.getEntry(),resultDO.getEntry()));
        } 
        ((Dropdown<String>)table.columns.get(5).getColumnWidget()).setModel(model);
    }
	
	public void setManager(OrganizationManager manager) {
       this.manager = manager;
        loaded = false;
        
        if(!dropdownsInited) {
            setContactTypes(DictionaryCache.getListByCategorySystemName("contact_type"));
            setCountriesModel(DictionaryCache.getListByCategorySystemName("country"));
            setStatesModel(DictionaryCache.getListByCategorySystemName("state"));
            dropdownsInited = true;
        }                
	}
	
	public void draw(){
	    if(!loaded)
	        DataChangeEvent.fire(this);
	    
	    loaded = true;
	}

	//
	//start table manager methods
	//
	/*
    public boolean canAdd(TableWidget widget, TableDataRow set, int row) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canAutoAdd(TableWidget widget, TableDataRow addRow) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canDelete(TableWidget widget, TableDataRow set, int row) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canEdit(TableWidget widget,
                           TableDataRow set,
                           int row,
                           int col) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean canSelect(TableWidget widget, TableDataRow set, int row) {
        // TODO Auto-generated method stub
        return false;
    }
    */
    //
    //end table manager methods
    //
}
