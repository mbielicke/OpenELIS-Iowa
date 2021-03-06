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
package org.openelis.modules.cron.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.constants.OpenELISConstants;
import org.openelis.domain.Constants;
import org.openelis.domain.CronDO;
import org.openelis.domain.IdNameVO;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.meta.CronMeta;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CronScreen extends Screen {
	private CronDO                data;
	private ModulePermission      userPermission;
	
	private TextBox               name,cronTab,bean,method,parameters;
	private CheckBox              isActive;
	private AppButton             queryButton, previousButton, nextButton, addButton, updateButton,
    						      commitButton, abortButton;
    protected MenuItem            history;
    private ButtonGroup           atoz;
    private ScreenNavigator       nav;
	
	public CronScreen(WindowInt window) throws Exception {
		super((ScreenDefInt)GWT.create(CronDef.class));
		
		setWindow(window);
       
        userPermission = UserCache.getPermission().getModule("cron");
        if (userPermission == null)
            throw new PermissionException(Messages.get().screenPermException("Cron Screen"));
		        
		data = new CronDO();
		
		initialize();
		setState(State.DEFAULT);
		DataChangeEvent.fire(this);
	}
	
	private void initialize() {
        queryButton = (AppButton)def.getWidget("query");
        addScreenHandler(queryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                query();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                queryButton.enable(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                          .contains(event.getState())  &&
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
                commitButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                           .contains(event.getState()));
            }
        });

        abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
            }
        });

        history = (MenuItem)def.getWidget("cronHistory");
        addScreenHandler(history, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                history();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                history.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        name = (TextBox)def.getWidget(CronMeta.getName());
        addScreenHandler(name, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                name.setValue(data.getName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                name.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                   .contains(event.getState()));
                name.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        cronTab = (TextBox)def.getWidget(CronMeta.getCronTab());
        addScreenHandler(cronTab, new ScreenEventHandler<String>() {
        	public void onDataChange(DataChangeEvent event) {
        		cronTab.setValue(data.getCronTab());
        	}
        	public void onValueChange(ValueChangeEvent<String> event) {
        		data.setCronTab(event.getValue());
        	}
            public void onStateChange(StateChangeEvent<State> event) {
                cronTab.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                   .contains(event.getState()));
                cronTab.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        bean = (TextBox)def.getWidget(CronMeta.getBean());
        addScreenHandler(bean, new ScreenEventHandler<String>() {
        	public void onDataChange(DataChangeEvent event) {
        		bean.setValue(data.getBean());
        	}
        	public void onValueChange(ValueChangeEvent<String> event) {
        		data.setBean(event.getValue());
        	}
            public void onStateChange(StateChangeEvent<State> event) {
                bean.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                   .contains(event.getState()));
                bean.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        method = (TextBox)def.getWidget(CronMeta.getMethod());
        addScreenHandler(method, new ScreenEventHandler<String>() {
        	public void onDataChange(DataChangeEvent event) {
        		method.setValue(data.getMethod());
        	}
        	public void onValueChange(ValueChangeEvent<String> event) {
        		data.setMethod(event.getValue());
        	}
            public void onStateChange(StateChangeEvent<State> event) {
                method.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                   .contains(event.getState()));
                method.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        parameters = (TextBox)def.getWidget(CronMeta.getParameters());
        addScreenHandler(parameters, new ScreenEventHandler<String>() {
        	public void onDataChange(DataChangeEvent event) {
        		parameters.setValue(data.getParameters());
        	}
        	public void onValueChange(ValueChangeEvent<String> event) {
        		data.setParameters(event.getValue());
        	}
            public void onStateChange(StateChangeEvent<State> event) {
                parameters.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                   .contains(event.getState()));
                parameters.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        isActive = (CheckBox)def.getWidget(CronMeta.getIsActive());
        addScreenHandler(isActive, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isActive.setValue(data.getIsActive());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setIsActive(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isActive.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                       .contains(event.getState()));
                isActive.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator<IdNameVO>(def) {
            public void executeQuery(final Query query) {
                window.setBusy(Messages.get().querying());

                query.setRowsPerPage(9);
                CronService.get().query(query, new AsyncCallback<ArrayList<IdNameVO>>() {
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
                            Window.alert("Error: Project call query failed; " + error.getMessage());
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

                result = nav.getQueryResult();
                model = new ArrayList<TableDataRow>();
                if (result != null) {
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
                field.setKey(CronMeta.getName());
                field.setQuery(((AppButton)event.getSource()).action);
                field.setType(QueryData.Type.STRING);

                query = new Query();
                query.setFields(field);
                nav.setQuery(query);
            }
        });
        
        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {                
                if (EnumSet.of(State.ADD, State.UPDATE).contains(state)) {
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
        data = new CronDO();
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
        data = new CronDO();
        data.setIsActive("Y");
        setState(State.ADD);
        DataChangeEvent.fire(this);

        setFocus(name);
        window.setDone(Messages.get().enterInformationPressCommit());
    }

    protected void update() {
        window.setBusy(Messages.get().lockForUpdate());

        try {
            data = CronService.get().fetchForUpdate(data.getId());

            setState(State.UPDATE);
            DataChangeEvent.fire(this);
            setFocus(name);
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
        window.clearStatus();
    }

    protected void commit() {
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
                data = CronService.get().add(data);

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
                data = CronService.get().update(data);

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
                data = CronService.get().abortUpdate(data.getId());
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
            } catch (Exception e) {
                Window.alert(e.getMessage());
                fetchById(null);
            }
            window.setDone(Messages.get().updateAborted());
        } else {
            window.clearStatus();
        }
    }
    
    protected void history() {
        IdNameVO hist;
        
        hist = new IdNameVO(data.getId(), data.getName());
        HistoryScreen.showHistory(Messages.get().cronHistory(), Constants.table().CRON, hist);
    }

    protected boolean fetchById(Integer id) {
        if (id == null) {
            data = new CronDO();
            setState(State.DEFAULT);
        } else {
            window.setBusy(Messages.get().fetching());
            try {
                data = CronService.get().fetchById(id);
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
	
	

}
