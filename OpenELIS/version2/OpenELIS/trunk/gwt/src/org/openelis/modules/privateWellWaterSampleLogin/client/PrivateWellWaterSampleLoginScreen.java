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
package org.openelis.modules.privateWellWaterSampleLogin.client;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.UserCache;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdAccessionVO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.OrderTestViewDO;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.StandardNoteDO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.LastPageException;
import org.openelis.gwt.common.LocalizedException;
import org.openelis.gwt.common.ModulePermission;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.PermissionException;
import org.openelis.gwt.common.RPC;
import org.openelis.gwt.common.Util;
import org.openelis.gwt.common.ValidationErrorsList;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.event.ActionEvent;
import org.openelis.gwt.event.ActionHandler;
import org.openelis.gwt.event.BeforeCloseEvent;
import org.openelis.gwt.event.BeforeCloseHandler;
import org.openelis.gwt.event.DataChangeEvent;
import org.openelis.gwt.event.HasActionHandlers;
import org.openelis.gwt.event.StateChangeEvent;
import org.openelis.gwt.screen.Screen;
import org.openelis.gwt.screen.ScreenDefInt;
import org.openelis.gwt.screen.ScreenEventHandler;
import org.openelis.gwt.screen.ScreenNavigator;
import org.openelis.gwt.services.ScreenService;
import org.openelis.gwt.widget.AppButton;
import org.openelis.gwt.widget.AppButton.ButtonState;
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.manager.OrderManager;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.SampleManager;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.order.client.SendoutOrderScreen;
import org.openelis.modules.sample.client.AccessionNumberUtility;
import org.openelis.modules.sample.client.AnalysisNotesTab;
import org.openelis.modules.sample.client.AnalysisTab;
import org.openelis.modules.sample.client.AuxDataTab;
import org.openelis.modules.sample.client.PrivateWellTab;
import org.openelis.modules.sample.client.QAEventsTab;
import org.openelis.modules.sample.client.ResultTab;
import org.openelis.modules.sample.client.SampleHistoryUtility;
import org.openelis.modules.sample.client.SampleItemAnalysisTreeTab;
import org.openelis.modules.sample.client.SampleItemTab;
import org.openelis.modules.sample.client.SampleNotesTab;
import org.openelis.modules.sample.client.SamplePrivateWellImportOrder;
import org.openelis.modules.sample.client.StorageTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TabPanel;

public class PrivateWellWaterSampleLoginScreen extends Screen implements HasActionHandlers {
    private boolean                        quickUpdate;
    private SampleManager                  manager;
    protected Tabs                         tab;
    private Integer                        sampleReleasedId;
    private SampleItemAnalysisTreeTab      treeTab;
    private PrivateWellTab                 privateWellTab;
    private SampleItemTab                  sampleItemTab;
    private AnalysisTab                    analysisTab;
    private ResultTab                      testResultsTab;
    private AnalysisNotesTab               analysisNotesTab;
    private SampleNotesTab                 sampleNotesTab;
    private StorageTab                     storageTab;
    private QAEventsTab                    qaEventsTab;
    private AuxDataTab                     auxDataTab;

    protected AccessionNumberUtility       accessionNumUtil;
    protected SampleHistoryUtility         historyUtility;

    protected TextBox                      clientReference;
    protected TextBox<Integer>             accessionNumber, orderNumber;
    protected TextBox<Datetime>            collectedTime;
    protected Dropdown<Integer>            statusId;
    protected CalendarLookUp               collectedDate, receivedDate;
    protected AppButton                    queryButton, addButton, updateButton,
                                           nextButton, prevButton, commitButton,
                                           abortButton, orderLookup;
    protected MenuItem                     historySample, historySamplePrivateWell,
                                           historySampleProject, historySampleItem,
                                           historyAnalysis, historyCurrentResult,
                                           historyStorage, historySampleQA,
                                           historyAnalysisQA, historyAuxData;
    protected TabPanel                     tabs;

    private ScreenNavigator                nav;
    private ModulePermission               userPermission;
    private SendoutOrderScreen             sendoutOrderScreen;
    private StandardNoteDO                 autoNote;
    private ScreenService                  standardNoteService;

    protected SamplePrivateWellImportOrder wellOrderImport;

    private enum Tabs {
        SAMPLE_ITEM, ANALYSIS, TEST_RESULT, ANALYSIS_NOTES, SAMPLE_NOTES, STORAGE, QA_EVENTS,
        AUX_DATA
    };

    public PrivateWellWaterSampleLoginScreen() throws Exception {
        super((ScreenDefInt)GWT.create(PrivateWellWaterSampleLoginDef.class));
        service = new ScreenService("controller?service=org.openelis.modules.sample.server.SampleService");
        standardNoteService = new ScreenService("controller?service=org.openelis.modules.standardnote.server.StandardNoteService");

        userPermission = UserCache.getPermission().getModule("sampleprivatewell");
        if (userPermission == null)
            throw new PermissionException("screenPermException", "Private Well Water Sample Login Screen");

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
        tab = Tabs.SAMPLE_ITEM;
        manager = SampleManager.getInstance();
        manager.getSample().setDomain(SampleManager.WELL_DOMAIN_FLAG);
        quickUpdate = false;

        try {
            CategoryCache.getBySystemNames("sample_status", "user_action",
                                           "analysis_status", "type_of_sample",
                                           "source_of_sample", "sample_container",
                                           "unit_of_measure", "qaevent_type",
                                           "aux_field_value_type", "worksheet_status");            
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }

        initialize();
        initializeDropdowns();
        setState(State.DEFAULT);
        DataChangeEvent.fire(this);
    }

    private void initialize() {
        final PrivateWellWaterSampleLoginScreen wellScreen = this;

        //
        // button panel buttons
        //
        queryButton = (AppButton)def.getWidget("query");
        addScreenHandler(queryButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                query();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState()) &&
                    userPermission.hasSelectPermission())
                    queryButton.enable(true);
                else if (event.getState() == State.QUERY)
                    queryButton.setState(ButtonState.LOCK_PRESSED);
                else
                    queryButton.enable(false);
            }
        });

        prevButton = (AppButton)def.getWidget("previous");
        addScreenHandler(prevButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                previous();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                prevButton.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
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
                if (EnumSet.of(State.DEFAULT, State.DISPLAY).contains(event.getState()) &&
                    userPermission.hasAddPermission())
                    addButton.enable(true);
                else if (EnumSet.of(State.ADD).contains(event.getState()))
                    addButton.setState(ButtonState.LOCK_PRESSED);
                else
                    addButton.enable(false);
            }
        });

        updateButton = (AppButton)def.getWidget("update");
        addScreenHandler(updateButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                update();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                if (EnumSet.of(State.DISPLAY).contains(event.getState()) &&
                    userPermission.hasUpdatePermission())
                    updateButton.enable(true);
                else if (EnumSet.of(State.UPDATE).contains(event.getState()))
                    updateButton.setState(ButtonState.LOCK_PRESSED);
                else
                    updateButton.enable(false);

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

        historyUtility = new SampleHistoryUtility(window) {
            public void historyCurrentResult() {
                ActionEvent.fire(wellScreen, ResultTab.Action.RESULT_HISTORY, null);
            }
        };

        historySample = (MenuItem)def.getWidget("historySample");
        addScreenHandler(historySample, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.setManager(manager);
                historyUtility.historySample();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historySample.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        historySamplePrivateWell = (MenuItem)def.getWidget("historySamplePrivateWell");
        addScreenHandler(historySamplePrivateWell, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.setManager(manager);
                historyUtility.historySamplePrivateWell();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historySamplePrivateWell.enable(EnumSet.of(State.DISPLAY)
                                                       .contains(event.getState()));
            }
        });

        historySampleProject = (MenuItem)def.getWidget("historySampleProject");
        addScreenHandler(historySampleProject, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.setManager(manager);
                historyUtility.historySampleProject();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historySampleProject.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        historySampleItem = (MenuItem)def.getWidget("historySampleItem");
        addScreenHandler(historySampleItem, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.setManager(manager);
                historyUtility.historySampleItem();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historySampleItem.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        historyAnalysis = (MenuItem)def.getWidget("historyAnalysis");
        addScreenHandler(historyAnalysis, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.setManager(manager);
                historyUtility.historyAnalysis();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historyAnalysis.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        historyCurrentResult = (MenuItem)def.getWidget("historyCurrentResult");
        addScreenHandler(historyCurrentResult, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.setManager(manager);
                historyUtility.historyCurrentResult();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historyCurrentResult.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        historyStorage = (MenuItem)def.getWidget("historyStorage");
        addScreenHandler(historyStorage, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.setManager(manager);
                historyUtility.historyStorage();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historyStorage.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        historySampleQA = (MenuItem)def.getWidget("historySampleQA");
        addScreenHandler(historySampleQA, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.setManager(manager);
                historyUtility.historySampleQA();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historySampleQA.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        historyAnalysisQA = (MenuItem)def.getWidget("historyAnalysisQA");
        addScreenHandler(historyAnalysisQA, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.setManager(manager);
                historyUtility.historyAnalysisQA();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historyAnalysisQA.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        historyAuxData = (MenuItem)def.getWidget("historyAuxData");
        addScreenHandler(historyAuxData, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.setManager(manager);
                historyUtility.historyAuxData();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historyAuxData.enable(EnumSet.of(State.DISPLAY).contains(event.getState()));
            }
        });

        accessionNumber = (TextBox<Integer>)def.getWidget(SampleMeta.getAccessionNumber());
        addScreenHandler(accessionNumber, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                accessionNumber.setValue(Util.toString(manager.getSample().getAccessionNumber()));
            }

            public void onValueChange(final ValueChangeEvent<Integer> event) {
                Integer       oldNumber;
                SampleManager quickEntryMan;
                NoteViewDO    exn;

                oldNumber = manager.getSample().getAccessionNumber();
                if (oldNumber != null) {
                    if (quickUpdate) {
                        Window.alert(consts.get("cantChangeQuickEntryAccessionNumber"));
                        accessionNumber.setValue(Util.toString(oldNumber));
                        setFocus(accessionNumber);
                        return;
                    } else if (!Window.confirm(consts.get("accessionNumberEditConfirm"))) {
                        accessionNumber.setValue(Util.toString(oldNumber));
                        setFocus(accessionNumber);
                        return;
                    }
                }
                try {
                    manager.getSample().setAccessionNumber(event.getValue());

                    if (accessionNumUtil == null)
                        accessionNumUtil = new AccessionNumberUtility();

                    quickEntryMan = accessionNumUtil.accessionNumberEntered(manager.getSample());
                    if (quickEntryMan != null) {
                        if (state == State.ADD) {
                            manager = quickEntryMan;
                            manager.getSample().setDomain(SampleManager.WELL_DOMAIN_FLAG);
                            manager.createEmptyDomainManager();
    
                            /*
                             * We add the standard note, if any, defined through
                             * a system variable for this domain, because it isn't 
                             * present in the manager fetched from the back-end. 
                             */
                            if (autoNote != null) { 
                                exn = manager.getExternalNote().getEditingNote();
                                exn.setIsExternal("Y");
                                exn.setText(autoNote.getText());
                            }
                            DeferredCommand.addCommand(new Command() {
                                public void execute() {
                                    setFocus(null);
                                    setState(State.UPDATE);
                                    DataChangeEvent.fire(wellScreen);
                                    window.clearStatus();
                                    quickUpdate = true;
                                }
                            });
                        } else {
                            quickEntryMan.abortUpdate();
                            throw new Exception(consts.get("quickEntryNumberExists"));
                        }
                    }
                } catch (ValidationErrorsList e) {
                    showErrors(e);
                    accessionNumber.setValue(Util.toString(oldNumber));
                    manager.getSample().setAccessionNumber(oldNumber);
                    setFocus(accessionNumber);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    accessionNumber.setValue(Util.toString(oldNumber));
                    manager.getSample().setAccessionNumber(oldNumber);
                    setFocus(accessionNumber);
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                accessionNumber.enable(event.getState() == State.QUERY ||
                                       (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                            .contains(event.getState())));
                accessionNumber.setQueryMode(event.getState() == State.QUERY);
            }
        });

        orderNumber = (TextBox<Integer>)def.getWidget(SampleMeta.getOrderId());
        addScreenHandler(orderNumber, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                orderNumber.setValue(Util.toString(manager.getSample().getOrderId()));
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                ValidationErrorsList errors;
                OrderManager man;

                if (DataBaseUtil.isEmpty(event.getValue())) {
                    manager.getSample().setOrderId(event.getValue());
                    return;
                }
                
                if (manager.getSample().getId() != null) {
                    Window.alert(consts.get("existSampleCantFillFromOrder"));
                    return;
                } 
                
                manager.getSample().setOrderId(event.getValue());
                
                try {
                    man = OrderManager.fetchById(event.getValue());
                    if (!OrderManager.TYPE_SEND_OUT.equals(man.getOrder().getType())) {
                        orderNumber.addException(new LocalizedException("orderIdInvalidException"));                           
                        return;
                    }
                } catch (NotFoundException e) {                    
                    orderNumber.addException(new LocalizedException("orderIdInvalidException"));
                    return;
                } catch (Exception ex) {
                    Window.alert(ex.getMessage());
                    return;
                }

                if (wellOrderImport == null)
                    wellOrderImport = new SamplePrivateWellImportOrder();

                try {
                    errors = wellOrderImport.importOrderInfo(event.getValue(), manager);
                    DataChangeEvent.fire(wellScreen);

                    ArrayList<OrderTestViewDO> orderTests = wellOrderImport.getTestsFromOrder(event.getValue());

                    if (orderTests != null && orderTests.size() > 0)
                        ActionEvent.fire(wellScreen, AnalysisTab.Action.ORDER_LIST_ADDED,
                                         orderTests);

                    if (errors != null)
                        showErrors(errors);

                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                }
            }

            public void onStateChange(StateChangeEvent<State> event) {
                orderNumber.enable(event.getState() == State.QUERY ||
                                   (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                        .contains(event.getState())));
                orderNumber.setQueryMode(event.getState() == State.QUERY);
            }
        });

        orderLookup = (AppButton)def.getWidget("orderButton");
        addScreenHandler(orderLookup, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                onOrderLookupClick();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                orderLookup.enable(event.getState() == State.DISPLAY ||
                                   (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                        .contains(event.getState())));
            }
        });

        collectedDate = (CalendarLookUp)def.getWidget(SampleMeta.getCollectionDate());
        addScreenHandler(collectedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                collectedDate.setValue(manager.getSample().getCollectionDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getSample().setCollectionDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectedDate.enable(event.getState() == State.QUERY ||
                                     (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                          .contains(event.getState())));
                collectedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        collectedTime = (TextBox<Datetime>)def.getWidget(SampleMeta.getCollectionTime());
        addScreenHandler(collectedTime, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                collectedTime.setValue(manager.getSample().getCollectionTime());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getSample().setCollectionTime(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectedTime.enable(canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                         .contains(event.getState()));
                collectedTime.setQueryMode(event.getState() == State.QUERY);
            }
        });

        receivedDate = (CalendarLookUp)def.getWidget(SampleMeta.getReceivedDate());
        addScreenHandler(receivedDate, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                receivedDate.setValue(manager.getSample().getReceivedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getSample().setReceivedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                receivedDate.enable(event.getState() == State.QUERY ||
                                    (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                         .contains(event.getState())));
                receivedDate.setQueryMode(event.getState() == State.QUERY);
            }
        });

        statusId = (Dropdown<Integer>)def.getWidget(SampleMeta.getStatusId());
        addScreenHandler(statusId, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                statusId.setSelection(manager.getSample().getStatusId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                manager.getSample().setStatusId(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                statusId.enable(EnumSet.of(State.QUERY).contains(event.getState()));
                statusId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        clientReference = (TextBox)def.getWidget(SampleMeta.getClientReference());
        addScreenHandler(clientReference, new ScreenEventHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                clientReference.setValue(manager.getSample().getClientReference());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                manager.getSample().setClientReference(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                clientReference.enable(event.getState() == State.QUERY ||
                                       (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                            .contains(event.getState())));
                clientReference.setQueryMode(event.getState() == State.QUERY);
            }
        });

        // Get TabPanel and set Tab Selection Handlers
        tabs = (TabPanel)def.getWidget("sampleItemTabPanel");
        tabs.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                int i;

                // tab screen order should be the same as enum or this will
                // not work
                i = event.getItem().intValue();
                tab = Tabs.values()[i];

                window.setBusy(consts.get("loadingMessage"));
                drawTabs();
                window.clearStatus();
            }
        });

        // Set up tabs to recieve State Change events from the main Screen.
        // analysis tree section of the screen
        treeTab = new SampleItemAnalysisTreeTab(def, window, wellScreen);

        addScreenHandler(treeTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                treeTab.setData(manager);
                treeTab.draw();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                treeTab.setState(event.getState());
            }
        });

        // well section of the screen
        try {
            privateWellTab = new PrivateWellTab(def, window);
        } catch (Exception e) {
            Window.alert("well tab initialize: " + e.getMessage());
        }

        addScreenHandler(privateWellTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                privateWellTab.setData(manager);
                privateWellTab.draw();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                privateWellTab.setState(event.getState());
            }
        });

        sampleItemTab = new SampleItemTab(def, window);

        addScreenHandler(sampleItemTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                sampleItemTab.setData(null);

                if (tab == Tabs.SAMPLE_ITEM)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleItemTab.setState(event.getState());
            }
        });

        analysisTab = new AnalysisTab(def, window);

        addScreenHandler(analysisTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                analysisTab.setData(null);

                if (tab == Tabs.ANALYSIS)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisTab.setState(event.getState());
            }
        });

        testResultsTab = new ResultTab(def, window, this);

        addScreenHandler(testResultsTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                testResultsTab.setData(null);

                if (tab == Tabs.TEST_RESULT)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                testResultsTab.setState(event.getState());
            }
        });

        analysisNotesTab = new AnalysisNotesTab(def, window, "anExNotesPanel", "anExNoteButton",
                                                "anIntNotesPanel", "anIntNoteButton");
        addScreenHandler(analysisNotesTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                analysisNotesTab.setData(null);

                if (tab == Tabs.ANALYSIS_NOTES)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisNotesTab.setState(event.getState());
            }
        });

        sampleNotesTab = new SampleNotesTab(def, window, "sampleExtNotesPanel",
                                            "sampleExtNoteButton", "sampleIntNotesPanel",
                                            "sampleIntNoteButton");
        addScreenHandler(sampleNotesTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                sampleNotesTab.setManager(manager);

                if (tab == Tabs.SAMPLE_NOTES)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                sampleNotesTab.setState(event.getState());
            }
        });

        storageTab = new StorageTab(def, window);
        addScreenHandler(storageTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                storageTab.setData(null);

                if (tab == Tabs.STORAGE)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                storageTab.setState(event.getState());
            }
        });

        qaEventsTab = new QAEventsTab(def, window);
        addScreenHandler(qaEventsTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                qaEventsTab.setData(null);
                qaEventsTab.setManager(manager);

                if (tab == Tabs.QA_EVENTS)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                qaEventsTab.setState(event.getState());
            }
        });

        auxDataTab = new AuxDataTab(def, window);
        addScreenHandler(auxDataTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                auxDataTab.setManager(manager);

                if (tab == Tabs.AUX_DATA)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                auxDataTab.setState(event.getState());
            }
        });

        treeTab.addActionHandler(new ActionHandler<SampleItemAnalysisTreeTab.Action>() {
            public void onAction(ActionEvent<SampleItemAnalysisTreeTab.Action> event) {
                if (event.getAction() == SampleItemAnalysisTreeTab.Action.REFRESH_TABS) {
                    SampleDataBundle data = (SampleDataBundle)event.getData();
                    sampleItemTab.setData(data);
                    analysisTab.setData(data);
                    testResultsTab.setData(data);
                    analysisNotesTab.setData(data);
                    storageTab.setData(data);
                    qaEventsTab.setData(data);
                    auxDataTab.setManager(manager);

                    drawTabs();
                }
            }
        });

        sampleItemTab.addActionHandler(new ActionHandler<SampleItemTab.Action>() {
            public void onAction(ActionEvent<SampleItemTab.Action> event) {
                if (state != State.QUERY)
                    ActionEvent.fire(wellScreen, event.getAction(), event.getData());
            }
        });

        analysisTab.addActionHandler(new ActionHandler<AnalysisTab.Action>() {
            public void onAction(ActionEvent<AnalysisTab.Action> event) {
                if (state != State.QUERY)
                    ActionEvent.fire(wellScreen, event.getAction(), event.getData());
            }
        });

        testResultsTab.addActionHandler(new ActionHandler<ResultTab.Action>() {
            public void onAction(ActionEvent<ResultTab.Action> event) {
                if (state != State.QUERY)
                    ActionEvent.fire(wellScreen, event.getAction(), event.getData());
            }
        });

        nav = new ScreenNavigator(def) {
            public void executeQuery(final Query query) {
                window.setBusy(consts.get("querying"));

                query.setRowsPerPage(5);
                service.callList("query", query, new AsyncCallback<ArrayList<IdAccessionVO>>() {
                    public void onSuccess(ArrayList<IdAccessionVO> result) {
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
                            Window.alert("Error: envsample call query failed; " +
                                         error.getMessage());
                            window.setError(consts.get("queryFailed"));
                        }
                    }
                });
            }

            public boolean fetch(RPC entry) {
                return fetchById( (entry == null) ? null : ((IdAccessionVO)entry).getId());
            }

            public ArrayList<TableDataRow> getModel() {
                ArrayList<IdAccessionVO> result;
                ArrayList<TableDataRow> model;

                result = nav.getQueryResult();
                model = new ArrayList<TableDataRow>();
                if (result != null) {
                    for (IdAccessionVO entry : result)
                        model.add(new TableDataRow(entry.getId(), entry.getAccessionNumber()));
                }
                return model;
            }
        };

        //
        // screen fields
        //
        window.addBeforeClosedHandler(new BeforeCloseHandler<ScreenWindow>() {
            public void onBeforeClosed(BeforeCloseEvent<ScreenWindow> event) {
                if (EnumSet.of(State.ADD, State.UPDATE, State.DELETE).contains(state)) {
                    event.cancel();
                    window.setError(consts.get("mustCommitOrAbort"));
                }
            }
        });
    }

    protected void query() {
        manager = SampleManager.getInstance();
        manager.getSample().setDomain(SampleManager.WELL_DOMAIN_FLAG);

        setState(Screen.State.QUERY);
        DataChangeEvent.fire(this);

        // we need to make sure the tabs are cleared
        sampleItemTab.draw();
        analysisTab.draw();
        testResultsTab.draw();
        analysisNotesTab.draw();
        sampleNotesTab.draw();
        storageTab.draw();
        qaEventsTab.draw();
        auxDataTab.draw();

        setFocus(accessionNumber);
        window.setDone(consts.get("enterFieldsToQuery"));
    }

    protected void previous() {
        nav.previous();
    }

    protected void next() {
        nav.next();
    }

    protected void add() {
        NoteViewDO exn;
        
        manager = SampleManager.getInstance();
        manager.getSample().setDomain(SampleManager.WELL_DOMAIN_FLAG);

        // default the form        
        try {
            manager.setDefaults();
            manager.getSample().setReceivedById(UserCache.getPermission().getSystemUserId());
            if (autoNote != null) { 
                exn = manager.getExternalNote().getEditingNote();
                exn.setIsExternal("Y");
                exn.setText(autoNote.getText());
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            return;
        }
        
        setState(Screen.State.ADD);
        DataChangeEvent.fire(this);
        setFocus(accessionNumber);
        window.setDone(consts.get("enterInformationPressCommit"));
    }

    protected void update() {
        window.setBusy(consts.get("lockForUpdate"));

        try {
            manager = manager.fetchForUpdate();
            setState(State.UPDATE);

            DataChangeEvent.fire(this);
            setFocus(orderNumber);
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
        window.clearStatus();
    }

    protected void commit() {
        Query query;

        setFocus(null);
        clearErrors();
        manager.setStatusWithError(false);

        if ( !validate()) {
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
                manager.validate();
                manager = manager.add();

                setState(Screen.State.DISPLAY);
                DataChangeEvent.fire(this);
                window.clearStatus();
            } catch (ValidationErrorsList e) {
                showErrors(e);
                if ( !e.hasErrors() && e.hasWarnings())
                    showWarningsDialog(e);
            } catch (Exception e) {
                Window.alert("commitAdd(): " + e.getMessage());
                window.clearStatus();
            }
        } else if (state == State.UPDATE) {
            window.setBusy(consts.get("updating"));
            try {
                manager.validate();
                manager = manager.update();

                setState(Screen.State.DISPLAY);
                DataChangeEvent.fire(this);
                window.clearStatus();
                quickUpdate = false;
            } catch (ValidationErrorsList e) {
                showErrors(e);
                if ( !e.hasErrors() && e.hasWarnings())
                    showWarningsDialog(e);
            } catch (Exception e) {
                Window.alert("commitUpdate(): " + e.getMessage());
                window.clearStatus();
            }
        }
    }

    protected void commitWithWarnings() {
        clearErrors();
        manager.setStatusWithError(true);

        if (state == State.ADD) {
            window.setBusy(consts.get("adding"));
            try {
                manager = manager.add();

                setState(Screen.State.DISPLAY);
                DataChangeEvent.fire(this);
                window.clearStatus();
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

                setState(Screen.State.DISPLAY);
                DataChangeEvent.fire(this);
                window.clearStatus();
                quickUpdate = false;
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
            manager = SampleManager.getInstance();
            manager.getSample().setDomain(SampleManager.WELL_DOMAIN_FLAG);
            setState(State.DEFAULT);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("queryAborted"));
        } else if (state == State.ADD) {
            manager = SampleManager.getInstance();
            manager.getSample().setDomain(SampleManager.WELL_DOMAIN_FLAG);
            setState(State.DEFAULT);
            DataChangeEvent.fire(this);
            window.setDone(consts.get("addAborted"));
        } else if (state == State.UPDATE) {
            try {
                manager = manager.abortUpdate();

                quickUpdate = false;
                if (SampleManager.QUICK_ENTRY.equals(manager.getSample().getDomain())) {
                    setState(State.DEFAULT);
                    manager = SampleManager.getInstance();
                    manager.getSample().setDomain(SampleManager.WELL_DOMAIN_FLAG);
                } else {
                    setState(State.DISPLAY);
                }
                DataChangeEvent.fire(this);
                window.clearStatus();
            } catch (Exception e) {
                Window.alert(e.getMessage());
                window.clearStatus();
            }
        } else {
            window.clearStatus();
        }
    }

    protected void onOrderLookupClick() {
        Integer id;
        OrderManager man;

        man = null;
        id = manager.getSample().getOrderId();
        if (id == null) {
            man = OrderManager.getInstance();
        } else {
            try {
                man = OrderManager.fetchById(id);
                if ( !OrderManager.TYPE_SEND_OUT.equals(man.getOrder().getType())) {
                    orderNumber.addException(new LocalizedException("orderIdInvalidException"));
                    return;
                }
            } catch (NotFoundException e) {
                orderNumber.addException(new LocalizedException("orderIdInvalidException"));
            } catch (Exception e) {
                Window.alert(e.getMessage());
                e.printStackTrace();
            }
        }

        if (man != null)
            showOrder(man);
    }

    private void showOrder(OrderManager orderManager) {
        ScreenWindow modal;
        try {
            modal = new ScreenWindow(ScreenWindow.Mode.LOOK_UP);
            modal.setName(consts.get("kitOrder"));
            if (sendoutOrderScreen == null)
                sendoutOrderScreen = new SendoutOrderScreen(modal);

            modal.setContent(sendoutOrderScreen);
            sendoutOrderScreen.setManager(orderManager);
        } catch (Throwable e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            window.clearStatus();
        }

    }

    protected boolean fetchById(Integer id) {
        if (id == null) {
            manager = SampleManager.getInstance();
            manager.getSample().setDomain(SampleManager.WELL_DOMAIN_FLAG);

            setState(State.DEFAULT);
        } else {
            window.setBusy(consts.get("fetching"));

            try {
                manager = SampleManager.fetchWithItemsAnalyses(id);

            } catch (Exception e) {
                e.printStackTrace();
                setState(State.DEFAULT);
                Window.alert(consts.get("fetchFailed") + e.getMessage());
                window.clearStatus();
                return false;
            }
            setState(Screen.State.DISPLAY);
        }
        DataChangeEvent.fire(this);
        window.clearStatus();

        return true;
    }

    public ArrayList<QueryData> getQueryFields() {
        int                  i;
        ArrayList<QueryData> fields, auxFields;
        QueryData field;

        fields = super.getQueryFields();

        // add aux data values if necessary
        auxFields = auxDataTab.getQueryFields();
        addPrivateWellFields(fields);

        // add the domain
        field = new QueryData();
        field.key = SampleMeta.getDomain();
        field.query = SampleManager.WELL_DOMAIN_FLAG;
        field.type = QueryData.Type.STRING;
        fields.add(field);

        if (auxFields.size() > 0) {
            // add ref table
            field = new QueryData();
            field.key = SampleMeta.getAuxDataReferenceTableId();
            field.type = QueryData.Type.INTEGER;
            field.query = String.valueOf(ReferenceTable.SAMPLE);
            fields.add(field);

            // add aux fields
            for (i = 0; i < auxFields.size(); i++ ) {
                fields.add(auxFields.get(i));
            }
        }

        return fields;
    }

    private void initializeDropdowns() {
        ArrayList<TableDataRow> model;

        // preload dictionary models and single entries, close the window if an
        // error is found
        try {
            sampleReleasedId = DictionaryCache.getIdBySystemName("sample_released");

            // sample status dropdown
            model = new ArrayList<TableDataRow>();
            model.add(new TableDataRow(null, ""));
            for (DictionaryDO d : CategoryCache.getBySystemName("sample_status"))
                model.add(new TableDataRow(d.getId(), d.getEntry()));

            statusId.setModel(model);

        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
        
        try {
            autoNote = standardNoteService.call("fetchBySystemVariableName", "auto_comment_private_well");
        } catch (NotFoundException nfE) {
            // ignore not found exceptions since this domain may not have a default note
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
    }

    private boolean canEdit() {
        return (manager != null && !sampleReleasedId.equals(manager.getSample().getStatusId()));
    }
    
    private void drawTabs() {
        switch (tab) {
            case SAMPLE_ITEM:
                sampleItemTab.draw();
                break;
            case ANALYSIS:
                analysisTab.draw();
                break;
            case TEST_RESULT:
                testResultsTab.draw();
                break;
            case ANALYSIS_NOTES:
                analysisNotesTab.draw();
                break;
            case SAMPLE_NOTES:
                sampleNotesTab.draw();
                break;
            case STORAGE:
                storageTab.draw();
                break;
            case QA_EVENTS:
                qaEventsTab.draw();
                break;
            case AUX_DATA:
                auxDataTab.draw();
                break;
        }
    }

    public boolean validate() {
        return super.validate() & storageTab.validate();
    }

    public HandlerRegistration addActionHandler(ActionHandler handler) {
        return addHandler(handler, ActionEvent.getType());
    }

    /**
     * We need to add additional fields to the list of queried fields if it
     * contains any field belonging to private well water's report
     * to/organization. This is done in order to make sure that names and
     * addresses belonging to organizations as well as the ones that don't are
     * searched.
     */
    private void addPrivateWellFields(ArrayList<QueryData> fields) {
        int size;
        String dataKey, orgName, addressMult, addressStreet, addressCity, addressState, addressZip, addressWorkPhone, addressFaxPhone;
        QueryData data;

        orgName = null;
        addressMult = null;
        addressStreet = null;
        addressCity = null;
        addressState = null;
        addressZip = null;
        addressWorkPhone = null;
        addressFaxPhone = null;

        size = fields.size();
        for (int i = size - 1; i >= 0; i-- ) {
            data = fields.get(i);
            dataKey = data.key;

            if (SampleMeta.getWellOrganizationName().equals(dataKey)) {
                orgName = data.query;

                data = new QueryData();
                data.key = SampleMeta.getWellReportToName();
                data.type = QueryData.Type.STRING;
                data.query = orgName;
                fields.add(data);
            } else if (SampleMeta.getWellReportToAddressMultipleUnit().equals(dataKey)) {
                addressMult = data.query;

                data = new QueryData();
                data.key = SampleMeta.getAddressMultipleUnit();
                data.type = QueryData.Type.STRING;
                data.query = addressMult;
                fields.add(data);
            } else if (SampleMeta.getWellReportToAddressStreetAddress().equals(dataKey)) {
                addressStreet = data.query;

                data = new QueryData();
                data.key = SampleMeta.getAddressStreetAddress();
                data.type = QueryData.Type.STRING;
                data.query = addressStreet;
                fields.add(data);
            } else if (SampleMeta.getWellReportToAddressCity().equals(dataKey)) {
                addressCity = data.query;

                data = new QueryData();
                data.key = SampleMeta.getAddressCity();
                data.type = QueryData.Type.STRING;
                data.query = addressCity;
                fields.add(data);
            } else if (SampleMeta.getWellReportToAddressState().equals(dataKey)) {
                addressState = data.query;

                data = new QueryData();
                data.key = SampleMeta.getAddressState();
                data.type = QueryData.Type.STRING;
                data.query = addressState;
                fields.add(data);
            } else if (SampleMeta.getWellReportToAddressZipCode().equals(dataKey)) {
                addressZip = data.query;

                data = new QueryData();
                data.key = SampleMeta.getAddressZipCode();
                data.type = QueryData.Type.STRING;
                data.query = addressZip;
                fields.add(data);
            } else if (SampleMeta.getWellReportToAddressWorkPhone().equals(dataKey)) {
                addressWorkPhone = data.query;

                data = new QueryData();
                data.key = SampleMeta.getAddressWorkPhone();
                data.type = QueryData.Type.STRING;
                data.query = addressWorkPhone;
                fields.add(data);
            } else if (SampleMeta.getWellReportToAddressFaxPhone().equals(dataKey)) {
                addressFaxPhone = data.query;

                data = new QueryData();
                data.key = SampleMeta.getAddressFaxPhone();
                data.type = QueryData.Type.STRING;
                data.query = addressFaxPhone;
                fields.add(data);
            }
        }

    }
}
