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
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.InventoryItemDO;
import org.openelis.domain.QcAnalyteViewDO;
import org.openelis.domain.SecuritySystemUserDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.LocalizedException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.SecurityException;
import org.openelis.gwt.common.SecurityModule;
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
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.QcManager;
import org.openelis.meta.QcMeta;
import org.openelis.meta.CategoryMeta;
import org.openelis.modules.dictionary.client.DictionaryLookupScreen;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.utilcommon.DataBaseUtil;
import org.openelis.utilcommon.ResultRangeNumeric;
import org.openelis.utilcommon.ResultRangeTiter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class QcScreen extends Screen {
    private QcManager                   manager;
    private SecurityModule              security;

    private AppButton                   queryButton, previousButton, nextButton, addButton,
                                        updateButton, commitButton, abortButton, addAnalyteButton,
                                        removeAnalyteButton, dictionaryButton;
    private ButtonGroup                 atoz;
    private ScreenNavigator             nav;
    private CalendarLookUp              preparedDate, usableDate, expireDate;
    private AutoComplete<Integer>       inventoryItem, preparedBy, analyte;
    private Dropdown<Integer>           typeId, preparedUnitId, analyteTypeId;
    private TextBox                     name, source, lotNumber, preparedVolume;
    private CheckBox                    isActive;
    private TableWidget                 qcAnalyteTable;

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

        security = OpenELIS.security.getModule("qc");
        if (security == null)
            throw new SecurityException("screenPermException", "QC Screen");

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
        queryButton = (AppButton)def.getWidget("query");
        addScreenHandler(queryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                query();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                queryButton.enable(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                          .contains(event.getState()) &&
                                   security.hasSelectPermission());
                if (event.getState() == State.QUERY)
                    queryButton.setState(ButtonState.LOCK_PRESSED);
            }
        });

        previousButton = (AppButton)def.getWidget("previous");
        addScreenHandler(previousButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                previous();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                previousButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        nextButton = (AppButton)def.getWidget("next");
        addScreenHandler(nextButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                next();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                nextButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        addButton = (AppButton)def.getWidget("add");
        addScreenHandler(addButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                add();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addButton.enable(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                        .contains(event.getState()) &&
                                 security.hasAddPermission());
                if (event.getState() == State.ADD)
                    addButton.setState(ButtonState.LOCK_PRESSED);
            }
        });

        updateButton = (AppButton)def.getWidget("update");
        addScreenHandler(updateButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                update();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                updateButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()) &&
                                    security.hasUpdatePermission());
                if (event.getState() == State.UPDATE)
                    updateButton.setState(ButtonState.LOCK_PRESSED);
            }
        });

        commitButton = (AppButton)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE, State.DELETE)
                                           .contains(event.getState()));
            }
        });

        abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE, State.DELETE)
                                          .contains(event.getState()));
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
                name.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                   .contains(event.getState()));
                name.setQueryMode(event.getState() == State.QUERY);
            }
        });

        typeId = (Dropdown)def.getWidget(QcMeta.getTypeId());
        addScreenHandler(typeId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                typeId.setSelection(manager.getQc().getTypeId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getQc().setTypeId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                typeId.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                     .contains(event.getState()));
                typeId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        inventoryItem = (AutoComplete)def.getWidget(QcMeta.getInventoryItemName());
        addScreenHandler(inventoryItem, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                inventoryItem.setSelection(manager.getQc().getInventoryItemId(),
                                           manager.getQc().getInventoryItemName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getQc().setInventoryItemId(event.getValue());
                manager.getQc().setInventoryItemName(inventoryItem.getTextBoxDisplay());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                inventoryItem.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                inventoryItem.setQueryMode(event.getState() == State.QUERY);
            }
        });
        inventoryItem.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                DictionaryDO   dict;
                QueryFieldUtil parser;
                ArrayList<InventoryItemDO> list;
                ArrayList<TableDataRow> model;

                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());

                try {
                    list = inventoryService.callList("fetchActiveByName", parser.getParameter().get(0));
                    model = new ArrayList<TableDataRow>();

                    for (InventoryItemDO data : list) {
                        dict = DictionaryCache.getEntryFromId(data.getStoreId());
                        model.add(new TableDataRow(data.getId(), data.getName(), dict.getEntry()));
                    }
                    inventoryItem.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
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
                source.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
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
                lotNumber.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
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
                isActive.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
                isActive.setQueryMode(event.getState() == State.QUERY);
            }
        });

        preparedDate = (CalendarLookUp)def.getWidget(QcMeta.getPreparedDate());
        addScreenHandler(preparedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                preparedDate.setValue(manager.getQc().getPreparedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getQc().setPreparedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                preparedDate.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
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
                preparedVolume.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                             .contains(event.getState()));
                preparedVolume.setQueryMode(event.getState() == State.QUERY);
            }
        });

        preparedUnitId = (Dropdown)def.getWidget(QcMeta.getPreparedUnitId());
        addScreenHandler(preparedUnitId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                preparedUnitId.setSelection(manager.getQc().getPreparedUnitId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getQc().setPreparedUnitId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                preparedUnitId.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                             .contains(event.getState()));
                preparedUnitId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        preparedBy = (AutoComplete)def.getWidget(QcMeta.getPreparedById());
        addScreenHandler(preparedBy, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                preparedBy.setSelection(manager.getQc().getPreparedById(),
                                        manager.getQc().getPreparedByName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getQc().setPreparedById(event.getValue());
                manager.getQc().setPreparedByName(preparedBy.getTextBoxDisplay());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                preparedBy.enable(EnumSet.of(State.ADD, State.UPDATE)
                                         .contains(event.getState()));
                preparedBy.setQueryMode(event.getState() == State.QUERY);
            }
        });

        preparedBy.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                ArrayList<SecuritySystemUserDO> users;
                ArrayList<TableDataRow> model;

                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());

                try {
                    users = userService.callList("fetchByLogin", parser.getParameter().get(0));
                    model = new ArrayList<TableDataRow>();
                    for (SecuritySystemUserDO user : users)
                        model.add(new TableDataRow(user.getId(), user.getLoginName()));
                    preparedBy.showAutoMatches(model);
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert(e.toString());
                }
            }
        });

        usableDate = (CalendarLookUp)def.getWidget(QcMeta.getUsableDate());
        addScreenHandler(usableDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                usableDate.setValue(manager.getQc().getUsableDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getQc().setUsableDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                usableDate.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                         .contains(event.getState()));
                usableDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        expireDate = (CalendarLookUp)def.getWidget(QcMeta.getExpireDate());
        addScreenHandler(expireDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                expireDate.setValue(manager.getQc().getExpireDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getQc().setExpireDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                expireDate.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                         .contains(event.getState()));
                expireDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        qcAnalyteTable = (TableWidget)def.getWidget("QcAnalyteTable");
        analyte = (AutoComplete<Integer>)qcAnalyteTable.getColumnWidget(QcMeta.getQcAnalyteAnalyteName());
        analyteTypeId = (Dropdown)qcAnalyteTable.getColumnWidget(QcMeta.getQcAnalyteTypeId());
        addScreenHandler(qcAnalyteTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                // table is not queried,so it needs to be cleared in query mode
                qcAnalyteTable.load(getAnalyteTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                qcAnalyteTable.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));                
            }
        });

        rangeNumeric = new ResultRangeNumeric();
        rangeTiter   = new ResultRangeTiter();
        
        qcAnalyteTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                TableDataRow row;
                QcAnalyteViewDO data;

                r = event.getRow();
                c = event.getCol();
                val = qcAnalyteTable.getObject(r, c);
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
                        qcAnalyteTable.clearCellExceptions(r, 3);
                        try {
                            validateValue(data, (String)qcAnalyteTable.getObject(r, 3));
                        } catch (LocalizedException e) {
                            qcAnalyteTable.setCellException(r, 3, e);
                        }
                        break;
                    case 2:
                        data.setIsTrendable((String)val);
                        break;
                    case 3:
                        qcAnalyteTable.clearCellExceptions(r, c);
                        try {
                            validateValue(data, (String)val);
                        } catch (LocalizedException e) {
                            qcAnalyteTable.setCellException(r, c, e);
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
                    Window.alert(e.getMessage());
                }
            }
        });

        qcAnalyteTable.addRowDeletedHandler(new RowDeletedHandler() {
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
                QueryFieldUtil parser;
                IdNameVO data;
                ArrayList<IdNameVO> list;
                ArrayList<TableDataRow> model;

                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());

                try {
                    list = analyteService.callList("fetchByName", parser.getParameter().get(0));
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
                int r, n;

                r = qcAnalyteTable.getSelectedRow() + 1;
                if (r == 0) {
                    r = qcAnalyteTable.numRows();
                }
                qcAnalyteTable.addRow(r);
                qcAnalyteTable.selectRow(r);
                qcAnalyteTable.scrollToSelection();
                qcAnalyteTable.startEditing(r, 0);
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

                r = qcAnalyteTable.getSelectedRow();
                if (r > -1 && qcAnalyteTable.numRows() > 0)
                    qcAnalyteTable.deleteRow(r);
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
                            window.setError("No more records in this direction");
                        } else {
                            Window.alert("Error: QC call query failed; " + error.getMessage());
                            window.setError(consts.get("queryFailed"));
                        }
                    }
                });
            }

            public boolean fetch(RPC entry) {
                return fetchById( (entry == null) ? null : ((IdNameVO)entry).getId());
            }

            public ArrayList<TableDataRow> getModel() {
                ArrayList<IdNameVO> result;
                ArrayList<TableDataRow> model;

                model = null;
                result = nav.getQueryResult();
                if (result != null) {
                    model = new ArrayList<TableDataRow>();
                    for (IdNameVO entry : result)
                        model.add(new TableDataRow(entry.getId(), entry.getName(),
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
                         security.hasSelectPermission();
                atoz.enable(enable);
                nav.enable(enable);
            }

            public void onClick(ClickEvent event) {
                Query query;
                QueryData field;

                field = new QueryData();
                field.key = QcMeta.getName();
                field.query = ((AppButton)event.getSource()).getAction();
                field.type = QueryData.Type.STRING;

                query = new Query();
                query.setFields(field);
                nav.setQuery(query);
            }
        });
        
        window.addBeforeClosedHandler(new BeforeCloseHandler<ScreenWindow>() {
            public void onBeforeClosed(BeforeCloseEvent<ScreenWindow> event) {                
                if (EnumSet.of(State.ADD, State.UPDATE).contains(state)) {
                    event.cancel();
                    window.setError(consts.get("mustCommitOrAbort"));
                }
            }
        });
    }

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;

        // type dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("qc_type"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));

        typeId.setModel(model);

        // prepareUnit dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("unit_of_measure"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));

        preparedUnitId.setModel(model);

        // analyte table type dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("qc_analyte_type"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));

        analyteTypeId.setModel(model);

        try {
            typeDict    = DictionaryCache.getIdFromSystemName("qc_analyte_dictionary");
            typeNumeric = DictionaryCache.getIdFromSystemName("qc_analyte_numeric");
            typeTiter   = DictionaryCache.getIdFromSystemName("qc_analyte_titer");
            typeDefault = DictionaryCache.getIdFromSystemName("qc_analyte_default");
        } catch (Exception e) {
            Window.alert(e.getMessage());
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
            Window.alert(e.getMessage());
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
                Window.alert("commitAdd(): " + e.getMessage());
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
                Window.alert("commitUpdate(): " + e.getMessage());
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
                Window.alert(e.getMessage());
                fetchById(null);
            }
            window.setDone(consts.get("updateAborted"));
        } else {
            window.clearStatus();
        }
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
                Window.alert(consts.get("fetchFailed") + e.getMessage());
                return false;
            }
        }
        DataChangeEvent.fire(this);
        window.clearStatus();

        return true;
    }

    private ArrayList<TableDataRow> getAnalyteTableModel() {
        int i;
        QcAnalyteViewDO data;
        ArrayList<TableDataRow> model;
        String value;

        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.getAnalytes().count(); i++ ) {
                data = manager.getAnalytes().getAnalyteAt(i);
                // either show them the value or the dictionary entry
                value = data.getValue();
                if (data.getDictionary() != null)
                    value = data.getDictionary();
                model.add(new TableDataRow(null, 
                                           new TableDataRow(data.getAnalyteId(),data.getAnalyteName()),
                                           data.getTypeId(), data.getIsTrendable(), value));
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
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
        field = new QueryData();
        field.key = CategoryMeta.getDictionaryEntry();
        field.type = QueryData.Type.STRING;
        field.query = entry;
        query.setFields(field);       
        
        field = new QueryData();
        field.key = CategoryMeta.getIsSystem();
        field.type = QueryData.Type.STRING;
        field.query = "N";
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
            Window.alert(e.getMessage());
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
                                qcAnalyteTable.setCell(r, 1, typeDict);
                                qcAnalyteTable.setCell(r, 3, data.getDictionary());
                                qcAnalyteTable.clearCellExceptions(r, 3);
                            } catch (Exception e) {
                                e.printStackTrace();
                                qcAnalyteTable.setCell(r, 3, "");
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
}