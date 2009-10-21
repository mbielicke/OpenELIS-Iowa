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
package org.openelis.modules.test.client;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.openelis.cache.DictionaryCache;
import org.openelis.common.AutocompleteRPC;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestResultDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.gwt.common.GridFieldErrorException;
import org.openelis.gwt.common.LocalizedException;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.BeforeGetMatchesEvent;
import org.openelis.gwt.event.BeforeGetMatchesHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.ScrollableTabBar;
import org.openelis.gwt.widget.table.TableColumn;
import org.openelis.gwt.widget.table.TableDataCell;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.BeforeRowAddedEvent;
import org.openelis.gwt.widget.table.event.BeforeRowAddedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.TestAnalyteManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestResultManager;
import org.openelis.manager.TestTypeOfSampleManager;
import org.openelis.metamap.TestMetaMap;
import org.openelis.modules.dictionaryentrypicker.client.DictionaryEntryPickerScreen;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class AnalyteAndResultTab extends Screen implements GetMatchesHandler,BeforeGetMatchesHandler,
                                                           HasActionHandlers<AnalyteAndResultTab.Action> {
    public enum Action {
        ANALYTE_CHANGED,ANALYTE_DELETED,RESULT_CHANGED,RESULT_DELETED
    };
    
    private TestManager                        manager;
    private TestMetaMap                        TestMeta;
    
    private TestAnalyteManager                 testAnalyteManager;
    private TestResultManager                  testResultManager;
    private TestTypeOfSampleManager            sampleTypeManager;
    
    private AnalyteAndResultTab                source;
    private TestAnalyteDisplayManager          displayManager;
    
    private DictionaryEntryPickerScreen        dictEntryPicker; 
    
    private TableWidget                        analyteTable, resultTable;
    private Dropdown<Integer>                  typeId;       
    private Dropdown<String>                   tableActions;
    private ScrollableTabBar                   resultTabPanel;
    private CheckBox                           isReportable;
    private AutoComplete<Integer>              scriptlet;  
    private AppButton                          addButton,removeButton,addResultTabButton,
                                               dictionaryLookUpButton,addTestResultButton,
                                               removeTestResultButton;
    
    private ArrayList<GridFieldErrorException> resultErrorList;   
            
    private boolean                            addAnalyteRow,dropdownsInited,loaded,
                                               headerAddedInTheMiddle,canAddRemoveColumn;
    
    private int                                anaSelCol,tempId;  
    
    public AnalyteAndResultTab(ScreenDefInt def,ScreenService service) {
        setDef(def);
        
        TestMeta = new TestMetaMap();
        this.service = service;
        initialize();          
    }
        
    private void initialize() {                        
        addAnalyteRow = true;
        
        anaSelCol = -1;
        
        source = this;
        
        tempId =  -1;
        
        headerAddedInTheMiddle = false;
        
        resultErrorList = null;
        
        canAddRemoveColumn = false;               
        
        analyteTable = (TableWidget)def.getWidget("analyteTable");
        addScreenHandler(analyteTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                //
                //this table is not queried by,so it needs to be cleared in query mode
                //
                analyteTable.load(getAnalyteTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {                
                analyteTable.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));                
            }
        });
       
        analyteTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler () {

            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                int row, col;
                Integer key;
                TableDataRow set,vset;
                
                row = event.getRow();
                col = event.getCol();                                              
                anaSelCol = col;
                enableAnalyteWidgets(true);
                try {                    
                    set = analyteTable.getRow(row); 
                    if(displayManager.isHeaderRow(row)) {
                        // 
                        // the first two columns of a header row are immutable
                        // and we also don't want to allow a user to be able to
                        // edit a header row column that's beyond the number of
                        // columns for the row group that the analyte rows under
                        // the header belong to; in additionto cancelling the
                        // edit event, we disable the three widgets, disallow 
                        // adding or removing columns and set anaSelCol to -1 
                        // such that the three widgets don't show any data when
                        // refreshAnalyteWidgets() is called
                        //
                        if(col < 2 || col > (displayManager.columnCount(row)+1)) {
                            event.cancel();                            
                            enableAnalyteWidgets(false);
                            canAddRemoveColumn = false;                            
                            anaSelCol = -1;                            
                        } else if(col != 2) {  
                            vset = (TableDataRow)set.cells.get(col-1).getValue(); 
                            //
                            // we don't want to allow editing of a header cell if the
                            // cell to its left doesn't have any data in it                            
                            //
                            if(vset == null || vset.key == null) { 
                                event.cancel();
                                enableAnalyteWidgets(false);
                                anaSelCol = -1;                                
                            } 
                                                    
                            //
                            // since we cannot allow adding or removing of columns
                            // if col exceeds the number of columns that a given
                            // row group has for itself in the manager, we set
                            // canAddRemoveColumn to false if this is the case 
                            // and to true otherwise
                            //
                            if(col <= displayManager.columnCount(row)) 
                                canAddRemoveColumn = true;                                
                            else
                                canAddRemoveColumn = false;                                                           
                        } else if(col == 2) {
                            //
                            // we always allow adding or removing of columns at
                            // the third column of a header row 
                            // 
                            canAddRemoveColumn = true;                            
                        }                           
                    } else if(col > displayManager.columnCount(row)){
                        //
                        // for a header row, we allow editing of the cell that's
                        // next to last cell that has data in it but in the case
                        // of a non-header row, i.e. here we don't;
                        // only cells under those header cells that have data in
                        // them can be edited; in addition to this, we disable 
                        // the three widgets and disallow adding or removing columns 
                        //
                        event.cancel();
                        enableAnalyteWidgets(false);
                        canAddRemoveColumn = false;                        
                    } else if(col > 0) {
                        vset = (TableDataRow)set.cells.get(col).getValue();           
                        if(vset != null && vset.key != null){
                           //
                           // here we check to see if there was a result group 
                           // selected and if there was we open the tab corresponding
                           // to it   
                           key = (Integer)vset.key;
                           if(key-1 < resultTabPanel.getTabBar().getTabCount()){
                               resultTabPanel.selectTab(key-1);
                           }
                        }
                        // 
                        // we disable the three widgets and disallow adding or 
                        // removing columns and make the three widget to show no data 
                        //
                        enableAnalyteWidgets(false);
                        canAddRemoveColumn = false;                        
                        anaSelCol = -1; 
                    }                                    
                } catch(Exception ex) {
                    ex.printStackTrace();
                }                                
                
                //
                // send DataChangeEvent to the three widgets to make them either
                // show the data that corresponds to this cell or make them go blank
                //
                refreshAnalyteWidgets();       
            }
            
        });
        
        analyteTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r,col,numCol,dindex;
                TableDataRow row,value;
                TestAnalyteViewDO anaDO;                
                Integer key;
                AutoComplete<Integer> auto;
                

                r = event.getRow();
                col = event.getCol();
                row = analyteTable.getRow(r);
                value = (TableDataRow)row.cells.get(col).value;
                key = (Integer)value.key;    
                auto = (AutoComplete<Integer>)analyteTable.columns.get(col).getColumnWidget();
                
                try {                                                                                              
                    if((Boolean)row.data) {                        
                        numCol = displayManager.columnCount(r);                        
                        if(numCol < col) {
                            if(key != null) {
                                //
                                // we need to add a new column to the data grid if
                                // this column in the table in a sub header was edited
                                // for the first time and the key set as its value is not null 
                                //
                                dindex = displayManager.getDataRowIndex(r);
                                testAnalyteManager.addColumnAt(dindex, col-1, key);
                                displayManager.setDataGrid(testAnalyteManager.getAnalytes());
                            }
                        } else {
                            //
                            // we need to update all the cells in this column in
                            // the data grid if the column already exists in it 
                            //
                            for(int i = r+1; i < analyteTable.numRows(); i++) {
                                if(displayManager.isHeaderRow(i))
                                    break;
                                anaDO = displayManager.getTestAnalyteAt(i, col-1);
                                anaDO.setAnalyteId(key);                                
                            }
                        }                           
                    } else {                                                
                        if(col == 0) {
                            //
                            // if the updated cell was in a regular row, then 
                            // we need to set the key as the test analyte's id 
                            // in the DO at the appropriate location in the grid 
                            //
                            anaDO = displayManager.getTestAnalyteAt(r, col);
                            anaDO.setAnalyteId(key);
                            anaDO.setAnalyteName(auto.getTextBoxDisplay());
                            ActionEvent.fire(source, Action.ANALYTE_CHANGED, anaDO);
                        } else {
                            //
                            // otherwise we need to set the key as the result group
                            // number in the DO  
                            //
                            anaDO = displayManager.getTestAnalyteAt(r, col-1);
                            anaDO.setResultGroup(key);
                        }
                    }                                        
                                        
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        });       

        analyteTable.addBeforeRowAddedHandler(new BeforeRowAddedHandler() {
            public void onBeforeRowAdded(BeforeRowAddedEvent event) {   
                TableDataRow row;
                int index;
                try {
                    row = event.getRow();
                    index = event.getIndex();
                                                              
                    if(!(Boolean)row.data && index == 0) {             
                            addAnalyteRow = false;
                            analyteTable.addRow(createHeaderRow());                            
                    }
                                                                    
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        analyteTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {                                 
                TableDataRow prow,row,nrow,addrow;
                int index,dindex;
                
                try {
                    //
                    // the current row
                    //
                    row = event.getRow();
                    
                    //
                    // index of the current row in the table
                    //
                    index = event.getIndex();                                        
                    
                    if(!(Boolean)row.data) {     
                        //
                        // the row above the current one in the table
                        //
                        prow = analyteTable.getRow(index-1);  
                        
                        //
                        // the index of the list in TestAnalyteManager that 
                        // corresponds to the row above the current one
                        //
                        dindex = displayManager.getDataRowIndex(index-1);
                        
                        //
                        // dindex can be returned as -1 if index-1 excceds the size of 
                        // displayManager's index list because the list corresponding 
                        // to the new row won't have been added to the grid maintained 
                        // by TestAnalyteManager;so if the number of rows in the table 
                        // is more than one (index > 0), we try to find dindex for the
                        // last row in displayManager's index list; if prow is a header
                        // this row corresponds, is added in the middle of the table,
                        // (headerAddedInTheMiddle == true), i.e. not at the end, then this
                        // means that the list corresponding to the row above this one
                        // which is the header row exists neither in TestAnalyteManager
                        // nor in displayManager,thus we need to look at the list
                        // corresponding to the row two places above (index-2) in the table 
                        //                                                
                        if((dindex == -1 && index > 0) || headerAddedInTheMiddle) {
                            dindex = displayManager.getDataRowIndex(index-2);
                            headerAddedInTheMiddle = false;
                        }
                        
                        if((Boolean)prow.data) { 
                            //
                            // if there were rows after the header row in the table
                            // before the current row was added then we need to 
                            // find out whether the next row after the current row is
                            // an analyte row and if it is then the current
                            // has not been added to a new row group but an existing one
                            // and thus the first boolean argument to addRowAt is false
                            //                                                                                    
                            if(index+1 < analyteTable.numRows()) {
                                nrow = analyteTable.getRow(index+1);
                                if(!(Boolean)nrow.data) {
                                    testAnalyteManager.addRowAt(dindex,false,false,getNextTempId());
                                    return;
                                }
                            }
                            //
                            // otherwise the header row is for a new row group 
                            // and thus the first boolean argument to addRowAt is true 
                            //
                            testAnalyteManager.addRowAt(dindex+1,true,false,getNextTempId());
                        } else {
                            //
                            // if prow
                            //
                            testAnalyteManager.addRowAt(dindex+1,false,true,getNextTempId());
                        }                        
                        displayManager.setDataGrid(testAnalyteManager.getAnalytes());
                        analyteTable.selectRow(index);
                    } else if(addAnalyteRow){
                        addrow = new TableDataRow(10);
                        addrow.data = new Boolean(false);
                        if(index+1 >= analyteTable.numRows()) {                                
                            analyteTable.addRow(addrow);                                
                        } else {                                
                            analyteTable.addRow(index+1,addrow);                                
                        }
                    } else {
                        addAnalyteRow = true;
                    }
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        analyteTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                int index,dindex;
                TableDataRow row;
                TestAnalyteViewDO anaDO;
                
                index = event.getIndex();
                row = event.getRow();

                if(!(Boolean)row.data) {
                    dindex = displayManager.getDataRowIndex(index);
                    anaDO = testAnalyteManager.getAnalyteAt(dindex, 0);
                    testAnalyteManager.removeRowAt(dindex);
                    displayManager.setDataGrid(testAnalyteManager.getAnalytes());
                    ActionEvent.fire(source, Action.ANALYTE_DELETED, anaDO);
                }
            }
        });
        
        addMatchesHandlerToAnalyteCells();

        typeId = (Dropdown<Integer>)def.getWidget(TestMeta.getTestAnalyte().getTypeId());
        addScreenHandler(typeId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {                      
                TestAnalyteViewDO anaDO;                                              
                
                if(displayManager != null && analyteTable.activeRow != -1) {                    
                    if(anaSelCol == 0)
                        anaDO = displayManager.getTestAnalyteAt(analyteTable.activeRow, anaSelCol);
                    else 
                        anaDO = displayManager.getTestAnalyteAt(analyteTable.activeRow, anaSelCol-1);
                                        
                    if(anaDO != null)
                        typeId.setSelection(anaDO.getTypeId());
                    else
                        typeId.setSelection(null);
                    
                }  
               
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                TestAnalyteViewDO anaDO;
                TableDataRow row;
                int i,ar;
                
                ar = analyteTable.activeRow;
                if(ar != -1  && anaSelCol != -1) {
                    row =  analyteTable.getRow(ar);
                    if((Boolean)row.data) {
                        for(i = ar; i < analyteTable.numRows(); i++) {                            
                            anaDO = displayManager.getTestAnalyteAt(i, anaSelCol-1);
                            anaDO.setTypeId(event.getValue());
                            
                            if(displayManager.isHeaderRow(i+1))
                                break;
                        }
                    } else {
                        anaDO = displayManager.getTestAnalyteAt(ar, anaSelCol);
                        anaDO.setTypeId(event.getValue());
                    }
                }                
            }

            public void onStateChange(StateChangeEvent<State> event) {                               
                if(EnumSet.of(State.DEFAULT,State.DISPLAY).contains(event.getState()) && typeId.getData() != null) 
                    typeId.setSelection(null);   
                
                typeId.enable(false);
            }
        });

        isReportable = (CheckBox)def.getWidget(TestMeta.getTestAnalyte().getIsReportable());
        addScreenHandler(isReportable, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                TestAnalyteViewDO anaDO;                   
                
                if(displayManager != null && analyteTable.activeRow != -1) {                                                      
                    if(anaSelCol == 0)
                        anaDO = displayManager.getTestAnalyteAt(analyteTable.activeRow, anaSelCol);
                    else 
                        anaDO = displayManager.getTestAnalyteAt(analyteTable.activeRow, anaSelCol-1);
                                        
                    if(anaDO != null)
                        isReportable.setValue(anaDO.getIsReportable());
                    else 
                        isReportable.setValue("N");
                    
                }   
                
            }

            public void onValueChange(ValueChangeEvent<String> event) {                                
                TestAnalyteViewDO anaDO;
                TableDataRow row;
                int i,ar;
                
                ar = analyteTable.activeRow;
                if(ar != -1  && anaSelCol != -1) {
                    row =  analyteTable.getRow(ar);
                    if((Boolean)row.data) {
                        for(i = ar; i < analyteTable.numRows(); i++) {                            
                            anaDO = displayManager.getTestAnalyteAt(i, anaSelCol-1);
                            anaDO.setIsReportable(event.getValue());
                            
                            if(displayManager.isHeaderRow(i+1))
                                break;
                        }
                    } else {
                        anaDO = displayManager.getTestAnalyteAt(ar, anaSelCol);
                        anaDO.setIsReportable(event.getValue());
                    }
                }
                                    
            }

            public void onStateChange(StateChangeEvent<State> event) {                
                if(EnumSet.of(State.DEFAULT,State.DISPLAY).contains(event.getState())) 
                    isReportable.setValue("N");          
                
                isReportable.enable(false);                
            }
        });

        scriptlet = (AutoComplete<Integer>)def.getWidget(TestMeta.getTestAnalyte().getScriptletId());
        addScreenHandler(scriptlet, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                TestAnalyteViewDO anaDO;
                
                if(displayManager != null && analyteTable.activeRow != -1) {
                        if(anaSelCol == 0)
                            anaDO = displayManager.getTestAnalyteAt(analyteTable.activeRow, anaSelCol);
                        else 
                            anaDO = displayManager.getTestAnalyteAt(analyteTable.activeRow, anaSelCol-1);                       
                        if(anaDO != null)
                            scriptlet.setSelection(anaDO.getScriptletId(),anaDO.getScriptletName());
                        else
                            scriptlet.setSelection(null,"");
                        
                }                                 
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {                                
                TestAnalyteViewDO anaDO;
                TableDataRow row;
                int i,ar;
                
                ar = analyteTable.activeRow;
                if(ar != -1  && anaSelCol != -1) {
                    row =  analyteTable.getRow(ar);
                    if((Boolean)row.data) {
                        for(i = ar; i < analyteTable.numRows(); i++) {                            
                            anaDO = displayManager.getTestAnalyteAt(i, anaSelCol-1);
                            anaDO.setScriptletId(event.getValue());
                            anaDO.setScriptletName(scriptlet.getTextBoxDisplay());
                            
                            if(displayManager.isHeaderRow(i+1))
                                break;
                        }
                    } else {
                        anaDO = displayManager.getTestAnalyteAt(ar, anaSelCol);
                        anaDO.setScriptletId(event.getValue());
                        anaDO.setScriptletName(scriptlet.getTextBoxDisplay());
                    }
                }
                
            }

            public void onStateChange(StateChangeEvent<State> event) {                                
                if(EnumSet.of(State.DEFAULT,State.DISPLAY).contains(event.getState())) 
                    scriptlet.setSelection(null,"");
                
                scriptlet.enable(false);
            }
        });
        
        // Screens now must implement AutoCompleteCallInt and set themselves as the calling interface       
        scriptlet.addGetMatchesHandler(new GetMatchesHandler() {                        
            public void onGetMatches(GetMatchesEvent event) {
                AutocompleteRPC trpc;
                ArrayList<TableDataRow> model;
                TableDataRow row;
                IdNameDO autoDO;
                
                trpc = new AutocompleteRPC(); 
                trpc.match = event.getMatch();                
                try {
                    trpc = service.call("getScriptletMatches",trpc);
                    model = new ArrayList<TableDataRow>();
                    for(int i = 0; i < trpc.model.size(); i++) {
                        autoDO = (IdNameDO)trpc.model.get(i);
                        row = new TableDataRow(1);
                        row.key = autoDO.getId();
                        row.cells.get(0).value = autoDO.getName();
                        model.add(row);
                    }
                    scriptlet.showAutoMatches(model);
                }catch(Exception e) {
                    Window.alert(e.getMessage());                     
                }
            }
            
        });
        
        tableActions = (Dropdown<String>)def.getWidget("tableActions");
        addScreenHandler(tableActions, new ScreenEventHandler<Integer>() {
            public void onStateChange(StateChangeEvent<State> event) {
                tableActions.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));                
                tableActions.setQueryMode(false);
                if(tableActions.getData() != null && EnumSet.of(State.ADD,State.UPDATE).contains(event.getState())) {
                    tableActions.setSelection("analyte");
                }
            }                        
        });        
        
        addButton = (AppButton)def.getWidget("addButton");
        addScreenHandler(addButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                analyteTable.finishEditing();
                if("analyte".equals(tableActions.getValue())) {
                    addAnalyte();
                } else if("column".equals(tableActions.getValue())) {
                    addColumn();
                } else {
                    addHeader();
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addButton.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });

        removeButton = (AppButton)def.getWidget("removeButton");
        addScreenHandler(removeButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                analyteTable.finishEditing();
                if("analyte".equals(tableActions.getValue())) {
                    removeAnalyte();
                } else if("column".equals(tableActions.getValue())) {
                    removeColumn();
                } else {
                    removeHeader();
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeButton.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });
        
        resultTabPanel = (ScrollableTabBar)def.getWidget("resultTabPanel");
        
        addScreenHandler(resultTabPanel,new ScreenEventHandler<Object>(){
            public void onDataChange(DataChangeEvent event) {
                int numTabs;
                
                resultTabPanel.clearTabs();
                numTabs = testResultManager.groupCount();
                
                for(int i = 0; i < numTabs; i++) 
                    resultTabPanel.addTab(String.valueOf(i+1));                    
                if(numTabs > 0)
                    resultTabPanel.selectTab(0);
            }           
            
        });                              
        
        resultTabPanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>(){
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {                
                resultTable.finishEditing();
            }            
        });
        
        resultTabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                int tab;                
                tab = resultTabPanel.getTabBar().getSelectedTab();
                resultTable.load(getResultTableModel(tab));
                showErrorsForResultGroup(tab);
            }
        });
        
        resultTable = (TableWidget)def.getWidget("resultTable");
        addScreenHandler(resultTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                int selTab;
                
                if(state != State.QUERY) {
                    selTab = resultTabPanel.getTabBar().getSelectedTab();                
                    resultTable.load(getResultTableModel(selTab));
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                resultTable.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                resultTable.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        resultTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {

            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                int row,col,group;                

                row = event.getRow();
                col = event.getCol();                
                group = resultTabPanel.getTabBar().getSelectedTab();
                
                switch(col) {
                    case 0:   
                        clearResultCellError(group,row,TestMeta.getTestResult().getUnitOfMeasureId());
                        break;
                    case 1:   
                        clearResultCellError(group,row,TestMeta.getTestResult().getTypeId());
                        break;
                    case 2:             
                        clearResultCellError(group,row,TestMeta.getTestResult().getValue());
                        break;                    
                }
            }
            
        });

        resultTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int row,col,group;                
                TestResultDO result ;
                Object val;
                Integer typeId;
                TableDataRow trow;
                String sysName,value;

                row = event.getRow();
                col = event.getCol();
                val = resultTable.getRow(row).cells.get(col).getValue();
                group = resultTabPanel.getTabBar().getSelectedTab();
                result = testResultManager.getResultAt(group+1, row);
                sysName = null;
                
                switch(col) {
                    case 0:   
                        result.setUnitOfMeasureId((Integer)val);                        
                        break;
                    case 1:                           
                        result.setTypeId((Integer)val);
                        break;
                    case 2:                                     
                        trow = resultTable.getRow(row);
                        typeId = (Integer)trow.cells.get(1).getValue();
                        
                        if(typeId != null)
                            sysName = DictionaryCache.getSystemNameFromId(typeId);
                        
                        if("test_res_type_numeric".equals(sysName)) {
                            value = validateAndSetNumericValue((String)val,row);                            
                        } else if("test_res_type_titer".equals(sysName)) {
                            value = validateAndSetTiterValue((String)val,row);                            
                        } else {
                            value = (String)val;
                        }                       
                        result.setValue(value);
                        ActionEvent.fire(source, Action.RESULT_CHANGED, result);
                        break;
                    case 3:                
                        result.setFlagsId((Integer)val);
                        break;
                    case 4:   
                        result.setSignificantDigits((Integer)val);                        
                        break;
                    case 5:     
                        result.setRoundingMethodId((Integer)val);
                        break;
                }
            }
        });

        resultTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                int selTab;                                
                                
                if(testResultManager.groupCount() == 0) 
                    testResultManager.addResultGroup();                                    
                
                selTab = resultTabPanel.getTabBar().getSelectedTab();                                                               
                testResultManager.addResult(selTab+1,getNextTempId());
            }
        });

        resultTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                int row,selTab;
                TestResultDO resDO;
                
                selTab = resultTabPanel.getTabBar().getSelectedTab();                
                row = event.getIndex();                     
                resDO = testResultManager.getResultAt(selTab+1, row);
                
                testResultManager.removeResultAt(selTab+1, row);
                
                ActionEvent.fire(source, Action.RESULT_DELETED, resDO);
            }
        });

        addResultTabButton = (AppButton)def.getWidget("addResultTabButton");
        addScreenHandler(addResultTabButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int count;
                count = resultTabPanel.getTabBar().getTabCount();
                                
                resultTabPanel.addTab(String.valueOf(count+1));
                resultTabPanel.selectTab(count);
                testResultManager.addResultGroup();
               
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if(event.getState() == State.ADD || event.getState() == State.UPDATE)
                    addResultTabButton.enable(true);
                else
                    addResultTabButton.enable(false);
            }
        });

        removeTestResultButton = (AppButton)def.getWidget("removeTestResultButton");
        addScreenHandler(removeTestResultButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
               int ar;
               
               ar = resultTable.activeRow;
               
               if(ar == -1)
                   return;
               
               resultTable.deleteRow(ar);   
               resultTable.refresh();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if(event.getState() == State.ADD || event.getState() == State.UPDATE)
                    removeTestResultButton.enable(true);
                else
                    removeTestResultButton.enable(false);
            }
        });

        addTestResultButton = (AppButton)def.getWidget("addTestResultButton");
        addScreenHandler(addTestResultButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {         
                int numTabs;
                
                numTabs = resultTabPanel.getTabBar().getTabCount();
                
                if(numTabs == 0) {                    
                    resultTabPanel.addTab(String.valueOf(1)); 
                    resultTabPanel.selectTab(0);                    
                }
                
                resultTable.addRow();
                resultTable.selectRow(resultTable.numRows()-1);
                resultTable.scrollToSelection();
                resultTable.startEditing(resultTable.numRows()-1, 1);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if(event.getState() == State.ADD || event.getState() == State.UPDATE)
                    addTestResultButton.enable(true);
                else
                    addTestResultButton.enable(false);
            }
        });

        dictionaryLookUpButton = (AppButton)def.getWidget("dictionaryLookUpButton");
        addScreenHandler(dictionaryLookUpButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ScreenWindow modal;                                
                
                if(dictEntryPicker == null) {
                    try {
                        dictEntryPicker = new DictionaryEntryPickerScreen();
                        dictEntryPicker.addActionHandler(new ActionHandler<DictionaryEntryPickerScreen.Action>(){

                            public void onAction(ActionEvent<DictionaryEntryPickerScreen.Action> event) {
                               int selTab,numTabs;
                               ArrayList<TableDataRow> model;
                               TestResultDO resDO;
                               TableDataRow row;
                               Integer dictId;   
                               
                               selTab = resultTabPanel.getTabBar().getSelectedTab();     
                               numTabs = resultTabPanel.getTabBar().getTabCount();
                               if(event.getAction() == DictionaryEntryPickerScreen.Action.OK) {
                                   model = (ArrayList<TableDataRow>)event.getData();
                                   if(model != null) {
                                       if (model.size() > 0 && numTabs == 0) {
                                           Window.alert(consts.get("atleastOneResGrp"));
                                           return;
                                       }               
                                       for(int i = 0; i < model.size(); i++) {
                                           row = model.get(i);                                                   
                                           testResultManager.addResultAt(selTab+1,resultTable.numRows(),getNextTempId());
                                           resDO = testResultManager.getResultAt(selTab+1,resultTable.numRows());
                                           dictId = DictionaryCache.getIdFromSystemName("test_res_type_dictionary");
                                           resDO.setValue((String)row.cells.get(0).getValue());
                                           resDO.setTypeId(dictId);                                           
                                       }
                                       DataChangeEvent.fire(source, resultTable);
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
                modal = new ScreenWindow("Dictionary LookUp","dictionaryEntryPickerScreen","",true,false);
                modal.setName(consts.get("chooseDictEntry"));
                modal.setContent(dictEntryPicker);
                dictEntryPicker.setScreenState(State.DEFAULT);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if(event.getState() == State.ADD || event.getState() == State.UPDATE)
                    dictionaryLookUpButton.enable(true);
                else
                    dictionaryLookUpButton.enable(false);
            }
        });
     
    }
    
    public void setManager(TestManager manager) {        
        this.manager = manager;               
        
        loaded = false;
        
        displayManager = new TestAnalyteDisplayManager();
        
        if(!dropdownsInited) {
            setTestAnalyteTypes();
            setUnitsOfMeasure();
            setTestResultTypes();
            setTestResultFlags();
            setRoundingMethods();
            setTableActions();
            dropdownsInited = true;
        }
    }
    
    public void draw(){
        if(!loaded) {
            try {            
                testAnalyteManager = manager.getTestAnalytes();
                testResultManager = manager.getTestResults();
                resultErrorList = null;
                if(state == State.UPDATE || state == State.ADD) 
                    sampleTypeManager = manager.getSampleTypes();                    
                
                if(testAnalyteManager != null)
                    displayManager.setDataGrid(testAnalyteManager.getAnalytes());
                
            } catch(Exception ex) {
                ex.printStackTrace();
            }
            DataChangeEvent.fire(this);
        }       
        
        setUnitsOfMeasure();
        loaded = true;
    }        
    
    public void onGetMatches(GetMatchesEvent event) {
        AutocompleteRPC trpc;
        ArrayList<TableDataRow> model;
        TableDataRow row;
        int rg;
        String match;
        IdNameDO autoDO;
        
        trpc = new AutocompleteRPC(); 
        trpc.match = event.getMatch();
        model = null;
        
        try {
            if(isAnalyteQuery()) {
                trpc = service.call("getAnalyteMatches",trpc);
                model = new ArrayList<TableDataRow>();
                for(int i = 0; i < trpc.model.size(); i++) {
                    autoDO = (IdNameDO)trpc.model.get(i);
                    row = new TableDataRow(1);
                    row.key = autoDO.getId();
                    row.cells.get(0).value = autoDO.getName();
                    model.add(row);
                }
            } else {
                model = new ArrayList<TableDataRow>(); 
                match = event.getMatch();
                try {
                    rg = Integer.parseInt(match);
                    if(rg <= resultTabPanel.getTabBar().getTabCount()){
                        model.add(new TableDataRow(rg,match));
                    } else {
                        model.add(new TableDataRow(null,""));
                    }
                }catch(NumberFormatException e){
                    model.add(new TableDataRow(null,""));
                }
            }
            ((AutoComplete)event.getSource()).showAutoMatches(model);
        }catch(Exception e) {
            Window.alert(e.getMessage());                     
        }
        
    }

    public void onBeforeGetMatches(BeforeGetMatchesEvent event) {
        // TODO Auto-generated method stub
        
    }
    
    public void showTestAnalyteError(GridFieldErrorException error) {
        int dindex,trindex,col,i;
        String field,message;
        TableDataRow trow;
        ArrayList<LocalizedException> errors;
        
        
        dindex = error.getRowIndex();
        col = error.getColumnIndex();
        field = error.getFieldName();
        message = error.getMessage();
        
        //
        // find out which table row does the grid row at dindex represent
        //
        for(trindex = 0; trindex < displayManager.rowCount(); trindex++) {
           if(dindex == displayManager.getIndexAt(trindex))                
               break;                                         
        }
        
        if(TestMeta.getTestAnalyte().getResultGroup().equals(field)) {
            errors = analyteTable.getCell(trindex, col+1).getExceptions();
            if(errors == null || !errors.contains(message))
                analyteTable.setCellException(trindex, col+1, error);
        } else if(col == 0){
            errors = analyteTable.getCell(trindex, col).getExceptions();
            if(errors == null || !errors.contains(message))
                analyteTable.setCellException(trindex, col, error);
        } else {
            for(i = trindex-1; i > -1; i--) {            
                trow = analyteTable.getRow(i); 
                errors = analyteTable.getCell(i, col+1).getExceptions();                
                if((Boolean)trow.data) { 
                    if((errors == null || !errors.contains(message))) {                               
                        analyteTable.setCellException(i, col+1, error);
                    }
                    break;                                      
              }
            }
        }
    }
    
    public void showTestResultError(GridFieldErrorException error) {        
        if(resultErrorList == null)
            resultErrorList = new ArrayList<GridFieldErrorException>();
        resultErrorList.add(error);
        showErrorsForResultGroup(resultTabPanel.getTabBar().getSelectedTab());
    }
    
    public HandlerRegistration addActionHandler(ActionHandler<AnalyteAndResultTab.Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }        
    
    public void setState(State state) {
        super.setState(state);
        if(state == State.ADD || state == State.UPDATE) {
            anaSelCol = -1;
            canAddRemoveColumn = false;
        }
    } 

    protected void clearKeys(TestAnalyteManager tam,TestResultManager trm) {
        TestAnalyteViewDO anaDO;
        TestResultDO resDO;
        Integer id;
        int i, j;
        
        for(i = 0; i < tam.rowCount(); i++) {
            for(j = 0; j < tam.columnCount(i); j++) {
                anaDO = tam.getAnalyteAt(i, j);
                if(j == 0) {
                    anaDO.setId(anaDO.getId()*(-1));
                    id = anaDO.getId();
                    if(id < tempId) 
                        tempId = id;
                } else {
                    anaDO.setId(null);
                }
                anaDO.setTestId(null);                
            }
        }
        
        for(i = 1; i < trm.groupCount()+1; i++) {
            for(j = 0; j < trm.getResultGroupSize(i); j++) {
                resDO = trm.getResultAt(i, j);
                resDO.setId(resDO.getId()*(-1));
                resDO.setTestId(null);
                id = resDO.getId();
                if(id < tempId) 
                    tempId = id;
            }
        }
            
    }

    protected void finishEditing() {
        analyteTable.finishEditing();
        resultTable.finishEditing();       
    }
        
    
    private ArrayList<TableDataRow> getAnalyteTableModel() {
       int m,c,len;
       ArrayList<TableDataRow> model;
       TableDataRow hrow,row;
       TestAnalyteViewDO anaDO;
       boolean headerFilled;
       
       model = new ArrayList<TableDataRow>();
       if (manager == null)
           return model; 
              
       hrow = null;
       headerFilled = false;
       
       for(m = 0; m < displayManager.rowCount(); m++) {
           if(displayManager.isHeaderRow(m)) {
               m++;
               hrow = createHeaderRow();
               model.add(hrow);
               headerFilled = false;              
           }
           
           len = displayManager.columnCount(m);
           row =  new TableDataRow(10);
           row.data = new Boolean(false);
           for(c = 0; c < len; c++) {                        
               anaDO = displayManager.getTestAnalyteAt(m,c);
               row.key = anaDO.getId();
               if(c == 0) {
                   row.cells.get(0).setValue(new TableDataRow(anaDO.getAnalyteId(),anaDO.getAnalyteName()));
                   row.cells.get(1).setValue(new TableDataRow(anaDO.getResultGroup(),String.valueOf(anaDO.getResultGroup())));
                   continue;
               }                        
               
               if(!headerFilled) 
                   hrow.cells.get(c+1).setValue(new TableDataRow(anaDO.getAnalyteId(),anaDO.getAnalyteName()));
               
               row.cells.get(c+1).setValue(new TableDataRow(anaDO.getResultGroup(),String.valueOf(anaDO.getResultGroup())));
           }
           headerFilled = true;
           model.add(row);
       }
       return model;
       
    } 
    
    private TableDataRow createHeaderRow() {
        TableDataRow row;       
        TableDataCell cell;
                
        row = new TableDataRow(10);
        
        cell = row.cells.get(0);
        cell.setValue(new TableDataRow(-1,consts.get("analyte")));      
        
        cell = row.cells.get(1);
        cell.setValue(new TableDataRow(-1,consts.get("value")));
        row.style = "SubHeader";
        row.data = new Boolean(true);
        
        return row;
    } 
    
    private void setTestAnalyteTypes() {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        List<DictionaryDO> list = DictionaryCache.getListByCategorySystemName("test_analyte_type");
        model.add(new TableDataRow(null, ""));
        for(DictionaryDO resultDO :  list){
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        } 
        typeId.setModel(model);
        
    }            
    
    private void setUnitsOfMeasure() {        
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        TableColumn column =  resultTable.columns.get(0);
        List<DictionaryDO> dictList;
        TestTypeOfSampleDO stDO;
        String entry;
        Integer unitId;
        
        model.add(new TableDataRow(null, ""));
        if(state == State.ADD || state == State.UPDATE) {
            for(int i=0; i < sampleTypeManager.count(); i++) {
                stDO = sampleTypeManager.getTypeAt(i);
                unitId = stDO.getUnitOfMeasureId();
                if(unitId != null) {
                 entry = DictionaryCache.getEntryFromId(unitId).getEntry();
                 model.add(new TableDataRow(unitId, entry));
                }
            }
        } else {
            dictList = DictionaryCache.getListByCategorySystemName("unit_of_measure");
            for (DictionaryDO resultDO : dictList) {
                model.add(new TableDataRow(resultDO.getId(), resultDO.getEntry()));
            }
        }
        ((Dropdown<Integer>)column.getColumnWidget()).setModel(model);
    }
    
    private void setTestResultTypes() {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        TableColumn column =  resultTable.columns.get(1);
        List<DictionaryDO> list = DictionaryCache.getListByCategorySystemName("test_result_type");        
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : list) {
            model.add(new TableDataRow(resultDO.getId(), resultDO.getEntry()));
        }
        ((Dropdown<Integer>)column.getColumnWidget()).setModel(model);
    }
    
    private void setTestResultFlags() {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        TableColumn column =  resultTable.columns.get(3);
        List<DictionaryDO> list = DictionaryCache.getListByCategorySystemName("test_result_flags");        
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : list) {
            model.add(new TableDataRow(resultDO.getId(), resultDO.getEntry()));
        }
        ((Dropdown<Integer>)column.getColumnWidget()).setModel(model);
    }
    
    private void setRoundingMethods() {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        TableColumn column =  resultTable.columns.get(5);
        List<DictionaryDO> list = DictionaryCache.getListByCategorySystemName("rounding_method");        
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : list) {
            model.add(new TableDataRow(resultDO.getId(), resultDO.getEntry()));
        }
        ((Dropdown<Integer>)column.getColumnWidget()).setModel(model);
    }
    
    private boolean isAnalyteQuery() {
        TableDataRow row;
        int ac;        
        row = analyteTable.getRow(analyteTable.activeRow);
        ac = analyteTable.activeCell;
        if(((Boolean)row.data && ac > 1) || 
                        (!(Boolean)row.data && ac == 0))
            return true;
        
        return false;
    }
    
    private void addMatchesHandlerToAnalyteCells() {
        AutoComplete<Integer> ac;
        ArrayList<TableColumn> columns;
        
        columns = analyteTable.columns;
        for(int i = 0; i < columns.size(); i++) {
            ac = (AutoComplete<Integer>)columns.get(i).getColumnWidget();
            ac.addBeforeGetMatchesHandler(this);
            ac.addGetMatchesHandler(this);
        }
    }
   
    
   private void refreshAnalyteWidgets() {
        DataChangeEvent.fire(source, isReportable);
        DataChangeEvent.fire(source, typeId);
        DataChangeEvent.fire(source, scriptlet);
    }       
      
   private void enableAnalyteWidgets(boolean enable) {
       isReportable.enable(enable);
       typeId.enable(enable);
       scriptlet.enable(enable);
   }
    
    private void shiftDataAboveToTheRight(int row) {
        TableDataRow trow;
        
        for(int i = row-1; i > -1; i--) {            
            trow = analyteTable.getRow(i);
            shiftDataInRowToTheRight(i);            
            if((Boolean)trow.data)              
                break;
        }
    }
    
    private void shiftDataBelowToTheRight(int row) {
        TableDataRow trow;
        
        for(int i = row+1; i < analyteTable.numRows(); i++) {
            trow = analyteTable.getRow(i);
            if(!(Boolean)trow.data)
                shiftDataInRowToTheRight(i);
            else 
                break;
        }
    }
    
    private void shiftDataInRowToTheRight(int row) {
        int finCol; 
        TableDataRow trow;        
        String lname;
        Integer lkey;
        TableDataRow lrow;
                       
        if(anaSelCol < 2 || anaSelCol == 10 || row < 0) 
            return;
        
        trow = analyteTable.getRow(row);        
        finCol = displayManager.columnCount(row);
        
        if (finCol == 10)
            return;        
        
        lkey = null;
        lname = "";
        try{
            for (int i = finCol; i > anaSelCol; i--) {               
                lrow = (TableDataRow)trow.cells.get(i-1).getValue();     
                if(lrow != null) {
                    lkey = (Integer)lrow.key;                        
                    lname = (String)lrow.cells.get(0).getValue();
                }
                trow.cells.get(i).setValue(new TableDataRow(lkey,lname));
                trow.cells.get(i-1).setValue(new TableDataRow(null,""));                                     
            }   
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }
    
    private void shiftDataInRowToTheLeft(int row) {
        int finalIndex; 
        TableDataRow trow;        
        String rname;
        Integer rkey;
        TableDataRow rrow,blankrow;
        
        if(anaSelCol < 2 || row < 0) 
            return;
        
        trow = analyteTable.getRow(row);        
        finalIndex = displayManager.columnCount(row)+1;
        blankrow = new TableDataRow(null,"");
        
        if(finalIndex == 2)
            return;
        
        if(anaSelCol == 9) {
            trow.cells.get(anaSelCol).setValue(blankrow);            
        } else {                
            for (int i = anaSelCol; i < finalIndex; i++) {
                if(i == finalIndex-1) {
                    trow.cells.get(i).setValue(blankrow);
                } else {
                    rrow = (TableDataRow)trow.cells.get(i + 1).getValue();
                    if(rrow == null) {
                        trow.cells.get(i).setValue(blankrow);
                        continue;
                    }
                    
                    rkey = (Integer)rrow.key;
                    rname = (String)rrow.cells.get(0).getValue();

                    if (rkey != null)
                        trow.cells.get(i)
                                  .setValue(new TableDataRow(rkey, rname));
                    else
                        trow.cells.get(i).setValue(blankrow);
                    
                }
            }
        }        
    }
    
    private void shiftDataAboveToTheLeft(int row) {
        TableDataRow trow;
        
        for(int i = row-1; i > -1; i--) {            
            trow = analyteTable.getRow(i);
            shiftDataInRowToTheLeft(i);            
            if((Boolean)trow.data)              
                break;
        }
    }
    
    private void shiftDataBelowToTheLeft(int row) {
        TableDataRow trow;
        
        for(int i = row+1; i < analyteTable.numRows(); i++) {
            trow = analyteTable.getRow(i);
            if(!(Boolean)trow.data)
                shiftDataInRowToTheLeft(i);
            else 
                break;
        }
    }
    
    private ArrayList<TableDataRow> getResultTableModel(int group) {
        int size;
        ArrayList<TableDataRow> model;        
        TableDataRow row;
        TestResultDO trDO;
        
        model = new ArrayList<TableDataRow>();
        
        if(manager == null)
            return model;
        
        try{            
            size = testResultManager.getResultGroupSize(group+1);
            for(int i = 0; i < size; i++) {
                trDO = testResultManager.getResultAt(group+1, i);
                row = new TableDataRow(6);
                row.cells.get(0).setValue(trDO.getUnitOfMeasureId());
                row.cells.get(1).setValue(trDO.getTypeId());
                row.cells.get(2).setValue(trDO.getValue());
                row.cells.get(3).setValue(trDO.getFlagsId());
                row.cells.get(4).setValue(trDO.getSignificantDigits());
                row.cells.get(5).setValue(trDO.getRoundingMethodId());
                model.add(row);
            }                         
                           
        } catch (Exception e) {    
            e.printStackTrace();
            return null;
        }
        return model;
    }
    
    
    private void showErrorsForResultGroup(int group) {
        GridFieldErrorException exc;
        String message, unit[];
        TableDataRow row;
        int i,j;                
        
        if (resultErrorList == null || resultErrorList.size() == 0)
            return;        
                
        for(i = 0; i < resultTable.numRows(); i++) {
            row = resultTable.getRow(i);
            for(j = 0; j < row.cells.size(); j++) {
                resultTable.clearCellExceptions(i, j);
            }
        }
        
        for (i = 0; i < resultErrorList.size(); i++) {
            exc = resultErrorList.get(i);
            message = exc.getMessage();
            if (exc.getRowIndex() == group) {
                if(message.indexOf("illegalUnitOfMeasureException") != -1) {
                    unit = message.split(":");
                    resultTable.setCellException(exc.getColumnIndex(),
                                             exc.getFieldName(),
                                             new LocalizedException("illegalUnitOfMeasureException",unit[1]));
                    
                    
                } else {      
                    resultTable.setCellException(exc.getColumnIndex(),
                                              exc.getFieldName(),
                                              exc);
                }
            }
        }
    }
    
    private String validateAndSetNumericValue(String value,int row) {              
        boolean convert;
        Double doubleVal,darray[];
        String token,finalValue,strList[];
        int selTab;
        
        selTab = resultTabPanel.getTabBar().getSelectedTab();
        darray = new Double[2];
        //
        // Get the string that was entered if the type
        // chosen was "Numeric" and try to break it up at
        // the "," if it follows the pattern number,number
        //
        if (!"".equals(value.trim())) {    
            strList = value.split(",");
            convert = false;
            if (strList.length == 2) {
                for (int iter = 0; iter < strList.length; iter++) {
                    token = strList[iter];
                    try {
                        // 
                        // Convert each number obtained
                        // from the string and store its value
                        // converted to double if its a valid
                        // number, into an array
                        //
                        doubleVal = Double.valueOf(token);
                        darray[iter] = doubleVal;
                        convert = true;
                    } catch (NumberFormatException ex) {
                        convert = false;
                    }
                }
            }
            
            if (convert) {
                //
                // If it's a valid string store the converted
                // string back into the column otherwise add
                // an error to the cell and store empty
                // string into the cell
                //  
                if (darray[0].toString()
                                .indexOf(".") == -1) {
                    finalValue = darray[0].toString() + ".0"
                    + ",";
                } else {
                    finalValue = darray[0].toString() + ",";
                }
                
                if (darray[1].toString()
                                .indexOf(".") == -1) {
                    finalValue += darray[1].toString() + ".0";
                } else {
                    finalValue += darray[1].toString();
                }
                resultTable.setCell(row,2,finalValue);
                return finalValue;                
            } else {
                resultTable.setCellException(row,2, new LocalizedException("illegalNumericFormatException"));
                addToResltErrorList(selTab,row,TestMeta.getTestResult().getValue(),"illegalNumericFormatException"); 
            }    
        }  else {
            resultTable.setCellException(row,2,new LocalizedException("fieldRequiredException"));  
            addToResltErrorList(selTab,row,TestMeta.getTestResult().getValue(),"fieldRequiredException"); 
        }
        resultTable.setCell(row,2,value);
        return value;
    }
    
    private String validateAndSetTiterValue(String value,int row) {                
        boolean valid;
        int selTab;
        String token,strList[];
        
        selTab = resultTabPanel.getTabBar().getSelectedTab();
                
        //
        // Get the string that was entered if the type
        // chosen was "Numeric" and try to break it up at
        // the "," if it follows the pattern number,number
        //
        if (!"".equals(value.trim())) {    
            strList = value.split(":");
            valid = false;
            if (strList.length == 2) {
                for (int iter = 0; iter < strList.length; iter++) {
                    token = strList[iter];
                    try {
                        // 
                        // Convert each number obtained
                        // from the string and store its value
                        // converted to double if its a valid
                        // number, into an array
                        //
                        Integer.parseInt(token);
                        valid = true;
                    } catch (NumberFormatException ex) {
                        valid = false;
                    }
                }
            }
            
            if (!valid) {                                            
                resultTable.setCellException(row,2, new LocalizedException("illegalTiterFormatException"));
                addToResltErrorList(selTab,row,TestMeta.getTestResult().getValue(),"illegalTiterFormatException");   
            }
        }  else {
            resultTable.setCellException(row,2,new LocalizedException("fieldRequiredException"));  
            addToResltErrorList(selTab,row,TestMeta.getTestResult().getValue(),"fieldRequiredException");
        }
        resultTable.setCell(row,2,value); 
        return value;
    }
    
    private int getNextTempId() {
        return --tempId;
    }
    
    private void clearResultCellError(int group,int row, String field) {
        GridFieldErrorException exc;
        int i;
        
        if (resultErrorList == null)
            return;
        
                    
        for (i = 0; i < resultErrorList.size(); i++) {
            exc = resultErrorList.get(i);            
            if (exc.getRowIndex() == group && exc.getColumnIndex() == row && field.equals(exc.getFieldName())) {
                resultErrorList.remove(exc);
            }
        }
    }
    
    private void addToResltErrorList(int group,int row,String field,String error) {
        GridFieldErrorException exc;
        
        exc = new GridFieldErrorException(error,group, row,field,"resultTable");
        if(resultErrorList == null)
            resultErrorList = new ArrayList<GridFieldErrorException>();
        resultErrorList.add(exc);
        
    }
    
    private void setTableActions() {        
        ArrayList<TableDataRow> model;
        
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow("analyte",consts.get("analyte")));
        model.add(new TableDataRow("column", consts.get("column")));
        model.add(new TableDataRow("header",consts.get("header")));
        
        tableActions.setModel(model);
        tableActions.setSelection("analyte");
    }
    
    private void addAnalyte() {
        int ar;
        TableDataRow addrow;
        
        ar = analyteTable.activeRow;
        addrow = new TableDataRow(10);
        addrow.data = new Boolean(false);
        if(ar == -1 || ar == analyteTable.numRows()-1) {            
            analyteTable.addRow(addrow);
            analyteTable.selectRow(analyteTable.numRows()-1);
            analyteTable.scrollToSelection();
            analyteTable.startEditing(analyteTable.numRows()-1, 0);
        } else {
            analyteTable.addRow(ar+1,addrow);
            analyteTable.selectRow(ar+1);
            analyteTable.scrollToSelection();
            analyteTable.startEditing(ar+1, 0);
        }
    }
    
    private void removeAnalyte() {
        int ar;
        TableDataRow row;
        
        ar = analyteTable.activeRow;
        if(ar != -1) {
            row = analyteTable.getRow(ar);
            if(!(Boolean)row.data) {                        
                analyteTable.deleteRow(ar);
                analyteTable.activeRow = -1;
                analyteTable.refresh();
            }
        }
        
    }
    
    private void addColumn() {
        int ar,index;
        
        if(!canAddRemoveColumn) 
            Window.alert(consts.get("cantAddColumn"));
        
        if(anaSelCol != -1 && analyteTable.activeRow != -1) {
            index = displayManager.getDataRowIndex(analyteTable.activeRow);
            testAnalyteManager.addColumnAt(index, anaSelCol-1, null);
            displayManager.setDataGrid(testAnalyteManager.getAnalytes());
            
            ar = analyteTable.activeRow;               
            
            shiftDataInRowToTheRight(ar);
            //shiftDataAboveToTheRight(ar);
            shiftDataBelowToTheRight(ar);        

            analyteTable.refresh();
            anaSelCol = -1;
        }
    }
    
    private void removeColumn() {
        int ar,index;
        
        if(!canAddRemoveColumn) 
            Window.alert(consts.get("cantRemoveColumn"));
        
        ar = analyteTable.activeRow;
        if(anaSelCol != -1 && ar != -1) {                                                                                              
            shiftDataInRowToTheLeft(ar);                   
            shiftDataBelowToTheLeft(ar);        

            analyteTable.refresh();                    
            
            index = displayManager.getDataRowIndex(ar);
            testAnalyteManager.removeColumnAt(index, anaSelCol-1);
            displayManager.setDataGrid(testAnalyteManager.getAnalytes());
            
            anaSelCol = -1;
        }
    }

    private void addHeader() {
        int ar,num;
        TableDataRow row;
        
        ar = analyteTable.activeRow;
        num = analyteTable.numRows(); 
        
        if(ar == -1 || ar == num-1) {
            analyteTable.addRow(createHeaderRow());
            //analyteTable.selectRow(num);
            analyteTable.scrollToSelection();
        } else {
            row = analyteTable.getRow(ar);
            if((Boolean)row.data) {    
                headerAddedInTheMiddle = true;
                analyteTable.addRow(ar,createHeaderRow());
                //analyteTable.selectRow(ar);
                analyteTable.scrollToSelection();                                    
            } else { 
                row = analyteTable.getRow(ar+1);
                if((Boolean)row.data) {
                    headerAddedInTheMiddle = true;
                    analyteTable.addRow(ar+1,createHeaderRow());
                    //analyteTable.selectRow(ar+1);
                    analyteTable.scrollToSelection();                                        
                } else {
                    Window.alert(consts.get("headerCantBeAddedInsideGroup"));
                }
            }
        }
    }
    
    private void removeHeader() {
        int ar;
        TableDataRow row;
        
        ar = analyteTable.activeRow;                
        
        if(ar != -1) {
            row = analyteTable.getRow(ar);                    
            if((Boolean)row.data) {  
                analyteTable.deleteRow(ar);
                while(ar < analyteTable.numRows()) {                        
                    row = analyteTable.getRow(ar);
                    if((Boolean)row.data) 
                        break;
                    
                    analyteTable.deleteRow(ar);
                }
                analyteTable.activeRow = -1;
            }
        }
    }
    
    
}
 