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
import org.openelis.manager.WorksheetManager1;
import org.openelis.modules.note.client.EditNoteLookupUI;
import org.openelis.modules.worksheetCompletion.client.WorksheetCompletionScreenUI;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.State;
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
import com.google.gwt.user.client.ui.Widget;

public class WorksheetNotesTabUI extends Screen {

    @UiTemplate("WorksheetNotesTab.ui.xml")
    interface WorksheetNotesTabUiBinder extends UiBinder<Widget, WorksheetNotesTabUI> {
    };

    private static WorksheetNotesTabUiBinder uiBinder = GWT.create(WorksheetNotesTabUiBinder.class);

    @UiField
    protected Button                         addNoteButton;
    @UiField
    protected NotesPanel                     notesPanel;
    
    protected boolean                        canEdit, isVisible, redraw;
    protected EditNoteLookupUI               editNoteLookup;
    protected EventBus                       parentBus;
    protected NoteViewDO                     displayedNote;
    protected Screen                         parentScreen;
    protected WorksheetManager1              manager;

    public WorksheetNotesTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
        displayedNote = null;
    }

    public void initialize() {
        addDataChangeHandler(new DataChangeEvent.Handler() {
            public void onDataChange(DataChangeEvent event) {
                drawNotes();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                addNoteButton.setEnabled(isState(ADD, UPDATE) && canEdit && !getUpdateTransferMode());
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displayNotes();
            }
        });
    }

    public void setData(WorksheetManager1 manager) {
        if (DataBaseUtil.isDifferent(this.manager, manager)) {
            this.manager = manager;
            if (manager != null)
                canEdit = Constants.dictionary().WORKSHEET_WORKING.equals(manager.getWorksheet()
                                                                                 .getStatusId());
            else
                canEdit = false;
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
        id1 = displayedNote != null ? displayedNote.getId() : null;
        id2 = null;
        if (manager != null && manager.note.count() > 0)
            id2 = manager.note.get(0).getId();
        if (DataBaseUtil.isDifferent(id1, id2))
            redraw = true;

        displayNotes();
    }
    
    private void displayNotes() {
        if (!isVisible)
            return;

        if (redraw) {
            /*
             * don't redraw unless the data has changed
             */
            redraw = false;
            if (manager != null && manager.note.count() > 0) {
                displayedNote = manager.note.get(0);
            } else {
                displayedNote = null;
            }
            setState(state);
            fireDataChange();
        }
    }
    
    private void drawNotes() {
        int i;
        NoteViewDO note;

        notesPanel.clearNotes();
        if (manager != null) {
            for (i = 0; i < manager.note.count(); i++) {
                note = manager.note.get(i);
                notesPanel.addNote(note.getSubject(),
                                   note.getSystemUser(),
                                   note.getText(),
                                   note.getTimestamp());
            }
        }
    }

    protected boolean getUpdateTransferMode() {
        if (parentScreen instanceof WorksheetCompletionScreenUI)
            return ((WorksheetCompletionScreenUI)parentScreen).getUpdateTransferMode();
        else
            return false;
    }
    
    @SuppressWarnings("unused")
    @UiHandler("addNoteButton")
    protected void showNoteLookup(ClickEvent event) {
        ModalWindow modal;
        NoteViewDO note;

        if (editNoteLookup == null) {
            editNoteLookup = new EditNoteLookupUI() {
                public void ok() {
                    if (DataBaseUtil.isEmpty(editNoteLookup.getText())) {
                        manager.note.removeEditing();
                    } else {
                        displayedNote = manager.note.getEditing();
                        displayedNote.setSubject(editNoteLookup.getSubject());
                        displayedNote.setText(editNoteLookup.getText());
                        displayedNote.setSystemUser(UserCache.getPermission().getLoginName());
                        displayedNote.setSystemUserId(UserCache.getPermission().getSystemUserId());
                        displayedNote.setTimestamp(Datetime.getInstance(Datetime.YEAR, Datetime.SECOND));
                    }
                    drawNotes();
                }
                
                public void cancel() {
                    // ignore
                }
            };
        }

        modal = new ModalWindow();
        modal.setSize("620px", "550px");
        modal.setName(Messages.get().gen_noteEditor());
        modal.setCSS(UIResources.INSTANCE.popupWindow());
        modal.setContent(editNoteLookup);

        note = manager.note.getEditing();
        editNoteLookup.setWindow(modal);
        editNoteLookup.setSubject(note.getSubject());
        editNoteLookup.setText(note.getText());
        editNoteLookup.setHasSubject("N".equals(note.getIsExternal()));
    }
}