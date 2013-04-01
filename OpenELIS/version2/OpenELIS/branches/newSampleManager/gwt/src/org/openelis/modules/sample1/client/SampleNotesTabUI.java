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

import org.openelis.ui.screen.Screen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class SampleNotesTabUI extends Screen { //extends NotesTab
    
    @UiTemplate("SampleNotesTab.ui.xml")
    interface SampleNotesTabUIBinder extends UiBinder<Widget, SampleNotesTabUI> {        
    };
    
    private static SampleNotesTabUIBinder uiBinder = GWT.create(SampleNotesTabUIBinder.class);
    
    public SampleNotesTabUI() {
        initWidget(uiBinder.createAndBindUi(this));
        
        initialize();
    }
    /*protected String         internalNotesPanelKey;
    protected String         internalEditButtonKey;

    protected SampleManager  sampleManager;
    protected NoteManager    internalManager;

    protected NotesPanel     internalNotesPanel;
    
    protected AppButton      standardNote, internalEditButton;
    protected EditNoteScreen internalEditNote;

    protected NoteViewDO     internalNote;

    private Integer          sampleReleasedId;

    public SampleNotesTabUI(ScreenDefInt def, WindowInt window, String externalNotesPanelKey,
                            String externalEditButtonKey, String internalNotesPanelKey,
                            String internalEditButtonKey) {

          super(def, window, externalNotesPanelKey, externalEditButtonKey);

          this.internalNotesPanelKey = internalNotesPanelKey;
          this.internalEditButtonKey = internalEditButtonKey;

          initialize();
      }*/

      // overrides the notetab initialize to control the external notes button
      public void initialize() {
         /* // we dont have all the info set yet, dont run through this method
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
                  boolean enable;
                  
                  try {
                      enable = canEdit() && EnumSet.of(State.ADD, State.UPDATE).contains(event.getState());
                  } catch (Exception anyE) {
                      enable = false;
                  }
                  standardNote.enable(enable);
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
                  modal.setName(Messages.get().standardNote());
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

                  if (event.getState() == State.ADD || event.getState() == State.UPDATE)
                      internalEditButton.enable(true);
                  else
                      internalEditButton.enable(false);
              }
          });*/
      }

      /*
      protected void drawInternalNotes() {
          internalNotesPanel.clearNotes();
          for (int i = 0; i < internalManager.count(); i++ ) {
              NoteViewDO noteRow = internalManager.getNoteAt(i);
              internalNotesPanel.addNote(noteRow.getSubject(), noteRow.getSystemUser(),
                                         noteRow.getText(), noteRow.getTimestamp());
          }
      }

      public void setManager(SampleManager sampleManager) {
          this.sampleManager = sampleManager;
          loaded = false;
      }

      public void draw() {
          if ( !loaded) {
              try {
                  if (sampleManager == null ) {
                      internalManager = NoteManager.getInstance();
                      internalManager.setIsExternal(false);
                      manager = NoteManager.getInstance();
                      manager.setIsExternal(true);
                  } else {
                      manager = sampleManager.getExternalNote();
                      internalManager = sampleManager.getInternalNotes();
                  }   

                  DataChangeEvent.fire(this);
                  loaded = true;

              } catch (Exception e) {
                  Window.alert(e.getMessage());
              }
          }
      }

      private boolean canEdit() {
          return (sampleManager != null && !Constants.dictionary().SAMPLE_RELEASED.equals(sampleManager.getSample().getStatusId()));
      }*/
  }