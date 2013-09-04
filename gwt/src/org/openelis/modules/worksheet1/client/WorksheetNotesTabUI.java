/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.modules.worksheet1.client;

import static org.openelis.ui.screen.State.*;

import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.NoteViewDO;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.manager.WorksheetManager1;
import org.openelis.modules.note.client.EditNoteScreen;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.ModalWindow;
import org.openelis.ui.widget.NotesPanel;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class WorksheetNotesTabUI extends Screen {

    @UiTemplate("WorksheetNotesTab.ui.xml")
    interface WorksheetNotesTabUiBinder extends UiBinder<Widget, WorksheetNotesTabUI> {
    };

    private static WorksheetNotesTabUiBinder uiBinder = GWT.create(WorksheetNotesTabUiBinder.class);

    protected boolean                        canEdit, isVisible;
    protected Integer                        userId;
    protected String                         userName;
    @UiField
    protected Button                         editButton;
    protected EditNoteScreen                 editNote;
    protected NoteViewDO                     note;
    @UiField
    protected NotesPanel                     notesPanel;
    protected Screen                         parentScreen;
    protected WorksheetManager1              displayedManager, manager;
    protected WorksheetNotesTabUI            screen;

    public WorksheetNotesTabUI(Screen parentScreen, EventBus bus) {
        this.parentScreen = parentScreen;
        if (bus != null)
            setEventBus(bus);
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        userId = UserCache.getPermission().getSystemUserId();
        userName = UserCache.getPermission().getLoginName();
    }

    public void initialize() {
        screen = this;

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                editButton.setEnabled(isState(ADD, UPDATE) && canEdit);
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displayNotes();
            }
        });

        /*
         * handlers for the events fired by the screen containing this tab
         */
        bus.addHandlerToSource(StateChangeEvent.getType(), parentScreen, new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                evaluateEdit();
                setState(event.getState());
            }
        });

        bus.addHandlerToSource(DataChangeEvent.getType(), parentScreen, new DataChangeEvent.Handler() {
            public void onDataChange(DataChangeEvent event) {
                displayNotes();
            }
        });
    }

    public void setData(WorksheetManager1 manager) {
        if (!DataBaseUtil.isSame(this.manager, manager)) {
            displayedManager = this.manager;
            this.manager = manager;
        }
    }

    public void evaluateEdit() {
        canEdit = false;
        if (manager != null)
            canEdit = Constants.dictionary().WORKSHEET_WORKING.equals(manager.getWorksheet()
                                                                             .getStatusId());
    }

    private void displayNotes() {
        boolean dataChanged;
        int count1, count2, i;
        NoteViewDO note1, note2;

        if (!isVisible)
            return;

        count1 = displayedManager == null ? 0 : displayedManager.note.count();
        count2 = manager == null ? 0 : manager.note.count();

        if (count1 == count2) {
            dataChanged = false;
            if (count1 != 0) {
                note1 = displayedManager.note.get(0);
                note2 = manager.note.get(0);
                if ((note1 == null && note2 != null) || (note1 != null && note2 == null) ||
                    DataBaseUtil.isDifferent(note1.getId(), note2.getId()))
                    dataChanged = true;
            }
        } else {
            dataChanged = true;
        }

        if (dataChanged) {
            displayedManager = manager;
            evaluateEdit();
            notesPanel.clearNotes();
            for (i = 0; i < manager.note.count(); i++) {
                note1 = manager.note.get(i);
                notesPanel.addNote(note1.getSubject(),
                                   note1.getSystemUser(),
                                   note1.getText(),
                                   note1.getTimestamp());
            }
        }
    }

    @SuppressWarnings("unused")
    @UiHandler("editButton")
    protected void showEditWindow(ClickEvent event) {
        ModalWindow modal;

        if (editNote == null) {
            try {
                editNote = new EditNoteScreen();
                editNote.addActionHandler(new ActionHandler<EditNoteScreen.Action>() {
                    public void onAction(ActionEvent<EditNoteScreen.Action> event) {
                        if (event.getAction() == EditNoteScreen.Action.OK) {
                            if (note.getText() == null || note.getText().trim().length() == 0)
                                manager.note.removeEditing();
                            fireDataChange();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Window.alert("Error in EditNote:" + e.getMessage());
                return;
            }
        }

        modal = new ModalWindow();
        modal.setSize("518px", "569px");
        modal.setName(Messages.get().noteEditor());
        modal.setContent(editNote);

        note = null;
        try {
            note = manager.note.getEditing();
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert("Error in EditNote:" + e.getMessage());
        }
        note.setSystemUser(userName);
        note.setSystemUserId(userId);
        note.setTimestamp(Datetime.getInstance(Datetime.YEAR, Datetime.SECOND));
        editNote.setNote(note);
    }
}