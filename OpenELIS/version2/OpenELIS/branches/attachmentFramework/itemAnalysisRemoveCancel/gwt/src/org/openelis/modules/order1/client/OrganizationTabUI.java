/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.modules.order1.client;

import static org.openelis.modules.main.client.Logger.logger;
import static org.openelis.ui.screen.State.ADD;
import static org.openelis.ui.screen.State.DISPLAY;
import static org.openelis.ui.screen.State.QUERY;
import static org.openelis.ui.screen.State.UPDATE;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.cache.CategoryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OrderOrganizationViewDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.manager.OrderManager1;
import org.openelis.meta.OrderMeta;
import org.openelis.modules.organization.client.OrganizationService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.QueryFieldUtil;
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

public class OrganizationTabUI extends Screen {

    @UiTemplate("OrganizationTab.ui.xml")
    interface OrganizationTabUiBinder extends UiBinder<Widget, OrganizationTabUI> {
    };

    private static OrganizationTabUiBinder uiBinder = GWT.create(OrganizationTabUiBinder.class);

    @UiField
    protected Table                        table;

    @UiField
    protected Dropdown<Integer>            type;

    @UiField
    protected Dropdown<String>             orgState, orgCountry;

    @UiField
    protected AutoComplete                 organizationName;

    @UiField
    protected Button                       removeOrganizationButton, addOrganizationButton;

    protected Screen                       parentScreen;

    protected boolean                      isVisible;

    protected OrderManager1                manager, displayedManager;

    public OrganizationTabUI(Screen parentScreen, EventBus bus) {
        this.parentScreen = parentScreen;
        setEventBus(bus);
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
        displayedManager = null;
    }

    private void initialize() {
        ArrayList<DictionaryDO> list;
        ArrayList<Item<Integer>> model;
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
                for (int i = 0; i < 9; i++ ) {
                    qd = (QueryData) ((Queryable)table.getColumnWidget(i)).getQuery();
                    if (qd != null) {
                        switch (i) {
                            case 0:
                                qd.setKey(OrderMeta.getOrderOrganizationTypeId());
                                break;
                            case 1:
                                qd.setKey(OrderMeta.getOrderOrganizationOrganizationAttention());
                                break;
                            case 2:
                                qd.setKey(OrderMeta.getOrderOrganizationOrganizationName());
                                break;
                            case 3:
                                qd.setKey(OrderMeta.getOrderOrganizationOrganizationAddressMultipleUnit());
                                break;
                            case 4:
                                qd.setKey(OrderMeta.getOrderOrganizationOrganizationAddressStreetAddress());
                                break;
                            case 5:
                                qd.setKey(OrderMeta.getOrderOrganizationOrganizationAddressCity());
                                break;
                            case 6:
                                qd.setKey(OrderMeta.getOrderOrganizationOrganizationAddressState());
                                break;
                            case 7:
                                qd.setKey(OrderMeta.getOrganizationOrganizationAddressZipCode());
                                break;
                            case 8:
                                qd.setKey(OrderMeta.getOrderOrganizationOrganizationAddressCountry());
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
                if ( !isState(QUERY) && ! (isState(ADD, UPDATE) && event.getCol() <= 2))
                    event.cancel();
            }
        });

        table.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                OrderOrganizationViewDO data;
                OrganizationDO org;

                r = event.getRow();
                c = event.getCol();
                val = table.getValueAt(r, c);

                data = table.getRowAt(r).getData();

                switch (c) {
                    case 0:
                        data.setTypeId((Integer)val);
                        break;
                    case 1:
                        data.setOrganizationAttention((String)val);
                        break;
                    case 2:
                        if (val == null) {
                            data.setOrganizationId(null);
                            data.setOrganizationName(null);
                            data.setOrganizationAddressMultipleUnit(null);
                            data.setOrganizationAddressStreetAddress(null);
                            data.setOrganizationAddressCity(null);
                            data.setOrganizationAddressState(null);
                            data.setOrganizationAddressZipCode(null);
                            data.setOrganizationAddressCountry(null);
                            table.setValueAt(r, 3, null);
                            table.setValueAt(r, 4, null);
                            table.setValueAt(r, 5, null);
                            table.setValueAt(r, 6, null);
                            table.setValueAt(r, 7, null);
                            table.setValueAt(r, 8, null);
                        } else {
                            org = (OrganizationDO) ( ((AutoCompleteValue)val).getData());
                            if (org != null) {
                                data.setOrganizationId(org.getId());
                                data.setOrganizationName(org.getName());
                                data.setOrganizationAddressMultipleUnit(org.getAddress()
                                                                           .getMultipleUnit());
                                data.setOrganizationAddressStreetAddress(org.getAddress()
                                                                            .getStreetAddress());
                                data.setOrganizationAddressCity(org.getAddress().getCity());
                                data.setOrganizationAddressState(org.getAddress().getState());
                                data.setOrganizationAddressZipCode(org.getAddress().getZipCode());
                                data.setOrganizationAddressCountry(org.getAddress().getCountry());
                                table.setValueAt(r, 3, org.getAddress().getMultipleUnit());
                                table.setValueAt(r, 4, org.getAddress().getStreetAddress());
                                table.setValueAt(r, 5, org.getAddress().getCity());
                                table.setValueAt(r, 6, org.getAddress().getState());
                                table.setValueAt(r, 7, org.getAddress().getZipCode());
                                table.setValueAt(r, 8, org.getAddress().getCountry());
                            }
                        }
                        break;
                }
            }
        });

        table.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                OrderOrganizationViewDO data;
                
                /*
                 * When the fields of the order organizations are updated in the handler for cellEditedEvent,
                 * the index of the row can't be used to access the organization that corresponds to a given
                 * row, because that organization may be of type ship to. In order to keep track of the
                 * organization corresponding to this row, we set it as the data of the row that caused it to 
                 * be added to the manager.
                 */
                data = manager.organization.add();
                event.getRow().setData(data);
            }
        });

        table.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                OrderOrganizationViewDO data;
                
                data = event.getRow().getData();
                manager.organization.remove(data);
            }
        });

        organizationName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                getOrganizationMatches(event.getMatch());
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                removeOrganizationButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                addOrganizationButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displayOrganizations();
            }
        });

        /*
         * handlers for the events fired by the screen containing this tab
         */
        bus.addHandlerToSource(StateChangeEvent.getType(),
                               parentScreen,
                               new StateChangeEvent.Handler() {
                                   public void onStateChange(StateChangeEvent event) {
                                       setState(event.getState());
                                   }
                               });

        bus.addHandlerToSource(DataChangeEvent.getType(),
                               parentScreen,
                               new DataChangeEvent.Handler() {
                                   public void onDataChange(DataChangeEvent event) {
                                       displayOrganizations();
                                   }
                               });

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = CategoryCache.getBySystemName("organization_type");
        for (DictionaryDO data : list) {
            item = new Item<Integer>(data.getId(), data.getEntry());
            item.setEnabled( ("Y".equals(data.getIsActive())));
            model.add(item);
        }
        type.setModel(model);

        smodel = new ArrayList<Item<String>>();
        smodel.add(new Item<String>(null, ""));
        list = CategoryCache.getBySystemName("state");
        for (DictionaryDO data : list) {
            if ("Y".equals(data.getIsActive()))
                smodel.add(new Item<String>(data.getEntry(), data.getEntry()));
        }
        orgState.setModel(smodel);

        smodel = new ArrayList<Item<String>>();
        smodel.add(new Item<String>(null, ""));
        list = CategoryCache.getBySystemName("country");
        for (DictionaryDO data : list) {
            if ("Y".equals(data.getIsActive()))
                smodel.add(new Item<String>(data.getEntry(), data.getEntry()));
        }
        orgCountry.setModel(smodel);

    }

    public void setData(OrderManager1 manager) {
        if ( DataBaseUtil.isDifferent(this.manager, manager)) {
            displayedManager = this.manager;
            this.manager = manager;
        }
    }

    @UiHandler("removeOrganizationButton")
    protected void removeOrganization(ClickEvent event) {
        int r;

        r = table.getSelectedRow();
        if (r > -1 && table.getRowCount() > 0)
            table.removeRowAt(r);
    }

    @UiHandler("addOrganizationButton")
    protected void addOrganization(ClickEvent event) {
        int n;

        table.addRow();
        n = table.getRowCount() - 1;
        table.selectRowAt(n);
        table.scrollToVisible(table.getSelectedRow());
        table.startEditing(n, 0);
    }
    
    private void displayOrganizations() {
        int count1, count2;
        boolean dataChanged;
        OrderOrganizationViewDO org1, org2;

        if ( !isVisible)
            return;

        count1 = displayedManager == null ? 0 : displayedManager.organization.count();
        count2 = manager == null ? 0 : manager.organization.count();

        /*
         * find out if there's any difference between the organizations of the
         * two managers
         */
        if (count1 == count2) {
            dataChanged = false;
            for (int i = 0; i < count1; i++ ) {
                org1 = displayedManager.organization.get(i);
                org2 = manager.organization.get(i);

                if (DataBaseUtil.isDifferent(org1.getTypeId(), org2.getTypeId()) ||
                    DataBaseUtil.isDifferent(org1.getOrganizationAttention(),
                                             org2.getOrganizationAttention()) ||
                    DataBaseUtil.isDifferent(org1.getOrganizationId(), org2.getOrganizationId())) {
                    dataChanged = true;
                    break;
                }
            }
        } else {
            dataChanged = true;
        }

        if (dataChanged) {
            displayedManager = manager;
            setState(state);
            fireDataChange();
        }
    }

    private ArrayList<Row> getTableModel() {
        int i;
        Row row;
        OrderOrganizationViewDO data;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        for (i = 0; i < manager.organization.count(); i++ ) {
            data = manager.organization.get(i);
            row = new Row(9);
            row.setCell(0, data.getTypeId());
            row.setCell(1, data.getOrganizationAttention());
            row.setCell(2, new AutoCompleteValue(data.getOrganizationId(),
                                                 data.getOrganizationName()));
            row.setCell(3, data.getOrganizationAddressMultipleUnit());
            row.setCell(4, data.getOrganizationAddressStreetAddress());
            row.setCell(5, data.getOrganizationAddressCity());
            row.setCell(6, data.getOrganizationAddressState());
            row.setCell(7, data.getOrganizationAddressZipCode());
            row.setCell(8, data.getOrganizationAddressCountry());
            row.setData(data);
            model.add(row);
        }

        return model;
    }

    private void getOrganizationMatches(String match) {
        Item<Integer> row;
        OrganizationDO data;
        ArrayList<OrganizationDO> list;
        ArrayList<Item<Integer>> model;

        parentScreen.getWindow().setBusy();
        try {
            list = OrganizationService.get()
                                      .fetchByIdOrName(QueryFieldUtil.parseAutocomplete(match));
            model = new ArrayList<Item<Integer>>();
            for (int i = 0; i < list.size(); i++ ) {
                row = new Item<Integer>(4);
                data = list.get(i);

                row.setKey(data.getId());
                row.setData(data);
                row.setCell(0, data.getName());
                row.setCell(1, data.getAddress().getStreetAddress());
                row.setCell(2, data.getAddress().getCity());
                row.setCell(3, data.getAddress().getState());

                model.add(row);
            }
            organizationName.showAutoMatches(model);
        } catch (Throwable e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        parentScreen.getWindow().clearStatus();
    }
}
