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

import static org.openelis.modules.main.client.Logger.logger;
import static org.openelis.ui.screen.Screen.ShortKeys.CTRL;
import static org.openelis.ui.screen.Screen.Validation.Status.VALID;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;

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
import org.openelis.manager.OrganizationContactManager;
import org.openelis.manager.OrganizationManager;
import org.openelis.manager.OrganizationParameterManager;
import org.openelis.meta.OrganizationMeta;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.main.client.OpenELIS;
import org.openelis.modules.note.client.NotesTabUI;
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
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.ScreenNavigator;
import org.openelis.ui.widget.AtoZButtons;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.MenuItem;
import org.openelis.ui.widget.NotesPanel;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.TabLayoutPanel;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.table.Table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class OrganizationScreenUI extends Screen {
    
    @UiTemplate("Organization.ui.xml")
    interface OrganizationUiBinder extends UiBinder<Widget, OrganizationScreenUI> {
    };

    private static OrganizationUiBinder uiBinder = GWT.create(OrganizationUiBinder.class);

    protected OrganizationManager       manager;
    protected ModulePermission          userPermission;

    @UiField
    protected AtoZButtons               atozButtons;

    protected ScreenNavigator<IdNameVO> nav;

    @UiField(provided=true)
    protected ContactTabUI              contactTab;
    @UiField(provided=true)
    protected ParameterTabUI            parameterTab;

    protected NotesTabUI                notesTab;

    @UiField
    protected NotesPanel                notesPanel;

    @UiField
    protected Button                    query, previous, next, add, standardNote, update, commit,
                                        abort, atozNext, atozPrev;
    @UiField
    protected MenuItem                  orgHistory, orgAddressHistory, orgContactHistory,
                                        orgContactAddressHistory, orgParameterHistory;

    @UiField
    protected TextBox<Integer>          id;
    @UiField
    protected TextBox<String>           name, multipleUnit, city, zipCode, streetAddress;
    @UiField
    protected CheckBox                  isActive;
    @UiField
    protected Dropdown<String>          state, country;
    @UiField
    protected AutoComplete              parentName;

    @UiField
    protected TabLayoutPanel            tabPanel;

    @UiField
    protected Table                     atozTable;

    private enum Tabs {
        CONTACT, PARAMETER, NOTE
    };

    private Tabs tab;

    public OrganizationScreenUI(WindowInt window) throws Exception {

        setWindow(window);

        userPermission = UserCache.getPermission().getModule("organization");
        if (userPermission == null)
            throw new PermissionException(Messages.get().screenPermException("Oranization Screen"));

        contactTab = new ContactTabUI(this);
        parameterTab = new ParameterTabUI(this);
        
        initWidget(uiBinder.createAndBindUi(this));

        tab = Tabs.CONTACT;
        manager = OrganizationManager.getInstance();

        try {
            CategoryCache.getBySystemNames("country", "state", "contact_type", "parameter_type");
        } catch (Exception e) {
            Window.alert("OrganizationScreen: missing dictionary entry; " + e.getMessage());
            window.close();
        }

        initialize();
        setState(DEFAULT);
        initializeDropdowns();
        fireDataChange();
        
        logger.fine("Organization Screen Opened");
        
        
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        //
        // button panel buttons
        //
        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                query.setEnabled(isState(QUERY,DEFAULT, DISPLAY) && userPermission.hasSelectPermission());
                if (isState(QUERY)){ 
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
                add.setEnabled(isState(ADD,DEFAULT, DISPLAY) && userPermission.hasAddPermission());
                if (isState(ADD)) {
                    add.lock();
                    add.setPressed(true);
                }
            }
        });

        addShortcut(add, 'a', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                update.setEnabled(isState(UPDATE,DISPLAY) && userPermission.hasUpdatePermission());
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

        //
        // screen fields
        //
        addScreenHandler(id, OrganizationMeta.getId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                id.setValue(manager.getOrganization().getId());
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
                name.setValue(manager.getOrganization().getName());
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
                city.setValue(manager.getOrganization().getAddress().getCity());
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
                                 multipleUnit.setValue(manager.getOrganization()
                                                              .getAddress()
                                                              .getMultipleUnit());
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
                state.setValue(manager.getOrganization().getAddress().getState());
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
                                 zipCode.setValue(manager.getOrganization()
                                                         .getAddress()
                                                         .getZipCode());
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
                                 streetAddress.setValue(manager.getOrganization()
                                                               .getAddress()
                                                               .getStreetAddress());
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
                                 country.setValue(manager.getOrganization()
                                                         .getAddress()
                                                         .getCountry());
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
                                 parentName.setValue(manager.getOrganization()
                                                            .getParentOrganizationId(),
                                                     manager.getOrganization()
                                                            .getParentOrganizationName());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 manager.getOrganization()
                                        .setParentOrganizationId(event.getValue());
                                 manager.getOrganization()
                                        .setParentOrganizationName(parentName.getDisplay());
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

                window.setBusy();
                try {
                    list = OrganizationService.get()
                                              .fetchByIdOrName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
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
                window.clearStatus();
            }
        });

        addScreenHandler(isActive, OrganizationMeta.getIsActive(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isActive.setValue(manager.getOrganization().getIsActive());
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

        //
        // tabs
        //
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

        tabPanel.setPopoutBrowser(OpenELIS.getBrowser());
        
        notesTab = new NotesTabUI(window, notesPanel, standardNote);
        addDataChangeHandler(new DataChangeEvent.Handler() {
            public void onDataChange(DataChangeEvent event) {
                notesTab.setManager(manager);
                if (tab == Tabs.NOTE)
                    drawTabs();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                notesTab.setState(event.getState());
            }
        });

        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator<IdNameVO>(atozTable, atozNext, atozPrev) {
            public void executeQuery(final Query query) {
                window.setBusy(Messages.get().querying());

                query.setRowsPerPage(20);
                OrganizationService.get().query(query, new AsyncCallback<ArrayList<IdNameVO>>() {
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
                            Window.alert("Error: Organization call query failed; " +
                                         error.getMessage());
                            window.setError(Messages.get().queryFailed());
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
                field.setKey(OrganizationMeta.getName());
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
        
        window.addCloseHandler(new CloseHandler<WindowInt>() {
            
            @Override
            public void onClose(CloseEvent<WindowInt> event) {
                tabPanel.close();
            }
        });
    }

    private void initializeDropdowns() {
        ArrayList<Item<String>> model;
        ArrayList<DictionaryDO> list;
        Item<String> row;

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
    @UiHandler("query")
    protected void query(ClickEvent event) {
        manager = OrganizationManager.getInstance();

        setState(QUERY);
        fireDataChange();

        notesTab.draw();

        id.setFocus(true);
        window.setDone(Messages.get().enterFieldsToQuery());
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
        manager = OrganizationManager.getInstance();
        manager.getOrganization().setIsActive("Y");

        setState(ADD);
        fireDataChange();

        name.setFocus(true);
        window.setDone(Messages.get().enterInformationPressCommit());
    }

    @UiHandler("update")
    protected void update(ClickEvent event) {
        window.setBusy(Messages.get().lockForUpdate());

        try {
            manager = manager.fetchForUpdate();

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

        switch (super.state) {
            case QUERY:
                Query query;

                query = new Query();
                query.setFields(getQueryFields());
                nav.setQuery(query);
                break;
            case ADD:
                window.setBusy(Messages.get().adding());
                try {
                    manager = manager.add();

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
                    manager = manager.update();

                    setState(DISPLAY);
                    fireDataChange();
                    window.setDone(Messages.get().updatingComplete());
                } catch (ValidationErrorsList e) {
                    showErrors(e);
                } catch (Exception e) {
                    Window.alert("commitUpdate(): " + e.getMessage());
                    window.clearStatus();
                }
                break;
            default:
                break;
        }
    }

    @UiHandler("abort")
    protected void abort(ClickEvent event) {
        finishEditing();
        clearErrors();
        window.setBusy(Messages.get().cancelChanges());

        switch (super.state) {
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
                    manager = manager.abortUpdate();
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

        HistoryScreen.showHistory(Messages.get().orgContactHistory(),
                                  Constants.table().ORGANIZATION_CONTACT,
                                  refVoList);
    }

    protected void orgContactAddressHistory() {
        int i, count;
        IdNameVO refVoList[];
        OrganizationContactManager man;
        AddressDO addr;

        try {
            man = manager.getContacts();
            count = man.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                addr = man.getContactAt(i).getAddress();
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

        HistoryScreen.showHistory(Messages.get().orgParameterHistory(),
                                  Constants.table().ORGANIZATION_PARAMETER,
                                  refVoList);
    }

    protected boolean fetchById(Integer id) {
        if (id == null) {
            manager = OrganizationManager.getInstance();
            setState(DEFAULT);
        } else {
            window.setBusy(Messages.get().fetching());
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

    private void drawTabs() {
        switch (tab) {
            case NOTE:
                notesTab.draw();
                break;
        }
    }
}