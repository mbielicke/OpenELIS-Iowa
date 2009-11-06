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
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestResultViewDO;
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
import org.openelis.gwt.widget.QueryFieldUtil;
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
import org.openelis.modules.dictionary.client.DictionaryEntryLookupScreen;
import org.openelis.utilcommon.DataBaseUtil;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class AnalyteAndResultTab extends Screen implements GetMatchesHandler,BeforeGetMatchesHandler,
                                                           HasActionHandlers<AnalyteAndResultTab.Action>,
                                                           ActionHandler<SampleTypeTab.Action> {
    public enum Action {
        ANALYTE_CHANGED,ANALYTE_DELETED,RESULT_CHANGED,RESULT_DELETED
    };
    
    private TestManager                        manager;
    private TestMetaMap                        meta = new TestMetaMap();
    
    private TestAnalyteManager                 testAnalyteManager;
    private TestResultManager                  testResultManager;
    private TestTypeOfSampleManager            sampleTypeManager;
    
    private AnalyteAndResultTab                screen;
    private TestAnalyteDisplayManager          displayManager;
    
    private DictionaryEntryLookupScreen        dictEntryPicker; 
    
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
            
    private boolean                            addAnalyteRow,loaded,
                                               headerAddedInTheMiddle,canAddRemoveColumn;
    
    private int                                anaSelCol,tempId;  
    
    private ScreenService                      scriptletService,analyteService,dictionaryService;
    
    public AnalyteAndResultTab(ScreenDefInt def, ScreenService service,
                               ScreenService scriptletService,ScreenService analyteService,
                               ScreenService dictionaryService) {
        setDef(def);
        
        
        this.service = service;
        this.scriptletService = scriptletService;
        this.analyteService = analyteService;
        this.dictionaryService = dictionaryService;
        initialize();  
        
        initializeDropdowns();
    }
        
    private void initialize() {                        
        addAnalyteRow = true;
        
        anaSelCol = -1;
        
        screen = this;
        
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
                        // the header belong to; in addition to cancelling the
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
                int r,c,numCol,dindex;
                TableDataRow row,value;
                TestAnalyteViewDO data;                
                Integer key;
                AutoComplete<Integer> auto;
                
                r = event.getRow();
                c = event.getCol();
                row = analyteTable.getRow(r);
                value = (TableDataRow)analyteTable.getObject(r, c);
                key = (Integer)value.key;    
                auto = (AutoComplete<Integer>)analyteTable.getColumns().get(c).getColumnWidget();
                
                try {                                                                                              
                    if((Boolean)row.data) {                        
                        numCol = displayManager.columnCount(r);                                                
                        if(numCol < c) {
                            if(key != null) {
                                //
                                // we need to add a new column to the data grid if
                                // this column in the table in a sub header was edited
                                // for the first time and the key set as its value is not null 
                                //
                                dindex = displayManager.getDataRowIndex(r);
                                testAnalyteManager.addColumnAt(dindex, c-1, key,auto.getTextBoxDisplay());
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
                                data = displayManager.getTestAnalyteAt(i, c-1);
                                data.setAnalyteId(key);                                
                            }
                        }   
                        
                        //
                        // since we cannot allow adding or removing of columns
                        // if col exceeds the number of columns that a given
                        // row group has for itself in the manager, we set
                        // canAddRemoveColumn to false if this is the case 
                        // and to true otherwise
                        //
                        if(c <= displayManager.columnCount(r)) 
                            canAddRemoveColumn = true;                                
                        else
                            canAddRemoveColumn = false;
                    } else {                                                
                        if(c == 0) {
                            //
                            // if the updated cell was in a regular row, then 
                            // we need to set the key as the test analyte's id 
                            // in the DO at the appropriate location in the grid 
                            //
                            data = displayManager.getTestAnalyteAt(r, c);
                            data.setAnalyteId(key);
                            data.setAnalyteName(auto.getTextBoxDisplay());
                            ActionEvent.fire(screen, Action.ANALYTE_CHANGED, data);
                        } else {
                            //
                            // otherwise we need to set the key as the result group
                            // number in the DO  
                            //
                            data = displayManager.getTestAnalyteAt(r, c-1);
                            data.setResultGroup(key);
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
                                 
                    //
                    // if the table is empty and an analyte row is to be added 
                    // then a header row should be added before that row 
                    //
                    if(!(Boolean)row.data && index == 0) {             
                        addAnalyteRow = true;                        
                        event.cancel();
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
                    
                    //
                    // if the row added to the table is an analyte row
                    //
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
                        // and has been added in the middle of the table,
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
                                    displayManager.setDataGrid(testAnalyteManager.getAnalytes());
                                    analyteTable.selectRow(index);
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
                            // if prow is an analyte row and then the newly added row has
                            // not been added to a new group and it will have to look at
                            // row previous to it in the data grid to copy data from look
                            //
                            testAnalyteManager.addRowAt(dindex+1,false,true,getNextTempId());
                        }                        
                        displayManager.setDataGrid(testAnalyteManager.getAnalytes());
                        analyteTable.selectRow(index);
                    } else if(addAnalyteRow){
                        //
                        // if the row added is a header row and it's the first
                        // one in the table then an analyte row should be added after it
                        // if the add button was clicked with "Header" selected 
                        //
                        addrow = new TableDataRow(10);
                        addrow.data = new Boolean(false);
                        if(index+1 >= analyteTable.numRows()) {                                
                            analyteTable.addRow(addrow);                                
                        } else {                                
                            analyteTable.addRow(index+1,addrow);                                
                        }
                    } else {
                        //
                        // if the row added is a header row and it's the first
                        // one in the table then an analyte row should be added after it
                        //
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
                TestAnalyteViewDO data;
                
                index = event.getIndex();
                row = event.getRow();

                if(!(Boolean)row.data) {
                    dindex = displayManager.getDataRowIndex(index);
                    data = testAnalyteManager.getAnalyteAt(dindex, 0);
                    testAnalyteManager.removeRowAt(dindex);
                    displayManager.setDataGrid(testAnalyteManager.getAnalytes());
                    ActionEvent.fire(screen, Action.ANALYTE_DELETED, data);
                }
            }
        });
        
        addMatchesHandlerToAnalyteCells();

        typeId = (Dropdown<Integer>)def.getWidget(meta.getTestAnalyte().getTypeId());
        addScreenHandler(typeId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {                      
                TestAnalyteViewDO data;                                              
                
                if(displayManager != null && analyteTable.getSelectedRow() != -1) {                    
                    if(anaSelCol == 0)
                        data = displayManager.getTestAnalyteAt(analyteTable.getSelectedRow(), anaSelCol);
                    else 
                        data = displayManager.getTestAnalyteAt(analyteTable.getSelectedRow(), anaSelCol-1);
                                        
                    if(data != null)
                        typeId.setSelection(data.getTypeId());
                    else
                        typeId.setSelection(null);                    
                }  
               
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                TestAnalyteViewDO anaDO;
                TableDataRow row;
                int i,ar;
                
                ar = analyteTable.getSelectedRow();
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

        isReportable = (CheckBox)def.getWidget(meta.getTestAnalyte().getIsReportable());
        addScreenHandler(isReportable, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                TestAnalyteViewDO data; 
                int ar;
                
                ar = analyteTable.getSelectedRow();
                
                if(displayManager != null &&  ar != -1) {                                                      
                    if(anaSelCol == 0)
                        data = displayManager.getTestAnalyteAt(ar, anaSelCol);
                    else 
                        data = displayManager.getTestAnalyteAt(ar, anaSelCol-1);
                                        
                    if(data != null)
                        isReportable.setValue(data.getIsReportable());
                    else 
                        isReportable.setValue("N");
                    
                }   
                
            }

            public void onValueChange(ValueChangeEvent<String> event) {                                
                TestAnalyteViewDO anaDO;
                TableDataRow row;
                int i,ar;
                
                ar = analyteTable.getSelectedRow();
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

        scriptlet = (AutoComplete<Integer>)def.getWidget(meta.getTestAnalyte().getScriptletId());
        addScreenHandler(scriptlet, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                TestAnalyteViewDO anaDO;
                int ar;
                
                ar = analyteTable.getSelectedRow();
                
                if(displayManager != null && ar != -1) {
                        if(anaSelCol == 0)
                            anaDO = displayManager.getTestAnalyteAt(analyteTable.getSelectedRow(), anaSelCol);
                        else 
                            anaDO = displayManager.getTestAnalyteAt(analyteTable.getSelectedRow(), anaSelCol-1);                       
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
                
                ar = analyteTable.getSelectedRow();
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
                ArrayList<TableDataRow> model;
                ArrayList<IdNameVO> list;                   
                
                try {
                    list = scriptletService.callList("fetchByName",event.getMatch()+"%");
                    model = new ArrayList<TableDataRow>();
                    for(IdNameVO data : list) 
                        model.add(new TableDataRow(data.getId(),data.getName()));
                    
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
                
                //
                //this table is not queried by,so it needs to be cleared in query mode
                //
                if(state != State.QUERY) {
                    selTab = resultTabPanel.getTabBar().getSelectedTab();                
                    resultTable.load(getResultTableModel(selTab));
                } else {
                    resultTable.load(new ArrayList<TableDataRow>());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                resultTable.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));                
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
                        clearResultCellError(group,row,meta.getTestResult().getUnitOfMeasureId());
                        break;
                    case 1:   
                        clearResultCellError(group,row,meta.getTestResult().getTypeId());
                        break;
                    case 2:             
                        clearResultCellError(group,row,meta.getTestResult().getValue());
                        break;                    
                }
            }
            
        });

        resultTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r,c,group;                
                TestResultViewDO data ;
                Object val;
                Integer typeId;
                String sysName,value;

                r = event.getRow();
                c = event.getCol();
                val = resultTable.getObject(r, c);
                group = resultTabPanel.getTabBar().getSelectedTab();
                data = testResultManager.getResultAt(group+1, r);
                sysName = null;
                value = null;
                
                switch(c) {
                    case 0:   
                        data.setUnitOfMeasureId((Integer)val);                        
                        break;
                    case 1:                           
                        data.setTypeId((Integer)val);
                        break;
                    case 2:                                                             
                        typeId = data.getTypeId();
                        
                        if(typeId != null)
                            sysName = DictionaryCache.getSystemNameFromId(typeId);
                        
                        if("test_res_type_numeric".equals(sysName)) {
                            value = validateAndSetNumericValue((String)val,r);                            
                        } else if("test_res_type_titer".equals(sysName)) {
                            value = validateAndSetTiterValue((String)val,r);                            
                        } else if("test_res_type_dictionary".equals(sysName)) {
                            value = validateAndSetDictValue((String)val,r,data);
                            if(value == null)
                                data.setDictionary("");
                            else 
                                data.setDictionary((String)val);
                        } else {
                            value = (String)val;
                        }                     
                        data.setValue(value);
                        ActionEvent.fire(screen, Action.RESULT_CHANGED, data);
                        break;
                    case 3:                
                        data.setFlagsId((Integer)val);
                        break;
                    case 4:   
                        data.setSignificantDigits((Integer)val);                        
                        break;
                    case 5:     
                        data.setRoundingMethodId((Integer)val);
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
                TestResultViewDO resDO;
                
                selTab = resultTabPanel.getTabBar().getSelectedTab();                
                row = event.getIndex();                     
                resDO = testResultManager.getResultAt(selTab+1, row);
                
                testResultManager.removeResultAt(selTab+1, row);
                
                ActionEvent.fire(screen, Action.RESULT_DELETED, resDO);
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
                addResultTabButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        removeTestResultButton = (AppButton)def.getWidget("removeTestResultButton");
        addScreenHandler(removeTestResultButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
               int ar;
               
               ar = resultTable.getSelectedRow();
               
               if(ar != -1 && resultTable.numRows() > 0)                                      
                   resultTable.deleteRow(ar);   
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeTestResultButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));  
            }
        });

        addTestResultButton = (AppButton)def.getWidget("addTestResultButton");
        addScreenHandler(addTestResultButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {         
                int n,numTabs;
                
                numTabs = resultTabPanel.getTabBar().getTabCount();
                
                if(numTabs == 0) {                    
                    resultTabPanel.addTab(String.valueOf(1)); 
                    resultTabPanel.selectTab(0);                    
                }
                
                resultTable.addRow();
                n = resultTable.numRows() - 1;
                resultTable.selectRow(n);
                resultTable.scrollToSelection();
                resultTable.startEditing(n, 1);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addTestResultButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        dictionaryLookUpButton = (AppButton)def.getWidget("dictionaryLookUpButton");
        addScreenHandler(dictionaryLookUpButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                showDictionaryPopUp(null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                dictionaryLookUpButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });
     
    }
    
    public void setManager(TestManager manager) {        
        this.manager = manager;               
        
        loaded = false;
        
        displayManager = new TestAnalyteDisplayManager();        
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
            setUnitsOfMeasure();
        }       
                
        loaded = true;
    }        
    
    public void onGetMatches(GetMatchesEvent event) {
        QueryFieldUtil parser;
        ArrayList<TableDataRow> model;
        int rg;
        String match;
        ArrayList<IdNameVO> list;
        
        parser = new QueryFieldUtil();
        parser.parse(event.getMatch());

        try {
            if(isAnalyteQuery()) {
                list = analyteService.callList("fetchByName",parser.getParameter().get(0));
                model = new ArrayList<TableDataRow>();
                for(IdNameVO data: list) 
                    model.add(new TableDataRow(data.getId(),data.getName()));                
            } else {
                model = new ArrayList<TableDataRow>(); 
                match = event.getMatch();
                try {
                    rg = Integer.parseInt(match);
                    if(rg > 0 && rg <= resultTabPanel.getTabBar().getTabCount()){
                        model.add(new TableDataRow(rg,match));
                    } else {
                        model.add(new TableDataRow(null,""));
                    }
                } catch(NumberFormatException e){
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
        String field;
        TableDataRow trow;
        ArrayList<LocalizedException> errors;
        
        
        dindex = error.getRowIndex();
        col = error.getColumnIndex();
        field = error.getFieldName();
        
        //
        // find out which table row does the grid row at dindex represent
        //
        for(trindex = 0; trindex < displayManager.rowCount(); trindex++) {
           if(dindex == displayManager.getIndexAt(trindex))                
               break;                                         
        }
        
        if(meta.getTestAnalyte().getResultGroup().equals(field)) {
            errors = analyteTable.getCell(trindex, col+1).getExceptions();
            if(errors == null || !errors.contains(error))
                analyteTable.setCellException(trindex, col+1, error);
        } else if(col == 0){
            errors = analyteTable.getCell(trindex, col).getExceptions();
            if(errors == null || !errors.contains(error))
                analyteTable.setCellException(trindex, col, error);
        } else {
            for(i = trindex-1; i > -1; i--) {            
                trow = analyteTable.getRow(i); 
                errors = analyteTable.getCell(i, col+1).getExceptions();                
                if((Boolean)trow.data) { 
                    if((errors == null || !errors.contains(error))) {                               
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
    
    public void onAction(ActionEvent<SampleTypeTab.Action> event) {        
        if(state == State.QUERY)
            return;
        
        if(event.getAction() == SampleTypeTab.Action.UNIT_CHANGED ||
                        event.getAction() == SampleTypeTab.Action.UNIT_DELETED) {
            setUnitsOfMeasure();
        }
    }

    protected void clearKeys(TestAnalyteManager tam,TestResultManager trm) {
        TestAnalyteViewDO anaDO;
        TestResultViewDO resDO;
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
    
    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        List<DictionaryDO> list;
        
        model = new ArrayList<TableDataRow>();
        list = DictionaryCache.getListByCategorySystemName("test_analyte_type");
        model.add(new TableDataRow(null, ""));
        for(DictionaryDO resultDO :  list){
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        } 
        typeId.setModel(model);
              
        model = new ArrayList<TableDataRow>();        
        list = DictionaryCache.getListByCategorySystemName("test_result_type");        
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : list) {
            model.add(new TableDataRow(resultDO.getId(), resultDO.getEntry()));
        }
        ((Dropdown<Integer>)resultTable.getColumnWidget(meta.getTestResult().getTypeId())).setModel(model);
    
        model = new ArrayList<TableDataRow>();
        list = DictionaryCache.getListByCategorySystemName("test_result_flags");        
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : list) {
            model.add(new TableDataRow(resultDO.getId(), resultDO.getEntry()));
        }
        ((Dropdown<Integer>)resultTable.getColumnWidget(meta.getTestResult().getFlagsId())).setModel(model);
    
        model = new ArrayList<TableDataRow>();
        list = DictionaryCache.getListByCategorySystemName("rounding_method");        
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : list) {
            model.add(new TableDataRow(resultDO.getId(), resultDO.getEntry()));
        }
        ((Dropdown<Integer>)resultTable.getColumnWidget(meta.getTestResult().getRoundingMethodId())).setModel(model);
        
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow("analyte",consts.get("analyte")));
        model.add(new TableDataRow("column", consts.get("column")));
        model.add(new TableDataRow("header",consts.get("header")));
        
        tableActions.setModel(model);
        tableActions.setSelection("analyte");
        
        setUnitsOfMeasure();
    }
    
    private void setUnitsOfMeasure() {        
        ArrayList<TableDataRow> model;
        List<DictionaryDO> dictList;
        TestTypeOfSampleDO stDO;
        String entry;
        Integer unitId;
        
        model = new ArrayList<TableDataRow>();
        
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
        ((Dropdown<Integer>)resultTable.getColumnWidget(meta.getTestResult().getUnitOfMeasureId())).setModel(model);
    }
    
    private boolean isAnalyteQuery() {
        TableDataRow row;
        int ac;        
        row = analyteTable.getRow(analyteTable.getSelectedRow());
        ac = analyteTable.getSelectedCol();
        if(((Boolean)row.data && ac > 1) || 
                        (!(Boolean)row.data && ac == 0))
            return true;
        
        return false;
    }
    
    private void addMatchesHandlerToAnalyteCells() {
        AutoComplete<Integer> ac;
        ArrayList<TableColumn> columns;
        
        columns = analyteTable.getColumns();
        for(int i = 0; i < columns.size(); i++) {
            ac = (AutoComplete<Integer>)columns.get(i).getColumnWidget();
            ac.addBeforeGetMatchesHandler(this);
            ac.addGetMatchesHandler(this);
        }
    }
   
    
   private void refreshAnalyteWidgets() {
        DataChangeEvent.fire(screen, isReportable);
        DataChangeEvent.fire(screen, typeId);
        DataChangeEvent.fire(screen, scriptlet);
    }       
      
   private void enableAnalyteWidgets(boolean enable) {
       isReportable.enable(enable);
       typeId.enable(enable);
       scriptlet.enable(enable);
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
        TestResultViewDO data;
        
        model = new ArrayList<TableDataRow>();
        
        if(manager == null)
            return model;
                   
        size = testResultManager.getResultGroupSize(group+1);
        for(int i = 0; i < size; i++) {
            data = testResultManager.getResultAt(group+1, i);
            row = new TableDataRow(6);
            row.cells.get(0).setValue(data.getUnitOfMeasureId());
            row.cells.get(1).setValue(data.getTypeId());
            if(data.getDictionary() == null) {
                row.cells.get(2).setValue(data.getValue());
            } else {
                row.cells.get(2).setValue(data.getDictionary());
                row.data = data.getValue();
            }
                
            row.cells.get(3).setValue(data.getFlagsId());
            row.cells.get(4).setValue(data.getSignificantDigits());
            row.cells.get(5).setValue(data.getRoundingMethodId());
            model.add(row);
        }                         
                           
        return model;
    }
    
    
    private void showErrorsForResultGroup(int group) {
        GridFieldErrorException exc;
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
            if (exc.getRowIndex() == group) {
                    resultTable.setCellException(exc.getColumnIndex(),
                                              exc.getFieldName(),
                                              exc);
            }
        }
    }
    
    private String validateAndSetNumericValue(String value,int row) {              
        boolean convert;
        Double doubleVal,darray[];
        String token,finalValue,strList[];
        int selTab;
        GridFieldErrorException exc;        
        
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
                exc = addToResultErrorList(selTab,row,meta.getTestResult().getValue(),"illegalNumericFormatException");
                resultTable.setCellException(row,2, exc);
            }    
        }  else {            
            exc = addToResultErrorList(selTab,row,meta.getTestResult().getValue(),"fieldRequiredException"); 
            resultTable.setCellException(row,2,exc);  
        }
        resultTable.setCell(row,2,value);
        return value;
    }
    
    private String validateAndSetTiterValue(String value,int row) {                
        boolean valid;
        int selTab;
        String token,strList[];
        GridFieldErrorException exc;
        
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
                exc = addToResultErrorList(selTab,row,meta.getTestResult().getValue(),"illegalTiterFormatException"); 
                resultTable.setCellException(row,2, exc);
            }
        }  else {
            exc = addToResultErrorList(selTab,row,meta.getTestResult().getValue(),"fieldRequiredException");
            resultTable.setCellException(row,2,exc);              
        }
        resultTable.setCell(row,2,value); 
        return value;
    }
    
    private String validateAndSetDictValue(String value, int row,TestResultViewDO result) {
        ArrayList<DictionaryDO> list;
        int selTab;        
        GridFieldErrorException exc;                 
        
        selTab = resultTabPanel.getTabBar().getSelectedTab();
        
        if(value.indexOf("*") > -1) {
            showDictionaryPopUp(DataBaseUtil.trim(value));
        } else {
            try {
                list = dictionaryService.callList("fetchByEntry", DataBaseUtil.trim(value));
                if (list == null || list.size() == 0) {             
                    exc = addToResultErrorList(selTab,row,meta.getTestResult().getValue(),"illegalDictEntryException"); 
                    resultTable.setCellException(row, 2, exc);
                } else if(list.size() > 1) {                
                    resultTable.setCell(row, 2, "");                                                
                    showDictionaryPopUp(DataBaseUtil.trim(value + "*"));                
                } else {        
                    return String.valueOf(list.get(0).getId());                
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                //Window.alert(ex.getMessage());
            }
        }
        
        return null;
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
    
    private GridFieldErrorException addToResultErrorList(int group,int row,String field,String error) {
        GridFieldErrorException exc;
        
        exc = new GridFieldErrorException(error,group, row,field,"resultTable");
        if(resultErrorList == null)
            resultErrorList = new ArrayList<GridFieldErrorException>();
        resultErrorList.add(exc);
        
        return exc;
        
    }   
    
    private void addAnalyte() {
        int ar;
        TableDataRow addrow;
        
        ar = analyteTable.getSelectedRow();
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
        
        ar = analyteTable.getSelectedRow();
        if(ar != -1) {
            row = analyteTable.getRow(ar);
            if(!(Boolean)row.data) {                        
                analyteTable.deleteRow(ar);
                analyteTable.refresh();
            }
        }
        
    }
    
    private void addColumn() {
        int ar,index;
        
        if(!canAddRemoveColumn) {
            Window.alert(consts.get("cantAddColumn"));
            return;
        }
        
        ar = analyteTable.getSelectedRow();
        
        if(anaSelCol != -1 && ar != -1) {
            index = displayManager.getDataRowIndex(ar);
            testAnalyteManager.addColumnAt(index, anaSelCol-1, null);
            displayManager.setDataGrid(testAnalyteManager.getAnalytes());                                      
            
            shiftDataInRowToTheRight(ar);
            shiftDataBelowToTheRight(ar);        

            analyteTable.refresh();
            anaSelCol = -1;
        }
    }
    
    private void removeColumn() {
        int ar,index;
        
        if(!canAddRemoveColumn) {
            Window.alert(consts.get("cantRemoveColumn"));
            return;
        }
        
        ar = analyteTable.getSelectedRow();
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
        
        ar = analyteTable.getSelectedRow();
        num = analyteTable.numRows(); 
        
        if(ar == -1 || ar == num-1) {
            analyteTable.addRow(createHeaderRow());
            analyteTable.scrollToSelection();
        } else {
            row = analyteTable.getRow(ar);
            if((Boolean)row.data) {    
                headerAddedInTheMiddle = true;
                analyteTable.addRow(ar,createHeaderRow());
                analyteTable.scrollToSelection();                                    
            } else { 
                row = analyteTable.getRow(ar+1);
                if((Boolean)row.data) {
                    headerAddedInTheMiddle = true;
                    analyteTable.addRow(ar+1,createHeaderRow());
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
        
        ar = analyteTable.getSelectedRow();                
        
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
            }
        }
    }    
    
    private void showDictionaryPopUp(String entry) {
        ScreenWindow modal;                                
        
        if(dictEntryPicker == null) {
            try {
                dictEntryPicker = new DictionaryEntryLookupScreen();
                dictEntryPicker.addActionHandler(new ActionHandler<DictionaryEntryLookupScreen.Action>(){
                    public void onAction(ActionEvent<DictionaryEntryLookupScreen.Action> event) {
                       int selTab,numTabs;
                       ArrayList<TableDataRow> model;
                       TestResultViewDO resDO;
                       TableDataRow row;
                       Integer dictId;   
                       
                       selTab = resultTabPanel.getTabBar().getSelectedTab();     
                       numTabs = resultTabPanel.getTabBar().getTabCount();
                       if(event.getAction() == DictionaryEntryLookupScreen.Action.OK) {
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
                                   resDO.setValue(String.valueOf((Integer)row.key));
                                   resDO.setDictionary((String)row.cells.get(0).getValue());
                                   resDO.setTypeId(dictId);                                           
                               }
                               DataChangeEvent.fire(screen, resultTable);
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
        if (entry != null) {
            dictEntryPicker.clearFields();
            dictEntryPicker.executeQuery(entry);
        }
    }
    
}
 