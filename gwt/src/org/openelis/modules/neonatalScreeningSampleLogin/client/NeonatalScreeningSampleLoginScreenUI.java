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
package org.openelis.modules.neonatalScreeningSampleLogin.client;

import static org.openelis.modules.main.client.Logger.logger;
import static org.openelis.ui.screen.Screen.ShortKeys.CTRL;
import static org.openelis.ui.screen.Screen.Validation.Status.FLAGGED;
import static org.openelis.ui.screen.State.ADD;
import static org.openelis.ui.screen.State.DEFAULT;
import static org.openelis.ui.screen.State.DISPLAY;
import static org.openelis.ui.screen.State.QUERY;
import static org.openelis.ui.screen.State.UPDATE;

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
import org.openelis.domain.AddressDO;
import org.openelis.domain.AnalysisQaEventDO;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AttachmentDO;
import org.openelis.domain.AttachmentItemViewDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.EOrderDO;
import org.openelis.domain.IdAccessionVO;
import org.openelis.domain.NoteDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.PatientDO;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.ProviderDO;
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
import org.openelis.manager.AuxFieldGroupManager;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.attachment.client.AddAttachmentEvent;
import org.openelis.modules.attachment.client.AttachmentUtil;
import org.openelis.modules.attachment.client.DisplayAttachmentEvent;
import org.openelis.modules.attachment.client.TRFAttachmentScreenUI;
import org.openelis.modules.auxData.client.AddAuxGroupEvent;
import org.openelis.modules.auxData.client.AuxDataTabUI;
import org.openelis.modules.auxData.client.RemoveAuxGroupEvent;
import org.openelis.modules.auxiliary.client.AuxiliaryService;
import org.openelis.modules.eorder.client.EOrderLookupUI;
import org.openelis.modules.main.client.OpenELIS;
import org.openelis.modules.organization1.client.OrganizationService1Impl;
import org.openelis.modules.patient.client.PatientLookupUI;
import org.openelis.modules.patient.client.PatientService;
import org.openelis.modules.project.client.ProjectService;
import org.openelis.modules.provider1.client.ProviderService1;
import org.openelis.modules.sample1.client.AddRowAnalytesEvent;
import org.openelis.modules.sample1.client.AddTestEvent;
import org.openelis.modules.sample1.client.AnalysisChangeEvent;
import org.openelis.modules.sample1.client.AnalysisNotesTabUI;
import org.openelis.modules.sample1.client.AnalysisTabUI;
import org.openelis.modules.sample1.client.AttachmentTabUI;
import org.openelis.modules.sample1.client.NextOfKinChangeEvent;
import org.openelis.modules.sample1.client.NoteChangeEvent;
import org.openelis.modules.sample1.client.PatientChangeEvent;
import org.openelis.modules.sample1.client.QAEventAddedEvent;
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
import org.openelis.modules.systemvariable.client.SystemVariableService;
import org.openelis.modules.test.client.TestService;
import org.openelis.scriptlet.SampleSO;
import org.openelis.scriptlet.SampleSO.Action_After;
import org.openelis.scriptlet.SampleSO.Action_Before;
import org.openelis.ui.common.Caution;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.PermissionException;
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
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.CheckMenuItem;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.HasExceptions;
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
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Widget;

public class NeonatalScreeningSampleLoginScreenUI extends Screen implements CacheProvider {

    @UiTemplate("NeonatalScreeningSampleLogin.ui.xml")
    interface NeonatalScreeningSampleLoginUiBinder
                                                  extends
                                                  UiBinder<Widget, NeonatalScreeningSampleLoginScreenUI> {
    };

    private static NeonatalScreeningSampleLoginUiBinder uiBinder   = GWT.create(NeonatalScreeningSampleLoginUiBinder.class);

    protected SampleManager1                            manager, previousManager;

    protected ScreenNavigator<IdAccessionVO>            nav;

    @UiField
    protected Calendar                                  collectionDate, collectionTime,
                    receivedDate, patientBirthDate, patientBirthTime, nextOfKinBirthDate,
                    transfusionDate;

    @UiField
    protected TextBox<Integer>                          accessionNumber, patientId, nextOfKinId,
                    birthOrder, gestationalAge, weight, transfusionAge, collectionAge;

    @UiField
    protected TextBox<String>                           clientReference, orderId, patientLastName,
                    patientFirstName, patientAddrMultipleUnit, patientAddrStreetAddress,
                    patientAddrCity, patientAddrZipCode, nextOfKinNationalId, nextOfKinLastName,
                    nextOfKinMiddleName, nextOfKinFirstName, nextOfKinAddrHomePhone,
                    nextOfKinAddrMultipleUnit, nextOfKinAddrStreetAddress, nextOfKinAddrCity,
                    nextOfKinAddrZipCode, providerFirstName, formNumber;

    @UiField
    protected Dropdown<Integer>                         status, feedingId, patientGender,
                    patientRace, patientEthnicity, nextOfKinRelation, nextOfKinGender,
                    nextOfKinRace, nextOfKinEthnicity;

    @UiField
    protected Dropdown<String>                          patientAddrState, nextOfKinAddrState;

    @UiField
    protected CheckBox                                  isNicu, isTransfused, isRepeat,
                    isCollectionValid;

    @UiField
    protected AutoComplete                              projectName, reportToName,
                    birthHospitalName, providerLastName;

    @UiField
    protected Button                                    query, previous, next, add, update, commit,
                    abort, optionsButton, orderLookupButton, patientEmptySearchButton,
                    patientFieldSearchButton, unlinkPatientButton, editPatientButton,
                    nextOfKinEmptySearchButton, nextOfKinFieldSearchButton, unlinkNextOfKinButton,
                    editNextOfKinButton, projectButton, reportToButton, birthHospitalButton;

    @UiField
    protected Menu                                      optionsMenu, historyMenu;

    @UiField
    protected MenuItem                                  duplicate, historySample,
                    historySampleNeonatal, historyPatient, historyNextOfKin, historySampleProject,
                    historySampleOrganization, historySampleItem, historyAnalysis,
                    historyCurrentResult, historyStorage, historySampleQA, historyAnalysisQA,
                    historyAuxData;

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

    protected boolean                                   canEditSample, canEditPatient,
                    isPatientLocked, canEditNextOfKin, isNextOfKinLocked, isBusy,
                    setSelectedAsNextOfKin, closeLoginScreen, isAttachmentScreenOpen, revalidate,
                    hasDomainScriptlet;

    protected ModulePermission                          userPermission;

    protected NeonatalScreeningSampleLoginScreenUI      screen;

    protected TestSelectionLookupUI                     testSelectionLookup;

    protected SampleProjectLookupUI                     sampleprojectLookUp;

    protected SampleOrganizationLookupUI                sampleOrganizationLookup;

    protected PatientLookupUI                           patientLookup;

    protected TRFAttachmentScreenUI                     trfAttachmentScreen;

    protected EOrderLookupUI                            eorderLookup;

    protected Focusable                                 focusedWidget;

    protected HashMap<String, Object>                   cache;

    protected AsyncCallbackUI<ArrayList<IdAccessionVO>> queryCall;

    protected AsyncCallbackUI<SampleManager1>           addCall, fetchForUpdateCall,
                    commitUpdateCall, fetchByIdCall, unlockCall, mergeQuickEntryCall;

    protected AsyncCallbackUI<Void>                     validateAccessionNumberCall;

    protected AsyncCallbackUI<SampleTestReturnVO>       duplicateCall;

    protected ScriptletRunner<SampleSO>                 scriptletRunner;

    protected HashMap<Integer, HashSet<Integer>>        scriptlets;

    protected SystemVariableDO                          domainScriptletVariable,
                    attachmentPatternVariable;

    protected Integer                                   domainScriptletId;

    protected static final SampleManager1.Load          elements[] = {
                    SampleManager1.Load.ANALYSISUSER, SampleManager1.Load.AUXDATA,
                    SampleManager1.Load.NOTE, SampleManager1.Load.ORGANIZATION,
                    SampleManager1.Load.PROJECT, SampleManager1.Load.QA,
                    SampleManager1.Load.RESULT, SampleManager1.Load.STORAGE,
                    SampleManager1.Load.WORKSHEET, SampleManager1.Load.ATTACHMENT,
                    SampleManager1.Load.EORDER, SampleManager1.Load.PROVIDER};

    protected enum Tabs {
        SAMPLE_ITEM, ANALYSIS, TEST_RESULT, ANALYSIS_NOTES, SAMPLE_NOTES, STORAGE, QA_EVENTS,
        AUX_DATA, ATTACHMENT
    };

    protected static final String REPORT_TO_KEY = "reportTo", BIRTH_HOSPITAL_KEY = "birthHospital";

    /**
     * Check the permissions for this screen, intialize the tabs and widgets
     */
    public NeonatalScreeningSampleLoginScreenUI(WindowInt window) throws Exception {
        setWindow(window);

        userPermission = UserCache.getPermission().getModule("sampleneonatal");
        if (userPermission == null)
            throw new PermissionException(Messages.get()
                                                  .screenPermException("Neonatal Screening Sample Login Screen"));

        try {
            CategoryCache.getBySystemNames("sample_status",
                                           "gender",
                                           "race",
                                           "ethnicity",
                                           "state",
                                           "country",
                                           "patient_relation",
                                           "feeding",
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
                                           "scriptlet_domain",
                                           "scriptlet_test",
                                           "scriptlet_test_analyte",
                                           "newborn_results",
                                           "newborn_interpretation");
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

        initWidget(uiBinder.createAndBindUi(this));

        manager = null;
        hasDomainScriptlet = true;

        initialize();
        evaluateEdit();
        setData();
        setState(DEFAULT);
        fireDataChange();

        logger.fine("Neonatal Screening Sample Login Screen Opened");
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        String r;
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
                duplicate.setEnabled(isState(State.DISPLAY));
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
                                      userPermission.hasAddPermission());
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
                historySampleNeonatal.setEnabled(isState(DISPLAY));
            }
        });

        historySampleNeonatal.addCommand(new Command() {
            @Override
            public void execute() {
                SampleHistoryUtility1.neonatal(manager);
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                historyPatient.setEnabled(isState(DISPLAY));
            }
        });

        historyPatient.addCommand(new Command() {
            @Override
            public void execute() {
                SampleHistoryUtility1.neonatalPatient(manager);
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                historyNextOfKin.setEnabled(isState(DISPLAY));
            }
        });

        historyNextOfKin.addCommand(new Command() {
            @Override
            public void execute() {
                SampleHistoryUtility1.neonatalNextOfKin(manager);
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
                                                            (canEditSample && isState(ADD, UPDATE)));
                                 accessionNumber.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? orderId : projectName;
                             }
                         });

        addScreenHandler(orderId,
                         SampleMeta.getEorderPaperOrderValidator(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 orderId.setValue(getOrderId());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setOrderId(event.getValue());
                                 focusedWidget = orderId;
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 orderId.setEnabled(isState(QUERY) ||
                                                    (canEditSample && isState(ADD, UPDATE)));
                                 orderId.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? collectionDate : accessionNumber;
                             }
                         });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                orderLookupButton.setEnabled(isState(DISPLAY) ||
                                             (canEditSample && isState(ADD, UPDATE)));
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
                                                           (canEditSample && isState(ADD, UPDATE)));
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
                                 collectionTime.setEnabled(canEditSample && isState(ADD, UPDATE));
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
                if (receivedDate.isEnabled() && revalidate)
                    revalidate(receivedDate);
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                setReceivedDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                receivedDate.setEnabled(isState(QUERY) || (canEditSample && isState(ADD, UPDATE)));
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
                                                            (canEditSample && isState(ADD, UPDATE)));
                                 clientReference.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? patientId : status;
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

        addScreenHandler(patientId,
                         SampleMeta.getNeonatalPatientId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientId.setValue(getPatientId());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientId.setEnabled(isState(QUERY));
                                 patientId.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? patientLastName : clientReference;
                             }
                         });

        addScreenHandler(patientEmptySearchButton,
                         "patientEmptySearchButton",
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 patientEmptySearchButton.setEnabled(canEditSample &&
                                                                     !isPatientLocked &&
                                                                     isState(ADD, UPDATE));
                             }
                         });

        addScreenHandler(patientFieldSearchButton,
                         "patientFieldSearchButton",
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 patientFieldSearchButton.setEnabled(canEditSample &&
                                                                     !isPatientLocked &&
                                                                     isState(ADD, UPDATE));
                             }
                         });

        addScreenHandler(unlinkPatientButton, "unlinkPatientButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                unlinkPatientButton.setEnabled(canEditSample && isState(ADD, UPDATE));
            }
        });

        addScreenHandler(editPatientButton, "editPatientButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                editPatientButton.setEnabled(canEditSample && isState(ADD, UPDATE));
            }
        });

        addScreenHandler(patientLastName,
                         SampleMeta.getNeonatalPatientLastName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientLastName.setValue(getPatientLastName());
                                 if (revalidate)
                                     revalidate(patientLastName);
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setPatientLastName(event.getValue());
                                 if (getPatientLastName() != null && getPatientFirstName() != null)
                                     patientQueryChanged(patientLastName);
                                 runScriptlets(null, SampleMeta.getNeonatalPatientLastName(), null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientLastName.setEnabled(isState(QUERY) ||
                                                            (canEditSample && canEditPatient && isState(ADD,
                                                                                                        UPDATE)));
                                 patientLastName.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? patientFirstName : patientId;
                             }
                         });

        addScreenHandler(patientFirstName,
                         SampleMeta.getNeonatalPatientFirstName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientFirstName.setValue(getPatientFirstName());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setPatientFirstName(event.getValue());
                                 if (getPatientLastName() != null && getPatientFirstName() != null)
                                     patientQueryChanged(patientFirstName);
                                 runScriptlets(null, SampleMeta.getNeonatalPatientFirstName(), null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientFirstName.setEnabled(isState(QUERY) ||
                                                             (canEditSample && canEditPatient && isState(ADD,
                                                                                                         UPDATE)));
                                 patientFirstName.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? patientBirthDate : patientLastName;
                             }
                         });

        addScreenHandler(patientBirthDate,
                         SampleMeta.getNeonatalPatientBirthDate(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientBirthDate.setValue(getPatientBirthDate());
                                 if (revalidate)
                                     revalidate(patientBirthDate);
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 setPatientBirthDate(event.getValue());
                                 collectionAgeChanged(SampleMeta.getNeonatalPatientBirthDate());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientBirthDate.setEnabled(isState(QUERY) ||
                                                             (canEditSample && canEditPatient && isState(ADD,
                                                                                                         UPDATE)));
                                 patientBirthDate.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? patientBirthTime : patientFirstName;
                             }
                         });

        addScreenHandler(patientBirthTime,
                         SampleMeta.getNeonatalPatientBirthTime(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientBirthTime.setValue(getPatientBirthTime());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 setPatientBirthTime(event.getValue());
                                 collectionAgeChanged(SampleMeta.getNeonatalPatientBirthTime());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientBirthTime.setEnabled(isState(QUERY) ||
                                                             (canEditSample && canEditPatient && isState(ADD,
                                                                                                         UPDATE)));
                                 patientBirthTime.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? patientAddrMultipleUnit : patientBirthDate;
                             }
                         });

        addScreenHandler(patientAddrMultipleUnit,
                         SampleMeta.getNeonatalPatientAddrMultipleUnit(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientAddrMultipleUnit.setValue(getPatientAddressMultipleUnit());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setPatientAddressMultipleUnit(event.getValue());
                                 runScriptlets(null,
                                               SampleMeta.getNeonatalPatientAddrMultipleUnit(),
                                               null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientAddrMultipleUnit.setEnabled(isState(QUERY) ||
                                                                    (canEditSample &&
                                                                     canEditPatient && isState(ADD,
                                                                                               UPDATE)));
                                 patientAddrMultipleUnit.setQueryMode(event.getState() == State.QUERY);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? patientAddrStreetAddress : patientBirthTime;
                             }
                         });

        addScreenHandler(patientAddrStreetAddress,
                         SampleMeta.getNeonatalPatientAddrStreetAddress(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientAddrStreetAddress.setValue(getPatientAddressStreetAddress());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setPatientAddressStreetAddress(event.getValue());
                                 runScriptlets(null,
                                               SampleMeta.getNeonatalPatientAddrStreetAddress(),
                                               null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientAddrStreetAddress.setEnabled(isState(QUERY) ||
                                                                     (canEditSample &&
                                                                      canEditPatient && isState(ADD,
                                                                                                UPDATE)));
                                 patientAddrStreetAddress.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? patientAddrCity : patientAddrMultipleUnit;
                             }
                         });

        addScreenHandler(patientAddrCity,
                         SampleMeta.getNeonatalPatientAddrCity(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientAddrCity.setValue(getPatientAddressCity());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setPatientAddressCity(event.getValue());
                                 runScriptlets(null, SampleMeta.getNeonatalPatientAddrCity(), null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientAddrCity.setEnabled(isState(QUERY) ||
                                                            (canEditSample && canEditPatient && isState(ADD,
                                                                                                        UPDATE)));
                                 patientAddrCity.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? patientAddrState : patientAddrStreetAddress;
                             }
                         });

        addScreenHandler(patientAddrState,
                         SampleMeta.getNeonatalPatientAddrState(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientAddrState.setValue(getPatientAddressState());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setPatientAddressState(event.getValue());
                                 runScriptlets(null, SampleMeta.getNeonatalPatientAddrState(), null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientAddrState.setEnabled(isState(QUERY) ||
                                                             (canEditSample && canEditPatient && isState(ADD,
                                                                                                         UPDATE)));
                                 patientAddrState.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? patientAddrZipCode : patientAddrCity;
                             }
                         });

        addScreenHandler(patientAddrZipCode,
                         SampleMeta.getNeonatalPatientAddrZipCode(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientAddrZipCode.setValue(getPatientAddressZipCode());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setPatientAddressZipCode(event.getValue());
                                 runScriptlets(null,
                                               SampleMeta.getNeonatalPatientAddrZipCode(),
                                               null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientAddrZipCode.setEnabled(isState(QUERY) ||
                                                               (canEditSample && canEditPatient && isState(ADD,
                                                                                                           UPDATE)));
                                 patientAddrZipCode.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? patientGender : patientAddrState;
                             }
                         });

        addScreenHandler(patientGender,
                         SampleMeta.getNeonatalPatientGenderId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientGender.setValue(getPatientGenderId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setPatientGenderId(event.getValue());
                                 runScriptlets(null, SampleMeta.getNeonatalPatientGenderId(), null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientGender.setEnabled(isState(QUERY) ||
                                                          (canEditSample && canEditPatient && isState(ADD,
                                                                                                      UPDATE)));
                                 patientGender.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? patientRace : patientAddrZipCode;
                             }
                         });

        addScreenHandler(patientRace,
                         SampleMeta.getNeonatalPatientRaceId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientRace.setValue(getPatientRaceId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setPatientRaceId(event.getValue());
                                 // runDomainScriptlet(SampleMeta.getNeonatalPatientLastName());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientRace.setEnabled(isState(QUERY) ||
                                                        (canEditSample && canEditPatient && isState(ADD,
                                                                                                    UPDATE)));
                                 patientRace.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? patientEthnicity : patientGender;
                             }
                         });

        addScreenHandler(patientEthnicity,
                         SampleMeta.getNeonatalPatientEthnicityId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientEthnicity.setValue(getPatientEthnicityId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setPatientEthnicityId(event.getValue());
                                 // runDomainScriptlet(SampleMeta.getNeonatalPatientEthnicityId());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientEthnicity.setEnabled(isState(QUERY) ||
                                                             (canEditSample && canEditPatient && isState(ADD,
                                                                                                         UPDATE)));
                                 patientEthnicity.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? isNicu : patientRace;
                             }
                         });

        bus.addHandler(PatientChangeEvent.getType(), new PatientChangeEvent.Handler() {
            @Override
            public void onPatientChange(PatientChangeEvent event) {
                patientId.setValue(getPatientId());
                patientLastName.setValue(getPatientLastName());
                patientFirstName.setValue(getPatientFirstName());
                patientBirthDate.setValue(getPatientBirthDate());
                patientBirthTime.setValue(getPatientBirthTime());
                patientAddrMultipleUnit.setValue(getPatientAddressMultipleUnit());
                patientAddrStreetAddress.setValue(getPatientAddressStreetAddress());
                patientAddrCity.setValue(getPatientAddressCity());
                patientAddrState.setValue(getPatientAddressState());
                patientAddrZipCode.setValue(getPatientAddressZipCode());
                patientGender.setValue(getPatientGenderId());
                patientRace.setValue(getPatientRaceId());
                patientEthnicity.setValue(getPatientEthnicityId());

                revalidate(patientLastName);
                revalidate(patientFirstName);
                revalidate(patientBirthDate);
            }
        });

        addScreenHandler(isNicu, SampleMeta.getNeonatalIsNicu(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isNicu.setValue(getIsNicu());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setIsNicu(event.getValue());
                // runDomainScriptlet(SampleMeta.getNeonatalIsNicu());
            }

            public void onStateChange(StateChangeEvent event) {
                isNicu.setEnabled(isState(QUERY) || (canEditSample && isState(ADD, UPDATE)));
                isNicu.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? birthOrder : patientEthnicity;
            }
        });

        addScreenHandler(birthOrder,
                         SampleMeta.getNeonatalBirthOrder(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 birthOrder.setValue(getBirthOrder());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setBirthOrder(event.getValue());
                                 runScriptlets(null, SampleMeta.getNeonatalBirthOrder(), null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 birthOrder.setEnabled(isState(QUERY) ||
                                                       (canEditSample && isState(ADD, UPDATE)));
                                 birthOrder.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? gestationalAge : isNicu;
                             }
                         });

        addScreenHandler(gestationalAge,
                         SampleMeta.getNeonatalGestationalAge(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 gestationalAge.setValue(getGestationalAge());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setGestationalAge(event.getValue());
                                 runScriptlets(null, SampleMeta.getNeonatalGestationalAge(), null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 gestationalAge.setEnabled(isState(QUERY) ||
                                                           (canEditSample && isState(ADD, UPDATE)));
                                 gestationalAge.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? feedingId : birthOrder;
                             }
                         });

        addScreenHandler(feedingId,
                         SampleMeta.getNeonatalFeedingId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 feedingId.setValue(getFeedingId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setFeedingId(event.getValue());
                                 runScriptlets(null, SampleMeta.getNeonatalFeedingId(), null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 feedingId.setEnabled(isState(QUERY) ||
                                                      (canEditSample && isState(ADD, UPDATE)));
                                 feedingId.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? weight : gestationalAge;
                             }
                         });

        addScreenHandler(weight, SampleMeta.getNeonatalWeight(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                weight.setValue(getWeight());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setWeight(event.getValue());
                runScriptlets(null, SampleMeta.getNeonatalWeight(), null);
            }

            public void onStateChange(StateChangeEvent event) {
                weight.setEnabled(isState(QUERY) || (canEditSample && isState(ADD, UPDATE)));
                weight.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? isTransfused : feedingId;
            }
        });

        addScreenHandler(isTransfused,
                         SampleMeta.getNeonatalIsTransfused(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 isTransfused.setValue(getIsTransfused());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setIsTransfused(event.getValue());
                                 runScriptlets(null, SampleMeta.getNeonatalIsTransfused(), null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 isTransfused.setEnabled(isState(QUERY) ||
                                                         (canEditSample && isState(ADD, UPDATE)));
                                 isTransfused.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? transfusionDate : weight;
                             }
                         });

        addScreenHandler(transfusionDate,
                         SampleMeta.getNeonatalTransfusionDate(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 transfusionDate.setValue(getTransfusionDate());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 setTransfusionDate(event.getValue());
                                 runScriptlets(null, SampleMeta.getNeonatalTransfusionDate(), null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 transfusionDate.setEnabled(isState(QUERY) ||
                                                            (canEditSample && isState(ADD, UPDATE)));
                                 transfusionDate.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? transfusionAge : isTransfused;
                             }
                         });

        addScreenHandler(transfusionAge, "transfusionAge", new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                transfusionAge.setValue(getTransfusionAge());
            }

            public void onStateChange(StateChangeEvent event) {
                transfusionAge.setEnabled(isState(QUERY));
                transfusionAge.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? isRepeat : transfusionDate;
            }
        });

        addScreenHandler(isRepeat, SampleMeta.getNeonatalIsRepeat(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isRepeat.setValue(getIsRepeat());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setIsRepeat(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                isRepeat.setEnabled(isState(QUERY) || (canEditSample && isState(ADD, UPDATE)));
                isRepeat.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? collectionAge : transfusionAge;
            }
        });

        addScreenHandler(collectionAge,
                         SampleMeta.getNeonatalCollectionAge(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 collectionAge.setValue(getNeonatalCollectionAge());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 collectionAge.setEnabled(isState(QUERY));
                                 collectionAge.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? isCollectionValid : isRepeat;
                             }
                         });

        addScreenHandler(isCollectionValid,
                         SampleMeta.getNeonatalIsCollectionValid(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 isCollectionValid.setValue(getIsCollectionValid());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setIsCollectionValid(event.getValue());
                                 // runDomainScriptlet(SampleMeta.getNeonatalIsCollectionValid());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 isCollectionValid.setEnabled(isState(QUERY) ||
                                                              (canEditSample && isState(ADD, UPDATE)));
                                 isCollectionValid.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? nextOfKinId : collectionAge;
                             }
                         });

        addScreenHandler(nextOfKinId,
                         SampleMeta.getNeonatalNextOfKinId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 nextOfKinId.setValue(getNextOfKinId());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 nextOfKinId.setEnabled(isState(QUERY));
                                 nextOfKinId.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? nextOfKinLastName : isCollectionValid;
                             }
                         });

        addScreenHandler(nextOfKinEmptySearchButton,
                         "nextOfKinEmptySearchButton",
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 nextOfKinEmptySearchButton.setEnabled(canEditSample &&
                                                                       !isNextOfKinLocked &&
                                                                       isState(ADD, UPDATE));
                             }
                         });

        addScreenHandler(nextOfKinFieldSearchButton,
                         "nextOfKinFieldSearchButton",
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 nextOfKinFieldSearchButton.setEnabled(canEditSample &&
                                                                       !isNextOfKinLocked &&
                                                                       isState(ADD, UPDATE));
                             }
                         });

        addScreenHandler(unlinkNextOfKinButton,
                         "unlinkNextOfKinButton",
                         new ScreenHandler<Object>() {
                             public void onStateChange(StateChangeEvent event) {
                                 unlinkNextOfKinButton.setEnabled(canEditSample &&
                                                                  isState(ADD, UPDATE));
                             }
                         });

        addScreenHandler(editNextOfKinButton, "editNextOfKinButton", new ScreenHandler<Object>() {
            public void onStateChange(StateChangeEvent event) {
                editNextOfKinButton.setEnabled(canEditSample && isState(ADD, UPDATE));
            }
        });

        addScreenHandler(nextOfKinLastName,
                         SampleMeta.getNeonatalNextOfKinLastName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 nextOfKinLastName.setValue(getNextOfKinLastName());
                                 if (revalidate)
                                     revalidate(nextOfKinLastName);
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setNextOfKinLastName(event.getValue());
                                 if (getNextOfKinLastName() != null &&
                                     getNextOfKinFirstName() != null)
                                     nextOfKinQueryChanged(nextOfKinLastName);
                                 runScriptlets(null,
                                               SampleMeta.getNeonatalNextOfKinLastName(),
                                               null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 nextOfKinLastName.setEnabled(isState(QUERY) ||
                                                              (canEditSample && canEditNextOfKin && isState(ADD,
                                                                                                            UPDATE)));
                                 nextOfKinLastName.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? nextOfKinMiddleName : nextOfKinId;
                             }
                         });

        addScreenHandler(nextOfKinMiddleName,
                         SampleMeta.getNeonatalNextOfKinMiddleName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 nextOfKinMiddleName.setValue(getNextOfKinMiddleName());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setNextOfKinMiddleName(event.getValue());
                                 runScriptlets(null,
                                               SampleMeta.getNeonatalNextOfKinMiddleName(),
                                               null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 nextOfKinMiddleName.setEnabled(isState(QUERY) ||
                                                                (canEditSample && canEditNextOfKin && isState(ADD,
                                                                                                              UPDATE)));
                                 nextOfKinMiddleName.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? nextOfKinFirstName : nextOfKinLastName;
                             }
                         });

        addScreenHandler(nextOfKinFirstName,
                         SampleMeta.getNeonatalNextOfKinFirstName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 nextOfKinFirstName.setValue(getNextOfKinFirstName());
                                 if (revalidate)
                                     revalidate(nextOfKinFirstName);
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setNextOfKinFirstName(event.getValue());
                                 if (getNextOfKinLastName() != null &&
                                     getNextOfKinFirstName() != null)
                                     nextOfKinQueryChanged(nextOfKinFirstName);
                                 runScriptlets(null,
                                               SampleMeta.getNeonatalNextOfKinFirstName(),
                                               null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 nextOfKinFirstName.setEnabled(isState(QUERY) ||
                                                               (canEditSample && canEditNextOfKin && isState(ADD,
                                                                                                             UPDATE)));
                                 nextOfKinFirstName.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? nextOfKinRelation : nextOfKinMiddleName;
                             }
                         });

        addScreenHandler(nextOfKinRelation,
                         SampleMeta.getNeonatalNextOfKinRelationId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 nextOfKinRelation.setValue(getNeonatalNextOfKinRelationId());
                                 if (revalidate)
                                     revalidate(nextOfKinRelation);
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setNeonatalNextOfKinRelationId(event.getValue());
                                 runScriptlets(null,
                                               SampleMeta.getNeonatalNextOfKinRelationId(),
                                               null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 nextOfKinRelation.setEnabled(isState(QUERY) ||
                                                              (canEditSample && isState(ADD, UPDATE)));
                                 nextOfKinRelation.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? nextOfKinBirthDate : nextOfKinFirstName;
                             }
                         });

        addScreenHandler(nextOfKinBirthDate,
                         SampleMeta.getNeonatalNextOfKinBirthDate(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 nextOfKinBirthDate.setValue(getNextOfKinBirthDate());
                                 if (revalidate)
                                     revalidate(nextOfKinBirthDate);
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 setNextOfKinBirthDate(event.getValue());
                                 runScriptlets(null,
                                               SampleMeta.getNeonatalNextOfKinBirthDate(),
                                               null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 nextOfKinBirthDate.setEnabled(isState(QUERY) ||
                                                               (canEditSample && canEditNextOfKin && isState(ADD,
                                                                                                             UPDATE)));
                                 nextOfKinBirthDate.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? nextOfKinNationalId : nextOfKinRelation;
                             }
                         });

        addScreenHandler(nextOfKinNationalId,
                         SampleMeta.getNeonatalNextOfKinNationalId(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 nextOfKinNationalId.setValue(getNextOfKinNationalId());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setNextOfKinNationalId(event.getValue());
                                 if (getNextOfKinNationalId() != null)
                                     nextOfKinQueryChanged(nextOfKinNationalId);
                                 else
                                     runScriptlets(null,
                                                   SampleMeta.getNeonatalNextOfKinNationalId(),
                                                   null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 nextOfKinNationalId.setEnabled(isState(QUERY) ||
                                                                (canEditSample && canEditNextOfKin && isState(ADD,
                                                                                                              UPDATE)));
                                 nextOfKinNationalId.setQueryMode(event.getState() == State.QUERY);
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? nextOfKinAddrMultipleUnit : nextOfKinBirthDate;
                             }
                         });

        addScreenHandler(nextOfKinAddrMultipleUnit,
                         SampleMeta.getNeonatalNextOfKinAddrMultipleUnit(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 nextOfKinAddrMultipleUnit.setValue(getNextOfKinAddressMultipleUnit());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setNextOfKinAddressMultipleUnit(event.getValue());
                                 runScriptlets(null,
                                               SampleMeta.getNeonatalNextOfKinAddrMultipleUnit(),
                                               null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 nextOfKinAddrMultipleUnit.setEnabled(isState(QUERY) ||
                                                                      (canEditSample &&
                                                                       canEditNextOfKin && isState(ADD,
                                                                                                   UPDATE)));
                                 nextOfKinAddrMultipleUnit.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? nextOfKinAddrStreetAddress : nextOfKinNationalId;
                             }
                         });

        addScreenHandler(nextOfKinAddrStreetAddress,
                         SampleMeta.getNeonatalNextOfKinAddrStreetAddress(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 nextOfKinAddrStreetAddress.setValue(getNextOfKinAddressStreetAddress());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setNextOfKinAddressStreetAddress(event.getValue());
                                 runScriptlets(null,
                                               SampleMeta.getNeonatalNextOfKinAddrStreetAddress(),
                                               null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 nextOfKinAddrStreetAddress.setEnabled(isState(QUERY) ||
                                                                       (canEditSample &&
                                                                        canEditNextOfKin && isState(ADD,
                                                                                                    UPDATE)));
                                 nextOfKinAddrStreetAddress.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? nextOfKinAddrCity : nextOfKinAddrMultipleUnit;
                             }
                         });

        addScreenHandler(nextOfKinAddrCity,
                         SampleMeta.getNeonatalNextOfKinAddrCity(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 nextOfKinAddrCity.setValue(getNextOfKinAddressCity());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setNextOfKinAddressCity(event.getValue());
                                 runScriptlets(null,
                                               SampleMeta.getNeonatalNextOfKinAddrCity(),
                                               null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 nextOfKinAddrCity.setEnabled(isState(QUERY) ||
                                                              (canEditSample && canEditNextOfKin && isState(ADD,
                                                                                                            UPDATE)));
                                 nextOfKinAddrCity.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? nextOfKinAddrState : nextOfKinAddrStreetAddress;
                             }
                         });

        addScreenHandler(nextOfKinAddrState,
                         SampleMeta.getNeonatalNextOfKinAddrState(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 nextOfKinAddrState.setValue(getNextOfKinAddressState());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setNextOfKinAddressState(event.getValue());
                                 runScriptlets(null,
                                               SampleMeta.getNeonatalNextOfKinAddrState(),
                                               null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 nextOfKinAddrState.setEnabled(isState(QUERY) ||
                                                               (canEditSample && canEditNextOfKin && isState(ADD,
                                                                                                             UPDATE)));
                                 nextOfKinAddrState.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? nextOfKinAddrZipCode : nextOfKinAddrCity;
                             }
                         });

        addScreenHandler(nextOfKinAddrZipCode,
                         SampleMeta.getNeonatalNextOfKinAddrZipCode(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 nextOfKinAddrZipCode.setValue(getNextOfKinAddressZipCode());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setNextOfKinAddressZipCode(event.getValue());
                                 runScriptlets(null,
                                               SampleMeta.getNeonatalNextOfKinAddrZipCode(),
                                               null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 nextOfKinAddrZipCode.setEnabled(isState(QUERY) ||
                                                                 (canEditSample && canEditNextOfKin && isState(ADD,
                                                                                                               UPDATE)));
                                 nextOfKinAddrZipCode.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? nextOfKinAddrHomePhone : nextOfKinAddrState;
                             }
                         });

        addScreenHandler(nextOfKinAddrHomePhone,
                         SampleMeta.getNeonatalNextOfKinAddrHomePhone(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 nextOfKinAddrHomePhone.setValue(getNextOfKinAddressHomePhone());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setNextOfKinAddressHomePhone(event.getValue());
                                 runScriptlets(null,
                                               SampleMeta.getNeonatalNextOfKinAddrHomePhone(),
                                               null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 nextOfKinAddrHomePhone.setEnabled(isState(QUERY) ||
                                                                   (canEditSample &&
                                                                    canEditNextOfKin && isState(ADD,
                                                                                                UPDATE)));
                                 nextOfKinAddrHomePhone.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? nextOfKinGender : nextOfKinAddrZipCode;
                             }
                         });

        addScreenHandler(nextOfKinGender,
                         SampleMeta.getNeonatalNextOfKinGenderId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 nextOfKinGender.setValue(getNextOfKinGenderId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setNextOfKinGenderId(event.getValue());
                                 runScriptlets(null,
                                               SampleMeta.getNeonatalNextOfKinGenderId(),
                                               null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 nextOfKinGender.setEnabled(isState(QUERY) ||
                                                            (canEditSample && canEditNextOfKin && isState(ADD,
                                                                                                          UPDATE)));
                                 nextOfKinGender.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? nextOfKinRace : nextOfKinAddrHomePhone;
                             }
                         });

        addScreenHandler(nextOfKinRace,
                         SampleMeta.getNeonatalNextOfKinRaceId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 nextOfKinRace.setValue(getNextOfKinRaceId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setNextOfKinRaceId(event.getValue());
                                 // runDomainScriptlet(SampleMeta.getNeonatalNextOfKinRaceId());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 nextOfKinRace.setEnabled(isState(QUERY) ||
                                                          (canEditSample && canEditNextOfKin && isState(ADD,
                                                                                                        UPDATE)));
                                 nextOfKinRace.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? nextOfKinEthnicity : nextOfKinGender;
                             }
                         });

        addScreenHandler(nextOfKinEthnicity,
                         SampleMeta.getNeonatalNextOfKinEthnicityId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 nextOfKinEthnicity.setValue(getNextOfKinEthnicityId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setNextOfKinEthnicityId(event.getValue());
                                 // runDomainScriptlet(SampleMeta.getNeonatalNextOfKinEthnicityId());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 nextOfKinEthnicity.setEnabled(isState(QUERY) ||
                                                               (canEditSample && canEditNextOfKin && isState(ADD,
                                                                                                             UPDATE)));
                                 nextOfKinEthnicity.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? providerLastName : nextOfKinRace;
                             }
                         });

        bus.addHandler(NextOfKinChangeEvent.getType(), new NextOfKinChangeEvent.Handler() {
            @Override
            public void onNextOfKinChange(NextOfKinChangeEvent event) {
                nextOfKinId.setValue(getNextOfKinId());
                nextOfKinLastName.setValue(getNextOfKinLastName());
                nextOfKinMiddleName.setValue(getNextOfKinMiddleName());
                nextOfKinFirstName.setValue(getNextOfKinFirstName());
                nextOfKinBirthDate.setValue(getNextOfKinBirthDate());
                nextOfKinNationalId.setValue(getNextOfKinNationalId());
                nextOfKinAddrMultipleUnit.setValue(getNextOfKinAddressMultipleUnit());
                nextOfKinAddrStreetAddress.setValue(getNextOfKinAddressStreetAddress());
                nextOfKinAddrCity.setValue(getNextOfKinAddressCity());
                nextOfKinAddrState.setValue(getNextOfKinAddressState());
                nextOfKinAddrZipCode.setValue(getNextOfKinAddressZipCode());
                nextOfKinAddrHomePhone.setValue(getNextOfKinAddressHomePhone());
                nextOfKinGender.setValue(getNextOfKinGenderId());
                nextOfKinRace.setValue(getNextOfKinRaceId());
                nextOfKinEthnicity.setValue(getNextOfKinEthnicityId());

                revalidate(nextOfKinLastName);
                revalidate(nextOfKinFirstName);
                revalidate(nextOfKinBirthDate);
            }
        });

        addScreenHandler(providerLastName,
                         SampleMeta.getNeonatalProviderLastName(),
                         new ScreenHandler<AutoCompleteValue>() {
                             public void onDataChange(DataChangeEvent event) {
                                 providerLastName.setValue(getProviderId(), getProviderLastName());
                             }

                             public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                                 ProviderDO data;

                                 data = null;
                                 if (event.getValue() != null)
                                     data = (ProviderDO)event.getValue().getData();
                                 setProvider(data);
                                 runScriptlets(null, SampleMeta.getNeonatalProviderLastName(), null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 providerLastName.setEnabled(isState(QUERY) ||
                                                             (canEditSample && isState(ADD, UPDATE)));
                                 providerLastName.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? providerFirstName : nextOfKinEthnicity;
                             }
                         });

        providerLastName.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (canCopyFromPrevious(event.getNativeKeyCode())) {
                    setProvider(previousManager.getSampleNeonatal().getProvider());
                    screen.focusNextWidget((Focusable)providerLastName, true);
                    event.preventDefault();
                    event.stopPropagation();
                }
            }
        });

        providerLastName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ProviderDO data;
                ArrayList<ProviderDO> list;
                Item<Integer> row;
                ArrayList<Item<Integer>> model;

                setBusy();
                try {
                    list = ProviderService1.get()
                                           .fetchByLastNameNpiExternalId(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<Item<Integer>>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new Item<Integer>(4);
                        data = list.get(i);

                        row.setKey(data.getId());
                        row.setData(data);
                        row.setCell(0, data.getLastName());
                        row.setCell(1, data.getFirstName());
                        row.setCell(2, data.getMiddleName());
                        row.setCell(3, data.getNpi());

                        model.add(row);
                    }
                    providerLastName.showAutoMatches(model);
                } catch (Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
                clearStatus();
            }
        });

        addScreenHandler(providerFirstName,
                         SampleMeta.getNeonatalProviderFirstName(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 providerFirstName.setValue(getProviderFirstName());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 providerFirstName.setEnabled(isState(QUERY));
                                 providerFirstName.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? reportToName : providerLastName;
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
                reportToName.setEnabled(isState(QUERY) || (canEditSample && isState(ADD, UPDATE)));
                reportToName.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? birthHospitalName : providerFirstName;
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
                reportToButton.setEnabled(isState(DISPLAY) ||
                                          (canEditSample && isState(ADD, UPDATE)));
            }
        });

        addScreenHandler(birthHospitalName,
                         BIRTH_HOSPITAL_KEY,
                         new ScreenHandler<AutoCompleteValue>() {
                             public void onDataChange(DataChangeEvent event) {
                                 setBirthHospital(getSampleOrganization(manager,
                                                                        Constants.dictionary().ORG_BIRTH_HOSPITAL));
                             }

                             public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                                 OrganizationDO data;

                                 data = null;
                                 if (event.getValue() != null)
                                     data = (OrganizationDO)event.getValue().getData();
                                 changeOrganization(Constants.dictionary().ORG_BIRTH_HOSPITAL, data);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 birthHospitalName.setEnabled(isState(QUERY) ||
                                                              (canEditSample && isState(ADD, UPDATE)));
                                 birthHospitalName.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? formNumber : reportToName;
                             }
                         });

        birthHospitalName.addGetMatchesHandler(new GetMatchesHandler() {
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
                    birthHospitalName.showAutoMatches(model);
                } catch (Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
                clearStatus();
            }
        });

        birthHospitalName.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (canCopyFromPrevious(event.getNativeKeyCode())) {
                    setBirthHospital(getSampleOrganization(previousManager,
                                                           Constants.dictionary().ORG_BIRTH_HOSPITAL));
                    screen.focusNextWidget((Focusable)birthHospitalName, true);
                    event.preventDefault();
                    event.stopPropagation();
                }
            }
        });

        addScreenHandler(birthHospitalButton, "birthHospitalButton", new ScreenHandler<Integer>() {
            public void onStateChange(StateChangeEvent event) {
                birthHospitalButton.setEnabled(isState(DISPLAY) ||
                                               (canEditSample && isState(ADD, UPDATE)));
            }
        });

        addScreenHandler(formNumber,
                         SampleMeta.getNeonatalFormNumber(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 formNumber.setValue(getFormNumber());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setFormNumber(event.getValue());
                                 runScriptlets(null, SampleMeta.getNeonatalFormNumber(), null);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 formNumber.setEnabled(isState(QUERY) ||
                                                       (canEditSample && isState(ADD, UPDATE)));
                                 formNumber.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? projectName : birthHospitalName;
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
                                                        (canEditSample && isState(ADD, UPDATE)));
                                 projectName.setQueryMode(isState(QUERY));
                             }

                             public Widget onTab(boolean forward) {
                                 return forward ? accessionNumber : formNumber;
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
                projectButton.setEnabled(isState(DISPLAY) ||
                                         (canEditSample && isState(ADD, UPDATE)));
            }
        });

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

        /*
         * querying by this tab is allowed on this screen, but not on all
         * screens
         */
        auxDataTab.setCanQuery(true);

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
        attachmentTab.setCanQuery(true);

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
                            Window.alert("Error: Neonatal Login call query failed; " +
                                         e.getMessage());
                            logger.log(Level.SEVERE, e.getMessage(), e);
                            setError(Messages.get().gen_queryFailed());
                        }
                    };
                }

                /*
                 * only query for neonatal screening samples
                 */
                field = new QueryData(SampleMeta.getDomain(),
                                      QueryData.Type.STRING,
                                      Constants.domain().NEONATAL);
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
        for (DictionaryDO d : CategoryCache.getBySystemName("gender")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }

        patientGender.setModel(model);
        nextOfKinGender.setModel(model);

        /*
         * show the combination of code (1,2,3 etc.) and entry("White", "Black")
         * for each race
         */
        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("race")) {
            r = DataBaseUtil.concatWithSeparator(d.getCode(), " - ", d.getEntry());
            row = new Item<Integer>(d.getId(), r);
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }

        patientRace.setModel(model);
        nextOfKinRace.setModel(model);

        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("ethnicity")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }

        patientEthnicity.setModel(model);
        nextOfKinEthnicity.setModel(model);

        stmodel = new ArrayList<Item<String>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("state")) {
            strow = new Item<String>(d.getEntry(), d.getEntry());
            strow.setEnabled( ("Y".equals(d.getIsActive())));
            stmodel.add(strow);
        }

        patientAddrState.setModel(stmodel);
        nextOfKinAddrState.setModel(stmodel);

        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("patient_relation")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }

        nextOfKinRelation.setModel(model);

        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("feeding")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }

        feedingId.setModel(model);
    }

    /*
     * basic button methods
     */

    /**
     * puts the screen in query state, sets the manager to null and instantiates
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
     * fetches the previous sample in the list returned by the latest query
     */
    @UiHandler("previous")
    protected void previous(ClickEvent event) {
        nav.previous();
    }

    /**
     * fetches the next sample sample in the list returned by the latest query
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
                    previousManager = manager;
                    manager = result;
                    setNeonatalNextOfKinRelationId(Constants.dictionary().PATIENT_RELATION_MOTHER);
                    if (isAttachmentScreenOpen)
                        addReservedAttachment();
                    cache = new HashMap<String, Object>();
                    isPatientLocked = false;
                    isNextOfKinLocked = false;
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

        SampleService1.get().getInstance(Constants.domain().NEONATAL, addCall);
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
                    if ( !Constants.domain().NEONATAL.equals(manager.getSample().getDomain())) {
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
        AddressDO paddr, naddr;
        Validation validation;

        finishEditing();

        if (isState(ADD, UPDATE)) {
            /*
             * if the patient's address has not been set, then set it from next
             * of kin's address
             */
            paddr = manager.getSampleNeonatal().getPatient().getAddress();
            naddr = manager.getSampleNeonatal().getNextOfKin().getAddress();

            if (isEmpty(paddr) && !isEmpty(naddr)) {
                paddr.setMultipleUnit(naddr.getMultipleUnit());
                paddr.setStreetAddress(naddr.getStreetAddress());
                paddr.setCity(naddr.getCity());
                paddr.setState(naddr.getState());
                paddr.setZipCode(naddr.getZipCode());
                fireDataChange();
            }
        }

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
        Integer accession;
        String prefix;
        PatientDO data;
        ValidationErrorsList e1;

        if (state == ADD)
            setBusy(Messages.get().adding());
        else
            setBusy(Messages.get().updating());

        try {
            /*
             * add the patient if it's a new one; update it if it's locked
             */
            data = manager.getSampleNeonatal().getPatient();
            if (data.getId() == null) {
                PatientService.get().validate(data);
                data = PatientService.get().add(data);
                manager.getSampleNeonatal().setPatientId(data.getId());
                manager.getSampleNeonatal().setPatient(data);
            } else if (isPatientLocked) {
                PatientService.get().validate(data);
                data = PatientService.get().update(data);
                manager.getSampleNeonatal().setPatient(data);
                isPatientLocked = false;
            }

            /*
             * add the next of kin if it's a new one; update it if it's locked
             */
            data = manager.getSampleNeonatal().getNextOfKin();
            if (data.getId() == null) {
                PatientService.get().validate(data);
                data = PatientService.get().add(data);
                manager.getSampleNeonatal().setNextOfKinId(data.getId());
                manager.getSampleNeonatal().setNextOfKin(data);
            } else if (isNextOfKinLocked) {
                PatientService.get().validate(data);
                data = PatientService.get().update(data);
                manager.getSampleNeonatal().setNextOfKin(data);
                isNextOfKinLocked = false;
            }
        } catch (ValidationErrorsList e) {
            /*
             * for display
             */
            accession = manager.getSample().getAccessionNumber();
            if (accession == null)
                accession = 0;

            /*
             * new FormErrorExceptions are created to prepend accession number
             * to the messages of FormErrorExceptions returned by patient
             * validation; other exceptions are shown as is
             */
            e1 = new ValidationErrorsList();
            prefix = Messages.get().sample_accessionPrefix(accession);
            for (Exception ex : e.getErrorList()) {
                if (ex instanceof FormErrorException)
                    e1.add(new FormErrorException(DataBaseUtil.concatWithSeparator(prefix,
                                                                                   " ",
                                                                                   ex.getMessage())));
                else
                    e1.add(ex);
            }

            showErrors(e1);
            return;
        } catch (Exception e) {
            if (isState(ADD))
                Window.alert("commitAdd(): " + e.getMessage());
            else
                Window.alert("commitUpdate(): " + e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            clearStatus();
            return;
        }

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
                    if (state == ADD)
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
     * of the widgets. If the sample was locked calls the service method to
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
            /*
             * unlock the patient if it's locked
             */
            try {
                unlockPatient();
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }

            /*
             * unlock the next of kin if it's locked
             */
            try {
                unlockNextOfKin();
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }

            manager = null;
            evaluateEdit();
            setData();
            setState(DEFAULT);
            fireDataChange();
            setDone(Messages.get().gen_addAborted());
            cache = null;
            clearScriptlets();
        } else if (isState(UPDATE)) {
            /*
             * unlock the patient if it's locked
             */
            try {
                unlockPatient();
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }

            /*
             * unlock the next of kin if it's locked
             */
            try {
                unlockNextOfKin();
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }

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
                        clearScriptlets();
                    }

                    public void failure(Throwable e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        clearStatus();
                        cache = null;
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

                    if ( !Constants.domain().NEONATAL.equals(result.getManager()
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
                attachmentPatternVariable = SystemVariableService.get()
                                                                 .fetchByExactName("attachment_pattern_neonatal");
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

            window = new org.openelis.ui.widget.Window();
            window.setName(Messages.get().trfAttachment_dataEntryTRFAttachment());
            window.setSize("670px", "520px");
            trfAttachmentScreen.setWindow(window);
            window.setContent(trfAttachmentScreen);
            OpenELIS.getBrowser().addWindow(window, "neoTRFAttachment");
            isAttachmentScreenOpen = true;

            trfAttachmentScreen.fetchUnattached(attachmentPatternVariable.getValue());
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
     * Shows the order linked to the sample on the eorder lookup screen
     */
    @UiHandler("orderLookupButton")
    protected void orderLookup(ClickEvent event) {
        /*
         * checking isBusy here makes sure that the eorder lookup screen is only
         * brought up if it or any other lookup is not already showing, which
         * could happen when the widget with the focus previously loses focus
         * because of this button being clicked
         */
        if ( !isBusy) {
            if (getAccessionNumber() == null) {
                Window.alert(Messages.get().sample_enterAccNumBeforeOrderLoad());
                orderId.setValue(null);
                return;
            }

            showEOrderLookup(orderId.getValue());
        }
    }

    /**
     * Overridden because the patient/next of kin fields can be enabled or
     * disabled several times in Add or Update states, based on factors such as
     * whether the patient is locked
     */
    public void setState(State state) {
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
    }

    /**
     * validates the screen and sets the status of validation to "Flagged" if
     * some operation needs to be completed before committing
     */
    public Validation validate() {
        Validation validation;

        if (isState(QUERY)) {
            /*
             * the user can't query for both report-to and birth hospital
             */
            if (reportToName.getQuery() != null && birthHospitalName.getQuery() != null) {
                reportToName.addException(new Exception(Messages.get()
                                                                .sampleOrganization_cantQueryByMultipleTypeException()));
                birthHospitalName.addException(new Exception(Messages.get()
                                                                     .sampleOrganization_cantQueryByMultipleTypeException()));
            } else {
                reportToName.clearExceptions();
                birthHospitalName.clearExceptions();
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
            } else if (BIRTH_HOSPITAL_KEY.equals(field.getKey())) {
                nameField = field;
                type = Constants.dictionary().ORG_BIRTH_HOSPITAL.toString();
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
     * returns from the cache, the object that has the specified key and is of
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
     * Shows the popup for patient lookup with no initial search specified
     */
    @UiHandler("patientEmptySearchButton")
    protected void patientEmptySearch(ClickEvent event) {
        if ( !isBusy) {
            setSelectedAsNextOfKin = false;
            /*
             * this makes sure that the focus gets set to some widget after the
             * patient selection is over, because in this case, patient lookup
             * screen is brought up by clicking this button instead of by making
             * an editable widget lose focus
             */
            focusedWidget = (Focusable)patientId;

            lookupPatient(null, false);
        }
    }

    /**
     * Shows the popup for patient lookup to search by the patient's fields
     */
    @UiHandler("patientFieldSearchButton")
    protected void patientFieldSearch(ClickEvent event) {
        if ( !isBusy) {
            setSelectedAsNextOfKin = false;
            /*
             * this makes sure that the focus gets set to some widget after the
             * patient selection is over, because in this case, patient lookup
             * screen is brought up by clicking this button instead of by making
             * an editable widget lose focus
             */
            focusedWidget = (Focusable)patientId;

            lookupPatient(manager.getSampleNeonatal().getPatient(), false);
        }
    }

    /**
     * Unlinks the current patient from the sample and unlocks the patient if
     * it's an existing one
     */
    @UiHandler("unlinkPatientButton")
    protected void unlinkPatient(ClickEvent event) {
        try {
            unlockPatient();
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            return;
        }

        manager.getSampleNeonatal().setPatientId(null);
        manager.getSampleNeonatal().setPatient(new PatientDO());
        evaluateEdit();
        setData();
        setState(state);
        bus.fireEvent(new PatientChangeEvent());
        patientLastName.setFocus(true);
    }

    /**
     * If the patient is an existing one and not locked then locks it and
     * enables the patient fields for editing
     */
    @UiHandler("editPatientButton")
    protected void editPatient(ClickEvent event) {
        PatientDO data;

        if (isPatientLocked || manager.getSampleNeonatal().getPatientId() == null) {
            patientLastName.setFocus(true);
            return;
        }

        try {
            data = PatientService.get().fetchForUpdate(manager.getSampleNeonatal().getPatientId());
            manager.getSampleNeonatal().setPatient(data);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            return;
        }

        isPatientLocked = true;
        evaluateEdit();
        setData();
        setState(state);
        bus.fireEvent(new PatientChangeEvent());
        patientLastName.setFocus(true);
    }

    /**
     * Shows the popup for patient lookup with no initial search specified
     */
    @UiHandler("nextOfKinEmptySearchButton")
    protected void nextOfKinEmptySearch(ClickEvent event) {
        if ( !isBusy) {
            setSelectedAsNextOfKin = true;
            /*
             * this makes sure that the focus gets set to some widget after the
             * patient selection is over, because in this case, patient lookup
             * screen is brought up by clicking this button instead of by making
             * an editable widget lose focus
             */
            focusedWidget = (Focusable)nextOfKinId;

            lookupPatient(null, false);
        }
    }

    /**
     * Shows the popup for patient lookup to search by the patient's fields
     */
    @UiHandler("nextOfKinFieldSearchButton")
    protected void nextOfKinFieldSearch(ClickEvent event) {
        if ( !isBusy) {
            setSelectedAsNextOfKin = true;
            /*
             * this makes sure that the focus gets set to some widget after the
             * patient selection is over, because in this case, patient lookup
             * screen is brought up by clicking this button instead of by making
             * an editable widget lose focus
             */
            focusedWidget = (Focusable)nextOfKinId;

            lookupPatient(manager.getSampleNeonatal().getNextOfKin(), false);
        }
    }

    /**
     * Unlinks the current next of kin from the sample and unlocks it if it's an
     * existing patient
     */
    @UiHandler("unlinkNextOfKinButton")
    protected void unlinkNextOfKin(ClickEvent event) {
        try {
            unlockNextOfKin();
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            return;
        }

        manager.getSampleNeonatal().setNextOfKinId(null);
        manager.getSampleNeonatal().setNextOfKin(new PatientDO());

        evaluateEdit();
        setData();
        setState(state);
        bus.fireEvent(new NextOfKinChangeEvent());
        nextOfKinLastName.setFocus(true);
    }

    /**
     * If the next of kin is an existing one and not locked then locks it and
     * enables the next of kin fields for editing
     */
    @UiHandler("editNextOfKinButton")
    protected void editNextOfKin(ClickEvent event) {
        PatientDO data;

        if (isNextOfKinLocked || manager.getSampleNeonatal().getNextOfKinId() == null) {
            nextOfKinLastName.setFocus(true);
            return;
        }

        try {
            data = PatientService.get()
                                 .fetchForUpdate(manager.getSampleNeonatal().getNextOfKinId());
            manager.getSampleNeonatal().setNextOfKin(data);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            return;
        }

        isNextOfKinLocked = true;
        evaluateEdit();
        setData();
        setState(state);
        bus.fireEvent(new NextOfKinChangeEvent());
        nextOfKinLastName.setFocus(true);
    }

    /**
     * shows the popup for the sample's projects
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
     * shows the popup for the sample's organizations
     */
    @UiHandler("reportToButton")
    protected void reportTo(ClickEvent event) {
        showOrganizationLookup();
    }

    /**
     * shows the popup for the sample's organizations
     */
    @UiHandler("birthHospitalButton")
    protected void birthHospital(ClickEvent event) {
        showOrganizationLookup();
    }

    /*
     * methods used for operations that affect the entire screen
     */

    /**
     * fetches the manager for the sample with this id, sets the manager to null
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
    }

    /**
     * determines if the fields on the screen can be edited based on the data
     */
    private void evaluateEdit() {
        canEditSample = (manager != null && !Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample()
                                                                                                  .getStatusId()));
        canEditPatient = (getPatientId() == null || isPatientLocked);
        canEditNextOfKin = (getNextOfKinId() == null || isNextOfKinLocked);
    }

    /**
     * creates the cache of objects like TestManager that are used frequently by
     * the different parts of the screen
     */
    private void buildCache() {
        int i;
        Integer prevId;
        ArrayList<Integer> ids;
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
     * Creates a string containing, the message that there are warnings on the
     * screen, all warning messages; if "isConfirm" is true then shows the
     * message whether the data should be committed, otherwise not
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
     * Adds the scriptlets for the domain and for all the records in the manager
     * to the scriptlet runner
     */
    private void addScriptlets() throws Exception {
        if (scriptletRunner == null)
            scriptletRunner = new ScriptletRunner<SampleSO>();

        /*
         * add the scriptlet for the domain, which is the value of this system
         * variable; don't try to look up the system variable again if it's not
         * found the first time because the scriptlet is optional
         */
        if (hasDomainScriptlet) {
            try {
                domainScriptletVariable = SystemVariableService.get()
                                                               .fetchByExactName("neonatal_scriptlet");
            } catch (NotFoundException e) {
                // ignore
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }

            /*
             * if the system variable was found, its value must point to an
             * existing dictionary entry; so if an exception is thrown on trying
             * to look up the dictionary, the user must be informed of it even
             * if it's a NotFoundException
             */
            if (domainScriptletVariable != null) {
                try {
                    domainScriptletId = DictionaryCache.getIdBySystemName(domainScriptletVariable.getValue());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }
            hasDomainScriptlet = false;
        }
        if (domainScriptletId != null)
            addScriptlet(domainScriptletId, null);

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
        data.setChanged(changed);
        data.setUid(uid);
        data.setManager(manager);
        data.setCache(cache);

        /*
         * run the scritplet and show the errors and the changed data
         */
        data = scriptletRunner.run(data);

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
        AuxFieldGroupManager auxfgm;
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
            auxfgm = get(id, AuxFieldGroupManager.class);
            for (i = 0; i < auxfgm.getFields().count(); i++ ) {
                auxf = auxfgm.getFields().getAuxFieldAt(i);
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

    /**
     * Gets the next attachment reserved for the current user on Attachment
     * screen, if any, and adds it to the sample.
     */
    private void addReservedAttachment() {
        AttachmentManager am;

        am = trfAttachmentScreen.getSelected();
        if (am != null)
            bus.fireEvent(new AddAttachmentEvent(am.getAttachment()));
    }

    /*
     * getters and setters for the fields at the sample or domain level
     */

    /**
     * returns the accession number or null if the manager is null
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
                        } catch (Exception e) {
                            Window.alert(e.getMessage());
                            logger.log(Level.SEVERE, e.getMessage(), e);
                        }
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
                         * no exceptions were thrown, so the accession number is
                         * valid
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
     * returns the order id or null if the manager is null
     */
    private String getOrderId() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getPaperOrderValidator();
    }

    /**
     * Calls the service method to load the sample from the order with the
     * specified id, if the id and the accession number are not null. Loads the
     * screen with the returned manager.
     */
    private void setOrderId(String ordId) {
        if (ordId == null || ordId.length() == 0)
            return;

        if (getAccessionNumber() == null) {
            Window.alert(Messages.get().sample_enterAccNumBeforeOrderLoad());
            orderId.setValue(null);
            return;
        }

        if (getAccessionNumber() == null) {
            Window.alert(Messages.get().sample_enterAccNumBeforeOrderLoad());
            manager.getSample().setOrderId(null);
            manager.getSampleClinical().setPaperOrderValidator(null);
            orderId.setValue(null);
            return;
        }

        /*
         * don't allow loading the order if the patient or next of kin is locked
         */
        if (isPatientLocked) {
            Window.alert(Messages.get().sample_cantLoadEOrderPatientLocked());
            orderId.setValue(getOrderId());
            return;
        } else if (isNextOfKinLocked) {
            Window.alert(Messages.get().sample_cantLoadEOrderNextOfKinLocked());
            orderId.setValue(getOrderId());
            return;
        }

        showEOrderLookup(ordId);
    }

    private void showEOrderLookup(String ordId) {
        ModalWindow modal;

        if (eorderLookup == null) {
            eorderLookup = new EOrderLookupUI() {
                @Override
                public void select() {
                    EOrderDO eorderDO;
                    SampleTestReturnVO ret;
                    ValidationErrorsList errors;

                    eorderDO = eorderLookup.getSelectedEOrder();
                    if (eorderDO == null) {
                        manager.getSample().setOrderId(null);
                        manager.getSampleNeonatal().setPaperOrderValidator(null);
                        orderId.setValue(null);
                        setFocusToNext();
                        isBusy = false;
                        return;
                    }

                    try {
                        screen.setBusy(Messages.get().gen_fetching());
                        ret = SampleService1.get().importOrder(manager, eorderDO.getId());
                    } catch (Exception e) {
                        manager.getSample().setOrderId(null);
                        manager.getSampleNeonatal().setPaperOrderValidator(null);
                        orderId.setValue(null);
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        screen.clearStatus();
                        setFocusToNext();
                        isBusy = false;
                        return;
                    }

                    manager = ret.getManager();
                    manager.getSampleNeonatal()
                           .setPaperOrderValidator(eorderDO.getPaperOrderValidator());
                    orderId.setValue(eorderDO.getPaperOrderValidator());
                    evaluateEdit();
                    setData();
                    screen.setState(screen.state);
                    revalidate = true;
                    screen.fireDataChange();
                    screen.clearStatus();
                    revalidate = false;

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
                        errors = ret.getErrors();
                        if (errors != null && errors.size() > 0) {
                            if (errors.hasWarnings())
                                Window.alert(getWarnings(errors.getErrorList(), false));
                            if (errors.hasErrors())
                                showErrors(errors);
                        }

                        if (ret.getTests() == null || ret.getTests().size() == 0) {
                            isBusy = false;
                        } else {
                            /*
                             * show the pop up for selecting the prep/reflex
                             * tests for the tests added during the import
                             */
                            showTests(ret);
                        }
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        isBusy = false;
                    }
                    setFocusToNext();
                }

                @Override
                public void cancel() {
                    orderId.setValue(getOrderId());
                    setFocusToNext();
                    isBusy = false;
                }
            };
        }

        modal = new ModalWindow();
        modal.setSize("735px", "350px");
        modal.setName(Messages.get().eorder_eorderLookup());
        if (isState(ADD, UPDATE)) {
            /*
             * isBusy is only set in add or update state because no editable
             * widget can lose focus in display state and also, the look up
             * screen doesn't call select or cancel on being closed in display
             * state, because those buttons are disabled and so it doesn't get a
             * chance to set isBusy to false
             */
            isBusy = true;
            modal.setCSS(UIResources.INSTANCE.popupWindow());
        } else {
            modal.setCSS(UIResources.INSTANCE.window());
        }
        modal.setContent(eorderLookup);

        eorderLookup.setWindow(modal);
        eorderLookup.setState(state);
        eorderLookup.setPaperOrderValidator(ordId);
    }

    /**
     * returns the collection date or null if the manager is null
     */
    private Datetime getCollectionDate() {
        if (manager == null)
            return null;
        return manager.getSample().getCollectionDate();
    }

    /**
     * sets the collection date; also resets the transfusion age
     */
    private void setCollectionDate(Datetime date) {
        manager.getSample().setCollectionDate(date);
        transfusionAge.setValue(getTransfusionAge());
    }

    /**
     * returns the collection time or null if the manager is null
     */
    private Datetime getCollectionTime() {
        if (manager == null)
            return null;
        return manager.getSample().getCollectionTime();
    }

    /**
     * sets the collection time
     */
    private void setCollectionTime(Datetime time) {
        manager.getSample().setCollectionTime(time);
    }

    /**
     * returns the received date or null if the manager is null
     */
    private Datetime getReceivedDate() {
        if (manager == null)
            return null;
        return manager.getSample().getReceivedDate();
    }

    /**
     * sets the received date
     */
    private void setReceivedDate(Datetime date) {
        manager.getSample().setReceivedDate(date);
    }

    /**
     * returns the status or null if the manager is null
     */
    private Integer getStatusId() {
        if (manager == null)
            return null;
        return manager.getSample().getStatusId();
    }

    /**
     * sets the status id
     */
    private void setStatusId(Integer statusId) {
        manager.getSample().setStatusId(statusId);
    }

    /**
     * returns the client reference or null if the manager is null
     */
    private String getClientReference() {
        if (manager == null)
            return null;
        return manager.getSample().getClientReference();
    }

    /**
     * sets the client reference
     */
    private void setClientReference(String clientReference) {
        manager.getSample().setClientReference(clientReference);
    }

    /**
     * Returns the patient's id or null if either the manager or the patient DO
     * is null
     */
    private Integer getPatientId() {
        if (manager == null || manager.getSampleNeonatal() == null ||
            manager.getSampleNeonatal().getPatient() == null)
            return null;
        return manager.getSampleNeonatal().getPatient().getId();
    }

    /**
     * returns the patient's last name or null if either the manager or the
     * patient DO is null
     */
    private String getPatientLastName() {
        if (manager == null || manager.getSampleNeonatal() == null ||
            manager.getSampleNeonatal().getPatient() == null)
            return null;
        return manager.getSampleNeonatal().getPatient().getLastName();
    }

    /**
     * sets the patient's last name
     */
    private void setPatientLastName(String name) {
        manager.getSampleNeonatal().getPatient().setLastName(name);
    }

    /**
     * returns the patient's first name or null if either the manager or the
     * patient DO is null
     */
    private String getPatientFirstName() {
        if (manager == null || manager.getSampleNeonatal() == null ||
            manager.getSampleNeonatal().getPatient() == null)
            return null;
        return manager.getSampleNeonatal().getPatient().getFirstName();
    }

    /**
     * sets the patient's first name
     */
    private void setPatientFirstName(String name) {
        manager.getSampleNeonatal().getPatient().setFirstName(name);
    }

    /**
     * returns the patient's birth date or null if either the manager or the
     * patient DO is null
     */
    private Datetime getPatientBirthDate() {
        if (manager == null || manager.getSampleNeonatal() == null ||
            manager.getSampleNeonatal().getPatient() == null)
            return null;
        return manager.getSampleNeonatal().getPatient().getBirthDate();
    }

    /**
     * sets the patient's birth date
     */
    private void setPatientBirthDate(Datetime date) {
        manager.getSampleNeonatal().getPatient().setBirthDate(date);
    }

    /**
     * returns the patient's birth time or null if either the manager or the
     * patient DO is null
     */
    private Datetime getPatientBirthTime() {
        if (manager == null || manager.getSampleNeonatal() == null ||
            manager.getSampleNeonatal().getPatient() == null)
            return null;
        return manager.getSampleNeonatal().getPatient().getBirthTime();
    }

    /**
     * sets the patient's birth time
     */
    private void setPatientBirthTime(Datetime time) {
        manager.getSampleNeonatal().getPatient().setBirthTime(time);
    }

    /**
     * returns the patient's multiple unit (apt/suite) or null if either the
     * manager or the patient DO is null
     */
    private String getPatientAddressMultipleUnit() {
        if (manager == null || manager.getSampleNeonatal() == null ||
            manager.getSampleNeonatal().getPatient() == null)
            return null;
        return manager.getSampleNeonatal().getPatient().getAddress().getMultipleUnit();
    }

    /**
     * sets the patient's multiple unit (apt/suite)
     */
    private void setPatientAddressMultipleUnit(String multipleUnit) {
        manager.getSampleNeonatal().getPatient().getAddress().setMultipleUnit(multipleUnit);
    }

    /**
     * returns the patient's street address or null if either the manager or the
     * patient DO is null
     */
    private String getPatientAddressStreetAddress() {
        if (manager == null || manager.getSampleNeonatal() == null ||
            manager.getSampleNeonatal().getPatient() == null)
            return null;
        return manager.getSampleNeonatal().getPatient().getAddress().getStreetAddress();
    }

    /**
     * sets the patient's street address
     */
    private void setPatientAddressStreetAddress(String streetAddress) {
        manager.getSampleNeonatal().getPatient().getAddress().setStreetAddress(streetAddress);
    }

    /**
     * returns the patient's city or null if either the manager or the patient
     * DO is null
     */
    private String getPatientAddressCity() {
        if (manager == null || manager.getSampleNeonatal() == null ||
            manager.getSampleNeonatal().getPatient() == null)
            return null;
        return manager.getSampleNeonatal().getPatient().getAddress().getCity();
    }

    /**
     * sets the patient's city
     */
    private void setPatientAddressCity(String city) {
        manager.getSampleNeonatal().getPatient().getAddress().setCity(city);
    }

    /**
     * returns the patient's state or null if either the manager or the patient
     * DO is null
     */
    private String getPatientAddressState() {
        if (manager == null || manager.getSampleNeonatal() == null ||
            manager.getSampleNeonatal().getPatient() == null)
            return null;
        return manager.getSampleNeonatal().getPatient().getAddress().getState();
    }

    /**
     * sets the patient's state
     */
    private void setPatientAddressState(String state) {
        manager.getSampleNeonatal().getPatient().getAddress().setState(state);
    }

    /**
     * returns the patient's zip code or null if either the manager or the
     * patient DO is null
     */
    private String getPatientAddressZipCode() {
        if (manager == null || manager.getSampleNeonatal() == null ||
            manager.getSampleNeonatal().getPatient() == null)
            return null;
        return manager.getSampleNeonatal().getPatient().getAddress().getZipCode();
    }

    /**
     * sets the patient's zip code
     */
    private void setPatientAddressZipCode(String zipCode) {
        manager.getSampleNeonatal().getPatient().getAddress().setZipCode(zipCode);
    }

    /**
     * returns the patient's gender or null if either the manager or the patient
     * DO is null
     */
    private Integer getPatientGenderId() {
        if (manager == null || manager.getSampleNeonatal() == null ||
            manager.getSampleNeonatal().getPatient() == null)
            return null;
        return manager.getSampleNeonatal().getPatient().getGenderId();
    }

    /**
     * sets the patient's gender
     */
    private void setPatientGenderId(Integer genderId) {
        manager.getSampleNeonatal().getPatient().setGenderId(genderId);
    }

    /**
     * returns the patient's race or null if either the manager or the patient
     * DO is null
     */
    private Integer getPatientRaceId() {
        if (manager == null || manager.getSampleNeonatal() == null ||
            manager.getSampleNeonatal().getPatient() == null)
            return null;
        return manager.getSampleNeonatal().getPatient().getRaceId();
    }

    /**
     * sets the patient's race
     */
    private void setPatientRaceId(Integer raceId) {
        manager.getSampleNeonatal().getPatient().setRaceId(raceId);
    }

    /**
     * returns the patient's ethnicity or null if either the manager or the
     * patient DO is null
     */
    private Integer getPatientEthnicityId() {
        if (manager == null || manager.getSampleNeonatal() == null ||
            manager.getSampleNeonatal().getPatient() == null)
            return null;
        return manager.getSampleNeonatal().getPatient().getEthnicityId();
    }

    /**
     * sets the patient's ethnicity
     */
    private void setPatientEthnicityId(Integer ethnicityId) {
        manager.getSampleNeonatal().getPatient().setEthnicityId(ethnicityId);
    }

    /**
     * returns next of kin's zip code or null if either the manager or next of
     * kin DO is null
     */
    private String getIsNicu() {
        if (manager == null || manager.getSampleNeonatal() == null ||
            manager.getSampleNeonatal().getNextOfKin() == null)
            return null;
        return manager.getSampleNeonatal().getIsNicu();
    }

    private void setIsNicu(String isNicu) {
        manager.getSampleNeonatal().setIsNicu(isNicu);
    }

    /**
     * returns the birth order or null if the manager is null
     */
    private Integer getBirthOrder() {
        if (manager == null || manager.getSampleNeonatal() == null)
            return null;
        return manager.getSampleNeonatal().getBirthOrder();
    }

    /**
     * sets the birth order
     */
    private void setBirthOrder(Integer birthOrder) {
        manager.getSampleNeonatal().setBirthOrder(birthOrder);
    }

    /**
     * returns the gestational age or null if the manager is null
     */
    private Integer getGestationalAge() {
        if (manager == null || manager.getSampleNeonatal() == null)
            return null;
        return manager.getSampleNeonatal().getGestationalAge();
    }

    /**
     * sets the gestational age
     */
    private void setGestationalAge(Integer gestationalAge) {
        manager.getSampleNeonatal().setGestationalAge(gestationalAge);
    }

    /**
     * returns the type of feeding or null if the manager is null
     */
    private Integer getFeedingId() {
        if (manager == null || manager.getSampleNeonatal() == null)
            return null;
        return manager.getSampleNeonatal().getFeedingId();
    }

    /**
     * sets the type of feeding
     */
    private void setFeedingId(Integer feedingId) {
        manager.getSampleNeonatal().setFeedingId(feedingId);
    }

    /**
     * returns the weight or null if the manager is null
     */
    private Integer getWeight() {
        if (manager == null || manager.getSampleNeonatal() == null)
            return null;
        return manager.getSampleNeonatal().getWeight();
    }

    /**
     * sets the weight
     */
    private void setWeight(Integer weight) {
        manager.getSampleNeonatal().setWeight(weight);
    }

    /**
     * returns whether the sample is tranfused or null if the manager is null
     */
    private String getIsTransfused() {
        if (manager == null || manager.getSampleNeonatal() == null)
            return null;
        return manager.getSampleNeonatal().getIsTransfused();
    }

    /**
     * sets whether the sample is tranfused
     */
    private void setIsTransfused(String isTransfused) {
        manager.getSampleNeonatal().setIsTransfused(isTransfused);
    }

    /**
     * returns the tranfusion date or null if the manager is null
     */
    private Datetime getTransfusionDate() {
        if (manager == null || manager.getSampleNeonatal() == null)
            return null;
        return manager.getSampleNeonatal().getTransfusionDate();
    }

    /**
     * sets the tranfusion date
     */
    private void setTransfusionDate(Datetime transfusionDate) {
        manager.getSampleNeonatal().setTransfusionDate(transfusionDate);
        transfusionAge.setValue(getTransfusionAge());
    }

    /**
     * returns the tranfusion age, calculated as the difference between the
     * collection date and transfusion date, or null if the manager is null or
     * any of the dates is null
     */
    private Integer getTransfusionAge() {
        Datetime cd, td;
        Long diff, day;
        Double numDays;

        cd = getCollectionDate();
        td = getTransfusionDate();
        if (cd == null || td == null)
            return null;

        diff = cd.getDate().getTime() - td.getDate().getTime();
        day = 86400000L;
        numDays = diff.doubleValue() / day.doubleValue();

        return numDays.intValue();
    }

    /**
     * returns the whether the sample is repeat sample or null if the manager is
     * null
     */
    private String getIsRepeat() {
        if (manager == null || manager.getSampleNeonatal() == null)
            return null;
        return manager.getSampleNeonatal().getIsRepeat();
    }

    /**
     * sets the whether the sample is repeat sample
     */
    private void setIsRepeat(String isRepeat) {
        manager.getSampleNeonatal().setIsRepeat(isRepeat);
    }

    /**
     * returns the collection age or null if the manager is null
     */
    private Integer getNeonatalCollectionAge() {
        if (manager == null || manager.getSampleNeonatal() == null)
            return null;
        return manager.getSampleNeonatal().getCollectionAge();
    }

    /**
     * returns whether the collection is valid or null if the manager is null
     */
    private String getIsCollectionValid() {
        if (manager == null || manager.getSampleNeonatal() == null)
            return null;
        return manager.getSampleNeonatal().getIsCollectionValid();
    }

    /**
     * sets whether the collection is valid
     */
    private void setIsCollectionValid(String isCollectionValid) {
        manager.getSampleNeonatal().setIsCollectionValid(isCollectionValid);
    }

    /**
     * Returns the next of kin's id or null if either the manager or the patient
     * DO is null
     */
    private Integer getNextOfKinId() {
        if (manager == null || manager.getSampleNeonatal() == null ||
            manager.getSampleNeonatal().getNextOfKin() == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKin().getId();
    }

    /**
     * returns next of kin's last name or null if either the manager or next of
     * kin DO is null
     */
    private String getNextOfKinLastName() {
        if (manager == null || manager.getSampleNeonatal() == null ||
            manager.getSampleNeonatal().getNextOfKin() == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKin().getLastName();
    }

    /**
     * sets next of kin's last name
     */
    private void setNextOfKinLastName(String name) {
        manager.getSampleNeonatal().getNextOfKin().setLastName(name);
    }

    /**
     * returns next of kin's middle name or null if either the manager or next
     * of kin DO is null
     */
    private String getNextOfKinMiddleName() {
        if (manager == null || manager.getSampleNeonatal() == null ||
            manager.getSampleNeonatal().getNextOfKin() == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKin().getMiddleName();
    }

    /**
     * returns next of kin's first name or null if either the manager or next of
     * kin DO is null
     */
    private void setNextOfKinMiddleName(String name) {
        manager.getSampleNeonatal().getNextOfKin().setMiddleName(name);
    }

    /**
     * returns next of kin's first name or null if either the manager or next of
     * kin DO is null
     */
    private String getNextOfKinFirstName() {
        if (manager == null || manager.getSampleNeonatal() == null ||
            manager.getSampleNeonatal().getNextOfKin() == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKin().getFirstName();
    }

    /**
     * sets next of kin's first name
     */
    private void setNextOfKinFirstName(String name) {
        manager.getSampleNeonatal().getNextOfKin().setFirstName(name);
    }

    /**
     * returns next of kin relation or null if the manager is null
     */
    private Integer getNeonatalNextOfKinRelationId() {
        if (manager == null || manager.getSampleNeonatal() == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKinRelationId();
    }

    /**
     * sets next of kin relation
     */
    private void setNeonatalNextOfKinRelationId(Integer nextOfKinRelationId) {
        manager.getSampleNeonatal().setNextOfKinRelationId(nextOfKinRelationId);
    }

    /**
     * returns next of kin's gender or null if either the manager or next of kin
     * DO is null
     */
    private Integer getNextOfKinGenderId() {
        if (manager == null || manager.getSampleNeonatal() == null ||
            manager.getSampleNeonatal().getNextOfKin() == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKin().getGenderId();
    }

    /**
     * sets next of kin's gender
     */
    private void setNextOfKinGenderId(Integer genderId) {
        manager.getSampleNeonatal().getNextOfKin().setGenderId(genderId);
    }

    /**
     * returns next of kin's race or null if either the manager or next of kin
     * DO is null
     */
    private Integer getNextOfKinRaceId() {
        if (manager == null || manager.getSampleNeonatal() == null ||
            manager.getSampleNeonatal().getNextOfKin() == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKin().getRaceId();
    }

    /**
     * sets next of kin's race
     */
    private void setNextOfKinRaceId(Integer raceId) {
        manager.getSampleNeonatal().getNextOfKin().setRaceId(raceId);
    }

    /**
     * returns next of kin's ethnicity or null if either the manager or next of
     * kin DO is null
     */
    private Integer getNextOfKinEthnicityId() {
        if (manager == null || manager.getSampleNeonatal() == null ||
            manager.getSampleNeonatal().getNextOfKin() == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKin().getEthnicityId();
    }

    /**
     * sets next of kin's ethnicity
     */
    private void setNextOfKinEthnicityId(Integer ethnicityId) {
        manager.getSampleNeonatal().getNextOfKin().setEthnicityId(ethnicityId);
    }

    /**
     * returns next of kin's birth date or null if either the manager or next of
     * kin DO is null
     */
    private Datetime getNextOfKinBirthDate() {
        if (manager == null || manager.getSampleNeonatal() == null ||
            manager.getSampleNeonatal().getNextOfKin() == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKin().getBirthDate();
    }

    /**
     * sets next of kin's birth date
     */
    private void setNextOfKinBirthDate(Datetime date) {
        manager.getSampleNeonatal().getNextOfKin().setBirthDate(date);
    }

    /**
     * Returns the next of kin' national id or null if either the manager or the
     * next of kin DO is null
     */
    private String getNextOfKinNationalId() {
        if (manager == null || manager.getSampleNeonatal() == null ||
            manager.getSampleNeonatal().getPatient() == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKin().getNationalId();
    }

    /**
     * Sets the next of kin's national id
     */
    private void setNextOfKinNationalId(String nationalId) {
        manager.getSampleNeonatal().getNextOfKin().setNationalId(nationalId);
    }

    /**
     * returns next of kin's home phone or null if either the manager or next of
     * kin DO is null
     */
    private String getNextOfKinAddressHomePhone() {
        if (manager == null || manager.getSampleNeonatal() == null ||
            manager.getSampleNeonatal().getNextOfKin() == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKin().getAddress().getHomePhone();
    }

    /**
     * sets next of kin's home phone
     */
    private void setNextOfKinAddressHomePhone(String homePhone) {
        manager.getSampleNeonatal().getNextOfKin().getAddress().setHomePhone(homePhone);
    }

    /**
     * returns next of kin's multiple unit (apt/suite) or null if either the
     * manager or next of kin DO is null
     */
    private String getNextOfKinAddressMultipleUnit() {
        if (manager == null || manager.getSampleNeonatal() == null ||
            manager.getSampleNeonatal().getNextOfKin() == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKin().getAddress().getMultipleUnit();
    }

    /**
     * sets next of kin's multiple unit (apt/suite)
     */
    private void setNextOfKinAddressMultipleUnit(String multipleUnit) {
        manager.getSampleNeonatal().getNextOfKin().getAddress().setMultipleUnit(multipleUnit);
    }

    /**
     * returns next of kin's street address or null if either the manager or
     * next of kin DO is null
     */
    private String getNextOfKinAddressStreetAddress() {
        if (manager == null || manager.getSampleNeonatal() == null ||
            manager.getSampleNeonatal().getNextOfKin() == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKin().getAddress().getStreetAddress();
    }

    /**
     * sets next of kin's street address
     */
    private void setNextOfKinAddressStreetAddress(String streetAddress) {
        manager.getSampleNeonatal().getNextOfKin().getAddress().setStreetAddress(streetAddress);
    }

    /**
     * returns next of kin's city or null if either the manager or next of kin
     * DO is null
     */
    private String getNextOfKinAddressCity() {
        if (manager == null || manager.getSampleNeonatal() == null ||
            manager.getSampleNeonatal().getNextOfKin() == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKin().getAddress().getCity();
    }

    /**
     * sets next of kin's city
     */
    private void setNextOfKinAddressCity(String city) {
        manager.getSampleNeonatal().getNextOfKin().getAddress().setCity(city);
    }

    /**
     * returns next of kin's state or null if either the manager or next of kin
     * DO is null
     */
    private String getNextOfKinAddressState() {
        if (manager == null || manager.getSampleNeonatal() == null ||
            manager.getSampleNeonatal().getNextOfKin() == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKin().getAddress().getState();
    }

    /**
     * sets next of kin's state
     */
    private void setNextOfKinAddressState(String state) {
        manager.getSampleNeonatal().getNextOfKin().getAddress().setState(state);
    }

    /**
     * returns next of kin's zip code or null if either the manager or next of
     * kin DO is null
     */
    private String getNextOfKinAddressZipCode() {
        if (manager == null || manager.getSampleNeonatal() == null ||
            manager.getSampleNeonatal().getNextOfKin() == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKin().getAddress().getZipCode();
    }

    /**
     * sets next of kin's zip code
     */
    private void setNextOfKinAddressZipCode(String zipCode) {
        manager.getSampleNeonatal().getNextOfKin().getAddress().setZipCode(zipCode);
    }

    /**
     * returns the provider's id or null if the manager is null
     */
    private Integer getProviderId() {
        if (manager == null || manager.getSampleNeonatal() == null)
            return null;
        return manager.getSampleNeonatal().getProviderId();
    }

    /**
     * sets whether the provider's id
     */
    private void setProviderId(Integer providerId) {
        manager.getSampleNeonatal().setProviderId(providerId);
    }

    /**
     * returns the provider's last name or null if the manager is null
     */
    private String getProviderLastName() {
        if (manager == null || manager.getSampleNeonatal() == null ||
            manager.getSampleNeonatal().getProvider() == null)
            return null;
        return manager.getSampleNeonatal().getProvider().getLastName();
    }

    /**
     * returns the provider's first name or null if the manager is null
     */
    private String getProviderFirstName() {
        if (manager == null || manager.getSampleNeonatal() == null ||
            manager.getSampleNeonatal().getProvider() == null)
            return null;
        return manager.getSampleNeonatal().getProvider().getFirstName();
    }

    /**
     * If the passed provider is not null then sets it as the sample's provider,
     * otherwise, blanks the provider. Refreshes the display of the autcomplete
     * accordingly.
     */
    private void setProvider(ProviderDO data) {
        if (data == null || data.getId() == null)
            setProviderId(null);
        else
            setProviderId(data.getId());
        manager.getSampleNeonatal().setProvider(data);
        providerLastName.setValue(getProviderId(), getProviderLastName());
        providerFirstName.setValue(getProviderFirstName());
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
     * Sets the display of the autocomplete for birth hospital from the passed
     * sample organization
     */
    private void setBirthHospital(SampleOrganizationViewDO data) {
        if (data != null)
            birthHospitalName.setValue(data.getOrganizationId(), data.getOrganizationName());
        else
            birthHospitalName.setValue(null, "");
    }

    /**
     * returns the form (barcode) number or null if the manager is null
     */
    private String getFormNumber() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getFormNumber();
    }

    /**
     * sets the form (barcode) number
     */
    private void setFormNumber(String formNumber) {
        manager.getSampleNeonatal().setFormNumber(formNumber);
    }

    /**
     * Makes the patient lookup screen find patients matching the data in the
     * DO. If the flag is true then the screen is not shown if no patient was
     * found, otherwise the screen is shown regardless. Sets the patient
     * selected after the query as the sample's patient.
     */
    private void lookupPatient(PatientDO data, boolean dontShowIfNoPatient) {
        if (patientLookup == null) {
            patientLookup = new PatientLookupUI() {
                boolean nidUsedForOther;

                public void select() {
                    boolean patChanged, nokChanged;

                    patChanged = false;
                    nokChanged = false;
                    if (setSelectedAsNextOfKin) {
                        /*
                         * set the patient selected on the pop up as the next of
                         * kin
                         */
                        setNextOfKin(selectedPatient);
                        nokChanged = true;
                    } else {
                        /*
                         * set the patient selected on the pop up as the patient
                         * and the patient relation as the next of kin, also set
                         * the relation
                         */
                        setPatient(selectedPatient);
                        patChanged = true;
                        if ( !isNextOfKinLocked && selectedNextOfKin != null) {
                            setNeonatalNextOfKinRelationId(selectedNextOfKin.getRelationId());
                            setNextOfKin(selectedNextOfKin);
                            nokChanged = true;
                        }
                    }

                    screen.evaluateEdit();
                    screen.setData();
                    screen.setState(screen.state);
                    if (patChanged)
                        screen.bus.fireEvent(new PatientChangeEvent());
                    if (nokChanged)
                        screen.bus.fireEvent(new NextOfKinChangeEvent());
                    setFocusToNext();
                    isBusy = false;
                    nidUsedForOther = false;
                }

                public void cancel() {
                    boolean nokChanged;
                    /*
                     * if the query on the popup was executed only for NID and
                     * that NID has been used for another patient, blank the
                     * sample's next of kin's NID and refresh the screen
                     */
                    nokChanged = false;
                    if (queryByNId && nidUsedForOther) {
                        setNextOfKinNationalId(null);
                        setNextOfKin(manager.getSampleNeonatal().getNextOfKin());
                        nokChanged = true;
                    }
                    if (nokChanged)
                        screen.bus.fireEvent(new NextOfKinChangeEvent());
                    setFocusToNext();
                    isBusy = false;
                    nidUsedForOther = false;
                }

                @Override
                public void patientsFound() {
                    PatientDO samPat, otherPat;

                    /*
                     * if the query on the popup was executed only for NID, at
                     * most one patient will be returned; find out if that
                     * patient is the same as the sample's next of kin
                     */
                    if (queryByNId) {
                        samPat = manager.getSampleNeonatal().getNextOfKin();
                        otherPat = patientTable.getRowAt(0).getData();
                        nidUsedForOther = !otherPat.getId().equals(samPat.getId());
                    }
                }
            };
        }

        patientLookup.query(data, dontShowIfNoPatient);
    }

    /**
     * sets the focus to the first enabled widget in the tabbing order after
     * "focusedWidget"
     */
    private void setFocusToNext() {
        /*
         * focusNextWidget() throws an exception if focusedWidget is null and it
         * can be null e.g. when the user clicks the order look-up button
         * without entering anything in order #; the check for null is here to
         * avoid having to put it in all places where this could happen
         */
        if (focusedWidget != null) {
            focusNextWidget(focusedWidget, true);
            focusedWidget = null;
        }
    }

    /**
     * Clears the validation errors of the widget and validates it based on its
     * latest data
     */
    private void revalidate(HasExceptions widget) {
        widget.clearValidateExceptions();
        widget.hasExceptions();
    }

    /**
     * Returns true if all the fields shown on the screen from the address are
     * null, false otherwise
     */
    private boolean isEmpty(AddressDO addr) {
        return addr.getMultipleUnit() == null && addr.getStreetAddress() == null &&
               addr.getCity() == null && addr.getState() == null && addr.getZipCode() == null;
    }

    /**
     * Unlocks the patient if it's locked
     */
    private void unlockPatient() throws Exception {
        PatientDO data;

        if (isPatientLocked) {
            data = PatientService.get().abortUpdate(manager.getSampleNeonatal().getPatientId());
            manager.getSampleNeonatal().setPatient(data);
            isPatientLocked = false;
        }
    }

    /**
     * Unlocks the next of kin if it's locked
     */
    private void unlockNextOfKin() throws Exception {
        PatientDO data;

        if (isNextOfKinLocked) {
            data = PatientService.get().abortUpdate(manager.getSampleNeonatal().getNextOfKinId());
            manager.getSampleNeonatal().setNextOfKin(data);
            isNextOfKinLocked = false;
        }
    }

    /**
     * Sets the busy flag and the passed widget as the current one with focus;
     * looks up the patients matching the data entered in the patient's fields
     */
    private void patientQueryChanged(Focusable widget) {
        /*
         * look up patients only if the current patient is not locked
         */
        if ( !isPatientLocked) {
            isBusy = true;
            setSelectedAsNextOfKin = false;
            focusedWidget = widget;
            lookupPatient(manager.getSampleNeonatal().getPatient(), true);
        }
    }

    /**
     * Sets the busy flag and looks up the patients matching the data entered in
     * the next of kin's fields
     */
    private void nextOfKinQueryChanged(Focusable widget) {
        /*
         * if the current next of kin is locked then look up patients only if
         * the NID was changed
         */
        if ( !isNextOfKinLocked || widget == nextOfKinNationalId) {
            isBusy = true;
            setSelectedAsNextOfKin = true;
            focusedWidget = widget;
            lookupPatient(manager.getSampleNeonatal().getNextOfKin(), true);
        }
    }

    /**
     * Sets the passed patient as the sample's patient and refreshes the screen
     * to show its data
     */
    private void setPatient(PatientDO data) {
        if (data == null)
            return;

        manager.getSampleNeonatal().setPatientId(data.getId());
        manager.getSampleNeonatal().setPatient(data);

        if (getPatientLastName() != null)
            patientLastName.clearExceptions();

        if (getPatientAddressStreetAddress() != null)
            patientAddrStreetAddress.clearExceptions();

        runScriptlets(null, SampleMeta.getNeonatalPatientId(), null);
    }

    /**
     * Sets the passed patient as the sample's next of kin and refreshes the
     * screen to show its data
     */
    private void setNextOfKin(PatientDO data) {
        if (data == null)
            return;

        manager.getSampleNeonatal().setNextOfKinId(data.getId());
        manager.getSampleNeonatal().setNextOfKin(data);

        if (getNextOfKinLastName() != null)
            nextOfKinLastName.clearExceptions();

        if (getNextOfKinAddressStreetAddress() != null)
            nextOfKinAddrStreetAddress.clearExceptions();

        runScriptlets(null, SampleMeta.getNeonatalNextOfKinId(), null);
    }

    /**
     * TODO change comment Returns true if the collection date-time is after the
     * patient birth date-time
     */
    private void collectionAgeChanged(String changed) {
        Integer accession;
        Datetime cd, ct, bd, bt;

        cd = getCollectionDate();
        ct = getCollectionTime();
        bd = getPatientBirthDate();
        bt = getPatientBirthTime();

        accession = manager.getSample().getAccessionNumber();
        if (accession == null)
            accession = 0;

        if (bd != null &&
            cd != null &&
            DataBaseUtil.isAfter(bd, cd) ||
            (DataBaseUtil.isSame(cd, bd) && bt != null && ct != null && DataBaseUtil.isAfter(bt, ct)))
            setError(Messages.get().sampleNeonatal_patBirthDateAfterCollectDateException(accession));
        else
            runScriptlets(null, changed, null);
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
            else if (Constants.dictionary().ORG_BIRTH_HOSPITAL.equals(type))
                setBirthHospital(data);
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
                        setBirthHospital(getSampleOrganization(manager,
                                                               Constants.dictionary().ORG_BIRTH_HOSPITAL));
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
        name = isSameWindow ? Messages.get().sampleNeonatal_login() : null;
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