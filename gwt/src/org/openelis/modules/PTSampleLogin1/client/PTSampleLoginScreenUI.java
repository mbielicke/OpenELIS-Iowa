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
package org.openelis.modules.PTSampleLogin1.client;

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
import org.openelis.domain.AnalysisDO;
import org.openelis.domain.AnalysisQaEventDO;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AttachmentDO;
import org.openelis.domain.AttachmentItemViewDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.IdAccessionVO;
import org.openelis.domain.NoteDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.ResultDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleItemDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.domain.SampleTestRequestVO;
import org.openelis.domain.SampleTestReturnVO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.manager.AttachmentManager;
import org.openelis.manager.AuxFieldGroupManager1;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.attachment.client.AddAttachmentEvent;
import org.openelis.modules.attachment.client.AttachmentEntry;
import org.openelis.modules.attachment.client.AttachmentUtil;
import org.openelis.modules.attachment.client.DisplayAttachmentEvent;
import org.openelis.modules.attachment.client.TRFAttachmentScreenUI;
import org.openelis.modules.auxData.client.AddAuxGroupEvent;
import org.openelis.modules.auxData.client.AuxDataTabUI;
import org.openelis.modules.auxData.client.RemoveAuxGroupEvent;
import org.openelis.modules.auxiliary1.client.AuxiliaryService1Impl;
import org.openelis.modules.eventLog.client.EventLogService;
import org.openelis.modules.main.client.OpenELIS;
import org.openelis.modules.order1.client.OrderEntry;
import org.openelis.modules.order1.client.SendoutOrderScreenUI;
import org.openelis.modules.organization1.client.OrganizationService1Impl;
import org.openelis.modules.project.client.ProjectService;
import org.openelis.modules.sample1.client.AddRowAnalytesEvent;
import org.openelis.modules.sample1.client.AddTestEvent;
import org.openelis.modules.sample1.client.AnalysisChangeEvent;
import org.openelis.modules.sample1.client.AnalysisNotesTabUI;
import org.openelis.modules.sample1.client.AnalysisTabUI;
import org.openelis.modules.sample1.client.AttachmentTabUI;
import org.openelis.modules.sample1.client.ClinicalTabUI;
import org.openelis.modules.sample1.client.NoteChangeEvent;
import org.openelis.modules.sample1.client.PatientPermission;
import org.openelis.modules.sample1.client.QAEventAddedEvent;
import org.openelis.modules.sample1.client.QAEventRemovedEvent;
import org.openelis.modules.sample1.client.QAEventTabUI;
import org.openelis.modules.sample1.client.RemoveAnalysisEvent;
import org.openelis.modules.sample1.client.ResultChangeEvent;
import org.openelis.modules.sample1.client.ResultTabUI;
import org.openelis.modules.sample1.client.RunScriptletEvent;
import org.openelis.modules.sample1.client.SampleHistoryUtility1;
import org.openelis.modules.sample1.client.SampleItemAddedEvent;
import org.openelis.modules.sample1.client.SampleItemAnalysisTreeTabUI;
import org.openelis.modules.sample1.client.SampleItemChangeEvent;
import org.openelis.modules.sample1.client.SampleItemChangeEvent.Action;
import org.openelis.modules.sample1.client.SampleItemTabUI;
import org.openelis.modules.sample1.client.SampleNotesTabUI;
import org.openelis.modules.sample1.client.SampleOrganizationLookupUI;
import org.openelis.modules.sample1.client.SampleOrganizationUtility1;
import org.openelis.modules.sample1.client.SampleProjectLookupUI;
import org.openelis.modules.sample1.client.SampleService1;
import org.openelis.modules.sample1.client.SelectionEvent;
import org.openelis.modules.sample1.client.StorageTabUI;
import org.openelis.modules.sample1.client.TestSelectionLookupUI;
import org.openelis.modules.scriptlet.client.ScriptletFactory;
import org.openelis.modules.systemvariable1.client.SystemVariableService1Impl;
import org.openelis.modules.test.client.TestService;
import org.openelis.scriptlet.SampleSO;
import org.openelis.scriptlet.SampleSO.Action_After;
import org.openelis.scriptlet.SampleSO.Action_Before;
import org.openelis.ui.common.Caution;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.SystemUserVO;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.Warning;
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
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Widget;

public class PTSampleLoginScreenUI extends Screen implements CacheProvider {

    @UiTemplate("PTSampleLogin.ui.xml")
    interface PTSampleLoginUiBinder extends UiBinder<Widget, PTSampleLoginScreenUI> {
    };

    private static PTSampleLoginUiBinder                uiBinder   = GWT.create(PTSampleLoginUiBinder.class);

    protected SampleManager1                            manager, previousManager;

    protected ScreenNavigator<IdAccessionVO>            nav;

    @UiField
    protected Calendar                                  collectionDate, collectionTime,
                    receivedDate, ptDueDate;

    @UiField
    protected TextBox<Integer>                          accessionNumber, orderId;

    @UiField
    protected TextBox<String>                           clientReference, ptSeries;

    @UiField
    protected Dropdown<Integer>                         status, ptPTProvider;

    @UiField
    protected Dropdown<String>                          ptAdditionalDomain;

    @UiField
    protected AutoComplete                              receivedByName, reportToName, billToName,
                    projectName;

    @UiField
    protected Button                                    query, previous, next, add, update, commit,
                    abort, optionsButton, orderLookupButton, projectButton, reportToButton,
                    billToButton;

    @UiField
    protected Menu                                      optionsMenu, historyMenu;

    @UiField
    protected MenuItem                                  duplicate, historySample, historySamplePT,
                    historySampleProject, historySampleOrganization, historySampleItem,
                    historyAnalysis, historyCurrentResult, historyStorage, historySampleQA,
                    historyAnalysisQA, historyAuxData;

    @UiField
    protected CheckMenuItem                             addWithTRF;

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

    @UiField(provided = true)
    protected ClinicalTabUI                             clinicalTab;

    protected boolean                                   canEdit, isBusy, closeLoginScreen,
                    isAttachmentScreenOpen, isFullLogin;

    protected ModulePermission                          samplePermission;

    protected PatientPermission                         patientPermission;

    protected PTSampleLoginScreenUI                     screen;

    protected TestSelectionLookupUI                     testSelectionLookup;

    protected SampleProjectLookupUI                     sampleprojectLookUp;

    protected SampleOrganizationLookupUI                sampleOrganizationLookup;

    protected TRFAttachmentScreenUI                     trfAttachmentScreen;

    protected Focusable                                 focusedWidget;

    protected HashMap<String, Object>                   cache;

    protected AsyncCallbackUI<ArrayList<IdAccessionVO>> queryCall;

    protected AsyncCallbackUI<SampleManager1>           addCall, fetchForUpdateCall,
                    commitUpdateCall, fetchByIdCall, unlockCall, mergeQuickEntryCall;

    protected AsyncCallbackUI<Void>                     validateAccessionNumberCall;

    protected AsyncCallbackUI<SampleTestReturnVO>       duplicateCall;

    protected AsyncCallback<SampleTestReturnVO>         setOrderIdCall;

    protected ScriptletRunner<SampleSO>                 scriptletRunner;

    protected HashMap<Integer, HashSet<Integer>>        scriptlets;

    protected SystemVariableDO                          attachmentPatternVariable,
                    genTRFPatternVariable;

    protected PageDirection                             pageDirection;

    protected static final SampleManager1.Load          elements[] = {
                    SampleManager1.Load.ANALYSISUSER, SampleManager1.Load.AUXDATA,
                    SampleManager1.Load.NOTE, SampleManager1.Load.ORGANIZATION,
                    SampleManager1.Load.PROJECT, SampleManager1.Load.QA,
                    SampleManager1.Load.RESULT, SampleManager1.Load.STORAGE,
                    SampleManager1.Load.WORKSHEET, SampleManager1.Load.ATTACHMENT};

    protected enum Tabs {
        SAMPLE_ITEM, ANALYSIS, TEST_RESULT, ANALYSIS_NOTES, SAMPLE_NOTES, STORAGE, QA_EVENTS,
        AUX_DATA, ATTACHMENT, CLINICAL
    };

    protected enum PageDirection {
        PREV, NEXT
    }

    private static final String REPORT_TO_KEY = "reportTo", BILL_TO_KEY = "billTo";

    /**
     * Check the permissions for this screen, intialize the tabs and widgets
     */
    public PTSampleLoginScreenUI(WindowInt window) throws Exception {
        setWindow(window);

        if (UserCache.getPermission().getModule("samplept") == null)
            throw new PermissionException(Messages.get()
                                                  .screenPermException("PT Sample Login Screen"));

        samplePermission = UserCache.getPermission().getModule("sample");
        if (samplePermission == null)
            samplePermission = new ModulePermission();

        patientPermission = new PatientPermission();

        try {
            CategoryCache.getBySystemNames("sample_status",
                                           "sample_domain",
                                           "pt_provider",
                                           "organization_type",
                                           "type_of_sample",
                                           "source_of_sample",
                                           "sample_container",
                                           "unit_of_measure",
                                           "analysis_status",
                                           "user_action",
                                           "unit_of_measure",
                                           "qaevent_type",
                                           "worksheet_status",
                                           "scriptlet_test",
                                           "scriptlet_test_analyte");
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
        attachmentTab = new AttachmentTabUI(this) {
            @Override
            public int count() {
                if (manager != null)
                    return manager.attachment.count();
                return 0;
            }

            @Override
            public AttachmentItemViewDO get(int i) {
                return manager.attachment.get(i);
            }

            @Override
            public String getAttachmentCreatedDateMetaKey() {
                return SampleMeta.getAttachmentItemAttachmentCreatedDate();
            }

            @Override
            public String getAttachmentSectionIdKey() {
                return SampleMeta.getAttachmentItemAttachmentSectionId();
            }

            @Override
            public String getAttachmentDescriptionKey() {
                return SampleMeta.getAttachmentItemAttachmentDescription();
            }

            @Override
            public AttachmentItemViewDO createAttachmentItem(AttachmentDO att) {
                AttachmentItemViewDO atti;

                atti = manager.attachment.add();
                atti.setId(manager.getNextUID());
                atti.setAttachmentId(att.getId());
                atti.setAttachmentCreatedDate(att.getCreatedDate());
                atti.setAttachmentSectionId(att.getSectionId());
                atti.setAttachmentDescription(att.getDescription());

                return atti;
            }

            @Override
            public void remove(int i) {
                manager.attachment.remove(i);
            }
        };
        clinicalTab = new ClinicalTabUI(this);

        initWidget(uiBinder.createAndBindUi(this));

        manager = null;

        initialize();
        evaluateEdit();
        setData();
        setState(DEFAULT);
        fireDataChange();

        logger.fine("PT Sample Login Screen Opened");
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
                                 samplePermission.hasSelectPermission());
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
                add.setEnabled(isState(ADD, DEFAULT, DISPLAY) &&
                               samplePermission.hasAddPermission());
                if (isState(ADD)) {
                    add.lock();
                    add.setPressed(true);
                }
            }
        });
        addShortcut(add, 'a', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                update.setEnabled(isState(UPDATE, DISPLAY) &&
                                  samplePermission.hasUpdatePermission());
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
                /*
                 * pageDirection keeps track of whether the previous or next
                 * page (sample) is to be fetched; so it's set to null when the
                 * previous and next buttons are disabled
                 */
                if ( !isState(DISPLAY))
                    pageDirection = null;
            }
        });

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
                duplicate.setEnabled(isState(DISPLAY) && samplePermission.hasAddPermission());
            }
        });

        duplicate.addCommand(new Command() {
            public void execute() {
                duplicate();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                addWithTRF.setEnabled(isState(ADD, DEFAULT, DISPLAY) &&
                                      samplePermission.hasAddPermission());
            }
        });

        addWithTRF.addCommand(new Command() {
            @Override
            public void execute() {
                addWithTRF();
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
                historySamplePT.setEnabled(isState(DISPLAY));
            }
        });

        historySamplePT.addCommand(new Command() {
            @Override
            public void execute() {
                SampleHistoryUtility1.pt(manager);
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
                             public void onDataChange(DataChangeEvent<Integer> event) {
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
                                 return forward ? orderId : projectName;
                             }
                         });

        addScreenHandler(orderId, SampleMeta.getOrderId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent<Integer> event) {
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
                        orderId.setValue(prevOrdId);
                        setOrderId(prevOrdId);
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
                             public void onDataChange(DataChangeEvent<Datetime> event) {
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
                             public void onDataChange(DataChangeEvent<Datetime> event) {
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
            public void onDataChange(DataChangeEvent<Datetime> event) {
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
            public void onDataChange(DataChangeEvent<Integer> event) {
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
                             public void onDataChange(DataChangeEvent<String> event) {
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
                                 return forward ? ptPTProvider : status;
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

        addScreenHandler(ptPTProvider,
                         SampleMeta.getPTPTProviderId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent<Integer> event) {
                                 ptPTProvider.setValue(getPTProviderId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setPTProviderId(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 ptPTProvider.setEnabled(isState(QUERY) ||
                                                         (canEdit && isState(ADD, UPDATE)));
                                 ptPTProvider.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? ptSeries : clientReference;
                             }
                         });

        addScreenHandler(ptSeries, SampleMeta.getPTSeries(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent<String> event) {
                ptSeries.setValue(getSeries());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setSeries(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                ptSeries.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                ptSeries.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? ptDueDate : ptPTProvider;
            }
        });

        addScreenHandler(ptDueDate, SampleMeta.getPTDueDate(), new ScreenHandler<Datetime>() {
            public void onDataChange(DataChangeEvent<Datetime> event) {
                ptDueDate.setValue(getDueDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                setDueDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                ptDueDate.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                ptDueDate.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? ptAdditionalDomain : ptSeries;
            }
        });

        addScreenHandler(ptAdditionalDomain,
                         SampleMeta.getPTAdditionalDomain(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent<String> event) {
                                 ptAdditionalDomain.setValue(getAdditionalDomain());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setAdditionalDomain(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 ptAdditionalDomain.setEnabled(isState(QUERY) ||
                                                               (canEdit && isState(ADD, UPDATE)));
                                 ptAdditionalDomain.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? receivedByName : ptDueDate;
                             }
                         });

        addScreenHandler(receivedByName,
                         SampleMeta.getReceivedById(),
                         new ScreenHandler<AutoCompleteValue>() {
                             public void onDataChange(DataChangeEvent<AutoCompleteValue> event) {
                                 try {
                                     receivedByName.setValue(getReceivedById(), getReceivedByName());
                                 } catch (Exception e) {
                                     Window.alert(e.getMessage());
                                     logger.log(Level.SEVERE, e.getMessage(), e);
                                 }
                             }

                             public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                                 Integer id;
                                 SystemUserVO user;

                                 id = null;
                                 if (event.getValue() != null) {
                                     user = (SystemUserVO)event.getValue().getData();
                                     id = user.getId();
                                 }
                                 setReceivedById(id);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 receivedByName.setEnabled(canEdit && isState(ADD, UPDATE));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? reportToName : ptAdditionalDomain;
                             }
                         });

        receivedByName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                Item<Integer> item;
                ArrayList<SystemUserVO> users;
                ArrayList<Item<Integer>> model;

                try {
                    users = UserCache.getEmployees(QueryFieldUtil.parseAutocomplete(event.getMatch() +
                                                                                    "%"));
                    model = new ArrayList<Item<Integer>>();
                    for (SystemUserVO user : users) {
                        item = new Item<Integer>(user.getId(), user.getLoginName());
                        item.setData(user);
                        model.add(item);
                    }
                    receivedByName.showAutoMatches(model);
                } catch (Exception e) {
                    Window.alert(e.toString());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        });

        addScreenHandler(reportToName, REPORT_TO_KEY, new ScreenHandler<AutoCompleteValue>() {
            public void onDataChange(DataChangeEvent<AutoCompleteValue> event) {
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
                return forward ? billToName : receivedByName;
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
                    list = OrganizationService1Impl.INSTANCE.fetchByIdOrName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<Item<Integer>>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new Item<Integer>(5);
                        data = list.get(i);

                        row.setKey(data.getId());
                        row.setData(data);
                        row.setCell(0, data.getName());
                        row.setCell(1, data.getAddress().getMultipleUnit());
                        row.setCell(2, data.getAddress().getStreetAddress());
                        row.setCell(3, data.getAddress().getCity());
                        row.setCell(4, data.getAddress().getState());

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
            public void onDataChange(DataChangeEvent<AutoCompleteValue> event) {
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
                return forward ? projectName : reportToName;
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
                    list = OrganizationService1Impl.INSTANCE.fetchByIdOrName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<Item<Integer>>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new Item<Integer>(5);
                        data = list.get(i);

                        row.setKey(data.getId());
                        row.setData(data);
                        row.setCell(0, data.getName());
                        row.setCell(1, data.getAddress().getMultipleUnit());
                        row.setCell(2, data.getAddress().getStreetAddress());
                        row.setCell(3, data.getAddress().getCity());
                        row.setCell(4, data.getAddress().getState());

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

        addScreenHandler(projectName,
                         SampleMeta.getProjectName(),
                         new ScreenHandler<AutoCompleteValue>() {
                             public void onDataChange(DataChangeEvent<AutoCompleteValue> event) {
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
                                 return forward ? accessionNumber : billToName;
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
                             public void onDataChange(DataChangeEvent<Object> event) {
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
            public void onDataChange(DataChangeEvent<Object> event) {
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
        sampleItemTab.setCanQuery(true);

        addScreenHandler(analysisTab, "analysisTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent<Object> event) {
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
            public void onDataChange(DataChangeEvent<Object> event) {
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
            public void onDataChange(DataChangeEvent<Object> event) {
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
            public void onDataChange(DataChangeEvent<Object> event) {
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
            public void onDataChange(DataChangeEvent<Object> event) {
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
            public void onDataChange(DataChangeEvent<Object> event) {
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
            public void onDataChange(DataChangeEvent<Object> event) {
                auxDataTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                auxDataTab.setState(event.getState());
            }

            public Object getQuery() {
                return auxDataTab.getQueryFields();
            }
        });
        auxDataTab.setCanQuery(true);

        addScreenHandler(attachmentTab, "attachmentTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent<Object> event) {
                attachmentTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                attachmentTab.setState(event.getState());
            }

            public Object getQuery() {
                return attachmentTab.getQueryFields();
            }
        });
        attachmentTab.setCanQuery(true);

        addScreenHandler(clinicalTab, "clinicalTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent<Object> event) {
                clinicalTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                tabPanel.setTabVisible(Tabs.CLINICAL.ordinal(),
                                       isState(QUERY) ||
                                                       (manager != null && manager.getSampleClinical() != null));
                clinicalTab.setState(event.getState());
            }

            public Object getQuery() {
                return clinicalTab.getQueryFields();
            }
        });
        clinicalTab.setCanQuery(true);

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
                            /*
                             * if the last page is reached in query state
                             * instead of display state, it means that no
                             * samples returned by the query can be shown to the
                             * user, because they contain patient data and the
                             * user can't view patients; that case is treated
                             * the same as when the query returns no samples
                             */
                            if (isState(QUERY)) {
                                notFound();
                            } else {
                                setQueryResult(null);
                                setError(Messages.get().gen_noMoreRecordInDir());
                            }
                        }

                        public void failure(Throwable e) {
                            setQueryResult(null);
                            Window.alert("Error: PT Login call query failed; " + e.getMessage());
                            logger.log(Level.SEVERE, e.getMessage(), e);
                            setError(Messages.get().gen_queryFailed());
                        }
                    };
                }

                /*
                 * only query for PT samples
                 */
                field = new QueryData(SampleMeta.getDomain(),
                                      QueryData.Type.STRING,
                                      Constants.domain().PT);
                query.setFields(field);
                query.setRowsPerPage(1);
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

        bus.addHandler(SelectionEvent.getType(), new SelectionEvent.Handler() {
            public void onSelection(SelectionEvent event) {
                String uid;
                SampleItemViewDO item;
                AnalysisViewDO ana;

                uid = event.getUid();
                item = null;
                ana = null;
                /*
                 * if a node was selected in the tree, the uid won't be null and
                 * the selected type will be either analysis or sample type;
                 * otherwise, the two will be null and that would mean that the
                 * tree got reloaded
                 */
                if (uid != null) {
                    switch (event.getSelectedType()) {
                        case ANALYSIS:
                            ana = (AnalysisViewDO)manager.getObject(uid);
                            break;
                        case SAMPLE_ITEM:
                            item = (SampleItemViewDO)manager.getObject(uid);
                            break;
                    }
                }

                /*
                 * set the notification on the tab based on the node selected
                 */
                setTabNotification(Tabs.ANALYSIS_NOTES, item, ana);
                setTabNotification(Tabs.SAMPLE_NOTES, item, ana);
                setTabNotification(Tabs.STORAGE, item, ana);
                setTabNotification(Tabs.QA_EVENTS, item, ana);
                setTabNotification(Tabs.AUX_DATA, item, ana);
                setTabNotification(Tabs.ATTACHMENT, item, ana);
            }
        });

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

        bus.addHandler(RunScriptletEvent.getType(), new RunScriptletEvent.Handler() {
            @Override
            public void onRunScriptlet(RunScriptletEvent event) {
                if (screen != event.getSource())
                    runScriptlets(event.getUid(), event.getChanged(), event.getOperation());
            }
        });

        bus.addHandler(DisplayAttachmentEvent.getType(), new DisplayAttachmentEvent.Handler() {
            @Override
            public void onDisplayAttachment(DisplayAttachmentEvent event) {
                displayAttachment(event.getId(), event.getIsSameWindow());
            }
        });

        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                if (isState(ADD, UPDATE)) {
                    event.cancel();
                    setError(Messages.get().mustCommitOrAbort());
                } else {
                    if (isAttachmentScreenOpen) {
                        /*
                         * don't close the main screen before trying to close
                         * attachment screen because that screen may be in
                         * update state and may not close
                         */
                        event.cancel();
                        closeLoginScreen = true;
                        trfAttachmentScreen.getWindow().close();
                        closeLoginScreen = false;
                    } else {
                        /*
                         * make sure that all detached tabs are closed when the
                         * main screen is closed
                         */
                        tabPanel.close();
                    }
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

        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("pt_provider")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }

        ptPTProvider.setModel(model);

        stmodel = new ArrayList<Item<String>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("sample_domain")) {
            if (Constants.domain().CLINICAL.equals(d.getCode()) ||
                Constants.domain().NEONATAL.equals(d.getCode())) {
                /*
                 * don't enable an additional domain if the user doesn't have
                 * permission to view patients; disable neonatal until it's
                 * fully implemented in OpenELIS
                 */
                strow = new Item<String>(d.getCode(), d.getEntry());
                strow.setEnabled( ("Y".equals(d.getIsActive())) &&
                                 patientPermission.canViewSample(d) &&
                                 Constants.domain().CLINICAL.equals(d.getCode()));
                stmodel.add(strow);
            }
        }

        ptAdditionalDomain.setModel(stmodel);
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
        pageDirection = PageDirection.PREV;
        nav.previous();
    }

    /**
     * Fetches the next sample sample in the list returned by the latest query
     */
    @UiHandler("next")
    protected void next(ClickEvent event) {
        pageDirection = PageDirection.NEXT;
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
                    isFullLogin = true;
                    previousManager = manager;
                    manager = result;
                    if (isAttachmentScreenOpen)
                        addReservedAttachment();
                    cache = new HashMap<String, Object>();
                    try {
                        addScriptlets();
                        runScriptlets(null, null, Action_Before.NEW_DOMAIN);
                        evaluateEdit();
                        setData();
                        setState(ADD);
                        fireDataChange();
                        accessionNumber.setFocus(true);
                        setDone(Messages.get().gen_enterInformationPressCommit());
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        clearStatus();
                    }
                }

                public void failure(Throwable error) {
                    Window.alert(error.getMessage());
                    logger.log(Level.SEVERE, error.getMessage(), error);
                    clearStatus();
                }
            };
        }

        SampleService1.get().getInstance(Constants.domain().PT, addCall);
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
                    if ( !Constants.domain().PT.equals(manager.getSample().getDomain())) {
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
                    if ( !Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample()
                                                                               .getStatusId())) {
                        try {
                            addScriptlets();
                        } catch (Exception e) {
                            Window.alert(e.getMessage());
                            logger.log(Level.SEVERE, e.getMessage(), e);
                        }
                    }
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
                if ( !Window.confirm(getWarnings(validation.getExceptions(), true)))
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
                    Integer id;

                    manager = result;
                    /*
                     * if either a new sample or a quick entered one was fully
                     * logged in, create an event log to record that
                     */
                    if (isFullLogin) {
                        try {
                            id = DictionaryCache.getIdBySystemName("log_type_sample_login");
                            EventLogService.get().add(id,
                                                      Messages.get().sample_login(),
                                                      Constants.table().SAMPLE,
                                                      manager.getSample().getId(),
                                                      Constants.dictionary().LOG_LEVEL_INFO,
                                                      null);
                            isFullLogin = false;
                        } catch (Exception e) {
                            Window.alert(e.getMessage());
                            logger.log(Level.SEVERE, e.getMessage(), e);
                            clearStatus();
                            return;
                        }
                    }
                    evaluateEdit();
                    setData();
                    setState(DISPLAY);
                    fireDataChange();
                    clearStatus();

                    /*
                     * the cache and scriptlets are cleared only if the
                     * add/update succeeds because otherwise, they can't be used
                     * by any tabs if the user wants to change any data
                     */
                    cache = null;
                    clearScriptlets();
                }

                public void validationErrors(ValidationErrorsList e) {
                    showErrors(e);
                    if ( !e.hasErrors() && (e.hasWarnings() || e.hasCautions()) && !ignoreWarning)
                        if (Window.confirm(getWarnings(e.getErrorList(), true)))
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
            isFullLogin = false;
            clearScriptlets();
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
                        isFullLogin = false;
                        clearScriptlets();
                    }

                    public void failure(Throwable e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        clearStatus();
                        cache = null;
                        isFullLogin = false;
                        clearScriptlets();
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

                    if ( !Constants.domain().PT.equals(result.getManager().getSample().getDomain())) {
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

                    isFullLogin = true;
                    previousManager = manager;
                    manager = result.getManager();
                    if (isAttachmentScreenOpen)
                        addReservedAttachment();
                    buildCache();
                    evaluateEdit();
                    setData();
                    setState(ADD);
                    fireDataChange();
                    accessionNumber.setFocus(true);
                    try {
                        addScriptlets();

                        /*
                         * show any errors/warnings found during duplication
                         */
                        errors = result.getErrors();
                        if (errors != null && errors.size() > 0) {
                            if (errors.hasWarnings())
                                Window.alert(getWarnings(errors.getErrorList(), false));
                            if (errors.hasErrors())
                                showErrors(errors);
                            else
                                setDone(Messages.get().gen_enterInformationPressCommit());
                        } else {
                            setDone(Messages.get().gen_enterInformationPressCommit());
                        }
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        clearStatus();
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

    /**
     * If the checkbox of the menu item "Add With TRF" is checked, opens the
     * data entry attachment screen and executes the query to fetch unattached
     * attachments for this domain; if the checkbox is unchecked, closes the
     * attachment screen if it's open.
     */
    protected void addWithTRF() {
        org.openelis.ui.widget.Window window;
        ScheduledCommand cmd;
        AttachmentEntry entry;

        if ( !addWithTRF.isChecked()) {
            /*
             * the user unchecked the checkbox for showing Attachment screen, so
             * try to close that screen if it's open, but recheck the checkbox
             * to make sure that if the screen can't be closed due to some
             * record locked on it, the checkbox doesn't stay unchecked
             */
            addWithTRF.setCheck(true);
            if (trfAttachmentScreen != null)
                trfAttachmentScreen.getWindow().close();
            return;
        }

        try {
            /*
             * get the system variable that specifies the pattern for finding
             * this domain's TRFs
             */
            if (attachmentPatternVariable == null)
                attachmentPatternVariable = SystemVariableService1Impl.INSTANCE.fetchByExactName("attachment_pattern_pt");

            /*
             * the user checked the checkbox for showing attachment screen, so
             * open that screen if it's closed
             */
            if (trfAttachmentScreen == null) {
                trfAttachmentScreen = new TRFAttachmentScreenUI() {
                    @Override
                    public String getPattern() {
                        return attachmentPatternVariable.getValue();
                    }
                };
            }

            entry = new AttachmentEntry();
            window = entry.addTRFScreen(trfAttachmentScreen, "ptTRFAttachment");
            isAttachmentScreenOpen = true;

            /*
             * a ScheduledCommand is used here because otherwise any "Busy"
             * message shown by the trf attachment screen get overwritten by the
             * default "Done" message shown on creating the window
             */
            cmd = new ScheduledCommand() {
                @Override
                public void execute() {
                    trfAttachmentScreen.fetchUnattached();
                }
            };
            Scheduler.get().scheduleDeferred(cmd);

            window.addCloseHandler(new CloseHandler<WindowInt>() {
                @Override
                public void onClose(CloseEvent<WindowInt> event) {
                    isAttachmentScreenOpen = false;
                    if (closeLoginScreen)
                        /*
                         * the login screen needs to be closed because it is
                         * waiting for Attachment screen to be closed
                         */
                        screen.window.close();
                    else
                        addWithTRF.setCheck(false);
                }
            });
        } catch (Throwable e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Shows the order linked to the sample on the Send-out order screen
     */
    @UiHandler("orderLookupButton")
    protected void orderLookup(ClickEvent event) {
        final SendoutOrderScreenUI orderScreen;
        ScheduledCommand cmd;
        OrderEntry entry;

        try {
            entry = new OrderEntry();
            orderScreen = entry.addSendoutOrderScreen();
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
     * Overridden because the some parts of the screen e.g. clinical tab can be
     * enabled or disabled several times in Add or Update states, based on
     * factors such as the additional domain changed
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
        else if (c == AuxFieldGroupManager1.class)
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
            else if (c == AuxFieldGroupManager1.class)
                obj = AuxiliaryService1Impl.INSTANCE.fetchById((Integer)key);

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
                        /*
                         * this sample can be shown to the user only if it
                         * doesn't contain patient data or if the user has
                         * permission to view patients; if it can't be shown,
                         * fetch the previous or next sample in the system
                         * (depending upon the direction of paging), so that it
                         * can be shown if possible
                         */
                        if ( !patientPermission.canViewSample(result.getSamplePT())) {
                            if (pageDirection == null)
                                pageDirection = PageDirection.NEXT;
                            clearStatus();
                            switch (pageDirection) {
                                case PREV:
                                    nav.previous();
                                    break;
                                case NEXT:
                                    nav.next();
                                    break;
                            }
                            return;
                        }

                        manager = result;
                        setData();
                        setState(DISPLAY);
                        fireDataChange();
                        clearStatus();
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
        clinicalTab.setData(manager);
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
        int i;
        Integer prevId;
        ArrayList<Integer> ids;
        AnalysisViewDO ana;
        AuxDataViewDO aux;
        ArrayList<TestManager> tms;
        ArrayList<AuxFieldGroupManager1> afgms;

        cache = new HashMap<String, Object>();

        try {
            /*
             * the list of tests to be fetched
             */
            ids = new ArrayList<Integer>();
            for (i = 0; i < manager.analysis.count(); i++ ) {
                ana = manager.analysis.get(i);
                ids.add(ana.getTestId());
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
                afgms = AuxiliaryService1Impl.INSTANCE.fetchByIds(ids);
                for (AuxFieldGroupManager1 afgm : afgms)
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
    private String getWarnings(ArrayList<Exception> warnings, boolean isConfirm) {
        StringBuilder b;

        b = new StringBuilder();
        b.append(Messages.get().gen_warningDialogLine1()).append("\n");
        if (warnings != null) {
            for (Exception ex : warnings) {
                if (ex instanceof Warning || ex instanceof Caution)
                    b.append(" * ").append(ex.getMessage()).append("\n");
            }
        }

        if (isConfirm)
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
     * Adds the scriptlets for all the records in the manager, to the scriptlet
     * runner
     */
    private void addScriptlets() throws Exception {
        if (scriptletRunner == null)
            scriptletRunner = new ScriptletRunner<SampleSO>();

        /*
         * add all the scriptlets for all tests, test analytes and aux fields
         * linked to the manager
         */
        addTestScriptlets();
        addAuxScriptlets();
    }

    /**
     * Adds a new scriptlet to the list of scriptlets that are executed. It
     * ensures that for each reference id, there is only one scriptlet in the
     * list, since we go through all records and add scriptlets every time we
     * receive the manager from the back-end.
     */
    private void addScriptlet(Integer scriptletId, Integer referenceId) throws Exception {
        HashSet<Integer> ids;

        if (scriptlets == null)
            scriptlets = new HashMap<Integer, HashSet<Integer>>();

        /*
         * the same scriptlet can be added for multiple records e.g. when a test
         * is added multiple times; get the ids of all the records for which
         * this scriptlet has been added
         */
        ids = scriptlets.get(scriptletId);
        if (ids == null) {
            ids = new HashSet<Integer>();
            scriptlets.put(scriptletId, ids);
        }

        if ( !ids.contains(referenceId)) {
            scriptletRunner.add((ScriptletInt<SampleSO>)ScriptletFactory.get(scriptletId,
                                                                             referenceId));
            ids.add(referenceId);
        }
    }

    /**
     * Runs all the scriptlets in the runner for the passed action performed on
     * the field "changed" of the record with the passed uid. Also refreshes the
     * screen based on the actions performed by the scriptlets.
     */
    private void runScriptlets(String uid, String changed, Action_Before action) {
        String auid, selUid;
        Object obj;
        SampleSO data;
        ResultViewDO res;
        NoteDO note;
        AnalysisQaEventViewDO aqa;
        EnumSet<Action_After> actionAfter;
        ValidationErrorsList errors;

        /*
         * scriptletRunner will be null here if this method is called by a
         * widget losing focus but the reason for the lost focus was the user
         * clicking Abort; this is because in abort() both the scriptlet runner
         * and hash are set to null and that happens before the widget can lose
         * focus
         */
        if (scriptletRunner == null)
            return;

        /*
         * create the sciptlet object
         */
        data = new SampleSO();
        if (action != null)
            data.addActionBefore(action);
        data.setChange(changed);
        data.setUid(uid);
        data.setManager(manager);
        data.setCache(cache);

        /*
         * run the scritplet and show the errors and the changed data
         */
        data = scriptletRunner.run(data);
        clearStatus();
        if (data.getExceptions() != null && data.getExceptions().size() > 0) {
            errors = new ValidationErrorsList();
            for (Exception e : data.getExceptions())
                errors.add(e);
            showErrors(errors);
        }

        manager = data.getManager();
        evaluateEdit();
        setData();

        if (data.getChangedUids() == null)
            return;
        /*
         * go through the changed uids and fire appropriate events to refresh
         * particular parts of the screen
         */
        selUid = sampleItemAnalysisTreeTab.getSelectedUid();
        actionAfter = data.getActionAfter();
        for (String cuid : data.getChangedUids()) {
            obj = manager.getObject(cuid);
            if (obj instanceof ResultDO) {
                /*
                 * if any results were changed and if any of them belong to the
                 * analysis selected in the tree then refresh the result tab,
                 * otherwise don't
                 */
                res = (ResultViewDO)obj;
                auid = Constants.uid().getAnalysis(res.getAnalysisId());
                if (auid.equals(selUid))
                    bus.fireEvent(new ResultChangeEvent(auid));
            } else if (obj instanceof SampleItemDO) {
                /*
                 * if any sample items were changed or added then refresh the
                 * tree and sample item tabs
                 */
                if (actionAfter != null) {
                    if (actionAfter.contains(Action_After.SAMPLE_ITEM_ADDED))
                        bus.fireEventFromSource(new SampleItemAddedEvent(cuid), screen);
                    else if (actionAfter.contains(Action_After.SAMPLE_ITEM_CHANGED))
                        bus.fireEvent(new SampleItemChangeEvent(cuid, Action.SAMPLE_TYPE_CHANGED));
                }
            } else if (obj instanceof AnalysisDO) {
                /*
                 * if analysis qa events were removed and any of them belong to
                 * the analysis selected in the tree, refresh the qa event tab
                 */
                if (actionAfter.contains(Action_After.QA_REMOVED) && cuid.equals(selUid))
                    bus.fireEventFromSource(new QAEventRemovedEvent(cuid), screen);
            } else if (obj instanceof AnalysisQaEventDO) {
                /*
                 * if analysis qa events were changed was added and if any of
                 * them belong to the analysis selected in the tree then refresh
                 * the qa event tab, otherwise don't
                 */
                aqa = (AnalysisQaEventViewDO)obj;
                auid = Constants.uid().getAnalysis(aqa.getAnalysisId());
                if (auid.equals(selUid))
                    bus.fireEventFromSource(new QAEventAddedEvent(auid), screen);
            } else if (obj instanceof NoteDO) {
                /*
                 * find out whether the note was a sample or analysis note
                 */
                note = (NoteDO)obj;
                if (Constants.table().ANALYSIS.equals(note.getReferenceTableId())) {
                    auid = Constants.uid().getAnalysis(note.getReferenceId());
                    /*
                     * if the note belongs to the analysis selected in the tree
                     * then refresh the note tab, otherwise don't
                     */
                    if (auid.equals(selUid))
                        bus.fireEvent(new NoteChangeEvent(auid));
                } else {
                    /*
                     * refresh the note tab because it's sample note
                     */
                    bus.fireEvent(new NoteChangeEvent(null));
                }
            }
        }
    }

    /**
     * Clears the scriptlet runner and scriptlet hash
     */
    private void clearScriptlets() {
        scriptletRunner = null;
        scriptlets = null;
    }

    /**
     * Gets the next attachment reserved for the current user on Attachment
     * screen, if any, and adds it to the sample
     */
    private void addReservedAttachment() {
        AttachmentManager am;

        am = trfAttachmentScreen.getSelected();
        if (am != null)
            bus.fireEvent(new AddAttachmentEvent(am.getAttachment()));
    }

    /**
     * Adds scriptlets for analyses and results, to the scriptlet runner
     */
    private void addTestScriptlets() throws Exception {
        int i, j, k;
        Integer sid;
        AnalysisViewDO ana;
        TestAnalyteViewDO ta;
        ResultViewDO res;
        TestManager tm;
        HashMap<Integer, Integer> tasids;

        tasids = new HashMap<Integer, Integer>();
        /*
         * find out the tests and test analytes in the manager for which
         * scriptlets need to be added
         */
        for (i = 0; i < manager.analysis.count(); i++ ) {
            ana = manager.analysis.get(i);
            tm = get(ana.getTestId(), TestManager.class);
            /*
             * scriptlets for analyses
             */
            if (tm.getTest().getScriptletId() != null)
                addScriptlet(tm.getTest().getScriptletId(), ana.getId());

            /*
             * find out which test analytes have scriptlets
             */
            for (j = 0; j < tm.getTestAnalytes().rowCount(); j++ ) {
                for (k = 0; k < tm.getTestAnalytes().columnCount(j); k++ ) {
                    ta = tm.getTestAnalytes().getAnalyteAt(j, k);
                    if (ta.getScriptletId() != null && tasids.get(ta.getId()) == null)
                        tasids.put(ta.getId(), ta.getScriptletId());
                }
            }

            /*
             * scriptlets for results
             */
            for (j = 0; j < manager.result.count(ana); j++ ) {
                for (k = 0; k < manager.result.count(ana, j); k++ ) {
                    res = manager.result.get(ana, j, k);
                    sid = tasids.get(res.getTestAnalyteId());
                    if (sid != null)
                        addScriptlet(sid, res.getId());
                }
            }
        }
    }

    /**
     * Adds scriptlets for aux data, to the scriptlet runner
     */
    private void addAuxScriptlets() throws Exception {
        int i;
        AuxFieldViewDO auxf;
        AuxDataViewDO aux;
        AuxFieldGroupManager1 auxfgm;
        HashSet<Integer> auxfgids;
        HashMap<Integer, Integer> auxfids;

        auxfids = new HashMap<Integer, Integer>();
        auxfgids = new HashSet<Integer>();
        /*
         * find the ids of the aux groups and also find which aux field is
         * linked to which aux data; duplicate aux groups are not allowed, so an
         * aux field won't be repeated
         */
        for (i = 0; i < manager.auxData.count(); i++ ) {
            aux = manager.auxData.get(i);
            auxfgids.add(aux.getAuxFieldGroupId());
            auxfids.put(aux.getAuxFieldId(), aux.getId());
        }

        /*
         * add the scriptlets linked to the aux fields for the aux data
         * belonging to the groups found above
         */
        for (Integer id : auxfgids) {
            auxfgm = get(id, AuxFieldGroupManager1.class);
            for (i = 0; i < auxfgm.field.count(); i++ ) {
                auxf = auxfgm.field.get(i);
                if (auxf.getScriptletId() != null)
                    addScriptlet(auxf.getScriptletId(), auxfids.get(auxf.getId()));
            }
        }
    }

    /**
     * Creates and sets notification, e.g. number of records, on the header of
     * the specified tab
     */
    private void setTabNotification(Tabs tabs, SampleItemViewDO item, AnalysisViewDO ana) {
        int count1, count2;
        String label;

        label = null;

        if (manager != null) {
            switch (tabs) {
                case ANALYSIS_NOTES:
                    count1 = 0;
                    count2 = 0;
                    if (ana != null) {
                        count1 = manager.analysisExternalNote.get(ana) == null ? 0 : 1;
                        count2 = manager.analysisInternalNote.count(ana);
                    }
                    if (count1 > 0 || count2 > 0)
                        label = DataBaseUtil.concatWithSeparator(count1, " - ", count2);
                    break;
                case SAMPLE_NOTES:
                    count1 = manager.sampleExternalNote.get() == null ? 0 : 1;
                    count2 = manager.sampleInternalNote.count();
                    if (count1 > 0 || count2 > 0)
                        label = DataBaseUtil.concatWithSeparator(count1, " - ", count2);
                    break;
                case STORAGE:
                    count1 = 0;
                    if (item != null)
                        count1 = manager.storage.count(item);
                    else if (ana != null)
                        count1 = manager.storage.count(ana);
                    if (count1 > 0)
                        label = String.valueOf(count1);
                    break;
                case QA_EVENTS:
                    count1 = manager.qaEvent.count();
                    count2 = ana != null ? manager.qaEvent.count(ana) : 0;
                    if (count1 > 0 || count2 > 0)
                        label = DataBaseUtil.concatWithSeparator(count1, " - ", count2);
                    break;
                case AUX_DATA:
                    count1 = manager.auxData.count();
                    if (count1 > 0)
                        label = String.valueOf(count1);
                    break;
                case ATTACHMENT:
                    count1 = manager.attachment.count();
                    if (count1 > 0)
                        label = String.valueOf(count1);
                    break;
            }
        }

        tabPanel.setTabNotification(tabs.ordinal(), label);
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
                        try {
                            /*
                             * add scriptlets for any newly added tests and aux
                             * data
                             */
                            addTestScriptlets();
                            addAuxScriptlets();
                            runScriptlets(null, null, Action_Before.NEW_DOMAIN);
                            setData();
                            setState(UPDATE);
                            fireDataChange();
                            clearStatus();
                            checkTRF();
                        } catch (Exception e) {
                            Window.alert(e.getMessage());
                            logger.log(Level.SEVERE, e.getMessage(), e);
                            clearStatus();
                        }
                    }

                    public void notFound() {
                        clearStatus();
                        checkTRF();
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
                        clearStatus();
                    }

                    public void finish() {
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
                        clearStatus();
                        if (isFullLogin)
                            checkTRF();
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
                        clearStatus();
                    }

                    public void finish() {
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
        if (ordId == null) {
            manager.getSample().setOrderId(ordId);
            return;
        }

        if (getAccessionNumber() == null) {
            Window.alert(Messages.get().sample_enterAccNumBeforeOrderLoad());
            manager.getSample().setOrderId(null);
            orderId.setValue(null);
            return;
        }

        setBusy(Messages.get().gen_fetching());

        if (setOrderIdCall == null) {
            setOrderIdCall = new AsyncCallback<SampleTestReturnVO>() {
                public void onSuccess(SampleTestReturnVO result) {
                    ValidationErrorsList errors;

                    manager = result.getManager();
                    setData();
                    fireDataChange();
                    clearStatus();
                    try {
                        /*
                         * add scriptlets for any newly added tests and aux data
                         */
                        addTestScriptlets();
                        addAuxScriptlets();
                        /*
                         * show any validation errors encountered while
                         * importing the order
                         */
                        errors = result.getErrors();
                        if (errors != null && errors.size() > 0) {
                            if (errors.hasWarnings())
                                Window.alert(getWarnings(errors.getErrorList(), false));
                            if (errors.hasErrors())
                                showErrors(errors);
                        }

                        if (result.getTests() == null || result.getTests().size() == 0) {
                            isBusy = false;
                        } else {
                            /*
                             * show the pop up for selecting the prep/reflex
                             * tests for the tests added during the import;
                             * setting focusedWidget makes sure that after the
                             * tests have been added, the focus gets set to the
                             * field next to this field in the tabbing order
                             */
                            focusedWidget = orderId;
                            showTests(result);
                        }
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                    }
                }

                public void onFailure(Throwable error) {
                    manager.getSample().setOrderId(null);
                    orderId.setValue(null);
                    Window.alert(error.getMessage());
                    logger.log(Level.SEVERE, error.getMessage(), error);
                    clearStatus();
                }
            };
        }

        SampleService1.get().importOrder(manager, ordId, setOrderIdCall);
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
     * Returns pt provider id or null if the manager is null or if this is not a
     * pt sample
     */
    private Integer getPTProviderId() {
        if (manager == null || manager.getSamplePT() == null)
            return null;
        return manager.getSamplePT().getPTProviderId();
    }

    /**
     * Sets the pt provider id
     */
    private void setPTProviderId(Integer ptProviderId) {
        manager.getSamplePT().setPTProviderId(ptProviderId);
    }

    /**
     * Returns series or null if the manager is null or if this is not a pt
     * sample
     */
    private String getSeries() {
        if (manager == null || manager.getSamplePT() == null)
            return null;
        return manager.getSamplePT().getSeries();
    }

    /**
     * Sets the series
     */
    private void setSeries(String series) {
        manager.getSamplePT().setSeries(series);
    }

    /**
     * Returns due date or null if the manager is null or if this is not a pt
     * sample
     */
    private Datetime getDueDate() {
        if (manager == null || manager.getSamplePT() == null)
            return null;
        return manager.getSamplePT().getDueDate();
    }

    private void setDueDate(Datetime dueDate) {
        manager.getSamplePT().setDueDate(dueDate);
    }

    /**
     * Returns additional domain or null if the manager is null or if this is
     * not a pt sample
     */
    private String getAdditionalDomain() {
        if (manager == null || manager.getSamplePT() == null)
            return null;
        return manager.getSamplePT().getAdditionalDomain();
    }

    /**
     * Calls the service method to set the additional domain and add or remove
     * the domain data for it
     */
    private void setAdditionalDomain(String additionalDomain) {
        try {
            manager = SampleService1.get().setAdditionalDomain(manager, additionalDomain);
            setData();
            setState(state);
            fireDataChange();
            if (manager.getSampleClinical() != null) {
                tabPanel.selectTab(Tabs.CLINICAL.ordinal());
                runScriptlets(null, SampleMeta.getClinicalPatientId(), Action_Before.PATIENT);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Returns the received by id or null if the manager is null
     */
    private Integer getReceivedById() throws Exception {
        if (manager == null)
            return null;
        return manager.getSample().getReceivedById();
    }

    /**
     * Returns the login name of the user linked by received by id or null if
     * the manager is null or received by id is null
     */
    private String getReceivedByName() throws Exception {
        if (manager == null || manager.getSample().getReceivedById() == null)
            return null;
        return UserCache.getSystemUser(manager.getSample().getReceivedById()).getLoginName();
    }

    /**
     * Sets the received by id
     */
    private void setReceivedById(Integer receivedById) {
        manager.getSample().setReceivedById(receivedById);
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
            if (data == null) {
                data = manager.project.add();
                data.setIsPermanent("N");
            }

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
            /*
             * add scriptlets for the newly added aux data
             */
            addAuxScriptlets();

            errors = ret.getErrors();
            if (errors != null && errors.size() > 0) {
                if (errors.hasWarnings())
                    Window.alert(getWarnings(errors.getErrorList(), false));
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
     * Checks whether the accession number on the TRF attached to the sample
     * matched the accession number entered by the user; shows a warning if it
     * doesn't
     */
    protected void checkTRF() {
        String accession, pattern, arr[];
        AttachmentItemViewDO data;

        if (getAccessionNumber() == null || manager.attachment.count() == 0)
            return;

        if (genTRFPatternVariable == null) {
            try {
                /*
                 * get the system variable that specifies the generic pattern
                 * for TRFs and can be used in java code for pattern matching
                 */
                if (genTRFPatternVariable == null)
                    genTRFPatternVariable = SystemVariableService1Impl.INSTANCE.fetchByExactName("attachment_pattern_gen_java");
            } catch (Throwable e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
                return;
            }
        }

        pattern = genTRFPatternVariable.getValue();
        accession = DataBaseUtil.toString(getAccessionNumber());

        /*
         * find out if a TRF is attached to the sample; notify the user if the
         * TRF's accession number doesn't match the sample's accession number
         */
        for (int i = 0; i < manager.attachment.count(); i++ ) {
            data = manager.attachment.get(i);
            if (data.getAttachmentDescription().matches(pattern)) {
                arr = data.getAttachmentDescription().split("-");
                if (arr.length > 1 && !accession.equals(arr[1])) {
                    setError(Messages.get()
                                     .sample_trfNotMatchEntered(data.getAttachmentDescription()));
                    break;
                }
            }
        }
    }

    /**
     * Opens the file linked to the attachment on the selected row in the table
     * showing the sample's attachment items. If isSameWindow is true then the
     * file is opened in the same browser window/tab as before, otherwise it's
     * opened in a different one.
     */
    private void displayAttachment(Integer id, boolean isSameWindow) {
        String name;

        /*
         * if isSameWindow is true then the name passed to displayAttachment is
         * this screen's window's title because ReportScreen sets that as the
         * title of the window passed to it, so if the name is not the same,
         * then the screen's window's title will get changed
         */
        name = isSameWindow ? Messages.get().samplePT_login() : null;
        try {
            AttachmentUtil.displayAttachment(id, name, window);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Adds the tests/panels in the list to the sample; shows any errors found
     * while adding the tests or the popup for selecting additional prep/reflex
     * tests
     */
    private void addAnalyses(ArrayList<SampleTestRequestVO> tests) {
        int numAuxBef, numAuxAft;
        SampleTestReturnVO ret;
        ValidationErrorsList errors;

        setBusy();
        try {
            numAuxBef = manager.auxData.count();
            ret = SampleService1.get().addAnalyses(manager, tests);
            manager = ret.getManager();
            numAuxAft = manager.auxData.count();
            setData();
            setState(state);
            /*
             * notify the tabs that some new tests have been added
             */
            bus.fireEventFromSource(new AddTestEvent(tests), this);
            if (numAuxAft > numAuxBef) {
                /*
                 * the number of aux data after adding the tests is more than
                 * the ones before, so it means that a panel was added which
                 * linked to some aux groups, so notify the tabs
                 */
                bus.fireEventFromSource(new AddAuxGroupEvent(null), this);
            }
            clearStatus();

            /*
             * add scriptlets for any newly added tests and aux data
             */
            addTestScriptlets();
            addAuxScriptlets();

            /*
             * show any validation errors encountered while adding the tests
             */
            errors = ret.getErrors();
            if (errors != null && errors.size() > 0) {
                if (errors.hasWarnings())
                    Window.alert(getWarnings(errors.getErrorList(), false));
                if (errors.hasErrors())
                    showErrors(errors);
            }

            if (ret.getTests() == null || ret.getTests().size() == 0) {
                isBusy = false;
                runScriptlets(null, null, Action_Before.ANALYSIS);
                /*
                 * if any widget like order # had focus before adding tests,
                 * this will set the focus to the field next in the tabbing
                 * order
                 */
                if (focusedWidget != null) {
                    screen.focusNextWidget(focusedWidget, true);
                    focusedWidget = null;
                }
            } else {
                /*
                 * show the pop up for selecting the prep/reflex tests for the
                 * tests added
                 */
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
             * add scriptlets for the changed test
             */
            addTestScriptlets();

            /*
             * show any validation errors encountered while changing the method
             */
            errors = ret.getErrors();
            if (errors != null && errors.size() > 0) {
                if (errors.hasWarnings())
                    Window.alert(getWarnings(errors.getErrorList(), false));
                if (errors.hasErrors())
                    showErrors(errors);
            }

            if (ret.getTests() == null || ret.getTests().size() == 0)
                isBusy = false;
            else
                /*
                 * show the pop up for selecting the prep/reflex tests for the
                 * tests added
                 */
                showTests(ret);
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
                    if (tests != null && tests.size() > 0) {
                        addAnalyses(tests);
                    } else {
                        isBusy = false;
                        if (focusedWidget != null) {
                            screen.focusNextWidget(focusedWidget, true);
                            focusedWidget = null;
                        }
                    }
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