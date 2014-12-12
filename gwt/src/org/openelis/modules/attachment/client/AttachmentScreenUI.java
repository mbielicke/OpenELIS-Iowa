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
import org.openelis.modules.main.client.resources.OpenELISResources;
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
import org.openelis.ui.widget.CheckBox;
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
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class AttachmentScreenUI extends Screen {

    @UiTemplate("Attachment.ui.xml")
    interface AttachmentUiBinder extends UiBinder<Widget, AttachmentScreenUI> {
    };

    public static final AttachmentUiBinder                  uiBinder      = GWT.create(AttachmentUiBinder.class);

    protected AttachmentManager                             manager, previousManager;

    protected HashMap<Integer, AttachmentManager>           managers;

    @UiField
    protected Calendar                                      createdDate;

    @UiField
    protected TextBox<String>                               description;

    @UiField
    protected Dropdown<Integer>                             querySection, tableSection, type;

    @UiField
    protected Tree                                          tree;

    @UiField
    protected Button                                        searchButton, attachButton,
                    updateButton, commitButton, abortButton;

    @UiField
    protected CheckBox                                      autoSelectNext;

    protected AttachmentScreenUI                            screen;

    protected FileDrop                                      fileDrop;

    protected DropIndicator                                 dropIndicator;

    protected Confirm                                       confirm;

    protected Query                                         query;

    protected AsyncCallbackUI<ArrayList<AttachmentManager>> queryCall;

    protected AsyncCallbackUI<AttachmentManager>            fetchForUpdateCall, updateCall,
                    unlockCall;

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
                return forward ? attachButton : searchButton;
            }
        });

        tree.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            @Override
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                Integer id;
                Node node;

                if (isState(RESERVED)) {
                    /*
                     * if an attachment is reserved then no other nodes can be
                     * selected
                     */
                    event.cancel();
                    return;
                }

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
                if (isDataEntry())
                    displayAttachment(tree.getNodeAt(event.getSelectedItem()),
                                      Messages.get().attachment_attachment());
            }
        });

        tree.addDoubleClickHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                if ( !isState(UPDATE))
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
                         * the user has assign permission to the current section
                         */
                        name = SectionCache.getById(sectId).getName();
                        if ( !perm.has(name, SectionFlags.ASSIGN)) {
                            event.cancel();
                            return;
                        }
                    }

                    /*
                     * make sure that the section can't be changed to one that
                     * the user doesn't have assign permission to
                     */
                    for (Item<Integer> row : tableSection.getModel()) {
                        name = SectionCache.getById(row.getKey()).getName();
                        row.setEnabled(perm.has(name, SectionFlags.ASSIGN));
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

        tree.setAllowMultipleSelection(isAttach());

        addScreenHandler(attachButton, "attachButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                attachButton.setEnabled(false);
            }

            public Widget onTab(boolean forward) {
                return forward ? autoSelectNext : tree;
            }
        });

        addScreenHandler(autoSelectNext, "autoSelectNext", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                autoSelectNext.setEnabled(isState(DISPLAY) && isDataEntry());
            }

            public Widget onTab(boolean forward) {
                return forward ? updateButton : tree;
            }
        });

        if (isDataEntry())
            autoSelectNext.setValue("Y");

        addScreenHandler(updateButton, "updateButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                updateButton.setEnabled(isState(DISPLAY, UPDATE));
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
                    if (isState(UPDATE, RESERVED))
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
                         * the table is showing the attachments returned by a
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
                    if (isDataEntry())
                        displayAttachment(tree.getNodeAt(0), Messages.get().attachment_attachment());
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

                if (isState(UPDATE, RESERVED) && !closeHandlerAdded && window != null) {
                    /*
                     * this handler is not added in initialize() because this
                     * screen can be shown in a modal window and in that case,
                     * the window is not available in initialize()
                     */
                    window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
                        public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                            if (isState(UPDATE, RESERVED)) {
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

        addScreenHandler(attachButton, "attachButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                attachButton.setEnabled(false);
            }

            public Widget onTab(boolean forward) {
                return forward ? updateButton : abortButton;
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
    }

    @UiHandler("searchButton")
    protected void search(ClickEvent event) {
        search();
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
                    nodeSelected(tree.getSelectedNode());
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
     * of the widgets. If the attachment was locked, calls the service method to
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
                        nodeSelected(tree.getSelectedNode());
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
                    loadTree(result, isNewQuery, true, isLoadedFromQuery);
                    clearStatus();
                    tree.selectNodeAt(0);
                    nodeSelected(0);
                    searchSuccessful();
                    if (isDataEntry())
                        displayAttachment(tree.getNodeAt(0), Messages.get().attachment_attachment());
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

        if (isDataEntry())
            AttachmentService.get()
                             .fetchUnattachedByDescription(query.getFields().get(0).getQuery(),
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
     * If "auto select next" is checked then reserves the next attachment that
     * can be reserved and returns its manager. Otherwise, reserves the
     * currently selected attachment and returns its manager.
     */
    public AttachmentManager getReserved() {
        AttachmentManager am;
        Node node, next, prevSelNode;
        AttachmentNode anode;

        node = tree.getNodeAt(tree.getSelectedNode());
        if (node == null)
            return null;

        if (isState(UPDATE)) {
            /*
             * don't allow reserving an attachment if an attachment is being
             * updated
             */
            setError(Messages.get().gen_mustCommitOrAbort());
            return null;
        }

        if (ATTACHMENT_ITEM_LEAF.equals(node.getType())) {
            node = node.getParent();
        } else if (CLICK_FOR_MORE_LEAF.equals(node.getType())) {
            /*
             * no attachment or attachment item was selected
             */
            return null;
        }

        setBusy(Messages.get().gen_lockForUpdate());
        /*
         * if "auto select next" is checked then try to find the next manager
         * that can be reserved and return it; otherwise return the currently
         * selected manager
         */
        am = null;
        anode = (AttachmentNode)node;
        if ("Y".equals(autoSelectNext.getValue())) {
            /*
             * if previousManager is not null then an attachment was reserved
             * before, so try to reserve the next node's attachment; otherwise
             * try to reserve the current node's attachment
             */
            if (previousManager != null) {
                next = anode.nextSibling();
                /*
                 * go to the next node only if there are any attachments left in
                 * the tree, otherwise try to reserve the current node
                 */
                if (next != null && ATTACHMENT_LEAF.equals(next.getType())) {
                    anode.setStatus(AttachmentNode.Status.ATTACHED);
                    tree.refreshNode(anode);
                    node = next;
                }
            }
            while (node != null && ATTACHMENT_LEAF.equals(node.getType())) {
                anode = (AttachmentNode)node;
                try {
                    prevSelNode = tree.getNodeAt(tree.getSelectedNode());
                    am = reserve(anode);
                    /*
                     * show the attachment linked to the currently selected node
                     * but only if the node wasn't already selected
                     */
                    if (anode != prevSelNode)
                        displayAttachment(anode, Messages.get().attachment_attachment());
                    break;
                } catch (EntityLockedException e) {
                    /*
                     * mark the current node as locked by someone else and go to
                     * the next node and try to reserve its attachment
                     */
                    anode.setStatus(AttachmentNode.Status.LOCKED_BY_OTHER);
                    tree.refreshNode(anode);
                    node = anode.nextSibling();
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        } else {
            /*
             * reserve the currently selected attachment
             */
            anode = (AttachmentNode)node;
            try {
                am = reserve(anode);
            } catch (EntityLockedException e) {
                /*
                 * mark the current node as locked by someone else
                 */
                anode.setStatus(AttachmentNode.Status.LOCKED_BY_OTHER);
                tree.refreshNode(anode);
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        clearStatus();

        return am;
    }

    /**
     * Removes the reservation from the last attachment that was reserved. If
     * the flag is true then the reserved attachment was successfully attached
     * to the other record e.g. sample and if "auto select next" is checked then
     * an attachment after the current one will be reserved, otherwise the
     * current attachment will be reserved because it was not attached.
     */
    public void removeReservation(boolean isAttached) {
        Integer id;
        Node node;
        AttachmentNode anode;

        node = tree.getNodeAt(tree.getSelectedNode());
        if (node == null)
            return;

        if (ATTACHMENT_ITEM_LEAF.equals(node.getType())) {
            node = node.getParent();
        } else if (CLICK_FOR_MORE_LEAF.equals(node.getType())) {
            /*
             * no attachment or attachment item was selected
             */
            return;
        }

        setBusy(Messages.get().gen_cancelChanges());
        anode = (AttachmentNode)node;
        try {
            id = anode.getData();
            manager = AttachmentService.get().unlock(id);
            managers.put(id, manager);
            if (isAttached) {
                previousManager = manager;
                anode.setStatus(AttachmentNode.Status.ATTACHED);
            } else {
                anode.setStatus(AttachmentNode.Status.UNATTACHED);
                previousManager = null;
            }
            setState(DISPLAY);
            reloadAttachment(anode);
            setDone(Messages.get().gen_updateAborted());
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            clearStatus();
        }
    }

    /**
     * Returns true if the screen was opened from another screen for data entry
     * , otherwise returns false
     */
    public boolean isDataEntry() {
        return false;
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
     * Reserves and returns the manager for the attachment showing on the passed
     * node and refreshes the tree. Throws exception if there was an issue with
     * reserving the attachment.
     */
    private AttachmentManager reserve(AttachmentNode node) throws Exception {
        Integer id;

        id = node.getData();
        manager = AttachmentService.get().getReserved(id);
        previousManager = manager;
        /*
         * the attachment is now reserved for this user, so refresh the screen
         * and display the attachment
         */
        managers.put(id, manager);
        setState(RESERVED);
        tree.selectNodeAt(node);
        node.setStatus(AttachmentNode.Status.LOCKED_BY_SELF);
        reloadAttachment(node);
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

        node = tree.getNodeAt(index);
        if (ATTACHMENT_LEAF.equals(node.getType())) {
            id = node.getData();
        } else if (ATTACHMENT_ITEM_LEAF.equals(node.getType())) {
            id = node.getParent().getData();
        } else {
            loadNextPage();
            id = null;
        }

        if ( !isState(UPDATE))
            /*
             * in update state, the Update button can't be disabled
             */
            updateButton.setEnabled(id != null && (isState(DISPLAY)));
        /*
         * the "Attach" button shouldn't be enabled unless any attachment or
         * attachment item is selected but once it is enabled it shouldn't be
         * disabled on selecting "Click for more..." because the tree supports
         * multiple selection, and other nodes may be already selected
         */
        if ( !CLICK_FOR_MORE_LEAF.equals(node.getType()))
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
        for (AttachmentManager am : ams) {
            node = new AttachmentNode(6);
            loadAttachment(node, am);
            if (reloadTree)
                root.add(node);
            else
                tree.addNodeAt( ++index, node);
        }

        if (reloadTree) {
            if (isLoadedFromQuery) {
                /*
                 * add the node for "Click for more...", because the tree is
                 * getting reloaded and is showing the results of a query; this
                 * node is of type AttachmentNode and not Node because otherwise
                 * it won't have the blank icon and so the alignment of the text
                 * won't be like the other nodes at the top level
                 */
                node = new AttachmentNode(1);
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
        AttachmentNode inode;

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
        anode.setData(data.getId());
        anode.setType(ATTACHMENT_LEAF);
        /*
         * the nodes for the attachment items; these nodes are of type
         * AttachmentNode and not Node because otherwise they won't have the
         * blank icon and so the alignment of the text won't be like the nodes
         * at the top level
         */
        for (i = 0; i < am.item.count(); i++ ) {
            inode = new AttachmentNode(1);
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

    /**
     * This class allows changing the image shown on the node for attachment
     * depending upon the various operations being performed for that attachment
     * e.g. locked by other or attached etc.
     */
    private static class AttachmentNode extends Node {
        enum Status {
            LOCKED_BY_OTHER, LOCKED_BY_SELF, ATTACHED, UNATTACHED
        }

        Status status;

        AttachmentNode(int columns) {
            super(columns);
            status = Status.UNATTACHED;
        }

        public String getImage() {
            switch (status) {
                case LOCKED_BY_OTHER:
                    return OpenELISResources.INSTANCE.icon().lockedByOtherIcon();
                case LOCKED_BY_SELF:
                    return OpenELISResources.INSTANCE.icon().lockedBySelfIcon();
                case ATTACHED:
                    return OpenELISResources.INSTANCE.icon().attachedIcon();
                default:
                    return OpenELISResources.INSTANCE.icon().blankIcon();
            }
        }

        void setStatus(Status status) {
            this.status = status;
        }
    }
}