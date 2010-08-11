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
package org.openelis.modules.sample.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AnalyteDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.TestAnalyteDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestResultDO;
import org.openelis.exception.ParseException;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.BeforeCloseEvent;
import org.openelis.gwt.event.BeforeCloseHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.Popup;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.table.TableColumn;
import org.openelis.gwt.widget.table.TableDataCell;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.AnalysisManager;
import org.openelis.manager.AnalysisResultManager;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.AnalysisResultManager.TestAnalyteListItem;
import org.openelis.modules.test.client.TestAnalyteDisplayManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class ResultTab extends Screen implements HasActionHandlers<ResultTab.Action> {
    public enum Action {
        RESULT_HISTORY, REFLEX_ADDED
    };

    private boolean                                 loaded;

    protected AppButton                             addResultButton, removeResultButton,
                    suggestionsButton, popoutTable;
    protected TableWidget                           testResultsTable;
    private ArrayList<TableColumn>                  resultTableCols;

    protected TestAnalyteLookupScreen               testAnalyteScreen;
    protected ResultSuggestionsScreen               suggestionsScreen;

    protected ResultTab                             resultPopoutScreen;
    protected AnalysisResultManager                 manager;
    private TestAnalyteDisplayManager<ResultViewDO> displayManager;
    protected GetMatchesHandler                     resultMatchesHandler;
    protected AnalysisManager                       analysisMan;
    protected AnalysisViewDO                        anDO;
    protected SampleDataBundle                      bundle;
    private Screen                       parentScreen;

    private Integer                                 analysisCancelledId, analysisReleasedId,
                    testAnalyteReadOnlyId, testAnalyteRequiredId, addedTestAnalyteId,
                    addedAnalyteId, typeAlphaLower, typeAlphaUpper;
    private String                                  addedAnalyteName;

    private ReflexTestUtility                       reflexTestUtil;

    public ResultTab(ScreenDefInt def, ScreenWindow window, Screen parentScreen) {
        setDefinition(def);
        setWindow(window);
        
        this.parentScreen = parentScreen;

        initialize();
        initializeDropdowns();
    }

    public ResultTab() throws Exception {
        super((ScreenDefInt)GWT.create(ResultPopoutTabDef.class));

        // Setup link between Screen and widget Handlers
        initialize();
        initializeDropdowns();
        popoutTable.setVisible(false);
        // Initialize Screen
        setState(State.DEFAULT);
    }

    private void initialize() {
        final ResultTab resultTab = this;
        
        testResultsTable = (TableWidget)def.getWidget("testResultsTable");
        addScreenHandler(testResultsTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                testResultsTable.load(getTableModel());

                if (testResultsTable.numRows() > 0)
                    popoutTable.enable(true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                testResultsTable.enable(canEdit() &&
                                        EnumSet.of(State.ADD, State.UPDATE)
                                               .contains(event.getState()));
            }
        });

        testResultsTable.addBeforeSelectionHandler(new BeforeSelectionHandler<TableRow>() {
            public void onBeforeSelection(BeforeSelectionEvent<TableRow> event) {
                if (anDO.getUnitOfMeasureId() == null) {
                    addResultButton.enable(false);
                    removeResultButton.enable(false);
                }

                TableDataRow row;
                boolean isHeader;

                row = event.getItem().row;
                isHeader = ((Boolean)row.data).booleanValue();

                if (isHeader)
                    event.cancel();
            }
        });

        testResultsTable.addSelectionHandler(new SelectionHandler<TableRow>() {
            public void onSelection(SelectionEvent<TableRow> event) {
                int row;
                ResultViewDO resultDO;

                if (EnumSet.of(State.ADD, State.UPDATE).contains(state)) {
                    row = testResultsTable.getSelectedRow();
                    resultDO = displayManager.getObjectAt(row, 0);

                    if (testAnalyteRequiredId.equals(resultDO.getTypeId()))
                        removeResultButton.enable(false);
                    else
                        removeResultButton.enable(true);

                    addResultButton.enable(true);
                    suggestionsButton.enable(true);
                }
            }
        });

        testResultsTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                boolean           isHeaderRow, enableButton;
                int               r, c;
                TableDataRow      row;
                ResultViewDO      resultDO;
                TestAnalyteViewDO testAnalyte;

                isHeaderRow  = false;
                enableButton = true;

                r = event.getRow();
                c = event.getCol();
                row = testResultsTable.getRow(r);
                isHeaderRow = ((Boolean)row.data).booleanValue();

                if (isHeaderRow || c == 1 || c >= displayManager.columnCount(r)) {
                    event.cancel();
                    enableButton = false;
                } else {
                    if (c < 2)
                        resultDO = displayManager.getObjectAt(r, 0);
                    else
                        resultDO = displayManager.getObjectAt(r, c - 2);
                    
                    testAnalyte = manager.getTestAnalyte(resultDO.getRowGroup(), resultDO.getTestAnalyteId());
                    if (testAnalyte == null || testAnalyteReadOnlyId.equals(testAnalyte.getTypeId())) {
                        if (testAnalyte == null)
                            window.setError(consts.get("testAnalyteDefinitionChanged"));
                        event.cancel();
                        enableButton = false;
                    } else if (anDO.getUnitOfMeasureId() == null &&
                               !manager.getResultValidator(resultDO.getResultGroup()).noUnitsSpecified()) {
                        window.setError(consts.get("unitOfMeasureException"));
                        event.cancel();
                        enableButton = false;
                    } else {
                        window.clearStatus();
                    }
                }
                suggestionsButton.enable(enableButton);
            }
        });

        testResultsTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int row, col;
                String val;
                TableDataRow tableRow;
                ResultViewDO resultDO;
                Integer testResultId;
                TestResultDO testResultDo;

                row = event.getRow();
                col = event.getCol();
                resultDO = null;

                tableRow = testResultsTable.getRow(row);
                val = (String)tableRow.cells.get(col).value;

                if (col == 0)
                    resultDO = displayManager.getObjectAt(row, 0);
                else
                    resultDO = displayManager.getObjectAt(row, col - 2);

                if (col == 0) {
                    resultDO.setIsReportable(val);

                } else if ( !"".equals(val)) {
                    try {
                        testResultId = manager.validateResultValue(resultDO.getResultGroup(),
                                                                   anDO.getUnitOfMeasureId(), val);
                        testResultDo = manager.getTestResultList().get(testResultId);

                        resultDO.setTypeId(testResultDo.getTypeId());
                        resultDO.setTestResultId(testResultDo.getId());
                        //if its alpha we need to set it to the right case
                        val = formatValue(testResultDo, val);
                        resultDO.setValue(val);
                        testResultsTable.setCell(row, col, val);

                        if (reflexTestUtil == null){
                            reflexTestUtil = new ReflexTestUtility();
                        
                            reflexTestUtil.addActionHandler(new ActionHandler<ReflexTestUtility.Action>(){
                                public void onAction(ActionEvent<ReflexTestUtility.Action> event) {
                                    ActionEvent.fire(resultTab, Action.REFLEX_ADDED, event.getData());
                                }
                            });
                        }

                        reflexTestUtil.setScreen(parentScreen);
                        reflexTestUtil.resultEntered(bundle, resultDO);

                    } catch (ParseException e) {
                        testResultsTable.clearCellExceptions(row, col);
                        testResultsTable.setCellException(row, col, e);
                        resultDO.setTypeId(null);
                        resultDO.setTestResultId(null);
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                    }
                } else {
                    testResultsTable.clearCellExceptions(row, col);
                    resultDO.setValue(val);
                    resultDO.setTypeId(null);
                    resultDO.setTestResultId(null);
                }
            }
        });

        testResultsTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                TableDataRow row;
                int index, prowIndex, numCols;
                TestResultDO testResult;
                Integer rowGroup;
                ResultViewDO resultDO;
                String val;

                index = event.getIndex();
                row = event.getRow();
                prowIndex = index - 1;

                rowGroup = displayManager.getObjectAt(prowIndex, 0).getRowGroup();

                manager.addRowAt(displayManager.getIndexAt(prowIndex) + 1, rowGroup,
                                 addedTestAnalyteId, addedAnalyteId, addedAnalyteName);

                addedTestAnalyteId = null;
                addedAnalyteId = null;
                addedAnalyteName = null;

                displayManager.setDataGrid(manager.getResults());

                resultDO = null;
                numCols = displayManager.columnCount(index);
                manager.setDefaultsLoaded(false);
                for (int i = 2; i < numCols; i++ ) {
                    resultDO = displayManager.getObjectAt(index, i - 2);
                    row.key = resultDO.getId();
                    try {
                        val = getDefaultValue(resultDO, anDO.getUnitOfMeasureId());
                        testResultsTable.setCell(index, i, val);   
                        
                        if (val != null && !"".equals(val)) {
                            resultDO.setValue(val);
                            displayManager.validateResultValue(manager, resultDO,
                                                               anDO.getUnitOfMeasureId());
                            
                            testResult = displayManager.validateResultValue(manager, resultDO,
                                                               anDO.getUnitOfMeasureId());
                            val = formatValue(testResult, val);
                            resultDO.setValue(val);
                            testResultsTable.setCell(index, i, val);   
                        }
                    } catch (ParseException e) {
                        testResultsTable.clearCellExceptions(index, i);
                        testResultsTable.setCellException(index, i, e);
                        resultDO.setTypeId(null);
                        resultDO.setTestResultId(null);

                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                    }
                }
                manager.setDefaultsLoaded(true);
            }
        });

        testResultsTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                int index;
                try {
                    index = displayManager.getIndexAt(event.getIndex());
                    manager.removeRowAt(index);
                    displayManager.setDataGrid(manager.getResults());
                    removeResultButton.enable(false);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        addResultButton = (AppButton)def.getWidget("addResultButton");
        addScreenHandler(addResultButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int row = testResultsTable.getSelectedRow();
                Integer rowGroup = displayManager.getObjectAt(row, 0).getRowGroup();

                if (testAnalyteScreen == null) {
                    try {
                        testAnalyteScreen = new TestAnalyteLookupScreen();
                        testAnalyteScreen.addActionHandler(new ActionHandler<TestAnalyteLookupScreen.Action>() {
                            public void onAction(ActionEvent<TestAnalyteLookupScreen.Action> event) {
                                if (event.getAction() == TestAnalyteLookupScreen.Action.OK) {
                                    addResultRows((ArrayList<TestAnalyteViewDO>)event.getData());
                                }
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        Window.alert("error: " + e.getMessage());
                        return;
                    }
                }

                ScreenWindow modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
                modal.setName(consts.get("testAnalyteSelection"));
                modal.setContent(testAnalyteScreen);

                testAnalyteScreen.setData(manager.getNonColumnTestAnalytes(rowGroup));
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addResultButton.enable(false);
            }
        });

        removeResultButton = (AppButton)def.getWidget("removeResultButton");
        addScreenHandler(removeResultButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = testResultsTable.getSelectedRow();
                if (r > -1 && testResultsTable.numRows() > 0) {
                    if ( !onlyRowUnderHeading(r))
                        testResultsTable.deleteRow(r);
                    else
                        window.setError(consts.get("atLeastOneResultUnderHeading"));
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeResultButton.enable(false);
            }
        });

        suggestionsButton = (AppButton)def.getWidget("suggestionsButton");
        addScreenHandler(suggestionsButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int row;
                int col;
                ResultViewDO resultDO;

                row = testResultsTable.getSelectedRow();
                col = testResultsTable.getSelectedCol();
                resultDO = displayManager.getObjectAt(row, col - 2);

                if (suggestionsScreen == null) {
                    try {
                        suggestionsScreen = new ResultSuggestionsScreen();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Window.alert("error: " + e.getMessage());
                        return;
                    }
                }

                Popup popUp = new Popup(consts.get("suggestions"), suggestionsScreen);

                suggestionsScreen.setValidator(manager.getResultValidator(resultDO.getResultGroup()),
                                               anDO.getUnitOfMeasureId());

                popUp.show();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                suggestionsButton.enable(false);
            }
        });

        popoutTable = (AppButton)def.getWidget("popoutTable");
        addScreenHandler(popoutTable, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                onTablePopoutClick();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                popoutTable.enable(false);
            }
        });
    }

    private ArrayList<TableDataRow> getTableModel() {
        int m, c, len, numberOfCols;
        TestResultDO testResult;
        ArrayList<TableDataRow> model;
        TableDataRow hrow, row;
        ResultViewDO resultDO;
        boolean headerFilled;
        String val;
        boolean validateResults;

        //
        // we are assuming there will be at least 1 non supplemental
        // if there are only supplementals in a row group it will not
        // show a header so the user wont be able to add any analytes
        //
        model = new ArrayList<TableDataRow>();
        if (manager == null || displayManager == null)
            return model;

        numberOfCols = displayManager.maxColumnCount();
        resizeResultTable(numberOfCols);

        hrow = null;
        headerFilled = false;

        for (m = 0; m < displayManager.rowCount(); m++ ) {
            if (displayManager.isHeaderRow(m)) {
                m++ ;
                hrow = createHeaderRow(numberOfCols);
                model.add(hrow);
                headerFilled = false;
            }

            len = displayManager.columnCount(m) - 2;
            row = new TableDataRow(numberOfCols);
            row.data = new Boolean(false);
            validateResults = (state == State.ADD || state == State.UPDATE);

            for (c = 0; c < len; c++ ) {
                resultDO = displayManager.getObjectAt(m, c);
                row.key = resultDO.getId();
                try {
                    if (c == 0) {
                        row.cells.get(0).setValue(resultDO.getIsReportable());
                        row.cells.get(1).setValue(resultDO.getAnalyte());
                    }

                    val = resultDO.getValue();
                    row.cells.get(c + 2).setValue(val);

                    if (validateResults && val != null && !"".equals(val)) {
                        resultDO.setValue(val);
                        testResult = displayManager.validateResultValue(manager, resultDO,
                                                           anDO.getUnitOfMeasureId());
                        val = formatValue(testResult, val);
                        resultDO.setValue(val);
                        row.cells.get(c + 2).setValue(val);
                    }
                } catch (ParseException e) {
                    row.cells.get(c + 2).clearExceptions();
                    row.cells.get(c + 2).addException(e);
                    resultDO.setTypeId(null);
                    resultDO.setTestResultId(null);

                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }

                if ( !headerFilled && c > 0)
                    hrow.cells.get(c + 2).setValue(resultDO.getAnalyte());
            }
            headerFilled = true;
            model.add(row);
        }
        manager.setDefaultsLoaded(true);

        return model;
    }

    private String getDefaultValue(ResultViewDO resultDO, Integer unitOfMeasureId) {
        String val;
        if (resultDO.getValue() != null || resultDO.getId() != null)
            val = resultDO.getValue();

        else
            val = manager.getDefaultValue(resultDO.getResultGroup(), anDO.getUnitOfMeasureId());

        return val;
    }

    private TableDataRow createHeaderRow(int numOfColumns) {
        TableDataRow row;
        TableDataCell cell;

        row = new TableDataRow(numOfColumns);

        cell = row.cells.get(0);
        cell.setValue(consts.get("reportable"));

        cell = row.cells.get(1);
        cell.setValue(consts.get("analyte"));

        cell = row.cells.get(2);
        cell.setValue(consts.get("value"));

        row.style = "SubHeader";
        row.data = new Boolean(true);

        return row;
    }

    private void resizeResultTable(int numOfCols) {
        TableColumn col;
        int width = 206;

        if (numOfCols == 1)
            return;
        else if (numOfCols == 3)
            width = 311;

        if (resultTableCols == null)
            resultTableCols = (ArrayList<TableColumn>)testResultsTable.getColumns().clone();
        testResultsTable.getColumns().clear();
        testResultsTable.clear();

        for (int i = 0; i < numOfCols; i++ ) {
            col = resultTableCols.get(i);
            col.enable(testResultsTable.isEnabled());

            if (i == 0)
                col.setCurrentWidth(65);
            else
                col.setCurrentWidth(width);
            testResultsTable.addColumn(col);
        }
    }

    private void addResultRows(ArrayList<TestAnalyteViewDO> rows) {
        int r, maxCols;
        TableDataRow row;
        TestAnalyteViewDO an;

        r = testResultsTable.getSelectedRow();
        maxCols = displayManager.maxColumnCount();

        for (int i = 0; i < rows.size(); i++ ) {
            an = rows.get(i);
            row = new TableDataRow(maxCols);

            row.data = new Boolean(false);
            row.cells.get(0).value = an.getIsReportable();
            row.cells.get(1).value = an.getAnalyteName();

            addedTestAnalyteId = an.getId();
            addedAnalyteId = an.getAnalyteId();
            addedAnalyteName = an.getAnalyteName();

            r++ ;
            testResultsTable.addRow(r, row);
        }
    }

    private void onTablePopoutClick() {
        try {
            if (resultPopoutScreen == null)
                resultPopoutScreen = new ResultTab();

            ScreenWindow modal = new ScreenWindow(ScreenWindow.Mode.LOOK_UP);
            modal.setName(consts.get("testResults"));

            // having problems with losing the last cell edited. This will tell
            // the table
            // to save all values before closing
            modal.addBeforeClosedHandler(new BeforeCloseHandler<ScreenWindow>() {
                public void onBeforeClosed(BeforeCloseEvent<ScreenWindow> event) {
                    if (EnumSet.of(State.ADD, State.UPDATE).contains(state))
                        resultPopoutScreen.testResultsTable.finishEditing();
                }
            });

            modal.setContent(resultPopoutScreen);
            resultPopoutScreen.setData(bundle);
            resultPopoutScreen.setScreenState(state);
            resultPopoutScreen.draw();

            modal.addBeforeClosedHandler(new BeforeCloseHandler<ScreenWindow>() {
                public void onBeforeClosed(BeforeCloseEvent<ScreenWindow> event) {
                    // having problems with losing the last cell edited. This
                    // will tell the table
                    // to save all values before closing
                    resultPopoutScreen.testResultsTable.finishEditing();

                    loaded = false;
                    draw();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Window.alert("onTablePopoutClick: " + e.getMessage());
            return;
        }
    }

    private void initializeDropdowns() {
        try {
            analysisCancelledId = DictionaryCache.getIdFromSystemName("analysis_cancelled");
            analysisReleasedId = DictionaryCache.getIdFromSystemName("analysis_released");
            testAnalyteReadOnlyId = DictionaryCache.getIdFromSystemName("test_analyte_read_only");
            testAnalyteRequiredId = DictionaryCache.getIdFromSystemName("test_analyte_req");
            typeAlphaLower = DictionaryCache.getIdFromSystemName("test_res_type_alpha_lower");
            typeAlphaUpper = DictionaryCache.getIdFromSystemName("test_res_type_alpha_upper");

        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
    }

    private boolean onlyRowUnderHeading(int index) {
        boolean prevHeader, postHeader;

        prevHeader = (Boolean)testResultsTable.getRow(index - 1).data;
        postHeader = (Boolean)testResultsTable.getRow(index + 1).data;

        return prevHeader && postHeader;
    }

    private boolean canEdit() {
        return (anDO != null && !analysisCancelledId.equals(anDO.getStatusId()) && !analysisReleasedId.equals(anDO.getStatusId()));
    }
    
    private String formatValue(TestResultDO testResultDO, String value){
        if(typeAlphaUpper.equals(testResultDO.getTypeId()))
            return value.toUpperCase();
        else if(typeAlphaLower.equals(testResultDO.getTypeId()))
            return value.toLowerCase();
        else
            return value;
    }

    public void setScreenState(State state) {
        setState(state);
    }

    public void setData(SampleDataBundle data) {
        try {
            if (data != null && SampleDataBundle.Type.ANALYSIS.equals(data.getType())) {
                analysisMan = data.getSampleManager()
                                  .getSampleItems()
                                  .getAnalysisAt(data.getSampleItemIndex());
                anDO = analysisMan.getAnalysisAt(data.getAnalysisIndex());

                if (state == State.ADD || state == State.UPDATE)
                    StateChangeEvent.fire(this, State.UPDATE);
            } else {
                analysisMan = null;
                anDO = new AnalysisViewDO();
                StateChangeEvent.fire(this, State.DEFAULT);
            }

            bundle = data;
            loaded = false;

        } catch (Exception e) {
            Window.alert("resultTab setData: " + e.getMessage());
        }
    }

    public void draw() {
        if ( !loaded) {
            try {
                if (analysisMan == null || anDO.getTestId() == null)
                    manager = AnalysisResultManager.getInstance();
                else {
                    if (state == State.ADD || state == State.UPDATE)
                        manager = analysisMan.getAnalysisResultAt(bundle.getAnalysisIndex());
                    else
                        manager = analysisMan.getDisplayAnalysisResultAt(bundle.getAnalysisIndex());
                }

                displayManager = new TestAnalyteDisplayManager<ResultViewDO>();
                displayManager.setDataGrid(manager.getResults());

                DataChangeEvent.fire(this);
                loaded = true;
            } catch (Exception e) {
                Window.alert(e.getMessage());
            }
        }
    }

    public HandlerRegistration addActionHandler(ActionHandler<ResultTab.Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}
