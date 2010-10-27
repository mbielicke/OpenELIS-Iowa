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
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Item;
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
import org.openelis.manager.OrganizationManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;

public class ContactTab extends Screen {

    private OrganizationManager manager;
    private Table         table;
    private Button           removeButton, addButton;
    private boolean             loaded;

    public ContactTab(ScreenDefInt def, org.openelis.gwt.widget.Window window) {
        setDefinition(def);
        setWindow(window);
        initialize();
        
        initializeDropdowns();
    }

    private void initialize() {
        table = (Table)def.getWidget("contactTable");
        addScreenHandler(table, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    table.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                table.setEnabled(true);
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
                val = table.getValueAt(r,c);

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

        removeButton = (Button)def.getWidget("removeContactButton");
        addScreenHandler(removeButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;
                
                r = table.getSelectedRow();
                if (r > -1 && table.getRowCount() > 0)
                    table.removeRowAt(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeButton.setEnabled(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        addButton = (Button)def.getWidget("addContactButton");
        addScreenHandler(addButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n;
                
                table.addRow();
                n = table.getRowCount() - 1;
                table.selectRowAt(n);
                table.scrollToVisible(n);
                table.startEditing(n, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addButton.setEnabled(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });
    }

    private void initializeDropdowns() {
        ArrayList<Item<Integer>> iModel;
        ArrayList<Item<String>> sModel;
        ArrayList<DictionaryDO> list;
        Item<Integer> iItem;
        Item<String> sItem;
        Dropdown<String> state, country;
        Dropdown<Integer> type;
        
        iModel = new ArrayList<Item<Integer>>();
        iModel.add(new Item<Integer>(null, ""));
        list = DictionaryCache.getListByCategorySystemName("contact_type");
        for (DictionaryDO d : list) {
            iItem = new Item<Integer>(d.getId(), d.getEntry());
            iItem.setEnabled("Y".equals(d.getIsActive()));
            iModel.add(iItem);
        }
        type = (Dropdown<Integer>)table.getColumnAt(0).getCellEditor().getWidget();
        type.setModel(iModel);

        sModel = new ArrayList<Item<String>>();
        sModel.add(new Item<String>(null, ""));
        list =  DictionaryCache.getListByCategorySystemName("state");
        for (DictionaryDO d : list) {
            sItem = new Item<String>(d.getEntry(), d.getEntry());
            sItem.setEnabled("Y".equals(d.getIsActive()));
            sModel.add(sItem);
        }
        state = (Dropdown<String>)table.getColumnAt(5).getCellEditor().getWidget();
        state.setModel(sModel);

        sModel = new ArrayList<Item<String>>();
        sModel.add(new Item<String>(null, "")); 
        list = DictionaryCache.getListByCategorySystemName("country");
        for (DictionaryDO d : list) {
            sItem = new Item<String>(d.getEntry(), d.getEntry());
            sItem.setEnabled("Y".equals(d.getIsActive()));
            sModel.add(sItem);
        }
        country = (Dropdown<String>)table.getColumnAt(7).getCellEditor().getWidget();
        country.setModel(sModel);
    }
    
    private ArrayList<Row> getTableModel() {
        int i;
        Row row;
        OrganizationContactDO data;
        ArrayList<Row> model;
        
        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.getContacts().count(); i++) {
                data = (OrganizationContactDO)manager.getContacts().getContactAt(i);

                row = new Row(13);
                row.setCell(0,data.getContactTypeId());
                row.setCell(1,data.getName());
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
