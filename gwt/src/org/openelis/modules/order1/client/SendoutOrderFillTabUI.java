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

import org.openelis.domain.InventoryXUseViewDO;
import org.openelis.manager.IOrderManager1;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
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

public class SendoutOrderFillTabUI extends Screen {
    @UiTemplate("SendoutOrderFillTab.ui.xml")
    interface SendoutOrderFillTabUiBinder extends UiBinder<Widget, SendoutOrderFillTabUI> {
    };

    private static SendoutOrderFillTabUiBinder uiBinder = GWT.create(SendoutOrderFillTabUiBinder.class);

    @UiField
    protected Table<Row>                       table;

    protected Screen                           parentScreen;

    protected EventBus                         parentBus;

    protected boolean                          isVisible, redraw;

    protected IOrderManager1                    manager;

    public SendoutOrderFillTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
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
    }

    public void setData(IOrderManager1 manager) {
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
        InventoryXUseViewDO fill;
        Row r;
        StringBuffer buf;
        ArrayList<String> names;

        count1 = table.getRowCount();
        count2 = manager == null ? 0 : manager.fill.count();
        names = new ArrayList<String>();
        buf = new StringBuffer();

        /*
         * find out if there's any difference between the fill data being
         * displayed and the fill data in the manager
         */
        if (count1 == count2) {
            for (int i = 0; i < count1; i++ ) {
                fill = manager.fill.get(i);
                r = table.getRowAt(i);
                names.clear();
                names.add(fill.getStorageLocationName());
                names.add(", ");
                names.add(fill.getStorageLocationUnitDescription());
                names.add(" ");
                names.add(fill.getStorageLocationLocation());
                buf.setLength(0);
                if (DataBaseUtil.isDifferent(fill.getInventoryItemName(), r.getCell(0)) ||
                    DataBaseUtil.isDifferent(concat(names, buf), r.getCell(1)) ||
                    DataBaseUtil.isDifferent(fill.getQuantity(), r.getCell(2)) ||
                    DataBaseUtil.isDifferent(fill.getInventoryLocationLotNumber(), r.getCell(3)) ||
                    DataBaseUtil.isDifferent(fill.getInventoryLocationExpirationDate(),
                                             r.getCell(4))) {
                    redraw = true;
                    break;
                }
            }
        } else {
            redraw = true;
        }

        displayFills();
    }

    private void displayFills() {
        if ( !isVisible)
            return;

        if (redraw) {
            redraw = false;
            setState(state);
            fireDataChange();
        }
    }

    private ArrayList<Row> getTableModel() {
        Row row;
        InventoryXUseViewDO fillData;
        StringBuffer buf;
        ArrayList<String> names;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        names = new ArrayList<String>();
        buf = new StringBuffer();

        for (int i = 0; i < manager.fill.count(); i++ ) {
            fillData = manager.fill.get(i);
            row = new Row(5);
            row.setCell(0, fillData.getInventoryItemName());
            names.clear();
            names.add(fillData.getStorageLocationName());
            names.add(", ");
            names.add(fillData.getStorageLocationUnitDescription());
            names.add(" ");
            names.add(fillData.getStorageLocationLocation());
            buf.setLength(0);
            row.setCell(1, concat(names, buf));
            row.setCell(2, fillData.getQuantity());
            row.setCell(3, fillData.getInventoryLocationLotNumber());
            row.setCell(4, fillData.getInventoryLocationExpirationDate());
            row.setData(fillData);
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
