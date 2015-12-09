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
package org.openelis.modules.secondDataEntry.client.field;

import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;

import org.openelis.cache.CategoryCache;
import org.openelis.constants.Messages;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SampleQaEventViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.modules.secondDataEntry.client.VerificationScreen;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class manages the widgets used for verifying sample qa events
 */
public class SampleQAEvent extends MultiField<Table> {
    public SampleQAEvent(VerificationScreen parentScreen, TableRowElement tableRowElement,
                         Table editableWidget, int rowIndex) {
        super(parentScreen, tableRowElement, editableWidget, rowIndex);
        init();
    }

    /**
     * Makes the row in which the widgets are shown, visible and sets its style;
     * adds handlers to the widgets in the row
     */
    protected void init() {
        ScheduledCommand cmd;
        Item<Integer> row;
        ArrayList<Item<Integer>> model;

        setRowVisible();

        /*
         * this is done so that the table gets resized to show the headers and
         * rows after it's made visible; it won't get resized otherwise because
         * it's in a LayoutPanel, and that panel is inside a <td> and not
         * another panel; so the browser's chain for resizing panels gets broken
         * and doesn't reach the table
         */
        cmd = new ScheduledCommand() {
            @Override
            public void execute() {
                editableWidget.onResize();
            }
        };
        Scheduler.get().scheduleDeferred(cmd);

        parentScreen.addScreenHandler(editableWidget,
                                      "sampleQATable",
                                      new ScreenHandler<ArrayList<Row>>() {
                                          public void onDataChange(DataChangeEvent<ArrayList<Row>> event) {
                                              clear();
                                              editableWidget.setModel(getTableModel());
                                              setCount(parentScreen.getManager() != null ? parentScreen.getManager().qaEvent.count()
                                                                                        : 0);
                                          }

                                          public void onStateChange(StateChangeEvent event) {
                                              editableWidget.setEnabled(true);
                                          }

                                          public Widget onTab(boolean forward) {
                                              return forward ? nextTabWidget : prevTabWidget;
                                          }
                                      });
        /*
         * overridden because the table by default doesn't show any special
         * style on getting the focus
         */
        editableWidget.addDomHandler(new FocusHandler() {
            @Override
            public void onFocus(FocusEvent event) {
                if (editableWidget.getRowCount() > 0 && editableWidget.getSelectedRow() == -1)
                    editableWidget.selectRowAt(0);
            }
        }, FocusEvent.getType());

        editableWidget.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            @Override
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if ( !parentScreen.isState(UPDATE) || event.getCol() > 0)
                    event.cancel();
            }
        });

        editableWidget.addCellEditedHandler(new CellEditedHandler() {
            @Override
            public void onCellUpdated(CellEditedEvent event) {
                switch (event.getCol()) {
                    case 0:
                        verify(event.getRow());
                        break;
                }
            }
        });

        /*
         * set model for qa event type
         */
        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("qaevent_type")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }
        ((Dropdown<Integer>)editableWidget.getColumnWidget(2)).setModel(model);
    }

    public void copyFromSample() {
    }

    public void copyToSample() {
    }

    /**
     * Marks the qa event at the passed index as verified if the user checks the
     * checkbox in the row; if a qa event has not been verified, shows an error
     * for it
     */
    protected void verify(int i) {
        SampleQaEventViewDO sqa;

        isVerified[i] = "Y".equals(editableWidget.getValueAt(i, 0));
        if ( !isVerified[i] && parentScreen.getValidation() != null) {
            sqa = parentScreen.getManager().qaEvent.get(i);
            parentScreen.getValidation()
                        .addException(new Exception(Messages.get()
                                                            .secondDataEntry_qaEventNotVerified(sqa.getQaEventName())));
        }
    }

    /**
     * Creates and returns the model for the table
     */
    private ArrayList<Row> getTableModel() {
        SampleQaEventViewDO data;
        SampleManager1 sm;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        sm = parentScreen.getManager();
        if (sm == null)
            return model;

        for (int i = 0; i < sm.qaEvent.count(); i++ ) {
            data = sm.qaEvent.get(i);
            model.add(new Row("N", data.getQaEventName(), data.getTypeId(), data.getIsBillable()));
        }

        return model;
    }
}