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
package org.openelis.modules.dictionary.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.SectionCache;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SectionDO;
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
import org.openelis.gwt.widget.table.event.RowMovedEvent;
import org.openelis.gwt.widget.table.event.RowMovedHandler;
import org.openelis.manager.DictionaryManager;
import org.openelis.metamap.CategoryMetaMap;
import org.openelis.modules.main.client.openelis.OpenELIS;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DictionaryScreen extends Screen implements GetMatchesHandler, DragHandler {
    private DictionaryManager manager;
    private CategoryMetaMap   meta = new CategoryMetaMap();
    private SecurityModule    security;

    private TextBox           name, description, systemName;
    private AppButton         queryButton, previousButton, nextButton, addButton, updateButton,
                              commitButton, abortButton, removeEntryButton, addEntryButton;
    private Dropdown<Integer> sectionId;
    private TableWidget       dictEntTable;
    private ButtonGroup       atoz;
    private ScreenNavigator   nav;
    
    public DictionaryScreen() throws Exception {
        super((ScreenDefInt)GWT.create(DictionaryDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.dictionary.server.DictionaryService");

        security = OpenELIS.security.getModule("dictionary");
        if (security == null)
            throw new SecurityException("screenPermException", "Dictionary Screen");

        // Setup link between Screen and widget Handlers
        initialize();

        manager = DictionaryManager.getInstance();

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
        setState(State.DEFAULT);
        DataChangeEvent.fire(this);
    }

    private void initialize() {
        AutoComplete<Integer> relEntry;        
        
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

        name = (TextBox)def.getWidget(meta.getName());
        addScreenHandler(name, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                name.setValue(manager.getCategory().getName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getCategory().setName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                name.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                   .contains(event.getState()));
                name.setQueryMode(event.getState() == State.QUERY);
                if (event.getState() == State.ADD || event.getState() == State.UPDATE)
                    name.setFocus(true);
            }
        });

        description = (TextBox)def.getWidget(meta.getDescription());
        addScreenHandler(description, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                description.setValue(manager.getCategory().getDescription());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getCategory().setDescription(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                description.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                          .contains(event.getState()));
                description.setQueryMode(event.getState() == State.QUERY);
            }
        });

        sectionId = (Dropdown)def.getWidget(meta.getSectionId());
        addScreenHandler(sectionId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                sectionId.setSelection(manager.getCategory().getSectionId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getCategory().setSectionId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sectionId.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                        .contains(event.getState()));
                sectionId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        systemName = (TextBox)def.getWidget(meta.getSystemName());
        addScreenHandler(systemName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                systemName.setValue(manager.getCategory().getSystemName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getCategory().setSystemName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                systemName.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                         .contains(event.getState()));
                systemName.setQueryMode(event.getState() == State.QUERY);
            }
        });

        dictEntTable = (TableWidget)def.getWidget("dictEntTable");
        addScreenHandler(dictEntTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                if(state != State.QUERY)
                    dictEntTable.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                dictEntTable.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                           .contains(event.getState()));
                dictEntTable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        dictEntTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                DictionaryViewDO entry;
                TableDataRow row;

                r = event.getRow();
                c = event.getCol();
                val = dictEntTable.getRow(r).cells.get(c).getValue();
                entry = manager.getEntryAt(r);

                switch (c) {
                    case 0:
                        entry.setIsActive((String)val);
                        break;
                    case 1:
                        entry.setSystemName((String)val);
                        break;
                    case 2:
                        entry.setLocalAbbrev((String)val);
                        break;
                    case 3:
                        entry.setEntry((String)val);
                        break;
                    case 4:
                        row = (TableDataRow)val;
                        entry.setRelatedEntryId((Integer)row.key);
                        entry.setRelatedEntryName((String)row.cells.get(0).getValue());
                        break;
                }
            }
        });

        dictEntTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                DictionaryViewDO entry;
                
                entry = new DictionaryViewDO();
                entry.setIsActive("Y");
                manager.addEntry(entry);                                
            }
        });

        dictEntTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                manager.removeEntryAt(event.getIndex());
            }
        });
        
        dictEntTable.addRowMovedHandler(new RowMovedHandler() {
            public void onRowMoved(RowMovedEvent event) {                
                manager.moveEntry(event.getOldIndex(),event.getNewIndex());                
            }
            
        });

        relEntry = (AutoComplete<Integer>)dictEntTable.getColumnWidget(meta.getDictionary().getRelatedEntry().getEntry());
        relEntry.addGetMatchesHandler(this);                    
        
        removeEntryButton = (AppButton)def.getWidget("removeEntryButton");
        addScreenHandler(removeEntryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;
                
                r = dictEntTable.getSelectedRow();
                
                if (r > -1 && dictEntTable.numRows() > 0) {
                    if(validateForDelete(manager.getEntryAt(r)))
                        dictEntTable.deleteRow(r);
                    else 
                        Window.alert(consts.get("dictionaryDeleteException"));
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeEntryButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                                .contains(event.getState()));
            }
        });

        addEntryButton = (AppButton)def.getWidget("addEntryButton");
        addScreenHandler(addEntryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                dictEntTable.addRow();
                dictEntTable.selectRow(dictEntTable.numRows() - 1);
                dictEntTable.scrollToSelection();
                dictEntTable.setCell(dictEntTable.numRows() - 1, 0, "Y");
                dictEntTable.startEditing(dictEntTable.numRows() - 1, 3);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addEntryButton.enable(EnumSet.of(State.ADD, State.UPDATE)
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
                            Window.alert("Error: Dictionary call query failed; " +
                                         error.getMessage());
                            window.setError(consts.get("queryFailed"));
                        }
                    }
                });
            }

            public boolean fetch(RPC entry) {
                return fetchByCategoryId( (entry == null) ? null : ((IdNameVO)entry).getId());
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

        setSectionModel();

    }

    public void onGetMatches(GetMatchesEvent event) {
        QueryFieldUtil parser;
        TableDataRow row;
        ArrayList<TableDataRow> model;
        ArrayList<IdNameVO> list;
        IdNameVO data;

        parser = new QueryFieldUtil();
        parser.parse(event.getMatch());

        window.setBusy();
        try {
            list = service.callList("fetchIdEntryByEntry", parser.getParameter().get(0));
            model = new ArrayList<TableDataRow>();
            for (int i = 0; i < list.size(); i++ ) {
                data = (IdNameVO)list.get(i);
                row = new TableDataRow(data.getId(), data.getName());
                model.add(row);
            }
            ((AutoComplete)event.getSource()).showAutoMatches(model);
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
        
        window.clearStatus();
    }   

    public void onDragEnd(DragEndEvent event) {
        
    }
    
    public void onDragStart(DragStartEvent event) {
        
    }
    
    public void onPreviewDragEnd(DragEndEvent event) {  
        
    }
    
    public void onPreviewDragStart(DragStartEvent event) {
        
    }
    protected void query() {
        manager = DictionaryManager.getInstance();
        setState(State.QUERY);
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
        manager = DictionaryManager.getInstance();
        setState(State.ADD);
        DataChangeEvent.fire(this);
        enableDragAndDrop(true);
        window.setDone(consts.get("enterInformationPressCommit"));
    }

    protected void update() {
        window.setBusy("Locking Record for update...");

        try {
            manager = manager.fetchForUpdate();

            setState(State.UPDATE);
            DataChangeEvent.fire(this);
            enableDragAndDrop(true);
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }

        window.clearStatus();
    }

    protected void commit() {
        Query query;
        //
        // set the focus to null so every field will commit its data.
        //
        name.setFocus(false);

        if (!validate()) {
            window.setError(consts.get("correctErrors"));
            return;
        }

        if (state == State.QUERY) {
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
                enableDragAndDrop(false);
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
                enableDragAndDrop(false);
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                Window.alert("commitUpdate(): " + e.getMessage());
                window.clearStatus();
            }
        }

    }

    protected void abort() {
        name.setFocus(false);
        
        clearErrors();
        window.setBusy(consts.get("cancelChanges"));
        
        if (state == State.QUERY) {
            fetchByCategoryId(null);
            window.setDone(consts.get("queryAborted"));
        } else if (state == State.ADD) {
            fetchByCategoryId(null);
            window.setDone(consts.get("addAborted"));
            enableDragAndDrop(false);
        } else if (state == State.UPDATE) {
            try {
                manager = manager.abortUpdate();
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                enableDragAndDrop(false);
            } catch (Exception e) {
                Window.alert(e.getMessage());
                fetchByCategoryId(null);
            }
            window.setDone(consts.get("updateAborted"));
        } else {
            window.clearStatus();
        }

    }

    protected boolean fetchByCategoryId(Integer id) {
        if (id == null) {
            manager = DictionaryManager.getInstance();
            setState(State.DEFAULT);
        } else {
            window.setBusy(consts.get("fetching"));
            try {
                window.setBusy(consts.get("fetching"));
                manager = DictionaryManager.fetchByCategoryId(id);
            } catch (Exception e) {
                fetchByCategoryId(null);
                e.printStackTrace();
                Window.alert(consts.get("fetchFailed") + e.getMessage());
                return false;
            }
            setState(State.DISPLAY);
        }
        DataChangeEvent.fire(this);
        window.clearStatus();

        return true;
    }

    private ArrayList<TableDataRow> getTableModel() {
        ArrayList<TableDataRow> model;
        DictionaryViewDO entry;
        TableDataRow row;

        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        for (int i = 0; i < manager.count(); i++ ) {
            entry = manager.getEntryAt(i);

            row = new TableDataRow(5);
            row.key = entry.getId();
            row.data = entry.getCategoryId();
            
            row.cells.get(0).setValue(entry.getIsActive());
            row.cells.get(1).setValue(entry.getSystemName());
            row.cells.get(2).setValue(entry.getLocalAbbrev());
            row.cells.get(3).setValue(entry.getEntry());
            row.cells.get(4).setValue(new TableDataRow(entry.getRelatedEntryId(),
                                                       entry.getRelatedEntryName()));
            model.add(row);
        }

        return model;
    }

    private void setSectionModel() {
        ArrayList<TableDataRow> model;
        ArrayList<SectionDO> list;

        model = new ArrayList<TableDataRow>();
        list = SectionCache.getSectionList();

        model.add(new TableDataRow(null, ""));
        for (SectionDO section : list) {
            model.add(new TableDataRow(section.getId(), section.getName()));
        }

        sectionId.setModel(model);
    }
    
    private void enableDragAndDrop(boolean enable) {          
        dictEntTable.enableDrag(enable);
        dictEntTable.enableDrop(enable);
        if(enable) {
            dictEntTable.addTarget(dictEntTable);
            dictEntTable.addDragHandler(this);
        } 
    }
    
    private boolean validateForDelete(DictionaryViewDO data) {
        DictionaryRPC rpc;
        
        rpc = new DictionaryRPC();
        
        if(data.getId() == null)
            return true;
        
        rpc.data = data;
        rpc.valid = true;
        
        try {
            window.setBusy(consts.get("validatingDelete"));
            rpc = (DictionaryRPC)service.call("validateDelete", rpc);            
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        
        window.clearStatus();
        return rpc.valid;
    }
}