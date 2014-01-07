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
import java.util.logging.Level;

import org.openelis.cache.CacheProvider;
import org.openelis.cache.CategoryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AnalysisViewDO;
import org.openelis.domain.AuxDataViewDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
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
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.AsyncCallbackUI;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
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
    protected NeonatalTabUI                     neonatalTab;

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

    protected boolean                                    canEdit, isBusy, reloadTable;

    protected ModulePermission                           userPermission;

    protected CompleteReleaseScreenUI                    screen;

    protected HashMap<String, Object>                    cache;

    protected AsyncCallbackUI<ArrayList<SampleManager1>> queryCall;

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
        refreshScreen(null, DEFAULT);
        // reloadTabs(DEFAULT, Tabs.BLANK);

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
                query.setEnabled(isState(DEFAULT, DISPLAY) && userPermission.hasSelectPermission());
                if (isState(QUERY)) {
                    query.setPressed(true);
                    query.lock();
                } else
                    query.setPressed(false);
            }
        });
        addShortcut(query, 'q', CTRL);

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                update.setEnabled(isState(DISPLAY) && userPermission.hasUpdatePermission());
                if (isState(UPDATE)) {
                    update.setPressed(true);
                    update.lock();
                } else {
                    update.setPressed(false);
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
            // public void onDataChange(DataChangeEvent event) {
            // if (reloadTable)
            // table.setModel(getTableModel());
            // }

            public void onStateChange(StateChangeEvent event) {
                table.setEnabled(true);
            }
        });

        table.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                Bundle data;

                data = table.getRowAt(event.getSelectedItem()).getData();
                refreshTabs(state, data, getTabsForDomain(data.manager));
            }
        });

        table.addUnselectionHandler(new UnselectionHandler<Integer>() {
            public void onUnselection(UnselectionEvent<Integer> event) {
                /*
                 * in Update state, the currently selected row's manager gets
                 * locked, so selecting another row is not allowed
                 */
                if (isState(UPDATE))
                    event.cancel();
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
        refreshScreen(null, QUERY);
        refreshTabs(QUERY, null, Tabs.SAMPLE, Tabs.ANALYSIS);
        setDone(Messages.get().gen_enterFieldsToQuery());
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
        // case UPDATE:
        // commitUpdate(false);
        // break;
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
            refreshScreen(null, DEFAULT);
            refreshTabs(DEFAULT, null, Tabs.BLANK);
            setDone(Messages.get().gen_queryAborted());
        }
    }

    public void setState(State state) {
        evaluateEdit();
        this.state = state;
        bus.fireEventFromSource(new StateChangeEvent(state), this);
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
     * determines if the fields on the screen can be edited based on the data
     */
    private void evaluateEdit() {
        canEdit = (manager != null && !Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample()
                                                                                            .getStatusId()));
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
                    Bundle data;

                    clearStatus();
                    refreshScreen(result, DISPLAY);
                    if (table.getRowCount() > 0) {
                        table.selectRowAt(0);
                        data = table.getRowAt(0).getData();
                        refreshTabs(DISPLAY, data, getTabsForDomain(data.manager));
                    }
                }

                public void notFound() {
                    setState(DEFAULT);
                    setDone(Messages.get().gen_noRecordsFound());
                }

                public void lastPage() {
                    setError(Messages.get().gen_noMoreRecordInDir());
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

    private void refreshScreen(ArrayList<SampleManager1> managers,
                               State state) {
        setState(state);
        table.setModel(getTableModel(managers));
    }

    private void refreshTabs(State state, Bundle data, Tabs... tabs) {
        String uid;
        SelectedType type;

        if (data != null) {
            manager = data.manager;
            type = SelectedType.ANALYSIS;
            uid = data.analysisUid;
        } else {
            manager = null;
            type = SelectedType.NONE;
            uid = null;
        }
        showTabs(tabs);
        setData();
        setState(state);
        fireDataChange();
        bus.fireEvent(new org.openelis.modules.sample1.client.SelectionEvent(type, uid));
    }

    private Tabs[] getTabsForDomain(SampleManager1 manager) {
        Tabs domainTab;

        domainTab = null;
        if (Constants.domain().ENVIRONMENTAL.equals(manager.getSample().getDomain()))
            domainTab = Tabs.ENVIRONMENTAL;
        else if (Constants.domain().PRIVATEWELL.equals(manager.getSample().getDomain()))
            domainTab = Tabs.PRIVATE_WELL;
        else if (Constants.domain().SDWIS.equals(manager.getSample().getDomain()))
            domainTab = Tabs.SDWIS;
        else if (Constants.domain().NEONATAL.equals(manager.getSample().getDomain()))
            domainTab = Tabs.NEONATAL;

        Tabs tabs[] = {Tabs.SAMPLE, domainTab, Tabs.SAMPLE_ITEM, Tabs.ANALYSIS, Tabs.TEST_RESULT,
                        Tabs.ANALYSIS_NOTES, Tabs.SAMPLE_NOTES, Tabs.STORAGE, Tabs.QA_EVENTS,
                        Tabs.AUX_DATA};

        return tabs;
    }

    /**
     * makes the tabs specified in the argument visible and the others not
     * visible
     */
    private void showTabs(Tabs... tabs) {
        EnumSet<Tabs> el;

        el = EnumSet.copyOf(Arrays.asList(tabs));

        for (Tabs tab : Tabs.values())
            tabPanel.setTabVisible(tab.ordinal(), el.contains(tab));

        if (tabs[0] != Tabs.BLANK && tabPanel.getSelectedIndex() < 0)
            tabPanel.selectTab(tabs[0].ordinal());
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
         * load the table with the analyses that have results, as those are the
         * ones returned by the query executed on the screen
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
                    row.setData(new Bundle(Constants.uid().get(ana), Constants.uid().get(item), sm));
                    model.add(row);
                }
            }
        }

        /*
         * sort the model before loading it into the table because
         */
        Collections.sort(model, new RowComparator());

        return model;
    }

    private class Bundle {
        String         analysisUid, sampleItemUid;
        SampleManager1 manager;

        public Bundle(String analysisUid, String sampleItemUid, SampleManager1 manager) {
            this.analysisUid = analysisUid;
            this.sampleItemUid = sampleItemUid;
            this.manager = manager;
        }
    }

    private class RowComparator implements Comparator<Row> {
        public int compare(Row row1, Row row2) {
            int compVal;
            AnalysisViewDO ana1, ana2;
            SampleItemViewDO item1, item2;
            SampleManager1 sm1, sm2;
            Bundle b1, b2;

            b1 = row1.getData();
            b2 = row2.getData();

            sm1 = b1.manager;
            sm2 = b2.manager;
            /*
             * order by accession number then item sequence then test name and
             * then method name
             */
            compVal = sm1.getSample().getAccessionNumber().compareTo(sm2.getSample()
                                                                        .getAccessionNumber());
            if (compVal == 0) {
                item1 = (SampleItemViewDO)sm1.getObject(b1.sampleItemUid);
                item2 = (SampleItemViewDO)sm2.getObject(b2.sampleItemUid);
                compVal = item1.getItemSequence().compareTo(item2.getItemSequence());
                if (compVal == 0) {
                    ana1 = (AnalysisViewDO)sm1.getObject(b1.analysisUid);
                    ana2 = (AnalysisViewDO)sm2.getObject(b2.analysisUid);
                    compVal = ana1.getTestName().compareTo(ana2.getTestName());
                    if (compVal == 0)
                        compVal = ana1.getMethodName().compareTo(ana2.getMethodName());
                }
            }

            return compVal;
        }
    }
}