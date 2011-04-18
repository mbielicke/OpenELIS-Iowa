package org.openelis.modules.provider.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.CategoryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ProviderLocationDO;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.ProviderManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;

public class LocationTab extends Screen {
	
	private ProviderManager manager;
	private TableWidget locationTable; 
	private AppButton addAddressButton, removeAddressButton;
	private boolean loaded;
	
	public LocationTab(ScreenDefInt def, ScreenWindow window) {
		setDefinition(def);
		setWindow(window);
		initialize();

		initializeDropdowns();
    }
	
	private void initialize() {
        locationTable = (TableWidget)def.getWidget("locationTable");
        addScreenHandler(locationTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    locationTable.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                locationTable.enable(true);
                locationTable.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        locationTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {                
                if(state != State.ADD && state != State.UPDATE && state != State.QUERY)  
                    event.cancel();
            }            
        });

        locationTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                r = event.getRow();
                c = event.getCol();
                Object val = event.getValue();
                ProviderLocationDO data;
                try {
                	data = manager.getLocations().getLocationAt(r);
                }catch(Exception e) {
                	return;
                }
                val = locationTable.getObject(r,c);
                switch(c) {
                    case 0:
                        data.setLocation((String)val);
                        break;
                    case 1:
                        data.setExternalId((String)val);
                        break;
                    case 2:
                        data.getAddress().setMultipleUnit((String)val);
                        break;
                    case 3:
                        data.getAddress().setStreetAddress((String)val);
                        break;
                    case 4:
                        data.getAddress().setCity((String)val);
                        break;
                    case 5:
                        data.getAddress().setState((String)val);
                        break;
                    case 6:
                        data.getAddress().setZipCode((String)val);
                        break;
                    case 7:
                        data.getAddress().setCountry((String)val);
                        break;
                    case 8:
                        data.getAddress().setWorkPhone((String)val);
                        break;
                    case 9:
                        data.getAddress().setHomePhone((String)val);
                        break;
                    case 10:
                        data.getAddress().setCellPhone((String)val);
                        break;
                    case 11:
                        data.getAddress().setFaxPhone((String)val);
                        break;
                    case 12:
                        data.getAddress().setEmail((String)val);
                        break;
                }
            }
        });

        locationTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
            	try {
            		manager.getLocations().addLocation(new ProviderLocationDO());
            	}catch(Exception e){
            		e.printStackTrace();
            		Window.alert(e.toString());
            	}
            }
        });

        locationTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
            	try {
            		manager.getLocations().removeLocationAt(event.getIndex());
            	}catch(Exception e) {
            		e.printStackTrace();
            		Window.alert(e.toString());
            	}
            }
        });

        removeAddressButton = (AppButton)def.getWidget("removeLocationButton");
        addScreenHandler(removeAddressButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;
                
                r = locationTable.getSelectedRow();
                if (r > -1 && locationTable.numRows() > 0)
                    locationTable.deleteRow(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeAddressButton.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });     

        addAddressButton = (AppButton)def.getWidget("addLocationButton");
        addScreenHandler(addAddressButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n;
                
                locationTable.addRow();
                n = locationTable.numRows() - 1;
                locationTable.selectRow(n);
                locationTable.scrollToSelection();
                locationTable.startEditing(n, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addAddressButton.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });
	}
	
	private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        ArrayList<DictionaryDO> list;
        TableDataRow row;
        Dropdown<String> state, country;
        
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = CategoryCache.getBySystemName("state");
        for (DictionaryDO d : list) {            
            row = new TableDataRow(d.getEntry(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);
        }
        
        state = ((Dropdown<String>)locationTable.getColumns().get(5).getColumnWidget());
        state.setModel(model);

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list =  CategoryCache.getBySystemName("country");
        for (DictionaryDO d : list) {         
            row = new TableDataRow(d.getEntry(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);
        }
        
        country = ((Dropdown<String>)locationTable.getColumns().get(7).getColumnWidget());
        country.setModel(model);		
	}
	
    private ArrayList<TableDataRow> getTableModel() {
        int i;
        TableDataRow row;
        ProviderLocationDO data;
        ArrayList<TableDataRow> model;
        
        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.getLocations().count(); i++) {
                data = (ProviderLocationDO)manager.getLocations().getLocationAt(i);

                row = new TableDataRow(13);
                row.cells.get(0).value = data.getLocation();
                row.cells.get(1).value = data.getExternalId();
                row.cells.get(2).value = data.getAddress().getMultipleUnit();
                row.cells.get(3).value = data.getAddress().getStreetAddress();
                row.cells.get(4).value = data.getAddress().getCity();
                row.cells.get(5).value = data.getAddress().getState();
                row.cells.get(6).value = data.getAddress().getZipCode();
                row.cells.get(7).value = data.getAddress().getCountry();
                row.cells.get(8).value = data.getAddress().getWorkPhone();
                row.cells.get(9).value = data.getAddress().getHomePhone();
                row.cells.get(10).value = data.getAddress().getCellPhone();
                row.cells.get(11).value = data.getAddress().getFaxPhone();
                row.cells.get(12).value = data.getAddress().getEmail();
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }
	
    public void setManager(ProviderManager manager) {
        this.manager = manager;
        loaded = false;
    }

    public void draw() {
        if ( !loaded)
            DataChangeEvent.fire(this);

        loaded = true;
    }
}
