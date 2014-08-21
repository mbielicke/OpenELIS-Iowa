package org.openelis.modules.provider.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ProviderLocationDO;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
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

public class LocationTab extends Screen {
	
	private ProviderManager manager;
	private Table           locationTable; 
	private Button          addAddressButton, removeAddressButton;
	private boolean         loaded;
	
	public LocationTab(ScreenDefInt def, Window window) {
		setDefinition(def);
		setWindow(window);
		initialize();

		initializeDropdowns();
    }
	
	private void initialize() {
        locationTable = (Table)def.getWidget("locationTable");
        addScreenHandler(locationTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    locationTable.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                locationTable.setEnabled(true);
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
                Object val;
                ProviderLocationDO data;

                try {
                	data = manager.getLocations().getLocationAt(r);
                }catch(Exception e) {
                	return;
                }
                
                val = locationTable.getValueAt(r,c);
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
            		com.google.gwt.user.client.Window.alert(e.toString());
            	}
            }
        });

        locationTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
            	try {
            		manager.getLocations().removeLocationAt(event.getIndex());
            	}catch(Exception e) {
            		e.printStackTrace();
            		com.google.gwt.user.client.Window.alert(e.toString());
            	}
            }
        });
        

        addAddressButton = (Button)def.getWidget("addLocationButton");
        addScreenHandler(addAddressButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
            	locationTable.addRow();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addAddressButton.setEnabled(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });

        removeAddressButton = (Button)def.getWidget("removeLocationButton");
        addScreenHandler(removeAddressButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
            	int r = locationTable.getSelectedRow();
            	locationTable.removeRowAt(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeAddressButton.setEnabled(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });		
	}
	
	private void initializeDropdowns() {
        ArrayList<Item<String>> model;
        ArrayList<DictionaryDO> list;
        Item<String> row;
        Dropdown<String> state, country;
        
        model = new ArrayList<Item<String>>();
        model.add(new Item<String>(null, ""));
        list = DictionaryCache.getListByCategorySystemName("state");
        for (DictionaryDO d : list) {            
            row = new Item<String>(d.getEntry(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }
        
        state = ((Dropdown<String>)locationTable.getColumnAt(5).getCellEditor().getWidget());
        state.setModel(model);

        model = new ArrayList<Item<String>>();
        model.add(new Item<String>(null, ""));
        list =  DictionaryCache.getListByCategorySystemName("country");
        for (DictionaryDO d : list) {         
            row = new Item<String>(d.getEntry(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }
        
        country = ((Dropdown<String>)locationTable.getColumnAt(7).getCellEditor().getWidget());
        country.setModel(model);		
	}
	
    private ArrayList<Row> getTableModel() {
        int i;
        Row row;
        ProviderLocationDO data;
        ArrayList<Row> model;
        
        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.getLocations().count(); i++) {
                data = (ProviderLocationDO)manager.getLocations().getLocationAt(i);

                row = new Row(13);
                row.setCell(0,data.getLocation());
                row.setCell(1,data.getExternalId());
                row.setCell(2,data.getAddress().getMultipleUnit());
                row.setCell(3,data.getAddress().getStreetAddress());
                row.setCell(4,data.getAddress().getCity());
                row.setCell(5,data.getAddress().getState());
                row.setCell(6,data.getAddress().getZipCode());
                row.setCell(7,data.getAddress().getCountry());
                row.setCell(8,data.getAddress().getWorkPhone());
                row.setCell(9,data.getAddress().getHomePhone());
                row.setCell(10,data.getAddress().getCellPhone());
                row.setCell(11,data.getAddress().getFaxPhone());
                row.setCell(12,data.getAddress().getEmail());
                model.add(row);
            }
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
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
