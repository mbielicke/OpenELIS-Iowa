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
package org.openelis.modules.environmentalSampleLogin1.client;

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.Screen.ShortKeys.*;
import static org.openelis.ui.screen.Screen.Validation.Status.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

import org.openelis.cache.CacheProvider;
import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AttachmentDO;
import org.openelis.domain.AttachmentItemViewDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdAccessionVO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.domain.SampleTestRequestVO;
import org.openelis.domain.SampleTestReturnVO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.manager.AttachmentManager;
import org.openelis.manager.AuxFieldGroupManager;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.attachment.client.AttachmentScreenUI;
import org.openelis.modules.auxData.client.AddAuxGroupEvent;
import org.openelis.modules.auxData.client.AuxDataTabUI;
import org.openelis.modules.auxData.client.RemoveAuxGroupEvent;
import org.openelis.modules.auxiliary.client.AuxiliaryService;
import org.openelis.modules.main.client.OpenELIS;
import org.openelis.modules.order1.client.SendoutOrderScreenUI;
import org.openelis.modules.organization.client.OrganizationService;
import org.openelis.modules.project.client.ProjectService;
import org.openelis.modules.sample1.client.AddRowAnalytesEvent;
import org.openelis.modules.sample1.client.AddTestEvent;
import org.openelis.modules.sample1.client.AnalysisChangeEvent;
import org.openelis.modules.sample1.client.AnalysisNotesTabUI;
import org.openelis.modules.sample1.client.AnalysisTabUI;
import org.openelis.modules.sample1.client.AttachmentTabUI;
import org.openelis.modules.sample1.client.QAEventTabUI;
import org.openelis.modules.sample1.client.RemoveAnalysisEvent;
import org.openelis.modules.sample1.client.ResultChangeEvent;
import org.openelis.modules.sample1.client.ResultTabUI;
import org.openelis.modules.sample1.client.SampleHistoryUtility1;
import org.openelis.modules.sample1.client.SampleItemAnalysisTreeTabUI;
import org.openelis.modules.sample1.client.SampleItemTabUI;
import org.openelis.modules.sample1.client.SampleNotesTabUI;
import org.openelis.modules.sample1.client.SampleOrganizationLookupUI;
import org.openelis.modules.sample1.client.SampleOrganizationUtility1;
import org.openelis.modules.sample1.client.SampleProjectLookupUI;
import org.openelis.modules.sample1.client.SampleService1;
import org.openelis.modules.sample1.client.StorageTabUI;
import org.openelis.modules.sample1.client.TestSelectionLookupUI;
import org.openelis.modules.scriptlet.client.ScriptletFactory;
import org.openelis.modules.systemvariable.client.SystemVariableService;
import org.openelis.modules.test.client.TestService;
import org.openelis.scriptlet.SampleSO;
import org.openelis.scriptlet.SampleSO.Operation;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.InconsistencyException;
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
import org.openelis.ui.event.ShortcutHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.ScreenNavigator;
import org.openelis.ui.screen.State;
import org.openelis.ui.scriptlet.ScriptletInt;
import org.openelis.ui.scriptlet.ScriptletRunner;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.CheckMenuItem;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.KeyCodes;
import org.openelis.ui.widget.Menu;
import org.openelis.ui.widget.MenuItem;
import org.openelis.ui.widget.ModalWindow;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.TabLayoutPanel;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.calendar.Calendar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Widget;

public class EnvironmentalSampleLoginScreenUI extends Screen implements CacheProvider {

    @UiTemplate("EnvironmentalSampleLogin.ui.xml")
    interface EnvironmentalSampleLoginUiBinder extends
                                              UiBinder<Widget, EnvironmentalSampleLoginScreenUI> {
    };

    private static EnvironmentalSampleLoginUiBinder     uiBinder   = GWT.create(EnvironmentalSampleLoginUiBinder.class);

    protected SampleManager1                            manager, previousManager;

    protected ScreenNavigator<IdAccessionVO>            nav;

    @UiField
    protected Calendar                                  collectionDate, collectionTime,
                    receivedDate;

    @UiField
    protected TextBox<Integer>                          accessionNumber, orderId, envPriority;

    @UiField
    protected TextBox<String>                           clientReference, envCollector,
                    envCollectorPhone, envDescription, envLocation, locationAddressMultipleUnit,
                    locationAddressStreetAddress, locationAddressCity, locationAddressZipCode;

    @UiField
    protected Dropdown<Integer>                         status;

    @UiField
    protected CheckBox                                  envIsHazardous;

    @UiField
    protected Dropdown<String>                          locationAddressState,
                    locationAddressCountry;

    @UiField
    protected AutoComplete                              projectName, reportToName, billToName;

    @UiField
    protected Button                                    query, previous, next, add, update, commit,
                    abort, optionsButton, orderLookupButton, projectButton, reportToButton,
                    billToButton;

    @UiField
    protected Menu                                      optionsMenu, historyMenu;

    @UiField
    protected MenuItem                                  duplicate, historySample,
                    historySampleEnvironmental, historySampleProject, historySampleOrganization,
                    historySampleItem, historyAnalysis, historyCurrentResult, historyStorage,
                    historySampleQA, historyAnalysisQA, historyAuxData;

    @UiField
    protected CheckMenuItem                             fromTRF;

    @UiField
    protected TabLayoutPanel                            tabPanel;

    @UiField(provided = true)
    protected SampleItemAnalysisTreeTabUI               sampleItemAnalysisTreeTab;

    @UiField(provided = true)
    protected SampleItemTabUI                           sampleItemTab;

    @UiField(provided = true)
    protected AnalysisTabUI                             analysisTab;

    @UiField(provided = true)
    protected ResultTabUI                               resultTab;

    @UiField(provided = true)
    protected AnalysisNotesTabUI                        analysisNotesTab;

    @UiField(provided = true)
    protected SampleNotesTabUI                          sampleNotesTab;

    @UiField(provided = true)
    protected StorageTabUI                              storageTab;

    @UiField(provided = true)
    protected QAEventTabUI                              qaEventTab;

    @UiField(provided = true)
    protected AuxDataTabUI                              auxDataTab;

    @UiField(provided = true)
    protected AttachmentTabUI                           attachmentTab;

    protected boolean                                   canEdit, isBusy;

    protected ModulePermission                          userPermission;

    protected EnvironmentalSampleLoginScreenUI          screen;

    protected TestSelectionLookupUI                     testSelectionLookup;

    protected SampleProjectLookupUI                     sampleprojectLookUp;

    protected SampleOrganizationLookupUI                sampleOrganizationLookup;

    protected AttachmentScreenUI                        attachmentScreen;

    protected HashMap<String, Object>                   cache;

    protected AsyncCallbackUI<ArrayList<IdAccessionVO>> queryCall;

    protected AsyncCallbackUI<SampleManager1>           addCall, fetchForUpdateCall,
                    commitUpdateCall, fetchByIdCall, unlockCall, mergeQuickEntryCall;

    protected AsyncCallbackUI<Void>                     validateAccessionNumberCall;

    protected AsyncCallbackUI<SampleTestReturnVO>       duplicateCall;

    protected ScriptletRunner<SampleSO>                 scriptletRunner;

    protected SystemVariableDO                          domainScriptletVariable;

    protected Integer                                   domainScriptletId;

    protected static final SampleManager1.Load          elements[] = {
                    SampleManager1.Load.ANALYSISUSER, SampleManager1.Load.AUXDATA,
                    SampleManager1.Load.NOTE, SampleManager1.Load.ORGANIZATION,
                    SampleManager1.Load.PROJECT, SampleManager1.Load.QA,
                    SampleManager1.Load.RESULT, SampleManager1.Load.STORAGE,
                    SampleManager1.Load.WORKSHEET, SampleManager1.Load.ATTACHMENT};

    private static final String                         REPORT_TO_KEY = "reportTo",
                    BILL_TO_KEY = "billTo";

    /**
     * Check the permissions for this screen, intialize the tabs and widgets
     */
    public EnvironmentalSampleLoginScreenUI(WindowInt window) throws Exception {
        setWindow(window);

        userPermission = UserCache.getPermission().getModule("sampleenvironmental");
        if (userPermission == null)
            throw new PermissionException(Messages.get()
                                                  .screenPermException("Environmental Sample Login Screen"));

        try {
            CategoryCache.getBySystemNames("sample_status",
                                           "state",
                                           "country",
                                           "organization_type",
                                           "type_of_sample",
                                           "source_of_sample",
                                           "sample_container",
                                           "unit_of_measure",
                                           "analysis_status",
                                           "user_action",
                                           "unit_of_measure",
                                           "qaevent_type",
                                           "worksheet_status");
        } catch (Exception e) {
            window.close();
            throw e;
        }

        sampleItemAnalysisTreeTab = new SampleItemAnalysisTreeTabUI(this);
        sampleItemTab = new SampleItemTabUI(this);
        analysisTab = new AnalysisTabUI(this);
        resultTab = new ResultTabUI(this);
        analysisNotesTab = new AnalysisNotesTabUI(this);
        sampleNotesTab = new SampleNotesTabUI(this);
        storageTab = new StorageTabUI(this);
        qaEventTab = new QAEventTabUI(this);
        auxDataTab = new AuxDataTabUI(this) {
            @Override
            public boolean evaluateEdit() {
                return manager != null &&
                       !Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample()
                                                                             .getStatusId());
            }

            @Override
            public int count() {
                if (manager != null)
                    return manager.auxData.count();
                return 0;
            }

            @Override
            public AuxDataViewDO get(int i) {
                return manager.auxData.get(i);
            }

            @Override
            public String getAuxFieldMetaKey() {
                return SampleMeta.getAuxDataAuxFieldId();
            }

            @Override
            public String getValueMetaKey() {
                return SampleMeta.getAuxDataValue();
            }
        };
        attachmentTab = new AttachmentTabUI(this);

        initWidget(uiBinder.createAndBindUi(this));

        manager = null;

        initialize();
        evaluateEdit();
        setData();
        setState(DEFAULT);
        fireDataChange();

        logger.fine("Environmental Sample Login Screen Opened");
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        Item<Integer> row;
        Item<String> strow;
        ArrayList<Item<Integer>> model;
        ArrayList<Item<String>> stmodel;

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

        /*
         * option menu items
         */
        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                optionsMenu.setEnabled(isState(DEFAULT, ADD, DISPLAY));
                optionsButton.setEnabled(isState(DEFAULT, ADD, DISPLAY));
                historyMenu.setEnabled(isState(DISPLAY));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                duplicate.setEnabled(isState(DISPLAY));
            }
        });

        duplicate.addCommand(new Command() {
            public void execute() {
                duplicate();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                fromTRF.setEnabled(true);
            }
        });

        fromTRF.addCommand(new Command() {
            @Override
            public void execute() {
                fromTRF();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                historySample.setEnabled(isState(DISPLAY));
            }
        });

        historySample.addCommand(new Command() {
            @Override
            public void execute() {
                SampleHistoryUtility1.sample(manager);
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                historySampleEnvironmental.setEnabled(isState(DISPLAY));
            }
        });

        historySampleEnvironmental.addCommand(new Command() {
            @Override
            public void execute() {
                SampleHistoryUtility1.environmental(manager);
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                historySampleProject.setEnabled(isState(DISPLAY));
            }
        });

        historySampleProject.addCommand(new Command() {
            @Override
            public void execute() {
                SampleHistoryUtility1.project(manager);
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                historySampleOrganization.setEnabled(isState(DISPLAY));
            }
        });

        historySampleOrganization.addCommand(new Command() {
            @Override
            public void execute() {
                SampleHistoryUtility1.organization(manager);
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                historySampleItem.setEnabled(isState(DISPLAY));
            }
        });

        historySampleItem.addCommand(new Command() {
            @Override
            public void execute() {
                SampleHistoryUtility1.item(manager);
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                historyAnalysis.setEnabled(isState(DISPLAY));
            }
        });

        historyAnalysis.addCommand(new Command() {
            @Override
            public void execute() {
                SampleHistoryUtility1.analysis(manager);
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                historyCurrentResult.setEnabled(isState(DISPLAY));
            }
        });

        historyCurrentResult.addCommand(new Command() {
            @Override
            public void execute() {
                String uid;
                Object obj;

                /*
                 * Show the history of the results of the analysis selected in
                 * the tree. Inform the user that they first need to select an
                 * analysis if this is not the case.
                 */
                uid = sampleItemAnalysisTreeTab.getSelectedUid();
                if (uid != null) {
                    obj = manager.getObject(uid);
                    if (obj instanceof AnalysisViewDO) {
                        SampleHistoryUtility1.currentResult(manager, ((AnalysisViewDO)obj).getId());
                        return;
                    }
                }

                setError(Messages.get().result_historyException());
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                historyStorage.setEnabled(isState(DISPLAY));
            }
        });
        historyStorage.addCommand(new Command() {
            @Override
            public void execute() {
                SampleHistoryUtility1.storage(manager);
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                historySampleQA.setEnabled(isState(DISPLAY));
            }
        });

        historySampleQA.addCommand(new Command() {
            @Override
            public void execute() {
                SampleHistoryUtility1.sampleQA(manager);
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                historyAnalysisQA.setEnabled(isState(DISPLAY));
            }
        });

        historyAnalysisQA.addCommand(new Command() {
            @Override
            public void execute() {
                SampleHistoryUtility1.analysisQA(manager);
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                historyAuxData.setEnabled(isState(DISPLAY));
            }
        });

        historyAuxData.addCommand(new Command() {
            @Override
            public void execute() {
                SampleHistoryUtility1.auxData(manager);
            }
        });

        /*
         * screen fields and widgets
         */
        addScreenHandler(accessionNumber,
                         SampleMeta.getAccessionNumber(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 accessionNumber.setValue(getAccessionNumber());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setAccessionNumber(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 accessionNumber.setEnabled(isState(QUERY) ||
                                                            (canEdit && isState(ADD, UPDATE)));
                                 accessionNumber.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? orderId : billToName;
                             }
                         });

        addScreenHandler(orderId, SampleMeta.getOrderId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                orderId.setValue(getOrderId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setOrderId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                orderId.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                orderId.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? collectionDate : accessionNumber;
            }
        });

        orderId.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                Integer ordId, prevOrdId;

                if (canCopyFromPrevious(event.getNativeKeyCode())) {
                    ordId = manager.getSample().getOrderId();
                    prevOrdId = previousManager.getSample().getOrderId();
                    /*
                     * we don't want to incur the cost of importing the order if
                     * the order id in the previous manager is the same as the
                     * one in the current manager
                     */
                    if ( !DataBaseUtil.isSame(ordId, prevOrdId)) {
                        setOrderId(prevOrdId);
                        orderId.setValue(prevOrdId);
                    }
                    screen.focusNextWidget((Focusable)orderId, true);
                    event.preventDefault();
                    event.stopPropagation();
                }
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                orderLookupButton.setEnabled(isState(DISPLAY) || (canEdit && isState(ADD, UPDATE)));
            }
        });

        addScreenHandler(collectionDate,
                         SampleMeta.getCollectionDate(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 collectionDate.setValue(getCollectionDate());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 setCollectionDate(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 collectionDate.setEnabled(isState(QUERY) ||
                                                           (canEdit && isState(ADD, UPDATE)));
                                 collectionDate.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? collectionTime : orderId;
                             }
                         });

        collectionDate.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                Datetime cd;

                if (canCopyFromPrevious(event.getNativeKeyCode())) {
                    cd = previousManager.getSample().getCollectionDate();
                    setCollectionDate(cd);
                    collectionDate.setValue(cd);
                    screen.focusNextWidget((Focusable)collectionDate, true);
                    event.preventDefault();
                    event.stopPropagation();
                }
            }
        });

        addScreenHandler(collectionTime,
                         SampleMeta.getCollectionTime(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 collectionTime.setValue(getCollectionTime());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 setCollectionTime(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 collectionTime.setEnabled(canEdit && isState(ADD, UPDATE));
                                 collectionTime.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? receivedDate : collectionDate;
                             }
                         });

        collectionTime.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                Datetime ct;

                if (canCopyFromPrevious(event.getNativeKeyCode())) {
                    ct = previousManager.getSample().getCollectionTime();
                    setCollectionTime(ct);
                    collectionTime.setValue(ct);
                    screen.focusNextWidget((Focusable)collectionTime, true);
                    event.preventDefault();
                    event.stopPropagation();
                }
            }
        });

        addScreenHandler(receivedDate, SampleMeta.getReceivedDate(), new ScreenHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                receivedDate.setValue(getReceivedDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                setReceivedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                receivedDate.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                receivedDate.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? status : collectionTime;
            }
        });

        receivedDate.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                Datetime rd;

                if (canCopyFromPrevious(event.getNativeKeyCode())) {
                    rd = previousManager.getSample().getReceivedDate();
                    setReceivedDate(rd);
                    receivedDate.setValue(rd);
                    screen.focusNextWidget((Focusable)receivedDate, true);
                    event.preventDefault();
                    event.stopPropagation();
                }
            }
        });

        addScreenHandler(status, SampleMeta.getStatusId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                status.setValue(getStatusId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setStatusId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                status.setEnabled(isState(QUERY));
                status.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? clientReference : receivedDate;
            }
        });

        addScreenHandler(clientReference,
                         SampleMeta.getClientReference(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 clientReference.setValue(getClientReference());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setClientReference(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 clientReference.setEnabled(isState(QUERY) ||
                                                            (canEdit && isState(ADD, UPDATE)));
                                 clientReference.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? envIsHazardous : status;
                             }
                         });

        clientReference.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                String cr;

                if (canCopyFromPrevious(event.getNativeKeyCode())) {
                    cr = previousManager.getSample().getClientReference();
                    setClientReference(cr);
                    clientReference.setValue(cr);
                    screen.focusNextWidget((Focusable)clientReference, true);
                    event.preventDefault();
                    event.stopPropagation();
                }
            }
        });

        addScreenHandler(envIsHazardous,
                         SampleMeta.getEnvIsHazardous(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 envIsHazardous.setValue(getIsHazardous());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setIsHazardous(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 envIsHazardous.setEnabled(isState(QUERY) ||
                                                           (canEdit && isState(ADD, UPDATE)));
                                 envIsHazardous.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? envPriority : clientReference;
                             }
                         });

        envIsHazardous.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                String ih;

                if (canCopyFromPrevious(event.getNativeKeyCode())) {
                    ih = previousManager.getSampleEnvironmental().getIsHazardous();
                    setIsHazardous(ih);
                    envIsHazardous.setValue(ih);
                    screen.focusNextWidget((Focusable)envIsHazardous, true);
                    event.preventDefault();
                    event.stopPropagation();
                }
            }
        });

        addScreenHandler(envPriority, SampleMeta.getEnvPriority(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                envPriority.setValue(getPriority());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setPriority(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                envPriority.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                envPriority.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? envCollector : envIsHazardous;
            }
        });

        envPriority.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                Integer p;

                if (canCopyFromPrevious(event.getNativeKeyCode())) {
                    p = previousManager.getSampleEnvironmental().getPriority();
                    setPriority(p);
                    envPriority.setValue(p);
                    screen.focusNextWidget((Focusable)envPriority, true);
                    event.preventDefault();
                    event.stopPropagation();
                }
            }
        });

        addScreenHandler(envCollector, SampleMeta.getEnvCollector(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                envCollector.setValue(getCollector());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setCollector(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                envCollector.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                envCollector.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? envCollectorPhone : envPriority;
            }
        });

        envCollector.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                String col;

                if (canCopyFromPrevious(event.getNativeKeyCode())) {
                    col = previousManager.getSampleEnvironmental().getCollector();
                    setCollector(col);
                    envCollector.setValue(col);
                    screen.focusNextWidget((Focusable)envCollector, true);
                    event.preventDefault();
                    event.stopPropagation();
                }
            }
        });

        addScreenHandler(envCollectorPhone,
                         SampleMeta.getEnvCollectorPhone(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 envCollectorPhone.setValue(getCollectorPhone());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setCollectorPhone(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 envCollectorPhone.setEnabled(isState(QUERY) ||
                                                              (canEdit && isState(ADD, UPDATE)));
                                 envCollectorPhone.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? envDescription : envCollector;
                             }
                         });

        envCollectorPhone.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                String cp;

                if (canCopyFromPrevious(event.getNativeKeyCode())) {
                    cp = previousManager.getSampleEnvironmental().getCollectorPhone();
                    setCollectorPhone(cp);
                    envCollectorPhone.setValue(cp);
                    screen.focusNextWidget((Focusable)envCollectorPhone, true);
                    event.preventDefault();
                    event.stopPropagation();
                }
            }
        });

        addScreenHandler(envDescription,
                         SampleMeta.getEnvDescription(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 envDescription.setValue(getDescription());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setDescription(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 envDescription.setEnabled(isState(QUERY) ||
                                                           (canEdit && isState(ADD, UPDATE)));
                                 envDescription.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? envLocation : envCollectorPhone;
                             }
                         });

        envDescription.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                String d;

                if (canCopyFromPrevious(event.getNativeKeyCode())) {
                    d = previousManager.getSampleEnvironmental().getDescription();
                    setDescription(d);
                    envDescription.setValue(d);
                    screen.focusNextWidget((Focusable)envDescription, true);
                    event.preventDefault();
                    event.stopPropagation();
                }
            }
        });

        addScreenHandler(envLocation, SampleMeta.getEnvLocation(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                envLocation.setValue(getLocation());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setLocation(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                envLocation.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                envLocation.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? locationAddressMultipleUnit : envDescription;
            }
        });

        envLocation.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                String l;

                if (canCopyFromPrevious(event.getNativeKeyCode())) {
                    l = previousManager.getSampleEnvironmental().getLocation();
                    setLocation(l);
                    envLocation.setValue(l);
                    screen.focusNextWidget((Focusable)envLocation, true);
                    event.preventDefault();
                    event.stopPropagation();
                }
            }
        });

        addScreenHandler(locationAddressMultipleUnit,
                         SampleMeta.getLocationAddrMultipleUnit(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 locationAddressMultipleUnit.setValue(getLocationAddressMultipleUnit());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setLocationAddressMultipleUnit(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 locationAddressMultipleUnit.setEnabled(isState(QUERY) ||
                                                                        (canEdit && isState(ADD,
                                                                                            UPDATE)));
                                 locationAddressMultipleUnit.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? locationAddressStreetAddress : envLocation;
                             }
                         });

        locationAddressMultipleUnit.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                String mu;

                if (canCopyFromPrevious(event.getNativeKeyCode())) {
                    mu = previousManager.getSampleEnvironmental()
                                        .getLocationAddress()
                                        .getMultipleUnit();
                    setLocationAddressMultipleUnit(mu);
                    locationAddressMultipleUnit.setValue(mu);
                    screen.focusNextWidget((Focusable)locationAddressMultipleUnit, true);
                    event.preventDefault();
                    event.stopPropagation();
                }
            }
        });

        addScreenHandler(locationAddressStreetAddress,
                         SampleMeta.getLocationAddrStreetAddress(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 locationAddressStreetAddress.setValue(getLocationAddressStreetAddress());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setLocationAddressStreetAddress(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 locationAddressStreetAddress.setEnabled(isState(QUERY) ||
                                                                         (canEdit && isState(ADD,
                                                                                             UPDATE)));
                                 locationAddressStreetAddress.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? locationAddressCity : locationAddressMultipleUnit;
                             }
                         });

        locationAddressStreetAddress.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                String sa;

                if (canCopyFromPrevious(event.getNativeKeyCode())) {
                    sa = previousManager.getSampleEnvironmental()
                                        .getLocationAddress()
                                        .getStreetAddress();
                    setLocationAddressStreetAddress(sa);
                    locationAddressStreetAddress.setValue(sa);
                    screen.focusNextWidget((Focusable)locationAddressStreetAddress, true);
                    event.preventDefault();
                    event.stopPropagation();
                }
            }
        });

        addScreenHandler(locationAddressCity,
                         SampleMeta.getLocationAddrCity(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 locationAddressCity.setValue(getLocationAddressCity());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setLocationAddressCity(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 locationAddressCity.setEnabled(isState(QUERY) ||
                                                                (canEdit && isState(ADD, UPDATE)));
                                 locationAddressCity.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? locationAddressState
                                               : locationAddressStreetAddress;
                             }
                         });

        locationAddressCity.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                String c;

                if (canCopyFromPrevious(event.getNativeKeyCode())) {
                    c = previousManager.getSampleEnvironmental().getLocationAddress().getCity();
                    setLocationAddressCity(c);
                    locationAddressCity.setValue(c);
                    screen.focusNextWidget((Focusable)locationAddressCity, true);
                    event.preventDefault();
                    event.stopPropagation();
                }
            }
        });

        addScreenHandler(locationAddressState,
                         SampleMeta.getLocationAddrState(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 locationAddressState.setValue(getLocationAddressState());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setLocationAddressState(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 locationAddressState.setEnabled(isState(QUERY) ||
                                                                 (canEdit && isState(ADD, UPDATE)));
                                 locationAddressState.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? locationAddressZipCode : locationAddressCity;
                             }
                         });

        locationAddressState.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                String s;

                if (canCopyFromPrevious(event.getNativeKeyCode())) {
                    s = previousManager.getSampleEnvironmental().getLocationAddress().getState();
                    setLocationAddressState(s);
                    locationAddressState.setValue(s);
                    screen.focusNextWidget((Focusable)locationAddressState, true);
                    event.preventDefault();
                    event.stopPropagation();
                }
            }
        });

        addScreenHandler(locationAddressZipCode,
                         SampleMeta.getLocationAddrZipCode(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 locationAddressZipCode.setValue(getLocationAddressZipCode());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setLocationAddressZipCode(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 locationAddressZipCode.setEnabled( (isState(QUERY)) ||
                                                                   (canEdit && isState(ADD, UPDATE)));
                                 locationAddressZipCode.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? locationAddressCountry : locationAddressState;
                             }
                         });

        locationAddressZipCode.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                String zip;

                if (canCopyFromPrevious(event.getNativeKeyCode())) {
                    zip = previousManager.getSampleEnvironmental()
                                         .getLocationAddress()
                                         .getZipCode();
                    setLocationAddressZipCode(zip);
                    locationAddressZipCode.setValue(zip);
                    screen.focusNextWidget((Focusable)locationAddressZipCode, true);
                    event.preventDefault();
                    event.stopPropagation();
                }
            }
        });

        addScreenHandler(locationAddressCountry,
                         SampleMeta.getLocationAddrCountry(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 locationAddressCountry.setValue(getLocationAddressCountry());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setLocationAddressCountry(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 locationAddressCountry.setEnabled(isState(QUERY) ||
                                                                   (canEdit && isState(ADD, UPDATE)));
                                 locationAddressCountry.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? projectName : locationAddressZipCode;
                             }
                         });

        locationAddressCountry.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                String c;

                if (canCopyFromPrevious(event.getNativeKeyCode())) {
                    c = previousManager.getSampleEnvironmental().getLocationAddress().getCountry();
                    setLocationAddressCountry(c);
                    locationAddressCountry.setValue(c);
                    screen.focusNextWidget((Focusable)locationAddressCountry, true);
                    event.preventDefault();
                    event.stopPropagation();
                }
            }
        });

        addScreenHandler(projectName,
                         SampleMeta.getProjectName(),
                         new ScreenHandler<AutoCompleteValue>() {
                             public void onDataChange(DataChangeEvent event) {
                                 setProject(getFirstProject(manager));
                             }

                             public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                                 ProjectDO data;

                                 data = null;
                                 if (event.getValue() != null)
                                     data = (ProjectDO)event.getValue().getData();
                                 changeProject(data);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 projectName.setEnabled(isState(QUERY) ||
                                                        (canEdit && isState(ADD, UPDATE)));
                                 projectName.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? reportToName : locationAddressCountry;
                             }
                         });

        projectName.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (canCopyFromPrevious(event.getNativeKeyCode())) {
                    setProject(getFirstProject(previousManager));
                    screen.focusNextWidget((Focusable)projectName, true);
                    event.preventDefault();
                    event.stopPropagation();
                }
            }
        });

        projectName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                Item<Integer> row;
                ArrayList<ProjectDO> list;
                ArrayList<Item<Integer>> model;

                setBusy();
                try {
                    list = ProjectService.get()
                                         .fetchActiveByName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<Item<Integer>>();
                    for (ProjectDO p : list) {
                        row = new Item<Integer>(4);

                        row.setKey(p.getId());
                        row.setCell(0, p.getName());
                        row.setCell(1, p.getDescription());
                        row.setData(p);
                        model.add(row);
                    }
                    projectName.showAutoMatches(model);
                } catch (Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
                clearStatus();
            }
        });

        addScreenHandler(projectButton, "projectButton", new ScreenHandler<Integer>() {
            public void onStateChange(StateChangeEvent event) {
                projectButton.setEnabled(isState(DISPLAY) || (canEdit && isState(ADD, UPDATE)));
            }
        });

        addScreenHandler(reportToName, REPORT_TO_KEY, new ScreenHandler<AutoCompleteValue>() {
            public void onDataChange(DataChangeEvent event) {
                setReportTo(getSampleOrganization(manager, Constants.dictionary().ORG_REPORT_TO));
            }

            public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                OrganizationDO data;

                data = null;
                if (event.getValue() != null)
                    data = (OrganizationDO)event.getValue().getData();
                changeOrganization(Constants.dictionary().ORG_REPORT_TO, data);
            }

            public void onStateChange(StateChangeEvent event) {
                reportToName.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                reportToName.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? billToName : projectName;
            }
        });

        reportToName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                Item<Integer> row;
                OrganizationDO data;
                ArrayList<OrganizationDO> list;
                ArrayList<Item<Integer>> model;

                setBusy();
                try {
                    list = OrganizationService.get()
                                              .fetchByIdOrName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<Item<Integer>>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new Item<Integer>(4);
                        data = list.get(i);

                        row.setKey(data.getId());
                        row.setData(data);
                        row.setCell(0, data.getName());
                        row.setCell(1, data.getAddress().getStreetAddress());
                        row.setCell(2, data.getAddress().getCity());
                        row.setCell(3, data.getAddress().getState());

                        model.add(row);
                    }
                    reportToName.showAutoMatches(model);
                } catch (Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
                clearStatus();
            }
        });

        reportToName.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (canCopyFromPrevious(event.getNativeKeyCode())) {
                    setReportTo(getSampleOrganization(previousManager,
                                                      Constants.dictionary().ORG_REPORT_TO));
                    screen.focusNextWidget((Focusable)reportToName, true);
                    event.preventDefault();
                    event.stopPropagation();
                }
            }
        });

        addScreenHandler(reportToButton, "reportToButton", new ScreenHandler<Integer>() {
            public void onStateChange(StateChangeEvent event) {
                reportToButton.setEnabled(isState(DISPLAY) || (canEdit && isState(ADD, UPDATE)));
            }
        });

        addScreenHandler(billToName, BILL_TO_KEY, new ScreenHandler<AutoCompleteValue>() {
            public void onDataChange(DataChangeEvent event) {
                setBillTo(getSampleOrganization(manager, Constants.dictionary().ORG_BILL_TO));
            }

            public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                OrganizationDO data;

                data = null;
                if (event.getValue() != null)
                    data = (OrganizationDO)event.getValue().getData();
                changeOrganization(Constants.dictionary().ORG_BILL_TO, data);
            }

            public void onStateChange(StateChangeEvent event) {
                billToName.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                billToName.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? accessionNumber : reportToName;
            }
        });

        billToName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                Item<Integer> row;
                OrganizationDO data;
                ArrayList<OrganizationDO> list;
                ArrayList<Item<Integer>> model;

                setBusy();
                try {
                    list = OrganizationService.get()
                                              .fetchByIdOrName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<Item<Integer>>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new Item<Integer>(4);
                        data = list.get(i);

                        row.setKey(data.getId());
                        row.setData(data);
                        row.setCell(0, data.getName());
                        row.setCell(1, data.getAddress().getStreetAddress());
                        row.setCell(2, data.getAddress().getCity());
                        row.setCell(3, data.getAddress().getState());

                        model.add(row);
                    }
                    billToName.showAutoMatches(model);
                } catch (Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
                clearStatus();
            }
        });

        billToName.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (canCopyFromPrevious(event.getNativeKeyCode())) {
                    setBillTo(getSampleOrganization(previousManager,
                                                    Constants.dictionary().ORG_BILL_TO));
                    screen.focusNextWidget((Focusable)billToName, true);
                    event.preventDefault();
                    event.stopPropagation();
                }
            }
        });

        addScreenHandler(billToButton, "billToButton", new ScreenHandler<Integer>() {
            public void onStateChange(StateChangeEvent event) {
                billToButton.setEnabled(isState(DISPLAY) || (canEdit && isState(ADD, UPDATE)));
            }
        });

        /*
         * this is done to make the tabs detachable
         */
        tabPanel.setPopoutBrowser(OpenELIS.getBrowser());

        /*
         * add the handlers for the tabs, so that they can be treated like other
         * widgets
         */
        addScreenHandler(sampleItemAnalysisTreeTab,
                         "sampleItemAnalysisTreeTab",
                         new ScreenHandler<Object>() {
                             public void onDataChange(DataChangeEvent event) {
                                 sampleItemAnalysisTreeTab.onDataChange();
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 sampleItemAnalysisTreeTab.setState(event.getState());
                             }

                             public Object getQuery() {
                                 return null;
                             }
                         });

        addScreenHandler(sampleItemTab, "sampleItemTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                /*
                 * the tab is refreshed when a node in the tree is selected
                 */
            }

            public void onStateChange(StateChangeEvent event) {
                sampleItemTab.setState(event.getState());
            }

            public Object getQuery() {
                return sampleItemTab.getQueryFields();
            }
        });

        /*
         * querying by this tab is allowed on this screen, but not on all
         * screens
         */
        sampleItemTab.setCanQuery(true);

        addScreenHandler(analysisTab, "analysisTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                /*
                 * the tab is refreshed when a node in the tree is selected
                 */
            }

            public void onStateChange(StateChangeEvent event) {
                analysisTab.setState(event.getState());
            }

            public Object getQuery() {
                return analysisTab.getQueryFields();
            }

            public void isValid(Validation validation) {
                super.isValid(validation);
                if (analysisTab.getIsBusy())
                    validation.setStatus(FLAGGED);
            }
        });

        addScreenHandler(resultTab, "resultTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                /*
                 * the tab is refreshed when a node in the tree is selected
                 */
            }

            public void onStateChange(StateChangeEvent event) {
                resultTab.setState(event.getState());
            }

            public Object getQuery() {
                return null;
            }

            public void isValid(Validation validation) {
                super.isValid(validation);
                if (resultTab.getIsBusy())
                    validation.setStatus(FLAGGED);
            }
        });

        addScreenHandler(analysisNotesTab, "analysisNotesTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                /*
                 * the tab is refreshed when a node in the tree is selected
                 */
            }

            public void onStateChange(StateChangeEvent event) {
                analysisNotesTab.setState(event.getState());
            }

            public Object getQuery() {
                return null;
            }
        });

        addScreenHandler(sampleNotesTab, "sampleNotesTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                sampleNotesTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                sampleNotesTab.setState(event.getState());
            }

            public Object getQuery() {
                return null;
            }
        });

        addScreenHandler(storageTab, "storageTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                /*
                 * the tab is refreshed when a node in the tree is selected
                 */
            }

            public void onStateChange(StateChangeEvent event) {
                storageTab.setState(event.getState());
            }

            public Object getQuery() {
                return null;
            }
        });

        addScreenHandler(qaEventTab, "qaEventTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                /*
                 * the tab is refreshed when a node in the tree is selected
                 */
            }

            public void onStateChange(StateChangeEvent event) {
                qaEventTab.setState(event.getState());
            }

            public Object getQuery() {
                return null;
            }
        });

        addScreenHandler(auxDataTab, "auxDataTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                auxDataTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                auxDataTab.setState(event.getState());
            }

            public Object getQuery() {
                return auxDataTab.getQueryFields();
            }
        });

        addScreenHandler(attachmentTab, "attachmentTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                attachmentTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                attachmentTab.setState(event.getState());
            }

            public Object getQuery() {
                return attachmentTab.getQueryFields();
            }
        });

        /*
         * querying by this tab is allowed on this screen, but not on all
         * screens
         */
        auxDataTab.setCanQuery(true);

        /*
         * add shortcuts to select the tabs on the screen by using the Ctrl key
         * and a number, e.g. Ctrl+'1' for the first tab, and so on; the
         * ScheduledCommands make sure that the tab is opened before the focus
         * is set
         */
        addShortcut(new ShortcutHandler() {
            @Override
            public void onShortcut() {
                ScheduledCommand cmd;

                tabPanel.selectTab(0);
                cmd = new ScheduledCommand() {
                    @Override
                    public void execute() {
                        sampleItemTab.setFocus();
                    }
                };
                Scheduler.get().scheduleDeferred(cmd);
            }
        }, '1', CTRL);

        addShortcut(new ShortcutHandler() {
            @Override
            public void onShortcut() {
                ScheduledCommand cmd;

                tabPanel.selectTab(1);
                cmd = new ScheduledCommand() {
                    @Override
                    public void execute() {
                        analysisTab.setFocus();
                    }
                };
                Scheduler.get().scheduleDeferred(cmd);
            }
        }, '2', CTRL);

        addShortcut(new ShortcutHandler() {
            @Override
            public void onShortcut() {
                ScheduledCommand cmd;

                tabPanel.selectTab(2);
                cmd = new ScheduledCommand() {
                    @Override
                    public void execute() {
                        resultTab.setFocus();
                    }
                };
                Scheduler.get().scheduleDeferred(cmd);
            }
        }, '3', CTRL);

        addShortcut(new ShortcutHandler() {
            @Override
            public void onShortcut() {
                ScheduledCommand cmd;

                tabPanel.selectTab(3);
                cmd = new ScheduledCommand() {
                    @Override
                    public void execute() {
                        analysisNotesTab.setFocus();
                    }
                };
                Scheduler.get().scheduleDeferred(cmd);
            }
        }, '4', CTRL);

        addShortcut(new ShortcutHandler() {
            @Override
            public void onShortcut() {
                ScheduledCommand cmd;

                tabPanel.selectTab(4);
                cmd = new ScheduledCommand() {
                    @Override
                    public void execute() {
                        sampleNotesTab.setFocus();
                    }
                };
                Scheduler.get().scheduleDeferred(cmd);
            }
        }, '5', CTRL);

        addShortcut(new ShortcutHandler() {
            @Override
            public void onShortcut() {
                ScheduledCommand cmd;

                tabPanel.selectTab(5);
                cmd = new ScheduledCommand() {
                    @Override
                    public void execute() {
                        storageTab.setFocus();
                    }
                };
                Scheduler.get().scheduleDeferred(cmd);
            }
        }, '6', CTRL);

        addShortcut(new ShortcutHandler() {
            @Override
            public void onShortcut() {
                ScheduledCommand cmd;

                tabPanel.selectTab(6);
                cmd = new ScheduledCommand() {
                    @Override
                    public void execute() {
                        qaEventTab.setFocus();
                    }
                };
                Scheduler.get().scheduleDeferred(cmd);
            }
        }, '7', CTRL);

        addShortcut(new ShortcutHandler() {
            @Override
            public void onShortcut() {
                ScheduledCommand cmd;

                tabPanel.selectTab(7);
                cmd = new ScheduledCommand() {
                    @Override
                    public void execute() {
                        auxDataTab.setFocus();
                    }
                };
                Scheduler.get().scheduleDeferred(cmd);
            }
        }, '8', CTRL);

        addShortcut(new ShortcutHandler() {
            @Override
            public void onShortcut() {
                ScheduledCommand cmd;

                tabPanel.selectTab(8);
                cmd = new ScheduledCommand() {
                    @Override
                    public void execute() {
                        attachmentTab.setFocus();
                    }
                };
                Scheduler.get().scheduleDeferred(cmd);
            }
        }, '9', CTRL);

        //
        // navigation panel
        //
        nav = new ScreenNavigator<IdAccessionVO>(null, null, null) {
            public void executeQuery(final Query query) {
                QueryData field;

                setBusy(Messages.get().gen_querying());

                if (queryCall == null) {
                    queryCall = new AsyncCallbackUI<ArrayList<IdAccessionVO>>() {
                        public void success(ArrayList<IdAccessionVO> result) {
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

                        public void failure(Throwable e) {
                            setQueryResult(null);
                            Window.alert("Error: Environmental Login call query failed; " +
                                         e.getMessage());
                            logger.log(Level.SEVERE, e.getMessage(), e);
                            setError(Messages.get().gen_queryFailed());
                        }
                    };
                }

                /*
                 * only query for environmental samples
                 */
                field = new QueryData(SampleMeta.getDomain(),
                                      QueryData.Type.STRING,
                                      Constants.domain().ENVIRONMENTAL);
                query.setFields(field);
                query.setRowsPerPage(5);
                SampleService1.get().query(query, queryCall);
            }

            public boolean fetch(IdAccessionVO entry) {
                fetchById( (entry == null) ? null : entry.getId());
                return true;
            }

            public ArrayList<Item<Integer>> getModel() {
                ArrayList<IdAccessionVO> result;
                ArrayList<Item<Integer>> model;
                Item<Integer> row;

                model = null;
                result = nav.getQueryResult();
                if (result != null) {
                    model = new ArrayList<Item<Integer>>();
                    for (IdAccessionVO entry : result) {
                        row = new Item<Integer>(1);
                        row.setKey(entry.getId());
                        row.setCell(0, entry.getAccessionNumber());
                        model.add(row);
                    }
                }
                return model;
            }
        };

        /*
         * handlers for the events fired by the tabs
         */

        bus.addHandler(AddTestEvent.getType(), new AddTestEvent.Handler() {
            @Override
            public void onAddTest(AddTestEvent event) {
                if (screen != event.getSource())
                    addAnalyses(event.getTests());
            }
        });

        bus.addHandler(RemoveAnalysisEvent.getType(), new RemoveAnalysisEvent.Handler() {
            @Override
            public void onAnalysisRemove(RemoveAnalysisEvent event) {
                AnalysisViewDO ana;

                if (screen == event.getSource())
                    return;

                ana = (AnalysisViewDO)manager.getObject(event.getUid());
                setBusy();
                try {
                    manager = SampleService1.get().removeAnalysis(manager, ana.getId());
                    setData();
                    setState(state);
                    bus.fireEventFromSource(new RemoveAnalysisEvent(event.getUid()), screen);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
                clearStatus();
            }
        });

        bus.addHandler(AnalysisChangeEvent.getType(), new AnalysisChangeEvent.Handler() {
            @Override
            public void onAnalysisChange(AnalysisChangeEvent event) {
                if (screen == event.getSource())
                    return;

                switch (event.getAction()) {
                    case METHOD_CHANGED:
                        changeAnalysisMethod(event.getUid(), event.getChangeId());
                        break;
                    case STATUS_CHANGED:
                        changeAnalysisStatus(event.getUid(), event.getChangeId());
                        break;
                    case UNIT_CHANGED:
                        changeAnalysisUnit(event.getUid(), event.getChangeId());
                        break;
                    case PREP_CHANGED:
                        changeAnalysisPrep(event.getUid(), event.getChangeId());
                        break;
                }
            }
        });

        bus.addHandler(AddRowAnalytesEvent.getType(), new AddRowAnalytesEvent.Handler() {
            @Override
            public void onAddRowAnalytes(AddRowAnalytesEvent event) {
                addRowAnalytes(event.getAnalysis(), event.getAnalytes(), event.getIndexes());
            }
        });

        bus.addHandler(AddAuxGroupEvent.getType(), new AddAuxGroupEvent.Handler() {
            @Override
            public void onAddAuxGroup(AddAuxGroupEvent event) {
                if (screen != event.getSource())
                    addAuxGroups(event.getGroupIds());
            }
        });

        bus.addHandler(RemoveAuxGroupEvent.getType(), new RemoveAuxGroupEvent.Handler() {
            @Override
            public void onRemoveAuxGroup(RemoveAuxGroupEvent event) {
                if (event.getGroupIds() != null && event.getGroupIds().size() > 0) {
                    if (screen != event.getSource())
                        removeAuxGroups(event.getGroupIds());
                }
            }
        });

        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                if (isState(ADD, UPDATE)) {
                    event.cancel();
                    setError(Messages.get().mustCommitOrAbort());
                } else {
                    /*
                     * make sure that all detached tabs are closed when the main
                     * screen is closed
                     */
                    tabPanel.close();
                }
            }
        });

        /*
         * load models in the dropdowns
         */
        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("sample_status")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }

        status.setModel(model);

        stmodel = new ArrayList<Item<String>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("state")) {
            strow = new Item<String>(d.getEntry(), d.getEntry());
            strow.setEnabled( ("Y".equals(d.getIsActive())));
            stmodel.add(strow);
        }

        locationAddressState.setModel(stmodel);

        stmodel = new ArrayList<Item<String>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("country")) {
            strow = new Item<String>(d.getEntry(), d.getEntry());
            strow.setEnabled("Y".equals(d.getIsActive()));
            stmodel.add(strow);
        }
        locationAddressCountry.setModel(stmodel);
    }

    /*
     * basic button methods
     */

    /**
     * Puts the screen in query state, sets the manager to null and instantiates
     * the cache so that it can be used by aux data tab
     */
    @UiHandler("query")
    protected void query(ClickEvent event) {
        manager = null;
        /*
         * the tab for aux data uses the cache in Query state
         */
        cache = new HashMap<String, Object>();
        evaluateEdit();
        setData();
        setState(QUERY);
        fireDataChange();
        accessionNumber.setFocus(true);
        setDone(Messages.get().gen_enterFieldsToQuery());
    }

    /**
     * Fetches the previous sample in the list returned by the latest query
     */
    @UiHandler("previous")
    protected void previous(ClickEvent event) {
        nav.previous();
    }

    /**
     * Fetches the next sample sample in the list returned by the latest query
     */
    @UiHandler("next")
    protected void next(ClickEvent event) {
        nav.next();
    }

    /**
     * Puts the screen in add state and loads it with a new manager with some
     * fields with default values.
     */
    @UiHandler("add")
    protected void add(ClickEvent event) {
        setBusy();

        if (addCall == null) {
            addCall = new AsyncCallbackUI<SampleManager1>() {
                public void success(SampleManager1 result) {
                    AttachmentManager am;
                    AttachmentDO att;
                    AttachmentItemViewDO atti;

                    previousManager = manager;
                    manager = result;
                    if (attachmentScreen != null) {
                        am = attachmentScreen.getReserved();
                        /*
                         * add an attachment item for the record selected on the
                         * attachment screen
                         */
                        if (am != null) {
                            att = am.getAttachment();
                            atti = manager.attachment.add();
                            atti.setAttachmentId(att.getId());
                            atti.setAttachmentDescription(att.getDescription());
                            atti.setAttachmentCreatedDate(att.getCreatedDate());
                            atti.setAttachmentSectionId(att.getSectionId());
                        }
                    }
                    cache = new HashMap<String, Object>();
                    addScriptlet(null);
                    runDomainScriptlet(Operation.NEW_DOMAIN_ADDED);
                    evaluateEdit();
                    setData();
                    setState(ADD);
                    fireDataChange();
                    accessionNumber.setFocus(true);
                    setDone(Messages.get().gen_enterInformationPressCommit());
                }

                public void failure(Throwable error) {
                    Window.alert(error.getMessage());
                    logger.log(Level.SEVERE, error.getMessage(), error);
                    clearStatus();
                }
            };
        }

        SampleService1.get().getInstance(Constants.domain().ENVIRONMENTAL, addCall);
    }

    /**
     * Puts the screen in update state and loads it with a locked manager.
     * Builds the cache from the manager
     */
    @UiHandler("update")
    protected void update(ClickEvent event) {
        setBusy(Messages.get().lockForUpdate());

        if (fetchForUpdateCall == null) {
            fetchForUpdateCall = new AsyncCallbackUI<SampleManager1>() {
                public void success(SampleManager1 result) {
                    manager = result;
                    if ( !Constants.domain().ENVIRONMENTAL.equals(manager.getSample().getDomain())) {
                        /*
                         * the sample's domain may have changed after it was
                         * loaded on the screen, so the sample needs to be
                         * unlocked because it can't be edited here
                         */
                        Window.alert(Messages.get().sample_domainChangedException());
                        state = UPDATE;
                        abort(null);
                        return;
                    }

                    buildCache();
                    evaluateEdit();
                    setData();
                    setState(UPDATE);
                    fireDataChange();
                    accessionNumber.setFocus(true);
                }

                public void failure(Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }

                public void finish() {
                    clearStatus();
                }
            };
        }

        SampleService1.get().fetchForUpdate(manager.getSample().getId(),
                                            elements,
                                            fetchForUpdateCall);
    }

    /**
     * Validates the data on the screen and based on the current state, executes
     * various service operations to commit the data.
     */
    @UiHandler("commit")
    protected void commit(ClickEvent event) {
        Validation validation;

        finishEditing();

        validation = validate();

        switch (validation.getStatus()) {
            case WARNINGS:
                /*
                 * show the warnings and ask the user if the data should still
                 * be committed; commit only if the user says yes
                 */
                if ( !Window.confirm(getWarnings(validation.getExceptions())))
                    return;
                break;
            case FLAGGED:
                /*
                 * some part of the screen has some operation that needs to be
                 * completed before committing the data
                 */
                return;
            case ERRORS:
                setError(Messages.get().gen_correctErrors());
                return;
        }

        switch (state) {
            case QUERY:
                commitQuery();
                break;
            case ADD:
                commitUpdate(false);
                break;
            case UPDATE:
                commitUpdate(false);
                break;
        }
    }

    /**
     * Creates query fields from the data on the screen and calls the service
     * method for executing a query to return a list of samples. Loads the
     * screen with the first sample's data if any samples were found otherwise
     * notifies the user.
     */
    protected void commitQuery() {
        Query query;

        query = new Query();
        query.setFields(getQueryFields());
        nav.setQuery(query);
        cache = null;
    }

    /**
     * Calls the service method to commit the data on the screen, to the
     * database. Shows any errors/warnings encountered during the commit,
     * otherwise loads the screen with the committed data.
     */
    protected void commitUpdate(final boolean ignoreWarning) {
        if (isState(ADD))
            setBusy(Messages.get().gen_adding());
        else
            setBusy(Messages.get().gen_updating());

        if (commitUpdateCall == null) {
            commitUpdateCall = new AsyncCallbackUI<SampleManager1>() {
                public void success(SampleManager1 result) {
                    manager = result;
                    evaluateEdit();
                    setData();
                    setState(DISPLAY);
                    fireDataChange();
                    clearStatus();

                    /*
                     * the cache and scriptlet runner are set to null only if
                     * the add/update succeeds because otherwise, they can't be
                     * used by any tabs if the user wants to change any data
                     */
                    cache = null;
                    scriptletRunner = null;
                    if (attachmentScreen != null)
                        attachmentScreen.removeReservation();
                }

                public void validationErrors(ValidationErrorsList e) {
                    showErrors(e);
                    if ( !e.hasErrors() && (e.hasWarnings() || e.hasCautions()) && !ignoreWarning)
                        if (Window.confirm(getWarnings(e.getErrorList())))
                            commitUpdate(true);
                }

                public void failure(Throwable e) {
                    if (isState(ADD))
                        Window.alert("commitAdd(): " + e.getMessage());
                    else
                        Window.alert("commitUpdate(): " + e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    clearStatus();
                }
            };
        }

        SampleService1.get().update(manager, ignoreWarning, commitUpdateCall);
    }

    /**
     * Reverts any changes made to the data on the screen and disables editing
     * of the widgets. If the sample was locked, calls the service method to
     * unlock it and loads the screen with that data.
     */
    @UiHandler("abort")
    protected void abort(ClickEvent event) {
        finishEditing();
        clearErrors();
        setBusy(Messages.get().gen_cancelChanges());

        if (isState(QUERY)) {
            manager = null;
            evaluateEdit();
            setData();
            setState(DEFAULT);
            fireDataChange();
            setDone(Messages.get().gen_queryAborted());
            cache = null;
        } else if (isState(ADD)) {
            manager = null;
            evaluateEdit();
            setData();
            setState(DEFAULT);
            fireDataChange();
            setDone(Messages.get().gen_addAborted());
            cache = null;
            scriptletRunner = null;
            if (attachmentScreen != null)
                attachmentScreen.removeReservation();
        } else if (isState(UPDATE)) {
            if (unlockCall == null) {
                unlockCall = new AsyncCallbackUI<SampleManager1>() {
                    public void success(SampleManager1 result) {
                        State st;

                        manager = result;
                        if (Constants.domain().QUICKENTRY.equals(manager.getSample().getDomain())) {
                            /*
                             * the screen was loaded from a quick-entry sample,
                             * so that data can't be shown anymore
                             */
                            manager = null;
                            st = DEFAULT;
                        } else {
                            st = DISPLAY;
                        }

                        evaluateEdit();
                        setData();
                        setState(st);
                        fireDataChange();
                        setDone(Messages.get().gen_updateAborted());
                        cache = null;
                        scriptletRunner = null;
                    }

                    public void failure(Throwable e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        clearStatus();
                        cache = null;
                        scriptletRunner = null;
                    }
                };
            }

            SampleService1.get().unlock(manager.getSample().getId(), elements, unlockCall);
        }
    }

    /**
     * Duplicates the manager loaded on the screen
     */
    protected void duplicate() {
        setBusy();

        if (duplicateCall == null) {
            duplicateCall = new AsyncCallbackUI<SampleTestReturnVO>() {
                public void success(SampleTestReturnVO result) {
                    ValidationErrorsList errors;

                    if ( !Constants.domain().ENVIRONMENTAL.equals(result.getManager()
                                                                        .getSample()
                                                                        .getDomain())) {
                        /*
                         * the sample's domain may have changed after it was
                         * loaded on the screen, so it can't be edited here
                         */
                        Window.alert(Messages.get().sample_domainChangedException());

                        /*
                         * the manager is set to null because the manager
                         * returned from the back-end has null or negative ids
                         * and the screen can't be loaded from such a manager in
                         * a non-editing state
                         */
                        manager = null;
                        evaluateEdit();
                        setData();
                        setState(DEFAULT);
                        fireDataChange();
                        clearStatus();
                        return;
                    }

                    previousManager = manager;
                    manager = result.getManager();

                    buildCache();
                    evaluateEdit();
                    setData();
                    setState(ADD);
                    fireDataChange();
                    accessionNumber.setFocus(true);

                    /*
                     * show any errors/warnings found during duplication
                     */
                    errors = result.getErrors();
                    if (errors != null && errors.size() > 0) {
                        if (errors.hasWarnings())
                            Window.alert(getWarnings(errors.getErrorList()));
                        if (errors.hasErrors())
                            showErrors(errors);
                        else
                            setDone(Messages.get().gen_enterInformationPressCommit());
                    } else {
                        setDone(Messages.get().gen_enterInformationPressCommit());
                    }
                }

                public void failure(Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    clearStatus();
                }
            };
        }

        SampleService1.get().duplicate(manager.getSample().getId(), duplicateCall);
    }

    private void fromTRF() {
        org.openelis.ui.widget.Window window;

        if (fromTRF.isChecked()) {
            /*
             * if the user checks the checkbox for showing attachment screen
             * then open the attachment screen if it's closed
             */
            if (attachmentScreen != null)
                return;

            try {
                window = new org.openelis.ui.widget.Window();
                window.setName(Messages.get().attachment_attachment());
                window.setSize("782px", "521px");
                attachmentScreen = new AttachmentScreenUI(window) {
                    @Override
                    public void search() {
                        QueryData field;

                        query = new Query();
                        field = new QueryData();
                        field.setQuery("%");
                        query.setFields(field);
                        query.setRowsPerPage(ROWS_PER_PAGE);
                        isNewQuery = true;
                        isLoadedFromQuery = true;
                        managers = null;

                        executeQuery(query);
                    }

                    @Override
                    public void attachmentSelected(Integer id) {
                        if (id != null)
                            displayAttachment(id);
                    }
                    
                    @Override
                    public boolean isDataEntry() {
                        return true;
                    }
                };
                window.setContent(attachmentScreen);
                OpenELIS.getBrowser().addWindow(window, "attachment");
                attachmentScreen.search();
                window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
                    @Override
                    public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                        attachmentScreen = null;
                        fromTRF.setCheck(false);
                    }
                });
            } catch (Throwable e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        } else {
            /*
             * if the user unchecks the checkbox for showing attachment screen
             * then close the attachment screen if it's open
             */
            if (attachmentScreen != null) {
                attachmentScreen.getWindow().close();
                attachmentScreen = null;
            }
        }
    }

    /**
     * Shows the order linked to the sample on the screen corresponding to the
     * type of order e.g. Send-out order screen for environmental samples
     */
    @UiHandler("orderLookupButton")
    protected void orderLookup(ClickEvent event) {
        org.openelis.ui.widget.Window window;
        final SendoutOrderScreenUI orderScreen;
        ScheduledCommand cmd;

        if (getOrderId() == null)
            return;

        try {
            window = new org.openelis.ui.widget.Window();
            window.setName(Messages.get().order_sendoutOrder());
            window.setSize("1020px", "588px");
            orderScreen = new SendoutOrderScreenUI(window);
            window.setContent(orderScreen);
            OpenELIS.getBrowser().addWindow(window, "sendoutOrder");
            cmd = new ScheduledCommand() {
                @Override
                public void execute() {
                    orderScreen.query(manager.getSample().getOrderId());
                }
            };
            Scheduler.get().scheduleDeferred(cmd);
        } catch (Throwable e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Overridden because the patient fields can be enabled or disabled several
     * times in Add or Update states, based on factors such as whether the
     * patient is locked
     */
    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    /**
     * Validates the screen and sets the status of validation to "Flagged" if
     * some operation needs to be completed before committing
     */
    public Validation validate() {
        Validation validation;

        if (isState(QUERY)) {
            /*
             * the user can't query for both report to and bill to
             */
            if (reportToName.getQuery() != null && billToName.getQuery() != null) {
                reportToName.addException(new Exception(Messages.get()
                                                                .sampleOrganization_cantQueryByMultipleTypeException()));
                billToName.addException(new Exception(Messages.get()
                                                              .sampleOrganization_cantQueryByMultipleTypeException()));
            } else {
                reportToName.clearExceptions();
                billToName.clearExceptions();
            }
        }

        validation = super.validate();
        if (isBusy)
            validation.setStatus(FLAGGED);

        return validation;
    }

    /**
     * Returns the list of fields that the user wants to query by
     */
    public ArrayList<QueryData> getQueryFields() {
        String type;
        QueryData nameField, typeField;
        ArrayList<QueryData> fields;

        fields = super.getQueryFields();
        nameField = null;
        type = null;

        /*
         * find the query field for sample organization
         */
        for (QueryData field : fields) {
            if (REPORT_TO_KEY.equals(field.getKey())) {
                nameField = field;
                type = Constants.dictionary().ORG_REPORT_TO.toString();
            } else if (BILL_TO_KEY.equals(field.getKey())) {
                nameField = field;
                type = Constants.dictionary().ORG_BILL_TO.toString();
            }
        }

        if (nameField == null)
            return fields;

        /*
         * the widgets for sample organization don't use keys from the meta, so
         * the correct key needs to be set in their query field
         */
        nameField.setKey(SampleMeta.getSampleOrgOrganizationName());

        /*
         * this field is added to restrict the query by the type of organization
         * queried for
         */
        typeField = new QueryData();
        typeField.setKey(SampleMeta.getSampleOrgTypeId());
        typeField.setQuery(type);
        typeField.setType(QueryData.Type.INTEGER);
        fields.add(typeField);

        return fields;
    }

    /**
     * Returns from the cache, the object that has the specified key and is of
     * the specified class
     */
    @Override
    public <T> T get(Object key, Class<?> c) {
        String cacheKey;
        Object obj;

        if (cache == null)
            return null;

        cacheKey = null;
        if (c == TestManager.class)
            cacheKey = Constants.uid().getTest((Integer)key);
        else if (c == AuxFieldGroupManager.class)
            cacheKey = Constants.uid().getAuxFieldGroup((Integer)key);

        obj = cache.get(cacheKey);
        if (obj != null)
            return (T)obj;

        /*
         * if the requested object is not in the cache then obtain it and put it
         * in the cache
         */
        try {
            if (c == TestManager.class)
                obj = TestService.get().fetchById((Integer)key);
            else if (c == AuxFieldGroupManager.class)
                obj = AuxiliaryService.get().fetchById((Integer)key);

            cache.put(cacheKey, obj);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return (T)obj;
    }

    /**
     * Shows the popup for the sample's projects
     */
    @UiHandler("projectButton")
    protected void project(ClickEvent event) {
        ModalWindow modal;

        if (sampleprojectLookUp == null) {
            sampleprojectLookUp = new SampleProjectLookupUI() {
                @Override
                public void ok() {
                    /*
                     * refresh the display of the autocomplete showing the
                     * project because the list of projects may have been
                     * changed through the popup
                     */
                    if (isState(ADD, UPDATE))
                        setProject(getFirstProject(manager));
                }
            };
        }

        modal = new ModalWindow();
        modal.setSize("550px", "400px");
        modal.setName(Messages.get().sampleProject());
        modal.setCSS(UIResources.INSTANCE.popupWindow());
        modal.setContent(sampleprojectLookUp);

        sampleprojectLookUp.setWindow(modal);
        sampleprojectLookUp.setData(manager, state);
    }

    /**
     * Shows the popup for the sample's organizations
     */
    @UiHandler("reportToButton")
    protected void reportTo(ClickEvent event) {
        showOrganizationLookup();
    }

    /**
     * Shows the popup for the sample's organizations
     */
    @UiHandler("billToButton")
    protected void billTo(ClickEvent event) {
        showOrganizationLookup();
    }

    /*
     * methods used for operations that affect the entire screen
     */

    /**
     * Fetches the manager for the sample with this id, sets the manager to null
     * if fetch fails or the id is null
     */
    protected void fetchById(Integer id) {
        if (id == null) {
            manager = null;
            setData();
            setState(DEFAULT);
            fireDataChange();
            clearStatus();
        } else {
            setBusy(Messages.get().gen_fetching());
            if (fetchByIdCall == null) {
                fetchByIdCall = new AsyncCallbackUI<SampleManager1>() {
                    public void success(SampleManager1 result) {
                        manager = result;
                        setData();
                        setState(DISPLAY);
                    }

                    public void notFound() {
                        fetchById(null);
                        setDone(Messages.get().gen_noRecordsFound());
                        nav.clearSelection();
                    }

                    public void failure(Throwable e) {
                        fetchById(null);
                        Window.alert(Messages.get().gen_fetchFailed() + e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        nav.clearSelection();
                    }

                    public void finish() {
                        fireDataChange();
                        clearStatus();
                    }
                };
            }

            SampleService1.get().fetchById(id, elements, fetchByIdCall);
        }
    }

    /**
     * Sets the latest manager in the tabs
     */
    private void setData() {
        sampleItemAnalysisTreeTab.setData(manager);
        sampleItemTab.setData(manager);
        analysisTab.setData(manager);
        resultTab.setData(manager);
        analysisNotesTab.setData(manager);
        sampleNotesTab.setData(manager);
        storageTab.setData(manager);
        qaEventTab.setData(manager);
        attachmentTab.setData(manager);
    }

    /**
     * Determines if the fields on the screen can be edited based on the data
     */
    private void evaluateEdit() {
        canEdit = (manager != null && !Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample()
                                                                                            .getStatusId()));
    }

    /**
     * Creates the cache of objects like TestManager that are used frequently by
     * the different parts of the screen
     */
    private void buildCache() {
        int i, j;
        Integer prevId;
        ArrayList<Integer> ids;
        SampleItemViewDO item;
        AnalysisViewDO ana;
        AuxDataViewDO aux;
        ArrayList<TestManager> tms;
        ArrayList<AuxFieldGroupManager> afgms;

        cache = new HashMap<String, Object>();

        try {
            /*
             * the list of tests to be fetched
             */
            ids = new ArrayList<Integer>();
            for (i = 0; i < manager.item.count(); i++ ) {
                item = manager.item.get(i);
                for (j = 0; j < manager.analysis.count(item); j++ ) {
                    ana = manager.analysis.get(item, j);
                    ids.add(ana.getTestId());
                }
            }

            if (ids.size() > 0) {
                tms = TestService.get().fetchByIds(ids);
                for (TestManager tm : tms)
                    cache.put(Constants.uid().getTest(tm.getTest().getId()), tm);
            }

            /*
             * the list of aux field groups to be fetched
             */
            ids.clear();
            prevId = null;
            for (i = 0; i < manager.auxData.count(); i++ ) {
                aux = manager.auxData.get(i);
                if ( !aux.getAuxFieldGroupId().equals(prevId)) {
                    ids.add(aux.getAuxFieldGroupId());
                    prevId = aux.getAuxFieldGroupId();
                }
            }

            if (ids.size() > 0) {
                afgms = AuxiliaryService.get().fetchByIds(ids);
                for (AuxFieldGroupManager afgm : afgms)
                    cache.put(Constants.uid().getAuxFieldGroup(afgm.getGroup().getId()), afgm);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Creates a string containing the message that there are warnings on the
     * screen, followed by all warning messages, followed by the question
     * whether the data should be committed
     */
    private String getWarnings(ArrayList<Exception> warnings) {
        StringBuilder b;

        b = new StringBuilder();
        b.append(Messages.get().gen_warningDialogLine1()).append("\n");
        if (warnings != null) {
            for (Exception ex : warnings)
                b.append(" * ").append(ex.getMessage()).append("\n");
        }
        b.append("\n").append(Messages.get().gen_warningDialogLastLine());

        return b.toString();
    }

    /**
     * Returns true if the data for a field can be copied from the previous
     * manager that the screen was loaded with, based on the passed code
     * representing the key pressed by the user
     */
    private boolean canCopyFromPrevious(int keyCode) {
        return previousManager != null && KeyCodes.KEY_F2 == keyCode;
    }

    /**
     * If the passed id is not null then adds the scriptlet with the id to the
     * scriptlet runner; otherwise adds the scriptlets for the domain and for
     * all the records in the manager to the scriptlet runner
     */
    private void addScriptlet(Integer scriptletId) {
        HashSet<Integer> scids;

        if (scriptletRunner == null)
            scriptletRunner = new ScriptletRunner<SampleSO>();

        try {
            scids = new HashSet<Integer>();
            if (scriptletId == null) {
                /*
                 * add the scriptlet for the domain, which is the value of this
                 * system variable
                 */
                if (domainScriptletVariable == null) {
                    domainScriptletVariable = SystemVariableService.get()
                                                                   .fetchByExactName("environmental_ia_scriptlet_1");
                    domainScriptletId = DictionaryCache.getIdBySystemName(domainScriptletVariable.getValue());
                }
                scids.add(domainScriptletId);
            } else {
                scids.add(scriptletId);
            }

            addScriptlets(scids);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Adds the scriptlet with the passed ids to the scriptlet runner
     */
    private void addScriptlets(HashSet<Integer> ids) throws Exception {
        for (Integer id : ids)
            scriptletRunner.add((ScriptletInt<SampleSO>)ScriptletFactory.get(id));
    }

    /**
     * Runs the scriptlet with the passed id for the passed operation performed
     * on the field "changed" of the record with the passed uid.
     */
    private void runScriptlet(String uid, String changed, Operation operation) {
        SampleSO data;
        EnumSet<Operation> operations;
        ValidationErrorsList errors;

        /*
         * create the sciptlet object
         */
        data = new SampleSO();
        operations = EnumSet.of(operation);
        if (manager.getSampleEnvironmental().getId() == null &&
            Operation.NEW_DOMAIN_ADDED != operation)
            /*
             * this is either an uncommitted sample or was a quick-entry sample
             * before being loaded on the screen
             */
            operations.add(Operation.NEW_DOMAIN_ADDED);
        data.setOperations(operations);
        data.setChanged(changed);
        data.setManager(manager);

        /*
         * run the scritplet and show the errors and the changed data
         */
        data = scriptletRunner.run(data);

        if (data.getExceptions() != null && data.getExceptions().size() > 0) {
            errors = new ValidationErrorsList();
            for (Exception e : data.getExceptions())
                errors.add(e);
            showErrors(errors);
        } else {
            clearErrors();
        }

        manager = data.getManager();
        evaluateEdit();
        setData();
        setState(state);
        fireDataChange();
    }

    /**
     * Runs the scriptlet for the environmental domain
     */
    private void runDomainScriptlet(Operation operation) {
        runScriptlet(null, null, operation);
    }

    /*
     * getters and setters for the fields at the sample or domain level
     */

    /**
     * Returns the accession number or null if the manager is null
     */
    private Integer getAccessionNumber() {
        if (manager == null)
            return null;
        return manager.getSample().getAccessionNumber();
    }

    /**
     * Calls the service method to merge a quick-entered sample that has this
     * accession number with the sample on the screen, if the number is not
     * null, the user confirms changing it and the sample on the screen is not
     * an existing one. Otherwise, just sets the number in the manager.
     */
    private void setAccessionNumber(Integer accession) {
        if (accession == null) {
            manager.getSample().setAccessionNumber(accession);
            return;
        }

        if (getAccessionNumber() != null) {
            if ( !Window.confirm(Messages.get().sample_accessionNumberEditConfirm())) {
                accessionNumber.setValue(getAccessionNumber());
                accessionNumber.setFocus(true);
                return;
            }
        }

        /*
         * remove any exceptions added because of the previous value
         */
        accessionNumber.clearExceptions();

        manager.getSample().setAccessionNumber(accession);
        setBusy(Messages.get().gen_fetching());
        if (isState(ADD)) {
            if (mergeQuickEntryCall == null) {
                mergeQuickEntryCall = new AsyncCallbackUI<SampleManager1>() {
                    @Override
                    public void success(SampleManager1 result) {
                        manager = result;
                        runDomainScriptlet(Operation.NEW_DOMAIN_ADDED);
                        setData();
                        setState(UPDATE);
                        fireDataChange();
                    }

                    public void notFound() {
                        /*
                         * ignore because there's no sample with the accession
                         * number entered by the user
                         */
                    }

                    public void failure(Throwable e) {
                        if (e instanceof InconsistencyException) {
                            accessionNumber.addException((InconsistencyException)e);
                        } else {
                            manager.getSample().setAccessionNumber(null);
                            accessionNumber.setValue(null);
                            Window.alert(e.getMessage());
                            logger.log(Level.SEVERE, e.getMessage(), e);
                        }
                    }

                    public void finish() {
                        clearStatus();
                        isBusy = false;
                    }
                };
            }
            isBusy = true;
            /*
             * this is an async call to make sure that the focus gets set to the
             * field next in the tabbing order to accession number, regardless
             * of the browser and OS, which may not happen with a sync call
             */
            SampleService1.get().mergeQuickEntry(manager, mergeQuickEntryCall);
        } else if (isState(UPDATE)) {
            if (validateAccessionNumberCall == null) {
                validateAccessionNumberCall = new AsyncCallbackUI<Void>() {
                    @Override
                    public void success(Void result) {
                        /*
                         * ignore because the accession number is valid, as no
                         * exceptions were thrown
                         */
                    }

                    public void failure(Throwable e) {
                        if (e instanceof InconsistencyException) {
                            accessionNumber.addException((InconsistencyException)e);
                        } else {
                            manager.getSample().setAccessionNumber(null);
                            accessionNumber.setValue(null);
                            Window.alert(e.getMessage());
                            logger.log(Level.SEVERE, e.getMessage(), e);
                        }
                    }

                    public void finish() {
                        clearStatus();
                        isBusy = false;
                    }
                };
            }

            isBusy = true;
            /*
             * this is an async call to make sure that the focus gets set to the
             * field next in the tabbing order to accession number, regardless
             * of the browser and OS, which may not happen with a sync call
             */
            SampleService1.get().validateAccessionNumber(manager, validateAccessionNumberCall);
        }
    }

    /**
     * Returns the order id or null if the manager is null
     */
    private Integer getOrderId() {
        if (manager == null)
            return null;
        return manager.getSample().getOrderId();
    }

    /**
     * Calls the service method to load the sample from the order with the
     * specified id, if the id and the accession number are not null. Loads the
     * screen with the returned manager.
     */
    private void setOrderId(Integer ordId) {
        SampleTestReturnVO ret;
        ValidationErrorsList errors;

        if (ordId == null) {
            manager.getSample().setOrderId(ordId);
            return;
        }

        if (getAccessionNumber() == null) {
            Window.alert(Messages.get().sample_enterAccNumBeforeOrderLoad());
            orderId.setValue(null);
            return;
        }

        try {
            setBusy(Messages.get().gen_fetching());
            ret = SampleService1.get().importOrder(manager, ordId);
            manager = ret.getManager();
            setData();
            fireDataChange();
            clearStatus();
            /*
             * show any validation errors encountered while importing the order
             * or the pop up for selecting the prep/reflex tests for the tests
             * added during the import
             */
            errors = ret.getErrors();
            if (errors != null && errors.size() > 0) {
                if (errors.hasWarnings())
                    Window.alert(getWarnings(errors.getErrorList()));
                if (errors.hasErrors())
                    showErrors(errors);
                isBusy = false;
            } else if (ret.getTests() == null || ret.getTests().size() == 0) {
                isBusy = false;
            } else {
                showTests(ret);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            clearStatus();
        }
    }

    /**
     * Returns the collection date or null if the manager is null
     */
    private Datetime getCollectionDate() {
        if (manager == null)
            return null;
        return manager.getSample().getCollectionDate();
    }

    /**
     * Sets the collection date; also resets the transfusion age
     */
    private void setCollectionDate(Datetime date) {
        manager.getSample().setCollectionDate(date);
    }

    /**
     * Returns the collection time or null if the manager is null
     */
    private Datetime getCollectionTime() {
        if (manager == null)
            return null;
        return manager.getSample().getCollectionTime();
    }

    /**
     * Sets the collection time
     */
    private void setCollectionTime(Datetime time) {
        manager.getSample().setCollectionTime(time);
    }

    /**
     * Returns the received date or null if the manager is null
     */
    private Datetime getReceivedDate() {
        if (manager == null)
            return null;
        return manager.getSample().getReceivedDate();
    }

    /**
     * Sets the received date
     */
    private void setReceivedDate(Datetime date) {
        manager.getSample().setReceivedDate(date);
    }

    /**
     * Returns the status or null if the manager is null
     */
    private Integer getStatusId() {
        if (manager == null)
            return null;
        return manager.getSample().getStatusId();
    }

    /**
     * Sets the status id
     */
    private void setStatusId(Integer statusId) {
        manager.getSample().setStatusId(statusId);
    }

    /**
     * Returns the client reference or null if the manager is null
     */
    private String getClientReference() {
        if (manager == null)
            return null;
        return manager.getSample().getClientReference();
    }

    /**
     * Sets the client reference
     */
    private void setClientReference(String clientReference) {
        manager.getSample().setClientReference(clientReference);
    }

    /*
     * getters and setters for the fields at the domain level
     */

    /**
     * returns is hazardous or null if the manager is null or if this is not an
     * environmental sample
     */
    private String getIsHazardous() {
        if (manager == null || manager.getSampleEnvironmental() == null)
            return null;
        return manager.getSampleEnvironmental().getIsHazardous();
    }

    /**
     * sets is hazardous
     */
    private void setIsHazardous(String isHazardous) {
        manager.getSampleEnvironmental().setIsHazardous(isHazardous);
    }

    /**
     * returns the priority or null if the manager is null or if this is not an
     * environmental sample
     */
    private Integer getPriority() {
        if (manager == null || manager.getSampleEnvironmental() == null)
            return null;
        return manager.getSampleEnvironmental().getPriority();
    }

    /**
     * sets the priority
     */
    private void setPriority(Integer priority) {
        manager.getSampleEnvironmental().setPriority(priority);
    }

    /**
     * returns the collector or null if the manager is null or if this is not an
     * environmental sample
     */
    private String getCollector() {
        if (manager == null || manager.getSampleEnvironmental() == null)
            return null;
        return manager.getSampleEnvironmental().getCollector();
    }

    /**
     * sets the priority
     */
    private void setCollector(String collector) {
        manager.getSampleEnvironmental().setCollector(collector);
    }

    /**
     * returns the collector phone or null if the manager is null or if this is
     * not an environmental sample
     */
    private String getCollectorPhone() {
        if (manager == null || manager.getSampleEnvironmental() == null)
            return null;
        return manager.getSampleEnvironmental().getCollectorPhone();
    }

    /**
     * sets the collector phone
     */
    private void setCollectorPhone(String collectorPhone) {
        manager.getSampleEnvironmental().setCollectorPhone(collectorPhone);
    }

    /**
     * returns the location or null if the manager is null or if this is not an
     * environmental sample
     */
    private String getLocation() {
        if (manager == null || manager.getSampleEnvironmental() == null)
            return null;
        return manager.getSampleEnvironmental().getLocation();
    }

    /**
     * sets the location
     */
    private void setLocation(String location) {
        manager.getSampleEnvironmental().setLocation(location);
    }

    /**
     * returns the description or null if the manager is null or if this is not
     * an environmental sample
     */
    private String getDescription() {
        if (manager == null || manager.getSampleEnvironmental() == null)
            return null;
        return manager.getSampleEnvironmental().getDescription();
    }

    /**
     * sets the description
     */
    private void setDescription(String description) {
        manager.getSampleEnvironmental().setDescription(description);
    }

    /**
     * returns the location's multiple unit (apt/suite) or null if the manager
     * is null or if this is not an environmental sample
     */
    private String getLocationAddressMultipleUnit() {
        if (manager == null || manager.getSampleEnvironmental() == null)
            return null;
        return manager.getSampleEnvironmental().getLocationAddress().getMultipleUnit();
    }

    /**
     * sets the location's multiple unit (apt/suite)
     */
    private void setLocationAddressMultipleUnit(String multipleUnit) {
        manager.getSampleEnvironmental().getLocationAddress().setMultipleUnit(multipleUnit);

    }

    /**
     * returns the location's street address or null if the manager is null or
     * if this is not an environmental sample
     */
    private String getLocationAddressStreetAddress() {
        if (manager == null || manager.getSampleEnvironmental() == null)
            return null;
        return manager.getSampleEnvironmental().getLocationAddress().getStreetAddress();
    }

    /**
     * sets the location's street address
     */
    private void setLocationAddressStreetAddress(String streetAddress) {
        manager.getSampleEnvironmental().getLocationAddress().setStreetAddress(streetAddress);

    }

    /**
     * returns the location's city or null if the manager is null or if this is
     * not an environmental sample
     */
    private String getLocationAddressCity() {
        if (manager == null || manager.getSampleEnvironmental() == null)
            return null;
        return manager.getSampleEnvironmental().getLocationAddress().getCity();
    }

    /**
     * sets the location's city
     */
    private void setLocationAddressCity(String city) {
        manager.getSampleEnvironmental().getLocationAddress().setCity(city);
    }

    /**
     * returns the location's state or null if the manager is null or if this is
     * not an environmental sample
     */
    private String getLocationAddressState() {
        if (manager == null || manager.getSampleEnvironmental() == null)
            return null;
        return manager.getSampleEnvironmental().getLocationAddress().getState();
    }

    /**
     * sets the location's state
     */
    private void setLocationAddressState(String state) {
        manager.getSampleEnvironmental().getLocationAddress().setState(state);

    }

    /**
     * returns the location's zip code or null if the manager is null or if this
     * is not an environmental sample
     */
    private String getLocationAddressZipCode() {
        if (manager == null || manager.getSampleEnvironmental() == null)
            return null;
        return manager.getSampleEnvironmental().getLocationAddress().getZipCode();
    }

    /**
     * sets the location's zip code
     */
    private void setLocationAddressZipCode(String zipCode) {
        manager.getSampleEnvironmental().getLocationAddress().setZipCode(zipCode);
    }

    /**
     * returns the location's country or null if the manager is null or if this
     * is not an environmental sample
     */
    private String getLocationAddressCountry() {
        if (manager == null || manager.getSampleEnvironmental() == null)
            return null;
        return manager.getSampleEnvironmental().getLocationAddress().getCountry();
    }

    /**
     * sets the location's country
     */
    private void setLocationAddressCountry(String country) {
        manager.getSampleEnvironmental().getLocationAddress().setCountry(country);
    }

    /**
     * Sets the display of the autocomplete for project from the passed sample
     * project
     */
    private void setProject(SampleProjectViewDO data) {
        if (data != null)
            projectName.setValue(data.getProjectId(), data.getProjectName());
        else
            projectName.setValue(null, "");
    }

    /**
     * Adds or updates the first project of the sample if the argument is not
     * null, otherwise deletes the first project. Also refreshes the display of
     * the autocomplete.
     */
    private void changeProject(ProjectDO proj) {
        SampleProjectViewDO data;

        data = getFirstProject(manager);
        if (proj == null) {
            /*
             * if a project was not selected and if there were projects present
             * then the first project is deleted and the next project is set as
             * the first one
             */
            if (data != null) {
                manager.project.remove(data);
                data = getFirstProject(manager);
            }
        } else {
            /*
             * otherwise the first project is modified or a new one is created
             * if no project existed
             */
            if (data == null)
                data = manager.project.add();

            data.setProjectId(proj.getId());
            data.setProjectName(proj.getName());
            data.setProjectDescription(proj.getDescription());
        }

        setProject(data);
    }

    /**
     * Returns the first project from the manager, or null if the manager is
     * null or it doesn't have any projects
     */
    private SampleProjectViewDO getFirstProject(SampleManager1 sm) {
        if (sm == null)
            return null;

        if (sm.project.count() > 0)
            return sm.project.get(0);

        return null;
    }

    /**
     * Sets the display of the autocomplete for report-to from the passed sample
     * organization
     */
    private void setReportTo(SampleOrganizationViewDO data) {
        if (data != null)
            reportToName.setValue(data.getOrganizationId(), data.getOrganizationName());
        else
            reportToName.setValue(null, "");
    }

    /**
     * Sets the display of the autocomplete for bill-to from the passed sample
     * organization
     */
    private void setBillTo(SampleOrganizationViewDO data) {
        if (data != null)
            billToName.setValue(data.getOrganizationId(), data.getOrganizationName());
        else
            billToName.setValue(null, "");
    }

    /**
     * Adds or updates the sample organization of the specified type if the
     * passed organization is not null, otherwise deletes the sample
     * organization of this type. Also refreshes the display of the autocomplete
     * showing the organization of this type.
     */
    private void changeOrganization(Integer type, OrganizationDO org) {
        SampleOrganizationViewDO data;

        data = getSampleOrganization(manager, type);
        if (org == null) {
            /*
             * this method is called only when the organization changes and if
             * there isn't a sample organization of this type selected
             * currently, then there must have been before, thus it needs to be
             * removed from the manager
             */
            manager.organization.remove(data);
        } else {
            if (data == null) {
                /*
                 * an organization was selected by the user but there isn't one
                 * present in the manager, thus it needs to be added
                 */
                data = manager.organization.add(org);
                data.setTypeId(type);
            } else {
                /*
                 * the organization was changed, so the sample organization
                 * needs to be updated
                 */
                data.setOrganizationId(org.getId());
                data.setOrganizationName(org.getName());
                data.setOrganizationMultipleUnit(org.getAddress().getMultipleUnit());
                data.setOrganizationStreetAddress(org.getAddress().getStreetAddress());
                data.setOrganizationCity(org.getAddress().getCity());
                data.setOrganizationState(org.getAddress().getState());
                data.setOrganizationZipCode(org.getAddress().getZipCode());
                data.setOrganizationCountry(org.getAddress().getCountry());
            }

            /*
             * warn the user if samples from this organization are to held or
             * refused
             */
            try {
                showHoldRefuseWarning(org.getId(), org.getName());
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }

            if (Constants.dictionary().ORG_REPORT_TO.equals(type))
                setReportTo(data);
            else if (Constants.dictionary().ORG_BILL_TO.equals(type))
                setBillTo(data);
        }
    }

    /**
     * Warn the user if samples from this organization are to held or refused
     */
    private void showHoldRefuseWarning(Integer orgId, String name) throws Exception {
        if (SampleOrganizationUtility1.isHoldRefuseSampleForOrg(orgId))
            Window.alert(Messages.get().gen_orgMarkedAsHoldRefuseSample(name));
    }

    /**
     * Shows the popup for this sample's organizations
     */
    private void showOrganizationLookup() {
        ModalWindow modal;

        if (sampleOrganizationLookup == null) {
            sampleOrganizationLookup = new SampleOrganizationLookupUI() {
                @Override
                public void ok() {
                    if (isState(ADD, UPDATE)) {
                        /*
                         * refresh the display of the autocompletes showing
                         * organizations because the list of organizations may
                         * have been changed through the popup
                         */
                        setReportTo(getSampleOrganization(manager,
                                                          Constants.dictionary().ORG_REPORT_TO));
                        setBillTo(getSampleOrganization(manager, Constants.dictionary().ORG_BILL_TO));
                    }
                }
            };
        }

        modal = new ModalWindow();
        modal.setSize("800px", "400px");
        modal.setName(Messages.get().sampleOrganization());
        modal.setCSS(UIResources.INSTANCE.popupWindow());
        modal.setContent(sampleOrganizationLookup);

        sampleOrganizationLookup.setWindow(modal);
        sampleOrganizationLookup.setData(manager, state);
    }

    /**
     * returns the organization of the specified type from the manager or null
     * if the manager is null or doesn't have an organization of this type
     */
    private SampleOrganizationViewDO getSampleOrganization(SampleManager1 sm, Integer type) {
        ArrayList<SampleOrganizationViewDO> orgs;

        if (sm == null)
            return null;

        orgs = sm.organization.getByType(type);
        if (orgs != null && orgs.size() > 0)
            return orgs.get(0);

        return null;
    }

    /**
     * Adds the aux groups with the given ids to the manager; shows any errors
     * found while adding the groups
     */
    private void addAuxGroups(ArrayList<Integer> ids) {
        SampleTestReturnVO ret;
        ValidationErrorsList errors;

        setBusy();
        try {
            ret = SampleService1.get().addAuxGroups(manager, ids);
            manager = ret.getManager();
            setData();
            setState(state);
            bus.fireEventFromSource(new AddAuxGroupEvent(ids), this);
            clearStatus();
            errors = ret.getErrors();
            if (errors != null && errors.size() > 0) {
                if (errors.hasWarnings())
                    Window.alert(getWarnings(errors.getErrorList()));
                if (errors.hasErrors())
                    showErrors(errors);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            clearStatus();
        }
    }

    /**
     * removes the aux groups with the given ids from the manager
     */
    private void removeAuxGroups(ArrayList<Integer> ids) {
        setBusy();
        try {
            manager = SampleService1.get().removeAuxGroups(manager, ids);
            setData();
            setState(state);
            bus.fireEventFromSource(new RemoveAuxGroupEvent(ids), screen);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        clearStatus();
    }

    /**
     * Adds the tests/panels in the list to the sample; shows any errors found
     * while adding the tests or the popup for selecting additional prep/reflex
     * tests
     */
    private void addAnalyses(ArrayList<SampleTestRequestVO> tests) {
        SampleTestReturnVO ret;
        ValidationErrorsList errors;

        setBusy();
        try {
            ret = SampleService1.get().addAnalyses(manager, tests);
            manager = ret.getManager();
            setData();
            setState(state);
            /*
             * notify the tabs that some new tests have been added
             */
            bus.fireEventFromSource(new AddTestEvent(tests), this);
            clearStatus();
            /*
             * show any validation errors encountered while adding the tests or
             * the pop up for selecting the prep/reflex tests for the tests
             * added
             */
            errors = ret.getErrors();
            if (errors != null && errors.size() > 0) {
                if (errors.hasWarnings())
                    Window.alert(getWarnings(errors.getErrorList()));
                if (errors.hasErrors())
                    showErrors(errors);
            } else if (ret.getTests() == null || ret.getTests().size() == 0) {
                isBusy = false;
            } else {
                showTests(ret);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            clearStatus();
        }
    }

    /**
     * Changes the method of the analysis with this uid to the passed value;
     * shows any errors found while making the change or the popup for selecting
     * additional prep/reflex tests
     */
    private void changeAnalysisMethod(String uid, Integer methodId) {
        AnalysisViewDO ana;
        SampleTestReturnVO ret;
        ValidationErrorsList errors;

        ana = (AnalysisViewDO)manager.getObject(uid);
        try {
            setBusy();
            ret = SampleService1.get().changeAnalysisMethod(manager, ana.getId(), methodId);
            manager = ret.getManager();
            setData();
            setState(state);

            /*
             * notify all tabs that need to refresh themselves because of the
             * change in the analysis
             */
            bus.fireEventFromSource(new AnalysisChangeEvent(uid,
                                                            methodId,
                                                            AnalysisChangeEvent.Action.METHOD_CHANGED),
                                    screen);
            bus.fireEvent(new ResultChangeEvent(uid));
            clearStatus();
            /*
             * show any validation errors encountered while changing the method
             * or the pop up for selecting the prep/reflex tests for the tests
             * added
             */
            errors = ret.getErrors();
            if (errors != null && errors.size() > 0) {
                if (errors.hasWarnings())
                    Window.alert(getWarnings(errors.getErrorList()));
                if (errors.hasErrors())
                    showErrors(errors);
                isBusy = false;
            } else if (ret.getTests() == null || ret.getTests().size() == 0) {
                isBusy = false;
            } else {
                showTests(ret);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            clearStatus();
        }
    }

    /**
     * Changes the status of the analysis with this uid to the passed value
     */
    private void changeAnalysisStatus(String uid, Integer statusId) {
        AnalysisViewDO ana;

        ana = (AnalysisViewDO)manager.getObject(uid);
        try {
            setBusy();
            manager = SampleService1.get().changeAnalysisStatus(manager, ana.getId(), statusId);
            setData();
            setState(state);

            /*
             * notify all tabs that need to refresh themselves because of the
             * change in the analysis
             */
            bus.fireEventFromSource(new AnalysisChangeEvent(uid,
                                                            statusId,
                                                            AnalysisChangeEvent.Action.STATUS_CHANGED),
                                    screen);
            bus.fireEvent(new ResultChangeEvent(uid));
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        clearStatus();
    }

    /**
     * Changes the unit of the analysis with this uid to the passed value
     */
    private void changeAnalysisUnit(String uid, Integer unitId) {
        AnalysisViewDO ana;

        ana = (AnalysisViewDO)manager.getObject(uid);
        try {
            setBusy();
            manager = SampleService1.get().changeAnalysisUnit(manager, ana.getId(), unitId);
            setData();
            setState(state);

            /*
             * notify all tabs that need to refresh themselves because of the
             * change in the analysis
             */
            bus.fireEventFromSource(new AnalysisChangeEvent(uid,
                                                            unitId,
                                                            AnalysisChangeEvent.Action.UNIT_CHANGED),
                                    screen);
            bus.fireEvent(new ResultChangeEvent(uid));
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        clearStatus();
    }

    /**
     * Changes the prep analysis of the analysis with this uid to the passed
     * value
     */
    private void changeAnalysisPrep(String uid, Integer preAnalysisId) {
        AnalysisViewDO ana;

        ana = (AnalysisViewDO)manager.getObject(uid);
        try {
            setBusy();
            manager = SampleService1.get().changeAnalysisPrep(manager, ana.getId(), preAnalysisId);
            setData();
            setState(state);

            /*
             * notify all tabs that need to refresh themselves because of the
             * change in the analysis
             */
            bus.fireEventFromSource(new AnalysisChangeEvent(uid,
                                                            preAnalysisId,
                                                            AnalysisChangeEvent.Action.PREP_CHANGED),
                                    screen);
            bus.fireEvent(new ResultChangeEvent(uid));
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        clearStatus();
    }

    /**
     * Adds to the analysis, at the given indexes, the result rows beginning
     * with the specified row analytes
     */
    private void addRowAnalytes(AnalysisViewDO ana, ArrayList<TestAnalyteViewDO> analytes,
                                ArrayList<Integer> indexes) {
        setBusy();
        try {
            manager = SampleService1.get().addRowAnalytes(manager, ana, analytes, indexes);
            setData();
            setState(state);
            bus.fireEvent(new ResultChangeEvent(Constants.uid().get(ana)));
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        clearStatus();
    }

    /**
     * Shows the popup for selecting the prep/reflex tests for the analyses in
     * the VO
     */
    private void showTests(SampleTestReturnVO ret) {
        ModalWindow modal;

        /*
         * show the pop for selecting prep/reflex tests
         */
        if (testSelectionLookup == null) {
            testSelectionLookup = new TestSelectionLookupUI() {
                @Override
                public TestManager getTestManager(Integer testId) {
                    return screen.get(testId, TestManager.class);
                }

                @Override
                public void ok() {
                    ArrayList<SampleTestRequestVO> tests;

                    tests = testSelectionLookup.getSelectedTests();
                    /*
                     * keep isBusy to be true if some tests were selected on the
                     * popup because they need to be added to the manager
                     */
                    if (tests != null && tests.size() > 0)
                        addAnalyses(tests);
                    else
                        isBusy = false;
                }
            };
        }

        /*
         * make sure that the data can't be committed before the process of
         * adding tests has completed
         */
        isBusy = true;

        modal = new ModalWindow();
        modal.setSize("520px", "350px");
        modal.setName(Messages.get().testSelection_prepTestSelection());
        modal.setCSS(UIResources.INSTANCE.popupWindow());
        modal.setContent(testSelectionLookup);

        testSelectionLookup.setData(manager, ret.getTests());
        testSelectionLookup.setWindow(modal);
    }
}