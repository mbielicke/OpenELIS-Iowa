package org.openelis.modules.note.client;

import java.util.EnumSet;

import org.openelis.domain.NoteViewDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.ModalWindow;
import org.openelis.manager.HasNotesInt;
import org.openelis.manager.NoteManager;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;

public class RichTextTab extends Screen {

    protected EditRichTextScreen richtext;
    protected HasNotesInt        parentManager;
    protected NoteManager        manager;
    protected NoteViewDO         note;
    protected HTML               richTextPanel;
    protected String             richtextPanelKey, editButtonKey;
    protected Button             editButton;
    protected String             userName;
    protected Integer            userId;
    protected boolean            loaded;

    public RichTextTab(ScreenDefInt def, org.openelis.gwt.widget.Window window, String richtextPanelKey,
                       String editButtonKey) {
        setDefinition(def);
        setWindow(window);

        userName = OpenELIS.getSystemUserPermission().getLoginName();
        userId = OpenELIS.getSystemUserPermission().getSystemUserId();
        this.richtextPanelKey = richtextPanelKey;
        this.editButtonKey = editButtonKey;

        initialize();
    }

    public void initialize() {
        richTextPanel = (HTML)def.getWidget(richtextPanelKey);
        addScreenHandler(richTextPanel, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                if (manager.count() > 0)
                    richTextPanel.setHTML(manager.getNoteAt(0).getText());
                else
                    richTextPanel.setHTML("");
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (event.getState() == State.ADD)
                    richTextPanel.setHTML("");
            }
        });

        editButton = (Button)def.getWidget(editButtonKey);
        addScreenHandler(editButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                showEditWindow();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                editButton.setEnabled(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });
    }

    private void showEditWindow() {
        ModalWindow modal;
        
        if (richtext == null) {
            try {
                richtext = new EditRichTextScreen();
                richtext.addActionHandler(new ActionHandler<EditRichTextScreen.Action>() {
                    public void onAction(ActionEvent<EditRichTextScreen.Action> event) {
                        if (event.getAction() == EditRichTextScreen.Action.OK) {
                            if (note.getText() == null || note.getText().trim().length() == 0)
                                manager.removeEditingNote();
                            loaded = false;
                            draw();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Window.alert("Error in RichText: " + e.getMessage());
                return;
            }
        }

        modal = new ModalWindow();
        modal.setName(consts.get("richTextEditor"));
        modal.setContent(richtext);

        note = null;
        try {
            note = manager.getEditingNote();
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert("Error in RichText: " + e.getMessage());
        }
        note.setSystemUser(userName);
        note.setSystemUserId(userId);
        note.setTimestamp(Datetime.getInstance(Datetime.YEAR, Datetime.SECOND));
        richtext.setNote(note);
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
