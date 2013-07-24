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
package org.openelis.modules.qc.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.AnalyteDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.InventoryItemDO;
import org.openelis.domain.QcAnalyteViewDO;
import org.openelis.domain.QcViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.LocalizedException;
import org.openelis.gwt.common.ModulePermission;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.PermissionException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.SystemUserVO;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.BeforeCloseEvent;
import org.openelis.gwt.event.BeforeCloseHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.screen.Screen.State;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.calendar.Calendar;
import org.openelis.gwt.widget.AutoCompleteValue;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.ModalWindow;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.QcAnalyteManager;
import org.openelis.manager.QcManager;
import org.openelis.meta.CategoryMeta;
import org.openelis.meta.QcMeta;
import org.openelis.modules.dictionary.client.DictionaryLookupScreen;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.utilcommon.ResultRangeNumeric;
import org.openelis.utilcommon.ResultRangeTiter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class QcScreen extends Screen {
    private QcManager                   manager;
    private ModulePermission            userPermission;

    private Button                      queryButton, previousButton, nextButton, addButton,
                                        updateButton, commitButton, abortButton, addAnalyteButton,
                                        removeAnalyteButton, dictionaryButton;
    protected MenuItem                  duplicate,qcHistory, qcAnalyteHistory;
    private ButtonGroup                 atoz;
    private ScreenNavigator             nav;
    private Calendar                    preparedDate, usableDate, expireDate;
    private AutoComplete                inventoryItem, preparedBy, analyte;
    private Dropdown<Integer>           typeId, preparedUnitId, analyteTypeId;
    private TextBox                     name, source, lotNumber, preparedVolume;
    private CheckBox                    isActive;
    private Table                       qcAnalyteTable;

    private Integer                     typeDict, typeNumeric, typeTiter, typeDefault;
    private DictionaryLookupScreen      dictLookup;
    private ScreenService               analyteService, inventoryService, userService,
                                        dictionaryService;
    private ResultRangeNumeric          rangeNumeric;
    private ResultRangeTiter            rangeTiter;

    public QcScreen() throws Exception {
        super((ScreenDefInt)GWT.create(QcDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.qc.server.QcService");
        userService = new ScreenService("controller?service=org.openelis.server.SystemUserService");
        analyteService = new ScreenService("controller?service=org.openelis.modules.analyte.server.AnalyteService");
        inventoryService = new ScreenService("controller?service=org.openelis.modules.inventoryItem.server.InventoryItemService");
        dictionaryService = new ScreenService("controller?service=org.openelis.modules.dictionary.server.DictionaryService");

        userPermission = OpenELIS.getSystemUserPermission().getModule("qc");
        if (userPermission == null)
            throw new PermissionException("screenPermException", "QC Screen");

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }

    /**
     * This method is called to set the initial state of widgets after the
     * screen is attached to the browser. It is usually called in deferred
     * command.
     */
    private void postConstructor() {
        manager = QcManager.getInstance();

        initialize();
        setState(State.DEFAULT);
        initializeDropdowns();
        DataChangeEvent.fire(this);
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    @SuppressWarnings("unchecked")
    private void initialize() {
        //
        // button panel buttons
        //
        queryButton = (Button)def.getWidget("query");
        addScreenHandler(queryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                query();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                queryButton.setEnabled(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                          .contains(event.getState()) &&
                                   userPermission.hasSelectPermission());
                if (event.getState() == State.QUERY) {
                    queryButton.setPressed(true);
                    queryButton.lock();
                }
            }
        });

        previousButton = (Button)def.getWidget("previous");
        addScreenHandler(previousButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                previous();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                previousButton.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        nextButton = (Button)def.getWidget("next");
        addScreenHandler(nextButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                next();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                nextButton.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        addButton = (Button)def.getWidget("add");
        addScreenHandler(addButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                add();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addButton.setEnabled(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                        .contains(event.getState()) &&
                                 userPermission.hasAddPermission());
                if (event.getState() == State.ADD) {
                    addButton.setPressed(true);
                    addButton.lock();
                }
            }
        });

        updateButton = (Button)def.getWidget("update");
        addScreenHandler(updateButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                update();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                updateButton.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()) &&
                                    userPermission.hasUpdatePermission());
                if (event.getState() == State.UPDATE) {
                    updateButton.setPressed(true);
                    updateButton.lock();
                }
            }
        });

        commitButton = (Button)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE, State.DELETE)
                                           .contains(event.getState()));
            }
        });

        abortButton = (Button)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE, State.DELETE)
                                          .contains(event.getState()));
            }
        });
        
        duplicate = (MenuItem)def.getWidget("duplicateRecord");
        addScreenHandler(duplicate, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                duplicate.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        duplicate.addCommand(new Command() {
			public void execute() {
				duplicate();
			}
		});
        
        qcHistory = (MenuItem)def.getWidget("qcHistory");
        addScreenHandler(qcHistory, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                qcHistory.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        qcHistory.addCommand(new Command() {
			public void execute() {
				qcHistory();
			}
		});
        
        qcAnalyteHistory = (MenuItem)def.getWidget("qcAnalyteHistory");
        addScreenHandler(qcAnalyteHistory, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                qcAnalyteHistory.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        qcAnalyteHistory.addCommand(new Command() {
			public void execute() {
				qcAnalyteHistory();
			}
		});

        //
        // screen fields
        //
        name = (TextBox)def.getWidget(QcMeta.getName());
        addScreenHandler(name, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                name.setValue(manager.getQc().getName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getQc().setName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                name.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                   .contains(event.getState()));
                name.setQueryMode(event.getState() == State.QUERY);
            }
        });

        typeId = (Dropdown<Integer>)def.getWidget(QcMeta.getTypeId());
        addScreenHandler(typeId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                typeId.setValue(manager.getQc().getTypeId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getQc().setTypeId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                typeId.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                     .contains(event.getState()));
                typeId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        inventoryItem = (AutoComplete)def.getWidget(QcMeta.getInventoryItemName());
        addScreenHandler(inventoryItem, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                inventoryItem.setValue(manager.getQc().getInventoryItemId(),
                                           manager.getQc().getInventoryItemName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getQc().setInventoryItemId(event.getValue());
                manager.getQc().setInventoryItemName(inventoryItem.getDisplay());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                inventoryItem.setEnabled(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                inventoryItem.setQueryMode(event.getState() == State.QUERY);
            }
        });
        inventoryItem.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                DictionaryDO   dict;
                QueryFieldUtil parser;
                ArrayList<InventoryItemDO> list;
                ArrayList<Item<Integer>> model;
                String param = "";

                parser = new QueryFieldUtil();

                try {
                	if(!event.getMatch().equals("")) {
                		parser.parse(event.getMatch());
                		param = parser.getParameter().get(0);
                	}
                    list = inventoryService.callList("fetchActiveByName", param);
                    model = new ArrayList<Item<Integer>>();

                    for (InventoryItemDO data : list) {
                        dict = DictionaryCache.getEntryFromId(data.getStoreId());
                        model.add(new Item<Integer>(data.getId(), data.getName(), dict.getEntry()));
                    }
                    inventoryItem.showAutoMatches(model);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });

        source = (TextBox)def.getWidget(QcMeta.getSource());
        addScreenHandler(source, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                source.setValue(manager.getQc().getSource());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getQc().setSource(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                source.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                     .contains(event.getState()));
                source.setQueryMode(event.getState() == State.QUERY);
            }
        });

        lotNumber = (TextBox)def.getWidget(QcMeta.getLotNumber());
        addScreenHandler(lotNumber, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                lotNumber.setValue(manager.getQc().getLotNumber());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getQc().setLotNumber(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                lotNumber.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                        .contains(event.getState()));
                lotNumber.setQueryMode(event.getState() == State.QUERY);
            }
        });

        isActive = (CheckBox)def.getWidget(QcMeta.getIsActive());
        addScreenHandler(isActive, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isActive.setValue(manager.getQc().getIsActive());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getQc().setIsActive(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isActive.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
                isActive.setQueryMode(event.getState() == State.QUERY);
            }
        });

        preparedDate = (Calendar)def.getWidget(QcMeta.getPreparedDate());
        addScreenHandler(preparedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                preparedDate.setValue(manager.getQc().getPreparedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getQc().setPreparedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                preparedDate.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                           .contains(event.getState()));
                preparedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        preparedVolume = (TextBox)def.getWidget(QcMeta.getPreparedVolume());
        addScreenHandler(preparedVolume, new ScreenEventHandler<Double>() {
            public void onDataChange(DataChangeEvent event) {
                preparedVolume.setValue(manager.getQc().getPreparedVolume());
            }

            public void onValueChange(ValueChangeEvent<Double> event) {
                manager.getQc().setPreparedVolume(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                preparedVolume.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                             .contains(event.getState()));
                preparedVolume.setQueryMode(event.getState() == State.QUERY);
            }
        });

        preparedUnitId = (Dropdown<Integer>)def.getWidget(QcMeta.getPreparedUnitId());
        addScreenHandler(preparedUnitId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                preparedUnitId.setValue(manager.getQc().getPreparedUnitId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getQc().setPreparedUnitId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                preparedUnitId.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                             .contains(event.getState()));
                preparedUnitId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        preparedBy = (AutoComplete)def.getWidget(QcMeta.getPreparedById());
        addScreenHandler(preparedBy, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                preparedBy.setValue(manager.getQc().getPreparedById(),
                                        manager.getQc().getPreparedByName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getQc().setPreparedById(event.getValue());
                manager.getQc().setPreparedByName(preparedBy.getDisplay());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                preparedBy.setEnabled(EnumSet.of(State.ADD, State.UPDATE)
                                         .contains(event.getState()));
                preparedBy.setQueryMode(event.getState() == State.QUERY);
            }
        });

        preparedBy.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                ArrayList<SystemUserVO> users;
                ArrayList<Item<Integer>> model;
                String param = "";

                parser = new QueryFieldUtil();

                try {
                	if(!event.getMatch().equals("")) {
                		parser.parse(event.getMatch());
                		param = parser.getParameter().get(0);
                	}
                    users = userService.callList("fetchByLoginName", param);
                    model = new ArrayList<Item<Integer>>();
                    for (SystemUserVO user : users)
                        model.add(new Item<Integer>(user.getId(), user.getLoginName()));
                    preparedBy.showAutoMatches(model);
                } catch (Exception e) {
                    e.printStackTrace();
                    com.google.gwt.user.client.Window.alert(e.toString());
                }
            }
        });

        usableDate = (Calendar)def.getWidget(QcMeta.getUsableDate());
        addScreenHandler(usableDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                usableDate.setValue(manager.getQc().getUsableDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getQc().setUsableDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                usableDate.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                         .contains(event.getState()));
                usableDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        expireDate = (Calendar)def.getWidget(QcMeta.getExpireDate());
        addScreenHandler(expireDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                expireDate.setValue(manager.getQc().getExpireDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getQc().setExpireDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                expireDate.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                         .contains(event.getState()));
                expireDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        qcAnalyteTable = (Table)def.getWidget("QcAnalyteTable");
        analyte = (AutoComplete)qcAnalyteTable.getColumnAt(qcAnalyteTable.getColumnByName(QcMeta.getQcAnalyteAnalyteName())).getCellEditor().getWidget();
        analyteTypeId = (Dropdown)qcAnalyteTable.getColumnAt(qcAnalyteTable.getColumnByName(QcMeta.getQcAnalyteTypeId())).getCellEditor().getWidget();
        addScreenHandler(qcAnalyteTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                // table is not queried,so it needs to be cleared in query mode
                qcAnalyteTable.setModel(getAnalyteTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                qcAnalyteTable.setEnabled(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));                
            }
        });

        rangeNumeric = new ResultRangeNumeric();
        rangeTiter   = new ResultRangeTiter();
        
        qcAnalyteTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                AutoCompleteValue av;
                QcAnalyteViewDO data;

                r = event.getRow();
                c = event.getCol();
                val = qcAnalyteTable.getValueAt(r, c);
                try {
                    data = manager.getAnalytes().getAnalyteAt(r);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                    return;
                }
                switch (c) {
                    case 0:
                        av = (AutoCompleteValue)val;
                        data.setAnalyteId((Integer)av.getId());
                        data.setAnalyteName(analyte.getDisplay());
                        break;
                    case 1:
                        data.setTypeId((Integer)val);
                        qcAnalyteTable.clearExceptions(r, 3);
                        try {
                            validateValue(data, (String)qcAnalyteTable.getValueAt(r, 3));
                        } catch (LocalizedException e) {
                            qcAnalyteTable.addException(r, 3, e);
                        }
                        break;
                    case 2:
                        data.setIsTrendable((String)val);
                        break;
                    case 3:
                        qcAnalyteTable.clearExceptions(r, c);
                        try {
                            validateValue(data, (String)val);
                        } catch (LocalizedException e) {
                            qcAnalyteTable.addException(r, c, e);
                        }
                        break;
                }
            }
        });

        qcAnalyteTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                int r;
                
                r = event.getIndex();
                try {
                    manager.getAnalytes().addAnalyteAt(new QcAnalyteViewDO(), r);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });

        qcAnalyteTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getAnalytes().removeAnalyteAt(event.getIndex());
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });

        analyte.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                AnalyteDO data;
                ArrayList<AnalyteDO> list;
                ArrayList<Item<Integer>> model;
                String param = "";

                parser = new QueryFieldUtil();

                try {
                	if(!event.getMatch().equals("")) {
                		parser.parse(event.getMatch());
                		param = parser.getParameter().get(0);
                	}
                    list = analyteService.callList("fetchByName", param);
                    model = new ArrayList<Item<Integer>>();

                    for (int i = 0; i < list.size(); i++ ) {
                        data = list.get(i);
                        model.add(new Item<Integer>(data.getId(), data.getName()));
                    }
                    analyte.showAutoMatches(model);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });

        addAnalyteButton = (Button)def.getWidget("addAnalyteButton");
        addScreenHandler(addAnalyteButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = qcAnalyteTable.getSelectedRow() + 1;
                if (r == 0) {
                    r = qcAnalyteTable.getRowCount();
                }
                qcAnalyteTable.addRowAt(r);
                qcAnalyteTable.selectRowAt(r);
                qcAnalyteTable.scrollToVisible(r);
                qcAnalyteTable.startEditing(r, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addAnalyteButton.setEnabled(EnumSet.of(State.ADD, State.UPDATE)
                                               .contains(event.getState()));
            }

        });

        removeAnalyteButton = (Button)def.getWidget("removeAnalyteButton");
        addScreenHandler(removeAnalyteButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = qcAnalyteTable.getSelectedRow();
                if (r > -1 && qcAnalyteTable.getRowCount() > 0)
                    qcAnalyteTable.removeRowAt(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeAnalyteButton.setEnabled(EnumSet.of(State.ADD, State.UPDATE)
                                                  .contains(event.getState()));
            }
        });

        dictionaryButton = (Button)def.getWidget("dictionaryButton");
        addScreenHandler(dictionaryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                showDictionary(null,null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                dictionaryButton.setEnabled(EnumSet.of(State.ADD, State.UPDATE)
                                               .contains(event.getState()));
            }
        });

        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator(def) {
            public void executeQuery(final Query query) {
                window.setBusy(consts.get("querying"));

                service.callList("query", query, new AsyncCallback<ArrayList<IdNameVO>>() {
                    public void onSuccess(ArrayList<IdNameVO> result) {
                        setQueryResult(result);
                    }

                    public void onFailure(Throwable error) {
                        setQueryResult(null);
                        if (error instanceof NotFoundException) {
                            window.setDone(consts.get("noRecordsFound"));
                            setState(State.DEFAULT);
                        } else if (error instanceof LastPageException) {
                            window.setError(consts.get("noMoreRecordInDir"));
                        } else {
                            com.google.gwt.user.client.Window.alert("Error: QC call query failed; " + error.getMessage());
                            window.setError(consts.get("queryFailed"));
                        }
                    }
                });
            }

            public boolean fetch(RPC entry) {
                return fetchById( (entry == null) ? null : ((IdNameVO)entry).getId());
            }

            public ArrayList<Item<Integer>> getModel() {
                ArrayList<IdNameVO> result;
                ArrayList<Item<Integer>> model;

                model = null;
                result = nav.getQueryResult();
                if (result != null) {
                    model = new ArrayList<Item<Integer>>();
                    for (IdNameVO entry : result)
                        model.add(new Item<Integer>(entry.getId(), entry.getName(),
                                                   entry.getDescription()));
                }
                return model;
            }
        };

        atoz = (ButtonGroup)def.getWidget("atozButtons");
        addScreenHandler(atoz, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                boolean enable;
                enable = EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState()) &&
                         userPermission.hasSelectPermission();
                atoz.setEnabled(enable);
                nav.enable(enable);
            }

            public void onClick(ClickEvent event) {
                Query query;
                QueryData field;

                field = new QueryData(QcMeta.getName(),QueryData.Type.STRING,((Button)event.getSource()).getAction());

                query = new Query();
                query.setFields(field);
                nav.setQuery(query);
            }
        });
        
        window.addBeforeClosedHandler(new BeforeCloseHandler<Window>() {
            public void onBeforeClosed(BeforeCloseEvent<Window> event) {                
                if (EnumSet.of(State.ADD, State.UPDATE).contains(state)) {
                    event.cancel();
                    window.setError(consts.get("mustCommitOrAbort"));
                }
            }
        });
    }

    private void initializeDropdowns() {
        ArrayList<Item<Integer>> model;
        ArrayList<DictionaryDO> list;
        Item<Integer> row;

        // type dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list =  DictionaryCache.getListByCategorySystemName("qc_type");
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        typeId.setModel(model);

        // prepareUnit dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = DictionaryCache.getListByCategorySystemName("unit_of_measure");
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        preparedUnitId.setModel(model);

        // analyte table type dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = DictionaryCache.getListByCategorySystemName("qc_analyte_type");
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        analyteTypeId.setModel(model);

        try {
            typeDict    = DictionaryCache.getIdFromSystemName("qc_analyte_dictionary");
            typeNumeric = DictionaryCache.getIdFromSystemName("qc_analyte_numeric");
            typeTiter   = DictionaryCache.getIdFromSystemName("qc_analyte_titer");
            typeDefault = DictionaryCache.getIdFromSystemName("qc_analyte_default");
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            window.close();
        }
    }

    /*
     * basic button methods
     */

    protected void query() {
        manager = QcManager.getInstance();

        setState(State.QUERY);
        DataChangeEvent.fire(this);
        
        setFocus(name);
        window.setDone(consts.get("enterFieldsToQuery"));
    }

    protected void previous() {
        nav.previous();
    }

    protected void next() {
        nav.next();
    }

    protected void add() {
        manager = QcManager.getInstance();
        manager.getQc().setIsActive("Y");

        setState(State.ADD);
        DataChangeEvent.fire(this);

        setFocus(name);
        window.setDone(consts.get("enterInformationPressCommit"));
    }

    protected void update() {
        window.setBusy(consts.get("lockForUpdate"));

        try {
            manager = manager.fetchForUpdate();

            setState(State.UPDATE);
            DataChangeEvent.fire(this);
            setFocus(name);
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
        }
        window.clearStatus();
    }

    public void commit() {
        setFocus(null);

        if ( !validate()) {
            window.setError(consts.get("correctErrors"));
            return;
        }

        if (state == State.QUERY) {
            Query query;

            query = new Query();
            query.setFields(getQueryFields());
            nav.setQuery(query);
        } else if (state == State.ADD) {
            window.setBusy(consts.get("adding"));
            try {
                manager = manager.add();

                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(consts.get("addingComplete"));
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                com.google.gwt.user.client.Window.alert("commitAdd(): " + e.getMessage());
                window.clearStatus();
            }
        } else if (state == State.UPDATE) {
            window.setBusy(consts.get("updating"));
            try {
                manager = manager.update();

                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(consts.get("updatingComplete"));
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                com.google.gwt.user.client.Window.alert("commitUpdate(): " + e.getMessage());
                window.clearStatus();
            }
        }
    }

    protected void abort() {
        setFocus(null);
        clearErrors();
        window.setBusy(consts.get("cancelChanges"));

        if (state == State.QUERY) {
            fetchById(null);
            window.setDone(consts.get("queryAborted"));
        } else if (state == State.ADD) {
            fetchById(null);
            window.setDone(consts.get("addAborted"));
        } else if (state == State.UPDATE) {
            try {
                manager = manager.abortUpdate();
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
            } catch (Exception e) {
                com.google.gwt.user.client.Window.alert(e.getMessage());
                fetchById(null);
            }
            window.setDone(consts.get("updateAborted"));
        } else {
            window.clearStatus();
        }
    }
    
    protected void duplicate() {
        QcViewDO data;
        try {
            manager = QcManager.fetchById(manager.getQc().getId());            
            setState(State.ADD);
            DataChangeEvent.fire(this);
            data = manager.getQc();
            data.setId(null);
            setFocus(name);
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
        }
    }
    
    private void qcHistory() {
        IdNameVO hist;
        
        hist = new IdNameVO(manager.getQc().getId(), manager.getQc().getName());
        HistoryScreen.showHistory(consts.get("qcHistory"),
                                  ReferenceTable.QC, hist); 
    }
    
    private void qcAnalyteHistory() {
        int i, count;
        IdNameVO refVoList[];
        QcAnalyteManager man;
        QcAnalyteViewDO data;

        try {
            man = manager.getAnalytes();
            count = man.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = man.getAnalyteAt(i);                
                refVoList[i] = new IdNameVO(data.getId(), data.getAnalyteName());                
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(consts.get("qcAnalyteHistory"),
                                  ReferenceTable.QC_ANALYTE, refVoList);
    }

    protected boolean fetchById(Integer id) {
        if (id == null) {
            manager = QcManager.getInstance();
            setState(State.DEFAULT);
        } else {
            window.setBusy(consts.get("fetching"));
            try {
                manager = QcManager.fetchWithAnalytes(id);
                setState(State.DISPLAY);
            } catch (NotFoundException e) {
                fetchById(null);
                window.setDone(consts.get("noRecordsFound"));
                return false;
            } catch (Exception e) {
                fetchById(null);
                e.printStackTrace();
                com.google.gwt.user.client.Window.alert(consts.get("fetchFailed") + e.getMessage());
                return false;
            }
        }
        DataChangeEvent.fire(this);
        window.clearStatus();

        return true;
    }

    private ArrayList<Row> getAnalyteTableModel() {
        int i;
        QcAnalyteViewDO data;
        ArrayList<Row> model;
        String value;

        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.getAnalytes().count(); i++ ) {
                data = manager.getAnalytes().getAnalyteAt(i);
                // either show them the value or the dictionary entry
                value = data.getValue();
                if (data.getDictionary() != null)
                    value = data.getDictionary();
                model.add(new Row(new AutoCompleteValue(data.getAnalyteId(),data.getAnalyteName()),
                                           data.getTypeId(), data.getIsTrendable(), value));
            }
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }

    private IdNameVO getDictionary(String entry) {
        ArrayList<IdNameVO> list;
        Query query;  
        QueryData field;
        
        entry = DataBaseUtil.trim(entry); 
        if (entry == null)
            return null;
        
        query = new Query();
        field = new QueryData(CategoryMeta.getDictionaryEntry(),QueryData.Type.STRING,entry);
        query.setFields(field);       
        
        field = new QueryData(CategoryMeta.getIsSystem(),QueryData.Type.STRING,"N");
        query.setFields(field); 
        
        try {
            list = dictionaryService.callList("fetchByEntry", query);
            if (list.size() == 1)
                return list.get(0);
            else if (list.size() > 1)                
                showDictionary(entry,list);
        } catch(NotFoundException e){
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert(e.getMessage());
        }
        return null;
    }
    
    private void validateValue(QcAnalyteViewDO data, String value) throws LocalizedException {
        IdNameVO dict;

        try {
            if (typeDict.equals(data.getTypeId())) {
                dict = getDictionary((String)value);
                if (dict != null) {
                    data.setValue(dict.getId().toString());
                    data.setDictionary(dict.getName());
                } else {
                    data.setDictionary(null);
                    throw new LocalizedException("qc.invalidValueException");
                }
            } else if (typeNumeric.equals(data.getTypeId())) {
                rangeNumeric.setRange((String)value);
                data.setValue(rangeNumeric.toString());
            } else if (typeTiter.equals(data.getTypeId())) {
                rangeTiter.setRange((String)value);
                data.setValue(rangeTiter.toString());
            } else if (typeDefault.equals(data.getTypeId())) {
                data.setValue((String)value);
            } else {
                throw new LocalizedException("qc.invalidValueException");
            }
        } catch (LocalizedException e) {
            data.setValue(null);
            data.setDictionary(null);
            throw e;
        }
    }

    private void showDictionary(String entry,ArrayList<IdNameVO> list) {
        ModalWindow modal;

        if (dictLookup == null) {
            try {
                dictLookup = new DictionaryLookupScreen();
            } catch (Exception e) {
                e.printStackTrace();
                com.google.gwt.user.client.Window.alert("DictionaryLookup Error: " + e.getMessage());
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
                            r = qcAnalyteTable.getSelectedRow();
                            if (r == -1) {
                                window.setError(consts.get("qc.noSelectedRow"));
                                return;
                            }
                            entry = list.get(0);
                            try {
                                data = manager.getAnalytes().getAnalyteAt(r);
                                data.setValue(entry.getId().toString());
                                data.setDictionary(entry.getName());
                                data.setTypeId(typeDict);
                                qcAnalyteTable.setValueAt(r, 1, typeDict);
                                qcAnalyteTable.setValueAt(r, 3, data.getDictionary());
                                qcAnalyteTable.clearExceptions(r, 3);
                            } catch (Exception e) {
                                e.printStackTrace();
                                qcAnalyteTable.setValueAt(r, 3, "");
                                com.google.gwt.user.client.Window.alert("DictionaryLookup Error: " + e.getMessage());
                            }
                        }
                    }
                }
            });
        }
        modal = new ModalWindow();
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
}