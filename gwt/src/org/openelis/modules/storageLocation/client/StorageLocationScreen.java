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
package org.openelis.modules.storageLocation.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.StorageLocationViewDO;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.QueryFieldUtil;
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
import org.openelis.manager.StorageLocationChildManager;
import org.openelis.manager.StorageLocationManager;
import org.openelis.meta.StorageLocationMeta;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.storageunit.client.StorageUnitService;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class StorageLocationScreen extends Screen {
    private StorageLocationManager manager;
    private ModulePermission       userPermission;

    private AutoComplete<Integer>  storageUnit, childStorageUnit;
    private TextBox                name, location;
    private CheckBox               isAvailable;
    private AppButton              queryButton, previousButton, nextButton, addButton,
                                   updateButton, commitButton, abortButton, addChildButton, removeChildButton;
    protected MenuItem             storageLocationHistory, subLocationHistory;
    private ButtonGroup            atoz;
    private ScreenNavigator        nav;
    private TableWidget            childStorageLocsTable;

    public StorageLocationScreen(WindowInt window) throws Exception {
        super((ScreenDefInt)GWT.create(StorageLocationDef.class));
        
        setWindow(window);

        userPermission = UserCache.getPermission().getModule("storagelocation");
        if (userPermission == null)
            throw new PermissionException(Messages.get().screenPermException("StorageLocation Screen"));

        manager = StorageLocationManager.getInstance();

        initialize();
        setState(State.DEFAULT);
        DataChangeEvent.fire(this);
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
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
        
        storageLocationHistory = (MenuItem)def.getWidget("storageLocationHistory");
        addScreenHandler(storageLocationHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                storageLocationHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                storageLocationHistory.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        subLocationHistory = (MenuItem)def.getWidget("subLocationHistory");
        addScreenHandler(subLocationHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                subLocationHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                subLocationHistory.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        name = (TextBox)def.getWidget(StorageLocationMeta.getName());
        addScreenHandler(name, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                name.setValue(manager.getStorageLocation().getName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getStorageLocation().setName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                name.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                   .contains(event.getState()));
                name.setQueryMode(event.getState() == State.QUERY);
            }
        });

        location = (TextBox)def.getWidget(StorageLocationMeta.getLocation());
        addScreenHandler(location, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                location.setValue(manager.getStorageLocation().getLocation());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getStorageLocation().setLocation(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                location.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                       .contains(event.getState()));
                location.setQueryMode(event.getState() == State.QUERY);
            }
        });

        storageUnit = (AutoComplete)def.getWidget(StorageLocationMeta.getStorageUnitDescription());
        addScreenHandler(storageUnit, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                storageUnit.setSelection(manager.getStorageLocation().getStorageUnitId(),
                                         manager.getStorageLocation().getStorageUnitDescription());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getStorageLocation().setStorageUnitId(event.getValue());
                manager.getStorageLocation()
                       .setStorageUnitDescription(storageUnit.getTextBoxDisplay());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                storageUnit.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
                storageUnit.setQueryMode(event.getState() == State.QUERY);
            }
        });

        storageUnit.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                IdNameVO data;
                ArrayList<IdNameVO> list;
                ArrayList<TableDataRow> model;

                try {
                    list = StorageUnitService.get().fetchByDescription(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<TableDataRow>();

                    for (int i = 0; i < list.size(); i++ ) {
                        data = list.get(i);
                        model.add(new TableDataRow(data.getId(), data.getName(),
                                                   data.getDescription()));
                    }
                    storageUnit.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        isAvailable = (CheckBox)def.getWidget(StorageLocationMeta.getIsAvailable());
        addScreenHandler(isAvailable, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isAvailable.setValue(manager.getStorageLocation().getIsAvailable());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getStorageLocation().setIsAvailable(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isAvailable.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
                isAvailable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        childStorageLocsTable = (TableWidget)def.getWidget("childStorageLocsTable");
        childStorageUnit = (AutoComplete<Integer>)childStorageLocsTable.getColumnWidget(StorageLocationMeta.getChildStorageUnitDescription());

        addScreenHandler(childStorageLocsTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    childStorageLocsTable.load(getChildLocationModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                childStorageLocsTable.enable(true);
                childStorageLocsTable.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        childStorageLocsTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if(state != State.ADD && state != State.UPDATE && state != State.QUERY) 
                    event.cancel();
                
            }
            
        });

        childStorageLocsTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                StorageLocationViewDO data;
                TableDataRow row;

                r = event.getRow();
                c = event.getCol();
                val = childStorageLocsTable.getObject(r, c);
                try {
                    data = manager.getChildren().getChildAt(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }

                switch (c) {
                    case 0:
                        row = (TableDataRow)val;
                        data.setStorageUnitId((Integer)row.key);
                        data.setStorageUnitDescription(childStorageUnit.getTextBoxDisplay());
                        break;
                    case 1:
                        data.setLocation((String)val);
                        break;
                    case 2:
                        data.setIsAvailable((String)val);
                        break;
                }
            }
        });

        childStorageLocsTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                int r;
                StorageLocationViewDO data;

                r = event.getIndex();
                try {
                    data = new StorageLocationViewDO();
                    data.setIsAvailable("Y");
                    manager.getChildren().addChildAt(data, r);
                    childStorageLocsTable.setCell(r, 2, "Y");
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        childStorageLocsTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getChildren().removeChildAt(event.getIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        childStorageUnit.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                IdNameVO data;
                ArrayList<IdNameVO> list;
                ArrayList<TableDataRow> model;

                try {
                    list = StorageUnitService.get().fetchByDescription(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<TableDataRow>();

                    for (int i = 0; i < list.size(); i++ ) {
                        data = list.get(i);
                        model.add(new TableDataRow(data.getId(), data.getName(),
                                                   data.getDescription()));
                    }
                    childStorageUnit.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        addChildButton = (AppButton)def.getWidget("addChildButton");
        addScreenHandler(addChildButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = childStorageLocsTable.getSelectedRow() + 1;
                if (r == 0)
                    r = childStorageLocsTable.numRows();

                childStorageLocsTable.addRow(r);
                childStorageLocsTable.selectRow(r);
                childStorageLocsTable.scrollToSelection();
                childStorageLocsTable.startEditing(r, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addChildButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                             .contains(event.getState()));
            }
        });

        removeChildButton = (AppButton)def.getWidget("removeChildButton");
        addScreenHandler(removeChildButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = childStorageLocsTable.getSelectedRow();
                try {
                    if (r > -1 && childStorageLocsTable.numRows() > 0) {
                        window.setBusy(Messages.get().validatingDelete());
                        validateForDelete(manager.getChildren().getChildAt(r));
                        childStorageLocsTable.deleteRow(r);
                    }
                } catch (ValidationErrorsList e) {
                    Window.alert(Messages.get().storageLocationDeleteException());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    e.printStackTrace();
                }
                window.clearStatus();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeChildButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                                .contains(event.getState()));
            }
        });

        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator<IdNameVO>(def) {
            public void executeQuery(final Query query) {
                window.setBusy(Messages.get().querying());

                query.setRowsPerPage(18);
                StorageLocationService.get().query(query, new AsyncCallback<ArrayList<IdNameVO>>() {
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
                            Window.alert("Error: Storage Location call query failed; " +
                                         error.getMessage());
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
                field.setKey(StorageLocationMeta.getName());
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

    /*
     * basic button methods
     */

    protected void query() {
        manager = StorageLocationManager.getInstance();

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
        manager = StorageLocationManager.getInstance();
        manager.getStorageLocation().setIsAvailable("Y");

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
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
        window.clearStatus();
    }

    public void commit() {
        setFocus(null);

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
        }
    }

    protected void abort() {
        setFocus(null);
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
    
    protected void storageLocationHistory() {
        IdNameVO hist;
        
        hist = new IdNameVO(manager.getStorageLocation().getId(), manager.getStorageLocation().getName());
        HistoryScreen.showHistory(Messages.get().storageLocationHistory(),
                                  Constants.table().STORAGE_LOCATION, hist);        
        
    }

    protected void subLocationHistory() {
        int i, count;
        IdNameVO refVoList[];
        StorageLocationChildManager man;
        StorageLocationViewDO data;

        try {
            man = manager.getChildren();
            count = man.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = man.getChildAt(i);
                refVoList[i] = new IdNameVO(data.getId(), data.getStorageUnitDescription());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(Messages.get().subLocationHistory(),
                                  Constants.table().STORAGE_LOCATION, refVoList);        
    }

    protected boolean fetchById(Integer id) {
        if (id == null) {
            manager = StorageLocationManager.getInstance();
            setState(State.DEFAULT);
        } else {
            window.setBusy(Messages.get().fetching());
            try {
                manager = StorageLocationManager.fetchWithChildren(id);
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

    private ArrayList<TableDataRow> getChildLocationModel() {
        int i;
        StorageLocationViewDO data;
        ArrayList<TableDataRow> model;
        TableDataRow row;

        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.getChildren().count(); i++ ) {
                data = manager.getChildren().getChildAt(i);
                row = new TableDataRow(3);
                row.key = data.getId();
                row.cells.get(0).setValue(new TableDataRow(data.getStorageUnitId(),
                                                           data.getStorageUnitDescription()));
                row.cells.get(1).setValue(data.getLocation());
                row.cells.get(2).setValue(data.getIsAvailable());

                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }

    private void validateForDelete(StorageLocationViewDO data) throws Exception {
        if (data.getId() == null)
            return;
        StorageLocationService.get().validateForDelete(data);
    }
}
