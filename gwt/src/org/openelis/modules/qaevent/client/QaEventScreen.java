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

package org.openelis.modules.qaevent.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.UserCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.QaEventVO;
import org.openelis.domain.QaEventViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.TestMethodVO;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.PermissionException;
import org.openelis.gwt.common.ModulePermission;
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
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AutoComplete;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.CheckBox;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextArea;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.meta.QaEventMeta;
import org.openelis.modules.history.client.HistoryScreen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class QaEventScreen extends Screen {
    private QaEventViewDO         data;
    private ModulePermission      userPermission;

    private TextBox               name, description, reportingSequence, methodName;
    private CheckBox              isBillable;
    private AppButton             queryButton, previousButton, nextButton, addButton, 
                                  updateButton, commitButton, abortButton;
    protected MenuItem            duplicate, history;
    private Dropdown<Integer>     typeId;
    private AutoComplete<Integer> testName;
    private TextArea              reportingText;

    private ButtonGroup           atoz;
    private ScreenNavigator       nav;
    
    private ScreenService         testService;

    public QaEventScreen() throws Exception {
        super((ScreenDefInt)GWT.create(QaEventDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.qaevent.server.QaEventService");
        testService = new ScreenService("controller?service=org.openelis.modules.test.server.TestService");

        userPermission = UserCache.getPermission().getModule("qaevent");
        if (userPermission == null)
            throw new PermissionException("screenPermException", "QA Event Screen");

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                postConstructor();
            }
        });
    }

    /**
     * This method is called to set the initial state of widgets after the
     * screen is attached to the browser. It is usually called in deferred command.
     */
    private void postConstructor() {
        data = new QaEventViewDO();

        // Setup link between Screen and widget Handlers
        initialize();
        setState(State.DEFAULT);
        initializeDropdowns();

        DataChangeEvent.fire(this);
    }
    
    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        //
        // button panel buttons
        //
        queryButton = (AppButton)def.getWidget("query");
        addScreenHandler(queryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                query();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (event.getState() == State.QUERY)
                    queryButton.setState(ButtonState.LOCK_PRESSED);
                queryButton.enable(EnumSet.of(State.DEFAULT, State.DISPLAY)
                                          .contains(event.getState()) &&
                                   userPermission.hasSelectPermission());
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
        
        duplicate = (MenuItem)def.getWidget("duplicateRecord");
        addScreenHandler(duplicate, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                duplicate();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                duplicate.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });
        
        history = (MenuItem)def.getWidget("qaeventHistory");
        addScreenHandler(history, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                history();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                history.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        //
        // screen fields
        //
        name = (TextBox)def.getWidget(QaEventMeta.getName());
        addScreenHandler(name, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                name.setValue(data.getName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                name.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                name.setQueryMode(event.getState() == State.QUERY);
            }
        });

        description = (TextBox)def.getWidget(QaEventMeta.getDescription());
        addScreenHandler(description, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                description.setValue(data.getDescription());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setDescription(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                description.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                description.setQueryMode(event.getState() == State.QUERY);
            }
        });

        typeId = (Dropdown)def.getWidget(QaEventMeta.getTypeId());
        addScreenHandler(typeId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                typeId.setSelection(data.getTypeId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                data.setTypeId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                typeId.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                typeId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        testName = (AutoComplete)def.getWidget(QaEventMeta.getTestName());
        addScreenHandler(testName, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                testName.setSelection(data.getTestId(), data.getTestName());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                TableDataRow row;
                String method;
                
                row = testName.getSelection();
                data.setTestId(event.getValue());
                data.setTestName(testName.getTextBoxDisplay());
                
                if (row != null) {
                    method = (String)row.cells.get(1).getValue();                                    
                    data.setMethodName(method);
                    methodName.setText(method);
                } else {                    
                    data.setMethodName(null);
                    methodName.setText(null);
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                testName.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                testName.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        testName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                TestMethodVO data;
                ArrayList<TestMethodVO> list;
                ArrayList<TableDataRow> model;

                window.setBusy();
                try {
                    list = testService.callList("fetchByName", QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<TableDataRow>();
                    for (int i = 0; i < list.size(); i++ ) {
                        data = list.get(i);
                        model.add(new TableDataRow(data.getTestId(), data.getTestName(),
                                                   data.getMethodName(), data.getTestDescription()));
                    }
                    testName.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
                window.clearStatus();
            }
        });
        
        methodName = (TextBox)def.getWidget("method");
        addScreenHandler(methodName, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                methodName.setValue(data.getMethodName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                // this field will not be edited
            }

            public void onStateChange(StateChangeEvent<State> event) {
                methodName.enable(false);
            }
        });

        isBillable = (CheckBox)def.getWidget(QaEventMeta.getIsBillable());
        addScreenHandler(isBillable, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isBillable.setValue(data.getIsBillable());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setIsBillable(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isBillable.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                isBillable.setQueryMode(event.getState() == State.QUERY);
            }
        });

        reportingSequence = (TextBox)def.getWidget(QaEventMeta.getReportingSequence());
        addScreenHandler(reportingSequence, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                reportingSequence.setValue(data.getReportingSequence());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                data.setReportingSequence(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                reportingSequence.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                reportingSequence.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        reportingText = (TextArea)def.getWidget(QaEventMeta.getReportingText());
        addScreenHandler(reportingText, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                reportingText.setValue(data.getReportingText());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                data.setReportingText(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                reportingText.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
            }
        });

        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator(def) {
            public void executeQuery(final Query query) {
                window.setBusy(consts.get("querying"));

                service.callList("query", query, new AsyncCallback<ArrayList<QaEventVO>>() {
                    public void onSuccess(ArrayList<QaEventVO> result) {
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
                            Window.alert("Error: QAEvent call query failed; " +
                                         error.getMessage());
                            window.setError(consts.get("queryFailed"));
                        }
                    }
                });
            }

            public boolean fetch(RPC entry) {
                return fetchById( (entry == null) ? null : ((IdNameVO)entry).getId());
            }

            public ArrayList<TableDataRow> getModel() {
                ArrayList<IdNameVO> result;
                ArrayList<TableDataRow> model;

                model = null;
                result = nav.getQueryResult();
                if (result != null) {
                    model = new ArrayList<TableDataRow>();
                    for (IdNameVO entry : result)
                        model.add(new TableDataRow(entry.getId(), entry.getName(), entry.getDescription()));
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
                field.key = QaEventMeta.getName();
                field.query = ((AppButton)event.getSource()).getAction();
                field.type = QueryData.Type.STRING;

                query = new Query();
                query.setFields(field);
                nav.setQuery(query);
            }
        });
        
        window.addBeforeClosedHandler(new BeforeCloseHandler<ScreenWindow>() {
            public void onBeforeClosed(BeforeCloseEvent<ScreenWindow> event) {                
                if (EnumSet.of(State.ADD, State.UPDATE).contains(state)) {
                    event.cancel();
                    window.setError(consts.get("mustCommitOrAbort"));
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
        list = CategoryCache.getBySystemName("qaevent_type");
        for (DictionaryDO d : list) {
            row = new TableDataRow(d.getId(), d.getEntry()); 
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);
        }
        
        typeId.setModel(model);
    }

    /*
     * basic button methods
     */
    protected void query() {
        data = new QaEventViewDO();
        
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
        data = new QaEventViewDO();
        data.setIsBillable("N");

        setState(State.ADD);
        DataChangeEvent.fire(this);

        setFocus(name);
        window.setDone(consts.get("enterInformationPressCommit"));
    }

    protected void update() {
        window.setBusy(consts.get("lockForUpdate"));

        try {
            data = service.call("fetchForUpdate", data.getId());

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
                data = service.call("add", data);

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
                data = service.call("update", data);

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
            fetchById(null);
            window.setDone(consts.get("queryAborted"));
        } else if (state == State.ADD) {
            fetchById(null);
            window.setDone(consts.get("addAborted"));
        } else if (state == State.UPDATE) {
            try {
                data = service.call("abortUpdate", data.getId());
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
            } catch (Exception e) {
                Window.alert(e.getMessage());
                fetchById(null);
            }
            window.setDone(consts.get("updateAborted"));
        } else {
            window.clearStatus();
        }
    }
    
    protected void duplicate() {
        try {
            data = service.call("fetchById", data.getId());
            data.setId(null);
            setState(State.ADD);
            DataChangeEvent.fire(this);
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
    }
    
    protected void history() {
        IdNameVO hist;
        
        hist = new IdNameVO(data.getId(), data.getName());
        HistoryScreen.showHistory(consts.get("qaeventHistory"), ReferenceTable.QAEVENT, hist);
    }

    protected boolean fetchById(Integer id) {
        if (id == null) {
            data = new QaEventViewDO();
            setState(State.DEFAULT);
        } else {
            window.setBusy(consts.get("fetching"));
            try {
                data = service.call("fetchById", id);
                setState(State.DISPLAY);
            } catch (NotFoundException e) {
                fetchById(null);
                window.setDone(consts.get("noRecordsFound"));
                return false;
            } catch (Exception e) {
                fetchById(null);
                e.printStackTrace();
                Window.alert(consts.get("fetchFailed") + e.getMessage());
                return false;
            }
        }
        DataChangeEvent.fire(this);
        window.clearStatus();

        return true;
    }
}