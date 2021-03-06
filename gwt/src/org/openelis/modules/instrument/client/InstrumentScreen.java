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

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.InstrumentLogDO;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.widget.WindowInt;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.GetMatchesEvent;
import org.openelis.gwt.event.GetMatchesHandler;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataCell;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableWidget;
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
import org.openelis.modules.scriptlet.client.ScriptletService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class InstrumentScreen extends Screen {
    
    private InstrumentManager           manager;
    private ModulePermission            userPermission;
    
    private CalendarLookUp              activeBegin, activeEnd;
    private TextBox                     name, description, modelNumber, serialNumber, location;
    private CheckBox                    isActive;
    private AppButton                   queryButton, previousButton, nextButton, addButton, updateButton,
                                        commitButton, abortButton, addLogButton, removeLogButton;
    protected MenuItem                  instrumentHistory, instrumentLogHistory;
    private ButtonGroup                 atoz;
    private ScreenNavigator             nav;
    private Dropdown<Integer>           typeId;
    private TableWidget                 logTable;
    private AutoComplete<Integer>       scriptlet;
    
    public InstrumentScreen(WindowInt window) throws Exception {
        super((ScreenDefInt)GWT.create(InstrumentDef.class));
        
        setWindow(window);
        
        userPermission = UserCache.getPermission().getModule("instrument");
        if (userPermission == null)
            throw new PermissionException(Messages.get().screenPermException("Instrument Screen"));

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
        queryButton = (AppButton)def.getWidget("query");
        addScreenHandler(queryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                query();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                queryButton.enable(EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState())
                                     && userPermission.hasSelectPermission());
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
                addButton.enable(EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState())
                                     && userPermission.hasAddPermission());
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
                updateButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState())
                                     && userPermission.hasUpdatePermission());
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
                commitButton.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
            }
        });

        abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
            }
        });
        
        instrumentHistory = (MenuItem)def.getWidget("instrumentHistory");
        addScreenHandler(instrumentHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                instrumentHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                instrumentHistory.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        instrumentLogHistory = (MenuItem)def.getWidget("instrumentLogHistory");
        addScreenHandler(instrumentLogHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                instrumentLogHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                instrumentLogHistory.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
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
                name.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
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
                description.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
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
                modelNumber.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
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
                serialNumber.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                serialNumber.setQueryMode(event.getState() == State.QUERY);
            }
        });

        typeId = (Dropdown)def.getWidget(InstrumentMeta.getTypeId());
        addScreenHandler(typeId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                typeId.setSelection(manager.getInstrument().getTypeId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getInstrument().setTypeId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                typeId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
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
                isActive.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
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
                location.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                location.setQueryMode(event.getState() == State.QUERY);
            }
        });

        activeBegin = (CalendarLookUp)def.getWidget(InstrumentMeta.getActiveBegin());
        addScreenHandler(activeBegin, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                activeBegin.setValue(manager.getInstrument().getActiveBegin());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getInstrument().setActiveBegin(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                activeBegin.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                activeBegin.setQueryMode(event.getState() == State.QUERY);
            }
        });

        scriptlet = (AutoComplete)def.getWidget(InstrumentMeta.getScriptletName());
        addScreenHandler(scriptlet, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                scriptlet.setSelection(manager.getInstrument().getScriptletId(),
                                  manager.getInstrument().getScriptletName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getInstrument().setScriptletId(event.getValue());
                manager.getInstrument().setScriptletName(scriptlet.getTextBoxDisplay());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                scriptlet.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                scriptlet.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        scriptlet.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<TableDataRow> model;
                ArrayList<IdNameVO> list;

                try {
                    list = ScriptletService.get().fetchByName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<TableDataRow>();
                    for (IdNameVO data : list) {                       
                        model.add(new TableDataRow(data.getId(),data.getName()));
                    }
                    scriptlet.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        activeEnd = (CalendarLookUp)def.getWidget(InstrumentMeta.getActiveEnd());
        addScreenHandler(activeEnd, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                activeEnd.setValue(manager.getInstrument().getActiveEnd());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getInstrument().setActiveEnd(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                activeEnd.enable(true);
                activeEnd.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        logTable = (TableWidget)def.getWidget("logTable");
        addScreenHandler(logTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                //
                //this table is not queried by,so it needs to be cleared in query mode
                //
                logTable.load(getLogTableModel()); 
            }

            public void onStateChange(StateChangeEvent<State> event) {
                logTable.enable(true);               
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
                TableDataCell cell;

                r = event.getRow();
                c = event.getCol();
                
                cell = logTable.getCell(r, c);
                
                val = logTable.getObject(r,c);
                try {
                    data = manager.getLogs().getLogAt(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }                
                switch(c) {
                    case 0:
                        data.setTypeId((Integer)val);
                        break;
                    case 1:                        
                        if(cell.getExceptions() != null)
                            data.setWorksheetId(null);
                        else
                            data.setWorksheetId((Integer)val);
                        break;
                    case 2:
                        if(cell.getExceptions() != null)
                            data.setEventBegin(null);
                        else 
                            data.setEventBegin((Datetime)val);
                        break;
                    case 3:
                        if(cell.getExceptions() != null)
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
                   Window.alert(e.getMessage());
               }
            }
        });

        logTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getLogs().removeLogAt(event.getIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        addLogButton = (AppButton)def.getWidget("addLogButton");
        addScreenHandler(addLogButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n;
                
                logTable.addRow();
                n = logTable.numRows() - 1;
                logTable.selectRow(n);
                logTable.scrollToSelection();
                logTable.startEditing(n, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addLogButton.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });
        
        removeLogButton = (AppButton)def.getWidget("removeLogButton");
        addScreenHandler(removeLogButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;
                
                r = logTable.getSelectedRow();
                if (r > -1 && logTable.numRows() > 0)
                    logTable.deleteRow(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeLogButton.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });
        
        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator<IdNameVO>(def) {
            public void executeQuery(final Query query) {
                window.setBusy(Messages.get().querying());

                query.setRowsPerPage(20);
                InstrumentService.get().query(query, new AsyncCallback<ArrayList<IdNameVO>>() {
                    public void onSuccess(ArrayList<IdNameVO> result) {
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
                            Window.alert("Error: Instrument call query failed; " + error.getMessage());
                            window.setError(Messages.get().queryFailed());
                        }
                    }
                });
            }

            public boolean fetch(IdNameVO entry) {
                return fetchById( (entry == null) ? null : entry.getId());
            }

            public ArrayList<TableDataRow> getModel() {
                ArrayList<IdNameVO> result;
                ArrayList<TableDataRow> model;

                model = null;
                result = nav.getQueryResult();
                if (result != null) {
                    model = new ArrayList<TableDataRow>();
                    for (IdNameVO entry : result)
                        model.add(new TableDataRow(entry.getId(), entry.getName(),
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
                atoz.enable(enable);
                nav.enable(enable);
            }

            public void onClick(ClickEvent event) {
                Query query;
                QueryData field;

                field = new QueryData();
                field.setKey(InstrumentMeta.getName());
                field.setQuery(((AppButton)event.getSource()).getAction());
                field.setType(QueryData.Type.STRING);

                query = new Query();
                query.setFields(field);
                nav.setQuery(query);
            }
        });
        
        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {                
                if (EnumSet.of(State.ADD, State.UPDATE, State.DELETE).contains(state)) {
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

        // type dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        list = CategoryCache.getBySystemName("instrument_type");
        for (DictionaryDO d : list) {         
            row = new TableDataRow(d.getId(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);
        }

        typeId.setModel(model);

        // log type dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, "")); 
        list = CategoryCache.getBySystemName("instrument_log_type");
        for (DictionaryDO d : list) {
            row = new TableDataRow(d.getId(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);
        }

        ((Dropdown<Integer>)(logTable.getColumnWidget(InstrumentMeta.getLogTypeId()))).setModel(model);        
    }
    
    /*
     * basic button methods
     */
    protected void query() {
        manager = InstrumentManager.getInstance();

        setState(State.QUERY);
        DataChangeEvent.fire(this);
        
        setFocus(name);
        window.setDone(Messages.get().enterFieldsToQuery());
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
        window.setDone(Messages.get().enterInformationPressCommit());
    }

    protected void update() {
        window.setBusy(Messages.get().lockForUpdate());

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
    
    protected void instrumentHistory() {
        IdNameVO hist;
        
        hist = new IdNameVO(manager.getInstrument().getId(), manager.getInstrument().getName());
        HistoryScreen.showHistory(Messages.get().instrumentHistory(),
                                  Constants.table().INSTRUMENT, hist); 
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
                dict = DictionaryCache.getById(typeId);
                if(dict != null)
                    entry = dict.getEntry();
                else
                    entry = typeId.toString();
                
                refVoList[i] = new IdNameVO(data.getId(), entry);                
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(Messages.get().instrumentLogHistory(),
                                  Constants.table().INSTRUMENT_LOG, refVoList);
    }


    protected boolean fetchById(Integer id) {
        if (id == null) {
            manager = InstrumentManager.getInstance();
            setState(State.DEFAULT);
        } else {
            window.setBusy(Messages.get().fetching());
            try {
                manager = InstrumentManager.fetchWithLogs(id);
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
    
    private ArrayList<TableDataRow> getLogTableModel() {
        int i;
        InstrumentLogDO data;
        ArrayList<TableDataRow> model;
        TableDataRow row;
        
        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;
        
        try {
            for (i = 0; i < manager.getLogs().count(); i++ ) {
                data = manager.getLogs().getLogAt(i);
                row = new TableDataRow(5);
                
                row.key = data.getId();
                row.cells.get(0).setValue(data.getTypeId());
                row.cells.get(1).setValue(data.getWorksheetId());
                row.cells.get(2).setValue(data.getEventBegin());
                row.cells.get(3).setValue(data.getEventEnd());
                row.cells.get(4).setValue(data.getText());
          
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }        
        return model;
    }
    
}
