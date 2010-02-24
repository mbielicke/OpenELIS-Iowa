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
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestResultDO;
import org.openelis.exception.ParseException;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;

public class ResultTab extends Screen {
    public enum Action {
        RESULT_HISTORY
    };
    
    private boolean                   loaded;

    protected AppButton               addResultButton, removeResultButton, suggestionsButton;
    protected TableWidget             testResultsTable;
    private ArrayList<TableColumn>    resultTableCols;

    protected TestAnalyteLookupScreen testAnalyteScreen;
    protected ResultSuggestionsScreen suggestionsScreen;

    protected AnalysisResultManager   manager;
    private ResultDisplayManager      displayManager;
    protected GetMatchesHandler       resultMatchesHandler;
    protected AnalysisManager         analysisMan;
    protected AnalysisViewDO          anDO;
    protected SampleDataBundle        bundle;

    private Integer                   analysisCancelledId, analysisReleasedId,
                    testAnalyteReadOnlyId, testAnalyteRequiredId, addedTestAnalyteId,
                    addedAnalyteId;
    private String                    addedAnalyteName;

    public ResultTab(ScreenDefInt def, ScreenWindow window) {
        setDef(def);
        setWindow(window);

        initialize();

        initializeDropdowns();
    }

    private void initialize() {
        testResultsTable = (TableWidget)def.getWidget("testResultsTable");
        addScreenHandler(testResultsTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                testResultsTable.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                testResultsTable.enable(canEdit() &&
                                        EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                               .contains(event.getState()));
                testResultsTable.setQueryMode(event.getState() == State.QUERY);
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

                row = testResultsTable.getSelectedRow();
                resultDO = displayManager.getResultAt(row, 0);

                if (testAnalyteRequiredId.equals(resultDO.getTypeId()))
                    removeResultButton.enable(false);
                else
                    removeResultButton.enable(true);

                addResultButton.enable(true);
                suggestionsButton.enable(true);
            }
        });

        testResultsTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                int r, c;
                TableDataRow row;
                boolean isHeaderRow = false, enableButton = true;
                ResultViewDO resultDO;

                r = event.getRow();
                c = event.getCol();
                row = testResultsTable.getRow(r);
                isHeaderRow = ((Boolean)row.data).booleanValue();

                resultDO = null;
                if(c == 0)
                    resultDO = displayManager.getResultAt(r, 0);
                else if (c != 1)
                    resultDO = displayManager.getResultAt(r, c - 2);

                if (isHeaderRow || c == 1 || c >= (displayManager.columnCount(r) + 2) ||
                    testAnalyteReadOnlyId.equals(resultDO.getTypeId())){
                    event.cancel();
                    enableButton = false;
                }

                if (anDO.getUnitOfMeasureId() == null &&
                    !manager.getResultValidator(resultDO.getResultGroup()).noUnitsSpecified()) {
                    window.setError(consts.get("unitOfMeasureException"));
                    event.cancel();
                    enableButton = false;
                } else
                    window.clearStatus();
                
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

                tableRow = testResultsTable.getRow(row);
                if(col == 0)
                    resultDO = displayManager.getResultAt(row, 0);
                else
                    resultDO = displayManager.getResultAt(row, col - 1);
                val = (String)tableRow.cells.get(col).value;
                resultDO.setValue(val);
                
                if(col == 0){
                    resultDO.setIsReportable(val);
                    
                }else if (!"".equals(val)) {
                    try {
                        testResultId = manager.validateResultValue(resultDO.getResultGroup(), anDO.getUnitOfMeasureId(), val);
                        testResultDo = manager.getTestResultList().get(testResultId);

                        resultDO.setTypeId(testResultDo.getTypeId());
                        resultDO.setTestResultId(testResultDo.getId());
                        resultDO.setValue(val);

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
                    resultDO.setTypeId(null);
                    resultDO.setTestResultId(null);
                }
            }
        });

        testResultsTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                TableDataRow prow;
                int index, prowIndex;
                Integer rowGroup;

                try {

                    index = event.getIndex();
                    prow = testResultsTable.getRow(index - 1);
                    prowIndex = index - 1;

                    // if the row is a header try the row after
                    // this assumes there was at least 1 analyte row before the
                    // current row was added
                    if ( ((Boolean)prow.data).booleanValue()) {
                        prow = testResultsTable.getRow(index + 1);
                        prowIndex = index + 1;
                    }

                    rowGroup = displayManager.getResultAt(prowIndex, 0).getRowGroup();

                    manager.addRowAt(displayManager.getIndexAt(prowIndex), rowGroup,
                                     addedTestAnalyteId, addedAnalyteId, addedAnalyteName);

                    addedTestAnalyteId = null;
                    addedAnalyteId = null;
                    addedAnalyteName = null;

                    displayManager.setDataGrid(manager.getResults());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        testResultsTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                int index;
                try {
                    index = displayManager.getIndexAt(event.getIndex());
                    manager.removeRowAt(index);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        addResultButton = (AppButton)def.getWidget("addResultButton");
        addScreenHandler(addResultButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int row = testResultsTable.getSelectedRow();
                Integer rowGroup = displayManager.getResultAt(row, 0).getRowGroup();

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
                if (r > -1 && testResultsTable.numRows() > 0)
                    testResultsTable.deleteRow(r);
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
                resultDO = displayManager.getResultAt(row, col - 1);

                if (suggestionsScreen == null) {
                    try {
                        suggestionsScreen = new ResultSuggestionsScreen();
                        suggestionsScreen.addActionHandler(new ActionHandler<ResultSuggestionsScreen.Action>() {
                            public void onAction(ActionEvent<ResultSuggestionsScreen.Action> event) {
                                if (event.getAction() == ResultSuggestionsScreen.Action.OK) {
                                    int row;
                                    int col;
                                    ResultViewDO resultDO;
                                    TestResultDO testResultDO;
                                    Integer testResultId;
                                    String val;
                                    
                                    row = testResultsTable.getSelectedRow();
                                    col = testResultsTable.getSelectedCol();
                                    val = (String)event.getData();
                                    testResultsTable.setCell(row, col, val);
                                    resultDO = displayManager.getResultAt(row, col - 1);
                                    testResultsTable.clearCellExceptions(row, col);
                                    
                                    try {
                                        testResultId = manager.validateResultValue(resultDO.getResultGroup(), anDO.getUnitOfMeasureId(), val);
                                        testResultDO = manager.getTestResultList().get(testResultId);

                                        resultDO.setTypeId(testResultDO.getTypeId());
                                        resultDO.setTestResultId(testResultDO.getId());
                                        resultDO.setValue(val);
                                    } catch (ParseException e) {
                                        testResultsTable.clearCellExceptions(row, col);
                                        testResultsTable.setCellException(row, col, e);
                                        resultDO.setTypeId(null);
                                        resultDO.setTestResultId(null);
                                    } catch (Exception e) {
                                        Window.alert(e.getMessage());
                                    }
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
                modal.setContent(suggestionsScreen);

                suggestionsScreen.setValidator(manager.getResultValidator(resultDO.getResultGroup()),
                                               anDO.getUnitOfMeasureId(),
                                               resultDO.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                suggestionsButton.enable(false);
            }
        });
    }

    private ArrayList<TableDataRow> getTableModel() {
        int m, c, len, numberOfCols;
        ArrayList<TableDataRow> model;
        TableDataRow hrow, row;
        ResultViewDO resultDO;
        boolean headerFilled;
        String val;
        Integer testResultId;
        TestResultDO testResultDo;
        boolean validateResults;

        //
        // we are assuming there will be at least 1 non supplemental
        // if there are only supplementals in a row group it will not
        // show a header so the user wont be able to add any analytes
        //
        model = new ArrayList<TableDataRow>();
        if (manager == null || displayManager == null)
            return model;

        numberOfCols = displayManager.maxColumnCount()+1;
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

            len = displayManager.columnCount(m);
            row = new TableDataRow(numberOfCols);
            row.data = new Boolean(false);
            validateResults = (state == State.ADD || state == State.UPDATE);
            
            for (c = 0; c < len; c++ ) {
                resultDO = displayManager.getResultAt(m, c);
                row.key = resultDO.getId();
                if (c == 0) {
                    try{
                        row.cells.get(0).setValue(resultDO.getIsReportable());
                        row.cells.get(1).setValue(resultDO.getAnalyte());
    
                        if (resultDO.getValue() != null || resultDO.getId() != null)
                            val = resultDO.getValue();
                        else
                            val = manager.getDefaultValue(resultDO.getResultGroup(), anDO.getUnitOfMeasureId());
                        
                        row.cells.get(2).setValue(val);
                        
                        if(validateResults && val != null && !"".equals(val)){
                            resultDO.setValue(val);
                            
                            testResultId = manager.validateResultValue(resultDO.getResultGroup(), anDO.getUnitOfMeasureId(), val);
                            testResultDo = manager.getTestResultList().get(testResultId);
    
                            resultDO.setTypeId(testResultDo.getTypeId());
                            resultDO.setTestResultId(testResultDo.getId());
                        }
                    } catch (ParseException e) {
                        row.cells.get(2).clearExceptions();
                        row.cells.get(2).addException(e);
                        resultDO.setTypeId(null);
                        resultDO.setTestResultId(null);
                        
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                    }

                    continue;
                }

                if ( !headerFilled) {
                    hrow.cells.get(c + 2).setValue(resultDO.getAnalyte());
                }

                try{
                    if (resultDO.getValue() != null || resultDO.getId() != null)
                        val = resultDO.getValue();
                        
                    else
                        val = manager.getDefaultValue(resultDO.getResultGroup(), anDO.getUnitOfMeasureId());
                    
                    row.cells.get(c + 2).setValue(val);
                    
                    if(validateResults && val != null && !"".equals(val)){
                        resultDO.setValue(val);
                        testResultId = manager.validateResultValue(resultDO.getResultGroup(), anDO.getUnitOfMeasureId(), val);
                        testResultDo = manager.getTestResultList().get(testResultId);

                        resultDO.setTypeId(testResultDo.getTypeId());
                        resultDO.setTestResultId(testResultDo.getId());
                    }
                } catch (ParseException e) {
                    row.cells.get(c+2).clearExceptions();
                    row.cells.get(c+2).addException(e);
                    resultDO.setTypeId(null);
                    resultDO.setTestResultId(null);
                    
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
            headerFilled = true;
            model.add(row);
        }
        manager.setDefaultsLoaded(true);
        
        return model;
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
        int width = 200;

        if (numOfCols == 1)
            return;
        else if(numOfCols == 3)
            width=250;
        else if(numOfCols == 4)
            width=210;
        
        if (resultTableCols == null)
            resultTableCols = (ArrayList<TableColumn>)testResultsTable.getColumns().clone();
        testResultsTable.getColumns().clear();
        testResultsTable.clear();

        for (int i = 0; i < numOfCols; i++ ) {
            col = resultTableCols.get(i);
            if(i == 0)
                col.setCurrentWidth(65);
            else
                col.setCurrentWidth(width);
            testResultsTable.addColumn(col);
        }
    }

    private void addResultRows(ArrayList<TestAnalyteViewDO> rows) {
        int r, maxCols, numCols;
        TableDataRow row;
        TestAnalyteViewDO an;
        Integer resultGroup;

        r = testResultsTable.getSelectedRow();
        resultGroup = displayManager.getResultAt(r, 0).getResultGroup();
        numCols = displayManager.columnCount(r) + 1;
        maxCols = displayManager.maxColumnCount();

        for (int i = 0; i < rows.size(); i++ ) {
            an = rows.get(i);
            row = new TableDataRow(maxCols);

            row.data = new Boolean(false);
            row.cells.get(0).value = an.getAnalyteName();

            // iterate through cols and load default if necessary
            for (int k = 1; k < numCols; k++ )
                row.cells.get(k).setValue(
                                          manager.getDefaultValue(resultGroup,
                                                                  anDO.getUnitOfMeasureId()));

            addedTestAnalyteId = an.getId();
            addedAnalyteId = an.getAnalyteId();
            addedAnalyteName = an.getAnalyteName();

            r++ ;
            testResultsTable.addRow(r, row);
        }
    }

    private void initializeDropdowns() {
        try {
            analysisCancelledId = DictionaryCache.getIdFromSystemName("analysis_cancelled");
            analysisReleasedId = DictionaryCache.getIdFromSystemName("analysis_released");
            testAnalyteReadOnlyId = DictionaryCache.getIdFromSystemName("test_analyte_read_only");
            testAnalyteRequiredId = DictionaryCache.getIdFromSystemName("test_analyte_req");

        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
    }

    private boolean canEdit() {
        return (anDO != null && !analysisCancelledId.equals(anDO.getStatusId()) && !analysisReleasedId.equals(anDO.getStatusId()));
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

                displayManager = new ResultDisplayManager();
                displayManager.setDataGrid(manager.getResults());

                DataChangeEvent.fire(this);
                loaded = true;
            } catch (Exception e) {
                Window.alert(e.getMessage());
            }
        }
    }
}
