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
package org.openelis.modules.secondDataEntry.client;

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.Screen.ShortKeys.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AttachmentItemViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.SecondDataEntryVO;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.attachment.client.AttachmentUtil;
import org.openelis.modules.sample1.client.SampleService1;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.tree.Node;
import org.openelis.ui.widget.tree.Tree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Widget;

public class SecondDataEntryScreenUI extends Screen {

    @UiTemplate("SecondDataEntry.ui.xml")
    interface SecondDataEntryUiBinder extends UiBinder<Widget, SecondDataEntryScreenUI> {
    };

    public static final SecondDataEntryUiBinder             uiBinder      = GWT.create(SecondDataEntryUiBinder.class);

    protected ModulePermission                              userPermission;

    @UiField
    protected Tree                                          tree;

    @UiField
    protected Button                                        queryButton, nextButton,
                    previousButton, updateButton, commitButton, abortButton, nextPageButton;

    @UiField
    protected TextBox<Integer>                              accessionNumber;

    @UiField
    protected Dropdown<Integer>                             historySystemUserId;

    @UiField
    protected DeckPanel                                     deckPanel;

    @UiField(provided = true)
    protected EnvironmentalTabUI                            environmentalTab;

    // @UiField
    // protected FlexTable widgetTable;

    protected SampleManager1                                manager;

    protected HashMap<Integer, SampleManager1>              managers;

    protected SecondDataEntryScreenUI                       screen;

    protected Screen                                        sampleWidgetScreen;

    protected SecondDataEntryServiceImpl                    service       = SecondDataEntryServiceImpl.INSTANCE;

    protected AsyncCallbackUI<ArrayList<SecondDataEntryVO>> queryCall;

    protected AsyncCallbackUI<SampleManager1>               fetchForUpdateCall, updateCall,
                    unlockCall;

    protected AsyncCallbackUI<ArrayList<SampleManager1>>    fetchByIdsCall;

    protected Query                                         query;

    protected String                                        envXML, domain;

    protected Widget                                        focusedWidget;

    protected HashMap<String, ArrayList<Item>>              models;

    protected HashMap<String, Integer>                      numEdits;

    protected static int                                    ROWS_PER_PAGE = 23;

    protected static final String                           SAMPLE_USER_LEAF = "sampleUser",
                    ATTACHMENT_LEAF = "attachment", REPORT_TO = "report_to", BILL_TO = "bill_to";

    protected static final SampleManager1.Load              fetchElements[]  = {SampleManager1.Load.ATTACHMENT},
                    updateElements[] = {SampleManager1.Load.AUXDATA,
                    SampleManager1.Load.ORGANIZATION, SampleManager1.Load.PROJECT,
                    SampleManager1.Load.RESULT, SampleManager1.Load.ATTACHMENT,
                    SampleManager1.Load.EORDER, SampleManager1.Load.PROVIDER};

    protected enum Type {
        INTEGER, DOUBLE, STRING
    }

    public SecondDataEntryScreenUI(WindowInt window) throws Exception {
        setWindow(window);

        userPermission = UserCache.getPermission().getModule("sampletracking");
        if (userPermission == null)
            throw new PermissionException(Messages.get()
                                                  .gen_screenPermException("Second Data Entry"));

        environmentalTab = new EnvironmentalTabUI(this);

        initWidget(uiBinder.createAndBindUi(this));

        initialize();
        setData();
        setState(DEFAULT);
        fireDataChange();

        logger.fine("Second Data Entry Screen Opened");
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        ArrayList<Item<Integer>> model;
        ArrayList<SystemUserVO> users;

        screen = this;

        //
        // button panel buttons
        //
        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                queryButton.setEnabled(isState(QUERY, DEFAULT, DISPLAY) &&
                                       userPermission.hasSelectPermission());
                if (isState(QUERY)) {
                    queryButton.lock();
                    queryButton.setPressed(true);
                }
            }
        });

        addShortcut(queryButton, 'q', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                previousButton.setEnabled(isState(DISPLAY));
            }
        });

        addShortcut(previousButton, 'p', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                nextButton.setEnabled(isState(DISPLAY));
            }
        });

        addShortcut(nextButton, 'n', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                updateButton.setEnabled(isState(UPDATE, DISPLAY) &&
                                        userPermission.hasUpdatePermission());
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

        /*
         * screen fields and widgets
         */
        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                tree.setEnabled(true);
            }
        });

        tree.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            @Override
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                Integer sampleId;
                Node node;
                AttachmentItemViewDO att;

                if ( !isState(UPDATE))
                    return;

                /*
                 * in Update state, only the nodes that belong to the locked
                 * sample can be selected
                 */
                node = tree.getNodeAt(event.getItem());
                if (SAMPLE_USER_LEAF.equals(node.getType())) {
                    sampleId = node.getData();
                } else {
                    att = node.getData();
                    sampleId = att.getReferenceId();
                }
                if ( !manager.getSample().getId().equals(sampleId))
                    event.cancel();
            }
        });

        tree.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                nodeSelected(tree.getNodeAt(event.getSelectedItem()));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                nextPageButton.setEnabled(isState(DISPLAY));
            }
        });

        addScreenHandler(accessionNumber,
                         SampleMeta.getAccessionNumber(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 accessionNumber.setValue(null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 accessionNumber.setEnabled(isState(QUERY));
                                 accessionNumber.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? historySystemUserId : historySystemUserId;
                             }
                         });

        addScreenHandler(historySystemUserId,
                         SampleMeta.getHistorySystemUserId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 historySystemUserId.setValue(null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 historySystemUserId.setEnabled(isState(QUERY));
                                 historySystemUserId.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? accessionNumber : accessionNumber;
                             }
                         });

        addScreenHandler(environmentalTab, "environmentalTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                environmentalTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                environmentalTab.setState(event.getState());
            }
        });

        //
        // screen fields
        //
        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                if (isState(ADD, UPDATE)) {
                    event.cancel();
                    setError(Messages.get().gen_mustCommitOrAbort());
                }
            }
        });

        try {
            model = new ArrayList<Item<Integer>>();
            users = UserCache.getEmployees(QueryFieldUtil.parseAutocomplete("*"));
            for (SystemUserVO user : users)
                model.add(new Item<Integer>(user.getId(), user.getLoginName()));
            historySystemUserId.setModel(model);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            Window.alert(e.getMessage());
        }
    }

    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    /*
     * basic button methods
     */

    /**
     * Puts the screen in query state, sets the manager to null
     */
    @UiHandler("queryButton")
    protected void query(ClickEvent event) {
        manager = null;
        managers = null;

        setData();
        setState(QUERY);
        tree.setRoot(getRoot(null));
        domain = null;
        fireDataChange();
        accessionNumber.setFocus(true);
        setDone(Messages.get().gen_enterFieldsToQuery());
    }

    @UiHandler("previousButton")
    protected void previous(ClickEvent event) {
        int index;
        Node node, root, prevNode;

        node = tree.getNodeAt(tree.getSelectedNode());
        if (ATTACHMENT_LEAF.equals(node.getType()))
            node = node.getParent();

        root = tree.getRoot();
        index = root.getIndex(node);
        if (index == 0) {
            setError(Messages.get().gen_noMoreRecordInDir());
        } else {
            prevNode = root.getChildAt(index - 1);
            tree.selectNodeAt(prevNode);
            nodeSelected(prevNode);
        }
    }

    @UiHandler("nextButton")
    protected void next(ClickEvent event) {
        int index;
        Node node, root, prevNode;

        node = tree.getNodeAt(tree.getSelectedNode());
        if (ATTACHMENT_LEAF.equals(node.getType()))
            node = node.getParent();

        root = tree.getRoot();
        index = root.getIndex(node);
        if (index < root.getChildCount() - 1) {
            prevNode = root.getChildAt(index + 1);
            tree.selectNodeAt(prevNode);
            nodeSelected(prevNode);
        } else {
            loadNextPage();
        }
    }

    @UiHandler("updateButton")
    protected void update(ClickEvent event) {
        setBusy(Messages.get().lockForUpdate());

        if (fetchForUpdateCall == null) {
            fetchForUpdateCall = new AsyncCallbackUI<SampleManager1>() {
                public void success(SampleManager1 result) {
                    Focusable f;

                    manager = result;
                    managers.put(manager.getSample().getId(), manager);
                    /*
                     * this keeps track of how many times a widget with a given
                     * key was edited
                     */
                    numEdits = new HashMap<String, Integer>();

                    /*
                     * this makes sure that if the domain of the locked sample
                     * is different from when it was fetched, the fields for
                     * verification are redrawn
                     */
                    addWidgets(manager.getSample().getDomain());

                    setState(UPDATE);
                    fireDataChange();
                    /*
                     * if (widgetTable.getRowCount() > 0 &&
                     * widgetTable.getCellCount(0) > 0) { f =
                     * (Focusable)widgetTable.getWidget(0, 0); f.setFocus(true);
                     * }
                     */
                }

                public void failure(Throwable e) {
                    logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
                    Window.alert(e.getMessage());
                }

                public void finish() {
                    clearStatus();
                }
            };
        }

        SampleService1.get().fetchForUpdate(manager.getSample().getId(),
                                            updateElements,
                                            fetchForUpdateCall);
    }

    @UiHandler("commitButton")
    protected void commit(ClickEvent event) {
        commit();
    }

    private void commit() {
        Validation validation;

        finishEditing();

        validation = validate();
        if (Validation.Status.ERRORS.equals(validation.getStatus())) {
            setError(Messages.get().gen_correctErrors());
            return;
        }

        switch (state) {
            case QUERY:
                commitQuery();
                break;
            case ADD:
                commitUpdate();
                break;
            case UPDATE:
                commitUpdate();
                break;
        }
    }

    protected void commitQuery() {
        query = new Query();
        query.setFields(getQueryFields());
        query.setRowsPerPage(ROWS_PER_PAGE);
        executeQuery(query);
    }

    protected void commitUpdate() {
        if (isState(ADD))
            setBusy(Messages.get().gen_adding());
        else
            setBusy(Messages.get().gen_updating());

        if (updateCall == null) {
            updateCall = new AsyncCallbackUI<SampleManager1>() {
                public void success(SampleManager1 result) {
                    manager = result;
                    managers.put(manager.getSample().getId(), manager);
                    setData();
                    setState(DISPLAY);
                    fireDataChange();
                    clearStatus();
                }

                public void validationErrors(ValidationErrorsList e) {
                    showErrors(e);
                }

                public void failure(Throwable e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    if (isState(ADD))
                        Window.alert("commitAdd(): " + e.getMessage());
                    else
                        Window.alert("commitUpdate(): " + e.getMessage());
                    clearStatus();
                }
            };
        }

        SampleService1.get().update(manager, true, updateCall);
    }

    @UiHandler("abortButton")
    protected void abort(ClickEvent event) {
        finishEditing();
        clearErrors();
        setBusy(Messages.get().gen_cancelChanges());

        if (isState(QUERY)) {
            try {
                setData();
                setState(DEFAULT);
                fireDataChange();
                setDone(Messages.get().gen_queryAborted());
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                Window.alert(e.getMessage());
                clearStatus();
            }
        } else if (isState(UPDATE)) {
            if (unlockCall == null) {
                unlockCall = new AsyncCallbackUI<SampleManager1>() {
                    public void success(SampleManager1 result) {
                        manager = result;
                        managers.put(manager.getSample().getId(), manager);
                        setData();
                        setState(DISPLAY);
                        fireDataChange();
                        setDone(Messages.get().gen_updateAborted());
                    }

                    public void failure(Throwable e) {
                        setData();
                        setState(DEFAULT);
                        fireDataChange();
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        Window.alert(e.getMessage());
                        clearStatus();
                    }
                };
            }

            SampleService1.get().unlock(manager.getSample().getId(), fetchElements, unlockCall);
        }
    }

    /**
     * Executes the user query to get the next page of results
     */
    @UiHandler("nextPageButton")
    protected void nextPage(ClickEvent event) {
        loadNextPage();
    }
    
    /**
     * Sets the latest manager in the tabs
     */
    private void setData() {
        environmentalTab.setData(manager);
    }

    /**
     * Executes the passed query and loads the screen with the results
     */
    private void executeQuery(final Query query) {
        setBusy(Messages.get().gen_querying());

        if (queryCall == null) {
            queryCall = new AsyncCallbackUI<ArrayList<SecondDataEntryVO>>() {
                public void success(ArrayList<SecondDataEntryVO> result) {
                    int index;
                    Node root, first, last;
                    HashSet<Integer> ids;

                    /*
                     * if "managers" is null, it's a new query i.e. the first
                     * page will be loaded in the tree; otherwise, the next page
                     * will be loaded after all the previous pages
                     */
                    if (query.getPage() == 0) {
                        index = 0;
                        root = getRoot(result);
                    } else {
                        root = tree.getRoot();
                        index = root.getChildCount();
                        loadSamples(root, result);
                    }

                    /*
                     * reload the tree; select the first node of the newest page
                     * and load the screen with its sample's data; make sure
                     * that only the nodes of the newest page are in the visible
                     * area
                     */
                    tree.setRoot(root);
                    first = root.getChildAt(index);
                    last = root.getLastChild();
                    tree.selectNodeAt(first);
                    tree.scrollToVisible(tree.getNodeViewIndex(last));

                    ids = new HashSet<Integer>();
                    for (SecondDataEntryVO data : result)
                        ids.add(data.getSampleId());
                    fetchSamples(DataBaseUtil.toArrayList(ids));
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

                public void notFound() {
                    manager = null;
                    setData();
                    setState(DEFAULT);
                    fireDataChange();
                    setDone(Messages.get().gen_noRecordsFound());
                }

                public void failure(Throwable e) {
                    Window.alert("Error: Second Data Entry call query failed; " + e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    setError(Messages.get().gen_queryFailed());
                }
            };
        }

        service.query(query, queryCall);
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
        executeQuery(query);
    }

    private void fetchSamples(ArrayList<Integer> ids) {
        if (fetchByIdsCall == null) {
            fetchByIdsCall = new AsyncCallbackUI<ArrayList<SampleManager1>>() {
                public void success(ArrayList<SampleManager1> result) {
                    Node first;

                    if (managers == null)
                        managers = new HashMap<Integer, SampleManager1>();

                    /*
                     * this map is used to link a tree node with the manager
                     * containing the sample and analysis that the node is
                     * showing
                     */
                    for (SampleManager1 sm : result)
                        managers.put(sm.getSample().getId(), sm);

                    first = tree.getNodeAt(tree.getSelectedNode());
                    loadAttachments(tree.getSelectedNode());

                    setState(DISPLAY);
                    fireDataChange();
                    clearStatus();
                    nodeSelected(first);
                }

                public void notFound() {
                    manager = null;
                    setData();
                    setState(DEFAULT);
                    fireDataChange();
                    setDone(Messages.get().gen_noRecordsFound());
                }

                public void failure(Throwable e) {
                    Window.alert("Error: Second Data Entry call query failed; " + e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    setError(Messages.get().gen_queryFailed());
                }
            };
        }

        SampleService1.get().fetchByIds(ids, fetchElements, fetchByIdsCall);
    }

    /**
     * Creates a tree loaded from the passed list where every sample in the
     * passed list has a different subtree and returns the root node
     */
    private Node getRoot(ArrayList<SecondDataEntryVO> list) {
        Node root;

        root = new Node();
        if (list == null)
            return root;

        loadSamples(root, list);

        return root;
    }

    /**
     * Creates subtrees for each sample in the passed list; adds all subtrees to
     * the passed node, which is the root of the tree
     */
    private void loadSamples(Node root, ArrayList<SecondDataEntryVO> list) {
        Integer prevId, currId;
        Node node;

        prevId = null;
        node = null;
        for (SecondDataEntryVO data : list) {
            currId = data.getSampleId();
            if ( !currId.equals(prevId)) {
                node = new Node(2);
                node.setCell(0, data.getSampleAccessionNumber());
                node.setCell(1, data.getHistorysystemUserLoginName());
                node.setType(SAMPLE_USER_LEAF);
                node.setData(data.getSampleId());
                root.add(node);
            } else {
                node.setCell(1,
                             DataBaseUtil.concatWithSeparator(node.getCell(1),
                                                              ", ",
                                                              data.getHistorysystemUserLoginName()));
            }
            prevId = currId;
        }
    }

    private void loadAttachments(int sel) {
        int i, j;
        Node root, node, child;
        SampleManager1 sm;
        AttachmentItemViewDO att;

        root = tree.getRoot();
        for (i = sel; i < root.getChildCount(); i++ ) {
            node = root.getChildAt(i);
            sm = managers.get(node.getData());

            for (j = 0; j < sm.attachment.count(); j++ ) {
                att = sm.attachment.get(j);
                child = new Node(1);
                child.setCell(0, att.getAttachmentDescription());
                child.setType(ATTACHMENT_LEAF);
                child.setData(att);
                tree.addNodeAt(node, child);
            }
        }
    }

    private void nodeSelected(Node node) {
        Integer sampleId;
        Node child;
        AttachmentItemViewDO att;

        child = null;
        /*
         * if a top level node is selected and if its sample has any
         * attachments, open the attachment if there's only one, otherwise tell
         * the user that an attachment should be selected to open it; if an
         * attachment's node is selected, open it
         */
        sampleId = null;
        if (SAMPLE_USER_LEAF.equals(node.getType())) {
            if (node.getChildCount() == 1)
                child = node.getChildAt(0);
            else if (node.getChildCount() > 1)
                Window.alert(Messages.get().secondDataEntry_selectAttachmentToView());
            sampleId = node.getData();
        } else {
            child = node;
        }

        if (child != null) {
            att = child.getData();
            displayAttachment(att.getAttachmentId(), Messages.get()
                                                             .secondDataEntry_secondDataEntry());
            sampleId = att.getReferenceId();
        }

        manager = managers.get(sampleId);
        setData();

        if ( !isState(UPDATE))
            addWidgets(manager.getSample().getDomain());
    }

    /**
     * Opens the file linked to the attachment showing on the passed row; if
     * "name" is null or if it's different from the previous time this method
     * was called then the file is opened in a new window, otherwise it's opened
     * in the same window as before.
     */
    private void displayAttachment(Integer id, String name) {
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
     * Adds widgets used for verification for the passed domain to the screen
     */
    private void addWidgets(String dom) {
        ReportStatus status;

        /*
         * this is done to avoid clearing the widget table unless the current
         * sample's domain is different from the previous one's
         */
        if (DataBaseUtil.isSame(domain, dom))
            return;
        else
            domain = dom;

        try {
            /*
             * get the xml string for the passed domain from the back-end
             */
            if (Constants.domain().ENVIRONMENTAL.equals(dom)) {
                if (envXML == null) {
                    status = service.getFields("env_verification_fields");
                    envXML = status.getMessage();
                    environmentalTab.addWidgets(envXML);
                }
                deckPanel.showWidget(0);
            } else {
                deckPanel.showWidget(1);
                return;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            Window.alert(e.getMessage());
            return;
        }
    }
}