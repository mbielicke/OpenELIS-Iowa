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
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.ProviderManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;

public class LocationTab extends Screen {
	
	private ProviderManager manager;
	private TableWidget locationTable; 
	private AppButton addAddressButton, removeAddressButton;
	private boolean loaded;
	
	public LocationTab(ScreenDefInt def, ScreenWindow window) {
		setDef(def);
		setWindow(window);
		initialize();

		DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }

    private void postConstructor() {
        initializeDropdowns();
    }
	
	private void initialize() {
        locationTable = (TableWidget)def.getWidget("locationTable");
        addScreenHandler(locationTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                locationTable.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                locationTable.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                locationTable.setQueryMode(event.getState() == State.QUERY);
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
                        data.getAddressDO().setMultipleUnit((String)val);
                        break;
                    case 3:
                        data.getAddressDO().setStreetAddress((String)val);
                        break;
                    case 4:
                        data.getAddressDO().setCity((String)val);
                        break;
                    case 5:
                        data.getAddressDO().setState((String)val);
                        break;
                    case 6:
                        data.getAddressDO().setZipCode((String)val);
                        break;
                    case 7:
                        data.getAddressDO().setCountry((String)val);
                        break;
                    case 8:
                        data.getAddressDO().setWorkPhone((String)val);
                        break;
                    case 9:
                        data.getAddressDO().setHomePhone((String)val);
                        break;
                    case 10:
                        data.getAddressDO().setCellPhone((String)val);
                        break;
                    case 11:
                        data.getAddressDO().setFaxPhone((String)val);
                        break;
                    case 12:
                        data.getAddressDO().setEmail((String)val);
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
        

        addAddressButton = (AppButton)def.getWidget("addLocationButton");
        addScreenHandler(addAddressButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
            	locationTable.addRow();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addAddressButton.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });

        removeAddressButton = (AppButton)def.getWidget("removeLocationButton");
        addScreenHandler(removeAddressButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
            	int r = locationTable.getSelectedRow();
            	locationTable.deleteRow(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeAddressButton.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });		
	}
	
	private void initializeDropdowns() {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("state"))
            model.add(new TableDataRow(d.getEntry(), d.getEntry()));
        ((Dropdown<String>)locationTable.getColumns().get(5).getColumnWidget()).setModel(model);

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("country"))
            model.add(new TableDataRow(d.getEntry(), d.getEntry()));
        ((Dropdown<String>)locationTable.getColumns().get(7).getColumnWidget()).setModel(model);		
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
                row.cells.get(2).value = data.getAddressDO().getMultipleUnit();
                row.cells.get(3).value = data.getAddressDO().getStreetAddress();
                row.cells.get(4).value = data.getAddressDO().getCity();
                row.cells.get(5).value = data.getAddressDO().getState();
                row.cells.get(6).value = data.getAddressDO().getZipCode();
                row.cells.get(7).value = data.getAddressDO().getCountry();
                row.cells.get(8).value = data.getAddressDO().getWorkPhone();
                row.cells.get(9).value = data.getAddressDO().getHomePhone();
                row.cells.get(10).value = data.getAddressDO().getCellPhone();
                row.cells.get(11).value = data.getAddressDO().getFaxPhone();
                row.cells.get(12).value = data.getAddressDO().getEmail();
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
