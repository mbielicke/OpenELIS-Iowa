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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class InternalNotesTabUI extends Screen {

    @UiTemplate("InternalNotesTab.ui.xml")
    interface OrderInternalNotesTabUIBinder extends UiBinder<Widget, InternalNotesTabUI> {
    };

    private static OrderInternalNotesTabUIBinder uiBinder = GWT.create(OrderInternalNotesTabUIBinder.class);

    @UiField
    protected NotesPanel                         notePanel;

    @UiField
    protected Button                             addNoteButton;

    protected EditNoteLookupUI                   editNoteLookup;

    protected Screen                             parentScreen;

    protected OrderManager1                      manager;

    protected NoteViewDO                         displayedInternalNote;

    protected boolean                            isVisible, redraw;

    public InternalNotesTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
        displayedInternalNote = null;
    }

    private void initialize() {
        addScreenHandler(notePanel, "orderIntNotesPanel", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                drawNotes();
            }
        });

        addScreenHandler(addNoteButton, "orderIntNoteButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                addNoteButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displayNotes();
            }
        });
    }

    public void setData(OrderManager1 manager) {
        if (DataBaseUtil.isDifferent(this.manager, manager)) {
            this.manager = manager;
        }
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    /**
     * notifies the tab that it may need to refresh its widgets; if the data
     * currently showing in the widgets is the same as the data in the latest
     * manager then the widgets are not refreshed
     */
    public void onDataChange() {
        Integer id1, id2;

        /*
         * compare internal notes
         */
        id1 = displayedInternalNote != null ? displayedInternalNote.getId() : null;
        id2 = null;
        if (manager != null && manager.internalNote.count() > 0)
            id2 = manager.internalNote.get(0).getId();
        if (DataBaseUtil.isDifferent(id1, id2))
            redraw = true;

        displayNotes();
    }

    @UiHandler("addNoteButton")
    protected void addNote(ClickEvent event) {
        String subject, text;
        NoteViewDO note;
        ModalWindow modal;

        if (editNoteLookup == null) {
            editNoteLookup = new EditNoteLookupUI() {
                public void ok() {
                    NoteViewDO note;
                    if (DataBaseUtil.isEmpty(editNoteLookup.getText())) {
                        manager.internalNote.removeEditing();
                    } else {
                        note = manager.internalNote.getEditing();
                        note.setSubject(editNoteLookup.getSubject());
                        note.setText(editNoteLookup.getText());
                        note.setSystemUser(UserCache.getPermission().getLoginName());
                        note.setSystemUserId(UserCache.getPermission().getSystemUserId());
                        note.setTimestamp(Datetime.getInstance(Datetime.YEAR, Datetime.SECOND));
                    }
                    drawNotes();
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
         * If the order has an internal editing note as requested by the user
         * then set the note's subject and text in the lookup. If there isn't
         * such a note then don't create one until the user enters its subject
         * and text.
         */

        if (manager.internalNote.count() > 0)
            note = manager.internalNote.get(0);

        if (note != null && note.getId() == null) {
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
        editNoteLookup.setHasSubject(true);
    }

    private void displayNotes() {
        if ( !isVisible)
            return;

        if (redraw) {
            /*
             * don't redraw unless the data has changed
             */
            redraw = false;
            if (manager != null && manager.internalNote.count() > 0) {
                displayedInternalNote = manager.internalNote.get(0);
            } else {
                displayedInternalNote = null;
            }
            setState(state);
            fireDataChange();
        }
    }

    private void drawNotes() {
        NoteViewDO n;

        notePanel.clearNotes();
        if (manager != null) {
            for (int i = 0; i < manager.internalNote.count(); i++ ) {
                n = manager.internalNote.get(i);
                notePanel.addNote(n.getSubject(), n.getSystemUser(), n.getText(), n.getTimestamp());
            }
        }
    }
}