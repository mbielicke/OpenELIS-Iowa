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

import static org.openelis.modules.main.client.Logger.*;
import static org.openelis.ui.screen.Screen.ShortKeys.*;
import static org.openelis.ui.screen.State.*;
import static org.openelis.ui.screen.Screen.Validation.Status.VALID;

import java.util.ArrayList;
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
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.ProviderDO;
import org.openelis.domain.SampleItemViewDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.domain.SampleTestRequestVO;
import org.openelis.domain.SampleTestReturnVO;
import org.openelis.manager.AuxFieldGroupManager;
import org.openelis.manager.SampleManager1;
import org.openelis.manager.TestManager;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.auxData.client.AddAuxGroupEvent;
import org.openelis.modules.auxData.client.AuxDataTabUI;
import org.openelis.modules.auxData.client.RemoveAuxGroupEvent;
import org.openelis.modules.auxiliary.client.AuxiliaryService;
import org.openelis.modules.main.client.OpenELIS;
import org.openelis.modules.organization.client.OrganizationService;
import org.openelis.modules.project.client.ProjectService;
import org.openelis.modules.provider.client.ProviderService;
import org.openelis.modules.sample1.client.AddRowAnalytesEvent;
import org.openelis.modules.sample1.client.AddTestEvent;
import org.openelis.modules.sample1.client.AnalysisChangeEvent;
import org.openelis.modules.sample1.client.AnalysisNotesTabUI;
import org.openelis.modules.sample1.client.AnalysisTabUI;
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
import org.openelis.modules.test.client.TestService;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.Menu;
import org.openelis.ui.widget.MenuItem;
import org.openelis.ui.widget.ModalWindow;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.widget.TabLayoutPanel;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.calendar.Calendar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class NeonatalScreeningSampleLoginScreenUI extends Screen implements CacheProvider {

    @UiTemplate("NeonatalScreeningSampleLogin.ui.xml")
    interface NeonatalScreeningSampleLoginUiBinder
                                                  extends
                                                  UiBinder<Widget, NeonatalScreeningSampleLoginScreenUI> {
    };

    private static NeonatalScreeningSampleLoginUiBinder uiBinder = GWT.create(NeonatalScreeningSampleLoginUiBinder.class);

    protected SampleManager1                            manager;

    @UiField
    protected Calendar                                  collectionDate, collectionTime,
                    receivedDate, patientBirthDate, patientBirthTime, nextOfKinBirthDate,
                    transfusionDate;

    @UiField
    protected TextBox<Integer>                          accessionNumber, orderId, birthOrder,
                    gestationalAge, weight, transfusionAge, collectionAge;

    @UiField
    protected TextBox<String>                           clientReference, patientLastName,
                    patientFirstName, patientAddrMultipleUnit, patientAddrStreetAddress,
                    patientAddrCity, patientAddrZipCode, nextOfKinLastName, nextOfKinMiddleName,
                    nextOfKinFirstName, nextOfKinAddrHomePhone, nextOfKinAddrMultipleUnit,
                    nextOfKinAddrStreetAddress, nextOfKinAddrCity, nextOfKinAddrZipCode,
                    providerFirstName, formNumber;

    @UiField
    protected Dropdown<Integer>                         status, feedingId, patientGenderId,
                    patientRaceId, patientEthnicityId, nextOfKinRelationId, nextOfKinGenderId,
                    nextOfKinRaceId, nextOfKinEthnicityId;

    @UiField
    protected Dropdown<String>                          patientAddrState, nextOfKinAddrState;

    @UiField
    protected CheckBox                                  isNicu, isTransfused, isRepeat,
                    isCollectionValid;

    @UiField
    protected AutoComplete                              providerLastName, projectName,
                    reportToName, birthHospitalName;

    @UiField
    protected Button                                    query, previous, next, add, update, commit,
                    abort, optionsButton, orderLookupButton, projectButton, reportToButton,
                    birthHospitalButton;

    @UiField
    protected Menu                                      optionsMenu, historyMenu;

    @UiField
    protected MenuItem                                  duplicate, historySample,
                    historySampleNeonatal, historyPatient, historyNextOfKin, historySampleProject,
                    historySampleOrganization, historySampleItem, historyAnalysis,
                    historyCurrentResult, historyStorage, historySampleQA, historyAnalysisQA,
                    historyAuxData;

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

    protected ArrayList<SampleManager1>                 queriedList;

    protected int                                       queryIndex;

    protected boolean                                   canEdit;

    protected ModulePermission                          userPermission;

    protected NeonatalScreeningSampleLoginScreenUI      screen;

    protected HashMap<String, Object>                   cache;

    protected TestSelectionLookupUI                     testSelectionLookup;

    protected SampleProjectLookupUI                     sampleprojectLookUp;

    protected SampleOrganizationLookupUI                sampleOrganizationLookup;

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
                                           "qaevent_type");
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            window.close();
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

        initWidget(uiBinder.createAndBindUi(this));

        manager = null;

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
                add.setEnabled(isState(DEFAULT, DISPLAY) && userPermission.hasAddPermission());
                if (isState(ADD)) {
                    add.setPressed(true);
                    add.lock();
                } else {
                    add.setPressed(false);
                }
            }
        });
        addShortcut(add, 'a', CTRL);

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

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                optionsMenu.setEnabled(isState(DISPLAY));
                optionsButton.setEnabled(isState(DISPLAY));
                historyMenu.setEnabled(isState(DISPLAY));
            }
        });

        /*
         * option menu items
         */
        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                duplicate.setEnabled(isState(State.DISPLAY));
            }
        });

        duplicate.addCommand(new Command() {
            public void execute() {
                try {
                    manager = SampleService1.get().duplicate(manager.getSample().getId());                    
                    buildCache();
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    return;
                }
                evaluateEdit();
                setData();
                setState(ADD);
                fireDataChange();
                accessionNumber.setFocus(true);
                window.setDone(Messages.get().gen_enterInformationPressCommit());
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
                SampleHistoryUtility1.sample(manager, window);
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
                SampleHistoryUtility1.neonatal(manager, window);
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
                SampleHistoryUtility1.neonatalPatient(manager, window);
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
                SampleHistoryUtility1.neonatalNextOfKin(manager, window);
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
                SampleHistoryUtility1.project(manager, window);
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
                SampleHistoryUtility1.organization(manager, window);
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
                SampleHistoryUtility1.item(manager, window);
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
                SampleHistoryUtility1.analysis(manager, window);
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
                        SampleHistoryUtility1.currentResult(manager,
                                                            ((AnalysisViewDO)obj).getId(),
                                                            window);
                        return;
                    }
                }

                window.setError(Messages.get().result_historyException());
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
                SampleHistoryUtility1.storage(manager, window);
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
                SampleHistoryUtility1.sampleQA(manager, window);
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
                SampleHistoryUtility1.analysisQA(manager, window);
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
                SampleHistoryUtility1.auxData(manager, window);
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
                                 return forward ? orderId : clientReference;
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
                /*
                 * disabled until the functionality for the orders for this
                 * domain has been implemented
                 */
                // orderId.setEnabled(isState(QUERY) || (canEdit && isState(ADD,
                // UPDATE)));
                // orderId.setQueryMode(isState(QUERY));
                orderId.setEnabled(false);
            }

            public Widget onTab(boolean forward) {
                return forward ? collectionDate : accessionNumber;
            }
        });

        addStateChangeHandler(new StateChangeEvent.Handler() {
            public void onStateChange(StateChangeEvent event) {
                /*
                 * disabled until the functionality for the orders for this
                 * domain has been implemented
                 */
                //orderLookupButton.setEnabled(isState(DISPLAY) || (canEdit && isState(ADD, UPDATE)));
                orderLookupButton.setEnabled(false);
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
                                 return forward ? accessionNumber : status;
                             }
                         });

        addScreenHandler(patientLastName,
                         SampleMeta.getNeoPatientLastName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientLastName.setValue(getPatientLastName());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setPatientLastName(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientLastName.setEnabled(isState(QUERY) ||
                                                            (canEdit && isState(ADD, UPDATE)));
                                 patientLastName.setQueryMode(event.getState() == State.QUERY);
                             }
                         });

        addScreenHandler(patientFirstName,
                         SampleMeta.getNeoPatientFirstName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientFirstName.setValue(getPatientFirstName());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setPatientFirstName(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientFirstName.setEnabled(isState(QUERY) ||
                                                             (canEdit && isState(ADD, UPDATE)));
                                 patientFirstName.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(patientGenderId,
                         SampleMeta.getNeoPatientGenderId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientGenderId.setValue(getPatientGenderId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setPatientGenderId(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientGenderId.setEnabled(isState(QUERY) ||
                                                            (canEdit && isState(ADD, UPDATE)));
                                 patientGenderId.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(patientRaceId,
                         SampleMeta.getNeoPatientRaceId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientRaceId.setValue(getPatientRaceId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setPatientRaceId(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientRaceId.setEnabled(isState(QUERY) ||
                                                          (canEdit && isState(ADD, UPDATE)));
                                 patientRaceId.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(patientEthnicityId,
                         SampleMeta.getNeoPatientEthnicityId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientEthnicityId.setValue(getPatientEthnicityId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setPatientEthnicityId(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientEthnicityId.setEnabled(isState(QUERY) ||
                                                               (canEdit && isState(ADD, UPDATE)));
                                 patientEthnicityId.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(patientBirthDate,
                         SampleMeta.getNeoPatientBirthDate(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientBirthDate.setValue(getPatientBirthDate());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 setPatientBirthDate(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientBirthDate.setEnabled(isState(QUERY) ||
                                                             (canEdit && isState(ADD, UPDATE)));
                                 patientBirthDate.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(patientBirthTime,
                         SampleMeta.getNeoPatientBirthTime(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientBirthTime.setValue(getPatientBirthTime());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 setPatientBirthTime(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientBirthTime.setEnabled(isState(QUERY) ||
                                                             (canEdit && isState(ADD, UPDATE)));
                                 patientBirthTime.setQueryMode(event.getState() == State.QUERY);
                             }
                         });

        addScreenHandler(patientAddrMultipleUnit,
                         SampleMeta.getNeoPatientAddrMultipleUnit(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientAddrMultipleUnit.setValue(getPatientAddressMultipleUnit());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setPatientAddressMultipleUnit(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientAddrMultipleUnit.setEnabled(isState(QUERY) ||
                                                                    (canEdit && isState(ADD, UPDATE)));
                                 patientAddrMultipleUnit.setQueryMode(event.getState() == State.QUERY);
                             }
                         });

        addScreenHandler(patientAddrStreetAddress,
                         SampleMeta.getNeoPatientAddrStreetAddress(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientAddrStreetAddress.setValue(getPatientAddressStreetAddress());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setPatientAddressStreetAddress(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientAddrStreetAddress.setEnabled(isState(QUERY) ||
                                                                     (canEdit && isState(ADD,
                                                                                         UPDATE)));
                                 patientAddrStreetAddress.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(patientAddrCity,
                         SampleMeta.getNeoPatientAddrCity(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientAddrCity.setValue(getPatientAddressCity());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setPatientAddressCity(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientAddrCity.setEnabled(isState(QUERY) ||
                                                            (canEdit && isState(ADD, UPDATE)));
                                 patientAddrCity.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(patientAddrState,
                         SampleMeta.getNeoPatientAddrState(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientAddrState.setValue(getPatientAddressState());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setPatientAddressState(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientAddrState.setEnabled(isState(QUERY) ||
                                                             (canEdit && isState(ADD, UPDATE)));
                                 patientAddrState.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(patientAddrZipCode,
                         SampleMeta.getNeoPatientAddrZipCode(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 patientAddrZipCode.setValue(getPatientAddressZipCode());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setPatientAddressZipCode(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 patientAddrZipCode.setEnabled(isState(QUERY) ||
                                                               (canEdit && isState(ADD, UPDATE)));
                                 patientAddrZipCode.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(nextOfKinLastName,
                         SampleMeta.getNeoNextOfKinLastName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 nextOfKinLastName.setValue(getNextOfKinLastName());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setNextOfKinLastName(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 nextOfKinLastName.setEnabled(isState(QUERY) ||
                                                              (canEdit && isState(ADD, UPDATE)));
                                 nextOfKinLastName.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(nextOfKinMiddleName,
                         SampleMeta.getNeoNextOfKinMiddleName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 nextOfKinMiddleName.setValue(getNextOfKinMiddleName());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setNextOfKinMiddleName(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 nextOfKinMiddleName.setEnabled(isState(QUERY) ||
                                                                (canEdit && isState(ADD, UPDATE)));
                                 nextOfKinMiddleName.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(nextOfKinFirstName,
                         SampleMeta.getNeoNextOfKinFirstName(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 nextOfKinFirstName.setValue(getNextOfKinFirstName());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setNextOfKinFirstName(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 nextOfKinFirstName.setEnabled(isState(QUERY) ||
                                                               (canEdit && isState(ADD, UPDATE)));
                                 nextOfKinFirstName.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(nextOfKinRelationId,
                         SampleMeta.getNeoNextOfKinRelationId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 nextOfKinRelationId.setValue(getNeonatalNextOfKinRelationId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setNeonatalNextOfKinRelationId(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 nextOfKinRelationId.setEnabled(isState(QUERY) ||
                                                                (canEdit && isState(ADD, UPDATE)));
                                 nextOfKinRelationId.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(nextOfKinGenderId,
                         SampleMeta.getNeoNextOfKinGenderId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 nextOfKinGenderId.setValue(getNextOfKinGenderId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setNextOfKinGenderId(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 nextOfKinGenderId.setEnabled(isState(QUERY) ||
                                                              (canEdit && isState(ADD, UPDATE)));
                                 nextOfKinGenderId.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(nextOfKinRaceId,
                         SampleMeta.getNeoNextOfKinRaceId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 nextOfKinRaceId.setValue(getNextOfKinRaceId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setNextOfKinRaceId(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 nextOfKinRaceId.setEnabled(isState(QUERY) ||
                                                            (canEdit && isState(ADD, UPDATE)));
                                 nextOfKinRaceId.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(nextOfKinEthnicityId,
                         SampleMeta.getNeoNextOfKinEthnicityId(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 nextOfKinEthnicityId.setValue(getNextOfKinEthnicityId());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setNextOfKinEthnicityId(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 nextOfKinEthnicityId.setEnabled(isState(QUERY) ||
                                                                 (canEdit && isState(ADD, UPDATE)));
                                 nextOfKinEthnicityId.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(nextOfKinBirthDate,
                         SampleMeta.getNeoNextOfKinBirthDate(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 nextOfKinBirthDate.setValue(getNextOfKinBirthDate());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 setNextOfKinBirthDate(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 nextOfKinBirthDate.setEnabled(isState(QUERY) ||
                                                               (canEdit && isState(ADD, UPDATE)));
                                 nextOfKinBirthDate.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(nextOfKinAddrHomePhone,
                         SampleMeta.getNeoNextOfKinAddrHomePhone(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 nextOfKinAddrHomePhone.setValue(getNextOfKinAddressHomePhone());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setNextOfKinAddressHomePhone(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 nextOfKinAddrHomePhone.setEnabled(isState(QUERY) ||
                                                                   (canEdit && isState(ADD, UPDATE)));
                                 nextOfKinAddrHomePhone.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(nextOfKinAddrMultipleUnit,
                         SampleMeta.getNeoNextOfKinAddrMultipleUnit(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 nextOfKinAddrMultipleUnit.setValue(getNextOfKinAddressMultipleUnit());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setNextOfKinAddressMultipleUnit(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 nextOfKinAddrMultipleUnit.setEnabled(isState(QUERY) ||
                                                                      (canEdit && isState(ADD,
                                                                                          UPDATE)));
                                 nextOfKinAddrMultipleUnit.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(nextOfKinAddrStreetAddress,
                         SampleMeta.getNeoNextOfKinAddrStreetAddress(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 nextOfKinAddrStreetAddress.setValue(getNextOfKinAddressStreetAddress());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setNextOfKinAddressStreetAddress(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 nextOfKinAddrStreetAddress.setEnabled(isState(QUERY) ||
                                                                       (canEdit && isState(ADD,
                                                                                           UPDATE)));
                                 nextOfKinAddrStreetAddress.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(nextOfKinAddrCity,
                         SampleMeta.getNeoNextOfKinAddrCity(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 nextOfKinAddrCity.setValue(getNextOfKinAddressCity());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setNextOfKinAddressCity(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 nextOfKinAddrCity.setEnabled(isState(QUERY) ||
                                                              (canEdit && isState(ADD, UPDATE)));
                                 nextOfKinAddrCity.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(nextOfKinAddrState,
                         SampleMeta.getNeoNextOfKinAddrState(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 nextOfKinAddrState.setValue(getNextOfKinAddressState());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setNextOfKinAddressState(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 nextOfKinAddrState.setEnabled(isState(QUERY) ||
                                                               (canEdit && isState(ADD, UPDATE)));
                                 nextOfKinAddrState.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(nextOfKinAddrZipCode,
                         SampleMeta.getNeoNextOfKinAddrZipCode(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 nextOfKinAddrZipCode.setValue(getNextOfKinAddressZipCode());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setNextOfKinAddressZipCode(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 nextOfKinAddrZipCode.setEnabled(isState(QUERY) ||
                                                                 (canEdit && isState(ADD, UPDATE)));
                                 nextOfKinAddrZipCode.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(isNicu, SampleMeta.getNeoIsNicu(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isNicu.setValue(getIsNicu());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setIsNicu(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                isNicu.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                isNicu.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(birthOrder, SampleMeta.getNeoBirthOrder(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                birthOrder.setValue(getBirthOrder());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setBirthOrder(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                birthOrder.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                birthOrder.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(gestationalAge,
                         SampleMeta.getNeoGestationalAge(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 gestationalAge.setValue(getGestationalAge());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setGestationalAge(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 gestationalAge.setEnabled(isState(QUERY) ||
                                                           (canEdit && isState(ADD, UPDATE)));
                                 gestationalAge.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(feedingId, SampleMeta.getNeoFeedingId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                feedingId.setValue(getFeedingId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setFeedingId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                feedingId.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                feedingId.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(weight, SampleMeta.getNeoWeight(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                weight.setValue(getWeight());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setWeight(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                weight.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                weight.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(isTransfused,
                         SampleMeta.getNeoIsTransfused(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 isTransfused.setValue(getIsTransfused());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setIsTransfused(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 isTransfused.setEnabled(isState(QUERY) ||
                                                         (canEdit && isState(ADD, UPDATE)));
                                 isTransfused.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(transfusionDate,
                         SampleMeta.getNeoTransfusionDate(),
                         new ScreenHandler<Datetime>() {
                             public void onDataChange(DataChangeEvent event) {
                                 transfusionDate.setValue(getTransfusionDate());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 setTransfusionDate(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 transfusionDate.setEnabled(isState(QUERY) ||
                                                            (canEdit && isState(ADD, UPDATE)));
                                 transfusionDate.setQueryMode(isState(QUERY));
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
        });

        addScreenHandler(isRepeat, SampleMeta.getNeoIsRepeat(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isRepeat.setValue(getIsRepeat());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setIsRepeat(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                isRepeat.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                isRepeat.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(collectionAge,
                         SampleMeta.getCollectionAge(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 collectionAge.setValue(getNeonatalCollectionAge());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setCollectionAge(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 collectionAge.setEnabled(isState(QUERY) ||
                                                          (canEdit && isState(ADD, UPDATE)));
                                 collectionAge.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(isCollectionValid,
                         SampleMeta.getNeoIsCollectionValid(),
                         new ScreenHandler<String>() {
                             public void onDataChange(DataChangeEvent event) {
                                 isCollectionValid.setValue(getIsCollectionValid());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setIsCollectionValid(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 isCollectionValid.setEnabled(isState(QUERY) ||
                                                              (canEdit && isState(ADD, UPDATE)));
                                 isCollectionValid.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(providerLastName,
                         SampleMeta.getNeoProviderLastName(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 // TODO
                                 setProviderSelection();
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 // TODO
                                 getProviderFromSelection();
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 providerLastName.setEnabled(isState(QUERY) ||
                                                             (canEdit && isState(ADD, UPDATE)));
                                 providerLastName.setQueryMode(isState(QUERY));
                             }
                         });

        providerLastName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                ProviderDO data;
                ArrayList<ProviderDO> list;
                Item<Integer> row;
                ArrayList<Item<Integer>> model;

                window.setBusy();
                try {
                    list = ProviderService.get()
                                          .fetchByLastName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
                    model = new ArrayList<Item<Integer>>();
                    for (int i = 0; i < list.size(); i++ ) {
                        row = new Item<Integer>(3);
                        data = list.get(i);

                        row.setKey(data.getId());
                        row.setData(data);
                        row.setCell(0, data.getLastName());
                        row.setCell(1, data.getFirstName());
                        row.setCell(2, data.getMiddleName());

                        model.add(row);
                    }
                    providerLastName.showAutoMatches(model);
                } catch (Throwable e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
                window.clearStatus();
            }
        });

        addScreenHandler(providerFirstName,
                         SampleMeta.getNeoProviderFirstName(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 providerFirstName.setValue(getProviderFirstName());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 providerFirstName.setEnabled(isState(QUERY) ||
                                                              (canEdit && isState(ADD, UPDATE)));
                                 providerFirstName.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(projectName,
                         SampleMeta.getProjectName(),
                         new ScreenHandler<AutoCompleteValue>() {
                             public void onDataChange(DataChangeEvent event) {
                                 SampleProjectViewDO data;

                                 data = getFirstPermanentProject();
                                 if (data != null)
                                     projectName.setValue(data.getProjectId(),
                                                          data.getProjectName());
                                 else
                                     projectName.setValue(null, "");
                             }

                             public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                                 ProjectDO data;

                                 data = null;
                                 if (event.getValue() != null)
                                     data = (ProjectDO)event.getValue().getData();
                                 setProject(data);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 projectName.setEnabled(isState(QUERY) ||
                                                        (canEdit && isState(ADD, UPDATE)));
                                 projectName.setQueryMode(isState(QUERY));
                             }
                         });

        projectName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                Item<Integer> row;
                ArrayList<ProjectDO> list;
                ArrayList<Item<Integer>> model;

                window.setBusy();
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
                window.clearStatus();
            }
        });

        addScreenHandler(projectButton, "projectButton", new ScreenHandler<Integer>() {
            public void onStateChange(StateChangeEvent event) {
                projectButton.setEnabled(isState(DISPLAY) || (canEdit && isState(ADD, UPDATE)));
            }
        });

        addScreenHandler(reportToName,
                         SampleMeta.getOrgName(),
                         new ScreenHandler<AutoCompleteValue>() {
                             public void onDataChange(DataChangeEvent event) {
                                 SampleOrganizationViewDO data;

                                 data = getSampleOrganization(Constants.dictionary().ORG_REPORT_TO);

                                 if (data != null)
                                     reportToName.setValue(data.getOrganizationId(),
                                                           data.getOrganizationName());
                                 else 
                                     reportToName.setValue(null, "");
                             }

                             public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                                 OrganizationDO data;

                                 data = null;
                                 if (event.getValue() != null)
                                     data = (OrganizationDO)event.getValue().getData();
                                 setOrganization(Constants.dictionary().ORG_REPORT_TO, data);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 reportToName.setEnabled(isState(QUERY) ||
                                                         (canEdit && isState(ADD, UPDATE)));
                                 reportToName.setQueryMode(isState(QUERY));
                             }
                         });

        reportToName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                showOrganizationMatches(event.getMatch(), reportToName);
            }
        });

        addScreenHandler(reportToButton, "reportToButton", new ScreenHandler<Integer>() {
            public void onStateChange(StateChangeEvent event) {
                reportToButton.setEnabled(isState(DISPLAY) || (canEdit && isState(ADD, UPDATE)));
            }
        });

        addScreenHandler(birthHospitalName,
                         SampleMeta.getBillTo(),
                         new ScreenHandler<AutoCompleteValue>() {
                             public void onDataChange(DataChangeEvent event) {
                                 SampleOrganizationViewDO data;

                                 data = getSampleOrganization(Constants.dictionary().ORG_BIRTH_HOSPITAL);

                                 if (data != null)
                                     birthHospitalName.setValue(data.getOrganizationId(),
                                                                data.getOrganizationName());
                                 else 
                                     birthHospitalName.setValue(null, "");
                             }

                             public void onValueChange(ValueChangeEvent<AutoCompleteValue> event) {
                                 OrganizationDO data;

                                 data = null;
                                 if (event.getValue() != null)
                                     data = (OrganizationDO)event.getValue().getData();
                                 setOrganization(Constants.dictionary().ORG_BIRTH_HOSPITAL, data);
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 birthHospitalName.setEnabled(isState(QUERY) ||
                                                              (canEdit && isState(ADD, UPDATE)));
                                 birthHospitalName.setQueryMode(isState(QUERY));
                             }
                         });

        birthHospitalName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                // TODO
                showOrganizationMatches(event.getMatch(), birthHospitalName);
            }
        });

        addScreenHandler(birthHospitalButton, "birthHospitalButton", new ScreenHandler<Integer>() {
            public void onStateChange(StateChangeEvent event) {
                birthHospitalButton.setEnabled(isState(DISPLAY) ||
                                               (canEdit && isState(ADD, UPDATE)));
            }
        });

        addScreenHandler(formNumber, SampleMeta.getNeoFormNumber(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                formNumber.setValue(getFormNumber());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setFormNumber(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                formNumber.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                formNumber.setQueryMode(isState(QUERY));
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
                             public Object getQuery() {
                                 return null;
                             }
                         });

        addScreenHandler(sampleItemTab, "sampleItemTab", new ScreenHandler<Object>() {
            public Object getQuery() {
                return sampleItemTab.getQueryFields();
            }
        });

        addScreenHandler(analysisTab, "analysisTab", new ScreenHandler<Object>() {
            public Object getQuery() {
                return analysisTab.getQueryFields();
            }
        });

        addScreenHandler(resultTab, "resultTab", new ScreenHandler<Object>() {
            public Object getQuery() {
                return null;
            }
        });

        addScreenHandler(analysisNotesTab, "analysisNotesTab", new ScreenHandler<Object>() {
            public Object getQuery() {
                return null;
            }
        });

        addScreenHandler(sampleNotesTab, "sampleNotesTab", new ScreenHandler<Object>() {
            public Object getQuery() {
                return null;
            }
        });

        addScreenHandler(qaEventTab, "qaEventTab", new ScreenHandler<Object>() {
            public Object getQuery() {
                return null;
            }
        });

        addScreenHandler(auxDataTab, "auxDataTab", new ScreenHandler<Object>() {
            public Object getQuery() {
                return auxDataTab.getQueryFields();
            }
        });

        /*
         * handlers for the events fired by the tabs
         */

        bus.addHandler(AddTestEvent.getType(), new AddTestEvent.Handler() {
            @Override
            public void onAddTest(AddTestEvent event) {
                if (event.getSource() != screen)
                    addTests(event.getTests());
            }
        });

        bus.addHandler(RemoveAnalysisEvent.getType(), new RemoveAnalysisEvent.Handler() {
            @Override
            public void onAnalysisRemove(RemoveAnalysisEvent event) {
                AnalysisViewDO ana;

                if (screen == event.getSource())
                    return;

                ana = (AnalysisViewDO)manager.getObject(event.getUid());
                try {
                    manager = SampleService1.get().removeAnalysis(manager, ana.getId());
                    setData();
                    setState(state);
                    bus.fireEventFromSource(new RemoveAnalysisEvent(event.getUid()), screen);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        });

        bus.addHandler(AnalysisChangeEvent.getType(), new AnalysisChangeEvent.Handler() {
            @Override
            public void onAnalysisChange(AnalysisChangeEvent event) {
                AnalysisViewDO ana;
                SampleTestReturnVO ret;

                if (screen == event.getSource())
                    return;

                ana = (AnalysisViewDO)manager.getObject(event.getUid());
                ret = null;

                /*
                 * based on the field in the analysis being changed, call a
                 * specific service method
                 */
                try {
                    switch (event.getAction()) {
                        case METHOD_CHANGED:
                            ret = SampleService1.get().changeAnalysisMethod(manager,
                                                                            ana.getId(),
                                                                            event.getChangeId());
                            manager = ret.getManager();
                            break;
                        case STATUS_CHANGED:
                            manager = SampleService1.get()
                                                    .changeAnalysisStatus(manager,
                                                                          ana.getId(),
                                                                          event.getChangeId());
                            break;
                        case UNIT_CHANGED:
                            manager = SampleService1.get().changeAnalysisUnit(manager,
                                                                              ana.getId(),
                                                                              event.getChangeId());
                            break;
                        case PREP_CHANGED:
                            manager = SampleService1.get().changeAnalysisPrep(manager,
                                                                              ana.getId(),
                                                                              event.getChangeId());
                            break;
                    }

                    setData();
                    setState(state);

                    /*
                     * notify all tabs that need to refresh themselves because
                     * of the change in the analysis
                     */
                    bus.fireEventFromSource(new AnalysisChangeEvent(event.getUid(),
                                                                    event.getChangeId(),
                                                                    event.getAction()), this);
                    bus.fireEvent(new ResultChangeEvent());

                    showErrorsOrTests(ret);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        });

        bus.addHandler(AddRowAnalytesEvent.getType(), new AddRowAnalytesEvent.Handler() {
            @Override
            public void onAddRowAnalytes(AddRowAnalytesEvent event) {
                try {
                    manager = SampleService1.get().addRowAnalytes(manager,
                                                                  event.getAnalysis(),
                                                                  event.getAnalytes(),
                                                                  event.getIndexes());
                    setData();
                    setState(state);
                    bus.fireEvent(new ResultChangeEvent());
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        });

        bus.addHandler(AddAuxGroupEvent.getType(), new AddAuxGroupEvent.Handler() {
            @Override
            public void onAddAuxGroup(AddAuxGroupEvent event) {
                SampleTestReturnVO ret;
                ArrayList<Integer> ids;

                if (screen == event.getSource())
                    return;

                ids = event.getGroupIds();
                if (ids != null && ids.size() > 0) {
                    try {
                        ret = SampleService1.get().addAuxGroups(manager, ids);
                        manager = ret.getManager();
                        setData();
                        setState(state);
                        bus.fireEventFromSource(new AddAuxGroupEvent(ids), screen);
                        showErrorsOrTests(ret);
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                    }
                }
            }
        });

        bus.addHandler(RemoveAuxGroupEvent.getType(), new RemoveAuxGroupEvent.Handler() {
            @Override
            public void onRemoveAuxGroup(RemoveAuxGroupEvent event) {
                if (event.getGroupIds() != null && event.getGroupIds().size() > 0) {
                    if (screen == event.getSource())
                        return;

                    try {
                        manager = SampleService1.get()
                                                .removeAuxGroups(manager, event.getGroupIds());
                        setData();
                        setState(state);
                        bus.fireEventFromSource(new RemoveAuxGroupEvent(event.getGroupIds()),
                                                screen);
                    } catch (Exception e) {
                        Window.alert(e.getMessage());
                        logger.log(Level.SEVERE, e.getMessage(), e);
                    }
                }
            }
        });

        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                if (isState(ADD, UPDATE)) {
                    event.cancel();
                    window.setError(Messages.get().mustCommitOrAbort());
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

        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("gender")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }

        patientGenderId.setModel(model);
        nextOfKinGenderId.setModel(model);

        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("race")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }

        patientRaceId.setModel(model);
        nextOfKinRaceId.setModel(model);

        model = new ArrayList<Item<Integer>>();
        for (DictionaryDO d : CategoryCache.getBySystemName("ethnicity")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }

        patientEthnicityId.setModel(model);
        nextOfKinEthnicityId.setModel(model);

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

        nextOfKinRelationId.setModel(model);

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
         * the tab for aux data uses the cache in query state
         */
        cache = new HashMap<String, Object>();
        evaluateEdit();
        setData();
        setState(QUERY);
        fireDataChange();
        accessionNumber.setFocus(true);
        window.setDone(Messages.get().gen_enterFieldsToQuery());
    }

    // TODO make it like the other screens
    @UiHandler("previous")
    protected void previous(ClickEvent event) {
        if (queriedList == null || queryIndex - 1 < 0) {
            window.setError(Messages.get().noMoreRecordInDir());
            return;
        }

        manager = queriedList.get( --queryIndex);
        evaluateEdit();
        setData();
        setState(DISPLAY);
        fireDataChange();
        window.clearStatus();
    }

    // TODO make it like the other screens
    @UiHandler("next")
    protected void next(ClickEvent event) {
        if (queriedList == null || queryIndex + 1 == queriedList.size()) {
            window.setError(Messages.get().noMoreRecordInDir());
            return;
        }

        manager = queriedList.get( ++queryIndex);
        evaluateEdit();
        setData();
        setState(DISPLAY);
        fireDataChange();
        window.clearStatus();
    }

    /**
     * Puts the screen in add state and loads it with a new manager with some
     * fields with default values.
     */
    @UiHandler("add")
    protected void add(ClickEvent event) {
        /*
         * clear the query list
         */
        queriedList = null;

        try {
            manager = SampleService1.get().getInstance(Constants.domain().NEONATAL);
            cache = new HashMap<String, Object>();
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            return;
        }
        evaluateEdit();
        setData();
        setState(ADD);
        fireDataChange();
        accessionNumber.setFocus(true);
        window.setDone(Messages.get().gen_enterInformationPressCommit());
    }

    /**
     * Puts the screen in update state and loads it with a locked manager.
     * Builds the cache from the manager
     */
    @UiHandler("update")
    protected void update(ClickEvent event) {
        /*
         * clear the query list
         */
        queriedList = null;

        window.setBusy(Messages.get().lockForUpdate());
        try {
            manager = SampleService1.get().fetchForUpdate(manager.getSample().getId(),
                                                          SampleManager1.Load.ANALYSISUSER,
                                                          SampleManager1.Load.AUXDATA,
                                                          SampleManager1.Load.NOTE,
                                                          SampleManager1.Load.ORGANIZATION,
                                                          SampleManager1.Load.PROJECT,
                                                          SampleManager1.Load.QA,
                                                          SampleManager1.Load.RESULT,
                                                          SampleManager1.Load.STORAGE);
            buildCache();
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            window.clearStatus();
            return;
        }
        evaluateEdit();
        setData();
        setState(UPDATE);
        fireDataChange();
        orderId.setFocus(true);
        window.clearStatus();
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

        if (validation.getStatus() != VALID) {
            window.setError(Messages.get().correctErrors());
            return;
        }

        switch (super.state) {
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
        queriedList = null;
        window.setBusy(Messages.get().querying());
        try {
            queriedList = SampleService1.get().fetchByQuery(getQueryFields(),
                                                            0,
                                                            5,
                                                            SampleManager1.Load.ANALYSISUSER,
                                                            SampleManager1.Load.AUXDATA,
                                                            SampleManager1.Load.NOTE,
                                                            SampleManager1.Load.ORGANIZATION,
                                                            SampleManager1.Load.PROJECT,
                                                            SampleManager1.Load.QA,
                                                            SampleManager1.Load.RESULT,
                                                            SampleManager1.Load.STORAGE);
            queryIndex = 0;
            manager = queriedList.get(queryIndex);
            evaluateEdit();
            setData();
            setState(DISPLAY);
            fireDataChange();
            window.clearStatus();
        } catch (NotFoundException e) {
            manager = null;
            evaluateEdit();
            setData();
            setState(State.DEFAULT);
            fireDataChange();
            window.setDone(Messages.get().noRecordsFound());
        } catch (Exception e) {
            Window.alert("Error: neonatalsample call query failed; " + e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            window.setError(Messages.get().queryFailed());
        }
        cache = null;
    }

    /**
     * Calls the service method to commit the data on the screen, to the
     * database. Shows any errors/warnings encountered during the operation.
     * Otherwise loads the screen with the committed data.
     */
    protected void commitUpdate(boolean ignoreWarning) {
        if (state == ADD)
            window.setBusy(Messages.get().adding());
        else
            window.setBusy(Messages.get().updating());

        try {
            manager = SampleService1.get().update(manager, ignoreWarning);
            evaluateEdit();
            setData();
            setState(DISPLAY);
            fireDataChange();
            window.clearStatus();

            /*
             * the cache is set to null only if the add/update succeeds because
             * otherwise, it can't be used by any tabs if the user wants to
             * change any data
             */
            cache = null;
        } catch (ValidationErrorsList e) {
            showErrors(e);
            if ( !e.hasErrors() && e.hasWarnings() && !ignoreWarning)
                showWarnings(e);
        } catch (Exception e) {
            if (state == ADD)
                Window.alert("commitAdd(): " + e.getMessage());
            else
                Window.alert("commitUpdate(): " + e.getMessage());
            window.clearStatus();
        }
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
        window.setBusy(Messages.get().cancelChanges());

        if (state == QUERY) {
            try {
                manager = null;
                evaluateEdit();
                setData();
                setState(DEFAULT);
                fireDataChange();
                window.setDone(Messages.get().queryAborted());
            } catch (Exception e) {
                Window.alert(e.getMessage());
                window.clearStatus();
            }
        } else if (state == ADD) {
            try {
                if (manager.getSample().getId() != null) {
                    /*
                     * the screen was loaded from a Quick Entry sample which is
                     * still locked, thus it needs to be unlocked here
                     */
                    SampleService1.get().unlock(manager.getSample().getId());
                }
                manager = null;
                evaluateEdit();
                setData();
                setState(DEFAULT);
                fireDataChange();
                window.setDone(Messages.get().addAborted());
            } catch (Exception e) {
                Window.alert(e.getMessage());
                window.clearStatus();
            }
        } else if (state == UPDATE) {
            try {
                manager = SampleService1.get().unlock(manager.getSample().getId(),
                                                      SampleManager1.Load.ANALYSISUSER,
                                                      SampleManager1.Load.AUXDATA,
                                                      SampleManager1.Load.NOTE,
                                                      SampleManager1.Load.ORGANIZATION,
                                                      SampleManager1.Load.PROJECT,
                                                      SampleManager1.Load.QA,
                                                      SampleManager1.Load.RESULT,
                                                      SampleManager1.Load.STORAGE);
                evaluateEdit();
                setData();
                setState(DISPLAY);
                fireDataChange();
                window.setDone(Messages.get().updateAborted());
            } catch (Exception e) {
                Window.alert(e.getMessage());
                window.clearStatus();
            }
        }
        cache = null;
    }

    /**
     * Overridden to customize the list of query fields
     */
    public ArrayList<QueryData> getQueryFields() {
        ArrayList<QueryData> fields;
        QueryData field;

        fields = super.getQueryFields();
        field = new QueryData(SampleMeta.getDomain(),
                              QueryData.Type.STRING,
                              Constants.domain().NEONATAL);
        fields.add(field);

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
            cacheKey = "tm:" + key;
        else if (c == AuxFieldGroupManager.class)
            cacheKey = "am:" + key;

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
            logger.severe(e.getMessage());
        }
        return (T)obj;
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
                    SampleProjectViewDO data;
                    if (isState(ADD, UPDATE)) {
                        /*
                         * refresh the display of the autocomplete showing the
                         * project because the list of projects may have been
                         * changed through the popup
                         */
                        data = getFirstPermanentProject();
                        if (data != null)
                            projectName.setValue(data.getProjectId(), data.getProjectName());
                        else
                            projectName.setValue(null, "");
                    }
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
        canEdit = (manager != null && !Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample()
                                                                                            .getStatusId()));
    }

    /**
     * creates the cache of objects like TestManager that are used frequently by
     * the different parts of the screen
     */
    private void buildCache() throws Exception {
        int i, j;
        Integer prevId;
        ArrayList<Integer> ids;
        SampleItemViewDO item;
        AnalysisViewDO ana;
        AuxDataViewDO aux;
        ArrayList<TestManager> tms;
        ArrayList<AuxFieldGroupManager> afgms;

        cache = new HashMap<String, Object>();

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
    }

    /**
     * shows the warnings and asks the user to confirm committing the data with
     * warnings
     */
    private void showWarnings(ValidationErrorsList warnings) {
        StringBuilder b;

        b = new StringBuilder();
        b.append(Messages.get().gen_warningDialogLine1()).append("\n");
        for (Exception ex : warnings.getErrorList())
            b.append(" * ").append(ex.getMessage()).append("\n");

        b.append("\n").append(Messages.get().gen_warningDialogLastLine());
        if (Window.confirm(b.toString()))
            commitUpdate(true);
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
     * Calls the service method to change the accession number, if the new
     * number is not null or negative and the user confirms changing it. Loads
     * the screen with the returned manager.
     */
    private void setAccessionNumber(Integer accNum) {
        ValidationErrorsList errors;

        if (accNum == null || accNum < 0) {
            manager.getSample().setAccessionNumber(accNum);
            window.setError(Messages.get()
                                    .sample_accessionNumberNotValidException(DataBaseUtil.toInteger(accNum)));
            return;
        }

        if (getAccessionNumber() != null) {
            if ( !Window.confirm(Messages.get().accessionNumberEditConfirm())) {
                accessionNumber.setValue(getAccessionNumber());
                accessionNumber.setFocus(true);
                return;
            }
        }

        window.setBusy(Messages.get().fetching());
        try {
            manager = SampleService1.get().setAccessionNumber(manager, accNum);
            setData();
            fireDataChange();
            window.clearStatus();
        } catch (FormErrorException e) {
            errors = new ValidationErrorsList();
            errors.add(e);
            showErrors(errors);
            accessionNumber.setValue(getAccessionNumber());
            accessionNumber.setFocus(true);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            window.clearStatus();
        }
    }

    /**
     * returns the order id or null if the manager is null
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

        if (ordId == null) {
            manager.getSample().setOrderId(ordId);
            return;
        }

        if (getAccessionNumber() == null) {
            Window.alert(Messages.get().enterAccNumBeforeOrderLoad());
            orderId.setValue(null);
            return;
        }

        try {
            window.setBusy(Messages.get().fetching());

            ret = SampleService1.get().setOrderId(manager, ordId);
            manager = ret.getManager();
            setData();
            fireDataChange();
            window.clearStatus();
            showErrorsOrTests(ret);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
            window.clearStatus();
        }
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
     * returns the patient's last name or null if either the manager or the
     * patient DO is null
     */
    private String getPatientLastName() {
        if (manager == null || manager.getSampleNeonatal().getPatient() == null)
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
        if (manager == null || manager.getSampleNeonatal().getPatient() == null)
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
     * returns the patient's gender or null if either the manager or the patient
     * DO is null
     */
    private Integer getPatientGenderId() {
        if (manager == null || manager.getSampleNeonatal().getPatient() == null)
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
        if (manager == null || manager.getSampleNeonatal().getPatient() == null)
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
        if (manager == null || manager.getSampleNeonatal().getPatient() == null)
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
     * returns the patient's birth date or null if either the manager or the
     * patient DO is null
     */
    private Datetime getPatientBirthDate() {
        if (manager == null || manager.getSampleNeonatal().getPatient() == null)
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
        if (manager == null || manager.getSampleNeonatal().getPatient() == null)
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
        if (manager == null || manager.getSampleNeonatal().getPatient() == null)
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
        if (manager == null || manager.getSampleNeonatal().getPatient() == null)
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
        if (manager == null || manager.getSampleNeonatal().getPatient() == null)
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
        if (manager == null || manager.getSampleNeonatal().getPatient() == null)
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
        if (manager == null || manager.getSampleNeonatal().getPatient() == null)
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
     * returns next of kin's last name or null if either the manager or next of
     * kin DO is null
     */
    private String getNextOfKinLastName() {
        if (manager == null || manager.getSampleNeonatal().getNextOfKin() == null)
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
        if (manager == null || manager.getSampleNeonatal().getNextOfKin() == null)
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
        if (manager == null || manager.getSampleNeonatal().getNextOfKin() == null)
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
        if (manager == null)
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
        if (manager == null || manager.getSampleNeonatal().getNextOfKin() == null)
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
        if (manager == null || manager.getSampleNeonatal().getNextOfKin() == null)
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
        if (manager == null || manager.getSampleNeonatal().getNextOfKin() == null)
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
        if (manager == null)
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
     * returns next of kin's home phone or null if either the manager or next of
     * kin DO is null
     */
    private String getNextOfKinAddressHomePhone() {
        if (manager == null || manager.getSampleNeonatal().getNextOfKin() == null)
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
        if (manager == null || manager.getSampleNeonatal().getNextOfKin() == null)
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
        if (manager == null || manager.getSampleNeonatal().getNextOfKin() == null)
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
        if (manager == null || manager.getSampleNeonatal().getNextOfKin() == null)
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
        if (manager == null || manager.getSampleNeonatal().getNextOfKin() == null)
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
        if (manager == null || manager.getSampleNeonatal().getNextOfKin() == null)
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
     * returns next of kin's zip code or null if either the manager or next of
     * kin DO is null
     */
    private String getIsNicu() {
        if (manager == null || manager.getSampleNeonatal().getNextOfKin() == null)
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
        if (manager == null)
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
        if (manager == null)
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
        if (manager == null)
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
        if (manager == null)
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
        if (manager == null)
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
        if (manager == null)
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
        if (manager == null)
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
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getCollectionAge();
    }

    /**
     * sets the collection age
     */
    private void setCollectionAge(Integer collectionAge) {
        manager.getSampleNeonatal().setCollectionAge(collectionAge);
    }

    /**
     * returns whether the collection is valid or null if the manager is null
     */
    private String getIsCollectionValid() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getIsCollectionValid();
    }

    /**
     * sets whether the collection is valid
     */
    private void setIsCollectionValid(String isCollectionValid) {
        manager.getSampleNeonatal().setIsCollectionValid(isCollectionValid);
    }

    private Integer getProviderId() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getProviderId();
    }

    private void setProviderId(Integer providerId) {
        manager.getSampleNeonatal().setProviderId(providerId);
    }

    private String getNeonatalProviderLastName() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getProviderLastName();
    }

    private void setNeonatalProviderLastName(String providerlastName) {
        manager.getSampleNeonatal().setProviderlastName(providerlastName);
    }

    private String getNeonatalProviderFirstName() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getProviderFirstName();
    }

    private void setNeonatalProviderFirstName(String providerFirstName) {
        manager.getSampleNeonatal().setProviderFirstName(providerFirstName);
    }

    private void setProviderSelection() {
        if (manager == null) {
            providerLastName.setValue(null, "");
            return;
        }

        providerLastName.setValue(getProviderId(), getNeonatalProviderLastName());
        providerFirstName.setValue(getNeonatalProviderFirstName());
    }

    private void getProviderFromSelection() {
        ProviderDO data;
        AutoCompleteValue row;

        row = providerLastName.getValue();
        if (row == null || row.getId() == null) {
            setProviderId(null);
            setProviderFirstName(null);
            setNeonatalProviderLastName(null);

            providerLastName.setValue(null, "");
            providerFirstName.setValue("");
            return;
        } else {
            data = (ProviderDO)row.getData();

            setProviderId(data.getId());
            setNeonatalProviderFirstName(data.getFirstName());
            setNeonatalProviderLastName(data.getLastName());

            providerLastName.setValue(getProviderId(), getNeonatalProviderLastName());
            providerFirstName.setValue(getNeonatalProviderFirstName());
        }
    }

    private void setProviderFirstName(String name) {
        manager.getSampleNeonatal().setProviderFirstName(name);
    }

    private String getProviderFirstName() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getProviderFirstName();
    }

    /**
     * Adds or updates the first permanent project to the sample if the argument
     * is not null, otherwise deletes the first permanent project. Also
     * refreshes the display of the autocomplete.
     */
    private void setProject(ProjectDO proj) {
        SampleProjectViewDO data;

        data = getFirstPermanentProject();
        if (proj == null) {
            /*
             * if a project was not selected and if there were permanent
             * projects present then the first permanent project is deleted and
             * the next permanent project is set as the first one
             */
            if (data != null)
                manager.project.remove(data);
        } else {
            /*
             * otherwise the first permanent project is modified or a new one is
             * created if no project existed
             */
            if (data == null) {
                data = manager.project.add();
                data.setIsPermanent("Y");
            }
            data.setProjectId(proj.getId());
            data.setProjectName(proj.getName());
            data.setProjectDescription(proj.getDescription());
        }

        if (proj != null)
            projectName.setValue(proj.getId(), proj.getName());
        else
            projectName.setValue(null, "");
    }

    /**
     * returns the first permanent project from the manager, or null if the
     * manager is null or it doesn't have any permanent projects
     */
    private SampleProjectViewDO getFirstPermanentProject() {
        ArrayList<SampleProjectViewDO> p;

        if (manager == null)
            return null;

        p = manager.project.getByType("Y");
        if (p != null && p.size() > 0)
            return p.get(0);
        return null;
    }

    /**
     * Adds or updates the sample organization of the specified type if the
     * passed organization is not null, otherwise deletes the sample
     * organization of this type. Also refreshes the display of the autocomplete
     * showing the organization of this type.
     */
    private void setOrganization(Integer type, OrganizationDO org) {
        Integer id;
        String name;
        SampleOrganizationViewDO data;

        data = getSampleOrganization(type);
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
                 * the organization was changed, thus the sample organization
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

            id = null;
            name = null;
            if (org != null) {
                id = org.getId();
                name = org.getName();
                /*
                 * warn the user if samples from this organization are to held
                 * or refused
                 */
                try {
                    showHoldRefuseWarning(id, name);
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }

            if (Constants.dictionary().ORG_REPORT_TO.equals(type))
                reportToName.setValue(id, name);
            else if (Constants.dictionary().ORG_BIRTH_HOSPITAL.equals(type))
                birthHospitalName.setValue(id, name);
        }
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
     * warn the user if samples from this organization are to held or refused
     */
    private void showHoldRefuseWarning(Integer orgId, String name) throws Exception {
        if (SampleOrganizationUtility1.isHoldRefuseSampleForOrg(orgId))
            Window.alert(Messages.get().gen_orgMarkedAsHoldRefuseSample(name));
    }

    /**
     * shows the popup for this sample's organizations
     */
    private void showOrganizationLookup() {
        ModalWindow modal;

        if (sampleOrganizationLookup == null) {
            sampleOrganizationLookup = new SampleOrganizationLookupUI() {
                @Override
                public void ok() {
                    SampleOrganizationViewDO data;
                    if (isState(ADD, UPDATE)) {
                        /*
                         * refresh the display of the autocompletes showing
                         * organizations because the list of organizations may
                         * have been changed through the popup
                         */
                        data = getSampleOrganization(Constants.dictionary().ORG_REPORT_TO);
                        if (data != null)
                            reportToName.setValue(data.getOrganizationId(),
                                                  data.getOrganizationName());
                        else
                            reportToName.setValue(null, "");

                        data = getSampleOrganization(Constants.dictionary().ORG_BIRTH_HOSPITAL);
                        if (data != null)
                            birthHospitalName.setValue(data.getOrganizationId(),
                                                       data.getOrganizationName());
                        else
                            birthHospitalName.setValue(null, "");
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
     * shows the organizations whose name or id matches the string
     */
    private void showOrganizationMatches(String match, AutoComplete widget) {
        Item<Integer> row;
        OrganizationDO data;
        ArrayList<OrganizationDO> list;
        ArrayList<Item<Integer>> model;

        window.setBusy();
        try {
            list = OrganizationService.get()
                                      .fetchByIdOrName(QueryFieldUtil.parseAutocomplete(match));
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
            widget.showAutoMatches(model);
        } catch (Throwable e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        window.clearStatus();
    }

    /**
     * returns the organization of the specified type from the manager or null
     * if the manager is null or doesn't have an organization of this type
     */
    private SampleOrganizationViewDO getSampleOrganization(Integer type) {
        ArrayList<SampleOrganizationViewDO> orgs;

        if (manager == null)
            return null;

        orgs = manager.organization.getByType(type);
        if (orgs != null && orgs.size() > 0)
            return orgs.get(0);

        return null;
    }

    /**
     * Calls the service method to add the tests/panels in the list, to the
     * sample. If there were any errors during the operation then shows them or
     * shows the popup for selecting prep/reflex tests for the added tests. Also
     * notifies the tabs to reload themselves.
     */
    private void addTests(ArrayList<SampleTestRequestVO> tests) {
        SampleTestReturnVO ret;

        try {
            ret = SampleService1.get().addTests(manager, tests);
            manager = ret.getManager();
            setData();
            setState(state);
            bus.fireEventFromSource(new AddTestEvent(tests), this);
            showErrorsOrTests(ret);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Called whenever a service method like "addTests()" returns errors and/or
     * a list of prep/reflex tests to be selected for the tests added/changed
     * during the operation. Shows the errors or the popup for selecting the
     * prep/reflex tests.
     */
    private void showErrorsOrTests(SampleTestReturnVO ret) {
        if (ret == null)
            return;

        if (ret.getErrors() != null && ret.getErrors().size() > 0)
            showErrors(ret.getErrors());
        else if (ret.getTests() != null && ret.getTests().size() > 0)
            showTestSelectionLookup(ret);
    }

    /**
     * shows the pop up screen for prep/reflex tests loaded using the manager
     */
    private void showTestSelectionLookup(SampleTestReturnVO ret) {
        ModalWindow modal;

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
                    if (tests != null && tests.size() > 0)
                        addTests(tests);
                }
            };
        }

        modal = new ModalWindow();
        modal.setSize("520px", "350px");
        modal.setName(Messages.get().testSelection_prepTestSelection());
        modal.setCSS(UIResources.INSTANCE.popupWindow());
        modal.setContent(testSelectionLookup);

        testSelectionLookup.setData(manager, ret.getTests());
        testSelectionLookup.setWindow(modal);
    }
}