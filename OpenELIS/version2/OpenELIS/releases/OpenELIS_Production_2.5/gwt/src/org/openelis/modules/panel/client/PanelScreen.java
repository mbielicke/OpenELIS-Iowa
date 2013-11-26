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
package org.openelis.modules.panel.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AuxFieldGroupDO;
import org.openelis.domain.Constants;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.PanelItemDO;
import org.openelis.domain.PanelVO;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.widget.WindowInt;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.PanelItemManager;
import org.openelis.manager.PanelManager;
import org.openelis.meta.PanelMeta;
import org.openelis.modules.auxiliary.client.AuxiliaryService;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.test.client.TestService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PanelScreen extends Screen {
    private PanelManager     manager;
    private ModulePermission userPermission;

    private ButtonGroup      atoz;
    private ScreenNavigator  nav;

    private PanelScreen      screen;
    private TextBox          name, description;
    private AppButton        queryButton, previousButton, nextButton, addButton, updateButton,
                             deleteButton, commitButton, abortButton, addTestButton, removeTestButton,
                             moveUpButton, moveDownButton, refreshButton;
    protected MenuItem       panelHistory, panelItemHistory;
    private TableWidget      panelItemTable, allTestAuxTable;

    public PanelScreen(WindowInt window) throws Exception {
        super((ScreenDefInt)GWT.create(PanelDef.class));
        
        setWindow(window);

        userPermission = UserCache.getPermission().getModule("panel");
        if (userPermission == null)
            throw new PermissionException(Messages.get().screenPermException("Panel Screen"));

        manager = PanelManager.getInstance();

        initialize();
        setState(State.DEFAULT);
        initializeAllTestTable();
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

        deleteButton = (AppButton)def.getWidget("delete");
        addScreenHandler(deleteButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                delete();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                deleteButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()) &&
                                    userPermission.hasDeletePermission());
                if (event.getState() == State.DELETE)
                    deleteButton.setState(ButtonState.LOCK_PRESSED);
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

        panelHistory = (MenuItem)def.getWidget("panelHistory");
        addScreenHandler(panelHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                panelHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                panelHistory.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        panelItemHistory = (MenuItem)def.getWidget("panelItemHistory");
        addScreenHandler(panelItemHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                panelItemHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                panelItemHistory.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        name = (TextBox<String>)def.getWidget(PanelMeta.getName());
        addScreenHandler(name, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                name.setValue(manager.getPanel().getName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getPanel().setName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                name.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                   .contains(event.getState()));
                name.setQueryMode(event.getState() == State.QUERY);
            }
        });

        description = (TextBox)def.getWidget(PanelMeta.getDescription());
        addScreenHandler(description, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                description.setValue(manager.getPanel().getDescription());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getPanel().setDescription(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                description.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
                description.setQueryMode(event.getState() == State.QUERY);
            }
        });

        panelItemTable = (TableWidget)def.getWidget("panelItemTable");
        addScreenHandler(panelItemTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    panelItemTable.load(getPanelItemTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                panelItemTable.enable(true);
                panelItemTable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        panelItemTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if (state != State.QUERY)
                    event.cancel();
            }
        });

        panelItemTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                int r;
                PanelItemDO data;
                TableDataRow row;

                r = event.getIndex();
                row = panelItemTable.getRow(r);
                try {
                    data = new PanelItemDO();                    
                    data.setName((String)row.cells.get(0).getValue());
                    data.setMethodName((String)row.cells.get(1).getValue());
                    data.setType((String)row.data);
                    manager.getItems().addItem(data);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        panelItemTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getItems().remoteItemAt(event.getIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        addTestButton = (AppButton)def.getWidget("addTestButton");
        addScreenHandler(addTestButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                boolean ok;
                TableDataRow row;
                ArrayList<TableDataRow> rows;                

                rows = allTestAuxTable.getSelections();
                try {
                    for (int i = 0; i < rows.size(); i++ ) {
                        row = rows.get(i);

                        if (itemExistsInPanel(row)) {
                            if ("A".equals(row.data)) {
                                Window.alert(Messages.get().auxAlreadyAddedException());
                                return;
                            }
                            ok = Window.confirm(Messages.get().testAlreadyAdded());
                            if (ok)
                                panelItemTable.addRow(getPanelItemRow(row));
                        } else {
                            panelItemTable.addRow(getPanelItemRow(row));
                        }
                    }
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addTestButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        allTestAuxTable = (TableWidget)def.getWidget("allTestAuxTable");
        addScreenHandler(allTestAuxTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {

            public void onStateChange(StateChangeEvent<State> event) {
                allTestAuxTable.enable(true);
            }
        });

        removeTestButton = (AppButton)def.getWidget("removeTestButton");
        addScreenHandler(removeTestButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = panelItemTable.getSelectedRow();
                if (r > -1 && panelItemTable.numRows() > 0)
                    panelItemTable.deleteRow(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeTestButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                               .contains(event.getState()));
            }
        });

        moveUpButton = (AppButton)def.getWidget("moveUpButton");
        addScreenHandler(moveUpButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = panelItemTable.getSelectedRow();
                if (r <= 0)
                    return;

                try {
                    manager.getItems().moveItem(r, r - 1);
                    DataChangeEvent.fire(screen, panelItemTable);
                    panelItemTable.selectRow(r - 1);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }

            }

            public void onStateChange(StateChangeEvent<State> event) {
                moveUpButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        moveDownButton = (AppButton)def.getWidget("moveDownButton");
        addScreenHandler(moveDownButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = panelItemTable.getSelectedRow();
                if (r >= panelItemTable.numRows() - 1)
                    return;

                try {
                    manager.getItems().moveItem(r, r + 1);
                    DataChangeEvent.fire(screen, panelItemTable);
                    panelItemTable.selectRow(r + 1);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                moveDownButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                             .contains(event.getState()));
            }
        });

        refreshButton = (AppButton)def.getWidget("refreshButton");
        addScreenHandler(refreshButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                window.setBusy(Messages.get().fetching());
                initializeAllTestTable();
                if (state == State.ADD)
                    window.setDone(Messages.get().enterInformationPressCommit());
                else
                    window.clearStatus();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                refreshButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator<IdNameVO>(def) {
            public void executeQuery(final Query query) {
                window.setBusy(Messages.get().querying());

                query.setRowsPerPage(14);
                PanelService.get().query(query, new AsyncCallback<ArrayList<IdNameVO>>() {
                    public void onSuccess(ArrayList<IdNameVO> result) {
                        setQueryResult(result);
                    }

                    public void onFailure(Throwable error) {
                        setQueryResult(null);
                        if (error instanceof NotFoundException) {
                            window.setDone(Messages.get().noRecordsFound());
                            setState(State.DEFAULT);
                        } else if (error instanceof LastPageException) {
                            window.setError(Messages.get().noMoreRecordInDir());
                        } else {
                            Window.alert("Error: Panel call query failed; " + error.getMessage());
                            window.setError(Messages.get().queryFailed());
                        }
                    }
                });
            }

            public boolean fetch(IdNameVO entry) {
                return fetchById( (entry == null) ? null : entry.getId());
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
                field.setKey(PanelMeta.getName());
                field.setQuery(((AppButton)event.getSource()).getAction());
                field.setType(QueryData.Type.STRING);

                query = new Query();
                query.setFields(field);
                nav.setQuery(query);
            }
        });

        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                if (EnumSet.of(State.ADD, State.UPDATE, State.DELETE).contains(state)) {
                    event.cancel();
                    window.setError(Messages.get().mustCommitOrAbort());
                }
            }
        });
    }

    private void initializeAllTestTable() {
        ArrayList<PanelVO> testList;
        ArrayList<AuxFieldGroupDO> auxList;
        ArrayList<TableDataRow> model;
        TableDataRow row;

        try {
            testList = TestService.get().fetchNameMethodSectionByName("");
            model = new ArrayList<TableDataRow>();
            for (PanelVO data : testList) {
                row = new TableDataRow(data.getId(), data.getName(), data.getMethodName(),
                                       data.getSectionName());
                row.data = "T";
                model.add(row);
            }
            
            auxList = AuxiliaryService.get().fetchActive();
            for (AuxFieldGroupDO data : auxList) {
                row = new TableDataRow(data.getId(), data.getName(), null, null);
                row.data = "A";
                model.add(row);
            }
            
            allTestAuxTable.load(model);
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            window.close();
        }

    }

    protected void query() {
        manager = PanelManager.getInstance();

        setState(State.QUERY);
        DataChangeEvent.fire(this);

        setFocus(name);
        window.setDone(Messages.get().enterFieldsToQuery());
    }

    protected void previous() {
        nav.previous();
    }

    protected void next() {
        nav.next();
    }

    protected void add() {
        manager = PanelManager.getInstance();

        setState(State.ADD);
        DataChangeEvent.fire(this);

        setFocus(name);
        window.setDone(Messages.get().enterInformationPressCommit());
    }

    protected void update() {
        window.setBusy(Messages.get().lockForUpdate());

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

    protected void delete() {
        window.setBusy(Messages.get().lockForUpdate());

        try {
            manager = manager.fetchForUpdate();

            setState(State.DELETE);
            DataChangeEvent.fire(this);
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
        window.clearStatus();
    }

    protected void commit() {
        setFocus(null);
        allTestAuxTable.clearSelections();

        if ( !validate()) {
            window.setError(Messages.get().correctErrors());
            return;
        }

        if (state == State.QUERY) {
            Query query;

            query = new Query();
            query.setFields(getQueryFields());
            nav.setQuery(query);
        } else if (state == State.ADD) {
            window.setBusy(Messages.get().adding());
            try {
                manager = manager.add();

                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(Messages.get().addingComplete());
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                Window.alert("commitAdd(): " + e.getMessage());
                window.clearStatus();
            }
        } else if (state == State.UPDATE) {
            window.setBusy(Messages.get().updating());
            try {
                manager = manager.update();

                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(Messages.get().updatingComplete());
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                Window.alert("commitUpdate(): " + e.getMessage());
                window.clearStatus();
            }
        } else if (state == State.DELETE) {
            window.setBusy(Messages.get().deleting());
            try {
                manager.delete();

                fetchById(null);
                window.setDone(Messages.get().deleteComplete());
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                Window.alert("commitDelete(): " + e.getMessage());
                window.clearStatus();
            }
        }
    }

    protected void abort() {
        setFocus(null);
        allTestAuxTable.clearSelections();
        clearErrors();
        window.setBusy(Messages.get().cancelChanges());

        if (state == State.QUERY) {
            fetchById(null);
            window.setDone(Messages.get().queryAborted());
        } else if (state == State.ADD) {
            fetchById(null);
            window.setDone(Messages.get().addAborted());
        } else if (state == State.UPDATE) {
            try {
                manager = manager.abortUpdate();
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
            } catch (Exception e) {
                Window.alert(e.getMessage());
                fetchById(null);
            }
            window.setDone(Messages.get().updateAborted());
        } else if (state == State.DELETE) {
            try {
                manager = manager.abortUpdate();
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
            } catch (Exception e) {
                Window.alert(e.getMessage());
                fetchById(null);
            }
            window.setDone(Messages.get().deleteAborted());
        } else {
            window.clearStatus();
        }
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
        PanelItemManager man;
        PanelItemDO data;

        try {
            man = manager.getItems();
            count = man.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = man.getItemAt(i);
                name = data.getName();
                mname = data.getMethodName();
                
                if (mname == null)
                    iname = name;
                else
                    iname = name + "," + mname;
                refVoList[i] = new IdNameVO(data.getId(), iname);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(Messages.get().panelItemHistory(), Constants.table().PANEL_ITEM,
                                  refVoList);
    }

    protected boolean fetchById(Integer id) {
        if (id == null) {
            manager = PanelManager.getInstance();
            setState(State.DEFAULT);
        } else {
            window.setBusy(Messages.get().fetching());
            try {
                manager = PanelManager.fetchWithItems(id);
                setState(State.DISPLAY);
            } catch (NotFoundException e) {
                fetchById(null);
                window.setDone(Messages.get().noRecordsFound());
                return false;
            } catch (Exception e) {
                fetchById(null);
                e.printStackTrace();
                Window.alert(Messages.get().fetchFailed() + e.getMessage());
                return false;
            }
        }
        DataChangeEvent.fire(this);
        window.clearStatus();

        return true;
    }

    private ArrayList<TableDataRow> getPanelItemTableModel() {
        int i;
        PanelItemDO data;
        ArrayList<TableDataRow> model;
        TableDataRow row;

        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.getItems().count(); i++ ) {
                data = manager.getItems().getItemAt(i);
                row = new TableDataRow(2);
                row.key = data.getId();
                row.cells.get(0).setValue(data.getName());
                row.cells.get(1).setValue(data.getMethodName());

                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }

    private TableDataRow getPanelItemRow(TableDataRow trow) {
        TableDataRow row;

        row = new TableDataRow(2);
        row.cells.get(0).setValue((String)trow.cells.get(0).getValue());
        row.cells.get(1).setValue((String)trow.cells.get(1).getValue());
        row.data = trow.data;
        return row;
    }

    private boolean itemExistsInPanel(TableDataRow row) throws Exception {        
        PanelItemDO data;
        String name, method;
        
        name = (String)row.cells.get(0).getValue();
        method = (String)row.cells.get(1).getValue();
        
        for (int j = 0; j < manager.getItems().count(); j++ ) {
            data = manager.getItems().getItemAt(j);
            if (data.getName().equals(name)) {                
                if (data.getMethodName() != null) {
                    if (data.getMethodName().equals(method)) 
                        return true;                                                                     
                } else {
                    return true;
                }
            }
        }
        return false;
    }
}