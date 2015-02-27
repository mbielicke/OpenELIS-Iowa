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

import static org.openelis.ui.screen.State.ADD;
import static org.openelis.ui.screen.State.DEFAULT;
import static org.openelis.ui.screen.State.DISPLAY;
import static org.openelis.ui.screen.State.QUERY;
import static org.openelis.ui.screen.State.UPDATE;

import java.util.ArrayList;

import org.openelis.cache.UserCacheService;
import org.openelis.constants.Messages;
import org.openelis.constants.OpenELISConstants;
import org.openelis.domain.Constants;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.MethodDO;
import org.openelis.meta.MethodMeta;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.ui.annotation.Handler;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Presenter;
import org.openelis.ui.screen.ScreenNavigator;
import org.openelis.ui.screen.View.Validation;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;

public class MethodScreenUI extends Presenter {

    protected MethodDO                 data;
    private ModulePermission           userPermission;

    private ScreenNavigator<IdNameVO>  nav;
    
    private AsyncCallbackUI<MethodDO>  fetchForUpdateCall,addCall,updateCall, abortCall, fetchCall;
    
    private AsyncCallbackUI<ArrayList<IdNameVO>> queryCall;
    
    MethodViewImpl view;

    public MethodScreenUI(WindowInt window) throws Exception {
        setWindow(window);
                
        view = new MethodViewImpl(); 
        view.setPresenter(this);
        addStateChangeHandler(view);
        addDataChangeHandler(view);
        initWidget(view);
        
        ensureDebugId("method");
        
        userPermission = getUserCacheService().getPermission().getModule("method");
        if (userPermission == null)
            throw new PermissionException(getMessages().screenPermException("Method Screen"));

        initialize();
        setState(DEFAULT);
        fireDataChange(new MethodDO());
    }

    protected void initialize() {
        view.history.addCommand(new Command() {

            @Override
            public void execute() {
                history();
            }
        });

        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator<IdNameVO>(view.atozTable, view.atozNext, view.atozPrev) {
            public void executeQuery(final Query query) {
                setBusy(getMessages().querying());

                query.setRowsPerPage(16);
                
                if(queryCall == null) {
                    queryCall = new AsyncCallbackUI<ArrayList<IdNameVO>>() {
                        public void success(ArrayList<IdNameVO> result) {
                            clearStatus();
                            setQueryResult(result);
                        }

                        public void failure(Throwable error) {
                            setQueryResult(null);
                            Window.alert("Error: Method call query failed; " + error.getMessage());
                            setError("Query Failed");//getMessages().queryFailed());
                        }
                    
                        public void notFound() {
                            setDone(getMessages().noRecordsFound());
                            setQueryResult(null);
                            setState(DEFAULT);
                        }
                     
                        public void lastPage() {
                            setQueryResult(null);
                            setError(getMessages().noMoreRecordInDir());
                        }
                    };
                }
                
                getMethodService().query(query,queryCall);
            }

            public boolean fetch(IdNameVO entry) {
                fetchById( (entry == null) ? null : entry.getId());
                return true;
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
                view.atozButtons.setEnabled(enable);
                nav.enable(enable);
            }
        });

        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                if (isState(ADD, UPDATE)) {
                    event.cancel();
                    setError(getMessages().mustCommitOrAbort());
                }
            }
        });
    }
    
    @Handler("name")
    public void nameValueChange(ValueChangeEvent<String> event) {
    	data.setName(event.getValue());
    }
    
    @Handler("description")
    public void descriptionValueChange(ValueChangeEvent<String> event) {
         data.setDescription(event.getValue());
    }

    @Handler("reportingDescription")
    public void reportingValueChange(ValueChangeEvent<String> event) {
         data.setReportingDescription(event.getValue());
    }

    @Handler("isActive")
    public void isActiveValueChange(ValueChangeEvent<String> event) {
         data.setIsActive(event.getValue());
    }

    @Handler("activeBegin")
    public void activeBeginValueChange(ValueChangeEvent<Datetime> event) {
         data.setActiveBegin(event.getValue());
    }

    @Handler("activeEnd")
    public void onValueChange(ValueChangeEvent<Datetime> event) {
         data.setActiveEnd(event.getValue());
    }
    
    @Handler("atozButtons")
    public void atozQuery(ClickEvent event) {
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
    

    @Handler("query")
    protected void query(ClickEvent event) {
        setState(QUERY);
        fireDataChange(new MethodDO());

        view.name.setFocus(true);
        setDone(getMessages().enterFieldsToQuery());
    }

    @Handler("next")
    protected void next(ClickEvent event) {
        nav.next();
    }

    @Handler("previous")
    protected void previous(ClickEvent event) {
        nav.previous();
    }

    @Handler("add")
    protected void add(ClickEvent event) {
        data.setIsActive("Y");
        setState(ADD);
        fireDataChange(new MethodDO());

        view.name.setFocus(true);
        setDone(getMessages().enterInformationPressCommit());
    }

    @Handler("update")
    protected void update(ClickEvent event) {
        setBusy(getMessages().lockForUpdate());

        if(fetchForUpdateCall == null) {
            fetchForUpdateCall = new AsyncCallbackUI<MethodDO>() {
                public void success(MethodDO result) {
                    data = result;
                    setState(UPDATE);
                    fireDataChange(result);
                    view.name.setFocus(true);
                }
        
                public void failure(Throwable e) {
                    Window.alert(e.getMessage());
                }
            
                public void finish() {
                    clearStatus();
                }
            };
        }
        
        getMethodService().fetchForUpdate(data.getId(),fetchForUpdateCall); 
    }

    @Handler("commit")
    protected void commit(ClickEvent event) {
        Validation validation;
        
        view.finishEditing();
        
        validation = view.validate();

        if (validation.getStatus() != Validation.Status.VALID) {
            window.setError(getMessages().correctErrors());
            return;
        }

        switch (state) {
            case QUERY:
                Query query;

                query = new Query();
                query.setFields(view.getQueryFields());
                nav.setQuery(query);
                break;
            case ADD:
                setBusy(getMessages().adding());
                if(addCall == null) {
                    addCall = new AsyncCallbackUI<MethodDO>() {
                        public void success(MethodDO result) {
                            data = result;
                            setState(DISPLAY);
                            fireDataChange(result);
                            setDone(getMessages().addingComplete());
                        }
                        public void validationErrors(ValidationErrorsList e) {
                            view.showErrors(e);
                        }
                        public void failure(Throwable caught) {
                            Window.alert("commitAdd(): " + caught.getMessage());
                            clearStatus();
                        }
                    };
                }
                getMethodService().add(data, addCall);
                break;
            case UPDATE:
                setBusy(getMessages().updating());
                if(updateCall == null) {
                    updateCall = new AsyncCallbackUI<MethodDO>() {
                        public void success(MethodDO result) {
                            data = result;
                            setState(DISPLAY);
                            fireDataChange(result);
                            setDone(getMessages().updatingComplete());
                        }
                
                        public void validationErrors(ValidationErrorsList e) {
                            view.showErrors(e);
                        }
                    
                        public void failure(Throwable e) {
                            Window.alert("commitUpdate(): " + e.getMessage());
                            clearStatus();
                        }
                    };
                }
                getMethodService().update(data, updateCall);
            default :
                clearStatus();
        }
    }

    @Handler("abort")
    protected void abort(ClickEvent event) {
        view.finishEditing();
        view.clearErrors();
        setBusy(getMessages().cancelChanges());

        switch (state) {
            case QUERY:
                fetchById(null);
                setDone(getMessages().queryAborted());
                break;
            case ADD:
                fetchById(null);
                setDone(getMessages().addAborted());
                break;
            case UPDATE:
                if(abortCall == null) {
                    abortCall =  new AsyncCallbackUI<MethodDO>() {
                        public void success(MethodDO result) {
                            data = result;
                            setState(DISPLAY);
                            fireDataChange(result);
                        }
                    
                        public void failure(Throwable e) {
                            Window.alert(e.getMessage());
                            fetchById(null);
                        }
                    
                        public void finish() {
                            setDone(getMessages().updateAborted());
                        }
                    };
                }
                getMethodService().abortUpdate(data.getId(),abortCall);
                break;
            default:
                clearStatus();
        }
    }

    protected void history() {
        IdNameVO hist;

        hist = new IdNameVO(data.getId(), data.getName());
        HistoryScreen.showHistory(getMessages().methodHistory(), Constants.table().METHOD, hist);
    }

    protected void fetchById(Integer id) {
        if (id == null) {
            data = new MethodDO();
            setState(DEFAULT);
            fireDataChange(new MethodDO());
        } else {
            setBusy(getMessages().fetching());
            if(fetchCall == null) {
                fetchCall = new AsyncCallbackUI<MethodDO>() {
                    public void success(MethodDO result) {
                        data = result;
                        setState(DISPLAY);
                    }
            
                    public void notFound() {
                        fetchById(null);
                        setDone(getMessages().noRecordsFound());
                        nav.clearSelection();
                    }
                
                    public void failure(Throwable e) {
                        fetchById(null);
                        e.printStackTrace();
                        Window.alert(getMessages().fetchFailed() + e.getMessage());
                        nav.clearSelection();
                    }
                
                    public void finish() {
                        fireDataChange(data);
                        clearStatus();
                    }
                };
            }
            getMethodService().fetchById(id, fetchCall);
        }

    }

    protected MethodService getMethodService() {
        return MethodService.get();
    }
    
    protected UserCacheService getUserCacheService() {
        return UserCacheService.get();
    }
    
    protected OpenELISConstants getMessages() {
        return Messages.get();
    }
    
	public ModulePermission permissions() {
		return userPermission;
	}
}
