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
import org.openelis.manager.OrganizationManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;

public class ContactTab extends Screen {

    private OrganizationManager manager;
    private TableWidget         table;
    private AppButton           removeButton, addButton;
    private boolean             loaded;

    public ContactTab(ScreenDefInt def, ScreenWindow window) {
        setDefinition(def);
        setWindow(window);
        initialize();
        
        initializeDropdowns();
    }

    private void initialize() {
        table = (TableWidget)def.getWidget("contactTable");
        addScreenHandler(table, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    table.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                table.enable(true);
                table.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {                
                if(state != State.ADD && state != State.UPDATE && state != State.QUERY)  
                    event.cancel();
            }            
        });

        table.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                OrganizationContactDO data;

                r = event.getRow();
                c = event.getCol();
                val = table.getObject(r,c);

                try {
                    data = manager.getContacts().getContactAt(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }

                switch (c) {
                    case 0:
                        data.setContactTypeId((Integer)val);
                        break;
                    case 1:
                        data.setName((String)val);
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

        table.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                try {
                    manager.getContacts().addContact(new OrganizationContactDO());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        table.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getContacts().removeContactAt(event.getIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        removeButton = (AppButton)def.getWidget("removeContactButton");
        addScreenHandler(removeButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;
                
                r = table.getSelectedRow();
                if (r > -1 && table.numRows() > 0)
                    table.deleteRow(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        addButton = (AppButton)def.getWidget("addContactButton");
        addScreenHandler(addButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n;
                
                table.addRow();
                n = table.numRows() - 1;
                table.selectRow(n);
                table.scrollToSelection();
                table.startEditing(n, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });
    }

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        ArrayList<DictionaryDO> list;
        TableDataRow row;
        Dropdown<String> state, country;
        Dropdown<Integer> type;
        
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = DictionaryCache.getListByCategorySystemName("contact_type");
        for (DictionaryDO d : list) {
            row = new TableDataRow(d.getId(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);
        }
        type = ((Dropdown<Integer>)table.getColumns().get(0).getColumnWidget());
        type.setModel(model);

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list =  DictionaryCache.getListByCategorySystemName("state");
        for (DictionaryDO d : list) {
            row = new TableDataRow(d.getEntry(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);
        }
        state = ((Dropdown<String>)table.getColumns().get(5).getColumnWidget());
        state.setModel(model);

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, "")); 
        list = DictionaryCache.getListByCategorySystemName("country");
        for (DictionaryDO d : list) {
            row = new TableDataRow(d.getEntry(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);
        }
        country = ((Dropdown<String>)table.getColumns().get(7).getColumnWidget());
        country.setModel(model);
    }
    
    private ArrayList<TableDataRow> getTableModel() {
        int i;
        TableDataRow row;
        OrganizationContactDO data;
        ArrayList<TableDataRow> model;
        
        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.getContacts().count(); i++) {
                data = (OrganizationContactDO)manager.getContacts().getContactAt(i);

                row = new TableDataRow(13);
                row.cells.get(0).value = data.getContactTypeId();
                row.cells.get(1).value = data.getName();
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

    public void setManager(OrganizationManager manager) {
        this.manager = manager;
        loaded = false;
    }

    public void draw() {
        if ( !loaded)
            DataChangeEvent.fire(this);

        loaded = true;
    }
}
