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
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AttachmentDO;
import org.openelis.manager.AttachmentManager;
import org.openelis.ui.common.EntityLockedException;
import org.openelis.ui.common.SystemUserPermission;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Confirm;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;
import org.openelis.ui.widget.tree.Node;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class TRFAttachmentScreenUI extends Screen {

    @UiTemplate("TRFAttachment.ui.xml")
    interface TRFAttachmentUiBinder extends UiBinder<Widget, TRFAttachmentScreenUI> {
    };

    public static final TRFAttachmentUiBinder               uiBinder      = GWT.create(TRFAttachmentUiBinder.class);

    protected AttachmentManager                             manager, previousManager;

    protected HashMap<Integer, AttachmentManager>           managers;

    @UiField
    protected Table                                         table;

    @UiField
    protected Button                                        refreshButton, unlockButton;

    protected TRFAttachmentScreenUI                         screen;

    protected Confirm                                       confirm;

    protected Query                                         query;

    protected AsyncCallbackUI<ArrayList<AttachmentManager>> queryCall;

    protected AsyncCallbackUI<AttachmentManager>            fetchForUpdateCall, updateCall,
                    unlockCall;

    protected boolean                                       closeHandlerAdded;

    protected static int                                    MAX_ATTACHMENTS = 1000;

    public TRFAttachmentScreenUI() throws Exception {
        initWidget(uiBinder.createAndBindUi(this));

        initialize();
        setState(QUERY);
        loadTable(null);

        logger.fine("TRF Attachment Screen Opened");
    }

    protected void initialize() {

        screen = this;

        addScreenHandler(table, "table", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                table.setModel(null);
            }

            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? refreshButton : unlockButton;
            }
        });

        /*
         * screen fields and widgets
         */
        table.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                nodeSelected(event.getSelectedItem());
                displayAttachment(table.getRowAt(event.getSelectedItem()),
                                  Messages.get().trfAttachment_trfAttachment());
            }
        });

        table.addDoubleClickHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                if ( !isState(UPDATE))
                    displayAttachment(table.getRowAt(table.getSelectedRow()), null);
            }
        });

        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            @Override
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                int r, c;
                Object val, data;

                r = event.getRow();
                c = event.getCol();
                if (c != 0) {
                    event.cancel();
                    return;
                }

                val = table.getValueAt(r, c);
                data = table.getRowAt(r).getData();
                if ("Y".equals(val) || data == null)
                    event.cancel();
            }
        });

        table.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                Object val;
                AttachmentDO data;
                Row row;
                SystemUserPermission perm;

                // val = table.getValueAt(event.getRow(), event.getCol());
                // data = manager.getAttachment();
                perm = UserCache.getPermission();
                switch (event.getCol()) {
                    case 0:
                        table.setValueAt(event.getRow(), 1, perm.getLoginName());
                        break;
                }
            }
        });

        addScreenHandler(refreshButton, "refreshButton", new ScreenHandler<ArrayList<Row>>() {
            public void onStateChange(StateChangeEvent event) {
                refreshButton.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? unlockButton : table;
            }
        });

        addScreenHandler(unlockButton, "unlockButton", new ScreenHandler<ArrayList<Row>>() {
            public void onStateChange(StateChangeEvent event) {
                unlockButton.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? table : refreshButton;
            }
        });
    }

    public void setWindow(WindowInt window) {
        super.setWindow(window);
        previousManager = null;
        /*
         * this flag needs to be reset every time the screen's window changes,
         * which can happen when the screen is brought up in a modal window and
         * in that case the handler for BeforeCloseHandler will be needed to be
         * added to the new window
         */
        closeHandlerAdded = false;
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
     * Creates and executes a query to fetch the attachments to be shown on the
     * screen
     */
    public void search() {
        /*
         * query is a class variable because this screen doesn't use a screen
         * navigator but it needs to keep track of the previously executed query
         * and not a query created from the screen's current data
         */
        query = new Query();
        query.setFields(getQueryFields());
        query.setRowsPerPage(MAX_ATTACHMENTS);
        managers = null;

        executeQuery(query);
    }

    /**
     * Uses the passed query to fetch attachments and refreshes the screen with
     * the returned data. If "fetchUnattached" is true then only unattached
     * attachments matching the query are fetched otherwise all matching
     * attachments are fetched.
     */
    public void executeQuery(final Query query) {
        setBusy(Messages.get().gen_querying());

        if (queryCall == null) {
            queryCall = new AsyncCallbackUI<ArrayList<AttachmentManager>>() {
                public void success(ArrayList<AttachmentManager> result) {
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
                    table.selectRowAt(0);
                    nodeSelected(0);
                    searchSuccessful();
                    displayAttachment(table.getRowAt(0), Messages.get()
                                                                 .trfAttachment_trfAttachment());
                }

                public void notFound() {
                    setState(QUERY);
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
                    Window.alert("Error: TRF Attachment call query failed; " + e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    setError(Messages.get().gen_queryFailed());
                }
            };
        }

        AttachmentService.get()
                         .fetchUnattachedByDescription(query.getFields().get(0).getQuery(),
                                                       query.getPage() * query.getRowsPerPage(),
                                                       query.getRowsPerPage(),
                                                       queryCall);
    }

    /**
     * If "auto select next" is checked then reserves the next attachment that
     * can be reserved and returns its manager. Otherwise, reserves the
     * currently selected attachment and returns its manager.
     */
    public AttachmentManager getReserved() {
        AttachmentManager am;
        Row row;

        row = table.getRowAt(table.getSelectedRow());
        if (row == null)
            return null;

        if (isState(UPDATE)) {
            /*
             * don't allow reserving an attachment if an attachment is being
             * updated
             */
            setError(Messages.get().gen_mustCommitOrAbort());
            return null;
        }

        setBusy(Messages.get().gen_lockForUpdate());
        am = null;
        /*
         * reserve the currently selected attachment and return its manager
         */
        try {
            am = reserve(row);
        } catch (EntityLockedException e) {
            /*
             * mark the current node as locked by someone else
             */
            // table.refreshRow(row);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        clearStatus();

        return am;
    }

    /**
     * Defines the action to be performed when attachments selected by a user
     * are to be attached to another record like sample
     */
    public void attach(ArrayList<AttachmentDO> attachments) {
    }

    /**
     * Defines the action to be performed after the search executed on the
     * screen has completed and attachments were found
     */
    public void searchSuccessful() {
    }

    @UiHandler("unlockButton")
    protected void unlock(ClickEvent event) {
        Row row;

        for (int i = 0; i < table.getRowCount(); i++ ) {
            row = table.getRowAt(i);
            if (row.getData() != null) {
                table.setValueAt(i, 0, "N");
                table.setValueAt(i, 1, null);
            }
        }
    }

    /**
     * Reserves and returns the manager for the attachment showing on the passed
     * node and refreshes the tree. Throws exception if there was an issue with
     * reserving the attachment.
     */
    private AttachmentManager reserve(Row row) throws Exception {
        Integer id;

        id = row.getData();
        manager = AttachmentService.get().getReserved(id);
        previousManager = manager;
        /*
         * the attachment is now reserved for this user, so refresh the screen
         * and display the attachment
         */
        managers.put(id, manager);
        setState(RESERVED);
        // table.selectRowAt(row);
        // node.setStatus(AttachmentNode.Status.LOCKED_BY_SELF);
        // reloadAttachment(node);
        return manager;
    }

    /**
     * Performs specific actions based on node selected in the tree, specified
     * by the passed index. If an attachment or attachment item was selected
     * then resets the manager otherwise tries to load the next page, because
     * "Click for Next.." is selected. Also, enables or disables widgets.
     */
    private void nodeSelected(int index) {
        Integer id;
        Node node;

        // node = tree.getNodeAt(index);
        // id = node.getData();
        // manager = id != null ? managers.get(id) : null;
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

        if (ams == null)
            return;
        /*
         * create and add the nodes for attachments and attachment items to the
         * tree; if "reloadTree" is true then the nodes are added to the root
         * because the tree will be reloaded, otherwise they're added after the
         * current nodes
         */
        for (AttachmentManager am : ams) {
            row = new Row(6);
            loadAttachment(row, am);
            table.addRow(row);
        }
    }

    /**
     * Creates a subtree loaded from the passed manager. Makes passed node the
     * attachment node and also the root of the subtree.
     */
    private void loadAttachment(Row row, AttachmentManager am) {
        AttachmentDO data;

        data = am.getAttachment();
        /*
         * the node for the attachment
         */
        row.setCell(0, "N");
        row.setCell(2, data.getDescription());
        row.setCell(3, data.getCreatedDate());
        row.setCell(4, data.getId());
        row.setData(data.getId());
    }
}