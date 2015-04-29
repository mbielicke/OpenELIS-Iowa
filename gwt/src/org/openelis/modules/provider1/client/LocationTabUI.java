package org.openelis.modules.provider1.client;

import static org.openelis.ui.screen.State.ADD;
import static org.openelis.ui.screen.State.DISPLAY;
import static org.openelis.ui.screen.State.QUERY;
import static org.openelis.ui.screen.State.UPDATE;

import java.util.ArrayList;

import org.openelis.cache.CategoryCache;
import org.openelis.domain.AddressDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.ProviderLocationDO;
import org.openelis.manager.ProviderManager1;
import org.openelis.meta.ProviderMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.Queryable;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;
import org.openelis.ui.widget.table.event.RowAddedEvent;
import org.openelis.ui.widget.table.event.RowAddedHandler;
import org.openelis.ui.widget.table.event.RowDeletedEvent;
import org.openelis.ui.widget.table.event.RowDeletedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class LocationTabUI extends Screen {

    @UiTemplate("LocationTab.ui.xml")
    interface LocationTabUIBinder extends UiBinder<Widget, LocationTabUI> {
    };

    private static LocationTabUIBinder uiBinder = GWT.create(LocationTabUIBinder.class);

    private ProviderManager1           manager;

    @UiField
    protected Table<Row>               table;

    @UiField
    protected Dropdown<String>         locationState, locationCountry;

    @UiField
    protected Button                   addLocationButton, removeLocationButton;

    protected Screen                   parentScreen;

    protected EventBus                 parentBus;

    private boolean                    redraw, isVisible;

    public LocationTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
    }

    private void initialize() {
        ArrayList<DictionaryDO> list;
        ArrayList<Item<String>> smodel;
        Item<Integer> item;

        addScreenHandler(table, "table", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                if ( !isState(QUERY))
                    table.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(isState(QUERY, DISPLAY, ADD, UPDATE));
                table.setQueryMode(isState(QUERY));
            }

            public Object getQuery() {
                ArrayList<QueryData> qds;
                QueryData qd;

                qds = new ArrayList<QueryData>();
                for (int i = 0; i < 12; i++ ) {
                    qd = (QueryData) ((Queryable)table.getColumnWidget(i)).getQuery();
                    if (qd != null) {
                        switch (i) {
                            case 0:
                                qd.setKey(ProviderMeta.getProviderLocationLocation());
                                break;
                            case 1:
                                qd.setKey(ProviderMeta.getProviderLocationExternalId());
                                break;
                            case 2:
                                qd.setKey(ProviderMeta.getProviderLocationAddressMultipleUnit());
                                break;
                            case 3:
                                qd.setKey(ProviderMeta.getProviderLocationAddressStreetAddress());
                                break;
                            case 4:
                                qd.setKey(ProviderMeta.getProviderLocationAddressCity());
                                break;
                            case 5:
                                qd.setKey(ProviderMeta.getProviderLocationAddressState());
                                break;
                            case 6:
                                qd.setKey(ProviderMeta.getProviderLocationAddressZipCode());
                                break;
                            case 7:
                                qd.setKey(ProviderMeta.getProviderLocationAddressCountry());
                                break;
                            case 8:
                                qd.setKey(ProviderMeta.getProviderLocationAddressWorkPhone());
                                break;
                            case 9:
                                qd.setKey(ProviderMeta.getProviderLocationAddressHomePhone());
                                break;
                            case 10:
                                qd.setKey(ProviderMeta.getProviderLocationAddressCellPhone());
                                break;
                            case 11:
                                qd.setKey(ProviderMeta.getProviderLocationAddressFaxPhone());
                                break;
                            case 12:
                                qd.setKey(ProviderMeta.getProviderLocationAddressEmail());
                                break;
                        }
                        qds.add(qd);
                    }
                }

                return qds;
            }
        });

        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if ( !isState(QUERY, ADD, UPDATE))
                    event.cancel();
            }
        });

        table.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                ProviderLocationDO data;

                r = event.getRow();
                c = event.getCol();
                val = table.getValueAt(r, c);
                data = manager.location.get(r);

                switch (c) {
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

        table.addRowAddedHandler(new RowAddedHandler<Row>() {
            public void onRowAdded(RowAddedEvent<Row> event) {
                ProviderLocationDO data;

                data = manager.location.add();
                event.getRow().setData(data);
            }
        });

        table.addRowDeletedHandler(new RowDeletedHandler<Row>() {
            public void onRowDeleted(RowDeletedEvent<Row> event) {
                manager.location.remove(event.getIndex());
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                removeLocationButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                addLocationButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displayLocations();
            }
        });

        smodel = new ArrayList<Item<String>>();
        list = CategoryCache.getBySystemName("state");
        for (DictionaryDO data : list) {
            item = new Item<Integer>(data.getId(), data.getEntry());
            item.setEnabled( ("Y".equals(data.getIsActive())));
            smodel.add(new Item<String>(data.getEntry(), data.getEntry()));
        }
        locationState.setModel(smodel);

        smodel = new ArrayList<Item<String>>();
        list = CategoryCache.getBySystemName("country");
        for (DictionaryDO data : list) {
            item = new Item<Integer>(data.getId(), data.getEntry());
            item.setEnabled( ("Y".equals(data.getIsActive())));
            smodel.add(new Item<String>(data.getEntry(), data.getEntry()));
        }
        locationCountry.setModel(smodel);
    }

    @UiHandler("addLocationButton")
    protected void addLocation(ClickEvent event) {
        int n;

        table.addRow();
        n = table.getRowCount() - 1;
        table.selectRowAt(n);
        table.scrollToVisible(table.getSelectedRow());
        table.startEditing(n, 0);
    }

    @UiHandler("removeLocationButton")
    protected void removeLocation(ClickEvent event) {
        int r;

        r = table.getSelectedRow();
        if (r > -1 && table.getRowCount() > 0)
            table.removeRowAt(r);
    }

    public void setData(ProviderManager1 manager) {
        if (DataBaseUtil.isDifferent(this.manager, manager)) {
            this.manager = manager;
        }
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    public void onDataChange() {
        int count1, count2;
        ProviderLocationDO location;
        AddressDO address;
        Row r;

        count1 = table.getRowCount();
        count2 = manager == null ? 0 : manager.location.count();

        /*
         * find out if there's any difference between the location being
         * displayed and the location in the manager
         */
        if (count1 == count2) {
            for (int i = 0; i < count1; i++ ) {
                r = table.getRowAt(i);
                location = manager.location.get(i);
                address = location.getAddress();
                if (address == null &&
                    ( !DataBaseUtil.isEmpty(r.getCell(2)) || !DataBaseUtil.isEmpty(r.getCell(3)) ||
                     !DataBaseUtil.isEmpty(r.getCell(4)) || !DataBaseUtil.isEmpty(r.getCell(5)) ||
                     !DataBaseUtil.isEmpty(r.getCell(6)) || !DataBaseUtil.isEmpty(r.getCell(7)) ||
                     !DataBaseUtil.isEmpty(r.getCell(8)) || !DataBaseUtil.isEmpty(r.getCell(9)) ||
                     !DataBaseUtil.isEmpty(r.getCell(10)) || !DataBaseUtil.isEmpty(r.getCell(11)) || !DataBaseUtil.isEmpty(r.getCell(12)))) {
                    redraw = true;
                    break;
                } else if (DataBaseUtil.isDifferent(location.getLocation(), r.getCell(0)) ||
                           DataBaseUtil.isDifferent(location.getExternalId(), r.getCell(1)) ||
                           DataBaseUtil.isDifferent(address.getMultipleUnit(), r.getCell(2)) ||
                           DataBaseUtil.isDifferent(address.getStreetAddress(), r.getCell(3)) ||
                           DataBaseUtil.isDifferent(address.getCity(), r.getCell(4)) ||
                           DataBaseUtil.isDifferent(address.getState(), r.getCell(5)) ||
                           DataBaseUtil.isDifferent(address.getZipCode(), r.getCell(6)) ||
                           DataBaseUtil.isDifferent(address.getCountry(), r.getCell(7)) ||
                           DataBaseUtil.isDifferent(address.getWorkPhone(), r.getCell(8)) ||
                           DataBaseUtil.isDifferent(address.getHomePhone(), r.getCell(9)) ||
                           DataBaseUtil.isDifferent(address.getCellPhone(), r.getCell(10)) ||
                           DataBaseUtil.isDifferent(address.getFaxPhone(), r.getCell(11)) ||
                           DataBaseUtil.isDifferent(address.getEmail(), r.getCell(12))) {
                    redraw = true;
                    break;
                }
            }
        } else {
            redraw = true;
        }

        displayLocations();
    }

    private void displayLocations() {
        if ( !isVisible)
            return;

        if (redraw) {
            redraw = false;
            setState(state);
            fireDataChange();
        }
    }

    private ArrayList<Row> getTableModel() {
        int i;
        Row row;
        ProviderLocationDO data;
        AddressDO address;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        for (i = 0; i < manager.location.count(); i++ ) {
            data = manager.location.get(i);
            row = new Row(13);
            row.setCell(0, data.getLocation());
            row.setCell(1, data.getExternalId());
            if (data.getAddress() != null) {
                address = data.getAddress();
                row.setCell(2, address.getMultipleUnit());
                row.setCell(3, address.getStreetAddress());
                row.setCell(4, address.getCity());
                row.setCell(5, address.getState());
                row.setCell(6, address.getZipCode());
                row.setCell(7, address.getCountry());
                row.setCell(8, address.getWorkPhone());
                row.setCell(9, address.getHomePhone());
                row.setCell(10, address.getCellPhone());
                row.setCell(11, address.getFaxPhone());
                row.setCell(12, address.getEmail());
            }
            row.setData(data);
            model.add(row);
        }

        return model;
    }
}
