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
package org.openelis.modules.sample.client;

import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.NotesPanel;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.HasNotesInt;
import org.openelis.manager.NoteManager;
import org.openelis.manager.SampleDataBundle;
import org.openelis.modules.note.client.EditNoteScreen;
import org.openelis.modules.note.client.NotesTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;

public class AnalysisNotesTab extends NotesTab {

    protected AnalysisViewDO  analysis;
    protected AnalysisManager anMan;
    protected SampleDataBundle bundle;

    protected String          internalNotesPanelKey;
    protected String          internalEditButtonKey;

    protected NoteManager     internalManager;

    protected NotesPanel      internalNotesPanel;
    protected AppButton       standardNote, internalEditButton;
    protected EditNoteScreen  internalEditNote;
    protected NoteViewDO      internalNote;

    private Integer           analysisCancelledId, analysisReleasedId;

    public AnalysisNotesTab(ScreenDefInt def, ScreenWindow window, String notesPanelKey,
                            String editButtonKey) {
        super(def, window, notesPanelKey, editButtonKey);
    }

    public AnalysisNotesTab(ScreenDefInt def, ScreenWindow window, String externalNotesPanelKey,
                            String externalEditButtonKey, String internalNotesPanelKey,
                            String internalEditButtonKey) {

        super(def, window, externalNotesPanelKey, externalEditButtonKey);

        this.internalNotesPanelKey = internalNotesPanelKey;
        this.internalEditButtonKey = internalEditButtonKey;
        
        initialize();

        initializeDropdowns();
    }

    // overrides the notetab initialize to control the external notes button
    public void initialize() {
        // we dont have all the info set yet, dont run through this method
        if (internalNotesPanelKey == null)
            return;

        notesPanel = (NotesPanel)def.getWidget(notesPanelKey);
        addScreenHandler(notesPanel, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                drawNotes();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (event.getState() == State.ADD)
                    notesPanel.clearNotes();
            }
        });

        standardNote = (AppButton)def.getWidget(editButtonKey);
        addScreenHandler(standardNote, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                showEditWindow();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                standardNote.enable( !isReleased() && !isCancelled() &&
                                    EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        internalNotesPanel = (NotesPanel)def.getWidget(internalNotesPanelKey);
        addScreenHandler(internalNotesPanel, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                drawInternalNotes();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (event.getState() == State.ADD)
                    internalNotesPanel.clearNotes();
            }
        });

        internalEditButton = (AppButton)def.getWidget(internalEditButtonKey);
        addScreenHandler(internalEditButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                if (internalEditNote == null) {
                    try {
                        internalEditNote = new EditNoteScreen();
                        internalEditNote.addActionHandler(new ActionHandler<EditNoteScreen.Action>() {
                            public void onAction(ActionEvent<EditNoteScreen.Action> event) {
                                if (event.getAction() == EditNoteScreen.Action.OK) {
                                    if (internalNote.getText() == null ||
                                        internalNote.getText().trim().length() == 0)
                                        internalManager.removeEditingNote();

                                    loaded = false;
                                    draw();
                                }
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        Window.alert("error: " + e.getMessage());
                        return;
                    }
                }

                ScreenWindow modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
                modal.setName(consts.get("standardNote"));
                modal.setContent(internalEditNote);

                internalNote = null;
                try {
                    internalNote = internalManager.getEditingNote();
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert("error!");
                }
                internalNote.setSystemUser(userName);
                internalNote.setSystemUserId(userId);
                internalNote.setTimestamp(Datetime.getInstance(Datetime.YEAR, Datetime.SECOND));
                internalEditNote.setNote(internalNote);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                internalEditButton.enable( !isCancelled() &&
                                          EnumSet.of(State.ADD, State.UPDATE)
                                                 .contains(event.getState()));
            }
        });
    }

    protected void drawInternalNotes() {
        internalNotesPanel.clearNotes();
        for (int i = 0; i < internalManager.count(); i++ ) {
            NoteViewDO noteRow = internalManager.getNoteAt(i);
            internalNotesPanel.addNote(noteRow.getSubject(), noteRow.getSystemUser(),
                                       noteRow.getText(), noteRow.getTimestamp());
        }
    }

    public void draw() {
        if ( !loaded) {
            try {
                if (anMan == null || bundle == null) {
                    internalManager = NoteManager.getInstance();
                    internalManager.setIsExternal(false);
                    manager = NoteManager.getInstance();
                    manager.setIsExternal(true);
                } else {
                    internalManager = anMan.getInternalNotesAt(bundle.getAnalysisIndex());
                    manager = anMan.getExternalNoteAt(bundle.getAnalysisIndex());
                }

                DataChangeEvent.fire(this);
                loaded = true;
            } catch (Exception e) {
                Window.alert(e.getMessage());
            }
        }
    }

    private boolean isCancelled() {
        return (analysis != null && analysisCancelledId.equals(analysis.getStatusId()));
    }

    private boolean isReleased() {
        return (analysis != null && analysisReleasedId.equals(analysis.getStatusId()));
    }

    private void initializeDropdowns() {
        try {
            analysisCancelledId = DictionaryCache.getIdFromSystemName("analysis_cancelled");
            analysisReleasedId = DictionaryCache.getIdFromSystemName("analysis_released");

        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
    }

    public void setData(SampleDataBundle data) {
        if (data == null || data.getSampleManager() == null || 
                            SampleDataBundle.Type.SAMPLE_ITEM.equals(data.getType())) {
            analysis = new AnalysisViewDO();
            anMan = null;
            StateChangeEvent.fire(this, State.DEFAULT);
        } else {
            try{
                anMan = data.getSampleManager().getSampleItems().getAnalysisAt(data.getSampleItemIndex());
                analysis = anMan.getAnalysisAt(data.getAnalysisIndex());
                this.bundle = data;
    
                if (state == State.ADD || state == State.UPDATE)
                    StateChangeEvent.fire(this, State.UPDATE);
            
            }catch(Exception e){
                Window.alert("AnalysisNotesTab.setData: "+e.getMessage());
            }
        }

        loaded = false;
    }

    public void setManager(HasNotesInt parentManager) {
        throw new UnsupportedOperationException();
    }
}
