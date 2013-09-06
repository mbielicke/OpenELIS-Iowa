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

import java.util.ArrayList;
import java.util.List;

import org.openelis.domain.InventoryLocationViewDO;
import org.openelis.domain.InventoryXPutViewDO;
import org.openelis.manager.OrderManager1;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class VendorOrderFillTabUI extends Screen {
    @UiTemplate("VendorOrderFillTab.ui.xml")
    interface VendorOrderFillTabUiBinder extends UiBinder<Widget, VendorOrderFillTabUI> {
    };

    private static VendorOrderFillTabUiBinder uiBinder = GWT.create(VendorOrderFillTabUiBinder.class);

    @UiField
    protected Table                           table;

    protected Screen                          parentScreen;

    protected boolean                         isVisible;

    protected OrderManager1                   manager, displayedManager;

    public VendorOrderFillTabUI(Screen parentScreen, EventBus bus) {
        this.parentScreen = parentScreen;
        setEventBus(bus);
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
        displayedManager = null;
    }

    private void initialize() {
        addScreenHandler(table, "table", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                table.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(true);
                table.setQueryMode(false);
            }
        });

        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displayFills();
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
                                       displayFills();
                                   }
                               });
    }

    public void setData(OrderManager1 manager) {
        if (DataBaseUtil.isDifferent(this.manager, manager)) {
            displayedManager = this.manager;
            this.manager = manager;
        }
    }

    private void displayFills() {
        int count1, count2;
        boolean dataChanged;
        InventoryLocationViewDO loc1, loc2;
        InventoryXPutViewDO receipt1, receipt2;

        if ( !isVisible)
            return;

        count1 = displayedManager == null ? 0 : displayedManager.receipt.count();
        count2 = manager == null ? 0 : manager.receipt.count();

        /*
         * find out if there's any difference between the receipts of the two
         * managers
         */
        if (count1 == count2) {
            dataChanged = false;
            for (int i = 0; i < count1; i++ ) {
                receipt1 = displayedManager.receipt.get(i);
                receipt2 = manager.receipt.get(i);
                loc1 = receipt1.getInventoryLocation();
                loc2 = receipt2.getInventoryLocation();
                if (DataBaseUtil.isDifferent(receipt1.getId(), receipt1.getId()) ||
                    DataBaseUtil.isDifferent(loc1.getInventoryItemName(),
                                             loc2.getInventoryItemName()) ||
                    DataBaseUtil.isDifferent(loc1.getStorageLocationName(),
                                             loc2.getStorageLocationName()) ||
                    DataBaseUtil.isDifferent(loc1.getStorageLocationUnitDescription(),
                                             loc2.getStorageLocationUnitDescription()) ||
                    DataBaseUtil.isDifferent(loc1.getStorageLocationLocation(),
                                             loc2.getStorageLocationLocation()) ||
                    DataBaseUtil.isDifferent(receipt1.getQuantity(), receipt2.getQuantity()) ||
                    DataBaseUtil.isDifferent(loc1.getLotNumber(), loc2.getLotNumber()) ||
                    DataBaseUtil.isDifferent(loc1.getExpirationDate(), loc2.getExpirationDate()) ||
                    DataBaseUtil.isDifferent(loc1.getStorageLocationLocation(),
                                             loc2.getStorageLocationLocation()) ||
                    DataBaseUtil.isDifferent(receipt1.getInventoryReceiptReceivedDate(),
                                             receipt2.getInventoryReceiptReceivedDate()) ||
                    DataBaseUtil.isDifferent(receipt1.getInventoryReceiptUnitCost(),
                                             receipt2.getInventoryReceiptUnitCost()) ||
                    DataBaseUtil.isDifferent(receipt1.getExternalReference(),
                                             receipt2.getExternalReference())) {
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
        Row row;
        InventoryXPutViewDO data;
        InventoryLocationViewDO loc;
        StringBuffer buf;
        ArrayList<String> names;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        names = new ArrayList<String>();
        buf = new StringBuffer();

        for (int i = 0; i < manager.receipt.count(); i++ ) {
            data = manager.receipt.get(i);
            loc = data.getInventoryLocation();
            row = new Row(8);
            row.setCell(0, loc.getInventoryItemName());
            names.clear();
            names.add(loc.getStorageLocationName());
            names.add(", ");
            names.add(loc.getStorageLocationUnitDescription());
            names.add(" ");
            names.add(loc.getStorageLocationLocation());
            buf.setLength(0);
            row.setCell(1, concat(names, buf));
            row.setCell(2, data.getQuantity());
            row.setCell(3, loc.getLotNumber());
            row.setCell(4, loc.getExpirationDate());
            row.setCell(5, data.getInventoryReceiptReceivedDate());
            row.setCell(6, data.getInventoryReceiptUnitCost());
            row.setCell(7, data.getExternalReference());
            row.setData(data);
            model.add(row);
        }

        return model;
    }

    private String concat(List<String> list, StringBuffer buf) {
        for (String i : list) {
            if (i != null)
                buf.append(i);
        }
        return buf.toString();
    }
}
