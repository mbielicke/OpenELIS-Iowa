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
package org.openelis.modules.environmentalSampleLogin.client;

import org.openelis.common.NotesTab;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.deprecated.ScreenWindow;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.NotesPanel;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.HasNotesInt;
import org.openelis.manager.NoteManager;
import org.openelis.modules.editNote.client.EditNoteScreen;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;

public class AnalysisNotesTab extends NotesTab {

    protected AnalysisViewDO analysis;
    protected AnalysisManager anMan;
    
    protected String internalNotesPanelKey;
    protected String internalEditButtonKey;
    
    protected NoteManager internalManager;
    
    protected NotesPanel internalNotesPanel;
    protected EditNoteScreen internalEditNote;
    
    public AnalysisNotesTab(ScreenDefInt def, String notesPanelKey, String editButtonKey, boolean isExternal) {
        super(def, notesPanelKey, editButtonKey, isExternal);
    }
    
    public AnalysisNotesTab(ScreenDefInt def, String externalNotesPanelKey, String externalEditButtonKey,
                          String internalNotesPanelKey, String internalEditButtonKey) {
   
       super(def, externalNotesPanelKey, externalEditButtonKey, true);
       
       this.internalNotesPanelKey = internalNotesPanelKey;
       this.internalEditButtonKey = internalEditButtonKey;
       
       initializeTab();
    }
    
    public void initializeTab() {
        internalNotesPanel = (NotesPanel)def.getWidget(internalNotesPanelKey);
        addScreenHandler(internalNotesPanel, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                drawNotes();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (event.getState() == State.ADD)
                    internalNotesPanel.clearNotes();
            }
        });
        final AppButton internalEditButton = (AppButton)def.getWidget(internalEditButtonKey);
        addScreenHandler(internalEditButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                if (internalEditNote == null) {
                    try {
                        internalEditNote = new EditNoteScreen();
                        internalEditNote.addActionHandler(new ActionHandler<EditNoteScreen.Action>() {
                            public void onAction(ActionEvent<EditNoteScreen.Action> event) {
                                if (event.getAction() == EditNoteScreen.Action.COMMIT) {
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

                ScreenWindow modal = new ScreenWindow(null,
                                                      "Edit Note Screen",
                                                      "editNoteScreen",
                                                      "",
                                                      true,
                                                      false);
                modal.setName(consts.get("standardNote"));
                modal.setContent(internalEditNote);

                NoteViewDO note = null;
                
                try{
                    note = internalManager.getInternalEditingNote();
                }catch(Exception e ){
                    e.printStackTrace();
                    Window.alert("error!");
                }
                note.setSystemUser(userName);
                note.setSystemUserId(userId);
                note.setTimestamp(Datetime.getInstance(Datetime.YEAR, Datetime.SECOND));
                internalEditNote.setNote(note);
                internalEditNote.setScreenState(State.DEFAULT);
            }

            public void onStateChange(StateChangeEvent<State> event) {

                if (event.getState() == State.ADD || event.getState() == State.UPDATE)
                    internalEditButton.enable(true);
                else
                    internalEditButton.enable(false);
            }
        });
    }
    
    private void drawNotes() {
        internalNotesPanel.clearNotes();
        for (int i = 0; i < internalManager.count(); i++) {
            NoteViewDO noteRow = internalManager.getNoteAt(i);
            internalNotesPanel.addNote(noteRow.getSubject(),
                               noteRow.getSystemUser(),
                               noteRow.getText(),
                               noteRow.getTimestamp());
        }
    }
    
    public void draw() {
        if (!loaded) {
            try {
                if(anMan == null){
                    internalManager = NoteManager.getInstance();
                    manager = NoteManager.getInstance();
                }else{
                    int index = anMan.getIndex(analysis);
                    
                    if(index != -1){
                        internalManager = anMan.getInternalNotesAt(index);
                        manager = anMan.getExternalNoteAt(index);
                    }
                }
                
                DataChangeEvent.fire(this);
                loaded = true;
            } catch (Exception e) {
                Window.alert(e.getMessage());
            }
        }
    }
    
    public void setData(SampleDataBundle data) {
        if(data.analysisTestDO == null){
            analysis = new AnalysisViewDO();
            StateChangeEvent.fire(this, State.DEFAULT);   
        }else{
            analysis = data.analysisTestDO;
            
            if(state == State.ADD || state == State.UPDATE)
                StateChangeEvent.fire(this, State.UPDATE);
        }

        anMan = data.analysisManager;
        loaded = false;
    }
    
    public void setManager(HasNotesInt parentManager) {
        throw new UnsupportedOperationException();
    }
}
