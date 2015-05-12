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
package org.openelis.modules.organization1.client;

import static org.openelis.modules.main.client.Logger.logger;
import static org.openelis.ui.screen.Screen.ShortKeys.CTRL;
import static org.openelis.ui.screen.State.ADD;
import static org.openelis.ui.screen.State.DEFAULT;
import static org.openelis.ui.screen.State.DISPLAY;
import static org.openelis.ui.screen.State.QUERY;
import static org.openelis.ui.screen.State.UPDATE;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AddressDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrganizationContactDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.manager.OrganizationManager1;
import org.openelis.manager.OrganizationManager1.Load;
import org.openelis.meta.OrganizationMeta;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.main.client.OpenELIS;
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
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.Menu;
import org.openelis.ui.widget.MenuItem;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.TabLayoutPanel;
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

public class OrganizationScreenUI extends Screen {

    @UiTemplate("Organization.ui.xml")
    interface OrganizationUiBinder extends UiBinder<Widget, OrganizationScreenUI> {
    };

    private static OrganizationUiBinder             uiBinder   = GWT.create(OrganizationUiBinder.class);

    protected OrganizationManager1                  manager;

    protected ModulePermission                      userPermission;

    @UiField
    protected AtoZButtons                           atozButtons;

    protected ScreenNavigator<IdNameVO>             nav;

    @UiField
    protected TabLayoutPanel                        tabPanel;

    @UiField(provided = true)
    protected ContactTabUI                          contactTab;

    @UiField(provided = true)
    protected ParameterTabUI                        parameterTab;

    @UiField(provided = true)
    protected InternalNotesTabUI                    notesTab;

    protected OrganizationScreenUI                  screen;

    @UiField
    protected Button                                query, previous, next, add, update, commit,
                    abort, optionsButton, loadResults;

    @UiField
    protected Menu                                  optionsMenu;

    @UiField
    protected MenuItem                              orgHistory, orgAddressHistory,
                    orgContactHistory, orgContactAddressHistory, orgParameterHistory;

    @UiField
    protected TextBox<Integer>                      id;

    @UiField
    protected TextBox<String>                       name, multipleUnit, city, zipCode,
                    streetAddress;

    @UiField
    protected CheckBox                              isActive;

    @UiField
    protected Dropdown<String>                      state, country;

    @UiField
    protected AutoComplete                          parentName;

    @UiField
    protected Table                                 atozTable;

    private OrganizationService1Impl                service    = OrganizationService1Impl.INSTANCE;

    protected AsyncCallbackUI<ArrayList<IdNameVO>>  queryCall;

    protected AsyncCallbackUI<OrganizationManager1> fetchForUpdateCall, updateCall, fetchByIdCall,
                    unlockCall;

    protected OrganizationManager1.Load             elements[] = {Load.CONTACTS, Load.PARAMETERS,
                    Load.NOTES                                 };

    public OrganizationScreenUI(WindowInt window) throws Exception {

        setWindow(window);

        userPermission = UserCache.getPermission().getModule("organization");
        if (userPermission == null)
            throw new PermissionException(Messages.get().screenPermException("Oranization Screen"));

        contactTab = new ContactTabUI(this);
        parameterTab = new ParameterTabUI(this);
        notesTab = new InternalNotesTabUI(this);

        initWidget(uiBinder.createAndBindUi(this));

        manager = null;

        try {
            CategoryCache.getBySystemNames("country", "state", "contact_type", "parameter_type");
        } catch (Exception e) {
            Window.alert("OrganizationScreen: missing dictionary entry; " + e.getMessage());
            window.close();
        }

        initialize();
        setData();
        setState(DEFAULT);
        fireDataChange();

        logger.fine("Organization Screen Opened");

    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        ArrayList<Item<String>> model;
        ArrayList<DictionaryDO> list;
        Item<String> row;

        screen = this;
        /*
         * button panel buttons
         */
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

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                optionsMenu.setEnabled(isState(DISPLAY));
                optionsButton.setEnabled(isState(DISPLAY));
                orgHistory.setEnabled(isState(DISPLAY));
            }
        });

        /*
         * option menu items
         */
        orgHistory.addCommand(new Command() {
            @Override
            public void execute() {
                orgHistory();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                orgHistory.setEnabled(isState(DISPLAY));
            }
        });

        orgAddressHistory.addCommand(new Command() {
            public void execute() {
                orgAddressHistory();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                orgAddressHistory.setEnabled(isState(DISPLAY));
            }
        });

        orgContactHistory.addCommand(new Command() {
            public void execute() {
                orgContactHistory();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                orgContactHistory.setEnabled(isState(DISPLAY));
            }
        });

        orgContactAddressHistory.addCommand(new Command() {
            public void execute() {
                orgContactAddressHistory();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                orgContactAddressHistory.setEnabled(isState(DISPLAY));
            }
        });

        orgParameterHistory.addCommand(new Command() {
            public void execute() {
                orgParameterHistory();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                orgParameterHistory.setEnabled(isState(DISPLAY));
            }
        });

        /*
         * screen fields
         */
        addScreenHandler(id, OrganizationMeta.getId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                id.setValue(getId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getOrganization().setId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                id.setEnabled(isState(QUERY));
                id.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? name : null;
            }
        });

        addScreenHandler(name, OrganizationMeta.getName(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                name.setValue(getName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getOrganization().setName(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                name.setEnabled(isState(QUERY, ADD, UPDATE));
                name.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? city : id;
            }
        });

        addScreenHandler(city, OrganizationMeta.getAddressCity(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                city.setValue(getCity());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getOrganization().getAddress().setCity(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                city.setEnabled(isState(QUERY, ADD, UPDATE));
                city.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? multipleUnit : name;
            }
        });

        addScreenHandler(multipleUnit,
                         OrganizationMeta.getAddressMultipleUnit(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 multipleUnit.setValue(getMultipleUnit());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 manager.getOrganization()
                                        .getAddress()
                                        .setMultipleUnit(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 multipleUnit.setEnabled(isState(QUERY, ADD, UPDATE));
                                 multipleUnit.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? state : city;
                             }
                         });

        addScreenHandler(state, OrganizationMeta.getAddressState(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                state.setValue(getState());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getOrganization().getAddress().setState(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                state.setEnabled(isState(QUERY, ADD, UPDATE));
                state.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? zipCode : multipleUnit;
            }
        });

        addScreenHandler(zipCode,
                         OrganizationMeta.getAddressZipCode(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 zipCode.setValue(getZipCode());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 manager.getOrganization()
                                        .getAddress()
                                        .setZipCode(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 zipCode.setEnabled(isState(QUERY, ADD, UPDATE));
                                 zipCode.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? streetAddress : state;
                             }
                         });

        addScreenHandler(streetAddress,
                         OrganizationMeta.getAddressStreetAddress(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 streetAddress.setValue(getStreetAddress());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 manager.getOrganization()
                                        .getAddress()
                                        .setStreetAddress(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 streetAddress.setEnabled(isState(QUERY, ADD, UPDATE));
                                 streetAddress.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? country : zipCode;
                             }
                         });

        addScreenHandler(country,
                         OrganizationMeta.getAddressCountry(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 country.setValue(getCountry());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 manager.getOrganization()
                                        .getAddress()
                                        .setCountry(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 country.setEnabled(isState(QUERY, ADD, UPDATE));
                                 country.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(parentName,
                         OrganizationMeta.getParentOrganizationName(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 parentName.setValue(getParentOrganizationId(),
                                                     getParentOrganizationName());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 AutoCompleteValue row;

                                 row = parentName.getValue();
                                 manager.getOrganization().setParentOrganizationId(row.getId());
                                 manager.getOrganization()
                                        .setParentOrganizationName(row.getDisplay());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 parentName.setEnabled(isState(QUERY, ADD, UPDATE));
                                 parentName.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? isActive : country;
                             }
                         });

        parentName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                Item<Integer> row;
                OrganizationDO data;
                ArrayList<OrganizationDO> list;
                ArrayList<Item<Integer>> model;

                setBusy();
                try {
                    list = service.fetchByIdOrName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<Item<Integer>>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new Item<Integer>(4);
                        data = list.get(i);

                        row.setKey(data.getId());
                        row.setCell(0, data.getName());
                        row.setCell(1, data.getAddress().getStreetAddress());
                        row.setCell(2, data.getAddress().getCity());
                        row.setCell(3, data.getAddress().getState());

                        model.add(row);
                    }
                    parentName.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
                clearStatus();
            }
        });

        addScreenHandler(isActive, OrganizationMeta.getIsActive(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isActive.setValue(getIsActive());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getOrganization().setIsActive(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                isActive.setEnabled(isState(QUERY, ADD, UPDATE));
                isActive.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? name : parentName;
            }
        });

        /*
         * tabs
         */
        tabPanel.setPopoutBrowser(OpenELIS.getBrowser());

        addScreenHandler(contactTab, "contactTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                contactTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                contactTab.setState(event.getState());
            }
        });

        addScreenHandler(parameterTab, "parameterTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                parameterTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                parameterTab.setState(event.getState());
            }
        });

        addScreenHandler(notesTab, "notesTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                notesTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                notesTab.setState(event.getState());
            }
        });

        /*
         * left hand navigation panel
         */
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
                            Window.alert("Error: Organization call query failed; " +
                                         error.getMessage());
                            setError(Messages.get().gen_queryFailed());
                        }
                    };
                }
                query.setRowsPerPage(23);
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
                    setError(Messages.get().mustCommitOrAbort());
                } else {
                    /*
                     * make sure that all detached tabs are closed when the main
                     * screen is closed
                     */
                    tabPanel.close();
                }
            }
        });

        // country dropdown
        model = new ArrayList<Item<String>>();
        model.add(new Item<String>(null, ""));
        list = CategoryCache.getBySystemName("country");
        for (DictionaryDO d : list) {
            row = new Item<String>(d.getEntry(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }

        country.setModel(model);

        // state dropdown
        model = new ArrayList<Item<String>>();
        model.add(new Item<String>(null, ""));
        list = CategoryCache.getBySystemName("state");
        for (DictionaryDO d : list) {
            row = new Item<String>(d.getEntry(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }

        state.setModel(model);
    }

    /*
     * basic button methods
     */
    @UiHandler("atozButtons")
    public void atozQuery(ClickEvent event) {
        Query query;
        QueryData field;

        field = new QueryData();
        field.setKey(OrganizationMeta.getName());
        field.setQuery( ((Button)event.getSource()).getAction());
        field.setType(QueryData.Type.STRING);

        query = new Query();
        query.setFields(field);
        nav.setQuery(query);
    }

    @UiHandler("query")
    protected void query(ClickEvent event) {
        manager = new OrganizationManager1();
        ;
        setData();
        setState(QUERY);
        fireDataChange();

        id.setFocus(true);
        setDone(Messages.get().enterFieldsToQuery());
    }

    @UiHandler("previous")
    protected void previous(ClickEvent event) {
        nav.previous();
    }

    @UiHandler("next")
    protected void next(ClickEvent event) {
        nav.next();
    }

    @UiHandler("add")
    protected void add(ClickEvent event) {
        setBusy();

        manager = new OrganizationManager1();
        manager.getOrganization().setIsActive("N");

        setData();
        setState(ADD);
        fireDataChange();
        name.setFocus(true);
        setDone(Messages.get().gen_enterInformationPressCommit());
    }

    @UiHandler("update")
    protected void update(ClickEvent event) {
        setBusy(Messages.get().lockForUpdate());

        if (fetchForUpdateCall == null) {
            fetchForUpdateCall = new AsyncCallbackUI<OrganizationManager1>() {
                public void success(OrganizationManager1 result) {
                    manager = result;
                    setData();
                    setState(UPDATE);
                    fireDataChange();
                    name.setFocus(true);
                }

                public void failure(Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
                }

                public void finish() {
                    clearStatus();
                }
            };
        }

        service.fetchForUpdate(manager.getOrganization().getId(), elements, fetchForUpdateCall);
    }

    @UiHandler("commit")
    protected void commit(ClickEvent event) {
        Validation validation;

        finishEditing();

        validation = validate();

        switch (validation.getStatus()) {
            case FLAGGED:
                /*
                 * some part of the screen has some operation that needs to be
                 * completed before committing the data
                 */
                return;
            case ERRORS:
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
            updateCall = new AsyncCallbackUI<OrganizationManager1>() {
                public void success(OrganizationManager1 result) {
                    manager = result;
                    setData();
                    setState(DISPLAY);
                    fireDataChange();
                    clearStatus();
                }

                public void validationErrors(ValidationErrorsList e) {
                    showErrors(e);
                }

                public void failure(Throwable e) {
                    if (isState(ADD))
                        Window.alert("commitAdd(): " + e.getMessage());
                    else
                        Window.alert("commitUpdate(): " + e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    clearStatus();
                }
            };
        }

        service.update(manager, updateCall);
    }

    @UiHandler("abort")
    protected void abort(ClickEvent event) {
        finishEditing();
        clearErrors();
        setBusy(Messages.get().cancelChanges());

        if (isState(QUERY)) {
            fetchById(null);
            setDone(Messages.get().gen_queryAborted());
        } else if (isState(ADD)) {
            fetchById(null);
            setDone(Messages.get().gen_addAborted());
        } else if (isState(UPDATE)) {
            if (unlockCall == null) {
                unlockCall = new AsyncCallbackUI<OrganizationManager1>() {
                    public void success(OrganizationManager1 result) {
                        manager = result;
                        setData();
                        setState(DISPLAY);
                        fireDataChange();
                        setDone(Messages.get().gen_updateAborted());
                    }

                    public void failure(Throwable e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        clearStatus();
                    }
                };
            }
            service.unlock(manager.getOrganization().getId(), elements, unlockCall);
        }
    }

    protected void orgHistory() {
        IdNameVO hist;

        hist = new IdNameVO(manager.getOrganization().getId(), manager.getOrganization().getName());
        HistoryScreen.showHistory(Messages.get().orgHistory(), Constants.table().ORGANIZATION, hist);
    }

    protected void orgAddressHistory() {
        IdNameVO hist;
        AddressDO addr;

        addr = manager.getOrganization().getAddress();
        hist = new IdNameVO(addr.getId(), addr.getStreetAddress());
        HistoryScreen.showHistory(Messages.get().orgAddressHistory(),
                                  Constants.table().ADDRESS,
                                  hist);
    }

    protected void orgContactHistory() {
        int i, count;
        IdNameVO refVoList[];
        OrganizationContactDO data;

        try {
            count = manager.contact.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = manager.contact.get(i);
                refVoList[i] = new IdNameVO(data.getId(), data.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(Messages.get().orgContactHistory(),
                                  Constants.table().ORGANIZATION_CONTACT,
                                  refVoList);
    }

    protected void orgContactAddressHistory() {
        int i, count;
        IdNameVO refVoList[];
        AddressDO addr;

        try {
            count = manager.contact.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                addr = manager.contact.get(i).getAddress();
                refVoList[i] = new IdNameVO(addr.getId(), addr.getStreetAddress());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(Messages.get().orgContactAddressHistory(),
                                  Constants.table().ADDRESS,
                                  refVoList);
    }

    protected void orgParameterHistory() {
        int i, count;
        IdNameVO refVoList[];
        OrganizationParameterDO data;

        try {
            count = manager.parameter.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = manager.parameter.get(i);
                refVoList[i] = new IdNameVO(data.getId(), data.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(Messages.get().orgParameterHistory(),
                                  Constants.table().ORGANIZATION_PARAMETER,
                                  refVoList);
    }

    /*
     * getters and setters
     */
    private Integer getId() {
        if (manager == null)
            return null;
        return manager.getOrganization().getId();
    }

    private String getName() {
        if (manager == null)
            return null;
        return manager.getOrganization().getName();
    }

    private String getCity() {
        if (manager == null || manager.getOrganization().getAddress() == null)
            return null;
        return manager.getOrganization().getAddress().getCity();
    }

    private String getMultipleUnit() {
        if (manager == null || manager.getOrganization().getAddress() == null)
            return null;
        return manager.getOrganization().getAddress().getMultipleUnit();
    }

    private String getState() {
        if (manager == null || manager.getOrganization().getAddress() == null)
            return null;
        return manager.getOrganization().getAddress().getState();
    }

    private String getZipCode() {
        if (manager == null || manager.getOrganization().getAddress() == null)
            return null;
        return manager.getOrganization().getAddress().getZipCode();
    }

    private String getStreetAddress() {
        if (manager == null || manager.getOrganization().getAddress() == null)
            return null;
        return manager.getOrganization().getAddress().getStreetAddress();
    }

    private String getCountry() {
        if (manager == null || manager.getOrganization().getAddress() == null)
            return null;
        return manager.getOrganization().getAddress().getCountry();
    }

    private Integer getParentOrganizationId() {
        if (manager == null)
            return null;
        return manager.getOrganization().getParentOrganizationId();
    }

    private String getParentOrganizationName() {
        if (manager == null)
            return null;
        return manager.getOrganization().getParentOrganizationName();
    }

    private String getIsActive() {
        if (manager == null || manager.getOrganization().getAddress() == null)
            return null;
        return manager.getOrganization().getIsActive();
    }

    private void setData() {
        contactTab.setData(manager);
        parameterTab.setData(manager);
        notesTab.setData(manager);
    }

    /**
     * Sets the latest manager in the tabs
     */
    protected void fetchById(Integer id) {
        if (id == null) {
            manager = null;
            setData();
            setState(DEFAULT);
            fireDataChange();
            clearStatus();
        } else {
            setBusy(Messages.get().fetching());
            if (fetchByIdCall == null) {
                fetchByIdCall = new AsyncCallbackUI<OrganizationManager1>() {
                    public void success(OrganizationManager1 result) {
                        manager = result;
                        setData();
                        setState(DISPLAY);
                    }

                    public void notFound() {
                        fetchById(null);
                        setDone(Messages.get().gen_noRecordsFound());
                    }

                    public void failure(Throwable e) {
                        fetchById(null);
                        Window.alert(Messages.get().gen_fetchFailed() + e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                    }

                    public void finish() {
                        fireDataChange();
                        clearStatus();
                    }
                };
            }

            service.fetchById(id, elements, fetchByIdCall);

        }
    }
}