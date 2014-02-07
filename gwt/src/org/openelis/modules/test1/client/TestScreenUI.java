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
package org.openelis.modules.test1.client;

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
import org.openelis.cache.SectionCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.SectionViewDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestMethodVO;
import org.openelis.domain.TestPrepViewDO;
import org.openelis.domain.TestReflexViewDO;
import org.openelis.domain.TestSectionViewDO;
import org.openelis.domain.TestTypeOfSampleDO;
import org.openelis.domain.TestWorksheetAnalyteViewDO;
import org.openelis.domain.TestWorksheetItemDO;
import org.openelis.domain.TestWorksheetViewDO;
import org.openelis.manager.TestManager1;
import org.openelis.meta.TestMeta;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.ScreenNavigator;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.ButtonGroup;
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
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class TestScreenUI extends Screen {

    @UiTemplate("TestScreen.ui.xml")
    interface TestScreenUiBinder extends UiBinder<Widget, TestScreenUI> {
    };

    public static final TestScreenUiBinder         uiBinder = GWT.create(TestScreenUiBinder.class);

    protected TestManager1                         manager;

    protected ModulePermission                     userPermission;

    protected ScreenNavigator<IdNameVO>            nav;

    @UiField
    protected ButtonGroup                          atozButtons;

    @UiField
    protected Table                                atozTable;

    @UiField
    protected Button                               query, previous, next, add, update, commit,
                    abort, optionsButton, atozNext, atozPrev;

    @UiField
    protected Menu                                 optionsMenu, historyMenu;

    @UiField
    protected MenuItem                             duplicate, testHistory, testSectionHistory,
                    testSampleTypeHistory, testAnalyteHistory, testResultHistory, testPrepHistory,
                    testReflexHistory, testWorksheetHistory, testWorksheetItemHistory,
                    testWorksheetAnalyteHistory;

    @UiField
    protected TextBox<Integer>                     id;

    @UiField
    protected TextBox<String>                      name;

    @UiField
    protected AutoComplete                         method;

    @UiField
    protected TabLayoutPanel                       tabPanel;

    @UiField(provided = true)
    protected DetailsTabUI                         detailsTab;

    protected TestScreenUI                         screen;

    protected AsyncCallbackUI<ArrayList<IdNameVO>> queryCall;

    protected AsyncCallbackUI<TestManager1>        addCall, fetchForUpdateCall, updateCall,
                    fetchByIdCall, unlockCall, duplicateCall;
    
    // @formatter:off
    protected TestManager1.Load                    elements[] = {
                                                                 TestManager1.Load.SECTION,
                                                                 TestManager1.Load.TYPE,
                                                                 TestManager1.Load.ANALYTE,
                                                                 TestManager1.Load.RESULT,
                                                                 TestManager1.Load.PREP,
                                                                 TestManager1.Load.REFLEX,
                                                                 TestManager1.Load.WORKSHEET
                                                                 };
    // @formatter:on

    public TestScreenUI(WindowInt window) throws Exception {
        setWindow(window);

        userPermission = UserCache.getPermission().getModule("test");
        if (userPermission == null)
            throw new PermissionException(Messages.get().gen_screenPermException("Test Screen"));

        try {
            CategoryCache.getBySystemNames("test_format",
                                           "test_reporting_method",
                                           "test_sorting_method",
                                           "test_revision_method",
                                           "test_section_flags",
                                           "type_of_sample",
                                           "unit_of_measure",
                                           "test_analyte_type",
                                           "test_result_type",
                                           "test_result_flags",
                                           "rounding_method",
                                           "test_reflex_flags",
                                           "test_res_type_dictionary",
                                           "test_worksheet_analyte_flags",
                                           "test_worksheet_item_type",
                                           "test_worksheet_format");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }

        detailsTab = new DetailsTabUI(this);
        // organizationTab = new OrganizationTabUI(this);
        // testTab = new TestTabUI(this);
        // containerTab = new ContainerTabUI(this);
        // itemTab = new SendoutOrderItemTabUI(this);
        // shippingNotesTab = new ShippingNotesTabUI(this);
        // customerNotesTab = new CustomerNotesTabUI(this);
        // internalNotesTab = new InternalNotesTabUI(this);
        // sampleNotesTab = new SampleNotesTabUI(this);
        // recurrenceTab = new RecurrenceTabUI(this);
        // fillTab = new SendoutOrderFillTabUI(this);

        initWidget(uiBinder.createAndBindUi(this));

        manager = null;

        initialize();
        setData();
        setState(DEFAULT);
        fireDataChange();

        logger.fine("Test Screen Opened");
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        screen = this;

        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator<IdNameVO>(atozTable, atozNext, atozPrev) {
            public void executeQuery(final Query query) {
                QueryData field;

                setBusy(Messages.get().gen_querying());

                if (queryCall == null) {
                    queryCall = new AsyncCallbackUI<ArrayList<IdNameVO>>() {
                        public void success(ArrayList<IdNameVO> result) {
                            clearStatus();
                            setQueryResult(result);
                        }

                        public void notFound() {
                            setQueryResult(null);
                            setState(DEFAULT);
                            setDone(Messages.get().gen_noRecordsFound());
                        }

                        public void lastPage() {
                            setQueryResult(null);
                            setError(Messages.get().gen_noMoreRecordInDir());
                        }

                        public void failure(Throwable error) {
                            setQueryResult(null);
                            Window.alert("Error: Test call query failed; " + error.getMessage());
                            setError(Messages.get().gen_queryFailed());
                        }
                    };
                }
                // TODO
                // field = new QueryData(TestMeta.getName(),
                // QueryData.Type.STRING, "");
                // query.setFields(field);
                query.setRowsPerPage(26);
                TestService1.get().query(query, queryCall);
            }

            public boolean fetch(IdNameVO entry) {
                fetchById( (entry == null) ? null : entry.getId());
                return true;
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
                        row = new Item<Integer>(2);
                        row.setKey(entry.getId());
                        row.setCell(0, entry.getId());
                        row.setCell(1, entry.getName());
                        model.add(row);
                    }
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
                field.setKey(TestMeta.getId());
                field.setQuery( ((Button)event.getSource()).getAction());
                field.setType(QueryData.Type.INTEGER);

                query = new Query();
                query.setFields(field);
                nav.setQuery(query);
            }
        });

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

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                optionsMenu.setEnabled(isState(DISPLAY));
                optionsButton.setEnabled(isState(DISPLAY));
                historyMenu.setEnabled(isState(DISPLAY));
            }
        });

        /*
         * option menu items
         */
        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                duplicate.setEnabled(isState(State.DISPLAY) && userPermission.hasAddPermission());
            }
        });
        duplicate.addCommand(new Command() {
            public void execute() {
                duplicate();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                testHistory.setEnabled(isState(DISPLAY));
            }
        });
        testHistory.addCommand(new Command() {
            @Override
            public void execute() {
                testHistory();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                testSectionHistory.setEnabled(isState(DISPLAY));
            }
        });
        testSectionHistory.addCommand(new Command() {
            @Override
            public void execute() {
                testSectionHistory();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                testSampleTypeHistory.setEnabled(isState(DISPLAY));
            }
        });
        testSampleTypeHistory.addCommand(new Command() {
            @Override
            public void execute() {
                testSampleTypeHistory();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                testAnalyteHistory.setEnabled(isState(DISPLAY));
            }
        });
        testAnalyteHistory.addCommand(new Command() {
            @Override
            public void execute() {
                testAnalyteHistory();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                testResultHistory.setEnabled(isState(DISPLAY));
            }
        });
        testResultHistory.addCommand(new Command() {
            @Override
            public void execute() {
                testResultHistory();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                testPrepHistory.setEnabled(isState(DISPLAY));
            }
        });
        testPrepHistory.addCommand(new Command() {
            @Override
            public void execute() {
                testPrepHistory();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                testReflexHistory.setEnabled(isState(DISPLAY));
            }
        });
        testReflexHistory.addCommand(new Command() {
            @Override
            public void execute() {
                testReflexHistory();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                testWorksheetHistory.setEnabled(isState(DISPLAY));
            }
        });
        testWorksheetHistory.addCommand(new Command() {
            @Override
            public void execute() {
                testWorksheetHistory();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                testWorksheetItemHistory.setEnabled(isState(DISPLAY));
            }
        });
        testWorksheetItemHistory.addCommand(new Command() {
            @Override
            public void execute() {
                testWorksheetItemHistory();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                testWorksheetAnalyteHistory.setEnabled(isState(DISPLAY));
            }
        });
        testWorksheetAnalyteHistory.addCommand(new Command() {
            @Override
            public void execute() {
                testWorksheetAnalyteHistory();
            }
        });

        //
        // screen fields
        //
        addScreenHandler(id, TestMeta.getId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                id.setValue(getId());
            }

            public void onStateChange(StateChangeEvent event) {
                id.setEnabled(isState(QUERY));
                id.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? name : method;
            }
        });

        addScreenHandler(name, TestMeta.getName(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                name.setValue(getName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setName(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                name.setEnabled(isState(QUERY, ADD, UPDATE));
                name.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? method : id;
            }
        });

        addScreenHandler(method, TestMeta.getMethodName(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                setMethodSelection();
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                getMethodFromSelection();
            }

            public void onStateChange(StateChangeEvent event) {
                method.setEnabled(isState(QUERY, ADD, UPDATE));
                method.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? id : name;
            }
        });

        method.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                getMethodMatches(event.getMatch());
            }
        });
    }

    protected void duplicate() {
        if (duplicateCall == null) {
            duplicateCall = new AsyncCallbackUI<TestManager1>() {
                public void success(TestManager1 result) {
                    manager = result;
                    setData();
                    setState(ADD);
                    fireDataChange();
                }

                public void failure(Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            };
        }

        TestService1.get().duplicate(manager.getTest().getId(), duplicateCall);
    }

    protected void testHistory() {
        IdNameVO hist;

        hist = new IdNameVO(manager.getTest().getId(), manager.getTest().getName());
        HistoryScreen.showHistory(Messages.get().testHistory(), Constants.table().TEST, hist);
    }

    protected void testSectionHistory() {
        int i, count;
        IdNameVO refVoList[];
        TestSectionViewDO data;
        SectionViewDO sect;
        String section;

        try {
            count = manager.section.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = manager.section.get(i);
                sect = SectionCache.getById(data.getSectionId());
                if (sect != null)
                    section = sect.getName();
                else
                    section = data.getSectionId().toString();

                refVoList[i] = new IdNameVO(data.getId(), section);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(Messages.get().testSectionHistory(),
                                  Constants.table().TEST_SECTION,
                                  refVoList);
    }

    protected void testSampleTypeHistory() {
        int i, count;
        IdNameVO refVoList[];
        TestTypeOfSampleDO data;
        DictionaryDO dict;
        String entry;
        Integer typeId;

        try {
            count = manager.type.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = manager.type.get(i);
                typeId = data.getTypeOfSampleId();
                dict = DictionaryCache.getById(typeId);
                if (dict != null)
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

        HistoryScreen.showHistory(Messages.get().testSampleTypeHistory(),
                                  Constants.table().TEST_TYPE_OF_SAMPLE,
                                  refVoList);
    }

    protected void testAnalyteHistory() {
        int i;
        IdNameVO refVoList[];
        TestAnalyteViewDO data;
        ArrayList<IdNameVO> list;

        try {
            list = new ArrayList<IdNameVO>();
            for (i = 0; i < manager.analyte.count(); i++ ) {
                data = manager.analyte.get(i);
                list.add(new IdNameVO(data.getId(), data.getAnalyteName()));
            }

            refVoList = new IdNameVO[list.size()];
            for (i = 0; i < list.size(); i++ ) {
                refVoList[i] = list.get(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(Messages.get().testAnalyteHistory(),
                                  Constants.table().TEST_ANALYTE,
                                  refVoList);
    }

    protected void testResultHistory() {
        // TODO
    }

    protected void testPrepHistory() {
        int i, count;
        IdNameVO refVoList[];
        TestPrepViewDO data;

        try {
            count = manager.prep.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = manager.prep.get(i);
                refVoList[i] = new IdNameVO(data.getId(), data.getPrepTestName() + ", " +
                                                          data.getMethodName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(Messages.get().testPrepHistory(),
                                  Constants.table().TEST_PREP,
                                  refVoList);
    }

    protected void testReflexHistory() {
        int i, count;
        IdNameVO refVoList[];
        TestReflexViewDO data;

        try {
            count = manager.reflex.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = manager.reflex.get(i);
                refVoList[i] = new IdNameVO(data.getId(), data.getAddTestName() + ", " +
                                                          data.getAddMethodName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(Messages.get().testReflexHistory(),
                                  Constants.table().TEST_REFLEX,
                                  refVoList);
    }

    protected void testWorksheetHistory() {
        IdNameVO hist;
        TestWorksheetViewDO data;
        Integer id;

        try {
            data = manager.getWorksheet();
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

        id = data.getId();
        if (id == null)
            id = -1;
        hist = new IdNameVO(id, Messages.get().worksheet());
        HistoryScreen.showHistory(Messages.get().testWorksheetHistory(),
                                  Constants.table().TEST_WORKSHEET,
                                  hist);
    }

    protected void testWorksheetItemHistory() {
        int i, count;
        IdNameVO refVoList[];
        TestWorksheetItemDO data;
        String label;
        Integer position;

        try {
            count = manager.item.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = manager.item.get(i);
                position = data.getPosition();
                if (position != null)
                    label = position.toString();
                else
                    label = "";
                refVoList[i] = new IdNameVO(data.getId(), label);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(Messages.get().testWorksheetItemHistory(),
                                  Constants.table().TEST_WORKSHEET_ITEM,
                                  refVoList);
    }

    protected void testWorksheetAnalyteHistory() {
        int i, count;
        IdNameVO refVoList[];
        TestWorksheetAnalyteViewDO data;

        try {
            count = manager.worksheetAnalyte.count();
            refVoList = new IdNameVO[count];
            for (i = 0; i < count; i++ ) {
                data = manager.worksheetAnalyte.get(i);
                refVoList[i] = new IdNameVO(data.getId(), data.getAnalyteName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }

        HistoryScreen.showHistory(Messages.get().testWorksheetAnalyteHistory(),
                                  Constants.table().TEST_WORKSHEET_ANALYTE,
                                  refVoList);
    }

    private void getMethodMatches(String match) {
        Item<Integer> row;
        TestMethodVO data;
        ArrayList<TestMethodVO> list;
        ArrayList<Item<Integer>> model;
        // TODO
        setBusy();
        try {
            list = TestService1.get().fetchByName(QueryFieldUtil.parseAutocomplete(match));
            model = new ArrayList<Item<Integer>>();
            for (int i = 0; i < list.size(); i++ ) {
                row = new Item<Integer>(1);
                data = list.get(i);

                row.setKey(data.getMethodId());
                row.setData(data);
                row.setCell(0, data.getMethodName());

                model.add(row);
            }
            method.showAutoMatches(model);
        } catch (Throwable e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        clearStatus();
    }

    /*
     * getters and setters
     */
    private Integer getId() {
        if (manager == null)
            return null;
        return manager.getTest().getId();
    }

    private String getName() {
        if (manager == null)
            return null;
        return manager.getTest().getName();
    }

    private void setName(String name) {
        manager.getTest().setName(name);
    }

    private void setMethodSelection() {
        if (manager == null || manager.getTest() == null)
            method.setValue(null, "");
        else
            method.setValue(manager.getTest().getMethodId(), manager.getTest().getMethodName());
    }

    private void getMethodFromSelection() {
        AutoCompleteValue row;

        row = method.getValue();
        if (row == null || row.getId() == null) {
            manager.getTest().setMethodId(null);
            manager.getTest().setMethodName(null);
            method.setValue(null, "");
        } else {
            manager.getTest().setMethodId(row.getId());
            manager.getTest().setMethodName(method.getDisplay());
        }
    }

    /**
     * Sets the latest manager in the tabs
     */
    private void setData() {
        // TODO
        detailsTab.setData(manager);
    }

    private void fetchById(Integer id) {
        if (id == null) {
            manager = null;
            setData();
            setState(DEFAULT);
            fireDataChange();
            clearStatus();
        } else {
            setBusy(Messages.get().gen_fetching());
            if (fetchByIdCall == null) {
                fetchByIdCall = new AsyncCallbackUI<TestManager1>() {
                    public void success(TestManager1 result) {
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

            TestService1.get().fetchById(id, elements, fetchByIdCall);
        }
    }
}
