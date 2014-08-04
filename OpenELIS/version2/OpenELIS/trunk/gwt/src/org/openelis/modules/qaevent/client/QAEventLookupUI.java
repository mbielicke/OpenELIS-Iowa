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
package org.openelis.modules.qaevent.client;

import static org.openelis.modules.main.client.Logger.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.openelis.cache.CategoryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.QaEventDO;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class is used to allow users to add or more qa event(s) to a sample or
 * analysis
 */

public abstract class QAEventLookupUI extends Screen {

    @UiTemplate("QAEventLookup.ui.xml")
    interface QAEventLookupUIBinder extends UiBinder<Widget, QAEventLookupUI> {
    };

    private static QAEventLookupUIBinder uiBinder = GWT.create(QAEventLookupUIBinder.class);

    @UiField
    protected Button                     okButton, cancelButton;

    @UiField
    protected Table                      table;

    @UiField
    protected Dropdown<Integer>          type;

    protected Integer                    testId;

    protected ArrayList<QaEventDO>       allQAEvents, selectedQAEvents;

    public QAEventLookupUI() {
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
    }

    private void initialize() {
        ArrayList<Item<Integer>> model;

        addScreenHandler(table, "table", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                table.setModel(getTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? okButton : cancelButton;
            }
        });

        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }
        });

        table.setAllowMultipleSelection(true);

        addScreenHandler(okButton, "okButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                okButton.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? cancelButton : table;
            }
        });

        addScreenHandler(cancelButton, "cancelButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                cancelButton.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? table : okButton;
            }
        });

        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("qaevent_type"))
            model.add(new Item<Integer>(d.getId(), d.getEntry()));

        type.setModel(model);
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    /**
     * refreshes the screen's view by setting the state and loading data in
     * widgets
     */
    public void setData(Integer testId) {
        ScheduledCommand cmd;

        this.testId = testId;

        setState(state);
        fireDataChange();
        /*
         * without this scheduled command, setting focus on the table doesn't
         * work
         */
        cmd = new ScheduledCommand() {
            @Override
            public void execute() {
                if (table.getRowCount() > 0) {
                    table.selectRowAt(0);
                    table.setFocus(true);
                }
            }
        };
        Scheduler.get().scheduleDeferred(cmd);
    }

    public Integer getTestId() {
        return testId;
    }
    
    public ArrayList<QaEventDO> getSelectedQAEvents() {
        return selectedQAEvents;
    }

    /**
     * overridden to respond to the user clicking "ok"
     */
    public abstract void ok();

    /**
     * overridden to respond to the user clicking "cancel"
     */
    public abstract void cancel();

    @UiHandler("okButton")
    protected void ok(ClickEvent event) {
        Row row;

        if (selectedQAEvents == null)
            selectedQAEvents = new ArrayList<QaEventDO>();
        else
            selectedQAEvents.clear();

        for (int i : table.getSelectedRows()) {
            row = table.getRowAt(i);
            selectedQAEvents.add((QaEventDO)row.getData());
        }

        window.close();
        ok();
    }

    @UiHandler("cancelButton")
    protected void cancel(ClickEvent event) {
        window.close();
        cancel();
    }

    private ArrayList<Row> getTableModel() {
        QaEventDO lastData;
        Row row;
        ArrayList<Row> model;

        try {
            if (allQAEvents == null) {
                window.setBusy(Messages.get().fetching());
                /*
                 * all qa events in the system
                 */
                allQAEvents = QaEventService.get().fetchAll();
                window.clearStatus();
            }
        } catch (Throwable e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            window.close();
        }

        /*
         * If a test is not specified then only the generic qa events (not
         * linked to any test) are shown. Otherwise, make a list of generic and
         * this test's specific qa events and in case of name conflict, let test
         * specific override.
         */
        model = new ArrayList<Row>();
        for (QaEventDO data : allQAEvents) {
            if ( (data.getTestId() == null && testId == null) ||
                (data.getTestId() == null && testId != null) || data.getTestId().equals(testId)) {
                if (model.size() != 0) {
                    lastData = model.get(model.size() - 1).getData();
                    /*
                     * if a test was specified then generic qa events are
                     * replaced with the ones specific to the test if their
                     * names match
                     */
                    if (data.getName().equals(lastData.getName()) && lastData.getTestId() == null)
                        model.remove(model.size() - 1);
                }

                row = new Item<Integer>(data.getId(),
                                        data.getName(),
                                        data.getDescription(),
                                        data.getTypeId(),
                                        data.getIsBillable());
                row.setData(data);
                model.add(row);
            }
        }

        return model;
    }
}