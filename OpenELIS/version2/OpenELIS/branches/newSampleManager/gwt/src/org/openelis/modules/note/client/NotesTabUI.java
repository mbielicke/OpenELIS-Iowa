package org.openelis.modules.note.client;

import static org.openelis.ui.screen.State.*;


import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.NoteViewDO;
import org.openelis.ui.common.Datetime;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.event.StateChangeHandler;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.ModalWindow;
import org.openelis.ui.widget.NotesPanel;
import org.openelis.manager.HasNotesInt;
import org.openelis.manager.NoteManager;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;

public class NotesTabUI extends Screen {

    protected HasNotesInt    parentManager;
    protected NoteManager    manager;
    protected NoteViewDO     note;
    protected NotesPanel     notesPanel;
    protected EditNoteScreen editNote;
    protected String         userName;
    protected Integer        userId;
    protected Button         editButton;
    protected boolean        loaded;

    public NotesTabUI(WindowInt window, NotesPanel notesPanel, Button editButton) {
        setWindow(window);

        userName = UserCache.getPermission().getLoginName();
        userId = UserCache.getPermission().getSystemUserId();
        this.notesPanel = notesPanel;
        this.editButton = editButton;

        initialize();
    }

    public void initialize() {

        addScreenHandler(notesPanel, "notesPanel", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                drawNotes();
            }

            public void onStateChange(StateChangeEvent event) {
                if (isState(ADD))
                    notesPanel.clearNotes();
            }
        });
        
        addStateChangeHandler(new StateChangeHandler() {
            public void onStateChange(StateChangeEvent event) {
                editButton.setEnabled(isState(ADD,UPDATE));
            }
        });
        
        editButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showEditWindow();
            }
        });            

    }

    protected void showEditWindow() {
        ModalWindow modal;
        
        if (editNote == null) {
            try {
                editNote = new EditNoteScreen();
                editNote.addActionHandler(new ActionHandler<EditNoteScreen.Action>() {
                    public void onAction(ActionEvent<EditNoteScreen.Action> event) {
                        if (event.getAction() == EditNoteScreen.Action.OK) {
                            if (note.getText() == null || note.getText().trim().length() == 0)
                                manager.removeEditingNote();
                            loaded = false;
                            draw();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Window.alert("Error in EditNote:" + e.getMessage());
                return;
            }
        }

        modal = new ModalWindow();
        modal.setName(Messages.get().noteEditor());
        modal.setContent(editNote);

        note = null;
        try {
            note = manager.getEditingNote();
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert("Error in EditNote:" + e.getMessage());
        }
        note.setSystemUser(userName);
        note.setSystemUserId(userId);
        note.setTimestamp(Datetime.getInstance(Datetime.YEAR, Datetime.SECOND));
        editNote.setNote(note);
    }
    
    protected void drawNotes() {
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
                fireDataChange();
                loaded = true;
            } catch (Exception e) {
                e.printStackTrace();
                Window.alert(e.getMessage());
            }
        }
    }
}
