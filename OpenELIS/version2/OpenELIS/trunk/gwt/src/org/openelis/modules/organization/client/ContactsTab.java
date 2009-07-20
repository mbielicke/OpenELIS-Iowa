package org.openelis.modules.organization.client;

import java.util.ArrayList;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OrganizationContactDO;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.rewrite.Screen;
import org.openelis.gwt.screen.rewrite.ScreenDef;
import org.openelis.gwt.screen.rewrite.ScreenEventHandler;
import org.openelis.gwt.widget.rewrite.AppButton;
import org.openelis.gwt.widget.rewrite.Dropdown;
import org.openelis.gwt.widget.table.rewrite.TableDataRow;
import org.openelis.gwt.widget.table.rewrite.TableWidget;
import org.openelis.gwt.widget.table.rewrite.event.CellEditedEvent;
import org.openelis.gwt.widget.table.rewrite.event.CellEditedHandler;
import org.openelis.gwt.widget.table.rewrite.event.RowAddedEvent;
import org.openelis.gwt.widget.table.rewrite.event.RowAddedHandler;
import org.openelis.gwt.widget.table.rewrite.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.rewrite.event.RowDeletedHandler;
import org.openelis.manager.OrganizationsManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;

public class ContactsTab extends Screen {

	private OrganizationsManager manager;
	private TableWidget table;
	private boolean dropdownsInited;

	public ContactsTab(ScreenDef def) {
		this.def = def;
		initialize();
	}
	
	private void initialize() {
		table = (TableWidget)def.getWidget("contactsTable");
		addScreenHandler(table,new ScreenEventHandler<ArrayList<TableDataRow>>() {
			public void onDataChange(DataChangeEvent event) {
				table.load(getTableModel());
			}
			public void onValueChange(ValueChangeEvent<ArrayList<TableDataRow>> event) {
			//	manager.getContacts().setContacts(getContacts());
			}
			public void onStateChange(StateChangeEvent<State> event) {
				if(event.getState() == State.ADD || event.getState() == State.UPDATE) {
					table.enable(true);
					table.enableAutoAdd(true);
				}else if(event.getState() == State.QUERY) {
					table.enable(true);
				}else{
					table.enable(false);
					table.enableAutoAdd(false);
				}
			}
		});
		
		table.addCellEditedHandler(new CellEditedHandler(){
            public void onCellUpdated(CellEditedEvent event) {
                int row,col;
                row = event.getRow();
                col = event.getCell();
                TableDataRow tableRow = table.getRow(row);
                OrganizationContactDO contactDO = manager.getContacts().getContactAt(row);
                Object val = tableRow.cells.get(col).value;
                
                switch (col){
                    case 0:
                            contactDO.setContactType((Integer)val);
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
                manager.getContacts().addContact(new OrganizationContactDO());
                
            }
		});
		
		table.addRowDeletedHandler(new RowDeletedHandler(){
            public void onRowDeleted(RowDeletedEvent event) {
                manager.getContacts().removeContactAt(event.getIndex());
                
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
	}
	
	private ArrayList<TableDataRow> getTableModel() {
		ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
		
		try 
        {	
    		for(int iter = 0;iter < manager.getContacts().count();iter++) {
    			OrganizationContactDO contactRow = (OrganizationContactDO)manager.getContacts().getContactAt(iter);
    		
    		   TableDataRow row = new TableDataRow(13);
               Contact key = new Contact();
               key.orgId = contactRow.getId();
               key.addId = contactRow.getAddressDO().getId();
               row.key = key;

               row.cells.get(0).value = contactRow.getContactType();
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
            contactDO.setOrganization(contactDO.getOrganization());
            contactDO.setName((String)row.cells.get(1).value);
            contactDO.getAddressDO().setId(((Contact)row.key).addId);
            contactDO.setContactType((Integer)row.cells.get(0).value);
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
	
	public void setManager(OrganizationsManager manager) {
       this.manager = manager;
        
        if(!dropdownsInited) {
            setContactTypes(DictionaryCache.getListByCategorySystemName("contact_type"));
            setCountriesModel(DictionaryCache.getListByCategorySystemName("country"));
            setStatesModel(DictionaryCache.getListByCategorySystemName("state"));
            dropdownsInited = true;
        }                
        DataChangeEvent.fire(this);
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
