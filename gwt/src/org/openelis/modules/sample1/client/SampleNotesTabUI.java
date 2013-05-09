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

import static org.openelis.ui.screen.State.*;

import org.openelis.domain.Constants;
import org.openelis.domain.NoteViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.NotesPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class SampleNotesTabUI extends Screen { // extends NotesTab

    @UiTemplate("SampleNotesTab.ui.xml")
    interface SampleNotesTabUIBinder extends UiBinder<Widget, SampleNotesTabUI> {
    };

    private static SampleNotesTabUIBinder uiBinder = GWT.create(SampleNotesTabUIBinder.class);

    @UiField
    protected NotesPanel                  sampleExtNotesPanel, sampleIntNotesPanel;

    @UiField
    protected Button                      sampleExtNoteButton, sampleIntNoteButton;
    // protected EditNoteScreen internalEditNote;

    // protected NoteViewDO internalNote;

    protected Screen                      parentScreen;

    protected SampleManager1              manager, displayedManager;

    protected boolean                     canEdit, canQuery, isVisible;

    public SampleNotesTabUI(Screen parentScreen, EventBus bus) {
        this.parentScreen = parentScreen;
        setEventBus(bus);
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
        displayedManager = null;
    }

    public void initialize() {
        addScreenHandler(sampleExtNotesPanel, "sampleExtNotesPanel", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                drawExternalNote();
            }
        });

        addScreenHandler(sampleExtNoteButton, "sampleExtNoteButton", new ScreenHandler<Object>() {
            /*
             * public void onClick(ClickEvent event) { showEditWindow(); }
             */

            public void onStateChange(StateChangeEvent event) {
                sampleExtNoteButton.setEnabled(canEdit);
            }
        });

        addScreenHandler(sampleIntNotesPanel, "sampleIntNotesPanel", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                drawInternalNotes();
            }
        });

        addScreenHandler(sampleIntNoteButton, "sampleIntNoteButton", new ScreenHandler<Object>() {
            /*
             * public void onClick(ClickEvent event) { if (internalEditNote ==
             * null) { try { internalEditNote = new EditNoteScreen();
             * internalEditNote.addActionHandler(new
             * ActionHandler<EditNoteScreen.Action>() { public void
             * onAction(ActionEvent<EditNoteScreen.Action> event) { if
             * (event.getAction() == EditNoteScreen.Action.OK) { if
             * (internalNote.getText() == null ||
             * internalNote.getText().trim().length() == 0)
             * internalManager.removeEditingNote();
             * 
             * loaded = false; draw(); } } });
             * 
             * } catch (Exception e) { e.printStackTrace();
             * Window.alert("error: " + e.getMessage()); return; } }
             * 
             * ScreenWindow modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
             * modal.setName(Messages.get().standardNote());
             * modal.setContent(internalEditNote);
             * 
             * internalNote = null; try { internalNote =
             * internalManager.getEditingNote(); } catch (Exception e) {
             * e.printStackTrace(); Window.alert("error!"); }
             * internalNote.setSystemUser(userName);
             * internalNote.setSystemUserId(userId);
             * internalNote.setTimestamp(Datetime.getInstance(Datetime.YEAR,
             * Datetime.SECOND)); internalEditNote.setNote(internalNote); }
             */

            public void onStateChange(StateChangeEvent event) {
                sampleIntNoteButton.setEnabled(canEdit);
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
                                       evaluateEdit();
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

    public void setData(SampleManager1 manager) {
        if ( !DataBaseUtil.isSame(this.manager, manager)) {
            displayedManager = this.manager;
            this.manager = manager;
            evaluateEdit();
        }
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    private void evaluateEdit() {
        canEdit = false;
        if (manager != null && isState(ADD, UPDATE))
            canEdit = !Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample()
                                                                            .getStatusId());
    }

    private void displayNotes() {
        int count1, count2;
        Integer id1, id2;
        boolean dataChanged;

        if ( !isVisible)
            return;

        /*
         * compare external notes
         */
        id1 = null;
        id2 = null;
        if (displayedManager != null && displayedManager.sampleExternalNote.get() != null)
            id1 = displayedManager.sampleExternalNote.get().getId();

        if (manager != null && manager.sampleExternalNote.get() != null)
            id2 = manager.sampleExternalNote.get().getId();

        dataChanged = DataBaseUtil.isDifferent(id1, id2);

        if ( !dataChanged) {
            /*
             * compare internal notes
             */
            count1 = displayedManager == null ? 0 : displayedManager.sampleInternalNote.count();
            count2 = manager == null ? 0 : manager.sampleInternalNote.count();

            if (count1 == count2) {
                if (count1 > 0)
                    dataChanged = DataBaseUtil.isDifferent(displayedManager.sampleInternalNote.get(0)
                                                                                              .getId(),
                                                           manager.sampleInternalNote.get(0)
                                                                                     .getId());
            } else {
                dataChanged = true;
            }
        }

        if (dataChanged) {
            displayedManager = manager;
            evaluateEdit();
            setState(state);
            fireDataChange();
        }
    }

    private void drawExternalNote() {
        NoteViewDO n;

        sampleExtNotesPanel.clearNotes();
        if (manager != null) {
            n = manager.sampleExternalNote.get();
            if (n != null)
                sampleExtNotesPanel.addNote(n.getSubject(),
                                            n.getSystemUser(),
                                            n.getText(),
                                            n.getTimestamp());
        }
    }

    private void drawInternalNotes() {
        NoteViewDO n;

        sampleIntNotesPanel.clearNotes();
        if (manager != null) {
            for (int i = 0; i < manager.sampleInternalNote.count(); i++ ) {
                n = manager.sampleInternalNote.get(i);
                sampleIntNotesPanel.addNote(n.getSubject(),
                                            n.getSystemUser(),
                                            n.getText(),
                                            n.getTimestamp());
            }
        }
    }
}