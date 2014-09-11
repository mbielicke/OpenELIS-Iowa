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
package org.openelis.modules.sample1.client;

import java.util.ArrayList;

import org.openelis.cache.SectionCache;
import org.openelis.domain.AttachmentItemViewDO;
import org.openelis.domain.SectionDO;
import org.openelis.manager.SampleManager1;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.calendar.Calendar;
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

public class AttachmentTabUI extends Screen {

    @UiTemplate("AttachmentTab.ui.xml")
    interface AttachmentTabUIBinder extends UiBinder<Widget, AttachmentTabUI> {
    };

    private static AttachmentTabUIBinder uiBinder = GWT.create(AttachmentTabUIBinder.class);

    @UiField
    protected Table                      currentTable, searchTable;

    @UiField
    protected Dropdown<Integer>          attachmentSection;

    @UiField
    protected Button                     detachButton, displayButton, moveLeftbutton, searchButton,
                    attachButton;

    @UiField
    protected Calendar                   attachmentCreatedDate;

    @UiField
    protected TextBox<String>            attachmentDescription;

    protected Screen                     parentScreen;

    protected AttachmentTabUI            screen;

    protected EventBus                   parentBus;

    protected SampleManager1             manager;

    protected boolean                    isVisible, redraw;

    public AttachmentTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
    }

    private void initialize() {
        ArrayList<Item<Integer>> model;

        screen = this;

        addScreenHandler(currentTable, "currentTable", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                currentTable.setModel(getCurrentTableModel());
            }

            public void onStateChange(StateChangeEvent event) {
                currentTable.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? detachButton : attachButton;
            }
        });

        currentTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displayAttachments();
            }
        });

        model = new ArrayList<Item<Integer>>();
        for (SectionDO s : SectionCache.getList())
            model.add(new Item<Integer>(s.getId(), s.getName()));
        
        attachmentSection.setModel(model);
    }

    public void setData(SampleManager1 manager) {
        this.manager = manager;
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    public void onDataChange() {
        int i, count1, count2;
        AttachmentItemViewDO data;
        Row row;

        count1 = currentTable.getRowCount();
        count2 = manager == null ? 0 : manager.attachment.count();

        if (count1 == count2) {
            /*
             * find out if there's any difference between the attachment item
             * being displayed and the attachment item in the manager
             */
            for (i = 0; i < count1; i++ ) {
                data = manager.attachment.get(i);

                row = currentTable.getRowAt(i);
                if (DataBaseUtil.isDifferent(data.getAttachmentCreatedDate(), row.getCell(0)) ||
                    DataBaseUtil.isDifferent(data.getAttachmentSectionId(), row.getCell(1)) ||
                    DataBaseUtil.isDifferent(data.getAttachmentDescription(), row.getCell(2))) {
                    redraw = true;
                    break;
                }
            }
        } else {
            redraw = true;
        }
        displayAttachments();
    }
    
    private ArrayList<Row> getCurrentTableModel() {
        AttachmentItemViewDO data;
        Row row;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        for (int i = 0; i < manager.attachment.count(); i++ ) {
            data = manager.attachment.get(i);
            row = new Row(3);
            row.setCell(0, data.getAttachmentCreatedDate());
            row.setCell(1, data.getAttachmentSectionId());
            row.setCell(2, data.getAttachmentDescription());
            model.add(row);
        }

        return model;
    }

    public void setFocus() {
        /*
         * set the button for sample qa lookup in focus
         */
        // if (isState(ADD, UPDATE))
        // sampleQALookupButton.setFocus(true);
    }

    private void displayAttachments() {
        if ( !isVisible)
            return;

        if (redraw) {
            redraw = false;
            fireDataChange();
        }
    }
}