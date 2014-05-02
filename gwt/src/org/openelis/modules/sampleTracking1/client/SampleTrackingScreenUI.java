package org.openelis.modules.sampleTracking1.client;

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.Screen.ShortKeys.*;
import static org.openelis.ui.screen.Screen.Validation.Status.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

import org.openelis.cache.CacheProvider;
import org.openelis.cache.CategoryCache;
import org.openelis.cache.DictionaryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.PatientDO;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleTestRequestVO;
import org.openelis.domain.SampleTestReturnVO;
import org.openelis.domain.StorageViewDO;
import org.openelis.domain.TestAnalyteViewDO;
import org.openelis.domain.TestMethodVO;
import org.openelis.manager.AuxFieldGroupManager;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.meta.SampleMeta;
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
import org.openelis.modules.sample1.client.ClinicalTabUI;
import org.openelis.modules.sample1.client.EnvironmentalTabUI;
import org.openelis.modules.sample1.client.NeonatalTabUI;
import org.openelis.modules.sample1.client.PatientLockEvent;
import org.openelis.modules.sample1.client.PrivateWellTabUI;
import org.openelis.modules.sample1.client.QAEventTabUI;
import org.openelis.modules.sample1.client.QuickEntryTabUI;
import org.openelis.modules.sample1.client.RemoveAnalysisEvent;
import org.openelis.modules.sample1.client.ResultChangeEvent;
import org.openelis.modules.sample1.client.ResultTabUI;
import org.openelis.modules.sample1.client.SDWISTabUI;
import org.openelis.modules.sample1.client.SampleHistoryUtility1;
import org.openelis.modules.sample1.client.SampleItemChangeEvent;
import org.openelis.modules.sample1.client.SampleItemPopoutLookupUI;
import org.openelis.modules.sample1.client.SampleItemTabUI;
import org.openelis.modules.sample1.client.SampleNotesTabUI;
import org.openelis.modules.sample1.client.SampleService1;
import org.openelis.modules.sample1.client.SampleTabUI;
import org.openelis.modules.sample1.client.SelectedType;
import org.openelis.modules.sample1.client.StorageTabUI;
import org.openelis.modules.sample1.client.TestSelectionLookupUI;
import org.openelis.modules.test.client.TestService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.FormErrorWarning;
import org.openelis.ui.common.InconsistencyException;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.ValidationErrorsList;
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
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Confirm;
import org.openelis.ui.widget.Menu;
import org.openelis.ui.widget.MenuItem;
import org.openelis.ui.widget.ModalWindow;
import org.openelis.ui.widget.TabLayoutPanel;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.tree.Node;
import org.openelis.ui.widget.tree.Tree;

import com.google.gwt.core.client.GWT;
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

public class SampleTrackingScreenUI extends Screen implements CacheProvider {

    @UiTemplate("SampleTracking.ui.xml")
    interface SampleTrackingScreenUiBinder extends UiBinder<Widget, SampleTrackingScreenUI> {
    };

    private static SampleTrackingScreenUiBinder          uiBinder = GWT.create(SampleTrackingScreenUiBinder.class);

    @UiField
    protected Button                                     expandButton, collapseButton,
                    similarButton, queryButton, updateButton, addTestButton, cancelTestButton,
                    commitButton, abortButton, optionsButton, prevPageButton, nextPageButton,
                    popoutTreeButton;

    @UiField
    protected Menu                                       optionsMenu, historyMenu;

    @UiField
    protected MenuItem                                   unreleaseSample, viewFinalReport,
                    changeDomain, historySample, historySampleSpecific, historySampleProject,
                    historySampleOrganization, historySampleItem, historyAnalysis,
                    historyCurrentResult, historyStorage, historySampleQA, historyAnalysisQA,
                    historyAuxData;

    @UiField
    protected Tree                                       tree;

    @UiField
    protected TabLayoutPanel                             tabPanel;

    @UiField(provided = true)
    protected SampleTabUI                                sampleTab;

    @UiField(provided = true)
    protected EnvironmentalTabUI                         environmentalTab;

    @UiField(provided = true)
    protected PrivateWellTabUI                           privateWellTab;

    @UiField(provided = true)
    protected SDWISTabUI                                 sdwisTab;

    @UiField(provided = true)
    protected NeonatalTabUI                              neonatalTab;

    @UiField(provided = true)
    protected ClinicalTabUI                              clinicalTab;

    @UiField(provided = true)
    protected QuickEntryTabUI                            quickEntryTab;

    @UiField(provided = true)
    protected SampleItemTabUI                            sampleItemTab;

    @UiField(provided = true)
    protected AnalysisTabUI                              analysisTab;

    @UiField(provided = true)
    protected ResultTabUI                                resultTab;

    @UiField(provided = true)
    protected AnalysisNotesTabUI                         analysisNotesTab;

    @UiField(provided = true)
    protected SampleNotesTabUI                           sampleNotesTab;

    @UiField(provided = true)
    protected StorageTabUI                               storageTab;

    @UiField(provided = true)
    protected QAEventTabUI                               qaEventTab;

    @UiField(provided = true)
    protected AuxDataTabUI                               auxDataTab;

    protected SampleManager1                             manager;

    protected HashMap<Integer, SampleManager1>           managers;

    protected boolean                                    canEdit, isBusy, unrelease;

    protected ModulePermission                           userPermission, unreleasePermission,
                    changeDomainPermission;

    protected SampleTrackingScreenUI                     screen;

    protected TestSelectionLookupUI                      testSelectionLookup;

    protected SampleItemPopoutLookupUI                   sampleItemPopout;

    protected Confirm                                    cancelAnalysisConfirm,
                    unreleaseSampleConfirm;

    protected EditNoteLookupUI                           editNoteLookup;

    protected AddTestLookupUI                            addTestLookup;

    protected ChangeDomainLookupUI                       changeDomainLookup;

    protected HashMap<String, Object>                    cache;

    protected HashMap<Integer, String>                   analysisStatuses;

    protected AsyncCallbackUI<ArrayList<SampleManager1>> queryCall;

    protected AsyncCallbackUI<SampleManager1>            fetchForUpdateCall, unlockCall,
                    commitUpdateCall;

    protected Query                                      query;

    protected static int                                 ROWS_PER_PAGE = 15, DEEPEST_LEVEL = 3;

    protected static final SampleManager1.Load           elements[]    = {
                    SampleManager1.Load.ANALYSISUSER, SampleManager1.Load.AUXDATA,
                    SampleManager1.Load.NOTE, SampleManager1.Load.ORGANIZATION,
                    SampleManager1.Load.PROJECT, SampleManager1.Load.QA,
                    SampleManager1.Load.RESULT, SampleManager1.Load.STORAGE,
                    SampleManager1.Load.WORKSHEET                      };

    protected static final String                        SAMPLE_LEAF   = "sample",
                    SAMPLE_ITEM_LEAF = "sampleItem", ANALYSIS_LEAF = "analysis",
                    STORAGE_LEAF = "storage", NOTE_LEAF = "note", QA_EVENT_LEAF = "qaEvent",
                    AUX_DATA_LEAF = "auxData", RESULT_LEAF = "result";

    protected enum Tab {
        SAMPLE, ENVIRONMENTAL, PRIVATE_WELL, SDWIS, NEONATAL, CLINICAL, QUICK_ENTRY, SAMPLE_ITEM,
        ANALYSIS, TEST_RESULT, ANALYSIS_NOTES, SAMPLE_NOTES, STORAGE, QA_EVENTS, AUX_DATA, BLANK
    };

    /**
     * Check the permissions for this screen, intialize the tabs and widgets
     */
    public SampleTrackingScreenUI(WindowInt window) throws Exception {
        setWindow(window);

        userPermission = UserCache.getPermission().getModule("sampletracking");
        if (userPermission == null)
            throw new PermissionException(Messages.get()
                                                  .screenPermException("Sample Tracking Screen"));

        unreleasePermission = UserCache.getPermission().getModule("sampleunrelease");
        if (unreleasePermission == null)
            unreleasePermission = new ModulePermission();

        changeDomainPermission = UserCache.getPermission().getModule("sampledomainchange");
        if (changeDomainPermission == null)
            changeDomainPermission = new ModulePermission();

        try {
            CategoryCache.getBySystemNames("sample_status",
                                           "sample_domain",
                                           "gender",
                                           "race",
                                           "ethnicity",
                                           "state",
                                           "country",
                                           "analysis_status",
                                           "type_of_sample",
                                           "source_of_sample",
                                           "sample_container",
                                           "unit_of_measure",
                                           "qaevent_type",
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

        initWidget(uiBinder.createAndBindUi(this));

        initialize();
        manager = null;
        managers = null;
        showTabs(Tab.BLANK);
        setData();
        evaluateEdit();
        setState(DEFAULT);
        fireDataChange();
        bus.fireEvent(new org.openelis.modules.sample1.client.SelectionEvent(SelectedType.NONE,
                                                                             null));

        logger.fine("Sample Tracking Screen Opened");
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        screen = this;
        //
        // button panel buttons
        //

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                expandButton.setEnabled(isState(DISPLAY));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                collapseButton.setEnabled(isState(DISPLAY));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                similarButton.setEnabled(isState(DISPLAY));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                queryButton.setEnabled(isState(QUERY, DEFAULT, DISPLAY) &&
                                       userPermission.hasSelectPermission());
                if (isState(QUERY)) {
                    queryButton.lock();
                    queryButton.setPressed(true);
                }
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                updateButton.setEnabled(isState(UPDATE, DISPLAY) &&
                                        userPermission.hasUpdatePermission());
                if (isState(UPDATE)) {
                    updateButton.lock();
                    updateButton.setPressed(true);
                }
            }
        });

        addShortcut(updateButton, 'u', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                addTestButton.setEnabled(false);
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                cancelTestButton.setEnabled(false);
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                commitButton.setEnabled(isState(QUERY, ADD, UPDATE));
            }
        });

        addShortcut(commitButton, 'm', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                abortButton.setEnabled(isState(QUERY, ADD, UPDATE));
            }
        });
        addShortcut(abortButton, 'o', CTRL);

        /*
         * option menu items
         */
        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                optionsMenu.setEnabled(isState(DISPLAY, UPDATE));
                optionsButton.setEnabled(isState(DISPLAY, UPDATE));
                historyMenu.setEnabled(isState(DISPLAY));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                unreleaseSample.setEnabled(isState(DISPLAY) &&
                                           unreleasePermission.hasSelectPermission());
            }
        });

        unreleaseSample.addCommand(new Command() {
            @Override
            public void execute() {
                unreleaseSample();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                viewFinalReport.setEnabled(isState(DISPLAY));
            }
        });

        viewFinalReport.addCommand(new Command() {
            @Override
            public void execute() {
                viewFinalReport();
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                changeDomain.setEnabled(isState(UPDATE) &&
                                        changeDomainPermission.hasSelectPermission() &&
                                        !Constants.domain().QUICKENTRY.equals(manager.getSample()
                                                                                     .getDomain()) &&
                                        !Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample()
                                                                                              .getStatusId()));
            }
        });

        changeDomain.addCommand(new Command() {
            @Override
            public void execute() {
                changeDomain();
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
                Node node;

                uid = null;
                if (tree.getSelectedNode() > 0) {
                    node = tree.getNodeAt(tree.getSelectedNode());
                    if (ANALYSIS_LEAF.equals(node.getType()))
                        uid = ((UUID)node.getData()).uid;
                }

                /*
                 * Show the history of the results of the analysis selected in
                 * the tree. Inform the user that they first need to select an
                 * analysis if this is not the case.
                 */
                if (uid != null) {
                    obj = manager.getObject(uid);
                    SampleHistoryUtility1.currentResult(manager, ((AnalysisViewDO)obj).getId());
                } else {
                    setError(Messages.get().result_historyException());
                }
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
                tree.setEnabled(true);
            }
        });

        tree.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            @Override
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                Node node;
                UUID data;

                if ( !isState(UPDATE))
                    return;

                /*
                 * in Update state, only the nodes that belong to the locked
                 * sample can be selected
                 */
                node = tree.getNodeAt(event.getItem());
                data = node.getData();
                if ( !manager.getSample().getId().equals(data.sampleId))
                    event.cancel();
            }
        });

        tree.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                AnalysisViewDO ana;
                UUID data;
                Node node;

                node = tree.getNodeAt(event.getSelectedItem());
                data = node.getData();
                manager = managers.get(data.sampleId);

                /*
                 * enable the button for adding a test if the nodes for sample
                 * item, result or any of its children is selected and the
                 * sample can be changed; enable the button for cancelling a
                 * test if the nodes for result or any of its children is
                 * selected, the sample can be changed and the analysis can be
                 * cancelled
                 */
                if (RESULT_LEAF.equals(node.getType()) ||
                    RESULT_LEAF.equals(node.getParent().getType())) {
                    ana = (AnalysisViewDO)manager.getObject(data.uid);
                    addTestButton.setEnabled(canEdit && isState(UPDATE));
                    cancelTestButton.setEnabled(canEdit &&
                                                isState(UPDATE) &&
                                                !Constants.dictionary().ANALYSIS_CANCELLED.equals(ana.getStatusId()) &&
                                                !Constants.dictionary().ANALYSIS_RELEASED.equals(ana.getStatusId()));
                } else if (SAMPLE_ITEM_LEAF.equals(node.getType())) {
                    addTestButton.setEnabled(canEdit && isState(UPDATE));
                    cancelTestButton.setEnabled(false);
                } else {
                    addTestButton.setEnabled(false);
                    cancelTestButton.setEnabled(false);
                }

                setData();
                refreshTabs(node);
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                prevPageButton.setEnabled(isState(DISPLAY));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                nextPageButton.setEnabled(isState(DISPLAY));
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                popoutTreeButton.setEnabled(isState(DISPLAY, ADD, UPDATE));
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
                return environmentalTab.getQueryFields();
            }

            public void isValid(Validation validation) {
                if (isState(QUERY) || (manager != null && manager.getSampleEnvironmental() != null))
                    super.isValid(validation);
            }
        });

        addScreenHandler(privateWellTab, "privateWellTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                privateWellTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                privateWellTab.setState(event.getState());
            }

            public Object getQuery() {
                return privateWellTab.getQueryFields();
            }

            public void isValid(Validation validation) {
                if (isState(QUERY) || (manager != null && manager.getSamplePrivateWell() != null))
                    super.isValid(validation);
            }
        });

        addScreenHandler(sdwisTab, "sdwisTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                sdwisTab.onDataChange();
            }

            public void onStateChange(StateChangeEvent event) {
                sdwisTab.setState(event.getState());
            }

            public Object getQuery() {
                return sdwisTab.getQueryFields();
            }

            public void isValid(Validation validation) {
                if (isState(QUERY) || (manager != null && manager.getSampleSDWIS() != null))
                    super.isValid(validation);
            }
        });

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

            public void isValid(Validation validation) {
                if (isState(QUERY) || (manager != null && manager.getSampleNeonatal() != null))
                    super.isValid(validation);
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
                return clinicalTab.getQueryFields();
            }

            public void isValid(Validation validation) {
                if (isState(QUERY) || (manager != null && manager.getSampleClinical() != null)) {
                    super.isValid(validation);
                    if (clinicalTab.getIsBusy())
                        validation.setStatus(FLAGGED);
                }
            }
        });

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
                return sampleItemTab.getQueryFields();
            }
        });

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
                return auxDataTab.getQueryFields();
            }
        });

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

        bus.addHandler(SampleItemChangeEvent.getType(), new SampleItemChangeEvent.Handler() {
            public void onSampleItemChange(SampleItemChangeEvent event) {
                sampleItemChanged(event.getUid(), event.getAction());
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
    }

    /*
     * basic button methods
     */

    /**
     * Expands the tree at every level
     */
    @UiHandler("expandButton")
    protected void expand(ClickEvent event) {
        tree.expand(DEEPEST_LEVEL);
    }

    /**
     * Collpases the tree at every level
     */
    @UiHandler("collapseButton")
    protected void collapse(ClickEvent event) {
        tree.collapse();
    }

    /**
     * Puts the screen in query state, sets the manager to null and instantiates
     * the cache so that it can be used by aux data tab
     */
    @UiHandler("queryButton")
    protected void query(ClickEvent event) {
        manager = null;
        managers = null;
        /*
         * the tab for aux data uses the cache in Query state
         */
        cache = new HashMap<String, Object>();
        showTabs(Tab.SAMPLE,
                 Tab.ENVIRONMENTAL,
                 Tab.PRIVATE_WELL,
                 Tab.SDWIS,
                 Tab.CLINICAL,
                 Tab.SAMPLE_ITEM,
                 Tab.ANALYSIS,
                 Tab.AUX_DATA);
        setData();
        evaluateEdit();
        setState(QUERY);
        tree.setRoot(getRoot(null));
        fireDataChange();
        bus.fireEvent(new org.openelis.modules.sample1.client.SelectionEvent(SelectedType.NONE,
                                                                             null));
        setDone(Messages.get().gen_enterFieldsToQuery());
    }

    /**
     * Puts the screen in update state and loads the tabs with a locked manager.
     * Builds the cache from the manager.
     */
    @UiHandler("updateButton")
    protected void update(ClickEvent event) {
        /*
         * unrelease is not passed as an argument to update() because update()
         * uses an async call for fetching the locked manager and loading it on
         * the screen and if it is passed as an argument then its value doesn't
         * change in the call after the call has been instantiated
         */
        unrelease = false;
        update();
    }

    /**
     * Shows the popup window to allow the user to select the new domain for the
     * sample. Changes the domain if the selected domain is different from the
     * current one.
     */
    @UiHandler("addTestButton")
    protected void addTest(ClickEvent event) {
        SampleItemViewDO item;
        Node node;
        ModalWindow modal;

        if (addTestLookup == null) {
            addTestLookup = new AddTestLookupUI() {

                @Override
                public void ok() {
                    TestMethodVO data;

                    data = addTestLookup.getTest();
                    if (data != null)
                        addAnalysis(data.getTestId(), data.getMethodId());
                }

                @Override
                public void cancel() {
                    // ignore
                }
            };
        }

        /*
         * find the sample item that the test will be added to
         */
        node = findAncestorByType(SAMPLE_ITEM_LEAF);
        item = (SampleItemViewDO)manager.getObject( ((UUID)node.getData()).uid);

        modal = new ModalWindow();
        modal.setSize("380px", "125px");
        modal.setName(Messages.get().gen_addTest());
        modal.setCSS(UIResources.INSTANCE.popupWindow());
        modal.setContent(addTestLookup);
        addTestLookup.setWindow(modal);
        addTestLookup.setSampleType(item.getTypeOfSampleId());
    }

    /**
     * Shows the confirm window to ask the user whether the analysis selected in
     * the tree should be cancelled. cancels the analysis if the user says yes.
     */
    @UiHandler("cancelTestButton")
    protected void cancelTest(ClickEvent event) {
        String uid;
        Node node;
        AnalysisViewDO ana;

        node = tree.getNodeAt(tree.getSelectedNode());
        uid = ((UUID)node.getData()).uid;
        ana = (AnalysisViewDO)manager.getObject(uid);
 
        if (ana.getId() > 0) {
            /*
             * existing analyses cannot be removed, only cancelled
             */
            if (cancelAnalysisConfirm == null) {
                cancelAnalysisConfirm = new Confirm(Confirm.Type.QUESTION,
                                                    Messages.get().analysis_cancelCaption(),
                                                    Messages.get().analysis_cancelMessage(),
                                                    Messages.get().gen_no(),
                                                    Messages.get().gen_yes());
                cancelAnalysisConfirm.setWidth("300px");
                cancelAnalysisConfirm.setHeight("150px");
                cancelAnalysisConfirm.addSelectionHandler(new SelectionHandler<Integer>() {
                    public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<Integer> event) {
                        String uid;
                        Node node;

                        switch (event.getSelectedItem().intValue()) {
                            case 1:
                                /*
                                 * the uid is obtained here again to make sure
                                 * that the event fired below has the uid of the
                                 * currently selected analysis and not of the
                                 * analysis for which the event was fired for
                                 * the first time after creating this popup
                                 */
                                node = tree.getNodeAt(tree.getSelectedNode());
                                uid = ((UUID)node.getData()).uid;

                                changeAnalysisStatus(uid, Constants.dictionary().ANALYSIS_CANCELLED);

                                break;
                        }
                    }
                });
            }

            cancelAnalysisConfirm.show();
        } else {
            /*
             * remove this analysis as it is not an existing one
             */
            setBusy();
            try {
                manager = SampleService1.get().removeAnalysis(manager, ana.getId());
                managers.put(manager.getSample().getId(), manager);
                setData();
                setState(state);
                /*
                 * the tree needs to be reloaded because the analysis was
                 * removed
                 */
                node = findAncestorByType(SAMPLE_LEAF);
                reloadSample(node);
                bus.fireEventFromSource(new RemoveAnalysisEvent(uid), screen);
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
            clearStatus();
        }
    }

    /**
     * Validates the data on the screen and based on the current state; executes
     * various service operations to commit the data
     */
    @UiHandler("commitButton")
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
            case UPDATE:
                commitUpdate(false);
                break;
        }
    }

    /**
     * Creates query fields from the data on the screen and executes a query to
     * return a list of samples
     */
    protected void commitQuery() {
        int numDomains;
        String domain;
        QueryData field;
        ArrayList<QueryData> fields;

        domain = null;
        numDomains = 0;

        /*
         * find out if the user is trying to query by more than one domain
         */
        if (environmentalTab.getQueryFields().size() > 0) {
            domain = Constants.domain().ENVIRONMENTAL;
            numDomains++ ;
        }

        if (privateWellTab.getQueryFields().size() > 0) {
            domain = Constants.domain().PRIVATEWELL;
            numDomains++ ;
        }

        if (sdwisTab.getQueryFields().size() > 0) {
            domain = Constants.domain().SDWIS;
            numDomains++ ;
        }

        if (clinicalTab.getQueryFields().size() > 0) {
            domain = Constants.domain().CLINICAL;
            numDomains++ ;
        }

        /*
         * querying by more than one domain is not allowed
         */
        if (numDomains > 1) {
            window.setError(Messages.get().sampleTracking_queryDomainException());
            return;
        }

        fields = getQueryFields();
        /*
         * make sure that only the samples belong to the domain queried by are
         * returned by the query
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
        query.setRowsPerPage(ROWS_PER_PAGE);
        executeQuery(query);
        cache = null;
    }

    /**
     * Commits the data on the screen to the database. Shows any errors/warnings
     * encountered during the commit, otherwise loads the screen with the
     * committed data.
     */
    protected void commitUpdate(final boolean ignoreWarning) {
        boolean noteAdded;
        Integer accession;
        String prefix;
        PatientDO data;
        NoteViewDO note;
        ValidationErrorsList e1;

        if (unrelease) {
            /*
             * every unreleased sample needs an internal note describing the
             * reason
             */
            noteAdded = false;
            if (manager.sampleInternalNote.count() > 0) {
                note = manager.sampleInternalNote.get(0);
                if (note.getId() < 0)
                    noteAdded = true;
            }

            if ( !noteAdded) {
                window.setError(Messages.get()
                                        .sample_unreleaseNoNoteException(manager.getSample()
                                                                                .getAccessionNumber()));
                return;
            }
        }

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
                    Node node;

                    manager = result;
                    managers.put(manager.getSample().getId(), manager);

                    setData();
                    evaluateEdit();
                    setState(DISPLAY);

                    /*
                     * find the sample node for the currently selected node
                     */
                    node = findAncestorByType(SAMPLE_LEAF);
                    /*
                     * reload the newly fetched sample in the tree and refresh
                     * the tree
                     */
                    reloadSample(node);
                    refreshTabs(node);

                    clearStatus();
                    /*
                     * the cache is set to null only if the update succeeds
                     * because otherwise, it can't be used by any tabs if the
                     * user wants to change any data
                     */
                    cache = null;
                }

                public void validationErrors(ValidationErrorsList e) {
                    showErrors(e);
                    if ( !e.hasErrors() && e.hasWarnings() && !ignoreWarning)
                        if (Window.confirm(getWarnings(e.getErrorList())))
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
    @UiHandler("abortButton")
    protected void abort(ClickEvent event) {
        PatientDO data;

        finishEditing();
        clearErrors();
        setBusy(Messages.get().gen_cancelChanges());

        if (isState(QUERY)) {
            manager = null;
            showTabs(Tab.BLANK);
            setData();
            evaluateEdit();
            setState(DEFAULT);
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
                unlockCall = new AsyncCallbackUI<SampleManager1>() {
                    public void success(SampleManager1 result) {
                        Node node;

                        manager = result;
                        managers.put(manager.getSample().getId(), manager);

                        setData();
                        evaluateEdit();
                        setState(DISPLAY);

                        /*
                         * find the sample node for the currently selected node
                         */
                        node = findAncestorByType(SAMPLE_LEAF);
                        /*
                         * reload the newly fetched sample in the tree and
                         * refresh the tree
                         */
                        reloadSample(node);
                        refreshTabs(node);

                        setDone(Messages.get().gen_updateAborted());
                        cache = null;
                    }

                    public void failure(Throwable e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        clearStatus();
                        cache = null;
                    }
                };
            }

            SampleService1.get().unlock(manager.getSample().getId(), elements, unlockCall);
        }
    }

    /**
     * Executes the user query to get the previous page of results
     */
    @UiHandler("prevPageButton")
    protected void prevPage(ClickEvent event) {
        int page;

        /*
         * query is a class variable because this screen doesn't use a screen
         * navigator but it needs to keep track of the previously executed query
         * and not a query created from the screen's current data
         */
        page = query.getPage();

        if (page == 0) {
            clearStatus();
            return;
        }

        query.setPage(page - 1);
        executeQuery(query);
    }

    /**
     * Executes the user query to get the next page of results
     */
    @UiHandler("nextPageButton")
    protected void nextPage(ClickEvent event) {
        int page;

        /*
         * query is a class variable because this screen doesn't use a screen
         * navigator but it needs to keep track of the previously executed query
         * and not a query created from the screen's current data
         */
        page = query.getPage();

        query.setPage(page + 1);
        executeQuery(query);
    }

    /**
     * Shows the popup window for moving analyses between sample items and
     * reloads the tree with the rearranged data
     */
    @UiHandler("popoutTreeButton")
    protected void popoutTree(ClickEvent event) {
        ModalWindow modal;

        if (sampleItemPopout == null) {
            sampleItemPopout = new SampleItemPopoutLookupUI() {
                @Override
                public void ok() {
                    Node node;

                    if ( !canEdit && !isState(ADD, UPDATE))
                        return;

                    screen.setData();
                    screen.evaluateEdit();
                    screen.setState(state);

                    /*
                     * find the sample node for the currently selected node
                     */
                    node = findAncestorByType(SAMPLE_LEAF);
                    /*
                     * reload the changed sample in the tree and refresh the
                     * tree
                     */
                    reloadSample(node);
                    refreshTabs(node);
                }
            };
        }

        modal = new ModalWindow();
        modal.setSize("500px", "500px");
        modal.setName(Messages.get().itemsAndAnalyses());
        modal.setCSS(UIResources.INSTANCE.popupWindow());
        modal.setContent(sampleItemPopout);

        sampleItemPopout.setWindow(modal);
        sampleItemPopout.setData(manager, state);
    }

    /**
     * Shows the confirm window to ask the user if the changes made to the
     * sample to unrelease it are acceptable. Reloads the screen with a manager
     * with those changes made, if the user agrees.
     */
    protected void unreleaseSample() {
        DictionaryDO data;

        /*
         * the sample must be in Released status to unrelease it
         */
        if ( !Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample().getStatusId())) {
            try {
                data = DictionaryCache.getById(Constants.dictionary().SAMPLE_RELEASED);
                Window.alert(Messages.get().sample_wrongStatusUnrelease(data.getEntry()));
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
            return;
        }

        if (unreleaseSampleConfirm == null) {
            unreleaseSampleConfirm = new Confirm(Confirm.Type.QUESTION,
                                                 Messages.get().sampleTracking_unrelease(),
                                                 Messages.get().sampleTracking_unreleaseMessage(),
                                                 Messages.get().gen_cancel(),
                                                 Messages.get().gen_ok());
            unreleaseSampleConfirm.setWidth("300px");
            unreleaseSampleConfirm.setHeight("150px");
            unreleaseSampleConfirm.addSelectionHandler(new SelectionHandler<Integer>() {
                public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<Integer> event) {
                    switch (event.getSelectedItem().intValue()) {
                        case 1:
                            /*
                             * unrelease is not passed as an argument to
                             * update() because update() uses an async call for
                             * fetching the locked manager and if it is passed
                             * as an argument then its value doesn't change in
                             * the call after the call has been instantiated
                             */
                            unrelease = true;
                            update();
                            break;
                    }
                }
            });
        }

        unreleaseSampleConfirm.show();
    }

    /**
     * Shows the final report for the selected node's sample
     */
    protected void viewFinalReport() {
        Query query;
        QueryData field;
        ArrayList<QueryData> fields;

        query = new Query();
        fields = new ArrayList<QueryData>();

        field = new QueryData();
        field.setKey("ACCESSION_NUMBER");
        field.setQuery(manager.getSample().getAccessionNumber().toString());
        field.setType(QueryData.Type.STRING);
        fields.add(field);

        field = new QueryData();
        field.setKey("PRINTER");
        field.setQuery("-view-");
        field.setType(QueryData.Type.STRING);
        fields.add(field);

        query.setFields(fields);

        setBusy(Messages.get().gen_generatingReport());

        FinalReportService.get().runReportForSingle(query, new AsyncCallback<ReportStatus>() {
            public void onSuccess(ReportStatus status) {
                String url;

                url = "report?file=" + status.getMessage();
                Window.open(URL.encode(url), "FinalReport", null);
                setDone(Messages.get().gen_loadCompleteMessage());
            }

            public void onFailure(Throwable e) {
                setError("Failed");
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        });
    }

    /**
     * Shows the popup window to allow the user to select the new domain for the
     * sample. Changes the domain if the selected domain is different from the
     * current one.
     */
    protected void changeDomain() {
        ModalWindow modal;

        if (changeDomainLookup == null) {
            changeDomainLookup = new ChangeDomainLookupUI() {
                @Override
                public void ok() {
                    String domain;

                    domain = changeDomainLookup.getDomain();
                    if (domain != null && !manager.getSample().getDomain().equals(domain))
                        changeDomain(domain);
                }

                @Override
                public void cancel() {
                    // ignore
                }
            };
        }

        modal = new ModalWindow();
        modal.setSize("225px", "125px");
        modal.setName(Messages.get().changeDomain_changeDomain());
        modal.setCSS(UIResources.INSTANCE.popupWindow());
        modal.setContent(changeDomainLookup);
        changeDomainLookup.setWindow(modal);
        changeDomainLookup.setDomain(manager.getSample().getDomain());
    }

    /**
     * Executes a query to fetch the samples whose ids are in the passed list
     */
    public void query(Collection<Integer> ids) {
        StringBuilder sb;
        QueryData field;

        if (ids == null)
            return;

        query = new Query();
        query.setRowsPerPage(ROWS_PER_PAGE);
        field = new QueryData();
        field.setKey(SampleMeta.getId());

        sb = new StringBuilder();
        for (Integer id : ids) {
            if (sb.length() > 0)
                sb.append("|");
            sb.append(id);
        }

        field.setQuery(sb.toString());
        field.setType(QueryData.Type.INTEGER);
        query.setFields(field);
        executeQuery(query);
    }

    /**
     * Validates the screen and sets the status of validation to "Flagged" if
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
     * Sets the latest manager in the tabs
     */
    private void setData() {
        sampleTab.setData(manager);
        environmentalTab.setData(manager);
        privateWellTab.setData(manager);
        sdwisTab.setData(manager);
        neonatalTab.setData(manager);
        clinicalTab.setData(manager);
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
     * creates a string containing, the message that there are warnings on the
     * screen, all warning messages and the question whether the data should be
     * committed
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
     * Executes the passed query and loads the screen with the results
     */
    private void executeQuery(final Query query) {
        setBusy(Messages.get().gen_querying());

        if (queryCall == null) {
            queryCall = new AsyncCallbackUI<ArrayList<SampleManager1>>() {
                public void success(ArrayList<SampleManager1> result) {
                    UUID data;

                    /*
                     * this map is used to link a table row with the manager
                     * containing the sample and analysis that it's showing
                     */
                    managers = new HashMap<Integer, SampleManager1>();
                    for (SampleManager1 sm : result)
                        managers.put(sm.getSample().getId(), sm);

                    tree.setRoot(getRoot(result));
                    tree.selectNodeAt(0);

                    data = tree.getNodeAt(0).getData();
                    manager = managers.get(data.sampleId);
                    setData();
                    evaluateEdit();
                    setState(DISPLAY);
                    refreshTabs(tree.getNodeAt(0));
                    clearStatus();
                }

                public void lastPage() {
                    int page;

                    /*
                     * make sure that the page doesn't stay one more than the
                     * current one, if there are no more pages in this direction
                     */
                    page = query.getPage();
                    if (page > 0)
                        query.setPage(page - 1);
                    setError(Messages.get().gen_noMoreRecordInDir());
                }

                public void notFound() {
                    manager = null;
                    showTabs(Tab.BLANK);
                    setData();
                    evaluateEdit();
                    setState(DEFAULT);
                    fireDataChange();
                    bus.fireEvent(new org.openelis.modules.sample1.client.SelectionEvent(SelectedType.NONE,
                                                                                         null));
                    setDone(Messages.get().gen_noRecordsFound());
                }

                public void failure(Throwable e) {
                    Window.alert("Error: Sample Tracking call query failed; " + e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    setError(Messages.get().gen_queryFailed());
                }
            };
        }

        SampleService1.get().fetchByQuery(query.getFields(),
                                          query.getPage() * query.getRowsPerPage(),
                                          query.getRowsPerPage(),
                                          elements,
                                          queryCall);
    }

    /**
     * Puts the screen in update state and loads the tabs with a locked manager.
     * Builds the cache from the manager.
     */
    private void update() {
        setBusy(Messages.get().gen_lockForUpdate());

        if (fetchForUpdateCall == null) {
            fetchForUpdateCall = new AsyncCallbackUI<SampleManager1>() {
                public void success(SampleManager1 result) {
                    Node node;

                    if (unrelease) {
                        /*
                         * unrelease the sample
                         */
                        try {
                            manager = SampleService1.get().unrelease(result);
                        } catch (InconsistencyException e) {
                            Window.alert(e.getMessage());
                        } catch (Exception e) {
                            Window.alert(e.getMessage());
                            logger.log(Level.SEVERE, e.getMessage(), e);
                            clearStatus();
                            return;
                        }
                    } else {
                        manager = result;
                    }

                    managers.put(manager.getSample().getId(), manager);

                    buildCache();
                    setData();
                    evaluateEdit();
                    setState(UPDATE);
                    /*
                     * find the sample node for the currently selected node
                     */
                    node = findAncestorByType(SAMPLE_LEAF);
                    /*
                     * reload the newly fetched sample in the tree and refresh
                     * the tree
                     */
                    reloadSample(node);
                    refreshTabs(node);
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
     * Creates a tree loaded from the passed list where every sample in the
     * passed list has a different subtree and returns the root node
     */
    private Node getRoot(ArrayList<SampleManager1> sms) {
        Node root, node;

        /*
         * this prevents any problems with trying to show the errors that were
         * added to the previous nodes and not to the latest ones added below
         */
        tree.clearExceptions();

        root = new Node();
        if (sms == null)
            return root;

        for (SampleManager1 sm : sms) {
            node = new Node(1);
            loadSample(node, sm);
            root.add(node);
        }

        return root;
    }

    /**
     * Creates a subtree loaded from the passed manager; makes passed node the
     * sample node and also the root of the subtree
     */
    private void loadSample(Node node, SampleManager1 sm) {
        int i, j;
        boolean validate;
        AnalysisViewDO ana;
        SampleItemViewDO item;
        SampleDO sample;
        Node inode, rnode;
        UUID sdata, idata, adata;

        /*
         * if the tree is reloaded in update state then it could mean that the
         * sample could have been changed, so the sample type for analyses needs
         * to be validated
         */
        validate = canEdit && isState(UPDATE);

        /*
         * sample
         */
        sample = sm.getSample();
        node.setType(SAMPLE_LEAF);
        setSampleNodeDisplay(node, sample);
        sdata = new UUID(null, sample.getId());
        node.setData(sdata);

        /*
         * sample items
         */
        for (i = 0; i < sm.item.count(); i++ ) {
            item = sm.item.get(i);
            idata = new UUID(Constants.uid().get(item), sample.getId());
            inode = createItemNode(item, idata);
            inode.setOpen(true);

            /*
             * analyses
             */
            for (j = 0; j < sm.analysis.count(item); j++ ) {
                ana = sm.analysis.get(item, j);
                adata = (new UUID(Constants.uid().get(ana), sample.getId()));

                /*
                 * results
                 */
                rnode = createResultNode(ana, adata);

                /*
                 * analysis
                 */
                rnode.add(createAnalysisNode(ana, adata));

                /*
                 * storage, qa events and notes
                 */
                rnode.add(createStorageNode(ana, null, sm, adata));
                rnode.add(createQAEventNode(ana, sm, adata));
                rnode.add(createNoteNode(ana, sm, adata));

                if (validate)
                    validateSampleType(rnode, item.getTypeOfSampleId());

                inode.add(rnode);
            }

            /*
             * storage
             */
            inode.add(createStorageNode(null, item, sm, idata));

            node.add(inode);
        }
        /*
         * qa events, notes
         */
        node.add(createNoteNode(null, sm, sdata));
        node.add(createQAEventNode(null, sm, sdata));

        /*
         * aux data
         */
        node.add(createAuxDataNode(sm, sdata));
    }

    /**
     * Reloads the subtree rooted at the passed node from the latest data in the
     * manager for the currently selected sample
     */
    private void reloadSample(Node sampleNode) {
        HashMap<String, Boolean> openNodes;

        /*
         * find out which nodes in the existing subtree are open right now, so
         * that if their data is present in the current manager, then they can
         * be opened again
         */
        openNodes = new HashMap<String, Boolean>();
        checkOpen(openNodes, sampleNode);

        /*
         * this prevents any problems with trying to show the errors that were
         * added to the previous nodes and not to the latest ones added below
         */
        tree.clearExceptions();

        tree.close(sampleNode);
        sampleNode.removeAllChildren();
        loadSample(sampleNode, manager);

        /*
         * open the nodes in the new subtree if they were open in the old one
         */
        setOpen(openNodes, sampleNode);

        tree.refreshNode(sampleNode);
        tree.selectNodeAt(sampleNode);
    }

    /**
     * For the passed node and all its children, adds entries to the map where
     * the key is the uid of the node's data and the value is whether the node
     * is open
     */
    private void checkOpen(HashMap<String, Boolean> openNodes, Node node) {
        UUID data;

        data = (UUID)node.getData();

        if (SAMPLE_LEAF.equals(node.getType()) || SAMPLE_ITEM_LEAF.equals(node.getType()) ||
            RESULT_LEAF.equals(node.getType()))
            openNodes.put(data.uid, node.isOpen());

        for (int i = 0; i < node.getChildCount(); i++ )
            checkOpen(openNodes, node.getChildAt(i));
    }

    /**
     * Opens the node if the uid of its data is in the map and the value is
     * true, otherwise closes it; does the same for all the node's children
     */
    private void setOpen(HashMap<String, Boolean> openNodes, Node node) {
        Boolean isOpen;

        isOpen = openNodes.get( ((UUID)node.getData()).uid);
        if (isOpen != null && isOpen)
            tree.open(node);
        else
            tree.close(node);

        for (int i = 0; i < node.getChildCount(); i++ )
            setOpen(openNodes, node.getChildAt(i));
    }

    /**
     * If the currently selected node has an ancestor with the passed leaf type
     * then returns it; otherwise returns null
     */
    private Node findAncestorByType(String leafType) {
        Node node;

        /*
         * go up the tree from the currently selected node and find the node
         * with this leaf type; return null if such a node is not found because
         * it's not an ancestor of the selected node
         */
        node = tree.getNodeAt(tree.getSelectedNode());
        while (node != tree.getRoot()) {
            if (leafType.equals(node.getType()))
                return node;

            node = node.getParent();
        }

        return null;
    }

    /**
     * Shows the passed sample's information on the passed node
     */
    private void setSampleNodeDisplay(Node node, SampleDO sample) {
        StringBuilder sb;

        sb = new StringBuilder();
        sb.append(sample.getAccessionNumber());
        if (sample.getStatusId() != null) {
            try {
                sb.append(" [");
                sb.append(DictionaryCache.getById(sample.getStatusId()).getEntry());
                sb.append("]");
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        node.setCell(0, sb.toString());
    }

    /**
     * Shows the passed sample item's information on the passed node
     */
    private void setItemDisplay(Node node, SampleItemViewDO item) {
        StringBuilder sb;

        sb = new StringBuilder();
        sb.append(item.getItemSequence());
        if (item.getTypeOfSample() != null) {
            sb.append(" - ");
            sb.append(item.getTypeOfSample());
        }
        if (item.getContainer() != null) {
            sb.append(" [");
            sb.append(item.getContainer());
            sb.append("]");
        }
        node.setCell(0, sb.toString());
    }

    /**
     * Returns the node showing the analysis with the passed uid in the passed
     * sample node's subtree
     */
    private Node getResultNode(String uid, Node sampleNode) {
        int i, j;
        UUID data;
        Node inode, rnode;

        for (i = 0; i < sampleNode.getChildCount(); i++ ) {
            inode = sampleNode.getChildAt(i);
            for (j = 0; j < inode.getChildCount(); j++ ) {
                rnode = inode.getChildAt(j);
                data = (UUID)rnode.getData();
                if (uid.equals(data.uid))
                    return rnode;
            }
        }

        return null;
    }

    private Node createItemNode(SampleItemViewDO item, UUID data) {
        Node node;
        StringBuilder sb;

        node = new Node(1);
        node.setType(SAMPLE_ITEM_LEAF);

        sb = new StringBuilder();
        sb.append(item.getItemSequence());
        if (item.getTypeOfSample() != null) {
            sb.append(" - ");
            sb.append(item.getTypeOfSample());
        }
        if (item.getContainer() != null) {
            sb.append(" [");
            sb.append(item.getContainer());
            sb.append("]");
        }
        node.setCell(0, sb.toString());
        node.setData(data);

        return node;
    }

    /**
     * Shows the passed analysis' information on the passed node
     */
    private Node createResultNode(AnalysisViewDO ana, UUID data) {
        Node node;

        node = new Node(1);
        node.setType(RESULT_LEAF);

        setResultNodeDisplay(ana, node);
        node.setData(data);

        return node;
    }

    private void setResultNodeDisplay(AnalysisViewDO ana, Node node) {
        StringBuilder sb;

        sb = new StringBuilder();
        sb.append(ana.getTestName());
        sb.append(", ");
        sb.append(ana.getMethodName());
        if (ana.getStatusId() != null) {
            try {
                sb.append(" [");
                sb.append(DictionaryCache.getById(ana.getStatusId()).getEntry());
                sb.append("]");
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }

        node.setCell(0, sb.toString());
    }

    /**
     * Shows the passed analysis' information on the passed node
     */
    private Node createAnalysisNode(AnalysisViewDO ana, UUID data) {
        Node node;
        StringBuilder sb;

        node = new Node(1);
        node.setType(ANALYSIS_LEAF);

        sb = new StringBuilder();
        sb.append(Messages.get().analysis_analysis());
        sb.append(" [");
        if (ana.getUnitOfMeasureId() != null) {
            try {
                sb.append(DictionaryCache.getById(ana.getUnitOfMeasureId()).getEntry());
                sb.append(", ");
            } catch (Exception e) {
                Window.alert(e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }

        if ("Y".equals(ana.getIsReportable()))
            sb.append(Messages.get().gen_reportable());
        else
            sb.append(Messages.get().gen_notReportable());
        sb.append("]");
        node.setCell(0, sb.toString());
        node.setData(data);

        return node;
    }

    /**
     * Creates a node for storage and sets the passed UUID as its data
     */
    private Node createStorageNode(AnalysisViewDO ana, SampleItemViewDO item, SampleManager1 sm,
                                   UUID data) {
        int count;
        StorageViewDO st;
        Node node;
        StringBuilder sb;

        node = new Node(1);
        node.setType(STORAGE_LEAF);
        sb = new StringBuilder();
        sb.append(Messages.get().storage_storage());

        /*
         * show the most recent storage location for the analysis/sample item
         */
        st = null;
        if (ana != null) {
            count = sm.storage.count(ana);
            if (count > 0)
                st = sm.storage.get(ana, count - 1);
        } else {
            count = sm.storage.count(item);
            if (count > 0)
                st = sm.storage.get(item, count - 1);
        }

        if (st != null) {
            sb.append(" [");
            sb.append(st.getStorageLocationName());
            sb.append(", ");
            sb.append(st.getStorageUnitDescription());
            sb.append(" ");
            sb.append(st.getStorageLocationLocation());
            sb.append("]");
        }

        node.setCell(0, sb.toString());
        node.setData(data);

        return node;
    }

    /**
     * Creates a node for note and sets the passed UUID as its data
     */
    private Node createNoteNode(AnalysisViewDO ana, SampleManager1 sm, UUID data) {
        int count1, count2;
        Node node;
        StringBuilder sb;

        node = new Node(1);
        node.setType(NOTE_LEAF);
        sb = new StringBuilder();
        sb.append(Messages.get().note_notes());

        /*
         * show note counts for the analysis/sample
         */
        if (ana != null) {
            count1 = sm.analysisExternalNote.get(ana) == null ? 0 : 1;
            count2 = sm.analysisInternalNote.count(ana);
        } else {
            count1 = sm.sampleExternalNote.get() == null ? 0 : 1;
            count2 = sm.sampleInternalNote.count();
        }

        if (count1 > 0 || count2 > 0) {
            sb.append(" [");
            sb.append(count1);
            sb.append(" - ");
            sb.append(count2);
            sb.append("]");
        }

        node.setCell(0, sb.toString());
        node.setData(data);

        return node;
    }

    /**
     * Creates a node for qa event and sets the passed UUID as its data
     */
    private Node createQAEventNode(AnalysisViewDO ana, SampleManager1 sm, UUID data) {
        int i, count;
        Node node;
        StringBuilder sb;

        node = new Node(1);
        node.setType(QA_EVENT_LEAF);

        sb = new StringBuilder();
        sb.append(Messages.get().qaEvent_qaEvents());

        /*
         * show qa events for the analysis/sample
         */
        if (ana != null) {
            count = sm.qaEvent.count(ana);
            if (count > 0)
                sb.append(" [");
            for (i = 0; i < count; i++ ) {
                if (i > 0)
                    sb.append(", ");
                sb.append(sm.qaEvent.get(ana, i).getQaEventName());
            }
        } else {
            count = sm.qaEvent.count();
            if (count > 0)
                sb.append(" [");
            for (i = 0; i < count; i++ ) {
                if (i > 0)
                    sb.append(", ");
                sb.append(sm.qaEvent.get(i).getQaEventName());
            }
        }

        if (count > 0)
            sb.append("]");

        node.setCell(0, sb.toString());
        node.setData(data);
        return node;
    }

    /**
     * Creates a node for qa event and sets the passed UUID as its data
     */
    private Node createAuxDataNode(SampleManager1 sm, UUID data) {
        int i, count;
        AuxDataViewDO aux;
        Node node;
        StringBuilder sb;
        HashSet<String> grps;

        node = new Node(1);
        node.setType(AUX_DATA_LEAF);

        sb = new StringBuilder();
        sb.append(Messages.get().aux_data());

        /*
         * show the names of the aux groups added to the sample
         */
        count = sm.auxData.count();
        if (count > 0)
            sb.append(" [");
        grps = new HashSet<String>();
        for (i = 0; i < count; i++ ) {
            aux = sm.auxData.get(i);
            if (grps.contains(aux.getAuxFieldGroupName()))
                continue;
            if (grps.size() > 0)
                sb.append(", ");
            sb.append(aux.getAuxFieldGroupName());
            grps.add(aux.getAuxFieldGroupName());
        }

        if (count > 0)
            sb.append("]");

        node.setCell(0, sb.toString());
        node.setData(data);
        return node;
    }

    /**
     * Based on the leaf type of the passed node e.g. sample, analysis etc,
     * shows different tabs and loads them with the data related to the node
     */
    private void refreshTabs(Node selection) {
        ArrayList<Tab> tabs;
        String domain;
        SelectedType type;
        UUID data;

        type = SelectedType.NONE;
        tabs = new ArrayList<Tab>();
        data = selection.getData();

        if (SAMPLE_LEAF.equals(selection.getType())) {
            /*
             * show sample and domain
             */
            tabs.add(Tab.SAMPLE);

            /*
             * find out which domain's tab is to be shown
             */
            domain = manager.getSample().getDomain();
            if (Constants.domain().ENVIRONMENTAL.equals(domain))
                tabs.add(Tab.ENVIRONMENTAL);
            else if (Constants.domain().PRIVATEWELL.equals(domain))
                tabs.add(Tab.PRIVATE_WELL);
            else if (Constants.domain().SDWIS.equals(domain))
                tabs.add(Tab.SDWIS);
            else if (Constants.domain().NEONATAL.equals(domain))
                tabs.add(Tab.NEONATAL);
            else if (Constants.domain().CLINICAL.equals(domain))
                tabs.add(Tab.CLINICAL);
            else if (Constants.domain().QUICKENTRY.equals(domain))
                tabs.add(Tab.QUICK_ENTRY);
        } else if (SAMPLE_ITEM_LEAF.equals(selection.getType())) {
            /*
             * show sample item
             */
            type = SelectedType.SAMPLE_ITEM;
            tabs.add(Tab.SAMPLE_ITEM);
        } else if (RESULT_LEAF.equals(selection.getType())) {
            /*
             * show results
             */
            type = SelectedType.ANALYSIS;
            tabs.add(Tab.TEST_RESULT);
        } else if (ANALYSIS_LEAF.equals(selection.getType())) {
            /*
             * show analysis
             */
            type = SelectedType.ANALYSIS;
            tabs.add(Tab.ANALYSIS);
        } else if (STORAGE_LEAF.equals(selection.getType())) {
            /*
             * show storage
             */
            if (SAMPLE_ITEM_LEAF.equals(selection.getParent().getType()))
                type = SelectedType.SAMPLE_ITEM;
            else
                type = SelectedType.ANALYSIS;
            tabs.add(Tab.STORAGE);
        } else if (QA_EVENT_LEAF.equals(selection.getType())) {
            /*
             * show sample or analysis qa event
             */
            if (RESULT_LEAF.equals(selection.getParent().getType()))
                type = SelectedType.ANALYSIS;
            tabs.add(Tab.QA_EVENTS);
        } else if (NOTE_LEAF.equals(selection.getType())) {
            /*
             * show notes
             */
            if (RESULT_LEAF.equals(selection.getParent().getType())) {
                type = SelectedType.ANALYSIS;
                tabs.add(Tab.ANALYSIS_NOTES);
            } else {
                tabs.add(Tab.SAMPLE_NOTES);
            }
        } else if (AUX_DATA_LEAF.equals(selection.getType())) {
            /*
             * show aux data
             */
            tabs.add(Tab.AUX_DATA);
        }

        showTabs(tabs);
        fireDataChange();
        bus.fireEvent(new org.openelis.modules.sample1.client.SelectionEvent(type, data.uid));
    }

    /**
     * Converts the argument to a list and calls showTabs(List) to make the
     * specified tabs visible and others not visible. To be used when the tabs
     * to be made visible are always the same e.g. in query state.
     */
    private void showTabs(Tab... tabs) {
        showTabs(Arrays.asList(tabs));
    }

    /**
     * Makes the specified tabs visible and the others not visible. Selects the
     * first visible tab if no tab is already selected.
     */
    private void showTabs(List<Tab> tabs) {
        EnumSet<Tab> el;

        el = EnumSet.copyOf(tabs);

        for (Tab tab : Tab.values())
            tabPanel.setTabVisible(tab.ordinal(), el.contains(tab));

        if (tabs.get(0) != Tab.BLANK && tabPanel.getSelectedIndex() < 0)
            tabPanel.selectTab(tabs.get(0).ordinal());
    }

    /**
     * Changes the accession number of the selected sample to the passed value
     * if it is valid for the sample, otherwise shows the validation error
     */
    private void changeAccession(Integer accession) {
        AccessionChangeEvent event;
        Exception error;
        Node node;

        manager.getSample().setAccessionNumber(accession);

        setBusy();

        error = null;
        try {
            SampleService1.get().validateAccessionNumber(manager);
            /*
             * show the new accession number on the sample node
             */
            node = findAncestorByType(SAMPLE_LEAF);
            setSampleNodeDisplay(node, manager.getSample());
            tree.refreshNode(node);
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
        setData();

        event = new AccessionChangeEvent(accession);
        event.setError(error);
        bus.fireEventFromSource(event, screen);
    }

    /**
     * changes the domain of the currently selected sample to passed value and
     * reloads the screen with the changed manager
     */
    private void changeDomain(String domain) {
        Node node;

        /*
         * unlock any locked patient, before changing the domain
         */
        try {
            if (manager.getSampleClinical() != null &&
                manager.getSampleClinical().getPatientId() != null) {
                PatientService.get().abortUpdate(manager.getSampleClinical().getPatientId());
                bus.fireEventFromSource(new PatientLockEvent(manager.getSampleClinical()
                                                                    .getPatient(),
                                                             PatientLockEvent.Action.UNLOCK), this);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            return;
        }

        try {
            manager = SampleService1.get().changeDomain(manager, domain);
            managers.put(manager.getSample().getId(), manager);

            if (manager.getSampleNeonatal() != null) {
                manager.getSampleNeonatal().setPatient(new PatientDO());
                manager.getSampleNeonatal().setNextOfKin(new PatientDO());
            } else if (manager.getSampleClinical() != null) {
                manager.getSampleClinical().setPatient(new PatientDO());
            }

            setData();
            evaluateEdit();
            setState(state);
            /*
             * find the sample node for the currently selected node
             */
            node = findAncestorByType(SAMPLE_LEAF);
            /*
             * reload the changed sample in the tree and refresh the tree
             */
            reloadSample(node);
            refreshTabs(node);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Adds the aux groups with the given ids to the selected manager. Shows any
     * errors found while adding the groups.
     */
    private void addAuxGroups(ArrayList<Integer> ids) {
        SampleTestReturnVO ret;

        setBusy();
        try {
            ret = SampleService1.get().addAuxGroups(manager, ids);
            manager = ret.getManager();
            managers.put(manager.getSample().getId(), manager);
            setData();
            evaluateEdit();
            setState(state);
            bus.fireEventFromSource(new AddAuxGroupEvent(ids), this);
            clearStatus();
            if (ret.getErrors() != null && ret.getErrors().size() > 0)
                showErrors(ret.getErrors());
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            clearStatus();
        }
    }

    /**
     * Removes the aux groups with the given ids from the selected manager
     */
    private void removeAuxGroups(ArrayList<Integer> ids) {
        setBusy();
        try {
            manager = SampleService1.get().removeAuxGroups(manager, ids);
            managers.put(manager.getSample().getId(), manager);
            setData();
            evaluateEdit();
            setState(state);
            bus.fireEventFromSource(new RemoveAuxGroupEvent(ids), screen);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        clearStatus();
    }

    /**
     * Refreshes the selected node's display from the latest data in the sample
     * item with the passed uid. If the passed action specifies that the sample
     * type was changed, then validates all analyses based on the new type.
     */
    private void sampleItemChanged(String itemUid, SampleItemChangeEvent.Action action) {
        Node inode, rnode;
        SampleItemViewDO item;

        /*
         * find the sample item with this uid and refresh the display of the
         * selected node because it's showing that sample item
         */
        item = (SampleItemViewDO)manager.getObject(itemUid);
        inode = tree.getNodeAt(tree.getSelectedNode());
        setItemDisplay(inode, item);
        tree.refreshNode(inode);

        if ( !SampleItemChangeEvent.Action.SAMPLE_TYPE_CHANGED.equals(action))
            return;

        /*
         * show a warning if the new sample type isn't valid for an analysis
         * linked to this sample item
         */
        for (int i = 0; i < inode.getChildCount(); i++ ) {
            rnode = inode.getChildAt(i);
            if (RESULT_LEAF.equals(rnode.getType()))
                validateSampleType(rnode, item.getTypeOfSampleId());
        }
    }

    /**
     * Adds a warning to the node if the unit assigned to the analysis it is
     * showing, isn't valid for the passed type
     */
    private void validateSampleType(Node node, Integer typeId) {
        AnalysisViewDO ana;
        TestManager tm;

        ana = (AnalysisViewDO)manager.getObject( ((UUID)node.getData()).uid);

        if (typeId == null || Constants.dictionary().ANALYSIS_CANCELLED.equals(ana.getStatusId()))
            return;

        try {
            tm = get(ana.getTestId(), TestManager.class);
            if (ana.getUnitOfMeasureId() != null &&
                !tm.getSampleTypes().hasUnit(ana.getUnitOfMeasureId(), typeId))
                tree.addException(node,
                                  0,
                                  new FormErrorWarning(Messages.get()
                                                               .analysis_unitInvalidForSampleType()));
            else
                tree.clearExceptions(node, 0);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Adds the test or panel whose id and method are addId and methodId
     * respectively, to the selected sample. methodId is null for panel.
     */
    private void addAnalysis(Integer addId, Integer methodId) {
        SampleItemViewDO item;
        SampleTestRequestVO test;
        Node node;
        ArrayList<SampleTestRequestVO> tests;

        /*
         * find the sample item to get its sample type
         */
        node = findAncestorByType(SAMPLE_ITEM_LEAF);
        item = (SampleItemViewDO)manager.getObject( ((UUID)node.getData()).uid);

        tests = new ArrayList<SampleTestRequestVO>();
        if (methodId != null)
            test = new SampleTestRequestVO(item.getId(), addId, null, null, null, null, false, null);
        else
            test = new SampleTestRequestVO(item.getId(), null, null, null, null, addId, false, null);

        tests.add(test);
        addAnalyses(tests);
    }

    /**
     * Adds the tests/panels in the list to the selected sample. Shows any
     * errors found while adding the tests or the popup for selecting additional
     * prep/reflex tests.
     */
    private void addAnalyses(ArrayList<SampleTestRequestVO> tests) {
        SampleTestReturnVO ret;

        setBusy();
        try {
            ret = SampleService1.get().addAnalyses(manager, tests);
            manager = ret.getManager();
            managers.put(manager.getSample().getId(), manager);

            setData();
            setState(state);
            /*
             * notify the tabs that some new tests have been added
             */
            bus.fireEventFromSource(new AddTestEvent(tests), this);

            /*
             * reload the sample's subtree to show the newly added analyses
             */
            reloadSample(findAncestorByType(SAMPLE_LEAF));

            clearStatus();
            if (ret.getErrors() != null && ret.getErrors().size() > 0)
                showErrors(ret.getErrors());
            else
                showTests(ret);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            clearStatus();
        }
    }

    /**
     * Changes the method of the analysis with this uid to the passed value.
     * Shows any errors found while making the change or the popup for selecting
     * additional prep/reflex tests.
     */
    private void changeAnalysisMethod(String uid, Integer methodId) {
        AnalysisViewDO ana;
        SampleTestReturnVO ret;
        Node node;

        ana = (AnalysisViewDO)manager.getObject(uid);
        try {
            setBusy();
            ret = SampleService1.get().changeAnalysisMethod(manager, ana.getId(), methodId);
            manager = ret.getManager();
            managers.put(manager.getSample().getId(), manager);

            /*
             * refresh the display of the selected node's parent because it
             * shows the test and method, not the selected node
             */
            ana = (AnalysisViewDO)manager.getObject(uid);
            node = tree.getNodeAt(tree.getSelectedNode());
            setResultNodeDisplay(ana, node.getParent());
            tree.refreshNode(node.getParent());

            setData();
            evaluateEdit();
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

            if (ret.getErrors() != null && ret.getErrors().size() > 0) {
                /*
                 * show any validation errors encountered while adding the tests
                 */
                showErrors(ret.getErrors());
                isBusy = false;
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
     * Changes the status of the analysis with this uid to the passed value
     */
    private void changeAnalysisStatus(String uid, Integer statusId) {
        AnalysisViewDO ana;
        Node node;

        ana = (AnalysisViewDO)manager.getObject(uid);
        try {
            setBusy();
            manager = SampleService1.get().changeAnalysisStatus(manager, ana.getId(), statusId);
            managers.put(manager.getSample().getId(), manager);

            setData();
            evaluateEdit();
            setState(state);

            /*
             * if an analysis' status changed then some other analyses may have
             * been affected too, so the tree needs to be reloaded
             */
            node = findAncestorByType(SAMPLE_LEAF);
            reloadSample(node);

            /*
             * select the node showing the analysis in the new subtree, because
             * it was selected when the user clicked "Cancel Test" button
             */
            tree.selectNodeAt(getResultNode(uid, node));

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
            managers.put(manager.getSample().getId(), manager);
            setData();
            evaluateEdit();
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
        Node node;

        ana = (AnalysisViewDO)manager.getObject(uid);
        try {
            setBusy();
            manager = SampleService1.get().changeAnalysisPrep(manager, ana.getId(), preAnalysisId);
            managers.put(manager.getSample().getId(), manager);

            setData();
            evaluateEdit();
            setState(state);

            /*
             * if an analysis' prep test changed then some other analyses may
             * have been affected too, so the tree needs to be reloaded
             */
            node = findAncestorByType(SAMPLE_LEAF);
            reloadSample(node);

            /*
             * select the node showing the analysis in the new subtree
             */
            tree.selectNodeAt(getResultNode(uid, node));

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
     * Adds to the passed analysis, result rows beginning with the specified row
     * analytes at the given indexes
     */
    private void addRowAnalytes(AnalysisViewDO ana, ArrayList<TestAnalyteViewDO> analytes,
                                ArrayList<Integer> indexes) {
        setBusy();
        try {
            manager = SampleService1.get().addRowAnalytes(manager, ana, analytes, indexes);
            managers.put(manager.getSample().getId(), manager);
            setData();
            evaluateEdit();
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

        if (ret.getTests() == null || ret.getTests().size() == 0) {
            isBusy = false;
            return;
        }

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
     * This class contains the unique ids that link a tree node with its sample
     * and analysis or sample item
     */
    private class UUID {
        Integer sampleId;
        String  uid;

        public UUID(String analysisUid, Integer sampleId) {
            this.uid = analysisUid;
            this.sampleId = sampleId;
        }
    }
}