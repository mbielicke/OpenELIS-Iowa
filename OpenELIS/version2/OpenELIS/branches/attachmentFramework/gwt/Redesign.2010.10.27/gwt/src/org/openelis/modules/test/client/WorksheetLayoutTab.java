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
package org.openelis.modules.test.client;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.QcDO;
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
import org.openelis.gwt.screen.Screen.State;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AutoCompleteValue;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.ModalWindow;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.TestAnalyteManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestWorksheetManager;
import org.openelis.meta.TestMeta;
import org.openelis.modules.test.client.AnalyteAndResultTab.Action;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;

public class WorksheetLayoutTab extends Screen implements ActionHandler<AnalyteAndResultTab.Action> {

    private TestManager                      manager;
    private TestAnalyteManager               analyteManager;

    private WorksheetLayoutTab               screen;
    private TestWorksheetAnalyteLookupScreen analyteLookup;

    private boolean                          loaded;

    private Dropdown<Integer>                formatId;
    private Table                            worksheetAnalyteTable, worksheetTable;
    private Button                           addWSItemButton, removeWSItemButton,
                                             addWSAnalyteButton, removeWSAnalyteButton;
    private TextBox<Integer>                 batchCapacity, totalCapacity;
    private AutoComplete                     scriptlet, qcname;
    private ScreenService                    scriptletService, qcService;

    public WorksheetLayoutTab(ScreenDefInt def, Window window, ScreenService service,
                              ScreenService scriptletService, ScreenService qcService) {
        setDefinition(def);
        setWindow(window);
        this.service = service;
        this.scriptletService = scriptletService;
        this.qcService = qcService;
        
        initialize();
        initializeDropdowns();            
    }    

    private void initialize() {
        screen = this;

        formatId = (Dropdown)def.getWidget(TestMeta.getWorksheetFormatId());
        addScreenHandler(formatId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                try {
                    if(manager != null)
                        formatId.setValue(manager.getTestWorksheet().getWorksheet().getFormatId());
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                try {                    
                    manager.getTestWorksheet().getWorksheet().setFormatId(event.getValue());
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                formatId.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                .contains(event.getState()));
                formatId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        batchCapacity = (TextBox)def.getWidget(TestMeta.getWorksheetBatchCapacity());
        addScreenHandler(batchCapacity, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                try {
                    if(manager != null)
                        batchCapacity.setValue(manager.getTestWorksheet().getWorksheet().getBatchCapacity());
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                try {
                    manager.getTestWorksheet().getWorksheet().setBatchCapacity(event.getValue());
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                batchCapacity.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                            .contains(event.getState()));
                batchCapacity.setQueryMode(event.getState() == State.QUERY);
            }
        });

        totalCapacity = (TextBox)def.getWidget(TestMeta.getWorksheetTotalCapacity());
        addScreenHandler(totalCapacity, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                try {
                    if(manager != null)
                        totalCapacity.setValue(manager.getTestWorksheet().getWorksheet().getTotalCapacity());
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
                
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                try {
                    manager.getTestWorksheet().getWorksheet().setTotalCapacity(event.getValue());
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                totalCapacity.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                            .contains(event.getState()));
                totalCapacity.setQueryMode(event.getState() == State.QUERY);
            }
        });

        scriptlet = (AutoComplete)def.getWidget(TestMeta.getWorksheetScriptletName());
        addScreenHandler(scriptlet, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                try {
                    if(manager != null)
                        scriptlet.setValue(manager.getTestWorksheet().getWorksheet().getScriptletId(),
                                               manager.getTestWorksheet().getWorksheet().getScriptletName());
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                try {
                    manager.getTestWorksheet().getWorksheet().setScriptletId(event.getValue());
                    manager.getTestWorksheet().getWorksheet().setScriptletName(scriptlet.getDisplay());
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                scriptlet.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                        .contains(event.getState()));
                scriptlet.setQueryMode(event.getState() == State.QUERY);
            }
        });

        scriptlet.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<Item<Integer>> model;
                ArrayList<IdNameVO> list;

                try {
                    list = scriptletService.callList("fetchByName", event.getMatch() + "%");
                    model = new ArrayList<Item<Integer>>();
                    for (IdNameVO data : list) {
                        model.add(new Item<Integer>(data.getId(), data.getName()));
                    }
                    scriptlet.showAutoMatches(model);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }

        });

        worksheetTable = (Table)def.getWidget("worksheetTable");
        addScreenHandler(worksheetTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    worksheetTable.setModel(getWSItemsModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                worksheetTable.setEnabled(true);
                worksheetTable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        qcname = (AutoComplete)worksheetTable.getColumnWidget(TestMeta.getWorksheetItemQcName());
        qcname.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                ArrayList<Item<Integer>> model;
                ArrayList<QcDO> list;
                String param = "";

                parser = new QueryFieldUtil();

                try {
                	if(!event.getMatch().equals("")) {
                		parser.parse(event.getMatch());
                		param = parser.getParameter().get(0);
                	}
                    list = qcService.callList("fetchByName", param);
                    model = new ArrayList<Item<Integer>>();
                    for (QcDO data : list) 
                        model.add(new Item<Integer>(data.getId(), data.getName()));                    
                    qcname.showAutoMatches(model);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }

        });
        
        worksheetTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {                
                if(state != State.ADD && state != State.UPDATE && state != State.QUERY)  
                    event.cancel();
            }            
        });

        worksheetTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                TestWorksheetItemDO data;

                r = event.getRow();
                c = event.getCol();

                val = worksheetTable.getValueAt(r, c);
                try {
                    data = manager.getTestWorksheet().getItemAt(r);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                    return;
                }
                switch (c) {
                    case 0:
                        data.setPosition((Integer)val);
                        break;
                    case 1:
                        data.setTypeId((Integer)val);
                        break;
                    case 2:
                        data.setQcName(((AutoCompleteValue)val).getDisplay());
                        break;
                }
            }
        });

        worksheetTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                try {
                    manager.getTestWorksheet().addItem(new TestWorksheetItemDO());
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });

        worksheetTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getTestWorksheet().removeItemAt(event.getIndex());
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });

        addWSItemButton = (Button)def.getWidget("addWSItemButton");
        addScreenHandler(addWSItemButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {               
                int n;

                worksheetTable.addRow();
                n = worksheetTable.getRowCount() - 1;
                worksheetTable.selectRowAt(n);
                worksheetTable.scrollToVisible(n);
                worksheetTable.startEditing(n, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addWSItemButton.setEnabled(EnumSet.of(State.ADD, State.UPDATE)
                                              .contains(event.getState()));
            }
        });

        removeWSItemButton = (Button)def.getWidget("removeWSItemButton");
        addScreenHandler(removeWSItemButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = worksheetTable.getSelectedRow();
                if (r > -1 && worksheetTable.getRowCount() > 0)
                    worksheetTable.removeRowAt(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeWSItemButton.setEnabled(EnumSet.of(State.ADD, State.UPDATE)
                                                 .contains(event.getState()));
            }
        });

        worksheetAnalyteTable = (Table)def.getWidget("worksheetAnalyteTable");
        addScreenHandler(worksheetAnalyteTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    worksheetAnalyteTable.setModel(getWSAnalytesModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                worksheetAnalyteTable.setEnabled(true);
                worksheetAnalyteTable.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        worksheetAnalyteTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {                
                if(state != State.ADD && state != State.UPDATE && state != State.QUERY)  
                    event.cancel();
            }            
        });

        worksheetAnalyteTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Integer val;
                TestWorksheetAnalyteViewDO data;

                r = event.getRow();
                c = event.getCol();

                val = (Integer)worksheetAnalyteTable.getValueAt(r,c);
                
                try {
                    data = manager.getTestWorksheet().getAnalyteAt(r);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                    return;
                }
                switch (c) {
                    case 1:
                        data.setRepeat(val);
                        break;
                    case 2:
                        data.setFlagId(val);
                        break;
                }
            }
        });

        worksheetAnalyteTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getTestWorksheet().removeAnalyteAt(event.getIndex());
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());

                }
            }
        });

        addWSAnalyteButton = (Button)def.getWidget("addWSAnalyteButton");
        addScreenHandler(addWSAnalyteButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ModalWindow modal;

                if (analyteLookup == null) {
                    try {
                        analyteLookup = new TestWorksheetAnalyteLookupScreen(analyteManager);
                    } catch (Exception e) {
                        e.printStackTrace();
                        com.google.gwt.user.client.Window.alert("TestWorksheetAnalyteLookup error: " + e.getMessage());
                        return;
                    }

                    analyteLookup.addActionHandler(new ActionHandler<TestWorksheetAnalyteLookupScreen.Action>() {
                        public void onAction(ActionEvent<TestWorksheetAnalyteLookupScreen.Action> event) {
                            ArrayList<TestAnalyteViewDO> model;
                            TestWorksheetAnalyteViewDO data;
                            TestAnalyteViewDO ana;
                            try {
                                if (event.getAction() == TestWorksheetAnalyteLookupScreen.Action.OK) {
                                    model = (ArrayList<TestAnalyteViewDO>)event.getData();
                                    for (int i = 0; i < model.size(); i++ ) {
                                        ana = model.get(i);
                                        data = new TestWorksheetAnalyteViewDO();
                                        data.setAnalyteName(ana.getAnalyteName());
                                        data.setTestAnalyteId(ana.getId());
                                        data.setRepeat(1);
                                        manager.getTestWorksheet().addAnalyte(data);
                                    }
                                    DataChangeEvent.fire(screen, worksheetAnalyteTable);
                                }
                            } catch (Exception e) {
                                com.google.gwt.user.client.Window.alert(e.getMessage());
                                e.printStackTrace();
                            }

                        }

                    });
                } else {                    
                    analyteLookup.refresh(analyteManager);
                } 
                
                modal = new ModalWindow();
                modal.setName(consts.get("testAnalyteSelection"));
                modal.setContent(analyteLookup);
                analyteLookup.setScreenState(State.DEFAULT);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addWSAnalyteButton.setEnabled(EnumSet.of(State.ADD, State.UPDATE)
                                                 .contains(event.getState()));
            }
        });

        removeWSAnalyteButton = (Button)def.getWidget("removeWSAnalyteButton");
        addScreenHandler(removeWSAnalyteButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;
                
                r = worksheetAnalyteTable.getSelectedRow();
                if (r != -1 && worksheetAnalyteTable.getRowCount() > 0)
                    worksheetAnalyteTable.removeRowAt(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeWSAnalyteButton.setEnabled(EnumSet.of(State.ADD, State.UPDATE)
                                                    .contains(event.getState()));
            }
        });
    }

    public void setManager(TestManager manager) {
        this.manager = manager;
        loaded = false;
    }

    public void draw() {
        if ( !loaded) {
            try {
                if (state == State.UPDATE || state == State.ADD)
                    analyteManager = manager.getTestAnalytes();
                DataChangeEvent.fire(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        loaded = true;
    }

    public void onAction(ActionEvent<AnalyteAndResultTab.Action> event) {
        TestAnalyteViewDO data;

        if (state == State.QUERY)
            return;

        if (event.getAction() == Action.ANALYTE_CHANGED) {
            data = (TestAnalyteViewDO)event.getData();
            setAnalyteErrors(data.getId(), data.getAnalyteName(), "analyteNameChanged", true);
        } else if (event.getAction() == Action.ANALYTE_DELETED) {
            data = (TestAnalyteViewDO)event.getData();
            setAnalyteErrors(data.getId(), data.getAnalyteName(), "analyteDeleted", false);
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

        for (i = 0; i < twm.itemCount(); i++ ) {
            item = twm.getItemAt(i);
            item.setId(null);
            item.setTestWorksheetId(null);
            hasData = true;
        }

        for (i = 0; i < twm.analyteCount(); i++ ) {
            ana = twm.getAnalyteAt(i);
            ana.setId(null);
            ana.setTestId(null);
            ana.setTestAnalyteId(ana.getTestAnalyteId() * ( -1));
            hasData = true;
        }

        if (hasData)
            tw.setId(null);
    }

    private void initializeDropdowns() {
        ArrayList<Item<Integer>> model;
        List<DictionaryDO> list;
        Item<Integer> row;

        model = new ArrayList<Item<Integer>>();
        list = DictionaryCache.getListByCategorySystemName("test_worksheet_analyte_flags");
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO resultDO : list) {
            row = new Item<Integer>(resultDO.getId(), resultDO.getEntry());
            row.setEnabled("Y".equals(resultDO.getIsActive()));
            model.add(row);
        }
        ((Dropdown)worksheetAnalyteTable.getColumnWidget(TestMeta.getWorksheetAnalyteFlagId())).setModel(model);

        model = new ArrayList<Item<Integer>>();
        list = DictionaryCache.getListByCategorySystemName("test_worksheet_item_type");
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO resultDO : list) {
            row = new Item<Integer>(resultDO.getId(), resultDO.getEntry());
            row.setEnabled("Y".equals(resultDO.getIsActive()));
            model.add(row);
        }
        ((Dropdown)worksheetTable.getColumnWidget(TestMeta.getWorksheetItemTypeId())).setModel(model);

        model = new ArrayList<Item<Integer>>();
        list = DictionaryCache.getListByCategorySystemName("test_worksheet_format");
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO resultDO : list) {
            row = new Item<Integer>(resultDO.getId(), resultDO.getEntry());
            row.setEnabled("Y".equals(resultDO.getIsActive()));
            model.add(row);
        }
        formatId.setModel(model);
    }

    private ArrayList<Row> getWSItemsModel() {
        ArrayList<Row> model;
        TestWorksheetItemDO item;
        Row row;

        model = new ArrayList<Row>();

        if (manager == null)
            return model;

        try {
            for (int i = 0; i < manager.getTestWorksheet().itemCount(); i++ ) {
                item = manager.getTestWorksheet().getItemAt(i);
                row = new Row(3);
                //row.key = item.getId();

                row.setCell(0,item.getPosition());
                row.setCell(1,item.getTypeId());
                row.setCell(2,new AutoCompleteValue(item.getId(), item.getQcName()));

                model.add(row);
            }
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            e.printStackTrace();
        }        

        return model;
    }

    private ArrayList<Row> getWSAnalytesModel() {
        ArrayList<Row> model;
        TestWorksheetAnalyteViewDO data;
        Row row;
        int i;

        model = new ArrayList<Row>();

        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.getTestWorksheet().analyteCount(); i++ ) {
                data = manager.getTestWorksheet().getAnalyteAt(i);
                row = new Row(3);
                //row.key = data.getId();
                row.setData(data.getTestAnalyteId());
    
                row.setCell(0,data.getAnalyteName());
                row.setCell(1,data.getRepeat());
                row.setCell(2,data.getFlagId());
    
                model.add(row);
            }
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            e.printStackTrace();
        }

        return model;
    }

    private void setAnalyteErrors(Integer id, String name, String key, boolean matchLabel) {
        Row trow;
        String val;
        Integer data;

        for (int i = 0; i < worksheetAnalyteTable.getRowCount(); i++ ) {
            trow = worksheetAnalyteTable.getRowAt(i);
            val = (String)trow.getCell(0);
            data = (Integer)trow.getData();

            if (data.equals(id)) {
                if ( (matchLabel && ! (val.equals(name))) || !matchLabel)
                    worksheetAnalyteTable.addException(i, 0, new LocalizedException(key));
            }
        }
    }

}
