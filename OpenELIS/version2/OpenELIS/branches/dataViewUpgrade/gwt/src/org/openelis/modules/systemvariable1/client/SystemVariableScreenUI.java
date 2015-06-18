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
package org.openelis.modules.systemvariable1.client;

import static org.openelis.modules.main.client.Logger.logger;
import static org.openelis.ui.screen.Screen.ShortKeys.CTRL;
import static org.openelis.ui.screen.State.ADD;
import static org.openelis.ui.screen.State.DEFAULT;
import static org.openelis.ui.screen.State.DELETE;
import static org.openelis.ui.screen.State.DISPLAY;
import static org.openelis.ui.screen.State.QUERY;
import static org.openelis.ui.screen.State.UPDATE;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.meta.SystemVariableMeta;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.systemvariable.client.SystemVariableService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.ValidationErrorsList;
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
import org.openelis.ui.widget.AtoZButtons;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.Menu;
import org.openelis.ui.widget.MenuItem;
import org.openelis.ui.widget.TextArea;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.table.Table;

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

public class SystemVariableScreenUI extends Screen {

    @UiTemplate("SystemVariable.ui.xml")
    interface SystemVariableUiBinder extends UiBinder<Widget, SystemVariableScreenUI> {
    };

    public static final SystemVariableUiBinder     uiBinder = GWT.create(SystemVariableUiBinder.class);

    private SystemVariableDO                       data;

    protected ModulePermission                     userPermission;

    protected ScreenNavigator<IdNameVO>            nav;

    @UiField
    protected AtoZButtons                          atozButtons;

    @UiField
    protected Table                                atozTable;

    @UiField
    protected TextBox<String>                      name;

    @UiField
    protected TextArea                             description, value;

    @UiField
    protected Button                               query, previous, next, add, update, delete,
                    commit, abort, optionsButton, loadResults;

    @UiField
    protected Menu                                 optionsMenu;

    @UiField
    protected MenuItem                             systemVariableHistory;

    private SystemVariableService1Impl             service  = SystemVariableService1Impl.INSTANCE;

    protected AsyncCallbackUI<ArrayList<IdNameVO>> queryCall;

    protected AsyncCallbackUI<SystemVariableDO>    fetchForUpdateCall, fetchByIdCall, updateCall,
                    addCall, unlockCall;

    protected SystemVariableScreenUI               screen;

    public SystemVariableScreenUI(WindowInt window) throws Exception {
        setWindow(window);

        userPermission = UserCache.getPermission().getModule("systemvariable");
        if (userPermission == null)
            throw new PermissionException(Messages.get()
                                                  .gen_screenPermException("System Variable Screen"));

        initWidget(uiBinder.createAndBindUi(this));

        data = new SystemVariableDO();

        initialize();
        setState(DEFAULT);
        fireDataChange();

        logger.fine("System Variable Screen Opened");
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        screen = this;

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
                delete.setEnabled(isState(DISPLAY) && userPermission.hasDeletePermission());
                if (isState(DELETE)) {
                    delete.lock();
                    delete.setPressed(true);
                }
            }
        });

        addShortcut(delete, 'd', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                commit.setEnabled(isState(QUERY, ADD, UPDATE, DELETE));
            }
        });

        addShortcut(commit, 'm', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                abort.setEnabled(isState(QUERY, ADD, UPDATE, DELETE));
            }
        });

        addShortcut(abort, 'o', CTRL);

        //
        // screen fields
        //
        addScreenHandler(name, SystemVariableMeta.getName(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                name.setValue(getName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setName(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                name.setEnabled(isState(QUERY, ADD, UPDATE));
                name.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? description : value;
            }
        });

        // TODO
        addScreenHandler(description, "description", new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                description.setValue(getDescription());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                String text;

                text = event.getValue();
                if (text.length() > 256) {
                    // TODO what do we want to do if there are too many
                    // characters
                    description.addException(new Exception("The maximum length of the Description field is 256 characters"));
                } else {
                    // data.setDescription(description);
                }
            }

            public void onStateChange(StateChangeEvent event) {
                // TODO
                // description.setEnabled(isState(QUERY, ADD, UPDATE));
                description.setEnabled(false);
                // description.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? value : name;
            }
        });

        addScreenHandler(value, SystemVariableMeta.getValue(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                value.setValue(getValue());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setValue(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                value.setEnabled(isState(QUERY, ADD, UPDATE));
                value.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? name : description;
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
                            Window.alert("Error: Dictionary call query failed; " +
                                         error.getMessage());
                            setError(Messages.get().gen_queryFailed());
                        }
                    };
                }
                query.setRowsPerPage(15);
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
                systemVariableHistory.setEnabled(isState(DISPLAY));
            }
        });
        systemVariableHistory.addCommand(new Command() {
            @Override
            public void execute() {
                systemVariableHistory();
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
                if (isState(ADD, UPDATE, DELETE)) {
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
        field.setKey(SystemVariableMeta.getName());
        field.setQuery( ((Button)event.getSource()).getAction());
        field.setType(QueryData.Type.STRING);

        query = new Query();
        query.setFields(field);
        nav.setQuery(query);
    }

    @UiHandler("query")
    protected void query(ClickEvent event) {
        data = null;

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
        data = new SystemVariableDO();
        setState(ADD);
        fireDataChange();
        name.setFocus(true);
        setDone(Messages.get().enterInformationPressCommit());
    }

    @UiHandler("update")
    protected void update(ClickEvent event) {
        setBusy(Messages.get().lockForUpdate());

        if (fetchForUpdateCall == null) {
            fetchForUpdateCall = new AsyncCallbackUI<SystemVariableDO>() {
                public void success(SystemVariableDO result) {
                    data = result;
                    setState(UPDATE);
                    fireDataChange();
                    name.setFocus(true);
                }

                public void failure(Throwable e) {
                    logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
                    Window.alert(e.getMessage());
                }

                public void finish() {
                    clearStatus();
                }
            };
        }

        service.fetchForUpdate(data.getId(), fetchForUpdateCall);
    }

    @UiHandler("delete")
    protected void delete(ClickEvent event) {
        setBusy(Messages.get().lockForUpdate());

        try {
            data = SystemVariableService.get().fetchForUpdate(data.getId());

            setState(DELETE);
            fireDataChange();
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
        clearStatus();
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
                commitAdd();
                break;
            case UPDATE:
                commitUpdate();
                break;
            case DELETE:
                commitDelete();
                break;
        }
    }

    protected void commitQuery() {
        Query query;

        query = new Query();
        query.setFields(getQueryFields());
        nav.setQuery(query);
    }

    protected void commitAdd() {
        setBusy(Messages.get().gen_adding());

        if (addCall == null) {
            addCall = new AsyncCallbackUI<SystemVariableDO>() {
                public void success(SystemVariableDO result) {
                    data = result;
                    setState(DISPLAY);
                    fireDataChange();
                    clearStatus();
                }

                public void validationErrors(ValidationErrorsList e) {
                    showErrors(e);
                }

                public void failure(Throwable e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    Window.alert("commitAdd(): " + e.getMessage());
                    clearStatus();
                }
            };
        }

        service.add(data, addCall);
    }

    protected void commitUpdate() {
        setBusy(Messages.get().gen_updating());

        if (updateCall == null) {
            updateCall = new AsyncCallbackUI<SystemVariableDO>() {
                public void success(SystemVariableDO result) {
                    data = result;
                    setState(DISPLAY);
                    fireDataChange();
                    clearStatus();
                }

                public void validationErrors(ValidationErrorsList e) {
                    showErrors(e);
                }

                public void failure(Throwable e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    Window.alert("commitUpdate(): " + e.getMessage());
                    clearStatus();
                }
            };
        }

        service.update(data, updateCall);
    }

    protected void commitDelete() {
        setBusy(Messages.get().deleting());
        try {
            service.delete(data);
            fetchById(null);
            setDone(Messages.get().deleteComplete());
        } catch (ValidationErrorsList e) {
            showErrors(e);
        } catch (Exception e) {
            Window.alert("commitDelete(): " + e.getMessage());
            clearStatus();
        }
    }

    @UiHandler("abort")
    protected void abort(ClickEvent event) {
        finishEditing();
        clearErrors();
        setBusy(Messages.get().gen_cancelChanges());

        if (isState(QUERY)) {
            try {
                data = null;
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
                data = null;
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
                unlockCall = new AsyncCallbackUI<SystemVariableDO>() {
                    public void success(SystemVariableDO result) {
                        data = result;
                        setState(DISPLAY);
                        fireDataChange();
                        setDone(Messages.get().gen_updateAborted());
                    }

                    public void failure(Throwable e) {
                        data = null;
                        setState(DEFAULT);
                        fireDataChange();
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        Window.alert(e.getMessage());
                        clearStatus();
                    }
                };
            }
            service.unlock(data.getId(), unlockCall);
        } else if (isState(DELETE)) {
            if (unlockCall == null) {
                unlockCall = new AsyncCallbackUI<SystemVariableDO>() {
                    public void success(SystemVariableDO result) {
                        data = result;
                        setState(DISPLAY);
                        fireDataChange();
                        setDone(Messages.get().gen_deleteAborted());
                    }

                    public void failure(Throwable e) {
                        data = null;
                        setState(DEFAULT);
                        fireDataChange();
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        Window.alert(e.getMessage());
                        clearStatus();
                    }
                };
            }
            service.unlock(data.getId(), unlockCall);
        }
    }

    private String getName() {
        if (data == null)
            return null;
        return data.getName();
    }

    // TODO
    private String getDescription() {
        if (data == null)
            return null;
        return null;
    }

    private String getValue() {
        if (data == null)
            return null;
        return data.getValue();
    }

    protected void systemVariableHistory() {
        IdNameVO hist;

        hist = new IdNameVO(data.getId(), data.getName());
        HistoryScreen.showHistory(Messages.get().systemVariableHistory(),
                                  Constants.table().SYSTEM_VARIABLE,
                                  hist);
    }

    protected void fetchById(Integer id) {
        if (id == null) {
            data = null;
            setState(DEFAULT);
            fireDataChange();
        } else {
            setBusy(Messages.get().fetching());
            if (fetchByIdCall == null) {
                fetchByIdCall = new AsyncCallbackUI<SystemVariableDO>() {
                    public void success(SystemVariableDO result) {
                        data = result;
                        setState(DISPLAY);
                    }

                    public void notFound() {
                        fetchById(null);
                        setDone(Messages.get().gen_noRecordsFound());
                    }

                    public void failure(Throwable e) {
                        fetchById(null);
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        Window.alert(Messages.get().gen_fetchFailed() + e.getMessage());
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
}