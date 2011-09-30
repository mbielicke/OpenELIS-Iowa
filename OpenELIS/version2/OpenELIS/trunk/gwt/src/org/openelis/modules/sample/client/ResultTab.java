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
import org.openelis.cache.SectionCache;
import org.openelis.cache.UserCache;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestResultDO;
import org.openelis.exception.ParseException;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.SectionPermission;
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
import org.openelis.gwt.widget.ScreenWindowInt;
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
import org.openelis.modules.test.client.TestAnalyteDisplayManager;
import org.openelis.utilcommon.ResultValidator;
import org.openelis.utilcommon.ResultValidator.OptionItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;

public class ResultTab extends Screen implements HasActionHandlers<ResultTab.Action> {
    public enum Action {
        RESULT_HISTORY, REFLEX_ADDED
    };

    private boolean                                 loaded;

    protected AppButton                             addResultButton, removeResultButton,
                                                    suggestionsButton, popoutTable;
    protected Label                                 overrideLabel;
    protected TableWidget                           testResultsTable;
    private ArrayList<TableColumn>                  resultTableCols;

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
                                                    typeDictionary, sampleReleasedId;
    private String                                  addedAnalyteName;

    private TestReflexUtility                       reflexTestUtil;

    public ResultTab(ScreenDefInt def, ScreenWindowInt window, Screen parentScreen) {
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
                testResultsTable.enable(true);
            }
        });

        testResultsTable.addBeforeSelectionHandler(new BeforeSelectionHandler<TableRow>() {
            public void onBeforeSelection(BeforeSelectionEvent<TableRow> event) {
                boolean isHeader;
                TableDataRow row;

                row = event.getItem().row;
                isHeader = ((Boolean)row.data).booleanValue();

                if (isHeader)
                    event.cancel();
            }
        });

        testResultsTable.addSelectionHandler(new SelectionHandler<TableRow>() {
            public void onSelection(SelectionEvent<TableRow> event) {
                int row;
                ResultViewDO data;

                if (EnumSet.of(State.ADD, State.UPDATE).contains(state) &&
                    canEdit() && canEditAnalysis()) {
                    row = testResultsTable.getSelectedRow();
                    data = displayManager.getObjectAt(row, 0);

                    addResultButton.enable(true);
                    suggestionsButton.enable(true);

                    if (testAnalyteRequiredId.equals(data.getTestAnalyteTypeId()))
                        removeResultButton.enable(false);
                    else
                        removeResultButton.enable(true);
                } else {
                    addResultButton.enable(false);
                    removeResultButton.enable(false);
                    suggestionsButton.enable(false);
                }
            }
        });

        testResultsTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                boolean isHeaderRow, enableButton;
                int r, c;
                TableDataRow row;
                ResultViewDO data;
                SectionViewDO section;
                TestAnalyteViewDO testAnalyte;
                SectionPermission perm;
                SampleResultValueTableColumn tabcol;

                perm = null;
                section = null;
                tabcol = null;
                isHeaderRow = false;
                enableButton = true;

                r = event.getRow();
                c = event.getCol();
                row = testResultsTable.getRow(r);
                isHeaderRow = ((Boolean)row.data).booleanValue();                

                if (analysis.getSectionId() != null) {
                	try {
                		section = SectionCache.getById(analysis.getSectionId());
                		perm = UserCache.getPermission().getSection(section.getName());
                	} catch (Exception e) {
                		section = null;
                		perm = null;
                	}
                }
                
                if (isHeaderRow || c == 1 || c >= displayManager.columnCount(r) ||
                    !EnumSet.of(State.ADD, State.UPDATE).contains(state)) {
                    event.cancel();
                    enableButton = false;
                } else if (!canEdit()) {
                    window.setError(consts.get("cantUpdateReleasedException"));
                    event.cancel();
                    enableButton = false;
                } else if (section == null) { 
					window.setError(consts.get("noSectionsForTest"));
                    event.cancel();
                    enableButton = false;
                } else if (perm == null || !perm.hasCompletePermission()) {
                	window.setError(consts.get("noCompleteTestPermission"));
                    event.cancel();
                    enableButton = false;
                } else if (!canEditAnalysis()) {
                	window.setError(consts.get("analysisCancledOrReleased"));
                    event.cancel();
                    enableButton = false;
                } else {
                    if (c < 2) 
                        data = displayManager.getObjectAt(r, 0);
                    else 
                        data = displayManager.getObjectAt(r, c - 2);                                            

                    testAnalyte = manager.getTestAnalyte(data.getRowGroup(),
                                                         data.getTestAnalyteId());                    
                    if (testAnalyte == null) {
                        window.setError(consts.get("testAnalyteDefinitionChanged"));
                        event.cancel();
                        enableButton = false;
                    } else if (testAnalyteReadOnlyId.equals(testAnalyte.getTypeId()) && c > 0) {
                        event.cancel();
                        enableButton = false;
                    } else if (analysis.getUnitOfMeasureId() == null &&
                               !manager.getResultValidator(data.getResultGroup())
                                       .noUnitsSpecified()) {
                        window.setError(consts.get("unitOfMeasureException"));
                        event.cancel();
                        enableButton = false;
                    } else {
                        window.clearStatus();
                        if (c >= 2) {
                            /*
                             * we do this here in order to make sure that the correct
                             * dropdown for the result group that this cell is showing
                             * the values from is returned as the editor 
                             */
                            tabcol = (SampleResultValueTableColumn)testResultsTable.getColumns().get(c);
                            tabcol.setResultGroup(data.getResultGroup());
                        }
                    }                                                                                   
                }
                suggestionsButton.enable(enableButton);
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

                val = (String)testResultsTable.getObject(row, col);
                if (col == 0) {
                    data = displayManager.getObjectAt(row, 0);
                    data.setIsReportable(val);
                } else if ( !DataBaseUtil.isEmpty(val)) {
                    data = displayManager.getObjectAt(row, col - 2);

                    try {
                        testResultId = manager.validateResultValue(data.getResultGroup(),
                                                                   analysis.getUnitOfMeasureId(),
                                                                   val);
                        testResult = manager.getTestResultList().get(testResultId);

                        data.setTypeId(testResult.getTypeId());
                        data.setTestResultId(testResult.getId());
                        
                        val = manager.formatResultValue(data.getResultGroup(),
                                                        analysis.getUnitOfMeasureId(),
                                                        testResultId, val);
                        data.setValue(val);

                        if ( !typeDictionary.equals(testResult.getTypeId()))
                            testResultsTable.setCell(row, col, val);

                        if (reflexTestUtil == null) {
                            reflexTestUtil = new TestReflexUtility();

                            reflexTestUtil.addActionHandler(new ActionHandler<TestReflexUtility.Action>() {
                                public void onAction(ActionEvent<TestReflexUtility.Action> event) {
                                    if (((ArrayList<SampleDataBundle>)event.getData()).size() > 0)
                                        ActionEvent.fire(resultTab, Action.REFLEX_ADDED,
                                                         event.getData());
                                }
                            });
                        }

                        reflexTestUtil.setScreen(parentScreen);
                        reflexTestUtil.resultEntered(bundle, data);

                    } catch (ParseException e) {
                        testResultsTable.clearCellExceptions(row, col);
                        testResultsTable.setCellException(row, col, e);
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                    }
                } else {
                    testResultsTable.clearCellExceptions(row, col);
                    data = displayManager.getObjectAt(row, col - 2);
                    data.setValue(val);
                    data.setTypeId(null);
                    data.setTestResultId(null);
                }
            }
        });

        testResultsTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                int index, prowIndex, numCols;
                Integer rg,rowGroup;
                String val;
                TableDataRow row;
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
                    rg = data.getResultGroup();
                    row.key = data.getId();
                    try {
                        val = getDefaultValue(data, analysis.getUnitOfMeasureId());
                        testResultsTable.setCell(index, i, val);

                        if ( !DataBaseUtil.isEmpty(val)) {                            
                            testResult = displayManager.validateResultValue(manager,
                                                                            data,
                                                                            analysis.getUnitOfMeasureId());
                            val = manager.formatResultValue(data.getResultGroup(),
                                                            analysis.getUnitOfMeasureId(),
                                                            testResult.getId(), val);
                            data.setValue(val);

                            if (typeDictionary.equals(testResult.getTypeId()))
                                val = DictionaryCache.getById(Integer.parseInt(val))
                                                     .getEntry();

                            testResultsTable.setCell(index, i, val);
                        } 
                    } catch (ParseException e) {
                        testResultsTable.clearCellExceptions(index, i);
                        testResultsTable.setCellException(index, i, e);
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                    }
                    /* 
                     * we can't do this in the block for try-catch because if
                     * the default value set in the DO is invalid, the dropdown 
                     * for that result group won't be created because of the
                     * exception thrown during validation
                     */
                    setDropdownForResultGroup(rg,i);
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
                int row, col;
                ResultViewDO data;
                Popup popUp;

                row = testResultsTable.getSelectedRow();
                col = testResultsTable.getSelectedCol();
                data = displayManager.getObjectAt(row, col - 2);

                if (suggestionsScreen == null) {
                    try {
                        suggestionsScreen = new ResultSuggestionsScreen();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Window.alert("error: " + e.getMessage());
                        return;
                    }
                }

                popUp = new Popup(consts.get("suggestions"), suggestionsScreen);

                suggestionsScreen.setValidator(manager.getResultValidator(data.getResultGroup()),
                                               analysis.getUnitOfMeasureId());

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

        overrideLabel = (Label)def.getWidget("overrideLabel");
        addScreenHandler(popoutTable, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                try {
                    if (bundle.getSampleManager().getQaEvents().hasResultOverrideQA() ||
                        analysisMan.getQAEventAt(bundle.getAnalysisIndex()).hasResultOverrideQA())
                        overrideLabel.setVisible(true);
                    else
                        overrideLabel.setVisible(false);
                } catch (Exception anyE) {
                    overrideLabel.setVisible(false);
                }                    
            }
        });
    }
    
    public void setData(SampleDataBundle data) {
        try {
            bundle = data;
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
            loaded = false;
        } catch (Exception e) {
            Window.alert("resultTab setData: " + e.getMessage());
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
                Window.alert(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public HandlerRegistration addActionHandler(ActionHandler<ResultTab.Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }

    private ArrayList<TableDataRow> getTableModel() {
        int i, c, len, numberOfCols;
        boolean headerFilled, validateResults;
        Integer rg;
        String val, entry;
        TestResultDO testResult;
        ArrayList<TableDataRow> model;
        TableDataRow hrow, row;
        ResultViewDO data;
        SampleResultValueTableColumn tabcol;
        ArrayList<TableColumn> cl;

        /*
         * we are assuming there will be at least 1 non supplemental if there are
         * only supplementals in a row group it will not show a header so the user
         * wont be able to add any analytes
         */
        model = new ArrayList<TableDataRow>();
        if (manager == null || displayManager == null)
            return model;

        validateResults = (state == State.ADD || state == State.UPDATE);
        numberOfCols = displayManager.maxColumnCount();
        resizeResultTable(numberOfCols);

        hrow = null;
        headerFilled = false;
        
        /*
         * Since we maintain a hashmap between result groups and dropdowns in each
         * instance of SampleResultValueTableColumn, we need to make sure that
         * these hashmaps get filled with the latest data every time the analysis
         * changes for which this table is to be loaded. This only needs to happen
         * if the data is to be edited and validated. 
         */
        if (validateResults) { 
            cl = testResultsTable.getColumns();
            for (i = 2; i < cl.size(); i++) {
                tabcol = (SampleResultValueTableColumn)cl.get(i);
                tabcol.clear();
            }
        }
        
        for (i = 0; i < displayManager.rowCount(); i++ ) {
            if (displayManager.isHeaderRow(i)) {
                i++ ;
                hrow = createHeaderRow(numberOfCols);
                model.add(hrow);
                headerFilled = false;
            }

            len = displayManager.columnCount(i) - 2;
            row = new TableDataRow(numberOfCols);
            row.data = new Boolean(false);
            
            for (c = 0; c < len; c++ ) {                
                data = displayManager.getObjectAt(i, c);
                rg = data.getResultGroup();
                row.key = data.getId();
                try {
                    if (c == 0) {
                        row.cells.get(0).setValue(data.getIsReportable());
                        row.cells.get(1).setValue(data.getAnalyte());
                    }

                    val = data.getValue();           
                    
                    /*
                     * this has to be done here as well as later on in this method
                     * because otherwise if validateResults is false, then the value
                     * that gets set in the cell for the type dictionary is the
                     * id of the record and not the entry 
                     */
                    if (!typeDictionary.equals(data.getTypeId())) {
                        row.cells.get(c+2).setValue(val);
                    } else {
                        entry = DictionaryCache.getById(Integer.parseInt(val)).getEntry();
                        row.cells.get(c+2).setValue(entry);
                    }
                    
                    if ( !headerFilled && c > 0)
                        hrow.cells.get(c+2).setValue(data.getAnalyte());
                    
                    if ( !validateResults)
                        continue;
                    
                    entry = null;
                    if ( !DataBaseUtil.isEmpty(val)) {
                        testResult = displayManager.validateResultValue(manager,
                                                                        data,
                                                                        analysis.getUnitOfMeasureId());
                        val = manager.formatResultValue(rg,
                                                        analysis.getUnitOfMeasureId(),
                                                        testResult.getId(), val);
                        data.setValue(val);
                        /* 
                         * Since a default can match a dictionary entry for the
                         * same unit, the value returned by validateResultValue()
                         * will be the test result that has the dictionary entry
                         * matching the default. Thus the field "typeId" will 
                         * have changed from what it was before validateResultValue() 
                         * was called, and we have to perform the check for the type
                         * again to set the appropriate value.    
                         */
                        if ( !typeDictionary.equals(data.getTypeId())) {
                            row.cells.get(c+2).setValue(val);
                        } else {
                            entry = DictionaryCache.getById(Integer.parseInt(val)).getEntry();
                            row.cells.get(c+2).setValue(entry);
                        }
                    }                                 
                } catch (ParseException e) {
                    row.cells.get(c+2).clearExceptions();
                    row.cells.get(c+2).addException(e);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }      
                
                /* 
                 * we can't do this in the block for try-catch because in Add mode
                 * the default is the value set in the DO in EJB and if the default
                 * is invalid, the dropdown for that result group won't be created
                 * because of the exception thrown during validation
                 */
                setDropdownForResultGroup(rg, c+2);
            }
            headerFilled = true;
            model.add(row);
        }
        manager.setDefaultsLoaded(true);

        return model;
    }

    private String getDefaultValue(ResultViewDO data, Integer unitOfMeasureId) {
        String val;
        if (data.getValue() != null || data.getId() != null)
            val = data.getValue();
        else
            val = manager.getDefaultValue(data.getResultGroup(), analysis.getUnitOfMeasureId());

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
            resultPopoutScreen.setState(state);
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
            analysisCancelledId = DictionaryCache.getIdBySystemName("analysis_cancelled");
            analysisReleasedId = DictionaryCache.getIdBySystemName("analysis_released");
            sampleReleasedId = DictionaryCache.getIdBySystemName("sample_released");
            testAnalyteReadOnlyId = DictionaryCache.getIdBySystemName("test_analyte_read_only");
            testAnalyteRequiredId = DictionaryCache.getIdBySystemName("test_analyte_req");
            typeDictionary = DictionaryCache.getIdBySystemName("test_res_type_dictionary");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
    }

    private boolean canEdit() {
        return (bundle != null && bundle.getSampleManager() != null &&
                !sampleReleasedId.equals(bundle.getSampleManager().getSample().getStatusId()));
    }
    
    private boolean canEditAnalysis() {
        return (!analysisReleasedId.equals(analysis.getStatusId()) &&
                !analysisCancelledId.equals(analysis.getStatusId()));
    }
    
    private boolean onlyRowUnderHeading(int index) {
        boolean prevHeader, postHeader;
        int size;

        size = testResultsTable.numRows();
        if (index == 0)
            prevHeader = false;
        else
            prevHeader = (Boolean)testResultsTable.getRow(index - 1).data;
        if (index == size - 1)
            postHeader = true;
        else
            postHeader = (Boolean)testResultsTable.getRow(index + 1).data;

        return prevHeader && postHeader;
    }
    
    private void setDropdownForResultGroup(Integer rg, int col) {
        SampleResultValueTableColumn tabcol;
        ResultValidator v;
        ArrayList<TableDataRow> model;
        
        if (rg == null)
            return;
        tabcol = (SampleResultValueTableColumn)testResultsTable.getColumns().get(col);                    
        v = manager.getResultValidator(rg);
        /* 
         * If a result group only has dictionary entries, we show a dropdown and
         * not a textbox when a user tries to edit the cell referring to that 
         * result group. The class SampleResultValueTableColumn is responsible
         * for returning the right dropdown based on the result group. 
         */
        if (v.hasOnlyDictionary()) {                            
            model = new ArrayList<TableDataRow>();
            model.add(new TableDataRow("", ""));
            for (OptionItem oi : v.getDictionaryRanges())                                 
                model.add(new TableDataRow(oi.getValue(), oi.getValue()));
            tabcol.setResultGroupModel(rg, model);
        }
    }
}
