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
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.QcLotViewDO;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.QueryFieldUtil;
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
import org.openelis.manager.QcLotManager;
import org.openelis.manager.QcManager;
import org.openelis.meta.QcMeta;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;

public class LotTab extends Screen {
    private QcManager                   manager;

    private AppButton                   addLotButton, removeLotButton, duplicateButton;
    private TableWidget                 table;
    private AutoComplete<Integer>       preparedBy;

    private boolean                     loaded;
    
    public LotTab(ScreenDefInt def, WindowInt window) {   
        setDefinition(def);
        setWindow(window);
        initialize();
        initializeDropdowns();
    }
    
    private void initialize() {
        table = (TableWidget)def.getWidget("qcLotTable");
        
        addScreenHandler(table, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    table.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                table.enable(true);
                table.setQueryMode(EnumSet.of(State.QUERY).contains(event.getState()));
                table.getColumns().get(6).enable(EnumSet.of(State.ADD, State.UPDATE)
                                                 .contains(event.getState()));
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
                QcLotViewDO data;

                r = event.getRow();
                c = event.getCol();
                val = table.getObject(r, c);
                
                try {
                    data = manager.getLots().getLotAt(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }
                
                switch (c) {
                    case 0:                        
                        data.setIsActive((String)val);
                        break;
                    case 1:
                        data.setLotNumber((String)val);                        
                        break;
                    case 2:
                        data.setLocationId((Integer)val);
                        break;
                    case 3:
                        data.setPreparedDate((Datetime)val);                        
                        break;
                    case 4:
                        data.setUsableDate((Datetime)val);
                        break;
                    case 5:
                        data.setExpireDate((Datetime)val);
                        break;    
                    case 6:
                        data.setPreparedVolume((Double)val);
                        break;
                    case 7:
                        data.setPreparedUnitId((Integer)val);
                        break;
                    case 8:
                        row = (TableDataRow)val;
                        if (row != null) {
                            data.setPreparedById((Integer)row.key);
                            data.setPreparedByName((String)row.cells.get(0).getValue());
                        } else {
                            data.setPreparedById(null);
                            data.setPreparedByName(null);
                        }
                        break;

                        
                }
            }
        });

        table.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                QcLotViewDO data;
                TableDataRow row, userRow;

                row = table.getRow(event.getIndex());
                try {
                    data = new QcLotViewDO();
                    data.setIsActive("Y");
                    data.setLotNumber((String)row.cells.get(1).getValue());
                    data.setLocationId((Integer)row.cells.get(2).getValue());
                    data.setPreparedDate((Datetime)row.cells.get(3).getValue());
                    data.setUsableDate((Datetime)row.cells.get(4).getValue());
                    data.setExpireDate((Datetime)row.cells.get(5).getValue());
                    data.setPreparedVolume((Double)row.cells.get(6).getValue());
                    data.setPreparedUnitId((Integer)row.cells.get(7).getValue());
                    
                    userRow = (TableDataRow)row.cells.get(8).getValue();
                    
                    data.setPreparedById((Integer)userRow.key);
                    data.setPreparedByName((String)userRow.cells.get(0).getValue());
                    
                    manager.getLots().addLotAt(data, event.getIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        
        preparedBy = (AutoComplete)table.getColumnWidget(QcMeta.getQcLotPreparedById());        
        preparedBy.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<SystemUserVO> users;
                ArrayList<TableDataRow> model;

                try {
                    users = UserCache.getEmployees(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<TableDataRow>();
                    for (SystemUserVO user : users)
                        model.add(new TableDataRow(user.getId(), user.getLoginName()));
                    preparedBy.showAutoMatches(model);
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert(e.toString());
                }
            }
        });

        table.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getLots().removeLotAt(event.getIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        addLotButton = (AppButton)def.getWidget("addLotButton");
        addScreenHandler(addLotButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n;

                try {
                    table.finishEditing();
                    table.addRow(createLotRow(null));
                    n = table.numRows() - 1;
                    table.selectRow(n);
                    table.scrollToSelection();
                    table.startEditing(n, 1);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    e.printStackTrace();
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addLotButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        removeLotButton = (AppButton)def.getWidget("removeLotButton");
        addScreenHandler(removeLotButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = table.getSelectedRow();

                try {
                    if (r > -1 && table.numRows() > 0) { 
                        window.setBusy(Messages.get().validatingDelete());
                        validateForDelete(manager.getLots().getLotAt(r));
                        table.deleteRow(r);
                    }
                } catch (ValidationErrorsList e) {
                    Window.alert(e.getErrorList().get(0).getMessage());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    e.printStackTrace();
                }   
                window.clearStatus();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeLotButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                                  .contains(event.getState()));
            }
        });
        
        duplicateButton = (AppButton)def.getWidget("duplicateLotButton");
        addScreenHandler(duplicateButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n, r;     
                TableDataRow row;
                QcLotManager man;
                
                r = table.getSelectedRow();
                if (r == -1)
                    return;
                
                table.finishEditing();
                try {
                    man = manager.getLots();
                    
                    row = createLotRow(man.getLotAt(r));
                    
                    n = r + 1;
                    if (n < table.numRows())
                        table.addRow(n, row);
                    else
                        table.addRow(row);
                    
                    table.selectRow(n);
                    table.scrollToSelection();
                    table.startEditing(n, 1);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    e.printStackTrace();
                }                
            }

            public void onStateChange(StateChangeEvent<State> event) {
                duplicateButton.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });
    }
    
    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        ArrayList<DictionaryDO> list;
        TableDataRow row;
        
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = CategoryCache.getBySystemName("laboratory_location");
        for (DictionaryDO d : list) {
            row = new TableDataRow(d.getId(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);
        }

        ((Dropdown<Integer>)table.getColumnWidget(QcMeta.getQcLotLocationId())).setModel(model);
        
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = CategoryCache.getBySystemName("unit_of_measure");
        for (DictionaryDO d : list) {
            row = new TableDataRow(d.getId(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);
        }

        ((Dropdown<Integer>)table.getColumnWidget(QcMeta.getQcLotPreparedUnitId())).setModel(model);
    }
    
    private ArrayList<TableDataRow> getTableModel() {
        QcLotManager man;
        QcLotViewDO data;
        ArrayList<TableDataRow> model;
        TableDataRow row, userRow;

        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        try {
            man = manager.getLots();
            for (int i = 0; i < man.count(); i++ ) {
                data = man.getLotAt(i);
                row = new TableDataRow(9);
                row.cells.get(0).setValue(data.getIsActive());
                row.cells.get(1).setValue(data.getLotNumber());
                row.cells.get(2).setValue(data.getLocationId());
                row.cells.get(3).setValue(data.getPreparedDate());
                row.cells.get(4).setValue(data.getUsableDate());
                row.cells.get(5).setValue(data.getExpireDate());
                row.cells.get(6).setValue(data.getPreparedVolume());
                row.cells.get(7).setValue(data.getPreparedUnitId());
                
                userRow = null;
                if (data.getPreparedById() != null)
                    userRow = new TableDataRow(data.getPreparedById(),data.getPreparedByName());
                
                row.cells.get(8).setValue(userRow);                
                row.data = data;
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
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
    
    protected void clearExceptions() {
        table.clearCellExceptions();
    }
    
    private void validateForDelete(QcLotViewDO data) throws Exception {
        if (data.getId() == null)
            return;
        QcService.get().validateForDelete(data);
    }
    
    private TableDataRow createLotRow(QcLotViewDO data) throws Exception {
        Integer userId;
        String userName;
        Datetime prepDate;
        TableDataRow row;
        
        row = new TableDataRow(9);
        
        row.cells.get(0).setValue("Y");
        
        if (data == null) {
            prepDate = Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE);
            userId = UserCache.getId();
            userName = UserCache.getName();
        } else {
            prepDate = data.getPreparedDate();
            userId = data.getPreparedById();
            userName = data.getPreparedByName();
            
            row.cells.get(1).setValue(data.getLotNumber());
            row.cells.get(2).setValue(data.getLocationId());    
            row.cells.get(4).setValue(data.getUsableDate());
            row.cells.get(5).setValue(data.getExpireDate());
            row.cells.get(6).setValue(data.getPreparedVolume());
            row.cells.get(7).setValue(data.getPreparedUnitId());
        }
        
        row.cells.get(3).setValue(prepDate);
        row.cells.get(8).setValue(new TableDataRow(userId, userName));
        
        return row;
    }
}