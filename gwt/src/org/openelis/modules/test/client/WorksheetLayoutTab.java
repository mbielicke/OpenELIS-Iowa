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

import org.openelis.cache.CategoryCache;
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
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.ScreenWindowInt;
import org.openelis.gwt.widget.TextBox;
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
import org.openelis.manager.TestAnalyteManager;
import org.openelis.manager.TestManager;
import org.openelis.manager.TestWorksheetManager;
import org.openelis.meta.TestMeta;
import org.openelis.modules.test.client.AnalyteAndResultTab.Action;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;

public class WorksheetLayoutTab extends Screen implements ActionHandler<AnalyteAndResultTab.Action> {

    private TestManager                      manager;
    private TestAnalyteManager               analyteManager;

    private WorksheetLayoutTab               screen;
    private TestWorksheetAnalyteLookupScreen analyteLookup;

    private boolean                          loaded;

    private Dropdown<Integer>                formatId;
    private TableWidget                      worksheetAnalyteTable, worksheetTable;
    private AppButton                        addWSItemButton, removeWSItemButton,
                                             addWSAnalyteButton, removeWSAnalyteButton;
    private TextBox<Integer>                 subsetCapacity, totalCapacity;
    private AutoComplete                     scriptlet, qcname;
    private ScreenService                    scriptletService, qcService;

    public WorksheetLayoutTab(ScreenDefInt def, ScreenWindowInt window, ScreenService service,
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
                        formatId.setSelection(manager.getTestWorksheet().getWorksheet().getFormatId());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                try {                    
                    manager.getTestWorksheet().getWorksheet().setFormatId(event.getValue());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                formatId.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                .contains(event.getState()));
                formatId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        subsetCapacity = (TextBox)def.getWidget(TestMeta.getWorksheetSubsetCapacity());
        addScreenHandler(subsetCapacity, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                try {
                    if(manager != null)
                        subsetCapacity.setValue(manager.getTestWorksheet().getWorksheet().getSubsetCapacity());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                try {
                    manager.getTestWorksheet().getWorksheet().setSubsetCapacity(event.getValue());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                subsetCapacity.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                            .contains(event.getState()));
                subsetCapacity.setQueryMode(event.getState() == State.QUERY);
            }
        });

        totalCapacity = (TextBox)def.getWidget(TestMeta.getWorksheetTotalCapacity());
        addScreenHandler(totalCapacity, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                try {
                    if(manager != null)
                        totalCapacity.setValue(manager.getTestWorksheet().getWorksheet().getTotalCapacity());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
                
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                try {
                    manager.getTestWorksheet().getWorksheet().setTotalCapacity(event.getValue());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                totalCapacity.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                            .contains(event.getState()));
                totalCapacity.setQueryMode(event.getState() == State.QUERY);
            }
        });

        scriptlet = (AutoComplete)def.getWidget(TestMeta.getWorksheetScriptletName());
        addScreenHandler(scriptlet, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                try {
                    if(manager != null)
                        scriptlet.setSelection(manager.getTestWorksheet().getWorksheet().getScriptletId(),
                                               manager.getTestWorksheet().getWorksheet().getScriptletName());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                try {
                    manager.getTestWorksheet().getWorksheet().setScriptletId(event.getValue());
                    manager.getTestWorksheet().getWorksheet().setScriptletName(scriptlet.getTextBoxDisplay());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                scriptlet.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                        .contains(event.getState()));
                scriptlet.setQueryMode(event.getState() == State.QUERY);
            }
        });

        scriptlet.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<TableDataRow> model;
                ArrayList<IdNameVO> list;

                try {
                    list = scriptletService.callList("fetchByName", event.getMatch() + "%");
                    model = new ArrayList<TableDataRow>();
                    for (IdNameVO data : list) {
                        model.add(new TableDataRow(data.getId(), data.getName()));
                    }
                    scriptlet.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }

        });

        worksheetTable = (TableWidget)def.getWidget("worksheetTable");
        addScreenHandler(worksheetTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    worksheetTable.load(getWSItemsModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                worksheetTable.enable(true);
                worksheetTable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        qcname = (AutoComplete<String>)worksheetTable.getColumnWidget(TestMeta.getWorksheetItemQcName());
        qcname.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<TableDataRow> model;
                ArrayList<QcDO> list;

                try {
                    list = qcService.callList("fetchByName", QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<TableDataRow>();
                    for (QcDO data : list) 
                        model.add(new TableDataRow(data.getName(), data.getName()));                    
                    qcname.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
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

                val = worksheetTable.getObject(r, c);
                try {
                    data = manager.getTestWorksheet().getItemAt(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
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
                        data.setQcName((String)((TableDataRow)val).key);
                        break;
                }
            }
        });

        worksheetTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                try {
                    manager.getTestWorksheet().addItem(new TestWorksheetItemDO());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        worksheetTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getTestWorksheet().removeItemAt(event.getIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        addWSItemButton = (AppButton)def.getWidget("addWSItemButton");
        addScreenHandler(addWSItemButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {               
                int n;

                worksheetTable.addRow();
                n = worksheetTable.numRows() - 1;
                worksheetTable.selectRow(n);
                worksheetTable.scrollToSelection();
                worksheetTable.startEditing(n, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addWSItemButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                              .contains(event.getState()));
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
                removeWSItemButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                                 .contains(event.getState()));
            }
        });

        worksheetAnalyteTable = (TableWidget)def.getWidget("worksheetAnalyteTable");
        addScreenHandler(worksheetAnalyteTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    worksheetAnalyteTable.load(getWSAnalytesModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                worksheetAnalyteTable.enable(true);
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

                val = (Integer)worksheetAnalyteTable.getObject(r,c);
                
                try {
                    data = manager.getTestWorksheet().getAnalyteAt(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
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
                    Window.alert(e.getMessage());

                }
            }
        });

        addWSAnalyteButton = (AppButton)def.getWidget("addWSAnalyteButton");
        addScreenHandler(addWSAnalyteButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                ScreenWindow modal;

                if (analyteLookup == null) {
                    try {
                        analyteLookup = new TestWorksheetAnalyteLookupScreen(analyteManager);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Window.alert("TestWorksheetAnalyteLookup error: " + e.getMessage());
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
                                Window.alert(e.getMessage());
                                e.printStackTrace();
                            }

                        }

                    });
                } else {                    
                    analyteLookup.refresh(analyteManager);
                } 
                
                modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
                modal.setName(consts.get("testAnalyteSelection"));
                modal.setContent(analyteLookup);
                analyteLookup.setScreenState(State.DEFAULT);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addWSAnalyteButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                                 .contains(event.getState()));
            }
        });

        removeWSAnalyteButton = (AppButton)def.getWidget("removeWSAnalyteButton");
        addScreenHandler(removeWSAnalyteButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;
                
                r = worksheetAnalyteTable.getSelectedRow();
                if (r != -1 && worksheetAnalyteTable.numRows() > 0)
                    worksheetAnalyteTable.deleteRow(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeWSAnalyteButton.enable(EnumSet.of(State.ADD, State.UPDATE)
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
        ArrayList<TableDataRow> model;
        List<DictionaryDO> list;
        TableDataRow row;

        model = new ArrayList<TableDataRow>();
        list = CategoryCache.getBySystemName("test_worksheet_analyte_flags");
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : list) {
            row = new TableDataRow(resultDO.getId(), resultDO.getEntry());
            row.enabled = ("Y".equals(resultDO.getIsActive()));
            model.add(row);
        }
        ((Dropdown)worksheetAnalyteTable.getColumnWidget(TestMeta.getWorksheetAnalyteFlagId())).setModel(model);

        model = new ArrayList<TableDataRow>();
        list = CategoryCache.getBySystemName("test_worksheet_item_type");
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : list) {
            row = new TableDataRow(resultDO.getId(), resultDO.getEntry());
            row.enabled = ("Y".equals(resultDO.getIsActive()));
            model.add(row);
        }
        ((Dropdown)worksheetTable.getColumnWidget(TestMeta.getWorksheetItemTypeId())).setModel(model);

        model = new ArrayList<TableDataRow>();
        list = CategoryCache.getBySystemName("test_worksheet_format");
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : list) {
            row = new TableDataRow(resultDO.getId(), resultDO.getEntry());
            row.enabled = ("Y".equals(resultDO.getIsActive()));
            model.add(row);
        }
        formatId.setModel(model);
    }

    private ArrayList<TableDataRow> getWSItemsModel() {
        ArrayList<TableDataRow> model;
        TestWorksheetItemDO item;
        TableDataRow row;

        model = new ArrayList<TableDataRow>();

        if (manager == null)
            return model;

        try {
            for (int i = 0; i < manager.getTestWorksheet().itemCount(); i++ ) {
                item = manager.getTestWorksheet().getItemAt(i);
                row = new TableDataRow(3);
                row.key = item.getId();

                row.cells.get(0).setValue(item.getPosition());
                row.cells.get(1).setValue(item.getTypeId());
                row.cells.get(2).setValue(new TableDataRow(item.getQcName(), item.getQcName()));

                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }        

        return model;
    }

    private ArrayList<TableDataRow> getWSAnalytesModel() {
        ArrayList<TableDataRow> model;
        TestWorksheetAnalyteViewDO data;
        TableDataRow row;
        int i;

        model = new ArrayList<TableDataRow>();

        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.getTestWorksheet().analyteCount(); i++ ) {
                data = manager.getTestWorksheet().getAnalyteAt(i);
                row = new TableDataRow(3);
                row.key = data.getId();
                row.data = data.getTestAnalyteId();
    
                row.cells.get(0).setValue(data.getAnalyteName());
                row.cells.get(1).setValue(data.getRepeat());
                row.cells.get(2).setValue(data.getFlagId());
    
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }

        return model;
    }

    private void setAnalyteErrors(Integer id, String name, String key, boolean matchLabel) {
        TableDataRow trow;
        String val;
        Integer data;

        for (int i = 0; i < worksheetAnalyteTable.numRows(); i++ ) {
            trow = worksheetAnalyteTable.getRow(i);
            val = (String)trow.cells.get(0).getValue();
            data = (Integer)trow.data;

            if (data.equals(id)) {
                if ( (matchLabel && ! (val.equals(name))) || !matchLabel)
                    worksheetAnalyteTable.setCellException(i, 0, new LocalizedException(key));
            }
        }
    }

}
