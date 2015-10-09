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
package org.openelis.modules.attachment.client;

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.Screen.ShortKeys.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.openelis.constants.Messages;
import org.openelis.domain.AttachmentIssueViewDO;
import org.openelis.modules.attachment.client.AttachmentIssueEvent.Action;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;
import org.openelis.ui.widget.table.event.UnselectionEvent;
import org.openelis.ui.widget.table.event.UnselectionHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.VisibleEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class IssuesTabUI extends Screen {
    @UiTemplate("IssuesTab.ui.xml")
    interface IssuesTabUIBinder extends UiBinder<Widget, IssuesTabUI> {
    };

    private static IssuesTabUIBinder                            uiBinder = GWT.create(IssuesTabUIBinder.class);

    @UiField
    protected Button                                            updateButton, deleteButton,
                    commitButton, abortButton, displayButton, refreshButton;

    @UiField
    protected Table                                             table;

    protected AsyncCallbackUI<ArrayList<AttachmentIssueViewDO>> fetchIssuesCall;

    protected AsyncCallbackUI<AttachmentIssueViewDO>            fetchForUpdateCall, updateCall,
                    unlockCall;

    protected AsyncCallback<Void>                               deleteCall;

    protected Screen                                            parentScreen, screen1;

    protected EventBus                                          parentBus;

    protected boolean                                           autoLoadIssues, lockForUpdate,
                    lockForDelete;

    protected AttachmentIssueViewDO                             removed;

    protected ArrayList<AttachmentIssueViewDO>                  issueList;

    protected HashMap<Integer, AttachmentIssueViewDO>           issueMap;

    public IssuesTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();

        autoLoadIssues = true;
    }

    protected void initialize() {
        screen1 = this;
        //
        // button panel buttons
        //
        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                updateButton.setEnabled(isState(UPDATE, DISPLAY));
                if (isState(UPDATE)) {
                    updateButton.lock();
                    updateButton.setPressed(true);
                }
            }
        });
        addShortcut(updateButton, 'u', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                deleteButton.setEnabled(isState(DELETE, DISPLAY));
                if (isState(DELETE)) {
                    deleteButton.lock();
                    deleteButton.setPressed(true);
                }
            }
        });

        addShortcut(deleteButton, 'd', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                commitButton.setEnabled(isState(UPDATE, DELETE));
            }
        });
        addShortcut(commitButton, 'm', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                abortButton.setEnabled(isState(UPDATE, DELETE));
            }
        });
        addShortcut(abortButton, 'o', CTRL);

        addScreenHandler(table, "table", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                loadTable();
            }

            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? displayButton : refreshButton;
            }
        });

        table.addUnselectionHandler(new UnselectionHandler<Integer>() {
            public void onUnselection(UnselectionEvent<Integer> event) {
                /*
                 * the selected row is not allowed to be unselected in Update or
                 * Delete states because its manager is locked
                 */
                if (isState(UPDATE, DELETE))
                    event.cancel();
            }
        });

        table.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                /*
                 * no other row is allowed to be selected in Update or Delete
                 * states because the selected row's manager is locked
                 */
                if (isState(UPDATE, DELETE))
                    event.cancel();
            }
        });

        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            @Override
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if (event.getCol() != 2 || !isState(UPDATE))
                    event.cancel();
            }
        });

        table.addCellEditedHandler(new CellEditedHandler() {
            @Override
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                AttachmentIssueViewDO data;

                r = event.getRow();
                c = event.getCol();

                val = table.getValueAt(r, c);
                data = table.getRowAt(r).getData();

                switch (c) {
                    case 2:
                        data.setText((String)val);
                        break;
                }
            }
        });

        addScreenHandler(displayButton, "displayButton", new ScreenHandler<ArrayList<Row>>() {
            public void onStateChange(StateChangeEvent event) {
                displayButton.setEnabled(isState(UPDATE, DISPLAY));
            }

            public Widget onTab(boolean forward) {
                return forward ? refreshButton : refreshButton;
            }
        });

        addScreenHandler(refreshButton, "refreshButton", new ScreenHandler<ArrayList<Row>>() {
            public void onStateChange(StateChangeEvent event) {
                refreshButton.setEnabled(isState(DEFAULT, DISPLAY));
            }

            public Widget onTab(boolean forward) {
                return forward ? table : displayButton;
            }
        });

        addVisibleHandler(new VisibleEvent.Handler() {
            public void onVisibleOrInvisible(VisibleEvent event) {
                if (event.isVisible() && autoLoadIssues) {
                    issueList = null;
                    issueMap = null;
                    autoLoadIssues = false;
                    fireAttachmentIssue(AttachmentIssueEvent.Action.FETCH, null);
                }
            }
        });

        parentBus.addHandler(AttachmentIssueEvent.getType(), new AttachmentIssueEvent.Handler() {
            @Override
            public void onAttachmentIssue(AttachmentIssueEvent event) {
                int r;
                Row row;
                AttachmentIssueViewDO data;

                if (parentScreen != event.getSource())
                    return;

                r = table.getSelectedRow();
                row = null;
                if (r >= 0)
                    row = table.getRowAt(r);

                issueList = event.getIssueList();
                issueMap = event.getIssueMap();
                data = null;
                switch (event.getAction()) {
                    case FETCH:
                    case ADD:
                    case UPDATE:
                    case DELETE:
                        /*
                         * find which issue is selected in the table before
                         * reloading the table
                         */
                        if (row != null)
                            data = row.getData();
                        loadTable();
                        refresh(data);
                        break;
                    case LOCK:
                        if (screen1 == event.getOriginalSource()) {
                            if (lockForUpdate) {
                                setState(UPDATE);
                                data = issueMap.get(event.getAttachmentId());
                                row.setData(data);
                                table.setValueAt(r, 3, data.getText());
                                table.startEditing(r, 2);
                            } else if (lockForDelete) {
                                setState(DELETE);
                                table.removeRowAt(r);
                            }
                        }
                        break;
                    case UNLOCK:
                        if (screen1 == event.getOriginalSource()) {
                            if (removed != null)
                                removed = null;
                            data = issueMap.get(event.getAttachmentId());
                            loadTable();
                            refresh(data);
                            lockForUpdate = false;
                            lockForDelete = false;
                        }
                        break;
                }
            }
        });
    }

    /**
     * Puts the screen in update state and locks the selected attachment
     */
    @UiHandler("updateButton")
    protected void update(ClickEvent event) {
        AttachmentIssueViewDO data;

        lockForUpdate = true;
        data = table.getRowAt(table.getSelectedRow()).getData();

        fireAttachmentIssue(AttachmentIssueEvent.Action.LOCK, data.getAttachmentId());
    }

    /**
     * Puts the screen in delete state and locks the selected attachment
     */
    @UiHandler("deleteButton")
    protected void delete(ClickEvent event) {
        lockForDelete = true;
        if (removed == null)
            removed = table.getRowAt(table.getSelectedRow()).getData();

        fireAttachmentIssue(AttachmentIssueEvent.Action.LOCK, removed.getAttachmentId());
    }

    /**
     * Validates the data on the screen and based on the current state, and
     * calls the service method to commit the data on the screen, to the
     * database. Shows any errors/warnings encountered during the commit,
     * otherwise refreshes the tree with the committed data.
     */
    @UiHandler("commitButton")
    protected void commit(ClickEvent event) {
        finishEditing();

        if (validate().getStatus() == Validation.Status.ERRORS) {
            parentScreen.getWindow().setError(Messages.get().gen_correctErrors());
            return;
        }

        switch (state) {
            case UPDATE:
                commitUpdate();
                break;
            case DELETE:
                commitDelete();
                break;
        }
    }

    /**
     * Commits the data on the screen to the database; shows any errors/warnings
     * encountered during the commit, otherwise loads the screen with the
     * committed data; if the checkbox for the attachment was checked i.e. it
     * was locked by this user before Update was clicked, tries to lock it
     * again, because update removes the lock in the back-end
     */
    protected void commitUpdate() {
        Row row;
        AttachmentIssueViewDO data;

        row = table.getRowAt(table.getSelectedRow());
        data = row.getData();
        fireAttachmentIssue(AttachmentIssueEvent.Action.UPDATE, data.getAttachmentId());
    }

    /**
     * Commits the data on the screen to the database; shows any errors/warnings
     * encountered during the commit, otherwise loads the screen with the
     * committed data; if the checkbox for the attachment was checked i.e. it
     * was locked by this user before Update was clicked, tries to lock it
     * again, because update removes the lock in the back-end
     */
    protected void commitDelete() {
        fireAttachmentIssue(AttachmentIssueEvent.Action.DELETE, removed.getAttachmentId());
    }

    public State getState() {
        return state;
    }

    /**
     * Reverts any changes made to the data on the screen and disables editing
     * of the widgets; if the checkbox for the attachment was checked i.e. it
     * was locked by this user before Update was clicked, tries to lock it
     * again, because the unlock removes the lock in the back-end
     */
    @UiHandler("abortButton")
    protected void abort(ClickEvent event) {
        Row row;
        AttachmentIssueViewDO data;

        finishEditing();
        clearErrors();

        if (isState(DELETE)) {
            data = removed;
        } else {
            row = table.getRowAt(table.getSelectedRow());
            data = row.getData();
        }

        fireAttachmentIssue(AttachmentIssueEvent.Action.UNLOCK, data.getAttachmentId());
    }

    /**
     * Opens the file linked to the attachment showing on the passed row; if
     * "name" is null or if it's different from the previous time this method
     * was called then the file is opened in a new window, otherwise it's opened
     * in the same window as before.
     */
    @UiHandler("displayButton")
    protected void displayAttachment(ClickEvent event) {
        Row row;
        AttachmentIssueViewDO data;

        row = table.getRowAt(table.getSelectedRow());
        data = row.getData();
        parentBus.fireEventFromSource(new DisplayAttachmentEvent(data.getAttachmentId(), true),
                                      screen1);
    }

    @UiHandler("refreshButton")
    protected void refresh(ClickEvent event) {
        issueList = null;
        issueMap = null;
        fireAttachmentIssue(AttachmentIssueEvent.Action.FETCH, null);
    }

    private void loadTable() {
        Row row;
        ArrayList<Row> model;

        if (issueList == null) {
            table.setModel(null);
            return;
        }

        model = new ArrayList<Row>();
        for (AttachmentIssueViewDO data : issueList) {
            row = new Row(4);
            row.setCell(0, data.getSystemUserLoginName());
            row.setCell(1, data.getTimestamp());
            row.setCell(2, data.getText());
            row.setCell(3, data.getAttachmentDescription());
            row.setData(data);

            model.add(row);
        }

        table.setModel(model);
    }

    private void fireAttachmentIssue(Action action, Integer attachmentId) {
        parentBus.fireEventFromSource(new AttachmentIssueEvent(action,
                                                               attachmentId,
                                                               issueList,
                                                               null,
                                                               screen1), this);
    }

    private void refresh(AttachmentIssueViewDO data) {
        int i, j;
        AttachmentIssueViewDO tdata;

        if (table.getRowCount() > 0) {
            i = 0;
            /*
             * if the issue selected previously is still in the table, find and
             * select the row that's showing it; otherwise, select the first row
             */
            if (data != null) {
                for (j = 0; j < table.getRowCount(); j++ ) {
                    tdata = table.getRowAt(j).getData();
                    if (data.getAttachmentId().equals(tdata.getAttachmentId())) {
                        i = j;
                        break;
                    }
                }
            }
            table.selectRowAt(i);
            setState(DISPLAY);
        } else {
            setState(DEFAULT);
        }
    }
}