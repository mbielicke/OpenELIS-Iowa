package org.openelis.modules.completeRelease1.client;

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.Screen.ShortKeys.*;
import static org.openelis.ui.screen.Screen.Validation.Status.*;
import static org.openelis.ui.screen.State.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.logging.Level;

import org.openelis.cache.CacheProvider;
import org.openelis.cache.CategoryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.NoteViewDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.manager.AuxFieldGroupManager;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.auxData.client.AuxDataTabUI;
import org.openelis.modules.auxiliary.client.AuxiliaryService;
import org.openelis.modules.main.client.OpenELIS;
import org.openelis.modules.sample1.client.AnalysisNotesTabUI;
import org.openelis.modules.sample1.client.AnalysisTabUI;
import org.openelis.modules.sample1.client.EnvironmentalTabUI;
import org.openelis.modules.sample1.client.NeonatalTabUI;
import org.openelis.modules.sample1.client.PrivateWellTabUI;
import org.openelis.modules.sample1.client.QAEventTabUI;
import org.openelis.modules.sample1.client.ResultTabUI;
import org.openelis.modules.sample1.client.SDWISTabUI;
import org.openelis.modules.sample1.client.SampleHistoryUtility1;
import org.openelis.modules.sample1.client.SampleItemTabUI;
import org.openelis.modules.sample1.client.SampleNotesTabUI;
import org.openelis.modules.sample1.client.SampleService1;
import org.openelis.modules.sample1.client.SampleTabUI;
import org.openelis.modules.sample1.client.SelectedType;
import org.openelis.modules.sample1.client.StorageTabUI;
import org.openelis.modules.test.client.TestService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.Menu;
import org.openelis.ui.widget.MenuItem;
import org.openelis.ui.widget.TabLayoutPanel;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.UnselectionEvent;
import org.openelis.ui.widget.table.event.UnselectionHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class CompleteReleaseScreenUI extends Screen implements CacheProvider {

    @UiTemplate("CompleteRelease.ui.xml")
    interface CompleteReleaseUiBinder extends UiBinder<Widget, CompleteReleaseScreenUI> {
    };

    private static CompleteReleaseUiBinder               uiBinder = GWT.create(CompleteReleaseUiBinder.class);

    @UiField
    protected Button                                     query, update, commit, complete, release,
                    abort, optionsButton;

    @UiField
    protected Menu                                       optionsMenu, historyMenu;

    @UiField
    protected MenuItem                                   historySample, historySampleSpecific,
                    historySampleProject, historySampleOrganization, historySampleItem,
                    historyAnalysis, historyCurrentResult, historyStorage, historySampleQA,
                    historyAnalysisQA, historyAuxData;

    @UiField
    protected Dropdown<Integer>                          sampleStatus, analysisStatus;

    @UiField
    protected Table                                      table;

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

    protected boolean                                    isBusy;

    protected ModulePermission                           userPermission;

    protected CompleteReleaseScreenUI                    screen;

    protected HashMap<String, Object>                    cache;

    protected AsyncCallbackUI<ArrayList<SampleManager1>> queryCall;

    protected AsyncCallbackUI<SampleManager1>            fetchForUpdateCall, updateCall,
                    unlockCall;

    private enum Tabs {
        SAMPLE, ENVIRONMENTAL, PRIVATE_WELL, SDWIS, NEONATAL, SAMPLE_ITEM, ANALYSIS, TEST_RESULT,
        ANALYSIS_NOTES, SAMPLE_NOTES, STORAGE, QA_EVENTS, AUX_DATA, BLANK
    };

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
                optionsMenu.setEnabled(isState(DISPLAY));
                optionsButton.setEnabled(isState(DISPLAY));
                historyMenu.setEnabled(isState(DISPLAY));
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
                 * 
                 * uid = sampleItemAnalysisTreeTab.getSelectedUid(); if (uid !=
                 * null) { obj = manager.getObject(uid); if (obj instanceof
                 * AnalysisViewDO) {
                 * SampleHistoryUtility1.currentResult(manager,
                 * ((AnalysisViewDO)obj).getId()); return; } }
                 * 
                 * setError(Messages.get().result_historyException());
                 */
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

        addScreenHandler(table, "table", new ScreenHandler<Item<Integer>>() {
            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(true);
                table.setAllowMultipleSelection(isState(DISPLAY));
            }
        });

        table.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                /*
                 * since in Update state, the selected row's manager is locked,
                 * no other row is allowed to be selected
                 */
                if (isState(UPDATE))
                    event.cancel();
            }
        });

        table.addUnselectionHandler(new UnselectionHandler<Integer>() {
            public void onUnselection(UnselectionEvent<Integer> event) {
                /*
                 * since in Update state, the selected row's manager is locked,
                 * it's not allowed ton be unselected
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
                 * and load the tabs for the selected row's domain
                 */
                if (rows.length > 1) {
                    data = null;
                    manager = null;
                } else {
                    data = (UUID)table.getRowAt(rows[0]).getData();
                    manager = managers.get(data.sampleId);
                }

                setData();
                refreshTabs(data);
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

        sampleStatus.setModel(model);

        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("analysis_status")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }

        analysisStatus.setModel(model);
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
    }

    /**
     * Puts the screen in update state and loads the tabs with a locked manager.
     * Builds the cache from the manager.
     */
    @UiHandler("update")
    protected void update(ClickEvent event) {
        if (table.getSelectedRows().length > 1) {
            window.setError(Messages.get().sample_cantUpdateMultiple());
            return;
        }

        setBusy(Messages.get().gen_lockForUpdate());

        if (fetchForUpdateCall == null) {
            fetchForUpdateCall = new AsyncCallbackUI<SampleManager1>() {
                public void success(SampleManager1 result) {
                    UUID data;

                    managers.put(manager.getSample().getId(), result);
                    updateRows(result.getSample().getId());
                    data = table.getRowAt(table.getSelectedRow()).getData();
                    manager = result;
                    buildCache();
                    setData();
                    setState(UPDATE);
                    refreshTabs(data);
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

        SampleManager1.Load elements[] = {SampleManager1.Load.ANALYSISUSER,
                        SampleManager1.Load.AUXDATA, SampleManager1.Load.NOTE,
                        SampleManager1.Load.ORGANIZATION, SampleManager1.Load.PROJECT,
                        SampleManager1.Load.QA, SampleManager1.Load.RESULT,
                        SampleManager1.Load.STORAGE, SampleManager1.Load.WORKSHEET};

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
        executeQuery(query);
        cache = null;
    }

    /**
     * Calls the service method to commit the data on the screen, to the
     * database. Shows any errors/warnings encountered during the commit,
     * otherwise loads the screen with the committed data.
     */
    protected void commitUpdate(final boolean ignoreWarning) {
        setBusy(Messages.get().gen_updating());

        if (updateCall == null) {
            updateCall = new AsyncCallbackUI<SampleManager1>() {
                public void success(SampleManager1 result) {
                    UUID data;

                    managers.put(manager.getSample().getId(), result);
                    updateRow(table.getSelectedRow(), result);
                    data = table.getRowAt(table.getSelectedRow()).getData();
                    manager = result;

                    setData();
                    setState(DISPLAY);
                    refreshTabs(data);
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

        SampleService1.get().update(manager, ignoreWarning, updateCall);
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

        if (state == QUERY) {
            manager = null;
            showTabs(Tabs.BLANK);
            setData();
            setState(DEFAULT);
            table.setModel(getTableModel(null));
            fireDataChange();
            bus.fireEvent(new org.openelis.modules.sample1.client.SelectionEvent(SelectedType.NONE,
                                                                                 null));
            setDone(Messages.get().gen_queryAborted());
        } else if (state == UPDATE) {
            if (unlockCall == null) {
                unlockCall = new AsyncCallbackUI<SampleManager1>() {
                    public void success(SampleManager1 result) {
                        UUID data;

                        managers.put(manager.getSample().getId(), result);
                        updateRow(table.getSelectedRow(), result);
                        data = table.getRowAt(table.getSelectedRow()).getData();
                        manager = result;

                        setData();
                        setState(DISPLAY);
                        refreshTabs(data);

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

            SampleManager1.Load elements[] = {SampleManager1.Load.ANALYSISUSER,
                            SampleManager1.Load.AUXDATA, SampleManager1.Load.NOTE,
                            SampleManager1.Load.ORGANIZATION, SampleManager1.Load.PROJECT,
                            SampleManager1.Load.QA, SampleManager1.Load.RESULT,
                            SampleManager1.Load.STORAGE, SampleManager1.Load.WORKSHEET};

            SampleService1.get().unlock(manager.getSample().getId(), elements, unlockCall);
        }
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
                    cache.put("tm:" + tm.getTest().getId(), tm);
            }

            /*
             * the list of aux field groups to be fetched
             */
            ids.clear();
            prevId = null;
            for (i = 0; i < manager.auxData.count(); i++ ) {
                aux = manager.auxData.get(i);
                if ( !aux.getGroupId().equals(prevId)) {
                    ids.add(aux.getGroupId());
                    prevId = aux.getGroupId();
                }
            }

            if (ids.size() > 0) {
                afgms = AuxiliaryService.get().fetchByIds(ids);
                for (AuxFieldGroupManager afgm : afgms)
                    cache.put("am:" + afgm.getGroup().getId(), afgm);
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * creates a string containing the message that there are warnings on the
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

    private void executeQuery(final Query query) {
        QueryData field;

        setBusy(Messages.get().gen_querying());

        if (queryCall == null) {
            queryCall = new AsyncCallbackUI<ArrayList<SampleManager1>>() {
                public void success(ArrayList<SampleManager1> result) {
                    UUID data;
                    ArrayList<Row> model;

                    /*
                     * this map is used to link a table row with the sample
                     * manager
                     */
                    managers = new HashMap<Integer, SampleManager1>();
                    for (SampleManager1 sm : result)
                        managers.put(sm.getSample().getId(), sm);

                    model = getTableModel(result);
                    /*
                     * sorting the model before loading it into the table is
                     * more efficient than loading the table with it and then
                     * sorting it one column at a time
                     */
                    Collections.sort(model, new RowComparator());
                    table.setModel(model);
                    table.selectRowAt(0);

                    data = table.getRowAt(0).getData();
                    manager = managers.get(data.sampleId);

                    setData();
                    setState(DISPLAY);
                    refreshTabs(data);
                    clearStatus();
                }

                public void notFound() {
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

                public void failure(Throwable error) {
                    Window.alert("Error: Complete Release call query failed; " + error.getMessage());
                    setError(Messages.get().gen_queryFailed());
                }
            };
        }

        /*
         * don't query for quick-entry samples
         */
        field = new QueryData(SampleMeta.getDomain(),
                              QueryData.Type.STRING,
                              "!" + Constants.domain().QUICKENTRY);
        query.setFields(field);
        query.setRowsPerPage(500);

        /*
         * querying by single result makes sure that only the analyses queried
         * for on the screen are present in the returned managers and not all
         * the analyses in those samples
         */
        SampleManager1.Load elements[] = {SampleManager1.Load.ANALYSISUSER,
                        SampleManager1.Load.AUXDATA, SampleManager1.Load.NOTE,
                        SampleManager1.Load.ORGANIZATION, SampleManager1.Load.PROJECT,
                        SampleManager1.Load.QA, SampleManager1.Load.SINGLERESULT,
                        SampleManager1.Load.STORAGE, SampleManager1.Load.WORKSHEET};

        SampleService1.get().fetchByAnalysisQuery(query, elements, queryCall);
    }

    private void updateRows(Integer sampleId) {
        UUID data;

        for (int i = 0; i < table.getRowCount(); i++ ) {
            data = table.getRowAt(i).getData();
            if (sampleId.equals(data.sampleId))
                updateRow(i, managers.get(sampleId));
        }
    }

    private void updateRow(int index, SampleManager1 manager) {
        AnalysisViewDO ana;
        UUID data;

        data = table.getRowAt(index).getData();
        ana = (AnalysisViewDO)manager.getObject(data.analysisUid);

        table.setValueAt(index, 0, manager.getSample().getAccessionNumber());
        table.setValueAt(index, 1, ana.getTestName());
        table.setValueAt(index, 2, ana.getMethodName());
        table.setValueAt(index, 3, ana.getStatusId());
        table.setValueAt(index, 4, manager.getSample().getStatusId());
    }

    private ArrayList<Row> getTableModel(ArrayList<SampleManager1> managers) {
        int i, j;
        SampleItemViewDO item;
        AnalysisViewDO ana;
        Row row;
        ArrayList<Row> model;

        model = new ArrayList<Row>();
        if (managers == null)
            return model;

        /*
         * only the analyses with results are shown because they were returned
         * by the screen's query
         */
        for (SampleManager1 sm : managers) {
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

    private void refreshTabs(UUID data) {
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
            ana = (AnalysisViewDO)sm.getObject(uid);
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

            showTabs(Tabs.SAMPLE,
                     domainTab,
                     Tabs.SAMPLE_ITEM,
                     Tabs.ANALYSIS,
                     Tabs.TEST_RESULT,
                     Tabs.ANALYSIS_NOTES,
                     Tabs.SAMPLE_NOTES,
                     Tabs.STORAGE,
                     Tabs.QA_EVENTS,
                     Tabs.AUX_DATA);

            setTabNotification(Tabs.TEST_RESULT, sm, ana);
            setTabNotification(Tabs.ANALYSIS_NOTES, sm, ana);
            setTabNotification(Tabs.SAMPLE_NOTES, sm, ana);
            setTabNotification(Tabs.STORAGE, sm, ana);
            setTabNotification(Tabs.QA_EVENTS, sm, ana);
            setTabNotification(Tabs.AUX_DATA, sm, ana);

            fireDataChange();
            bus.fireEvent(new org.openelis.modules.sample1.client.SelectionEvent(type, uid));
        }
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

    private void setTabNotification(Tabs tabs, SampleManager1 sm, AnalysisViewDO ana) {
        int count;
        String label;
        NoteViewDO note;

        label = null;

        switch (tabs) {
            case TEST_RESULT:
                label = String.valueOf(sm.result.count(ana));
                break;
            case ANALYSIS_NOTES:
                note = sm.analysisExternalNote.get(ana);
                if (note == null)
                    count = 0;
                else
                    count = 1;
                label = DataBaseUtil.concatWithSeparator(count,
                                                         " - ",
                                                         sm.analysisInternalNote.count(ana));
                break;
            case SAMPLE_NOTES:
                note = sm.sampleExternalNote.get();
                if (note == null)
                    count = 0;
                else
                    count = 1;
                label = DataBaseUtil.concatWithSeparator(count,
                                                         " - ",
                                                         sm.sampleInternalNote.count());
                break;
            case STORAGE:
                label = String.valueOf(sm.storage.count(ana));
                break;
            case QA_EVENTS:
                label = DataBaseUtil.concatWithSeparator(sm.qaEvent.count(),
                                                         " - ",
                                                         sm.qaEvent.count(ana));
                break;
            case AUX_DATA:
                label = String.valueOf(sm.auxData.count());
                break;
        }

        tabPanel.setTabNotification(tabs.ordinal(), label);
    }

    /**
     * This class is used to hold the data associated with a given row in the
     * table
     */
    private class UUID {
        String  analysisUid;
        Integer sampleId;

        public UUID(String analysisUid, Integer sampleId) {
            this.analysisUid = analysisUid;
            this.sampleId = sampleId;
        }
    }

    /**
     * This class is used to sort the rows of the table based on the sample,
     * sample item and analysis linked to a row. The rows are sorted by
     * accession number then item sequence then test name and then method name.
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