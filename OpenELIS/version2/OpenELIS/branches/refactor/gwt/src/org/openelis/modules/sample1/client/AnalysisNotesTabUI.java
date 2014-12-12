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

    protected SampleManager1                manager;

    protected AnalysisViewDO                analysis;

    protected NoteViewDO                    displayedExtNote, displayedIntNote;

    protected boolean                       canEdit, isVisible, redraw;

    public AnalysisNotesTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        manager = null;
        displayedExtNote = null;
        displayedIntNote = null;
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

            public Widget onTab(boolean forward) {
                return forward ? addNoteButton : addNoteButton;
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

            public Widget onTab(boolean forward) {
                return forward ? editNoteButton : editNoteButton;
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                isVisible = event.isVisible();
                displayNotes();
            }
        });

        parentBus.addHandler(SelectionEvent.getType(), new SelectionEvent.Handler() {
            public void onSelection(SelectionEvent event) {
                Integer id1, id2;
                String uid, txt1, txt2;

                switch (event.getSelectedType()) {
                    case ANALYSIS:
                        uid = event.getUid();
                        break;
                    default:
                        uid = null;
                        break;
                }

                if (uid != null)
                    analysis = (AnalysisViewDO)manager.getObject(uid);
                else
                    analysis = null;

                /*
                 * redraw only if the displayed data is different from the data
                 * in the manager; compare external note
                 */
                id1 = null;
                txt1 = null;
                if (displayedExtNote != null) {
                    id1 = displayedExtNote.getId();
                    txt1 = displayedExtNote.getText();
                }

                id2 = null;
                txt2 = null;
                if (manager != null && analysis != null &&
                    manager.analysisExternalNote.get(analysis) != null) {
                    id2 = manager.analysisExternalNote.get(analysis).getId();
                    txt2 = manager.analysisExternalNote.get(analysis).getText();
                }

                /*
                 * since the analysis only has one external note, its id won't
                 * change but its text can
                 */
                redraw = DataBaseUtil.isDifferent(id1, id2) || DataBaseUtil.isDifferent(txt1, txt2);

                if ( !redraw) {
                    /*
                     * compare internal notes
                     */
                    id1 = displayedIntNote != null ? displayedIntNote.getId() : null;
                    id2 = null;
                    if (manager != null && analysis != null &&
                        manager.analysisInternalNote.count(analysis) > 0)
                        id2 = manager.analysisInternalNote.get(analysis, 0).getId();
                    redraw = DataBaseUtil.isDifferent(id1, id2);
                }

                setState(state);
                displayNotes();
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
                    analysis = (AnalysisViewDO)manager.getObject(event.getUid());
                    setState(state);
                }
            }
        });
        
        parentBus.addHandler(NoteChangeEvent.getType(), new NoteChangeEvent.Handler() {
            @Override
            public void onNoteChange(NoteChangeEvent event) {
                if (event.getUid() != null) {
                    redraw = true;
                    analysis = (AnalysisViewDO)manager.getObject(event.getUid());
                    displayNotes();
                }
            }
        });
    }

    public void setData(SampleManager1 manager) {
        this.manager = manager;
    }

    public void setState(State state) {
        evaluateEdit();
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    public void setFocus() {
        /*
         * set the first enabled widget in the tabbing order in focus, i.e. edit
         * note button if it's enabled
         */
        if (isState(ADD, UPDATE))
            editNoteButton.setFocus(editNoteButton.isEnabled());
    }

    @UiHandler("editNoteButton")
    protected void editNote(ClickEvent event) {
        showNoteLookup(true);
    }

    @UiHandler("addNoteButton")
    protected void addNote(ClickEvent event) {
        showNoteLookup(false);
    }

    private void displayNotes() {
        if ( !isVisible)
            return;

        if (redraw) {
            displayedExtNote = null;
            displayedIntNote = null;
            if (manager != null && analysis != null) {
                displayedExtNote = manager.analysisExternalNote.get(analysis);
                if (manager.analysisInternalNote.count(analysis) > 0)
                    displayedIntNote = manager.analysisInternalNote.get(analysis, 0);
            }
            redraw = false;
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
                Window.alert("evaluateEdit:" + e.getMessage());
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
                     * created, even though different values for it may get
                     * passed to showNoteLookup on subsequent calls
                     */
                    if (editNoteLookup.getHasSubject()) {
                        displayedIntNote = manager.analysisInternalNote.getEditing(analysis);
                        setNoteFields(displayedIntNote,
                                      editNoteLookup.getSubject(),
                                      editNoteLookup.getText());
                        drawInternalNotes();
                    } else {
                        displayedExtNote = manager.analysisExternalNote.getEditing(analysis);
                        setNoteFields(displayedExtNote, null, editNoteLookup.getText());
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

            if (note != null && note.getId() < 0) {
                subject = note.getSubject();
                text = note.getText();
            }
        }

        modal = new ModalWindow();
        modal.setSize("620px", "550px");
        modal.setName(Messages.get().gen_noteEditor());
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