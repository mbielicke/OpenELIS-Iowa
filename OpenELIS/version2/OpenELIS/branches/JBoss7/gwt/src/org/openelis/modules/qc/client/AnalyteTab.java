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
package org.openelis.modules.qc.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.CategoryCache;
import org.openelis.domain.AnalyteDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.QcAnalyteViewDO;
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
import org.openelis.manager.QcManager;
import org.openelis.meta.QcMeta;
import org.openelis.modules.analyte.client.AnalyteService;
import org.openelis.modules.dictionary.client.DictionaryLookupScreen;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;

public class AnalyteTab extends Screen {
    private QcManager                   manager;

    private AppButton                   addAnalyteButton, removeAnalyteButton,
                                        dictionaryButton;
    private AutoComplete<Integer>       analyte;
    private Dropdown<Integer>           analyteTypeId;
    private TableWidget                 table;

    private DictionaryLookupScreen      dictLookup;

    private boolean                     loaded;

    public AnalyteTab(ScreenDefInt def, ScreenWindowInt window) {        
        setDefinition(def);
        setWindow(window);
                
        initialize();
        initializeDropdowns();
    }
    
    private void initialize() {
        table = (TableWidget)def.getWidget("qcAnalyteTable");
       
        analyte = (AutoComplete<Integer>)table.getColumnWidget(QcMeta.getQcAnalyteAnalyteName());
        analyteTypeId = (Dropdown)table.getColumnWidget(QcMeta.getQcAnalyteTypeId());
        
        addScreenHandler(table, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    table.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                table.enable(true);
                table.setQueryMode(EnumSet.of(State.QUERY).contains(event.getState()));                
            }
        });
        
        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if (state != State.ADD && state != State.UPDATE)
                    event.cancel();
            }
        });

        table.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                TableDataRow row;
                QcAnalyteViewDO data;

                r = event.getRow();
                c = event.getCol();
                val = table.getObject(r, c);
                try {
                    data = manager.getAnalytes().getAnalyteAt(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }
                switch (c) {
                    case 0:
                        row = (TableDataRow)val;
                        data.setAnalyteId((Integer)row.key);
                        data.setAnalyteName(analyte.getTextBoxDisplay());
                        break;
                    case 1:
                        data.setTypeId((Integer)val);                        
                        break;
                    case 2:
                        data.setIsTrendable((String)val);
                        break;
                    case 3:
                        data.setValue((String)val);                        
                        break;
                }
            }
        });

        table.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                int r;
                
                r = event.getIndex();
                try {
                    manager.getAnalytes().addAnalyteAt(new QcAnalyteViewDO(), r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        table.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getAnalytes().removeAnalyteAt(event.getIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        analyte.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                AnalyteDO data;
                ArrayList<AnalyteDO> list;
                ArrayList<TableDataRow> model;

                try {
                    list = AnalyteService.get().fetchByName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<TableDataRow>();

                    for (int i = 0; i < list.size(); i++ ) {
                        data = list.get(i);
                        model.add(new TableDataRow(data.getId(), data.getName()));
                    }
                    analyte.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        addAnalyteButton = (AppButton)def.getWidget("addAnalyteButton");
        addScreenHandler(addAnalyteButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = table.getSelectedRow() + 1;
                if (r == 0)
                    r = table.numRows();                
                table.addRow(r);
                table.selectRow(r);
                table.scrollToSelection();
                table.startEditing(r, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addAnalyteButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                               .contains(event.getState()));
            }
        });

        removeAnalyteButton = (AppButton)def.getWidget("removeAnalyteButton");
        addScreenHandler(removeAnalyteButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = table.getSelectedRow();
                if (r > -1 && table.numRows() > 0)
                    table.deleteRow(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeAnalyteButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                                  .contains(event.getState()));
            }
        });

        dictionaryButton = (AppButton)def.getWidget("dictionaryButton");
        addScreenHandler(dictionaryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                showDictionary(null,null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                dictionaryButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                               .contains(event.getState()));
            }
        });
    }
    
    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        ArrayList<DictionaryDO> list;
        TableDataRow row;

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = CategoryCache.getBySystemName("qc_analyte_type");
        for (DictionaryDO d : list) {
            row = new TableDataRow(d.getId(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);
        }

        analyteTypeId.setModel(model);
    }
    
    private ArrayList<TableDataRow> getTableModel() {
        int i;
        QcAnalyteViewDO data;
        ArrayList<TableDataRow> model;

        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.getAnalytes().count(); i++ ) {
                data = manager.getAnalytes().getAnalyteAt(i);
                model.add(new TableDataRow(null, 
                                           new TableDataRow(data.getAnalyteId(),data.getAnalyteName()),
                                           data.getTypeId(), data.getIsTrendable(), data.getValue()));
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }

    private void showDictionary(String entry,ArrayList<IdNameVO> list) {
        ScreenWindow modal;

        if (dictLookup == null) {
            try {
                dictLookup = new DictionaryLookupScreen();
            } catch (Exception e) {
                e.printStackTrace();
                Window.alert("DictionaryLookup Error: " + e.getMessage());
                return;
            }
        
            dictLookup.addActionHandler(new ActionHandler<DictionaryLookupScreen.Action>() {
                public void onAction(ActionEvent<DictionaryLookupScreen.Action> event) {
                    int r;
                    IdNameVO entry;
                    QcAnalyteViewDO data;
                    ArrayList<IdNameVO> list;

                    if (event.getAction() == DictionaryLookupScreen.Action.OK) {
                        list = (ArrayList<IdNameVO>)event.getData();
                        if (list != null) {
                            r = table.getSelectedRow();
                            if (r == -1) {
                                window.setError(consts.get("qc.noSelectedRow"));
                                return;
                            }
                            entry = list.get(0);
                            try {
                                data = manager.getAnalytes().getAnalyteAt(r);
                                data.setValue(entry.getName());
                                table.setCell(r, 3, entry.getName());
                                table.clearCellExceptions(r, 3);
                            } catch (Exception e) {
                                e.printStackTrace();
                                table.setCell(r, 3, "");
                                Window.alert("DictionaryLookup Error: " + e.getMessage());
                            }
                        }
                    }
                }
            });
        }
        modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
        modal.setName(consts.get("chooseDictEntry"));
        modal.setContent(dictLookup);
        dictLookup.setScreenState(State.DEFAULT);
        if (list != null) {
            dictLookup.clearFields();
            dictLookup.setQueryResult(entry, list);
        } else if (entry != null) {
            dictLookup.clearFields();
            dictLookup.executeQuery(entry);
        }
    }

    public void setManager(QcManager manager) {
        this.manager = manager;
        loaded = false;
    }

    public void draw() {
        if ( !loaded)
            DataChangeEvent.fire(this);

        loaded = true;
    }
}