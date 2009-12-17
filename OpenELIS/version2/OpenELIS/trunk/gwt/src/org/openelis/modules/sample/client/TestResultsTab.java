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
import org.openelis.gwt.common.LocalizedException;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.table.TableColumn;
import org.openelis.gwt.widget.table.TableDataCell;
import org.openelis.gwt.widget.table.TableDataRow;
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
import com.google.gwt.user.client.Window;

public class TestResultsTab extends Screen {
    private boolean                 loaded;

    protected AppButton               addResultButton, removeResultButton, duplicateResultButton;
    protected TableWidget             testResultsTable;
    private ArrayList<TableColumn> resultTableCols;
    
    protected TestAnalyteLookupScreen testAnalyteScreen;
    
    protected AnalysisResultManager manager;
    private ResultDisplayManager    displayManager;

    protected AnalysisManager       analysisMan;
    protected AnalysisViewDO        anDO;
    
    private Integer analysisCancelledId, analysisReleasedId, testAnalyteReadOnlyId, 
    testAnalyteRequiredId;

    public TestResultsTab(ScreenDefInt def, ScreenWindow window) {
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
        
        testResultsTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler(){
           public void onBeforeCellEdited(BeforeCellEditedEvent event) {
               int r, c;
               TableDataRow row;
               boolean isHeaderRow = false;
               ResultViewDO resultDO;
               TestAnalyteViewDO testAnDo;
               
               r = event.getRow();
               c = event.getCol();                                              
               row = testResultsTable.getRow(r);
               isHeaderRow = ((Boolean)row.data).booleanValue();
               
               testAnDo = null;
               if(c > 0){
                   resultDO = displayManager.getResultAt(r,c-1);
                   testAnDo = manager.getTestAnalyteList().get(resultDO.getTestAnalyteId());
               }
               
               if(isHeaderRow || c == 0 || c >= (displayManager.columnCount(r)+1) || testAnalyteReadOnlyId.equals(testAnDo.getTypeId()))
                   event.cancel();
            } 
        });

        testResultsTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int row, col;
                String val;
                TableDataRow tableRow;
                ResultViewDO resultDO;
                TestAnalyteViewDO testAnDo;
                Integer testResultId;
                TestResultDO testResultDo;
                
                row = event.getRow();
                col = event.getCol();
                
                tableRow = testResultsTable.getRow(row);                
                resultDO = displayManager.getResultAt(row,col-1);
                val = (String)tableRow.cells.get(col).value;
                
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
                    testAnDo = manager.getTestAnalyteList().get(resultDO.getTestAnalyteId());
                    if(testAnalyteRequiredId.equals(testAnDo.getTypeId()))
                        testResultsTable.setCellException(row, col, new LocalizedException("requiredResultException"));
                }
            }
        });

        testResultsTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                //<CHANGE-ME>;
            }
        });

        testResultsTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                //<CHANGE-ME>;
            }
        });

        addResultButton = (AppButton)def.getWidget("addResultButton");
        addScreenHandler(addResultButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                if (testAnalyteScreen == null) {
                    try {
                        testAnalyteScreen = new TestAnalyteLookupScreen();
                        testAnalyteScreen.addActionHandler(new ActionHandler<TestAnalyteLookupScreen.Action>() {
                            public void onAction(ActionEvent<TestAnalyteLookupScreen.Action> event) {
                                if (event.getAction() == TestAnalyteLookupScreen.Action.OK) {
                                    //do something
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
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addResultButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        removeResultButton = (AppButton)def.getWidget("removeResultButton");
        addScreenHandler(removeResultButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                //<CHANGE-ME>;
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeResultButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        duplicateResultButton = (AppButton)def.getWidget("duplicateResultButton");
        addScreenHandler(duplicateResultButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                //<CHANGE-ME>;
            }

            public void onStateChange(StateChangeEvent<State> event) {
                duplicateResultButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });        
    }
    
    private ArrayList<TableDataRow> getTableModel() {
        int m,c,len, numberOfCols;
        ArrayList<TableDataRow> model;
        TableDataRow hrow,row;
        ResultViewDO resultDO;
        boolean headerFilled;
        
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
                    row.cells.get(1).setValue(resultDO.getValue());
                    continue;
                }                        
                
                if(!headerFilled) {
                    hrow.cells.get(c+1).setValue(resultDO.getAnalyte());
                }
                
                row.cells.get(c+1).setValue(resultDO.getValue());
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
        cell.setValue(consts.get("value"));

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
