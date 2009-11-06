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
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
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
import org.openelis.meta.InventoryItemMeta;
import org.openelis.metamap.QcMetaMap;
import org.openelis.modules.dictionary.client.DictionaryEntryLookupScreen;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.utilcommon.DataBaseUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class QcScreen extends Screen {
    private QcManager                   manager;
    private QcMetaMap                   meta    = new QcMetaMap();
    private InventoryItemMeta           invMeta = meta.getInventoryItem();
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
    private CheckBox                    isSingleUse;

    private DictionaryEntryLookupScreen dictEntryPicker;

    private QcScreen                    screen;

    private TableWidget                 qcAnalyteTable;

    private ScreenService               analyteService, inventoryService, userService,
                                        dictionaryService;

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

        // Setup link between Screen and widget Handlers
        initialize();

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

        setState(State.DEFAULT);

        initializeDropdowns();
        DataChangeEvent.fire(this);
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    @SuppressWarnings("unchecked")
    private void initialize() {
        screen = this;

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
        name = (TextBox)def.getWidget(meta.getName());
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
                if (EnumSet.of(State.QUERY, State.ADD, State.UPDATE).contains(event.getState()))
                    name.setFocus(true);
            }
        });

        typeId = (Dropdown)def.getWidget(meta.getTypeId());
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

        inventoryItem = (AutoComplete)def.getWidget(invMeta.getName());
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
                // TODO enable this code after inventory rewrite
                inventoryItem.enable(false);
                // inventoryItem.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                // inventoryItem.setQueryMode(event.getState() == State.QUERY);
            }
        });
        inventoryItem.addGetMatchesHandler(new GetMatchesHandler() {
            // TODO after rewrite of the inventoryItem, check this code
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                ArrayList<IdNameVO> list;
                ArrayList<TableDataRow> model;

                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());

                try {
                    list = inventoryService.callList("fetchByName", parser.getParameter().get(0));
                    model = new ArrayList<TableDataRow>();

                    for (IdNameVO data : list)
                        model.add(new TableDataRow(data.getId(), data.getName()));
                    inventoryItem.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        source = (TextBox)def.getWidget(meta.getSource());
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

        lotNumber = (TextBox)def.getWidget(meta.getLotNumber());
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

        isSingleUse = (CheckBox)def.getWidget(meta.getIsSingleUse());
        addScreenHandler(isSingleUse, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isSingleUse.setValue(manager.getQc().getIsSingleUse());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getQc().setIsSingleUse(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isSingleUse.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
                isSingleUse.setQueryMode(event.getState() == State.QUERY);
            }
        });

        preparedDate = (CalendarLookUp)def.getWidget(meta.getPreparedDate());
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

        preparedVolume = (TextBox)def.getWidget(meta.getPreparedVolume());
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

        preparedUnitId = (Dropdown)def.getWidget(meta.getPreparedUnitId());
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

        preparedBy = (AutoComplete)def.getWidget(meta.getPreparedById());
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
                preparedBy.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
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

        usableDate = (CalendarLookUp)def.getWidget(meta.getUsableDate());
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

        expireDate = (CalendarLookUp)def.getWidget(meta.getExpireDate());
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
        analyte = (AutoComplete<Integer>)qcAnalyteTable.getColumnWidget(meta.QC_ANALYTE.getAnalyte()
                                                                                       .getName());
        analyteTypeId = (Dropdown)qcAnalyteTable.getColumnWidget(meta.QC_ANALYTE.getTypeId());
        addScreenHandler(qcAnalyteTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                //
                //this table is not queried by,so it needs to be cleared in query mode
                //
                qcAnalyteTable.load(getAnalyteTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                qcAnalyteTable.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));                
            }
        });
        
        qcAnalyteTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                QcAnalyteViewDO data;
                Integer typeId;
                TableDataRow trow, row;
                String sysName, value;

                r = event.getRow();
                c = event.getCol();
                val = qcAnalyteTable.getObject(r, c);
                sysName = null;
                value = null;

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
                        trow = qcAnalyteTable.getRow(r);
                        typeId = (Integer)trow.cells.get(1).getValue();

                        if (typeId != null)
                            sysName = DictionaryCache.getSystemNameFromId(typeId);

                        if ("qc_analyte_numeric".equals(sysName)) {
                            value = validateAndSetNumericValue((String)val, r);
                        } else if ("qc_analyte_titer".equals(sysName)) {
                            value = validateAndSetTiterValue((String)val, r);
                        } else if ("qc_analyte_dictionary".equals(sysName)) {
                            value = validateAndSetDictValue((String)val, r, data);
                            if (value == null)
                                data.setDictionary("");
                            else
                                data.setDictionary((String)val);
                        }
                        data.setValue(value);
                        break;
                }
            }
        });

        qcAnalyteTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                try {
                    manager.getAnalytes().addAnalyte(new QcAnalyteViewDO());
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
                int n;

                qcAnalyteTable.addRow();
                n = qcAnalyteTable.numRows() - 1;
                qcAnalyteTable.selectRow(n);
                qcAnalyteTable.scrollToSelection();
                qcAnalyteTable.startEditing(n, 0);
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
                showDictionaryPopUp();
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
                field.key = meta.getName();
                field.query = ((AppButton)event.getSource()).getAction();
                field.type = QueryData.Type.STRING;

                query = new Query();
                query.setFields(field);
                nav.setQuery(query);
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
    }

    /*
     * basic button methods
     */

    protected void query() {
        manager = QcManager.getInstance();

        setState(State.QUERY);
        DataChangeEvent.fire(this);
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

        setState(State.ADD);
        DataChangeEvent.fire(this);
        window.setDone(consts.get("enterInformationPressCommit"));
    }

    protected void update() {
        window.setBusy(consts.get("lockForUpdate"));

        try {
            manager = manager.fetchForUpdate();

            setState(State.UPDATE);
            DataChangeEvent.fire(this);
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
        window.clearStatus();
    }

    public void commit() {
        //
        // set the focus to null so every field will commit its data.
        //
        name.setFocus(false);

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
        name.setFocus(false);
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
        TableDataRow row;
        QcAnalyteViewDO data;
        ArrayList<TableDataRow> model;
        String value;

        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.getAnalytes().count(); i++ ) {
                data = manager.getAnalytes().getAnalyteAt(i);

                if (data.getDictionary() == null)
                    value = data.getValue();
                else
                    value = data.getDictionary();

                row = new TableDataRow(null, new TableDataRow(data.getAnalyteId(),
                                                              data.getAnalyteName()),
                                       data.getTypeId(), data.getIsTrendable(), value);
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }

    private void showDictionaryPopUp() {
        ScreenWindow modal;

        if (dictEntryPicker == null) {
            try {
                dictEntryPicker = new DictionaryEntryLookupScreen();
                dictEntryPicker.addActionHandler(new ActionHandler<DictionaryEntryLookupScreen.Action>() {

                    public void onAction(ActionEvent<DictionaryEntryLookupScreen.Action> event) {
                        ArrayList<TableDataRow> model;
                        QcAnalyteViewDO data;
                        TableDataRow row;
                        Integer dictId;

                        try {
                            if (event.getAction() == DictionaryEntryLookupScreen.Action.OK) {
                                model = (ArrayList<TableDataRow>)event.getData();
                                if (model != null) {
                                    for (int i = 0; i < model.size(); i++ ) {
                                        row = model.get(i);
                                        dictId = DictionaryCache.getIdFromSystemName("qc_analyte_dictionary");
                                        data = new QcAnalyteViewDO();
                                        data.setIsTrendable("N");
                                        data.setValue(String.valueOf((Integer)row.key));
                                        data.setDictionary((String)row.cells.get(0).getValue());
                                        data.setTypeId(dictId);
                                        manager.getAnalytes().addAnalyte(data);
                                    }
                                    DataChangeEvent.fire(screen, qcAnalyteTable);
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Window.alert("error: " + e.getMessage());
                            return;
                        }
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
                Window.alert("error: " + e.getMessage());
                return;
            }
        }
        modal = new ScreenWindow("Dictionary LookUp", "dictionaryEntryPickerScreen", "", true,
                                 false);
        modal.setName(consts.get("chooseDictEntry"));
        modal.setContent(dictEntryPicker);
        dictEntryPicker.setScreenState(State.DEFAULT);
    }

    private String validateAndSetNumericValue(String value, int row) {
        boolean convert;
        Double doubleVal, darray[];
        String token, finalValue, strList[];

        darray = new Double[2];
        //
        // Get the string that was entered if the type
        // chosen was "Numeric" and try to break it up at
        // the "," if it follows the pattern number,number
        //
        if ( !"".equals(value.trim())) {
            strList = value.split(",");
            convert = false;
            if (strList.length == 2) {
                for (int iter = 0; iter < strList.length; iter++ ) {
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
                if (darray[0].toString().indexOf(".") == -1) {
                    finalValue = darray[0].toString() + ".0" + ",";
                } else {
                    finalValue = darray[0].toString() + ",";
                }

                if (darray[1].toString().indexOf(".") == -1) {
                    finalValue += darray[1].toString() + ".0";
                } else {
                    finalValue += darray[1].toString();
                }
                qcAnalyteTable.setCell(row, 3, finalValue);
                return finalValue;
            } else {
                qcAnalyteTable.setCellException(
                                                row,
                                                3,
                                                new LocalizedException(
                                                                       "illegalNumericFormatException"));
            }
        } else {
            qcAnalyteTable.setCellException(row, 3,
                                            new LocalizedException("fieldRequiredException"));
        }
        qcAnalyteTable.setCell(row, 3, value);
        return value;
    }

    private String validateAndSetTiterValue(String value, int row) {
        boolean valid;
        String token, strList[];

        //
        // Get the string that was entered if the type
        // chosen was "Numeric" and try to break it up at
        // the "," if it follows the pattern number,number
        //
        if ( !"".equals(value.trim())) {
            strList = value.split(":");
            valid = false;
            if (strList.length == 2) {
                for (int iter = 0; iter < strList.length; iter++ ) {
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

            if ( !valid) {
                qcAnalyteTable.setCellException(
                                                row,
                                                3,
                                                new LocalizedException(
                                                                       "illegalTiterFormatException"));
            }
        } else {
            qcAnalyteTable.setCellException(row, 3,
                                            new LocalizedException("fieldRequiredException"));
        }
        qcAnalyteTable.setCell(row, 3, value);
        return value;
    }

    private String validateAndSetDictValue(String value, int row, QcAnalyteViewDO data) {
        ArrayList<DictionaryDO> list;

        try {
            list = dictionaryService.callList("fetchByEntry", DataBaseUtil.trim(value));

            if (DataBaseUtil.isEmpty(list) || list.size() == 0) {
                qcAnalyteTable.setCellException(row, 3,
                                                new LocalizedException("illegalDictEntryException"));
            } else if (list.size() > 1) {
                Window.alert(consts.get("chooseValueByCategory"));
                qcAnalyteTable.setCell(row, 3, "");
                showDictionaryPopUp();
            } else {
                return String.valueOf(list.get(0).getId());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            //Window.alert(ex.getMessage());
        }

        return null;
    }
}

/*
 * import java.util.ArrayList; import java.util.List; import
 * org.openelis.cache.DictionaryCache; import org.openelis.domain.DictionaryDO;
 * import org.openelis.domain.TestResultDO; import
 * org.openelis.gwt.common.data.deprecated.DropDownField; import
 * org.openelis.gwt.common.data.deprecated.KeyListManager; import
 * org.openelis.gwt.common.data.deprecated.QueryStringField; import
 * org.openelis.gwt.common.data.deprecated.StringField; import
 * org.openelis.gwt.common.data.deprecated.StringObject; import
 * org.openelis.gwt.common.data.deprecated.TableDataModel; import
 * org.openelis.gwt.common.data.deprecated.TableDataRow; import
 * org.openelis.gwt.common.deprecated.Form; import
 * org.openelis.gwt.common.deprecated.Query; import
 * org.openelis.gwt.event.ActionEvent; import
 * org.openelis.gwt.event.ActionHandler; import org.openelis.gwt.screen.Screen;
 * import org.openelis.gwt.screen.deprecated.CommandChain; import
 * org.openelis.gwt.screen.deprecated.ScreenWindow; import
 * org.openelis.gwt.widget.deprecated.AppButton; import
 * org.openelis.gwt.widget.deprecated.AutoComplete; import
 * org.openelis.gwt.widget.deprecated.ButtonPanel; import
 * org.openelis.gwt.widget.deprecated.Dropdown; import
 * org.openelis.gwt.widget.deprecated.ResultsTable; import
 * org.openelis.gwt.widget.deprecated.AppButton.ButtonState; import
 * org.openelis.gwt.widget.table.deprecated.TableDropdown; import
 * org.openelis.gwt.widget.table.deprecated.TableManager; import
 * org.openelis.gwt.widget.table.deprecated.TableWidget; import
 * org.openelis.gwt.widget.table.deprecated.event.SourcesTableWidgetEvents;
 * import org.openelis.gwt.widget.table.deprecated.event.TableWidgetListener;
 * import org.openelis.metamap.QcMetaMap; import
 * org.openelis.modules.dictionaryentrypicker
 * .client.DictionaryEntryPickerScreen; import
 * org.openelis.modules.main.client.OpenELISScreenForm; import
 * com.google.gwt.user.client.Window; import
 * com.google.gwt.user.client.rpc.SyncCallback; import
 * com.google.gwt.user.client.ui.ClickListener; import
 * com.google.gwt.user.client.ui.TextBox; import
 * com.google.gwt.user.client.ui.Widget; public class QcScreen extends
 * OpenELISScreenForm<QCForm, Query<TableDataRow<Integer>>> implements
 * TableManager, ClickListener, TableWidgetListener{ private
 * KeyListManager<Integer> keyList = new KeyListManager<Integer>(); private
 * QcMetaMap QcMeta = new QcMetaMap(); private AppButton removeQCAnalyteButton,
 * dictionaryLookUpButton; private TextBox qcName; private TableWidget
 * qcAnalyteTableWidget; private Dropdown qcType,preparedUnit; private
 * AutoComplete prepBy; private DictionaryEntryPickerScreen dictEntryPicker;
 * public QcScreen() { super("org.openelis.modules.qc.server.QCService"); query
 * = new Query<TableDataRow<Integer>>(); getScreen(new QCForm()); } public void
 * afterDraw(boolean success) { ButtonPanel bpanel, atozButtons; CommandChain
 * chain; ResultsTable atozTable; ArrayList cache; TableDataModel<TableDataRow>
 * model; atozTable = (ResultsTable)getWidget("azTable"); atozButtons =
 * (ButtonPanel)getWidget("atozButtons");
 * //((CollapsePanel)getWidget("collapsePanel")).addChangeListener(atozTable);
 * bpanel = (ButtonPanel)getWidget("buttons"); // // we are interested in
 * getting button actions in two places, // modelwidget and this screen. //
 * chain = new CommandChain(); chain.addCommand(this); chain.addCommand(bpanel);
 * chain.addCommand(keyList); chain.addCommand(atozTable);
 * chain.addCommand(atozButtons); qcType =
 * (Dropdown)getWidget(QcMeta.getTypeId()); preparedUnit =
 * (Dropdown)getWidget(QcMeta.getPreparedUnitId()); qcAnalyteTableWidget =
 * (TableWidget)getWidget("qcAnalyteTable");
 * qcAnalyteTableWidget.addTableWidgetListener(this); removeQCAnalyteButton =
 * (AppButton)getWidget("removeQCAnalyteButton"); dictionaryLookUpButton =
 * (AppButton)getWidget("dictionaryLookUpButton"); qcName =
 * (TextBox)getWidget(QcMeta.getName()); prepBy =
 * (AutoComplete)getWidget(QcMeta.getPreparedById());
 * updateChain.add(afterUpdate); commitUpdateChain.add(commitUpdateCallback);
 * commitAddChain.add(commitAddCallback); super.afterDraw(success); cache =
 * DictionaryCache.getListByCategorySystemName("unit_of_measure"); model =
 * getDictionaryIdEntryList(cache); preparedUnit.setModel(model); cache =
 * DictionaryCache.getListByCategorySystemName("qc_analyte_type"); model =
 * getDictionaryIdEntryList(cache);
 * ((TableDropdown)qcAnalyteTableWidget.columns.
 * get(1).getColumnWidget()).setModel(model); cache =
 * DictionaryCache.getListByCategorySystemName("qc_type"); model =
 * getDictionaryIdEntryList(cache); qcType.setModel(model); } public void
 * performCommand(Enum action, Object obj) { ArrayList<TableDataRow<Integer>>
 * selectedRows; String query; if(obj instanceof AppButton) { query =
 * ((AppButton)obj).action; if (query.indexOf("*") != -1) getQCs(query); else
 * super.performCommand(action, obj); } else if(action ==
 * DictionaryEntryPickerScreen.Action.COMMIT) { selectedRows =
 * (ArrayList<TableDataRow<Integer>>)obj; dictionaryLookupClosed(selectedRows);
 * } else{ super.performCommand(action, obj); } } public boolean
 * canPerformCommand(Enum action, Object obj) { if(action ==
 * DictionaryEntryPickerScreen.Action.OK || action ==
 * DictionaryEntryPickerScreen.Action.CANCEL) return true; else return
 * super.canPerformCommand(action, obj); } public void query() { super.query();
 * removeQCAnalyteButton.changeState(ButtonState.DISABLED);
 * dictionaryLookUpButton.changeState(ButtonState.DISABLED);
 * qcAnalyteTableWidget.model.enableAutoAdd(false); prepBy.enabled(false); }
 * public void add() { super.add(); qcName.setFocus(true);
 * qcAnalyteTableWidget.model.enableAutoAdd(true);
 * qcAnalyteTableWidget.activeRow = -1; prepBy.enabled(true); } public void
 * abort() { qcAnalyteTableWidget.model.enableAutoAdd(false); super.abort(); }
 * protected SyncCallback<QCForm> afterUpdate = new SyncCallback<QCForm>() {
 * public void onFailure(Throwable caught) { Window.alert(caught.getMessage());
 * } public void onSuccess(QCForm result) { qcName.setFocus(true);
 * qcAnalyteTableWidget.model.enableAutoAdd(true);
 * qcAnalyteTableWidget.activeRow = -1; prepBy.enabled(true); } }; protected
 * SyncCallback<QCForm> commitUpdateCallback = new SyncCallback<QCForm>() {
 * public void onSuccess(QCForm result) { if (form.status !=
 * Form.Status.invalid) qcAnalyteTableWidget.model.enableAutoAdd(false); }
 * public void onFailure(Throwable caught) { handleError(caught); } }; protected
 * SyncCallback<QCForm> commitAddCallback = new SyncCallback<QCForm>() { public
 * void onSuccess(QCForm result) { if (form.status != Form.Status.invalid)
 * qcAnalyteTableWidget.model.enableAutoAdd(false); } public void
 * onFailure(Throwable caught) { handleError(caught); } }; private void
 * getQCs(String query) { QueryStringField qField; if (state == State.DISPLAY ||
 * state == State.DEFAULT) { qField = new QueryStringField(QcMeta.getName());
 * qField.setValue(query); commitQuery(qField); } } public <T> boolean
 * canAdd(TableWidget widget, TableDataRow<T> set, int row) { return false; }
 * public <T> boolean canAutoAdd(TableWidget widget, TableDataRow<T> addRow) {
 * DropDownField<Integer> ddField; StringField strField; String val; int empty =
 * 0; ddField = (DropDownField<Integer>)addRow.cells[0];
 * if(ddField.getSelectedKey()==null) empty++; ddField =
 * (DropDownField<Integer>)addRow.cells[1]; if(ddField.getSelectedKey()==null)
 * empty++; strField = (StringField)addRow.cells[3]; val = strField.getValue();
 * if(val == null || (val != null && "".equals(val.trim()))) empty++; return
 * (empty != 3); } public <T> boolean canDelete(TableWidget
 * widget,TableDataRow<T> set,int row) { return false; } public <T> boolean
 * canEdit(TableWidget widget,TableDataRow<T> set,int row,int col) { if(state ==
 * State.ADD || state == State.UPDATE) return true; else if (state ==
 * State.QUERY && col != 3) return true; return false; } public <T> boolean
 * canSelect(TableWidget widget,TableDataRow<T> set,int row) { if(state ==
 * State.ADD || state == State.UPDATE || state == State.QUERY) return true;
 * return false; } public void onClick(Widget sender) { if(sender ==
 * removeQCAnalyteButton) onQCAnalyteButtonClicked(); else if (sender ==
 * dictionaryLookUpButton) onDictionaryLookUpButtonClicked(); } public void
 * finishedEditing(SourcesTableWidgetEvents sender,int row,int col) { Double
 * doubleVal,darray[]; String finalValue,systemName,value,token,strList[];
 * QCGeneralPurposeRPC agrpc; boolean convert; int iter; if(sender ==
 * qcAnalyteTableWidget && col == 3) { final int currRow = row; value =
 * ((StringField)qcAnalyteTableWidget.model.getRow(row).cells[3]).getValue();
 * darray = new Double[2]; finalValue = ""; systemName =
 * getSelectedSystemName(row); if(systemName!=null){ if
 * ("qc_analyte_dictionary".equals(systemName)) { // // Find out if this value
 * is stored in the database if // the type chosen was "Dictionary" // if
 * (!"".equals(value.trim())) { agrpc = new QCGeneralPurposeRPC();
 * agrpc.stringValue = value; screenService.call("getEntryIdForEntryText",agrpc,
 * new SyncCallback<QCGeneralPurposeRPC>() { public void
 * onSuccess(QCGeneralPurposeRPC result) { // // If this value is not stored in
 * the // database then add error to this // cell in the "Value" column // if
 * (result.key == null) { qcAnalyteTableWidget.model.setCellError(currRow,3,
 * consts.get("illegalDictEntryException")); } else {
 * qcAnalyteTableWidget.model.setCell(currRow,3,result.stringValue); } } public
 * void onFailure(Throwable caught) { Window.alert(caught.getMessage());
 * window.clearStatus(); } }); } } else if
 * ("qc_analyte_numeric".equals(systemName)) { // // Get the string that was
 * entered if the type // chosen was "Numeric" and try to break it up at // the
 * "," if it follows the pattern number,number // if (!"".equals(value.trim()))
 * { strList = value.split(","); convert = false; if (strList.length == 2) { for
 * (iter = 0; iter < strList.length; iter++) { token = strList[iter]; try { //
 * // Convert each number obtained // from the string and store its value //
 * converted to double if its a valid // number, into an array // doubleVal =
 * Double.valueOf(token); darray[iter] = doubleVal; convert = true; } catch
 * (NumberFormatException ex) { convert = false; } } } if (convert) { // // If
 * it's a valid string store the converted // string back into the column
 * otherwise add // an error to the cell and store empty // string into the cell
 * // if (darray[0].toString() .indexOf(".") == -1) { finalValue =
 * darray[0].toString() + ".0" + ","; } else { finalValue = darray[0].toString()
 * + ","; } if (darray[1].toString() .indexOf(".") == -1) { finalValue +=
 * darray[1].toString() + ".0"; } else { finalValue += darray[1].toString(); }
 * qcAnalyteTableWidget.model.setCell(currRow,3,finalValue); } else {
 * qcAnalyteTableWidget.model.setCellError(currRow,3,
 * consts.get("illegalNumericFormatException")); } } } else if
 * ("qc_analyte_titer".equals(systemName)) { // // Get the string that was
 * entered if the type chosen // was "Titer" and try to // break it up at the
 * ":" if it follows the pattern // "number:number" // if
 * (!"".equals(value.trim())) { strList = value.split(":"); convert = false; if
 * (strList.length == 2) { for (iter = 0; iter < strList.length; iter++) { token
 * = strList[iter]; try { // // Convert each number obtained from the // string
 * and store its value converted to // int if it's a valid number, into an array
 * // Integer.parseInt(token); convert = true; } catch (NumberFormatException
 * ex) { convert = false; } } } if (convert) { // // If it's a valid string
 * store the converted // string back into the column otherwise add // an error
 * to the cell and store empty // string into the cell //
 * qcAnalyteTableWidget.model.setCell(currRow,3,value); } else {
 * qcAnalyteTableWidget.model.setCellError(currRow,3,
 * consts.get("illegalTiterFormatException")); } } } } } } public void
 * startEditing(SourcesTableWidgetEvents sender, int row, int col) { // TODO
 * Auto-generated method stub } public void stopEditing(SourcesTableWidgetEvents
 * sender, int row, int col) { // TODO Auto-generated method stub } private void
 * onQCAnalyteButtonClicked() { int index; index =
 * qcAnalyteTableWidget.modelIndexList[qcAnalyteTableWidget.activeRow]; if
 * (index > -1) qcAnalyteTableWidget.model.deleteRow(index); } private void
 * onDictionaryLookUpButtonClicked() { ScreenWindow modal; if(dictEntryPicker ==
 * null) { try { dictEntryPicker = new DictionaryEntryPickerScreen();
 * dictEntryPicker.addActionHandler(new
 * ActionHandler<DictionaryEntryPickerScreen.Action>(){ public void
 * onAction(ActionEvent<DictionaryEntryPickerScreen.Action> event) { int selTab;
 * ArrayList<org.openelis.gwt.widget.table.TableDataRow> model; TestResultDO
 * resDO; org.openelis.gwt.widget.table.TableDataRow row; Integer dictId;
 * if(event.getAction() == DictionaryEntryPickerScreen.Action.OK) { model =
 * (ArrayList<org.openelis.gwt.widget.table.TableDataRow>)event.getData();
 * dictId = DictionaryCache.getIdFromSystemName("qc_analyte_dictionary");
 * addQCAnalyteRows(model,dictId); } } }); } catch (Exception e) {
 * e.printStackTrace(); Window.alert("error: " + e.getMessage()); return; } }
 * modal = new
 * ScreenWindow(null,"Dictionary LookUp","dictionaryEntryPickerScreen"
 * ,"",true,false); modal.setName(consts.get("chooseDictEntry"));
 * modal.setContent(dictEntryPicker);
 * dictEntryPicker.setScreenState(Screen.State.DEFAULT); } private void
 * dictionaryLookupClosed(ArrayList<org.openelis.gwt.widget.table.TableDataRow>
 * selectedRows) { Integer key; key =
 * DictionaryCache.getIdFromSystemName("qc_analyte_dictionary");
 * addQCAnalyteRows(selectedRows,key); } private void
 * addQCAnalyteRows(ArrayList<org.openelis.gwt.widget.table.TableDataRow>
 * selectedRows, Integer key) { List<String> entries; TableDataRow<Integer>
 * row,dictSet; org.openelis.gwt.widget.table.TableDataRow set; String entry; if
 * (selectedRows != null) { dictSet = new TableDataRow<Integer>(key); entries =
 * new ArrayList<String>(); for (int iter = 0; iter < selectedRows.size();
 * iter++) { set = selectedRows.get(iter); entry =
 * (String)(set.cells.get(0)).getValue(); if (entry != null &&
 * !entries.contains(entry.trim())) { entries.add(entry); row =
 * (TableDataRow<Integer>)qcAnalyteTableWidget.model.createRow();
 * row.cells[1].setValue(dictSet); row.cells[3].setValue(entry);
 * qcAnalyteTableWidget.model.addRow(row); } }
 * qcAnalyteTableWidget.model.refresh(); } } private String
 * getSelectedSystemName(int row){ TableDataRow<Integer> trow; String sysname;
 * Integer key; if(row > -1) { trow = qcAnalyteTableWidget.model.getRow(row);
 * key = (Integer)(((DropDownField<Integer>)trow.cells[1]).getSelectedKey());
 * sysname = DictionaryCache.getSystemNameFromId(key); return sysname; } return
 * null; } private TableDataModel<TableDataRow>
 * getDictionaryIdEntryList(ArrayList list){ TableDataModel<TableDataRow> m =
 * new TableDataModel<TableDataRow>(); TableDataRow<Integer> row; if(list ==
 * null) return m; m = new TableDataModel<TableDataRow>(); m.add(new
 * TableDataRow<Integer>(null,new StringObject(""))); for(int i=0;
 * i<list.size(); i++){ row = new TableDataRow<Integer>(1); DictionaryDO dictDO
 * = (DictionaryDO)list.get(i); row.key = dictDO.getId(); row.cells[0] = new
 * StringObject(dictDO.getEntry()); m.add(row); } return m; } }
 */