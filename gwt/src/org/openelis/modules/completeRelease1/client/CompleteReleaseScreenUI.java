package org.openelis.modules.completeRelease1.client;

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.Screen.ShortKeys.*;
import static org.openelis.ui.screen.State.*;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.Constants;
import org.openelis.manager.SampleManager1;
import org.openelis.modules.sample1.client.SampleHistoryUtility1;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Menu;
import org.openelis.ui.widget.MenuItem;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;

public class CompleteReleaseScreenUI extends Screen {

    @UiTemplate("CompleteRelease.ui.xml")
    interface CompleteReleaseUiBinder extends UiBinder<Widget, CompleteReleaseScreenUI> {
    };

    private static CompleteReleaseUiBinder uiBinder = GWT.create(CompleteReleaseUiBinder.class);

    @UiField
    protected Button                       query, update, commit, complete, release, abort,
                    optionsButton;

    @UiField
    protected Menu                         optionsMenu, historyMenu;

    @UiField
    protected MenuItem                     historySample, historySampleNeonatal, historyPatient,
                    historyNextOfKin, historySampleProject, historySampleOrganization,
                    historySampleItem, historyAnalysis, historyCurrentResult, historyStorage,
                    historySampleQA, historyAnalysisQA, historyAuxData;

    protected SampleManager1               manager;

    protected boolean                      canEdit;

    protected ModulePermission             userPermission;

    protected CompleteReleaseScreenUI      screen;

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
                                           "organization_type");
        } catch (Exception e) {
            window.close();
            throw e;
        }

        initWidget(uiBinder.createAndBindUi(this));

        manager = null;

        initialize();
        evaluateEdit();
        setData();
        setState(DEFAULT);
        fireDataChange();

        logger.fine("Complete Release Screen Opened");
    }

    private void initialize() {
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
                optionsMenu.setEnabled(isState(DEFAULT));
                optionsButton.setEnabled(isState(DEFAULT));
                historyMenu.setEnabled(isState(DEFAULT));
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
                 
                uid = sampleItemAnalysisTreeTab.getSelectedUid();
                if (uid != null) {
                    obj = manager.getObject(uid);
                    if (obj instanceof AnalysisViewDO) {
                        SampleHistoryUtility1.currentResult(manager, ((AnalysisViewDO)obj).getId());
                        return;
                    }
                }

                setError(Messages.get().result_historyException());*/
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
    }

    /**
     * Sets the latest manager in the tabs
     */
    private void setData() {
    }

    /**
     * determines if the fields on the screen can be edited based on the data
     */
    private void evaluateEdit() {
        canEdit = (manager != null && !Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample()
                                                                                            .getStatusId()));
    }
}