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
package org.openelis.modules.organization.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrganizationContactDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.domain.ReferenceTable;
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
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.Button;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.TextBox;
import org.openelis.manager.OrganizationContactManager;
import org.openelis.manager.OrganizationManager;
import org.openelis.manager.OrganizationParameterManager;
import org.openelis.meta.OrganizationMeta;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.main.client.openelis.OpenELIS;
import org.openelis.modules.note.client.NotesTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TabPanel;

public class OrganizationScreen extends Screen {
    private OrganizationManager   manager;
    private ModulePermission      userPermission;

    private ButtonGroup           atoz;
    private ScreenNavigator       nav;

    private ContactTab            contactTab;
    private ParameterTab          parameterTab;
    private NotesTab              noteTab;
    private Tabs                  tab;

    private Button                queryButton, previousButton, nextButton, addButton, updateButton,
                                  commitButton, abortButton;
    private MenuItem              orgHistory, orgContactHistory, orgParameterHistory;
    private TextBox               id, name, multipleUnit, city, zipCode, streetAddress;
    private CheckBox              isActive;
    private Dropdown<String>      stateCode, country;
    private AutoComplete          parentName;
    private TabPanel              tabPanel;

    private enum Tabs {
        CONTACT, PARAMETER, NOTE
    };

    public OrganizationScreen() throws Exception {
        super((ScreenDefInt)GWT.create(OrganizationDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.organization.server.OrganizationService");

        userPermission = OpenELIS.getSystemUserPermission().getModule("organization");
        if (userPermission == null)
            throw new PermissionException("screenPermException", "Oranization Screen");

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
        tab = Tabs.CONTACT;
        manager = OrganizationManager.getInstance();

        try {
            DictionaryCache.preloadByCategorySystemNames("country", "state",
                                                         "contact_type", "parameter_type");
        } catch (Exception e) {
            Window.alert("OrganizationScreen: missing dictionary entry; " + e.getMessage());
            window.close();
        }
        
        initialize();
        setState(State.DEFAULT);
        initializeDropdowns();
        DataChangeEvent.fire(this);
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        //
        // button panel buttons
        //
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
                commitButton.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE, State.DELETE)
                                           .contains(event.getState()));
            }
        });

        abortButton = (Button)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE, State.DELETE)
                                          .contains(event.getState()));
            }
        });
        
        orgHistory = (MenuItem)def.getWidget("orgHistory");
        addScreenHandler(orgHistory, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                orgHistory.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        orgHistory.addCommand(new Command() {
        	public void execute() {
        		orgHistory();
        	}
        });
        
        orgContactHistory = (MenuItem)def.getWidget("orgContactHistory");
        addScreenHandler(orgContactHistory, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                orgContactHistory.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        orgContactHistory.addCommand(new Command(){
        	public void execute() {
        		orgContactHistory();
        	}
         });
        
        orgParameterHistory = (MenuItem)def.getWidget("orgParameterHistory");
        addScreenHandler(orgParameterHistory, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                orgParameterHistory.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        orgParameterHistory.addCommand(new Command() {
        	public void execute() {
        		orgParameterHistory();
        	}
        });

        //
        // screen fields
        //
        id = (TextBox)def.getWidget(OrganizationMeta.getId());
        addScreenHandler(id, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                id.setValue(manager.getOrganization().getId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getOrganization().setId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                id.setEnabled(EnumSet.of(State.QUERY).contains(event.getState()));
                id.setQueryMode(event.getState() == State.QUERY);
            }
        });

        name = (TextBox)def.getWidget(OrganizationMeta.getName());
        addScreenHandler(name, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                name.setValue(manager.getOrganization().getName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getOrganization().setName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                name.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                   .contains(event.getState()));
                name.setQueryMode(event.getState() == State.QUERY);
            }
        });

        city = (TextBox)def.getWidget(OrganizationMeta.getAddressCity());
        addScreenHandler(city, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                city.setValue(manager.getOrganization().getAddress().getCity());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getOrganization().getAddress().setCity(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                city.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                   .contains(event.getState()));
                city.setQueryMode(event.getState() == State.QUERY);
            }
        });

        multipleUnit = (TextBox)def.getWidget(OrganizationMeta.getAddressMultipleUnit());
        addScreenHandler(multipleUnit, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                multipleUnit.setValue(manager.getOrganization().getAddress().getMultipleUnit());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getOrganization().getAddress().setMultipleUnit(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                multipleUnit.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                           .contains(event.getState()));
                multipleUnit.setQueryMode(event.getState() == State.QUERY);
            }
        });

        stateCode = (Dropdown)def.getWidget(OrganizationMeta.getAddressState());
        addScreenHandler(stateCode, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                stateCode.setValue(manager.getOrganization().getAddress().getState());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getOrganization().getAddress().setState(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                stateCode.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                        .contains(event.getState()));
                stateCode.setQueryMode(event.getState() == State.QUERY);
            }
        });

        zipCode = (TextBox)def.getWidget(OrganizationMeta.getAddressZipCode());
        addScreenHandler(zipCode, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                zipCode.setValue(manager.getOrganization().getAddress().getZipCode());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getOrganization().getAddress().setZipCode(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                zipCode.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                      .contains(event.getState()));
                zipCode.setQueryMode(event.getState() == State.QUERY);
            }
        });

        streetAddress = (TextBox)def.getWidget(OrganizationMeta.getAddressStreetAddress());
        addScreenHandler(streetAddress, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                streetAddress.setValue(manager.getOrganization().getAddress().getStreetAddress());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getOrganization().getAddress().setStreetAddress(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                streetAddress.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                            .contains(event.getState()));
                streetAddress.setQueryMode(event.getState() == State.QUERY);
            }
        });

        country = (Dropdown)def.getWidget(OrganizationMeta.getAddressCountry());
        addScreenHandler(country, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                country.setValue(manager.getOrganization().getAddress().getCountry());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getOrganization().getAddress().setCountry(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                country.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                      .contains(event.getState()));
                country.setQueryMode(event.getState() == State.QUERY);
            }
        });

        parentName = (AutoComplete)def.getWidget(OrganizationMeta.getParentOrganizationName());
        addScreenHandler(parentName, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                parentName.setValue(manager.getOrganization().getParentOrganizationId(),
                                        manager.getOrganization().getParentOrganizationName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getOrganization().setParentOrganizationId(event.getValue());
                manager.getOrganization().setParentOrganizationName(parentName.getDisplay());
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
                Item<Integer> item;
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
                    list = service.callList("fetchByIdOrName", param);
                    model = new ArrayList<Item<Integer>>();
                    for (int i = 0; i < list.size(); i++ ) {
                        item = new Item<Integer>(4);
                        data = list.get(i);

                        item.setKey(data.getId());
                        item.setCell(0,data.getName());
                        item.setCell(1,data.getAddress().getStreetAddress());
                        item.setCell(2,data.getAddress().getCity());
                        item.setCell(3,data.getAddress().getState());

                        model.add(item);
                    }
                    parentName.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
                window.clearStatus();
            }
        });

        isActive = (CheckBox)def.getWidget(OrganizationMeta.getIsActive());
        addScreenHandler(isActive, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isActive.setValue(manager.getOrganization().getIsActive());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getOrganization().setIsActive(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isActive.setEnabled(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                       .contains(event.getState()));
                isActive.setQueryMode(event.getState() == State.QUERY);
            }
        });

        //
        // tabs
        //
        tabPanel = (TabPanel)def.getWidget("tabPanel");
        tabPanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                int i;

                // tab screen order should be the same as enum or this will
                // not work
                i = event.getItem().intValue();
                tab = Tabs.values()[i];

                window.setBusy();
                drawTabs();
                window.clearStatus();
            }
        });

        contactTab = new ContactTab(def, window);
        addScreenHandler(contactTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                contactTab.setManager(manager);
                if (tab == Tabs.CONTACT)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                contactTab.setState(event.getState());
            }
        });

        parameterTab = new ParameterTab(def, window);
        addScreenHandler(parameterTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                parameterTab.setManager(manager);
                if (tab == Tabs.PARAMETER)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                parameterTab.setState(event.getState());
            }
        });

        noteTab = new NotesTab(def, window, "notesPanel", "standardNoteButton");
        addScreenHandler(noteTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                noteTab.setManager(manager);
                if (tab == Tabs.NOTE)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                noteTab.setState(event.getState());
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
                            Window.alert("Error: Organization call query failed; " +
                                         error.getMessage());
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

                field = new QueryData();
                field.key = OrganizationMeta.getName();
                field.query = ((Button)event.getSource()).getAction();
                field.type = QueryData.Type.STRING;

                query = new Query();
                query.setFields(field);
                nav.setQuery(query);
            }
        });
        
        window.addBeforeClosedHandler(new BeforeCloseHandler<org.openelis.gwt.widget.Window>() {
            public void onBeforeClosed(BeforeCloseEvent<org.openelis.gwt.widget.Window> event) {                
                if (EnumSet.of(State.ADD, State.UPDATE).contains(state)) {
                    event.cancel();
                    window.setError(consts.get("mustCommitOrAbort"));
                }
            }
        });
    }

    private void initializeDropdowns() {
        ArrayList<Item<String>> model;
        ArrayList<DictionaryDO> list;
        Item<String> item;

        // country dropdown
        model = new ArrayList<Item<String>>();
        model.add(new Item<String>(null, ""));
        list = DictionaryCache.getListByCategorySystemName("country");
        for (DictionaryDO d : list) {
            item = new Item<String>(d.getEntry(), d.getEntry());
            item.setEnabled("Y".equals(d.getIsActive()));
            model.add(item);
        }

        country.setModel(model);

        // state dropdown
        model = new ArrayList<Item<String>>();
        model.add(new Item<String>(null, ""));
        list =  DictionaryCache.getListByCategorySystemName("state");
        for (DictionaryDO d : list) {
            item = new Item<String>(d.getEntry(), d.getEntry());
            item.setEnabled("Y".equals(d.getIsActive()));
            model.add(item);
        }

        stateCode.setModel(model);
    }

    /*
     * basic button methods
     */

    protected void query() {
        manager = OrganizationManager.getInstance();

        setState(State.QUERY);
        DataChangeEvent.fire(this);

        // clear all the tabs
        contactTab.draw();
        noteTab.draw();
        parameterTab.draw();
        
        setFocus(id);
        window.setDone(consts.get("enterFieldsToQuery"));
    }

    protected void previous() {
        nav.previous();
    }

    protected void next() {
        nav.next();
    }

    protected void add() {
        manager = OrganizationManager.getInstance();
        manager.getOrganization().setIsActive("Y");

        setState(State.ADD);
        DataChangeEvent.fire(this);

        setFocus(name);
        window.setDone(consts.get("enterInformationPressCommit"));
    }

    protected void update() {
        window.setBusy(consts.get("lockForUpdate"));

        try {
            manager = manager.fetchForUpdate();

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
    
    protected void orgHistory() {
        IdNameVO hist;
        
        hist = new IdNameVO(manager.getOrganization().getId(), manager.getOrganization().getName());
        HistoryScreen.showHistory(consts.get("orgHistory"),
                                  ReferenceTable.ORGANIZATION, hist);                
    }
    
    protected void orgContactHistory() {
        int i, count;
        IdNameVO refVoList[];
        OrganizationContactManager man;
        OrganizationContactDO data;

        try {
            man = manager.getContacts();
            count = man.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = man.getContactAt(i);
                refVoList[i] = new IdNameVO(data.getId(), data.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(consts.get("orgContactHistory"),
                                  ReferenceTable.ORGANIZATION_CONTACT, refVoList);
    }
    
    protected void orgParameterHistory() {        
        int i, count;
        IdNameVO refVoList[];
        OrganizationParameterManager man;
        OrganizationParameterDO data;

        try {
            man = manager.getParameters();
            count = man.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = man.getParameterAt(i);
                refVoList[i] = new IdNameVO(data.getId(), data.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(consts.get("orgParameterHistory"),
                                  ReferenceTable.ORGANIZATION_PARAMETER, refVoList);
    }


    protected boolean fetchById(Integer id) {
        if (id == null) {
            manager = OrganizationManager.getInstance();
            setState(State.DEFAULT);
        } else {
            window.setBusy(consts.get("fetching"));
            try {
                switch (tab) {
                    case CONTACT:
                        manager = OrganizationManager.fetchWithContacts(id);
                        break;
                    case PARAMETER:
                        manager = OrganizationManager.fetchWithParameters(id);
                        break;
                    case NOTE:
                        manager = OrganizationManager.fetchWithNotes(id);
                        break;
                }
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

    private void drawTabs() {
        switch (tab) {
            case CONTACT:
                contactTab.draw();
                break;
            case PARAMETER:
                parameterTab.draw();
                break;
            case NOTE:
                noteTab.draw();
                break;
        }
    }
}