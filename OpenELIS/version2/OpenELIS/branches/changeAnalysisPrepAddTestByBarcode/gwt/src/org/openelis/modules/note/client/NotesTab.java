package org.openelis.modules.note.client;

import java.util.EnumSet;

import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.NoteViewDO;
import org.openelis.ui.common.Datetime;
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
import org.openelis.gwt.widget.ScreenWindowInt;
import org.openelis.manager.HasNotesInt;
import org.openelis.manager.NoteManager;
import org.openelis.ui.widget.WindowInt;

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
    protected boolean        loaded;

    public NotesTab(ScreenDefInt def, WindowInt window, String notesPanelKey,
                    String editButtonKey) {
        setDefinition(def);
        setWindow(window);

        userName = UserCache.getPermission().getLoginName();
        userId = UserCache.getPermission().getSystemUserId();
        this.notesPanelKey = notesPanelKey;
        this.editButtonKey = editButtonKey;

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
                showEditWindow();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                standardNote.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });
    }

    protected void showEditWindow() {
        ScreenWindow modal;
        
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

        modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
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
                DataChangeEvent.fire(this);
                loaded = true;
            } catch (Exception e) {
                Window.alert(e.getMessage());
            }
        }
    }
}
