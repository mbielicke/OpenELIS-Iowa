package org.openelis.modules.worksheetCompletion.client;

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
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.OrganizationManager;
import org.openelis.manager.WorksheetManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;

public class WorksheetTab extends Screen {

    private WorksheetManager manager;
    private TableWidget      table;
    private AppButton        editMultipleButton;
    private boolean          loaded;

    public WorksheetTab(ScreenDefInt def, ScreenWindow window) {
        setDefinition(def);
        setWindow(window);
        initialize();
        
        initializeDropdowns();
    }

    private void initialize() {
        table = (TableWidget)def.getWidget("worksheetItemTable");
        addScreenHandler(table, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    table.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                table.enable(true);
            }
        });

        table.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
/*
                int r, c;
                Object val;
                OrganizationContactDO data;

                if (state == State.QUERY)
                    return;

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
*/
            }
        });

        editMultipleButton = (AppButton)def.getWidget("editMultipleButton");
        addScreenHandler(editMultipleButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
            }

            public void onStateChange(StateChangeEvent<State> event) {
                editMultipleButton.enable(false);
            }
        });
    }

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
/*        
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("contact_type"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));
        ((Dropdown<Integer>)table.getColumns().get(0).getColumnWidget()).setModel(model);
*/
    }
    
    private ArrayList<TableDataRow> getTableModel() {
/*
        int i;
        TableDataRow row;
        OrganizationContactDO data;
*/
        ArrayList<TableDataRow> model;
        
        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;
/*
        try {
            for (i = 0; i < manager.getContacts().count(); i++) {
                data = (OrganizationContactDO)manager.getContacts().getContactAt(i);

                row = new TableDataRow(13);
                row.cells.get(0).value = data.getContactTypeId();
                row.cells.get(1).value = data.getName();
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
*/
        return model;
    }

    public void setManager(WorksheetManager manager) {
        this.manager = manager;
        loaded = false;
    }

    public void draw() {
        if ( !loaded)
            DataChangeEvent.fire(this);

        loaded = true;
    }
}
