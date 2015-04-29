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
package org.openelis.modules.analyte1.client;

import static org.openelis.modules.main.client.Logger.logger;
import static org.openelis.ui.screen.Screen.ShortKeys.CTRL;
import static org.openelis.ui.screen.State.ADD;
import static org.openelis.ui.screen.State.DEFAULT;
import static org.openelis.ui.screen.State.DISPLAY;
import static org.openelis.ui.screen.State.QUERY;
import static org.openelis.ui.screen.State.UPDATE;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalyteDO;
import org.openelis.domain.AnalyteViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.meta.AnalyteMeta;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.ScreenNavigator;
import org.openelis.ui.widget.AtoZButtons;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.Menu;
import org.openelis.ui.widget.MenuItem;
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

public class AnalyteScreenUI extends Screen {

    @UiTemplate("Analyte.ui.xml")
    interface AnalyteUiBinder extends UiBinder<Widget, AnalyteScreenUI> {
    };

    public static final AnalyteUiBinder            uiBinder = GWT.create(AnalyteUiBinder.class);

    private AnalyteViewDO                          data;

    protected ModulePermission                     userPermission;

    protected ScreenNavigator<IdNameVO>            nav;

    @UiField
    protected AtoZButtons                          atozButtons;

    @UiField
    protected Table                                atozTable;

    @UiField
    protected TextBox<Integer>                     id;

    @UiField
    protected TextBox<String>                      name, externalId;

    @UiField
    protected Button                               query, previous, next, add, update, commit,
                    abort, optionsButton, loadResults;

    @UiField
    protected Menu                                 optionsMenu;

    @UiField
    protected MenuItem                             analyteHistory;

    @UiField
    protected AutoComplete                         parentAnalyte;

    @UiField
    protected CheckBox                             isActive;

    protected AnalyteScreenUI                      screen;

    private AnalyteService1Impl                    service  = AnalyteService1Impl.INSTANCE;

    protected AsyncCallbackUI<ArrayList<IdNameVO>> queryCall;

    private AsyncCallbackUI<AnalyteViewDO>         fetchForUpdateCall, fetchByIdCall, unlockCall;

    private AsyncCallbackUI<AnalyteDO>             updateCall;

    public AnalyteScreenUI(WindowInt window) throws Exception {
        setWindow(window);

        userPermission = UserCache.getPermission().getModule("analyte");
        if (userPermission == null)
            throw new PermissionException(Messages.get().gen_screenPermException("Analyte Screen"));

        initWidget(uiBinder.createAndBindUi(this));

        data = new AnalyteViewDO();

        initialize();
        setState(DEFAULT);
        fireDataChange();

        logger.fine("Analyte Screen Opened");
    }

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

        //
        // screen fields
        //
        addScreenHandler(id, AnalyteMeta.getId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                id.setValue(getId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                data.setId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                id.setEnabled(isState(QUERY));
                id.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? name : isActive;
            }
        });

        addScreenHandler(name, AnalyteMeta.getName(), new ScreenHandler<String>() {
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
                return forward ? parentAnalyte : id;
            }
        });

        addScreenHandler(parentAnalyte,
                         AnalyteMeta.getParentAnalyteName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 setParentAnalyteSelection();
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 getParentAnalyteFromSelection();
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 parentAnalyte.setEnabled(isState(QUERY, ADD, UPDATE));
                                 parentAnalyte.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? externalId : name;
                             }
                         });

        parentAnalyte.addGetMatchesHandler(new GetMatchesHandler() {
            @Override
            public void onGetMatches(GetMatchesEvent event) {
                getParentAnalyteMatches(event.getMatch());
            }
        });

        addScreenHandler(externalId, AnalyteMeta.getExternalId(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                externalId.setValue(getExternalId());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setExternalId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                externalId.setEnabled(isState(QUERY, ADD, UPDATE));
                externalId.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? isActive : parentAnalyte;
            }
        });

        addScreenHandler(isActive, AnalyteMeta.getIsActive(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isActive.setValue(getIsActive());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setIsActive(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                isActive.setEnabled(isState(QUERY, ADD, UPDATE));
                isActive.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? id : externalId;
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
                query.setRowsPerPage(14);
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
                analyteHistory.setEnabled(isState(DISPLAY));
            }
        });
        analyteHistory.addCommand(new Command() {
            @Override
            public void execute() {
                analyteHistory();
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
        field.setKey(AnalyteMeta.getName());
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
        data = new AnalyteViewDO();
        data.setIsActive("Y");
        setState(ADD);
        fireDataChange();
        name.setFocus(true);
        setDone(Messages.get().enterInformationPressCommit());
    }

    @UiHandler("update")
    protected void update(ClickEvent event) {
        setBusy(Messages.get().lockForUpdate());

        if (fetchForUpdateCall == null) {
            fetchForUpdateCall = new AsyncCallbackUI<AnalyteViewDO>() {
                public void success(AnalyteViewDO result) {
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
            updateCall = new AsyncCallbackUI<AnalyteDO>() {
                public void success(AnalyteDO result) {
                    data = (AnalyteViewDO)result;
                    setState(DISPLAY);
                    fireDataChange();
                    clearStatus();
                }

                public void validationErrors(ValidationErrorsList e) {
                    showErrors(e);
                }

                public void failure(Throwable e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    if (isState(ADD))
                        Window.alert("commitAdd(): " + e.getMessage());
                    else
                        Window.alert("commitUpdate(): " + e.getMessage());
                    clearStatus();
                }
            };
        }

        service.update(data, updateCall);
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
                unlockCall = new AsyncCallbackUI<AnalyteViewDO>() {
                    public void success(AnalyteViewDO result) {
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
            service.abortUpdate(data.getId(), unlockCall);
        }
    }

    /*
     * getters and setters
     */
    private Integer getId() {
        if (data == null)
            return null;
        return data.getId();
    }

    private String getName() {
        if (data == null)
            return null;
        return data.getName();
    }

    private String getExternalId() {
        if (data == null)
            return null;
        return data.getExternalId();
    }

    private String getIsActive() {
        if (data == null)
            return null;
        return data.getIsActive();
    }

    private void setParentAnalyteSelection() {
        if (data != null)
            parentAnalyte.setValue(data.getParentAnalyteId(), data.getParentAnalyteName());
        else
            parentAnalyte.setValue(null, "");
    }

    private void getParentAnalyteFromSelection() {
        AutoCompleteValue row;

        row = parentAnalyte.getValue();
        if (row == null || row.getId() == null) {
            /*
             * this method is called only when the parent analyte changes and if
             * there isn't a parent analyte selected currently, then there must
             * have been before, thus it needs to be removed from the data
             * object
             */
            data.setParentAnalyteId(null);
            data.setParentAnalyteName(null);
            parentAnalyte.setValue(null, "");
        } else {
            data.setParentAnalyteId(row.getId());
            data.setParentAnalyteName(row.getDisplay());
        }
    }

    private void getParentAnalyteMatches(String match) {
        AnalyteDO data;
        ArrayList<AnalyteDO> list;
        ArrayList<Item<Integer>> model;

        try {
            list = service.fetchByName(QueryFieldUtil.parseAutocomplete(match));
            model = new ArrayList<Item<Integer>>();

            for (int i = 0; i < list.size(); i++ ) {
                data = list.get(i);
                model.add(new Item<Integer>(data.getId(), data.getName()));
            }
            parentAnalyte.showAutoMatches(model);
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
    }

    protected void analyteHistory() {
        IdNameVO hist;

        hist = new IdNameVO(data.getId(), data.getName());
        HistoryScreen.showHistory(Messages.get().analyteHistory(), Constants.table().ANALYTE, hist);
    }

    protected void fetchById(Integer id) {
        if (id == null) {
            data = null;
            setState(DEFAULT);
            fireDataChange();
            clearStatus();
        } else {
            setBusy(Messages.get().fetching());
            if (fetchByIdCall == null) {
                fetchByIdCall = new AsyncCallbackUI<AnalyteViewDO>() {
                    public void success(AnalyteViewDO result) {
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
