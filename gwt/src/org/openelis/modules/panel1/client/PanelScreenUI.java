package org.openelis.modules.panel1.client;

import static org.openelis.modules.main.client.Logger.logger;
import static org.openelis.ui.screen.Screen.ShortKeys.CTRL;
import static org.openelis.ui.screen.State.ADD;
import static org.openelis.ui.screen.State.DEFAULT;
import static org.openelis.ui.screen.State.DISPLAY;
import static org.openelis.ui.screen.State.QUERY;
import static org.openelis.ui.screen.State.UPDATE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;

import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.domain.Constants;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.PanelItemDO;
import org.openelis.domain.PanelVO;
import org.openelis.manager.PanelManager1;
import org.openelis.meta.PanelMeta;
import org.openelis.modules.auxiliary.client.AuxiliaryService;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.test.client.TestService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.ScreenNavigator;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.ButtonGroup;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.Menu;
import org.openelis.ui.widget.MenuItem;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.FilterEvent;
import org.openelis.ui.widget.table.event.FilterHandler;
import org.openelis.ui.widget.table.event.RowDeletedEvent;
import org.openelis.ui.widget.table.event.RowDeletedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class PanelScreenUI extends Screen {

    @UiTemplate("Panel.ui.xml")
    interface PanelUiBinder extends UiBinder<Widget, PanelScreenUI> {
    };

    public static final PanelUiBinder              uiBinder = GWT.create(PanelUiBinder.class);

    protected PanelManager1                        manager;

    protected ModulePermission                     userPermission;

    protected ScreenNavigator<IdNameVO>            nav;

    @UiField
    protected ButtonGroup                          atozButtons;

    @UiField
    protected Table                                atozTable;

    @UiField
    protected Button                               query, previous, next, add, update, commit,
                    abort, optionsButton, loadResults, removeButton, moveUpButton, moveDownButton,
                    refreshButton, addItemButton;

    @UiField
    protected Menu                                 optionsMenu;

    @UiField
    protected MenuItem                             panelHistory, panelItemHistory;

    @UiField
    protected TextBox<String>                      name, description;

    @UiField
    protected Table                                panelItemTable, allTestTable, allAuxTable;

    protected PanelScreenUI                        screen;

    private PanelService1Impl                      service  = PanelService1Impl.INSTANCE;

    protected AsyncCallbackUI<ArrayList<IdNameVO>> queryCall;

    protected AsyncCallbackUI<PanelManager1>       addCall, fetchForUpdateCall, updateCall,
                    fetchByIdCall, unlockCall;

    private ArrayList<PanelVO>                     testList;

    private TestRowComparator                      comparator;

    private enum SortField {
        TEST, METHOD, SECTION
    }

    public PanelScreenUI(WindowInt window) throws Exception {
        setWindow(window);

        userPermission = UserCache.getPermission().getModule("panel");
        if (userPermission == null)
            throw new PermissionException(Messages.get().gen_screenPermException("Panel Screen"));

        initWidget(uiBinder.createAndBindUi(this));

        manager = null;

        initialize();
        setState(DEFAULT);
        initializeAllTestTable();
        fireDataChange();

        logger.fine("Panel Screen Opened");
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        screen = this;

        //
        // button panel buttons
        //
        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                query.setEnabled(isState(QUERY, DEFAULT, DISPLAY) &&
                                 userPermission.hasSelectPermission());
                if (isState(QUERY)) {
                    query.lock();
                    query.setPressed(true);
                }
            }
        });

        addShortcut(query, 'q', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                previous.setEnabled(isState(DISPLAY));
            }
        });

        addShortcut(previous, 'p', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                next.setEnabled(isState(DISPLAY));
            }
        });

        addShortcut(next, 'n', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                add.setEnabled(isState(ADD, DEFAULT, DISPLAY) && userPermission.hasAddPermission());
                if (isState(ADD)) {
                    add.lock();
                    add.setPressed(true);
                }
            }
        });

        addShortcut(add, 'a', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                update.setEnabled(isState(UPDATE, DISPLAY) && userPermission.hasUpdatePermission());
                if (isState(UPDATE)) {
                    update.lock();
                    update.setPressed(true);
                }
            }
        });

        addShortcut(update, 'u', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                commit.setEnabled(isState(QUERY, ADD, UPDATE));
            }
        });

        addShortcut(commit, 'm', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                abort.setEnabled(isState(QUERY, ADD, UPDATE));
            }
        });

        addShortcut(abort, 'o', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                removeButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                moveUpButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                moveDownButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                refreshButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                addItemButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        //
        // screen fields
        //
        addScreenHandler(name, PanelMeta.getName(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                name.setValue(getName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setName(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                name.setEnabled(isState(QUERY, ADD, UPDATE));
                name.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? description : description;
            }
        });

        addScreenHandler(description, PanelMeta.getDescription(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                description.setValue(getDescription());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setDescription(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                description.setEnabled(isState(QUERY, ADD, UPDATE));
                description.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? name : name;
            }
        });

        addScreenHandler(allTestTable, "allTestTable", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
            }

            public void onStateChange(StateChangeEvent event) {
                allTestTable.setEnabled(isState(DEFAULT, DISPLAY, ADD, UPDATE));
            }
        });

        allTestTable.addFilterHandler(new FilterHandler() {
            @Override
            public void onFilter(FilterEvent event) {

            }
        });

        addScreenHandler(allAuxTable, "allAuxTable", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
            }

            public void onStateChange(StateChangeEvent event) {
                allAuxTable.setEnabled(isState(DEFAULT, DISPLAY, ADD, UPDATE));
            }
        });

        addScreenHandler(panelItemTable, "panelItemTable", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                if ( !isState(QUERY)) {
                    panelItemTable.setModel(getPanelItemTableModel());
                }
            }

            public void onStateChange(StateChangeEvent event) {
                panelItemTable.setEnabled(isState(QUERY, DISPLAY, ADD, UPDATE));
                panelItemTable.setQueryMode(isState(QUERY));
            }
        });

        panelItemTable.addRowDeletedHandler(new RowDeletedHandler() {
            @Override
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.item.remove(event.getIndex());
                } catch (Exception e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    Window.alert(e.getMessage());
                }
            }
        });

        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator<IdNameVO>(atozTable, loadResults) {
            public void executeQuery(final Query query) {
                setBusy(Messages.get().gen_querying());

                if (queryCall == null) {
                    queryCall = new AsyncCallbackUI<ArrayList<IdNameVO>>() {
                        public void success(ArrayList<IdNameVO> result) {
                            ArrayList<IdNameVO> addedList;
                            clearStatus();
                            if (nav.getQuery().getPage() == 0) {
                                setQueryResult(result);
                                fetchById(result.get(0).getId());
                                select(0);
                            } else {
                                addedList = getQueryResult();
                                addedList.addAll(result);
                                setQueryResult(addedList);
                                select(atozTable.getModel().size() - result.size());
                                atozTable.scrollToVisible(atozTable.getModel().size() - 1);
                            }
                        }

                        public void notFound() {
                            setQueryResult(null);
                            setDone(Messages.get().gen_noRecordsFound());
                        }

                        public void lastPage() {
                            setQueryResult(null);
                            setError(Messages.get().gen_noMoreRecordInDir());
                        }

                        public void failure(Throwable error) {
                            setQueryResult(null);
                            Window.alert("Error: Panel call query failed; " + error.getMessage());
                            setError(Messages.get().gen_queryFailed());
                        }
                    };
                }
                query.setRowsPerPage(16);
                service.query(query, queryCall);
            }

            public ArrayList<Item<Integer>> getModel() {
                ArrayList<IdNameVO> result;
                ArrayList<Item<Integer>> model;
                Item<Integer> row;

                model = null;
                result = nav.getQueryResult();
                if (result != null) {
                    model = new ArrayList<Item<Integer>>();
                    for (IdNameVO entry : result) {
                        row = new Item<Integer>(1);
                        row.setKey(entry.getId());
                        row.setCell(0, entry.getName());
                        model.add(row);
                    }
                }
                return model;
            }

            @Override
            public boolean fetch(IdNameVO entry) {
                fetchById( (entry == null) ? null : entry.getId());
                return true;
            }
        };

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                optionsMenu.setEnabled(isState(DISPLAY));
                optionsButton.setEnabled(isState(DISPLAY));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                panelHistory.setEnabled(isState(DISPLAY));
            }
        });
        panelHistory.addCommand(new Command() {
            @Override
            public void execute() {
                panelHistory();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                panelItemHistory.setEnabled(isState(DISPLAY));
            }
        });
        panelItemHistory.addCommand(new Command() {
            @Override
            public void execute() {
                panelItemHistory();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                boolean enable;
                enable = isState(DEFAULT, DISPLAY) && userPermission.hasSelectPermission();
                atozButtons.setEnabled(enable);
                nav.enable(enable);
            }
        });

        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                if (isState(ADD, UPDATE)) {
                    event.cancel();
                    setError(Messages.get().gen_mustCommitOrAbort());
                }
            }
        });
    }

    /*
     * basic button methods
     */
    @UiHandler("atozButtons")
    public void atozQuery(ClickEvent event) {
        Query query;
        QueryData field;

        field = new QueryData();
        field.setKey(PanelMeta.getName());
        field.setQuery( ((Button)event.getSource()).getAction());
        field.setType(QueryData.Type.STRING);

        query = new Query();
        query.setFields(field);
        nav.setQuery(query);
    }

    @UiHandler("query")
    protected void query(ClickEvent event) {
        manager = null;
        /*
         * the tab for aux data uses the cache in query state
         */
        setState(QUERY);
        fireDataChange();
        name.setFocus(true);
        setDone(Messages.get().gen_enterFieldsToQuery());
    }

    @UiHandler("previous")
    protected void previous(ClickEvent event) {
        if (DataBaseUtil.isSame(atozTable.getSelectedRow(), 0)) {
            setError(Messages.get().gen_noMoreRecordInDir());
            return;
        }
        nav.previous();
    }

    @UiHandler("next")
    protected void next(ClickEvent event) {
        nav.next();
    }

    @UiHandler("add")
    protected void add(ClickEvent event) {
        setBusy();
        if (addCall == null) {
            addCall = new AsyncCallbackUI<PanelManager1>() {
                public void success(PanelManager1 result) {
                    manager = result;
                    setState(ADD);
                    fireDataChange();
                    name.setFocus(true);
                    setDone(Messages.get().enterInformationPressCommit());
                }

                public void failure(Throwable error) {
                    logger.log(Level.SEVERE, error.getMessage(), error);
                    Window.alert(error.getMessage());
                    clearStatus();
                }
            };
        }
        service.getInstance(addCall);
    }

    @UiHandler("update")
    protected void update(ClickEvent event) {
        setBusy(Messages.get().lockForUpdate());

        if (fetchForUpdateCall == null) {
            fetchForUpdateCall = new AsyncCallbackUI<PanelManager1>() {
                public void success(PanelManager1 result) {
                    manager = result;
                    setState(UPDATE);
                    fireDataChange();
                    name.setFocus(true);
                }

                public void failure(Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
                }

                public void finish() {
                    clearStatus();
                }
            };
        }

        service.fetchForUpdate(manager.getPanel().getId(), fetchForUpdateCall);
    }

    @UiHandler("commit")
    protected void commit(ClickEvent event) {
        commit();
    }

    private void commit() {
        Validation validation;

        finishEditing();

        validation = validate();
        if (Validation.Status.ERRORS.equals(validation.getStatus())) {
            setError(Messages.get().gen_correctErrors());
            return;
        }

        switch (super.state) {
            case QUERY:
                commitQuery();
                break;
            case ADD:
                commitUpdate();
                break;
            case UPDATE:
                commitUpdate();
                break;
        }
    }

    protected void commitQuery() {
        Query query;

        query = new Query();
        query.setFields(getQueryFields());
        nav.setQuery(query);
    }

    protected void commitUpdate() {
        if (isState(ADD))
            setBusy(Messages.get().gen_adding());
        else
            setBusy(Messages.get().gen_updating());

        if (updateCall == null) {
            updateCall = new AsyncCallbackUI<PanelManager1>() {
                public void success(PanelManager1 result) {
                    manager = result;
                    setState(DISPLAY);
                    fireDataChange();
                    clearStatus();
                }

                public void failure(Throwable e) {
                    if (isState(ADD))
                        Window.alert("commitAdd(): " + e.getMessage());
                    else
                        Window.alert("commitUpdate(): " + e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    clearStatus();
                }
            };
        }

        service.update(manager, updateCall);
    }

    @UiHandler("abort")
    protected void abort(ClickEvent event) {
        finishEditing();
        clearErrors();
        setBusy(Messages.get().gen_cancelChanges());

        if (isState(QUERY)) {
            try {
                manager = null;
                setState(DEFAULT);
                fireDataChange();
                setDone(Messages.get().gen_queryAborted());
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                Window.alert(e.getMessage());
                clearStatus();
            }
        } else if (isState(ADD)) {
            try {
                manager = null;
                setState(DEFAULT);
                fireDataChange();
                setDone(Messages.get().gen_addAborted());
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                Window.alert(e.getMessage());
                clearStatus();
            }
        } else if (isState(UPDATE)) {
            if (unlockCall == null) {
                unlockCall = new AsyncCallbackUI<PanelManager1>() {
                    public void success(PanelManager1 result) {
                        manager = result;
                        setState(DISPLAY);
                        fireDataChange();
                        setDone(Messages.get().gen_updateAborted());
                    }

                    public void failure(Throwable e) {
                        manager = null;
                        setState(DEFAULT);
                        fireDataChange();
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        clearStatus();
                    }
                };
            }
            service.unlock(manager.getPanel().getId(), unlockCall);
        }
    }

    @UiHandler("removeButton")
    protected void remove(ClickEvent event) {
        int r;

        r = panelItemTable.getSelectedRow();
        if (r > -1 && panelItemTable.getRowCount() > 0)
            panelItemTable.removeRowAt(r);
    }

    @UiHandler("moveUpButton")
    protected void moveUp(ClickEvent event) {
        int r;

        r = panelItemTable.getSelectedRow();
        if (r <= 0)
            return;

        try {
            manager.item.moveItem(r, r - 1);
            fireDataChange();
            panelItemTable.selectRowAt(r - 1);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            Window.alert(e.getMessage());
        }
    }

    @UiHandler("moveDownButton")
    protected void moveDown(ClickEvent event) {
        int r;

        r = panelItemTable.getSelectedRow();
        if (r >= panelItemTable.getRowCount() - 1)
            return;

        try {
            manager.item.moveItem(r, r + 1);
            fireDataChange();
            panelItemTable.selectRowAt(r + 1);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            Window.alert(e.getMessage());
        }
    }

    @UiHandler("refreshButton")
    protected void refresh(ClickEvent event) {
        setBusy(Messages.get().fetching());
        initializeAllTestTable();
        if (isState(ADD))
            setDone(Messages.get().enterInformationPressCommit());
        else
            clearStatus();
    }

    @UiHandler("addItemButton")
    protected void addItem(ClickEvent event) {
        boolean ok;
        Row row;
        PanelItemDO data;
        Integer selRows[];
        ArrayList<Row> rows;

        try {
            selRows = allTestTable.getSelectedRows();
            rows = new ArrayList<Row>();
            for (int i = 0; i < selRows.length; i++ ) {
                rows.add(allTestTable.getRowAt(selRows[i]));
            }

            for (int i = 0; i < rows.size(); i++ ) {
                row = rows.get(i);

                if (itemExistsInPanel(row)) {
                    ok = Window.confirm(Messages.get().testAlreadyAdded());
                    if (ok) {
                        data = new PanelItemDO();
                        data.setName((String)row.getCell(0));
                        data.setMethodName((String)row.getCell(1));
                        data.setType("T");
                        panelItemTable.addRow(getPanelItemRow(row));
                        manager.item.add(data);
                    }
                } else {
                    data = new PanelItemDO();
                    data.setName((String)row.getCell(0));
                    data.setMethodName((String)row.getCell(1));
                    data.setType("T");
                    panelItemTable.addRow(getPanelItemRow(row));
                    manager.item.add(data);
                }
            }

            selRows = allAuxTable.getSelectedRows();
            rows = new ArrayList<Row>();
            for (int i = 0; i < selRows.length; i++ ) {
                rows.add(allAuxTable.getRowAt(selRows[i]));
            }

            for (int i = 0; i < rows.size(); i++ ) {
                row = rows.get(i);

                if (itemExistsInPanel(row)) {
                    Window.alert(Messages.get().auxAlreadyAddedException());
                    continue;
                } else {
                    data = new PanelItemDO();
                    data.setName((String)row.getCell(0));
                    data.setType("A");
                    panelItemTable.addRow(getPanelItemRow(row));
                    manager.item.add(data);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            Window.alert(e.getMessage());
        }
        allTestTable.unselectAll();
        allAuxTable.unselectAll();
    }

    private void initializeAllTestTable() {
        ArrayList<AuxFieldGroupDO> auxList;
        ArrayList<Row> model;
        Row row;

        try {
            testList = TestService.get().fetchNameMethodSectionByName("");
            allTestTable.setModel(getAllTestModel());

            model = new ArrayList<Row>();
            auxList = AuxiliaryService.get().fetchActive();
            for (AuxFieldGroupDO data : auxList) {
                row = new Row(1);
                row.setCell(0, data.getName());
                row.setData(data.getId());
                model.add(row);
            }

            allAuxTable.setModel(model);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            Window.alert(e.getMessage());
            window.close();
        }

    }

    private ArrayList<Row> getAllTestModel() {
        ArrayList<Row> model;
        Row row;

        model = new ArrayList<Row>();
        for (PanelVO data : testList) {
            row = new Row(3);
            row.setCell(0, data.getName());
            row.setCell(1, data.getMethodName());
            row.setCell(2, data.getSectionName());
            row.setData(data.getId());
            model.add(row);
        }
        return model;
    }

    private void sortTests(int column, boolean ascending) {
        if (comparator == null)
            comparator = new TestRowComparator();
        comparator.setSortAscending(ascending);
        comparator.setSortField(column);
        Collections.sort(testList, comparator);
    }

    private String getName() {
        if (manager == null || manager.getPanel() == null)
            return null;
        return manager.getPanel().getName();
    }

    private void setName(String name) {
        manager.getPanel().setName(name);
    }

    private String getDescription() {
        if (manager == null || manager.getPanel() == null)
            return null;
        return manager.getPanel().getDescription();
    }

    private void setDescription(String description) {
        manager.getPanel().setDescription(description);
    }

    protected void panelHistory() {
        IdNameVO hist;

        hist = new IdNameVO(manager.getPanel().getId(), manager.getPanel().getName());
        HistoryScreen.showHistory(Messages.get().panelHistory(), Constants.table().PANEL, hist);
    }

    protected void panelItemHistory() {
        int i, count;
        String name, mname, iname;
        IdNameVO refVoList[];
        PanelItemDO data;

        try {
            count = manager.item.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = manager.item.get(i);
                name = data.getName();
                mname = data.getMethodName();

                if (mname == null)
                    iname = name;
                else
                    iname = name + "," + mname;
                refVoList[i] = new IdNameVO(data.getId(), iname);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(Messages.get().panelItemHistory(),
                                  Constants.table().PANEL_ITEM,
                                  refVoList);
    }

    protected void fetchById(Integer id) {
        if (id == null) {
            manager = null;
            setState(DEFAULT);
            fireDataChange();
            clearStatus();
        } else {
            setBusy(Messages.get().fetching());
            if (fetchByIdCall == null) {
                fetchByIdCall = new AsyncCallbackUI<PanelManager1>() {
                    public void success(PanelManager1 result) {
                        manager = result;
                        setState(DISPLAY);
                    }

                    public void notFound() {
                        fetchById(null);
                        setDone(Messages.get().noRecordsFound());
                    }

                    public void failure(Throwable e) {
                        fetchById(null);
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        Window.alert(Messages.get().fetchFailed() + e.getMessage());
                    }

                    public void finish() {
                        fireDataChange();
                        clearStatus();
                    }
                };
            }
            service.fetchById(id, fetchByIdCall);
        }
    }

    private ArrayList<Row> getPanelItemTableModel() {
        int i;
        StringBuffer item;
        PanelItemDO data;
        ArrayList<Row> model;
        Row row;

        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        item = new StringBuffer();
        try {
            for (i = 0; i < manager.item.count(); i++ ) {
                data = manager.item.get(i);
                row = new Row(1);
                item.append(data.getName());
                if ("T".equals(data.getType()) && data.getMethodName() != null)
                    item.append(", ").append(data.getMethodName());
                row.setCell(0, item.toString());
                row.setData(data);
                model.add(row);
                item.setLength(0);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            Window.alert(e.getMessage());
        }
        return model;
    }

    private Row getPanelItemRow(Row trow) {
        StringBuffer item;
        Row row;
        ArrayList<Object> cells;

        cells = trow.getCells();

        row = new Row(1);
        item = new StringBuffer();
        item.append(trow.getCell(0));
        if (cells.size() > 1)
            item.append(", ").append(trow.getCell(1));
        row.setCell(0, item.toString());
        row.setData(trow.getData());
        return row;
    }

    private boolean itemExistsInPanel(Row row) throws Exception {
        PanelItemDO data;
        String name, method;
        ArrayList<Object> cells;

        cells = row.getCells();
        name = (String)row.getCell(0);
        method = null;
        if (cells.size() > 1)
            method = (String)row.getCell(1);

        for (int j = 0; j < manager.item.count(); j++ ) {
            data = manager.item.get(j);
            if (data.getName().equals(name)) {
                if (DataBaseUtil.isSame(data.getMethodName(), method)) {
                    return true;
                }
            }
        }
        return false;
    }

    //
    // Sort for table
    //
    private class TestRowComparator implements Comparator<PanelVO> {
        boolean   ascending;
        SortField sortField;

        public void setSortAscending(boolean ascending) {
            this.ascending = ascending;
        }

        public void setSortField(int column) {
            if (column == 1)
                this.sortField = SortField.METHOD;
            else if (column == 2)
                this.sortField = SortField.SECTION;
            else
                this.sortField = SortField.TEST;
        }

        public int compare(PanelVO data1, PanelVO data2) {
            String field1, field2;

            if (sortField == SortField.METHOD) {
                field1 = data1.getMethodName();
                field2 = data2.getMethodName();
            } else if (sortField == SortField.SECTION) {
                field1 = data1.getSectionName();
                field2 = data2.getSectionName();
            } else {
                field1 = data1.getName();
                field2 = data2.getName();
            }

            if (ascending) {
                return compare(field1, field2);
            } else {
                return (compare(field1, field2) * -1);
            }
        }

        private int compare(String entry1, String entry2) {
            if (DataBaseUtil.isEmpty(entry1)) {
                if (DataBaseUtil.isEmpty(entry2))
                    return 0;
                else
                    return 1;
            } else {
                if (DataBaseUtil.isEmpty(entry2))
                    return -1;
                else
                    return entry1.compareTo(entry2);
            }
        }

    }
}
