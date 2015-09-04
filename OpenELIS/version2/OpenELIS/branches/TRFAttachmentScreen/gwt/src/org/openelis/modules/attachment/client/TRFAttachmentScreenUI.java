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

import org.openelis.cache.CategoryCache;
import org.openelis.cache.SectionCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AttachmentDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.SectionDO;
import org.openelis.manager.AttachmentManager;
import org.openelis.meta.AttachmentMeta;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.EntityLockedException;
import org.openelis.ui.common.SystemUserPermission;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.SectionPermission.SectionFlags;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.MultiDropdown;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;
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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public abstract class TRFAttachmentScreenUI extends Screen {

    @UiTemplate("TRFAttachment.ui.xml")
    interface TRFAttachmentUiBinder extends UiBinder<Widget, TRFAttachmentScreenUI> {
    };

    public static final TRFAttachmentUiBinder               uiBinder        = GWT.create(TRFAttachmentUiBinder.class);

    protected HashMap<Integer, AttachmentManager>           managers;

    @UiField
    protected TextBox<String>                               description;

    @UiField
    protected MultiDropdown<Integer>                        queryType;

    @UiField
    protected CheckBox                                      unattached;

    @UiField
    protected Table                                         table;

    @UiField
    protected Dropdown<Integer>                             tableType, section;

    @UiField
    protected Button                                        queryButton, updateButton,
                    commitButton, abortButton, refreshButton, unlockAllButton;

    protected TRFAttachmentScreenUI                         screen;

    protected AsyncCallbackUI<ArrayList<AttachmentManager>> queryCall;

    protected AsyncCallbackUI<AttachmentManager>            fetchForUpdateCall, updateCall,
                    unlockCall;

    protected boolean                                       closeHandlerAdded;

    protected String                                        defaultPattern;

    protected int                                           attachmentShownRow;

    protected static int                                    MAX_ATTACHMENTS = 1000;

    public TRFAttachmentScreenUI() throws Exception {
        initWidget(uiBinder.createAndBindUi(this));

        initialize();
        setState(DEFAULT);
        loadTable(null);

        logger.fine("Data Entry TRF Attachment Screen Opened");
    }

    protected void initialize() {
        Item<Integer> row;
        ArrayList<Item<Integer>> model;

        screen = this;

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
                return forward ? queryType : unlockAllButton;
            }
        });

        addScreenHandler(queryType, AttachmentMeta.getTypeId(), new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                queryType.setEnabled(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? table : description;
            }
        });

        addScreenHandler(unattached, "unattached", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                unattached.setEnabled(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? table : queryType;
            }
        });

        addScreenHandler(table, "table", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                loadTable(null);
            }

            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? refreshButton : unlockAllButton;
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
                Integer sectId;
                String name;
                Object val;
                SystemUserPermission perm;
                AttachmentManager am;

                r = event.getRow();
                c = event.getCol();

                if (c == 0) {
                    val = table.getValueAt(r, c);
                    if ( ( !isState(DISPLAY) || "Y".equals(val)) || !reserve(r))
                        event.cancel();
                    return;
                }

                /*
                 * this makes sure that the attachment for a row is not shown
                 * more than once if different cells of the row are clicked one
                 * after the other
                 */
                if (attachmentShownRow != r) {
                    displayAttachment(table.getRowAt(r),
                                      Messages.get().trfAttachment_dataEntryTRFAttachment());
                    attachmentShownRow = r;
                }

                perm = UserCache.getPermission();
                am = managers.get(table.getRowAt(r).getData());
                sectId = am.getAttachment().getSectionId();

                try {
                    if (sectId != null) {
                        /*
                         * allow changing any field of the attachment only if
                         * the user has assign permission to the current section
                         */
                        name = SectionCache.getById(sectId).getName();
                        if ( !perm.has(name, SectionFlags.ASSIGN)) {
                            event.cancel();
                            return;
                        }
                    }

                    if (c == 2 || c == 3) {
                        if ( !isState(UPDATE))
                            event.cancel();
                    } else if (c == 4) {
                        /*
                         * make sure that the section can't be changed to one
                         * that the user doesn't have assign permission to
                         */
                        for (Item<Integer> row : section.getModel()) {
                            name = SectionCache.getById(row.getKey()).getName();
                            row.setEnabled(perm.has(name, SectionFlags.ASSIGN));
                        }
                        if ( !isState(UPDATE))
                            event.cancel();
                    } else {
                        event.cancel();
                    }
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    event.cancel();
                }
            }
        });

        table.addCellEditedHandler(new CellEditedHandler() {
            @Override
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Integer id;
                AttachmentDO data;
                Object val;

                r = event.getRow();
                c = event.getCol();

                val = table.getValueAt(r, c);
                id = table.getRowAt(r).getData();
                data = managers.get(id).getAttachment();
                
                switch (c) {
                    case 2:
                        data.setDescription((String)val);
                        break;
                    case 3:
                        data.setTypeId((Integer)val);
                        break;
                    case 4:
                        data.setSectionId((Integer)val);
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
                return forward ? table : refreshButton;
            }
        });

        /*
         * load models in the dropdowns
         */
        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("attachment_type")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }

        queryType.setModel(model);
        tableType.setModel(model);

        model = new ArrayList<Item<Integer>>();
        for (SectionDO s : SectionCache.getList())
            model.add(new Item<Integer>(s.getId(), s.getName()));

        section.setModel(model);
    }

    public abstract String getPattern();

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    public void setWindow(WindowInt window) {
        super.setWindow(window);
        /*
         * this flag needs to be reset every time the screen's window changes,
         * which can happen when the screen is brought up in a modal window and
         * in that case the handler for BeforeCloseHandler will be needed to be
         * added to the new window
         */
        closeHandlerAdded = false;

        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                if (attachmentsReserved()) {
                    event.cancel();
                    setError(Messages.get().trfAttachment_firstUnlockAll());
                } else if (isState(UPDATE)) {
                    event.cancel();
                    setError(Messages.get().gen_mustCommitOrAbort());
                }
            }
        });
    }

    public ArrayList<QueryData> getQueryFields() {
        QueryData field;
        ArrayList<QueryData> fields;

        fields = new ArrayList<QueryData>();
        field = (QueryData)description.getQuery();
        if (field != null) {
            field.setKey(AttachmentMeta.getDescription());
            fields.add(field);
        }
        field = (QueryData)queryType.getQuery();
        if (field != null) {
            field.setKey(AttachmentMeta.getTypeId());
            fields.add(field);
        }

        return fields;
    }

    /**
     * Creates and executes a query to fetch the attachments to be shown on the
     * screen
     */
    public void fetchUnattached(String pattern) {
        Query query;

        /*
         * query for the TRFs for this domain
         */
        description.setValue(pattern);
        queryType.setValue(null);
        unattached.setValue("Y");

        query = new Query();
        query.setFields(getQueryFields());
        query.setRowsPerPage(MAX_ATTACHMENTS);
        managers = null;

        executeQuery(query);
    }

    /**
     * Opens the file linked to the attachment showing on the passed node. If
     * "name" is null or if it's different from the previous time this method
     * was called then the file is opened in a new window, otherwise it's opened
     * in the same window as before.
     */
    public void displayAttachment(Row row, String name) {
        Integer id;

        id = (Integer)row.getData();
        if (id == null)
            return;
        try {
            /*
             * passing the same name to displayAttachment makes sure that the
             * files open in the same window
             */
            AttachmentUtil.displayAttachment(id, name, window);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * If "auto select next" is checked then reserves the next attachment that
     * can be reserved and returns its manager. Otherwise, reserves the
     * currently selected attachment and returns its manager.
     */
    public AttachmentManager getReserved() {
        Row row;

        row = table.getRowAt(table.getSelectedRow());
        if (row == null)
            return null;

        return managers.get((Integer)row.getData());
    }

    /**
     * puts the screen in query state
     */
    @UiHandler("queryButton")
    protected void query(ClickEvent event) {
        if (attachmentsReserved()) {
            setError(Messages.get().trfAttachment_firstUnlockAll());
        } else {
            description.setFocus(true);
            setState(QUERY);
            setDone(Messages.get().gen_enterFieldsToQuery());
        }
    }

    /**
     * puts the screen in update state and locks the selected attachment
     */
    @UiHandler("updateButton")
    protected void update(ClickEvent event) {
        Row row;
        AttachmentManager am;

        setBusy(Messages.get().gen_lockForUpdate());

        if (fetchForUpdateCall == null) {
            fetchForUpdateCall = new AsyncCallbackUI<AttachmentManager>() {
                public void success(AttachmentManager result) {
                    int r;
                    Row row;
                    String lock, st;

                    managers.put(result.getAttachment().getId(), result);
                    r = table.getSelectedRow();
                    row = table.getRowAt(r);
                    lock = row.getCell(0);
                    st = row.getCell(1);
                    /*
                     * if the attachment was not reserved, it could mean that it
                     * may have been locked by some other user; so the status
                     * may be "Locked by xyz", where "xyz" is the other user's
                     * name; if the attachment can be locked now, but is not
                     * reserved i.e. the checkbox is not checked, no status
                     * should be shown
                     */
                    if ("N".equals(lock))
                        st = null;
                    refreshRow(r, lock, st, result);
                    setState(UPDATE);
                    table.startEditing(r, 3);
                }

                public void failure(Throwable e) {
                    EntityLockedException le;

                    if (e instanceof EntityLockedException) {
                        le = (EntityLockedException)e;
                        table.setValueAt(table.getSelectedRow(),
                                         1,
                                         Messages.get().trfAttachment_lockedBy(le.getUserName()));
                    } else {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE,
                                   e.getMessage() != null ? e.getMessage() : "null",
                                   e);
                    }
                }

                public void finish() {
                    clearStatus();
                }
            };
        }

        row = table.getRowAt(table.getSelectedRow());
        am = managers.get(row.getData());
        AttachmentService.get().fetchForUpdate(am.getAttachment().getId(), fetchForUpdateCall);
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
            setError(Messages.get().gen_correctErrors());
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

    protected void commitQuery() {
        Query query;

        if (attachmentsReserved()) {
            setError(Messages.get().trfAttachment_firstUnlockAll());
            return;
        }

        query = new Query();
        query.setFields(getQueryFields());

        query.setRowsPerPage(MAX_ATTACHMENTS);
        managers = null;
        executeQuery(query);
    }

    protected void commitUpdate() {
        Row row;

        finishEditing();

        if (validate().getStatus() == Validation.Status.ERRORS) {
            setError(Messages.get().gen_correctErrors());
            return;
        }

        setBusy(Messages.get().gen_updating());

        if (updateCall == null) {
            updateCall = new AsyncCallbackUI<AttachmentManager>() {
                public void success(AttachmentManager result) {
                    reserveAndRefreshRow(result);
                    clearStatus();
                }

                public void validationErrors(ValidationErrorsList e) {
                    showErrors(e);
                }

                public void failure(Throwable e) {
                    Window.alert("commitUpdate(): " + e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    clearStatus();
                }
            };
        }

        row = table.getRowAt(table.getSelectedRow());
        AttachmentService.get().update(managers.get(row.getData()), updateCall);
    }

    /**
     * Reverts any changes made to the data on the screen and disables editing
     * of the widgets. If the attachment was locked, calls the service method to
     * unlock it and loads the screen with that data.
     */
    @UiHandler("abortButton")
    protected void abort(ClickEvent event) {
        Row row;
        AttachmentManager am;

        finishEditing();
        clearErrors();
        setBusy(Messages.get().gen_cancelChanges());

        if (isState(QUERY)) {
            if (table.getRowCount() > 0)
                setState(DISPLAY);
            else
                setState(DEFAULT);
            setDone(Messages.get().gen_queryAborted());
        }
        if (isState(UPDATE)) {
            if (unlockCall == null) {
                unlockCall = new AsyncCallbackUI<AttachmentManager>() {
                    public void success(AttachmentManager result) {
                        reserveAndRefreshRow(result);
                        setDone(Messages.get().gen_updateAborted());
                    }

                    public void failure(Throwable e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        clearStatus();
                    }
                };
            }

            row = table.getRowAt(table.getSelectedRow());
            am = managers.get(row.getData());
            AttachmentService.get().unlock(am.getAttachment().getId(), unlockCall);
        }
    }

    /**
     * Executes the query for the latest unattached attachments and reloads the
     * table with the returns data; doesn't execute the query if any attachment
     * currently in the table is locked
     */
    @UiHandler("refreshButton")
    protected void refresh(ClickEvent event) {
        if (attachmentsReserved())
            setError(Messages.get().trfAttachment_firstUnlockAll());
        else
            fetchUnattached(getPattern());
    }

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
                    managers.put(id, am);
                    refreshRow(i, "N", null, am);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
        setState(DISPLAY);
        clearStatus();
    }

    /**
     * Uses the passed query to fetch attachments and refreshes the screen with
     * the returned data. If "fetchUnattached" is true then only unattached
     * attachments matching the query are fetched otherwise all matching
     * attachments are fetched.
     */
    private void executeQuery(final Query query) {
        attachmentShownRow = -1;
        setBusy(Messages.get().gen_querying());

        if (queryCall == null) {
            queryCall = new AsyncCallbackUI<ArrayList<AttachmentManager>>() {
                public void success(ArrayList<AttachmentManager> result) {
                    Row row;

                    loadTable(null);

                    /*
                     * this map is used to link a tree node with the manager
                     * containing the attachment or attachment item that it's
                     * showing
                     */
                    if (managers == null)
                        managers = new HashMap<Integer, AttachmentManager>();

                    for (AttachmentManager am : result)
                        managers.put(am.getAttachment().getId(), am);

                    setState(DISPLAY);
                    loadTable(result);
                    clearStatus();
                    row = table.getRowAt(0);
                    table.selectRowAt(0);
                    displayAttachment(row, Messages.get().trfAttachment_dataEntryTRFAttachment());
                    attachmentShownRow = 0;
                }

                public void notFound() {
                    setState(DEFAULT);
                    loadTable(null);
                    setDone(Messages.get().gen_noRecordsFound());
                }

                public void lastPage() {
                    int page;

                    /*
                     * make sure that the page doesn't stay one more than the
                     * current one if there are no more pages in this direction
                     */
                    page = query.getPage();
                    if (page > 0)
                        query.setPage(page - 1);
                    setError(Messages.get().gen_noMoreRecordInDir());
                }

                public void failure(Throwable e) {
                    Window.alert("Error: Data Entry TRF Attachment call query failed; " +
                                 e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    setError(Messages.get().gen_queryFailed());
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

    public boolean reserve(int r) {
        Integer id;
        AttachmentManager am;

        id = table.getRowAt(r).getData();
        /*
         * this attachment is currently not reserved and the user is trying to
         * reserve it; try to lock the attachment; show the name of the user who
         * has it locked under "Status"; if the attachment has already been
         * attached, show "Attached" under "Status"
         */
        try {
            am = AttachmentService.get().fetchForReserve(id);
            managers.put(id, am);
            table.setValueAt(r, 1, getStatus(UserCache.getPermission().getLoginName(), am));
        } catch (EntityLockedException e) {
            table.setValueAt(r, 1, getStatus(e.getUserName(), null));
            return false;
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
        return true;
    }

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

    private boolean attachmentsReserved() {
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
     * Loads the tree with the data in the passed managers. If "reloadTree" is
     * true then loads the tree from scratch, otherwise only adds new nodes; if
     * "addAfter" is true then adds the new nodes after the current nodes,
     * otherwise adds them at the beginning. If "isLoadedFromQuery" and
     * "reloadTree" are both true then adds the node for "Click For More...",
     * because the tree is getting loaded from the results of a fresh query.
     */
    private void loadTable(ArrayList<AttachmentManager> ams) {
        Row row;
        AttachmentDO data;

        if (ams == null) {
            table.setModel(null);
            return;
        }

        for (AttachmentManager am : ams) {
            row = new Row(6);
            data = am.getAttachment();
            row.setCell(0, "N");
            row.setCell(2, data.getDescription());
            row.setCell(3, data.getTypeId());
            row.setCell(4, data.getSectionId());
            row.setCell(5, data.getCreatedDate());
            row.setData(data.getId());
            table.addRow(row);
        }
    }

    private void refreshRow(int index, String lock, String status, AttachmentManager am) {
        Row row;
        AttachmentDO data;

        row = table.getRowAt(index);
        data = am.getAttachment();
        table.setValueAt(index, 0, lock);
        table.setValueAt(index, 1, status);
        table.setValueAt(index, 2, data.getDescription());
        table.setValueAt(index, 3, data.getTypeId());
        table.setValueAt(index, 4, data.getSectionId());
        table.setValueAt(index, 5, data.getCreatedDate());
        row.setData(data.getId());
    }

    private void reserveAndRefreshRow(AttachmentManager am) {
        int r;
        Row row;
        String lock;

        r = table.getSelectedRow();
        row = table.getRowAt(r);
        lock = "N";
        if ("Y".equals(row.getCell(0))) {
            /*
             * if the updated attachment was reserved before updating, try to
             * lock it, because update in the back-end unlocks the record
             */
            if (reserve(r))
                lock = "Y";
        } else {
            managers.put(am.getAttachment().getId(), am);
        }
        refreshRow(r, lock, (String)row.getCell(1), am);
        setState(DISPLAY);
    }
}