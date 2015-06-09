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
import java.util.Arrays;
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
import org.openelis.domain.QcDO;
import org.openelis.domain.ReferenceIdTableIdNameVO;
import org.openelis.domain.TestMethodVO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.manager.AnalyteParameterManager1;
import org.openelis.manager.AnalyteParameterManager1.AnalyteCombo;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestTypeOfSampleManager;
import org.openelis.meta.AnalyteParameterMeta;
import org.openelis.modules.qc.client.QcService;
import org.openelis.modules.test.client.TestService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
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
import org.openelis.ui.screen.State;
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
import org.openelis.ui.widget.table.event.UnselectionEvent;
import org.openelis.ui.widget.table.event.UnselectionHandler;
import org.openelis.ui.widget.tree.Node;
import org.openelis.ui.widget.tree.Tree;
import org.openelis.ui.widget.tree.event.NodeAddedEvent;
import org.openelis.ui.widget.tree.event.NodeAddedHandler;
import org.openelis.ui.widget.tree.event.NodeDeletedEvent;
import org.openelis.ui.widget.tree.event.NodeDeletedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
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

    protected ModulePermission                                     userPermission;

    protected ScreenNavigator<ReferenceIdTableIdNameVO>            nav;

    @UiField
    protected Table                                                atozTable;

    @UiField
    protected Button                                               query, previous, next, add,
                    update, commit, abort, nextPageButton, duplicateButton, addParameterButton,
                    removeRowButton, expandButton, collapseButton;

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
            window.close();
            throw e;
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
                query.setEnabled(isState(QUERY, DEFAULT, DISPLAY) &&
                                 userPermission.hasSelectPermission());
                if (isState(QUERY)) {
                    query.lock();
                }
            }
        });

        addShortcut(query, 'q', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                previous.setEnabled(isState(DISPLAY));
            }
        });

        addShortcut(previous, 'p', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                next.setEnabled(isState(DISPLAY));
            }
        });

        addShortcut(next, 'n', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                add.setEnabled(isState(ADD, DEFAULT, DISPLAY) && userPermission.hasAddPermission());
                if (isState(ADD)) {
                    add.lock();
                    add.setPressed(true);
                }
            }
        });

        addShortcut(add, 'a', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                update.setEnabled(isState(UPDATE, DISPLAY) && userPermission.hasUpdatePermission());
                if (isState(UPDATE)) {
                    update.lock();
                    update.setPressed(true);
                }
            }
        });

        addShortcut(update, 'u', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                commit.setEnabled(isState(QUERY, ADD, UPDATE));
            }
        });

        addShortcut(commit, 'm', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                abort.setEnabled(isState(QUERY, ADD, UPDATE));
            }
        });

        addShortcut(abort, 'o', CTRL);

        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator<ReferenceIdTableIdNameVO>(atozTable, nextPageButton) {
            public void executeQuery(final Query query) {
                setBusy(Messages.get().gen_querying());

                if (queryCall == null) {
                    queryCall = new AsyncCallbackUI<ArrayList<ReferenceIdTableIdNameVO>>() {
                        public void success(ArrayList<ReferenceIdTableIdNameVO> result) {
                            ReferenceIdTableIdNameVO data;
                            ArrayList<ReferenceIdTableIdNameVO> addedList;

                            clearStatus();
                            if (nav.getQuery().getPage() == 0) {
                                setQueryResult(result);
                                data = result.get(0);
                                fetchByRefIdRefTableId(data.getReferenceId(),
                                                       data.getReferenceTableId());
                                select(0);
                            } else {
                                addedList = getQueryResult();
                                addedList.addAll(result);
                                setQueryResult(addedList);
                                select(atozTable.getModel().size() - result.size());
                                atozTable.scrollToVisible(atozTable.getModel().size() - 1);
                            }
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

                query.setRowsPerPage(22);
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
                                  * the screen is reloaded with an empty manager
                                  * when the reference table is changed using
                                  * this dropdown and firing this event; the
                                  * dropdown is not set in query mode to prevent
                                  * multiple selection, so the event is fired
                                  * even in query state; the following prevents
                                  * the screen from getting reloaded in query
                                  * state when the reference table is changed
                                  */
                                 if ( !isState(QUERY))
                                     refreshScreen(null, event.getValue());
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
                referenceName.setValue(getReferenceId(), getReferenceName());
            }

            public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                Integer refTableId;
                AutoCompleteValue val;

                val = event.getValue();
                refTableId = getReferenceTableId();
                if (val != null)
                    refreshScreen(val.getId(), refTableId);
                else
                    refreshScreen(null, refTableId);
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

            public Widget onTab(boolean forward) {
                return forward ? duplicateButton : referenceName;
            }
        });

        tree.addUnselectionHandler(new UnselectionHandler<Integer>() {
            @Override
            public void onUnselection(UnselectionEvent<Integer> event) {
                int numAnalytes, numParams;
                Node node;

                if ( !isState(ADD, UPDATE))
                    return;
                /*
                 * enable or disable buttons based on the number of nodes left
                 * selected in the tree after unselecting the node for which
                 * this event was fired; it's fired *before* the node gets
                 * unselected; find the number of selected nodes of each leaf
                 * type
                 */
                numAnalytes = getNumSelectedByType(ANALYTE_LEAF);
                numParams = getNumSelectedByType(PARAMETER_LEAF);

                /*
                 * find the number of nodes of the unselected node's leaf type
                 * that will be left selected after unselecting the node; enable
                 * or disable buttons based on the numbers
                 */
                node = tree.getNodeAt(event.getUnselectedItem());
                if (ANALYTE_LEAF.equals(node.getType()))
                    numAnalytes-- ;
                else if (PARAMETER_LEAF.equals(node.getType()))
                    numParams-- ;

                duplicateButton.setEnabled(numParams > 0 || numAnalytes > 0);
                removeRowButton.setEnabled(numParams > 0 || numAnalytes > 0);
                addParameterButton.setEnabled(numParams == 0 && numAnalytes > 0);
            }
        });

        tree.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                int numParams, numAnalytes;

                if (isState(ADD, UPDATE)) {
                    duplicateButton.setEnabled(true);
                    removeRowButton.setEnabled(true);

                    numAnalytes = getNumSelectedByType(ANALYTE_LEAF);
                    numParams = getNumSelectedByType(PARAMETER_LEAF);
                    /*
                     * enable "Add Parameter" button if only one analyte node
                     * and no parameter nodes are selected
                     */
                    addParameterButton.setEnabled(numAnalytes == 1 && numParams == 0);
                }
            }
        });

        tree.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            @Override
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                int col;
                Integer refTableId, sampleTypeId;
                Node node;

                if ( !isState(ADD, UPDATE)) {
                    event.cancel();
                    return;
                }

                node = tree.getNodeAt(tree.getSelectedNode());
                if (ANALYTE_LEAF.equals(node.getType())) {
                    refTableId = getReferenceTableId();
                    col = event.getCol();
                    /*
                     * don't allow editing the analyte; allow editing the sample
                     * type or unit only if the parameters are for a test
                     */
                    if (col == 0 || !Constants.table().TEST.equals(refTableId)) {
                        event.cancel();
                    } else if (col == 2) {
                        /*
                         * if the user is trying to select the unit, then only
                         * allow it if a sample type is specified and also
                         * disable the units that are not valid for the type
                         */
                        sampleTypeId = tree.getValueAt(event.getRow(), 1);
                        if (sampleTypeId == null) {
                            event.cancel();
                            setError(Messages.get().analyteParameter_selectSampleTypeBeforeUnit());
                        } else {
                            disableAndEnableUnits(sampleTypeId);
                        }
                    }
                }
            }
        });

        tree.addCellEditedHandler(new CellEditedHandler() {
            @Override
            public void onCellUpdated(CellEditedEvent event) {
                int r, c, index;
                String combo;
                Integer sampleTypeId, unitId;
                AnalyteParameterViewDO data, prevData;
                AnalyteCombo ac;
                Object val;
                Node node, prevNode, parent;
                ArrayList<Exception> errors;
                HashSet<Integer> types;

                node = tree.getNodeAt(tree.getSelectedNode());
                r = event.getRow();
                c = event.getCol();
                val = tree.getValueAt(r, c);
                if (ANALYTE_LEAF.equals(node.getType())) {
                    switch (c) {
                        case 1:
                            sampleTypeId = (Integer)val;
                            setSampleType(node, sampleTypeId);
                            unitId = tree.getValueAt(r, 2);
                            if (unitId != null) {
                                /*
                                 * if the unit selected for the analyte is not
                                 * valid for the currently selected sample type,
                                 * then blank the unit
                                 */
                                types = unitTypesMap.get(unitId);
                                if ( !types.contains(sampleTypeId)) {
                                    tree.setValueAt(r, 2, null);
                                    tree.refreshNode(node);
                                    setUnit(node, null);
                                }
                            }
                            break;
                        case 2:
                            setUnit(node, (Integer)val);
                            break;
                    }
                } else if (PARAMETER_LEAF.equals(node.getType())) {
                    /*
                     * create the string for the parent node's combination of
                     * analyte, sample type and unit
                     */
                    parent = node.getParent();
                    ac = manager.analyte.get((Integer)parent.getData());
                    try {
                        combo = getCombination(ac);
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        return;
                    }

                    data = (AnalyteParameterViewDO)manager.getObject((String)node.getData());
                    index = parent.getIndex(node);
                    /*
                     * find the node after the current one under the same
                     * parent; it's for the previously most recent parameter
                     */
                    if (index < parent.children().size() - 1)
                        prevNode = parent.getChildAt(index + 1);
                    else
                        prevNode = null;
                    tree.clearEndUserExceptions(r, c);
                    switch (c) {
                        case 0:
                            /*
                             * if the user entered an invalid date, the value in
                             * the cell is a String and not a Datetime
                             */
                            if (val instanceof Datetime)
                                data.setActiveBegin((Datetime)val);
                            else
                                data.setActiveBegin(null);
                            /*
                             * show an error if the begin date for this
                             * parameter is not more than a minute after the
                             * begin date of each previous parameter of its
                             * analyte, or if the begin date is not after the
                             * end date of each previous parameter, or if its
                             * end date is not after its begin date
                             */
                            errors = new ArrayList<Exception>();
                            validateBeginDate(parent, index, errors, combo);
                            if ( (errors.size() == 0))
                                validateEndDate(data, errors, combo);

                            if (errors.size() > 0) {
                                for (Exception e : errors)
                                    tree.addException(r, c, e);
                            } else if (data.getActiveBegin() != null && prevNode != null) {
                                prevData = (AnalyteParameterViewDO)manager.getObject((String)prevNode.getData());
                                /*
                                 * set the end date of the previous parameter to
                                 * be a minute before this parameter's begin
                                 * date if the previous parameter's end date is
                                 * not before this parameter's begin date
                                 */
                                if (prevData.getActiveEnd() != null &&
                                    !DataBaseUtil.isAfter(data.getActiveBegin(),
                                                          prevData.getActiveEnd())) {
                                    prevData.getActiveEnd()
                                            .getDate()
                                            .setTime(data.getActiveBegin().getDate().getTime() - 60000);
                                    prevNode.setCell(1, prevData.getActiveEnd());
                                    tree.refreshNode(prevNode);
                                }
                            }
                            break;
                        case 1:
                            /*
                             * if the user entered an invalid date, the value in
                             * the cell is a String and not a Datetime
                             */
                            if (val instanceof Datetime)
                                data.setActiveEnd((Datetime)val);
                            else
                                data.setActiveEnd(null);
                            /*
                             * show an error if the end date for this parameter
                             * is not after its begin date
                             */
                            errors = new ArrayList<Exception>();
                            validateEndDate(data, errors, combo);
                            for (Exception e : errors)
                                tree.addException(r, c, e);
                            break;
                        case 2:
                            data.setP1((Double)val);
                            break;
                        case 3:
                            data.setP2((Double)val);
                            break;
                        case 4:
                            data.setP3((Double)val);
                            break;
                    }
                }
            }
        });

        tree.addNodeAddedHandler(new NodeAddedHandler() {
            @Override
            public void onNodeAdded(NodeAddedEvent event) {
                Integer id;
                AnalyteParameterViewDO data;
                AnalyteCombo ac, newAC;
                Node node;

                node = event.getNode();
                if (ANALYTE_LEAF.equals(node.getType())) {
                    /*
                     * find the analyte node before the newly added node and
                     * copy the analyte information from it
                     */
                    id = node.previousSibling().getData();
                    ac = manager.analyte.get(id);
                    newAC = manager.analyte.add(id);
                    newAC.setAnalyteId(ac.getAnalyteId());
                    newAC.setAnalyteName(ac.getAnalyteName());
                    node.setData(newAC.getId());
                    node.setCell(0, newAC.getAnalyteName());
                } else if (PARAMETER_LEAF.equals(node.getType())) {
                    /*
                     * add a new parameter to the manager as the most recent one
                     * for its combination of analyte, sample type and unit;
                     * fill it from the node's and its parent's data
                     */
                    id = node.getParent().getData();
                    ac = manager.analyte.get(id);
                    data = manager.analyteParameter.add(id, 0);
                    data.setReferenceId(getReferenceId());
                    data.setReferenceName(getReferenceName());
                    data.setReferenceTableId(getReferenceTableId());
                    data.setAnalyteId(ac.getAnalyteId());
                    data.setAnalyteName(ac.getAnalyteName());
                    data.setTypeOfSampleId(ac.getTypeOfSampleId());
                    data.setUnitOfMeasureId(ac.getUnitOfMeasureId());
                    data.setActiveBegin((Datetime)node.getCell(0));
                    data.setActiveEnd((Datetime)node.getCell(1));
                    data.setP1((Double)node.getCell(2));
                    data.setP2((Double)node.getCell(3));
                    data.setP3((Double)node.getCell(4));
                    node.setData(Constants.uid().get(data));
                }
            }
        });

        tree.addNodeDeletedHandler(new NodeDeletedHandler() {
            @Override
            public void onNodeDeleted(NodeDeletedEvent event) {
                Integer id;
                String uid;
                AnalyteParameterViewDO data;
                Node node;

                node = event.getNode();
                if (ANALYTE_LEAF.equals(node.getType())) {
                    id = (Integer)node.getData();
                    manager.analyte.remove(id);
                } else if (PARAMETER_LEAF.equals(node.getType())) {
                    uid = (String)node.getData();
                    data = (AnalyteParameterViewDO)manager.getObject(uid);
                    id = (Integer)node.getParent().getData();
                    manager.analyteParameter.remove(id, data);
                }
            }
        });

        tree.setAllowMultipleSelection(true);

        addScreenHandler(duplicateButton, "duplicateButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                duplicateButton.setEnabled(false);
            }

            public Widget onTab(boolean forward) {
                return forward ? addParameterButton : tree;
            }
        });

        duplicateButton.setTip(Messages.get().analyteParameter_selectOneOrMoreRowsToDup());

        addScreenHandler(addParameterButton, "addParameterButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                addParameterButton.setEnabled(false);
            }

            public Widget onTab(boolean forward) {
                return forward ? removeRowButton : duplicateButton;
            }
        });

        addParameterButton.setTip(Messages.get().analyteParameter_selectAnalyteToAddParam());

        addScreenHandler(removeRowButton, "removeRowButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                removeRowButton.setEnabled(false);
            }

            public Widget onTab(boolean forward) {
                return forward ? expandButton : addParameterButton;
            }
        });

        removeRowButton.setTip(Messages.get().analyteParameter_selectOneOrMoreRowsToRemove());

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
                return forward ? tree : referenceTable;
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
    @UiHandler("query")
    protected void query(ClickEvent event) {
        manager = null;
        typeOfSample.setModel(allSampleTypesModel);
        unitOfMeasure.setModel(allUnitsModel);
        setState(QUERY);
        fireDataChange();
        referenceTable.setFocus(true);
        setDone(Messages.get().gen_enterFieldsToQuery());
    }

    @UiHandler("previous")
    protected void previous(ClickEvent event) {
        if (DataBaseUtil.isSame(atozTable.getSelectedRow(), 0)) {
            setError(Messages.get().gen_noMoreRecordInDir());
            return;
        }
        nav.previous();
    }

    @UiHandler("next")
    protected void next(ClickEvent event) {
        nav.next();
    }

    @UiHandler("add")
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

        AnalyteParameterService1.get().getInstance(null, null, addCall);
    }

    @UiHandler("update")
    protected void update(ClickEvent event) {
        setBusy(Messages.get().lockForUpdate());

        if (fetchForUpdateCall == null) {
            fetchForUpdateCall = new AsyncCallbackUI<AnalyteParameterManager1>() {
                public void success(AnalyteParameterManager1 result) {
                    manager = result;
                    if (Constants.table().TEST.equals(manager.getReferenceTableId())) {
                        try {
                            setTypeAndUnitModels(TestService.get()
                                                            .fetchById(manager.getReferenceId()));
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
                    logger.log(Level.SEVERE, e.getMessage(), e);
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

    @UiHandler("commit")
    protected void commit(ClickEvent event) {
        Validation validation;

        finishEditing();

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

    @UiHandler("abort")
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
                        typeOfSample.setModel(allSampleTypesModel);
                        unitOfMeasure.setModel(allUnitsModel);
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
        unitTypesMap = null;
    }

    /**
     * If only analyte nodes are selected, duplicates them at the position right
     * after them at the top level; if only parameter nodes are selected,
     * duplicates them under their own parent; if both types of nodes are
     * selected, duplicates all parameter nodes under all analyte nodes
     */
    @UiHandler("duplicateButton")
    protected void duplicate(ClickEvent event) {
        int index;
        String uid;
        Integer indexes[];
        ArrayList<Node> anaNodes, paramNodes;
        Node node, newNode;
        ArrayList<Node> newParents;

        indexes = tree.getSelectedNodes();
        Arrays.sort(indexes);

        /*
         * find out which analyte and/or parameter nodes have been selected
         */
        anaNodes = new ArrayList<Node>();
        paramNodes = new ArrayList<Node>();
        for (int i : indexes) {
            node = tree.getNodeAt(i);
            if (ANALYTE_LEAF.equals(node.getType()))
                anaNodes.add(node);
            else if (PARAMETER_LEAF.equals(node.getType()))
                paramNodes.add(node);
        }

        if (paramNodes.size() == 0) {
            /*
             * only analyte nodes have been selected; add a top level node after
             * each selected node for the same analyte as the selected node
             */
            for (Node n : anaNodes) {
                newNode = new Node(3);
                newNode.setType(ANALYTE_LEAF);
                tree.addNodeAfter(n, newNode);
                tree.refreshNode(newNode);
            }
        } else {
            /*
             * some parameter nodes have been selected; if any analyte nodes
             * have been selected, all parameter nodes will be duplicated to all
             * analyte nodes; otherwise they will be duplicated to their parent
             * analyte node; nodes are duplicated in the reverse order to make
             * sure that they are the most recent one for their parent
             */
            newParents = new ArrayList<Node>(1);
            index = paramNodes.size();
            while ( --index >= 0) {
                node = paramNodes.get(index);
                /*
                 * find the new parent(s) of this node
                 */
                if (anaNodes.size() > 0) {
                    newParents = anaNodes;
                } else {
                    newParents.clear();
                    newParents.add(node.getParent());
                }

                /*
                 * add the node to it new parent(s)
                 */
                for (Node p : newParents) {
                    uid = node.getData();
                    newNode = createParameterNode((AnalyteParameterViewDO)manager.getObject(uid));
                    tree.addNodeAt(p, newNode, 0);
                    if ( !p.isOpen())
                        tree.open(p);
                }
            }
        }

        /*
         * this is done because the tree loses its selection on adding a node
         * and no node is selected through code because it's not possible to
         * select multiple nodes; there's no other way to disable these widgets
         * because an UnselectionEvent is not fired at that time
         */
        duplicateButton.setEnabled(false);
        addParameterButton.setEnabled(false);
        removeRowButton.setEnabled(false);
    }

    /**
     * Adds an empty parameter node to the selected analyte node; selects thew
     * newly added node; this button is disabled on selecting parameter node(s)
     */
    @UiHandler("addParameterButton")
    protected void addParameter(ClickEvent event) {
        Node node, newNode;

        node = tree.getNodeAt(tree.getSelectedNode());
        newNode = new Node(5);
        newNode.setType(PARAMETER_LEAF);
        tree.addNodeAt(node, newNode, 0);
        if ( !node.isOpen())
            tree.open(node);
        tree.selectNodeAt(newNode);
        addParameterButton.setEnabled(false);
    }

    /**
     * Removes the nodes selected by the user; if only analyte nodes are
     * selected, removes their children and then removes them; if only parameter
     * nodes are selected, only removes them; if both parameter and analyte
     * nodes are selected, goes through the analyte nodes, removes them their
     * children; then removes any remaining parameter node; disables the buttons
     * that get enabled only on selecting nodes
     */
    @UiHandler("removeRowButton")
    protected void removeRow(ClickEvent event) {
        String uid;
        Node node, child;
        Integer indexes[];
        ArrayList<Node> anaNodes;
        HashMap<String, Node> paramMap;

        /*
         * make a list of selected analyte nodes; put the selected parameter
         * nodes in a map; the map will be used to prevent a child from being
         * removed twice when it and its parents are both selected; no nodes are
         * removed here to make sure that the indexes are valid
         */
        indexes = tree.getSelectedNodes();
        anaNodes = new ArrayList<Node>();
        paramMap = new HashMap<String, Node>();
        for (int i : indexes) {
            node = tree.getNodeAt(i);
            if (ANALYTE_LEAF.equals(node.getType()))
                anaNodes.add(node);
            else if (PARAMETER_LEAF.equals(node.getType()))
                paramMap.put((String)node.getData(), node);
        }

        /*
         * remove the selected analyte nodes and all their children; if a child
         * was also selected, remove it from the map
         */
        for (Node n : anaNodes) {
            while (n.getChildCount() > 0) {
                child = n.getChildAt(0);
                tree.removeNode(child);
                uid = child.getData();
                if (paramMap.containsKey(uid))
                    paramMap.remove(uid);
            }
            tree.removeNode(n);
        }

        /*
         * remove any remaining selected parameter nodes
         */
        for (Node n : paramMap.values())
            tree.removeNode(n);

        /*
         * this is done because the tree loses its selection on removing a node
         * and no node is selected through code; there's no other way to disable
         * these widgets because an UnselectionEvent is not fired at that time
         */
        duplicateButton.setEnabled(false);
        addParameterButton.setEnabled(false);
        removeRowButton.setEnabled(false);
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
                    State st;

                    manager = result;
                    /*
                     * if the updated manager didn't have any parameters then a
                     * null manager is returned by the back-end; so that the
                     * screen can be blanked and not give the user any
                     * opportunity to lock the record again
                     */
                    st = manager != null ? DISPLAY : DEFAULT;

                    typeOfSample.setModel(allSampleTypesModel);
                    unitOfMeasure.setModel(allUnitsModel);
                    setState(st);
                    fireDataChange();
                    clearStatus();
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

        AnalyteParameterService1.get().update(manager, updateCall);
    }

    /**
     * Expands the tree at every level
     */
    @UiHandler("expandButton")
    protected void expand(ClickEvent event) {
        tree.expand(1);
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

    /**
     * Loads the screen with a manager containing only the analytes for the
     * passed reference id and table and no parameters; also loads the dropdowns
     * for sample type and unit if the reference record is a test
     */
    private void refreshScreen(Integer refId, Integer refTableId) {
        try {
            manager = AnalyteParameterService1.get().getInstance(refId, refTableId);
            unitTypesMap = null;
            if (Constants.table().TEST.equals(refTableId) && refId != null) {
                setTypeAndUnitModels(TestManager.fetchById(refId));
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

    private Integer getReferenceId() {
        if (manager == null)
            return null;
        return manager.getReferenceId();
    }

    private String getReferenceName() {
        if (manager == null)
            return null;
        return manager.getReferenceName();
    }

    private Integer getReferenceTableId() {
        if (manager == null)
            return null;
        return manager.getReferenceTableId();
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

    private Node getRoot() {
        int i, j;
        Node root, anode, rnode;
        AnalyteParameterViewDO data;
        AnalyteCombo ac;

        root = new Node();
        if (manager == null)
            return root;

        /*
         * create a parent node for every new combination of analyte, sample
         * type and unit; create a child node for every parameter
         */
        for (i = 0; i < manager.analyte.count(); i++ ) {
            anode = new Node(3);
            anode.setType(ANALYTE_LEAF);
            ac = manager.analyte.get(i);
            anode.setCell(0, ac.getAnalyteName());
            anode.setCell(1, ac.getTypeOfSampleId());
            anode.setCell(2, ac.getUnitOfMeasureId());
            anode.setData(ac.getId());
            root.add(anode);
            for (j = 0; j < manager.analyteParameter.count(ac.getId()); j++ ) {
                data = manager.analyteParameter.get(ac.getId(), j);
                rnode = createParameterNode(data);
                rnode.setData(Constants.uid().get(data));
                anode.add(rnode);
            }
        }

        return root;
    }

    /**
     * Creates a parameter node and fills it from the passed parameter
     */
    private Node createParameterNode(AnalyteParameterViewDO data) {
        Node node;

        node = new Node(5);
        node.setType(PARAMETER_LEAF);
        node.setCell(0, data.getActiveBegin());
        node.setCell(1, data.getActiveEnd());
        node.setCell(2, data.getP1());
        node.setCell(3, data.getP2());
        node.setCell(4, data.getP3());

        return node;
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

    /**
     * Returns the number of selected nodes with the passed leaf type
     */
    private int getNumSelectedByType(String type) {
        int count;
        Integer indexes[];
        Node node;

        count = 0;
        indexes = tree.getSelectedNodes();
        if (indexes.length > 0) {
            for (int i : indexes) {
                node = tree.getNodeAt(i);
                if (node.getType().equals(type))
                    count++ ;
            }
        }

        return count;
    }

    /**
     * Creates the dropdown models for the sample types and units in the passed
     * test manager
     */
    private void setTypeAndUnitModels(TestManager tm) throws Exception {
        Integer unitId, typeId;
        TestTypeOfSampleDO ts;
        TestTypeOfSampleManager tsm;
        DictionaryDO dict;
        HashSet<Integer> types, units, tmp;
        ArrayList<Item<Integer>> typeModel, unitModel;

        tsm = tm.getSampleTypes();
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

    /**
     * Sets the sample type in the data for the passed parent node and all its
     * children
     */
    private void setSampleType(Node analyte, Integer sampleTypeId) {
        AnalyteParameterViewDO data;
        AnalyteCombo ac;
        Node param;

        ac = manager.analyte.get((Integer)analyte.getData());
        ac.setTypeOfSampleId(sampleTypeId);
        for (int i = 0; i < analyte.getChildCount(); i++ ) {
            param = analyte.getChildAt(i);
            data = (AnalyteParameterViewDO)manager.getObject((String)param.getData());
            data.setTypeOfSampleId(sampleTypeId);
        }
    }

    /**
     * Sets the unit in the data for the passed parent node and all its children
     */
    private void setUnit(Node analyte, Integer unitId) {
        AnalyteParameterViewDO data;
        AnalyteCombo ac;
        Node param;

        ac = manager.analyte.get((Integer)analyte.getData());
        ac.setUnitOfMeasureId(unitId);
        for (int i = 0; i < analyte.getChildCount(); i++ ) {
            param = analyte.getChildAt(i);
            data = (AnalyteParameterViewDO)manager.getObject((String)param.getData());
            data.setUnitOfMeasureId(unitId);
        }
    }

    /**
     * adds an exception to the passed list if the begin date for the parameter
     * at the passed index under the passed analyte node is not more than a
     * minute after the begin date of every previous parameter; doesn't validate
     * if the begin date for the parameter at the passed index is null
     */
    private void validateBeginDate(Node analyteNode, int i, ArrayList<Exception> errors,
                                   String combo) {
        int j;
        AnalyteParameterViewDO data;
        Node node;
        Datetime ab;

        /*
         * find the begin date for the child at the passed index
         */
        node = analyteNode.getChildAt(i);
        data = (AnalyteParameterViewDO)manager.getObject((String)node.getData());
        ab = data.getActiveBegin();
        if (ab == null)
            return;

        /*
         * find out if the begin date is more than one minute after the begin
         * date of all previous parameters; the two counters are used to make
         * sure that the error is not added more than once
         */
        for (j = i + 1; j < analyteNode.children().size(); j++ ) {
            node = analyteNode.getChildAt(j);
            data = (AnalyteParameterViewDO)manager.getObject((String)node.getData());
            if (data.getActiveBegin() != null &&
                (ab.getDate().getTime() - data.getActiveBegin().getDate().getTime()) <= 60000) {
                errors.add(new Exception(Messages.get()
                                                 .analyteParameter_beginDateMinuteAheadException(combo)));
                break;
            }
        }
    }

    /**
     * Adds an exception to the passed list if the end date for the passed
     * parameter is after its begin date; doesn't validate if both dates are
     * null
     */
    private void validateEndDate(AnalyteParameterViewDO data, ArrayList<Exception> errors,
                                 String combo) {
        Datetime ab, ae;

        ab = data.getActiveBegin();
        ae = data.getActiveEnd();
        if (ab != null && ae != null && !DataBaseUtil.isAfter(ae, ab))
            errors.add(new Exception(Messages.get()
                                             .analyteParameter_endDateAfterBeginException(combo)));
    }

    /**
     * Returns a string created by concatinating the analyte name and dictionary
     * entries for the passed combo's sample type and unit
     */
    private String getCombination(AnalyteCombo ac) throws Exception {
        String sampleType, unit, su;

        sampleType = null;
        unit = null;
        if (ac.getTypeOfSampleId() != null)
            sampleType = DictionaryCache.getById(ac.getTypeOfSampleId()).getEntry();

        if (ac.getUnitOfMeasureId() != null)
            unit = DictionaryCache.getById(ac.getUnitOfMeasureId()).getEntry();

        su = DataBaseUtil.concatWithSeparator(sampleType, ", ", unit);
        /*
         * if both sample type and unit are null, concatWithSeparator() returns
         * an empty string, not null; trim() is used below to pass a null, so
         * that the returned string doesn't contain the delimiter
         */
        return DataBaseUtil.concatWithSeparator(ac.getAnalyteName(), ", ", DataBaseUtil.trim(su));
    }
}