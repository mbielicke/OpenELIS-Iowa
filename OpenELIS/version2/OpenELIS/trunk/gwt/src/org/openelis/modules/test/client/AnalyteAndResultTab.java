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
import org.openelis.domain.IdNameDO;
import org.openelis.domain.TestAnalyteDO;
import org.openelis.domain.TestResultDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.gwt.common.GridFieldErrorException;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.BeforeGetMatchesEvent;
import org.openelis.gwt.event.BeforeGetMatchesHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDef;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.deprecated.ScreenWindow;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
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
import org.openelis.modules.dictionaryentrypicker.client.DictionaryEntryPickerScreen.Action;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;


public class AnalyteAndResultTab extends Screen implements GetMatchesHandler,BeforeGetMatchesHandler{
    private TestManager manager;
    private TestMetaMap TestMeta;
    
    private TestAnalyteManager testAnalyteManager;
    private TestResultManager testResultManager;
    private TestTypeOfSampleManager sampleTypeManager;
    
    private AnalyteAndResultTab source;
    private TestAnalyteDisplayManager displayManager;
    
    private DictionaryEntryPickerScreen dictEntryPicker; 
    
    private TableWidget analyteTable, resultTable;
    private Dropdown<Integer> typeId;       
    private ScrollableTabBar resultTabPanel;
    private CheckBox isReportable;
    private AutoComplete<Integer> scriptlet;  
    
    private AppButton removeColumnButton,addColumnButton;
    
    private ArrayList<GridFieldErrorException> resultErrorList;
            
    private boolean addAnalyteRow,dropdownsInited, loaded, headerAddedInTheMiddle;
    
    private int anaSelCol;           
    
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
        
        headerAddedInTheMiddle = false;
        
        resultErrorList = null;
        
        analyteTable = (TableWidget)def.getWidget("analyteTable");
        addScreenHandler(analyteTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                analyteTable.load(getAnalyteTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analyteTable.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                analyteTable.setQueryMode(event.getState() == State.QUERY);
            }
        });
       
        analyteTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler () {

            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                int row, col;
                Integer key;
                TableDataRow set,vset;
                
                row = event.getRow();
                col = event.getCell();                                              
                anaSelCol = col;
                enableAnalyteWidgets(true);
                try {                    
                    set = analyteTable.getRow(row); 
                    if(displayManager.isHeaderRow(row)) {
                        if(col < 2 || col > (displayManager.columnCount(row)+1)) {
                            event.cancel();                            
                            enableAnalyteWidgets(false);
                            enableAnalyteColumnButtons(false);
                            anaSelCol = -1;                            
                        } else if(col != 2) {  
                            vset = (TableDataRow)set.cells.get(col-1).getValue();                            
                            if(vset == null || vset.key == null) { 
                                event.cancel();
                                enableAnalyteWidgets(false);
                                anaSelCol = -1;                                
                            } 
                            
                            if(col <= displayManager.columnCount(row)) {
                                enableAnalyteColumnButtons(true);
                            } else {
                                enableAnalyteColumnButtons(false);
                            }
                        } else if(col == 2) {
                            enableAnalyteColumnButtons(true);
                        }                           
                    } else if(col > displayManager.columnCount(row)){
                        event.cancel();
                        enableAnalyteWidgets(false);
                        enableAnalyteColumnButtons(false);
                    } else if(col > 0) {
                        vset = (TableDataRow)set.cells.get(col).getValue();           
                        if(vset != null && vset.key != null){
                           key = (Integer)vset.key;
                           if(key-1 < resultTabPanel.getTabBar().getTabCount()){
                               resultTabPanel.selectTab(key-1);
                           }
                        }
                        enableAnalyteWidgets(false);
                        enableAnalyteColumnButtons(false);
                        anaSelCol = -1; 
                    }                                    
                } catch(Exception ex) {
                    ex.printStackTrace();
                }                                
                
                refreshAnalyteWidgets();       
            }
            
        });
        
        analyteTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r,col,numCol,dindex;
                TableDataRow row,value;
                TestAnalyteDO anaDO;                
                Integer key;
                

                r = event.getRow();
                col = event.getCell();
                row = analyteTable.getRow(r);
                value = (TableDataRow)row.cells.get(col).value;
                key = (Integer)value.key;                
                
                try {                                                                                              
                    if("SubHeader".equals(row.style)) {                        
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
                            // we need to set the key as the analyte's id in the
                            // DO at the appropriate location in the grid 
                            //
                            anaDO = displayManager.getTestAnalyteAt(r, col);
                            anaDO.setAnalyteId(key);
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
                                                              
                    if(!"SubHeader".equals(row.style) && index == 0) {             
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
                TableDataRow prow,row,nrow;
                int index,dindex;
                
                try {
                    row = event.getRow();
                    index = event.getIndex();                                        
                    
                    if(!"SubHeader".equals(row.style)) {                                                                          
                        prow = analyteTable.getRow(index-1);                         
                        dindex = displayManager.getDataRowIndex(index-1);
                        
                        //
                        // dindex can be returned as -1 if index-1 excceds the size of 
                        // displayManager's index list because the new row won't have
                        // been added to the grid maintained by TestAnalyteManager;
                        // so if the number of rows in the table is more than one
                        // (index > 0), we try to find dindex for the last row in
                        // displayManager's index list 
                        //                                                
                        if((dindex == -1 && index > 0) || headerAddedInTheMiddle) {
                            dindex = displayManager.getDataRowIndex(index-2);
                            headerAddedInTheMiddle = false;
                        }
                        
                        if("SubHeader".equals(prow.style)) { 
                            //
                            // if there were rows after the header row in the table
                            // before the current row was added then we need to 
                            // find out whether the next row after the current row is
                            // an analyte row and if it is then the current
                            // has not been added to a new row group but an existing one
                            //                                                                                    
                            if(index+1 < analyteTable.numRows()) {
                                nrow = analyteTable.getRow(index+1);
                                if(!"SubHeader".equals(nrow.style)) {
                                    testAnalyteManager.addRowAt(dindex,false,false);
                                    return;
                                }
                            }
                            testAnalyteManager.addRowAt(dindex+1,true,false);
                        }
                        else {
                            testAnalyteManager.addRowAt(dindex+1,false,true);
                        }
                        
                        displayManager.setDataGrid(testAnalyteManager.getAnalytes());
                    } else if(addAnalyteRow){
                            if(index+1 >= analyteTable.numRows())
                                analyteTable.addRow();
                            else
                                analyteTable.addRow(index+1);
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
                
                index = event.getIndex();
                row = event.getRow();

                if(!"SubHeader".equals(row.style)) {
                    dindex = displayManager.getDataRowIndex(index);
                    testAnalyteManager.removeRowAt(dindex);
                    displayManager.setDataGrid(testAnalyteManager.getAnalytes());                                          
                }
            }
        });
        
        addMatchesHandlerToAnalyteCells();

        typeId = (Dropdown<Integer>)def.getWidget(TestMeta.getTestAnalyte().getTypeId());
        addScreenHandler(typeId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {                      
                TestAnalyteDO anaDO;               
                               
                
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
                TestAnalyteDO anaDO;
                TableDataRow row;
                int i,ar;
                
                ar = analyteTable.activeRow;
                if(ar != -1  && anaSelCol != -1) {
                    row =  analyteTable.getRow(ar);
                    if("SubHeader".equals(row.style)) {
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
                typeId.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                
                if(EnumSet.of(State.DEFAULT,State.DISPLAY).contains(event.getState()) && typeId.getData() != null) 
                    typeId.setSelection(null);
                
                typeId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        isReportable = (CheckBox)def.getWidget(TestMeta.getTestAnalyte().getIsReportable());
        addScreenHandler(isReportable, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                TestAnalyteDO anaDO;                   
                
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
                TestAnalyteDO anaDO;
                TableDataRow row;
                int i,ar;
                
                ar = analyteTable.activeRow;
                if(ar != -1  && anaSelCol != -1) {
                    row =  analyteTable.getRow(ar);
                    if("SubHeader".equals(row.style)) {
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
                isReportable.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                
                if(EnumSet.of(State.DEFAULT,State.DISPLAY).contains(event.getState())) 
                    isReportable.setValue("N");
                
                isReportable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        scriptlet = (AutoComplete<Integer>)def.getWidget(TestMeta.getTestAnalyte().getScriptletId());
        addScreenHandler(scriptlet, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                TestAnalyteDO anaDO;
                
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
                TestAnalyteDO anaDO;
                TableDataRow row;
                int i,ar;
                
                ar = analyteTable.activeRow;
                if(ar != -1  && anaSelCol != -1) {
                    row =  analyteTable.getRow(ar);
                    if("SubHeader".equals(row.style)) {
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
                scriptlet.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                
                if(EnumSet.of(State.DEFAULT,State.DISPLAY).contains(event.getState())) 
                    scriptlet.setSelection(null,"");
                
                scriptlet.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        // Screens now must implement AutoCompleteCallInt and set themselves as the calling interface       
        scriptlet.addGetMatchesHandler(new GetMatchesHandler() {                        
            public void onGetMatches(GetMatchesEvent event) {
                TestAutoRPC trpc;
                ArrayList<TableDataRow> model;
                TableDataRow row;
                trpc = new TestAutoRPC(); 
                trpc.match = event.getMatch();                
                try {
                    trpc = service.call("getScriptletMatches",trpc);
                    model = new ArrayList<TableDataRow>();
                    for(IdNameDO autoDO : trpc.idNameList) {
                        row = new TableDataRow(autoDO.getId(),autoDO.getName());
                        model.add(row);
                    }
                    scriptlet.showAutoMatches(model);
                }catch(Exception e) {
                    Window.alert(e.getMessage());                     
                }
            }
            
        });

        final AppButton addAnalyteButton = (AppButton)def.getWidget("addAnalyteButton");
        addScreenHandler(addAnalyteButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int ar,num;
                
                ar = analyteTable.activeRow;
                num = analyteTable.numRows(); 
                if(ar == -1 || ar == num-1) {
                    analyteTable.addRow();
                    analyteTable.selectRow(num);
                    analyteTable.scrollToSelection();
                    //analyteTable.startEditing(num, 0);
                } else {
                    analyteTable.addRow(ar+1);
                    analyteTable.selectRow(ar+1);
                    analyteTable.scrollToSelection();
                    //analyteTable.startEditing(ar+1, 0);
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if(event.getState() == State.ADD || event.getState() == State.UPDATE)
                    addAnalyteButton.enable(true);
                else
                    addAnalyteButton.enable(false);
            }
        });

        final AppButton removeAnalyteButton = (AppButton)def.getWidget("removeAnalyteButton");
        addScreenHandler(removeAnalyteButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int ar;
                TableDataRow row;
                
                ar = analyteTable.activeRow;
                if(ar != -1) {
                    row = analyteTable.getRow(ar);
                    if(!"SubHeader".equals(row.style)) {                        
                        analyteTable.deleteRow(ar);
                        analyteTable.refresh();
                    }
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if(event.getState() == State.ADD || event.getState() == State.UPDATE)
                    removeAnalyteButton.enable(true);
                else
                    removeAnalyteButton.enable(false);
            }
        });

        final AppButton addHeaderButton = (AppButton)def.getWidget("addHeaderButton");
        addScreenHandler(addHeaderButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {                                 
                int ar,num;
                TableDataRow row;
                
                ar = analyteTable.activeRow;
                num = analyteTable.numRows(); 
                if(ar == -1 || ar == num-1) {
                    analyteTable.addRow(createHeaderRow());
                    analyteTable.selectRow(num);
                    analyteTable.scrollToSelection();
                    //analyteTable.startEditing(num, 0);
                } else {
                    row = analyteTable.getRow(ar+1);
                    if("SubHeader".equals(row.style)) {
                        headerAddedInTheMiddle = true;
                        analyteTable.addRow(ar+1,createHeaderRow());
                        analyteTable.selectRow(ar+1);
                        analyteTable.scrollToSelection();                    
                        //analyteTable.startEditing(ar+1, 0);
                    }
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if(event.getState() == State.ADD || event.getState() == State.UPDATE)
                    addHeaderButton.enable(true);
                else
                    addHeaderButton.enable(false);
            }
        });

        final AppButton removeHeaderButton = (AppButton)def.getWidget("removeHeaderButton");
        addScreenHandler(removeHeaderButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int ar,num;
                TableDataRow row;
                
                ar = analyteTable.activeRow;
                num = analyteTable.numRows();
                
                if(ar != -1) {
                    row = analyteTable.getRow(ar);                    
                    if("SubHeader".equals(row.style)) {  
                        analyteTable.deleteRow(ar);
                        while(ar < num) {                        
                            row = analyteTable.getRow(ar);
                            if("SubHeader".equals(row.style)) 
                                break;
                            
                            analyteTable.deleteRow(ar);
                        }
                    }
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if(event.getState() == State.ADD || event.getState() == State.UPDATE)
                    removeHeaderButton.enable(true);
                else
                    removeHeaderButton.enable(false);
            }
        });

        addColumnButton = (AppButton)def.getWidget("addColumnButton");
        addScreenHandler(addColumnButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
               int ar,index;
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
            public void onStateChange(StateChangeEvent<State> event) {                
                addColumnButton.enable(false);
            }
        });

        removeColumnButton = (AppButton)def.getWidget("removeColumnButton");
        addScreenHandler(removeColumnButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int ar,index;
                
                ar = analyteTable.activeRow;
                if(anaSelCol != -1 && ar != -1) {                                                                                              
                    shiftDataInRowToTheLeft(ar);
                    //shiftDataAboveToTheLeft(ar);
                    shiftDataBelowToTheLeft(ar);        

                    analyteTable.refresh();                    
                    
                    index = displayManager.getDataRowIndex(ar);
                    testAnalyteManager.removeColumnAt(index, anaSelCol-1);
                    displayManager.setDataGrid(testAnalyteManager.getAnalytes());
                    
                    anaSelCol = -1;
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {                
                removeColumnButton.enable(false);
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
                
                selTab = resultTabPanel.getTabBar().getSelectedTab();                
                resultTable.load(getResultTableModel(selTab));               
            }

            public void onStateChange(StateChangeEvent<State> event) {
                resultTable.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                resultTable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        resultTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int row,col,group;                
                TestResultDO result ;
                Object val;

                row = event.getRow();
                col = event.getCell();
                val = resultTable.getRow(row).cells.get(col).value;
                group = resultTabPanel.getTabBar().getSelectedTab();
                result = testResultManager.getResultAt(group+1, row);
                
                switch(col) {
                    case 0:   
                        result.setUnitOfMeasureId((Integer)val);
                        break;
                    case 1:   
                        result.setTypeId((Integer)val);
                        break;
                    case 2:             
                        result.setValue((String)val);
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
                testResultManager.addResult(selTab+1);
            }
        });

        resultTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                int row,selTab;
             
                selTab = resultTabPanel.getTabBar().getSelectedTab();                
                row = event.getIndex();        
                
                testResultManager.removeResultAt(selTab+1, row);
            }
        });

        final AppButton addResultTabButton = (AppButton)def.getWidget("addResultTabButton");
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

        final AppButton removeTestResultButton = (AppButton)def.getWidget("removeTestResultButton");
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

        final AppButton addTestResultButton = (AppButton)def.getWidget("addTestResultButton");
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

        final AppButton dictionaryLookUpButton = (AppButton)def.getWidget("dictionaryLookUpButton");
        addScreenHandler(dictionaryLookUpButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ScreenWindow modal;                                
                
                if(dictEntryPicker == null) {
                    try {
                        dictEntryPicker = new DictionaryEntryPickerScreen();
                        dictEntryPicker.addActionHandler(new ActionHandler<DictionaryEntryPickerScreen.Action>(){

                            public void onAction(ActionEvent<Action> event) {
                               int selTab;
                               ArrayList<TableDataRow> model;
                               TestResultDO resDO;
                               TableDataRow row;
                               Integer dictId;                               
                               if(event.getAction() == DictionaryEntryPickerScreen.Action.COMMIT) {
                                   model = (ArrayList<TableDataRow>)event.getData();
                                   if(model != null) {
                                       for(int i = 0; i < model.size(); i++) {
                                           row = model.get(i);        
                                           selTab = resultTabPanel.getTabBar().getSelectedTab();
                                           testResultManager.addResultAt(selTab+1,resultTable.numRows());
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
                modal = new ScreenWindow(null,"Dictionary LookUp","dictionaryEntryPickerScreen","",true,false);
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
        TestAutoRPC trpc;
        ArrayList<TableDataRow> model;
        TableDataRow row;
        int rg;
        String match;        
        
        trpc = new TestAutoRPC(); 
        trpc.match = event.getMatch();
        model = null;
        
        try {
            if(isAnalyteQuery()) {
                trpc = service.call("getAnalyteMatches",trpc);
                model = new ArrayList<TableDataRow>();
                for(IdNameDO autoDO : trpc.idNameList) {
                    row = new TableDataRow(autoDO.getId(),autoDO.getName());
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
                        model.add(new TableDataRow(-1,""));
                    }
                }catch(NumberFormatException e){
                    model.add(new TableDataRow(-1,""));
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
        int dindex,trindex,col;
        String field;
        TableDataRow trow;
        
        dindex = error.getRowIndex();
        col = error.getColumnIndex();
        field = error.getFieldName();
        
        //
        // find out which table row does the grid row at dindex represents
        //
        for(trindex = 0; trindex < displayManager.rowCount(); trindex++) {
           if(dindex == displayManager.getIndexAt(trindex))                
               break;                                         
        }
        
        if(TestMeta.getTestAnalyte().getResultGroup().equals(field)) {
            analyteTable.setCellError(trindex, col+1, consts.get(error.getMessage()));
        } else if(col == 0){
            analyteTable.setCellError(trindex, col, consts.get(error.getMessage()));
        } else {                      
            for(trindex = dindex-1; trindex > -1; trindex--) {            
                trow = analyteTable.getRow(trindex);                
                if("SubHeader".equals(trow.style)) {              
                    analyteTable.setCellError(trindex, col+1, consts.get(error.getMessage()));
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
    
    private ArrayList<TableDataRow> getAnalyteTableModel() {
       int m,c,len;
       ArrayList<TableDataRow> model;
       TableDataRow hrow,row;
       TestAnalyteDO anaDO;
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
        if(("SubHeader".equals(row.style) && ac > 1) || 
                        (!("SubHeader".equals(row.style)) && ac == 0))
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
   
   private void enableAnalyteColumnButtons(boolean enable) {
       addColumnButton.enable(enable);
       removeColumnButton.enable(enable);       
   }
    
    private void shiftDataAboveToTheRight(int row) {
        TableDataRow trow;
        
        for(int i = row-1; i > -1; i--) {            
            trow = analyteTable.getRow(i);
            shiftDataInRowToTheRight(i);            
            if("SubHeader".equals(trow.style))              
                break;
        }
    }
    
    private void shiftDataBelowToTheRight(int row) {
        TableDataRow trow;
        
        for(int i = row+1; i < analyteTable.numRows(); i++) {
            trow = analyteTable.getRow(i);
            if(!"SubHeader".equals(trow.style))
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
        
        try{
            for (int i = finCol; i > anaSelCol; i--) {               
                lrow = (TableDataRow)trow.cells.get(i-1).getValue();                        
                lkey = (Integer)lrow.key;                        
                lname = (String)lrow.cells.get(0).getValue();             
                trow.cells.get(i).setValue(new TableDataRow(lkey,lname));
                trow.cells.get(i-1).setValue(new TableDataRow(-1,""));                                     
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
        TableDataRow rrow;
        
        if(anaSelCol < 2 || row < 0) 
            return;
        
        trow = analyteTable.getRow(row);        
        finalIndex = displayManager.columnCount(row)+1;
        
        if(finalIndex == 2)
            return;
        
        if(anaSelCol == 9) {
            trow.cells.get(anaSelCol).setValue(new TableDataRow(-1,""));            
        } else {                
            for (int i = anaSelCol; i < finalIndex; i++) {
                if(i == finalIndex-1) {
                    trow.cells.get(i).setValue(new TableDataRow(-1,""));
                } else {
                    rrow = (TableDataRow)trow.cells.get(i + 1).getValue();
                    rkey = (Integer)rrow.key;
                    rname = (String)rrow.cells.get(0).getValue();

                    if (rkey != null)
                        trow.cells.get(i)
                                  .setValue(new TableDataRow(rkey, rname));
                    else
                        trow.cells.get(i).setValue(new TableDataRow(-1, ""));
                    
                }
            }
        }        
    }
    
    private void shiftDataAboveToTheLeft(int row) {
        TableDataRow trow;
        
        for(int i = row-1; i > -1; i--) {            
            trow = analyteTable.getRow(i);
            shiftDataInRowToTheLeft(i);            
            if("SubHeader".equals(trow.style))              
                break;
        }
    }
    
    private void shiftDataBelowToTheLeft(int row) {
        TableDataRow trow;
        
        for(int i = row+1; i < analyteTable.numRows(); i++) {
            trow = analyteTable.getRow(i);
            if(!"SubHeader".equals(trow.style))
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
        
        if (resultErrorList == null)
            return;
        
        for(i = 0; i < resultTable.numRows(); i++) {
           row = resultTable.getRow(i);
           for(j = 0; j < row.cells.size(); j++) {
               resultTable.clearCellError(i, j);
           }
        }
                
        for (i = 0; i < resultErrorList.size(); i++) {
            exc = resultErrorList.get(i);
            message = exc.getMessage();
            if (exc.getRowIndex() == group) {
                if(message.indexOf("illegalUnitOfMeasureException") != -1) {
                    unit = message.split(":");
                    resultTable.setCellError(exc.getColumnIndex(),
                                             exc.getFieldName(),
                                             consts.get("illegalUnitOfMeasureException")+unit[1]);
                    
                    
                } else {      
                    resultTable.setCellError(exc.getColumnIndex(),
                                              exc.getFieldName(),
                                              consts.get(exc.getMessage()));
                }
            }
        }
    }
    
}
 