package org.openelis.modules.auxiliary1.client;

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
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalyteDO;
import org.openelis.domain.AuxFieldValueViewDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.MethodDO;
import org.openelis.manager.AuxFieldGroupManager1;
import org.openelis.meta.AuxFieldGroupMeta;
import org.openelis.meta.CategoryMeta;
import org.openelis.modules.analyte1.client.AnalyteService1Impl;
import org.openelis.modules.dictionary1.client.DictionaryLookupScreenUI;
import org.openelis.modules.dictionary1.client.DictionaryService1Impl;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.method.client.MethodService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.GridFieldErrorException;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.ActionEvent;
import org.openelis.ui.event.ActionHandler;
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
import org.openelis.ui.widget.ModalWindow;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.Queryable;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.calendar.Calendar;
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
import org.openelis.utilcommon.ResultRangeNumeric;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class AuxiliaryScreenUI extends Screen {

    @UiTemplate("Auxiliary.ui.xml")
    interface AuxiliaryUiBinder extends UiBinder<Widget, AuxiliaryScreenUI> {
    };

    public static final AuxiliaryUiBinder            uiBinder = GWT.create(AuxiliaryUiBinder.class);

    private AuxFieldGroupManager1                    manager;

    protected ModulePermission                       userPermission;

    protected ScreenNavigator<IdNameVO>              nav;

    @UiField
    protected AtoZButtons                            atozButtons;

    @UiField
    protected TextBox<String>                        name, description;

    @UiField
    protected Button                                 query, previous, next, add, update, commit,
                    abort, optionsButton, loadResults, addFieldButton, removeFieldButton,
                    moveUpButton, moveDownButton, addValueButton, removeValueButton,
                    dictionaryLookupButton;

    @UiField
    protected Menu                                   optionsMenu;

    @UiField
    protected MenuItem                               groupHistory, fieldHistory, valueHistory;

    @UiField
    protected Dropdown<Integer>                      unitId, scriptletId, valueTypeId;

    @UiField
    protected AutoComplete                           analyte, method;

    @UiField
    protected CheckBox                               isActive;

    @UiField
    protected Calendar                               activeBegin, activeEnd;

    @UiField
    protected Table                                  atozTable, fieldTable, valueTable;

    protected AuxiliaryScreenUI                      screen;

    private ArrayList<GridFieldErrorException>       valueErrorList;

    private ResultRangeNumeric                       rangeNumeric;

    private DictionaryLookupScreenUI                 dictLookup;

    private AuxiliaryService1Impl                    service  = AuxiliaryService1Impl.INSTANCE;

    protected AsyncCallbackUI<AuxFieldGroupManager1> fetchForUpdateCall, updateCall, fetchByIdCall,
                    unlockCall;

    protected AsyncCallbackUI<ArrayList<IdNameVO>>   queryCall;

    public AuxiliaryScreenUI(WindowInt window) throws Exception {
        setWindow(window);

        userPermission = UserCache.getPermission().getModule("dictionary");
        if (userPermission == null)
            throw new PermissionException(Messages.get()
                                                  .gen_screenPermException("Dictionary Screen"));

        try {
            CategoryCache.getBySystemNames("unit_of_measure",
                                           "scriptlet_aux_field",
                                           "aux_field_value_type");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            window.close();
        }

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
        ArrayList<Item<Integer>> model;
        ArrayList<DictionaryDO> list;
        Item<Integer> row;

        screen = this;
        rangeNumeric = new ResultRangeNumeric();

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
        addScreenHandler(name, AuxFieldGroupMeta.getName(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                name.setValue(getName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getGroup().setName(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                name.setEnabled(isState(QUERY, ADD, UPDATE));
                name.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? description : activeEnd;
            }
        });

        addScreenHandler(description,
                         AuxFieldGroupMeta.getDescription(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 description.setValue(getDescription());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 manager.getGroup().setDescription(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 description.setEnabled(isState(QUERY, ADD, UPDATE));
                                 description.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? isActive : name;
                             }
                         });

        addScreenHandler(isActive, AuxFieldGroupMeta.getIsActive(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isActive.setValue(getIsActive());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getGroup().setIsActive(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                isActive.setEnabled(isState(QUERY, ADD, UPDATE));
                isActive.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? activeBegin : description;
            }
        });

        addScreenHandler(activeBegin,
                         AuxFieldGroupMeta.getActiveBegin(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 activeBegin.setValue(getActiveBegin());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 manager.getGroup().setActiveBegin(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 activeBegin.setEnabled(isState(QUERY, ADD, UPDATE));
                                 activeBegin.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? activeEnd : isActive;
                             }
                         });

        addScreenHandler(activeEnd,
                         AuxFieldGroupMeta.getActiveEnd(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 activeEnd.setValue(getActiveEnd());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 manager.getGroup().setActiveEnd(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 activeEnd.setEnabled(isState(QUERY, ADD, UPDATE));
                                 activeEnd.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? name : activeBegin;
                             }
                         });

        addScreenHandler(fieldTable, "fieldTable", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                if ( !isState(QUERY)) {
                    fieldTable.setModel(getAuxFieldModel());
                }
            }

            public void onStateChange(StateChangeEvent event) {
                fieldTable.setEnabled(true);
                fieldTable.setQueryMode(isState(QUERY));
            }

            public Object getQuery() {
                ArrayList<QueryData> qds;
                QueryData qd;

                qds = new ArrayList<QueryData>();
                for (int i = 0; i < 8; i++ ) {
                    qd = (QueryData) ((Queryable)fieldTable.getColumnWidget(i)).getQuery();
                    if (qd != null) {
                        switch (i) {
                            case 0:
                                qd.setKey(AuxFieldGroupMeta.getFieldAnalyteName());
                                break;
                            case 1:
                                qd.setKey(AuxFieldGroupMeta.getFieldMethodName());
                                break;
                            case 2:
                                qd.setKey(AuxFieldGroupMeta.getFieldUnitOfMeasureId());
                                break;
                            case 3:
                                qd.setKey(AuxFieldGroupMeta.getFieldIsActive());
                                break;
                            case 4:
                                qd.setKey(AuxFieldGroupMeta.getFieldIsRequired());
                                break;
                            case 5:
                                qd.setKey(AuxFieldGroupMeta.getFieldIsReportable());
                                break;
                            case 6:
                                qd.setKey(AuxFieldGroupMeta.getFieldDescription());
                                break;
                            case 7:
                                qd.setKey(AuxFieldGroupMeta.getFieldScriptletId());
                                break;
                        }
                        qds.add(qd);
                    }
                }

                return qds;
            }
        });

        fieldTable.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                valueTable.setModel(getAuxFieldValueModel(manager.field.get(event.getSelectedItem())));
                addValueButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        fieldTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            @Override
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if ( !isState(ADD, UPDATE))
                    event.cancel();
            }
        });

        fieldTable.addCellEditedHandler(new CellEditedHandler() {
            @Override
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                AuxFieldViewDO data;
                AnalyteDO analyte;
                MethodDO method;

                r = event.getRow();
                c = event.getCol();
                val = fieldTable.getValueAt(r, c);

                try {
                    data = manager.field.get(r);
                } catch (Exception e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    Window.alert(e.getMessage());
                    return;
                }

                switch (c) {
                    case 0:
                        analyte = (AnalyteDO) ( ((AutoCompleteValue)val).getData());
                        if (analyte != null) {
                            data.setAnalyteId(analyte.getId());
                            data.setAnalyteName(analyte.getName());
                        } else {
                            data.setAnalyteId(null);
                            data.setAnalyteName(null);
                        }
                        break;
                    case 1:
                        method = (MethodDO) ( ((AutoCompleteValue)val).getData());
                        if (method != null) {
                            data.setMethodId(method.getId());
                            data.setMethodName(method.getName());
                        } else {
                            data.setMethodId(null);
                            data.setMethodName(null);
                        }
                        break;
                    case 2:
                        data.setUnitOfMeasureId((Integer)val);
                        break;
                    case 3:
                        data.setIsActive((String)val);
                        break;
                    case 4:
                        data.setIsRequired((String)val);
                        break;
                    case 5:
                        data.setIsReportable((String)val);
                        break;
                    case 6:
                        data.setDescription((String)val);
                        break;
                    case 7:
                        data.setScriptletId((Integer)val);
                        break;
                }
            }
        });

        fieldTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                AuxFieldViewDO data;
                ArrayList<AuxFieldValueViewDO> values;
                int r;

                r = event.getIndex();
                try {

                    values = new ArrayList<AuxFieldValueViewDO>();
                    values.add(new AuxFieldValueViewDO());
                    data = manager.field.add();
                    data.setIsActive("Y");
                    data.setIsReportable("N");
                    data.setIsRequired("N");
                    manager.value.add(data);

                    fieldTable.setValueAt(r, 3, "Y");
                    fieldTable.setValueAt(r, 4, "N");
                    fieldTable.setValueAt(r, 5, "N");

                    valueTable.finishEditing();
                    valueTable.setModel(getAuxFieldValueModel(manager.field.get(r)));
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        fieldTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                manager.field.remove(event.getIndex());
            }
        });

        addScreenHandler(analyte,
                         AuxFieldGroupMeta.getFieldAnalyteName(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 if (fieldTable.getSelectedRow() != -1)
                                     analyte.setValue(new AutoCompleteValue(manager.field.get(fieldTable.getSelectedRow())
                                                                                         .getAnalyteId(),
                                                                            manager.field.get(fieldTable.getSelectedRow())
                                                                                         .getAnalyteName()));
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 AutoCompleteValue row;
                                 AnalyteDO ana;

                                 row = analyte.getValue();
                                 if (row == null || row.getId() == null) {
                                     manager.field.get(fieldTable.getSelectedRow())
                                                  .setAnalyteId(null);
                                     manager.field.get(fieldTable.getSelectedRow())
                                                  .setAnalyteName(null);
                                 } else {
                                     ana = (AnalyteDO)row.getData();
                                     manager.field.get(fieldTable.getSelectedRow())
                                                  .setAnalyteId(ana.getId());
                                     manager.field.get(fieldTable.getSelectedRow())
                                                  .setAnalyteName(ana.getName());
                                 }
                             }
                         });

        analyte.addGetMatchesHandler(new GetMatchesHandler() {
            @Override
            public void onGetMatches(GetMatchesEvent event) {
                getAnalyteMatches(event.getMatch());
            }
        });

        method.addGetMatchesHandler(new GetMatchesHandler() {
            @Override
            public void onGetMatches(GetMatchesEvent event) {
                getMethodMatches(event.getMatch());
            }
        });

        addScreenHandler(valueTable, "valueTable", new ScreenHandler<ArrayList<Row>>() {
            public void onDataChange(DataChangeEvent event) {
                if ( !isState(QUERY) && fieldTable.getSelectedRow() != -1) {
                    valueTable.setModel(getAuxFieldValueModel(manager.field.get(fieldTable.getSelectedRow())));
                } else {
                    valueTable.setModel(null);
                }
            }

            public void onStateChange(StateChangeEvent event) {
                valueTable.setEnabled(true);
            }
        });

        valueTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if ( !isState(ADD, UPDATE))
                    event.cancel();
            }
        });

        valueTable.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                int r;
                GridFieldErrorException ex;

                if (isState(DISPLAY, DEFAULT))
                    return;

                fieldTable.finishEditing();
                r = valueTable.getSelectedRow();

                if (valueErrorList != null) {
                    for (int i = 0; i < valueErrorList.size(); i++ ) {
                        ex = valueErrorList.get(i);
                        if (ex.getColumnIndex() == r)
                            valueErrorList.remove(i);
                    }
                }

                addValueButton.setEnabled(isState(ADD, UPDATE));
                if (valueTable.getRowCount() > 1)
                    removeValueButton.setEnabled(isState(ADD, UPDATE));
                else
                    removeValueButton.setEnabled(false);
                dictionaryLookupButton.setEnabled(isState(ADD, UPDATE));
            }

        });

        valueTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c, fr;
                Object val;
                AuxFieldValueViewDO data;

                fr = fieldTable.getSelectedRow();
                r = event.getRow();
                c = event.getCol();
                val = valueTable.getValueAt(r, c);
                data = null;

                try {
                    data = manager.value.get(manager.field.get(fr), r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }

                switch (c) {
                    case 0:
                        data.setTypeId((Integer)val);
                        valueTable.clearExceptions(r, 1);
                        try {
                            validateValue(data, (String)valueTable.getValueAt(r, 1));
                        } catch (Exception e) {
                            valueTable.addException(r, 1, e);
                        }
                        break;
                    case 1:
                        /*
                         * we need to set the new value before we can validate
                         * it
                         */
                        data.setValue((String)val);

                        valueTable.clearExceptions(r, c);
                        try {
                            validateValue(data, (String)val);
                        } catch (Exception e) {
                            valueTable.addException(r, c, e);
                        }
                        break;
                }
            }
        });

        valueTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                int r;

                r = fieldTable.getSelectedRow();
                dictionaryLookupButton.setEnabled(true);
                try {
                    manager.value.add(manager.field.get(r));
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        valueTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                int fr;

                fr = fieldTable.getSelectedRow();
                manager.value.remove(manager.field.get(fr), event.getIndex());
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                addFieldButton.setEnabled(isState(ADD, UPDATE));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                removeFieldButton.setEnabled(isState(ADD, UPDATE));
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

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                addValueButton.setEnabled(false);
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                removeValueButton.setEnabled(false);
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                dictionaryLookupButton.setEnabled(false);
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
                            Window.alert("Error: Dictionary call query failed; " +
                                         error.getMessage());
                            setError(Messages.get().gen_queryFailed());
                        }
                    };
                }
                query.setRowsPerPage(28);
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
                optionsMenu.setEnabled(isState(DISPLAY));
                optionsButton.setEnabled(isState(DISPLAY));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                groupHistory.setEnabled(isState(DISPLAY));
            }
        });
        groupHistory.addCommand(new Command() {
            @Override
            public void execute() {
                groupHistory();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                fieldHistory.setEnabled(isState(DISPLAY));
            }
        });
        fieldHistory.addCommand(new Command() {
            @Override
            public void execute() {
                fieldHistory();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                valueHistory.setEnabled(isState(DISPLAY));
            }
        });
        valueHistory.addCommand(new Command() {
            @Override
            public void execute() {
                valueHistory();
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

        model = new ArrayList<Item<Integer>>();
        list = CategoryCache.getBySystemName("unit_of_measure");
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        unitId.setModel(model);

        model = new ArrayList<Item<Integer>>();
        list = CategoryCache.getBySystemName("scriptlet_aux_field");
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        scriptletId.setModel(model);

        model = new ArrayList<Item<Integer>>();
        list = CategoryCache.getBySystemName("aux_field_value_type");
        for (DictionaryDO d : list) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled("Y".equals(d.getIsActive()));
            model.add(row);
        }

        valueTypeId.setModel(model);
    }

    /*
     * basic button methods
     */
    @UiHandler("atozButtons")
    public void atozQuery(ClickEvent event) {
        Query query;
        QueryData field;

        field = new QueryData();
        field.setKey(AuxFieldGroupMeta.getName());
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
        name.setFocus(true);
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

        manager = new AuxFieldGroupManager1();
        manager.getGroup().setIsActive("N");

        setState(ADD);
        fireDataChange();
        name.setFocus(true);
        setDone(Messages.get().enterInformationPressCommit());
    }

    @UiHandler("update")
    protected void update(ClickEvent event) {
        setBusy(Messages.get().lockForUpdate());

        if (fetchForUpdateCall == null) {
            fetchForUpdateCall = new AsyncCallbackUI<AuxFieldGroupManager1>() {
                public void success(AuxFieldGroupManager1 result) {
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

        service.fetchForUpdate(manager.getGroup().getId(), fetchForUpdateCall);
    }

    @UiHandler("commit")
    protected void commit(ClickEvent event) {
        commit();
    }

    private void commit() {
        Validation validation;

        finishEditing();

        fieldTable.clearExceptions();
        valueTable.clearExceptions();
        clearErrors();
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
            updateCall = new AsyncCallbackUI<AuxFieldGroupManager1>() {
                public void success(AuxFieldGroupManager1 result) {
                    manager = result;
                    setState(DISPLAY);
                    fireDataChange();
                    if (isState(ADD))
                        setDone(Messages.get().addingComplete());
                    else
                        setDone(Messages.get().updatingComplete());
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
        fieldTable.clearExceptions();
        valueTable.clearExceptions();
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
                unlockCall = new AsyncCallbackUI<AuxFieldGroupManager1>() {
                    public void success(AuxFieldGroupManager1 result) {
                        clearStatus();
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
            service.abortUpdate(manager.getGroup().getId(), unlockCall);
        }
    }

    @UiHandler("addFieldButton")
    protected void addField(ClickEvent event) {
        int r;

        valueTable.finishEditing();
        r = fieldTable.getRowCount();
        fieldTable.addRow();
        fieldTable.selectRowAt(r);
        fieldTable.startEditing(r, 0);
        fieldTable.scrollToVisible(fieldTable.getSelectedRow());
    }

    @UiHandler("removeFieldButton")
    protected void removeField(ClickEvent event) {
        int r;

        valueTable.finishEditing();
        r = fieldTable.getSelectedRow();
        if (r > -1 && fieldTable.getRowCount() > 0) {
            try {
                service.validateForDelete(manager.field.get(r));
            } catch (ValidationErrorsList e) {
                Window.alert(Messages.get().aux_deleteException());
                return;
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                Window.alert(e.getMessage());
                return;
            }
            fieldTable.removeRowAt(r);
            valueTable.setModel(getAuxFieldValueModel(null));
        }
    }

    @UiHandler("moveUpButton")
    protected void moveUp(ClickEvent event) {
        int r;

        fieldTable.finishEditing();
        r = fieldTable.getSelectedRow();
        if (r <= 0)
            return;
        manager.field.move(r, r - 1);
        fieldTable.setModel(getAuxFieldModel());
        fieldTable.selectRowAt(r - 1);
        fieldTable.scrollToVisible(fieldTable.getSelectedRow());
    }

    @UiHandler("moveDownButton")
    protected void moveDown(ClickEvent event) {
        int r;

        fieldTable.finishEditing();
        r = fieldTable.getSelectedRow();
        if (r < 0 || r >= manager.field.count() - 1)
            return;
        manager.field.move(r, r + 1);
        fieldTable.setModel(getAuxFieldModel());
        fieldTable.selectRowAt(r + 1);
        fieldTable.scrollToVisible(fieldTable.getSelectedRow());
    }

    @UiHandler("addValueButton")
    protected void addValue(ClickEvent event) {
        int r;

        r = valueTable.getRowCount();
        valueTable.addRow();
        valueTable.selectRowAt(r);
        removeValueButton.setEnabled(true);
        valueTable.startEditing(r, 0);
        fieldTable.scrollToVisible(fieldTable.getSelectedRow());
    }

    @UiHandler("removeValueButton")
    protected void removeValue(ClickEvent event) {
        int r;

        r = valueTable.getSelectedRow();
        if (r > -1 && valueTable.getRowCount() > 0)
            valueTable.removeRowAt(r);
        if (valueTable.getRowCount() < 2)
            removeValueButton.setEnabled(false);
    }

    @UiHandler("dictionaryLookupButton")
    protected void dictionaryLookup(ClickEvent event) {
        showDictionary(null, null);
    }

    /*
     * getters and setters
     */
    private Integer getId() {
        if (manager == null)
            return null;
        return manager.getGroup().getId();
    }

    private String getName() {
        if (manager == null)
            return null;
        return manager.getGroup().getName();
    }

    private String getDescription() {
        if (manager == null)
            return null;
        return manager.getGroup().getDescription();
    }

    private String getIsActive() {
        if (manager == null)
            return null;
        return manager.getGroup().getIsActive();
    }

    private Datetime getActiveBegin() {
        if (manager == null)
            return null;
        return manager.getGroup().getActiveBegin();
    }

    private Datetime getActiveEnd() {
        if (manager == null)
            return null;
        return manager.getGroup().getActiveEnd();
    }

    protected void groupHistory() {
        IdNameVO hist;

        hist = new IdNameVO(manager.getGroup().getId(), manager.getGroup().getName());
        HistoryScreen.showHistory(Messages.get().auxFieldGroupHistory(),
                                  Constants.table().AUX_FIELD_GROUP,
                                  hist);
    }

    protected void fieldHistory() {
        int i, count;
        IdNameVO refVoList[];
        AuxFieldViewDO data;

        try {
            count = manager.field.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = manager.field.get(i);
                refVoList[i] = new IdNameVO(data.getId(), data.getAnalyteName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(Messages.get().auxFieldHistory(),
                                  Constants.table().AUX_FIELD,
                                  refVoList);
    }

    protected void valueHistory() {
        int i, count, r;
        IdNameVO refVoList[];
        AuxFieldValueViewDO data;
        String value;
        DictionaryDO dict;
        String entry;
        Integer typeId;

        try {
            r = fieldTable.getSelectedRow();
            if (r == -1)
                return;

            count = manager.value.count(manager.field.get(r));
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = manager.value.get(manager.field.get(r), i);
                typeId = data.getTypeId();
                dict = DictionaryCache.getById(typeId);
                if (dict != null)
                    entry = dict.getEntry();
                else
                    entry = typeId.toString();

                value = data.getDictionary();
                if (value == null)
                    value = data.getValue();

                if (value != null)
                    refVoList[i] = new IdNameVO(data.getId(), entry + ":  " + value);
                else
                    refVoList[i] = new IdNameVO(data.getId(), entry);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(Messages.get().auxFieldValueHistory(),
                                  Constants.table().AUX_FIELD_VALUE,
                                  refVoList);
    }

    private void fetchById(Integer id) {
        if (id == null) {
            manager = null;
            setState(DEFAULT);
            fireDataChange();
            clearStatus();
        } else {
            setBusy(Messages.get().gen_fetching());
            if (fetchByIdCall == null) {
                fetchByIdCall = new AsyncCallbackUI<AuxFieldGroupManager1>() {
                    public void success(AuxFieldGroupManager1 result) {
                        manager = result;
                        setState(DISPLAY);
                        fieldTable.clearExceptions();
                        valueTable.clearExceptions();
                        clearErrors();
                        clearStatus();
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

            service.fetchById(id, fetchByIdCall);
        }
    }

    // public void showErrors(ValidationErrorsList errors) {
    // ArrayList<Exception> formErrors;
    // FieldErrorException flde;
    //
    // fieldTable.clearExceptions();
    // valueTable.clearExceptions();
    // clearErrors();
    // formErrors = new ArrayList<Exception>();
    //
    // for (Exception ex : errors.getErrorList()) {
    // if (ex instanceof FormErrorException) {
    // formErrors.add((FormErrorException)ex);
    // } else if (ex instanceof FieldErrorException) {
    // // convert all the field errors to form errors
    // flde = (FieldErrorException)ex;
    // formErrors.add(new FormErrorException(flde.getMessage()));
    // } else {
    // formErrors.add(new FormErrorException(ex.getMessage()));
    // }
    // }
    //
    // if (formErrors.size() == 0)
    // setError(Messages.get().correctErrors());
    // else if (formErrors.size() == 1)
    // setError(formErrors.get(0).getMessage());
    // else {
    // setError("(Error 1 of " + formErrors.size() + ") " +
    // formErrors.get(0).getMessage());
    // window.setMessagePopup(formErrors, "ErrorPanel");
    // }
    // }

    private ArrayList<Row> getAuxFieldModel() {
        int i;
        AuxFieldViewDO data;
        ArrayList<Row> model;
        Row row;

        model = new ArrayList<Row>();
        if (manager == null)
            return model;

        try {
            for (i = 0; i < manager.field.count(); i++ ) {
                data = manager.field.get(i);
                row = new Row(8);
                row.setCell(0, new AutoCompleteValue(data.getAnalyteId(), data.getAnalyteName()));
                if (data.getMethodId() != null)
                    row.setCell(1, new AutoCompleteValue(data.getMethodId(), data.getMethodName()));
                row.setCell(2, data.getUnitOfMeasureId());
                row.setCell(3, data.getIsActive());
                row.setCell(4, data.getIsRequired());
                row.setCell(5, data.getIsReportable());
                row.setCell(6, data.getDescription());
                row.setCell(7, data.getScriptletId());
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }

    private ArrayList<Row> getAuxFieldValueModel(AuxFieldViewDO field) {
        int i;
        AuxFieldValueViewDO data;
        ArrayList<Row> model;
        Row row;
        String value;

        model = new ArrayList<Row>();
        if (field == null)
            return model;

        try {
            for (i = 0; i < manager.value.count(field); i++ ) {
                data = manager.value.get(field, i);
                // either show them the value or the dictionary entry
                value = data.getValue();
                if (data.getDictionary() != null)
                    value = data.getDictionary();

                row = new Row(2);
                row.setCell(0, data.getTypeId());
                row.setCell(1, value);

                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }

    private IdNameVO getDictionary(String entry) {
        ArrayList<IdNameVO> list;
        Query query;
        QueryData field;

        entry = DataBaseUtil.trim(entry);
        if (entry == null)
            return null;

        query = new Query();
        field = new QueryData();
        field.setKey(CategoryMeta.getDictionaryEntry());
        field.setType(QueryData.Type.STRING);
        field.setQuery(entry);
        query.setFields(field);

        field = new QueryData();
        field.setKey(CategoryMeta.getIsSystem());
        field.setType(QueryData.Type.STRING);
        field.setQuery("N");
        query.setFields(field);

        try {
            list = DictionaryService1Impl.INSTANCE.fetchByEntry(query);
            if (list.size() == 1)
                return list.get(0);
            else if (list.size() > 1)
                showDictionary(entry, list);
        } catch (NotFoundException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
        return null;
    }

    private void validateValue(AuxFieldValueViewDO data, String value) throws Exception {
        IdNameVO dict;

        if (value == null)
            return;

        try {
            if (Constants.dictionary().AUX_DICTIONARY.equals(data.getTypeId())) {
                dict = getDictionary((String)value);
                if (dict != null) {
                    data.setValue(dict.getId().toString());
                    data.setDictionary(dict.getName());
                } else {
                    data.setDictionary(null);
                    throw new Exception(Messages.get().invalidValueException());
                }
            } else if (Constants.dictionary().AUX_NUMERIC.equals(data.getTypeId())) {
                rangeNumeric.setRange((String)value);
                data.setValue(rangeNumeric.toString());
            } else if (Constants.dictionary().AUX_DEFAULT.equals(data.getTypeId())) {
                data.setValue((String)value);
            } else if (DataBaseUtil.trim(value) != null) {
                throw new Exception(Messages.get().valuePresentForTypeException());
            }
        } catch (Exception e) {
            data.setValue(null);
            data.setDictionary(null);
            throw e;
        }
    }

    private void showDictionary(String entry, ArrayList<IdNameVO> list) {
        ModalWindow modal;

        if (dictLookup == null) {
            try {
                dictLookup = new DictionaryLookupScreenUI();
            } catch (Exception e) {
                e.printStackTrace();
                Window.alert("DictionaryLookup Error: " + e.getMessage());
                return;
            }

            dictLookup.addActionHandler(new ActionHandler<DictionaryLookupScreenUI.Action>() {
                public void onAction(ActionEvent<DictionaryLookupScreenUI.Action> event) {
                    int r, fr;
                    IdNameVO entry;
                    AuxFieldValueViewDO data;
                    ArrayList<IdNameVO> list;

                    if (event.getAction() == DictionaryLookupScreenUI.Action.OK) {
                        list = (ArrayList<IdNameVO>)event.getData();
                        if (list != null) {
                            r = valueTable.getSelectedRow();
                            fr = fieldTable.getSelectedRow();
                            if (r == -1) {
                                setError(Messages.get().aux_noSelectedRow());
                                return;
                            }
                            entry = list.get(0);
                            try {
                                data = manager.value.get(manager.field.get(fr), r);
                                data.setValue(entry.getId().toString());
                                data.setDictionary(entry.getName());
                                data.setTypeId(Constants.dictionary().AUX_DICTIONARY);
                                valueTable.setValueAt(r, 0, Constants.dictionary().AUX_DICTIONARY);
                                valueTable.setValueAt(r, 1, data.getDictionary());
                                valueTable.clearExceptions(r, 1);
                            } catch (Exception e) {
                                e.printStackTrace();
                                valueTable.setValueAt(r, 1, "");
                                Window.alert("DictionaryLookup Error: " + e.getMessage());
                            }
                        }
                    }
                }
            });
        }
        modal = new ModalWindow();
        modal.setName(Messages.get().chooseDictEntry());
        modal.setCSS(UIResources.INSTANCE.popupWindow());
        modal.setContent(dictLookup);
        modal.setSize("515px", "390px");
        dictLookup.setWindow(modal);
        dictLookup.setState(DEFAULT);
        if (list != null) {
            dictLookup.clearFields();
            dictLookup.setQueryResult(entry, list);
        } else if (entry != null) {
            dictLookup.clearFields();
            dictLookup.executeQuery(entry);
        }
    }

    private void getAnalyteMatches(String match) {
        Item<Integer> row;
        ArrayList<AnalyteDO> list;
        ArrayList<Item<Integer>> model;

        setBusy();
        try {
            list = AnalyteService1Impl.INSTANCE.fetchByName(QueryFieldUtil.parseAutocomplete(match));
            model = new ArrayList<Item<Integer>>();
            for (AnalyteDO data : list) {
                row = new Item<Integer>(1);

                row.setKey(data.getId());
                row.setData(data);
                row.setCell(0, data.getName());

                model.add(row);
            }
            analyte.showAutoMatches(model);
        } catch (Throwable e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        clearStatus();
    }

    private void getMethodMatches(String match) {
        Item<Integer> row;
        ArrayList<MethodDO> list;
        ArrayList<Item<Integer>> model;

        setBusy();
        try {
            list = MethodService.get().fetchActiveByName(QueryFieldUtil.parseAutocomplete(match));
            model = new ArrayList<Item<Integer>>();
            for (MethodDO data : list) {
                row = new Item<Integer>(1);

                row.setKey(data.getId());
                row.setData(data);
                row.setCell(0, data.getName());

                model.add(row);
            }
            method.showAutoMatches(model);
        } catch (Throwable e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        clearStatus();
    }
}
