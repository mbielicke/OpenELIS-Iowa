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
package org.openelis.modules.provider.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdFirstLastNameVO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ProviderDO;
import org.openelis.domain.ProviderLocationDO;
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
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.manager.ProviderLocationManager;
import org.openelis.manager.ProviderManager;
import org.openelis.meta.ProviderMeta;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.note.client.NotesTab;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TabPanel;

public class ProviderScreen extends Screen {
    private ProviderManager   manager;
    private ModulePermission  userPermission;

    private Tabs              tab;
    private LocationTab       locationTab;
    private NotesTab          notesTab;

    private TextBox           lastName, firstName, npi, middleName;
    private TextBox<Integer>  id;
    private AppButton         queryButton, previousButton, nextButton, addButton, updateButton,
                              commitButton, abortButton;
    protected MenuItem        providerHistory, providerLocationHistory;
    private Dropdown<Integer> typeId;
    private TabPanel          tabPanel;

    private ButtonGroup       atoz;
    private ScreenNavigator   nav;

    private enum Tabs {
        LOCATION, NOTE
    };

    public ProviderScreen(WindowInt window) throws Exception {
        super((ScreenDefInt)GWT.create(ProviderDef.class));
        
        setWindow(window);

        userPermission = UserCache.getPermission().getModule("provider");
        if (userPermission == null)
            throw new PermissionException(Messages.get().screenPermException("Provider Screen"));

        tab = Tabs.LOCATION;
        manager = ProviderManager.getInstance();

        initialize();
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
        
        providerHistory = (MenuItem)def.getWidget("providerHistory");
        addScreenHandler(providerHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                providerHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                providerHistory.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        providerLocationHistory = (MenuItem)def.getWidget("providerLocationHistory");
        addScreenHandler(providerLocationHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                providerLocationHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                providerLocationHistory.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        id = (TextBox<Integer>)def.getWidget(ProviderMeta.getId());
        addScreenHandler(id, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                id.setFieldValue(manager.getProvider().getId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getProvider().setId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                id.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                id.setQueryMode(event.getState() == State.QUERY);
            }
        });

        lastName = (TextBox)def.getWidget(ProviderMeta.getLastName());
        addScreenHandler(lastName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                lastName.setValue(manager.getProvider().getLastName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getProvider().setLastName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                lastName.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                       .contains(event.getState()));
                lastName.setQueryMode(event.getState() == State.QUERY);
            }
        });

        typeId = (Dropdown<Integer>)def.getWidget(ProviderMeta.getTypeId());
        addScreenHandler(typeId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                typeId.setSelection(manager.getProvider().getTypeId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getProvider().setTypeId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                typeId.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                     .contains(event.getState()));
                typeId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        firstName = (TextBox)def.getWidget(ProviderMeta.getFirstName());
        addScreenHandler(firstName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                firstName.setValue(manager.getProvider().getFirstName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getProvider().setFirstName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                firstName.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                        .contains(event.getState()));
                firstName.setQueryMode(event.getState() == State.QUERY);
            }
        });

        npi = (TextBox)def.getWidget(ProviderMeta.getNpi());
        addScreenHandler(npi, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                npi.setValue(manager.getProvider().getNpi());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getProvider().setNpi(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                npi.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                  .contains(event.getState()));
                npi.setQueryMode(event.getState() == State.QUERY);
            }
        });

        middleName = (TextBox)def.getWidget(ProviderMeta.getMiddleName());
        addScreenHandler(middleName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                middleName.setValue(manager.getProvider().getMiddleName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getProvider().setMiddleName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                middleName.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                         .contains(event.getState()));
                middleName.setQueryMode(event.getState() == State.QUERY);
            }
        });

        tabPanel = (TabPanel)def.getWidget("tabPanel");
        tabPanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                int i;

                i = event.getItem().intValue();
                tab = Tabs.values()[i];

                window.setBusy();
                drawTabs();
                window.clearStatus();
            }
        });

        locationTab = new LocationTab(def, window);
        addScreenHandler(locationTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                locationTab.setManager(manager);
                if (tab == Tabs.LOCATION)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                locationTab.setState(event.getState());
            }
        });

        notesTab = new NotesTab(def, window, "notesPanel", "standardNoteButton");
        addScreenHandler(notesTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                notesTab.setManager(manager);
                if (tab == Tabs.NOTE)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                notesTab.setState(event.getState());
            }
        });

        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator<IdFirstLastNameVO>(def) {
            public void executeQuery(final Query query) {
                window.setBusy(Messages.get().querying());

                query.setRowsPerPage(18);
                ProviderServiceImpl.INSTANCE.query(query, new AsyncCallback<ArrayList<IdFirstLastNameVO>>() {
                     public void onSuccess(ArrayList<IdFirstLastNameVO> result) {
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
                             Window.alert("Error: Provider call query failed; " +
                                          error.getMessage());
                             window.setError(Messages.get().queryFailed());
                         }
                     }
                 });
            }

            public boolean fetch(IdFirstLastNameVO entry) {
                return fetchById( (entry == null) ? null : entry.getId());
            }

            public ArrayList<TableDataRow> getModel() {
                ArrayList<IdFirstLastNameVO> result;
                ArrayList<TableDataRow> model;

                model = null;
                result = nav.getQueryResult();
                if (result != null) {
                    model = new ArrayList<TableDataRow>();
                    for (IdFirstLastNameVO entry : result)
                        model.add(new TableDataRow(entry.getId(), entry.getLastName(),
                                                   entry.getFirstName()));
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
                field.setKey(ProviderMeta.getLastName());
                field.setQuery(((AppButton)event.getSource()).getAction());
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

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        ArrayList<DictionaryDO> list;
        TableDataRow row;

        // typeId dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list =  CategoryCache.getBySystemName("provider_type");
        for (DictionaryDO d : list) {
            row = new TableDataRow(d.getId(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row); 
        }
        typeId.setModel(model);
    }

    protected void query() {
        manager = ProviderManager.getInstance();

        setState(State.QUERY);
        DataChangeEvent.fire(this);

        // clear all the tabs
        locationTab.draw();
        notesTab.draw();

        setFocus(id);
        window.setDone(Messages.get().enterFieldsToQuery());
    }

    protected void previous() {
        nav.previous();
    }

    protected void next() {
        nav.next();
    }

    protected void add() {
        manager = ProviderManager.getInstance();

        setState(State.ADD);
        DataChangeEvent.fire(this);

        setFocus(lastName);
        window.setDone(Messages.get().enterInformationPressCommit());
    }

    protected void update() {
        window.setBusy(Messages.get().lockForUpdate());

        try {
            manager = manager.fetchForUpdate();

            setState(State.UPDATE);
            DataChangeEvent.fire(this);
            setFocus(lastName);
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
        window.clearStatus();
    }

    public void commit() {
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
                manager = manager.add();

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
                manager = manager.update();

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
                manager = manager.abortUpdate();
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
    
    protected void providerHistory() {
        IdNameVO hist;
        String name;
        ProviderDO data;
        
        data = manager.getProvider();
        
        if(data.getFirstName() != null)
            name = data.getLastName()+", "+data.getFirstName();
        else 
            name = data.getLastName();
        
        hist = new IdNameVO(data.getId(), name);
        HistoryScreen.showHistory(Messages.get().providerHistory(),
                                  Constants.table().PROVIDER, hist); 
    }
    
    protected void providerLocationHistory() {
        int i, count;
        IdNameVO refVoList[];
        ProviderLocationManager man;
        ProviderLocationDO data;

        try {
            man = manager.getLocations();
            count = man.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++) {
                data = man.getLocationAt(i);                                
                refVoList[i] = new IdNameVO(data.getId(), data.getLocation());                
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(Messages.get().providerLocationHistory(),
                                  Constants.table().PROVIDER_LOCATION, refVoList);
    }

    protected boolean fetchById(Integer id) {
        if (id == null) {
            manager = ProviderManager.getInstance();
            setState(State.DEFAULT);
        } else {
            window.setBusy(Messages.get().fetching());
            try {
                switch (tab) {
                    case LOCATION:
                        manager = ProviderManager.fetchWithLocations(id);
                        break;
                    case NOTE:
                        manager = ProviderManager.fetchWithNotes(id);
                        break;
                }
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

    private void drawTabs() {
        switch (tab) {
            case LOCATION:
                locationTab.draw();
                break;
            case NOTE:
                notesTab.draw();
                break;
        }
    }
}
