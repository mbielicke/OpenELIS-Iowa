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
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.TestMethodVO;
import org.openelis.manager.OrderManager1;
import org.openelis.manager.TestManager1;
import org.openelis.meta.OrderMeta;
import org.openelis.meta.TestMeta;
import org.openelis.modules.test.client.TestService;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.AutoComplete;
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

//    @UiField
//    protected TabLayoutPanel                       tabPanel;

    protected TestScreenUI                         screen;

    protected AsyncCallbackUI<ArrayList<IdNameVO>> queryCall;

    protected AsyncCallbackUI<OrderManager1>       addCall, fetchForUpdateCall, updateCall,
                    fetchByIdCall, unlockCall, duplicateCall;

    public TestScreenUI(WindowInt window) throws Exception {
        setWindow(window);

        userPermission = UserCache.getPermission().getModule("test");
        if (userPermission == null)
            throw new PermissionException(Messages.get().gen_screenPermException("Test Screen"));

        try {
            CategoryCache.getBySystemNames("order_status",
                                           "cost_centers",
                                           "laboratory_location",
                                           "state",
                                           "organization_type",
                                           "country",
                                           "sample_container",
                                           "type_of_sample",
                                           "inventory_store",
                                           "inventory_unit",
                                           "standard_note_type",
                                           "order_recurrence_unit");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            window.close();
        }

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

        logger.fine("Sendout Order Screen Opened");
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        ArrayList<Item<Integer>> model;

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

        addScreenHandler(name, OrderMeta.getOrganizationAttention(), new ScreenHandler<String>() {
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

        addScreenHandler(method, OrderMeta.getOrganizationName(), new ScreenHandler<Integer>() {
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
        // TODO
        // if (duplicateCall == null) {
        // duplicateCall = new AsyncCallbackUI<TestManager1>() {
        // public void success(TestManager1 result) {
        // manager = result;
        // setData();
        // setState(ADD);
        // fireDataChange();
        // }
        //
        // public void failure(Throwable e) {
        // Window.alert(e.getMessage());
        // logger.log(Level.SEVERE, e.getMessage(), e);
        // }
        // };
        // }
        //
        // TestService1.get().duplicate(manager.getTest().getId(),
        // duplicateCall);
    }

    protected void testHistory() {
        // TODO
    }

    protected void testSectionHistory() {
        // TODO
    }

    protected void testSampleTypeHistory() {
        // TODO
    }

    protected void testAnalyteHistory() {
        // TODO
    }

    protected void testResultHistory() {
        // TODO
    }

    protected void testPrepHistory() {
        // TODO
    }

    protected void testReflexHistory() {
        // TODO
    }

    protected void testWorksheetHistory() {
        // TODO
    }

    protected void testWorksheetItemHistory() {
        // TODO
    }

    protected void testWorksheetAnalyteHistory() {
        // TODO
    }

    private void getMethodMatches(String match) {
        Item<Integer> row;
        TestMethodVO data;
        ArrayList<TestMethodVO> list;
        ArrayList<Item<Integer>> model;
        // TODO
        setBusy();
        try {
            list = TestService.get().fetchByName(QueryFieldUtil.parseAutocomplete(match));
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
        // TODO
    }

    private void getMethodFromSelection() {
        // TODO
    }

    /**
     * Sets the latest manager in the tabs
     */
    private void setData() {
        // TODO
    }
}
