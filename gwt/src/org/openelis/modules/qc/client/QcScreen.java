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

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.UserCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.InventoryItemDO;
import org.openelis.domain.QcAnalyteViewDO;
import org.openelis.domain.QcLotViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.ModulePermission;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.PermissionException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
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
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.manager.QcAnalyteManager;
import org.openelis.manager.QcLotManager;
import org.openelis.manager.QcManager;
import org.openelis.meta.OrganizationMeta;
import org.openelis.meta.QcMeta;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.inventoryItem.client.InventoryItemService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

public class QcScreen extends Screen {
    private QcManager             manager;
    private ModulePermission      userPermission;

    private Tabs                  tab;
    private AnalyteTab            analyteTab;
    private LotTab                lotTab;
    private AppButton             queryButton, previousButton, nextButton, addButton, updateButton,
                                  commitButton, abortButton;
    protected MenuItem            duplicate, qcHistory, qcAnalyteHistory, qcLotHistory;
    private ButtonGroup           atoz;
    private ScreenNavigator       nav;
    private AutoComplete<Integer> inventoryItem;
    private Dropdown<Integer>     typeId;
    private TextBox               id, name, source;
    private CheckBox              isActive;
    private TabPanel              tabPanel;

    private enum Tabs {
        ANALYTE, LOT
    };

    public QcScreen() throws Exception {
        super((ScreenDefInt)GWT.create(QcDef.class));

        userPermission = UserCache.getPermission().getModule("qc");
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
        tab = Tabs.ANALYTE;
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
                                   userPermission.hasSelectPermission());
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
                                 userPermission.hasAddPermission());
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
                                    userPermission.hasUpdatePermission());
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

        duplicate = (MenuItem)def.getWidget("duplicateRecord");
        addScreenHandler(duplicate, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                duplicate();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                duplicate.enable(EnumSet.of(State.DISPLAY).contains(event.getState()) &&
                                 userPermission.hasAddPermission());
            }
        });

        qcHistory = (MenuItem)def.getWidget("qcHistory");
        addScreenHandler(qcHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                qcHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                qcHistory.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        qcAnalyteHistory = (MenuItem)def.getWidget("qcAnalyteHistory");
        addScreenHandler(qcAnalyteHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                qcAnalyteHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                qcAnalyteHistory.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        qcLotHistory = (MenuItem)def.getWidget("qcLotHistory");
        addScreenHandler(qcLotHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                qcLotHistory();
            }

            private void qcLotHistory() {
                int i, count;
                IdNameVO refVoList[];
                QcLotManager man;
                QcLotViewDO data;

                try {
                    man = manager.getLots();
                    count = man.count();
                    refVoList = new IdNameVO[count];
                    for (i = 0; i < count; i++ ) {
                        data = man.getLotAt(i);
                        refVoList[i] = new IdNameVO(data.getId(), data.getLotNumber());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                    return;
                }

                HistoryScreen.showHistory(consts.get("qcLotHistory"), ReferenceTable.QC_LOT, refVoList);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                qcLotHistory.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        //
        // screen fields
        //
        id = (TextBox)def.getWidget(QcMeta.getId());
        addScreenHandler(id, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                id.setValue(manager.getQc().getId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getQc().setId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                id.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                id.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
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
                inventoryItem.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                            .contains(event.getState()));
                inventoryItem.setQueryMode(event.getState() == State.QUERY);
            }
        });

        inventoryItem.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                DictionaryDO dict;
                ArrayList<InventoryItemDO> list;
                ArrayList<TableDataRow> model;

                try {
                    list = InventoryItemService.get().fetchActiveByName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<TableDataRow>();

                    for (InventoryItemDO data : list) {
                        dict = DictionaryCache.getById(data.getStoreId());
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

        tabPanel = (TabPanel)def.getWidget("tabPanel");
        tabPanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                int i;

                i = event.getItem().intValue();
                tab = Tabs.values()[i];

                window.setBusy();
                drawTabs();
                window.clearStatus();
            }
        });

        analyteTab = new AnalyteTab(def, window);
        addScreenHandler(analyteTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                analyteTab.setManager(manager);
                if (tab == Tabs.ANALYTE)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analyteTab.setState(event.getState());
            }
        });

        lotTab = new LotTab(def, window);
        addScreenHandler(lotTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                lotTab.setManager(manager);
                if (tab == Tabs.LOT)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                lotTab.setState(event.getState());
            }
        });

        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator(def) {
            public void executeQuery(final Query query) {
                window.setBusy(consts.get("querying"));

                query.setRowsPerPage(20);
                QcService.get().query(query, new AsyncCallback<ArrayList<IdNameVO>>() {
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
                        model.add(new TableDataRow(entry.getId(), entry.getName()));
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
        ArrayList<DictionaryDO> list;
        TableDataRow row;

        // type dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = CategoryCache.getBySystemName("qc_type");
        for (DictionaryDO d : list) {
            row = new TableDataRow(d.getId(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);
        }

        typeId.setModel(model);
    }

    /*
     * basic button methods
     */
    protected void query() {
        manager = QcManager.getInstance();

        setState(State.QUERY);
        DataChangeEvent.fire(this);

        // clear all the tabs
        analyteTab.draw();
        lotTab.draw();

        setFocus(id);
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

        /*
         * this is done to make sure that the errors added from the back-end are
         * cleaned up before the screen does its validation so that if there are
         * no errors added on the screen, the data can be committed
         */
        lotTab.clearExceptions();

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

    protected void duplicate() {
        try {
            window.setBusy(consts.get("fetching"));

            manager = QcService.get().duplicate(manager.getQc().getId());

            analyteTab.setManager(manager);
            lotTab.setManager(manager);

            analyteTab.draw();
            lotTab.draw();

            setState(State.ADD);
            DataChangeEvent.fire(this);

            setFocus(name);
            window.setDone(consts.get("enterInformationPressCommit"));
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            window.clearStatus();
        }
    }

    private void qcHistory() {
        IdNameVO hist;

        hist = new IdNameVO(manager.getQc().getId(), manager.getQc().getName());
        HistoryScreen.showHistory(consts.get("qcHistory"), ReferenceTable.QC, hist);
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
            Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(consts.get("qcAnalyteHistory"), ReferenceTable.QC_ANALYTE,
                                  refVoList);
    }

    protected boolean fetchById(Integer id) {
        if (id == null) {
            manager = QcManager.getInstance();
            setState(State.DEFAULT);
        } else {
            window.setBusy(consts.get("fetching"));
            try {
                switch (tab) {
                    case ANALYTE:
                        manager = QcManager.fetchWithAnalytes(id);
                        break;
                    case LOT:
                        manager = QcManager.fetchWithLots(id);
                        break;
                }
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

    private void drawTabs() {
        switch (tab) {
            case ANALYTE:
                analyteTab.draw();
                break;
            case LOT:
                lotTab.draw();
                break;
        }
    }
}