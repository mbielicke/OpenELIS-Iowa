package org.openelis.modules.organization1.client;

import static org.openelis.ui.screen.State.ADD;
import static org.openelis.ui.screen.State.QUERY;
import static org.openelis.ui.screen.State.UPDATE;

import java.util.ArrayList;

import org.openelis.cache.CategoryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OrganizationContactDO;
import org.openelis.manager.OrganizationManager1;
import org.openelis.meta.OrganizationMeta;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class ContactTabUI extends Screen {

    @UiTemplate("ContactTab.ui.xml")
    interface ContactTabUiBinder extends UiBinder<Widget, ContactTabUI> {
    };

    private static ContactTabUiBinder uiBinder = GWT.create(ContactTabUiBinder.class);

    @UiField
    protected Table<Row>              table;

    @UiField
    protected Button                  remove, add;

    @UiField
    protected Dropdown<Integer>       type;

    @UiField
    protected Dropdown<String>        stateDrop, country;

    protected Screen                  parentScreen;

    protected EventBus                parentBus;

    protected boolean                 isVisible, redraw;

    private OrganizationManager1      manager;

    public ContactTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
    }

    private void initialize() {
        ArrayList<Item<String>> smodel;
        ArrayList<Item<Integer>> imodel;
        ArrayList<DictionaryDO> list;
        Item<Integer> irow;
        Item<String> srow;

        addScreenHandler(table, "table", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                if ( !isState(QUERY))
                    table.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(true);
                table.setQueryMode(isState(QUERY));
            }

            public Object getQuery() {
                ArrayList<QueryData> qds = new ArrayList<QueryData>();
                QueryData qd;

                for (int i = 0; i < 13; i++ ) {
                    qd = (QueryData) ((Queryable)table.getColumnWidget(i)).getQuery();
                    if (qd != null) {
                        switch (i) {
                            case 0:
                                qd.setKey(OrganizationMeta.getContactContactTypeId());
                                break;
                            case 1:
                                qd.setKey(OrganizationMeta.getContactName());
                                break;
                            case 2:
                                qd.setKey(OrganizationMeta.getContactAddressWorkPhone());
                                break;
                            case 3:
                                qd.setKey(OrganizationMeta.getContactAddressHomePhone());
                                break;
                            case 4:
                                qd.setKey(OrganizationMeta.getContactAddressCellPhone());
                                break;
                            case 5:
                                qd.setKey(OrganizationMeta.getContactAddressFaxPhone());
                                break;
                            case 6:
                                qd.setKey(OrganizationMeta.getContactAddressEmail());
                                break;
                            case 7:
                                qd.setKey(OrganizationMeta.getContactAddressMultipleUnit());
                                break;
                            case 8:
                                qd.setKey(OrganizationMeta.getContactAddressStreetAddress());
                                break;
                            case 9:
                                qd.setKey(OrganizationMeta.getContactAddressCity());
                                break;
                            case 10:
                                qd.setKey(OrganizationMeta.getContactAddressState());
                                break;
                            case 11:
                                qd.setKey(OrganizationMeta.getContactAddressZipCode());
                                break;
                            case 12:
                                qd.setKey(OrganizationMeta.getAddressCountry());
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
                if ( !isState(ADD, UPDATE, QUERY))
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
                val = table.getValueAt(r, c);

                try {
                    data = manager.contact.get(r);
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
                        data.getAddress().setWorkPhone((String)val);
                        break;
                    case 3:
                        data.getAddress().setHomePhone((String)val);
                        break;
                    case 4:
                        data.getAddress().setCellPhone((String)val);
                        break;
                    case 5:
                        data.getAddress().setFaxPhone((String)val);
                        break;
                    case 6:
                        data.getAddress().setEmail((String)val);
                        break;
                    case 7:
                        data.getAddress().setMultipleUnit((String)val);
                        break;
                    case 8:
                        data.getAddress().setStreetAddress((String)val);
                        break;
                    case 9:
                        data.getAddress().setCity((String)val);
                        break;
                    case 10:
                        data.getAddress().setState((String)val);
                        break;
                    case 11:
                        data.getAddress().setZipCode((String)val);
                        break;
                    case 12:
                        data.getAddress().setCountry((String)val);
                        break;
                }
            }
        });

        table.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                manager.contact.add();
            }
        });

        table.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                manager.contact.remove(event.getIndex());
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                remove.setEnabled(isState(ADD, UPDATE));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                add.setEnabled(isState(ADD, UPDATE));
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displayContacts();
            }
        });

        imodel = new ArrayList<Item<Integer>>();
        imodel.add(new Item<Integer>(null, ""));
        list = CategoryCache.getBySystemName("contact_type");
        for (DictionaryDO d : list) {
            irow = new Item<Integer>(d.getId(), d.getEntry());
            irow.setEnabled( ("Y".equals(d.getIsActive())));
            imodel.add(irow);
        }
        type.setModel(imodel);

        smodel = new ArrayList<Item<String>>();
        smodel.add(new Item<String>(null, ""));
        list = CategoryCache.getBySystemName("state");
        for (DictionaryDO d : list) {
            srow = new Item<String>(d.getEntry(), d.getEntry());
            srow.setEnabled( ("Y".equals(d.getIsActive())));
            smodel.add(srow);
        }
        stateDrop.setModel(smodel);

        smodel = new ArrayList<Item<String>>();
        smodel.add(new Item<String>(null, ""));
        list = CategoryCache.getBySystemName("country");
        for (DictionaryDO d : list) {
            srow = new Item<String>(d.getEntry(), d.getEntry());
            srow.setEnabled( ("Y".equals(d.getIsActive())));
            smodel.add(srow);
        }
        country.setModel(smodel);
    }

    @UiHandler("add")
    public void add(ClickEvent event) {
        int n;

        table.finishEditing();
        table.unselectAll();
        table.addRow();
        n = table.getRowCount() - 1;
        table.selectRowAt(n);
        table.scrollToVisible(table.getSelectedRow());
        table.startEditing(n, 0);
    }

    @UiHandler("remove")
    protected void remove(ClickEvent event) {
        int r;

        r = table.getSelectedRow();
        if (r > -1 && table.getRowCount() > 0)
            table.removeRowAt(r);
    }

    public void setData(OrganizationManager1 manager) {
        if (DataBaseUtil.isDifferent(this.manager, manager))
            this.manager = manager;
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    public void onDataChange() {
        int count1, count2;
        OrganizationContactDO contact;
        Row r;

        count1 = table.getRowCount();
        count2 = manager == null ? 0 : manager.contact.count();

        /*
         * find out if there's any difference between the contact being
         * displayed and the contact in the manager
         */
        if (count1 == count2) {
            for (int i = 0; i < count1; i++ ) {
                contact = manager.contact.get(i);
                r = table.getRowAt(i);
                if (DataBaseUtil.isDifferent(contact.getContactTypeId(), r.getCell(0)) ||
                    DataBaseUtil.isDifferent(contact.getName(), r.getCell(1)) ||
                    DataBaseUtil.isDifferent(contact.getAddress().getWorkPhone(), r.getCell(2)) ||
                    DataBaseUtil.isDifferent(contact.getAddress().getHomePhone(), r.getCell(3)) ||
                    DataBaseUtil.isDifferent(contact.getAddress().getCellPhone(), r.getCell(4)) ||
                    DataBaseUtil.isDifferent(contact.getAddress().getFaxPhone(), r.getCell(5)) ||
                    DataBaseUtil.isDifferent(contact.getAddress().getEmail(), r.getCell(6)) ||
                    DataBaseUtil.isDifferent(contact.getAddress().getMultipleUnit(), r.getCell(7)) ||
                    DataBaseUtil.isDifferent(contact.getAddress().getStreetAddress(), r.getCell(8)) ||
                    DataBaseUtil.isDifferent(contact.getAddress().getCity(), r.getCell(9)) ||
                    DataBaseUtil.isDifferent(contact.getAddress().getState(), r.getCell(10)) ||
                    DataBaseUtil.isDifferent(contact.getAddress().getZipCode(), r.getCell(11)) ||
                    DataBaseUtil.isDifferent(contact.getAddress().getCountry(), r.getCell(12))) {
                    redraw = true;
                    break;
                }
            }
        } else {
            redraw = true;
        }

        displayContacts();
    }

    private void displayContacts() {
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
        OrganizationContactDO data;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.contact.count(); i++ ) {
                data = (OrganizationContactDO)manager.contact.get(i);

                row = new Row(13);
                row.setCell(0, data.getContactTypeId());
                row.setCell(1, data.getName());
                row.setCell(2, data.getAddress().getWorkPhone());
                row.setCell(3, data.getAddress().getHomePhone());
                row.setCell(4, data.getAddress().getCellPhone());
                row.setCell(5, data.getAddress().getFaxPhone());
                row.setCell(6, data.getAddress().getEmail());
                row.setCell(7, data.getAddress().getMultipleUnit());
                row.setCell(8, data.getAddress().getStreetAddress());
                row.setCell(9, data.getAddress().getCity());
                row.setCell(10, data.getAddress().getState());
                row.setCell(11, data.getAddress().getZipCode());
                row.setCell(12, data.getAddress().getCountry());
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }
}
