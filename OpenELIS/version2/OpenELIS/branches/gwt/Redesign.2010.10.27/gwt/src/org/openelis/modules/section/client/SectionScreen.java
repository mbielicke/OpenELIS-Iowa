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
package org.openelis.modules.section.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SectionDO;
import org.openelis.domain.SectionViewDO;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.ModulePermission;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.PermissionException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.BeforeCloseEvent;
import org.openelis.gwt.event.BeforeCloseHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.Row;
import org.openelis.meta.SectionMeta;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SectionScreen extends Screen {
    private SectionViewDO    data;
    private ModulePermission userPermission;

    private AutoComplete          parentName, organizationName;
    private TextBox               name, description;
    private CheckBox              isExternal;
    private Button                queryButton, previousButton, nextButton, addButton, updateButton,
                                  commitButton, abortButton;
    protected MenuItem            history;
    private ButtonGroup           atoz;
    private ScreenNavigator       nav;
    private ScreenService         organizationService;

    public SectionScreen() throws Exception {
        super((ScreenDefInt)GWT.create(SectionDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.section.server.SectionService");
        organizationService = new ScreenService("controller?service=org.openelis.modules.organization.server.OrganizationService");

        userPermission = OpenELIS.getSystemUserPermission().getModule("section");
        if (userPermission == null)
            throw new PermissionException("screenPermException", "Section Screen");

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
        data = new SectionViewDO();

        initialize();
        setState(State.DEFAULT);
        DataChangeEvent.fire(this);
    }

    private void initialize() {
        queryButton = (Button)def.getWidget("query");
        addScreenHandler(queryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                query();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                queryButton.setEnabled(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                          .contains(event.getState()) &&
                                   userPermission.hasSelectPermission());
                if (event.getState() == State.QUERY) {
                    queryButton.setPressed(true);
                    queryButton.lock();
                }
            }
        });

        previousButton = (Button)def.getWidget("previous");
        addScreenHandler(previousButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                previous();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                previousButton.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        nextButton = (Button)def.getWidget("next");
        addScreenHandler(nextButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                next();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                nextButton.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        addButton = (Button)def.getWidget("add");
        addScreenHandler(addButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                add();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addButton.setEnabled(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                        .contains(event.getState()) &&
                                 userPermission.hasAddPermission());
                if (event.getState() == State.ADD) {
                    addButton.setPressed(true);
                    addButton.lock();
                }
            }
        });

        updateButton = (Button)def.getWidget("update");
        addScreenHandler(updateButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                update();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                updateButton.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()) &&
                                    userPermission.hasUpdatePermission());
                if (event.getState() == State.UPDATE) {
                    updateButton.setPressed(true);
                    updateButton.lock();
                }
            }
        });

        commitButton = (Button)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                           .contains(event.getState()));
            }
        });

        abortButton = (Button)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
            }
        });

        history = (MenuItem)def.getWidget("sectionHistory");
        addScreenHandler(history, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                history.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        history.addCommand(new Command() {
			public void execute() {
				history();
			}
		});

        name = (TextBox)def.getWidget(SectionMeta.getName());
        addScreenHandler(name, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                name.setValue(data.getName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                name.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                   .contains(event.getState()));
                name.setQueryMode(event.getState() == State.QUERY);
            }
        });

        description = (TextBox)def.getWidget(SectionMeta.getDescription());
        addScreenHandler(description, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                description.setValue(data.getDescription());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setDescription(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                description.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
                description.setQueryMode(event.getState() == State.QUERY);
            }
        });

        isExternal = (CheckBox)def.getWidget(SectionMeta.getIsExternal());
        addScreenHandler(isExternal, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isExternal.setValue(data.getIsExternal());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setIsExternal(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isExternal.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                         .contains(event.getState()));
                isExternal.setQueryMode(event.getState() == State.QUERY);
            }
        });

        organizationName = (AutoComplete)def.getWidget(SectionMeta.getOrganizationName());
        addScreenHandler(organizationName, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                organizationName.setValue(data.getOrganizationId(), data.getOrganizationName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                data.setOrganizationId(event.getValue());
                data.setOrganizationName(organizationName.getDisplay());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                organizationName.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                               .contains(event.getState()));
                organizationName.setQueryMode(event.getState() == State.QUERY);
            }
        });

        parentName = (AutoComplete)def.getWidget(SectionMeta.getParentSectionName());
        addScreenHandler(parentName, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                parentName.setValue(data.getParentSectionId(), data.getParentSectionName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                data.setParentSectionId(event.getValue());
                data.setParentSectionName(parentName.getDisplay());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                parentName.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                         .contains(event.getState()));
                parentName.setQueryMode(event.getState() == State.QUERY);
            }
        });

        parentName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                SectionDO data;
                ArrayList<SectionDO> list;
                ArrayList<Item<Integer>> model;
                String param = "";

                parser = new QueryFieldUtil();

                window.setBusy();
                try {
                	if(!event.getMatch().equals("")) {
                		parser.parse(event.getMatch());
                		param = parser.getParameter().get(0);
                	}
                    list = service.callList("fetchByName", param);
                    model = new ArrayList<Item<Integer>>();
                    for (int i = 0; i < list.size(); i++ ) {
                        data = list.get(i);
                        model.add(new Item<Integer>(data.getId(), data.getName()));
                    }
                    parentName.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
                window.clearStatus();
            }
        });

        organizationName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                Item<Integer> row;
                OrganizationDO data;
                ArrayList<OrganizationDO> list;
                ArrayList<Item<Integer>> model;
                String param = "";

                parser = new QueryFieldUtil();

                window.setBusy();
                try {
                	if(!event.getMatch().equals("")) {
                		parser.parse(event.getMatch());
                		param = parser.getParameter().get(0);
                	}
                    list = organizationService.callList("fetchByIdOrName", param);
                    model = new ArrayList<Item<Integer>>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new Item<Integer>(4);
                        data = list.get(i);

                        row.setKey(data.getId());
                        row.setCell(0,data.getName());
                        row.setCell(1,data.getAddress().getStreetAddress());
                        row.setCell(2,data.getAddress().getCity());
                        row.setCell(3,data.getAddress().getState());

                        model.add(row);
                    }
                    organizationName.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
                window.clearStatus();
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
                            window.setError(consts.get("noMoreRecordInDir"));
                        } else {
                            com.google.gwt.user.client.Window.alert("Error: Section call query failed; " + error.getMessage());
                            window.setError(consts.get("queryFailed"));
                        }
                    }
                });
            }

            public boolean fetch(RPC entry) {
                return fetchById( (entry == null) ? null : ((IdNameVO)entry).getId());
            }

            public ArrayList<Item<Integer>> getModel() {
                ArrayList<IdNameVO> result;
                ArrayList<Item<Integer>> model;

                model = null;
                result = nav.getQueryResult();
                if (result != null) {
                    model = new ArrayList<Item<Integer>>();
                    for (IdNameVO entry : result)
                        model.add(new Item<Integer>(entry.getId(), entry.getName()));
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
                atoz.setEnabled(enable);
                nav.enable(enable);
            }

            public void onClick(ClickEvent event) {
                Query query;
                QueryData field;

                field = new QueryData(SectionMeta.getName(),QueryData.Type.STRING,((Button)event.getSource()).getAction());

                query = new Query();
                query.setFields(field);
                nav.setQuery(query);
            }
        });

        window.addBeforeClosedHandler(new BeforeCloseHandler<Window>() {
            public void onBeforeClosed(BeforeCloseEvent<Window> event) {
                if (EnumSet.of(State.ADD, State.UPDATE, State.DELETE).contains(state)) {
                    event.cancel();
                    window.setError(consts.get("mustCommitOrAbort"));
                }
            }
        });
    }

    /*
     * basic button methods
     */
    protected void query() {
        data = new SectionViewDO();
        setState(State.QUERY);
        DataChangeEvent.fire(this);

        setFocus(name);
        window.setDone(consts.get("enterFieldsToQuery"));
    }

    protected void previous() {
        nav.previous();
    }

    protected void next() {
        nav.next();
    }

    protected void add() {
        data = new SectionViewDO();
        data.setIsExternal("N");

        setState(State.ADD);
        DataChangeEvent.fire(this);

        setFocus(name);
        window.setDone(consts.get("enterInformationPressCommit"));
    }

    protected void update() {
        window.setBusy(consts.get("lockForUpdate"));

        try {
            data = service.call("fetchForUpdate", data.getId());

            setState(State.UPDATE);
            DataChangeEvent.fire(this);
            setFocus(name);
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
        }
        window.clearStatus();
    }

    protected void commit() {
        setFocus(null);

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
                data = service.call("add", data);

                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(consts.get("addingComplete"));
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                com.google.gwt.user.client.Window.alert("commitAdd(): " + e.getMessage());
                window.clearStatus();
            }
        } else if (state == State.UPDATE) {
            window.setBusy(consts.get("updating"));
            try {
                data = service.call("update", data);

                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.setDone(consts.get("updatingComplete"));
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                com.google.gwt.user.client.Window.alert("commitUpdate(): " + e.getMessage());
                window.clearStatus();
            }
        }
    }

    protected void abort() {
        setFocus(null);
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
                data = service.call("abortUpdate", data.getId());
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
            } catch (Exception e) {
                com.google.gwt.user.client.Window.alert(e.getMessage());
                fetchById(null);
            }
            window.setDone(consts.get("updateAborted"));
        } else {
            window.clearStatus();
        }
    }

    protected void history() {
        IdNameVO hist;

        hist = new IdNameVO(data.getId(), data.getName());
        HistoryScreen.showHistory(consts.get("sectionHistory"), ReferenceTable.SECTION, hist);
    }

    protected boolean fetchById(Integer id) {
        if (id == null) {
            data = new SectionViewDO();
            setState(State.DEFAULT);
        } else {
            window.setBusy(consts.get("fetching"));
            try {
                data = service.call("fetchById", id);
                setState(State.DISPLAY);
            } catch (NotFoundException e) {
                fetchById(null);
                window.setDone(consts.get("noRecordsFound"));
                return false;
            } catch (Exception e) {
                fetchById(null);
                e.printStackTrace();
                com.google.gwt.user.client.Window.alert(consts.get("fetchFailed") + e.getMessage());
                return false;
            }
        }
        DataChangeEvent.fire(this);
        window.clearStatus();

        return true;
    }

}