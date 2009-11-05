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
import org.openelis.domain.TestWorksheetAnalyteViewDO;
import org.openelis.domain.TestWorksheetDO;
import org.openelis.domain.TestWorksheetItemDO;
import org.openelis.gwt.common.LocalizedException;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
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
import org.openelis.manager.TestWorksheetManager;
import org.openelis.metamap.TestMetaMap;
import org.openelis.modules.test.client.AnalyteAndResultTab.Action;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;

public class WorksheetLayoutTab extends Screen implements ActionHandler<AnalyteAndResultTab.Action>{

    private TestManager             manager;
    private TestAnalyteManager      analyteManager;
    private TestWorksheetManager    worksheetManager;
    
    private WorksheetLayoutTab      screen;
    private TestAnalyteLookupScreen testAnalytePicker; 
    
    private TestMetaMap             meta = new TestMetaMap();   
    
    private boolean                 loaded;
    
    private Dropdown<Integer>       formatId;    
    private TableWidget             worksheetAnalyteTable, worksheetTable;
    private AppButton               addWSItemButton,removeWSItemButton,
                                    addWSAnalyteButton,removeWSAnalyteButton;
    private TextBox<Integer>        batchCapacity,totalCapacity;
    private AutoComplete            scriptlet,qcname; 
    private ScreenService           scriptletService, qcService;
    
    public WorksheetLayoutTab(ScreenDefInt def,ScreenService service,
                              ScreenService scriptletService,ScreenService qcService) {
        setDef(def);
                       
        this.service = service;
        this.scriptletService = scriptletService;
        this.qcService = qcService;
        
        initialize();        
        
        initializeDropdowns();
    }

    private void initialize() {        
        screen = this;
        
        formatId = (Dropdown)def.getWidget(meta.getTestWorksheet().getFormatId());
        addScreenHandler(formatId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                formatId.setSelection(worksheetManager.getWorksheet().getFormatId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                worksheetManager.getWorksheet().setFormatId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                formatId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE,State.DELETE).contains(event.getState()));
                formatId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        batchCapacity = (TextBox)def.getWidget(meta.getTestWorksheet().getBatchCapacity());
        addScreenHandler(batchCapacity, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                batchCapacity.setValue(worksheetManager.getWorksheet().getBatchCapacity());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                worksheetManager.getWorksheet().setBatchCapacity(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                batchCapacity.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE,State.DELETE).contains(event.getState()));
                batchCapacity.setQueryMode(event.getState() == State.QUERY);
            }
        });

        totalCapacity = (TextBox)def.getWidget(meta.getTestWorksheet().getTotalCapacity());
        addScreenHandler(totalCapacity, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                totalCapacity.setValue(worksheetManager.getWorksheet().getTotalCapacity());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                worksheetManager.getWorksheet().setTotalCapacity(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                totalCapacity.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE,State.DELETE).contains(event.getState()));
                totalCapacity.setQueryMode(event.getState() == State.QUERY);
            }
        });

        scriptlet = (AutoComplete)def.getWidget(meta.getTestWorksheet().getScriptlet().getName());
        addScreenHandler(scriptlet, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                scriptlet.setSelection(worksheetManager.getWorksheet().getScriptletId(), worksheetManager.getWorksheet().getScriptletName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                worksheetManager.getWorksheet().setScriptletId(event.getValue());
                worksheetManager.getWorksheet().setScriptletName(scriptlet.getTextBoxDisplay());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                scriptlet.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE,State.DELETE).contains(event.getState()));
                scriptlet.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        scriptlet.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<TableDataRow> model;
                ArrayList<IdNameVO> scripts;               

                window.setBusy();                
                try {
                    scripts = scriptletService.callList("fetchByName",event.getMatch()+"%");
                    model = new ArrayList<TableDataRow>();
                    for(IdNameVO script : scripts) {
                        model.add(new TableDataRow(script.getId(),script.getName()));
                    }
                    scriptlet.showAutoMatches(model);
                }catch(Exception e) {
                    Window.alert(e.getMessage());                     
                }           
                window.clearStatus();
            }
            
        });

        worksheetTable = (TableWidget)def.getWidget("worksheetTable");
        addScreenHandler(worksheetTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                if(state != State.QUERY)
                    worksheetTable.load(getWSItemsModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                worksheetTable.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                worksheetTable.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        qcname = (AutoComplete<String>)worksheetTable.getColumnWidget(meta.getTestWorksheetItem().getQcName());            
        qcname.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;                
                ArrayList<TableDataRow> model;
                TableDataRow row;
                IdNameVO data;
                ArrayList<IdNameVO> list;
                
                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());

                window.setBusy();                
                try {
                    list = qcService.callList("fetchByName", parser.getParameter().get(0));
                    model = new ArrayList<TableDataRow>();
                    for(int i = 0; i <list.size(); i++) {
                        data = list.get(i);
                        row = new TableDataRow(data.getName(),data.getName());
                        model.add(row);
                    }
                    qcname.showAutoMatches(model);
                }catch(Exception e) {
                    Window.alert(e.getMessage());                     
                }   
                window.clearStatus();
            }
            
        });

        worksheetTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, col;
                Object val;
                TestWorksheetItemDO item;

                r = event.getRow();
                col = event.getCol();
                
                val = worksheetTable.getObject(r,col);
                item = worksheetManager.getItemAt(r);
                switch(col) {
                    case 0:
                        item.setPosition((Integer)val);
                        break;
                    case 1:
                        item.setTypeId((Integer)val);
                        break;
                    case 2:                        
                        item.setQcName((String)((TableDataRow)val).key);
                        break;
                }
            }
        });

        worksheetTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {                                
                worksheetManager.addItem(new TestWorksheetItemDO());               
            }
        });

        worksheetTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                worksheetManager.removeItemAt(event.getIndex());
            }
        });

        addWSItemButton = (AppButton)def.getWidget("addWSItemButton");
        addScreenHandler(addWSItemButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                worksheetTable.addRow();
                worksheetTable.selectRow(worksheetTable.numRows()-1);
                worksheetTable.scrollToSelection();
                worksheetTable.startEditing(worksheetTable.numRows()-1, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {               
                addWSItemButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        removeWSItemButton = (AppButton)def.getWidget("removeWSItemButton");
        addScreenHandler(removeWSItemButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;
                
                r = worksheetTable.getSelectedRow();
                if (r > -1 && worksheetTable.numRows() > 0) 
                    worksheetTable.deleteRow(r);   
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeWSItemButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        worksheetAnalyteTable = (TableWidget)def.getWidget("worksheetAnalyteTable");
        addScreenHandler(worksheetAnalyteTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                if(state != State.QUERY)
                    worksheetAnalyteTable.load(getWSAnalytesModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                worksheetAnalyteTable.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                worksheetAnalyteTable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        worksheetAnalyteTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, col;
                Integer val;
                TestWorksheetAnalyteViewDO anaDO;
                
                r = event.getRow();
                col = event.getCol();

                val = (Integer)worksheetAnalyteTable.getRow(r).cells.get(col).value;
                anaDO = worksheetManager.getAnalyteAt(r);
                switch(col) {                    
                    case 1:
                        anaDO.setRepeat(val);
                        break;
                    case 2:
                        anaDO.setFlagId(val);
                        break;
                }
            }
        });

        worksheetAnalyteTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {               
                worksheetManager.removeAnalyteAt(event.getIndex());                
            }
        });
        
        addWSAnalyteButton = (AppButton)def.getWidget("addWSAnalyteButton");
        addScreenHandler(addWSAnalyteButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ScreenWindow modal;

                try {
                    testAnalytePicker = new TestAnalyteLookupScreen(analyteManager);
                    testAnalytePicker.addActionHandler(new ActionHandler<TestAnalyteLookupScreen.Action>() {

                        public void onAction(ActionEvent<TestAnalyteLookupScreen.Action> event) {
                            ArrayList<TableDataRow> model;
                            TestWorksheetAnalyteViewDO anaDO;
                            TableDataRow row;
                            if (event.getAction() == TestAnalyteLookupScreen.Action.OK) {
                                model = (ArrayList<TableDataRow>)event.getData();
                                for (int i = 0; i < model.size(); i++ ) {
                                    row = model.get(i);
                                    anaDO = new TestWorksheetAnalyteViewDO();
                                    anaDO.setAnalyteName((String)row.cells.get(0).getValue());
                                    anaDO.setTestAnalyteId((Integer)row.key);
                                    anaDO.setRepeat(1);
                                    worksheetManager.addAnalyte(anaDO);
                                }
                                DataChangeEvent.fire(screen, worksheetAnalyteTable);
                            }

                        }

                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert("error: " + e.getMessage());
                    return;
                }
                modal = new ScreenWindow("Test Analyte LookUp", "testAnalytePickerScreen", "",
                                         true, false);
                modal.setName(consts.get("testAnalyteSelection"));
                modal.setContent(testAnalytePicker);
                testAnalytePicker.setScreenState(State.DEFAULT);
            }

            public void onStateChange(StateChangeEvent<State> event) {               
                addWSAnalyteButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        removeWSAnalyteButton = (AppButton)def.getWidget("removeWSAnalyteButton");
        addScreenHandler(removeWSAnalyteButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int ar = worksheetAnalyteTable.getSelectedRow();
                if(ar != -1)
                    worksheetAnalyteTable.deleteRow(ar);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeWSAnalyteButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });
    }
    
    public void setManager(TestManager manager) {
        this.manager = manager;
        loaded = false;     
    }
    
    public void draw(){
        if(!loaded) { 
            try {
                if(state == State.UPDATE || state == State.ADD)   
                    analyteManager = manager.getTestAnalytes();                   
                worksheetManager = manager.getTestWorksheet();                
                DataChangeEvent.fire(this);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }           
        loaded = true;       
    }
    
    public void onAction(ActionEvent<AnalyteAndResultTab.Action> event) {
        TestAnalyteViewDO anaDO;
        
        if(state == State.QUERY)
            return;
              
        if(event.getAction() == Action.ANALYTE_CHANGED) {
            anaDO = (TestAnalyteViewDO)event.getData();
            setAnalyteErrors(anaDO.getId(), anaDO.getAnalyteName(),"analyteNameChanged",true);
        } else if(event.getAction() == Action.ANALYTE_DELETED) {
            anaDO = (TestAnalyteViewDO)event.getData();
            setAnalyteErrors(anaDO.getId(), anaDO.getAnalyteName(),"analyteDeleted",false);
        }         
        
    }
    
    
    protected void clearKeys(TestWorksheetManager twm) {
        TestWorksheetItemDO item;
        TestWorksheetAnalyteViewDO ana;
        TestWorksheetDO tw;
        boolean hasData;
        int i;
        
        tw = twm.getWorksheet();
        hasData = false;
        
        twm.setTestId(null);
        
        for(i = 0; i < twm.itemCount(); i++) {
            item = twm.getItemAt(i);
            item.setId(null);
            item.setTestWorksheetId(null);   
            hasData = true;
        }
        
        for(i = 0; i < twm.analyteCount(); i++) {
            ana = twm.getAnalyteAt(i);
            ana.setId(null);
            ana.setTestId(null);
            ana.setTestAnalyteId(ana.getTestAnalyteId()*(-1));
            hasData = true;
        }
        
        if(hasData)
            tw.setId(null);
    }   
    
    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        List<DictionaryDO> list;
        
        model = new ArrayList<TableDataRow>();
        list = DictionaryCache.getListByCategorySystemName("test_worksheet_analyte_flags");
        model.add(new TableDataRow(null, ""));
        for(DictionaryDO resultDO :  list){
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        } 
        ((Dropdown)worksheetAnalyteTable.getColumnWidget(meta.getTestWorksheetAnalyte().getFlagId())).setModel(model);
        
        model = new ArrayList<TableDataRow>();
        list = DictionaryCache.getListByCategorySystemName("test_worksheet_item_type");
        model.add(new TableDataRow(null, ""));
        for(DictionaryDO resultDO :  list){
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        } 
        ((Dropdown)worksheetTable.getColumnWidget(meta.getTestWorksheetItem().getTypeId())).setModel(model);
        
        model = new ArrayList<TableDataRow>();
        list = DictionaryCache.getListByCategorySystemName("test_worksheet_format");
        model.add(new TableDataRow(null, ""));
        for(DictionaryDO resultDO :  list){
            model.add(new TableDataRow(resultDO.getId(),resultDO.getEntry()));
        } 
        formatId.setModel(model);
        
    }
    
    private ArrayList<TableDataRow> getWSItemsModel() {
        ArrayList<TableDataRow> model;       
        TestWorksheetItemDO item;
        TableDataRow row;
        
        model = new ArrayList<TableDataRow>();
                
        if(manager == null)
            return model;
        
                   
        for (int i = 0; i < worksheetManager.itemCount(); i++ ) {
            item = worksheetManager.getItemAt(i);
            row = new TableDataRow(3);
            row.key = item.getId();

            row.cells.get(0).setValue(item.getPosition());
            row.cells.get(1).setValue(item.getTypeId());
            row.cells.get(2).setValue(new TableDataRow(item.getQcName(), item.getQcName()));

            model.add(row);
        }        
        
        return model;
    }
    
    private ArrayList<TableDataRow> getWSAnalytesModel() {
        ArrayList<TableDataRow> model;       
        TestWorksheetAnalyteViewDO analyte;
        TableDataRow row;
        int i;
        
        model = new ArrayList<TableDataRow>();
                
        if(manager == null)            
            return model;
        
        for(i = 0 ; i < worksheetManager.analyteCount(); i++) {
            analyte = worksheetManager.getAnalyteAt(i);
            row = new TableDataRow(3);            
            row.key = analyte.getId();            
            row.data = analyte.getTestAnalyteId();
            
            row.cells.get(0).setValue(analyte.getAnalyteName());            
            row.cells.get(1).setValue(analyte.getRepeat());
            row.cells.get(2).setValue(analyte.getFlagId());
            
            model.add(row);            
        }                
        
        return model;
    }
    
    
    private void setAnalyteErrors(Integer id,String name,String key,boolean matchLabel) {
        TableDataRow trow;
        String val;
        Integer data;

        for (int i = 0; i < worksheetAnalyteTable.numRows(); i++ ) {
            trow = worksheetAnalyteTable.getRow(i);
            val = (String)trow.cells.get(0).getValue();
            data = (Integer)trow.data;

            if (data.equals(id)) {
                if ((matchLabel && ! (val.equals(name))) || !matchLabel)
                    worksheetAnalyteTable.setCellException(i, 0, new LocalizedException(key));
            }
        }
    }

}
