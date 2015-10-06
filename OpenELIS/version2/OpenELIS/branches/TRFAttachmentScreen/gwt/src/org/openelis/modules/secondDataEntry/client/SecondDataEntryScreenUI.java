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

import org.openelis.cache.CategoryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AttachmentItemViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.SecondDataEntryVO;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.CategoryMeta;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.attachment.client.AttachmentUtil;
import org.openelis.modules.main.client.resources.OpenELISResources;
import org.openelis.modules.organization1.client.OrganizationService1Impl;
import org.openelis.modules.project.client.ProjectService;
import org.openelis.modules.sample1.client.SampleService1;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.DoubleHelper;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.IntegerHelper;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.Label;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.TextBase;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.calendar.Calendar;
import org.openelis.ui.widget.table.CellRenderer;
import org.openelis.ui.widget.table.Column;
import org.openelis.ui.widget.table.LabelCell;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.tree.Node;
import org.openelis.ui.widget.tree.Tree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;

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
                environmentalTab.fireDataChange();
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
        int i, row;
        String xml;
        Document doc;
        com.google.gwt.xml.client.Node root, node;
        ReportStatus status;
        // final Screen child;

        /*
         * this is done to avoid clearing the widget table unless the current
         * sample's domain is different from the previous one's
         */
        if (DataBaseUtil.isSame(domain, dom))
            return;
        else
            domain = dom;

        xml = null;
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

        /*
         * this child screen is created so that the screen handlers for the
         * widgets created from the xml can be added to it; adding those
         * handlers to the main screen doesn't work; that's because the handler
         * for the same widget and key could be added multiple times because the
         * widget is created multiple times and handlers with duplicate keys are
         * not allowed
         */
        /*sampleWidgetScreen = new Screen();
        handlers.remove("sampleWidgetScreen");
        addScreenHandler(sampleWidgetScreen, "sampleWidgetScreen", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                sampleWidgetScreen.fireDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                sampleWidgetScreen.setState(event.getState());
            }
        });

        /*
         * parse the xml and add widgets for the fields to the widget table
         /
        doc = XMLParser.parse(xml);
        root = doc.getDocumentElement();
        row = 0;
        for (i = 0; i < root.getChildNodes().getLength(); i++ ) {
            node = root.getChildNodes().item(i);
            /*
             * this discards unwanted "child" nodes like whitespaces
             /
            if (com.google.gwt.xml.client.Node.ELEMENT_NODE != node.getNodeType())
                continue;

            addWidgetRow(sampleWidgetScreen, node.getNodeName(), ++row);
        }*/
    }

    /**
     * Adds a row for the widget with the passed key, at the passed index
     */
    private void addWidgetRow(Screen child, String key, int row) {
        switch (key) {
            case SampleMeta.COLLECTION_DATE:
                addCollectionDate(child, row);
                break;
            case SampleMeta.COLLECTION_TIME:
                addCollectionTime(child, row);
                break;
            case SampleMeta.RECEIVED_DATE:
                addReceievedDate(child, row);
                break;
            case SampleMeta.CLIENT_REFERENCE:
                addClientReference(child, row);
                break;
            case REPORT_TO:
                addReportTo(child, row);
                break;
            case BILL_TO:
                addBillTo(child, row);
                break;
            case SampleMeta.PROJECT_NAME:
                addProject(child, row);
                break;
            case SampleMeta.ENV_IS_HAZARDOUS:
                addEnvIsHazardous(child, row);
                break;
            case SampleMeta.ENV_PRIORITY:
                addEnvPriority(child, row);
                break;
            case SampleMeta.ENV_COLLECTOR:
                addEnvCollector(child, row);
                break;
            case SampleMeta.ENV_COLLECTOR_PHONE:
                addEnvCollectorPhone(child, row);
                break;
            case SampleMeta.ENV_DESCRIPTION:
                addEnvDescription(child, row);
                break;
            case SampleMeta.ENV_LOCATION:
                addEnvLocation(child, row);
                break;
            case SampleMeta.LOCATION_ADDR_MULTIPLE_UNIT:
                addLocationAddrMultipleUnit(child, row);
                break;
            case SampleMeta.LOCATION_ADDR_STREET_ADDRESS:
                addLocationAddrStreetAddress(child, row);
                break;
            case SampleMeta.LOCATION_ADDR_CITY:
                addLocationAddrCity(child, row);
                break;
            case SampleMeta.LOCATION_ADDR_STATE:
                addLocationAddrState(child, row);
                break;
            case SampleMeta.LOCATION_ADDR_ZIP_CODE:
                addLocationAddrZipCode(child, row);
                break;
            case SampleMeta.LOCATION_ADDR_COUNTRY:
                addLocationAddrCountry(child, row);
                break;
        }
    }

    /**
     * Adds the row for collection date to the row at the passed index; also
     * adds the applicable screen handlers to the widgets in the row
     */
    private void addCollectionDate(Screen child, int row) {
        Label<String> l1, l2;
        final Calendar c1, c2;
        final Image i;
        HorizontalPanel hp;
        final Button b;
        final ScheduledCommand cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = getLabel(Messages.get().sample_collected());

        c1 = getCalendar(90, 0, 2);
        i = new Image();
        hp = new HorizontalPanel();
        addWidgetAndImage(hp, c1, i);

        b = getCopyButton();
        l2 = getLabel(Messages.get().sample_collected());
        c2 = getCalendar(90, 0, 2);

        addWidgets(row, l1, hp, b, l2, c2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = getFocusCommand(c1);

        /*
         * add screen handler for the editable widget
         */
        child.addScreenHandler(c1, SampleMeta.COLLECTION_DATE, new ScreenHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                c1.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                Integer numEdit;

                /*
                 * find out how many times the widget has been edited
                 */
                numEdit = updateNumEdit(SampleMeta.COLLECTION_DATE);

                /*
                 * if the value entered is different from the value in the
                 * manager, show the icon for "no match" and if this widget has
                 * been edited multiple times, show the value in the manager in
                 * the widget on the right; if the value entered is the same as
                 * the value in the manager, show the icon for "match"
                 */
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSample()
                                                                      .getCollectionDate())) {
                    i.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1) {
                        c2.setValue(manager.getSample().getCollectionDate());
                        b.setEnabled(true);
                    }
                } else {
                    i.setResource(OpenELISResources.INSTANCE.commit());
                }

                /*
                 * set the focus back on the widget
                 */
                Scheduler.get().scheduleDeferred(cmd);
            }

            public void onStateChange(StateChangeEvent event) {
                c1.setEnabled(isState(UPDATE));
            }
        });

        /*
         * add screen handler for the image
         */
        child.addScreenHandler(i,
                               getImageKey(SampleMeta.COLLECTION_DATE),
                               new ScreenHandler<Datetime>() {
                                   public void onStateChange(StateChangeEvent event) {
                                       i.setResource(OpenELISResources.INSTANCE.blankIcon());
                                   }
                               });

        /*
         * add screen handler for the button
         */
        child.addScreenHandler(b,
                               getButtonKey(SampleMeta.COLLECTION_DATE),
                               new ScreenHandler<Datetime>() {
                                   public void onStateChange(StateChangeEvent event) {
                                       b.setEnabled(false);
                                   }
                               });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                c1.setValue(manager.getSample().getCollectionDate());
                i.setResource(OpenELISResources.INSTANCE.commit());
                c1.setFocus(true);
            }
        });

        /*
         * add screen handler for the widget on the right
         */
        child.addScreenHandler(c2,
                               getRightWidgetKey(SampleMeta.COLLECTION_DATE),
                               new ScreenHandler<Datetime>() {
                                   public void onDataChange(DataChangeEvent event) {
                                       c2.setValue(null);
                                   }

                                   public void onStateChange(StateChangeEvent event) {
                                       c2.setEnabled(false);
                                   }
                               });
    }

    /**
     * Adds the row for collection time to the row at the passed index; also
     * adds the applicable screen handlers to the widgets in the row
     */
    private void addCollectionTime(Screen child, int row) {
        Label<String> l1, l2;
        final Calendar c1, c2;
        final Image i;
        HorizontalPanel hp;
        final Button b;
        final ScheduledCommand cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = getLabel(Messages.get().gen_time());

        c1 = getCalendar(60, 3, 4);
        i = new Image();
        hp = new HorizontalPanel();
        addWidgetAndImage(hp, c1, i);

        b = getCopyButton();
        l2 = getLabel(Messages.get().gen_time());
        c2 = getCalendar(60, 3, 4);

        addWidgets(row, l1, hp, b, l2, c2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = getFocusCommand(c1);

        /*
         * add screen handler for the editable widget
         */
        child.addScreenHandler(c1, SampleMeta.COLLECTION_TIME, new ScreenHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                c1.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                Integer numEdit;

                /*
                 * find out how many times the widget has been edited
                 */
                numEdit = updateNumEdit(SampleMeta.COLLECTION_TIME);

                /*
                 * if the value entered is different from the value in the
                 * manager, show the icon for "no match" and if this widget has
                 * been edited multiple times, show the value in the manager in
                 * the widget on the right; if the value entered is the same as
                 * the value in the manager, show the icon for "match"
                 */
                if (DataBaseUtil.isDifferentDT(event.getValue(), manager.getSample()
                                                                        .getCollectionTime())) {
                    i.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1) {
                        c2.setValue(manager.getSample().getCollectionTime());
                        b.setEnabled(true);
                    }
                } else {
                    i.setResource(OpenELISResources.INSTANCE.commit());
                }

                /*
                 * set the focus back on the widget
                 */
                Scheduler.get().scheduleDeferred(cmd);
            }

            public void onStateChange(StateChangeEvent event) {
                c1.setEnabled(isState(UPDATE));
            }
        });

        /*
         * add screen handler for the image
         */
        child.addScreenHandler(i,
                               getImageKey(SampleMeta.COLLECTION_TIME),
                               new ScreenHandler<Datetime>() {
                                   public void onStateChange(StateChangeEvent event) {
                                       i.setResource(OpenELISResources.INSTANCE.blankIcon());
                                   }
                               });

        /*
         * add screen handler for the button
         */
        child.addScreenHandler(b,
                               getButtonKey(SampleMeta.COLLECTION_TIME),
                               new ScreenHandler<Datetime>() {
                                   public void onStateChange(StateChangeEvent event) {
                                       b.setEnabled(false);
                                   }
                               });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                c1.setValue(manager.getSample().getCollectionTime());
                i.setResource(OpenELISResources.INSTANCE.commit());
                c1.setFocus(true);
            }
        });

        /*
         * add screen handler for the widget on the right
         */
        child.addScreenHandler(c2,
                               getRightWidgetKey(SampleMeta.COLLECTION_TIME),
                               new ScreenHandler<Datetime>() {
                                   public void onDataChange(DataChangeEvent event) {
                                       c2.setValue(null);
                                   }

                                   public void onStateChange(StateChangeEvent event) {
                                       c2.setEnabled(false);
                                   }
                               });
    }

    /**
     * Adds the row for received date to the row at the passed index
     */
    private void addReceievedDate(Screen child, int row) {
        Label<String> l1, l2;
        final Calendar c1, c2;
        final Image i;
        HorizontalPanel h;
        final Button b;
        final ScheduledCommand cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = getLabel(Messages.get().sample_received());

        c1 = getCalendar(90, 0, 4);
        i = new Image();
        h = new HorizontalPanel();
        addWidgetAndImage(h, c1, i);

        b = getCopyButton();
        l2 = getLabel(Messages.get().sample_received());
        c2 = getCalendar(90, 0, 4);

        addWidgets(row, l1, h, b, l2, c2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = getFocusCommand(c1);

        /*
         * add screen handler for the editable widget
         */
        child.addScreenHandler(c1, SampleMeta.RECEIVED_DATE, new ScreenHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                c1.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                Integer numEdit;

                /*
                 * find out how many times the widget has been edited
                 */
                numEdit = updateNumEdit(SampleMeta.RECEIVED_DATE);

                /*
                 * if the value entered is different from the value in the
                 * manager, show the icon for "no match" and if this widget has
                 * been edited multiple times, show the value in the manager in
                 * the widget on the right; if the value entered is the same as
                 * the value in the manager, show the icon for "match"
                 */
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSample()
                                                                      .getReceivedDate())) {
                    i.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1) {
                        c2.setValue(manager.getSample().getReceivedDate());
                        b.setEnabled(true);
                    }
                } else {
                    i.setResource(OpenELISResources.INSTANCE.commit());
                }

                /*
                 * set the focus back on the widget
                 */
                Scheduler.get().scheduleDeferred(cmd);
            }

            public void onStateChange(StateChangeEvent event) {
                c1.setEnabled(isState(UPDATE));
            }
        });

        /*
         * add screen handler for the button
         */
        child.addScreenHandler(i,
                               getImageKey(SampleMeta.RECEIVED_DATE),
                               new ScreenHandler<Object>() {
                                   public void onStateChange(StateChangeEvent event) {
                                       i.setResource(OpenELISResources.INSTANCE.blankIcon());
                                   }
                               });

        /*
         * add screen handler for the button
         */
        child.addScreenHandler(b,
                               getButtonKey(SampleMeta.RECEIVED_DATE),
                               new ScreenHandler<Object>() {
                                   public void onStateChange(StateChangeEvent event) {
                                       b.setEnabled(false);
                                   }
                               });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                c1.setValue(manager.getSample().getReceivedDate());
                i.setResource(OpenELISResources.INSTANCE.commit());
                c1.setFocus(true);
            }
        });

        /*
         * add screen handler for the widget on the right
         */
        child.addScreenHandler(c2,
                               getRightWidgetKey(SampleMeta.RECEIVED_DATE),
                               new ScreenHandler<Datetime>() {
                                   public void onDataChange(DataChangeEvent event) {
                                       c2.setValue(null);
                                   }

                                   public void onStateChange(StateChangeEvent event) {
                                       c2.setEnabled(false);
                                   }
                               });
    }

    /**
     * Adds the row for client reference at the passed index; also adds the
     * applicable screen handlers to the widgets in the row
     */
    private void addClientReference(Screen child, int row) {
        Label<String> l1, l2;
        final TextBox<String> t1, t2;
        final Image i;
        HorizontalPanel h;
        final Button b;
        final ScheduledCommand cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = getLabel(Messages.get().sample_clntRef());

        t1 = getTextBox(Type.STRING, 196, TextBase.Case.LOWER, 20, null);
        i = new Image();
        h = new HorizontalPanel();
        addWidgetAndImage(h, t1, i);

        b = getCopyButton();
        l2 = getLabel(Messages.get().sample_clntRef());
        t2 = getTextBox(Type.STRING, 196, TextBase.Case.LOWER, 20, null);

        addWidgets(row, l1, h, b, l2, t2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = getFocusCommand(t1);

        /*
         * add screen handler for the editable widget
         */
        child.addScreenHandler(t1, SampleMeta.CLIENT_REFERENCE, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                t1.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                Integer numEdit;

                /*
                 * find out how many times the widget has been edited
                 */
                numEdit = updateNumEdit(SampleMeta.CLIENT_REFERENCE);

                /*
                 * if the value entered is different from the value in the
                 * manager, show the icon for "no match" and if this widget has
                 * been edited multiple times, show the value in the manager in
                 * the widget on the right; if the value entered is the same as
                 * the value in the manager, show the icon for "match"
                 */
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSample()
                                                                      .getClientReference())) {
                    i.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1) {
                        t2.setValue(manager.getSample().getClientReference());
                        b.setEnabled(true);
                    }
                } else {
                    i.setResource(OpenELISResources.INSTANCE.commit());
                }

                /*
                 * set the focus back on the widget
                 */
                Scheduler.get().scheduleDeferred(cmd);
            }

            public void onStateChange(StateChangeEvent event) {
                t1.setEnabled(isState(UPDATE));
            }
        });

        /*
         * add screen handler for the image
         */
        child.addScreenHandler(i,
                               getImageKey(SampleMeta.CLIENT_REFERENCE),
                               new ScreenHandler<Object>() {
                                   public void onStateChange(StateChangeEvent event) {
                                       i.setResource(OpenELISResources.INSTANCE.blankIcon());
                                   }
                               });

        /*
         * add screen handler for the button
         */
        child.addScreenHandler(b,
                               getButtonKey(SampleMeta.CLIENT_REFERENCE),
                               new ScreenHandler<Object>() {
                                   public void onStateChange(StateChangeEvent event) {
                                       b.setEnabled(false);
                                   }
                               });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                t1.setValue(manager.getSample().getClientReference());
                i.setResource(OpenELISResources.INSTANCE.commit());
                t1.setFocus(true);
            }
        });

        /*
         * add screen handler for the widget on the right
         */
        child.addScreenHandler(t2,
                               getRightWidgetKey(SampleMeta.CLIENT_REFERENCE),
                               new ScreenHandler<String>() {
                                   public void onDataChange(DataChangeEvent event) {
                                       t2.setValue(null);
                                   }

                                   public void onStateChange(StateChangeEvent event) {
                                       t2.setEnabled(false);
                                   }
                               });
    }

    /**
     * Adds the row for the report-to organization at the passed index
     */
    private void addReportTo(Screen child, int row) {
        Label<String> l1, l2;
        final AutoComplete a1, a2;
        final Image i;
        HorizontalPanel h;
        final Button b;
        final ScheduledCommand cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = getLabel(Messages.get().sampleOrganization_reportTo());
        a1 = getOrgAutocomplete(180, TextBase.Case.UPPER, 565);
        i = new Image();
        h = new HorizontalPanel();
        addWidgetAndImage(h, a1, i);

        b = getCopyButton();
        l2 = getLabel(Messages.get().sampleOrganization_reportTo());
        a2 = getOrgAutocomplete(180, TextBase.Case.UPPER, 565);

        addWidgets(row, l1, h, b, l2, a2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = getFocusCommand(a1);

        /*
         * add screen handler for the editable widget
         */
        child.addScreenHandler(a1, REPORT_TO, new ScreenHandler<AutoCompleteValue>() {
            public void onDataChange(DataChangeEvent event) {
                a1.setValue(null, "");
            }

            public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                // setCollectionDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                a1.setEnabled(isState(UPDATE));
            }
        });

        /*
         * add screen handler for the button
         */
        child.addScreenHandler(b, getButtonKey(REPORT_TO), new ScreenHandler<Datetime>() {
            public void onStateChange(StateChangeEvent event) {
                b.setEnabled(false);
            }
        });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // TODO Auto-generated method stub
            }
        });

        /*
         * add screen handler for the widget on the right
         */
        child.addScreenHandler(a2, getRightWidgetKey(REPORT_TO), new ScreenHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                a2.setValue(null, "");
            }

            public void onStateChange(StateChangeEvent event) {
                a2.setEnabled(false);
            }
        });
    }

    /**
     * Adds the row for the bill-to organization at the passed index
     */
    private void addBillTo(Screen child, int row) {
        Label<String> l1, l2;
        final AutoComplete a1, a2;
        final Image i;
        HorizontalPanel h;
        final Button b;

        l1 = getLabel(Messages.get().sampleOrganization_billTo());
        a1 = getOrgAutocomplete(180, TextBase.Case.UPPER, 565);
        i = new Image();
        h = new HorizontalPanel();
        addWidgetAndImage(h, a1, i);

        b = getCopyButton();
        l2 = getLabel(Messages.get().sampleOrganization_billTo());
        a2 = getOrgAutocomplete(180, TextBase.Case.UPPER, 565);

        addWidgets(row, l1, h, b, l2, a2);

        /*
         * add screen handler for the editable widget
         */
        child.addScreenHandler(a1, BILL_TO, new ScreenHandler<AutoCompleteValue>() {
            public void onDataChange(DataChangeEvent event) {
                a1.setValue(null, "");
            }

            public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                // setCollectionDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                a1.setEnabled(isState(UPDATE));
            }
        });

        /*
         * add screen handler for the button
         */
        child.addScreenHandler(b, getButtonKey(BILL_TO), new ScreenHandler<Datetime>() {
            public void onStateChange(StateChangeEvent event) {
                b.setEnabled(false);
            }
        });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // TODO Auto-generated method stub
            }
        });

        /*
         * add screen handler for the widget on the right
         */
        child.addScreenHandler(a2,
                               getRightWidgetKey(BILL_TO),
                               new ScreenHandler<AutoCompleteValue>() {
                                   public void onDataChange(DataChangeEvent event) {
                                       a2.setValue(null, "");
                                   }

                                   public void onStateChange(StateChangeEvent event) {
                                       a2.setEnabled(false);
                                   }
                               });
    }

    /**
     * Adds the row for project at the passed index
     */
    private void addProject(Screen child, int row) {
        Label<String> l1, l2;
        final AutoComplete a1, a2;
        final Image i;
        HorizontalPanel h;
        final Button b;

        l1 = getLabel(Messages.get().project_project());
        a1 = getProjectAutocomplete(180, TextBase.Case.LOWER, 440);
        i = new Image();
        h = new HorizontalPanel();
        addWidgetAndImage(h, a1, i);

        b = getCopyButton();
        l2 = getLabel(Messages.get().project_project());
        a2 = getProjectAutocomplete(180, TextBase.Case.LOWER, 440);

        addWidgets(row, l1, h, b, l2, a2);

        /*
         * add screen handler for the editable widget
         */
        child.addScreenHandler(a1, SampleMeta.PROJECT_NAME, new ScreenHandler<AutoCompleteValue>() {
            public void onDataChange(DataChangeEvent event) {
                a1.setValue(null, "");
            }

            public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                // setCollectionDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                a1.setEnabled(isState(UPDATE));
            }
        });

        /*
         * add screen handler for the button
         */
        child.addScreenHandler(b,
                               getButtonKey(SampleMeta.PROJECT_NAME),
                               new ScreenHandler<Datetime>() {
                                   public void onStateChange(StateChangeEvent event) {
                                       b.setEnabled(false);
                                   }
                               });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // TODO Auto-generated method stub
            }
        });

        /*
         * add screen handler for the widget on the right
         */
        child.addScreenHandler(a2,
                               getRightWidgetKey(SampleMeta.PROJECT_NAME),
                               new ScreenHandler<AutoCompleteValue>() {
                                   public void onDataChange(DataChangeEvent event) {
                                       a2.setValue(null, "");
                                   }

                                   public void onStateChange(StateChangeEvent event) {
                                       a2.setEnabled(false);
                                   }
                               });
    }

    /**
     * Adds the row for environmental is_hazardous at the passed index; also
     * adds the applicable screen handlers to the widgets in the row
     */
    private void addEnvIsHazardous(Screen child, int row) {
        Label<String> l1, l2;
        final CheckBox c1, c2;
        final Image i;
        HorizontalPanel h;
        final Button b;
        final ScheduledCommand cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = getLabel(Messages.get().sampleEnvironmental_hazardous());

        c1 = new CheckBox();
        i = new Image();
        h = new HorizontalPanel();
        addWidgetAndImage(h, c1, i);

        b = getCopyButton();
        l2 = getLabel(Messages.get().sampleEnvironmental_hazardous());
        c2 = new CheckBox();

        addWidgets(row, l1, h, b, l2, c2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = getFocusCommand(c1);

        /*
         * add screen handler for the editable widget
         */
        child.addScreenHandler(c1, SampleMeta.ENV_IS_HAZARDOUS, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                c1.setValue("N");
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                Integer numEdit;

                /*
                 * find out how many times the widget has been edited
                 */
                numEdit = updateNumEdit(SampleMeta.ENV_IS_HAZARDOUS);

                /*
                 * if the value entered is different from the value in the
                 * manager, show the icon for "no match" and if this widget has
                 * been edited multiple times, show the value in the manager in
                 * the widget on the right; if the value entered is the same as
                 * the value in the manager, show the icon for "match"
                 */
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSampleEnvironmental()
                                                                      .getIsHazardous())) {
                    i.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1) {
                        c2.setValue(manager.getSampleEnvironmental().getIsHazardous());
                        b.setEnabled(true);
                    }
                } else {
                    i.setResource(OpenELISResources.INSTANCE.commit());
                }

                /*
                 * set the focus back on the widget
                 */
                Scheduler.get().scheduleDeferred(cmd);
            }

            public void onStateChange(StateChangeEvent event) {
                c1.setEnabled(isState(UPDATE));
            }
        });

        /*
         * add screen handler for the image
         */
        child.addScreenHandler(i,
                               getImageKey(SampleMeta.ENV_IS_HAZARDOUS),
                               new ScreenHandler<Object>() {
                                   public void onStateChange(StateChangeEvent event) {
                                       i.setResource(OpenELISResources.INSTANCE.blankIcon());
                                   }
                               });

        /*
         * add screen handler for the button
         */
        child.addScreenHandler(b,
                               getButtonKey(SampleMeta.ENV_IS_HAZARDOUS),
                               new ScreenHandler<Object>() {
                                   public void onStateChange(StateChangeEvent event) {
                                       b.setEnabled(false);
                                   }
                               });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                c1.setValue(manager.getSampleEnvironmental().getIsHazardous());
                i.setResource(OpenELISResources.INSTANCE.commit());
                c1.setFocus(true);
            }
        });

        /*
         * add screen handler for the widget on the right
         */
        child.addScreenHandler(c2,
                               getRightWidgetKey(SampleMeta.ENV_IS_HAZARDOUS),
                               new ScreenHandler<String>() {
                                   public void onDataChange(DataChangeEvent event) {
                                       c2.setValue("N");
                                   }

                                   public void onStateChange(StateChangeEvent event) {
                                       c2.setEnabled(false);
                                   }
                               });
    }

    /**
     * Adds the row for environmental priority at the passed index
     */
    private void addEnvPriority(Screen child, int row) {
        Label<String> l1, l2;
        final TextBox<Integer> t1, t2;
        final Image i;
        HorizontalPanel h;
        final Button b;
        final ScheduledCommand cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = getLabel(Messages.get().gen_priority());

        t1 = getTextBox(Type.INTEGER, 24, null, null, null);
        i = new Image();
        h = new HorizontalPanel();
        addWidgetAndImage(h, t1, i);

        b = getCopyButton();
        l2 = getLabel(Messages.get().gen_priority());
        t2 = getTextBox(Type.INTEGER, 24, null, null, null);

        addWidgets(row, l1, h, b, l2, t2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = getFocusCommand(t1);

        /*
         * add screen handler for the editable widget
         */
        child.addScreenHandler(t1, SampleMeta.ENV_PRIORITY, new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                t1.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                Integer numEdit;

                /*
                 * find out how many times the widget has been edited
                 */
                numEdit = updateNumEdit(SampleMeta.ENV_PRIORITY);

                /*
                 * if the value entered is different from the value in the
                 * manager, show the icon for "no match" and if this widget has
                 * been edited multiple times, show the value in the manager in
                 * the widget on the right; if the value entered is the same as
                 * the value in the manager, show the icon for "match"
                 */
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSampleEnvironmental()
                                                                      .getPriority())) {
                    i.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1) {
                        t2.setValue(manager.getSampleEnvironmental().getPriority());
                        b.setEnabled(true);
                    }
                } else {
                    i.setResource(OpenELISResources.INSTANCE.commit());
                }

                /*
                 * set the focus back on the widget
                 */
                Scheduler.get().scheduleDeferred(cmd);
            }

            public void onStateChange(StateChangeEvent event) {
                t1.setEnabled(isState(UPDATE));
            }
        });

        /*
         * add screen handler for the image
         */
        child.addScreenHandler(i,
                               getImageKey(SampleMeta.ENV_PRIORITY),
                               new ScreenHandler<Object>() {
                                   public void onStateChange(StateChangeEvent event) {
                                       i.setResource(OpenELISResources.INSTANCE.blankIcon());
                                   }
                               });

        /*
         * add screen handler for the button
         */
        child.addScreenHandler(b,
                               getButtonKey(SampleMeta.ENV_PRIORITY),
                               new ScreenHandler<Object>() {
                                   public void onStateChange(StateChangeEvent event) {
                                       b.setEnabled(false);
                                   }
                               });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                t1.setValue(manager.getSampleEnvironmental().getPriority());
                i.setResource(OpenELISResources.INSTANCE.commit());
                t1.setFocus(true);
            }
        });

        /*
         * add screen handler for the widget on the right
         */
        child.addScreenHandler(t2,
                               getRightWidgetKey(SampleMeta.ENV_PRIORITY),
                               new ScreenHandler<Integer>() {
                                   public void onDataChange(DataChangeEvent event) {
                                       t2.setValue(null);
                                   }

                                   public void onStateChange(StateChangeEvent event) {
                                       t2.setEnabled(false);
                                   }
                               });
    }

    /**
     * Adds the row for environmental collector at the passed index
     */
    private void addEnvCollector(Screen child, int row) {
        Label<String> l1, l2;
        final TextBox<String> t1, t2;
        final Image i;
        HorizontalPanel h;
        final Button b;
        final ScheduledCommand cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = getLabel(Messages.get().sample_collector());

        t1 = getTextBox(Type.STRING, 235, null, 40, null);
        i = new Image();
        h = new HorizontalPanel();
        addWidgetAndImage(h, t1, i);

        b = getCopyButton();
        l2 = getLabel(Messages.get().sample_collector());
        t2 = getTextBox(Type.STRING, 235, null, 40, null);

        addWidgets(row, l1, h, b, l2, t2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = getFocusCommand(t1);

        /*
         * add screen handler for the editable widget
         */
        child.addScreenHandler(t1, SampleMeta.ENV_COLLECTOR, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                t1.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                Integer numEdit;

                /*
                 * find out how many times the widget has been edited
                 */
                numEdit = updateNumEdit(SampleMeta.ENV_COLLECTOR);

                /*
                 * if the value entered is different from the value in the
                 * manager, show the icon for "no match" and if this widget has
                 * been edited multiple times, show the value in the manager in
                 * the widget on the right; if the value entered is the same as
                 * the value in the manager, show the icon for "match"
                 */
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSampleEnvironmental()
                                                                      .getCollector())) {
                    i.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1) {
                        t2.setValue(manager.getSampleEnvironmental().getCollector());
                        b.setEnabled(true);
                    }
                } else {
                    i.setResource(OpenELISResources.INSTANCE.commit());
                }

                /*
                 * set the focus back on the widget
                 */
                Scheduler.get().scheduleDeferred(cmd);
            }

            public void onStateChange(StateChangeEvent event) {
                t1.setEnabled(isState(UPDATE));
            }
        });

        /*
         * add screen handler for the image
         */
        child.addScreenHandler(i,
                               getImageKey(SampleMeta.ENV_COLLECTOR),
                               new ScreenHandler<Object>() {
                                   public void onStateChange(StateChangeEvent event) {
                                       i.setResource(OpenELISResources.INSTANCE.blankIcon());
                                   }
                               });

        /*
         * add screen handler for the button
         */
        child.addScreenHandler(b,
                               getButtonKey(SampleMeta.ENV_COLLECTOR),
                               new ScreenHandler<Object>() {
                                   public void onStateChange(StateChangeEvent event) {
                                       b.setEnabled(false);
                                   }
                               });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                t1.setValue(manager.getSampleEnvironmental().getCollector());
                i.setResource(OpenELISResources.INSTANCE.commit());
                t1.setFocus(true);
            }
        });

        /*
         * add screen handler for the widget on the right
         */
        child.addScreenHandler(t2,
                               getRightWidgetKey(SampleMeta.ENV_COLLECTOR),
                               new ScreenHandler<String>() {
                                   public void onDataChange(DataChangeEvent event) {
                                       t2.setValue(null);
                                   }

                                   public void onStateChange(StateChangeEvent event) {
                                       t2.setEnabled(false);
                                   }
                               });
    }

    /**
     * Adds the row for environmental collector phone at the passed index
     */
    private void addEnvCollectorPhone(Screen child, int row) {
        Label<String> l1, l2;
        final TextBox<String> t1, t2;
        final Image i;
        HorizontalPanel h;
        final Button b;
        final ScheduledCommand cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = getLabel(Messages.get().address_phone());

        t1 = getTextBox(Type.STRING, 115, null, 17, Messages.get().gen_phoneWithExtensionPattern());
        i = new Image();
        h = new HorizontalPanel();
        addWidgetAndImage(h, t1, i);

        b = getCopyButton();
        l2 = getLabel(Messages.get().address_phone());
        t2 = getTextBox(Type.STRING, 115, null, 17, Messages.get().gen_phoneWithExtensionPattern());

        addWidgets(row, l1, h, b, l2, t2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = getFocusCommand(t1);

        /*
         * add screen handler for the editable widget
         */
        child.addScreenHandler(t1, SampleMeta.ENV_COLLECTOR_PHONE, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                t1.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                Integer numEdit;

                /*
                 * find out how many times the widget has been edited
                 */
                numEdit = updateNumEdit(SampleMeta.ENV_COLLECTOR_PHONE);

                /*
                 * if the value entered is different from the value in the
                 * manager, show the icon for "no match" and if this widget has
                 * been edited multiple times, show the value in the manager in
                 * the widget on the right; if the value entered is the same as
                 * the value in the manager, show the icon for "match"
                 */
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSampleEnvironmental()
                                                                      .getCollectorPhone())) {
                    i.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1) {
                        t2.setValue(manager.getSampleEnvironmental().getCollectorPhone());
                        b.setEnabled(true);
                    }
                } else {
                    i.setResource(OpenELISResources.INSTANCE.commit());
                }

                /*
                 * set the focus back on the widget
                 */
                Scheduler.get().scheduleDeferred(cmd);
            }

            public void onStateChange(StateChangeEvent event) {
                t1.setEnabled(isState(UPDATE));
            }
        });

        /*
         * add screen handler for the image
         */
        child.addScreenHandler(i,
                               getImageKey(SampleMeta.ENV_COLLECTOR_PHONE),
                               new ScreenHandler<Object>() {
                                   public void onStateChange(StateChangeEvent event) {
                                       i.setResource(OpenELISResources.INSTANCE.blankIcon());
                                   }
                               });

        /*
         * add screen handler for the button
         */
        child.addScreenHandler(b,
                               getButtonKey(SampleMeta.ENV_COLLECTOR_PHONE),
                               new ScreenHandler<Object>() {
                                   public void onStateChange(StateChangeEvent event) {
                                       b.setEnabled(false);
                                   }
                               });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                t1.setValue(manager.getSampleEnvironmental().getCollectorPhone());
                i.setResource(OpenELISResources.INSTANCE.commit());
                t1.setFocus(true);
            }
        });

        /*
         * add screen handler for the widget on the right
         */
        child.addScreenHandler(t2,
                               getRightWidgetKey(SampleMeta.ENV_COLLECTOR_PHONE),
                               new ScreenHandler<String>() {
                                   public void onDataChange(DataChangeEvent event) {
                                       t2.setValue(null);
                                   }

                                   public void onStateChange(StateChangeEvent event) {
                                       t2.setEnabled(false);
                                   }
                               });
    }

    /**
     * Adds the row for environmental description at the passed index
     */
    private void addEnvDescription(Screen child, int row) {
        Label<String> l1, l2;
        final TextBox<String> t1, t2;
        final Image i;
        HorizontalPanel h;
        final Button b;
        final ScheduledCommand cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = getLabel(Messages.get().gen_description());

        t1 = getTextBox(Type.STRING, 280, null, 40, null);
        i = new Image();
        h = new HorizontalPanel();
        addWidgetAndImage(h, t1, i);

        b = getCopyButton();
        l2 = getLabel(Messages.get().gen_description());
        t2 = getTextBox(Type.STRING, 280, null, 40, null);

        addWidgets(row, l1, h, b, l2, t2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = getFocusCommand(t1);

        /*
         * add screen handler for the editable widget
         */
        child.addScreenHandler(t1, SampleMeta.ENV_DESCRIPTION, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                t1.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                Integer numEdit;

                /*
                 * find out how many times the widget has been edited
                 */
                numEdit = updateNumEdit(SampleMeta.ENV_DESCRIPTION);

                /*
                 * if the value entered is different from the value in the
                 * manager, show the icon for "no match" and if this widget has
                 * been edited multiple times, show the value in the manager in
                 * the widget on the right; if the value entered is the same as
                 * the value in the manager, show the icon for "match"
                 */
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSampleEnvironmental()
                                                                      .getDescription())) {
                    i.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1) {
                        t2.setValue(manager.getSampleEnvironmental().getDescription());
                        b.setEnabled(true);
                    }
                } else {
                    i.setResource(OpenELISResources.INSTANCE.commit());
                }

                /*
                 * set the focus back on the widget
                 */
                Scheduler.get().scheduleDeferred(cmd);
            }

            public void onStateChange(StateChangeEvent event) {
                t1.setEnabled(isState(UPDATE));
            }
        });

        /*
         * add screen handler for the image
         */
        child.addScreenHandler(i,
                               getImageKey(SampleMeta.ENV_DESCRIPTION),
                               new ScreenHandler<String>() {
                                   public void onStateChange(StateChangeEvent event) {
                                       i.setResource(OpenELISResources.INSTANCE.blankIcon());
                                   }
                               });

        /*
         * add screen handler for the button
         */
        child.addScreenHandler(b,
                               getButtonKey(SampleMeta.ENV_DESCRIPTION),
                               new ScreenHandler<Datetime>() {
                                   public void onStateChange(StateChangeEvent event) {
                                       b.setEnabled(false);
                                   }
                               });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                t1.setValue(manager.getSampleEnvironmental().getDescription());
                i.setResource(OpenELISResources.INSTANCE.commit());
                t1.setFocus(true);
            }
        });

        /*
         * add screen handler for the widget on the right
         */
        child.addScreenHandler(t2,
                               getRightWidgetKey(SampleMeta.ENV_DESCRIPTION),
                               new ScreenHandler<Datetime>() {
                                   public void onDataChange(DataChangeEvent event) {
                                       t2.setValue(null);
                                   }

                                   public void onStateChange(StateChangeEvent event) {
                                       t2.setEnabled(false);
                                   }
                               });
    }

    /**
     * Adds the row for environmental description at the passed index
     */
    private void addEnvLocation(Screen child, int row) {
        Label<String> l1, l2;
        final TextBox<String> t1, t2;
        final Image i;
        HorizontalPanel h;
        final Button b;
        final ScheduledCommand cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = getLabel(Messages.get().gen_location());

        t1 = getTextBox(Type.STRING, 231, TextBase.Case.LOWER, 40, null);
        i = new Image();
        h = new HorizontalPanel();
        addWidgetAndImage(h, t1, i);

        b = getCopyButton();
        l2 = getLabel(Messages.get().gen_location());
        t2 = getTextBox(Type.STRING, 231, TextBase.Case.LOWER, 40, null);

        addWidgets(row, l1, h, b, l2, t2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = getFocusCommand(t1);

        /*
         * add screen handler for the editable widget
         */
        child.addScreenHandler(t1, SampleMeta.ENV_LOCATION, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                t1.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                Integer numEdit;

                /*
                 * find out how many times the widget has been edited
                 */
                numEdit = updateNumEdit(SampleMeta.ENV_LOCATION);

                /*
                 * if the value entered is different from the value in the
                 * manager, show the icon for "no match" and if this widget has
                 * been edited multiple times, show the value in the manager in
                 * the widget on the right; if the value entered is the same as
                 * the value in the manager, show the icon for "match"
                 */
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSampleEnvironmental()
                                                                      .getLocation())) {
                    i.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1) {
                        t2.setValue(manager.getSampleEnvironmental().getLocation());
                        b.setEnabled(true);
                    }
                } else {
                    i.setResource(OpenELISResources.INSTANCE.commit());
                }

                /*
                 * set the focus back on the widget
                 */
                Scheduler.get().scheduleDeferred(cmd);
            }

            public void onStateChange(StateChangeEvent event) {
                t1.setEnabled(isState(UPDATE));
            }
        });

        /*
         * add screen handler for the image
         */
        child.addScreenHandler(i,
                               getImageKey(SampleMeta.ENV_LOCATION),
                               new ScreenHandler<Object>() {
                                   public void onStateChange(StateChangeEvent event) {
                                       i.setResource(OpenELISResources.INSTANCE.blankIcon());
                                   }
                               });

        /*
         * add screen handler for the button
         */
        child.addScreenHandler(b,
                               getButtonKey(SampleMeta.ENV_LOCATION),
                               new ScreenHandler<Object>() {
                                   public void onStateChange(StateChangeEvent event) {
                                       b.setEnabled(false);
                                   }
                               });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                t1.setValue(manager.getSampleEnvironmental().getLocation());
                i.setResource(OpenELISResources.INSTANCE.commit());
                t1.setFocus(true);
            }
        });

        /*
         * add screen handler for the widget on the right
         */
        child.addScreenHandler(t2,
                               getRightWidgetKey(SampleMeta.ENV_LOCATION),
                               new ScreenHandler<String>() {
                                   public void onDataChange(DataChangeEvent event) {
                                       t2.setValue(null);
                                   }

                                   public void onStateChange(StateChangeEvent event) {
                                       t2.setEnabled(false);
                                   }
                               });
    }

    /**
     * Adds the row for environmental location multiple unit at the passed index
     */
    private void addLocationAddrMultipleUnit(Screen child, int row) {
        Label<String> l1, l2;
        final TextBox<String> t1, t2;
        final Image i;
        HorizontalPanel h;
        final Button b;
        final ScheduledCommand cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = getLabel(Messages.get().address_aptSuite());

        t1 = getTextBox(Type.STRING, 231, TextBase.Case.UPPER, null, null);
        i = new Image();
        h = new HorizontalPanel();
        addWidgetAndImage(h, t1, i);

        b = getCopyButton();
        l2 = getLabel(Messages.get().address_aptSuite());
        t2 = getTextBox(Type.STRING, 231, TextBase.Case.UPPER, null, null);

        addWidgets(row, l1, h, b, l2, t2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = getFocusCommand(t1);

        /*
         * add screen handler for the editable widget
         */
        child.addScreenHandler(t1,
                               SampleMeta.LOCATION_ADDR_MULTIPLE_UNIT,
                               new ScreenHandler<String>() {
                                   public void onDataChange(DataChangeEvent event) {
                                       t1.setValue(null);
                                   }

                                   public void onValueChange(ValueChangeEvent<String> event) {
                                       Integer numEdit;

                                       /*
                                        * find out how many times the widget has
                                        * been edited
                                        */
                                       numEdit = updateNumEdit(SampleMeta.LOCATION_ADDR_MULTIPLE_UNIT);

                                       /*
                                        * if the value entered is different from
                                        * the value in the manager, show the
                                        * icon for "no match" and if this widget
                                        * has been edited multiple times, show
                                        * the value in the manager in the widget
                                        * on the right; if the value entered is
                                        * the same as the value in the manager,
                                        * show the icon for "match"
                                        */
                                       if (DataBaseUtil.isDifferent(event.getValue(),
                                                                    manager.getSampleEnvironmental()
                                                                           .getLocationAddress()
                                                                           .getMultipleUnit())) {
                                           i.setResource(OpenELISResources.INSTANCE.abort());
                                           if (numEdit > 1) {
                                               t2.setValue(manager.getSampleEnvironmental()
                                                                  .getLocationAddress()
                                                                  .getMultipleUnit());
                                               b.setEnabled(true);
                                           }
                                       } else {
                                           i.setResource(OpenELISResources.INSTANCE.commit());
                                       }

                                       /*
                                        * set the focus back on the widget
                                        */
                                       Scheduler.get().scheduleDeferred(cmd);
                                   }

                                   public void onStateChange(StateChangeEvent event) {
                                       t1.setEnabled(isState(UPDATE));
                                   }
                               });

        /*
         * add screen handler for the image
         */
        child.addScreenHandler(i,
                               getImageKey(SampleMeta.LOCATION_ADDR_MULTIPLE_UNIT),
                               new ScreenHandler<Object>() {
                                   public void onStateChange(StateChangeEvent event) {
                                       i.setResource(OpenELISResources.INSTANCE.blankIcon());
                                   }
                               });

        /*
         * add screen handler for the button
         */
        child.addScreenHandler(b,
                               getButtonKey(SampleMeta.LOCATION_ADDR_MULTIPLE_UNIT),
                               new ScreenHandler<Object>() {
                                   public void onStateChange(StateChangeEvent event) {
                                       b.setEnabled(false);
                                   }
                               });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                t1.setValue(manager.getSampleEnvironmental().getLocationAddress().getMultipleUnit());
                i.setResource(OpenELISResources.INSTANCE.commit());
                t1.setFocus(true);
            }
        });

        /*
         * add screen handler for the widget on the right
         */
        child.addScreenHandler(t2,
                               getRightWidgetKey(SampleMeta.LOCATION_ADDR_MULTIPLE_UNIT),
                               new ScreenHandler<String>() {
                                   public void onDataChange(DataChangeEvent event) {
                                       t2.setValue(null);
                                   }

                                   public void onStateChange(StateChangeEvent event) {
                                       t2.setEnabled(false);
                                   }
                               });
    }

    /**
     * Adds the row for environmental location street address at the passed
     * index
     */
    private void addLocationAddrStreetAddress(Screen child, int row) {
        Label<String> l1, l2;
        final TextBox<String> t1, t2;
        final Image i;
        HorizontalPanel h;
        final Button b;
        final ScheduledCommand cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = getLabel(Messages.get().address_address());

        t1 = getTextBox(Type.STRING, 231, TextBase.Case.UPPER, null, null);
        i = new Image();
        h = new HorizontalPanel();
        addWidgetAndImage(h, t1, i);

        b = getCopyButton();
        l2 = getLabel(Messages.get().address_address());
        t2 = getTextBox(Type.STRING, 231, TextBase.Case.UPPER, null, null);

        addWidgets(row, l1, h, b, l2, t2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = getFocusCommand(t1);

        /*
         * add screen handler for the editable widget
         */
        child.addScreenHandler(t1,
                               SampleMeta.LOCATION_ADDR_STREET_ADDRESS,
                               new ScreenHandler<String>() {
                                   public void onDataChange(DataChangeEvent event) {
                                       t1.setValue(null);
                                   }

                                   public void onValueChange(ValueChangeEvent<String> event) {
                                       Integer numEdit;

                                       /*
                                        * find out how many times the widget has
                                        * been edited
                                        */
                                       numEdit = updateNumEdit(SampleMeta.LOCATION_ADDR_STREET_ADDRESS);

                                       /*
                                        * if the value entered is different from
                                        * the value in the manager, show the
                                        * icon for "no match" and if this widget
                                        * has been edited multiple times, show
                                        * the value in the manager in the widget
                                        * on the right; if the value entered is
                                        * the same as the value in the manager,
                                        * show the icon for "match"
                                        */
                                       if (DataBaseUtil.isDifferent(event.getValue(),
                                                                    manager.getSampleEnvironmental()
                                                                           .getLocationAddress()
                                                                           .getStreetAddress())) {
                                           i.setResource(OpenELISResources.INSTANCE.abort());
                                           if (numEdit > 1) {
                                               t2.setValue(manager.getSampleEnvironmental()
                                                                  .getLocationAddress()
                                                                  .getStreetAddress());
                                               b.setEnabled(true);
                                           }
                                       } else {
                                           i.setResource(OpenELISResources.INSTANCE.commit());
                                       }

                                       /*
                                        * set the focus back on the widget
                                        */
                                       Scheduler.get().scheduleDeferred(cmd);
                                   }

                                   public void onStateChange(StateChangeEvent event) {
                                       t1.setEnabled(isState(UPDATE));
                                   }
                               });

        /*
         * add screen handler for the image
         */
        child.addScreenHandler(i,
                               getImageKey(SampleMeta.LOCATION_ADDR_STREET_ADDRESS),
                               new ScreenHandler<Object>() {
                                   public void onStateChange(StateChangeEvent event) {
                                       i.setResource(OpenELISResources.INSTANCE.blankIcon());
                                   }
                               });

        /*
         * add screen handler for the button
         */
        child.addScreenHandler(b,
                               getButtonKey(SampleMeta.LOCATION_ADDR_STREET_ADDRESS),
                               new ScreenHandler<Object>() {
                                   public void onStateChange(StateChangeEvent event) {
                                       b.setEnabled(false);
                                   }
                               });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                t1.setValue(manager.getSampleEnvironmental()
                                   .getLocationAddress()
                                   .getStreetAddress());
                i.setResource(OpenELISResources.INSTANCE.commit());
                t1.setFocus(true);
            }
        });

        /*
         * add screen handler for the widget on the right
         */
        child.addScreenHandler(t2,
                               getRightWidgetKey(SampleMeta.LOCATION_ADDR_STREET_ADDRESS),
                               new ScreenHandler<String>() {
                                   public void onDataChange(DataChangeEvent event) {
                                       t2.setValue(null);
                                   }

                                   public void onStateChange(StateChangeEvent event) {
                                       t2.setEnabled(false);
                                   }
                               });
    }

    /**
     * Adds the row for environmental location city at the passed index
     */
    private void addLocationAddrCity(Screen child, int row) {
        Label<String> l1, l2;
        final TextBox<String> t1, t2;
        final Image i;
        HorizontalPanel h;
        final Button b;
        final ScheduledCommand cmd;

        /*
         * create the widgets and add them to the table panel
         */
        l1 = getLabel(Messages.get().address_city());

        t1 = getTextBox(Type.STRING, 98, TextBase.Case.UPPER, null, null);
        i = new Image();
        h = new HorizontalPanel();
        addWidgetAndImage(h, t1, i);

        b = getCopyButton();
        l2 = getLabel(Messages.get().address_city());
        t2 = getTextBox(Type.STRING, 98, TextBase.Case.UPPER, null, null);

        addWidgets(row, l1, h, b, l2, t2);

        /*
         * the scheduled command for setting the focus back to this widget when
         * it loses focus
         */
        cmd = getFocusCommand(t1);

        /*
         * add screen handler for the editable widget
         */
        child.addScreenHandler(t1, SampleMeta.LOCATION_ADDR_CITY, new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                t1.setValue(null);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                Integer numEdit;

                /*
                 * find out how many times the widget has been edited
                 */
                numEdit = updateNumEdit(SampleMeta.LOCATION_ADDR_CITY);

                /*
                 * if the value entered is different from the value in the
                 * manager, show the icon for "no match" and if this widget has
                 * been edited multiple times, show the value in the manager in
                 * the widget on the right; if the value entered is the same as
                 * the value in the manager, show the icon for "match"
                 */
                if (DataBaseUtil.isDifferent(event.getValue(), manager.getSampleEnvironmental()
                                                                      .getLocationAddress()
                                                                      .getCity())) {
                    i.setResource(OpenELISResources.INSTANCE.abort());
                    if (numEdit > 1) {
                        t2.setValue(manager.getSampleEnvironmental().getLocationAddress().getCity());
                        b.setEnabled(true);
                    }
                } else {
                    i.setResource(OpenELISResources.INSTANCE.commit());
                }

                /*
                 * set the focus back on the widget
                 */
                Scheduler.get().scheduleDeferred(cmd);
            }

            public void onStateChange(StateChangeEvent event) {
                t1.setEnabled(isState(UPDATE));
            }
        });

        /*
         * add screen handler for the image
         */
        child.addScreenHandler(i,
                               getImageKey(SampleMeta.LOCATION_ADDR_CITY),
                               new ScreenHandler<Object>() {
                                   public void onStateChange(StateChangeEvent event) {
                                       i.setResource(OpenELISResources.INSTANCE.blankIcon());
                                   }
                               });

        /*
         * add screen handler for the button
         */
        child.addScreenHandler(b,
                               getButtonKey(SampleMeta.LOCATION_ADDR_CITY),
                               new ScreenHandler<Object>() {
                                   public void onStateChange(StateChangeEvent event) {
                                       b.setEnabled(false);
                                   }
                               });

        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                t1.setValue(manager.getSampleEnvironmental().getLocationAddress().getCity());
                i.setResource(OpenELISResources.INSTANCE.commit());
                t1.setFocus(true);
            }
        });

        /*
         * add screen handler for the widget on the right
         */
        child.addScreenHandler(t2,
                               getRightWidgetKey(SampleMeta.LOCATION_ADDR_CITY),
                               new ScreenHandler<String>() {
                                   public void onDataChange(DataChangeEvent event) {
                                       t2.setValue(null);
                                   }

                                   public void onStateChange(StateChangeEvent event) {
                                       t2.setEnabled(false);
                                   }
                               });
    }

    /**
     * Adds the row for environmental location state at the passed index
     */
    private void addLocationAddrState(Screen child, int row) {
        Label<String> l1, l2;
        Dropdown<String> d1, d2;
        Image i;
        HorizontalPanel h;
        Button b;
        ArrayList<Item> model;

        l1 = getLabel(Messages.get().address_state());

        model = getDictionaryModel("state", CategoryMeta.getDictionaryEntry());
        d1 = getDropdown(Type.STRING, 42, TextBase.Case.UPPER, model);
        i = new Image();
        h = new HorizontalPanel();
        addWidgetAndImage(h, d1, i);

        b = getCopyButton();
        l2 = getLabel(Messages.get().address_state());
        d2 = getDropdown(Type.STRING, 42, TextBase.Case.UPPER, model);

        addWidgets(row, l1, h, b, l2, d2);
    }

    /**
     * Adds the row for environmental location state at the passed index
     */
    private void addLocationAddrZipCode(Screen child, int row) {
        Label<String> l1, l2;
        TextBox<String> t1, t2;
        Image i;
        HorizontalPanel h;
        Button b;

        l1 = getLabel(Messages.get().address_zipcode());

        t1 = getTextBox(Type.STRING, 75, TextBase.Case.UPPER, null, Messages.get()
                                                                            .gen_zipcodePattern());
        i = new Image();
        h = new HorizontalPanel();
        addWidgetAndImage(h, t1, i);

        b = getCopyButton();
        l2 = getLabel(Messages.get().address_zipcode());
        t2 = getTextBox(Type.STRING, 75, TextBase.Case.UPPER, null, Messages.get()
                                                                            .gen_zipcodePattern());

        addWidgets(row, l1, h, b, l2, t2);
    }

    /**
     * Adds the row for environmental location country at the passed index
     */
    private void addLocationAddrCountry(Screen child, int row) {
        Label<String> l1, l2;
        Dropdown<String> d1, d2;
        Image i;
        HorizontalPanel h;
        Button b;
        ArrayList<Item> model;

        l1 = getLabel(Messages.get().address_country());

        model = getDictionaryModel("country", CategoryMeta.getDictionaryEntry());
        d1 = getDropdown(Type.STRING, 231, null, model);
        i = new Image();
        h = new HorizontalPanel();
        addWidgetAndImage(h, d1, i);

        b = getCopyButton();
        l2 = getLabel(Messages.get().address_country());
        d2 = getDropdown(Type.STRING, 231, null, model);

        addWidgets(row, l1, h, b, l2, d2);
    }

    /**
     * Creates a Label; sets the passed value as its text and sets its style as
     * "Prompt"
     */
    private Label<String> getLabel(String text) {
        Label<String> l;

        l = new Label<String>(text + ":");
        l.setStyleName(OpenELISResources.INSTANCE.style().Prompt());

        return l;
    }

    /**
     * Creates a Calendar; sets "width" as its width and the integers as its
     * begin and end precisions
     */
    private Calendar getCalendar(int width, int begin, int end) {
        Calendar c;

        c = new Calendar();
        c.setWidth(width + "px");
        c.setBegin(begin);
        c.setEnd(end);

        return c;
    }

    /**
     * Creates a TextBox; sets "type" as its type e.g. Integer or String,
     * "width" as its width, "textCase" as its case e.g. UPPER or LOWER,
     * "maxLength" as its maximum allowed length and "mask" as its mask
     */
    private TextBox getTextBox(Type type, int width, TextBase.Case textCase, Integer maxLength,
                               String mask) {
        TextBox t;

        t = null;
        switch (type) {
            case INTEGER:
                t = new TextBox<Integer>();
                t.setHelper(new IntegerHelper());
                break;
            case DOUBLE:
                t = new TextBox<Double>();
                t.setHelper(new DoubleHelper());
                break;
            case STRING:
                t = new TextBox<String>();
                break;
        }

        t.setWidth(width + "px");
        if (textCase != null)
            t.setCase(textCase);
        if (maxLength != null)
            t.setMaxLength(maxLength);
        if (mask != null)
            t.setMask(mask);

        return t;
    }

    /**
     * Creates the button used to copy the value from the widget on the right to
     * the one on the left
     */
    private Button getCopyButton() {
        Button b;

        b = new Button();
        b.setText(Messages.get().moveLeft());
        b.setCss(OpenELISResources.INSTANCE.FormFieldButton());

        return b;
    }

    /**
     * Creates a Dropdown; sets "type" as its type e.g. Integer or String,
     * "width" as its width, "textCase" as its case e.g. UPPER or LOWER, and
     * "model" as its model
     */
    private Dropdown getDropdown(Type type, int width, TextBase.Case textCase, ArrayList<Item> model) {
        Dropdown d;

        d = null;
        switch (type) {
            case INTEGER:
                d = new Dropdown<Integer>();
                break;
            case STRING:
                d = new Dropdown<String>();
                break;
        }

        d.setWidth(width + "px");
        if (textCase != null)
            d.setCase(textCase);

        d.setModel(model);

        return d;
    }

    /**
     * Creates an autocomplete for showing organizations; sets "width" as its
     * width, "textCase" as its case e.g. UPPER or LOWER, and "dropWidth" as the
     * total width of its table; the table's columns show various fields of an
     * organization
     */
    private AutoComplete getOrgAutocomplete(int width, TextBase.Case textCase, int dropWidth) {
        final AutoComplete a;
        Table t;

        /*
         * set the basic fields e.g. dimensions
         */
        a = getAutocomplete(width, textCase, dropWidth);

        /*
         * create and set the table
         */
        t = new Table();
        t.setVisibleRows(10);
        t.setHeader(true);

        t.addColumn(getColumn(250, Messages.get().gen_name(), new LabelCell()));
        t.addColumn(getColumn(70, Messages.get().address_aptSuite(), new LabelCell()));
        t.addColumn(getColumn(110, Messages.get().address_street(), new LabelCell()));
        t.addColumn(getColumn(100, Messages.get().address_city(), new LabelCell()));
        t.addColumn(getColumn(20, Messages.get().address_st(), new LabelCell()));

        a.setPopupContext(t);

        /*
         * add the matches handler
         */
        a.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                Item<Integer> row;
                OrganizationDO data;
                ArrayList<OrganizationDO> list;
                ArrayList<Item<Integer>> model;

                setBusy();
                try {
                    list = OrganizationService1Impl.INSTANCE.fetchByIdOrName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<Item<Integer>>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new Item<Integer>(5);
                        data = list.get(i);

                        row.setKey(data.getId());
                        row.setData(data);
                        row.setCell(0, data.getName());
                        row.setCell(1, data.getAddress().getMultipleUnit());
                        row.setCell(2, data.getAddress().getStreetAddress());
                        row.setCell(3, data.getAddress().getCity());
                        row.setCell(4, data.getAddress().getState());

                        model.add(row);
                    }
                    a.showAutoMatches(model);
                } catch (Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
                clearStatus();
            }
        });

        return a;
    }

    /**
     * Creates an autocomplete for showing organizations; sets "width" as its
     * width, "textCase" as its case e.g. UPPER or LOWER, and "dropWidth" as the
     * total width of its table; the table's columns show various fields of a
     * project
     */
    private AutoComplete getProjectAutocomplete(int width, TextBase.Case textCase, int dropWidth) {
        final AutoComplete a;
        Table t;

        /*
         * set the basic fields e.g. dimensions
         */
        a = getAutocomplete(width, textCase, dropWidth);

        /*
         * create and set the table
         */
        t = new Table();
        t.setVisibleRows(10);
        t.setHeader(true);

        t.addColumn(getColumn(150, Messages.get().gen_name(), new LabelCell()));
        t.addColumn(getColumn(275, Messages.get().gen_description(), new LabelCell()));

        a.setPopupContext(t);

        /*
         * add the matches handler
         */
        a.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                Item<Integer> row;
                ArrayList<ProjectDO> list;
                ArrayList<Item<Integer>> model;

                setBusy();
                try {
                    list = ProjectService.get()
                                         .fetchActiveByName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<Item<Integer>>();
                    for (ProjectDO p : list) {
                        row = new Item<Integer>(2);

                        row.setKey(p.getId());
                        row.setCell(0, p.getName());
                        row.setCell(1, p.getDescription());
                        row.setData(p);
                        model.add(row);
                    }
                    a.showAutoMatches(model);
                } catch (Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
                clearStatus();
            }
        });

        return a;
    }

    /**
     * Creates an autocomplete; sets "width" as its width, "textCase" as its
     * case e.g. UPPER or LOWER, and "dropWidth" as the total width of its table
     */
    private AutoComplete getAutocomplete(int width, TextBase.Case textCase, int dropWidth) {
        AutoComplete a;

        a = new AutoComplete();
        a.setWidth(width + "px");
        a.setCase(textCase);
        a.setDropWidth(dropWidth + "px");

        return a;
    }

    /**
     * Creates a table column; sets "width" as its width, "label" as its header
     * , and "renderer" as the class that handles showing the data in the column
     */
    private Column getColumn(int width, String label, CellRenderer renderer) {
        Column c;

        c = new Column();
        c.setWidth(250);
        c.setLabel(Messages.get().gen_name());
        c.setCellRenderer(new LabelCell());

        return c;
    }

    /**
     * Creates the model for a dropdown filled from dictionary entries;
     * "category" is the category to which the entries belong and "key" is name
     * of the field that should be set as the key of each item in the model
     */
    private ArrayList<Item> getDictionaryModel(String category, String key) {
        Item row;
        ArrayList<Item> model;

        if (models == null)
            models = new HashMap<String, ArrayList<Item>>();

        model = models.get(category);
        if (model != null)
            return model;

        model = new ArrayList<Item>();
        models.put(category, model);
        for (DictionaryDO d : CategoryCache.getBySystemName(category)) {
            row = new Item(1);
            if (CategoryMeta.getDictionaryId().equals(key))
                row.setKey(d.getId());
            else if (CategoryMeta.getDictionaryEntry().equals(key))
                row.setKey(d.getEntry());

            row.setCell(0, d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        return model;
    }

    /**
     * Adds the passed widget "w" to the passed panel; also adds an Image widget
     * next to "w" for showing the image indicating whether the value entered in
     * "w" matches the value in the manager
     */
    private void addWidgetAndImage(HorizontalPanel h, Widget w, Image i) {
        HorizontalPanel h1;

        h.add(w);

        /*
         * some padding between the widget and the image
         */
        h1 = new HorizontalPanel();
        h1.setWidth("5px");
        h.add(h1);

        /*
         * the blank icon is used as a placeholder; without this icon, there's
         * no space between the widget and the button in the row, until another
         * icon is shown
         */
        i.setResource(OpenELISResources.INSTANCE.blankIcon());
        h.add(i);
    }

    /**
     * Creates a row at the passed index in the table panel and adds the passed
     * widgets to that row
     */
    private void addWidgets(int row, Widget... widgets) {
        // for (int i = 0; i < widgets.length; i++ )
        // widgetTable.setWidget(row, i, widgets[i]);
    }

    /**
     * Returns the command that makes the focus get set to the passed widget as
     * soon as it loses focus
     */
    private ScheduledCommand getFocusCommand(final Focusable w) {
        ScheduledCommand cmd;

        cmd = new ScheduledCommand() {
            @Override
            public void execute() {
                w.setFocus(true);
            }
        };

        return cmd;
    }

    /**
     * Creates a key for the image in the row for the widget with the passed key
     */
    private String getImageKey(String editWidgetKey) {
        return editWidgetKey + "_image";
    }

    /**
     * Creates a key for the button in the row for the widget with the passed
     * key
     */
    private String getButtonKey(String editWidgetKey) {
        return editWidgetKey + "_button";
    }

    /**
     * Creates a key for the widget on the right in the row for the widget with
     * the passed key
     */
    private String getRightWidgetKey(String editWidgetKey) {
        return editWidgetKey + "_1";
    }

    /**
     * Increments the number of times the widget with the passed key has been
     * edited by one; returns the incremented value
     */
    private Integer updateNumEdit(String key) {
        Integer numEdit;

        numEdit = numEdits.get(key);
        if (numEdit == null)
            numEdit = 1;
        else
            numEdit++ ;
        numEdits.put(key, numEdit);

        return numEdit;
    }
}