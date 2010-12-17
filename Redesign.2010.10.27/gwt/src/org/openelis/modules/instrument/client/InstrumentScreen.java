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
package org.openelis.modules.instrument.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.DictionaryCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.InstrumentLogDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.gwt.common.Datetime;
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
import org.openelis.gwt.widget.calendar.Calendar;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Item;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.Window;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.Row;
import org.openelis.gwt.widget.table.Table;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.manager.InstrumentLogManager;
import org.openelis.manager.InstrumentManager;
import org.openelis.meta.InstrumentMeta;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class InstrumentScreen extends Screen {
    
    private InstrumentManager           manager;
    private ModulePermission            userPermission;
    
    private Calendar                    activeBegin, activeEnd;
    private TextBox                     name, description, modelNumber, serialNumber, location;
    private CheckBox                    isActive;
    private Button                      queryButton, previousButton, nextButton, addButton, updateButton,
                                        commitButton, abortButton, addLogButton, removeLogButton;
    protected MenuItem                  instrumentHistory, instrumentLogHistory;
    private ButtonGroup                 atoz;
    private ScreenNavigator             nav;
    private Dropdown<Integer>           typeId;
    private Table                       logTable;
    private AutoComplete                scriptlet;
    private ScreenService               scriptletService;    
    
    public InstrumentScreen() throws Exception {
        super((ScreenDefInt)GWT.create(InstrumentDef.class));
        
        service = new ScreenService("controller?service=org.openelis.modules.instrument.server.InstrumentService");
        scriptletService = new ScreenService("controller?service=org.openelis.modules.scriptlet.server.ScriptletService");

        userPermission = OpenELIS.getSystemUserPermission().getModule("instrument");
        if (userPermission == null)
            throw new PermissionException("screenPermException", "Instrument Screen");

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
        manager = InstrumentManager.getInstance();

        initialize();
        setState(State.DEFAULT);
        initializeDropdowns();
        DataChangeEvent.fire(this);    
    }   

    /**
     * Setup state and data change handles for every widget on the screen
     */
    @SuppressWarnings("unchecked")
    private void initialize() {        
        queryButton = (Button)def.getWidget("query");
        addScreenHandler(queryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                query();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                queryButton.setEnabled(EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState())
                                     && userPermission.hasSelectPermission());
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
                addButton.setEnabled(EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState())
                                     && userPermission.hasAddPermission());
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
                updateButton.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState())
                                     && userPermission.hasUpdatePermission());
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
                commitButton.setEnabled(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
            }
        });

        abortButton = (Button)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.setEnabled(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
            }
        });
        
        instrumentHistory = (MenuItem)def.getWidget("instrumentHistory");
        addScreenHandler(instrumentHistory, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                instrumentHistory.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        instrumentHistory.addCommand(new Command() {
			public void execute() {
				instrumentHistory();
			}
		});
        
        instrumentLogHistory = (MenuItem)def.getWidget("instrumentLogHistory");
        addScreenHandler(instrumentLogHistory, new ScreenEventHandler<Object>() {
            public void onStateChange(StateChangeEvent<State> event) {
                instrumentLogHistory.setEnabled(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        instrumentLogHistory.addCommand(new Command() {
			public void execute() {
				instrumentLogHistory();
			}
		});

        name = (TextBox)def.getWidget(InstrumentMeta.getName());
        addScreenHandler(name, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                name.setValue(manager.getInstrument().getName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInstrument().setName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                name.setEnabled(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                name.setQueryMode(event.getState() == State.QUERY);
            }
        });

        description = (TextBox)def.getWidget(InstrumentMeta.getDescription());
        addScreenHandler(description, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                description.setValue(manager.getInstrument().getDescription());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInstrument().setDescription(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                description.setEnabled(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                description.setQueryMode(event.getState() == State.QUERY);
            }
        });

        modelNumber = (TextBox)def.getWidget(InstrumentMeta.getModelNumber());
        addScreenHandler(modelNumber, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                modelNumber.setValue(manager.getInstrument().getModelNumber());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInstrument().setModelNumber(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                modelNumber.setEnabled(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                modelNumber.setQueryMode(event.getState() == State.QUERY);
            }
        });

        serialNumber = (TextBox)def.getWidget(InstrumentMeta.getSerialNumber());
        addScreenHandler(serialNumber, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                serialNumber.setValue(manager.getInstrument().getSerialNumber());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInstrument().setSerialNumber(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                serialNumber.setEnabled(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                serialNumber.setQueryMode(event.getState() == State.QUERY);
            }
        });

        typeId = (Dropdown)def.getWidget(InstrumentMeta.getTypeId());
        addScreenHandler(typeId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                typeId.setValue(manager.getInstrument().getTypeId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getInstrument().setTypeId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                typeId.setEnabled(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                typeId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        isActive = (CheckBox)def.getWidget(InstrumentMeta.getIsActive());
        addScreenHandler(isActive, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isActive.setValue(manager.getInstrument().getIsActive());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInstrument().setIsActive(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isActive.setEnabled(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                isActive.setQueryMode(event.getState() == State.QUERY);
            }
        });

        location = (TextBox)def.getWidget(InstrumentMeta.getLocation());
        addScreenHandler(location, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                location.setValue(manager.getInstrument().getLocation());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getInstrument().setLocation(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                location.setEnabled(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                location.setQueryMode(event.getState() == State.QUERY);
            }
        });

        activeBegin = (Calendar)def.getWidget(InstrumentMeta.getActiveBegin());
        addScreenHandler(activeBegin, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                activeBegin.setValue(manager.getInstrument().getActiveBegin());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getInstrument().setActiveBegin(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                activeBegin.setEnabled(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                activeBegin.setQueryMode(event.getState() == State.QUERY);
            }
        });

        scriptlet = (AutoComplete)def.getWidget(InstrumentMeta.getScriptletName());
        addScreenHandler(scriptlet, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                scriptlet.setValue(manager.getInstrument().getScriptletId(),
                                  manager.getInstrument().getScriptletName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getInstrument().setScriptletId(event.getValue());
                manager.getInstrument().setScriptletName(scriptlet.getDisplay());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                scriptlet.setEnabled(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                scriptlet.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        scriptlet.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                QueryFieldUtil parser;
                ArrayList<Item<Integer>> model;
                ArrayList<IdNameVO> list;

                parser = new QueryFieldUtil();
                try {
                	parser.parse(!event.getMatch().equals("") ? event.getMatch() : "*");
                }catch(Exception e) {
                	
                }
                
                try {
                    list = scriptletService.callList("fetchByName", parser.getParameter().get(0));
                    model = new ArrayList<Item<Integer>>();
                    for (IdNameVO data : list) {                       
                        model.add(new Item<Integer>(data.getId(),data.getName()));
                    }
                    scriptlet.showAutoMatches(model);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });

        activeEnd = (Calendar)def.getWidget(InstrumentMeta.getActiveEnd());
        addScreenHandler(activeEnd, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                activeEnd.setValue(manager.getInstrument().getActiveEnd());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getInstrument().setActiveEnd(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                activeEnd.setEnabled(true);
                activeEnd.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        logTable = (Table)def.getWidget("logTable");
        addScreenHandler(logTable, new ScreenEventHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                //
                //this table is not queried by,so it needs to be cleared in query mode
                //
                logTable.setModel(getLogTableModel()); 
            }

            public void onStateChange(StateChangeEvent<State> event) {
                logTable.setEnabled(true);               
            }
        });        
        
        logTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if(state != State.ADD && state != State.UPDATE)  
                    event.cancel();                
            }            
        });

        logTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                InstrumentLogDO data;

                r = event.getRow();
                c = event.getCol();
                                
                val = logTable.getValueAt(r,c);
                try {
                    data = manager.getLogs().getLogAt(r);
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                    return;
                }                
                switch(c) {
                    case 0:
                        data.setTypeId((Integer)val);
                        break;
                    case 1:                        
                        if(logTable.hasExceptions(r, c))
                            data.setWorksheetId(null);
                        else
                            data.setWorksheetId((Integer)val);
                        break;
                    case 2:
                        if(logTable.hasExceptions(r, c))
                            data.setEventBegin(null);
                        else 
                            data.setEventBegin((Datetime)val);
                        break;
                    case 3:
                        if(logTable.hasExceptions(r,c))
                            data.setEventEnd(null);
                        else 
                            data.setEventEnd((Datetime)val);
                        break;
                    case 4:
                        data.setText((String)val);
                        break;
                }
            }
        });

        logTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
               try {
                   manager.getLogs().addLogAt(new InstrumentLogDO(), event.getIndex());
               } catch (Exception e) {
                   com.google.gwt.user.client.Window.alert(e.getMessage());
               }
            }
        });

        logTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getLogs().removeLogAt(event.getIndex());
                } catch (Exception e) {
                    com.google.gwt.user.client.Window.alert(e.getMessage());
                }
            }
        });

        addLogButton = (Button)def.getWidget("addLogButton");
        addScreenHandler(addLogButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n;
                
                logTable.addRow();
                n = logTable.getRowCount() - 1;
                logTable.selectRowAt(n);
                logTable.scrollToVisible(n);
                logTable.startEditing(n, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addLogButton.setEnabled(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });
        
        removeLogButton = (Button)def.getWidget("removeLogButton");
        addScreenHandler(removeLogButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;
                
                r = logTable.getSelectedRow();
                if (r > -1 && logTable.getRowCount() > 0)
                    logTable.removeRowAt(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeLogButton.setEnabled(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
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
                            com.google.gwt.user.client.Window.alert("Error: Instrument call query failed; " + error.getMessage());
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
                        model.add(new Item<Integer>(entry.getId(), entry.getName(),
                                                   entry.getDescription()));
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

                field = new QueryData(InstrumentMeta.getName(),QueryData.Type.STRING,((Button)event.getSource()).getAction());

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
    
    private void initializeDropdowns() {
        ArrayList<Item<Integer>> model;
        ArrayList<DictionaryDO> list;
        Item<Integer> row;

        // type dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        list = DictionaryCache.getListByCategorySystemName("instrument_type");
        for (DictionaryDO d : list) {         
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        typeId.setModel(model);

        // log type dropdown
        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, "")); 
        list = DictionaryCache.getListByCategorySystemName("instrument_log_type");
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        ((Dropdown<Integer>)(logTable.getColumnAt(logTable.getColumnByName(InstrumentMeta.getLogTypeId())).getCellEditor().getWidget())).setModel(model);        
    }
    
    /*
     * basic button methods
     */
    protected void query() {
        manager = InstrumentManager.getInstance();

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
        manager = InstrumentManager.getInstance();
        manager.getInstrument().setIsActive("N");
        
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
            com.google.gwt.user.client.Window.alert(e.getMessage());
        }
        window.clearStatus();
    }

    public void commit() {
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
                com.google.gwt.user.client.Window.alert("commitAdd(): " + e.getMessage());
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
                manager = manager.abortUpdate();
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
    
    protected void instrumentHistory() {
        IdNameVO hist;
        
        hist = new IdNameVO(manager.getInstrument().getId(), manager.getInstrument().getName());
        HistoryScreen.showHistory(consts.get("instrumentHistory"),
                                  ReferenceTable.INSTRUMENT, hist); 
    }
    
    protected void instrumentLogHistory() {
        int i, count;
        IdNameVO refVoList[];
        InstrumentLogManager man;
        InstrumentLogDO data;
        DictionaryDO dict;
        String entry;
        Integer typeId;

        try {
            man = manager.getLogs();
            count = man.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = man.getLogAt(i);
                typeId = data.getTypeId();
                dict = DictionaryCache.getEntryFromId(typeId);
                if(dict != null)
                    entry = dict.getEntry();
                else
                    entry = typeId.toString();
                
                refVoList[i] = new IdNameVO(data.getId(), entry);                
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.google.gwt.user.client.Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(consts.get("instrumentLogHistory"),
                                  ReferenceTable.INSTRUMENT_LOG, refVoList);
    }


    protected boolean fetchById(Integer id) {
        if (id == null) {
            manager = InstrumentManager.getInstance();
            setState(State.DEFAULT);
        } else {
            window.setBusy(consts.get("fetching"));
            try {
                manager = InstrumentManager.fetchWithLogs(id);
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
    
    private ArrayList<Row> getLogTableModel() {
        int i;
        InstrumentLogDO data;
        ArrayList<Row> model;
        Row row;
        
        model = new ArrayList<Row>();
        if (manager == null)
            return model;
        
        try {
            for (i = 0; i < manager.getLogs().count(); i++ ) {
                data = manager.getLogs().getLogAt(i);
                row = new Row(5);
                
                //row.key = data.getId();
                row.setCell(0,data.getTypeId());
                row.setCell(1,data.getWorksheetId());
                row.setCell(2,data.getEventBegin());
                row.setCell(3,data.getEventEnd());
                row.setCell(4,data.getText());
          
                model.add(row);
            }
        } catch (Exception e) {
            com.google.gwt.user.client.Window.alert(e.getMessage());
            e.printStackTrace();
        }        
        return model;
    }
    
}
