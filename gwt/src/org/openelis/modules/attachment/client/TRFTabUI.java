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

import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AttachmentDO;
import org.openelis.domain.AttachmentIssueViewDO;
import org.openelis.manager.AttachmentManager;
import org.openelis.meta.AttachmentMeta;
import org.openelis.modules.attachment.client.AttachmentIssueEvent.Action;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.EntityLockedException;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.TextBox;
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
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public abstract class TRFTabUI extends Screen {
    @UiTemplate("TRFTab.ui.xml")
    interface TRFTabUIBinder extends UiBinder<Widget, TRFTabUI> {
    };

    private static TRFTabUIBinder                           uiBinder        = GWT.create(TRFTabUIBinder.class);

    @UiField
    protected Button                                        queryButton, updateButton,
                    commitButton, abortButton, refreshButton, unlockAllButton;

    @UiField
    protected TextBox<String>                               description;

    @UiField
    protected CheckBox                                      unattached;

    @UiField
    protected Table                                         table;

    protected AsyncCallbackUI<ArrayList<AttachmentManager>> queryCall;

    protected Screen                                        parentScreen, screen1;

    protected EventBus                                      parentBus;

    protected int                                           trfShownRow;

    protected boolean                                       query, refresh;

    protected HashMap<Integer, AttachmentManager>           managerMap;

    protected HashMap<Integer, AttachmentIssueViewDO>       issueMap;

    protected static int                                    MAX_ATTACHMENTS = 1000;

    public TRFTabUI(Screen parentScreen) {
        this.parentScreen = parentScreen;
        this.parentBus = parentScreen.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        initialize();
    }

    protected void initialize() {
        screen1 = this;

        //
        // button panel buttons
        //
        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                queryButton.setEnabled(isState(QUERY, DEFAULT, DISPLAY));
                if (isState(QUERY)) {
                    queryButton.lock();
                    queryButton.setPressed(true);
                }
            }
        });
        addShortcut(queryButton, 'q', CTRL);

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
                commitButton.setEnabled(isState(QUERY, UPDATE));
            }
        });
        addShortcut(commitButton, 'm', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                abortButton.setEnabled(isState(QUERY, UPDATE));
            }
        });
        addShortcut(abortButton, 'o', CTRL);

        addScreenHandler(description, AttachmentMeta.getDescription(), new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                description.setEnabled(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? unattached : unlockAllButton;
            }
        });

        addScreenHandler(unattached, "unattached", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                unattached.setEnabled(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? table : description;
            }
        });

        addScreenHandler(table, "table", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent<ArrayList<Row>> event) {
                loadTable(null);
            }

            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? refreshButton : unattached;
            }
        });

        table.addUnselectionHandler(new UnselectionHandler<Integer>() {
            public void onUnselection(UnselectionEvent<Integer> event) {
                /*
                 * the selected row is not allowed to be unselected in Update
                 * state because its manager is locked
                 */
                if (isState(UPDATE))
                    event.cancel();
            }
        });

        table.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                /*
                 * no other row is allowed to be selected in Update state
                 * because the selected row's manager is locked
                 */
                if (isState(UPDATE))
                    event.cancel();
            }
        });

        /*
         * screen fields and widgets
         */
        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            @Override
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                int r, c;
                Object val;

                r = event.getRow();
                c = event.getCol();

                if (c == 0) {
                    val = table.getValueAt(r, c);
                    if ( ( !isState(DISPLAY) || "Y".equals(val)) || !lock(r))
                        event.cancel();
                    /*
                     * if this row got selected by clicking in the first column
                     * (the checkbox) instead of some other one, select the row
                     * whose TRF was shown the most recently, if any, otherwise
                     * unselect this row; the TRF is not shown if the first
                     * column is clicked, so if this row stayed selected, the
                     * user may assume that the TRF was shown and may not click
                     * in some other column to see it
                     */
                    if (trfShownRow != r) {
                        if (trfShownRow >= 0)
                            table.selectRowAt(trfShownRow);
                        else
                            table.unselectRowAt(r);
                    }
                    return;
                }

                /*
                 * this makes sure that the attachment for a row is not shown
                 * more than once if different cells of the row are clicked one
                 * after the other; displayAttachment is called from here
                 * instead of from SelectionHandler because the TRF is not shown
                 * if the user clicks in the first column (the checkbox) and the
                 * column can't be known in SelectionHandler
                 */
                if (trfShownRow != r) {
                    displayAttachment(table.getRowAt(r));
                    trfShownRow = r;
                }

                if (c != 3 || !isState(UPDATE))
                    event.cancel();
            }
        });

        table.addCellEditedHandler(new CellEditedHandler() {
            @Override
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Integer id;
                String val;
                AttachmentIssueViewDO ai;
                AttachmentDO a;
                AttachmentManager am;

                r = event.getRow();
                c = event.getCol();

                val = (String)table.getValueAt(r, c);
                id = table.getRowAt(r).getData();
                am = managerMap.get(id);
                ai = issueMap.get(id);

                switch (c) {
                    case 3:
                        if (ai == null) {
                            /*
                             * if there's no attachment issue for this row's
                             * attachment and the user entered some text, create
                             * a new issue
                             */
                            if ( !DataBaseUtil.isEmpty(val)) {
                                ai = new AttachmentIssueViewDO();
                                a = am.getAttachment();
                                ai.setAttachmentId(a.getId());
                                ai.setAttachmentDescription(a.getDescription());
                                ai.setText(val);
                                issueMap.put(id, ai);
                                table.getRowAt(r).setData(a.getId());
                            }
                        } else {
                            ai.setText(val);
                        }
                        break;
                }
            }
        });

        addScreenHandler(refreshButton, "refreshButton", new ScreenHandler<ArrayList<Row>>() {
            public void onStateChange(StateChangeEvent event) {
                refreshButton.setEnabled(isState(DEFAULT, DISPLAY));
            }

            public Widget onTab(boolean forward) {
                return forward ? unlockAllButton : table;
            }
        });

        addScreenHandler(unlockAllButton, "unlockAllButton", new ScreenHandler<ArrayList<Row>>() {
            public void onStateChange(StateChangeEvent event) {
                unlockAllButton.setEnabled(isState(DISPLAY));
            }

            public Widget onTab(boolean forward) {
                return forward ? description : refreshButton;
            }
        });

        parentBus.addHandler(AttachmentIssueEvent.getType(), new AttachmentIssueEvent.Handler() {
            @Override
            public void onAttachmentIssue(AttachmentIssueEvent event) {
                Query q;

                if (parentScreen != event.getSource())
                    return;

                /*
                 * the tab fires events to the main screen to request it to do
                 * various operations e.g. fetch, lock, unlock etc; that's
                 * because the main screen maintains the data structures used by
                 * all tabs; if the operation was successful, the main screen
                 * fires an event to let the tab know; that event is handled
                 * here; the tab can find out if this event was fired to respond
                 * to a previous event fired by it or some other tab by checking
                 * if it's the event's "originalSource"; it can then perform
                 * some operation or ignore the event
                 */
                issueMap = event.getIssueMap();
                switch (event.getAction()) {
                    case FETCH:
                        if (screen1 == event.getOriginalSource()) {
                            if (query) {
                                /*
                                 * the user wants to query; set the screen in
                                 * Query state
                                 */
                                query = false;
                                description.setFocus(true);
                                setState(QUERY);
                                parentScreen.setDone(Messages.get()
                                                                         .gen_enterFieldsToQuery());
                            } else if (refresh) {
                                /*
                                 * the user wants to refresh the list of
                                 * attachments; fetch unattached attachments for
                                 * the default pattern
                                 */
                                refresh = false;
                                description.setValue(getPattern());
                                unattached.setValue("Y");

                                managerMap = null;

                                q = new Query();
                                q.setFields(getQueryFields());
                                q.setRowsPerPage(MAX_ATTACHMENTS);

                                executeQuery(q);
                            }
                        } else {
                            /*
                             * refresh the issues in the table because they were
                             * fetched from the database by some other tab
                             */
                            query = false;
                            refresh = false;
                            refreshIssues();
                        }
                        break;
                    case ADD:
                    case UPDATE:
                    case DELETE:
                        /*
                         * refresh the issues in the table because some were
                         * added, updated or deleted
                         */
                        refreshIssues();
                        setState(DISPLAY);
                        break;
                    case LOCK:
                        if (screen1 == event.getOriginalSource()) {
                            /*
                             * an issue is locked to be updated; refresh the tab
                             * to show the latest data
                             */
                            refreshSelectedRow();
                            setState(UPDATE);
                            table.startEditing(table.getSelectedRow(), 3);
                        }
                        break;
                    case UNLOCK:
                        if (screen1 == event.getOriginalSource()) {
                            /*
                             * an issue was unlocked; refresh the tab to show
                             * the previously deleted or changed issues again
                             */
                            refreshIssues();
                            setState(DISPLAY);
                        }
                        break;
                }
            }

        });
    }

    /**
     * Overridden to specify the pattern for the TRFs of a particular domain
     */
    public abstract String getPattern();

    /**
     * Returns a list of fields that will be used in the query for fetching
     * attachments; it's overridden because the widgets used in the query are
     * not put in query mode; that's because they lose their previous values in
     * query mode and that's not the desired behavior here
     */
    public ArrayList<QueryData> getQueryFields() {
        QueryData field;
        ArrayList<QueryData> fields;

        fields = new ArrayList<QueryData>();
        field = (QueryData)description.getQuery();
        if (field != null) {
            field.setKey(AttachmentMeta.getDescription());
            fields.add(field);
        }

        return fields;
    }

    public Validation validate() {
        Row row;
        AttachmentIssueViewDO data;
        Validation validation;

        table.clearEndUserExceptions();

        validation = super.validate();

        if (isState(UPDATE)) {
            /*
             * don't allow the user to commit if there's no attachment issue for
             * the selected row; this can happen if the attachment didn't have
             * an issue to begin with and the user never entered any any text in
             * the row
             */
            row = table.getRowAt(table.getSelectedRow());
            data = issueMap.get(row.getData());

            if (data == null) {
                table.addException(table.getSelectedRow(),
                                   3,
                                   new Exception(Messages.get().gen_fieldRequiredException()));
                validation.setStatus(Validation.Status.ERRORS);
            }
        }

        return validation;
    }

    /**
     * Executes a query to fetch unattached attachments
     */
    public void fetchUnattached() {
        refresh = true;
        query = false;
        fireAttachmentIssue(AttachmentIssueEvent.Action.FETCH, null);
    }

    /**
     * If a row is selected returns its manager; otherwise, returns null
     */
    public AttachmentManager getSelected() {
        Row row;

        row = table.getRowAt(table.getSelectedRow());
        if (row == null)
            return null;

        return managerMap.get((Integer)row.getData());
    }

    /**
     * Returns true if the checkbox under "Lock" is checked for any attachment
     * in the table; false otherwise
     */
    public boolean isAttachmentsLocked() {
        boolean reserved;
        Row row;

        reserved = false;
        for (int i = 0; i < table.getRowCount(); i++ ) {
            row = table.getRowAt(i);
            if ("Y".equals(row.getCell(0))) {
                reserved = true;
                break;
            }
        }
        return reserved;
    }

    /**
     * Returns the tab's current state
     */
    public State getState() {
        return state;
    }

    /**
     * Puts the screen in query state; if no attachments are locked
     */
    @UiHandler("queryButton")
    protected void query(ClickEvent event) {
        if (isAttachmentsLocked()) {
            Window.alert(Messages.get().trfAttachment_firstUnlockAll());
        } else {
            query = true;
            refresh = false;
            fireAttachmentIssue(AttachmentIssueEvent.Action.FETCH, null);
        }
    }

    /**
     * Puts the screen in update state and locks the selected attachment
     */
    @UiHandler("updateButton")
    protected void update(ClickEvent event) {
        int r;
        Integer id;

        r = table.getSelectedRow();
        if (r < 0) {
            Window.alert(Messages.get().trfAttachment_selectAttachment());
            return;
        }
        id = table.getRowAt(r).getData();
        fireAttachmentIssue(AttachmentIssueEvent.Action.LOCK, id);
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
            parentScreen.setError(Messages.get().gen_correctErrors());
            return;
        }

        switch (state) {
            case QUERY:
                commitQuery();
                break;
            case UPDATE:
                commitUpdate();
                break;
        }
    }

    /**
     * Creates query fields from the data on the screen and executes a query to
     * return a list of attachments
     */
    protected void commitQuery() {
        Query query;

        managerMap = null;

        query = new Query();
        query.setFields(getQueryFields());
        query.setRowsPerPage(MAX_ATTACHMENTS);

        executeQuery(query);
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
        AttachmentIssueEvent.Action action;

        row = table.getRowAt(table.getSelectedRow());
        data = issueMap.get(row.getData());

        if (data.getText() == null) {
            action = AttachmentIssueEvent.Action.DELETE;
        } else {
            if (data.getId() == null)
                action = AttachmentIssueEvent.Action.ADD;
            else
                action = AttachmentIssueEvent.Action.UPDATE;
        }

        fireAttachmentIssue(action, data.getAttachmentId());
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

        finishEditing();
        clearErrors();

        if (isState(QUERY)) {
            parentScreen.setBusy(Messages.get().gen_cancelChanges());
            if (table.getRowCount() > 0)
                setState(DISPLAY);
            else
                setState(DEFAULT);
            parentScreen.setDone(Messages.get().gen_queryAborted());
        } else if (isState(UPDATE)) {
            row = table.getRowAt(table.getSelectedRow());
            fireAttachmentIssue(AttachmentIssueEvent.Action.UNLOCK, (Integer)row.getData());
        }
    }

    /**
     * Executes the query for the latest unattached attachments for a particular
     * domain and reloads the table with the returns data; doesn't execute the
     * query if any attachment currently in the table is locked
     */
    @UiHandler("refreshButton")
    protected void refresh(ClickEvent event) {
        if (isAttachmentsLocked()) {
            Window.alert(Messages.get().trfAttachment_firstUnlockAll());
        } else {
            refresh = true;
            query = false;
            fireAttachmentIssue(AttachmentIssueEvent.Action.FETCH, null);
        }
    }

    /**
     * Unlocks all the locked attachments i.e. the ones whose checkbox is
     * checked and refreshes the rows with the latest data
     */
    @UiHandler("unlockAllButton")
    protected void unlockAll(ClickEvent event) {
        Row row;
        Integer id;
        AttachmentManager am;

        for (int i = 0; i < table.getRowCount(); i++ ) {
            row = table.getRowAt(i);
            id = (Integer)row.getData();
            if ("Y".equals(row.getCell(0))) {
                try {
                    am = AttachmentService.get().unlock(id);
                    managerMap.put(id, am);
                    refreshRow(i, "N", null, id);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
        setState(DISPLAY);
        parentScreen.clearStatus();
    }

    /**
     * Uses the passed query to fetch attachments and refreshes the screen with
     * the returned data; if the checkbox "unattached" is checked, fetches only
     * unattached attachments; otherwise fetches all possible attachments
     * complying with the query
     */
    private void executeQuery(final Query query) {
        trfShownRow = -1;

        parentScreen.setBusy(Messages.get().gen_fetching());

        if (queryCall == null) {
            queryCall = new AsyncCallbackUI<ArrayList<AttachmentManager>>() {
                public void success(ArrayList<AttachmentManager> result) {
                    loadTable(null);

                    /*
                     * this map is used to link a table row with the manager
                     * containing the attachment that it's showing
                     */
                    if (managerMap == null)
                        managerMap = new HashMap<Integer, AttachmentManager>();

                    for (AttachmentManager am : result)
                        managerMap.put(am.getAttachment().getId(), am);

                    setState(DISPLAY);
                    loadTable(result);
                    refreshIssues();
                    parentScreen.clearStatus();
                }

                public void notFound() {
                    setState(DEFAULT);
                    loadTable(null);
                    parentScreen.setDone(Messages.get().gen_noRecordsFound());
                }

                public void failure(Throwable e) {
                    Window.alert("Error: Data Entry TRF Attachment call query failed; " +
                                 e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    parentScreen.setError(Messages.get().gen_queryFailed());
                }
            };
        }

        if ("Y".equals(unattached.getValue()))
            AttachmentService.get()
                             .fetchByQueryUnattached(query.getFields(),
                                                     query.getPage() * query.getRowsPerPage(),
                                                     query.getRowsPerPage(),
                                                     queryCall);
        else
            AttachmentService.get().fetchByQuery(query.getFields(),
                                                 query.getPage() * query.getRowsPerPage(),
                                                 query.getRowsPerPage(),
                                                 queryCall);
    }

    /**
     * Locks the attachment showing on the row at the passed index; the
     * attachment is locked for a longer duration than the one for update
     */
    private boolean lock(int index) {
        Integer id;
        AttachmentManager am;

        id = table.getRowAt(index).getData();
        /*
         * this attachment is currently not locked and the user is trying to
         * lock it; show "Locked by xyz" under status, where "xyz" is the name
         * of the user who has it locked under "Status"; if the attachment has
         * already been attached, show "Attached; Locked by xyz" under "Status"
         */
        try {
            am = AttachmentService.get().fetchForReserve(id);
            managerMap.put(id, am);
            table.setValueAt(index, 1, getStatus(UserCache.getPermission().getLoginName(), am));
        } catch (EntityLockedException e) {
            table.setValueAt(index, 1, getStatus(e.getUserName(), null));
            return false;
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * Creates and returns the message to be shown under "Status"; if the passed
     * manager has any attachment items, the message is
     * "Attached: Locked by xyz" where "xyz" is the passed username; otherwise
     * the message is "Locked by xyz"
     * 
     */
    private String getStatus(String userName, AttachmentManager am) {
        String attached;

        if (am != null && am.item.count() > 0)
            attached = Messages.get().trfAttachment_attached();
        else
            attached = null;
        return DataBaseUtil.concatWithSeparator(Messages.get().trfAttachment_lockedBy(userName),
                                                "; ",
                                                attached);
    }

    /**
     * Loads the table with data in the passed managers
     */
    private void loadTable(ArrayList<AttachmentManager> ams) {
        Row row;
        AttachmentDO data;

        if (ams == null) {
            table.setModel(null);
            return;
        }

        for (AttachmentManager am : ams) {
            row = new Row(5);
            data = am.getAttachment();
            row.setCell(0, "N");
            row.setCell(2, data.getDescription());
            row.setCell(4, data.getCreatedDate());
            row.setData(data.getId());
            table.addRow(row);
        }
    }

    /**
     * Refreshes the row at the passed index with passed arguments; the values
     * in the first two columns are set to "lock" and "status" respectively; the
     * values in the other columns are set as the corresponding fields in the
     * attachment issue whose attachment id is "attachmentId"
     */
    private void refreshRow(int index, String lock, String status, Integer attachmentId) {
        Row row;
        AttachmentDO a;
        AttachmentIssueViewDO ai;

        row = table.getRowAt(index);
        a = managerMap.get(attachmentId).getAttachment();
        ai = issueMap.get(attachmentId);

        table.setValueAt(index, 0, lock);
        table.setValueAt(index, 1, status);
        table.setValueAt(index, 2, a.getDescription());
        table.setValueAt(index, 3, ai != null ? ai.getText() : null);
        table.setValueAt(index, 4, a.getCreatedDate());
        row.setData(a.getId());
    }

    /**
     * This method is called when commit or abort is called for an attachment
     * that was locked for update; both of these operations release the lock in
     * the back-end; so if the attachment was locked by checking the checkbox
     * before clicking "Update", this method tries to obtain the lock again, so
     * that the user can keep the record locked until it's explicitly unlocked
     * by clicking "Unlock All"; refreshes the row with the latest data
     * regardless of whether the attachment could be locked
     */
    private void refreshSelectedRow() {
        int r;
        Integer id;
        Row row;

        r = table.getSelectedRow();
        row = table.getRowAt(r);
        id = row.getData();
        refreshRow(r, (String)row.getCell(0), (String)row.getCell(1), id);
    }

    /**
     * Refreshes the rows in the table to show the latest data in the attachment
     * issues for the attachments in the table
     */
    private void refreshIssues() {
        Integer id;
        AttachmentIssueViewDO data;

        if (issueMap != null) {
            for (int i = 0; i < table.getRowCount(); i++ ) {
                id = table.getRowAt(i).getData();
                data = issueMap.get(id);
                table.setValueAt(i, 3, data != null ? data.getText() : null);
            }
        }
    }

    /**
     * Opens the file linked to the attachment showing on the passed row; if
     * "name" is null or if it's different from the previous time this method
     * was called then the file is opened in a new window, otherwise it's opened
     * in the same window as before.
     */
    private void displayAttachment(Row row) {
        Integer id;

        id = (Integer)row.getData();
        if (id != null)
            parentBus.fireEventFromSource(new DisplayAttachmentEvent(id, true), screen1);
    }

    /**
     * Fires an AttachmentIssueEvent to the main screen to request it to do
     * various operations like fetch, lock, unlock etc. for an attachment issue;
     * the operation is specified by "action" and "attachmentId" is used by the
     * main screen to find the attachment issue
     */
    private void fireAttachmentIssue(Action action, Integer attachmentId) {
        parentBus.fireEventFromSource(new AttachmentIssueEvent(action,
                                                               attachmentId,
                                                               null,
                                                               null,
                                                               screen1), screen1);
    }
}