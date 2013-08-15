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

import static org.openelis.ui.screen.State.ADD;
import static org.openelis.ui.screen.State.UPDATE;

import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.NoteViewDO;
import org.openelis.manager.OrderManager1;
import org.openelis.modules.note.client.EditNoteLookupUI;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.ModalWindow;
import org.openelis.ui.widget.NotesPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class SampleNotesTabUI extends Screen {

    @UiTemplate("SampleNotesTab.ui.xml")
    interface SampleNotesTabUIBinder extends UiBinder<Widget, SampleNotesTabUI> {
    };

    private static SampleNotesTabUIBinder uiBinder = GWT.create(SampleNotesTabUIBinder.class);

    @UiField
    protected NotesPanel                  notePanel;

    @UiField
    protected Button                      editNoteButton;

    protected EditNoteLookupUI            editNoteLookup;

    protected Screen                      parentScreen;

    protected OrderManager1               manager, displayedManager;

    protected boolean                     isVisible;

    public SampleNotesTabUI(Screen parentScreen, EventBus bus) {
        this.parentScreen = parentScreen;
        setEventBus(bus);
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
        displayedManager = null;
    }

    private void initialize() {
        addScreenHandler(notePanel, "sampleNotesPanel", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                drawNote();
            }
        });

        addScreenHandler(editNoteButton, "sampleNoteButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                editNoteButton.setEnabled(isState(ADD, UPDATE));
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
                                       displayNotes();
                                   }
                               });
    }

    public void setData(OrderManager1 manager) {
        if (DataBaseUtil.isDifferent(this.manager, manager)) {
            displayedManager = this.manager;
            this.manager = manager;
        }
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    @UiHandler("editNoteButton")
    protected void editNote(ClickEvent event) {
        showNoteLookup();
    }

    private void displayNotes() {
        int count1, count2;
        Integer id1, id2;

        if ( !isVisible)
            return;

        /*
         * compare external notes
         */
        count1 = 0;
        count2 = 0;
        id1 = null;
        id2 = null;
        if (displayedManager != null && displayedManager.sampleNote.get() != null){
            count1 = 1;
            id1 = displayedManager.sampleNote.get().getId();
        }

        if (manager != null && manager.sampleNote.get() != null) {
            count2 = 1;
            id2 = manager.sampleNote.get().getId();
        }

        if ( (count1 != count2) || DataBaseUtil.isDifferent(id1, id2)) {
            displayedManager = manager;
            setState(state);
            fireDataChange();
        }
    }

    private void drawNote() {
        NoteViewDO n;

        notePanel.clearNotes();
        if (manager != null) {
            n = manager.sampleNote.get();
            if (n != null)
                notePanel.addNote(n.getSubject(), n.getSystemUser(), n.getText(), n.getTimestamp());
        }
    }

    private void showNoteLookup() {
        String subject, text;
        NoteViewDO note;
        ModalWindow modal;

        if (editNoteLookup == null) {
            editNoteLookup = new EditNoteLookupUI() {
                public void ok() {
                    if (DataBaseUtil.isEmpty(editNoteLookup.getText()))
                        manager.sampleNote.removeEditing();
                    else
                        setNoteFields(manager.sampleNote.getEditing(),
                                      null,
                                      editNoteLookup.getText());
                    drawNote();
                }

                public void cancel() {
                    // ignore
                }
            };
        }

        note = null;
        subject = null;
        text = null;
        /*
         * If the order has an external editing note as requested by the user
         * then set the note's text in the lookup. If there isn't such a note
         * then don't create one until the user enters its text.
         */
        note = manager.sampleNote.get();
        if (note != null) {
            subject = note.getSubject();
            text = note.getText();
        }

        modal = new ModalWindow();
        modal.setSize("620px", "550px");
        modal.setName(Messages.get().gen_noteEditor());
        modal.setCSS(UIResources.INSTANCE.popupWindow());
        modal.setContent(editNoteLookup);

        editNoteLookup.setWindow(modal);
        editNoteLookup.setSubject(subject);
        editNoteLookup.setText(text);
        editNoteLookup.setHasSubject(false);
    }

    private void setNoteFields(NoteViewDO note, String subject, String text) {
        note.setSubject(subject);
        note.setText(text);
        note.setSystemUser(UserCache.getPermission().getLoginName());
        note.setSystemUserId(UserCache.getPermission().getSystemUserId());
        note.setTimestamp(Datetime.getInstance(Datetime.YEAR, Datetime.SECOND));
    }
}