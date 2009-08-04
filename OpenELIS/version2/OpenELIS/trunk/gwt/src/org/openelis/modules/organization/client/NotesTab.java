package org.openelis.modules.organization.client;

import org.openelis.domain.NoteDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.AppScreen;
import org.openelis.gwt.screen.ScreenWindow;
import org.openelis.gwt.screen.rewrite.Screen;
import org.openelis.gwt.screen.rewrite.ScreenDef;
import org.openelis.gwt.screen.rewrite.ScreenEventHandler;
import org.openelis.gwt.widget.NotesPanel;
import org.openelis.gwt.widget.rewrite.AppButton;
import org.openelis.manager.HasNotesInt;
import org.openelis.manager.NotesManager;
import org.openelis.modules.editNote.client.EditNoteScreen;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;

public class NotesTab extends Screen {

    private HasNotesInt      parentManager;
    private NotesManager     manager;
    private NotesPanel       notesPanel;
    protected EditNoteScreen editNote;
    private String           userName;
    private Integer          userId;
    private boolean          loaded = false;

    public NotesTab(ScreenDef def) {
        setDef(def);
        userName = OpenELIS.security.getSystemUserName();
        userId = OpenELIS.security.getSystemUserId();

        initialize();
    }

    public void initialize() {
        notesPanel = (NotesPanel)def.getWidget("notesPanel");
        addScreenHandler(notesPanel, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                drawNotes();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (event.getState() == State.ADD)
                    notesPanel.clearNotes();
            }
        });
        final AppButton standardNote = (AppButton)def.getWidget("standardNoteButton");
        addScreenHandler(standardNote, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                if (editNote == null) {
                    try {
                        editNote = new EditNoteScreen();
                        editNote.addActionHandler(new ActionHandler<EditNoteScreen.Action>() {
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
                modal.setName(AppScreen.consts.get("standardNote"));
                modal.setContent(editNote);

                NoteDO note = manager.getInternalEditingNote();
                note.setSystemUser(userName);
                note.setSystemUserId(userId);
                note.setTimestamp(Datetime.getInstance(Datetime.YEAR, Datetime.SECOND));
                editNote.setNote(manager.getInternalEditingNote());
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
        for (int i = 0; i < manager.count(); i++) {
            NoteDO noteRow = manager.getNoteAt(i);
            notesPanel.addNote(noteRow.getSubject(),
                               noteRow.getSystemUser(),
                               noteRow.getText(),
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
