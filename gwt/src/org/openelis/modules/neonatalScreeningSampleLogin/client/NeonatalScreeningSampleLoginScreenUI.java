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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.openelis.cache.CacheProvider;
import org.openelis.cache.CategoryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AddressDO;
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
import org.openelis.modules.auxData.client.AuxDataChangeEvent;
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
import org.openelis.modules.sample1.client.QAEventsTabUI;
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
    protected Calendar                                  collectionDate, collectionTime, receivedDate,
                    patientBirthDate, patientBirthTime, nextOfKinBirthDate, transfusionDate;

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
                    abort, orderLookupButton, projectButton, reportToButton, birthHospitalButton;

    @UiField
    protected MenuItem                                  duplicate, historySample,
                    historySampleProject, historySampleItem, historyAnalysis, historyCurrentResult,
                    historyStorage, historySampleQA, historyAnalysisQA, historyAuxData;

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
    protected QAEventsTabUI                             qaEventTab;

    @UiField(provided = true)
    protected AuxDataTabUI                              auxDataTab;

    protected ArrayList<SampleManager1>                 queriedList;

    protected int                                       queryIndex;

    protected boolean                                   canEdit;

    protected ModulePermission                          userPermission;

    protected NeonatalScreeningSampleLoginScreenUI      screen;

    protected SampleHistoryUtility1                     historyUtility;

    protected HashMap<String, Object>                   cache;

    protected TestSelectionLookupUI                     testSelectionLookup;

    protected SampleProjectLookupUI                     sampleprojectLookUp;

    protected SampleOrganizationLookupUI                sampleOrganizationLookup;

    protected Long                                      day      = 86400000L;

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

        sampleItemAnalysisTreeTab = new SampleItemAnalysisTreeTabUI(this, bus);
        sampleItemTab = new SampleItemTabUI(this, bus);
        analysisTab = new AnalysisTabUI(this, bus);
        resultTab = new ResultTabUI(this, bus);
        analysisNotesTab = new AnalysisNotesTabUI(this, bus);
        sampleNotesTab = new SampleNotesTabUI(this, bus);
        storageTab = new StorageTabUI(this, bus);
        qaEventTab = new QAEventsTabUI(this, bus);

        auxDataTab = new AuxDataTabUI(this, bus) {
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
                historySample();
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
                historySampleProject();
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
                historySampleItem();
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
                historyAnalysis();
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
                historyCurrentResult();
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
                historyStorage();
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
                historySampleQA();
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
                historyAnalysisQA();
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
                historyAuxData();
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
                orderId.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                orderId.setQueryMode(isState(QUERY));
            }

            public Widget onTab(boolean forward) {
                return forward ? collectionDate : accessionNumber;
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
                isNicu.setValue(getNeonatalIsNicu());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setNeonatalIsNicu(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                isNicu.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                isNicu.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(birthOrder, SampleMeta.getNeoBirthOrder(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                birthOrder.setValue(getNeonatalBirthOrder());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setNeonatalBirthOrder(event.getValue());
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
                                 gestationalAge.setValue(getNeonatalGestationalAge());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setNeonatalGestationalAge(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 gestationalAge.setEnabled(isState(QUERY) ||
                                                           (canEdit && isState(ADD, UPDATE)));
                                 gestationalAge.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(feedingId, SampleMeta.getNeoFeedingId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                feedingId.setValue(getNeonatalFeedingId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setNeonatalFeedingId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                feedingId.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                feedingId.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(weight, SampleMeta.getNeoWeight(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                weight.setValue(getNeonatalWeight());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setNeonatalWeight(event.getValue());
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
                                 isTransfused.setValue(getNeonatalIsTransfused());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setNeonatalIsTransfused(event.getValue());
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
                                 transfusionDate.setValue(getNeonatalTransfusionDate());
                             }

                             public void onValueChange(ValueChangeEvent<Datetime> event) {
                                 setNeonatalTransfusionDate(event.getValue());
                             }

                             public void onStateChange(StateChangeEvent event) {
                                 transfusionDate.setEnabled(isState(QUERY) ||
                                                            (canEdit && isState(ADD, UPDATE)));
                                 transfusionDate.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(transfusionAge, "transfusionAge", new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                transfusionAge.setValue(getNeonatalTransfusionAge());
            }

            public void onStateChange(StateChangeEvent event) {
                transfusionAge.setEnabled(isState(QUERY));
                transfusionAge.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(isRepeat, SampleMeta.getNeoIsRepeat(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isRepeat.setValue(getNeonatalIsRepeat());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setNeonatalIsRepeat(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                isRepeat.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                isRepeat.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(collectionAge,
                         SampleMeta.getNeoCollectionAge(),
                         new ScreenHandler<Integer>() {
                             public void onDataChange(DataChangeEvent event) {
                                 collectionAge.setValue(getNeonatalCollectionAge());
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
                                 setNeonatalCollectionAge(event.getValue());
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
                                 isCollectionValid.setValue(getNeonatalIsCollectionValid());
                             }

                             public void onValueChange(ValueChangeEvent<String> event) {
                                 setNeonatalIsCollectionValid(event.getValue());
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
                                 setProviderSelection();
                             }

                             public void onValueChange(ValueChangeEvent<Integer> event) {
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
                getProviderMatches(event);
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
                                 providerLastName.setQueryMode(isState(QUERY));
                             }
                         });

        addScreenHandler(projectName, SampleMeta.getProjectName(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                setProjectSelection();
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                getProjectFromSelection();
            }

            public void onStateChange(StateChangeEvent event) {
                projectName.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                projectName.setQueryMode(isState(QUERY));
            }
        });

        projectName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                getProjectMatches(event);
            }
        });

        addScreenHandler(projectButton, "projectButton", new ScreenHandler<Integer>() {
            public void onStateChange(StateChangeEvent event) {
                projectButton.setEnabled(isState(DISPLAY) || (canEdit && isState(ADD, UPDATE)));
            }
        });

        addScreenHandler(reportToName, SampleMeta.getOrgName(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                setOrganizationSelection(reportToName, Constants.dictionary().ORG_REPORT_TO);
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                getOrganizationFromSelection(reportToName, Constants.dictionary().ORG_REPORT_TO);
            }

            public void onStateChange(StateChangeEvent event) {
                reportToName.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                reportToName.setQueryMode(isState(QUERY));
            }
        });

        reportToName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                getOrganizationMatches(event.getMatch(), reportToName);
            }
        });

        addScreenHandler(reportToButton, "reportToButton", new ScreenHandler<Integer>() {
            public void onStateChange(StateChangeEvent event) {
                reportToButton.setEnabled(isState(DISPLAY) || (canEdit && isState(ADD, UPDATE)));
            }
        });

        addScreenHandler(birthHospitalName, SampleMeta.getBillTo(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                setOrganizationSelection(birthHospitalName,
                                         Constants.dictionary().ORG_BIRTH_HOSPITAL);
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                getOrganizationFromSelection(birthHospitalName,
                                             Constants.dictionary().ORG_BIRTH_HOSPITAL);
            }

            public void onStateChange(StateChangeEvent event) {
                birthHospitalName.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                birthHospitalName.setQueryMode(isState(QUERY));
            }
        });

        birthHospitalName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                getOrganizationMatches(event.getMatch(), birthHospitalName);
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
                formNumber.setValue(getNeonatalFormNumber());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setNeonatalFormNumber(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                formNumber.setEnabled(isState(QUERY) || (canEdit && isState(ADD, UPDATE)));
                formNumber.setQueryMode(isState(QUERY));
            }
        });

        /*
         * tabs
         */
        tabPanel.setPopoutBrowser(OpenELIS.getBrowser());
        
        addScreenHandler(sampleItemAnalysisTreeTab, "sampleItemAnalysisTreeTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                /*
                 * ignore because the the tab gets refreshed only when some or
                 * no node in the tree is selected
                 */
            }

            public Object getQuery() {
                return null;
            }
        });

        addScreenHandler(sampleItemTab, "sampleItemTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                /*
                 * ignore because the the tab gets refreshed only when some or
                 * no node in the tree is selected
                 */
            }

            public void onStateChange(StateChangeEvent event) {
                /*
                 * ignore because the tab has its own handler for this event
                 */
            }

            public Object getQuery() {
                return sampleItemTab.getQueryFields();
            }
        });

        addScreenHandler(analysisTab, "analysisTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                /*
                 * ignore because the the tab gets refreshed only when some or
                 * no node in the tree is selected
                 */
            }

            public void onStateChange(StateChangeEvent event) {
                /*
                 * ignore because the tab has its own handler for this event
                 */
            }

            public Object getQuery() {
                return analysisTab.getQueryFields();
            }
        });

        addScreenHandler(resultTab, "resultTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                /*
                 * ignore because the the tab gets refreshed only when some or
                 * no node in the tree is selected
                 */
            }

            public void onStateChange(StateChangeEvent event) {
                /*
                 * ignore because the tab has its own handler for this event
                 */
            }

            public Object getQuery() {
                return null;
            }
        });

        addScreenHandler(analysisNotesTab, "analysisNotesTab", new ScreenHandler<Object>() {
            public void onDataChange(DataChangeEvent event) {
                /*
                 * ignore because the the tab gets refreshed only when some or
                 * no node in the tree is selected
                 */
            }

            public void onStateChange(StateChangeEvent event) {
                /*
                 * ignore because the tab has its own handler for this event
                 */
            }

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

        bus.addHandler(AddTestEvent.getType(), new AddTestEvent.Handler() {
            public void onAddTest(AddTestEvent event) {
                addTests(event.getTests());
            }
        });

        bus.addHandler(AnalysisChangeEvent.getType() , new AnalysisChangeEvent.Handler() {
            @Override
            public void onAnalysisChange(AnalysisChangeEvent event) {
                if (screen == event.getSource())
                    return;
                    
                switch (event.getAction()) {
                    case METHOD_CHANGED:
                        changeAnalysisMethod(event.getUid(), event.getChangeId());
                        break;
                    case STATUS_CHANGED:
                        break;
                    case UNIT_CHANGED:
                        changeAnalysisUnit(event.getUid(), event.getChangeId());
                        break;
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
                    bus.fireEvent(new ResultChangeEvent(ResultChangeEvent.Action.ROW_ANALYTE_ADDED));
                } catch (Exception e) {
                    Window.alert(e.getMessage());
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        });

        bus.addHandler(AddAuxGroupEvent.getType(), new AddAuxGroupEvent.Handler() {
            @Override
            public void onAddAuxGroup(AddAuxGroupEvent event) {
                if (event.getGroupIds() != null && event.getGroupIds().size() > 0) {
                    try {
                        manager = SampleService1.get().addAuxGroups(manager, event.getGroupIds());
                        setData();
                        setState(state);
                        bus.fireEvent(new AuxDataChangeEvent());
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
                    try {
                        manager = SampleService1.get()
                                                .removeAuxGroups(manager, event.getGroupIds());
                        setData();
                        setState(state);
                        bus.fireEvent(new AuxDataChangeEvent());
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
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO d : CategoryCache.getBySystemName("sample_status")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }

        status.setModel(model);

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO d : CategoryCache.getBySystemName("gender")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }

        patientGenderId.setModel(model);
        nextOfKinGenderId.setModel(model);

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO d : CategoryCache.getBySystemName("race")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }

        patientRaceId.setModel(model);
        nextOfKinRaceId.setModel(model);

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO d : CategoryCache.getBySystemName("ethnicity")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }

        patientEthnicityId.setModel(model);
        nextOfKinEthnicityId.setModel(model);

        stmodel = new ArrayList<Item<String>>();
        stmodel.add(new Item<String>(null, ""));
        for (DictionaryDO d : CategoryCache.getBySystemName("state")) {
            strow = new Item<String>(d.getEntry(), d.getEntry());
            strow.setEnabled( ("Y".equals(d.getIsActive())));
            stmodel.add(strow);
        }

        patientAddrState.setModel(stmodel);
        nextOfKinAddrState.setModel(stmodel);

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO d : CategoryCache.getBySystemName("patient_relation")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }

        nextOfKinRelationId.setModel(model);

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
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
        window.setDone(Messages.get().enterFieldsToQuery());
    }

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
        window.setDone(Messages.get().enterInformationPressCommit());
    }

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
            logger.severe(e.getMessage());
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

    @UiHandler("commit")
    protected void commit(ClickEvent event) {
        finishEditing();

        if ( !validate()) {
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
            cache = null;
        } catch (ValidationErrorsList e) {
            showErrors(e);
            if ( !e.hasErrors() && e.hasWarnings() && !ignoreWarning)
                showWarningsDialog(e);
        } catch (Exception e) {
            if (state == ADD)
                Window.alert("commitAdd(): " + e.getMessage());
            else
                Window.alert("commitUpdate(): " + e.getMessage());
            window.clearStatus();
        }
    }

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

    @UiHandler("projectButton")
    protected void project(ClickEvent event) {
        ModalWindow modal;

        if (sampleprojectLookUp == null) {
            sampleprojectLookUp = new SampleProjectLookupUI() {
                @Override
                public void ok() {
                    if (isState(ADD, UPDATE)) {
                        /*
                         * refresh the display of the autocomplete showing the
                         * project because the list of projects may have been
                         * changed through the popup
                         */
                        setProjectSelection();
                    }
                }
            };
        }

        modal = new ModalWindow();
        modal.setSize("530px", "400px");
        modal.setName(Messages.get().sampleProject());
        modal.setCSS(UIResources.INSTANCE.popupWindow());
        modal.setContent(sampleprojectLookUp);

        sampleprojectLookUp.setWindow(modal);
        sampleprojectLookUp.setData(manager, state);
    }

    @UiHandler("reportToButton")
    protected void reportTo(ClickEvent event) {
        showOrganizationLookup();
    }

    @UiHandler("birthHospitalButton")
    protected void birthHospital(ClickEvent event) {
        showOrganizationLookup();
    }

    private void historySample() {
        historyUtility.setManager(manager);
        historyUtility.historySampleProject();
    }

    private void historySampleProject() {
        historyUtility.setManager(manager);
        historyUtility.historySampleProject();
    }

    private void historySampleItem() {
        historyUtility.setManager(manager);
        historyUtility.historySampleItem();
    }

    private void historyAnalysis() {
        historyUtility.setManager(manager);
        historyUtility.historyAnalysis();
    }

    private void historyCurrentResult() {
        historyUtility.setManager(manager);
        /*
         * Since the analyses shown on this screen are managed by
         * SampleItemAnalysisTreeTab, this screen has no knowledge of the
         * analysis selected by the user to see the history of its results. So
         * an ActionEvent is fired so that SampleItemAnalysisTreeTab can find
         * the desired analysis and show the history.
         */
        // ActionEvent.fire(screen, ResultTabUI.Action.RESULT_HISTORY, null);
    }

    private void historyStorage() {
        historyUtility.setManager(manager);
        historyUtility.historyStorage();
    }

    private void historySampleQA() {
        historyUtility.setManager(manager);
        historyUtility.historySampleQA();
    }

    private void historyAnalysisQA() {
        historyUtility.setManager(manager);
        historyUtility.historyAnalysisQA();
    }

    private void historyAuxData() {
        historyUtility.setManager(manager);
        historyUtility.historyAuxData();
    }

    /*
     * getters and setters
     */
    private Integer getAccessionNumber() {
        if (manager == null)
            return null;
        return manager.getSample().getAccessionNumber();
    }

    private void setAccessionNumber(Integer accNum) {
        ValidationErrorsList errors;

        if (accNum == null || accNum < 0) {
            manager.getSample().setAccessionNumber(accNum);
            window.setError(Messages.get()
                                    .sample_accessionNumberNotValidException(DataBaseUtil.asString(accNum)));
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
            window.clearStatus();
        }
    }

    private Integer getOrderId() {
        if (manager == null)
            return null;
        return manager.getSample().getOrderId();
    }

    private void setOrderId(Integer ordId) {
        ValidationErrorsList errors;
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
            if (ret.getErrors() != null && ret.getErrors().size() > 0) {
                showErrors(ret.getErrors());
            } else if (ret.getTests() != null && ret.getTests().size() > 0) {
                showTestSelectionLookup(ret);
                window.clearStatus();
            }
        } catch (Exception e) {
            Window.alert(e.getMessage());
            window.clearStatus();
        }
    }

    private void setProjectSelection() {
        SampleProjectViewDO p;

        p = getFirstPermanentProject();

        if (p != null)
            projectName.setValue(p.getProjectId(), p.getProjectName());
        else
            projectName.setValue(null, "");
    }

    private void getProjectFromSelection() {
        AutoCompleteValue row;
        SampleProjectViewDO sp;
        ProjectDO data;

        row = projectName.getValue();
        if (row == null || row.getId() == null) {
            /*
             * if a project was not selected and if there were permanent
             * projects present then the first permanent project is deleted and
             * the next permanent project is set as the first one
             */
            sp = getFirstPermanentProject();
            if (sp != null)
                manager.project.remove(sp);
            sp = getFirstPermanentProject();

            if (sp != null)
                projectName.setValue(sp.getProjectId(), sp.getProjectName());
            else
                projectName.setValue(null, "");

        } else {
            /*
             * otherwise the first permanent project is modified or a new one is
             * created if no project existed
             */
            sp = getFirstPermanentProject();
            if (sp == null) {
                sp = manager.project.add();
                sp.setIsPermanent("Y");
            }

            data = (ProjectDO)row.getData();
            sp.setProjectId((Integer)row.getId());
            sp.setProjectName(data.getName());
            sp.setProjectDescription(data.getDescription());
        }
    }

    private void setOrganizationSelection(AutoComplete widget, Integer type) {
        SampleOrganizationViewDO sorg;

        sorg = getSampleOrganization(type);
        if (sorg != null)
            widget.setValue(sorg.getOrganizationId(), sorg.getOrganizationName());
        else
            widget.setValue(null, "");
    }

    private void getOrganizationFromSelection(AutoComplete widget, Integer type) {
        AutoCompleteValue row;
        OrganizationDO org;
        SampleOrganizationViewDO sorg;

        sorg = getSampleOrganization(type);
        row = widget.getValue();
        if (row == null || row.getId() == null) {
            /*
             * this method is called only when the report-to changes and if
             * there isn't a report-to selected currently, then there must have
             * been before, thus it needs to be removed from the manager
             */
            manager.organization.remove(sorg);
            widget.setValue(null, "");
        } else {
            org = (OrganizationDO)row.getData();
            if (sorg == null) {
                /*
                 * a report-to was selected by the user but there isn't one
                 * present in the manager, thus it needs to be added
                 */
                sorg = manager.organization.add(org);
                sorg.setTypeId(type);
            } else {
                /*
                 * the organization was changed, thus the report-to needs to be
                 * updated
                 */
                loadSampleOrganization(org, sorg);
            }

            widget.setValue(org.getId(), org.getName());
            /*
             * warn the user if samples from this organization are to held or
             * refused
             */
            try {
                showHoldRefuseWarning(org.getId(), org.getName());
            } catch (Exception e) {
                Window.alert(e.getMessage());
            }
        }
    }

    private Datetime getCollectionDate() {
        if (manager == null)
            return null;
        return manager.getSample().getCollectionDate();
    }

    private void setCollectionDate(Datetime date) {
        manager.getSample().setCollectionDate(date);
        transfusionAge.setValue(getNeonatalTransfusionAge());
    }

    private Datetime getCollectionTime() {
        if (manager == null)
            return null;
        return manager.getSample().getCollectionTime();
    }

    private void setCollectionTime(Datetime time) {
        manager.getSample().setCollectionTime(time);
    }

    private Datetime getReceivedDate() {
        if (manager == null)
            return null;
        return manager.getSample().getReceivedDate();
    }

    private void setReceivedDate(Datetime date) {
        manager.getSample().setReceivedDate(date);
    }

    private Integer getStatusId() {
        if (manager == null)
            return null;
        return manager.getSample().getStatusId();
    }

    private void setStatusId(Integer statusId) {
        manager.getSample().setStatusId(statusId);
    }

    private String getClientReference() {
        if (manager == null)
            return null;
        return manager.getSample().getClientReference();
    }

    private void setClientReference(String clientReference) {
        manager.getSample().setClientReference(clientReference);
    }

    private String getPatientLastName() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getPatient().getLastName();
    }

    private void setPatientLastName(String name) {
        manager.getSampleNeonatal().getPatient().setLastName(name);
    }

    private String getPatientFirstName() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getPatient().getFirstName();
    }

    private void setPatientFirstName(String name) {
        manager.getSampleNeonatal().getPatient().setFirstName(name);
    }
    
    private Integer getPatientGenderId() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getPatient().getGenderId();
    }

    private void setPatientGenderId(Integer genderId) {
        manager.getSampleNeonatal().getPatient().setGenderId(genderId);
    }

    private Integer getPatientRaceId() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getPatient().getRaceId();
    }

    private void setPatientRaceId(Integer raceId) {
        manager.getSampleNeonatal().getPatient().setRaceId(raceId);
    }

    private Integer getPatientEthnicityId() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getPatient().getEthnicityId();
    }

    private void setPatientEthnicityId(Integer ethnicityId) {
        manager.getSampleNeonatal().getPatient().setEthnicityId(ethnicityId);
    }

    private Datetime getPatientBirthDate() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getPatient().getBirthDate();
    }

    private void setPatientBirthDate(Datetime date) {
        manager.getSampleNeonatal().getPatient().setBirthDate(date);
    }

    private Datetime getPatientBirthTime() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getPatient().getBirthTime();
    }

    private void setPatientBirthTime(Datetime time) {
        manager.getSampleNeonatal().getPatient().setBirthTime(time);
    }

    private String getPatientAddressMultipleUnit() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getPatient().getAddress().getMultipleUnit();
    }

    private void setPatientAddressMultipleUnit(String multipleUnit) {
        manager.getSampleNeonatal().getPatient().getAddress().setMultipleUnit(multipleUnit);
    }

    private String getPatientAddressStreetAddress() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getPatient().getAddress().getStreetAddress();
    }

    private void setPatientAddressStreetAddress(String streetAddress) {
        manager.getSampleNeonatal().getPatient().getAddress().setStreetAddress(streetAddress);
    }

    private String getPatientAddressCity() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getPatient().getAddress().getCity();
    }

    private void setPatientAddressCity(String city) {
        manager.getSampleNeonatal().getPatient().getAddress().setCity(city);
    }

    private String getPatientAddressState() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getPatient().getAddress().getState();
    }

    private void setPatientAddressState(String state) {
        manager.getSampleNeonatal().getPatient().getAddress().setState(state);
    }

    private String getPatientAddressZipCode() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getPatient().getAddress().getZipCode();
    }

    private void setPatientAddressZipCode(String zipCode) {
        manager.getSampleNeonatal().getPatient().getAddress().setZipCode(zipCode);
    }

    private String getNextOfKinLastName() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKin().getLastName();
    }

    private void setNextOfKinLastName(String name) {
        manager.getSampleNeonatal().getNextOfKin().setLastName(name);
    }

    private String getNextOfKinMiddleName() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKin().getMiddleName();
    }

    private void setNextOfKinMiddleName(String name) {
        manager.getSampleNeonatal().getNextOfKin().setMiddleName(name);
    }
    
    private String getNextOfKinFirstName() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKin().getFirstName();
    }

    private void setNextOfKinFirstName(String name) {
        manager.getSampleNeonatal().getNextOfKin().setFirstName(name);
    }

    private Integer getNeonatalNextOfKinRelationId() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKinRelationId();
    }

    private void setNeonatalNextOfKinRelationId(Integer nextOfKinRelationId) {
        manager.getSampleNeonatal().setNextOfKinRelationId(nextOfKinRelationId);
    }    

    private Integer getNextOfKinGenderId() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKin().getGenderId();
    }

    private void setNextOfKinGenderId(Integer genderId) {
        manager.getSampleNeonatal().getNextOfKin().setGenderId(genderId);
    }

    private Integer getNextOfKinRaceId() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKin().getRaceId();
    }

    private void setNextOfKinRaceId(Integer raceId) {
        manager.getSampleNeonatal().getNextOfKin().setRaceId(raceId);
    }

    private Integer getNextOfKinEthnicityId() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKin().getEthnicityId();
    }

    private void setNextOfKinEthnicityId(Integer ethnicityId) {
        manager.getSampleNeonatal().getNextOfKin().setEthnicityId(ethnicityId);
    }

    private Datetime getNextOfKinBirthDate() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKin().getBirthDate();
    }

    private void setNextOfKinBirthDate(Datetime date) {
        manager.getSampleNeonatal().getNextOfKin().setBirthDate(date);
    }

    private String getNextOfKinAddressHomePhone() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKin().getAddress().getHomePhone();
    }

    private void setNextOfKinAddressHomePhone(String homePhone) {
        manager.getSampleNeonatal().getNextOfKin().getAddress().setHomePhone(homePhone);
    }    

    private String getNextOfKinAddressMultipleUnit() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKin().getAddress().getMultipleUnit();
    }

    private void setNextOfKinAddressMultipleUnit(String multipleUnit) {
        manager.getSampleNeonatal().getNextOfKin().getAddress().setMultipleUnit(multipleUnit);
    }

    private String getNextOfKinAddressStreetAddress() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKin().getAddress().getStreetAddress();
    }

    private void setNextOfKinAddressStreetAddress(String streetAddress) {
        manager.getSampleNeonatal().getNextOfKin().getAddress().setStreetAddress(streetAddress);
    }

    private String getNextOfKinAddressCity() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKin().getAddress().getCity();
    }

    private void setNextOfKinAddressCity(String city) {
        manager.getSampleNeonatal().getNextOfKin().getAddress().setCity(city);
    }

    private String getNextOfKinAddressState() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKin().getAddress().getState();
    }

    private void setNextOfKinAddressState(String state) {
        manager.getSampleNeonatal().getNextOfKin().getAddress().setState(state);
    }

    private String getNextOfKinAddressZipCode() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKin().getAddress().getZipCode();
    }        

    private void setNextOfKinAddressZipCode(String zipCode) {
        manager.getSampleNeonatal().getNextOfKin().getAddress().setZipCode(zipCode);
    }
    
    private String getNeonatalIsNicu() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getIsNicu();
    }

    private void setNeonatalIsNicu(String isNicu) {
        manager.getSampleNeonatal().setIsNicu(isNicu);
    }

    private Integer getNeonatalBirthOrder() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getBirthOrder();
    }

    private void setNeonatalBirthOrder(Integer birthOrder) {
        manager.getSampleNeonatal().setBirthOrder(birthOrder);
    }

    private Integer getNeonatalGestationalAge() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getGestationalAge();
    }

    private void setNeonatalGestationalAge(Integer gestationalAge) {
        manager.getSampleNeonatal().setGestationalAge(gestationalAge);
    }

    private Integer getNeonatalFeedingId() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getFeedingId();
    }

    private void setNeonatalFeedingId(Integer feedingId) {
        manager.getSampleNeonatal().setFeedingId(feedingId);
    }    

    private Integer getNeonatalWeight() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getWeight();
    }

    private void setNeonatalWeight(Integer weight) {
        manager.getSampleNeonatal().setWeight(weight);
    }

    private String getNeonatalIsTransfused() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getIsTransfused();
    }

    private void setNeonatalIsTransfused(String isTransfused) {
        manager.getSampleNeonatal().setIsTransfused(isTransfused);
    }

    private Datetime getNeonatalTransfusionDate() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getTransfusionDate();
    }

    private void setNeonatalTransfusionDate(Datetime transfusionDate) {
        manager.getSampleNeonatal().setTransfusionDate(transfusionDate);
        transfusionAge.setValue(getNeonatalTransfusionAge());
    }

    private Integer getNeonatalTransfusionAge() {
        Datetime cd, td;
        Long diff;
        Double numDays;

        cd = getCollectionDate();
        td = getNeonatalTransfusionDate();
        if (cd == null || td == null)
            return null;

        diff = cd.getDate().getTime() - td.getDate().getTime();
        numDays = diff.doubleValue() / day.doubleValue();

        return numDays.intValue();
    }

    private String getNeonatalIsRepeat() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getIsRepeat();
    }

    private void setNeonatalIsRepeat(String isRepeat) {
        manager.getSampleNeonatal().setIsRepeat(isRepeat);
    }

    private Integer getNeonatalCollectionAge() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getCollectionAge();
    }

    private void setNeonatalCollectionAge(Integer collectionAge) {
        manager.getSampleNeonatal().setCollectionAge(collectionAge);
    }

    private String getNeonatalIsCollectionValid() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getIsCollectionValid();
    }

    private void setNeonatalIsCollectionValid(String isCollectionValid) {
        manager.getSampleNeonatal().setIsCollectionValid(isCollectionValid);
    }

    private Integer getNeonatalProviderId() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getProviderId();
    }

    private void setNeonatalProviderId(Integer providerId) {
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

        providerLastName.setValue(getNeonatalProviderId(), getNeonatalProviderLastName());
        providerFirstName.setValue(getNeonatalProviderFirstName());
    }

    private void getProviderFromSelection() {
        ProviderDO data;
        AutoCompleteValue row;

        row = providerLastName.getValue();
        if (row == null || row.getId() == null) {
            setNeonatalProviderId(null);
            setProviderFirstName(null);
            setNeonatalProviderLastName(null);

            providerLastName.setValue(null, "");
            providerFirstName.setValue("");
            return;
        } else {
            data = (ProviderDO)row.getData();

            setNeonatalProviderId(data.getId());
            setNeonatalProviderFirstName(data.getFirstName());
            setNeonatalProviderLastName(data.getLastName());

            providerLastName.setValue(getNeonatalProviderId(), getNeonatalProviderLastName());
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
    
    private String getNeonatalFormNumber() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getFormNumber();
    }

    private void setNeonatalFormNumber(String formNumber) {
        manager.getSampleNeonatal().setFormNumber(formNumber);
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
        /*
         * qaEventsTab.setData(null);
         */
    }
    
    private void evaluateEdit() {
        canEdit = (manager != null && !Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample()
                                                                                            .getStatusId()));
    }
    
    /**
     * creates or updates the cache of objects like TestManager that are used
     * frequently by the different parts of the screen
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
     * fills the sample organization with the organization's data
     */
    private void loadSampleOrganization(OrganizationDO org, SampleOrganizationViewDO data) {
        AddressDO addr;

        addr = org.getAddress();
        data.setOrganizationId(org.getId());
        data.setOrganizationName(org.getName());
        data.setOrganizationMultipleUnit(addr.getMultipleUnit());
        data.setOrganizationStreetAddress(addr.getStreetAddress());
        data.setOrganizationCity(addr.getCity());
        data.setOrganizationState(addr.getState());
        data.setOrganizationZipCode(addr.getZipCode());
        data.setOrganizationCountry(addr.getCountry());
    }

    /**
     * warn the user if samples from this organization are to held or refused
     */
    private void showHoldRefuseWarning(Integer orgId, String name) throws Exception {
        if (SampleOrganizationUtility1.isHoldRefuseSampleForOrg(orgId))
            Window.alert(Messages.get().orgMarkedAsHoldRefuseSample(name));
    }

    private void showWarningsDialog(ValidationErrorsList warnings) {
        StringBuffer txt;

        txt = new StringBuffer();
        txt.append(Messages.get().warningDialogLine1()).append("\n");
        for (Exception ex : warnings.getErrorList())
            txt.append(" * ").append(ex.getMessage()).append("\n");

        txt.append("\n").append(Messages.get().warningDialogLastLine());
        if (Window.confirm(txt.toString()))
            commitUpdate(true);
    }

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
                        setOrganizationSelection(reportToName, Constants.dictionary().ORG_REPORT_TO);
                        setOrganizationSelection(birthHospitalName,
                                                 Constants.dictionary().ORG_BIRTH_HOSPITAL);
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

    private void getProviderMatches(GetMatchesEvent event) {
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
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
        window.clearStatus();
    }

    private SampleProjectViewDO getFirstPermanentProject() {
        ArrayList<SampleProjectViewDO> p;

        if (manager == null)
            return null;

        p = manager.project.getByType("Y");
        if (p != null && p.size() > 0)
            return p.get(0);
        return null;
    }

    private void getProjectMatches(GetMatchesEvent event) {
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

    private void getOrganizationMatches(String match, AutoComplete widget) {
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
     * returns the organization of the specified type from the manager
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

    private void addTests(ArrayList<SampleTestRequestVO> tests) {
        SampleTestReturnVO ret;

        try {
            ret = SampleService1.get().addTests(manager, tests);
            manager = ret.getManager();
            setData();
            setState(state);
            fireDataChange();
            if (ret.getErrors() != null && ret.getErrors().size() > 0)
                showErrors(ret.getErrors());
            else if (ret.getTests() != null && ret.getTests().size() > 0)
                showTestSelectionLookup(ret);            
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private void changeAnalysisMethod(String uid, Integer methodId) {
        AnalysisViewDO ana;
        SampleTestReturnVO ret;        

        ana = (AnalysisViewDO)manager.getObject(uid);
        try {
            ret = SampleService1.get().changeAnalysisMethod(manager, ana.getId(), methodId);
            manager = ret.getManager();
            setData();
            setState(state);
            bus.fireEventFromSource(new AnalysisChangeEvent(uid, methodId, AnalysisChangeEvent.Action.METHOD_CHANGED), this);
            bus.fireEvent(new ResultChangeEvent(ResultChangeEvent.Action.METHOD_CHANGED));
            
            if (ret.getErrors() != null && ret.getErrors().size() > 0)
                showErrors(ret.getErrors());
            else if (ret.getTests() != null && ret.getTests().size() > 0)
                showTestSelectionLookup(ret);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private void changeAnalysisUnit(String uid, Integer unitId) {
        AnalysisViewDO ana;
        
        ana = (AnalysisViewDO)manager.getObject(uid);
        try {
            manager = SampleService1.get().changeAnalysisUnit(manager, ana.getId(), unitId);
            setData();
            setState(state);            
            bus.fireEventFromSource(new AnalysisChangeEvent(uid, unitId, AnalysisChangeEvent.Action.UNIT_CHANGED), this);
            bus.fireEvent(new ResultChangeEvent(ResultChangeEvent.Action.UNIT_CHANGED));
        } catch (Exception e) {
            Window.alert(e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
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
        modal.setName(Messages.get().prepTestPicker());
        modal.setCSS(UIResources.INSTANCE.popupWindow());
        modal.setContent(testSelectionLookup);

        testSelectionLookup.setData(manager, ret.getTests());
        testSelectionLookup.setWindow(modal);
    }
}