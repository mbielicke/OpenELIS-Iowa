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
import org.openelis.domain.AnalysisTestDO;
import org.openelis.domain.NoteDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.ScreenDef;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.deprecated.ScreenWindow;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.NotesPanel;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.NoteManager;
import org.openelis.manager.SampleManager;
import org.openelis.modules.editNote.client.EditNoteScreen;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;

public class SampleCommentsTab extends NotesTab {
    protected AnalysisTestDO analysis;
    protected AnalysisManager anMan;
    
    //analysis values
    protected String analysisNotesPanelKey;
    protected String analysisEditButtonKey;
    protected boolean analysisIsExternal;
    
    protected NoteManager anNoteManager;
    
    protected NotesPanel analysisNotesPanel;
    protected EditNoteScreen anEditNote;
    
    public SampleCommentsTab(ScreenDefInt def, String sampleNotesPanelKey, String sampleEditButtonKey, boolean sampleIsExternal,
                               String analysisNotesPanelKey, String analysisEditButtonKey, boolean analysisIsExternal) {
        
        super(def, sampleNotesPanelKey, sampleEditButtonKey, sampleIsExternal);
        
        this.analysisNotesPanelKey = analysisNotesPanelKey;
        this.analysisEditButtonKey = analysisEditButtonKey;
        this.analysisIsExternal = analysisIsExternal;
        
        initializeTab();
    }
    
    public void initializeTab() {
        analysisNotesPanel = (NotesPanel)def.getWidget(analysisNotesPanelKey);
        addScreenHandler(analysisNotesPanel, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                drawNotes();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (event.getState() == State.ADD)
                    analysisNotesPanel.clearNotes();
            }
        });
        final AppButton anStandardNote = (AppButton)def.getWidget(analysisEditButtonKey);
        addScreenHandler(anStandardNote, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                if (anEditNote == null) {
                    try {
                        anEditNote = new EditNoteScreen();
                        anEditNote.addActionHandler(new ActionHandler<EditNoteScreen.Action>() {
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
                modal.setContent(anEditNote);

                NoteDO note = null;
                
                try{
                if(isExternal)
                    note = anNoteManager.getExternalEditingNote();
                else
                    note = anNoteManager.getInternalEditingNote();
                }catch(Exception e ){
                    e.printStackTrace();
                    Window.alert("error!");
                }
                note.setSystemUser(userName);
                note.setSystemUserId(userId);
                note.setTimestamp(Datetime.getInstance(Datetime.YEAR, Datetime.SECOND));
                anEditNote.setNote(note);
                anEditNote.setScreenState(State.DEFAULT);
            }

            public void onStateChange(StateChangeEvent<State> event) {

                if (event.getState() == State.ADD || event.getState() == State.UPDATE)
                    anStandardNote.enable(true);
                else
                    anStandardNote.enable(false);
            }
        });
    }
    
    private void drawNotes() {
        analysisNotesPanel.clearNotes();
        try {
            if(anMan == null)
                anNoteManager = NoteManager.getInstance();
            else{
                int index = anMan.getIndex(analysis);
                
                if(index != -1){
                    if(analysisIsExternal)
                        anNoteManager = anMan.getExternalNoteAt(index);
                    else
                        anNoteManager = anMan.getInternalNotesAt(index);
                }
            }

        } catch (Exception e) {
            Window.alert(e.getMessage());
            return;
        }
        
        
        for (int i = 0; i < anNoteManager.count(); i++) {
            NoteDO noteRow = anNoteManager.getNoteAt(i);
            analysisNotesPanel.addNote(noteRow.getSubject(),
                               noteRow.getSystemUser(),
                               noteRow.getText(),
                               noteRow.getTimestamp());
        }
    }
    
    public void draw() {
        if (!loaded) {
            try {
                if(anMan == null)
                    anNoteManager = NoteManager.getInstance();
                else{
                    int index = anMan.getIndex(analysis);
                    
                    if(index != -1)
                        anNoteManager = anMan.getExternalNoteAt(index);
                }
                
                if (parentManager != null){
                    if(isExternal)
                        manager = ((SampleManager)parentManager).getExternalNote();
                    else    
                        manager = ((SampleManager)parentManager).getInternalNotes();
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
            analysis = new AnalysisTestDO();
            StateChangeEvent.fire(this, State.DEFAULT);   
        }else{
            analysis = data.analysisTestDO;
            
            if(state == State.ADD || state == State.UPDATE)
                StateChangeEvent.fire(this, State.UPDATE);
        }

        anMan = data.analysisManager;
        loaded = false;
    }
}
