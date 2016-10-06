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
package org.openelis.modules.provider1.client;

import static org.openelis.modules.main.client.Logger.logger;
import static org.openelis.ui.screen.Screen.ShortKeys.CTRL;
import static org.openelis.ui.screen.State.ADD;
import static org.openelis.ui.screen.State.DEFAULT;
import static org.openelis.ui.screen.State.DISPLAY;
import static org.openelis.ui.screen.State.QUERY;
import static org.openelis.ui.screen.State.UPDATE;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdFirstLastNameVO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ProviderAnalyteViewDO;
import org.openelis.domain.ProviderDO;
import org.openelis.domain.ProviderLocationDO;
import org.openelis.manager.ProviderManager1;
import org.openelis.meta.ProviderMeta;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.main.client.OpenELIS;
import org.openelis.ui.common.Caution;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.Warning;
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
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.Menu;
import org.openelis.ui.widget.MenuItem;
import org.openelis.ui.widget.TabLayoutPanel;
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

public class ProviderScreenUI extends Screen {

    @UiTemplate("Provider.ui.xml")
    interface ProviderUiBinder extends UiBinder<Widget, ProviderScreenUI> {
    };

    public static final ProviderUiBinder                    uiBinder = GWT.create(ProviderUiBinder.class);

    private ProviderManager1                                manager;

    protected ModulePermission                              userPermission;

    protected ScreenNavigator<IdFirstLastNameVO>            nav;

    @UiField
    protected AtoZButtons                                   atozButtons;

    @UiField
    protected Table                                         atozTable;

    @UiField
    protected Button                                        query, previous, next, add, update,
                    commit, abort, optionsButton, loadResults;

    @UiField
    protected Menu                                          optionsMenu;

    @UiField
    protected TextBox<String>                               lastName, firstName, npi, middleName;

    @UiField
    protected TextBox<Integer>                              id;

    @UiField
    protected Dropdown<Integer>                             typeId, source;

    @UiField
    protected MenuItem                                      providerHistory,
                    providerLocationHistory, providerAnalyteHistory;

    @UiField
    protected TabLayoutPanel                                tabPanel;

    @UiField(provided = true)
    protected LocationTabUI                                 locationTab;

    @UiField(provided = true)
    protected NoteTabUI                                     noteTab;

    @UiField(provided = true)
    protected AnalyteTabUI                                  analyteTab;

    protected AsyncCallbackUI<ArrayList<IdFirstLastNameVO>> queryCall;

    protected AsyncCallbackUI<ProviderManager1>             addCall, fetchForUpdateCall,
                    updateCall, fetchByIdCall, unlockCall;

    public ProviderScreenUI(WindowInt window) throws Exception {
        setWindow(window);

        userPermission = UserCache.getPermission().getModule("provider");
        if (userPermission == null)
            throw new PermissionException(Messages.get().gen_screenPermException("Provider Screen"));

        try {
            CategoryCache.getBySystemNames("provider_type", "provider_source", "state", "country");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            window.close();
        }

        locationTab = new LocationTabUI(this);
        noteTab = new NoteTabUI(this);
        analyteTab = new AnalyteTabUI(this);

        initWidget(uiBinder.createAndBindUi(this));

        manager = null;

        initialize();
        setState(DEFAULT);
        fireDataChange();

        logger.fine("Provider Screen Opened");
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        ArrayList<Item<Integer>> model;
        ArrayList<DictionaryDO> list;
        Item<Integer> row;

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

        //
        // screen fields
        //
        addScreenHandler(id, ProviderMeta.getId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent<Integer> event) {
                id.setValue(getId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getProvider().setId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                id.setEnabled(isState(QUERY));
                id.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? lastName : source;
            }
        });

        addScreenHandler(lastName, ProviderMeta.getLastName(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                lastName.setValue(getLastName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getProvider().setLastName(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                lastName.setEnabled(isState(QUERY, ADD, UPDATE));
                lastName.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? firstName : id;
            }
        });

        addScreenHandler(typeId, ProviderMeta.getTypeId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent<Integer> event) {
                typeId.setValue(getTypeId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getProvider().setTypeId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                typeId.setEnabled(isState(QUERY, ADD, UPDATE));
                typeId.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? npi : middleName;
            }
        });

        addScreenHandler(firstName, ProviderMeta.getFirstName(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                firstName.setValue(getFirstName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getProvider().setFirstName(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                firstName.setEnabled(isState(QUERY, ADD, UPDATE));
                firstName.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? middleName : lastName;
            }
        });

        addScreenHandler(npi, ProviderMeta.getNpi(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                npi.setValue(getNpi());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getProvider().setNpi(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                npi.setEnabled(isState(QUERY, ADD, UPDATE));
                npi.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? source : typeId;
            }
        });

        addScreenHandler(middleName, ProviderMeta.getMiddleName(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                middleName.setValue(getMiddleName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getProvider().setMiddleName(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                middleName.setEnabled(isState(QUERY, ADD, UPDATE));
                middleName.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? typeId : firstName;
            }
        });

        addScreenHandler(source, ProviderMeta.getReferenceSourceId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent<Integer> event) {
                source.setValue(getReferenceSourceId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getProvider().setReferenceSourceId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                source.setEnabled(isState(QUERY));
                source.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? id : npi;
            }
        });

        /*
         * tabs
         */
        tabPanel.setPopoutBrowser(OpenELIS.getBrowser());

        addScreenHandler(locationTab, "locationTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent<Object> event) {
                locationTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                locationTab.setState(event.getState());
            }

            public Object getQuery() {
                return locationTab.getQueryFields();
            }
        });

        addScreenHandler(noteTab, "noteTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent<Object> event) {
                noteTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                noteTab.setState(event.getState());
            }

            public Object getQuery() {
                return null;
            }
        });
        
        addScreenHandler(analyteTab, "analyteTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent<Object> event) {
                analyteTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                analyteTab.setState(event.getState());
            }

            public Object getQuery() {
                return analyteTab.getQueryFields();
            }
        });

        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator<IdFirstLastNameVO>(atozTable, loadResults) {
            public void executeQuery(final Query query) {
                setBusy(Messages.get().gen_querying());

                if (queryCall == null) {
                    queryCall = new AsyncCallbackUI<ArrayList<IdFirstLastNameVO>>() {
                        public void success(ArrayList<IdFirstLastNameVO> result) {
                            ArrayList<IdFirstLastNameVO> addedList;
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
                            Window.alert("Error: Provider call query failed; " + error.getMessage());
                            setError(Messages.get().gen_queryFailed());
                        }
                    };
                }
                query.setRowsPerPage(21);
                ProviderService1.get().query(query, queryCall);
            }

            public ArrayList<Item<Integer>> getModel() {
                ArrayList<IdFirstLastNameVO> result;
                ArrayList<Item<Integer>> model;
                Item<Integer> row;

                model = null;
                result = nav.getQueryResult();
                if (result != null) {
                    model = new ArrayList<Item<Integer>>();
                    for (IdFirstLastNameVO entry : result) {
                        row = new Item<Integer>(2);
                        row.setKey(entry.getId());
                        row.setCell(0, entry.getLastName());
                        row.setCell(1, entry.getFirstName());
                        model.add(row);
                    }
                }
                return model;
            }

            @Override
            public boolean fetch(IdFirstLastNameVO entry) {
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
                providerHistory.setEnabled(isState(DISPLAY));
            }
        });
        providerHistory.addCommand(new Command() {
            @Override
            public void execute() {
                providerHistory();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                providerLocationHistory.setEnabled(isState(DISPLAY));
            }
        });
        providerLocationHistory.addCommand(new Command() {
            @Override
            public void execute() {
                providerLocationHistory();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                providerAnalyteHistory.setEnabled(isState(DISPLAY));
            }
        });
        
        providerAnalyteHistory.addCommand(new Command() {
            @Override
            public void execute() {
                providerAnalyteHistory();
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
                } else {
                    /*
                     * make sure that all detached tabs are closed when the main
                     * screen is closed
                     */
                    tabPanel.close();
                }
            }
        });

        model = new ArrayList<Item<Integer>>();
        list = CategoryCache.getBySystemName("provider_type");
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        typeId.setModel(model);

        model = new ArrayList<Item<Integer>>();
        // TODO
        // list = CategoryCache.getBySystemName("provider_source");
        list = new ArrayList<DictionaryDO>();
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }
        source.setModel(model);
    }

    /*
     * basic button methods
     */
    @UiHandler("atozButtons")
    public void atozQuery(ClickEvent event) {
        Query query;
        QueryData field;

        field = new QueryData();
        field.setKey(ProviderMeta.getLastName());
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
        setData();
        setState(QUERY);
        fireDataChange();
        id.setFocus(true);
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
            addCall = new AsyncCallbackUI<ProviderManager1>() {
                public void success(ProviderManager1 result) {
                    manager = result;
                    setData();
                    setState(ADD);
                    fireDataChange();
                    lastName.setFocus(true);
                    setDone(Messages.get().gen_enterInformationPressCommit());
                }

                public void failure(Throwable error) {
                    Window.alert(error.getMessage());
                    logger.log(Level.SEVERE, error.getMessage(), error);
                    clearStatus();
                }
            };
        }

        ProviderService1.get().getInstance(addCall);
    }

    @UiHandler("update")
    protected void update(ClickEvent event) {
        setBusy(Messages.get().lockForUpdate());

        if (fetchForUpdateCall == null) {
            fetchForUpdateCall = new AsyncCallbackUI<ProviderManager1>() {
                public void success(ProviderManager1 result) {
                    manager = result;
                    setData();
                    setState(UPDATE);
                    fireDataChange();
                    lastName.setFocus(true);
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

        ProviderService1.get().fetchForUpdate(manager.getProvider().getId(), fetchForUpdateCall);
    }

    @UiHandler("commit")
    protected void commit(ClickEvent event) {
        commit(false);
    }

    private void commit(boolean ignoreWarning) {
        Validation validation;

        finishEditing();

        validation = validate();

        switch (validation.getStatus()) {
            case WARNINGS:
                /*
                 * show the warnings and ask the user if the data should still
                 * be committed; commit only if the user says yes
                 */
                if ( !Window.confirm(getWarnings(validation.getExceptions(), true)))
                    return;
                break;
            case FLAGGED:
                /*
                 * some part of the screen has some operation that needs to be
                 * completed before committing the data
                 */
                return;
            case ERRORS:
                setError(Messages.get().gen_correctErrors());
                return;
        }

        switch (super.state) {
            case QUERY:
                commitQuery();
                break;
            case ADD:
                commitUpdate(ignoreWarning);
                break;
            case UPDATE:
                commitUpdate(ignoreWarning);
                break;
        }
    }

    protected void commitQuery() {
        Query query;

        query = new Query();
        query.setFields(getQueryFields());
        nav.setQuery(query);
    }

    protected void commitUpdate(final boolean ignoreWarning) {
        if (isState(ADD))
            setBusy(Messages.get().gen_adding());
        else
            setBusy(Messages.get().gen_updating());

        if (updateCall == null) {
            updateCall = new AsyncCallbackUI<ProviderManager1>() {
                public void success(ProviderManager1 result) {
                    manager = result;
                    setData();
                    setState(DISPLAY);
                    fireDataChange();
                    if (isState(ADD))
                        setDone(Messages.get().addingComplete());
                    else
                        setDone(Messages.get().updatingComplete());
                }

                public void validationErrors(ValidationErrorsList e) {
                    showErrors(e);
                    if ( !e.hasErrors() && e.hasWarnings() && !ignoreWarning) {
                        if (Window.confirm(getWarnings(e.getErrorList(), true)))
                            commitUpdate(true);
                    }
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

        ProviderService1.get().update(manager, updateCall);
    }

    @UiHandler("abort")
    protected void abort(ClickEvent event) {
        finishEditing();
        clearErrors();
        setBusy(Messages.get().gen_cancelChanges());

        if (isState(QUERY)) {
            try {
                manager = null;
                setData();
                setState(DEFAULT);
                fireDataChange();
                setDone(Messages.get().gen_queryAborted());
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
                clearStatus();
            }
        } else if (isState(ADD)) {
            manager = null;
            setData();
            setState(DEFAULT);
            fireDataChange();
            setDone(Messages.get().gen_addAborted());
        } else if (isState(UPDATE)) {
            if (unlockCall == null) {
                unlockCall = new AsyncCallbackUI<ProviderManager1>() {
                    public void success(ProviderManager1 result) {
                        manager = result;
                        setData();
                        setState(DISPLAY);
                        fireDataChange();
                        setDone(Messages.get().gen_updateAborted());
                    }

                    public void failure(Throwable e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        clearStatus();
                    }
                };
            }
            ProviderService1.get().unlock(manager.getProvider().getId(), unlockCall);
        }
    }

    private void providerHistory() {
        IdNameVO hist;
        String name;
        ProviderDO data;

        data = manager.getProvider();

        if (data.getFirstName() != null)
            name = data.getLastName() + ", " + data.getFirstName();
        else
            name = data.getLastName();

        hist = new IdNameVO(data.getId(), name);
        HistoryScreen.showHistory(Messages.get().provider_history(),
                                  Constants.table().PROVIDER,
                                  hist);
    }

    private void providerLocationHistory() {
        int i, count;
        IdNameVO refVoList[];
        ProviderLocationDO data;

        try {
            count = manager.location.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = manager.location.get(i);
                refVoList[i] = new IdNameVO(data.getId(), data.getLocation());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(Messages.get().provider_locationHistory(),
                                  Constants.table().PROVIDER_LOCATION,
                                  refVoList);
    }
    
    protected void providerAnalyteHistory() {
        int i, count;
        IdNameVO refVoList[];
        ProviderAnalyteViewDO data;

        try {
            count = manager.analyte.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = manager.analyte.get(i);
                refVoList[i] = new IdNameVO(data.getId(), data.getAnalyteName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(Messages.get().provider_analyteHistory(),
                                  Constants.table().PROVIDER_ANALYTE,
                                  refVoList);
    }

    /*
     * getters and setters
     */
    private Integer getId() {
        if (manager == null)
            return null;
        return manager.getProvider().getId();
    }

    private String getLastName() {
        if (manager == null)
            return null;
        return manager.getProvider().getLastName();
    }

    private Integer getTypeId() {
        if (manager == null)
            return null;
        return manager.getProvider().getTypeId();
    }

    private String getFirstName() {
        if (manager == null)
            return null;
        return manager.getProvider().getFirstName();
    }

    private String getNpi() {
        if (manager == null)
            return null;
        return manager.getProvider().getNpi();
    }

    private String getMiddleName() {
        if (manager == null)
            return null;
        return manager.getProvider().getMiddleName();
    }

    private Integer getReferenceSourceId() {
        if (manager == null)
            return null;
        return manager.getProvider().getReferenceSourceId();
    }

    /**
     * Sets the latest manager in the tabs
     */
    private void setData() {
        locationTab.setData(manager);
        noteTab.setData(manager);
        analyteTab.setData(manager);
    }

    private void fetchById(Integer id) {
        if (id == null) {
            manager = null;
            setData();
            setState(DEFAULT);
            fireDataChange();
            clearStatus();
        } else {
            setBusy(Messages.get().gen_fetching());
            if (fetchByIdCall == null) {
                fetchByIdCall = new AsyncCallbackUI<ProviderManager1>() {
                    public void success(ProviderManager1 result) {
                        manager = result;
                        setData();
                        setState(DISPLAY);
                    }

                    public void notFound() {
                        fetchById(null);
                        setDone(Messages.get().gen_noRecordsFound());
                    }

                    public void failure(Throwable e) {
                        fetchById(null);
                        Window.alert(Messages.get().gen_fetchFailed() + e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                    }

                    public void finish() {
                        fireDataChange();
                        clearStatus();
                    }
                };
            }

            ProviderService1.get().fetchById(id, fetchByIdCall);
        }
    }

    /**
     * Creates a string containing the message that there are warnings on the
     * screen, followed by all warning messages, followed by the question
     * whether the data should be committed
     */
    private String getWarnings(ArrayList<Exception> warnings, boolean isConfirm) {
        StringBuilder b;

        b = new StringBuilder();
        b.append(Messages.get().gen_warningDialogLine1()).append("\n");
        if (warnings != null) {
            for (Exception ex : warnings) {
                if (ex instanceof Warning || ex instanceof Caution)
                    b.append(" * ").append(ex.getMessage()).append("\n");
            }
        }

        if (isConfirm)
            b.append("\n").append(Messages.get().gen_warningDialogLastLine());

        return b.toString();
    }
}
