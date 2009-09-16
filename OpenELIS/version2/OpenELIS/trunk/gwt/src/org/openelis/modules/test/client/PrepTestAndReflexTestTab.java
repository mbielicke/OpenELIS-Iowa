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
import org.openelis.domain.TestAnalyteDO;
import org.openelis.domain.TestMethodAutoDO;
import org.openelis.domain.TestPrepDO;
import org.openelis.domain.TestReflexDO;
import org.openelis.domain.TestResultDO;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.Screen.State;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.table.TableColumn;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.TestAnalyteManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestPrepManager;
import org.openelis.manager.TestReflexManager;
import org.openelis.manager.TestResultManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;

public class PrepTestAndReflexTestTab extends Screen implements GetMatchesHandler{
    
    private TestManager manager;
    private TestAnalyteManager testAnalyteManager;
    private TestResultManager testResultManager;
    
    private boolean loaded,dropdownsInited;
    
    private TableWidget testPrepTable,testReflexTable;
    
    public PrepTestAndReflexTestTab(ScreenDefInt def,ScreenService service) {
        setDef(def);
        
        this.service = service;
        initialize();          
    }

    private void initialize() {
        AutoComplete<Integer> ac;
        ArrayList<TableColumn> columns;  
        final AutoComplete<Integer> analyteAuto, resultAuto;
        
        testPrepTable = (TableWidget)def.getWidget("testPrepTable");
        addScreenHandler(testPrepTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                testPrepTable.load(getPrepTestModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                testPrepTable.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                testPrepTable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        testPrepTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                TestPrepDO prepDO;

                r = event.getRow();
                c = event.getCell();
                
                val = testPrepTable.getRow(r).cells.get(c).value; 
                
                try{
                    prepDO = manager.getPrepTests().getPrepAt(r);
                }catch(Exception e){
                    Window.alert(e.getMessage());
                    return;
                }
                
                switch(c) {
                    case 0:
                        prepDO.setPrepTestId((Integer)((TableDataRow)val).key);
                        break;
                    case 1:
                        prepDO.setIsOptional((String)val);
                        break;
                }
                
            }
        });
        testPrepTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                try{ 
                    manager.getPrepTests().addPrep(new TestPrepDO());
                }catch(Exception e){
                    Window.alert(e.getMessage());
                }
            }
        });

        testPrepTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try{
                    manager.getPrepTests().removePrepAt(event.getIndex());
                }catch(Exception e){
                    Window.alert(e.getMessage());
                }
            }
        });
        
        columns = testPrepTable.columns;        
        ac = (AutoComplete<Integer>)columns.get(0).getColumnWidget();            
        ac.addGetMatchesHandler(this);
       
        final AppButton addPrepTestButton = (AppButton)def.getWidget("addPrepTestButton");
        addScreenHandler(addPrepTestButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                testPrepTable.addRow();
                testPrepTable.selectRow(testPrepTable.numRows()-1);
                testPrepTable.scrollToSelection();
                testPrepTable.startEditing(testPrepTable.numRows()-1, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if(event.getState() == State.ADD || event.getState() == State.UPDATE)
                    addPrepTestButton.enable(true);
                else
                    addPrepTestButton.enable(false);
            }
        });

        final AppButton removePrepTestButton = (AppButton)def.getWidget("removePrepTestButton");
        addScreenHandler(removePrepTestButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int selectedRow = testPrepTable.getSelectedIndex();
                if (selectedRow > -1 && testPrepTable.numRows() > 0) {
                    testPrepTable.deleteRow(selectedRow);
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if(event.getState() == State.ADD || event.getState() == State.UPDATE)
                    removePrepTestButton.enable(true);
                else
                    removePrepTestButton.enable(false);
            }
        });

        testReflexTable = (TableWidget)def.getWidget("testReflexTable");
        addScreenHandler(testReflexTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                testReflexTable.load(getReflexTestModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                testReflexTable.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                testReflexTable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        testReflexTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int row, col;
                Object val;
                TestReflexDO refDO;

                row = event.getRow();
                col = event.getCell();
                val = testReflexTable.getRow(row).cells.get(col).value;
                try{
                    refDO = manager.getReflexTests().getReflexAt(row);
                }catch(Exception e){
                    Window.alert(e.getMessage());
                    return;
                }
                
                switch(col) {
                    case 0:
                        refDO.setAddTestId((Integer)((TableDataRow)val).key);
                        break;
                    case 1:
                        refDO.setTestAnalyteId((Integer)((TableDataRow)val).key);
                        break;
                    case 2:
                        refDO.setTestResultId((Integer)((TableDataRow)val).key);
                        break;
                    case 3:
                        refDO.setFlagsId((Integer)val);
                        break;
                }
            }
        });

        testReflexTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                try{ 
                    manager.getReflexTests().addReflex(new TestReflexDO());
                }catch(Exception e){
                    Window.alert(e.getMessage());
                }
            }
        });

        testReflexTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                
            }
        });

        columns = testReflexTable.columns;        
        ac = (AutoComplete<Integer>)columns.get(0).getColumnWidget();            
        ac.addGetMatchesHandler(this);
        
        analyteAuto = (AutoComplete<Integer>)columns.get(1).getColumnWidget();
        analyteAuto.addGetMatchesHandler(new GetMatchesHandler(){

            public void onGetMatches(GetMatchesEvent event) {                
                TestAnalyteDO ana;
                ArrayList<TableDataRow> model;
                TableDataRow row;                
                
                model = new ArrayList<TableDataRow>();
                for(int i = 0; i < testAnalyteManager.rowCount(); i++) {
                    ana = testAnalyteManager.getAnalyteAt(i, 0);                    
                    if(ana.getAnalyteName().startsWith(event.getMatch())){
                        row = new TableDataRow(1);
                        row.key = ana.getId();
                        row.cells.get(0).setValue(ana.getAnalyteName());
                        model.add(row);
                    }
                }
                
                if(model.size() == 0)
                    model.add(new TableDataRow(null,""));
            
                analyteAuto.showAutoMatches(model);
            }
            
        });
        
        resultAuto = (AutoComplete<Integer>)columns.get(2).getColumnWidget();
        resultAuto.addGetMatchesHandler(new GetMatchesHandler(){

            public void onGetMatches(GetMatchesEvent event) {                
                TestResultDO res;
                ArrayList<TableDataRow> model;
                TableDataRow row,trow,arow;
                Integer rg;
                int ar,size;
                
                ar = testReflexTable.activeRow;                
                trow =  testReflexTable.getRow(ar);               
                arow = (TableDataRow)trow.cells.get(1).getValue();
                
                model = new ArrayList<TableDataRow>();
                
                if(arow.key == null) {                    
                    model.add(new TableDataRow(null,""));
                    return;
                }
                
                rg = getResultGroupForTestAnalyte((Integer)arow.key);
                
                if(rg == null) {
                    model.add(new TableDataRow(null,""));
                    return;
                }
                
                size = testResultManager.getResultGroupSize(rg);
                
                for(int i = 0; i < size; i++) {
                    res = testResultManager.getResultAt(rg, i);
                    row = new TableDataRow(1);
                    row.key = res.getId();
                    row.cells.get(0).setValue(res.getValue());
                    model.add(row);
                }
                
                if(model.size() == 0)
                    model.add(new TableDataRow(null,""));
            
                resultAuto.showAutoMatches(model);            
            }
            
        });
        
        final AppButton addReflexTestButton = (AppButton)def.getWidget("addReflexTestButton");
        addScreenHandler(addReflexTestButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                testReflexTable.addRow();
                testReflexTable.selectRow(testReflexTable.numRows()-1);
                testReflexTable.scrollToSelection();
                testReflexTable.startEditing(testReflexTable.numRows()-1, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {               
                if(event.getState() == State.ADD || event.getState() == State.UPDATE)
                    addReflexTestButton.enable(true);
                else
                    addReflexTestButton.enable(false);
            }
        });

        final AppButton removeReflexTestButton = (AppButton)def.getWidget("removeReflexTestButton");
        addScreenHandler(removeReflexTestButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                
            }

            public void onStateChange(StateChangeEvent<State> event) {                
                if(event.getState() == State.ADD || event.getState() == State.UPDATE)
                    removeReflexTestButton.enable(true);
                else
                    removeReflexTestButton.enable(false);
            }
        });           
            
    }

    public void setManager(TestManager manager) {
        this.manager = manager;
        loaded = false;
        
        if(!dropdownsInited) {
            setTestReflexFlags();
            dropdownsInited = true;
        }
    }
    
    public void draw(){
        if(!loaded) { 
            try {
                if(state == State.UPDATE || state == State.ADD) { 
                    testAnalyteManager = manager.getTestAnalytes();
                    testResultManager = manager.getTestResults();
                }
                DataChangeEvent.fire(this);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }       
        loaded = true;       
    }

    public void onGetMatches(GetMatchesEvent event) {
        AutocompleteRPC rpc;
        ArrayList<TableDataRow> model;
        TestMethodAutoDO autoDO;
        TableDataRow row;
                       
        rpc = new AutocompleteRPC();
        rpc.match = event.getMatch();
        try {
            rpc = service.call("getTestMethodMatches",rpc);
            model = new ArrayList<TableDataRow>();
            
            for (int i=0; i<rpc.model.size(); i++){
                autoDO = (TestMethodAutoDO)rpc.model.get(i);
                
                row = new TableDataRow(1);
                row.key = autoDO.getTestId();
                row.cells.get(0).value = autoDO.getTestName()+","+autoDO.getMethodName();
                //row.cells.get(1).value = autoDO.getTestDescription();
                //row.cells.get(2).value = autoDO.getMethodName();
                //row.cells.get(3).value = autoDO.getMethodDescription();
                model.add(row);
            }
            ((AutoComplete)event.getSource()).showAutoMatches(model);
       }catch(Exception e) {
           Window.alert(e.getMessage());                     
       }
        
    }
    

    private ArrayList<TableDataRow> getPrepTestModel() {
        ArrayList<TableDataRow> model;
        TestPrepManager tpm;
        TestPrepDO prepTest;
        TableDataRow row;
        
        model = new ArrayList<TableDataRow>();
                
        if(manager == null)
            return model;
        
        try{
            tpm = manager.getPrepTests();
            for(int i = 0; i < tpm.count(); i++) {
                prepTest = tpm.getPrepAt(i);
                row = new TableDataRow(2);
                row.key = prepTest.getId();               
                
                row.cells.get(0).setValue(new TableDataRow(prepTest.getPrepTestId(),
                                                           prepTest.getPrepTestName()+","+prepTest.getMethodName()));
                row.cells.get(1).setValue(prepTest.getIsOptional());
                model.add(row);
            }
        } catch (Exception e) {    
            e.printStackTrace();
            return null;
        }
        
        return model;
    }
    
    private ArrayList<TableDataRow> getReflexTestModel() {
        ArrayList<TableDataRow> model;
        TestReflexManager trm;
        TestReflexDO reflexTest;
        TableDataRow row;
        
        model = new ArrayList<TableDataRow>();
        
        if(manager == null)
            return model;
        
        try{
            trm = manager.getReflexTests();
            for(int i = 0; i < trm.count(); i++) {
                reflexTest = trm.getReflexAt(i);
                row = new TableDataRow(4);
                row.key = reflexTest.getId();               
                
                row.cells.get(0).setValue(new TableDataRow(reflexTest.getAddTestId(),
                                                           reflexTest.getAddTestName()+","+reflexTest.getMethodName()));
                row.cells.get(1).setValue(new TableDataRow(reflexTest.getTestAnalyteId(),
                                                           reflexTest.getAnalyteName()));                
                row.cells.get(2).setValue(new TableDataRow(reflexTest.getTestResultId(),
                                                           reflexTest.getResultValue()));
                row.cells.get(3).setValue(reflexTest.getFlagsId());
                
                model.add(row);
            }
            
        } catch (Exception e) {    
            e.printStackTrace();
            return null;
        }
        
        return model;
    }

    private void setTestReflexFlags() {                
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        List<DictionaryDO> list = DictionaryCache.getListByCategorySystemName("test_reflex_flags");
        model.add(new TableDataRow(null, ""));
        for(DictionaryDO resultDO :  list){
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        } 
        ((Dropdown)testReflexTable.columns.get(3).getColumnWidget()).setModel(model);
    }
    
    private Integer getResultGroupForTestAnalyte(Integer taId) {
        TestAnalyteDO anaDO;
        for(int i = 0; i < testAnalyteManager.rowCount(); i++) {
            anaDO = testAnalyteManager.getAnalyteAt(i, 0);
            if(taId.equals(anaDO.getId()))
                return anaDO.getResultGroup();
        } 
        
        return null;
    }


}
