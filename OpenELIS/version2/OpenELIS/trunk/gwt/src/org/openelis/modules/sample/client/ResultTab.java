/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
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
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;

public class ResultTab extends Screen {
    private boolean                 loaded;

    protected AppButton               addResultButton, removeResultButton;
    protected TableWidget             testResultsTable;
    private ArrayList<TableColumn> resultTableCols;
    
    protected TestAnalyteLookupScreen testAnalyteScreen;
    
    protected AnalysisResultManager manager;
    private ResultDisplayManager    displayManager;
    protected GetMatchesHandler     resultMatchesHandler;
    protected AnalysisManager       analysisMan;
    protected AnalysisViewDO        anDO;
    
    private Integer analysisCancelledId, analysisReleasedId, testAnalyteReadOnlyId, 
    testAnalyteRequiredId, addedTestAnalyteId, addedAnalyteId;
    private String addedAnalyteName;

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
                testResultsTable.enable(canEdit() && EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                testResultsTable.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        testResultsTable.addBeforeSelectionHandler(new BeforeSelectionHandler<TableRow>(){
            public void onBeforeSelection(BeforeSelectionEvent<TableRow> event) {
                TableDataRow row; 
                boolean isHeader;
                
                row = event.getItem().row;
                isHeader = ((Boolean)row.data).booleanValue();
                
                if(isHeader)
                    event.cancel();
            }
        });
        
        testResultsTable.addSelectionHandler(new SelectionHandler<TableRow>(){
           public void onSelection(SelectionEvent<TableRow> event) {
               int row;
               ResultViewDO resultDO;
               
               row = testResultsTable.getSelectedRow();
               resultDO = displayManager.getResultAt(row,0);
               
               if(testAnalyteRequiredId.equals(resultDO.getTypeId()))
                   removeResultButton.enable(false);
               else
                   removeResultButton.enable(true);
               
               addResultButton.enable(true);
           }
        });
        
        testResultsTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler(){
           public void onBeforeCellEdited(BeforeCellEditedEvent event) {
               int r, c;
               TableDataRow row;
               boolean isHeaderRow = false;
               ResultViewDO resultDO;
               
               r = event.getRow();
               c = event.getCol();                                              
               row = testResultsTable.getRow(r);
               isHeaderRow = ((Boolean)row.data).booleanValue();
               
               resultDO = null;
               if(c > 0)
                   resultDO = displayManager.getResultAt(r,c-1);
               
               if(isHeaderRow || c == 0 || c >= (displayManager.columnCount(r)+1) || testAnalyteReadOnlyId.equals(resultDO.getTypeId()))
                   event.cancel();
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
                resultDO = displayManager.getResultAt(row,col-1);
                val = (String)((TableDataRow)tableRow.cells.get(col).value).key;
                
                if(!"".equals(val)){
                    try{
                        testResultId = manager.validateResultValue(resultDO.getResultGroup(), anDO.getUnitOfMeasureId(), val);
                        testResultDo = manager.getTestResultList().get(testResultId);
                        
                        resultDO.setTypeId(testResultDo.getTypeId());
                        resultDO.setTestResultId(testResultDo.getId());
                        resultDO.setValue(val);
                        
                    }catch(ParseException e){
                        testResultsTable.clearCellExceptions(row, col);
                        testResultsTable.setCellException(row, col, e);
                    }catch(Exception e){
                        Window.alert(e.getMessage());
                    }
                }else{
                    testResultsTable.clearCellExceptions(row, col);
                }
            }
        });

        testResultsTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                TableDataRow prow;
                int index,prowIndex;
                Integer rowGroup;
                
                try {

                    index = event.getIndex();                                        
                    prow = testResultsTable.getRow(index-1); 
                    prowIndex = index-1;
                    
                    //if the row is a header try the row after
                    //this assumes there was at least 1 analyte row before the current row was added
                    if(((Boolean)prow.data).booleanValue()){
                        prow = testResultsTable.getRow(index+1);
                        prowIndex = index+1;
                    }
                    
                    rowGroup = displayManager.getResultAt(prowIndex, 0).getRowGroup();
                    
                    manager.addRowAt(displayManager.getIndexAt(index), rowGroup, addedTestAnalyteId, addedAnalyteId, addedAnalyteName);
                    
                    addedTestAnalyteId = null;
                    addedAnalyteId = null;
                    addedAnalyteName = null;
                    
                    displayManager.setDataGrid(manager.getResults());
                } catch(Exception ex) {
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
        
        resultMatchesHandler = new GetMatchesHandler(){
            public void onGetMatches(GetMatchesEvent event) {
                String valueEntered;
                int row, col;
                ResultViewDO resultDO;
                ArrayList<String> suggestions;
                ArrayList<TableDataRow> model;
                
                valueEntered= event.getMatch();
                row= testResultsTable.getSelectedRow();
                col = testResultsTable.getSelectedCol();
                
                resultDO = displayManager.getResultAt(row,col-1);
                suggestions = manager.getResultValidator(resultDO.getResultGroup()).getRanges(anDO.getUnitOfMeasureId());
                model = new ArrayList<TableDataRow>();
                model.add(new TableDataRow(valueEntered, valueEntered));
                
                for(int i=0; i<suggestions.size(); i++)
                    model.add(new TableDataRow(suggestions.get(i),suggestions.get(i)));
                
                ((AutoComplete<String>)event.getSource()).showAutoMatches(model);
            }
        };
        
        //set the same matches handler to all cols in the result table
        ((AutoComplete<String>)testResultsTable.getColumns().get(1).colWidget).addGetMatchesHandler(resultMatchesHandler);
        ((AutoComplete<String>)testResultsTable.getColumns().get(2).colWidget).addGetMatchesHandler(resultMatchesHandler);
        ((AutoComplete<String>)testResultsTable.getColumns().get(3).colWidget).addGetMatchesHandler(resultMatchesHandler);
        ((AutoComplete<String>)testResultsTable.getColumns().get(4).colWidget).addGetMatchesHandler(resultMatchesHandler);
        ((AutoComplete<String>)testResultsTable.getColumns().get(5).colWidget).addGetMatchesHandler(resultMatchesHandler);
        ((AutoComplete<String>)testResultsTable.getColumns().get(6).colWidget).addGetMatchesHandler(resultMatchesHandler);
        ((AutoComplete<String>)testResultsTable.getColumns().get(7).colWidget).addGetMatchesHandler(resultMatchesHandler);
        ((AutoComplete<String>)testResultsTable.getColumns().get(8).colWidget).addGetMatchesHandler(resultMatchesHandler);
        ((AutoComplete<String>)testResultsTable.getColumns().get(9).colWidget).addGetMatchesHandler(resultMatchesHandler);

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
    }
    
    private ArrayList<TableDataRow> getTableModel() {
        int m,c,len, numberOfCols;
        ArrayList<TableDataRow> model;
        TableDataRow hrow,row;
        ResultViewDO resultDO;
        boolean headerFilled;
        
        //
        //we are assuming there will be at least 1 non supplemental
        //if there are only supplementals in a row group it will not
        //show a header so the user wont be able to add any analytes
        //
        
        model = new ArrayList<TableDataRow>();
        if (manager == null || displayManager == null)
            return model; 
        
        numberOfCols = displayManager.maxColumnCount();
        resizeResultTable(numberOfCols);
               
        hrow = null;
        headerFilled = false;
        
        for(m = 0; m < displayManager.rowCount(); m++) {
            if(displayManager.isHeaderRow(m)) {
                m++;
                hrow = createHeaderRow(numberOfCols);
                model.add(hrow);
                headerFilled = false;              
            }
            
            len = displayManager.columnCount(m);
            row =  new TableDataRow(numberOfCols);
            row.data = new Boolean(false);
            for(c = 0; c < len; c++) {                        
                resultDO = displayManager.getResultAt(m,c);
                row.key = resultDO.getId();
                if(c == 0) {
                    row.cells.get(0).setValue(resultDO.getAnalyte());
                    row.cells.get(1).setValue(new TableDataRow(resultDO.getValue(),resultDO.getValue()));
                    continue;
                }                        
                
                if(!headerFilled) {
                    hrow.cells.get(c+1).setValue(new TableDataRow(resultDO.getAnalyte(),resultDO.getAnalyte()));
                }
                
                row.cells.get(c+1).setValue(new TableDataRow(resultDO.getValue(),resultDO.getValue()));
            }
            headerFilled = true;
            model.add(row);
        }
        return model;
    }
    
    private TableDataRow createHeaderRow(int numOfColumns) {
        TableDataRow row;       
        TableDataCell cell;
                
        row = new TableDataRow(numOfColumns);
        
        cell = row.cells.get(0);
        cell.setValue(consts.get("analyte"));
        
        cell = row.cells.get(1);
        cell.setValue(new TableDataRow(consts.get("value"),consts.get("value")));

        row.style = "SubHeader";
        row.data = new Boolean(true);
        
        return row;
    } 
    
    private void resizeResultTable(int numOfCols){
        TableColumn col;
        int width=200;
        
        if(numOfCols == 0)
            return;
        
        if(resultTableCols == null)
            resultTableCols = (ArrayList<TableColumn>)testResultsTable.getColumns().clone();
        testResultsTable.getColumns().clear();
        testResultsTable.clear();
        
       if(numOfCols < 5)
           width=(new Integer(testResultsTable.getTableWidth())-(numOfCols*2)) / numOfCols;
        
        for(int i = 0; i < numOfCols; i++) {
            col = resultTableCols.get(i);
            col.setCurrentWidth(width);
            testResultsTable.addColumn(resultTableCols.get(i));
        }
    }
    
    private void addResultRows(ArrayList<TestAnalyteViewDO> rows) {
        int r, numCols;
        TableDataRow row;
        TestAnalyteViewDO an;
        
        r = testResultsTable.getSelectedRow();
        
        numCols = displayManager.maxColumnCount();
        
        for(int i=0; i<rows.size(); i++){
            an = rows.get(i);
            row = new TableDataRow(numCols);
            
            row.data = new Boolean(false);
            row.cells.get(0).value = an.getAnalyteName();
            
            addedTestAnalyteId = an.getId();
            addedAnalyteId = an.getAnalyteId();
            addedAnalyteName = an.getAnalyteName();
            
            r++;
            testResultsTable.addRow(r, row);
        }
    }
    
    private void duplicateRow(){
        
    }
    
    private void initializeDropdowns() {
        try{
            analysisCancelledId = DictionaryCache.getIdFromSystemName("analysis_cancelled");
            analysisReleasedId = DictionaryCache.getIdFromSystemName("analysis_released");
            testAnalyteReadOnlyId = DictionaryCache.getIdFromSystemName("test_analyte_read_only");
            testAnalyteRequiredId = DictionaryCache.getIdFromSystemName("test_analyte_req");
            
        }catch(Exception e){
            Window.alert(e.getMessage());
            window.close();
        }
    }
    
    private boolean canEdit(){
        return (anDO != null && !analysisCancelledId.equals(anDO.getStatusId()) && !analysisReleasedId.equals(anDO.getStatusId()));
    }
    
    public void setData(SampleDataBundle data) {
        if(SampleDataBundle.Type.ANALYSIS == data.type){
            anDO = data.analysisTestDO;
            
            if(state == State.ADD || state == State.UPDATE)
                StateChangeEvent.fire(this, State.UPDATE);
        }else{
            anDO = new AnalysisViewDO();
            StateChangeEvent.fire(this, State.DEFAULT);   
        }
            
        analysisMan = data.analysisManager;
        
        loaded = false;
    }
    
    public void draw(){
        if (!loaded) {
            try {
                if(analysisMan == null)
                    manager = AnalysisResultManager.getInstance();
                else{
                    int index = analysisMan.getIndex(anDO);
                    
                    if(index != -1){
                        if(state == State.ADD || state == State.UPDATE)
                            manager = analysisMan.getAnalysisResultAt(index);
                        else
                            manager = analysisMan.getDisplayAnalysisResultAt(index);
                    }
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
