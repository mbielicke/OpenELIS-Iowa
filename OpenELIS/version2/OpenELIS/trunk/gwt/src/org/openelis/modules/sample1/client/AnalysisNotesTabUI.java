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

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.State.*;

import java.util.logging.Level;

import org.openelis.cache.SectionCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.manager.SampleManager1;
import org.openelis.modules.note.client.EditNoteLookupUI;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.SectionPermission;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.ModalWindow;
import org.openelis.ui.widget.NotesPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class AnalysisNotesTabUI extends Screen {

    @UiTemplate("AnalysisNotesTab.ui.xml")
    interface AnalysisNotesTabUIBinder extends UiBinder<Widget, AnalysisNotesTabUI> {
    };

    private static AnalysisNotesTabUIBinder uiBinder = GWT.create(AnalysisNotesTabUIBinder.class);

    @UiField
    protected NotesPanel                    externalNotePanel, internalNotePanel;

    @UiField
    protected Button                        editNoteButton, addNoteButton;

    protected Screen                        parentScreen;
    
    protected EventBus                      parentBus; 

    protected EditNoteLookupUI              editNoteLookup;

    protected SampleManager1                manager, displayedManager;

    protected AnalysisViewDO                analysis;
    
    protected boolean                      canEdit, isVisible, redraw;

    protected String                        displayedUid;

    public AnalysisNotesTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
        displayedManager = null;
        displayedUid = null;
    }

    private void initialize() {
        addScreenHandler(externalNotePanel, "analysisExtNotePanel", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                drawExternalNote();
            }
        });

        addScreenHandler(editNoteButton, "editNoteButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                editNoteButton.setEnabled(isState(ADD, UPDATE) &&
                                          canEdit &&
                                          !Constants.dictionary().ANALYSIS_RELEASED.equals(getStatusId()));
            }
        });

        addScreenHandler(internalNotePanel, "analysisIntNotePanel", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                drawInternalNotes();
            }
        });

        addScreenHandler(addNoteButton, "addNoteButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                addNoteButton.setEnabled(isState(ADD, UPDATE) && canEdit);
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                String uid;

                isVisible = event.isVisible();
                if (analysis != null)
                    uid = manager.getUid(analysis);
                else
                    uid = null;

                displayNotes(uid);
            }
        });

        /*
         * handlers for the events fired by the screen containing this tab
         */
        parentBus.addHandlerToSource(StateChangeEvent.getType(),
                               parentScreen,
                               new StateChangeEvent.Handler() {
                                   public void onStateChange(StateChangeEvent event) {
                                       evaluateEdit();
                                       setState(event.getState());
                                   }
                               });

        parentBus.addHandler(SelectionEvent.getType(), new SelectionEvent.Handler() {
            public void onSelection(SelectionEvent event) {
                int count1, count2;
                Integer id1, id2;
                String uid;

                switch (event.getSelectedType()) {
                    case ANALYSIS:
                        uid = event.getUid();
                        break;
                    default:
                        uid = null;
                        break;
                }

                if (DataBaseUtil.isDifferent(displayedUid, uid)) {
                    displayedUid = uid;
                    redraw = true;
                } else if (analysis != null) {
                    /*
                     * compare external notes
                     */
                    id1 = null;
                    id2 = null;
                    if (displayedManager != null &&
                        displayedManager.analysisExternalNote.get(analysis) != null)
                        id1 = displayedManager.analysisExternalNote.get(analysis).getId();

                    if (manager != null && manager.analysisExternalNote.get(analysis) != null)
                        id2 = manager.analysisExternalNote.get(analysis).getId();

                    redraw = DataBaseUtil.isDifferent(id1, id2);

                    if ( !redraw) {
                        /*
                         * compare internal notes
                         */
                        count1 = displayedManager == null ? 0
                                                         : displayedManager.analysisInternalNote.count(analysis);
                        count2 = manager == null ? 0 : manager.analysisInternalNote.count(analysis);

                        if (count1 == count2) {
                            if (count1 > 0)
                                redraw = DataBaseUtil.isDifferent(displayedManager.analysisInternalNote.get(analysis,
                                                                                                                 0)
                                                                                                            .getId(),
                                                                       manager.analysisInternalNote.get(analysis,
                                                                                                        0)
                                                                                                   .getId());
                        } else {
                            redraw = true;
                        }
                    }
                }
                
                displayNotes(uid);
            }
        });
        
        parentBus.addHandler(AnalysisChangeEvent.getType(), new AnalysisChangeEvent.Handler() {
            @Override
            public void onAnalysisChange(AnalysisChangeEvent event) {
                if (AnalysisChangeEvent.Action.STATUS_CHANGED.equals(event.getAction()) ||
                    AnalysisChangeEvent.Action.SECTION_CHANGED.equals(event.getAction())) {
                    /*
                     * reevaluate the permissions for this section or status to
                     * enable or disable the widgets in the tab
                     */
                    evaluateEdit();
                    setState(state);
                }
            }
        });
    }

    public void setData(SampleManager1 manager) {
        if (DataBaseUtil.isDifferent(this.manager, manager)) {
            displayedManager = this.manager;
            this.manager = manager;        
        }
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    @UiHandler("editNoteButton")
    protected void editNote(ClickEvent event) {
        showNoteLookup(true);
    }

    @UiHandler("addNoteButton")
    protected void addNote(ClickEvent event) {
        showNoteLookup(false);
    }

    private void displayNotes(String uid) {
        if (uid != null)
            analysis = (AnalysisViewDO)manager.getObject(uid);
        else
            analysis = null;

        if ( !isVisible)
            return;

        if (redraw) {
            /*
             * don't redraw unless the data has changed
             */
            redraw = false;
            displayedManager = manager;
            evaluateEdit();
            setState(state);
            fireDataChange();
        }
    }

    private void drawExternalNote() {
        NoteViewDO n;

        externalNotePanel.clearNotes();
        if (analysis != null) {
            n = manager.analysisExternalNote.get(analysis);
            if (n != null)
                externalNotePanel.addNote(n.getSubject(),
                                             n.getSystemUser(),
                                             n.getText(),
                                             n.getTimestamp());
        }
    }

    private void drawInternalNotes() {
        NoteViewDO n;

        internalNotePanel.clearNotes();
        if (analysis != null) {
            for (int i = 0; i < manager.analysisInternalNote.count(analysis); i++ ) {
                n = manager.analysisInternalNote.get(analysis, i);
                internalNotePanel.addNote(n.getSubject(),
                                             n.getSystemUser(),
                                             n.getText(),
                                             n.getTimestamp());
            }
        }
    }

    private void evaluateEdit() {
        Integer sectId, statId;
        SectionPermission perm;
        SectionViewDO sect;

        canEdit = false;
        if (manager != null) {
            perm = null;
            sectId = getSectionId();
            statId = getStatusId();

            try {
                if (sectId != null) {
                    sect = SectionCache.getById(sectId);
                    perm = UserCache.getPermission().getSection(sect.getName());
                }
                canEdit = !Constants.dictionary().ANALYSIS_CANCELLED.equals(statId) &&
                                    perm != null &&
                                    (perm.hasAssignPermission() || perm.hasCompletePermission());
            } catch (Exception e) {
                Window.alert("canEdit:" + e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    private void showNoteLookup(boolean isExternal) {
        String subject, text;
        NoteViewDO note;
        ModalWindow modal;

        if (editNoteLookup == null) {
            editNoteLookup = new EditNoteLookupUI() {
                @Override
                public void ok() {
                    /*
                     * isExternal is not used for this check because its value
                     * doesn't change in this inner class after the object is
                     * created, even though different values for it may get passed
                     * to showNoteLookup on subsequent calls
                     */
                    if (editNoteLookup.getHasSubject()) {
                        setNoteFields(manager.analysisInternalNote.getEditing(analysis),
                                      editNoteLookup.getSubject(),
                                      editNoteLookup.getText());
                        drawInternalNotes();
                    } else {
                        setNoteFields(manager.analysisExternalNote.getEditing(analysis),
                                      null,
                                      editNoteLookup.getText());
                        drawExternalNote();
                    }
                }

                @Override
                public void cancel() {
                    // ignore
                }
            };
        }

        note = null;
        subject = null;
        text = null;
        /*
         * If the analysis has an internal/external editing note as requested by
         * the user then set the note's subject and text in the lookup. If there
         * isn't such a note then don't create one until the user enters its
         * subject and text.
         */
        if (isExternal) {
            note = manager.analysisExternalNote.get(analysis);
            if (note != null) {
                subject = note.getSubject();
                text = note.getText();
            }
        } else {
            if (manager.analysisInternalNote.count(analysis) > 0)
                note = manager.analysisInternalNote.get(analysis, 0);

            if (note != null && note.getId() == null) {
                subject = note.getSubject();
                text = note.getText();
            }
        }

        modal = new ModalWindow();
        modal.setSize("620px", "550px");
        modal.setName(Messages.get().noteEditor());
        modal.setCSS(UIResources.INSTANCE.popupWindow());
        modal.setContent(editNoteLookup);

        editNoteLookup.setWindow(modal);
        editNoteLookup.setSubject(subject);
        editNoteLookup.setText(text);
        editNoteLookup.setHasSubject( !isExternal);
    }

    private Integer getSectionId() {
        if (analysis != null)
            return analysis.getSectionId();

        return null;
    }

    private Integer getStatusId() {
        if (analysis != null)
            return analysis.getStatusId();

        return null;
    }

    private void setNoteFields(NoteViewDO note, String subject, String text) {
        note.setSubject(subject);
        note.setText(text);
        note.setSystemUser(UserCache.getPermission().getLoginName());
        note.setSystemUserId(UserCache.getPermission().getSystemUserId());
        note.setTimestamp(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE));
    }
}