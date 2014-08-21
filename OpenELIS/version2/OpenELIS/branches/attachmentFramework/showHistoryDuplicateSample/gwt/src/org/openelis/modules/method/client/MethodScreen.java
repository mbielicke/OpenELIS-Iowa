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
package org.openelis.modules.method.client;

import static org.openelis.ui.screen.Screen.ShortKeys.CTRL;
import static org.openelis.ui.screen.State.ADD;
import static org.openelis.ui.screen.State.DEFAULT;
import static org.openelis.ui.screen.State.DELETE;
import static org.openelis.ui.screen.State.DISPLAY;
import static org.openelis.ui.screen.State.QUERY;
import static org.openelis.ui.screen.State.UPDATE;
import static org.openelis.ui.screen.Screen.Validation.Status.VALID;

import java.util.ArrayList;

import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.MethodDO;
import org.openelis.meta.MethodMeta;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.ScreenNavigator;
import org.openelis.ui.widget.AtoZButtons;
import org.openelis.ui.widget.Button;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class MethodScreen extends Screen {
    @UiTemplate("Method.ui.xml")
    interface MethodUiBinder extends UiBinder<Widget, MethodScreen> {
    };

    public static final MethodUiBinder uiBinder = GWT.create(MethodUiBinder.class);

    private MethodDO                   data;
    private ModulePermission           userPermission;

    @UiField
    protected Calendar                 activeBegin, activeEnd;
    @UiField
    protected TextBox<String>          name, description, reportingDescription;
    @UiField
    protected CheckBox                 isActive;
    @UiField
    protected Button                   query, previous, next, add, update, commit, abort, atozNext,
                                       atozPrev, optionsButton;

    @UiField
    protected Menu                     optionsMenu;
    @UiField
    protected MenuItem                 history;
    @UiField
    protected AtoZButtons              atozButtons;

    @UiField
    protected Table                    atozTable;

    private ScreenNavigator<IdNameVO>  nav;

    public MethodScreen(WindowInt window) throws Exception {
        setWindow(window);
        
        initWidget(uiBinder.createAndBindUi(this));
        
        userPermission = UserCache.getPermission().getModule("method");
        if (userPermission == null)
            throw new PermissionException(Messages.get().screenPermException("Method Screen"));

        data = new MethodDO();

        initialize();
        setState(DEFAULT);
        fireDataChange();
    }

    private void initialize() {
        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                query.setEnabled(isState(QUERY,DEFAULT, DISPLAY) && userPermission.hasSelectPermission());
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
        
        addShortcut(previous,'p',CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                next.setEnabled(isState(DISPLAY));
            }
        });
        
        addShortcut(next,'n',CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                add.setEnabled(isState(ADD,DEFAULT, DISPLAY) && userPermission.hasAddPermission());
                if (isState(ADD)) { 
                    add.lock();
                    add.setPressed(true);
                }
            }
        });
        
        addShortcut(add,'a',CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                update.setEnabled(isState(UPDATE,DISPLAY) && userPermission.hasUpdatePermission());
                if (isState(UPDATE)) {
                    update.lock();
                    update.setPressed(true);
                }
            }
        });
        
        addShortcut(update,'u',CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                commit.setEnabled(isState(QUERY, ADD, UPDATE, DELETE));
            }
        });
        
        addShortcut(commit,'m',CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                abort.setEnabled(isState(QUERY, ADD, UPDATE, DELETE));
            }
        });
        
        addShortcut(abort,'o',CTRL);

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

        addScreenHandler(name, MethodMeta.getName(), new ScreenHandler<String>() {
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
                return forward ? description : activeEnd;
            }
        });

        addScreenHandler(description, MethodMeta.getDescription(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                description.setValue(data.getDescription());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setDescription(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                description.setEnabled(isState(QUERY, ADD, UPDATE));
                description.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? reportingDescription : name;
            }
        });

        addScreenHandler(reportingDescription,
                         MethodMeta.getReportingDescription(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 reportingDescription.setValue(data.getReportingDescription());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 data.setReportingDescription(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 reportingDescription.setEnabled(isState(QUERY, ADD, UPDATE));
                                 reportingDescription.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? isActive : description;
                             }
                         });

        addScreenHandler(isActive, MethodMeta.getIsActive(), new ScreenHandler<String>() {
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
                return forward ? activeBegin : reportingDescription;
            }
        });

        addScreenHandler(activeBegin, MethodMeta.getActiveBegin(), new ScreenHandler<Datetime>() {
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

        addScreenHandler(activeEnd, MethodMeta.getActiveEnd(), new ScreenHandler<Datetime>() {
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
                MethodService.get().query(query, new AsyncCallback<ArrayList<IdNameVO>>() {
                    public void onSuccess(ArrayList<IdNameVO> result) {
                        setQueryResult(result);
                    }

                    public void onFailure(Throwable error) {
                        setQueryResult(null);
                        if (error instanceof NotFoundException) {
                            window.setDone(Messages.get().noRecordsFound());
                            setState(DEFAULT);
                        } else if (error instanceof LastPageException) {
                            window.setError(Messages.get().noMoreRecordInDir());
                        } else {
                            Window.alert("Error: Method call query failed; " + error.getMessage());
                            window.setError("Query Failed");//Messages.get().queryFailed());
                        }
                    }
                });
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
                field.setKey(MethodMeta.getName());
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
        data = new MethodDO();
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
        data = new MethodDO();
        data.setIsActive("Y");
        setState(ADD);
        fireDataChange();

        name.setFocus(true);
        window.setDone(Messages.get().enterInformationPressCommit());
    }

    @UiHandler("update")
    protected void update(ClickEvent event) {
        window.setBusy(Messages.get().lockForUpdate());

        try {
            data = MethodService.get().fetchForUpdate(data.getId());

            setState(UPDATE);
            fireDataChange();
            name.setFocus(true);
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
        window.clearStatus();
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
                try {
                    data = MethodService.get().add(data);

                    setState(DISPLAY);
                    fireDataChange();
                    window.setDone(Messages.get().addingComplete());
                } catch (ValidationErrorsList e) {
                    showErrors(e);
                } catch (Exception e) {
                    Window.alert("commitAdd(): " + e.getMessage());
                    window.clearStatus();
                }
                break;
            case UPDATE:
                window.setBusy(Messages.get().updating());
                try {
                    data = MethodService.get().update(data);

                    setState(DISPLAY);
                    fireDataChange();
                    window.setDone(Messages.get().updatingComplete());
                } catch (ValidationErrorsList e) {
                    showErrors(e);
                } catch (Exception e) {
                    Window.alert("commitUpdate(): " + e.getMessage());
                    window.clearStatus();
                }
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
                try {
                    data = MethodService.get().abortUpdate(data.getId());
                    setState(DISPLAY);
                    fireDataChange();
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    fetchById(null);
                }
                window.setDone(Messages.get().updateAborted());
                break;
            default:
                window.clearStatus();
        }
    }

    protected void history() {
        IdNameVO hist;

        hist = new IdNameVO(data.getId(), data.getName());
        HistoryScreen.showHistory(Messages.get().methodHistory(), Constants.table().METHOD, hist);
    }

    protected boolean fetchById(Integer id) {
        if (id == null) {
            data = new MethodDO();
            setState(DEFAULT);
        } else {
            window.setBusy(Messages.get().fetching());
            try {
                data = MethodService.get().fetchById(id);
                setState(DISPLAY);
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
        fireDataChange();
        window.clearStatus();

        return true;
    }

}
