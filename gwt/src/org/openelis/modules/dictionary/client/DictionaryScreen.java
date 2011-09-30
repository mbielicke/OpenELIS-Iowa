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
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;

import org.openelis.cache.SectionCache;
import org.openelis.cache.UserCache;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SectionViewDO;
import org.openelis.gwt.common.DataBaseUtil;
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
import org.openelis.gwt.event.BeforeDragStartEvent;
import org.openelis.gwt.event.BeforeDragStartHandler;
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
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.Label;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.gwt.widget.table.TableRow;
import org.openelis.gwt.widget.table.TableWidget;
import org.openelis.gwt.widget.table.event.BeforeCellEditedEvent;
import org.openelis.gwt.widget.table.event.BeforeCellEditedHandler;
import org.openelis.gwt.widget.table.event.CellEditedEvent;
import org.openelis.gwt.widget.table.event.CellEditedHandler;
import org.openelis.gwt.widget.table.event.RowAddedEvent;
import org.openelis.gwt.widget.table.event.RowAddedHandler;
import org.openelis.gwt.widget.table.event.RowDeletedEvent;
import org.openelis.gwt.widget.table.event.RowDeletedHandler;
import org.openelis.gwt.widget.table.event.RowMovedEvent;
import org.openelis.gwt.widget.table.event.RowMovedHandler;
import org.openelis.gwt.widget.table.event.SortEvent;
import org.openelis.gwt.widget.table.event.SortEvent.SortDirection;
import org.openelis.gwt.widget.table.event.SortHandler;
import org.openelis.manager.CategoryManager;
import org.openelis.manager.DictionaryManager;
import org.openelis.meta.CategoryMeta;
import org.openelis.modules.history.client.HistoryScreen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DictionaryScreen extends Screen {
    private CategoryManager       manager;
    private ModulePermission      userPermission;

    private DictionaryScreen      screen;
    private DictionaryComparator  comparator;
    private TextBox               name, description, systemName;
    private AppButton             queryButton, previousButton, nextButton, addButton, updateButton,
                    commitButton, abortButton, removeEntryButton, addEntryButton;
    protected MenuItem            dictionaryHistory, categoryHistory;
    private Dropdown<Integer>     sectionId;
    private AutoComplete<Integer> relatedEntry;
    private CheckBox              isSystem;
    private TableWidget           dictTable;
    private ButtonGroup           atoz;
    private ScreenNavigator       nav;

    public DictionaryScreen() throws Exception {
        super((ScreenDefInt)GWT.create(DictionaryDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.dictionary.server.DictionaryService");
        
        userPermission = UserCache.getPermission().getModule("dictionary");
        if (userPermission == null)
            throw new PermissionException("screenPermException", "Dictionary Screen");

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
        manager = CategoryManager.getInstance();

        initialize();
        setState(State.DEFAULT);
        initializeDropdowns();
        DataChangeEvent.fire(this);
    }

    private void initialize() {
        screen = this;

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

        categoryHistory = (MenuItem)def.getWidget("categoryHistory");
        addScreenHandler(categoryHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                categoryHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                categoryHistory.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        dictionaryHistory = (MenuItem)def.getWidget("dictionaryHistory");
        addScreenHandler(dictionaryHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                dictionaryHistory();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                dictionaryHistory.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        name = (TextBox)def.getWidget(CategoryMeta.getName());
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

        description = (TextBox)def.getWidget(CategoryMeta.getDescription());
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

        sectionId = (Dropdown)def.getWidget(CategoryMeta.getSectionId());
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

        systemName = (TextBox)def.getWidget(CategoryMeta.getSystemName());
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

        isSystem = (CheckBox)def.getWidget(CategoryMeta.getIsSystem());
        addScreenHandler(isSystem, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isSystem.setValue(manager.getCategory().getIsSystem());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getCategory().setIsSystem(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isSystem.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                       .contains(event.getState()));
                isSystem.setQueryMode(event.getState() == State.QUERY);
            }
        });

        dictTable = (TableWidget)def.getWidget("dictEntTable");
        relatedEntry = (AutoComplete<Integer>)dictTable.getColumnWidget(CategoryMeta.getDictionaryRelatedEntryEntry());
        addScreenHandler(dictTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    dictTable.load(getTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                boolean enable;

                dictTable.enable(true);
                dictTable.setQueryMode(event.getState() == State.QUERY);

                enable = EnumSet.of(State.ADD, State.UPDATE).contains(event.getState());
                dictTable.enableDrag(enable);
                dictTable.enableDrop(enable);
            }
        });

        dictTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if (state != State.ADD && state != State.UPDATE && state != State.QUERY)
                    event.cancel();
            }
        });

        dictTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                DictionaryViewDO data;
                TableDataRow row;

                r = event.getRow();
                c = event.getCol();
                val = dictTable.getObject(r, c);

                try {
                    data = manager.getEntries().getEntryAt(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }

                switch (c) {
                    case 0:
                        data.setIsActive((String)val);
                        break;
                    case 1:
                        data.setSystemName((String)val);
                        break;
                    case 2:
                        data.setLocalAbbrev((String)val);
                        break;
                    case 3:
                        data.setEntry((String)val);
                        break;
                    case 4:
                        row = (TableDataRow)val;
                        if (row != null) {
                            data.setRelatedEntryId((Integer)row.key);
                            data.setRelatedEntryName((String)row.cells.get(0).getValue());
                        } else {
                            data.setRelatedEntryId(null);
                            data.setRelatedEntryName(null);
                        }
                        break;
                }
            }
        });

        dictTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                int r;
                DictionaryViewDO data;

                r = event.getIndex();
                try {
                    data = new DictionaryViewDO();
                    data.setIsActive("Y");
                    manager.getEntries().addEntry(data);
                    dictTable.setCell(r, 0, "Y");
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        dictTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getEntries().removeEntryAt(event.getIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        dictTable.addSortHandler(new SortHandler() {
            public void onSort(SortEvent event) {
                SortDirection direction;

                direction = event.getDirection();

                try {
                    if (direction == SortEvent.SortDirection.ASCENDING)
                        sort(manager.getEntries().getEntries(), true);
                    else
                        sort(manager.getEntries().getEntries(), false);

                    DataChangeEvent.fire(screen, dictTable);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        dictTable.addRowMovedHandler(new RowMovedHandler() {
            public void onRowMoved(RowMovedEvent event) {
                try {
                    manager.getEntries().moveEntry(event.getOldIndex(), event.getNewIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        dictTable.enableDrag(true);
        dictTable.enableDrop(true);
        dictTable.addTarget(dictTable);

        dictTable.addBeforeDragStartHandler(new BeforeDragStartHandler<TableRow>() {
            public void onBeforeDragStart(BeforeDragStartEvent<TableRow> event) {
                TableDataRow row;
                Label label;
                String value;

                try {
                    row = event.getDragObject().row;
                    value = (String)row.cells.get(3).value;
                    if (value == null)
                        value = "";
                    label = new Label(value);
                    label.setStyleName("ScreenLabel");
                    label.setWordWrap(false);
                    event.setProxy(label);
                } catch (Exception e) {
                    Window.alert("table beforeDragStart: " + e.getMessage());
                }
            }

        });

        relatedEntry.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ArrayList<TableDataRow> model;
                ArrayList<DictionaryViewDO> list;

                try {
                    list = service.callList("fetchByEntry", QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<TableDataRow>();
                    for (DictionaryViewDO data : list)
                        model.add(new TableDataRow(data.getId(), data.getEntry(), data.getCategoryName()));
                    relatedEntry.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        removeEntryButton = (AppButton)def.getWidget("removeEntryButton");
        addScreenHandler(removeEntryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = dictTable.getSelectedRow();
                try {
                    if (r > -1 && dictTable.numRows() > 0) {
                        window.setBusy(consts.get("validatingDelete"));
                        validateForDelete(manager.getEntries().getEntryAt(r));
                        dictTable.deleteRow(r);
                    }
                } catch (ValidationErrorsList e) {
                    Window.alert(consts.get("dictionaryDeleteException"));
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    e.printStackTrace();
                }
                window.clearStatus();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeEntryButton.enable(EnumSet.of(State.ADD, State.UPDATE)
                                                .contains(event.getState()));
            }
        });

        addEntryButton = (AppButton)def.getWidget("addEntryButton");
        addScreenHandler(addEntryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;

                r = dictTable.numRows();
                dictTable.addRow();
                dictTable.selectRow(r);
                dictTable.scrollToSelection();
                dictTable.startEditing(r, 3);
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

                query.setRowsPerPage(20);
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
                         userPermission.hasSelectPermission();
                atoz.enable(enable);
                nav.enable(enable);
            }

            public void onClick(ClickEvent event) {
                Query query;
                QueryData field;

                field = new QueryData();
                field.key = CategoryMeta.getName();
                field.query = ((AppButton)event.getSource()).action;
                field.type = QueryData.Type.STRING;

                query = new Query();
                query.setFields(field);
                nav.setQuery(query);
            }
        });

        window.addBeforeClosedHandler(new BeforeCloseHandler<ScreenWindow>() {
            public void onBeforeClosed(BeforeCloseEvent<ScreenWindow> event) {
                if (EnumSet.of(State.ADD, State.UPDATE, State.DELETE).contains(state)) {
                    event.cancel();
                    window.setError(consts.get("mustCommitOrAbort"));
                }
            }
        });
    }

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;
        ArrayList<SectionViewDO> list;
        try {
            model = new ArrayList<TableDataRow>();
            list = SectionCache.getList();
            model.add(new TableDataRow(null, ""));
            for (SectionViewDO data : list)
                model.add(new TableDataRow(data.getId(), data.getName()));
            sectionId.setModel(model);
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }

    }

    protected void query() {
        manager = CategoryManager.getInstance();

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
        manager = CategoryManager.getInstance();
        manager.getCategory().setIsSystem("N");

        setState(State.ADD);
        DataChangeEvent.fire(this);

        setFocus(name);
        window.setDone(consts.get("enterInformationPressCommit"));
    }

    protected void update() {
        window.setBusy("Locking Record for update...");

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
            fetchByCategoryId(null);
            window.setDone(consts.get("queryAborted"));
        } else if (state == State.ADD) {
            fetchByCategoryId(null);
            window.setDone(consts.get("addAborted"));
        } else if (state == State.UPDATE) {
            try {
                manager = manager.abortUpdate();
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
            } catch (Exception e) {
                Window.alert(e.getMessage());
                fetchByCategoryId(null);
            }
            window.setDone(consts.get("updateAborted"));
        } else {
            window.clearStatus();
        }
    }

    protected void categoryHistory() {
        IdNameVO hist;

        hist = new IdNameVO(manager.getCategory().getId(), manager.getCategory().getName());
        HistoryScreen.showHistory(consts.get("categoryHistory"), ReferenceTable.CATEGORY, hist);
    }

    protected void dictionaryHistory() {
        int count;
        IdNameVO list[];
        DictionaryManager man;
        DictionaryViewDO data;

        try {
            man = manager.getEntries();
            count = man.count();
            list = new IdNameVO[count];
            for (int i = 0; i < count; i++ ) {
                data = man.getEntryAt(i);
                list[i] = new IdNameVO(data.getId(), data.getEntry());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(consts.get("dictionaryHistory"), ReferenceTable.DICTIONARY, list);
    }

    protected boolean fetchByCategoryId(Integer id) {
        if (id == null) {
            manager = CategoryManager.getInstance();
            setState(State.DEFAULT);
        } else {
            window.setBusy(consts.get("fetching"));
            try {
                window.setBusy(consts.get("fetching"));
                manager = CategoryManager.fetchWithEntries(id);
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

    private void sort(ArrayList<DictionaryViewDO> list, boolean ascending) {
        if (comparator == null)
            comparator = new DictionaryComparator();
        comparator.setSortAscending(ascending);
        Collections.sort(list, comparator);
    }

    private ArrayList<TableDataRow> getTableModel() {
        ArrayList<TableDataRow> model;
        DictionaryViewDO data;
        TableDataRow row;

        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        try {
            for (int i = 0; i < manager.getEntries().count(); i++ ) {
                data = manager.getEntries().getEntryAt(i);
                row = new TableDataRow(data.getId(), data.getIsActive(), data.getSystemName(),
                                       data.getLocalAbbrev(), data.getEntry(),
                                       new TableDataRow(data.getRelatedEntryId(),
                                                        data.getRelatedEntryName()));
                row.data = data.getCategoryId();
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }

    private void validateForDelete(DictionaryViewDO data) throws Exception {
        if (data.getId() == null)
            return;
        service.call("validateForDelete", data);
    }

    //
    // Sort for table
    //
    private class DictionaryComparator implements Comparator<DictionaryViewDO> {
        boolean ascending;

        public void setSortAscending(boolean ascending) {
            this.ascending = ascending;
        }

        public int compare(DictionaryViewDO data1, DictionaryViewDO data2) {
            String entry1, entry2;

            entry1 = data1.getEntry();
            entry2 = data2.getEntry();

            if (ascending) {
                return compare(entry1, entry2);
            } else {
                return (compare(entry1, entry2) * -1);
            }
        }

        private int compare(String entry1, String entry2) {
            if (DataBaseUtil.isEmpty(entry1)) {
                if (DataBaseUtil.isEmpty(entry2))
                    return 0;
                else
                    return 1;
            } else {
                if (DataBaseUtil.isEmpty(entry2))
                    return -1;
                else
                    return entry1.compareTo(entry2);
            }
        }

    }
}