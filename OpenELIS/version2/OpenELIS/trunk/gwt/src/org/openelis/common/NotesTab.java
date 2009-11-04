package org.openelis.common;

import org.openelis.domain.NoteViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.NotesPanel;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.manager.HasNotesInt;
import org.openelis.manager.NoteManager;
import org.openelis.modules.editNote.client.EditNoteScreen;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;

public class NotesTab extends Screen {

    protected HasNotesInt    parentManager;
    protected NoteManager    manager;
    protected NoteViewDO     note;
    protected NotesPanel     notesPanel;
    protected EditNoteScreen editNote;
    protected String         userName;
    protected Integer        userId;
    protected String         notesPanelKey, editButtonKey;
    protected boolean        loaded, isExternal;

    public NotesTab(ScreenDefInt def, String notesPanelKey, String editButtonKey, boolean isExternal) {
        setDef(def);

        userName = OpenELIS.security.getSystemUserName();
        userId = OpenELIS.security.getSystemUserId();

        this.notesPanelKey = notesPanelKey;
        this.editButtonKey = editButtonKey;

        this.isExternal = isExternal;

        initialize();
    }

    public void initialize() {
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
        final AppButton standardNote = (AppButton)def.getWidget(editButtonKey);
        addScreenHandler(standardNote, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                if (editNote == null) {
                    try {
                        editNote = new EditNoteScreen();
                        editNote.addActionHandler(new ActionHandler<EditNoteScreen.Action>() {
                            public void onAction(ActionEvent<EditNoteScreen.Action> event) {
                                if (event.getAction() == EditNoteScreen.Action.COMMIT) {
                                    if (note.getText() == null || note.getText().trim().length() == 0)
                                        manager.removeEditingNote();

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

                ScreenWindow modal = new ScreenWindow("Edit Note Screen", "editNoteScreen", "",
                                                      true, false);
                modal.setName(consts.get("standardNote"));
                modal.setContent(editNote);

                note = null;
                try {
                    if (isExternal)
                        note = manager.getExternalEditingNote();
                    else
                        note = manager.getInternalEditingNote();
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert("error!");
                }
                
                note.setSystemUser(userName);
                note.setSystemUserId(userId);
                note.setTimestamp(Datetime.getInstance(Datetime.YEAR, Datetime.SECOND));
                editNote.setNote(note);
                editNote.setScreenState(State.UPDATE);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (event.getState() == State.ADD || event.getState() == State.UPDATE)
                    standardNote.enable(true);
                else
                    standardNote.enable(false);
            }
        });
    }

    private void drawNotes() {
        notesPanel.clearNotes();
        for (int i = 0; i < manager.count(); i++ ) {
            NoteViewDO noteRow = manager.getNoteAt(i);
            notesPanel.addNote(noteRow.getSubject(), noteRow.getSystemUser(), noteRow.getText(),
                               noteRow.getTimestamp());
        }
    }

    public void setManager(HasNotesInt parentManager) {
        this.parentManager = parentManager;
        loaded = false;
    }

    public void draw() {
        if (parentManager != null && !loaded) {
            try {
                manager = parentManager.getNotes();
                DataChangeEvent.fire(this);
                loaded = true;
            } catch (Exception e) {
                Window.alert(e.getMessage());
            }
        }
    }
}
