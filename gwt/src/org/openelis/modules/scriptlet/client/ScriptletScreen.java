package org.openelis.modules.scriptlet.client;

import static org.openelis.ui.screen.Screen.ShortKeys.CTRL;
import static org.openelis.ui.screen.Screen.Validation.Status.VALID;
import static org.openelis.ui.screen.State.ADD;
import static org.openelis.ui.screen.State.DEFAULT;
import static org.openelis.ui.screen.State.DELETE;
import static org.openelis.ui.screen.State.DISPLAY;
import static org.openelis.ui.screen.State.QUERY;
import static org.openelis.ui.screen.State.UPDATE;

import java.util.ArrayList;

import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ScriptletDO;
import org.openelis.meta.ScriptletMeta;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.ui.common.Datetime;
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
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.ButtonGroup;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.Menu;
import org.openelis.ui.widget.MenuItem;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.calendar.Calendar;
import org.openelis.ui.widget.table.Table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class ScriptletScreen extends Screen {

    @UiTemplate("Scriptlet.ui.xml")
    interface ScriptletUiBinder extends UiBinder<Widget, ScriptletScreen> {
    };

    public static final ScriptletUiBinder uiBinder = GWT.create(ScriptletUiBinder.class);

    private ScriptletDO                   data;
    private ModulePermission              userPermission;

    @UiField
    protected TextBox<String>             name, bean;

    @UiField
    protected CheckBox                    isActive;

    @UiField
    protected Calendar                    activeBegin, activeEnd;

    @UiField
    protected Button                      query, add, update, commit, abort, next, previous,
                                          optionsButton, atozNext, atozPrev;

    @UiField
    protected ButtonGroup                 atozButtons;

    @UiField
    protected Menu                        optionsMenu;

    @UiField
    protected MenuItem                    history;

    @UiField
    protected Table                       atozTable;

    private ScreenNavigator<IdNameVO>     nav;
    
    private AsyncCallbackUI<ScriptletDO>  fetchCall, abortCall, updateCall, fetchForUpdateCall, addCall;
    
    private AsyncCallbackUI<ArrayList<IdNameVO>>     queryCall;

    public ScriptletScreen(WindowInt window) throws Exception {
        setWindow(window);

        initWidget(uiBinder.createAndBindUi(this));

        userPermission = UserCache.getPermission().getModule("scriptlet");
        if (userPermission == null)
            throw new PermissionException(Messages.get().screenPermException("Scriptlet Screen"));

        data = new ScriptletDO();

        initialize();
        setState(DEFAULT);
        fireDataChange();
    }

    protected void initialize() {
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

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                optionsMenu.setEnabled(isState(DISPLAY));
                optionsButton.setEnabled(isState(DISPLAY));
                history.setEnabled(isState(DISPLAY));
            }
        });

        history.addCommand(new Command() {

            @Override
            public void execute() {
                history();
            }
        });

        addScreenHandler(name, ScriptletMeta.getName(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                name.setValue(data.getName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setName(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                name.setEnabled(isState(QUERY, ADD, UPDATE));
                name.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? bean : activeEnd;
            }
        });

        addScreenHandler(bean, ScriptletMeta.getBean(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                bean.setValue(data.getBean());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setBean(event.getValue());
                bean.clearEndUserExceptions();
            }

            public void onStateChange(StateChangeEvent event) {
                bean.setEnabled(isState(QUERY, ADD, UPDATE));
                bean.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? isActive : name;
            }
        });

        addScreenHandler(isActive, ScriptletMeta.getIsActive(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isActive.setValue(data.getIsActive());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setIsActive(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                isActive.setEnabled(isState(QUERY, ADD, UPDATE));
                isActive.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? activeBegin : bean;
            }
        });

        addScreenHandler(activeBegin, ScriptletMeta.getIsActive(), new ScreenHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                activeBegin.setValue(data.getActiveBegin());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                data.setActiveBegin(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                activeBegin.setEnabled(isState(QUERY, ADD, UPDATE));
                activeBegin.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? activeEnd : isActive;
            }
        });

        addScreenHandler(activeEnd, ScriptletMeta.getIsActive(), new ScreenHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                activeEnd.setValue(data.getActiveEnd());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                data.setActiveEnd(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                activeEnd.setEnabled(isState(QUERY, ADD, UPDATE));
                activeEnd.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? name : activeBegin;
            }
        });

        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator<IdNameVO>(atozTable, atozNext, atozPrev) {
            public void executeQuery(final Query query) {
                window.setBusy(Messages.get().querying());

                query.setRowsPerPage(16);
                
                if(queryCall == null) {
                    queryCall = new AsyncCallbackUI<ArrayList<IdNameVO>>() {
                        public void success(ArrayList<IdNameVO> result) {
                            setQueryResult(result);
                        }

                        public void failure(Throwable error) {
                            setQueryResult(null);
                            Window.alert("Error: Method call query failed; " + error.getMessage());
                            window.setError("Query Failed");// Messages.get().queryFailed());
                        }
                    
                        public void notFound() {
                            window.setDone(Messages.get().noRecordsFound());
                            setState(DEFAULT);
                        }
                    
                        public void lastPage() {
                            window.setError(Messages.get().noMoreRecordInDir());
                        }                            
                     
                    };
                }
                
                ScriptletService.get().query(query,queryCall); 
            }

            public boolean fetch(IdNameVO entry) {
                return fetchById( (entry == null) ? null : entry.getId());
            }

            public ArrayList<Item<Integer>> getModel() {
                ArrayList<IdNameVO> result;
                ArrayList<Item<Integer>> model;

                result = nav.getQueryResult();
                model = new ArrayList<Item<Integer>>();
                if (result != null) {
                    for (IdNameVO entry : result)
                        model.add(new Item<Integer>(entry.getId(), entry.getName()));
                }
                return model;
            }
        };

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                boolean enable;
                enable = isState(DEFAULT, DISPLAY) && userPermission.hasSelectPermission();
                atozButtons.setEnabled(enable);
                nav.enable(enable);
            }
        });

        atozButtons.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                Query query;
                QueryData field;

                field = new QueryData();
                field.setKey(ScriptletMeta.getName());
                field.setQuery( ((Button)event.getSource()).getAction());
                field.setType(QueryData.Type.STRING);

                query = new Query();
                query.setFields(field);
                nav.setQuery(query);
            }
        });

        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                if (isState(ADD, UPDATE)) {
                    event.cancel();
                    window.setError(Messages.get().mustCommitOrAbort());
                }
            }
        });
    }
    
    @UiHandler("query")
    protected void query(ClickEvent event) {
        data = new ScriptletDO();
        setState(QUERY);
        fireDataChange();

        name.setFocus(true);
        window.setDone(Messages.get().enterFieldsToQuery());
    }

    @UiHandler("next")
    protected void next(ClickEvent event) {
        nav.next();
    }

    @UiHandler("previous")
    protected void previous(ClickEvent event) {
        nav.previous();
    }

    @UiHandler("add")
    protected void add(ClickEvent event) {
        data = new ScriptletDO();
        data.setIsActive("Y");
        setState(ADD);
        fireDataChange();

        name.setFocus(true);
        window.setDone(Messages.get().enterInformationPressCommit());
    }

    @UiHandler("update")
    protected void update(ClickEvent event) {
        window.setBusy(Messages.get().lockForUpdate());

        if(fetchForUpdateCall == null) {
            fetchForUpdateCall = new AsyncCallbackUI<ScriptletDO>() {
                public void success(ScriptletDO result) {
                    data = result;
                    setState(UPDATE);
                    fireDataChange();
                    name.setFocus(true);
                }
            
                public void failure(Throwable caught) {
                    Window.alert(caught.getMessage());
                }
            
                public void finish() {
                    window.clearStatus();
                }
            };
        }
        
        ScriptletService.get().fetchForUpdate(data.getId(),fetchForUpdateCall); 
    }

    @UiHandler("commit")
    protected void commit(ClickEvent event) {
        Validation validation;
        
        finishEditing();
        
        validation = validate();

        if (validation.getStatus() != VALID) {
            window.setError(Messages.get().correctErrors());
            return;
        }

        switch (state) {
            case QUERY:
                Query query;

                query = new Query();
                query.setFields(getQueryFields());
                nav.setQuery(query);
                break;
            case ADD:
                window.setBusy(Messages.get().adding());
                if(addCall == null) {
                    addCall = new AsyncCallbackUI<ScriptletDO> () {
                        public void success(ScriptletDO result) {
                            data = result;
                            setState(DISPLAY);
                            fireDataChange();
                            window.setDone(Messages.get().addingComplete());
                        }
                    
                        public void failure(Throwable e) {
                            Window.alert("commitAdd(): " + e.getMessage());
                            window.clearStatus();
                        }
                    
                        public void validationErrors(ValidationErrorsList e) {
                            showErrors(e);
                        }
                    };
                }
                ScriptletService.get().add(data,addCall); 
                break;
            case UPDATE:
                window.setBusy(Messages.get().updating());
                if(updateCall == null) {
                    updateCall = new AsyncCallbackUI<ScriptletDO>() {
                        public void success(ScriptletDO result) {
                            data = result;
                            setState(DISPLAY);
                            fireDataChange();
                            window.setDone(Messages.get().updatingComplete());
                        }
                    
                        public void failure(Throwable e) {
                            Window.alert("commitUpdate(): " + e.getMessage());
                            window.clearStatus();
                        }                    
                    
                        public void validationErrors(ValidationErrorsList e) {
                            showErrors(e);
                        }
                    };
                }
                ScriptletService.get().update(data,updateCall); 
            default :
                window.clearStatus();
        }
    }

    @UiHandler("abort")
    protected void abort(ClickEvent event) {
        finishEditing();
        clearErrors();
        window.setBusy(Messages.get().cancelChanges());

        switch (state) {
            case QUERY:
                fetchById(null);
                window.setDone(Messages.get().queryAborted());
                break;
            case ADD:
                fetchById(null);
                window.setDone(Messages.get().addAborted());
                break;
            case UPDATE:
                if(abortCall == null) {
                    abortCall = new AsyncCallbackUI<ScriptletDO>() {
                        public void success(ScriptletDO result) {
                            data = result;
                            setState(DISPLAY);
                            fireDataChange();
                        }
                        public void failure(Throwable e) {
                            Window.alert(e.getMessage());
                            fetchById(null);
                        }
                    
                        public void finish() {
                            window.setDone(Messages.get().updateAborted());
                        }
                    };
                }
                ScriptletService.get().abortUpdate(data.getId(),abortCall);
                break;
            default:
                window.clearStatus();
        }
    }

    protected void history() {
        IdNameVO hist;

        hist = new IdNameVO(data.getId(), data.getName());
        HistoryScreen.showHistory(Messages.get().methodHistory(), Constants.table().SCRIPTLET, hist);
    }
    
    protected boolean fetchById(Integer id) {
        if (id == null) {
            data = new ScriptletDO();
            setState(DEFAULT);
        } else {
            window.setBusy(Messages.get().fetching());
            
            if(fetchCall == null) {
                fetchCall = new AsyncCallbackUI<ScriptletDO>() {
                    public void success(ScriptletDO result) {
                        data = result;
                        setState(DISPLAY);
                    }
                    public void notFound() {
                        fetchById(null);
                        window.setDone(Messages.get().noRecordsFound());
                        nav.clearSelection();
                    }
                    public void failure(Throwable e) {
                        fetchById(null);
                        e.printStackTrace();
                        Window.alert(Messages.get().fetchFailed() + e.getMessage());
                        nav.clearSelection();
                    }
                
                    public void finish() {
                        fireDataChange();
                        window.clearStatus();
                    }
                };
            }
            
            ScriptletService.get().fetchById(id,fetchCall);
        }
        return true;
    }

}
