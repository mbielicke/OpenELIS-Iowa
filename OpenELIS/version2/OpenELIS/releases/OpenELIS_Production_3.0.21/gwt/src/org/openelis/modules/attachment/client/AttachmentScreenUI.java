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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;

import org.openelis.cache.SectionCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AttachmentDO;
import org.openelis.domain.AttachmentIssueViewDO;
import org.openelis.domain.SectionDO;
import org.openelis.manager.AttachmentManager;
import org.openelis.meta.AttachmentMeta;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.EntityLockedException;
import org.openelis.ui.common.SectionPermission.SectionFlags;
import org.openelis.ui.common.SystemUserPermission;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Confirm;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.calendar.Calendar;
import org.openelis.ui.widget.fileupload.DropIndicator;
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
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class AttachmentScreenUI extends Screen {

    @UiTemplate("Attachment.ui.xml")
    interface AttachmentUiBinder extends UiBinder<Widget, AttachmentScreenUI> {
    };

    public static final AttachmentUiBinder                  uiBinder      = GWT.create(AttachmentUiBinder.class);

    protected AttachmentManager                             manager, previousManager;

    protected HashMap<Integer, AttachmentManager>           managers;

    protected ArrayList<AttachmentManager>                  deleteManagers;

    @UiField
    protected Calendar                                      createdDate;

    @UiField
    protected TextBox<String>                               description, issueText;

    @UiField
    protected Dropdown<Integer>                             querySection, tableSection;

    @UiField
    protected Tree                                          tree;

    @UiField
    protected Button                                        searchButton, attachButton,
                    updateButton, deleteButton, commitButton, abortButton;

    protected AttachmentScreenUI                            screen;

    protected FileDrop                                      fileDrop;

    protected DropIndicator                                 dropIndicator;

    protected Confirm                                       confirm;

    protected Query                                         query;

    protected AsyncCallbackUI<ArrayList<AttachmentManager>> queryCall;

    protected AsyncCallbackUI<AttachmentManager>            fetchForUpdateCall, updateCall,
                    unlockCall;

    protected AsyncCallback<Void>                           deleteCall;

    protected boolean                                       isNewQuery, isLoadedFromQuery,
                    closeHandlerAdded;

    protected static int                                    ROWS_PER_PAGE = 19;

    protected static String                                 ATTACHMENT_LEAF = "attachment",
                    ATTACHMENT_ITEM_LEAF = "attachmentItem", CLICK_FOR_MORE_LEAF = "clickForMore";

    public AttachmentScreenUI(WindowInt window) throws Exception {
        setWindow(window);

        initWidget(uiBinder.createAndBindUi(this));

        initialize();
        setState(QUERY);
        loadTree(null, true, true, false);

        logger.fine("Attachment Screen Opened");
    }

    public AttachmentScreenUI() throws Exception {
        initWidget(uiBinder.createAndBindUi(this));

        initialize();
        setState(QUERY);
        loadTree(null, true, true, false);

        logger.fine("Attachment Screen Opened");
    }

    protected void initialize() {
        ArrayList<Item<Integer>> model1, model2;

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
                return forward ? issueText : createdDate;
            }
        });
        
        addScreenHandler(issueText, AttachmentMeta.getIssueText(), new ScreenHandler<String>() {
            public void onStateChange(StateChangeEvent event) {
                issueText.setEnabled(isState(QUERY, DISPLAY));
                issueText.setQueryMode(isState(QUERY, DISPLAY));
            }

            public Widget onTab(boolean forward) {
                return forward ? searchButton : querySection;
            }
        });

        addScreenHandler(searchButton, "searchButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                searchButton.setEnabled(isState(QUERY, DISPLAY));
            }

            public Widget onTab(boolean forward) {
                return forward ? tree : issueText;
            }
        });

        addScreenHandler(tree, "tree", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                tree.setEnabled(true);
            }

            public Widget onTab(boolean forward) {
                return forward ? attachButton : searchButton;
            }
        });

        tree.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            @Override
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                Integer id;
                Node node;

                if ( !isState(UPDATE))
                    return;

                /*
                 * in Update state, only the nodes that belong to the locked
                 * attachment can be selected
                 */
                node = tree.getNodeAt(event.getItem());

                if (ATTACHMENT_LEAF.equals(node.getType()))
                    id = node.getData();
                else if (ATTACHMENT_ITEM_LEAF.equals(node.getType()))
                    id = node.getParent().getData();
                else
                    id = null;

                if ( !manager.getAttachment().getId().equals(id))
                    event.cancel();
            }
        });

        tree.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                nodeSelected(event.getSelectedItem());
            }
        });

        tree.addDoubleClickHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                if ( !isState(UPDATE, DELETE))
                    displayAttachment(tree.getNodeAt(tree.getSelectedNode()), null);
            }
        });

        tree.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            @Override
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                String name;
                Integer sectId;
                SystemUserPermission perm;
                Node node;

                node = tree.getNodeAt(tree.getSelectedNode());
                if ( !ATTACHMENT_LEAF.equals(node.getType()) || !isState(UPDATE) ||
                    (event.getCol() != 0 && event.getCol() != 2)) {
                    event.cancel();
                    return;
                }

                perm = UserCache.getPermission();
                sectId = manager.getAttachment().getSectionId();
                try {
                    if (sectId != null) {
                        /*
                         * allow changing any field of the attachment only if
                         * the user has complete permission to the current
                         * section
                         */
                        name = SectionCache.getById(sectId).getName();
                        if ( !perm.has(name, SectionFlags.COMPLETE)) {
                            event.cancel();
                            return;
                        }
                    }

                    /*
                     * make sure that the section can't be changed to one that
                     * the user doesn't have complete permission to
                     */
                    for (Item<Integer> row : tableSection.getModel()) {
                        name = SectionCache.getById(row.getKey()).getName();
                        row.setEnabled(perm.has(name, SectionFlags.COMPLETE));
                    }
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    event.cancel();
                }
            }
        });

        tree.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                Object val;
                AttachmentDO data;

                val = tree.getValueAt(event.getRow(), event.getCol());
                data = manager.getAttachment();
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

        tree.setAllowMultipleSelection(true);

        addScreenHandler(attachButton, "attachButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                attachButton.setEnabled(false);
            }

            public Widget onTab(boolean forward) {
                return forward ? updateButton : tree;
            }
        });

        addScreenHandler(updateButton, "updateButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                updateButton.setEnabled(isState(DISPLAY, UPDATE) && manager != null);
                if (isState(UPDATE)) {
                    updateButton.lock();
                    updateButton.setPressed(true);
                }
            }

            public Widget onTab(boolean forward) {
                return forward ? deleteButton : tree;
            }
        });

        addShortcut(updateButton, 'u', CTRL);

        addScreenHandler(deleteButton, "deleteButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                deleteButton.setEnabled(isState(DISPLAY, DELETE) && manager != null);
                if (isState(DELETE)) {
                    deleteButton.lock();
                    deleteButton.setPressed(true);
                }
            }

            public Widget onTab(boolean forward) {
                return forward ? commitButton : updateButton;
            }
        });

        addShortcut(deleteButton, 'd', CTRL);

        addScreenHandler(commitButton, "commitButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                commitButton.setEnabled(isState(UPDATE, DELETE));
            }

            public Widget onTab(boolean forward) {
                return forward ? deleteButton : abortButton;
            }
        });

        addShortcut(commitButton, 'm', CTRL);

        addScreenHandler(abortButton, "abortButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                abortButton.setEnabled(isState(UPDATE, DELETE));
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

                if (dropIndicator != null) {
                    dropIndicator.removeFromParent();
                    dropIndicator = null;
                }

                super.onDrop(event);
            }

            public void onDropSuccess() {
                ArrayList<AttachmentManager> ams;

                try {
                    /*
                     * don't let a file be dropped for upload if a record is
                     * locked
                     */
                    if (isState(UPDATE, DELETE))
                        return;
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
                    previousManager = null;
                    setState(DISPLAY);
                    if (isLoadedFromQuery) {
                        /*
                         * the tree is showing the attachments returned by a
                         * previous search, so they are cleared and only the
                         * attachment created from the uploaded file is shown
                         */
                        isLoadedFromQuery = false;
                        loadTree(ams, true, false, isLoadedFromQuery);
                    } else {
                        /*
                         * add the attachment created from uploaded file as the
                         * first one in the tree
                         */
                        loadTree(ams, false, false, isLoadedFromQuery);
                    }
                    tree.selectNodeAt(0);
                    nodeSelected(0);
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

                if (isState(UPDATE, DELETE) && !closeHandlerAdded && window != null) {
                    /*
                     * this handler is not added in initialize() because this
                     * screen can be shown in a modal window and in that case,
                     * the window is not available in initialize()
                     */
                    window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
                        public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                            if (isState(UPDATE, DELETE)) {
                                event.cancel();
                                setError(Messages.get().gen_mustCommitOrAbort());
                            } else if (dropIndicator != null) {
                                dropIndicator.removeFromParent();
                                dropIndicator = null;
                            }
                        }
                    });
                    closeHandlerAdded = true;
                }
            }
        });

        fileDrop.addDragEnterHandler(new DragEnterHandler() {
            @Override
            public void onDragEnter(DragEnterEvent event) {
                LayoutPanel panel;

                if (dropIndicator == null) {
                    dropIndicator = new DropIndicator();
                    panel = (LayoutPanel)screen.getWidget();
                    panel.add(dropIndicator);
                    panel.setWidgetTopBottom(dropIndicator, 0, Unit.PX, 0, Unit.PX);
                }
            }
        });

        fileDrop.addDragLeaveHandler(new DragLeaveHandler() {
            @Override
            public void onDragLeave(DragLeaveEvent event) {
                if (dropIndicator != null) {
                    dropIndicator.removeFromParent();
                    dropIndicator = null;
                }
            }
        });

        /*
         * here two separate models are created for the two dropdowns to make
         * sure that any items disabled or enabled in one don't affect the other
         */
        model1 = new ArrayList<Item<Integer>>();
        model2 = new ArrayList<Item<Integer>>();
        for (SectionDO s : SectionCache.getList()) {
            model1.add(new Item<Integer>(s.getId(), s.getName()));
            model2.add(new Item<Integer>(s.getId(), s.getName()));
        }

        querySection.setModel(model1);
        tableSection.setModel(model2);

        /*
         * call for locking and fetching attachments
         */
        if (fetchForUpdateCall == null) {
            fetchForUpdateCall = new AsyncCallbackUI<AttachmentManager>() {
                public void success(AttachmentManager result) {
                    Node node;

                    manager = result;
                    managers.put(manager.getAttachment().getId(), manager);
                    setState(UPDATE);

                    node = tree.getNodeAt(tree.getSelectedNode());
                    if (ATTACHMENT_ITEM_LEAF.equals(node.getType()))
                        node = node.getParent();
                    reloadAttachment(node);
                    tree.selectNodeAt(node);
                    tree.startEditing(tree.getSelectedNode(), 0);
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

        /*
         * call for updating an attachment
         */
        if (updateCall == null) {
            updateCall = new AsyncCallbackUI<AttachmentManager>() {
                public void success(AttachmentManager result) {
                    Node node;

                    manager = result;
                    managers.put(manager.getAttachment().getId(), manager);
                    setState(DISPLAY);

                    node = tree.getNodeAt(tree.getSelectedNode());
                    if (ATTACHMENT_ITEM_LEAF.equals(node.getType()))
                        node = node.getParent();
                    reloadAttachment(node);
                    tree.selectNodeAt(node);
                    nodeSelected(tree.getSelectedNode());
                    clearStatus();
                }

                public void validationErrors(ValidationErrorsList e) {
                    showErrors(e);
                }

                public void failure(Throwable e) {
                    Window.alert("commitUpdate(): " + e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
                    clearStatus();
                }
            };
        }

        /*
         * call for unlocking an attachment
         */
        if (unlockCall == null) {
            unlockCall = new AsyncCallbackUI<AttachmentManager>() {
                public void success(AttachmentManager result) {
                    Node node;

                    manager = result;
                    managers.put(manager.getAttachment().getId(), manager);
                    setState(DISPLAY);

                    node = tree.getNodeAt(tree.getSelectedNode());
                    if (ATTACHMENT_ITEM_LEAF.equals(node.getType()))
                        node = node.getParent();
                    reloadAttachment(node);
                    tree.selectNodeAt(node);
                    nodeSelected(tree.getSelectedNode());
                    setDone(Messages.get().gen_updateAborted());
                }

                public void failure(Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
                    clearStatus();
                }
            };
        }

        /*
         * call for deleting attachments
         */
        if (deleteCall == null) {
            deleteCall = new AsyncCallbackUI<Void>() {
                public void success(Void result) {
                    setState(DISPLAY);
                    setDone(Messages.get().gen_deletingComplete());
                }
                
                public void validationErrors(ValidationErrorsList e) {
                    showErrors(e);
                }

                public void failure(Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
                    clearStatus();
                }
            };
        }
    }

    @UiHandler("searchButton")
    protected void search(ClickEvent event) {
        search();
    }

    /**
     * Puts the screen in update state and loads it with a locked attachment.
     */
    @UiHandler("updateButton")
    protected void update(ClickEvent event) {
        Integer id;
        Node node;

        /*
         * manager will be null if the user selected the node for loading the
         * next page or if no node is selected
         */
        if (manager == null) {
            Window.alert(Messages.get().attachment_selectAnAttachment());
            return;
        }

        /*
         * allow update only if all selected nodes belong to the same attachment
         */
        id = null;
        for (int i : tree.getSelectedNodes()) {
            node = tree.getNodeAt(i);
            if (ATTACHMENT_ITEM_LEAF.equals(node.getType()))
                node = node.getParent();
            if (id == null) {
                id = node.getData();
            } else if ( !id.equals(node.getData())) {
                Window.alert(Messages.get().attachment_selectOneAttachment());
                return;
            }
        }

        setBusy(Messages.get().gen_lockForUpdate());
        AttachmentService.get().fetchForUpdate(manager.getAttachment().getId(), fetchForUpdateCall);
    }

    /**
     * Puts the screen in delete state and deletes the nodes for all selected
     * attachments
     */
    @UiHandler("deleteButton")
    protected void delete(ClickEvent event) {
        Integer id, selNodes[];
        String desc, name;
        AttachmentManager am;
        SystemUserPermission perm;
        Node node;
        ArrayList<Node> removeNodes;

        /*
         * manager will be null if the user selected the node for loading the
         * next page or if no node is selected
         */
        if (manager == null) {
            Window.alert(Messages.get().attachment_selectAnAttachment());
            return;
        }

        setState(DELETE);
        deleteManagers = new ArrayList<AttachmentManager>();
        removeNodes = new ArrayList<Node>();
        perm = UserCache.getPermission();

        setBusy(Messages.get().gen_lockForDelete());
        /*
         * go through the selected nodes and make a list of managers and nodes
         * for the attachments that can be deleted; the list of nodes is used to
         * remove the nodes in a separate loop; they're not removed in this loop
         * because that'll mess up the indexes that this loop depends on
         */
        selNodes = tree.getSelectedNodes();
        Arrays.sort(selNodes);
        for (Integer i : selNodes) {
            node = tree.getNodeAt(i);
            if (ATTACHMENT_ITEM_LEAF.equals(node.getType()))
                node = node.getParent();

            id = node.getData();
            /*
             * the id will be null if the user selected the node that says
             * "Click for more records"
             */
            if (id == null)
                continue;

            am = managers.get(id);
            desc = am.getAttachment().getDescription();
            /*
             * try to lock each selected node's attachment and issue and refresh
             * the node with the fetched data
             */
            try {
                am = AttachmentService.get().fetchForUpdate(id);
                AttachmentService.get().fetchIssueForUpdate(id);
                manager = am;
                managers.put(id, am);
                reloadAttachment(node);
                /*
                 * the attachment will be deleted if it and its issue could be
                 * locked, it isn't attached and the user has permission to
                 * delete it; if it has an issue, ask the user to confirm
                 * deletion
                 */
                desc = am.getAttachment().getDescription();
                if (am.item.count() > 0) {
                    Window.alert(Messages.get().attachment_cantDeleteAttachedException(desc));
                    unlockAndReloadAttachment(id, node);
                    continue;
                }

                name = SectionCache.getById(am.getAttachment().getSectionId()).getName();
                if ( !perm.has(name, SectionFlags.CANCEL)) {
                    Window.alert(Messages.get().attachment_deletePermException(desc));
                    unlockAndReloadAttachment(id, node);
                    continue;
                }

                if (am.getIssue() != null) {
                    if ( !Window.confirm(Messages.get().attachment_attachmentHasIssue(desc,
                                                                                      am.getIssue()
                                                                                        .getText()))) {
                        unlockAndReloadAttachment(id, node);
                        continue;
                    }
                }
                /*
                 * this attachment can be deleted
                 */
                deleteManagers.add(am);
                removeNodes.add(node);
            } catch (EntityLockedException e) {
                /*
                 * either the attachment or the issue couldn't be locked; unlock
                 * them both
                 */
                Window.alert(Messages.get()
                                     .attachment_attachmentLockException(desc,
                                                                         e.getUserName(),
                                                                         new Date(e.getExpires()).toString()));
                unlockAndReloadAttachment(id, node);
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
            }
        }

        /*
         * remove the nodes whose attachments can be deleted
         */
        if (removeNodes.size() > 0)
            for (Node n : removeNodes)
                tree.removeNode(n);

        /*
         * don't leave any nodes selected because the user may think that they
         * will get deleted when "Commit" is clicked, but they won't be; that's
         * because their attachments either can't be deleted possibly because
         * they're attached, or the user chose not to delete them because they
         * have issues
         */
        tree.unselectAll();
        manager = null;
        clearStatus();
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

        if (isState(UPDATE)) {
            setBusy(Messages.get().gen_updating());
            AttachmentService.get().update(manager, updateCall);
        } else if (isState(DELETE)) {
            if (deleteManagers == null || deleteManagers.size() == 0) {
                Window.alert(Messages.get().attachment_noAttachmentToDelete());
                return;
            }
            setBusy(Messages.get().gen_deleting());
            AttachmentService.get().delete(deleteManagers, deleteCall);
        }
    }

    /**
     * Reverts any changes made to the data on the screen and disables editing
     * of the widgets. If the attachment was locked, calls the service method to
     * unlock it and loads the screen with that data.
     */
    @UiHandler("abortButton")
    protected void abort(ClickEvent event) {
        int i, index;
        Integer insId, currId;
        Node root, insNode;

        finishEditing();
        clearErrors();
        setBusy(Messages.get().gen_cancelChanges());

        if (isState(UPDATE)) {
            AttachmentService.get().unlock(manager.getAttachment().getId(), unlockCall);
        } else if (isState(DELETE)) {
            /*
             * if some attachment nodes were deleted, unlock their attachments
             * and issues and re-insert them in the tree
             */
            if (deleteManagers != null) {
                /*
                 * the attachments in the tree are sorted in descending order of
                 * their ids; re-insert a deleted node right before the first
                 * node whose attachment id is less than the deleted node's
                 * attachment id
                 */
                root = tree.getRoot();
                while (deleteManagers.size() > 0) {
                    insNode = new Node(6);
                    insId = deleteManagers.get(0).getAttachment().getId();
                    unlockAndReloadAttachment(insId, insNode);
                    index = -1;
                    /*
                     * find the first attachment whose id is less than this
                     * attachment's
                     */
                    for (i = 0; i < root.getChildCount(); i++ ) {
                        currId = root.getChildAt(i).getData();
                        if (currId == null || insId > currId) {
                            index = i;
                            break;
                        }
                    }
                    /*
                     * either there are no nodes in the tree or this
                     * attachment's id is greater than all other nodes' ids;
                     * insert it at the end
                     */
                    if (index < 0)
                        index = root.getChildCount() == 0 ? 0 : root.getChildCount() - 1;
                    root.add(insNode, index);
                    deleteManagers.remove(0);
                }
                tree.setRoot(root);
                deleteManagers = null;
            }
            setState(DISPLAY);
            setDone(Messages.get().gen_deleteAborted());
        }
    }

    /**
     * If there are any attachments selected in the tree, then passes them to
     * the screen that brought attachment screen up and closes the window
     */
    @UiHandler("attachButton")
    protected void attach(ClickEvent event) {
        Integer currId, prevId, selNodes[];
        Node node;
        ArrayList<AttachmentDO> attachments;

        selNodes = tree.getSelectedNodes();
        prevId = null;
        attachments = new ArrayList<AttachmentDO>();
        for (int i = 0; i < selNodes.length; i++ ) {
            node = tree.getNodeAt(selNodes[i]);
            if (CLICK_FOR_MORE_LEAF.equals(node.getType()))
                break;
            /*
             * if an attachment item's node is selected then get the id of the
             * attachment from its parent node
             */
            if (ATTACHMENT_ITEM_LEAF.equals(node.getType()))
                node = node.getParent();

            currId = (Integer)node.getData();
            if ( !currId.equals(prevId)) {
                attachments.add(managers.get(currId).getAttachment());
                prevId = currId;
            }
        }

        attach(attachments);
        window.close();
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
    public void displayAttachment(Node node, String name) {
        if ( !ATTACHMENT_LEAF.equals(node.getType()))
            return;

        try {
            /*
             * passing the same name to displayAttachment makes sure that the
             * files open in the same window
             */
            AttachmentUtil.displayAttachment((Integer)node.getData(), name, window);
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
        query.setRowsPerPage(ROWS_PER_PAGE);
        isNewQuery = true;
        isLoadedFromQuery = true;
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
                    int index;
                    
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
                    index = isNewQuery ? index = 0 : tree.getRowCount()-1;
                    loadTree(result, isNewQuery, true, isLoadedFromQuery);
                    clearStatus();
                    tree.selectNodeAt(index);
                    nodeSelected(index);
                    searchSuccessful();
                }

                public void notFound() {
                    setState(QUERY);
                    loadTree(null, isNewQuery, true, isLoadedFromQuery);
                    description.setFocus(true);
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
                    Window.alert("Error: Attachment call query failed; " + e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    setError(Messages.get().gen_queryFailed());
                }
            };
        }

        AttachmentService.get().fetchByQueryDescending(query.getFields(),
                                                       query.getPage() * query.getRowsPerPage(),
                                                       query.getRowsPerPage(),
                                                       queryCall);
    }

    /**
     * Returns true if the screen was opened from another screen for adding new
     * attachments to the system and then attaching them to a record, like
     * sample. Otherwise returns false.
     */
    public boolean isAttach() {
        return false;
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

    /**
     * Performs specific actions based on node selected in the tree, specified
     * by the passed index. If an attachment or attachment item was selected
     * then resets the manager; otherwise tries to load the next page, because
     * "Click for Next.." is selected. Also, enables or disables widgets.
     */
    private void nodeSelected(int index) {
        Integer id;
        Node node;

        node = tree.getNodeAt(index);
        id = null;
        if (ATTACHMENT_LEAF.equals(node.getType()))
            id = node.getData();
        else if (ATTACHMENT_ITEM_LEAF.equals(node.getType()))
            id = node.getParent().getData();
        else if (isState(DISPLAY))
            loadNextPage();

        /*
         * id is null if the node "Click for more records" is selected; "Update"
         * button is enabled if only one node is selected; "Delete" button is
         * enabled even if multiple nodes are selected
         */
        if ( !isState(UPDATE))
            updateButton.setEnabled(id != null && (isState(DISPLAY)));
        if ( !isState(DELETE))
            deleteButton.setEnabled( (id != null || tree.getSelectedNodes().length > 1) &&
                                    (isState(DISPLAY)));
        /*
         * the "Attach" button shouldn't be enabled unless any attachment or
         * attachment item is selected; but once it is enabled it shouldn't be
         * disabled on selecting "Click for more..."; that's because the tree
         * supports multiple selection, and other nodes may be already selected
         */
        if (id != null)
            attachButton.setEnabled(isAttach() && (isState(DISPLAY)));
        manager = id != null ? managers.get(id) : null;
    }

    /**
     * Executes the previously run query to fetch the next page of results
     */
    private void loadNextPage() {
        int page;

        /*
         * query is a class variable because this screen doesn't use a screen
         * navigator but it needs to keep track of the previously executed query
         * and not a query created from the screen's current data
         */
        page = query.getPage();

        query.setPage(page + 1);
        isNewQuery = false;
        executeQuery(query);
    }

    /**
     * Loads the tree with the data in the passed managers. If "reloadTree" is
     * true then loads the tree from scratch, otherwise only adds new nodes; if
     * "addAfter" is true then adds the new nodes after the current nodes,
     * otherwise adds them at the beginning. If "isLoadedFromQuery" and
     * "reloadTree" are both true then adds the node for "Click For More...",
     * because the tree is getting loaded from the results of a fresh query.
     */
    private void loadTree(ArrayList<AttachmentManager> ams, boolean reloadTree, boolean addAfter,
                          boolean isLoadedFromQuery) {
        int sel, index;
        Node root, node;

        sel = tree.getSelectedNode();
        root = tree.getRoot();
        if (reloadTree || root == null) {
            /*
             * if no records were found by the query, empty the tree if this was
             * a new search and not getting the next page
             */
            root = new Node();
            tree.setRoot(root);
            if (reloadTree && ams == null)
                return;
        }

        index = -1;

        if (addAfter)
            /*
             * find the index of the last attachment node before the node for
             * "Click For More..."
             */
            index = tree.getRoot().getChildCount() - 2;

        /*
         * create and add the nodes for attachments and attachment items to the
         * tree; if "reloadTree" is true then the nodes are added to the root
         * because the tree will be reloaded, otherwise they're added after the
         * current nodes
         */
        if (ams != null) {
            for (AttachmentManager am : ams) {
                node = new Node(6);
                loadAttachment(node, am);
                if (reloadTree)
                    root.add(node);
                else
                    tree.addNodeAt( ++index, node);
            }
        }

        if (reloadTree) {
            if (isLoadedFromQuery) {
                /*
                 * add the node for "Click for more...", because the tree is
                 * getting reloaded and is showing the results of a query
                 */
                node = new Node(1);
                node.setCell(0, Messages.get().gen_clickForMore());
                node.setType(CLICK_FOR_MORE_LEAF);
                root.add(node);
            }
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
     * Creates a subtree loaded from the passed manager. Makes passed node the
     * attachment node and also the root of the subtree.
     */
    private void loadAttachment(Node anode, AttachmentManager am) {
        int i;
        AttachmentDO data;
        AttachmentIssueViewDO issue;
        Node inode;

        data = am.getAttachment();
        /*
         * the node for the attachment
         */
        issue = am.getIssue();
        anode.setCell(0, data.getDescription());
        anode.setCell(1, data.getCreatedDate());
        anode.setCell(2, data.getSectionId());
        anode.setCell(3, issue != null ? issue.getText() : null);
        anode.setCell(4, issue != null ? issue.getSystemUserLoginName() : null);
        anode.setCell(5, data.getId());
        anode.setData(data.getId());
        anode.setType(ATTACHMENT_LEAF);
        /*
         * the nodes for the attachment items; these nodes are of type
         * AttachmentNode and not Node because otherwise they won't have the
         * blank icon and so the alignment of the text won't be like the nodes
         * at the top level
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
    }

    /**
     * Unlocks the attachment with the passed id and its issue; loads the passed
     * node with the latest data for the attachment from the database
     */
    private void unlockAndReloadAttachment(Integer attachmentId, Node node) {
        AttachmentManager am;

        try {
            am = AttachmentService.get().unlock(attachmentId);
            AttachmentService.get().unlockIssue(attachmentId);
            managers.put(attachmentId, am);
            node.removeAllChildren();
            loadAttachment(node, am);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
        }
    }
}