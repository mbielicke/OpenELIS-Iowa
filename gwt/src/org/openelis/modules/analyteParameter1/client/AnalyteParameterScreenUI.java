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
package org.openelis.modules.analyteParameter1.client;

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.Screen.ShortKeys.*;
import static org.openelis.ui.screen.Screen.Validation.Status.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalyteParameterViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.QcAnalyteViewDO;
import org.openelis.domain.QcDO;
import org.openelis.domain.ReferenceIdTableIdNameVO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestMethodVO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.manager.AnalyteParameterManager1;
import org.openelis.manager.QcAnalyteManager;
import org.openelis.manager.TestAnalyteManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestTypeOfSampleManager;
import org.openelis.meta.AnalyteParameterMeta;
import org.openelis.modules.qc.client.QcService;
import org.openelis.modules.test.client.TestService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.BeforeGetMatchesEvent;
import org.openelis.ui.event.BeforeGetMatchesHandler;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.ScreenNavigator;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;
import org.openelis.ui.widget.tree.Node;
import org.openelis.ui.widget.tree.Tree;
import org.openelis.ui.widget.tree.event.NodeAddedEvent;
import org.openelis.ui.widget.tree.event.NodeAddedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class AnalyteParameterScreenUI extends Screen {

    @UiTemplate("AnalyteParameter.ui.xml")
    interface AnalyteParameterUiBinder extends UiBinder<Widget, AnalyteParameterScreenUI> {
    };

    public static final AnalyteParameterUiBinder                   uiBinder           = GWT.create(AnalyteParameterUiBinder.class);

    protected AnalyteParameterManager1                             manager;

    protected TestManager                                          testManager;

    protected ModulePermission                                     userPermission;

    protected ScreenNavigator<ReferenceIdTableIdNameVO>            nav;

    @UiField
    protected Table                                                atozTable;

    @UiField
    protected Button                                               queryButton, previousButton,
                    nextButton, addButton, updateButton, commitButton, abortButton, atozNextButton,
                    atozPrevButton, addAnalyteButton, addParameterButton, removeRowButton,
                    expandButton, collapseButton;

    @UiField
    protected Dropdown<Integer>                                    atozReferenceTable,
                    referenceTable, typeOfSample, unitOfMeasure;

    @UiField
    protected AutoComplete                                         referenceName;

    @UiField
    protected Tree                                                 tree;

    protected AsyncCallbackUI<ArrayList<ReferenceIdTableIdNameVO>> queryCall;

    protected AsyncCallbackUI<AnalyteParameterManager1>            addCall, fetchForUpdateCall,
                    updateCall, fetchByIdCall, unlockCall;

    protected static final String                                  REFERENCE_NAME_KEY = "referenceName",
                    ANALYTE_LEAF = "analyte", PARAMETER_LEAF = "parameter";

    protected static int                                           DEEPEST_LEVEL      = 1;

    protected ArrayList<Item<Integer>>                             allSampleTypesModel,
                    allUnitsModel;

    protected HashMap<Integer, HashSet<Integer>>                   unitTypesMap;

    public AnalyteParameterScreenUI(WindowInt window) throws Exception {
        setWindow(window);

        userPermission = UserCache.getPermission().getModule("analyteparameter");
        if (userPermission == null)
            throw new PermissionException(Messages.get()
                                                  .gen_screenPermException("Analyte Parameter Screen"));

        try {
            CategoryCache.getBySystemNames("analyte_parameter_type",
                                           "type_of_sample",
                                           "unit_of_measure");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            window.close();
        }

        initWidget(uiBinder.createAndBindUi(this));

        manager = null;

        initialize();
        setState(DEFAULT);
        fireDataChange();

        logger.fine("Analyte Parameter Screen Opened");
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        Integer refTableId;
        Item<Integer> row;
        ArrayList<Item<Integer>> model;
        ArrayList<DictionaryDO> list;

        //
        // button panel buttons
        //
        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                queryButton.setEnabled(isState(QUERY, DEFAULT, DISPLAY) &&
                                       userPermission.hasSelectPermission());
                if (isState(QUERY)) {
                    queryButton.lock();
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
                addButton.setEnabled(isState(ADD, DEFAULT, DISPLAY) &&
                                     userPermission.hasAddPermission());
                if (isState(ADD)) {
                    addButton.lock();
                    addButton.setPressed(true);
                }
            }
        });

        addShortcut(addButton, 'a', CTRL);

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
                commitButton.setEnabled(isState(QUERY, ADD, UPDATE));
            }
        });

        addShortcut(commitButton, 'm', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                abortButton.setEnabled(isState(QUERY, ADD, UPDATE));
            }
        });

        addShortcut(abortButton, 'o', CTRL);

        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator<ReferenceIdTableIdNameVO>(atozTable,
                                                            atozNextButton,
                                                            atozPrevButton) {
            public void executeQuery(final Query query) {
                setBusy(Messages.get().gen_querying());

                if (queryCall == null) {
                    queryCall = new AsyncCallbackUI<ArrayList<ReferenceIdTableIdNameVO>>() {
                        public void success(ArrayList<ReferenceIdTableIdNameVO> result) {
                            clearStatus();
                            setQueryResult(result);
                        }

                        public void notFound() {
                            setQueryResult(null);
                            setState(DEFAULT);
                            setDone(Messages.get().gen_noRecordsFound());
                        }

                        public void lastPage() {
                            setQueryResult(null);
                            setError(Messages.get().gen_noMoreRecordInDir());
                        }

                        public void failure(Throwable error) {
                            setQueryResult(null);
                            Window.alert("Error: Analyte Parameter call query failed; " +
                                         error.getMessage());
                            setError(Messages.get().gen_queryFailed());
                        }
                    };
                }

                query.setRowsPerPage(20);
                AnalyteParameterService1.get().query(query, queryCall);
            }

            public boolean fetch(ReferenceIdTableIdNameVO entry) {
                if (entry != null)
                    fetchByRefIdRefTableId(entry.getReferenceId(), entry.getReferenceTableId());
                else
                    fetchByRefIdRefTableId(null, null);
                return true;
            }

            public ArrayList<Item<Integer>> getModel() {
                String name;
                ArrayList<ReferenceIdTableIdNameVO> result;
                ArrayList<Item<Integer>> model;

                model = null;
                result = nav.getQueryResult();
                if (result != null) {
                    model = new ArrayList<Item<Integer>>();
                    for (ReferenceIdTableIdNameVO entry : result) {
                        name = null;
                        if (Constants.table().TEST.equals(entry.getReferenceTableId()))
                            name = DataBaseUtil.concatWithSeparator(entry.getReferenceName(),
                                                                    ", ",
                                                                    entry.getReferenceDescription());
                        else if (Constants.table().QC.equals(entry.getReferenceTableId()))
                            name = getQcLabel(entry.getReferenceId(), entry.getReferenceName());
                        else if (Constants.table().PROVIDER.equals(entry.getReferenceTableId()))
                            name = "";

                        model.add(new Item<Integer>(entry.getReferenceId(),
                                                    entry.getReferenceTableId(),
                                                    name));
                    }
                }

                return model;
            }
        };

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                nav.enable(isState(DEFAULT, DISPLAY) && userPermission.hasSelectPermission());
            }
        });

        addScreenHandler(referenceTable,
                         AnalyteParameterMeta.getReferenceTableId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 referenceTable.setValue(getReferenceTableId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 /*
                                  * this dropdown is not set in query mode to
                                  * prevent multiple selection, so
                                  * ValueChangeEvent will get fired even when a
                                  * user is trying to query for something
                                  */
                                 if ( !isState(QUERY))
                                     refreshScreen(null, event.getValue(), null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 referenceTable.setEnabled(isState(QUERY, ADD));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? referenceName : tree;
                             }
                         });

        addScreenHandler(referenceName, REFERENCE_NAME_KEY, new ScreenHandler<AutoCompleteValue>() {
            public void onDataChange(DataChangeEvent event) {
                referenceName.setValue(getReferenceTableId(), getReferenceName());
            }

            public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                Integer refTableId;
                AutoCompleteValue val;

                val = event.getValue();
                refTableId = getReferenceTableId();
                if (val != null)
                    refreshScreen(val.getId(), refTableId, val.getDisplay());
                else
                    refreshScreen(null, refTableId, null);
            }

            public void onStateChange(StateChangeEvent event) {
                referenceName.setEnabled(isState(QUERY, ADD));
                referenceName.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? tree : referenceTable;
            }
        });

        referenceName.addBeforeGetMatchesHandler(new BeforeGetMatchesHandler() {
            @Override
            public void onBeforeGetMatches(BeforeGetMatchesEvent event) {
                if (getReferenceTableId() == null) {
                    setError(Messages.get().analyteParameter_selectType());
                    event.cancel();
                }
            }
        });

        referenceName.addGetMatchesHandler(new GetMatchesHandler() {
            @Override
            public void onGetMatches(GetMatchesEvent event) {
                String search;

                search = QueryFieldUtil.parseAutocomplete(event.getMatch());
                try {
                    if (Constants.table().TEST.equals(referenceTable.getValue()))
                        referenceName.showAutoMatches(getTestMatches(search));
                    else if (Constants.table().QC.equals(referenceTable.getValue()))
                        referenceName.showAutoMatches(getQcMatches(search));
                    else if (Constants.table().PROVIDER.equals(referenceTable.getValue()))
                        referenceName.showAutoMatches(getProviderMatches(search));
                } catch (NotFoundException e) {
                    // do nothing
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        });

        addScreenHandler(tree, "tree", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                tree.setRoot(getRoot());
            }

            public void onStateChange(StateChangeEvent event) {
                tree.setEnabled(isState(DISPLAY, ADD, UPDATE));
            }
        });

        tree.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            @Override
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                int col;
                Integer refTableId, typeId;
                Node node;

                node = tree.getNodeAt(tree.getSelectedNode());
                if ( !isState(ADD, UPDATE)) {
                    event.cancel();
                } else if (ANALYTE_LEAF.equals(node.getType())) {
                    refTableId = getReferenceTableId();
                    col = event.getCol();
                    if (col == 0 || !Constants.table().TEST.equals(refTableId)) {
                        event.cancel();
                    } else if (col == 2) {
                        /*
                         * if the user is trying to select the unit, then only
                         * allow it if a sample type is specified and also
                         * disable the units that are not valid for the type
                         */
                        typeId = tree.getValueAt(event.getRow(), 1);
                        if (typeId == null) {
                            event.cancel();
                            setError(Messages.get().analyteParameter_selectSampleTypeBeforeUnit());
                        } else {
                            disableAndEnableUnits(typeId);
                        }
                    }
                }
            }
        });

        tree.addCellEditedHandler(new CellEditedHandler() {
            @Override
            public void onCellUpdated(CellEditedEvent event) {
                int row, col;
                Node node;
                Integer typeId, unitId;
                HashSet<Integer> types;

                node = tree.getNodeAt(tree.getSelectedNode());
                row = event.getRow();
                col = event.getCol();
                if (ANALYTE_LEAF.equals(node.getType())) {
                    if (col == 1) {
                        typeId = tree.getValueAt(row, col);
                        unitId = tree.getValueAt(row, 2);
                        if (unitId != null) {
                            /*
                             * if the unit selected for the analyte is not valid
                             * for the currently selected sample type, then
                             * blank the unit
                             */
                            types = unitTypesMap.get(unitId);
                            if ( !types.contains(typeId)) {
                                tree.setValueAt(row, 2, null);
                                tree.refreshNode(node);
                            }
                        }
                    }
                } else {

                }
            }
        });

        tree.addNodeAddedHandler(new NodeAddedHandler() {
            @Override
            public void onNodeAdded(NodeAddedEvent event) {
                int index;
                String uid;
                AnalyteParameterViewDO data, newData;
                Node selNode, newNode;

                selNode = tree.getNodeAt(tree.getSelectedNode());
                newNode = event.getNode();
                if (ANALYTE_LEAF.equals(newNode.getType())) {
                    /*
                     * the newly added node is an analyte node, so a DO will be
                     * created for it with the same analyte as the currently
                     * selected node and that DO will be added after the DO for
                     * the last child of the currently selected node, if it has
                     * any children; otherwise it will be added after the DO for
                     * the currently selected node
                     */
                    if (selNode.hasChildren())
                        uid = selNode.getLastChild().getData();
                    else
                        uid = selNode.getData();

                    data = manager.getObject(uid);
                    index = manager.analyteParameter.getIndex(data);
                    newData = manager.analyteParameter.add(index + 1);
                    newData.setAnalyteId(data.getAnalyteId());
                    newData.setAnalyteName(data.getAnalyteName());
                    newNode.setCell(0, newData.getAnalyteName());
                    newNode.setData(Constants.uid().get(newData));
                }
            }
        });

        addScreenHandler(addAnalyteButton, "addAnalyteButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                addAnalyteButton.setEnabled(isState(ADD, UPDATE));
            }

            public Widget onTab(boolean forward) {
                return forward ? addParameterButton : tree;
            }
        });

        addScreenHandler(addParameterButton, "addParameterButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                addParameterButton.setEnabled(isState(ADD, UPDATE));
            }

            public Widget onTab(boolean forward) {
                return forward ? removeRowButton : addAnalyteButton;
            }
        });

        addScreenHandler(removeRowButton, "removeRowButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                removeRowButton.setEnabled(isState(ADD, UPDATE));
            }

            public Widget onTab(boolean forward) {
                return forward ? expandButton : addParameterButton;
            }
        });

        addScreenHandler(expandButton, "expandButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                expandButton.setEnabled(isState(DISPLAY, ADD, UPDATE));
            }

            public Widget onTab(boolean forward) {
                return forward ? collapseButton : removeRowButton;
            }
        });

        addScreenHandler(collapseButton, "collapseButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                collapseButton.setEnabled(isState(DISPLAY, ADD, UPDATE));
            }

            public Widget onTab(boolean forward) {
                return forward ? tree : expandButton;
            }
        });

        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                if (isState(ADD, UPDATE)) {
                    event.cancel();
                    setError(Messages.get().gen_mustCommitOrAbort());
                }
            }
        });

        // type dropdown
        model = new ArrayList<Item<Integer>>();
        list = CategoryCache.getBySystemName("analyte_parameter_type");
        refTableId = null;
        for (DictionaryDO data : list) {
            switch (data.getSystemName()) {
                case "analyte_param_type_test":
                    refTableId = Constants.table().TEST;
                    break;
                case "analyte_param_type_qc":
                    refTableId = Constants.table().QC;
                    break;
                case "analyte_param_type_provider":
                    refTableId = Constants.table().PROVIDER;
                    break;
            }
            row = new Item<Integer>(refTableId, data.getEntry());
            row.setEnabled("Y".equals(data.getIsActive()));
            model.add(row);
        }

        referenceTable.setModel(model);
        atozReferenceTable.setModel(model);

        // sample type dropdown
        allSampleTypesModel = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("type_of_sample")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            allSampleTypesModel.add(row);
        }
        typeOfSample.setModel(allSampleTypesModel);

        // unit of measure dropdown
        allUnitsModel = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("unit_of_measure")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            allUnitsModel.add(row);
        }
        unitOfMeasure.setModel(allUnitsModel);
    }

    /*
     * basic button methods
     */
    @UiHandler("queryButton")
    protected void query(ClickEvent event) {
        manager = null;
        typeOfSample.setModel(allSampleTypesModel);
        unitOfMeasure.setModel(allUnitsModel);
        setState(QUERY);
        fireDataChange();
        referenceTable.setFocus(true);
        setDone(Messages.get().gen_enterFieldsToQuery());
    }

    @UiHandler("previousButton")
    protected void previous(ClickEvent event) {
        nav.previous();
    }

    @UiHandler("nextButton")
    protected void next(ClickEvent event) {
        nav.next();
    }

    @UiHandler("addButton")
    protected void add(ClickEvent event) {
        if (addCall == null) {
            addCall = new AsyncCallbackUI<AnalyteParameterManager1>() {
                public void success(AnalyteParameterManager1 result) {
                    manager = result;
                    setState(ADD);
                    fireDataChange();
                    referenceTable.setFocus(true);
                    setDone(Messages.get().gen_enterInformationPressCommit());
                }

                public void failure(Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    clearStatus();
                }
            };
        }

        AnalyteParameterService1.get().getInstance(null, null, null, addCall);
    }

    @UiHandler("updateButton")
    protected void update(ClickEvent event) {
        setBusy(Messages.get().lockForUpdate());

        if (fetchForUpdateCall == null) {
            fetchForUpdateCall = new AsyncCallbackUI<AnalyteParameterManager1>() {
                public void success(AnalyteParameterManager1 result) {
                    manager = result;
                    if (Constants.table().TEST.equals(manager.getReferenceTableId())) {
                        try {
                            testManager = TestService.get().fetchById(manager.getReferenceId());
                            setTypeAndUnitModels();
                        } catch (Exception e) {
                            Window.alert(e.getMessage());
                            logger.log(Level.SEVERE, e.getMessage(), e);
                            clearStatus();
                            return;
                        }
                    }
                    setState(UPDATE);
                    fireDataChange();
                    referenceTable.setFocus(true);
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

        AnalyteParameterService1.get().fetchForUpdate(manager.getReferenceId(),
                                                      manager.getReferenceTableId(),
                                                      fetchForUpdateCall);
    }

    @UiHandler("commitButton")
    protected void commit(ClickEvent event) {
        commit();
    }

    private void commit() {
        Validation validation;

        finishEditing();
        clearErrors();

        validation = validate();

        if (validation.getStatus() != VALID) {
            setError(Messages.get().gen_correctErrors());
            return;
        }

        switch (super.state) {
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

    @UiHandler("abortButton")
    protected void abort(ClickEvent event) {
        finishEditing();
        clearErrors();
        setBusy(Messages.get().gen_cancelChanges());

        if (isState(QUERY)) {
            manager = null;
            setState(DEFAULT);
            fireDataChange();
            setDone(Messages.get().gen_queryAborted());
        } else if (isState(ADD)) {
            manager = null;
            setState(DEFAULT);
            fireDataChange();
            setDone(Messages.get().gen_addAborted());
        } else if (isState(UPDATE)) {
            if (unlockCall == null) {
                unlockCall = new AsyncCallbackUI<AnalyteParameterManager1>() {
                    public void success(AnalyteParameterManager1 result) {
                        manager = result;
                        setState(DISPLAY);
                        fireDataChange();
                        setDone(Messages.get().gen_updateAborted());
                    }

                    public void failure(Throwable e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        clearStatus();
                    }
                };
            }

            AnalyteParameterService1.get().unlock(manager.getReferenceId(),
                                                  manager.getReferenceTableId(),
                                                  unlockCall);
        }
        testManager = null;
        unitTypesMap = null;
    }

    @UiHandler("addAnalyteButton")
    protected void addAnalyte(ClickEvent event) {
        int index;
        Node node, newNode;

        index = tree.getSelectedNode();
        if (index == -1)
            return;

        node = tree.getNodeAt(index);
        /*
         * if a child node was selected then add the new node after its parent
         */
        if (PARAMETER_LEAF.equals(node.getType()))
            index = tree.getNodeViewIndex(node.getParent());

        newNode = new Node(3);
        newNode.setType(ANALYTE_LEAF);
        tree.addNodeAt(index + 1, newNode);
        tree.refreshNode(newNode);
        tree.selectNodeAt(newNode);
    }

    @UiHandler("addParameterButton")
    protected void addParameter(ClickEvent event) {
        int index;
        Node node, newNode;

        index = tree.getSelectedNode();
        if (index == -1)
            return;

        node = tree.getNodeAt(index);
        if (ANALYTE_LEAF.equals(node.getType())) {
            newNode = new Node(5);
            newNode.setType(PARAMETER_LEAF);
            tree.addNodeAt(node, newNode, 0);
            tree.open(node);
            tree.selectNodeAt(newNode);
        }
    }

    @UiHandler("removeRowButton")
    protected void removeRow(ClickEvent event) {

    }

    protected void commitQuery() {
        Query query;

        query = new Query();
        query.setFields(getQueryFields());
        nav.setQuery(query);
    }

    protected void commitUpdate() {
        if (isState(ADD))
            setBusy(Messages.get().gen_adding());
        else
            setBusy(Messages.get().gen_updating());

        if (updateCall == null) {
            updateCall = new AsyncCallbackUI<AnalyteParameterManager1>() {
                public void success(AnalyteParameterManager1 result) {
                    manager = result;
                    setState(DISPLAY);
                    fireDataChange();
                    clearStatus();
                    testManager = null;
                    unitTypesMap = null;
                }

                public void validationErrors(ValidationErrorsList e) {
                    showErrors(e);
                }

                public void failure(Throwable e) {
                    if (isState(ADD))
                        Window.alert("commitAdd(): " + e.getMessage());
                    else
                        Window.alert("commitUpdate(): " + e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    clearStatus();
                }
            };
        }

        // OrderService1.get().update(manager, ignoreWarning, updateCall);
    }

    /**
     * Expands the tree at every level
     */
    @UiHandler("expandButton")
    protected void expand(ClickEvent event) {
        tree.expand(DEEPEST_LEVEL);
    }

    /**
     * Collpases the tree at every level
     */
    @UiHandler("collapseButton")
    protected void collapse(ClickEvent event) {
        tree.collapse();
    }

    /**
     * Returns the list of fields that the user wants to query by
     */
    public ArrayList<QueryData> getQueryFields() {
        Integer refTableId;
        QueryData field;
        ArrayList<QueryData> fields;

        fields = super.getQueryFields();
        refTableId = referenceTable.getValue();
        if (refTableId == null)
            return fields;

        field = null;
        for (QueryData f : fields) {
            if (REFERENCE_NAME_KEY.equals(f.getKey())) {
                field = f;
                break;
            }
        }

        /*
         * if reference name is not queried for then create a field for it to
         * specify the table in the database that the name is from e.g. qc or
         * test
         */
        if (field == null) {
            field = new QueryData();
            field.setQuery("*");
            field.setType(QueryData.Type.STRING);
            fields.add(field);
        }

        if (Constants.table().TEST.equals(refTableId))
            field.setKey(AnalyteParameterMeta.getTestName());
        else if (Constants.table().QC.equals(refTableId))
            field.setKey(AnalyteParameterMeta.getQcName());
        else if (Constants.table().PROVIDER.equals(refTableId))
            field.setKey(AnalyteParameterMeta.getProviderName());

        /*
         * create a field for reference table, because that dropdown is not in
         * query mode to prevent multiple selection
         */
        field = new QueryData();
        field.setKey(AnalyteParameterMeta.getReferenceTableId());
        field.setQuery(refTableId.toString());
        field.setType(QueryData.Type.INTEGER);
        fields.add(field);

        return fields;
    }

    private void fetchByRefIdRefTableId(Integer refId, Integer refTableId) {
        if (refId == null || refTableId == null) {
            manager = null;
            setState(DEFAULT);
            fireDataChange();
            clearStatus();
        } else {
            setBusy(Messages.get().gen_fetching());
            if (fetchByIdCall == null) {
                fetchByIdCall = new AsyncCallbackUI<AnalyteParameterManager1>() {
                    public void success(AnalyteParameterManager1 result) {
                        manager = result;
                        setState(DISPLAY);
                    }

                    public void notFound() {
                        fetchByRefIdRefTableId(null, null);
                        setDone(Messages.get().gen_noRecordsFound());
                    }

                    public void failure(Throwable e) {
                        fetchByRefIdRefTableId(null, null);
                        Window.alert(Messages.get().gen_fetchFailed() + e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                    }

                    public void finish() {
                        fireDataChange();
                        clearStatus();
                    }
                };
            }

            AnalyteParameterService1.get().fetchByReferenceIdReferenceTableId(refId,
                                                                              refTableId,
                                                                              fetchByIdCall);
        }
    }

    private Integer getReferenceTableId() {
        if (manager == null)
            return null;
        return manager.getReferenceTableId();
    }

    private String getReferenceName() {
        if (manager == null)
            return null;
        return manager.getReferenceName();
    }

    private String getQcLabel(Integer qcId, String qcName) {
        StringBuilder sb;

        if (qcId == null || DataBaseUtil.isEmpty(qcName))
            return null;

        sb = new StringBuilder();

        sb.append(qcName);
        sb.append(" ");
        sb.append("(");
        sb.append(qcId.toString());
        sb.append(")");

        return sb.toString();
    }

    private Node getRoot() {
        Node root, anode, rnode;
        AnalyteParameterViewDO data, prev;

        root = new Node();
        if (manager == null)
            return root;

        prev = null;
        anode = null;
        for (int i = 0; i < manager.analyteParameter.count(); i++ ) {
            data = manager.analyteParameter.get(i);
            /*
             * create a parent for every new combination of analyte, sample type
             * and unit
             */
            if (prev == null || !DataBaseUtil.isSame(prev.getAnalyteId(), data.getAnalyteId()) ||
                !DataBaseUtil.isSame(prev.getTypeOfSampleId(), data.getTypeOfSampleId()) ||
                !DataBaseUtil.isSame(prev.getUnitOfMeasureId(), data.getUnitOfMeasureId())) {

                anode = new Node(3);
                anode.setType(ANALYTE_LEAF);
                anode.setCell(0, data.getAnalyteName());
                anode.setCell(1, data.getTypeOfSampleId());
                anode.setCell(2, data.getUnitOfMeasureId());
                anode.setData(Constants.uid().get(data));
                root.add(anode);
            }

            if (data.getActiveBegin() != null || data.getActiveEnd() != null ||
                data.getP1() != null || data.getP2() != null || data.getP3() != null) {
                /*
                 * create a child for every date range and P1, P2, P3 for an
                 * analyte
                 */
                rnode = new Node(5);
                rnode.setType(PARAMETER_LEAF);
                rnode.setCell(0, data.getActiveBegin());
                rnode.setCell(1, data.getActiveEnd());
                rnode.setCell(2, data.getP1());
                rnode.setCell(3, data.getP2());
                rnode.setCell(4, data.getP3());
                rnode.setData(Constants.uid().get(data));
                anode.add(rnode);
            }
            prev = data;
        }

        return root;
    }

    private void refreshScreen(Integer refId, Integer refTableId, String refName) {

        try {
            manager = AnalyteParameterService1.get().getInstance(refId, refTableId, refName);
            testManager = null;
            unitTypesMap = null;
            if (refId != null && refTableId != null)
                addAnalytesFromReference(refId, refTableId, refName);
            if (testManager != null) {
                setTypeAndUnitModels();
            } else {
                typeOfSample.setModel(null);
                unitOfMeasure.setModel(null);
            }
            setState(state);
            fireDataChange();
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private ArrayList<Item<Integer>> getTestMatches(String search) throws Exception {
        ArrayList<Item<Integer>> model;
        ArrayList<TestMethodVO> tests;

        model = new ArrayList<Item<Integer>>();
        try {
            tests = TestService.get().fetchActiveByName(search);
            for (TestMethodVO data : tests)
                model.add(new Item<Integer>(data.getTestId(),
                                            DataBaseUtil.concatWithSeparator(data.getTestName(),
                                                                             ", ",
                                                                             data.getMethodName())));
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return model;
    }

    private ArrayList<Item<Integer>> getQcMatches(String search) throws Exception {
        ArrayList<Item<Integer>> model;
        ArrayList<QcDO> qcs;

        model = new ArrayList<Item<Integer>>();
        qcs = QcService.get().fetchActiveByName(search);
        for (QcDO data : qcs)
            model.add(new Item<Integer>(data.getId(), getQcLabel(data.getId(), data.getName())));

        return model;
    }

    private ArrayList<Item<Integer>> getProviderMatches(String search) {
        // TODO Auto-generated method stub
        return null;
    }

    private void addAnalytesFromReference(Integer refId, Integer refTableId, String refName) throws Exception {
        int i, j;
        TestAnalyteViewDO ta;
        TestAnalyteManager tam;
        QcAnalyteManager qcm;
        QcAnalyteViewDO qca;
        AnalyteParameterViewDO data;
        ArrayList<TestAnalyteViewDO> row;
        ArrayList<ArrayList<TestAnalyteViewDO>> tas;

        if (Constants.table().TEST.equals(refTableId)) {
            testManager = TestManager.fetchById(refId);
            tam = testManager.getTestAnalytes();
            tas = tam.getAnalytes();
            /*
             * load analyte parameters in this manager from the test's analytes
             */
            for (i = 0; i < tas.size(); i++ ) {
                row = tas.get(i);
                for (j = 0; j < row.size(); j++ ) {
                    ta = row.get(j);
                    if ("Y".equals(ta.getIsColumn()))
                        continue;
                    data = manager.analyteParameter.add();
                    loadParameter(refId,
                                  refTableId,
                                  refName,
                                  ta.getAnalyteId(),
                                  ta.getAnalyteName(),
                                  data);
                }
            }
        } else if (Constants.table().QC.equals(refTableId)) {
            qcm = QcAnalyteManager.fetchByQcId(refId);
            /*
             * load analyte parameters in this manager from the qc's analytes
             */
            for (i = 0; i < qcm.count(); i++ ) {
                qca = qcm.getAnalyteAt(i);
                data = manager.analyteParameter.add();
                loadParameter(refId,
                              refTableId,
                              refName,
                              qca.getAnalyteId(),
                              qca.getAnalyteName(),
                              data);
            }

        }
    }

    /**
     * Creates the dropdown models for the sample types and units in the passed
     * test manager
     */
    private void setTypeAndUnitModels() throws Exception {
        Integer unitId, typeId;
        TestTypeOfSampleDO ts;
        TestTypeOfSampleManager tsm;
        DictionaryDO dict;
        HashSet<Integer> types, units, tmp;
        ArrayList<Item<Integer>> typeModel, unitModel;

        tsm = testManager.getSampleTypes();
        typeModel = new ArrayList<Item<Integer>>();
        unitModel = new ArrayList<Item<Integer>>();
        types = new HashSet<Integer>();
        units = new HashSet<Integer>();
        unitTypesMap = new HashMap<Integer, HashSet<Integer>>();

        for (int i = 0; i < tsm.count(); i++ ) {
            ts = tsm.getTypeAt(i);
            typeId = ts.getTypeOfSampleId();
            /*
             * show a sample type only once
             */
            if ( !types.contains(typeId)) {
                dict = DictionaryCache.getById(typeId);
                typeModel.add(new Item<Integer>(dict.getId(), dict.getEntry()));
                types.add(typeId);
            }

            unitId = ts.getUnitOfMeasureId();
            if (unitId == null)
                continue;
            /*
             * show a unit only once
             */
            if ( !units.contains(unitId)) {
                dict = DictionaryCache.getById(unitId);
                unitModel.add(new Item<Integer>(dict.getId(), dict.getEntry()));
                units.add(unitId);
            }

            /*
             * this map groups sample types by units and is used to find out
             * whether a unit is valid for a sample type
             */
            tmp = unitTypesMap.get(unitId);
            if (tmp == null) {
                tmp = new HashSet<Integer>();
                unitTypesMap.put(unitId, tmp);
            }
            tmp.add(typeId);
        }

        typeOfSample.setModel(typeModel);
        unitOfMeasure.setModel(unitModel);
    }

    /**
     * Loads the passed analyte parameter DO with the passed information
     */
    private void loadParameter(Integer referenceId, Integer referenceTableId, String referenceName,
                               Integer analyteId, String analyteName, AnalyteParameterViewDO data) {
        data.setReferenceId(referenceId);
        data.setReferenceTableId(referenceTableId);
        data.setReferenceName(referenceName);
        data.setAnalyteId(analyteId);
        data.setAnalyteName(analyteName);
    }

    /**
     * Enables the units in the dropdown that are valid for the passed sample
     * type, disables the others
     */
    private void disableAndEnableUnits(Integer typeId) {
        Integer unitId;
        Item<Integer> row;
        ArrayList<Item<Integer>> model;
        HashSet<Integer> types;

        model = unitOfMeasure.getModel();
        for (int i = 0; i < model.size(); i++ ) {
            row = model.get(i);
            unitId = row.getKey();
            types = unitTypesMap.get(unitId);
            row.setEnabled(types.contains(typeId));
        }
    }
}