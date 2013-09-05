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
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdAccessionVO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SamplePrivateWellViewDO;
import org.openelis.domain.StandardNoteDO;
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
import org.openelis.gwt.widget.CalendarLookUp;
import org.openelis.gwt.widget.Dropdown;
import org.openelis.gwt.widget.MenuItem;
import org.openelis.gwt.widget.ScreenWindow;
import org.openelis.gwt.widget.TextBox;
import org.openelis.gwt.widget.table.TableDataRow;
import org.openelis.manager.OrderManager;
import org.openelis.manager.SampleDataBundle;
import org.openelis.manager.SampleItemManager;
import org.openelis.manager.SampleManager;
import org.openelis.manager.SampleOrganizationManager;
import org.openelis.manager.SamplePrivateWellManager;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.order.client.SendoutOrderScreen;
import org.openelis.modules.sample.client.AccessionNumberUtility;
import org.openelis.modules.sample.client.AnalysisNotesTab;
import org.openelis.modules.sample.client.AnalysisTab;
import org.openelis.modules.sample.client.AuxDataTab;
import org.openelis.modules.sample.client.PrivateWellTab;
import org.openelis.modules.sample.client.QAEventsTab;
import org.openelis.modules.sample.client.ResultTab;
import org.openelis.modules.sample.client.SampleDuplicateUtil;
import org.openelis.modules.sample.client.SampleHistoryUtility;
import org.openelis.modules.sample.client.SampleItemAnalysisTreeTab;
import org.openelis.modules.sample.client.SampleItemTab;
import org.openelis.modules.sample.client.SampleMergeUtility;
import org.openelis.modules.sample.client.SampleNotesTab;
import org.openelis.modules.sample.client.SampleOrganizationUtility;
import org.openelis.modules.sample.client.SamplePrivateWellImportOrder;
import org.openelis.modules.sample.client.SampleService;
import org.openelis.modules.sample.client.StorageTab;
import org.openelis.modules.standardnote.client.StandardNoteService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.LastPageException;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.Util;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

public class PrivateWellWaterSampleLoginScreen extends Screen implements
                                                             HasActionHandlers {
    private boolean                           quickUpdate;
    private SampleManager                     manager, previousManager;
    protected Tabs                            tab;
    private PrivateWellWaterSampleLoginScreen screen;
    private SampleItemAnalysisTreeTab         treeTab;
    private PrivateWellTab                    privateWellTab;
    private SampleItemTab                     sampleItemTab;
    private AnalysisTab                       analysisTab;
    private ResultTab                         testResultsTab;
    private AnalysisNotesTab                  analysisNotesTab;
    private SampleNotesTab                    sampleNotesTab;
    private StorageTab                        storageTab;
    private QAEventsTab                       qaEventsTab;
    private AuxDataTab                        auxDataTab;

    protected AccessionNumberUtility          accessionNumUtil;
    protected SampleHistoryUtility            historyUtility;

    protected TextBox                         clientReference;
    protected TextBox<Integer>                accessionNumber, orderNumber;
    protected TextBox<Datetime>               collectedTime;
    protected Dropdown<Integer>               statusId;
    protected CalendarLookUp                  collectedDate, receivedDate;
    protected AppButton                       queryButton, addButton, updateButton,
                    nextButton, prevButton, commitButton, abortButton, orderLookup;
    protected MenuItem                        duplicate, historySample,
                    historySamplePrivateWell, historySampleProject, historySampleItem,
                    historyAnalysis, historyCurrentResult, historyStorage,
                    historySampleQA, historyAnalysisQA, historyAuxData;
    protected TabPanel                        tabs;

    private ScreenNavigator                   nav;
    private ModulePermission                  userPermission;
    private SendoutOrderScreen                sendoutOrderScreen;
    private StandardNoteDO                    autoNote;

    protected SamplePrivateWellImportOrder    wellOrderImport;

    private enum Tabs {
        SAMPLE_ITEM, ANALYSIS, TEST_RESULT, ANALYSIS_NOTES, SAMPLE_NOTES, STORAGE,
        QA_EVENTS, AUX_DATA
    };

    public PrivateWellWaterSampleLoginScreen(WindowInt window) throws Exception {
        super((ScreenDefInt)GWT.create(PrivateWellWaterSampleLoginDef.class));
        
        setWindow(window);

        userPermission = UserCache.getPermission().getModule("sampleprivatewell");
        if (userPermission == null)
            throw new PermissionException(Messages.get().screenPermException("Private Well Water Sample Login Screen"));

        tab = Tabs.SAMPLE_ITEM;
        manager = SampleManager.getInstance();
        manager.getSample().setDomain(SampleManager.WELL_DOMAIN_FLAG);
        quickUpdate = false;

        try {
            CategoryCache.getBySystemNames("sample_status",
                                           "user_action",
                                           "analysis_status",
                                           "type_of_sample",
                                           "source_of_sample",
                                           "sample_container",
                                           "unit_of_measure",
                                           "qaevent_type",
                                           "aux_field_value_type",
                                           "worksheet_status");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }

        initialize();
        initializeDropdowns();

        setDataInTabs();
        setState(State.DEFAULT);
        DataChangeEvent.fire(this);
    }

    private void initialize() {
        screen = this;

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

        historyUtility = new SampleHistoryUtility(window);

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
                historySampleProject.enable(EnumSet.of(State.DISPLAY)
                                                   .contains(event.getState()));
            }
        });

        historySampleItem = (MenuItem)def.getWidget("historySampleItem");
        addScreenHandler(historySampleItem, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.setManager(manager);
                historyUtility.historySampleItem();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historySampleItem.enable(EnumSet.of(State.DISPLAY)
                                                .contains(event.getState()));
            }
        });

        historyAnalysis = (MenuItem)def.getWidget("historyAnalysis");
        addScreenHandler(historyAnalysis, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.setManager(manager);
                historyUtility.historyAnalysis();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historyAnalysis.enable(EnumSet.of(State.DISPLAY)
                                              .contains(event.getState()));
            }
        });

        historyCurrentResult = (MenuItem)def.getWidget("historyCurrentResult");
        addScreenHandler(historyCurrentResult, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.setManager(manager);
                /*
                 * Since the analyses shown on this screen are managed by
                 * SampleItemAnalysisTreeTab, this screen has no knowledge of
                 * the analysis selected by the user to see the history of its
                 * results. So an ActionEvent is fired so that
                 * SampleItemAnalysisTreeTab can find the desired analysis and
                 * show the history.
                 */
                ActionEvent.fire(screen, ResultTab.Action.RESULT_HISTORY, null);
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historyCurrentResult.enable(EnumSet.of(State.DISPLAY)
                                                   .contains(event.getState()));
            }
        });

        historyStorage = (MenuItem)def.getWidget("historyStorage");
        addScreenHandler(historyStorage, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.setManager(manager);
                historyUtility.historyStorage();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historyStorage.enable(EnumSet.of(State.DISPLAY)
                                             .contains(event.getState()));
            }
        });

        historySampleQA = (MenuItem)def.getWidget("historySampleQA");
        addScreenHandler(historySampleQA, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.setManager(manager);
                historyUtility.historySampleQA();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historySampleQA.enable(EnumSet.of(State.DISPLAY)
                                              .contains(event.getState()));
            }
        });

        historyAnalysisQA = (MenuItem)def.getWidget("historyAnalysisQA");
        addScreenHandler(historyAnalysisQA, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.setManager(manager);
                historyUtility.historyAnalysisQA();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historyAnalysisQA.enable(EnumSet.of(State.DISPLAY)
                                                .contains(event.getState()));
            }
        });

        historyAuxData = (MenuItem)def.getWidget("historyAuxData");
        addScreenHandler(historyAuxData, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                historyUtility.setManager(manager);
                historyUtility.historyAuxData();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                historyAuxData.enable(EnumSet.of(State.DISPLAY)
                                             .contains(event.getState()));
            }
        });

        accessionNumber = (TextBox<Integer>)def.getWidget(SampleMeta.getAccessionNumber());
        addScreenHandler(accessionNumber, new ScreenEventHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                accessionNumber.setValue(Util.toString(manager.getSample()
                                                              .getAccessionNumber()));
            }

            public void onValueChange(final ValueChangeEvent<Integer> event) {
                Integer oldNumber, orderId;
                SampleManager quickEntryMan;
                NoteViewDO exn;

                oldNumber = manager.getSample().getAccessionNumber();
                if (oldNumber != null) {
                    if (quickUpdate) {
                        Window.alert(Messages.get().cantChangeQuickEntryAccessionNumber());
                        accessionNumber.setValue(Util.toString(oldNumber));
                        setFocus(accessionNumber);
                        return;
                    } else if ( !Window.confirm(Messages.get().accessionNumberEditConfirm())) {
                        accessionNumber.setValue(Util.toString(oldNumber));
                        setFocus(accessionNumber);
                        return;
                    }
                }

                try {
                    manager.getSample().setAccessionNumber(event.getValue());

                    if (accessionNumUtil == null)
                        accessionNumUtil = new AccessionNumberUtility();

                    window.setBusy(Messages.get().fetching());
                    quickEntryMan = accessionNumUtil.validateAccessionNumber(manager.getSample());

                    if (quickEntryMan == null) {
                        window.clearStatus();
                        return;
                    } else if (manager.getSample().getOrderId() != null) {
                        Window.alert(Messages.get().cantLoadQEIfOrderNumPresent());
                        quickEntryMan.abortUpdate();
                        accessionNumber.setValue(Util.toString(oldNumber));
                        setFocus(accessionNumber);
                        window.clearStatus();
                        return;
                    }

                    if (state == State.ADD) {
                        orderId = manager.getSample().getOrderId();
                        if (orderId != null) {
                            SampleMergeUtility.mergeTests(manager, quickEntryMan);
                            manager.setSample(quickEntryMan.getSample());
                            manager.getSample().setOrderId(orderId);
                        } else {
                            manager = quickEntryMan;
                        }

                        manager.getSample().setDomain(SampleManager.WELL_DOMAIN_FLAG);
                        manager.createEmptyDomainManager();

                        /*
                         * We add the standard note, if any, defined through a
                         * system variable for this domain, because it isn't
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
                                setDataInTabs();
                                setState(State.UPDATE);
                                DataChangeEvent.fire(screen);
                                window.clearStatus();
                                quickUpdate = true;
                            }
                        });
                    } else {
                        quickEntryMan.abortUpdate();
                        window.clearStatus();
                        throw new Exception(Messages.get().quickEntryNumberExists());
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
                window.clearStatus();
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
                importOrder(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                orderNumber.enable(event.getState() == State.QUERY ||
                                   (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                        .contains(event.getState())));
                orderNumber.setQueryMode(event.getState() == State.QUERY);
            }
        });

        orderNumber.addKeyDownHandler(new KeyDownHandler() {
            public void onKeyDown(KeyDownEvent event) {
                Integer orderId, prevOrderId;

                if (canCopyFromPrevious(event)) {
                    orderId = manager.getSample().getOrderId();
                    prevOrderId = previousManager.getSample().getOrderId();
                    /*
                     * we don't want to incur the cost of importing the order if
                     * the order id in the previous manager is the same as the
                     * one in the current manager
                     */
                    if ( !DataBaseUtil.isSame(orderId, prevOrderId)) {
                        importOrder(prevOrderId);
                        orderNumber.setValue(Util.toString(prevOrderId));
                    }
                    event.preventDefault();
                    event.stopPropagation();
                    setFocusToNext(orderNumber);
                }
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

        collectedDate.addKeyDownHandler(new KeyDownHandler() {
            public void onKeyDown(KeyDownEvent event) {
                Datetime dt;

                if (canCopyFromPrevious(event)) {
                    dt = previousManager.getSample().getCollectionDate();
                    manager.getSample().setCollectionDate(dt);
                    collectedDate.setValue(dt);

                    event.preventDefault();
                    event.stopPropagation();
                    setFocusToNext(collectedDate);
                }
            }
        });

        collectedTime = (TextBox<Datetime>)def.getWidget(SampleMeta.getCollectionTime());
        addScreenHandler(collectedTime, new ScreenEventHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                collectedTime.setFieldValue(manager.getSample().getCollectionTime());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                manager.getSample().setCollectionTime(event.getValue());
            }

            public void onStateChange(StateChangeEvent<State> event) {
                collectedTime.enable(canEdit() &&
                                     EnumSet.of(State.ADD, State.UPDATE)
                                            .contains(event.getState()));
                collectedTime.setQueryMode(event.getState() == State.QUERY);
            }
        });

        collectedTime.addKeyDownHandler(new KeyDownHandler() {
            public void onKeyDown(KeyDownEvent event) {
                Datetime dt;

                if (canCopyFromPrevious(event)) {
                    dt = previousManager.getSample().getCollectionTime();
                    manager.getSample().setCollectionTime(dt);
                    collectedTime.setFieldValue(dt);

                    event.preventDefault();
                    event.stopPropagation();
                    setFocusToNext(collectedTime);
                }
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

        receivedDate.addKeyDownHandler(new KeyDownHandler() {
            public void onKeyDown(KeyDownEvent event) {
                Datetime dt;

                if (canCopyFromPrevious(event)) {
                    dt = previousManager.getSample().getReceivedDate();
                    manager.getSample().setReceivedDate(dt);
                    receivedDate.setValue(dt);

                    event.preventDefault();
                    event.stopPropagation();
                    setFocusToNext(receivedDate);
                }
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

        clientReference.addKeyDownHandler(new KeyDownHandler() {
            public void onKeyDown(KeyDownEvent event) {
                String cr;

                if (canCopyFromPrevious(event)) {
                    cr = previousManager.getSample().getClientReference();
                    manager.getSample().setClientReference(cr);
                    clientReference.setValue(cr);

                    event.preventDefault();
                    event.stopPropagation();
                    setFocusToNext(clientReference);
                }
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

                window.setBusy(Messages.get().loadingMessage());
                drawTabs();
                window.clearStatus();
            }
        });

        // Set up tabs to recieve State Change events from the main Screen.
        // analysis tree section of the screen
        treeTab = new SampleItemAnalysisTreeTab(def, window, screen, historyUtility);

        addScreenHandler(treeTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                treeTab.draw();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                treeTab.setState(event.getState());
            }
        });

        /*
         * This handler would be invoked when the tree in the tree tab needs to
         * be refreshed due to new prep tests being added e.g. on importing an
         * order. The handler above isn't used for this purpose because it
         * responds to the DataChangeEvent fired by this screen and not by the
         * tree utility. The utility doesn't make a DataChangeEvent be fired by
         * the screen because the above handler would be invoked and it would
         * need to reset the manager in the tab, which is not the desired
         * default behavior. The data in the tab in the default case is set by
         * setDataInTabs() due to other issues.
         */
        treeTab.getTreeUtil().addScreenHandler(screen, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                treeTab.setData(manager);
                treeTab.draw();
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
                privateWellTab.draw();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                privateWellTab.setState(event.getState());
            }
        });

        sampleItemTab = new SampleItemTab(def, window);

        addScreenHandler(sampleItemTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
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
                if (tab == Tabs.TEST_RESULT)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                testResultsTab.setState(event.getState());
            }
        });

        analysisNotesTab = new AnalysisNotesTab(def,
                                                window,
                                                "anExNotesPanel",
                                                "anExNoteButton",
                                                "anIntNotesPanel",
                                                "anIntNoteButton");
        addScreenHandler(analysisNotesTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                if (tab == Tabs.ANALYSIS_NOTES)
                    drawTabs();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                analysisNotesTab.setState(event.getState());
            }
        });

        sampleNotesTab = new SampleNotesTab(def,
                                            window,
                                            "sampleExtNotesPanel",
                                            "sampleExtNoteButton",
                                            "sampleIntNotesPanel",
                                            "sampleIntNoteButton");
        addScreenHandler(sampleNotesTab, new ScreenEventHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
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
                    ActionEvent.fire(screen, event.getAction(), event.getData());
            }
        });

        analysisTab.addActionHandler(new ActionHandler<AnalysisTab.Action>() {
            public void onAction(ActionEvent<AnalysisTab.Action> event) {
                if (state != State.QUERY &&
                    event.getAction() != AnalysisTab.Action.UNIT_CHANGED)
                    ActionEvent.fire(screen, event.getAction(), event.getData());
            }
        });

        /*
         * The action UNIT_CHANGED fired by AnalysisTab affects only ResultTab
         * and no other so it doesn't need to be processed by the above handler
         * which makes SampleItemAnalysisTreeTab to respond to those actions.
         */
        analysisTab.addActionHandler(testResultsTab);

        testResultsTab.addActionHandler(new ActionHandler<ResultTab.Action>() {
            public void onAction(ActionEvent<ResultTab.Action> event) {
                if (state != State.QUERY)
                    ActionEvent.fire(screen, event.getAction(), event.getData());
            }
        });

        nav = new ScreenNavigator<IdAccessionVO>(def) {
            public void executeQuery(final Query query) {
                window.setBusy(Messages.get().querying());

                query.setRowsPerPage(5);
                SampleService.get().query(query, new AsyncCallback<ArrayList<IdAccessionVO>>() {
                    public void onSuccess(ArrayList<IdAccessionVO> result) {
                        setQueryResult(result);
                    }

                                     public void onFailure(Throwable error) {
                                         setQueryResult(null);
                                         if (error instanceof NotFoundException) {
                                             window.setDone(Messages.get().noRecordsFound());
                                             setState(State.DEFAULT);
                                         } else if (error instanceof LastPageException) {
                                             window.setError("No more records in this direction");
                                         } else {
                                             Window.alert("Error: envsample call query failed; " +
                                                          error.getMessage());
                                             window.setError(Messages.get().queryFailed());
                                         }
                                     }
                                 });
            }

            public boolean fetch(IdAccessionVO entry) {
                return fetchById( (entry == null) ? null : entry.getId());
            }

            public ArrayList<TableDataRow> getModel() {
                ArrayList<IdAccessionVO> result;
                ArrayList<TableDataRow> model;

                result = nav.getQueryResult();
                model = new ArrayList<TableDataRow>();
                if (result != null) {
                    for (IdAccessionVO entry : result)
                        model.add(new TableDataRow(entry.getId(),
                                                   entry.getAccessionNumber()));
                }
                return model;
            }
        };

        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                if (EnumSet.of(State.ADD, State.UPDATE, State.DELETE).contains(state)) {
                    event.cancel();
                    window.setError(Messages.get().mustCommitOrAbort());
                }
            }
        });
    }

    protected void query() {
        manager = SampleManager.getInstance();
        manager.getSample().setDomain(SampleManager.WELL_DOMAIN_FLAG);

        setDataInTabs();
        setState(State.QUERY);
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
        window.setDone(Messages.get().enterFieldsToQuery());
    }

    protected void previous() {
        nav.previous();
    }

    protected void next() {
        nav.next();
    }

    protected void add() {
        previousManager = manager;
        manager = SampleManager.getInstance();
        manager.getSample().setDomain(SampleManager.WELL_DOMAIN_FLAG);

        // default the form
        try {
            manager.setDefaults();
            manager.getSample().setReceivedById(UserCache.getPermission()
                                                         .getSystemUserId());
            setDefaults();
        } catch (Exception e) {
            Window.alert(e.getMessage());
            return;
        }

        setDataInTabs();
        setState(State.ADD);
        DataChangeEvent.fire(this);
        setFocus(accessionNumber);
        window.setDone(Messages.get().enterInformationPressCommit());
    }

    protected void update() {
        window.setBusy(Messages.get().lockForUpdate());

        try {
            manager = manager.fetchForUpdate();

            setDataInTabs();
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
            window.setError(Messages.get().correctErrors());
            return;
        }

        if (state == State.QUERY) {
            query = new Query();
            query.setFields(getQueryFields());

            nav.setQuery(query);
        } else if (state == State.ADD) {
            window.setBusy(Messages.get().adding());
            try {
                manager.validate();
                manager = manager.add();

                setDataInTabs();
                setState(State.DISPLAY);
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
            window.setBusy(Messages.get().updating());
            try {
                manager.validate();
                manager = manager.update();

                setDataInTabs();
                setState(State.DISPLAY);
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
            window.setBusy(Messages.get().adding());
            try {
                manager = manager.add();

                setDataInTabs();
                setState(State.DISPLAY);
                DataChangeEvent.fire(this);
                window.clearStatus();
            } catch (ValidationErrorsList e) {
                showErrors(e);
            } catch (Exception e) {
                Window.alert("commitAdd(): " + e.getMessage());
                window.clearStatus();
            }
        } else if (state == State.UPDATE) {
            window.setBusy(Messages.get().updating());
            try {
                manager = manager.update();

                setDataInTabs();
                setState(State.DISPLAY);
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
        window.setBusy(Messages.get().cancelChanges());

        if (state == State.QUERY) {
            manager = SampleManager.getInstance();
            manager.getSample().setDomain(SampleManager.WELL_DOMAIN_FLAG);

            setDataInTabs();
            setState(State.DEFAULT);
            DataChangeEvent.fire(this);
            window.setDone(Messages.get().queryAborted());
        } else if (state == State.ADD) {
            manager = SampleManager.getInstance();
            manager.getSample().setDomain(SampleManager.WELL_DOMAIN_FLAG);

            setDataInTabs();
            setState(State.DEFAULT);
            DataChangeEvent.fire(this);
            window.setDone(Messages.get().addAborted());
        } else if (state == State.UPDATE) {
            try {
                manager = manager.abortUpdate();

                quickUpdate = false;
                if (SampleManager.QUICK_ENTRY.equals(manager.getSample().getDomain())) {
                    manager = SampleManager.getInstance();
                    manager.getSample()
                           .setDomain(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG);

                    setDataInTabs();
                    setState(State.DEFAULT);
                } else {
                    setDataInTabs();
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

    protected void duplicate() {
        try {
            window.setBusy(Messages.get().fetching());
            manager = SampleManager.fetchWithAllDataById(manager.getSample().getId());
            if ( !SampleManager.WELL_DOMAIN_FLAG.equals(manager.getSample().getDomain())) {
                Window.alert(Messages.get().sampleDomainChangedException());
                abort();
                return;
            }
            previousManager = manager;
            manager = SampleDuplicateUtil.duplicate(manager);

            setDataInTabs();
            setState(State.ADD);

            treeTab.draw();
            privateWellTab.draw();
            storageTab.draw();
            auxDataTab.draw();
            sampleNotesTab.draw();

            DataChangeEvent.fire(this);

            setFocus(accessionNumber);
            window.setDone(Messages.get().enterInformationPressCommit());
        } catch (Exception e) {
            Window.alert("Sample duplicate: " + e.getMessage());
            e.printStackTrace();
            abort();
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
                    orderNumber.addException(new Exception(Messages.get().orderIdInvalidException()));
                    return;
                }
            } catch (NotFoundException e) {
                orderNumber.addException(new Exception(Messages.get().orderIdInvalidException()));
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
            modal.setName(Messages.get().sendoutOrder());
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

            setDataInTabs();
            setState(State.DEFAULT);
        } else {
            window.setBusy(Messages.get().fetching());

            try {
                manager = SampleManager.fetchWithItemsAnalyses(id);

            } catch (Exception e) {
                e.printStackTrace();
                setState(State.DEFAULT);
                Window.alert(Messages.get().fetchFailed() + e.getMessage());
                window.clearStatus();
                return false;
            }
            setDataInTabs();
            setState(State.DISPLAY);
        }
        DataChangeEvent.fire(this);
        window.clearStatus();

        return true;
    }

    public ArrayList<QueryData> getQueryFields() {
        int i;
        ArrayList<QueryData> fields, auxFields;
        QueryData field;

        fields = super.getQueryFields();

        // add aux data values if necessary
        auxFields = auxDataTab.getQueryFields();
        addPrivateWellFields(fields);

        // add the domain
        field = new QueryData();
        field.setKey(SampleMeta.getDomain());
        field.setQuery(SampleManager.WELL_DOMAIN_FLAG);
        field.setType(QueryData.Type.STRING);
        fields.add(field);

        if (auxFields.size() > 0) {
            // add ref table
            field = new QueryData();
            field.setKey(SampleMeta.getAuxDataReferenceTableId());
            field.setType(QueryData.Type.INTEGER);
            field.setQuery(String.valueOf(Constants.table().SAMPLE));
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

        // sample status dropdown
        model = new ArrayList<TableDataRow>();
        model.add(new TableDataRow(null, ""));
        for (DictionaryDO d : CategoryCache.getBySystemName("sample_status"))
            model.add(new TableDataRow(d.getId(), d.getEntry()));

        statusId.setModel(model);

        try {
            autoNote = StandardNoteService.get().fetchBySystemVariableName("auto_comment_private_well");
        } catch (NotFoundException nfE) {
            // ignore not found exceptions since this domain may not have a
            // default note
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.close();
        }
    }

    private boolean canEdit() {
        return (manager != null && !Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample()
                                                                                         .getStatusId()));
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

    private void setDefaults() throws Exception {
        NoteViewDO exn;

        if (autoNote != null) {
            exn = manager.getExternalNote().getEditingNote();
            exn.setIsExternal("Y");
            exn.setText(autoNote.getText());
        }
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
            dataKey = data.getKey();

            if (SampleMeta.getWellOrganizationName().equals(dataKey)) {
                orgName = data.getQuery();

                data = new QueryData();
                data.setKey(SampleMeta.getWellReportToName());
                data.setType(QueryData.Type.STRING);
                data.setQuery(orgName);
                fields.add(data);
            } else if (SampleMeta.getWellReportToAddressMultipleUnit().equals(dataKey)) {
                addressMult = data.getQuery();

                data = new QueryData();
                data.setKey(SampleMeta.getAddressMultipleUnit());
                data.setType(QueryData.Type.STRING);
                data.setQuery(addressMult);
                fields.add(data);
            } else if (SampleMeta.getWellReportToAddressStreetAddress().equals(dataKey)) {
                addressStreet = data.getQuery();

                data = new QueryData();
                data.setKey(SampleMeta.getAddressStreetAddress());
                data.setType(QueryData.Type.STRING);
                data.setQuery(addressStreet);
                fields.add(data);
            } else if (SampleMeta.getWellReportToAddressCity().equals(dataKey)) {
                addressCity = data.getQuery();

                data = new QueryData();
                data.setKey(SampleMeta.getAddressCity());
                data.setType(QueryData.Type.STRING);
                data.setQuery(addressCity);
                fields.add(data);
            } else if (SampleMeta.getWellReportToAddressState().equals(dataKey)) {
                addressState = data.getQuery();

                data = new QueryData();
                data.setKey(SampleMeta.getAddressState());
                data.setType(QueryData.Type.STRING);
                data.setQuery(addressState);
                fields.add(data);
            } else if (SampleMeta.getWellReportToAddressZipCode().equals(dataKey)) {
                addressZip = data.getQuery();

                data = new QueryData();
                data.setKey(SampleMeta.getAddressZipCode());
                data.setType(QueryData.Type.STRING);
                data.setQuery(addressZip);
                fields.add(data);
            } else if (SampleMeta.getWellReportToAddressWorkPhone().equals(dataKey)) {
                addressWorkPhone = data.getQuery();

                data = new QueryData();
                data.setKey(SampleMeta.getAddressWorkPhone());
                data.setType(QueryData.Type.STRING);
                data.setQuery(addressWorkPhone);
                fields.add(data);
            } else if (SampleMeta.getWellReportToAddressFaxPhone().equals(dataKey)) {
                addressFaxPhone = data.getQuery();

                data = new QueryData();
                data.setKey(SampleMeta.getAddressFaxPhone());
                data.setType(QueryData.Type.STRING);
                data.setQuery(addressFaxPhone);
                fields.add(data);
            }
        }
    }

    private boolean canCopyFromPrevious(KeyDownEvent event) {
        return (previousManager != null) && (event.getNativeKeyCode() == 113);
    }

    private void importOrder(Integer orderId) {
        int i;
        Integer orgId;
        ArrayList<Integer> orgIds;
        OrderManager man;
        SampleManager quickEntryMan;
        SampleItemManager itemMan;
        SampleOrganizationManager sorgMan;
        SampleOrganizationViewDO sorg;
        SamplePrivateWellManager orderPWMan, qePWMan;
        ValidationErrorsList errors;
        SamplePrivateWellViewDO well;

        if (orderId == null) {
            manager.getSample().setOrderId(orderId);
            return;
        }

        try {
            if (manager.getSample().getAccessionNumber() == null) {
                Window.alert(Messages.get().enterAccNumBeforeOrderLoad());
                orderNumber.setFieldValue(null);
                return;
            }

            window.setBusy(Messages.get().fetching());

            man = OrderManager.fetchById(orderId);
            if ( !OrderManager.TYPE_SEND_OUT.equals(man.getOrder().getType())) {
                orderNumber.addException(new Exception(Messages.get().orderIdInvalidException()));
                window.clearStatus();
                return;
            }
        } catch (NotFoundException e) {
            orderNumber.addException(new Exception(Messages.get().orderIdInvalidException()));
            window.clearStatus();
            return;
        } catch (Exception ex) {
            Window.alert(ex.getMessage());
            window.clearStatus();
            return;
        }

        try {
            if (wellOrderImport == null)
                wellOrderImport = new SamplePrivateWellImportOrder();

            quickEntryMan = null;
            if (quickUpdate) {
                /*
                 * keep track of the manager loaded through quick entry in order
                 * to be able to merge any sample items and tests present in it
                 * with the ones added from the order
                 */
                quickEntryMan = manager;
                manager = SampleManager.getInstance();
                manager.setSample(quickEntryMan.getSample());
                manager.createEmptyDomainManager();
                qePWMan = ((SamplePrivateWellManager)quickEntryMan.getDomainManager());
                orderPWMan = ((SamplePrivateWellManager)manager.getDomainManager());
                orderPWMan.setPrivateWell(qePWMan.getPrivateWell());

                itemMan = manager.getSampleItems();

                /*
                 * any existing sample items and tests in the manager created
                 * through quick entry are removed before loading the sample
                 * items and tests from the order so that after the order has
                 * been loaded, only the tests loaded from the order, which are
                 * treated as the base case for merging the tests, can be
                 * present when the two sets of tests are merged later
                 */
                while (itemMan.count() > 0)
                    itemMan.removeSampleItemAt(0);

                manager.getSample().setNextItemSequence(0);
                
                /*
                 * We need to copy the initial external note, if any, from the
                 * quick entry manager
                 */
                manager.getExternalNote().addNote(quickEntryMan.getExternalNote().getEditingNote());
            }

            errors = wellOrderImport.importOrderInfo(orderId, manager);
            
            if (quickEntryMan != null)
                SampleMergeUtility.mergeTests(manager, quickEntryMan);
            
            manager.getSample().setOrderId(orderId);
            setDataInTabs();
            DataChangeEvent.fire(screen);
            window.clearStatus();

            ActionEvent.fire(screen, AnalysisTab.Action.ORDER_LIST_ADDED, null);

            if (errors != null && errors.size() > 0)
                showErrors(errors);

            /*
             * check to see if any of the sample organizations has been marked
             * for holding or refusing samples from
             */
            orgIds = new ArrayList<Integer>();
            well = ((SamplePrivateWellManager)manager.getDomainManager()).getPrivateWell();
            orgId = well.getOrganizationId();
            if (orgId != null) {
                showHoldRefuseWarning(orgId, well.getOrganization().getName());
                orgIds.add(orgId);
            }

            /*
             * check to see if any of the sample organizations has been marked
             * for holding or refusing samples from
             */
            sorgMan = manager.getOrganizations();
            orgIds = new ArrayList<Integer>();
            for (i = 0; i < sorgMan.count(); i++ ) {
                sorg = sorgMan.getOrganizationAt(i);
                orgId = sorg.getOrganizationId();
                if ( !orgIds.contains(orgId)) {
                    if (SampleOrganizationUtility.isHoldRefuseSampleForOrg(orgId))
                        Window.alert(Messages.get().orgMarkedAsHoldRefuseSample(sorg.getOrganizationName()));
                    orgIds.add(orgId);
                }
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.clearStatus();
        }
    }

    private void setFocusToNext(Widget currWidget) {
        NativeEvent pressEvent;

        pressEvent = Document.get().createKeyPressEvent(false,
                                                        false,
                                                        false,
                                                        false,
                                                        KeyCodes.KEY_TAB,
                                                        KeyCodes.KEY_TAB);
        KeyPressEvent.fireNativeEvent(pressEvent, currWidget);
    }

    /**
     * If the status of the sample showing on the screen is changed from
     * Released to something else and on changing the state, the status stays
     * Released and the widgets in the tabs stay disabled. Also, if the status
     * changes from something else to Released, the widgets are not disabled.
     * This is because the data in the tabs is set in their handlers of
     * DataChangeEvent which is fired after StateChangeEvent and the handlers of
     * the latter in the widgets are responsible for enabling or disabling the
     * widgets. That is why we need to set the data in the tabs before changing
     * the state.
     */
    private void setDataInTabs() {
        treeTab.setData(manager);
        privateWellTab.setData(manager);
        privateWellTab.setPreviousData(previousManager);
        sampleItemTab.setData(null);
        analysisTab.setData(null);
        testResultsTab.setData(null);
        analysisNotesTab.setData(null);
        sampleNotesTab.setManager(manager);
        storageTab.setData(null);
        qaEventsTab.setData(null);
        qaEventsTab.setManager(manager);
        auxDataTab.setManager(manager);
    }

    private void showHoldRefuseWarning(Integer orgId, String name) throws Exception {
        if (SampleOrganizationUtility.isHoldRefuseSampleForOrg(orgId))
            Window.alert(Messages.get().orgMarkedAsHoldRefuseSample(name));
    }
}