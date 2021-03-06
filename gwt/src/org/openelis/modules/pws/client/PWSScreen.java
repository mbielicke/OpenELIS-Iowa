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
package org.openelis.modules.pws.client;

import static org.openelis.modules.main.client.Logger.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.logging.Level;

import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.IdNameVO;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.ButtonGroup;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.TabPanel;
import org.openelis.gwt.widget.TextArea;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.manager.PWSManager;
import org.openelis.meta.PWSMeta;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.widget.WindowInt;
import org.openelis.modules.main.client.StatusBarPopupScreenUI;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel;

public class PWSScreen extends Screen implements HasActionHandlers<PWSScreen.Action> {

    public enum Action {
        SELECT
    };

    private PWSManager             manager;
    protected Tabs                 tab;
    private ModulePermission       userPermission;

    private ScreenNavigator        nav;

    private CalendarLookUp         effBeginDt, effEndDt;
    private TextBox                number0, alternateStNum, name, dPrinCitySvdNm, dPrinCntySvdNm,
                    dPwsStTypeCd, activityStatusCd;
    private TextBox<Integer>       dPopulationCount, startDay, startMonth, endDay, endMonth;
    private TextArea               activityRsnTxt;
    private AppButton              queryButton, previousButton, nextButton, commitButton,
                    abortButton, selectButton;
    private TabPanel               tabPanel;
    private ButtonGroup            atoz;
    private MonitorTab             monitorTab;
    private FacilityTab            facilityTab;
    private AddressTab             addressTab;
    private ViolationTab           violationTab;
    private String                 pwsNumber0;
    private MenuItem               parse, violationScan, additionalScan;
    private StatusBarPopupScreenUI statusScreen;

    private enum Tabs {
        FACILITY, ADDRESS, MONITOR, VIOLATION
    };

    public PWSScreen(WindowInt window) throws Exception {
        this(null, window);
    }

    public PWSScreen(String pwsNumber0, WindowInt window) throws Exception {
        super((ScreenDefInt)GWT.create(PWSDef.class));

        setWindow(window);

        userPermission = UserCache.getPermission().getModule("pws");
        if (userPermission == null)
            throw new PermissionException(Messages.get().screenPermException("PWS Screen"));

        this.pwsNumber0 = pwsNumber0;

        tab = Tabs.FACILITY;
        manager = PWSManager.getInstance();
    }

    public void initialize() {
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

        commitButton = (AppButton)def.getWidget("commit");
        addScreenHandler(commitButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                commit();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                commitButton.enable(EnumSet.of(State.QUERY).contains(event.getState()));
            }
        });

        abortButton = (AppButton)def.getWidget("abort");
        addScreenHandler(abortButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                abort();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                abortButton.enable(EnumSet.of(State.QUERY).contains(event.getState()));
            }
        });

        selectButton = (AppButton)def.getWidget("select");
        if (pwsNumber0 != null) {
            addScreenHandler(selectButton, new ScreenEventHandler<Object>() {
                public void onClick(ClickEvent event) {
                    select();
                }

                public void onStateChange(StateChangeEvent<State> event) {
                    selectButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
                }
            });
        } else
            selectButton.setVisible(false);

        parse = (MenuItem)def.getWidget("parse");
        addScreenHandler(parse, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                parse();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                parse.enable(userPermission.hasAddPermission());
            }
        });

        violationScan = (MenuItem)def.getWidget("violationScan");
        addScreenHandler(violationScan, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                violationScan();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                violationScan.enable(true);
            }
        });

        additionalScan = (MenuItem)def.getWidget("additionalScan");
        addScreenHandler(additionalScan, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                additionalScan();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                additionalScan.enable(true);
            }
        });

        number0 = (TextBox)def.getWidget(PWSMeta.getNumber0());
        addScreenHandler(number0, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                number0.setValue(manager.getPWS().getNumber0());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getPWS().setTinwsysIsNumber(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                number0.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                number0.setQueryMode(event.getState() == State.QUERY);
            }
        });

        alternateStNum = (TextBox)def.getWidget(PWSMeta.getAlternateStNum());
        addScreenHandler(alternateStNum, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                alternateStNum.setValue(manager.getPWS().getAlternateStNum());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getPWS().setAlternateStNum(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                alternateStNum.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                alternateStNum.setQueryMode(event.getState() == State.QUERY);
            }
        });

        name = (TextBox)def.getWidget(PWSMeta.getName());
        addScreenHandler(name, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                name.setValue(manager.getPWS().getName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getPWS().setName(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                name.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                name.setQueryMode(event.getState() == State.QUERY);
            }
        });

        dPrinCitySvdNm = (TextBox)def.getWidget(PWSMeta.getDPrinCitySvdNm());
        addScreenHandler(dPrinCitySvdNm, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                dPrinCitySvdNm.setValue(manager.getPWS().getDPrinCitySvdNm());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getPWS().setDPrinCitySvdNm(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                dPrinCitySvdNm.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                dPrinCitySvdNm.setQueryMode(event.getState() == State.QUERY);
            }
        });

        dPrinCntySvdNm = (TextBox)def.getWidget(PWSMeta.getDPrinCntySvdNm());
        addScreenHandler(dPrinCntySvdNm, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                dPrinCntySvdNm.setValue(manager.getPWS().getDPrinCntySvdNm());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getPWS().setDPrinCntySvdNm(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                dPrinCntySvdNm.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                dPrinCntySvdNm.setQueryMode(event.getState() == State.QUERY);
            }
        });

        dPwsStTypeCd = (TextBox)def.getWidget(PWSMeta.getDPwsStTypeCd());
        addScreenHandler(dPwsStTypeCd, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                dPwsStTypeCd.setValue(manager.getPWS().getDPwsStTypeCd());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getPWS().setDPwsStTypeCd(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                dPwsStTypeCd.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                dPwsStTypeCd.setQueryMode(event.getState() == State.QUERY);
            }
        });

        activityStatusCd = (TextBox)def.getWidget(PWSMeta.getActivityStatusCd());
        addScreenHandler(activityStatusCd, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                activityStatusCd.setValue(manager.getPWS().getActivityStatusCd());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getPWS().setActivityStatusCd(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                activityStatusCd.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                activityStatusCd.setQueryMode(event.getState() == State.QUERY);
            }
        });

        dPopulationCount = (TextBox<Integer>)def.getWidget(PWSMeta.getDPopulationCount());
        addScreenHandler(dPopulationCount, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                dPopulationCount.setFieldValue(manager.getPWS().getDPopulationCount());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getPWS().setDPopulationCount(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                dPopulationCount.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                dPopulationCount.setQueryMode(event.getState() == State.QUERY);
            }
        });

        activityRsnTxt = (TextArea)def.getWidget(PWSMeta.getActivityRsnTxt());
        addScreenHandler(activityRsnTxt, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                activityRsnTxt.setValue(manager.getPWS().getActivityRsnTxt());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getPWS().setActivityRsnTxt(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                activityRsnTxt.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                activityRsnTxt.setQueryMode(event.getState() == State.QUERY);
            }
        });

        startDay = (TextBox<Integer>)def.getWidget(PWSMeta.getStartDay());
        addScreenHandler(startDay, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                startDay.setFieldValue(manager.getPWS().getStartDay());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getPWS().setStartDay(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                startDay.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                startDay.setQueryMode(event.getState() == State.QUERY);
            }
        });

        startMonth = (TextBox<Integer>)def.getWidget(PWSMeta.getStartMonth());
        addScreenHandler(startMonth, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                startMonth.setFieldValue(manager.getPWS().getStartMonth());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getPWS().setStartMonth(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                startMonth.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                startMonth.setQueryMode(event.getState() == State.QUERY);
            }
        });

        effBeginDt = (CalendarLookUp)def.getWidget(PWSMeta.getEffBeginDt());
        addScreenHandler(effBeginDt, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                effBeginDt.setValue(manager.getPWS().getEffBeginDt());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getPWS().setEffBeginDt(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                effBeginDt.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                effBeginDt.setQueryMode(event.getState() == State.QUERY);
            }
        });

        endDay = (TextBox<Integer>)def.getWidget(PWSMeta.getEndDay());
        addScreenHandler(endDay, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                endDay.setFieldValue(manager.getPWS().getEndDay());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getPWS().setEndDay(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                endDay.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                endDay.setQueryMode(event.getState() == State.QUERY);
            }
        });

        endMonth = (TextBox<Integer>)def.getWidget(PWSMeta.getEndMonth());
        addScreenHandler(endMonth, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                endMonth.setFieldValue(manager.getPWS().getEndMonth());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getPWS().setEndMonth(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                endMonth.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                endMonth.setQueryMode(event.getState() == State.QUERY);
            }
        });

        effEndDt = (CalendarLookUp)def.getWidget(PWSMeta.getEffEndDt());
        addScreenHandler(effEndDt, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                effEndDt.setValue(manager.getPWS().getEffEndDt());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getPWS().setEffEndDt(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                effEndDt.enable(EnumSet.of(State.QUERY, State.ADD, State.UPDATE)
                                       .contains(event.getState()));
                effEndDt.setQueryMode(event.getState() == State.QUERY);
            }
        });

        tabPanel = (TabPanel)def.getWidget("tabPanel");
        tabPanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                int i;

                // tab screen order should be the same as enum or this will
                // not work
                i = event.getItem().intValue();
                tab = Tabs.values()[i];

                window.setBusy();
                drawTabs();
                window.clearStatus();
            }
        });

        facilityTab = new FacilityTab(def, window);
        addScreenHandler(facilityTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                facilityTab.setManager(manager);
                if (tab == Tabs.FACILITY)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                facilityTab.setState(event.getState());
            }
        });

        addressTab = new AddressTab(def, window);
        addScreenHandler(addressTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                addressTab.setManager(manager);
                if (tab == Tabs.ADDRESS)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                addressTab.setState(event.getState());
            }
        });

        monitorTab = new MonitorTab(def, window);
        addScreenHandler(monitorTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                monitorTab.setManager(manager);
                if (tab == Tabs.MONITOR)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                monitorTab.setState(event.getState());
            }
        });

        violationTab = new ViolationTab(def, window);
        addScreenHandler(violationTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                violationTab.setManager(manager);
                if (tab == Tabs.VIOLATION)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                violationTab.setState(event.getState());
            }
        });

        //
        // left hand navigation panel
        //
        nav = new ScreenNavigator<IdNameVO>(def) {
            public void executeQuery(final Query query) {
                window.setBusy(Messages.get().querying());

                query.setRowsPerPage(20);
                PWSService.get().query(query, new AsyncCallback<ArrayList<IdNameVO>>() {
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
                            Window.alert("Error: Pws call query failed; " + error.getMessage());
                            window.setError(Messages.get().queryFailed());
                        }
                    }
                });
            }

            public boolean fetch(IdNameVO entry) {
                return fetchByTinwsysIsNumber( (entry == null) ? null : entry.getId());
            }

            public ArrayList<TableDataRow> getModel() {
                ArrayList<IdNameVO> list;
                ArrayList<TableDataRow> model;

                list = nav.getQueryResult();
                model = null;
                if (list != null) {
                    model = new ArrayList<TableDataRow>();
                    for (IdNameVO entry : list)
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
                field.setKey(PWSMeta.getName());
                field.setQuery( ((AppButton)event.getSource()).action);
                field.setType(QueryData.Type.STRING);

                query = new Query();
                query.setFields(field);
                nav.setQuery(query);
            }
        });

        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                if (EnumSet.of(State.ADD, State.UPDATE).contains(state)) {
                    event.cancel();
                    window.setError(Messages.get().mustCommitOrAbort());
                }
            }
        });

        setState(State.DEFAULT);
        DataChangeEvent.fire(this);

        if (pwsNumber0 != null)
            queryByPwsNumer0();
    }

    /*
     * basic button methods
     */
    protected void query() {
        manager = PWSManager.getInstance();

        setState(State.QUERY);
        DataChangeEvent.fire(this);

        // clear all the tabs
        facilityTab.draw();
        addressTab.draw();
        monitorTab.draw();
        violationTab.draw();

        setFocus(number0);
        window.setDone(Messages.get().enterFieldsToQuery());
    }

    protected void previous() {
        nav.previous();
    }

    protected void next() {
        nav.next();
    }

    public void commit() {
        Query query;

        setFocus(null);

        if ( !validate()) {
            window.setError(Messages.get().correctErrors());
            return;
        }

        query = new Query();
        query.setFields(getQueryFields());
        nav.setQuery(query);

    }

    protected void abort() {
        setFocus(null);
        clearErrors();
        window.setBusy(Messages.get().cancelChanges());

        fetchByTinwsysIsNumber(null);
        window.setDone(Messages.get().queryAborted());
    }

    protected void select() {
        ActionEvent.fire(this, Action.SELECT, manager.getPWS());
        window.close();
    }

    protected boolean fetchByTinwsysIsNumber(Integer tinwsysIsNumber) {
        if (tinwsysIsNumber == null) {
            manager = PWSManager.getInstance();
            setState(State.DEFAULT);
        } else {
            window.setBusy(Messages.get().fetching());
            try {
                switch (tab) {
                    case FACILITY:
                        manager = PWSManager.fetchWithFacilities(tinwsysIsNumber);
                        break;
                    case ADDRESS:
                        manager = PWSManager.fetchWithAddresses(tinwsysIsNumber);
                        break;
                    case MONITOR:
                        manager = PWSManager.fetchWithMonitors(tinwsysIsNumber);
                        break;
                    case VIOLATION:
                        manager = PWSManager.fetchWithViolations(tinwsysIsNumber);
                        break;
                }
                setState(State.DISPLAY);
            } catch (NotFoundException e) {
                fetchByTinwsysIsNumber(null);
                window.setDone(Messages.get().noRecordsFound());
                return false;
            } catch (Exception e) {
                fetchByTinwsysIsNumber(null);
                e.printStackTrace();
                Window.alert(Messages.get().fetchFailed() + e.getMessage());
                return false;
            }

        }
        DataChangeEvent.fire(this);
        window.clearStatus();

        return true;
    }

    private void queryByPwsNumer0() {
        query();
        number0.setValue(pwsNumber0);
        commit();
    }

    private void drawTabs() {
        switch (tab) {
            case FACILITY:
                facilityTab.draw();
                break;
            case ADDRESS:
                addressTab.draw();
                break;
            case MONITOR:
                monitorTab.draw();
                break;
            case VIOLATION:
                violationTab.draw();
                break;
        }
    }

    /**
     * creates a popup that shows the progress of parsing the PWS files
     */
    private void parse() {
        final PopupPanel statusPanel;

        if (statusScreen == null)
            statusScreen = new StatusBarPopupScreenUI() {
            @Override
            public boolean isStopVisible() {
                return false;
            }
            
            @Override
            public void stop() {                
                 // ignore           
            }
        };

        /*
         * initialize and show the popup screen
         */
        statusPanel = new PopupPanel();
        statusPanel.setSize("450px", "125px");
        statusPanel.setWidget(statusScreen);
        statusPanel.setPopupPosition(this.getAbsoluteLeft(), this.getAbsoluteTop());
        statusPanel.setModal(true);
        statusPanel.show();
        statusScreen.setStatus(null);

        /*
         * Read pws files and update database. Hide popup when database is
         * updated successfully or error is thrown.
         */
        PWSService.get().importFiles(new AsyncCallback<Void>() {
            public void onSuccess(Void result) {
                statusPanel.hide();
                statusScreen.setStatus(null);
            }

            public void onFailure(Throwable e) {
                statusPanel.hide();
                statusScreen.setStatus(null);
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        });

        /*
         * refresh the status of reading the files and updating the database
         * every second, until the process successfully completes or is aborted
         * because of an error
         */
        Timer timer = new Timer() {
            public void run() {
                ReportStatus status;
                try {
                    status = PWSService.get().getStatus();
                    /*
                     * the status only needs to be refreshed while the status
                     * panel is showing because once the job is finished, the
                     * panel is closed
                     */
                    if (statusPanel.isShowing()) {
                        statusScreen.setStatus(status);
                        this.schedule(1000);
                    }
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        };
        timer.schedule(1000);
    }

    private void violationScan() {
        PWSService.get().sdwisViolationScan(new AsyncCallback<Void>() {
            public void onSuccess(Void result) {
            }

            public void onFailure(Throwable e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        });
    }

    private void additionalScan() {
        PWSService.get().sdwisAdditionalScan(new AsyncCallback<Void>() {
            public void onSuccess(Void result) {
            }

            public void onFailure(Throwable e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        });
    }

    public HandlerRegistration addActionHandler(ActionHandler<Action> handler) {
        return addHandler(handler, ActionEvent.getType());
    }
}
