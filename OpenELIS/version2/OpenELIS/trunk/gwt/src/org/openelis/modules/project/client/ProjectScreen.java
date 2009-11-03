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
package org.openelis.modules.project.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ProjectParameterDO;
import org.openelis.domain.QcAnalyteViewDO;
import org.openelis.domain.SecuritySystemUserDO;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.SecurityException;
import org.openelis.gwt.common.SecurityModule;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.ProjectManager;
import org.openelis.meta.ScriptletMeta;
import org.openelis.metamap.ProjectMetaMap;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ProjectScreen extends Screen {
    private ProjectMetaMap   meta       = new ProjectMetaMap();
    private ScriptletMeta    scriptMeta = new ScriptletMeta();
    private SecurityModule   security;
    private ProjectManager   manager;

    private CalendarLookUp   startedDate, completedDate;
    private TextBox<Integer> id;
    private TextBox          name, description, referenceTo;
    private CheckBox         isActive;
    private AppButton        queryButton, previousButton, nextButton, addButton, updateButton,
                             commitButton, abortButton, addParameterButton, removeParameterButton;
    private TableWidget      parameterTable;
    private AutoComplete<Integer> ownerId, scriptlet;
    private ButtonGroup           atoz;
    private ScreenNavigator       nav;

    private ScreenService         userService, scriptletService;

    public ProjectScreen() throws Exception {
        super((ScreenDefInt)GWT.create(ProjectScreenDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.project.server.ProjectService");
        userService = new ScreenService("controller?service=org.openelis.server.SystemUserService");
        scriptletService = new ScreenService("controller?service=org.openelis.modules.scriptlet.server.ScriptletService");

        security = OpenELIS.security.getModule("project");
        if (security == null)
            throw new SecurityException("screenPermException", "Project Screen");

        // Setup link between Screen and widget Handlers
        initialize();

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }

    /**
     * This method is called to set the initial state of widgets after the
     * screen is attached to the browser. It is usually called in deferred
     * command.
     */
    private void postConstructor() {
        manager = ProjectManager.getInstance();

        setState(State.DEFAULT);

        initializeDropdowns();
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
                                          .contains(event.getState()) &&
                                   security.hasSelectPermission());
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
                                 security.hasAddPermission());
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
                                    security.hasUpdatePermission());
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

        id = (TextBox<Integer>)def.getWidget(meta.getId());
        addScreenHandler(id, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                id.setValue(manager.getProject().getId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getProject().setId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                id.enable(EnumSet.of(State.QUERY)
                                 .contains(event.getState()));
                id.setQueryMode(event.getState() == State.QUERY);
                if (state == State.QUERY)
                    id.setFocus(true);
            }
        });

        name = (TextBox)def.getWidget(meta.getName());
        addScreenHandler(name, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                name.setValue(manager.getProject().getName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getProject().setName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                name.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                   .contains(event.getState()));
                name.setQueryMode(event.getState() == State.QUERY);
                if (EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()))
                    name.setFocus(true);
            }
        });

        description = (TextBox)def.getWidget(meta.getDescription());
        addScreenHandler(description, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                description.setValue(manager.getProject().getDescription());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getProject().setDescription(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                description.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
                description.setQueryMode(event.getState() == State.QUERY);
            }
        });

        ownerId = (AutoComplete<Integer>)def.getWidget(meta.getOwnerId());
        addScreenHandler(ownerId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                ownerId.setSelection(manager.getProject().getOwnerId(), 
                                     manager.getProject().getOwnerName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getProject().setOwnerId(event.getValue());
                manager.getProject().setOwnerName(ownerId.getTextBoxDisplay());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                ownerId.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                      .contains(event.getState()));
                ownerId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        ownerId.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                ArrayList<SecuritySystemUserDO> users;
                ArrayList<TableDataRow> model;
                
                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());

                try {
                    users = userService.callList("fetchByLogin", parser.getParameter().get(0));
                    model = new ArrayList<TableDataRow>();
                    for (SecuritySystemUserDO user : users)
                        model.add(new TableDataRow(user.getId(), user.getLoginName()));
                    ownerId.showAutoMatches(model);
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert(e.toString());
                }
            }
        });

        isActive = (CheckBox)def.getWidget(meta.getIsActive());
        addScreenHandler(isActive, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isActive.setValue(manager.getProject().getIsActive());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getProject().setIsActive(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isActive.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                       .contains(event.getState()));
                isActive.setQueryMode(event.getState() == State.QUERY);
            }
        });

        referenceTo = (TextBox)def.getWidget(meta.getReferenceTo());
        addScreenHandler(referenceTo, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                referenceTo.setValue(manager.getProject().getReferenceTo());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getProject().setReferenceTo(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                referenceTo.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
                referenceTo.setQueryMode(event.getState() == State.QUERY);
            }
        });

        startedDate = (CalendarLookUp)def.getWidget(meta.getStartedDate());
        addScreenHandler(startedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                startedDate.setValue(manager.getProject().getStartedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getProject().setStartedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                startedDate.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
                startedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        scriptlet = (AutoComplete<Integer>)def.getWidget(meta.getScriptlet().getName());
        addScreenHandler(name, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                scriptlet.setSelection(manager.getProject().getScriptletId(),
                                       getString(manager.getProject().getScriptletName()));
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getProject().setScriptletId(event.getValue());
                manager.getProject().setScriptletName(scriptlet.getTextBoxDisplay());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                scriptlet.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                   .contains(event.getState()));
                scriptlet.setQueryMode(event.getState() == State.QUERY);
            }
        });
        scriptlet.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                ArrayList<IdNameVO> list;
                ArrayList<TableDataRow> model;

                parser = new QueryFieldUtil();
                parser.parse(event.getMatch());

                try {
                    list = scriptletService.callList("fetchByName", parser.getParameter().get(0));
                    model = new ArrayList<TableDataRow>();

                    for (IdNameVO data : list)
                        model.add(new TableDataRow(data.getId(), data.getName()));
                    scriptlet.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        completedDate = (CalendarLookUp)def.getWidget(meta.getCompletedDate());
        addScreenHandler(completedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                completedDate.setValue(manager.getProject().getCompletedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getProject().setCompletedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                completedDate.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                            .contains(event.getState()));
                completedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        parameterTable = (TableWidget)def.getWidget("parameterTable");
        addScreenHandler(parameterTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    parameterTable.load(getParameterTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                parameterTable.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                             .contains(event.getState()));
                parameterTable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        parameterTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                ProjectParameterDO data;

                r = event.getRow();
                c = event.getCol();
                val = parameterTable.getObject(r, c);
                try {
                    data = manager.getParameters().getParameterAt(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }
                switch (c) {
                    case 0:
                        data.setParameter((String)val);
                        break;
                    case 1:
                        data.setOperationId((Integer)val);
                        break;
                    case 2:
                        data.setValue((String)val);
                        break;
                }
            }
        });

        parameterTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                try {
                    manager.getParameters().addParameter(new ProjectParameterDO());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        parameterTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getParameters().removeParameterAt(event.getIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });
        
        ((Dropdown)parameterTable.getColumnWidget(meta.getProjectParameter().getOperationId())).searchLinear(true);
        
        addParameterButton = (AppButton)def.getWidget("addParameterButton");
        addScreenHandler(addParameterButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                parameterTable.addRow();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addParameterButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                                 .contains(event.getState()));
            }
        });

        removeParameterButton = (AppButton)def.getWidget("removeParameterButton");
        addScreenHandler(removeParameterButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = parameterTable.getSelectedRow();
                if (r > -1 && parameterTable.numRows() > 0)
                    parameterTable.deleteRow(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeParameterButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                                    .contains(event.getState()));
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
                            window.setError("No more records in this direction");
                        } else {
                            Window.alert("Error: Project call query failed; " + error.getMessage());
                            window.setError(consts.get("queryFailed"));
                        }
                    }
                });
            }

            public boolean fetch(RPC entry) {
                return fetchById( (entry == null) ? null : ((IdNameVO)entry).getId());
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
                         security.hasSelectPermission();
                atoz.enable(enable);
                nav.enable(enable);
            }

            public void onClick(ClickEvent event) {
                Query query;
                QueryData field;

                field = new QueryData();
                field.key = meta.getName();
                field.query = ((AppButton)event.getSource()).action;
                field.type = QueryData.Type.STRING;

                query = new Query();
                query.setFields(field);
                nav.setQuery(query);
            }
        });
    }

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;

        // parameter table project parameter
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : DictionaryCache.getListByCategorySystemName("project_parameter_operations"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));

        ((Dropdown)parameterTable.getColumnWidget(meta.PROJECT_PARAMETER.getOperationId())).setModel(model);
    }

    private void query() {
        manager = ProjectManager.getInstance();
        setState(State.QUERY);
        DataChangeEvent.fire(this);
        window.setDone(consts.get("enterFieldsToQuery"));
    }

    private void next() {
        nav.next();
    }

    private void previous() {
        nav.previous();
    }

    private void update() {
        window.setBusy(consts.get("lockForUpdate"));

        try {
            manager = manager.fetchForUpdate();

            setState(State.UPDATE);
            DataChangeEvent.fire(this);
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
        window.clearStatus();
    }

    private void add() {
        manager = ProjectManager.getInstance();
        manager.getProject().setIsActive("Y");

        setState(State.ADD);
        DataChangeEvent.fire(this);
        window.setDone(consts.get("enterInformationPressCommit"));
    }

    private void commit() {
        //
        // set the focus to null so every field will commit its data.
        //
        name.setFocus(false);

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
                Window.alert("commitAdd(): " + e.getMessage());
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
                Window.alert("commitUpdate(): " + e.getMessage());
                window.clearStatus();
            }
        }
    }

    private void abort() {
        name.setFocus(false);
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
                Window.alert(e.getMessage());
                fetchById(null);
            }
            window.setDone(consts.get("updateAborted"));
        } else {
            window.clearStatus();
        }
    }

    private boolean fetchById(Integer id) {
        if (id == null) {
            manager = ProjectManager.getInstance();
            setState(State.DEFAULT);
        } else {
            window.setBusy(consts.get("fetching"));
            try {
                manager = ProjectManager.fetchWithParameters(id);
                setState(State.DISPLAY);
            } catch (NotFoundException e) {
                fetchById(null);
                window.setDone(consts.get("noRecordsFound"));
                return false;
            } catch (Exception e) {
                fetchById(null);
                e.printStackTrace();
                Window.alert(consts.get("fetchFailed") + e.getMessage());
                return false;
            }
        }
        DataChangeEvent.fire(this);
        window.clearStatus();

        return true;
    }
    
    private ArrayList<TableDataRow> getParameterTableModel() {
        int i;
        TableDataRow row;
        ProjectParameterDO data;
        ArrayList<TableDataRow> model;
        
        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.getParameters().count(); i++) {
                data = manager.getParameters().getParameterAt(i);
                row = new TableDataRow(null,
                                       data.getParameter(),
                                       data.getOperationId(),
                                       data.getValue());
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }
}
