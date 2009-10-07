/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public Software License(the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa. Portions created by The University of Iowa are Copyright 2006-2008. All Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be used under the terms of a UIRF Software license ("UIRF Software License"), in which case the provisions of a UIRF Software License are applicable instead of those above.
 */
package org.openelis.modules.organization.client;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;

import org.openelis.cache.DictionaryCache;
import org.openelis.common.AutocompleteRPC;
import org.openelis.common.NotesTab;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameDO;
import org.openelis.domain.OrganizationVO;
import org.openelis.domain.OrganizationViewDO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.gwt.common.EntityLockedException;
import org.openelis.gwt.common.InconsistencyException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.SecurityModule;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.BeforeGetMatchesEvent;
import org.openelis.gwt.event.BeforeGetMatchesHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.screen.Screen.State;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.HasField;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.manager.OrganizationManager;
import org.openelis.metamap.OrganizationMetaMap;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TabPanel;

public class OrganizationScreen extends Screen implements BeforeGetMatchesHandler, GetMatchesHandler {

    public enum Tabs {
        CONTACTS, IDENTIFIERS, NOTES
    };

    protected Tabs               tab           = Tabs.CONTACTS;

    private OrganizationManager manager;
    private ContactsTab          contactsTab;
    private NotesTab             notesTab;

    private SecurityModule       security;

    private OrganizationMetaMap  meta       = new OrganizationMetaMap();

    ScreenNavigator nav;
    
    public OrganizationScreen() throws Exception {
        // Call base to get ScreenDef and draw screen
    	super((ScreenDefInt)GWT.create(OrganizationDef.class));
        service = new ScreenService("OpenELISServlet?service=org.openelis.modules.organization.server.OrganizationService");

        security = OpenELIS.security.getModule("organization");
        if (security == null)
            throw new InconsistencyException("Error: Missing security module entry 'organization'; No permission to this screen.");

        // Setup link between Screen and widget Handlers
        initialize();

        manager = OrganizationManager.getInstance();
        setState(State.DEFAULT);
        setCountriesModel();
        setStatesModel();
        DataChangeEvent.fire(this);
    }

    private void initialize() {

        final TextBox name = (TextBox)def.getWidget(meta.getName());
        addScreenHandler(name, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                name.setValue(manager.getOrganizationAddress().getName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getOrganizationAddress().setName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                name.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                   .contains(event.getState()));
                name.setQueryMode(event.getState() == State.QUERY);

                if (event.getState() == State.ADD || event.getState() == State.UPDATE)
                    name.setFocus(true);
            }
        });

        final TextBox street = (TextBox)def.getWidget(meta.ADDRESS.getStreetAddress());
        addScreenHandler(street, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                street.setValue(manager.getOrganizationAddress()
                                       .getAddressDO()
                                       .getStreetAddress());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getOrganizationAddress()
                       .getAddressDO()
                       .setStreetAddress(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                street.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                     .contains(event.getState()));
                street.setQueryMode(event.getState() == State.QUERY);
            }
        });

        final TextBox id = (TextBox)def.getWidget(meta.getId());
        addScreenHandler(id, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                id.setValue(getString(manager.getOrganizationAddress()
                                             .getId()));
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getOrganizationAddress()
                       .setId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                id.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                id.setQueryMode(event.getState() == State.QUERY);

                if (event.getState() == State.QUERY)
                    id.setFocus(true);
            }
        });

        final TextBox multipleUnit = (TextBox)def.getWidget(meta.getAddress()
                                                                   .getMultipleUnit());
        addScreenHandler(multipleUnit, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                multipleUnit.setValue(manager.getOrganizationAddress()
                                             .getAddressDO()
                                             .getMultipleUnit());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getOrganizationAddress()
                       .getAddressDO()
                       .setMultipleUnit(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                multipleUnit.enable(EnumSet.of(State.ADD,
                                               State.UPDATE,
                                               State.QUERY)
                                           .contains(event.getState()));
                multipleUnit.setQueryMode(event.getState() == State.QUERY);
            }
        });

        final TextBox city = (TextBox)def.getWidget(meta.getAddress()
                                                           .getCity());
        addScreenHandler(city, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                city.setValue(manager.getOrganizationAddress()
                                     .getAddressDO()
                                     .getCity());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getOrganizationAddress()
                       .getAddressDO()
                       .setCity(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                city.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                   .contains(event.getState()));
                city.setQueryMode(event.getState() == State.QUERY);
            }
        });

        final TextBox zipCode = (TextBox)def.getWidget(meta.getAddress()
                                                              .getZipCode());
        addScreenHandler(zipCode, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                zipCode.setValue(manager.getOrganizationAddress()
                                        .getAddressDO()
                                        .getZipCode());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getOrganizationAddress()
                       .getAddressDO()
                       .setZipCode(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                zipCode.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                      .contains(event.getState()));
                zipCode.setQueryMode(event.getState() == State.QUERY);
            }
        });

        final Dropdown<String> state = (Dropdown<String>)def.getWidget(meta.getAddress()
                                                                              .getState());
        addScreenHandler(state, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                state.setSelection(manager.getOrganizationAddress()
                                          .getAddressDO()
                                          .getState());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getOrganizationAddress()
                       .getAddressDO()
                       .setState(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                state.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                    .contains(event.getState()));
                state.setQueryMode(event.getState() == State.QUERY);
            }
        });

        final Dropdown<String> country = (Dropdown<String>)def.getWidget(meta.getAddress()
                                                                                .getCountry());
        addScreenHandler(country, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                country.setSelection(manager.getOrganizationAddress()
                                            .getAddressDO()
                                            .getCountry());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getOrganizationAddress()
                       .getAddressDO()
                       .setCountry(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                country.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                      .contains(event.getState()));
                country.setQueryMode(event.getState() == State.QUERY);
            }
        });

        final CheckBox isActive = (CheckBox)def.getWidget(meta.getIsActive());
        addScreenHandler(isActive, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isActive.setValue(manager.getOrganizationAddress()
                                         .getIsActive());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getOrganizationAddress().setIsActive(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isActive.enable(EnumSet.of(State.ADD, State.UPDATE, State.QUERY)
                                       .contains(event.getState()));
                isActive.setQueryMode(event.getState() == State.QUERY);
            }
        });

        final AutoComplete<Integer> parentOrg = (AutoComplete)def.getWidget(meta.getParentOrganization()
                                                                                   .getName());
        addScreenHandler(parentOrg, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                OrganizationViewDO orgDO = manager.getOrganizationAddress();
                if (orgDO.getParentOrganizationId() == null) {
                    parentOrg.clear();
                } else {
                    ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
                    model.add(new TableDataRow(orgDO.getParentOrganizationId(),
                                               orgDO.getParentOrganizationName()));
                    parentOrg.setModel(model);
                }
                parentOrg.setSelection(manager.getOrganizationAddress()
                                              .getParentOrganizationId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getOrganizationAddress()
                       .setParentOrganizationId(event.getValue());
                manager.getOrganizationAddress()
                .setParentOrganizationName(parentOrg.getTextBoxDisplay());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                parentOrg.enable(EnumSet.of(State.ADD,
                                            State.UPDATE,
                                            State.QUERY)
                                        .contains(event.getState()));
                parentOrg.setQueryMode(event.getState() == State.QUERY);
            }
            
            
        });

        // Create Default blank model for AutoComplete field Parent Org
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        TableDataRow row = new TableDataRow(4);
        model.add(row);
        parentOrg.setModel(model);

        // Screens now must implement AutoCompleteCallInt and set themselves as the calling interface
        parentOrg.addBeforeGetMatchesHandler(this);
        parentOrg.addGetMatchesHandler(this);

        final AppButton queryButton = (AppButton)def.getWidget("query");
        addScreenHandler(queryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                query();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if(EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState()) && 
                                security.hasSelectPermission())
                    queryButton.enable(true);
                else if (event.getState() == State.QUERY)
                    queryButton.changeState(ButtonState.LOCK_PRESSED);
                else
                    queryButton.enable(false);
            }
        });

        final AppButton addButton = (AppButton)def.getWidget("add");
        addScreenHandler(addButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                add();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState()) && 
                                security.hasAddPermission())
                    addButton.enable(true);
                else if (EnumSet.of(State.ADD).contains(event.getState()))
                    addButton.changeState(ButtonState.LOCK_PRESSED);
                else
                    addButton.enable(false);
            }
        });

        final AppButton updateButton = (AppButton)def.getWidget("update");
        addScreenHandler(updateButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                update();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (EnumSet.of(State.DISPLAY).contains(event.getState()) && 
                                security.hasUpdatePermission())
                    updateButton.enable(true);
                else if (EnumSet.of(State.UPDATE).contains(event.getState()))
                    updateButton.changeState(ButtonState.LOCK_PRESSED);
                else
                    updateButton.enable(false);

            }
        });

        final AppButton nextButton = (AppButton)def.getWidget("next");
        addScreenHandler(nextButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                next();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                nextButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        final AppButton prevButton = (AppButton)def.getWidget("previous");
        addScreenHandler(prevButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                previous();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                prevButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        final AppButton commitButton = (AppButton)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        final AppButton abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        // Get AZ buttons and setup Screen listeners and call to for query
        final ButtonGroup azButtons = (ButtonGroup)def.getWidget("atozButtons");
        addScreenHandler(azButtons, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                azButtons.enable(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                        .contains(event.getState()) &&
                                 security.hasSelectPermission());
            }

            public void onClick(ClickEvent event) {
                String baction = ((AppButton)event.getSource()).action;
                getOrganizations(baction.substring(6, baction.length()));
            }
        });

        // Get TabPanel and set Tab Selection Handlers
        final TabPanel tabs = (TabPanel)def.getWidget("orgTabPanel");
        tabs.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                int tabIndex = event.getSelectedItem().intValue();
                if (tabIndex == Tabs.CONTACTS.ordinal())
                    tab = Tabs.CONTACTS;
                else if (tabIndex == Tabs.IDENTIFIERS.ordinal())
                    tab = Tabs.IDENTIFIERS;
                else if (tabIndex == Tabs.NOTES.ordinal())
                    tab = Tabs.NOTES;

                window.setBusy(consts.get("loadingMessage"));
                drawTabs();
                window.clearStatus();
            }
        });

        // Create the Handler for the Contacts tab passing in the ScreenDef
        contactsTab = new ContactsTab(def);
        addScreenHandler(contactsTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                contactsTab.setManager(manager);

                if (tab == Tabs.CONTACTS)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                contactsTab.setState(event.getState());
            }
        });

        // Create the Handler for the Notes tab passing in the ScreenDef;
        notesTab = new NotesTab(def, "notesPanel", "standardNoteButton", false);
        addScreenHandler(notesTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                notesTab.setManager(manager);

                if (tab == Tabs.NOTES)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                notesTab.setState(event.getState());
            }
        });

/*        
        nav = new ScreenNavigator<OrgQuery>(this) {
            public void getSelection(RPC entry) {
                fetch(((IdNameDO)entry).getId());
            }
            public void loadPage(OrgQuery query) {
                loadQueryPage(query);
            }
        };
*/    

    }

    protected void query() {
        manager = OrganizationManager.getInstance();
        
        DataChangeEvent.fire(this);
        setState(Screen.State.QUERY);
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
        manager.getOrganizationAddress().setIsActive("Y");

        setState(Screen.State.ADD);
        DataChangeEvent.fire(this);
        window.setDone(consts.get("enterInformationPressCommit"));
    }

    protected void update() {
        window.setBusy(consts.get("lockForUpdate"));

        try {
            manager = manager.fetchForUpdate();

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

    public void commit() {
        if (state == State.UPDATE) {
            if (validate()) {
                window.setBusy(consts.get("updating"));
                commitUpdate();
            } else {
                window.setError(consts.get("correctErrors"));
            }
        }
        if (state == State.ADD) {
            if (validate()) {
                window.setBusy(consts.get("adding"));
                commitAdd();
            } else {
                window.setError(consts.get("correctErrors"));
            }
        }
        if (state == State.QUERY) {
            if (validate()) {
                ArrayList<QueryData> qFields = getQueryFields();
                commitQuery(qFields);
            } else {
                window.setError(consts.get("correctErrors"));
            }
        }
    }

    public void abort() {
        if (state == State.UPDATE) {
            window.setBusy(consts.get("cancelChanges"));

            try {
                manager = manager.abort();

                clearErrors();
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                
                window.clearStatus();
            } catch (Exception e) {
                Window.alert(e.getMessage());
                window.clearStatus();
            }

        } else if (state == State.ADD) {
            manager = OrganizationManager.getInstance();
            clearErrors();
            setState(State.DEFAULT);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("addAborted"));
        } else if (state == State.QUERY) {
            manager = OrganizationManager.getInstance();
            clearErrors();
            setState(State.DEFAULT);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("queryAborted"));
        }
    }

    protected void fetch(Integer id) {
        window.setBusy(consts.get("fetching"));
        try {
            if (tab == Tabs.CONTACTS) {
                manager = OrganizationManager.findByIdWithContacts(id);
            } else if (tab == Tabs.IDENTIFIERS) {
                manager = OrganizationManager.findByIdWithIdentifiers(id);
            } else if (tab == Tabs.NOTES) {
                manager = OrganizationManager.findByIdWithNotes(id);
            }

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

    private void getOrganizations(String query) {
        if (state == State.DISPLAY || state == State.DEFAULT) {
            QueryData qField = new QueryData();
            qField.key = meta.getName();
            qField.query = query;
            qField.type = QueryData.Type.STRING;
            commitQuery(qField);
        }
    }

    public ArrayList<QueryData> getQueryFields() {
        ArrayList<QueryData> list = new ArrayList<QueryData>();
        Set<String> keys = def.getWidgets().keySet();
        for (String key : def.getWidgets().keySet()) {
            if (def.getWidget(key) instanceof HasField) {
                ((HasField)def.getWidget(key)).getQuery(list, key);
            }
        }
        return list;
    }

    public void commitAdd() {
        window.setBusy(consts.get("commiting"));
        try {
            manager = manager.add();

            
            setState(Screen.State.DISPLAY);
            DataChangeEvent.fire(this);
            window.clearStatus();
        } catch (ValidationErrorsList e) {
            showErrors(e);

        } catch (Exception e) {
            Window.alert("commitAdd(): " + e.getMessage());
            window.clearStatus();
        }
    }

    public void commitUpdate() {
        window.setBusy(consts.get("commiting"));
        try {
            manager = manager.update();

            setState(Screen.State.DISPLAY);
            DataChangeEvent.fire(this);
            window.clearStatus();
        } catch (ValidationErrorsList e) {
            showErrors(e);

        } catch (Exception e) {
            Window.alert("commitUpdate(): " + e.getMessage());

        }
    }

    public void commitQuery(QueryData qField) {
        ArrayList<QueryData> qList = new ArrayList<QueryData>();
        qList.add(qField);
        commitQuery(qList);
    }

    public void commitQuery(ArrayList<QueryData> qFields) {
/*
        OrgQuery query = new OrgQuery();
//        query.fields = qFields;
        window.setBusy(consts.get("querying"));

        service.call("query", query, new AsyncCallback<OrgQuery>() {
            public void onSuccess(OrgQuery query) {
                loadQuery(query);
            }

            public void onFailure(Throwable caught) {
                if (caught instanceof ValidationErrorsList)
                    showErrors((ValidationErrorsList)caught);
                else
                    Window.alert(caught.getMessage());
            }
        });
*/
    }

    public void loadQuery(Query query) {
        manager = OrganizationManager.getInstance();
        DataChangeEvent.fire(this);

        loadQueryPage(query);

        //ActionEvent.fire(this, Action.NEW_MODEL, query);
    }

    private void loadQueryPage(Query query) {
        ArrayList<TableDataRow> model;
/*        
        window.setDone(consts.get("queryingComplete"));
        if (query.getResults() == null) {
            window.setDone(consts.get("noRecordsFound"));
            setState(State.DEFAULT);
        } else {
            window.setDone(consts.get("queryingComplete"));
        }
        model = new ArrayList<TableDataRow>();
        for (IdNameDO entry : query.getResults()) {
            model.add(new TableDataRow(entry.getId(), entry.getName()));
        }
        
        query.setModel(model);
        nav.setQuery(query);
*/
    }

    private void setCountriesModel() {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : (ArrayList<DictionaryDO>)DictionaryCache.getListByCategorySystemName("country")) {
            model.add(new TableDataRow(resultDO.getEntry(), resultDO.getEntry()));
        }
        ((Dropdown<String>)def.getWidget(meta.ADDRESS.getCountry())).setModel(model);
    }

    private void setStatesModel() {
        ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO resultDO : (ArrayList<DictionaryDO>)DictionaryCache.getListByCategorySystemName("state")) {
            model.add(new TableDataRow(resultDO.getEntry(), resultDO.getEntry()));
        }
        ((Dropdown<String>)def.getWidget(meta.ADDRESS.getState())).setModel(model);
    }

    private void drawTabs() {
        if (tab == Tabs.CONTACTS) {
            contactsTab.draw();
        } else if (tab == Tabs.IDENTIFIERS) {
            // manager = OrganizationsManager.findByIdWithIdentifiers(id);
        } else if (tab == Tabs.NOTES) {
            notesTab.draw();
        }
    }

    public void onBeforeGetMatches(BeforeGetMatchesEvent event) {
        
        
    }

    public void onGetMatches(GetMatchesEvent event) {
        AutocompleteRPC rpc = new AutocompleteRPC();
        rpc.match = event.getMatch();
        try {
            rpc = service.call("getMatches",rpc);
            ArrayList<TableDataRow> model = new ArrayList<TableDataRow>();
            
            for (int i=0; i<rpc.model.size(); i++){
                OrganizationVO autoDO = (OrganizationVO)rpc.model.get(i);
                
                TableDataRow row = new TableDataRow(4);
                row.key = autoDO.getId();
                row.cells.get(0).value = autoDO.getName();
                row.cells.get(1).value = autoDO.getStreetAddress();
                row.cells.get(2).value = autoDO.getCity();
                row.cells.get(3).value = autoDO.getState();
                model.add(row);
            }
            ((AutoComplete)event.getSource()).showAutoMatches(model);
       }catch(Exception e) {
           Window.alert(e.getMessage());                     
       }
    }
}