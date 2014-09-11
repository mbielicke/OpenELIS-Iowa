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
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.calendar.Calendar;
import org.openelis.ui.widget.fileupload.FileDrop;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;
import org.openelis.ui.widget.tree.Node;
import org.openelis.ui.widget.tree.Tree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class AttachmentScreenUI extends Screen {

    @UiTemplate("Attachment.ui.xml")
    interface AttachmentUiBinder extends UiBinder<Widget, AttachmentScreenUI> {
    };

    public static final AttachmentUiBinder                  uiBinder      = GWT.create(AttachmentUiBinder.class);

    protected AttachmentManager                             manager;

    protected HashMap<Integer, AttachmentManager>           managers;

    @UiField
    protected Calendar                                      createdDate;

    @UiField
    protected TextBox<String>                               description;

    @UiField
    protected Dropdown<Integer>                             querySection, tableSection, type;

    @UiField
    protected Button                                        searchButton, updateButton,
                    commitButton, abortButton;

    @UiField
    protected Tree                                          tree;

    protected ModulePermission                              userPermission;

    protected AttachmentScreenUI                            screen;

    protected FileDrop                                      fileDrop;

    protected DropMessage                                   dropMessage;

    protected Query                                         query;

    protected AsyncCallbackUI<ArrayList<AttachmentManager>> queryCall,
                    fetchUnattachedByDescriptionCall;

    protected AsyncCallbackUI<AttachmentManager>            fetchForUpdateCall, updateCall,
                    unlockCall;

    protected boolean                                       isNewSearch, isLoadedFromSearch,
                    isExternalSearch;

    protected static int                                    ROWS_PER_PAGE = 19;

    protected static String                                 ATTACHMENT_LEAF = "attachment",
                    ATTACHMENT_ITEM_LEAF = "attachmentItem", CLICK_FOR_MORE_LEAF = "clickForMore";

    public AttachmentScreenUI(WindowInt window) throws Exception {
        setWindow(window);

        userPermission = UserCache.getPermission().getModule("attachment");
        if (userPermission == null)
            throw new PermissionException(Messages.get().screenPermException("Attachment Screen"));

        initWidget(uiBinder.createAndBindUi(this));

        initialize();
        setState(QUERY);
        fireDataChange();

        logger.fine("Attachment Screen Opened");
    }

    protected void initialize() {
        Item<Integer> row;
        ArrayList<Item<Integer>> model;

        screen = this;

        /*
         * screen fields and widgets
         */
        addScreenHandler(description, AttachmentMeta.getDescription(), new ScreenHandler<String>() {
            public void onStateChange(StateChangeEvent event) {
                description.setEnabled(isState(QUERY, DISPLAY));
                description.setQueryMode(isState(QUERY, DISPLAY));
            }

            public Widget onTab(boolean forward) {
                return forward ? createdDate : abortButton;
            }
        });

        addScreenHandler(createdDate,
                         AttachmentMeta.getCreatedDate(),
                         new ScreenHandler<Datetime>() {
                             public void onStateChange(StateChangeEvent event) {
                                 createdDate.setEnabled(isState(QUERY, DISPLAY));
                                 createdDate.setQueryMode(isState(QUERY, DISPLAY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? querySection : description;
                             }
                         });

        addScreenHandler(querySection, AttachmentMeta.getSectionId(), new ScreenHandler<Integer>() {
            public void onStateChange(StateChangeEvent event) {
                querySection.setEnabled(isState(QUERY, DISPLAY));
                querySection.setQueryMode(isState(QUERY, DISPLAY));
            }

            public Widget onTab(boolean forward) {
                return forward ? searchButton : createdDate;
            }
        });

        addScreenHandler(searchButton, "searchButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                searchButton.setEnabled(isState(QUERY, DISPLAY));
            }

            public Widget onTab(boolean forward) {
                return forward ? tree : querySection;
            }
        });

        addScreenHandler(tree, "tree", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                tree.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? updateButton : searchButton;
            }
        });

        tree.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            @Override
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                Integer id;
                Node node, parent;

                if ( !isState(UPDATE))
                    return;

                /*
                 * in Update state, only the nodes that belong to the locked
                 * attachment can be selected
                 */
                node = tree.getNodeAt(event.getItem());

                if (ATTACHMENT_LEAF.equals(node.getType())) {
                    id = ((AttachmentDO)node.getData()).getId();
                } else if (ATTACHMENT_ITEM_LEAF.equals(node.getType())) {
                    parent = node.getParent();
                    id = ((AttachmentDO)parent.getData()).getId();
                } else {
                    id = null;
                }
                if ( !manager.getAttachment().getId().equals(id))
                    event.cancel();
            }
        });

        tree.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                Integer id;
                Node node, parent;

                node = tree.getNodeAt(event.getSelectedItem());
                if (ATTACHMENT_LEAF.equals(node.getType())) {
                    id = ((AttachmentDO)node.getData()).getId();
                } else if (ATTACHMENT_ITEM_LEAF.equals(node.getType())) {
                    parent = node.getParent();
                    id = ((AttachmentDO)parent.getData()).getId();
                } else {
                    loadNextPage();
                    id = null;
                }

                updateButton.setEnabled(ATTACHMENT_LEAF.equals(node.getType()) &&
                                        (isState(DISPLAY) && userPermission.hasUpdatePermission()));
                manager = id != null ? managers.get(id) : null;
                if (isExternalSearch && id != null)
                    displayAttachment(id);
            }
        });

        tree.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            @Override
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if ( !isState(UPDATE) || event.getCol() > 0)
                    event.cancel();
            }
        });

        tree.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                Object val;
                Node node;
                AttachmentDO data;

                val = tree.getValueAt(event.getRow(), event.getCol());
                node = tree.getNodeAt(tree.getSelectedNode());
                data = node.getData();
                switch (event.getCol()) {
                    case 0:
                        data.setDescription((String)val);
                        break;
                    case 2:
                        data.setSectionId((Integer)val);
                        break;
                }
            }
        });

        addScreenHandler(updateButton, "updateButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                updateButton.setEnabled(isState(UPDATE) && userPermission.hasUpdatePermission());
                if (isState(UPDATE)) {
                    updateButton.lock();
                    updateButton.setPressed(true);
                }
            }

            public Widget onTab(boolean forward) {
                return forward ? commitButton : tree;
            }
        });

        addShortcut(updateButton, 'u', CTRL);

        addScreenHandler(commitButton, "commitButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                commitButton.setEnabled(isState(UPDATE));
            }

            public Widget onTab(boolean forward) {
                return forward ? updateButton : abortButton;
            }
        });

        addShortcut(commitButton, 'm', CTRL);

        addScreenHandler(abortButton, "abortButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                abortButton.setEnabled(isState(UPDATE));
            }

            public Widget onTab(boolean forward) {
                return forward ? description : commitButton;
            }
        });

        addShortcut(abortButton, 'o', CTRL);

        fileDrop = new FileDrop(screen, "openelis/upload") {
            public void onDrop(DropEvent event) {
                if ( !fileDrop.isEnabled())
                    return;

                if (dropMessage != null) {
                    dropMessage.removeFromParent();
                    dropMessage = null;
                }

                super.onDrop(event);
            }

            public void onDropSuccess() {
                ArrayList<AttachmentManager> ams;
                AttachmentDO data;

                try {
                    setBusy(Messages.get().gen_saving());
                    /*
                     * create attachments for the newly uploaded files and get
                     * the managers for them
                     */
                    ams = AttachmentService.get().put();
                    if (managers == null)
                        managers = new HashMap<Integer, AttachmentManager>();

                    for (AttachmentManager am : ams)
                        managers.put(am.getAttachment().getId(), am);

                    setState(DISPLAY);
                    if (isLoadedFromSearch) {
                        /*
                         * the table is showing the attachments returned by a
                         * previous search, so they need to be cleared and only
                         * the attachment created from uploaded file is shown
                         */
                        loadTree(ams, true, false);
                        isLoadedFromSearch = false;
                    } else {
                        /*
                         * add the attachment created from uploaded file as the
                         * first one in the tree
                         */
                        loadTree(ams, false, false);
                    }
                    tree.selectNodeAt(0);
                    data = tree.getNodeAt(0).getData();
                    manager = managers.get(data.getId());
                    setDone(Messages.get().gen_savingComplete());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    clearStatus();
                }
            }
        };

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                fileDrop.setEnabled(isState(QUERY, DISPLAY));
            }
        });

        fileDrop.addDragEnterHandler(new DragEnterHandler() {
            @Override
            public void onDragEnter(DragEnterEvent event) {
                LayoutPanel panel;

                if (dropMessage == null) {
                    dropMessage = new DropMessage();
                    panel = (LayoutPanel)screen.getWidget();
                    panel.add(dropMessage);
                    panel.setWidgetTopBottom(dropMessage, 0, Unit.PX, 0, Unit.PX);
                }
            }
        });

        fileDrop.addDragLeaveHandler(new DragLeaveHandler() {
            @Override
            public void onDragLeave(DragLeaveEvent event) {
                if (dropMessage != null) {
                    dropMessage.removeFromParent();
                    dropMessage = null;
                }
            }
        });

        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("attachment_type")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }

        type.setModel(model);

        model = new ArrayList<Item<Integer>>();
        for (SectionDO s : SectionCache.getList())
            model.add(new Item<Integer>(s.getId(), s.getName()));

        querySection.setModel(model);
        tableSection.setModel(model);

        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                if (isState(UPDATE)) {
                    event.cancel();
                    setError(Messages.get().gen_mustCommitOrAbort());
                } else if (dropMessage != null) {
                    dropMessage.removeFromParent();
                    dropMessage = null;
                }
            }
        });
    }

    @UiHandler("searchButton")
    protected void search(ClickEvent event) {
        /*
         * query is a class variable because this screen doesn't use a screen
         * navigator but it needs to keep track of the previously executed query
         * and not a query created from the screen's current data
         */
        query = new Query();
        query.setFields(getQueryFields());
        query.setRowsPerPage(ROWS_PER_PAGE);
        
        isNewSearch = true;
        isLoadedFromSearch = true;
        managers = null;
        
        executeQuery(query);
    }
    
    public void showUnattached(String description) {
        QueryData field;

        query = new Query();
        field = new QueryData();
        field.setQuery(description);
        query.setFields(field);
        query.setRowsPerPage(ROWS_PER_PAGE);
        
        isNewSearch = true;
        isExternalSearch = true;
        managers = null;

        executeQuery(query);
    }

    /**
     * puts the screen in update state and loads it with a locked attachment.
     */
    @UiHandler("updateButton")
    protected void update(ClickEvent event) {
        /*
         * manager will be null if the user selected the node for next page
         */
        if (manager == null)
            return;

        setBusy(Messages.get().gen_lockForUpdate());

        if (fetchForUpdateCall == null) {
            fetchForUpdateCall = new AsyncCallbackUI<AttachmentManager>() {
                public void success(AttachmentManager result) {
                    Node node;

                    manager = result;
                    managers.put(manager.getAttachment().getId(), manager);
                    setState(UPDATE);

                    node = tree.getNodeAt(tree.getSelectedNode());
                    if (ATTACHMENT_ITEM_LEAF.endsWith(node.getType()))
                        node = node.getParent();
                    reloadAttachment(node);
                }

                public void failure(Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
                }

                public void finish() {
                    clearStatus();
                }
            };
        }

        AttachmentService.get().fetchForUpdate(manager.getAttachment().getId(), fetchForUpdateCall);
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

        setBusy(Messages.get().gen_updating());

        if (updateCall == null) {
            updateCall = new AsyncCallbackUI<AttachmentManager>() {
                public void success(AttachmentManager result) {
                    Node node;

                    manager = result;
                    managers.put(manager.getAttachment().getId(), manager);
                    setState(DISPLAY);

                    node = tree.getNodeAt(tree.getSelectedNode());
                    if (ATTACHMENT_ITEM_LEAF.endsWith(node.getType()))
                        node = node.getParent();
                    reloadAttachment(node);
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

        AttachmentService.get().update(manager, updateCall);
    }

    /**
     * Reverts any changes made to the data on the screen and disables editing
     * of the widgets. If the sample was locked calls the service method to
     * unlock it and loads the screen with that data.
     */
    @UiHandler("abortButton")
    protected void abort(ClickEvent event) {
        finishEditing();
        clearErrors();
        setBusy(Messages.get().gen_cancelChanges());

        if (isState(UPDATE)) {
            if (unlockCall == null) {
                unlockCall = new AsyncCallbackUI<AttachmentManager>() {
                    public void success(AttachmentManager result) {
                        Node node;

                        manager = result;
                        managers.put(manager.getAttachment().getId(), manager);
                        setState(DISPLAY);

                        node = tree.getNodeAt(tree.getSelectedNode());
                        if (ATTACHMENT_ITEM_LEAF.endsWith(node.getType()))
                            node = node.getParent();
                        reloadAttachment(node);
                        setDone(Messages.get().gen_updateAborted());
                    }

                    public void failure(Throwable e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        clearStatus();
                    }
                };
            }

            AttachmentService.get().unlock(manager.getAttachment().getId(), unlockCall);
        }
    }
    
    private void loadNextPage() {
        int page;

        /*
         * query is a class variable because this screen doesn't use a screen
         * navigator but it needs to keep track of the previously executed query
         * and not a query created from the screen's current data
         */
        page = query.getPage();

        query.setPage(page + 1);
        isNewSearch = false;
        executeQuery(query);
    }

    private void executeQuery(final Query query) {
        setBusy(Messages.get().gen_querying());

        if (queryCall == null) {
            queryCall = new AsyncCallbackUI<ArrayList<AttachmentManager>>() {
                public void success(ArrayList<AttachmentManager> result) {
                    AttachmentDO data;

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
                    loadTree(result, isNewSearch, true);
                    tree.selectNodeAt(0);
                    data = tree.getNodeAt(0).getData();
                    manager = managers.get(data.getId());
                    clearStatus();
                    displayAttachment(data.getId());
                }

                public void notFound() {
                    setState(QUERY);
                    loadTree(null, isNewSearch, true);
                    if (!isExternalSearch)
                        description.setFocus(true);
                    setDone(Messages.get().gen_noRecordsFound());
                }

                public void lastPage() {
                    int page;

                    /*
                     * make sure that the page doesn't stay one more than the
                     * current one, if there are no more pages in this direction
                     */
                    page = query.getPage();
                    if (page > 0)
                        query.setPage(page - 1);
                    setError(Messages.get().gen_noMoreRecordInDir());
                }

                public void failure(Throwable e) {
                    Window.alert("Error: Attachment call query failed; " + e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    setError(Messages.get().gen_queryFailed());
                }
            };
        }

        if (!isExternalSearch)
            AttachmentService.get().fetchByQuery(query.getFields(),
                                                 query.getPage() * query.getRowsPerPage(),
                                                 query.getRowsPerPage(),
                                                 queryCall);
        else
            AttachmentService.get()
            .fetchUnattachedByDescription(query.getFields().get(0).getQuery(),
                                          query.getPage() * query.getRowsPerPage(),
                                          query.getRowsPerPage(),
                                          queryCall);
    }

    private void showUnattached1(String description) {
        setBusy(Messages.get().gen_querying());

        if (fetchUnattachedByDescriptionCall == null) {
            fetchUnattachedByDescriptionCall = new AsyncCallbackUI<ArrayList<AttachmentManager>>() {
                public void success(ArrayList<AttachmentManager> result) {
                    AttachmentDO data;

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
                    loadTree(result, isNewSearch, true);
                    tree.selectNodeAt(0);
                    data = tree.getNodeAt(0).getData();
                    manager = managers.get(data.getId());
                    clearStatus();
                    displayAttachment(data.getId());
                }

                public void notFound() {
                    setState(DEFAULT);
                    loadTree(null, isNewSearch, true);
                    setDone(Messages.get().gen_noRecordsFound());
                }

                public void lastPage() {
                    int page;

                    /*
                     * make sure that the page doesn't stay one more than the
                     * current one, if there are no more pages in this direction
                     */
                    page = query.getPage();
                    if (page > 0)
                        query.setPage(page - 1);
                    setError(Messages.get().gen_noMoreRecordInDir());
                }

                public void failure(Throwable e) {
                    Window.alert("Error: Attachment call query failed; " + e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    setError(Messages.get().gen_queryFailed());
                }
            };
        }

        isNewSearch = true;
        isExternalSearch = true;
        query = new Query();
        query.setRowsPerPage(ROWS_PER_PAGE);

    }

    private void loadTree(ArrayList<AttachmentManager> ams, boolean reloadTree, boolean addAfter) {
        int sel, index;
        Node root, anode, cmnode;

        sel = tree.getSelectedNode();
        root = tree.getRoot();
        if (reloadTree || root == null) {
            /*
             * if no records were found by the query then empty the tree if this
             * was a new search as opposed to getting the next page
             */
            root = new Node();
            tree.setRoot(root);
            if (reloadTree && ams == null)
                return;
        }

        index = -1;

        if (addAfter)
            index = tree.getRoot().getChildCount() - 2;

        for (AttachmentManager am : ams) {
            anode = new Node(6);
            loadAttachment(anode, am);
            if (reloadTree)
                root.add(anode);
            else
                tree.addNodeAt( ++index, anode);
        }

        if (reloadTree) {
            /*
             * since this is a new search, add the node for showing the next
             * page and reload the tree with the new nodes
             */
            cmnode = new Node(1);
            cmnode.setCell(0, Messages.get().gen_clickForMore());
            cmnode.setType(CLICK_FOR_MORE_LEAF);
            root.add(cmnode);
            tree.setRoot(root);
        }

        /*
         * this needs to be done because tree.addNodeAt() gets rid of the
         * selection
         */
        if (sel > -1)
            tree.selectNodeAt(sel);
    }

    /**
     * Creates a subtree loaded from the passed manager; makes passed node the
     * attachment node and also the root of the subtree
     */
    private void loadAttachment(Node anode, AttachmentManager am) {
        int i;
        AttachmentDO data;
        Node inode;

        data = am.getAttachment();
        /*
         * the node for the attachment
         */
        anode.setCell(0, data.getDescription());
        anode.setCell(1, data.getCreatedDate());
        anode.setCell(2, data.getSectionId());
        anode.setCell(3, data.getTypeId());
        anode.setCell(4, data.getStorageReference());
        anode.setCell(5, data.getId());
        anode.setData(data);
        anode.setType(ATTACHMENT_LEAF);
        /*
         * the nodes for the attachment items
         */
        for (i = 0; i < am.item.count(); i++ ) {
            inode = new Node(1);
            inode.setCell(0, am.item.get(i).getReferenceDescription());
            inode.setType(ATTACHMENT_ITEM_LEAF);
            anode.add(inode);
        }
    }

    /**
     * Reloads the subtree rooted at the passed node from the latest data in the
     * manager for the currently selected attachment
     */
    private void reloadAttachment(Node node) {
        boolean isOpen;

        isOpen = node.isOpen();
        /*
         * this prevents any problems with trying to show the errors that were
         * added to the previous nodes and not to the latest ones added below
         */
        tree.clearExceptions();

        tree.close(node);
        node.removeAllChildren();
        loadAttachment(node, manager);

        /*
         * open the node in the new subtree if they were open in the old one
         */
        if (isOpen)
            tree.open(node);

        tree.refreshNode(node);
        tree.selectNodeAt(node);
    }

    private void displayAttachment(Integer id) {
        try {
            AttachmentUtil.displayAttachment(id);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}