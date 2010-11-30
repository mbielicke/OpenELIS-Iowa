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
import org.openelis.gwt.common.DataBaseUtil;
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
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.ModalWindow;
import org.openelis.gwt.widget.Popup;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.table.Column;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
import org.openelis.gwt.widget.table.TextBoxCell;
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
import org.openelis.modules.test.client.TestAnalyteDisplayManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;

public class ResultTab extends Screen implements HasActionHandlers<ResultTab.Action> {
    public enum Action {
        RESULT_HISTORY, REFLEX_ADDED
    };

    private boolean                                 loaded;

    protected Button                                addResultButton, removeResultButton,
                                                    suggestionsButton, popoutTable;
    protected Table                                 testResultsTable;
    private ArrayList<Column>                       resultTableCols;

    protected TestAnalyteLookupScreen               testAnalyteScreen;
    protected ResultSuggestionsScreen               suggestionsScreen;

    protected ResultTab                             resultPopoutScreen;
    protected AnalysisResultManager                 manager;
    private TestAnalyteDisplayManager<ResultViewDO> displayManager;
    protected GetMatchesHandler                     resultMatchesHandler;
    protected AnalysisManager                       analysisMan;
    protected AnalysisViewDO                        analysis;
    protected SampleDataBundle                      bundle;
    private Screen                                  parentScreen;

    private Integer                                 analysisCancelledId, analysisReleasedId,
                                                    testAnalyteReadOnlyId, testAnalyteRequiredId,
                                                    addedTestAnalyteId, addedAnalyteId,
                                                    typeAlphaLower, typeAlphaUpper;
    private String                                  addedAnalyteName;

    private ReflexTestUtility                       reflexTestUtil;

    public ResultTab(ScreenDefInt def, Window window, Screen parentScreen) {
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
        
        testResultsTable = (Table)def.getWidget("testResultsTable");
        addScreenHandler(testResultsTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                testResultsTable.setModel(getTableModel());

                if (testResultsTable.getRowCount() > 0)
                    popoutTable.setEnabled(true);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                testResultsTable.setEnabled(canEdit() &&
                                        EnumSet.of(State.ADD, State.UPDATE)
                                               .contains(event.getState()));
            }
        });

        testResultsTable.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                boolean isHeader;
                Row row;
                
                if (analysis.getUnitOfMeasureId() == null) {
                    addResultButton.setEnabled(false);
                    removeResultButton.setEnabled(false);
                }

                row = testResultsTable.getRowAt(event.getItem());
                isHeader = ((Boolean)row.getData()).booleanValue();

                if (isHeader)
                    event.cancel();
            }
        });

        testResultsTable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                int row;
                ResultViewDO data;

                if (EnumSet.of(State.ADD, State.UPDATE).contains(state)) {
                    row = testResultsTable.getSelectedRow();
                    data = displayManager.getObjectAt(row, 0);

                    if (testAnalyteRequiredId.equals(data.getTypeId()))
                        removeResultButton.setEnabled(false);
                    else
                        removeResultButton.setEnabled(true);

                    addResultButton.setEnabled(true);
                    suggestionsButton.setEnabled(true);
                }
            }
        });

        testResultsTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                boolean           isHeaderRow, enableButton;
                int               r, c;
                Row               row;
                ResultViewDO      data;
                TestAnalyteViewDO testAnalyte;

                isHeaderRow  = false;
                enableButton = true;

                r = event.getRow();
                c = event.getCol();
                row = testResultsTable.getRowAt(r);
                isHeaderRow = ((Boolean)row.getData()).booleanValue();

                if (isHeaderRow || c == 1 || c >= displayManager.columnCount(r)) {
                    event.cancel();
                    enableButton = false;
                } else {
                    if (c < 2)
                        data = displayManager.getObjectAt(r, 0);
                    else
                        data = displayManager.getObjectAt(r, c - 2);
                    
                    testAnalyte = manager.getTestAnalyte(data.getRowGroup(), data.getTestAnalyteId());
                    if (testAnalyte == null) {
                        window.setError(consts.get("testAnalyteDefinitionChanged"));
                        event.cancel();
                        enableButton = false;
                    } else if (testAnalyteReadOnlyId.equals(testAnalyte.getTypeId()) && c > 0) {                        
                        event.cancel();
                        enableButton = false;
                    } else if (analysis.getUnitOfMeasureId() == null &&                   
                               !manager.getResultValidator(data.getResultGroup()).noUnitsSpecified()) {
                        window.setError(consts.get("unitOfMeasureException"));
                        event.cancel();
                        enableButton = false;
                    } else {
                        window.clearStatus();
                    }
                }
                suggestionsButton.setEnabled(enableButton);
            }
        });

        testResultsTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int row, col;
                String val;
                ResultViewDO data;
                Integer testResultId;
                TestResultDO testResult;

                row = event.getRow();
                col = event.getCol();
                data = null;

                val = (String)testResultsTable.getValueAt(row, col);

                if (col == 0)
                    data = displayManager.getObjectAt(row, 0);
                else
                    data = displayManager.getObjectAt(row, col - 2);

                if (col == 0) {
                    data.setIsReportable(val);

                } else if ( !DataBaseUtil.isEmpty(val)) {
                    try {
                        testResultId = manager.validateResultValue(data.getResultGroup(),
                                                                   analysis.getUnitOfMeasureId(), val);
                        testResult = manager.getTestResultList().get(testResultId);

                        data.setTypeId(testResult.getTypeId());
                        data.setTestResultId(testResult.getId());
                        //if it's alpha we need to set it to the right case                        
                        val = manager.formatResultValue(data.getResultGroup(),
                                                          analysis.getUnitOfMeasureId(), testResultId, val);
                        //val = formatValue(testResult, val);
                        data.setValue(val);
                        testResultsTable.setValueAt(row, col, val);

                        if (reflexTestUtil == null){
                            reflexTestUtil = new ReflexTestUtility();
                        
                            reflexTestUtil.addActionHandler(new ActionHandler<ReflexTestUtility.Action>(){
                                public void onAction(ActionEvent<ReflexTestUtility.Action> event) {
                                    ActionEvent.fire(resultTab, Action.REFLEX_ADDED, event.getData());
                                }
                            });
                        }

                        reflexTestUtil.setScreen(parentScreen);
                        reflexTestUtil.resultEntered(bundle, data);

                    } catch (ParseException e) {
                        testResultsTable.clearExceptions(row, col);
                        testResultsTable.addException(row, col, e);
                        data.setTypeId(null);
                        data.setTestResultId(null);
                    } catch (Exception e) {
                        com.google.gwt.user.client.Window.alert(e.getMessage());
                    }
                } else {
                    testResultsTable.clearExceptions(row, col);
                    data.setValue(val);
                    data.setTypeId(null);
                    data.setTestResultId(null);
                }
            }
        });

        testResultsTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                int index, prowIndex, numCols;
                Integer rowGroup;
                String val;
                Row    row;
                ResultViewDO data;
                TestResultDO testResult;

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

                data = null;
                numCols = displayManager.columnCount(index);
                manager.setDefaultsLoaded(false);
                for (int i = 2; i < numCols; i++ ) {
                    data = displayManager.getObjectAt(index, i - 2);
                    row.setData(data.getId());
                    try {
                        val = getDefaultValue(data, analysis.getUnitOfMeasureId());
                        testResultsTable.setValueAt(index, i, val);   
                        
                        if (!DataBaseUtil.isEmpty(val)) {
                            data.setValue(val);
                            displayManager.validateResultValue(manager, data,
                                                               analysis.getUnitOfMeasureId());
                            
                            testResult = displayManager.validateResultValue(manager, data,
                                                               analysis.getUnitOfMeasureId());
                            //val = formatValue(testResult, val);
                            val = manager.formatResultValue(data.getResultGroup(),
                                                            analysis.getUnitOfMeasureId(),
                                                            testResult.getId(), val);
                            data.setValue(val);
                            testResultsTable.setValueAt(index, i, val);   
                        }
                    } catch (ParseException e) {
                        testResultsTable.clearExceptions(index, i);
                        testResultsTable.addException(index, i, e);
                        data.setTypeId(null);
                        data.setTestResultId(null);

                    } catch (Exception e) {
                        com.google.gwt.user.client.Window.alert(e.getMessage());
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
                    removeResultButton.setEnabled(false);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });

        addResultButton = (Button)def.getWidget("addResultButton");
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
                        com.google.gwt.user.client.Window.alert("error: " + e.getMessage());
                        return;
                    }
                }

                ModalWindow modal = new ModalWindow();
                modal.setName(consts.get("testAnalyteSelection"));
                modal.setContent(testAnalyteScreen);

                testAnalyteScreen.setData(manager.getNonColumnTestAnalytes(rowGroup));
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addResultButton.setEnabled(false);
            }
        });

        removeResultButton = (Button)def.getWidget("removeResultButton");
        addScreenHandler(removeResultButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = testResultsTable.getSelectedRow();
                if (r > -1 && testResultsTable.getRowCount() > 0) {
                    if ( !onlyRowUnderHeading(r))
                        testResultsTable.removeRowAt(r);
                    else
                        window.setError(consts.get("atLeastOneResultUnderHeading"));
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeResultButton.setEnabled(false);
            }
        });

        suggestionsButton = (Button)def.getWidget("suggestionsButton");
        addScreenHandler(suggestionsButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int row, col;
                ResultViewDO data;
                Popup popUp;

                row = testResultsTable.getSelectedRow();
                col = testResultsTable.getEditingCol();
                data = displayManager.getObjectAt(row, col - 2);

                if (suggestionsScreen == null) {
                    try {
                        suggestionsScreen = new ResultSuggestionsScreen();

                    } catch (Exception e) {
                        e.printStackTrace();
                        com.google.gwt.user.client.Window.alert("error: " + e.getMessage());
                        return;
                    }
                }

                popUp = new Popup(consts.get("suggestions"), suggestionsScreen);

                suggestionsScreen.setValidator(manager.getResultValidator(data.getResultGroup()),
                                               analysis.getUnitOfMeasureId());

                popUp.show();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                suggestionsButton.setEnabled(false);
            }
        });

        popoutTable = (Button)def.getWidget("popoutTable");
        addScreenHandler(popoutTable, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                onTablePopoutClick();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                popoutTable.setEnabled(false);
            }
        });
    }

    private ArrayList<Row> getTableModel() {
        int m, c, len, numberOfCols;
        TestResultDO testResult;
        ArrayList<Row> model;
        Row hrow, row;
        ResultViewDO resultDO;
        boolean headerFilled;
        String val;
        boolean validateResults;

        //
        // we are assuming there will be at least 1 non supplemental
        // if there are only supplementals in a row group it will not
        // show a header so the user wont be able to add any analytes
        //
        model = new ArrayList<Row>();
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
            row = new Row(numberOfCols);
            row.setData(new Boolean(false));
            validateResults = (state == State.ADD || state == State.UPDATE);

            for (c = 0; c < len; c++ ) {
                resultDO = displayManager.getObjectAt(m, c);
                //row.key = resultDO.getId();
                try {
                    if (c == 0) {
                        row.setCell(0,resultDO.getIsReportable());
                        row.setCell(1,resultDO.getAnalyte());
                    }

                    val = resultDO.getValue();
                    row.setCell(c + 2,val);

                    if (validateResults && !DataBaseUtil.isEmpty(val)) {
                        resultDO.setValue(val);
                        testResult = displayManager.validateResultValue(manager, resultDO,
                                                           analysis.getUnitOfMeasureId());
                        val = formatValue(testResult, val);
                        resultDO.setValue(val);
                        row.setCell(c + 2,val);
                    }
                } catch (ParseException e) {
                	// NEED TO FIND A WAY TO DO THIS NOW
                	//displayManager.clearExceptions(row,c+2);
                	//displayManager.addException(row,c+2,e);
                    resultDO.setTypeId(null);
                    resultDO.setTestResultId(null);

                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }

                if ( !headerFilled && c > 0)
                    hrow.setCell(c + 2,resultDO.getAnalyte());
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
            val = manager.getDefaultValue(resultDO.getResultGroup(), analysis.getUnitOfMeasureId());

        return val;
    }

    private SubHeaderRow createHeaderRow(int numOfColumns) {
        SubHeaderRow row;

        row = new SubHeaderRow(numOfColumns);

        row.setCell(0,consts.get("reportable"));
        row.setCell(1,consts.get("analyte"));
        row.setCell(2,consts.get("value"));
        
        row.setData(new Boolean(true));

        return row;
    }

    private void resizeResultTable(int numOfCols) {
        Column col;
        TextBoxCell<String> cell;
        TextBox<String> textbox;
        int width = 206;

        if (numOfCols == 1)
            return;
        else if (numOfCols == 3)
            width = 311;

        /*
        if (resultTableCols == null)
            resultTableCols = (ArrayList<Column>)testResultsTable.getColumns().clone();
        testResultsTable.getColumns().clear();
        testResultsTable.clear();
        */
        
       	while(testResultsTable.getColumnCount() > numOfCols)
       		testResultsTable.removeColumnAt(numOfCols);
       	
       	while(testResultsTable.getColumnCount() < numOfCols) {
       		col = testResultsTable.addColumn();
       		textbox = new TextBox<String>();
       		textbox.setMaxLength(80);
       		cell = new TextBoxCell<String>(textbox);
       		col.setCellRenderer(cell);
       	}


        for (int i = 0; i < numOfCols; i++ ) {
        	/*
            col = resultTableCols.get(i);
            col.setEnabled(testResultsTable.isEnabled());
			*/
        	col = testResultsTable.getColumnAt(i);
            if (i == 0)
                col.setWidth(65);
            else
                col.setWidth(width);
            //testResultsTable.addColumn(col);
        }
    }

    private void addResultRows(ArrayList<TestAnalyteViewDO> rows) {
        int r, maxCols;
        Row row;
        TestAnalyteViewDO an;

        r = testResultsTable.getSelectedRow();
        maxCols = displayManager.maxColumnCount();

        for (int i = 0; i < rows.size(); i++ ) {
            an = rows.get(i);
            row = new Row(maxCols);

            row.setData(new Boolean(false));
            row.setCell(0,an.getIsReportable());
            row.setCell(1,an.getAnalyteName());

            addedTestAnalyteId = an.getId();
            addedAnalyteId = an.getAnalyteId();
            addedAnalyteName = an.getAnalyteName();

            r++ ;
            testResultsTable.addRowAt(r, row);
        }
    }

    private void onTablePopoutClick() {
        try {
            if (resultPopoutScreen == null)
                resultPopoutScreen = new ResultTab();

            ModalWindow modal = new ModalWindow();
            modal.setName(consts.get("testResults"));

            // having problems with losing the last cell edited. This will tell
            // the table
            // to save all values before closing
            modal.addBeforeClosedHandler(new BeforeCloseHandler<Window>() {
                public void onBeforeClosed(BeforeCloseEvent<Window> event) {
                    if (EnumSet.of(State.ADD, State.UPDATE).contains(state))
                        resultPopoutScreen.testResultsTable.finishEditing();
                }
            });

            modal.setContent(resultPopoutScreen);
            resultPopoutScreen.setData(bundle);
            resultPopoutScreen.setScreenState(state);
            resultPopoutScreen.draw();

            modal.addBeforeClosedHandler(new BeforeCloseHandler<Window>() {
                public void onBeforeClosed(BeforeCloseEvent<Window> event) {
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
            com.google.gwt.user.client.Window.alert("onTablePopoutClick: " + e.getMessage());
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
            com.google.gwt.user.client.Window.alert(e.getMessage());
            window.close();
        }
    }

    private boolean onlyRowUnderHeading(int index) {
        boolean prevHeader, postHeader;

        prevHeader = (Boolean)testResultsTable.getRowAt(index - 1).getData();
        postHeader = (Boolean)testResultsTable.getRowAt(index + 1).getData();

        return prevHeader && postHeader;
    }

    private boolean canEdit() {
        return (analysis != null && !analysisCancelledId.equals(analysis.getStatusId()) && !analysisReleasedId.equals(analysis.getStatusId()));
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
                analysis = analysisMan.getAnalysisAt(data.getAnalysisIndex());

                if (state == State.ADD || state == State.UPDATE)
                    StateChangeEvent.fire(this, State.UPDATE);
            } else {
                analysisMan = null;
                analysis = new AnalysisViewDO();
                StateChangeEvent.fire(this, State.DEFAULT);
            }

            bundle = data;
            loaded = false;

        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert("resultTab setData: " + e.getMessage());
        }
    }

    public void draw() {
        if ( !loaded) {
            try {
                if (analysisMan == null || analysis.getTestId() == null)
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
                com.google.gwt.user.client.Window.alert(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public HandlerRegistration addActionHandler(ActionHandler<ResultTab.Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
    
    private class SubHeaderRow extends Row {
    	public SubHeaderRow(int cols) {
    		super(cols);
    	}
    	
    	@Override
    	public String getStyle(int index) {
    		return "SubHeader";
    	}
    }
}
