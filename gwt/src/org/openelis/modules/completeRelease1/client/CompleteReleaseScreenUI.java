package org.openelis.modules.completeRelease1.client;

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.Screen.ShortKeys.*;
import static org.openelis.ui.screen.Screen.Validation.Status.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.openelis.cache.CacheProvider;
import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisQaEventDO;
import org.openelis.domain.AnalysisQaEventViewDO;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AttachmentDO;
import org.openelis.domain.AttachmentItemViewDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.AuxFieldViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.NoteDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.PatientDO;
import org.openelis.domain.ResultDO;
import org.openelis.domain.ResultViewDO;
import org.openelis.domain.SampleItemDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleTestRequestVO;
import org.openelis.domain.SampleTestReturnVO;
import org.openelis.domain.SystemVariableDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.WorksheetAnalysisViewDO;
import org.openelis.domain.WorksheetItemDO;
import org.openelis.manager.AuxFieldGroupManager;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.SampleManager1.PostProcessing;
import org.openelis.manager.TestManager;
import org.openelis.manager.WorksheetManager1;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.attachment.client.AttachmentUtil;
import org.openelis.modules.attachment.client.DisplayAttachmentEvent;
import org.openelis.modules.auxData.client.AddAuxGroupEvent;
import org.openelis.modules.auxData.client.AuxDataTabUI;
import org.openelis.modules.auxData.client.RemoveAuxGroupEvent;
import org.openelis.modules.auxiliary.client.AuxiliaryService;
import org.openelis.modules.main.client.OpenELIS;
import org.openelis.modules.note.client.EditNoteLookupUI;
import org.openelis.modules.patient.client.PatientService;
import org.openelis.modules.report.client.FinalReportService;
import org.openelis.modules.sample1.client.AccessionChangeEvent;
import org.openelis.modules.sample1.client.AddRowAnalytesEvent;
import org.openelis.modules.sample1.client.AddTestEvent;
import org.openelis.modules.sample1.client.AnalysisChangeEvent;
import org.openelis.modules.sample1.client.AnalysisNotesTabUI;
import org.openelis.modules.sample1.client.AnalysisTabUI;
import org.openelis.modules.sample1.client.AttachmentTabUI;
import org.openelis.modules.sample1.client.ClinicalTabUI;
import org.openelis.modules.sample1.client.EnvironmentalTabUI;
import org.openelis.modules.sample1.client.NeonatalTabUI;
import org.openelis.modules.sample1.client.NoteChangeEvent;
import org.openelis.modules.sample1.client.PTTabUI;
import org.openelis.modules.sample1.client.PatientLockEvent;
import org.openelis.modules.sample1.client.PrivateWellTabUI;
import org.openelis.modules.sample1.client.QAEventAddedEvent;
import org.openelis.modules.sample1.client.QAEventTabUI;
import org.openelis.modules.sample1.client.QuickEntryTabUI;
import org.openelis.modules.sample1.client.ResultChangeEvent;
import org.openelis.modules.sample1.client.ResultTabUI;
import org.openelis.modules.sample1.client.RunScriptletEvent;
import org.openelis.modules.sample1.client.SDWISTabUI;
import org.openelis.modules.sample1.client.SampleHistoryUtility1;
import org.openelis.modules.sample1.client.SampleItemAddedEvent;
import org.openelis.modules.sample1.client.SampleItemChangeEvent;
import org.openelis.modules.sample1.client.SampleItemChangeEvent.Action;
import org.openelis.modules.sample1.client.SampleItemTabUI;
import org.openelis.modules.sample1.client.SampleNotesTabUI;
import org.openelis.modules.sample1.client.SampleService1;
import org.openelis.modules.sample1.client.SampleTabUI;
import org.openelis.modules.sample1.client.SelectedType;
import org.openelis.modules.sample1.client.StorageTabUI;
import org.openelis.modules.sample1.client.TestSelectionLookupUI;
import org.openelis.modules.sampleTracking1.client.SampleTrackingScreenUI;
import org.openelis.modules.scriptlet.client.ScriptletFactory;
import org.openelis.modules.systemvariable.client.SystemVariableService;
import org.openelis.modules.test.client.TestService;
import org.openelis.modules.worksheet1.client.WorksheetService1;
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
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.Warning;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.scriptlet.ScriptletInt;
import org.openelis.ui.scriptlet.ScriptletRunner;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.CheckMenuItem;
import org.openelis.ui.widget.Confirm;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.Menu;
import org.openelis.ui.widget.MenuItem;
import org.openelis.ui.widget.ModalWindow;
import org.openelis.ui.widget.TabLayoutPanel;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.UnselectionEvent;
import org.openelis.ui.widget.table.event.UnselectionHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class CompleteReleaseScreenUI extends Screen implements CacheProvider {

    @UiTemplate("CompleteRelease.ui.xml")
    interface CompleteReleaseUiBinder extends UiBinder<Widget, CompleteReleaseScreenUI> {
    };

    private static CompleteReleaseUiBinder     uiBinder = GWT.create(CompleteReleaseUiBinder.class);

    @UiField
    protected Button                           query, update, commit, complete, release, abort,
                    optionsButton, details;

    @UiField
    protected Menu                             optionsMenu, historyMenu;

    @UiField
    protected MenuItem                         unreleaseAnalysis, queryByWorksheet, historySample,
                    historySampleSpecific, historySampleProject, historySampleOrganization,
                    historySampleItem, historyAnalysis, historyCurrentResult, historyStorage,
                    historySampleQA, historyAnalysisQA, historyAuxData;

    @UiField
    protected CheckMenuItem                    previewFinalReport;

    @UiField
    protected Dropdown<Integer>                sampleStatus, analysisStatus;

    @UiField
    protected Table                            table;

    @UiField
    protected TabLayoutPanel                   tabPanel;

    @UiField(provided = true)
    protected SampleTabUI                      sampleTab;

    @UiField(provided = true)
    protected EnvironmentalTabUI               environmentalTab;

    @UiField(provided = true)
    protected PrivateWellTabUI                 privateWellTab;

    @UiField(provided = true)
    protected SDWISTabUI                       sdwisTab;

    @UiField(provided = true)
    protected NeonatalTabUI                    neonatalTab;

    @UiField(provided = true)
    protected ClinicalTabUI                    clinicalTab;

    @UiField(provided = true)
    protected PTTabUI                          ptTab;

    @UiField(provided = true)
    protected QuickEntryTabUI                  quickEntryTab;

    @UiField(provided = true)
    protected SampleItemTabUI                  sampleItemTab;

    @UiField(provided = true)
    protected AnalysisTabUI                    analysisTab;

    @UiField(provided = true)
    protected ResultTabUI                      resultTab;

    @UiField(provided = true)
    protected AnalysisNotesTabUI               analysisNotesTab;

    @UiField(provided = true)
    protected SampleNotesTabUI                 sampleNotesTab;

    @UiField(provided = true)
    protected StorageTabUI                     storageTab;

    @UiField(provided = true)
    protected QAEventTabUI                     qaEventTab;

    @UiField(provided = true)
    protected AuxDataTabUI                     auxDataTab;

    @UiField(provided = true)
    protected AttachmentTabUI                  attachmentTab;

    protected SampleManager1                   manager;

    protected HashMap<Integer, SampleManager1> managers;

    protected boolean                          isBusy, hasEnvScriptlet, hasNeonatalScriptlet,
                    hasSDWISScriptlet;

    protected ModulePermission                 userPermission;

    protected CompleteReleaseScreenUI          screen;

    protected TestSelectionLookupUI            testSelectionLookup;

    protected Confirm                          unreleaseAnalysisConfirm;

    protected QueryByWorksheetLookupUI         queryByWorksheetLookup;

    protected EditNoteLookupUI                 editNoteLookup;

    protected HashMap<String, Object>          cache;

    protected HashMap<Integer, String>         analysisStatuses;

    protected AsyncCallbackUI<ArrayList<SampleManager1>> queryCall, unlockCall;

    protected AsyncCallbackUI<SampleManager1>            fetchForUpdateCall, commitUpdateCall;

    protected ScriptletRunner<SampleSO>                  scriptletRunner;

    protected HashMap<Integer, HashSet<Integer>>         scriptlets;

    protected Integer                                    lastAccession, neonatalScriptletId,
                    envScriptletId, sdwisScriptletId;

    protected static final SampleManager1.Load           singleResultElements[] = {
                    SampleManager1.Load.ANALYSISUSER, SampleManager1.Load.AUXDATA,
                    SampleManager1.Load.NOTE, SampleManager1.Load.ORGANIZATION,
                    SampleManager1.Load.PROJECT, SampleManager1.Load.QA,
                    SampleManager1.Load.SINGLERESULT, SampleManager1.Load.STORAGE,
                    SampleManager1.Load.WORKSHEET, SampleManager1.Load.ATTACHMENT},

                    resultElements[] = {SampleManager1.Load.ANALYSISUSER,
                    SampleManager1.Load.AUXDATA, SampleManager1.Load.NOTE,
                    SampleManager1.Load.ORGANIZATION, SampleManager1.Load.PROJECT,
                    SampleManager1.Load.QA, SampleManager1.Load.RESULT,
                    SampleManager1.Load.STORAGE, SampleManager1.Load.WORKSHEET,
                    SampleManager1.Load.ATTACHMENT};

    protected enum Tabs {
        SAMPLE, ENVIRONMENTAL, PRIVATE_WELL, SDWIS, NEONATAL, CLINICAL, PT, QUICK_ENTRY,
        SAMPLE_ITEM, ANALYSIS, TEST_RESULT, ANALYSIS_NOTES, SAMPLE_NOTES, STORAGE, QA_EVENTS,
        AUX_DATA, ATTACHMENT, BLANK
    };

    protected static final String NEO_SCRIPTLET_SYSTEM_VARIABLE = "neonatal_scriptlet",
                    ENV_SCRIPTLET_SYSTEM_VARIABLE = "environmental_scriptlet",
                    SDWIS_SCRIPTLET_SYSTEM_VARIABLE = "sdwis_scriptlet";

    /**
     * Check the permissions for this screen, intialize the tabs and widgets
     */
    public CompleteReleaseScreenUI(WindowInt window) throws Exception {
        setWindow(window);

        userPermission = UserCache.getPermission().getModule("samplecompleterelease");
        if (userPermission == null)
            throw new PermissionException(Messages.get()
                                                  .screenPermException("Complete and Release Screen"));

        try {
            CategoryCache.getBySystemNames("sample_status",
                                           "sample_domain",
                                           "gender",
                                           "race",
                                           "ethnicity",
                                           "state",
                                           "country",
                                           "pt_provider",
                                           "analysis_status",
                                           "type_of_sample",
                                           "source_of_sample",
                                           "sample_container",
                                           "unit_of_measure",
                                           "qaevent_type",
                                           "worksheet_status",
                                           "aux_field_value_type",
                                           "organization_type",
                                           "sdwis_sample_type",
                                           "sdwis_sample_category");
        } catch (Exception e) {
            window.close();
            throw e;
        }

        sampleTab = new SampleTabUI(this);
        environmentalTab = new EnvironmentalTabUI(this);
        privateWellTab = new PrivateWellTabUI(this);
        sdwisTab = new SDWISTabUI(this);
        neonatalTab = new NeonatalTabUI(this);
        clinicalTab = new ClinicalTabUI(this);
        ptTab = new PTTabUI(this);
        quickEntryTab = new QuickEntryTabUI(this);
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

        initialize();
        manager = null;
        managers = null;
        hasEnvScriptlet = true;
        hasSDWISScriptlet = true;
        hasNeonatalScriptlet = true;
        showTabs(Tabs.BLANK);
        setData();
        setState(DEFAULT);
        table.setModel(getTableModel(null));
        fireDataChange();
        bus.fireEvent(new org.openelis.modules.sample1.client.SelectionEvent(SelectedType.NONE,
                                                                             null));

        logger.fine("Complete Release Screen Opened");
    }

    private void initialize() {
        Item<Integer> row;
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
                complete.setEnabled(isState(DISPLAY));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                release.setEnabled(isState(DISPLAY));
            }
        });

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
                optionsMenu.setEnabled(isState(DEFAULT, DISPLAY));
                optionsButton.setEnabled(isState(DEFAULT, DISPLAY));
                historyMenu.setEnabled(isState(DISPLAY));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                unreleaseAnalysis.setEnabled(isState(DISPLAY));
            }
        });

        unreleaseAnalysis.addCommand(new Command() {
            @Override
            public void execute() {
                unrelease();
            }
        });

        addDataChangeHandler(new DataChangeEvent.Handler() {
            public void onDataChange(DataChangeEvent event) {
                previewFinalReport();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                previewFinalReport.setEnabled(true);
            }
        });

        previewFinalReport.addCommand(new Command() {
            @Override
            public void execute() {
                previewFinalReport();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                queryByWorksheet.setEnabled(isState(DISPLAY, DEFAULT));
            }
        });

        queryByWorksheet.addCommand(new Command() {
            @Override
            public void execute() {
                queryByWorksheet();
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
                historySampleSpecific.setEnabled(isState(DISPLAY));
            }
        });

        historySampleSpecific.addCommand(new Command() {
            @Override
            public void execute() {
                String domain;

                /*
                 * show the history for the sample's domain
                 */
                domain = manager.getSample().getDomain();
                if (Constants.domain().ENVIRONMENTAL.equals(domain))
                    SampleHistoryUtility1.environmental(manager);
                else if (Constants.domain().PRIVATEWELL.equals(domain))
                    SampleHistoryUtility1.privateWell(manager);
                else if (Constants.domain().SDWIS.equals(domain))
                    SampleHistoryUtility1.sdwis(manager);
                else if (Constants.domain().NEONATAL.equals(domain))
                    SampleHistoryUtility1.neonatal(manager);
                else if (Constants.domain().CLINICAL.equals(domain))
                    SampleHistoryUtility1.clinical(manager);
                else if (Constants.domain().PT.equals(domain))
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
                UUID data;
                AnalysisViewDO ana;

                /*
                 * Show the history of the results of the analysis selected in
                 * the table.
                 */
                data = table.getRowAt(table.getSelectedRow()).getData();
                ana = (AnalysisViewDO)manager.getObject(data.analysisUid);
                SampleHistoryUtility1.currentResult(manager, ana.getId());
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

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                details.setEnabled(isState(DISPLAY));
            }
        });

        addScreenHandler(table, "table", new ScreenHandler<Item<Integer>>() {
            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(true);
                table.setAllowMultipleSelection(isState(DISPLAY));
            }
        });

        table.addUnselectionHandler(new UnselectionHandler<Integer>() {
            public void onUnselection(UnselectionEvent<Integer> event) {
                /*
                 * the selected row is not allowed to be unselected in Update
                 * state because its manager is locked
                 */
                if (isState(UPDATE))
                    event.cancel();
            }
        });

        table.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                /*
                 * no other row is allowed to be selected in Update state
                 * because the selected row's manager is locked
                 */
                if (isState(UPDATE))
                    event.cancel();
            }
        });

        table.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                UUID data;
                Integer rows[];

                rows = table.getSelectedRows();
                /*
                 * show no tabs if multiple rows are selected, otherwise show
                 * and load the tabs for the selected sample
                 */
                if (rows.length > 1) {
                    data = null;
                    manager = null;
                } else {
                    data = (UUID)table.getRowAt(rows[0]).getData();
                    manager = managers.get(data.sampleId);
                }

                setData();
                refreshTabs(data, false);
            }
        });

        table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
            public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                event.cancel();
            }
        });

        tabPanel.setPopoutBrowser(OpenELIS.getBrowser());

        addScreenHandler(sampleTab, "sampleTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                sampleTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                sampleTab.setState(event.getState());
            }

            public Object getQuery() {
                return sampleTab.getQueryFields();
            }

            public void isValid(Validation validation) {
                super.isValid(validation);
                if (sampleTab.getIsBusy())
                    validation.setStatus(FLAGGED);
            }
        });

        addScreenHandler(environmentalTab, "environmentalTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                environmentalTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                environmentalTab.setState(event.getState());
            }

            public Object getQuery() {
                return null;
            }
        });

        /*
         * querying by this tab is not allowed on this screen
         */
        environmentalTab.setCanQuery(false);

        addScreenHandler(privateWellTab, "privateWellTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                privateWellTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                privateWellTab.setState(event.getState());
            }

            public Object getQuery() {
                return null;
            }
        });

        /*
         * querying by this tab is not allowed on this screen
         */
        privateWellTab.setCanQuery(false);

        addScreenHandler(sdwisTab, "sdwisTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                sdwisTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                sdwisTab.setState(event.getState());
            }

            public Object getQuery() {
                return null;
            }
        });

        /*
         * querying by this tab is not allowed on this screen
         */
        sdwisTab.setCanQuery(false);

        addScreenHandler(neonatalTab, "neonatalTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                neonatalTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                neonatalTab.setState(event.getState());
            }

            public Object getQuery() {
                return null;
            }
        });

        addScreenHandler(clinicalTab, "clinicalTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                clinicalTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                clinicalTab.setState(event.getState());
            }

            public Object getQuery() {
                return null;
            }

            public void isValid(Validation validation) {
                if (manager != null && manager.getSampleClinical() != null) {
                    super.isValid(validation);
                    if (clinicalTab.getIsBusy())
                        validation.setStatus(FLAGGED);
                }
            }
        });

        /*
         * querying by this tab is not allowed on this screen
         */
        clinicalTab.setCanQuery(false);

        addScreenHandler(ptTab, "ptTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                ptTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                ptTab.setState(event.getState());
            }

            public Object getQuery() {
                return null;
            }
        });

        /*
         * querying by this tab is not allowed on this screen
         */
        ptTab.setCanQuery(false);

        addScreenHandler(quickEntryTab, "quickEntryTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                quickEntryTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                quickEntryTab.setState(event.getState());
            }

            public Object getQuery() {
                return null;
            }
        });

        addScreenHandler(sampleItemTab, "sampleItemTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                /*
                 * the tab is refreshed when a node in the table is selected
                 */
            }

            public void onStateChange(StateChangeEvent event) {
                sampleItemTab.setState(event.getState());
            }

            public Object getQuery() {
                return null;
            }
        });

        /*
         * querying by this tab is not allowed on this screen
         */
        sampleItemTab.setCanQuery(false);

        addScreenHandler(analysisTab, "analysisTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                /*
                 * the tab is refreshed when a node in the table is selected
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
                 * the tab is refreshed when a node in the table is selected
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
                 * the tab is refreshed when a node in the table is selected
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
                 * the tab is refreshed when a node in the table is selected
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
                 * the tab is refreshed when a node in the table is selected
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
                return null;
            }
        });

        /*
         * querying by this tab is not allowed on this screen
         */
        auxDataTab.setCanQuery(false);

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
         * querying by this tab is not allowed on this screen
         */
        attachmentTab.setCanQuery(false);

        /*
         * handlers for the events fired by the tabs
         */

        bus.addHandler(AccessionChangeEvent.getType(), new AccessionChangeEvent.Handler() {
            @Override
            public void onAccessionChange(AccessionChangeEvent event) {
                if (screen != event.getSource())
                    changeAccession(event.getAccession());
            }
        });

        bus.addHandler(PatientLockEvent.getType(), new PatientLockEvent.Handler() {
            @Override
            public void onPatientLock(PatientLockEvent event) {
                PatientDO data;

                if (screen == event.getSource())
                    return;

                try {
                    if (event.getAction() == PatientLockEvent.Action.LOCK)
                        data = PatientService.get().fetchForUpdate(event.getPatient().getId());
                    else
                        data = PatientService.get().abortUpdate(event.getPatient().getId());

                    bus.fireEventFromSource(new PatientLockEvent(data, event.getAction()), screen);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        });

        bus.addHandler(AddTestEvent.getType(), new AddTestEvent.Handler() {
            @Override
            public void onAddTest(AddTestEvent event) {
                if (screen != event.getSource())
                    addAnalyses(event.getTests());
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
                    setError(Messages.get().gen_mustCommitOrAbort());
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

        sampleStatus.setModel(model);

        model = new ArrayList<Item<Integer>>();
        analysisStatuses = new HashMap<Integer, String>();
        for (DictionaryDO d : CategoryCache.getBySystemName("analysis_status")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
            analysisStatuses.put(d.getId(), d.getEntry());
        }

        analysisStatus.setModel(model);
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
        managers = null;
        showTabs(Tabs.SAMPLE, Tabs.ANALYSIS);
        setData();
        setState(QUERY);
        table.setModel(getTableModel(null));
        fireDataChange();
        bus.fireEvent(new org.openelis.modules.sample1.client.SelectionEvent(SelectedType.NONE,
                                                                             null));
        setDone(Messages.get().gen_enterFieldsToQuery());
        lastAccession = null;
    }

    /**
     * Puts the screen in update state and loads the tabs with a locked manager.
     * Builds the cache from the manager.
     */
    @UiHandler("update")
    protected void update(ClickEvent event) {
        if (table.getSelectedRows().length > 1) {
            window.setError(Messages.get().completeRelease_cantUpdateMultiple());
            return;
        }

        setBusy(Messages.get().gen_lockForUpdate());

        if (fetchForUpdateCall == null) {
            fetchForUpdateCall = new AsyncCallbackUI<SampleManager1>() {
                public void success(SampleManager1 result) {
                    UUID data;

                    manager = result;
                    managers.put(manager.getSample().getId(), manager);
                    refreshRows(manager.getSample().getId());
                    buildCache();
                    setData();
                    setState(UPDATE);
                    data = table.getRowAt(table.getSelectedRow()).getData();
                    refreshTabs(data, true);
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

        /*
         * for update, the results for all analyses is fetched and not just for
         * the once that were returned by the initial query
         */
        SampleService1.get().fetchForUpdate(manager.getSample().getId(),
                                            resultElements,
                                            fetchForUpdateCall);
    }

    /**
     * Completes the analyses selected in the table
     */
    @UiHandler("complete")
    protected void complete(ClickEvent event) {
        int i;
        Integer selRows[];
        String completed, onHold;
        UUID data;
        AnalysisViewDO ana;
        SampleManager1 sm;
        ArrayList<Exception> errors;
        HashMap<Integer, Boolean> selectedSams;

        /*
         * warn the user that multiple analyses are being completed
         */
        selRows = table.getSelectedRows();
        if (selRows.length > 1 &&
            !Window.confirm(Messages.get().completeRelease_completeMultipleWarning(selRows.length)))
            return;

        /*
         * lock and refetch the samples
         */
        setBusy(Messages.get().gen_updating());
        try {
            fetchSelectedSamplesForUpdate();
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            clearStatus();
            return;
        }

        /*
         * complete the selected analyses
         */
        selRows = table.getSelectedRows();
        selectedSams = new HashMap<Integer, Boolean>();
        completed = analysisStatuses.get(Constants.dictionary().ANALYSIS_COMPLETED);
        onHold = analysisStatuses.get(Constants.dictionary().ANALYSIS_ON_HOLD);
        errors = new ArrayList<Exception>();
        for (i = 0; i < selRows.length; i++ ) {
            data = table.getRowAt(selRows[i]).getData();
            sm = managers.get(data.sampleId);
            /*
             * for each selected sample, set the flag that determines whether it
             * should be updated or unlocked; true means update
             */
            if (selectedSams.get(data.sampleId) == null)
                selectedSams.put(data.sampleId, false);

            ana = (AnalysisViewDO)sm.getObject(data.analysisUid);
            if ( !Constants.dictionary().ANALYSIS_COMPLETED.equals(ana.getStatusId()) &&
                !Constants.dictionary().ANALYSIS_ON_HOLD.equals(ana.getStatusId()) &&
                !Constants.dictionary().ANALYSIS_INITIATED.equals(ana.getStatusId()) &&
                !Constants.dictionary().ANALYSIS_LOGGED_IN.equals(ana.getStatusId())) {
                Window.alert(Messages.get()
                                     .analysis_cantChangeStatusException(sm.getSample()
                                                                           .getAccessionNumber(),
                                                                         ana.getTestName(),
                                                                         ana.getMethodName(),
                                                                         analysisStatuses.get(ana.getStatusId()),
                                                                         completed));
                continue;
            }

            if (Constants.dictionary().ANALYSIS_ON_HOLD.equals(ana.getStatusId()) &&
                !Window.confirm(Messages.get().completeRelease_onHoldWarning(onHold)))
                continue;

            try {
                sm = SampleService1.get()
                                   .changeAnalysisStatus(sm,
                                                         ana.getId(),
                                                         Constants.dictionary().ANALYSIS_COMPLETED);
                managers.put(data.sampleId, sm);
                refreshRow(selRows[i]);
                /*
                 * this sample needs to be updated because at least one of its
                 * analyses was successfully completed
                 */
                selectedSams.put(data.sampleId, true);
            } catch (ValidationErrorsList e) {
                errors.addAll(e.getErrorList());
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }

        updateAndRefreshScreen(selectedSams, errors);
    }

    /**
     * Releases the analyses selected in the table
     */
    @UiHandler("release")
    protected void release(ClickEvent event) {
        int i;
        Integer selRows[];
        UUID data;
        AnalysisViewDO ana;
        SampleManager1 sm;
        ArrayList<Exception> errors;
        HashMap<Integer, Boolean> selectedSams;

        /*
         * warn the user that multiple analyses are being released
         */
        selRows = table.getSelectedRows();
        if (selRows.length > 1 &&
            !Window.confirm(Messages.get().completeRelease_releaseMultipleWarning(selRows.length)))
            return;

        setBusy(Messages.get().gen_lockForUpdate());
        /*
         * lock and refetch the samples
         */
        try {
            fetchSelectedSamplesForUpdate();
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, DataBaseUtil.toString(e.getMessage()), e);
            clearStatus();
            return;
        }

        /*
         * release the selected analyses
         */
        selectedSams = new HashMap<Integer, Boolean>();
        errors = new ArrayList<Exception>();
        for (i = 0; i < selRows.length; i++ ) {
            data = table.getRowAt(selRows[i]).getData();
            sm = managers.get(data.sampleId);
            /*
             * for each selected sample, set the flag that determines whether it
             * should be updated or unlocked; true means update
             */
            if (selectedSams.get(data.sampleId) == null)
                selectedSams.put(data.sampleId, false);

            ana = (AnalysisViewDO)sm.getObject(data.analysisUid);
            if (Constants.dictionary().ANALYSIS_RELEASED.equals(ana.getStatusId())) {
                Window.alert(Messages.get()
                                     .completeRelease_alreadyReleased(sm.getSample()
                                                                        .getAccessionNumber(),
                                                                      ana.getTestName(),
                                                                      ana.getMethodName()));
                continue;
            } else if ( !Constants.dictionary().ANALYSIS_COMPLETED.equals(ana.getStatusId())) {
                Window.alert(Messages.get()
                                     .analysis_completeStatusRequiredToRelease(sm.getSample()
                                                                                 .getAccessionNumber(),
                                                                               ana.getTestName(),
                                                                               ana.getMethodName()));
                continue;
            }

            try {
                sm = SampleService1.get()
                                   .changeAnalysisStatus(sm,
                                                         ana.getId(),
                                                         Constants.dictionary().ANALYSIS_RELEASED);
                managers.put(data.sampleId, sm);
                refreshRow(selRows[i]);
                /*
                 * this sample needs to be updated because at least on of its
                 * analyses was successfully released
                 */
                selectedSams.put(data.sampleId, true);
            } catch (ValidationErrorsList e) {
                errors.addAll(e.getErrorList());
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }

        updateAndRefreshScreen(selectedSams, errors);
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
            case UPDATE:
                commitUpdate(false);
                break;
        }
    }

    /**
     * creates query fields from the data on the screen and calls the service
     * method for executing a query to return a list of samples.
     */
    protected void commitQuery() {
        String domain;
        Query query;
        QueryData field;
        ArrayList<QueryData> fields;

        /*
         * can't execute an empty query
         */
        fields = getQueryFields();
        if (fields.size() == 0) {
            setError(Messages.get().gen_emptyQueryException());
            return;
        }

        domain = null;
        /*
         * if a send-out order's id matches an eorder's id then querying by
         * send-out order id can return samples linked to the eorder and
         * querying by paper order validator (POV) can return samples linked to
         * the send-out order; this makes sure that only samples of the
         * appropriate domain are returned on querying by either field
         */
        for (QueryData f : fields) {
            if (SampleMeta.getOrderId().equals(f.getKey())) {
                domain = getDomainQuery(Constants.domain().ENVIRONMENTAL,
                                        Constants.domain().SDWIS,
                                        Constants.domain().PRIVATEWELL,
                                        Constants.domain().PT);
                break;
            } else if (SampleMeta.getEorderPaperOrderValidator().equals(f.getKey())) {
                domain = getDomainQuery(Constants.domain().CLINICAL, Constants.domain().NEONATAL);
                break;
            }
        }

        /*
         * make sure that only the samples belonging to the domain queried by
         * are returned by the query
         */
        if (domain != null) {
            field = new QueryData();
            field.setKey(SampleMeta.getDomain());
            field.setQuery(domain);
            field.setType(QueryData.Type.STRING);
            fields.add(field);
        }

        query = new Query();
        query.setFields(fields);
        executeQuery(query);
        cache = null;
    }

    /**
     * Commits the data on the screen to the database. Shows any errors/warnings
     * encountered during the commit, otherwise loads the screen with the
     * committed data.
     */
    protected void commitUpdate(final boolean ignoreWarning) {
        Integer accession;
        String prefix;
        PatientDO data;
        ValidationErrorsList e1;

        setBusy(Messages.get().gen_updating());

        /*
         * add/update patient(s)
         */
        if (manager.getSampleClinical() != null) {
            data = manager.getSampleClinical().getPatient();

            try {
                /*
                 * add the patient if it's a new one; otherwise update it
                 * because it may be locked
                 */
                if (data.getId() == null) {
                    PatientService.get().validate(data);
                    data = PatientService.get().add(data);
                    manager.getSampleClinical().setPatientId(data.getId());
                    manager.getSampleClinical().setPatient(data);
                } else {
                    PatientService.get().validate(data);
                    data = PatientService.get().update(data);
                    manager.getSampleClinical().setPatient(data);
                    bus.fireEventFromSource(new PatientLockEvent(data,
                                                                 PatientLockEvent.Action.UNLOCK),
                                            this);
                }
            } catch (ValidationErrorsList e) {
                /*
                 * for display
                 */
                accession = manager.getSample().getAccessionNumber();
                if (accession == null)
                    accession = 0;

                /*
                 * new FormErrorExceptions are created to prepend accession
                 * number to the messages of FormErrorExceptions returned by
                 * patient validation; other exceptions are shown as is
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
                Window.alert("commitUpdate(): " + e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
                clearStatus();
                return;
            }
        }

        if (commitUpdateCall == null) {
            commitUpdateCall = new AsyncCallbackUI<SampleManager1>() {
                public void success(SampleManager1 result) {
                    UUID data;

                    manager = result;
                    managers.put(manager.getSample().getId(), manager);
                    refreshRows(manager.getSample().getId());

                    setData();
                    setState(DISPLAY);
                    lastAccession = null;
                    data = table.getRowAt(table.getSelectedRow()).getData();
                    refreshTabs(data, true);
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
     * of the widgets. Unlocks the sample if it was locked and refreshes the
     * screen with its data.
     */
    @UiHandler("abort")
    protected void abort(ClickEvent event) {
        PatientDO data;

        finishEditing();
        clearErrors();
        setBusy(Messages.get().gen_cancelChanges());

        if (isState(QUERY)) {
            manager = null;
            showTabs(Tabs.BLANK);
            setData();
            setState(DEFAULT);
            table.setModel(getTableModel(null));
            fireDataChange();
            bus.fireEvent(new org.openelis.modules.sample1.client.SelectionEvent(SelectedType.NONE,
                                                                                 null));
            setDone(Messages.get().gen_queryAborted());
        } else if (isState(UPDATE)) {
            /*
             * unlock any locked or changed patient
             */
            if (manager.getSampleClinical() != null &&
                manager.getSampleClinical().getPatientId() != null) {
                try {
                    data = PatientService.get().abortUpdate(manager.getSampleClinical()
                                                                   .getPatientId());
                    bus.fireEventFromSource(new PatientLockEvent(data,
                                                                 PatientLockEvent.Action.UNLOCK),
                                            this);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    clearStatus();
                    return;
                }
            }

            if (unlockCall == null) {
                unlockCall = new AsyncCallbackUI<ArrayList<SampleManager1>>() {
                    public void success(ArrayList<SampleManager1> result) {
                        UUID data;

                        manager = result.get(0);
                        managers.put(manager.getSample().getId(), manager);
                        refreshRows(manager.getSample().getId());
                        data = table.getRowAt(table.getSelectedRow()).getData();

                        setData();
                        setState(DISPLAY);
                        refreshTabs(data, true);

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

            SampleService1.get().unlockByAnalyses(getAnalyses(manager.getSample().getId()),
                                                  singleResultElements,
                                                  unlockCall);
        }
    }

    /**
     * unreleases the analysis selected in the table
     */
    protected void unrelease() {
        UUID data;
        AnalysisViewDO ana;
        SampleManager1 sm;
        HashMap<Integer, Boolean> selectedSams;

        if (table.getSelectedRows().length != 1) {
            Window.alert(Messages.get().completeRelease_selOneRowUnrelease());
            return;
        }

        /*
         * lock and refetch the samples
         */
        setBusy(Messages.get().gen_updating());
        try {
            fetchSelectedSamplesForUpdate();
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            clearStatus();
            return;
        }

        data = table.getRowAt(table.getSelectedRow()).getData();
        sm = managers.get(data.sampleId);
        ana = (AnalysisViewDO)sm.getObject(data.analysisUid);
        if ( !Constants.dictionary().ANALYSIS_RELEASED.equals(ana.getStatusId())) {
            Window.alert(Messages.get()
                                 .sample_wrongStatusUnrelease(analysisStatuses.get(Constants.dictionary().ANALYSIS_RELEASED)));

            selectedSams = new HashMap<Integer, Boolean>();
            /*
             * since the analysis can't be unreleased, unlock the selected
             * sample and refresh the screen
             */
            selectedSams.put(data.sampleId, false);
            updateAndRefreshScreen(selectedSams, null);
        } else {
            /*
             * inform the user that an internal note needs to be added to
             * unrelease the analysis; the analysis will only be unreleased if
             * the user adds the note, which is handled by the confirm window
             */
            showConfirm();
        }
    }

    /**
     * shows the final report for the selected row's sample in preview mode
     */
    protected void previewFinalReport() {
        Query query;
        QueryData field;

        if ( !previewFinalReport.isChecked()) {
            /*
             * this allows previewing the final report of a sample just by
             * unchecking and checking the checkbox and not having to click the
             * row for a different sample
             */
            lastAccession = null;
            return;
        }

        if ( !isState(DISPLAY) || table.getSelectedRows().length != 1 || manager == null ||
            manager.getSample().getAccessionNumber().equals(lastAccession))
            return;

        lastAccession = manager.getSample().getAccessionNumber();
        query = new Query();
        field = new QueryData();
        field.setKey("ACCESSION_NUMBER");
        field.setQuery(lastAccession.toString());
        field.setType(QueryData.Type.INTEGER);
        query.setFields(field);

        setBusy(Messages.get().gen_generatingReport());

        FinalReportService.get().runReportForPreview(query, new AsyncCallback<ReportStatus>() {
            public void onSuccess(ReportStatus status) {
                String url;

                if (ReportStatus.Status.SAVED.equals(status.getStatus())) {
                    url = "/openelis/openelis/report?file=" + status.getMessage();
                    Window.open(URL.encode(url), "FinalReport", null);
                    setDone(Messages.get().gen_loadCompleteMessage());
                } else {
                    setDone(status.getMessage());
                }
            }

            public void onFailure(Throwable e) {
                setError("Failed");
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        });
    }

    /**
     * fetches the analyses linked to the worksheet specified by the user
     */
    protected void queryByWorksheet() {
        ModalWindow modal;

        if (queryByWorksheetLookup == null) {
            queryByWorksheetLookup = new QueryByWorksheetLookupUI() {
                @Override
                public void ok() {
                    queryByWorksheet(queryByWorksheetLookup.getWorksheetId());
                }

                @Override
                public void cancel() {
                    // ignore
                }
            };
        }

        modal = new ModalWindow();
        modal.setSize("225px", "125px");
        modal.setName(Messages.get().completeRelease_queryByWorksheet());
        modal.setCSS(UIResources.INSTANCE.popupWindow());
        modal.setContent(queryByWorksheetLookup);
        queryByWorksheetLookup.setWindow(modal);
        queryByWorksheetLookup.setData();
    }

    /**
     * shows the selected sample on Tracking screen
     */
    @UiHandler("details")
    protected void details(ClickEvent event) {
        org.openelis.ui.widget.Window window;
        final SampleTrackingScreenUI trackingScreen;
        ScheduledCommand cmd;

        try {
            window = new org.openelis.ui.widget.Window();
            window.setName(Messages.get().sampleTracking_tracking());
            window.setSize("1074px", "435px");
            trackingScreen = new SampleTrackingScreenUI(window);
            window.setContent(trackingScreen);
            OpenELIS.getBrowser().addWindow(window, "tracking");
            cmd = new ScheduledCommand() {
                @Override
                public void execute() {
                    trackingScreen.query(getSelectedSamples());
                }
            };
            Scheduler.get().scheduleDeferred(cmd);
        } catch (Throwable e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * validates the screen and sets the status of validation to "Flagged" if
     * some operation needs to be completed before committing
     */
    public Validation validate() {
        Validation validation;

        validation = super.validate();
        if (isBusy)
            validation.setStatus(FLAGGED);

        return validation;
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
     * sets the latest manager in the tabs
     */
    private void setData() {
        sampleTab.setData(manager);
        environmentalTab.setData(manager);
        privateWellTab.setData(manager);
        sdwisTab.setData(manager);
        neonatalTab.setData(manager);
        clinicalTab.setData(manager);
        ptTab.setData(manager);
        quickEntryTab.setData(manager);
        sampleItemTab.setData(manager);
        analysisTab.setData(manager);
        resultTab.setData(manager);
        analysisNotesTab.setData(manager);
        sampleNotesTab.setData(manager);
        storageTab.setData(manager);
        qaEventTab.setData(manager);
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
     * creates a string containing, the message that there are warnings on the
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
     * Adds the scriptlets for the domain and for all the records in the manager
     * to the scriptlet runner
     */
    private void addScriptlets() throws Exception {
        Integer id;

        if (scriptletRunner == null)
            scriptletRunner = new ScriptletRunner<SampleSO>();

        /*
         * add the scriptlet for the domain
         */
        id = getDomainScriptlet();
        if (id != null)
            addScriptlet(id, null);

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
        Row selRow;
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
        managers.put(manager.getSample().getId(), manager);
        setData();

        if (data.getChangedUids() == null)
            return;
        /*
         * go through the changed uids and fire appropriate events to refresh
         * particular parts of the screen
         */
        selRow = table.getRowAt(table.getSelectedRow());
        selUid = ((UUID)selRow.getData()).analysisUid;
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
                 * if analysis qa events were added and if any of them belong to
                 * the analysis selected in the tree then refresh the qa event
                 * tab, otherwise don't
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
     * Runs the scriptlet for the neonatal domain
     */
    private void runDomainScriptlet(Action_Before operation) throws Exception {
        Integer id;

        id = getDomainScriptlet();
        if (id != null)
            runScriptlets(null, null, operation);
    }

    /**
     * Returns the id of the scriptlet for the selected sample's domain; returns
     * null if a scriptlet is not defined for the domain
     */
    private Integer getDomainScriptlet() throws Exception {
        SystemVariableDO data;

        data = null;
        /*
         * add the scriptlet for the domain, which is the value of this system
         * variable; don't try to look up the system variable again if it's not
         * found the first time because the scriptlet is optional
         */
        if (Constants.domain().ENVIRONMENTAL.equals(manager.getSample().getDomain())) {
            if (hasEnvScriptlet) {
                try {
                    data = SystemVariableService.get()
                                                .fetchByExactName(ENV_SCRIPTLET_SYSTEM_VARIABLE);
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }

                /*
                 * if the system variable was found, its value must point to an
                 * existing dictionary entry; so if an exception is thrown on
                 * trying to look up the dictionary, the user must be informed
                 * of it even if it's a NotFoundException
                 */
                if (data != null) {
                    try {
                        envScriptletId = DictionaryCache.getIdBySystemName(data.getValue());
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                    }
                }
                hasEnvScriptlet = false;
            }
            return envScriptletId;
        } else if (Constants.domain().SDWIS.equals(manager.getSample().getDomain())) {
            if (hasSDWISScriptlet) {
                try {
                    data = SystemVariableService.get()
                                                .fetchByExactName(SDWIS_SCRIPTLET_SYSTEM_VARIABLE);
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }

                /*
                 * if the system variable was found, its value must point to an
                 * existing dictionary entry; so if an exception is thrown on
                 * trying to look up the dictionary, the user must be informed
                 * of it even if it's a NotFoundException
                 */
                if (data != null) {
                    try {
                        sdwisScriptletId = DictionaryCache.getIdBySystemName(data.getValue());
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                    }
                }
                hasSDWISScriptlet = false;
            }
            return sdwisScriptletId;
        } else if (Constants.domain().NEONATAL.equals(manager.getSample().getDomain())) {
            if (hasNeonatalScriptlet) {
                try {
                    data = SystemVariableService.get()
                                                .fetchByExactName(NEO_SCRIPTLET_SYSTEM_VARIABLE);
                } catch (NotFoundException e) {
                    // ignore
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }

                /*
                 * if the system variable was found, its value must point to an
                 * existing dictionary entry; so if an exception is thrown on
                 * trying to look up the dictionary, the user must be informed
                 * of it even if it's a NotFoundException
                 */
                if (data != null) {
                    try {
                        neonatalScriptletId = DictionaryCache.getIdBySystemName(data.getValue());
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                    }
                }
                hasNeonatalScriptlet = false;
            }
            return neonatalScriptletId;
        }

        return null;
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
     * Returns a string containing all passed domains, separated by the wild
     * card character for "OR"
     */
    private String getDomainQuery(String... domains) {
        return DataBaseUtil.concatWithSeparator(Arrays.asList(domains), "|");
    }

    /**
     * executes the passed query and loads the screen with the results
     */
    private void executeQuery(final Query query) {
        setBusy(Messages.get().gen_querying());

        query.setRowsPerPage(500);
        SampleService1.get().fetchByAnalysisQuery(query, singleResultElements, getQueryCallBack());
    }

    /**
     * returns the callback that handles loading the screen with the results of
     * the query executed by the user
     */
    private AsyncCallbackUI<ArrayList<SampleManager1>> getQueryCallBack() {
        if (queryCall == null) {
            queryCall = new AsyncCallbackUI<ArrayList<SampleManager1>>() {
                public void success(ArrayList<SampleManager1> result) {
                    UUID data;
                    ArrayList<Row> model;

                    /*
                     * this map is used to link a table row with the manager
                     * containing the sample and analysis that it's showing
                     */
                    managers = new HashMap<Integer, SampleManager1>();
                    for (SampleManager1 sm : result)
                        managers.put(sm.getSample().getId(), sm);

                    model = getTableModel(result);
                    /*
                     * sorting the model before loading it into the table is
                     * more efficient than loading the table and then sorting it
                     * one column at a time
                     */
                    Collections.sort(model, new RowComparator());
                    table.setModel(model);
                    table.selectRowAt(0);

                    data = table.getRowAt(0).getData();
                    manager = managers.get(data.sampleId);

                    setData();
                    setState(DISPLAY);
                    lastAccession = null;
                    refreshTabs(data, false);
                    clearStatus();
                }

                public void notFound() {
                    noRecordsFound();
                }

                public void failure(Throwable e) {
                    Window.alert("Error: Complete Release call query failed; " + e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    setError(Messages.get().gen_queryFailed());
                }
            };
        }

        return queryCall;
    }

    /**
     * empties the table and tabs and shows the message for no records found
     */
    private void noRecordsFound() {
        manager = null;
        showTabs(Tabs.BLANK);
        setData();
        setState(DEFAULT);
        table.setModel(getTableModel(null));
        fireDataChange();
        bus.fireEvent(new org.openelis.modules.sample1.client.SelectionEvent(SelectedType.NONE,
                                                                             null));
        setDone(Messages.get().gen_noRecordsFound());
    }

    /**
     * creates a model for the table at the top from the data in the managers
     */
    private ArrayList<Row> getTableModel(ArrayList<SampleManager1> sms) {
        int i, j;
        SampleItemViewDO item;
        AnalysisViewDO ana;
        Row row;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        if (sms == null)
            return model;

        /*
         * only the analyses with results are shown because they were returned
         * by the screen's query
         */
        for (SampleManager1 sm : sms) {
            for (i = 0; i < sm.item.count(); i++ ) {
                item = sm.item.get(i);
                for (j = 0; j < sm.analysis.count(item); j++ ) {
                    ana = sm.analysis.get(item, j);
                    if (sm.result.count(ana) == 0)
                        continue;
                    row = new Row(5);
                    row.setCell(0, sm.getSample().getAccessionNumber());
                    row.setCell(1, ana.getTestName());
                    row.setCell(2, ana.getMethodName());
                    row.setCell(3, ana.getStatusId());
                    row.setCell(4, sm.getSample().getStatusId());
                    row.setData(new UUID(Constants.uid().get(ana), sm.getSample().getId()));
                    model.add(row);
                }
            }
        }

        return model;
    }

    /**
     * If the UUID is not null then reloads the tabs with the manager and
     * analysis specified by it and updates the notifications on the tab
     * headers; otherwise shows the blank tab and empties the other tabs. Forces
     * the result tab to refresh itself if forceReloadResults is true.
     */
    private void refreshTabs(UUID data, boolean forceReloadResults) {
        String uid, domain;
        Tabs domainTab;
        SelectedType type;
        AnalysisViewDO ana;
        SampleManager1 sm;

        if (data == null) {
            uid = null;
            type = SelectedType.NONE;
            showTabs(Tabs.BLANK);
        } else {
            sm = managers.get(data.sampleId);
            uid = data.analysisUid;
            type = SelectedType.ANALYSIS;
            /*
             * find out which domain's tab is to be shown
             */
            domain = sm.getSample().getDomain();
            domainTab = null;
            if (Constants.domain().ENVIRONMENTAL.equals(domain))
                domainTab = Tabs.ENVIRONMENTAL;
            else if (Constants.domain().PRIVATEWELL.equals(domain))
                domainTab = Tabs.PRIVATE_WELL;
            else if (Constants.domain().SDWIS.equals(domain))
                domainTab = Tabs.SDWIS;
            else if (Constants.domain().NEONATAL.equals(domain))
                domainTab = Tabs.NEONATAL;
            else if (Constants.domain().CLINICAL.equals(domain))
                domainTab = Tabs.CLINICAL;
            else if (Constants.domain().PT.equals(domain))
                domainTab = Tabs.PT;
            else if (Constants.domain().QUICKENTRY.equals(domain))
                domainTab = Tabs.QUICK_ENTRY;

            showTabs(Tabs.SAMPLE,
                     domainTab,
                     Tabs.SAMPLE_ITEM,
                     Tabs.ANALYSIS,
                     Tabs.TEST_RESULT,
                     Tabs.ANALYSIS_NOTES,
                     Tabs.SAMPLE_NOTES,
                     Tabs.STORAGE,
                     Tabs.QA_EVENTS,
                     Tabs.AUX_DATA,
                     Tabs.ATTACHMENT);

            /*
             * show notifications on the header of the tabs
             */
            ana = (AnalysisViewDO)sm.getObject(uid);
            setTabNotification(Tabs.ANALYSIS_NOTES, sm, ana);
            setTabNotification(Tabs.SAMPLE_NOTES, sm, ana);
            setTabNotification(Tabs.STORAGE, sm, ana);
            setTabNotification(Tabs.QA_EVENTS, sm, ana);
            setTabNotification(Tabs.AUX_DATA, sm, ana);
            setTabNotification(Tabs.ATTACHMENT, sm, ana);
        }

        fireDataChange();
        bus.fireEvent(new org.openelis.modules.sample1.client.SelectionEvent(type, uid));
        /*
         * This is done when SelectionEvent isn't sufficient to refresh the
         * result tab, because SelectionEvent makes the tab compare the uids of
         * the previous and current analysis and not the new and old results.
         * The results can get changed when the manager is reloaded from the
         * database e.g. on going in Update state or clicking Abort.
         */
        if (forceReloadResults)
            bus.fireEvent(new ResultChangeEvent(uid));
    }

    /**
     * makes the tabs specified in the argument visible and the others not
     * visible; selects the first visible tab if no tab is already selected, to
     * show its widgets
     */
    private void showTabs(Tabs... tabs) {
        EnumSet<Tabs> el;

        el = EnumSet.copyOf(Arrays.asList(tabs));

        for (Tabs tab : Tabs.values())
            tabPanel.setTabVisible(tab.ordinal(), el.contains(tab));

        if (tabs[0] != Tabs.BLANK && tabPanel.getSelectedIndex() < 0)
            tabPanel.selectTab(tabs[0].ordinal());
    }

    /**
     * creates and sets notification, e.g. number of records, on the header of
     * the specified tab
     */
    private void setTabNotification(Tabs tabs, SampleManager1 sm, AnalysisViewDO ana) {
        int count1, count2;
        String label;

        label = null;

        switch (tabs) {
            case ANALYSIS_NOTES:
                count1 = sm.analysisExternalNote.get(ana) == null ? 0 : 1;
                count2 = sm.analysisInternalNote.count(ana);
                if (count1 > 0 || count2 > 0)
                    label = DataBaseUtil.concatWithSeparator(count1, " - ", count2);
                break;
            case SAMPLE_NOTES:
                count1 = sm.sampleExternalNote.get() == null ? 0 : 1;
                count2 = sm.sampleInternalNote.count();
                if (count1 > 0 || count2 > 0)
                    label = DataBaseUtil.concatWithSeparator(count1, " - ", count2);
                break;
            case STORAGE:
                count1 = sm.storage.count(ana);
                if (count1 > 0)
                    label = String.valueOf(count1);
                break;
            case QA_EVENTS:
                count1 = sm.qaEvent.count();
                count2 = sm.qaEvent.count(ana);
                if (count1 > 0 || count2 > 0)
                    label = DataBaseUtil.concatWithSeparator(count1, " - ", count2);
                break;
            case AUX_DATA:
                count1 = sm.auxData.count();
                if (count1 > 0)
                    label = String.valueOf(count1);
                break;
            case ATTACHMENT:
                count1 = sm.attachment.count();
                if (count1 > 0)
                    label = String.valueOf(count1);
                break;
        }

        tabPanel.setTabNotification(tabs.ordinal(), label);
    }

    /**
     * Refreshes all rows showing the analyses of the sample with this id
     */
    private void refreshRows(Integer sampleId) {
        boolean found;
        UUID data;

        found = false;
        for (int i = 0; i < table.getRowCount(); i++ ) {
            data = table.getRowAt(i).getData();
            if ( !data.sampleId.equals(sampleId)) {
                if ( !found)
                    continue;
                /*
                 * the previous row was the last one belonging to the sample
                 */
                break;
            }
            found = true;
            refreshRow(i);
        }
    }

    /**
     * refreshes the display of the row at this index
     */
    private void refreshRow(int index) {
        AnalysisViewDO ana;
        SampleManager1 sm;
        UUID data;

        data = table.getRowAt(index).getData();
        sm = managers.get(data.sampleId);
        ana = (AnalysisViewDO)sm.getObject(data.analysisUid);

        table.setValueAt(index, 0, sm.getSample().getAccessionNumber());
        table.setValueAt(index, 1, ana.getTestName());
        table.setValueAt(index, 2, ana.getMethodName());
        table.setValueAt(index, 3, ana.getStatusId());
        table.setValueAt(index, 4, sm.getSample().getStatusId());
    }

    /**
     * returns the ids of the samples that contain the analyses selected in the
     * table
     */
    private HashSet<Integer> getSelectedSamples() {
        UUID data;
        Integer rows[];
        HashSet<Integer> ids;

        rows = table.getSelectedRows();
        ids = new HashSet<Integer>();
        for (Integer i : rows) {
            data = table.getRowAt(i).getData();
            ids.add(data.sampleId);
        }

        return ids;
    }

    /**
     * locks and refetches the samples that the selected analyses belong to;
     * updates the screen's hash with the refetched managers
     */
    private void fetchSelectedSamplesForUpdate() throws Exception {
        int i;
        Integer selRows[];
        UUID data;
        AnalysisViewDO ana;
        SampleManager1 sm;
        ArrayList<Integer> anaIds;
        ArrayList<SampleManager1> sms;
        HashSet<Integer> samIds;

        /*
         * find the samples that the selected analyses belong to
         */
        selRows = table.getSelectedRows();
        samIds = new HashSet<Integer>();
        for (i = 0; i < selRows.length; i++ ) {
            data = table.getRowAt(selRows[i]).getData();
            samIds.add(data.sampleId);
        }

        /*
         * for locking and refetching samples, make a list of all analyses in
         * the table belonging to the samples that the selected analyses also
         * belong to
         */
        anaIds = new ArrayList<Integer>();
        for (i = 0; i < table.getRowCount(); i++ ) {
            data = table.getRowAt(i).getData();
            if ( !samIds.contains(data.sampleId))
                continue;
            sm = managers.get(data.sampleId);
            ana = (AnalysisViewDO)sm.getObject(data.analysisUid);

            anaIds.add(ana.getId());
        }

        /*
         * lock and refetch the samples and update the screen's hash; for
         * update, the results for all analyses is fetched and not just for the
         * once that were returned by the initial query
         */
        sms = SampleService1.get().fetchForUpdateByAnalyses(anaIds, resultElements);
        for (SampleManager1 man : sms)
            managers.put(man.getSample().getId(), man);
    }

    /**
     * Updates samples whose ids have the value "true" in the map, unlocks the
     * others and the ones that couldn't be updated. Shows the passed errors and
     * any found on trying to update. Refreshes the screen with the latest data.
     */
    private void updateAndRefreshScreen(HashMap<Integer, Boolean> updateSams,
                                        ArrayList<Exception> errors) {
        int i;
        Integer selRows[];
        UUID data;
        AnalysisViewDO ana;
        SampleManager1 sm;
        HashSet<Integer> unlockSams;
        ArrayList<Integer> anaIds;
        ArrayList<SampleManager1> sms;

        if (errors == null)
            errors = new ArrayList<Exception>();
        unlockSams = new HashSet<Integer>();
        for (Entry<Integer, Boolean> entry : updateSams.entrySet()) {
            if (entry.getValue()) {
                /*
                 * update the sample if the status of at least one of its
                 * analyses could be changed
                 */
                try {
                    sm = SampleService1.get().update(managers.get(entry.getKey()), true);
                    managers.put(entry.getKey(), sm);
                    continue;
                } catch (ValidationErrorsList e) {
                    errors.addAll(e.getErrorList());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, DataBaseUtil.toString(e.getMessage()), e);
                }
            }

            /*
             * this sample needs to be unlocked because either the status of
             * none of its analyses could be changed or update failed
             */
            unlockSams.add(entry.getKey());
        }

        if (unlockSams.size() > 0) {
            anaIds = new ArrayList<Integer>();
            /*
             * make a list of all analyses in the table, belonging to the
             * samples to be unlocked
             */
            for (i = 0; i < table.getRowCount(); i++ ) {
                data = table.getRowAt(i).getData();
                sm = managers.get(data.sampleId);
                ana = (AnalysisViewDO)sm.getObject(data.analysisUid);
                if (unlockSams.contains(data.sampleId))
                    anaIds.add(ana.getId());
            }
            /*
             * unlock and refetch managers
             */
            try {
                sms = SampleService1.get().unlockByAnalyses(anaIds, singleResultElements);
                for (SampleManager1 man : sms)
                    managers.put(man.getSample().getId(), man);
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }

        /*
         * refresh the rows whose samples were updated/unlocked above
         */
        for (i = 0; i < table.getRowCount(); i++ ) {
            data = table.getRowAt(i).getData();
            if (updateSams.get(data.sampleId) != null)
                refreshRow(i);
        }

        selRows = table.getSelectedRows();
        if (selRows.length == 1) {
            /*
             * when only one row is selected the tabs are shown, so reload them
             */
            data = table.getRowAt(selRows[0]).getData();
            manager = managers.get(data.sampleId);
            setData();
            refreshTabs(data, true);
        }

        /*
         * show any validation errors found while changing the status of
         * analyses or updating samples
         */
        if (errors.size() > 0) {
            setError("(Error 1 of " + errors.size() + ") " + errors.get(0).getMessage());
            window.setMessagePopup(errors, "ErrorPanel");
        } else {
            clearStatus();
        }
    }

    /**
     * Shows the confirm window informing the user that a internal note must be
     * added on unreleasing an analysis. If the user wants to add the note,
     * shows the note popup; otherwise, unlocks the sample .
     */
    private void showConfirm() {
        if (unreleaseAnalysisConfirm == null) {
            unreleaseAnalysisConfirm = new Confirm(Confirm.Type.QUESTION,
                                                   Messages.get().completeRelease_unrelease(),
                                                   Messages.get()
                                                           .completeRelease_unreleaseMessage(),
                                                   Messages.get().gen_cancel(),
                                                   Messages.get().gen_ok());
            unreleaseAnalysisConfirm.setWidth("300px");
            unreleaseAnalysisConfirm.setHeight("150px");
            unreleaseAnalysisConfirm.addSelectionHandler(new SelectionHandler<Integer>() {
                public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<Integer> event) {
                    switch (event.getSelectedItem().intValue()) {
                        case 0:
                            UUID data;
                            HashMap<Integer, Boolean> updateSams;

                            /*
                             * the user doesn't want to add a note for
                             * unreleasing an analysis, so unlock its sample and
                             * refresh the screen
                             */
                            data = table.getRowAt(table.getSelectedRow()).getData();
                            updateSams = new HashMap<Integer, Boolean>();
                            updateSams.put(data.sampleId, false);
                            updateAndRefreshScreen(updateSams, null);
                            break;
                        case 1:
                            showNoteLookup();
                            break;
                    }
                }
            });
        }

        unreleaseAnalysisConfirm.show();
    }

    /**
     * Shows the note popup to allow the user to add a note for unreleasing an
     * analysis. Unreleases the analysis if the user added a note, otherwise
     * shows the confirm window to inform the user that a note must be added.
     */
    private void showNoteLookup() {
        ModalWindow modal;

        if (editNoteLookup == null) {
            editNoteLookup = new EditNoteLookupUI() {
                @Override
                public void ok() {
                    AnalysisViewDO ana;
                    UUID data;
                    SampleManager1 sm;

                    if (DataBaseUtil.isEmpty(editNoteLookup.getText()) ||
                        DataBaseUtil.isEmpty(editNoteLookup.getSubject())) {
                        showConfirm();
                        return;
                    }

                    /*
                     * unrelease the selected analysis after adding an internal
                     * note with the subject and text entered by the user
                     */
                    data = table.getRowAt(table.getSelectedRow()).getData();
                    sm = managers.get(data.sampleId);
                    ana = (AnalysisViewDO)sm.getObject(data.analysisUid);
                    unreleaseWithNote(sm,
                                      ana,
                                      editNoteLookup.getSubject(),
                                      editNoteLookup.getText());

                }

                @Override
                public void cancel() {
                    showConfirm();
                }
            };
        }

        modal = new ModalWindow();
        modal.setSize("620px", "550px");
        modal.setName(Messages.get().gen_noteEditor());
        modal.setCSS(UIResources.INSTANCE.popupWindow());
        modal.setContent(editNoteLookup);

        editNoteLookup.setWindow(modal);
        editNoteLookup.setSubject(null);
        editNoteLookup.setText(null);
        editNoteLookup.setHasSubject(true);
    }

    /**
     * unreleases the specified analysis after adding an internal note having
     * the specified subject and text
     */
    private void unreleaseWithNote(SampleManager1 sm, AnalysisViewDO ana, String subject,
                                   String text) {
        NoteViewDO note;
        ArrayList<Exception> errors;
        HashMap<Integer, Boolean> updateSams;

        /*
         * create an editing note for the analysis
         */
        note = sm.analysisInternalNote.getEditing(ana);
        note.setSubject(subject);
        note.setText(text);
        note.setSystemUser(UserCache.getPermission().getLoginName());
        note.setSystemUserId(UserCache.getPermission().getSystemUserId());
        note.setTimestamp(Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE));

        updateSams = new HashMap<Integer, Boolean>();
        updateSams.put(sm.getSample().getId(), false);
        errors = new ArrayList<Exception>();
        /*
         * unrelease the analysis
         */
        try {
            sm = SampleService1.get()
                               .changeAnalysisStatus(sm,
                                                     ana.getId(),
                                                     Constants.dictionary().ANALYSIS_COMPLETED);
            managers.put(sm.getSample().getId(), sm);
            refreshRow(table.getSelectedRow());
            /*
             * this sample needs to be updated because the analysis was
             * unreleased
             */
            updateSams.put(sm.getSample().getId(), true);
            sm.setPostProcessing(PostProcessing.UNRELEASE);
        } catch (ValidationErrorsList e) {
            errors.addAll(e.getErrorList());
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        updateAndRefreshScreen(updateSams, errors);
    }

    /**
     * If there are any analyses linked to the worksheet specified by the passed
     * id then fetches and shows them; otherwise clears the screen
     */
    private void queryByWorksheet(Integer worksheetId) {
        int i, j;
        WorksheetItemDO wi;
        WorksheetAnalysisViewDO wa;
        WorksheetManager1 wm;
        ArrayList<Integer> ids;

        manager = null;
        managers = null;
        showTabs(Tabs.BLANK);
        setData();
        setState(DEFAULT);
        table.setModel(getTableModel(null));
        fireDataChange();
        bus.fireEvent(new org.openelis.modules.sample1.client.SelectionEvent(SelectedType.NONE,
                                                                             null));
        lastAccession = null;

        setBusy(Messages.get().gen_querying());
        try {
            /*
             * fetch the worksheet specified by the user and find out if there
             * are any analyses linked to it
             */
            wm = WorksheetService1.get().fetchById(worksheetId, WorksheetManager1.Load.DETAIL);
            if (wm == null) {
                noRecordsFound();
                return;
            }

            ids = new ArrayList<Integer>();
            for (i = 0; i < wm.item.count(); i++ ) {
                wi = wm.item.get(i);
                for (j = 0; j < wm.analysis.count(wi); j++ ) {
                    wa = wm.analysis.get(wi, j);
                    if (wa.getAnalysisId() != null)
                        ids.add(wa.getAnalysisId());
                }
            }

            /*
             * fetch and show the analyses, if any; otherwise clear the screen
             */
            if (ids.size() > 0)
                SampleService1.get().fetchByAnalyses(ids, singleResultElements, getQueryCallBack());
            else
                noRecordsFound();
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            setError(Messages.get().gen_queryFailed());
        }
    }

    /**
     * changes the accession number of the sample to the passed value if it is
     * valid for the sample, otherwise shows the validation error
     */
    private void changeAccession(Integer accession) {
        AccessionChangeEvent event;
        Exception error;

        manager.getSample().setAccessionNumber(accession);

        setBusy();

        error = null;
        try {
            SampleService1.get().validateAccessionNumber(manager);
        } catch (InconsistencyException e) {
            error = e;
        } catch (Exception e) {
            accession = null;
            manager.getSample().setAccessionNumber(accession);
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        clearStatus();

        managers.put(manager.getSample().getId(), manager);
        refreshRows(manager.getSample().getId());
        setData();

        event = new AccessionChangeEvent(accession);
        event.setError(error);
        bus.fireEventFromSource(event, screen);
    }

    /**
     * adds the aux groups with the given ids to the manager; shows any errors
     * found while adding the groups
     */
    private void addAuxGroups(ArrayList<Integer> ids) {
        SampleTestReturnVO ret;
        ValidationErrorsList errors;

        setBusy();
        try {
            ret = SampleService1.get().addAuxGroups(manager, ids);
            manager = ret.getManager();
            managers.put(manager.getSample().getId(), manager);
            setData();
            setState(state);
            bus.fireEventFromSource(new AddAuxGroupEvent(ids), this);
            clearStatus();
            /*
             * add scriptlets for the newly added aux data
             */
            addAuxScriptlets();

            /*
             * show any validation errors encountered while adding the tests or
             * the pop up for selecting the prep/reflex tests for the tests
             * added
             */
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
            managers.put(manager.getSample().getId(), manager);
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
        name = isSameWindow ? Messages.get().completeRelease_completeAndRelease() : null;
        try {
            AttachmentUtil.displayAttachment(id, name, window);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * returns the ids of all analyses in the table, belonging to the sample
     * with this id
     */
    private ArrayList<Integer> getAnalyses(Integer sampleId) {
        UUID data;
        AnalysisViewDO ana;
        ArrayList<Integer> ids;
        SampleManager1 sm;

        ids = new ArrayList<Integer>();
        for (int i = 0; i < table.getRowCount(); i++ ) {
            data = table.getRowAt(i).getData();
            if ( !data.sampleId.equals(sampleId))
                continue;
            sm = managers.get(data.sampleId);
            ana = (AnalysisViewDO)sm.getObject(data.analysisUid);
            ids.add(ana.getId());
        }

        return ids;
    }

    /**
     * adds the tests/panels in the list to the sample; shows any errors found
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
            managers.put(manager.getSample().getId(), manager);
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
             * show any validation errors encountered while adding the tests or
             * the pop up for selecting the prep/reflex tests for the tests
             * added
             */
            errors = ret.getErrors();
            if (errors != null && errors.size() > 0) {
                if (errors.hasWarnings())
                    Window.alert(getWarnings(errors.getErrorList(), false));
                if (errors.hasErrors())
                    showErrors(errors);
            } else if (ret.getTests() == null || ret.getTests().size() == 0) {
                isBusy = false;
                runDomainScriptlet(Action_Before.ANALYSIS);
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
     * changes the method of the analysis with this uid to the passed value;
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
            managers.put(manager.getSample().getId(), manager);
            refreshRows(manager.getSample().getId());
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
             * show any validation errors encountered while adding the tests or
             * the pop up for selecting the prep/reflex tests for the tests
             * added
             */
            errors = ret.getErrors();
            if (errors != null && errors.size() > 0) {
                if (errors.hasWarnings())
                    Window.alert(getWarnings(errors.getErrorList(), false));
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
     * changes the status of the analysis with this uid to the passed value
     */
    private void changeAnalysisStatus(String uid, Integer statusId) {
        AnalysisViewDO ana;

        ana = (AnalysisViewDO)manager.getObject(uid);
        try {
            setBusy();
            manager = SampleService1.get().changeAnalysisStatus(manager, ana.getId(), statusId);
            managers.put(manager.getSample().getId(), manager);
            refreshRows(manager.getSample().getId());
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
     * changes the unit of the analysis with this uid to the passed value
     */
    private void changeAnalysisUnit(String uid, Integer unitId) {
        AnalysisViewDO ana;

        ana = (AnalysisViewDO)manager.getObject(uid);
        try {
            setBusy();
            manager = SampleService1.get().changeAnalysisUnit(manager, ana.getId(), unitId);
            managers.put(manager.getSample().getId(), manager);
            refreshRows(manager.getSample().getId());
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
     * changes the prep analysis of the analysis with this uid to the passed
     * value
     */
    private void changeAnalysisPrep(String uid, Integer preAnalysisId) {
        AnalysisViewDO ana;

        ana = (AnalysisViewDO)manager.getObject(uid);
        try {
            setBusy();
            manager = SampleService1.get().changeAnalysisPrep(manager, ana.getId(), preAnalysisId);
            managers.put(manager.getSample().getId(), manager);
            refreshRows(manager.getSample().getId());
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
     * adds to the analysis, at the given indexes, the result rows beginning
     * with the specified row analytes
     */
    private void addRowAnalytes(AnalysisViewDO ana, ArrayList<TestAnalyteViewDO> analytes,
                                ArrayList<Integer> indexes) {
        setBusy();
        try {
            manager = SampleService1.get().addRowAnalytes(manager, ana, analytes, indexes);
            managers.put(manager.getSample().getId(), manager);
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
     * shows the popup for selecting the prep/reflex tests for the analyses in
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

    /**
     * This class contains the unique ids that link a table row with its sample
     * and analysis
     */
    private class UUID {
        Integer sampleId;
        String  analysisUid;

        public UUID(String analysisUid, Integer sampleId) {
            this.analysisUid = analysisUid;
            this.sampleId = sampleId;
        }
    }

    /**
     * This class is used to sort the rows of the table based on the sample,
     * sample item and analysis linked to a row.
     */
    private class RowComparator implements Comparator<Row> {
        public int compare(Row row1, Row row2) {
            int compVal;
            AnalysisViewDO ana1, ana2;
            SampleItemViewDO item1, item2;
            SampleManager1 sm1, sm2;
            UUID b1, b2;

            b1 = row1.getData();
            b2 = row2.getData();

            sm1 = managers.get(b1.sampleId);
            sm2 = managers.get(b2.sampleId);

            /*
             * sort the rows by accession number then item sequence then test
             * name and then method name.
             */
            compVal = sm1.getSample().getAccessionNumber().compareTo(sm2.getSample()
                                                                        .getAccessionNumber());
            if (compVal == 0) {
                ana1 = (AnalysisViewDO)sm1.getObject(b1.analysisUid);
                ana2 = (AnalysisViewDO)sm2.getObject(b2.analysisUid);

                item1 = (SampleItemViewDO)sm1.getObject(Constants.uid()
                                                                 .getSampleItem(ana1.getSampleItemId()));
                item2 = (SampleItemViewDO)sm2.getObject(Constants.uid()
                                                                 .getSampleItem(ana2.getSampleItemId()));

                compVal = item1.getItemSequence().compareTo(item2.getItemSequence());
                if (compVal == 0) {
                    compVal = ana1.getTestName().compareTo(ana2.getTestName());
                    if (compVal == 0)
                        compVal = ana1.getMethodName().compareTo(ana2.getMethodName());
                }
            }

            return compVal;
        }
    }
}