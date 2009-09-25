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
package org.openelis.modules.systemvariable.client;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;

import org.openelis.domain.IdNameDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.SecurityModule;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.*;
import org.openelis.gwt.screen.*;
import org.openelis.gwt.screen.Screen.State;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.*;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.manager.OrganizationManager;
import org.openelis.metamap.SystemVariableMetaMap;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.modules.organization.client.OrgQuery;
import org.openelis.modules.organization.client.OrganizationScreen.Tabs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SystemVariableScreen extends Screen {
    private SystemVariableDO      data;
    private SystemVariableMetaMap meta = new SystemVariableMetaMap();
    private ScreenNavigator       nav;

    private SecurityModule        security;

    public SystemVariableScreen() throws Exception {
        // Call base to get ScreenDef and draw screen
        super((ScreenDefInt)GWT.create(SystemVariableDef.class));
        service = new ScreenService("OpenELISServlet?service=org.openelis.modules.systemVariable.server.SystemVariableService");

        security = OpenELIS.security.getModule("system_variable");

        // Setup link between Screen and widget Handlers
        initialize();

        data = new SystemVariableDO();
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
        final AppButton queryButton = (AppButton)def.getWidget("query");
        addScreenHandler(queryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                query();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (event.getState() == State.QUERY)
                    queryButton.changeState(ButtonState.LOCK_PRESSED);
                queryButton.enable(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                          .contains(event.getState()) &&
                                   security.hasSelectPermission());
            }
        });

        final AppButton previousButton = (AppButton)def.getWidget("previous");
        addScreenHandler(previousButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                previous();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                previousButton.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        final AppButton nextButton = (AppButton)def.getWidget("next");
        addScreenHandler(nextButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                next();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                nextButton.enable(EnumSet.of(State.DEFAULT).contains(event.getState()));
            }
        });

        final AppButton addButton = (AppButton)def.getWidget("add");
        addScreenHandler(addButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                add();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (event.getState() == State.ADD)
                    addButton.changeState(ButtonState.LOCK_PRESSED);
                addButton.enable(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                        .contains(event.getState()) &&
                                 security.hasAddPermission());
            }
        });

        final AppButton updateButton = (AppButton)def.getWidget("update");
        addScreenHandler(updateButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                update();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (event.getState() == State.UPDATE)
                    updateButton.changeState(ButtonState.LOCK_PRESSED);
                updateButton.enable(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                           .contains(event.getState()) &&
                                    security.hasUpdatePermission());
            }
        });

        final AppButton deleteButton = (AppButton)def.getWidget("delete");
        addScreenHandler(deleteButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                delete();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (event.getState() == State.DELETE)
                    deleteButton.changeState(ButtonState.LOCK_PRESSED);
                deleteButton.enable(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                           .contains(event.getState()) &&
                                    security.hasDeletePermission());
            }
        });

        final AppButton commitButton = (AppButton)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE, State.DELETE)
                                           .contains(event.getState()));
            }
        });

        final AppButton abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE, State.DELETE)
                                          .contains(event.getState()));
            }
        });

        //
        // screen fields
        //
        final TextBox name = (TextBox)def.getWidget(meta.getName());
        addScreenHandler(name, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                name.setValue(data.getName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                name.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE, State.DELETE)
                                   .contains(event.getState()));
                name.setQueryMode(event.getState() == State.QUERY);
            }
        });

        final TextBox value = (TextBox)def.getWidget(meta.getValue());
        addScreenHandler(value, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                value.setValue(data.getValue());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setValue(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                value.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE, State.DELETE)
                                    .contains(event.getState()));
                value.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator<Query<IdNameVO>>(this) {
            public void getSelection(RPC entry) {
                fetch(((IdNameVO)entry).getId());
            }
            public void loadPage(Query<IdNameVO> query) {
                loadQueryResult(query);
            }
        };

        final ButtonGroup atoz = (ButtonGroup)def.getWidget("atozButtons");
        addScreenHandler(atoz, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                atoz.enable(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                   .contains(event.getState()) &&
                            security.hasSelectPermission());
            }

            public void onClick(ClickEvent event) {
                Query<IdNameVO> query;
                QueryData field;
                
                field = new QueryData();
                field.key = meta.getName();
                field.query = ((AppButton)event.getSource()).action;
                field.type = QueryData.Type.STRING;
                
                query = new Query<IdNameVO>();
                query.setFields(field);
                executeQuery(query);
            }
        });
    }
    
    /*
     * basic button methods
     */
    protected void query() {
        data = new SystemVariableDO();
        setState(Screen.State.QUERY);
        DataChangeEvent.fire(this);
        window.setDone(consts.get("enterFieldsToQuery"));
    }

    protected void previous() {
        nav.previous();
    }

    protected void next() {
        nav.next();
    }

    protected void add() {
        data = new SystemVariableDO();

        setState(Screen.State.ADD);
        DataChangeEvent.fire(this);
        window.setDone(consts.get("enterInformationPressCommit"));
    }

    protected void update() {
        window.setBusy(consts.get("lockForUpdate"));

        try {
            data = service.call("fetchForUpdate", data.getId());

            window.clearStatus();
            setState(State.UPDATE);
            DataChangeEvent.fire(this);
        } catch (EntityLockedException e) {
            window.clearStatus();
            Window.alert(e.getMessage());
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
    }

    protected void delete() {
        window.setBusy(consts.get("lockForUpdate"));

        try {
            data = service.call("fetchForUpdate", data.getId());

            window.clearStatus();
            setState(State.DELETE);
            DataChangeEvent.fire(this);
        } catch (EntityLockedException e) {
            window.clearStatus();
            Window.alert(e.getMessage());
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
    }

    protected void commit() {
        if (! validate())
            window.setError(consts.get("correctErrors"));
        
        if (state == State.QUERY) {
            Query<IdNameVO> query;
            
            query = new Query<IdNameVO>();
            query.setFields(getQueryFields());
            executeQuery(query);
        } else if (state == State.ADD) {
            window.setBusy(consts.get("adding"));
            try {
                data = service.call("add", data);

                setState(Screen.State.DISPLAY);
                DataChangeEvent.fire(this);
                window.clearStatus();
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                Window.alert("commitAdd(): " + e.getMessage());
                window.clearStatus();
            }
        } else if (state == State.UPDATE) {
            window.setBusy(consts.get("updating"));
            try {
                data = service.call("update", data);

                setState(Screen.State.DISPLAY);
                DataChangeEvent.fire(this);
                window.clearStatus();
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                Window.alert("commitUpdate(): " + e.getMessage());
            }
        } 
    }

    protected void abort() {
        clearErrors();
        window.setBusy(consts.get("cancelChanges"));

        if (state == State.QUERY) {
            data = new SystemVariableDO();
            setState(State.DEFAULT);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("queryAborted"));
        } else if (state == State.ADD) {
            setState(State.DEFAULT);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("addAborted"));
        } else if (state == State.UPDATE) {
            try {
                service.call("abortUpdate", data.getId());
            } catch (Exception e) {
                Window.alert(e.getMessage());
                fetch(data.getId());
            }
            setState(State.DISPLAY);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("updateAborted"));
        } else if (state == State.DELETE) {
            try {
                service.call("abortUpdate", data.getId());
            } catch (Exception e) {
                Window.alert(e.getMessage());
                fetch(data.getId());
            }
            setState(State.DISPLAY);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("deleteAborted"));
        } else {
            window.clearStatus();
        }
    }
    
    protected void fetch(Integer id) {
        window.setBusy(consts.get("fetching"));
        try {
            service.call("fetch", id);
        } catch (Exception e) {
            setState(Screen.State.DEFAULT);
            Window.alert(consts.get("fetchFailed") + e.getMessage());
            window.clearStatus();
            return;
        }
        setState(Screen.State.DISPLAY);
        DataChangeEvent.fire(this);
        window.clearStatus();
    }

    private void executeQuery(Query<IdNameVO> query) {
        
        window.setBusy(consts.get("querying"));

        service.call("query", query, new AsyncCallback<Query<IdNameVO>>() {
            public void onSuccess(Query<IdNameVO> query) {
                loadQueryResult(query);
            }
            public void onFailure(Throwable error) {
                if (error instanceof ValidationErrorsList)
                    showErrors((ValidationErrorsList)error);
                else
                    Window.alert(error.getMessage());
            }
        });
    }

    private void loadQueryResult(Query<IdNameVO> query) {
        ArrayList<TableDataRow> model;
        
        if (query.getResults() == null) {
            window.setDone(consts.get("noRecordsFound"));
            setState(State.DEFAULT);
        } else {
            window.setDone(consts.get("queryingComplete"));
        }

        model = new ArrayList<TableDataRow>();
        for (IdNameVO entry : query.getResults()) {
            model.add(new TableDataRow(entry.getId(), entry.getName()));
        }
        query.setModel(model);

        nav.setQuery(query);
    }
}