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
package org.openelis.modules.dictionary1.client;

import static org.openelis.modules.main.client.Logger.logger;
import static org.openelis.ui.screen.Screen.ShortKeys.CTRL;
import static org.openelis.ui.screen.State.ADD;
import static org.openelis.ui.screen.State.DEFAULT;
import static org.openelis.ui.screen.State.DISPLAY;
import static org.openelis.ui.screen.State.QUERY;
import static org.openelis.ui.screen.State.UPDATE;

import java.util.ArrayList;
import java.util.logging.Level;

import org.openelis.cache.SectionCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryViewDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SectionViewDO;
import org.openelis.manager.CategoryManager1;
import org.openelis.meta.CategoryMeta;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.ui.common.DataBaseUtil;
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
import org.openelis.ui.resources.UIResources;
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
import org.openelis.ui.widget.Queryable;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.table.CellTipProvider;
import org.openelis.ui.widget.table.Column;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;
import org.openelis.ui.widget.table.event.RowAddedEvent;
import org.openelis.ui.widget.table.event.RowAddedHandler;
import org.openelis.ui.widget.table.event.RowDeletedEvent;
import org.openelis.ui.widget.table.event.RowDeletedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class DictionaryScreenUI extends Screen {

    @UiTemplate("Dictionary.ui.xml")
    interface DictionaryUiBinder extends UiBinder<Widget, DictionaryScreenUI> {
    };

    public static final DictionaryUiBinder         uiBinder = GWT.create(DictionaryUiBinder.class);

    protected CategoryManager1                     manager;

    protected ModulePermission                     userPermission;

    protected ScreenNavigator<IdNameVO>            nav;

    @UiField
    protected AtoZButtons                          atozButtons;

    @UiField
    protected Table                                atozTable, table;

    @UiField
    protected TextBox<Integer>                     id;

    @UiField
    protected TextBox<String>                      name, description, systemName;

    @UiField
    protected Button                               query, previous, next, add, update, commit,
                    abort, optionsButton, loadResults, addEntryButton, removeEntryButton,
                    moveUpButton, moveDownButton;

    @UiField
    protected Menu                                 optionsMenu;

    @UiField
    protected MenuItem                             dictionaryHistory, categoryHistory;

    @UiField
    protected Dropdown<Integer>                    sectionId;

    @UiField
    protected AutoComplete                         relatedEntry;

    @UiField
    protected CheckBox                             isSystem;

    private MenuItem                               sortAsc, sortDesc;

    protected DictionaryScreenUI                   screen;

    private DictionaryService1Impl                 service  = DictionaryService1Impl.INSTANCE;

    protected AsyncCallbackUI<ArrayList<IdNameVO>> queryCall;

    protected AsyncCallbackUI<CategoryManager1>    fetchForUpdateCall, updateCall, fetchByIdCall,
                    unlockCall;

    public DictionaryScreenUI(WindowInt window) throws Exception {
        setWindow(window);

        userPermission = UserCache.getPermission().getModule("dictionary");
        if (userPermission == null)
            throw new PermissionException(Messages.get()
                                                  .gen_screenPermException("Dictionary Screen"));

        initWidget(uiBinder.createAndBindUi(this));

        manager = null;

        initialize();
        setState(DEFAULT);
        fireDataChange();

        logger.fine("Dictionary Screen Opened");
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        CellTipProvider<HTML> cellTip;
        Column column;
        ArrayList<Item<Integer>> model;
        ArrayList<SectionViewDO> list;

        screen = this;

        //
        // button panel buttons
        //
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

        //
        // screen fields
        //
        addScreenHandler(id, CategoryMeta.getId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                id.setValue(getId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getCategory().setId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                id.setEnabled(isState(QUERY));
                id.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? name : isSystem;
            }
        });

        addScreenHandler(name, CategoryMeta.getName(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                name.setValue(getName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getCategory().setName(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                name.setEnabled(isState(QUERY, ADD, UPDATE));
                name.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? description : id;
            }
        });

        addScreenHandler(description, CategoryMeta.getDescription(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                description.setValue(getDescription());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getCategory().setDescription(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                description.setEnabled(isState(QUERY, ADD, UPDATE));
                description.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? sectionId : name;
            }
        });

        addScreenHandler(sectionId, CategoryMeta.getSectionId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                sectionId.setValue(getSectionId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getCategory().setSectionId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                sectionId.setEnabled(isState(QUERY, ADD, UPDATE));
                sectionId.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? systemName : description;
            }
        });

        addScreenHandler(systemName, CategoryMeta.getSystemName(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                systemName.setValue(getSystemName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getCategory().setSystemName(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                systemName.setEnabled(isState(QUERY, ADD, UPDATE));
                systemName.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? isSystem : sectionId;
            }
        });

        addScreenHandler(isSystem, CategoryMeta.getIsSystem(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isSystem.setValue(getIsSystem());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getCategory().setIsSystem(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                isSystem.setEnabled(isState(QUERY, ADD, UPDATE));
                isSystem.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? id : systemName;
            }
        });

        addScreenHandler(table, "table", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                if ( !isState(QUERY)) {
                    table.setModel(getTableModel());
                }
            }

            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(true);
                table.setQueryMode(isState(QUERY));
            }

            public Object getQuery() {
                ArrayList<QueryData> qds;
                QueryData qd;

                qds = new ArrayList<QueryData>();
                for (int i = 0; i < 5; i++ ) {
                    qd = (QueryData) ((Queryable)table.getColumnWidget(i)).getQuery();
                    if (qd != null) {
                        switch (i) {
                            case 0:
                                qd.setKey(CategoryMeta.getDictionaryIsActive());
                                break;
                            case 1:
                                qd.setKey(CategoryMeta.getDictionarySystemName());
                                break;
                            case 2:
                                qd.setKey(CategoryMeta.getDictionaryCode());
                                break;
                            case 3:
                                qd.setKey(CategoryMeta.getDictionaryEntry());
                                break;
                            case 4:
                                qd.setKey(CategoryMeta.getDictionaryRelatedEntryEntry());
                                break;
                        }
                        qds.add(qd);
                    }
                }

                return qds;
            }
        });

        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            @Override
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if ( !isState(QUERY, ADD, UPDATE)) {
                    event.cancel();
                }
            }
        });

        table.addCellEditedHandler(new CellEditedHandler() {
            @Override
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                DictionaryViewDO data;
                AutoCompleteValue row;

                r = event.getRow();
                c = event.getCol();
                val = table.getValueAt(r, c);

                try {
                    data = manager.dictionary.get(r);
                } catch (Exception e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
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
                        data.setCode((String)val);
                        break;
                    case 3:
                        data.setEntry((String)val);
                        break;
                    case 4:
                        row = (AutoCompleteValue)val;
                        if (row != null) {
                            data.setRelatedEntryId(row.getId());
                             data.setRelatedEntryName(row.getDisplay());
                        } else {
                            data.setRelatedEntryId(null);
                            data.setRelatedEntryName(null);
                        }
                        break;
                }
            }
        });

        table.addRowAddedHandler(new RowAddedHandler() {
            @Override
            public void onRowAdded(RowAddedEvent event) {
                int r;
                DictionaryViewDO data;

                r = event.getIndex();
                try {
                    data = new DictionaryViewDO();
                    data.setIsActive("Y");
                    manager.dictionary.addAt(data, r);
                    table.setValueAt(r, 0, "Y");
                } catch (Exception e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    Window.alert(e.getMessage());
                }
            }
        });

        table.addRowDeletedHandler(new RowDeletedHandler() {
            @Override
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.dictionary.remove(event.getIndex());
                } catch (Exception e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    Window.alert(e.getMessage());
                }
            }
        });

        cellTip = new CellTipProvider<HTML>() {
            @Override
            public HTML getTip(int row, int col) {
                StringBuffer sb;
                DictionaryViewDO data;
                HTML html;

                if (manager == null)
                    return new HTML();
                data = manager.dictionary.get(row);
                if (data == null)
                    return new HTML();
                sb = new StringBuffer();
                sb.append("<div align=\"left\">");
                sb.append(Messages.get().gen_id())
                  .append(": ")
                  .append(DataBaseUtil.toString(data.getId()))
                  .append("<br>");
                sb.append(Messages.get().dictionary_systemName())
                  .append(": ")
                  .append(DataBaseUtil.toString(data.getSystemName()))
                  .append("<br>");
                sb.append(Messages.get().dictionary_code())
                  .append(": ")
                  .append(DataBaseUtil.toString(data.getCode()))
                  .append("<br>");
                sb.append(Messages.get().gen_entry())
                  .append(": ")
                  .append(DataBaseUtil.toString(data.getEntry()))
                  .append("<br>");
                sb.append(Messages.get().dictionary_relEntryId())
                  .append(": ")
                  .append(DataBaseUtil.toString(data.getRelatedEntryId()));
                sb.append("</div>");
                html = new HTML();
                html.setHTML(sb.toString());
                return html;
            }
        };
        table.setTipProvider(cellTip);

        column = table.getColumnAt(3);
        sortAsc = new MenuItem(UIResources.INSTANCE.menuCss().Ascending(),
                               org.openelis.ui.messages.Messages.get().header_ascending(),
                               "");
        sortAsc.addCommand(new Command() {
            public void execute() {
                if (table.getRowCount() > 1) {
                    setBusy(Messages.get().gen_sorting());
                    try {
                        manager = service.sort(manager, true);
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        Window.alert(e.getMessage());
                    }
                    table.setModel(getTableModel());
                    clearStatus();
                }
            }
        });
        column.addMenuItem(sortAsc);
        sortDesc = new MenuItem(UIResources.INSTANCE.menuCss().Descending(),
                                org.openelis.ui.messages.Messages.get().header_descending(),
                                "");
        sortDesc.addCommand(new Command() {
            public void execute() {
                if (table.getRowCount() > 1) {
                    setBusy(Messages.get().gen_sorting());
                    try {
                        manager = service.sort(manager, false);
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        Window.alert(e.getMessage());
                    }
                    table.setModel(getTableModel());
                    clearStatus();
                }
            }
        });
        column.addMenuItem(sortDesc);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                sortDesc.setVisible(isState(ADD, UPDATE));
                sortAsc.setVisible(isState(ADD, UPDATE));
            }
        });

        relatedEntry.addGetMatchesHandler(new GetMatchesHandler() {
            @Override
            public void onGetMatches(GetMatchesEvent event) {
                relatedEntry.showAutoMatches(getRelatedEntryMatches(event.getMatch()));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                addEntryButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                removeEntryButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                moveUpButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                moveDownButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        //
        // left hand navigation panel
        //
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
                                fetchByCategoryId(result.get(0).getId());
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
                            Window.alert("Error: Dictionary call query failed; " +
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
                fetchByCategoryId( (entry == null) ? null : entry.getId());
                return true;
            }
        };

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                optionsMenu.setEnabled(isState(DISPLAY));
                optionsButton.setEnabled(isState(DISPLAY));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                dictionaryHistory.setEnabled(isState(DISPLAY));
            }
        });
        dictionaryHistory.addCommand(new Command() {
            @Override
            public void execute() {
                dictionaryHistory();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                categoryHistory.setEnabled(isState(DISPLAY));
            }
        });
        categoryHistory.addCommand(new Command() {
            @Override
            public void execute() {
                categoryHistory();
            }
        });

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
                    setError(Messages.get().gen_mustCommitOrAbort());
                }
            }
        });

        try {
            model = new ArrayList<Item<Integer>>();
            list = SectionCache.getList();
            for (SectionViewDO data : list)
                model.add(new Item<Integer>(data.getId(), data.getName()));
            sectionId.setModel(model);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            Window.alert(e.getMessage());
        }
    }

    /*
     * basic button methods
     */
    @UiHandler("atozButtons")
    public void atozQuery(ClickEvent event) {
        Query query;
        QueryData field;

        field = new QueryData();
        field.setKey(CategoryMeta.getName());
        field.setQuery( ((Button)event.getSource()).getAction());
        field.setType(QueryData.Type.STRING);

        query = new Query();
        query.setFields(field);
        nav.setQuery(query);
    }

    @UiHandler("query")
    protected void query(ClickEvent event) {
        manager = null;

        setState(QUERY);
        fireDataChange();
        id.setFocus(true);
        setDone(Messages.get().gen_enterFieldsToQuery());
    }

    @UiHandler("previous")
    protected void previous(ClickEvent event) {
        if (DataBaseUtil.isSame(atozTable.getSelectedRow(), 0)) {
            setError(Messages.get().gen_noMoreRecordInDir());
            return;
        }
        nav.previous();
    }

    @UiHandler("next")
    protected void next(ClickEvent event) {
        nav.next();
    }

    @UiHandler("add")
    protected void add(ClickEvent event) {
        setBusy();
        manager = new CategoryManager1();
        manager.getCategory().setIsSystem("N");
        setState(ADD);
        fireDataChange();
        name.setFocus(true);
        setDone(Messages.get().enterInformationPressCommit());
    }

    @UiHandler("update")
    protected void update(ClickEvent event) {
        setBusy(Messages.get().lockForUpdate());

        if (fetchForUpdateCall == null) {
            fetchForUpdateCall = new AsyncCallbackUI<CategoryManager1>() {
                public void success(CategoryManager1 result) {
                    manager = result;
                    setState(UPDATE);
                    fireDataChange();
                    name.setFocus(true);
                }

                public void failure(Throwable e) {
                    logger.log(Level.SEVERE, e.getMessage() != null ? e.getMessage() : "null", e);
                    Window.alert(e.getMessage());
                }

                public void finish() {
                    clearStatus();
                }
            };
        }

        service.fetchForUpdate(manager.getCategory().getId(), fetchForUpdateCall);
    }

    @UiHandler("commit")
    protected void commit(ClickEvent event) {
        commit();
    }

    private void commit() {
        Validation validation;

        finishEditing();

        validation = validate();
        if (Validation.Status.ERRORS.equals(validation.getStatus())) {
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
            updateCall = new AsyncCallbackUI<CategoryManager1>() {
                public void success(CategoryManager1 result) {
                    manager = result;
                    setState(DISPLAY);
                    fireDataChange();
                    clearStatus();
                }

                public void validationErrors(ValidationErrorsList e) {
                    showErrors(e);
                }

                public void failure(Throwable e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    if (isState(ADD))
                        Window.alert("commitAdd(): " + e.getMessage());
                    else
                        Window.alert("commitUpdate(): " + e.getMessage());
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
        setBusy(Messages.get().gen_cancelChanges());

        if (isState(QUERY)) {
            try {
                manager = null;
                setState(DEFAULT);
                fireDataChange();
                setDone(Messages.get().gen_queryAborted());
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                Window.alert(e.getMessage());
                clearStatus();
            }
        } else if (isState(ADD)) {
            try {
                manager = null;
                setState(DEFAULT);
                fireDataChange();
                setDone(Messages.get().gen_addAborted());
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                Window.alert(e.getMessage());
                clearStatus();
            }
        } else if (isState(UPDATE)) {
            if (unlockCall == null) {
                unlockCall = new AsyncCallbackUI<CategoryManager1>() {
                    public void success(CategoryManager1 result) {
                        manager = result;
                        setState(DISPLAY);
                        fireDataChange();
                        setDone(Messages.get().gen_updateAborted());
                    }

                    public void failure(Throwable e) {
                        manager = null;
                        setState(DEFAULT);
                        fireDataChange();
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        Window.alert(e.getMessage());
                        clearStatus();
                    }
                };
            }
            service.abortUpdate(manager.getCategory().getId(), unlockCall);
        }
    }

    @UiHandler("addEntryButton")
    protected void addEntry(ClickEvent event) {
        int r;

        r = table.getSelectedRow();
        if (r < 0 || r == table.getRowCount() - 1) {
            r = table.getRowCount();
            table.addRow();
        } else {
            table.addRowAt( ++r);
        }
        table.selectRowAt(r);
        table.scrollToVisible(r);
        table.startEditing(r, 3);
    }

    @UiHandler("removeEntryButton")
    protected void removeEntry(ClickEvent event) {
        int r;

        r = table.getSelectedRow();
        try {
            if (r > -1 && table.getRowCount() > 0) {
                setBusy(Messages.get().validatingDelete());
                validateForDelete(manager.dictionary.get(r));
                table.removeRowAt(r);
            }
        } catch (ValidationErrorsList e) {
            Window.alert(Messages.get().dictionaryDeleteException());
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            Window.alert(e.getMessage());
        }
        clearStatus();
    }

    @UiHandler("moveUpButton")
    protected void moveUp(ClickEvent event) {
        int r;

        table.finishEditing();
        r = table.getSelectedRow();
        if (r <= 0)
            return;
        manager.dictionary.move(r, r - 1);
        table.setModel(getTableModel());
        table.selectRowAt(r - 1);
        table.scrollToVisible(table.getSelectedRow());
    }

    @UiHandler("moveDownButton")
    protected void moveDown(ClickEvent event) {
        int r;

        table.finishEditing();
        r = table.getSelectedRow();
        if (r < 0 || r >= manager.dictionary.count() - 1)
            return;
        manager.dictionary.move(r, r + 1);
        table.setModel(getTableModel());
        table.selectRowAt(r + 1);
        table.scrollToVisible(table.getSelectedRow());
    }

    /*
     * getters and setters
     */
    private Integer getId() {
        if (manager == null)
            return null;
        return manager.getCategory().getId();
    }

    private String getName() {
        if (manager == null)
            return null;
        return manager.getCategory().getName();
    }

    private String getDescription() {
        if (manager == null)
            return null;
        return manager.getCategory().getDescription();
    }

    private Integer getSectionId() {
        if (manager == null)
            return null;
        return manager.getCategory().getSectionId();
    }

    private String getSystemName() {
        if (manager == null)
            return null;
        return manager.getCategory().getSystemName();
    }

    private String getIsSystem() {
        if (manager == null)
            return null;
        return manager.getCategory().getIsSystem();
    }

    protected void categoryHistory() {
        IdNameVO hist;

        hist = new IdNameVO(manager.getCategory().getId(), manager.getCategory().getName());
        HistoryScreen.showHistory(Messages.get().categoryHistory(),
                                  Constants.table().CATEGORY,
                                  hist);
    }

    protected void dictionaryHistory() {
        int count;
        IdNameVO list[];
        DictionaryViewDO data;

        try {
            count = manager.dictionary.count();
            list = new IdNameVO[count];
            for (int i = 0; i < count; i++ ) {
                data = manager.dictionary.get(i);
                list[i] = new IdNameVO(data.getId(), data.getEntry());
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(Messages.get().dictionaryHistory(),
                                  Constants.table().DICTIONARY,
                                  list);
    }

    protected void fetchByCategoryId(Integer id) {
        if (id == null) {
            manager = null;
            setState(DEFAULT);
            fireDataChange();
            clearStatus();
        } else {
            setBusy(Messages.get().fetching());
            if (fetchByIdCall == null) {
                fetchByIdCall = new AsyncCallbackUI<CategoryManager1>() {
                    public void success(CategoryManager1 result) {
                        manager = result;
                        setState(DISPLAY);
                    }

                    public void notFound() {
                        fetchByCategoryId(null);
                        setDone(Messages.get().gen_noRecordsFound());
                    }

                    public void failure(Throwable e) {
                        fetchByCategoryId(null);
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        Window.alert(Messages.get().gen_fetchFailed() + e.getMessage());
                    }

                    public void finish() {
                        fireDataChange();
                        clearStatus();
                    }
                };
            }
            service.fetchById(id, fetchByIdCall);
        }
    }

    private ArrayList<Row> getTableModel() {
        ArrayList<Row> model;
        DictionaryViewDO data;
        Row row;

        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        try {
            for (int i = 0; i < manager.dictionary.count(); i++ ) {
                data = manager.dictionary.get(i);
                row = new Row(data.getIsActive(),
                              data.getSystemName(),
                              data.getCode(),
                              data.getEntry(),
                              new AutoCompleteValue(data.getRelatedEntryId(),
                                                    data.getRelatedEntryName()));
                row.setData(data.getId());
                model.add(row);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            Window.alert(e.getMessage());
        }
        return model;
    }

    private ArrayList<Item<Integer>> getRelatedEntryMatches(String match) {
        Item<Integer> row;
        ArrayList<Item<Integer>> model;
        ArrayList<DictionaryViewDO> list;

        model = new ArrayList<Item<Integer>>();
        try {
            list = service.fetchByEntry(QueryFieldUtil.parseAutocomplete(match));
            for (DictionaryViewDO data : list) {
                row = new Item<Integer>(data.getId(), data.getEntry(), data.getCategoryName());
                model.add(row);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            Window.alert(e.getMessage());
        }
        return model;
    }

    private void validateForDelete(DictionaryViewDO data) throws Exception {
        if (data.getId() == null)
            return;
        service.validateForDelete(data);
    }
}