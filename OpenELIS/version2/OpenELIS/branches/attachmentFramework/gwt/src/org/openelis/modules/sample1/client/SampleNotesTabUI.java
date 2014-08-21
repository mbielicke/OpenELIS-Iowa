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

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.State.*;

import java.util.logging.Level;

import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.NoteViewDO;
import org.openelis.manager.SampleManager1;
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

public class SampleNotesTabUI extends Screen {

    @UiTemplate("SampleNotesTab.ui.xml")
    interface SampleNotesTabUIBinder extends UiBinder<Widget, SampleNotesTabUI> {
    };

    private static SampleNotesTabUIBinder uiBinder = GWT.create(SampleNotesTabUIBinder.class);

    @UiField
    protected NotesPanel                  externalNotePanel, internalNotePanel;

    @UiField
    protected Button                      editNoteButton, addNoteButton;

    protected Screen                      parentScreen;

    protected EditNoteLookupUI            editNoteLookup;

    protected SampleManager1              manager;

    protected NoteViewDO                  displayedExtNote, displayedIntNote;

    protected boolean                     canEdit, isVisible, redraw;

    public SampleNotesTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
        displayedExtNote = null;
        displayedIntNote = null;
    }

    private void initialize() {
        addScreenHandler(externalNotePanel, "sampleExtNotesPanel", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                drawExternalNote();
            }
        });

        addScreenHandler(editNoteButton, "sampleExtNoteButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                editNoteButton.setEnabled(canEdit && isState(ADD, UPDATE));
            }

            public Widget onTab(boolean forward) {
                return forward ? addNoteButton : addNoteButton;
            }
        });

        addScreenHandler(internalNotePanel, "sampleIntNotesPanel", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                drawInternalNotes();
            }
        });

        addScreenHandler(addNoteButton, "sampleIntNoteButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                addNoteButton.setEnabled(isState(ADD, UPDATE));
            }

            public Widget onTab(boolean forward) {
                return forward ? editNoteButton : editNoteButton;
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                setState(state);
                displayNotes();
            }
        });
    }

    public void setData(SampleManager1 manager) {
        this.manager = manager;
    }

    public void setState(State state) {
        evaluateEdit();
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
        String txt1, txt2;

        /*
         * compare external note
         */
        id1 = null;
        txt1 = null;
        if (displayedExtNote != null) {
            id1 = displayedExtNote.getId();
            txt1 = displayedExtNote.getText();
        }

        id2 = null;
        txt2 = null;
        if (manager != null && manager.sampleExternalNote.get() != null) {
            id2 = manager.sampleExternalNote.get().getId();
            txt2 = manager.sampleExternalNote.get().getText();
        }

        /*
         * since the sample only has one external note, its id won't change but
         * its text can
         */
        redraw = DataBaseUtil.isDifferent(id1, id2) || DataBaseUtil.isDifferent(txt1, txt2);

        if ( !redraw) {
            /*
             * compare internal notes
             */
            id1 = displayedIntNote != null ? displayedIntNote.getId() : null;
            id2 = null;
            if (manager != null && manager.sampleInternalNote.count() > 0)
                id2 = manager.sampleInternalNote.get(0).getId();
            redraw = DataBaseUtil.isDifferent(id1, id2);
        }
        displayNotes();
    }

    public void setFocus() {
        /*
         * set the first enabled widget in the tabbing order in focus, i.e. edit
         * note button
         */
        if (isState(ADD, UPDATE))
            editNoteButton.setFocus(true);
    }

    @UiHandler("editNoteButton")
    protected void editNote(ClickEvent event) {
        showNoteLookup(true);
    }

    @UiHandler("addNoteButton")
    protected void addNote(ClickEvent event) {
        showNoteLookup(false);
    }

    private void displayNotes() {
        if ( !isVisible)
            return;

        if (redraw) {
            /*
             * don't redraw unless the data has changed
             */
            redraw = false;
            displayedExtNote = null;
            displayedIntNote = null;
            if (manager != null) {
                displayedExtNote = manager.sampleExternalNote.get();
                if (manager.sampleInternalNote.count() > 0)
                    displayedIntNote = manager.sampleInternalNote.get(0);
            }
            fireDataChange();
        }
    }

    private void evaluateEdit() {
        canEdit = false;
        if (manager != null)
            canEdit = !Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample()
                                                                            .getStatusId());
    }

    private void drawExternalNote() {
        NoteViewDO n;

        externalNotePanel.clearNotes();
        if (manager != null) {
            n = manager.sampleExternalNote.get();
            if (n != null)
                externalNotePanel.addNote(n.getSubject(),
                                          n.getSystemUser(),
                                          n.getText(),
                                          n.getTimestamp());
        }
    }

    private void drawInternalNotes() {
        NoteViewDO n;

        internalNotePanel.clearNotes();
        if (manager != null) {
            for (int i = 0; i < manager.sampleInternalNote.count(); i++ ) {
                n = manager.sampleInternalNote.get(i);
                internalNotePanel.addNote(n.getSubject(),
                                          n.getSystemUser(),
                                          n.getText(),
                                          n.getTimestamp());
            }
        }
    }

    private void showNoteLookup(final boolean isExternal) {
        String subject, text;
        NoteViewDO note;
        ModalWindow modal;

        if (editNoteLookup == null) {
            editNoteLookup = new EditNoteLookupUI() {
                public void ok() {
                    /*
                     * isExternal is not used for this check because its value
                     * doesn't change in this inner class after the lookup is
                     * created, even though subsequent calls to showNoteLookup
                     * may have different values passed to it
                     */
                    if (editNoteLookup.getHasSubject()) {
                        if (DataBaseUtil.isEmpty(editNoteLookup.getText())) {
                            manager.sampleInternalNote.removeEditing();
                        } else {
                            displayedIntNote = manager.sampleInternalNote.getEditing();
                            setNoteFields(displayedIntNote,
                                          editNoteLookup.getSubject(),
                                          editNoteLookup.getText());
                        }
                        drawInternalNotes();
                    } else {
                        if (DataBaseUtil.isEmpty(editNoteLookup.getText())) {
                            manager.sampleExternalNote.removeEditing();
                        } else {
                            displayedExtNote = manager.sampleExternalNote.getEditing();
                            setNoteFields(displayedExtNote, null, editNoteLookup.getText());
                        }
                        drawExternalNote();
                    }
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
         * If the sample has an internal/external editing note as requested by
         * the user then set the note's subject and text in the lookup. If there
         * isn't such a note then don't create one until the user enters its
         * subject and text.
         */
        if (isExternal) {
            note = manager.sampleExternalNote.get();
            if (note != null) {
                subject = note.getSubject();
                text = note.getText();
            }
        } else {
            if (manager.sampleInternalNote.count() > 0)
                note = manager.sampleInternalNote.get(0);

            if (note != null && note.getId() < 0) {
                subject = note.getSubject();
                text = note.getText();
            }
        }

        modal = new ModalWindow();
        modal.setSize("620px", "550px");
        modal.setName(Messages.get().gen_noteEditor());
        modal.setCSS(UIResources.INSTANCE.popupWindow());
        modal.setContent(editNoteLookup);

        editNoteLookup.setWindow(modal);
        editNoteLookup.setSubject(subject);
        editNoteLookup.setText(text);
        editNoteLookup.setHasSubject( !isExternal);
    }

    private void setNoteFields(NoteViewDO note, String subject, String text) {
        note.setSubject(subject);
        note.setText(text);
        note.setSystemUser(UserCache.getPermission().getLoginName());
        note.setSystemUserId(UserCache.getPermission().getSystemUserId());
        note.setTimestamp(Datetime.getInstance(Datetime.YEAR, Datetime.SECOND));
    }
}