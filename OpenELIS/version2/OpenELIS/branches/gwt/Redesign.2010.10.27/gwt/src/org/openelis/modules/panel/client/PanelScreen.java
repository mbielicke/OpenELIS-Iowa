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

import org.openelis.domain.IdNameVO;
import org.openelis.domain.PanelItemDO;
import org.openelis.domain.PanelVO;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.PermissionException;
import org.openelis.gwt.common.ModulePermission;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.BeforeCloseEvent;
import org.openelis.gwt.event.BeforeCloseHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.PanelItemManager;
import org.openelis.manager.PanelManager;
import org.openelis.meta.PanelMeta;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PanelScreen extends Screen {
    private PanelManager     manager;
    private ModulePermission userPermission;

    private ButtonGroup      atoz;
    private ScreenNavigator  nav;

    private PanelScreen      screen;
    private TextBox          name, description;
    private Button           queryButton, previousButton, nextButton, addButton, updateButton,
                             deleteButton, commitButton, abortButton, addTestButton, removeTestButton,
                             moveUpButton, moveDownButton, refreshButton;
    protected MenuItem       panelHistory, panelItemHistory;
    private Table            panelItemTable, allTestsTable;
    private ScreenService    testService;

    public PanelScreen() throws Exception {
        super((ScreenDefInt)GWT.create(PanelDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.panel.server.PanelService");
        testService = new ScreenService("controller?service=org.openelis.modules.test.server.TestService");

        userPermission = OpenELIS.getSystemUserPermission().getModule("panel");
        if (userPermission == null)
            throw new PermissionException("screenPermException", "Panel Screen");

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }

    private void postConstructor() {
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

        deleteButton = (Button)def.getWidget("delete");
        addScreenHandler(deleteButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                delete();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                deleteButton.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()) &&
                                    userPermission.hasDeletePermission());
                if (event.getState() == State.DELETE) {
                    deleteButton.setPressed(true);
                    deleteButton.lock();
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

        panelHistory = (MenuItem)def.getWidget("panelHistory");
        addScreenHandler(panelHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                panelHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                panelHistory.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        panelItemHistory = (MenuItem)def.getWidget("panelItemHistory");
        addScreenHandler(panelItemHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                panelItemHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                panelItemHistory.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
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
                name.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
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
                description.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
                description.setQueryMode(event.getState() == State.QUERY);
            }
        });

        panelItemTable = (Table)def.getWidget("panelItemTable");
        addScreenHandler(panelItemTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    panelItemTable.setModel(getPanelItemTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                panelItemTable.setEnabled(true);
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
                PanelItemDO data;
                int r;

                r = event.getIndex();
                try {
                    data = new PanelItemDO();
                    data.setTestName((String)panelItemTable.getValueAt(r, 0));
                    data.setMethodName((String)panelItemTable.getValueAt(r, 1));
                    manager.getItems().addItem(data);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });

        panelItemTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getItems().remoteItemAt(event.getIndex());
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });

        addTestButton = (Button)def.getWidget("addTestButton");
        addScreenHandler(addTestButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                Row selRow;
                PanelVO pvo;
                Integer[] selRows;
                boolean ok;

                selRows = allTestsTable.getSelectedRows();
                try {
                    for (int i = 0; i < selRows.length; i++ ) {
                        selRow = allTestsTable.getRowAt(selRows[i]);
                        pvo = (PanelVO)selRow.getData();

                        if (itemExistsInPanel(pvo)) {
                            ok = com.google.gwt.user.client.Window.confirm(consts.get("testAlreadyAdded"));
                            if (ok)
                                panelItemTable.addRow(getPanelItemRow(pvo));
                        } else {
                            panelItemTable.addRow(getPanelItemRow(pvo));
                        }
                    }
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addTestButton.setEnabled(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        allTestsTable = (Table)def.getWidget("allTestsTable");
        addScreenHandler(allTestsTable, new ScreenEventHandler<ArrayList<Row>>() {

            public void onStateChange(StateChangeEvent<State> event) {
                allTestsTable.setEnabled(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        removeTestButton = (Button)def.getWidget("removeTestButton");
        addScreenHandler(removeTestButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = panelItemTable.getSelectedRow();
                if (r > -1 && panelItemTable.getRowCount() > 0)
                    panelItemTable.removeRowAt(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeTestButton.setEnabled(EnumSet.of(State.ADD, State.UPDATE)
                                               .contains(event.getState()));
            }
        });

        moveUpButton = (Button)def.getWidget("moveUpButton");
        addScreenHandler(moveUpButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = panelItemTable.getSelectedRow();
                if (r <= 0)
                    return;

                try {
                    manager.getItems().moveItem(r, r - 1);
                    DataChangeEvent.fire(screen, panelItemTable);
                    panelItemTable.selectRowAt(r - 1);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }

            }

            public void onStateChange(StateChangeEvent<State> event) {
                moveUpButton.setEnabled(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        moveDownButton = (Button)def.getWidget("moveDownButton");
        addScreenHandler(moveDownButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = panelItemTable.getSelectedRow();
                if (r >= panelItemTable.getRowCount() - 1)
                    return;

                try {
                    manager.getItems().moveItem(r, r + 1);
                    DataChangeEvent.fire(screen, panelItemTable);
                    panelItemTable.selectRowAt(r + 1);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                moveDownButton.setEnabled(EnumSet.of(State.ADD, State.UPDATE)
                                             .contains(event.getState()));
            }
        });

        refreshButton = (Button)def.getWidget("refreshButton");
        addScreenHandler(refreshButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                window.setBusy(consts.get("fetching"));
                initializeAllTestTable();
                if (state == State.ADD)
                    window.setDone(consts.get("enterInformationPressCommit"));
                else
                    window.clearStatus();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                refreshButton.setEnabled(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
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
                            com.google.gwt.user.client.Window.alert("Error: Panel call query failed; " + error.getMessage());
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
                        model.add(new Item<Integer>(entry.getId(), entry.getName()));
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

                field = new QueryData();
                field.key = PanelMeta.getName();
                field.query = ((Button)event.getSource()).getAction();
                field.type = QueryData.Type.STRING;

                query = new Query();
                query.setFields(field);
                nav.setQuery(query);
            }
        });

        window.addBeforeClosedHandler(new BeforeCloseHandler<Window>() {
            public void onBeforeClosed(BeforeCloseEvent<Window> event) {
                if (EnumSet.of(State.ADD, State.UPDATE, State.DELETE).contains(state)) {
                    event.cancel();
                    window.setError(consts.get("mustCommitOrAbort"));
                }
            }
        });
    }

    private void initializeAllTestTable() {
        ArrayList<PanelVO> list;
        ArrayList<Row> model;
        Row row;

        try {
            list = testService.callList("fetchNameMethodSectionByName", "");
            model = new ArrayList<Row>();
            for (PanelVO data : list) {
                row = new Row(data.getTestName(), data.getMethodName(), data.getSectionName());
                row.setData(data);
                model.add(row);
            }
            allTestsTable.setModel(model);
        } catch (Exception e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert(e.getMessage());
            window.close();
        }

    }

    protected void query() {
        manager = PanelManager.getInstance();

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
        manager = PanelManager.getInstance();

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

    protected void delete() {
        window.setBusy(consts.get("lockForUpdate"));

        try {
            manager = manager.fetchForUpdate();

            setState(State.DELETE);
            DataChangeEvent.fire(this);
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
        }
        window.clearStatus();
    }

    protected void commit() {
        setFocus(null);
        allTestsTable.clearRowSelection();

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
        } else if (state == State.DELETE) {
            window.setBusy(consts.get("deleting"));
            try {
                manager.delete();

                fetchById(null);
                window.setDone(consts.get("deleteComplete"));
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                com.google.gwt.user.client.Window.alert("commitDelete(): " + e.getMessage());
                window.clearStatus();
            }
        }
    }

    protected void abort() {
        setFocus(null);
        allTestsTable.clearRowSelection();
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
        } else if (state == State.DELETE) {
            try {
                manager = manager.abortUpdate();
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
            } catch (Exception e) {
                com.google.gwt.user.client.Window.alert(e.getMessage());
                fetchById(null);
            }
            window.setDone(consts.get("deleteAborted"));
        } else {
            window.clearStatus();
        }
    }

    protected void panelHistory() {
        IdNameVO hist;

        hist = new IdNameVO(manager.getPanel().getId(), manager.getPanel().getName());
        HistoryScreen.showHistory(consts.get("panelHistory"), ReferenceTable.PANEL, hist);
    }

    protected void panelItemHistory() {
        int i, count;
        IdNameVO refVoList[];
        PanelItemManager man;
        PanelItemDO data;

        try {
            man = manager.getItems();
            count = man.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = man.getItemAt(i);
                refVoList[i] = new IdNameVO(data.getId(), data.getTestName() + "," +
                                                          data.getMethodName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(consts.get("panelItemHistory"), ReferenceTable.PANEL_ITEM,
                                  refVoList);
    }

    protected boolean fetchById(Integer id) {
        if (id == null) {
            manager = PanelManager.getInstance();
            setState(State.DEFAULT);
        } else {
            window.setBusy(consts.get("fetching"));
            try {
                manager = PanelManager.fetchWithItems(id);
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

    private ArrayList<Row> getPanelItemTableModel() {
        int i;
        PanelItemDO data;
        ArrayList<Row> model;
        Row row;

        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.getItems().count(); i++ ) {
                data = manager.getItems().getItemAt(i);
                row = new Row(2);
//              row.key = data.getId();
                row.setCell(0,data.getTestName());
                row.setCell(1,data.getMethodName());

                model.add(row);
            }
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }

    private Row getPanelItemRow(PanelVO data) {
        Row row;

        row = new Row(2);
        row.setCell(0,data.getTestName());
        row.setCell(1,data.getMethodName());

        return row;
    }

    private boolean itemExistsInPanel(PanelVO pvo) throws Exception {
        PanelItemDO data;
        for (int j = 0; j < manager.getItems().count(); j++ ) {
            data = manager.getItems().getItemAt(j);
            if (data.getTestName().equals(pvo.getTestName()) &&
                data.getMethodName().equals(pvo.getMethodName())) {
                return true;
            }
        }
        return false;
    }
}