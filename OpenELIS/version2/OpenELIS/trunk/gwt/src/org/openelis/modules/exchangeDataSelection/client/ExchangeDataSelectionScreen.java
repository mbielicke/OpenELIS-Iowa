/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.modules.exchangeDataSelection.client;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.UserCache;
import org.openelis.domain.AddressDO;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.EventLogDO;
import org.openelis.domain.ExchangeCriteriaViewDO;
import org.openelis.domain.ExchangeProfileDO;
import org.openelis.domain.IdAccessionVO;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.OrganizationParameterDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.TestMethodVO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.LocalizedException;
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
import org.openelis.gwt.screen.Screen.State;
import org.openelis.gwt.services.ScreenService;
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
import org.openelis.gwt.widget.TextArea;
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
import org.openelis.manager.ExchangeCriteriaManager;
import org.openelis.manager.ExchangeProfileManager;
import org.openelis.manager.SampleManager;
import org.openelis.meta.EventLogMeta;
import org.openelis.meta.ExchangeCriteriaMeta;
import org.openelis.meta.OrganizationMeta;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.history.client.HistoryScreen;
import org.openelis.modules.report.dataExchange.client.DataExchangeReportScreen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ExchangeDataSelectionScreen extends Screen {
    private ExchangeCriteriaManager     manager;
    private ModulePermission            userPermission;

    private ButtonGroup                 atoz;
    private ScreenNavigator             nav;
    
    private TextArea                    queryResults;
    private Dropdown<Integer>           environment;
    private AutoComplete<Integer>       reportToOrganizationName;
    private TextBox                     name, destinationUri;
    private CheckBox                    isAllAnalysesIncluded;
    private AppButton                   queryButton, previousButton, nextButton, 
                                        addButton, updateButton, deleteButton, commitButton,
                                        abortButton, addProfileButton, removeProfileButton,
                                        addReportToButton, removeReportToButton,
                                        searchButton, exportToLocationButton,
                                        lastRunPrevButton, lastRunNextButton;
    protected MenuItem                  duplicate, criteriaHistory, profileHistory;
    private TableWidget                 profileTable, lastRunTable, reportToTable;    
    private Dropdown<Integer>           test, profileVersion, testResultFlags;
    private Dropdown<String>            domain;
    private CalendarLookUp              releasedDate;
    private int                         pageNum;
    private ExchangeDataSelectionScreen screen;
    private DataExchangeReportScreen    dataExchangeReportScreen; 
    
    private ScreenService               testService, organizationService, eventLogService;

    public ExchangeDataSelectionScreen() throws Exception {
        super((ScreenDefInt)GWT.create(ExchangeDataSelectionDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.exchangeDataSelection.server.ExchangeDataSelectionService");
        testService = new ScreenService("controller?service=org.openelis.modules.test.server.TestService");
        organizationService = new ScreenService("controller?service=org.openelis.modules.organization.server.OrganizationService");
        eventLogService = new ScreenService("controller?service=org.openelis.modules.eventLog.server.EventLogService");

        userPermission = UserCache.getPermission().getModule("exchangedataselection");
        if (userPermission == null)
            throw new PermissionException("screenPermException", "Exchange Data Selection Screen");

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
        manager = ExchangeCriteriaManager.getInstance();
        
        try {
            CategoryCache.getBySystemNames("exchange_environment", "exchange_profile");
        } catch (Exception e) {
            Window.alert("ExchangeDataSelectionScreen: missing dictionary entry; " + e.getMessage());
            window.close();
        }
         
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
        
        deleteButton = (AppButton)def.getWidget("delete");
        addScreenHandler(deleteButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                delete();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                deleteButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()) &&
                                    userPermission.hasDeletePermission());
                if (event.getState() == State.DELETE)
                    deleteButton.setState(ButtonState.LOCK_PRESSED);
            }
        });

        commitButton = (AppButton)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE, State.DELETE)
                                    .contains(event.getState()));
            }
        });

        abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE, State.DELETE)
                                          .contains(event.getState()));
            }
        });
        
        duplicate = (MenuItem)def.getWidget("duplicateRecord");
        addScreenHandler(duplicate, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                duplicate();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                duplicate.enable(EnumSet.of(State.DISPLAY).contains(event.getState()) &&
                                 userPermission.hasAddPermission());
            }
        });
        
        criteriaHistory = (MenuItem)def.getWidget("exchangeCriteriaHistory");
        addScreenHandler(criteriaHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                criteriaHistory();
            }

            private void criteriaHistory() {
                IdNameVO hist;

                hist = new IdNameVO(manager.getExchangeCriteria().getId(), manager.getExchangeCriteria().getName());
                HistoryScreen.showHistory(consts.get("exchangeCriteriaHistory"), ReferenceTable.EXCHANGE_CRITERIA, hist);
                
            }

            public void onStateChange(StateChangeEvent<State> event) {
                criteriaHistory.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        profileHistory = (MenuItem)def.getWidget("exchangeProfileHistory");
        addScreenHandler(profileHistory, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                profileHistory();
            }

            private void profileHistory() {
                int count;
                IdNameVO list[];
                ExchangeProfileManager man;
                ExchangeProfileDO data;
                DictionaryDO dict;

                try {
                    man = manager.getProfiles();
                    count = man.count();
                    list = new IdNameVO[count];
                    for (int i = 0; i < count; i++ ) {
                        data = man.getProfileAt(i);
                        dict = DictionaryCache.getById(data.getProfileId());
                        list[i] = new IdNameVO(data.getId(), dict.getEntry());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                    return;
                }

                HistoryScreen.showHistory(consts.get("exchangeProfileHistory"), ReferenceTable.EXCHANGE_PROFILE, list);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                profileHistory.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        name = (TextBox)def.getWidget(ExchangeCriteriaMeta.getName());
        addScreenHandler(name, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                name.setValue(manager.getExchangeCriteria().getName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getExchangeCriteria().setName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                name.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                name.setQueryMode(event.getState() == State.QUERY);
            }
        });

        environment = (Dropdown)def.getWidget(ExchangeCriteriaMeta.getEnvironmentId());
        addScreenHandler(environment, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                environment.setSelection(manager.getExchangeCriteria().getEnvironmentId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getExchangeCriteria().setEnvironmentId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                environment.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                environment.setQueryMode(event.getState() == State.QUERY);
            }
        });

        destinationUri = (TextBox)def.getWidget(ExchangeCriteriaMeta.getDestinationUri());
        addScreenHandler(destinationUri, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                destinationUri.setValue(manager.getExchangeCriteria().getDestinationUri());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getExchangeCriteria().setDestinationUri(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                destinationUri.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE).contains(event.getState()));
                destinationUri.setQueryMode(event.getState() == State.QUERY);
            }
        });
        
        isAllAnalysesIncluded = (CheckBox)def.getWidget(ExchangeCriteriaMeta.getIsAllAnalysesIncluded());
        addScreenHandler(isAllAnalysesIncluded, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isAllAnalysesIncluded.setValue(manager.getExchangeCriteria().getIsAllAnalysesIncluded());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                String val;
                ArrayList<LocalizedException> exceptions;
                
                val = event.getValue();
                manager.getExchangeCriteria().setIsAllAnalysesIncluded(val);
                
                if ("N".equals(val))
                    return;
                           
                exceptions = test.getExceptions();
                if (exceptions == null)
                    return;
                
                /*
                 * this is done so that if the error for not having included at
                 * least one test in the query was added to the dropdown because 
                 * isAllAnalysisIncluded was unchecked, then the error gets removed
                 * on checking isAllAnalysisIncluded and commit doesn't fail in 
                 * the front-end   
                 */
                for (LocalizedException e : exceptions) {
                    if ("noTestForNotIncludeAllAnalysesException".equals(e.getKey())) {
                        exceptions.remove(e);
                        break;
                    }
                }
                test.clearExceptions();
                for (LocalizedException e : exceptions) 
                   test.addException(e);                
            }

            public void onStateChange(StateChangeEvent<State> event) {
                isAllAnalysesIncluded.enable(EnumSet.of(State.QUERY,State.ADD,State.UPDATE).contains(event.getState()));
                isAllAnalysesIncluded.setQueryMode(event.getState() == State.QUERY);
            }
        });

        profileTable = (TableWidget)def.getWidget("profileTable");
        addScreenHandler(profileTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                if (state != State.QUERY)
                    profileTable.load(getProfileTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                boolean enable;
                
                profileTable.enable(true);
                profileTable.setQueryMode(event.getState() == State.QUERY);
                
                enable = EnumSet.of(State.ADD, State.UPDATE).contains(event.getState());
                profileTable.enableDrag(enable);
                profileTable.enableDrop(enable);
            }
        });

        profileTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if (state != State.ADD && state != State.UPDATE)
                    event.cancel();                
            }
        });
        
        profileTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                ExchangeProfileDO data;

                r = event.getRow();
                c = event.getCol();                
                val = profileTable.getObject(r,c);

                try {
                    data = manager.getProfiles().getProfileAt(r);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }
                
                switch (c) {
                    case 0:
                        data.setProfileId((Integer)val);
                        break;
                }
            }
        });
        
        profileTable.addRowAddedHandler(new RowAddedHandler() {
            public void onRowAdded(RowAddedEvent event) {
                try {
                    manager.getProfiles().addProfile(new ExchangeProfileDO());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }
            }
        });

        profileTable.addRowDeletedHandler(new RowDeletedHandler() {
            public void onRowDeleted(RowDeletedEvent event) {
                try {
                    manager.getProfiles().removeProfileAt(event.getIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    return;
                }
            }
        });
        
        profileTable.addRowMovedHandler(new RowMovedHandler() {
            public void onRowMoved(RowMovedEvent event) {
                try {
                    manager.getProfiles().moveProfile(event.getOldIndex(), event.getNewIndex());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        profileTable.enableDrag(true);
        profileTable.enableDrop(true);
        profileTable.addTarget(profileTable);
        
        removeProfileButton = (AppButton)def.getWidget("removeProfileButton");
        addScreenHandler(removeProfileButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;
                
                r = profileTable.getSelectedRow();
                if (r > -1 && profileTable.numRows() > 0)
                    profileTable.deleteRow(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeProfileButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        addProfileButton = (AppButton)def.getWidget("addProfileButton");
        addScreenHandler(addProfileButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n;
                
                profileTable.addRow();
                n = profileTable.numRows() - 1;
                profileTable.selectRow(n);
                profileTable.scrollToSelection();
                profileTable.startEditing(n, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addProfileButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        releasedDate = (CalendarLookUp)def.getWidget(SampleMeta.getAnalysisReleasedDate());
        addScreenHandler(releasedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                releasedDate.setQuery(getQuery(SampleMeta.getAnalysisReleasedDate()));
            }

            public void onStateChange(StateChangeEvent<State> event) {
                releasedDate.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
                /*
                 * query mode is enabled in display mode to make sure that if there
                 * is a range of date to be shown, it get shown correctly
                 */
                releasedDate.setQueryMode(EnumSet.of(State.ADD, State.UPDATE, State.DISPLAY).contains(event.getState()));
            }
        });
        
        domain = (Dropdown)def.getWidget(SampleMeta.getDomain());
        addScreenHandler(domain, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                QueryData field;
                
                field = getQuery(SampleMeta.getDomain());
                domain.setSelection(field != null ? field.query : field);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                /*
                 * this dropdown is not set to be in query mode because it needs
                 * to only allow single selection 
                 */
                domain.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        test = (Dropdown)def.getWidget(SampleMeta.getAnalysisTestId());
        addScreenHandler(test, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                test.setQuery(getQuery(SampleMeta.getAnalysisTestId()));
            }

            public void onStateChange(StateChangeEvent<State> event) {
                test.enable(EnumSet.of(State.ADD,State.UPDATE).contains(event.getState()));
                /*
                 * query mode is enabled in display mode to make sure that multiple
                 * selections get shown in the textbox
                 */
                test.setQueryMode(EnumSet.of(State.ADD, State.UPDATE, State.DISPLAY).contains(event.getState()));
            }
        });
        
        reportToTable = (TableWidget)def.getWidget("reportToTable");
        addScreenHandler(reportToTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                reportToTable.load(getReportToTableModel());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                /*
                 * In query state the table is not set in query mode, because it's 
                 * not used for querying for exchange criteria. Due to that, since
                 * the autocomplete for organizations is required, an error could
                 * get added to the table in query state, because of the autocomplete
                 * being blank. Thus the autocomplete is made required based on
                 * the state. 
                 */
                //reportToOrganizationName.getField().required = EnumSet.of(State.ADD, State.UPDATE).contains(event.getState());
                
                reportToTable.enable(true);
            }
        });
        
        reportToTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                if (state != State.ADD && state != State.UPDATE)
                    event.cancel();                
            }
        });
        
        reportToTable.addCellEditedHandler(new CellEditedHandler() {
            public void onCellUpdated(CellEditedEvent event) {
                int r, c;
                Object val;
                TableDataRow row;

                r = event.getRow();               
                c = event.getCol();
                row = reportToTable.getRow(r);
                val = reportToTable.getObject(r,c);

                switch (c) {
                    case 0: 
                        row.key = (val != null ? ((TableDataRow)val).key : null);
                        break;
                }
            }
        });
        
        removeReportToButton = (AppButton)def.getWidget("removeReportToButton");
        addScreenHandler(removeReportToButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int r;
                
                r = reportToTable.getSelectedRow();
                if (r > -1 && reportToTable.numRows() > 0)
                    reportToTable.deleteRow(r);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                removeReportToButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        addReportToButton = (AppButton)def.getWidget("addReportToButton");
        addScreenHandler(addReportToButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                int n;
                
                reportToTable.addRow();
                n = reportToTable.numRows() - 1;
                reportToTable.selectRow(n);
                reportToTable.scrollToSelection();
                reportToTable.startEditing(n, 0);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addReportToButton.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });
        
        reportToOrganizationName = (AutoComplete)reportToTable.getColumns().get(0).getColumnWidget();        
        reportToOrganizationName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                TableDataRow row;
                AddressDO addr;
                OrganizationDO data;
                ArrayList<OrganizationDO> list;
                ArrayList<TableDataRow> model;

                window.setBusy();
                try {
                    list = organizationService.callList("fetchByIdOrName", QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<TableDataRow>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new TableDataRow(4);
                        data = list.get(i);

                        row.key = data.getId();
                        row.cells.get(0).setValue(data.getName());
                        addr = data.getAddress();
                        row.cells.get(1).setValue(addr.getStreetAddress());
                        row.cells.get(2).setValue(addr.getCity());
                        row.cells.get(3).setValue(addr.getState());

                        model.add(row);
                    }
                    reportToOrganizationName.showAutoMatches(model);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
                window.clearStatus();
            }
        });

        profileVersion = (Dropdown)def.getWidget(SampleMeta.getOrgParamValue());
        addScreenHandler(profileVersion, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                profileVersion.setQuery(getQuery(SampleMeta.getOrgParamValue()));
            }

            public void onStateChange(StateChangeEvent<State> event) {                
                /*
                 * this dropdown is not set to be in query mode because it needs
                 * to only allow single selection 
                 */
                profileVersion.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
            }
        });

        testResultFlags = (Dropdown)def.getWidget(SampleMeta.getAnalysisResultTestResultFlagsId());
        addScreenHandler(testResultFlags, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                testResultFlags.setQuery(getQuery(SampleMeta.getAnalysisResultTestResultFlagsId()));
            }

            public void onStateChange(StateChangeEvent<State> event) {
                testResultFlags.enable(EnumSet.of(State.ADD, State.UPDATE).contains(event.getState()));
                
                /*
                 * query mode is enabled in display mode to make sure that multiple
                 * selections get shown in the textbox
                 */
                testResultFlags.setQueryMode(EnumSet.of(State.ADD, State.UPDATE, State.DISPLAY).contains(event.getState()));
            }
        });
        
        searchButton = (AppButton)def.getWidget("searchButton");
        addScreenHandler(searchButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                search();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                searchButton.enable(EnumSet.of(State.ADD, State.UPDATE, State.DISPLAY).contains(event.getState()));
            }
        });
        
        lastRunTable = (TableWidget)def.getWidget("lastRunTable");
        addScreenHandler(lastRunTable, new ScreenEventHandler<ArrayList<TableDataRow>>() {
            public void onDataChange(DataChangeEvent event) {
                loadLastRunTableModel();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                lastRunTable.enable(true);
            }
        });
        
        lastRunTable.addSelectionHandler(new SelectionHandler<TableRow>() {
            public void onSelection(SelectionEvent<TableRow> event) {
                EventLogDO data;
                
                /*
                 * if a list of samples is being shown in the text area then ask
                 * the user if he/she wants them to be replaced with the list generated
                 * on the date associated with this row 
                 */
                if (DataBaseUtil.isEmpty(queryResults.getText()) ||
                                Window.confirm(consts.get("replaceCurrentSampleList"))) {
                    data = (EventLogDO)lastRunTable.getSelection().data;
                    setQueryResults(data.getText());
                }
            }
        });

        lastRunTable.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();                
            }
        });
        
        lastRunPrevButton = (AppButton)def.getWidget("lastRunPrevButton");
        addScreenHandler(lastRunPrevButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) { 
                if(pageNum > 0) {                               
                    pageNum--;
                    DataChangeEvent.fire(screen, lastRunTable);                                   
                } else {  
                    window.setError(consts.get("noMoreRecordInDir"));
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                lastRunPrevButton.enable(EnumSet.of(State.UPDATE, State.DISPLAY).contains(event.getState()));
            }
        });

        lastRunNextButton = (AppButton)def.getWidget("lastRunNextButton");
        addScreenHandler(lastRunNextButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {           
                pageNum++;                   
                DataChangeEvent.fire(screen, lastRunTable);           
            }

            public void onStateChange(StateChangeEvent<State> event) {
                lastRunNextButton.enable(EnumSet.of(State.UPDATE,State.DISPLAY).contains(event.getState()));
            }
        });
        
        queryResults = (TextArea)def.getWidget("queryResults");
        addScreenHandler(queryResults, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                setQueryResults(null);
            }
            
            public void onValueChange(ValueChangeEvent<String> event) {
                exportToLocationButton.enable(!DataBaseUtil.isEmpty(event.getValue()));
            }

            public void onStateChange(StateChangeEvent<State> event) {
                queryResults.enable(EnumSet.of(State.ADD, State.UPDATE,State.DISPLAY).contains(event.getState()));
            }
        });           

        exportToLocationButton = (AppButton)def.getWidget("exportToLocationButton");
        addScreenHandler(exportToLocationButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                exportToLocation();
            }
            
            public void onStateChange(StateChangeEvent<State> event) {
                exportToLocationButton.enable(false);
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
                            window.setError("No more records in this direction");
                        } else {
                            Window.alert("Error: Exchange Data Selection call query failed; " +
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
                field.key = ExchangeCriteriaMeta.getName();
                field.query = ((AppButton)event.getSource()).getAction();
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
        String label, value;
        ArrayList<TableDataRow> model;
        ArrayList<DictionaryDO> entries;
        ArrayList<TestMethodVO> tests;
        ArrayList<OrganizationParameterDO> params;
        HashSet<String> values;
        Dropdown<Integer> profile;
        TableDataRow row;

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        entries = CategoryCache.getBySystemName("exchange_environment");
        for (DictionaryDO d : entries) {
            row = new TableDataRow(d.getId(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);
        }

        environment.setModel(model);
        
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        entries =  CategoryCache.getBySystemName("sample_domain");
        for (DictionaryDO d : entries) {
            if ("animal".equals(d.getSystemName())) {
                row = new TableDataRow(SampleManager.ANIMAL_DOMAIN_FLAG, d.getEntry());
                row.enabled = ("Y".equals(d.getIsActive()));
                model.add(row);
            } else if ("environmental".equals(d.getSystemName())) {
                row = new TableDataRow(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG, d.getEntry());
                row.enabled = ("Y".equals(d.getIsActive()));
                model.add(row);
            } else if ("human".equals(d.getSystemName())) {
                row = new TableDataRow(SampleManager.HUMAN_DOMAIN_FLAG, d.getEntry());
                row.enabled = ("Y".equals(d.getIsActive()));
                model.add(row);
            } else if ("newborn".equals(d.getSystemName())) {
                row = new TableDataRow(SampleManager.NEWBORN_DOMAIN_FLAG, d.getEntry());
                row.enabled = ("Y".equals(d.getIsActive()));
                model.add(row);
            } else if ("pt".equals(d.getSystemName())) {
                row = new TableDataRow(SampleManager.PT_DOMAIN_FLAG, d.getEntry());
                row.enabled = ("Y".equals(d.getIsActive()));
                model.add(row);
            } else if ("private_well".equals(d.getSystemName())) {
                row = new TableDataRow(SampleManager.WELL_DOMAIN_FLAG, d.getEntry());
                row.enabled = ("Y".equals(d.getIsActive()));
                model.add(row);
            } else if ("quick_entry".equals(d.getSystemName())) {
                row = new TableDataRow(SampleManager.QUICK_ENTRY, d.getEntry());
                row.enabled = ("Y".equals(d.getIsActive()));
                model.add(row);
            } else if ("sdwis".equals(d.getSystemName())) {
                row = new TableDataRow(SampleManager.SDWIS_DOMAIN_FLAG, d.getEntry());
                row.enabled = ("Y".equals(d.getIsActive()));
                model.add(row);
            }
        }
        
        domain.setModel(model);
        
        try {
            tests = testService.callList("fetchList");
            model = new ArrayList<TableDataRow>();
            model.add(new TableDataRow(null, ""));
            for (TestMethodVO n : tests) {
                if ("N".equals(n.getIsActive())) {
                    label = DataBaseUtil.concatWithSeparator(DataBaseUtil.concatWithSeparator(n.getTestName(), ", ", n.getMethodName()),
                            " [", DataBaseUtil.concatWithSeparator(n.getActiveBegin(),"..", n.getActiveEnd()+"]"));
                } else {
                    label = DataBaseUtil.concatWithSeparator(n.getTestName(), ", ", n.getMethodName());
                }

                row = new TableDataRow(n.getTestId(), label);
                model.add(row);
            }
            
            test.setModel(model);
            
            model = new ArrayList<TableDataRow>();
            model.add(new TableDataRow(null, ""));
            params = organizationService.callList("fetchParametersByDictionarySystemName",  "org_electronic_format");
            values = new HashSet<String>();
            for (OrganizationParameterDO p : params) {
                /*
                 * the same value isn't added again to the dropdown 
                 */
                value = p.getValue();
                if (values.contains(value)) 
                    continue;                                   
                row = new TableDataRow(value, value);
                values.add(value);
                model.add(row);
            }
            
            profileVersion.setModel(model);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        entries = CategoryCache.getBySystemName("test_result_flags");
        for (DictionaryDO d : entries) {
            row = new TableDataRow(d.getId(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);
        }               

        testResultFlags.setModel(model);

        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        entries = CategoryCache.getBySystemName("exchange_profile");
        for (DictionaryDO d : entries) {
            row = new TableDataRow(d.getId(), d.getEntry());
            row.enabled = ("Y".equals(d.getIsActive()));
            model.add(row);
        }
        
        profile = ((Dropdown<Integer>)profileTable.getColumns().get(0).getColumnWidget());
        profile.setModel(model);
    }   
    
    private boolean reportToHasEmptyRows() {
        ArrayList<TableDataRow> model;
        
        model = reportToTable.getData();
        
        if (model == null || model.size() == 0)
            return false;
        
        for (TableDataRow r : model) {
            if (r.key == null)
                return true;
        }
        
        return false;
    }

    /*
     * basic button methods
     */
    protected void query() {
        manager = ExchangeCriteriaManager.getInstance();

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
        ExchangeCriteriaViewDO data;
        
        manager = ExchangeCriteriaManager.getInstance();
        data = manager.getExchangeCriteria();
        data.setIsAllAnalysesIncluded("Y");
        
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
            pageNum = 0;
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
        window.clearStatus();
    }
    
    protected void delete() {
        window.setBusy(consts.get("lockForUpdate"));

        try {
            manager = manager.fetchForUpdate();

            setState(State.DELETE);
            DataChangeEvent.fire(this);
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
        window.clearStatus();
    }

    protected void commit() {
        setFocus(null);

        if (state != State.QUERY && reportToHasEmptyRows()) {
            Window.alert(consts.get("removeEmptyReportToRows"));
            return;
        }
        
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
                createSampleQueryFields();
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
                createSampleQueryFields();
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
        } else if (state == State.DELETE) {
            window.setBusy(consts.get("deleting"));
            try {
                manager.delete();

                fetchById(null);
                window.setDone(consts.get("deleteComplete"));
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                Window.alert("commitDelete(): " + e.getMessage());
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
                Window.alert(e.getMessage());
                fetchById(null);
            }
            window.setDone(consts.get("updateAborted"));
        } else if (state == State.DELETE) {
            try {
                manager = manager.abortUpdate();
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
            } catch (Exception e) {
                Window.alert(e.getMessage());
                fetchById(null);
            }
            window.setDone(consts.get("deleteAborted"));
        } else {
            window.clearStatus();
        }
    }
    
    protected void duplicate() {
        try {
            window.setBusy(consts.get("fetching"));
            
            manager = service.call("duplicate", manager.getExchangeCriteria().getId());

            setState(State.ADD);
            DataChangeEvent.fire(this);

            setFocus(name);
            window.setDone(consts.get("enterInformationPressCommit"));
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            window.clearStatus();
        }
    }
    
    protected boolean fetchById(Integer id) {
        if (id == null) {
            manager = ExchangeCriteriaManager.getInstance();
            setState(State.DEFAULT);
        } else {
            window.setBusy(consts.get("fetching"));
            try {
                manager = ExchangeCriteriaManager.fetchWithProfiles(id);
                setState(State.DISPLAY);
                pageNum = 0;
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
    
    private ArrayList<TableDataRow> getReportToTableModel() {
        Query query;
        QueryData field, orgField;
        TableDataRow row;
        ArrayList<TableDataRow> model;
        ArrayList<IdNameVO> orgList;
        
        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;
        
        field = getQuery(SampleMeta.getOrgId());
        if (field == null)
            return model;
        
        query = new Query();
        
        orgField = new QueryData();
        orgField.query = field.query;
        orgField.key = OrganizationMeta.getId();
        orgField.type = QueryData.Type.INTEGER;
        query.setFields(orgField);
        
        try {
            /*
             * fetch the names of the organizations selected by the user in the 
             * past since they are not stored with the ids in the query   
             */
            orgList = organizationService.callList("query", query);
            
            for (IdNameVO org: orgList) {
                row = new TableDataRow(org.getId(), org.getName());
                model.add(new TableDataRow(org.getId(), row));
            }
            return model;
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }     
        return null; 
    }
     
    private ArrayList<TableDataRow> getProfileTableModel() {
        TableDataRow row;
        ExchangeProfileDO data;
        ArrayList<TableDataRow> model;
        
        model = new ArrayList<TableDataRow>();
        if (manager == null)
            return model;

        try {
            for (int i = 0; i < manager.getProfiles().count(); i++) {
                data = manager.getProfiles().getProfileAt(i);

                row = new TableDataRow(1);
                row.cells.get(0).setValue(data.getProfileId());
                model.add(row);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }
    
    private void loadLastRunTableModel() {
        Query query;
        QueryData field;
        ExchangeCriteriaViewDO data;
        TableDataRow row;
        ArrayList<TableDataRow> model;
        ArrayList<EventLogDO> logs;
        
        model = new ArrayList<TableDataRow>();
        if (manager == null || manager.getExchangeCriteria().getId() == null) {
            lastRunTable.load(model);
            return;
        }
        
        query = new Query();
        
        field = new QueryData();
        field.key = EventLogMeta.getReferenceTableId();
        field.query = String.valueOf(ReferenceTable.EXCHANGE_CRITERIA);
        field.type = QueryData.Type.INTEGER;
        query.setFields(field);
        
        data = manager.getExchangeCriteria();
        
        field = new QueryData();
        field.key = EventLogMeta.getReferenceId();
        field.query = data.getId().toString();
        field.type = QueryData.Type.INTEGER;
        query.setFields(field);
        
        query.setPage(pageNum);
        query.setRowsPerPage(10);
        
        try {           
            window.setBusy(consts.get("fetching"));                      
            
            logs = eventLogService.callList("query", query); 
            
            for (EventLogDO log : logs) {
                row = new TableDataRow(1);
                row.cells.get(0).setValue(log.getTimeStamp());
                row.data = log;
                model.add(row);
            }
            
            lastRunTable.load(model);
            window.clearStatus();      
        } catch (LastPageException e) {
            window.setError(consts.get("noMoreRecordInDir"));
            pageNum--;
            return;
        } catch (NotFoundException e) {
            window.setDone(consts.get("noRecordsFound"));
            lastRunTable.load(model);
            return;
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
            window.clearStatus();
            pageNum--;
            lastRunTable.load(model);
            return;
        }
    }
    
    private void search() {        
        Query query;
        ArrayList<QueryData> fields;
        ArrayList<IdAccessionVO> samples;
        ArrayList<String> list;

        if (reportToRowsEmpty()) {
            Window.alert(consts.get("removeEmptyReportToRows"));
            return;
        }
        
        createSampleQueryFields();
        
        fields = manager.getExchangeCriteria().getFields();
        if (fields.size() == 0) {
            Window.alert(consts.get("atleastOneFieldFilledException"));
            return;
        }
        
        query =  new Query();
        query.setFields(fields);
        try {
            /*
             * run the query, get the list of samples and show the accession numbers
             */
            window.setBusy(consts.get("fetching"));
            samples = service.callList("dataExchangeQuery", query);
            list = new ArrayList<String>();
            for (IdAccessionVO s : samples) 
                list.add(s.getAccessionNumber().toString());
            setQueryResults(DataBaseUtil.concatWithSeparator(list, ","));
            
            /*
             * this is done so that after the accession numbers fetched from the 
             * back-end are set in the text-area, the user can select a row in the
             * table showing the log entries  
             */
            lastRunTable.clearSelections();
            
            window.clearStatus();
        } catch (NotFoundException e) {
            setQueryResults(null);
            window.setDone(consts.get("noRecordsFound"));
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
            window.clearStatus();
        }
    }
    
    private void createSampleQueryFields() {
        ArrayList<QueryData> fields;
        ArrayList<Integer> orgIds; 
        TableDataRow selRow;
        QueryData field;
        
        fields = manager.getExchangeCriteria().getFields();
        if (fields == null) {
            fields = new ArrayList<QueryData>();
            manager.getExchangeCriteria().setFields(fields);
        } else {
            fields.clear();
        }
        
        releasedDate.getQuery(fields, SampleMeta.getAnalysisReleasedDate());
        
        selRow = domain.getSelection();        
        if (selRow != null && selRow.key != null) {
            field = new QueryData();
            field.query = (String)selRow.key;
            field.key = SampleMeta.getDomain();
            field.type = QueryData.Type.STRING;
            fields.add(field);
        }
        
        test.getQuery(fields, SampleMeta.getAnalysisTestId());
        
        orgIds = new ArrayList<Integer>(); 
        for (TableDataRow row : reportToTable.getData())
            orgIds.add((Integer)row.key);
        
        if (orgIds.size() > 0) {
            field = new QueryData();
            field.query = DataBaseUtil.concatWithSeparator(orgIds, "|");
            field.key = SampleMeta.getOrgId();
            field.type = QueryData.Type.INTEGER;
            fields.add(field);
        }       
        
        selRow = profileVersion.getSelection();        
        if (selRow != null && selRow.key != null) {
            field = new QueryData();
            field.query = (String)selRow.key;
            field.key = SampleMeta.getOrgParamValue();
            field.type = QueryData.Type.STRING;
            fields.add(field);
        }
        
        testResultFlags.getQuery(fields, SampleMeta.getAnalysisResultTestResultFlagsId());
    }
    
    private QueryData getQuery(String key) {
        ArrayList<QueryData> fields;
        
        fields = manager.getExchangeCriteria().getFields();
        if (fields == null)
            return null;
        
        for (QueryData f : fields) {
            if (key.equals(f.key))
                return f;
        }
        
        return null;
    }
    
    private boolean reportToRowsEmpty() {
        reportToTable.finishEditing();
        
        for (TableDataRow row : reportToTable.getData())
            if (row.key == null)
                return true;
    
        return false;
    }
    
    private void setQueryResults(String results) {
        queryResults.setText(results);
        exportToLocationButton.enable(!DataBaseUtil.isEmpty(results));
    }
    
    private void exportToLocation() {
        String uri;
        ExchangeCriteriaViewDO data;
        Query query;
        QueryData field;
        
        data = manager.getExchangeCriteria();
        uri = data.getDestinationUri();
        field = null;
        if (data.getId() == null) {
            if (uri == null) {
                Window.alert(consts.get("specifyDestURI"));
                return;
            }
        } else {
            field = new QueryData();
            field.key = "EXCHANGE_CRITERIA_ID"; 
            field.query = data.getId().toString();
            field.type = QueryData.Type.STRING;
        }
        
        query = new Query();
        if (field != null)
            query.setFields(field);
        
        field = new QueryData();
        field.key = "ACCESSION_NUMBERS"; 
        field.query = queryResults.getText();
        field.type = QueryData.Type.STRING;
        query.setFields(field);
        
        field = new QueryData();
        field.key = "DESTINATION_URI"; 
        field.query = uri;
        field.type = QueryData.Type.STRING;
        query.setFields(field);
        
        try {
            if (dataExchangeReportScreen == null) 
                dataExchangeReportScreen = new DataExchangeReportScreen("exportToLocation", window);  
            else
                dataExchangeReportScreen.setWindow(window);
            
            dataExchangeReportScreen.runReport(query);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            e.printStackTrace();
        }
    }
}