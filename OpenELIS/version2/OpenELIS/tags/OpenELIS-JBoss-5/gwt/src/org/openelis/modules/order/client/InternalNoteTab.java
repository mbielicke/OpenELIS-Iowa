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
package org.openelis.modules.order.client;

import java.util.EnumSet;

import org.openelis.domain.NoteViewDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.Screen.State;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.NotesPanel;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.ScreenWindowInt;
import org.openelis.manager.NoteManager;
import org.openelis.manager.OrderManager;
import org.openelis.modules.note.client.EditNoteScreen;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;

public class InternalNoteTab extends Screen {
    protected OrderManager   orderManager;
    protected NoteManager    manager;

    protected NotesPanel     notePanel;
    protected AppButton      addNoteButton;
    protected EditNoteScreen internalEditNoteScreen;
    private boolean          loaded;
    private String           userName;
    private Integer          userId;

    protected NoteViewDO     internalNote;

    public InternalNoteTab(ScreenDefInt def, ScreenWindowInt window, String userName,
                            Integer userId) {
        setDefinition(def);
        setWindow(window);
        
        this.userName = userName;
        this.userId = userId;
        initialize();
    }

    public void initialize() {
        notePanel = (NotesPanel)def.getWidget("internalNotesPanel");
        addScreenHandler(notePanel, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                drawNotes();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (event.getState() == State.ADD)
                    notePanel.clearNotes();
            }
        });

        addNoteButton = (AppButton)def.getWidget("addNoteButton");
        addScreenHandler(addNoteButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ScreenWindow modal;
                
                if (internalEditNoteScreen == null) {
                    try {
                        internalEditNoteScreen = new EditNoteScreen();
                        internalEditNoteScreen.addActionHandler(new ActionHandler<EditNoteScreen.Action>() {
                            public void onAction(ActionEvent<EditNoteScreen.Action> event) {
                                if (event.getAction() == EditNoteScreen.Action.OK) {
                                    if (DataBaseUtil.isEmpty(internalNote.getText()))
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

                modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
                modal.setName(consts.get("standardNote"));
                modal.setContent(internalEditNoteScreen);

                try {
                    internalNote = manager.getEditingNote();
                    internalNote.setSystemUser(userName);
                    internalNote.setSystemUserId(userId);
                    internalNote.setTimestamp(Datetime.getInstance(Datetime.YEAR, Datetime.SECOND));
                    internalEditNoteScreen.setNote(internalNote);
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert("error!");
                }                
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addNoteButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });
    }

    protected void drawNotes() {
        NoteViewDO data;
        
        notePanel.clearNotes();
        for (int i = 0; i < manager.count(); i++ ) {
            data = manager.getNoteAt(i);
            notePanel.addNote(data.getSubject(), data.getSystemUser(),
                                       data.getText(), data.getTimestamp());
        }
    }

    public void setManager(OrderManager orderManager) {
        this.orderManager = orderManager;
        loaded = false;
    }

    public void draw() {
        if ( !loaded) {
            try {
                if (orderManager == null ) {
                    manager = NoteManager.getInstance();
                    manager.setIsExternal(false);
                } else {
                    manager = orderManager.getInternalNotes();
                }   

                DataChangeEvent.fire(this);
                loaded = true;

            } catch (Exception e) {
                Window.alert(e.getMessage());
            }
        }
    }
}