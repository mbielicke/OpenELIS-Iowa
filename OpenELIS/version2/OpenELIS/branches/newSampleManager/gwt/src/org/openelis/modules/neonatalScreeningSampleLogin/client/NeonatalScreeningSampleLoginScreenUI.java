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
import static org.openelis.ui.screen.State.ADD;
import static org.openelis.ui.screen.State.DEFAULT;
import static org.openelis.ui.screen.State.DELETE;
import static org.openelis.ui.screen.State.DISPLAY;
import static org.openelis.ui.screen.State.QUERY;
import static org.openelis.ui.screen.State.UPDATE;

import java.util.ArrayList;
import java.util.EnumSet;

import org.openelis.cache.CategoryCache;
import org.openelis.cache.UserCache;
import org.openelis.constants.Messages;
import org.openelis.domain.AddressDO;
import org.openelis.domain.Constants;
import org.openelis.domain.DictionaryDO;
import org.openelis.domain.OrganizationDO;
import org.openelis.domain.ProjectDO;
import org.openelis.domain.ProviderDO;
import org.openelis.domain.SampleOrganizationViewDO;
import org.openelis.domain.SampleProjectViewDO;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.screen.ScreenNavigator;
import org.openelis.ui.widget.AutoComplete;
import org.openelis.ui.widget.calendar.Calendar;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.manager.SampleManager1;
import org.openelis.meta.SampleMeta;
import org.openelis.modules.organization.client.OrganizationService;
import org.openelis.modules.project.client.ProjectService;
import org.openelis.modules.sample1.client.SampleHistoryUtility1;
import org.openelis.modules.sample1.client.SampleOrganizationLookupScreen1;
import org.openelis.modules.sample1.client.SampleOrganizationUtility1;
import org.openelis.modules.sample1.client.SampleProjectLookupScreen1;
import org.openelis.modules.sample1.client.SampleService1;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.common.PermissionException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.event.StateChangeHandler;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.screen.ScreenHandler;
import org.openelis.ui.screen.State;
import org.openelis.ui.widget.AutoCompleteValue;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.MenuItem;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WindowInt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class NeonatalScreeningSampleLoginScreenUI extends Screen {
    
    @UiTemplate("NeonatalScreeningSampleLogin.ui.xml")
    interface NeonatalScreeningSampleLoginUiBinder extends UiBinder<Widget, NeonatalScreeningSampleLoginScreenUI> {
    };
    
    private static NeonatalScreeningSampleLoginUiBinder uiBinder = GWT.create(NeonatalScreeningSampleLoginUiBinder.class);
    
    protected SampleManager1                            manager;

    @UiField
    protected Calendar                                 collectionDate, receivedDate, patientBirthDate, nextOfKinBirthDate,
                                                        transfusionDate;

    @UiField
    protected TextBox<Integer>                          accessionNumber, orderId, birthOrder,
                                                         gestationalAge, weight, transfusionAge, collectionAge;

    @UiField
    protected TextBox                                   clientReference, patientLastName,
                                                         patientFirstName, patientAddrMultipleUnit,
                                                         patientAddrStreetAddress, patientAddrCity,
                                                         patientAddrZipCode, nextOfKinLastName,
                                                         nextOfKinMiddleName, nextOfKinFirstName,
                                                         nextOfKinAddrHomePhone, nextOfKinAddrMultipleUnit,
                                                         nextOfKinAddrStreetAddress, nextOfKinAddrCity,
                                                         nextOfKinAddrZipCode, providerFirstName,
                                                         formNumber;

    @UiField
    protected TextBox<Datetime>                         collectionTime, patientBirthTime;

    @UiField
    protected Dropdown<Integer>                         statusId, feedingId, patientGenderId,
                                                         patientRaceId, patientEthnicityId, nextOfKinRelationId, nextOfKinGenderId,
                                                         nextOfKinRaceId, nextOfKinEthnicityId;

    @UiField
    protected Dropdown<String>                          patientAddrState, nextOfKinAddrState;
    
    @UiField
    protected CheckBox                                  isNicu, isTransfused, isRepeat,
                                                         isCollectionValid;

    @UiField
    protected AutoComplete                                providerLastName, projectName, reportToName,
                                                         birthHospitalName;

    @UiField
    protected Button                                    query, previous, next, add, update, commit,
                                                         abort, orderLookupButton, addItemButton,
                                                         addAnalysisButton, removeRowButton, popoutTree, projectButton, reportToButton,
                                                         birthHospitalButton;

    //@UiField
    //protected TabPanel                                  tabPanel;

    protected NeonatalScreeningSampleLoginScreenUI      screen;

    protected SampleHistoryUtility1                     historyUtility;

    protected SampleProjectLookupScreen1                projectLookUp;

    protected SampleOrganizationLookupScreen1           organizationLookUp;

    @UiField
    protected MenuItem                                  duplicate, historySample,
                                                         historySampleProject, historySampleItem,
                                                         historyAnalysis, historyCurrentResult,
                                                         historyStorage, historySampleQA,
                                                         historyAnalysisQA, historyAuxData;

    protected ScreenNavigator                           nav;

    protected ModulePermission                          userPermission;

    public NeonatalScreeningSampleLoginScreenUI(WindowInt window) throws Exception {

        setWindow(window);
        
        userPermission = UserCache.getPermission().getModule("sampleneonatal");
        if (userPermission == null)
            throw new PermissionException(Messages.get().screenPermException("Neonatal Screening Sample Login Screen"));

        initWidget(uiBinder.createAndBindUi(this));
        
        manager = null;
        
        try {
            CategoryCache.getBySystemNames("sample_status", "analysis_status", 
                                           "user_action", "type_of_sample", "source_of_sample",
                                           "sample_container", "unit_of_measure",
                                           "qaevent_type", "aux_field_value_type",
                                           "organization_type", "worksheet_status",
                                           "parameter_type", "gender", "race", 
                                           "ethnicity", "patient_relation", "feeding");
        } catch (Exception e) {
            Window.alert("NeonatalScreeningSampleLoginScreen: missing dictionary entry; " + e.getMessage());
            window.close();
        }
        
        initialize();
        //setDataInTabs();
        setState(DEFAULT);
        initializeDropdowns();
        fireDataChange();
        
        logger.fine("Neonatal Screening Sample Login Screen Opened");
    }

    /**
     * Setup state and data change handles for every widget on the screen
     */
    private void initialize() {
        screen = this;
        
        //
        // button panel buttons
        //
        addStateChangeHandler(new StateChangeHandler() {
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

        addStateChangeHandler(new StateChangeHandler() {
            public void onStateChange(StateChangeEvent event) {
                previous.setEnabled(isState(DISPLAY));
            }
        });

        addShortcut(previous, 'p', CTRL);

        addStateChangeHandler(new StateChangeHandler() {
            public void onStateChange(StateChangeEvent event) {
                next.setEnabled(isState(DISPLAY));
            }
        });

        addShortcut(next, 'n', CTRL);

        addStateChangeHandler(new StateChangeHandler() {
            public void onStateChange(StateChangeEvent event) {
                add.setEnabled(isState(DEFAULT, DISPLAY) && userPermission.hasAddPermission());
                if (isState(ADD)) {
                    add.setPressed(true);
                    add.lock();
                } else
                    add.setPressed(false);
            }
        });

        addShortcut(add, 'a', CTRL);

        addStateChangeHandler(new StateChangeHandler() {
            public void onStateChange(StateChangeEvent event) {
                update.setEnabled(isState(DISPLAY) && userPermission.hasUpdatePermission());
                if (isState(UPDATE)) {
                    update.setPressed(true);
                    update.lock();
                } else
                    update.setPressed(false);
            }
        });

        addShortcut(update, 'u', CTRL);

        addStateChangeHandler(new StateChangeHandler() {
            public void onStateChange(StateChangeEvent event) {
                commit.setEnabled(isState(QUERY, ADD, UPDATE, DELETE));
            }
        });

        addShortcut(commit, 'm', CTRL);

        addStateChangeHandler(new StateChangeHandler() {
            public void onStateChange(StateChangeEvent event) {
                abort.setEnabled(isState(QUERY, ADD, UPDATE, DELETE));
            }
        });

        addShortcut(abort, 'o', CTRL);

        addStateChangeHandler(new StateChangeHandler() {
            public void onStateChange(StateChangeEvent event) {
                duplicate.setEnabled(isState(State.DISPLAY));
            }
        });

        duplicate.addCommand(new Command() {
            public void execute() {
                duplicate();
            }
        });
        
        historySample.addCommand(new Command() {
            @Override
            public void execute() {
                historySample();
            }
        });

        addStateChangeHandler(new StateChangeHandler() {
            public void onStateChange(StateChangeEvent event) {
                historySample.setEnabled(isState(DISPLAY));
            }
        });
        
        historySampleProject.addCommand(new Command() {
            @Override
            public void execute() {
                historySampleProject();
            }
        });

        addStateChangeHandler(new StateChangeHandler() {
            public void onStateChange(StateChangeEvent event) {
                historySampleProject.setEnabled(isState(DISPLAY));
            }
        });
        
        historySampleItem.addCommand(new Command() {
            @Override
            public void execute() {
                historySampleItem();
            }
        });

        addStateChangeHandler(new StateChangeHandler() {
            public void onStateChange(StateChangeEvent event) {
                historySampleItem.setEnabled(isState(DISPLAY));
            }
        });
        
        historyAnalysis.addCommand(new Command() {
            @Override
            public void execute() {
                historyAnalysis();
            }
        });

        addStateChangeHandler(new StateChangeHandler() {
            public void onStateChange(StateChangeEvent event) {
                historyAnalysis.setEnabled(isState(DISPLAY));
            }
        });
        
        historyCurrentResult.addCommand(new Command() {
            @Override
            public void execute() {
                historyCurrentResult();
            }
        });

        addStateChangeHandler(new StateChangeHandler() {
            public void onStateChange(StateChangeEvent event) {
                historyCurrentResult.setEnabled(isState(DISPLAY));
            }
        });
        
        historyStorage.addCommand(new Command() {
            @Override
            public void execute() {
                historyStorage();
            }
        });

        addStateChangeHandler(new StateChangeHandler() {
            public void onStateChange(StateChangeEvent event) {
                historyStorage.setEnabled(isState(DISPLAY));
            }
        });
        
        historySampleQA.addCommand(new Command() {
            @Override
            public void execute() {
                historySampleQA();
            }
        });

        addStateChangeHandler(new StateChangeHandler() {
            public void onStateChange(StateChangeEvent event) {
                historySampleQA.setEnabled(isState(DISPLAY));
            }
        });
        
        historyAnalysisQA.addCommand(new Command() {
            @Override
            public void execute() {
                historyAnalysisQA();
            }
        });

        addStateChangeHandler(new StateChangeHandler() {
            public void onStateChange(StateChangeEvent event) {
                historyAnalysisQA.setEnabled(isState(DISPLAY));
            }
        });
        
        historyAuxData.addCommand(new Command() {
            @Override
            public void execute() {
                historyAuxData();
            }
        });

        addStateChangeHandler(new StateChangeHandler() {
            public void onStateChange(StateChangeEvent event) {
                historyAuxData.setEnabled(isState(DISPLAY));
            }
        });
        
        addScreenHandler(accessionNumber, SampleMeta.getAccessionNumber(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                accessionNumber.setValue(getAccessionNumber());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setAccessionNumber(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                accessionNumber.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                accessionNumber.setQueryMode(isState(QUERY));
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
                orderId.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                orderId.setQueryMode(isState(QUERY));
            }
        });
        
        addStateChangeHandler(new StateChangeHandler() {
            public void onStateChange(StateChangeEvent event) {
                orderLookupButton.setEnabled(isState(DISPLAY) || (canEdit() && isState(ADD, UPDATE)));
            }
        });

        addScreenHandler(collectionDate, SampleMeta.getCollectionDate(), new ScreenHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                collectionDate.setValue(getCollectionDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                setCollectionDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                collectionDate.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                collectionDate.setQueryMode(isState(QUERY));
            }
        });
        
        addScreenHandler(collectionTime, SampleMeta.getCollectionTime(), new ScreenHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                collectionTime.setValue(getCollectionTime());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                setCollectionTime(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                collectionTime.setEnabled(canEdit() && isState(ADD, UPDATE));
                collectionTime.setQueryMode(isState(QUERY));
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
                receivedDate.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                receivedDate.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(statusId, SampleMeta.getStatusId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                statusId.setValue(getStatusId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setStatusId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                statusId.setEnabled(isState(QUERY));
                statusId.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(clientReference, SampleMeta.getClientReference(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                clientReference.setValue(getClientReference());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setClientReference(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {                
                clientReference.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                clientReference.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(patientLastName, SampleMeta.getNeoPatientLastName(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                patientLastName.setValue(getPatientLastName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setPatientLastName(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                patientLastName.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                patientLastName.setQueryMode(event.getState() == State.QUERY);
            }
        });

        addScreenHandler(patientFirstName, SampleMeta.getNeoPatientFirstName(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                patientFirstName.setValue(getPatientFirstName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setPatientFirstName(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                patientFirstName.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                patientFirstName.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(patientGenderId, SampleMeta.getNeoPatientGenderId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                patientGenderId.setValue(getPatientGenderId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setPatientGenderId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                patientGenderId.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                patientGenderId.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(patientRaceId, SampleMeta.getNeoPatientRaceId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                patientRaceId.setValue(getPatientRaceId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setPatientRaceId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {                
                patientRaceId.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                patientRaceId.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(patientEthnicityId, SampleMeta.getNeoPatientEthnicityId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                patientEthnicityId.setValue(getPatientEthnicityId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setPatientEthnicityId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                patientEthnicityId.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                patientEthnicityId.setQueryMode(event.getState() == State.QUERY);
            }
        });

        addScreenHandler(patientBirthDate, SampleMeta.getNeoPatientBirthDate(), new ScreenHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                patientBirthDate.setValue(getPatientBirthDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                setPatientBirthDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                patientBirthDate.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                patientBirthDate.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(patientBirthTime, SampleMeta.getNeoPatientBirthTime(), new ScreenHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                patientBirthTime.setValue(getPatientBirthTime());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                setPatientBirthTime(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                patientBirthTime.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                patientBirthTime.setQueryMode(event.getState() == State.QUERY);
            }
        });

        addScreenHandler(patientAddrMultipleUnit, SampleMeta.getNeoPatientAddrMultipleUnit(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                patientAddrMultipleUnit.setValue(getPatientAddressMultipleUnit());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setPatientAddressMultipleUnit(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                patientAddrMultipleUnit.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                patientAddrMultipleUnit.setQueryMode(event.getState() == State.QUERY);
            }
        });

        addScreenHandler(patientAddrStreetAddress, SampleMeta.getNeoPatientAddrStreetAddress(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                patientAddrStreetAddress.setValue(getPatientAddressStreetAddress());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setPatientAddressStreetAddress(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                patientAddrStreetAddress.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                patientAddrStreetAddress.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(patientAddrCity, SampleMeta.getNeoPatientAddrCity(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                patientAddrCity.setValue(getPatientAddressCity());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setPatientAddressCity(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                patientAddrCity.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                patientAddrCity.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(patientAddrState, SampleMeta.getNeoPatientAddrState(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                patientAddrState.setValue(getPatientAddressState());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setPatientAddressState(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                patientAddrState.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                patientAddrState.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(patientAddrZipCode, SampleMeta.getNeoPatientAddrZipCode(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                patientAddrZipCode.setValue(getPatientAddressZipCode());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setPatientAddressZipCode(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                patientAddrZipCode.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                patientAddrZipCode.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(nextOfKinLastName, SampleMeta.getNeoNextOfKinLastName(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                nextOfKinLastName.setValue(getNextOfKinLastName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setNextOfKinLastName(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                nextOfKinLastName.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                nextOfKinLastName.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(nextOfKinMiddleName, SampleMeta.getNeoNextOfKinMiddleName(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                nextOfKinMiddleName.setValue(getNextOfKinMiddleName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setNextOfKinMiddleName(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                nextOfKinMiddleName.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                nextOfKinMiddleName.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(nextOfKinFirstName, SampleMeta.getNeoNextOfKinFirstName(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                nextOfKinFirstName.setValue(getNextOfKinFirstName());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setNextOfKinFirstName(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                nextOfKinFirstName.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                nextOfKinFirstName.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(nextOfKinRelationId, SampleMeta.getNeoNextOfKinRelationId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                nextOfKinRelationId.setValue(getNeonatalNextOfKinRelationId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setNeonatalNextOfKinRelationId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                nextOfKinRelationId.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                nextOfKinRelationId.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(nextOfKinGenderId, SampleMeta.getNeoNextOfKinGenderId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                nextOfKinGenderId.setValue(getNextOfKinGenderId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setNextOfKinGenderId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                nextOfKinGenderId.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                nextOfKinGenderId.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(nextOfKinRaceId, SampleMeta.getNeoNextOfKinRaceId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                nextOfKinRaceId.setValue(getNextOfKinRaceId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setNextOfKinRaceId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                nextOfKinRaceId.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                nextOfKinRaceId.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(nextOfKinEthnicityId, SampleMeta.getNeoNextOfKinEthnicityId(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                nextOfKinEthnicityId.setValue(getNextOfKinEthnicityId());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setNextOfKinEthnicityId(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                nextOfKinEthnicityId.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                nextOfKinEthnicityId.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(nextOfKinBirthDate, SampleMeta.getNeoNextOfKinBirthDate(), new ScreenHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                nextOfKinBirthDate.setValue(getNextOfKinBirthDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                setNextOfKinBirthDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                nextOfKinBirthDate.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                nextOfKinBirthDate.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(nextOfKinAddrHomePhone, SampleMeta.getNeoNextOfKinAddrHomePhone(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                nextOfKinAddrHomePhone.setValue(getNextOfKinAddressHomePhone());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setNextOfKinAddressHomePhone(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                nextOfKinAddrHomePhone.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                nextOfKinAddrHomePhone.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(nextOfKinAddrMultipleUnit, SampleMeta.getNeoNextOfKinAddrMultipleUnit(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                nextOfKinAddrMultipleUnit.setValue(getNextOfKinAddressMultipleUnit());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setNextOfKinAddressMultipleUnit(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                nextOfKinAddrMultipleUnit.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                nextOfKinAddrMultipleUnit.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(nextOfKinAddrStreetAddress, SampleMeta.getNeoNextOfKinAddrStreetAddress(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                nextOfKinAddrStreetAddress.setValue(getNextOfKinAddressStreetAddress());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setNextOfKinAddressStreetAddress(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                nextOfKinAddrStreetAddress.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                nextOfKinAddrStreetAddress.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(nextOfKinAddrCity, SampleMeta.getNeoNextOfKinAddrCity(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                nextOfKinAddrCity.setValue(getNextOfKinAddressCity());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setNextOfKinAddressCity(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                nextOfKinAddrCity.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                nextOfKinAddrCity.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(nextOfKinAddrState, SampleMeta.getNeoNextOfKinAddrState(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                nextOfKinAddrState.setValue(getNextOfKinAddressState());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setNextOfKinAddressState(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                nextOfKinAddrState.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                nextOfKinAddrState.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(nextOfKinAddrZipCode, SampleMeta.getNeoNextOfKinAddrZipCode(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                nextOfKinAddrZipCode.setValue(getNextOfKinAddressZipCode());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setNextOfKinAddressZipCode(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                nextOfKinAddrZipCode.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
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
                isNicu.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
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
                birthOrder.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                birthOrder.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(gestationalAge, SampleMeta.getNeoGestationalAge(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                gestationalAge.setValue(getNeonatalGestationalAge());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setNeonatalGestationalAge(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                gestationalAge.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
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
                feedingId.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
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
                weight.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                weight.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(isTransfused, SampleMeta.getNeoIsTransfused(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isTransfused.setValue(getNeonatalIsTransfused());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setNeonatalIsTransfused(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                isTransfused.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                isTransfused.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(transfusionDate, SampleMeta.getNeoTransfusionDate(), new ScreenHandler<Datetime>() {
            public void onDataChange(DataChangeEvent event) {
                transfusionDate.setValue(getNeonatalTransfusionDate());
            }

            public void onValueChange(ValueChangeEvent<Datetime> event) {
                setNeonatalTransfusionDate(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                transfusionDate.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
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
                isRepeat.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                isRepeat.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(collectionAge, SampleMeta.getNeoCollectionAge(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                collectionAge.setValue(getNeonatalCollectionAge());
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                setNeonatalCollectionAge(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                collectionAge.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                collectionAge.setQueryMode(isState(QUERY));
            }
        });

        addScreenHandler(isCollectionValid, SampleMeta.getNeoIsCollectionValid(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                isCollectionValid.setValue(getNeonatalIsCollectionValid());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setNeonatalIsCollectionValid(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                isCollectionValid.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                isCollectionValid.setQueryMode(isState(QUERY));
            }
        });

        /*addItemButton = (AppButton)def.getWidget("addItemButton");
        addScreenHandler(addItemButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                // FIXME add on click handler
            }

            public void onStateChange(StateChangeEvent<State> event) {
            }
        });

        addAnalysisButton = (AppButton)def.getWidget("addAnalysisButton");
        addScreenHandler(addAnalysisButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                // FIXME add on click handler
            }

            public void onStateChange(StateChangeEvent<State> event) {
            }
        });

        removeRowButton = (AppButton)def.getWidget("removeRowButton");
        addScreenHandler(removeRowButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                // FIXME add on click handler
            }

            public void onStateChange(StateChangeEvent<State> event) {
            }
        });

        popoutTree = (AppButton)def.getWidget("popoutTree");
        addScreenHandler(popoutTree, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                // FIXME add on click handler
            }

            public void onStateChange(StateChangeEvent<State> event) {
            }
        });*/
        
        addScreenHandler(providerLastName, SampleMeta.getNeoProviderLastName(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                setProviderSelection();
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                getProviderFromSelection(); 
            }

            public void onStateChange(StateChangeEvent event) {
                providerLastName.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                providerLastName.setQueryMode(isState(QUERY));
            }
        });
        
        providerLastName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                getProviderMatches(event);
            }
        });
        
        addScreenHandler(providerFirstName, SampleMeta.getNeoProviderFirstName(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                providerFirstName.setValue(getProviderFirstName());
            }

            public void onStateChange(StateChangeEvent event) {
                providerFirstName.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
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
                projectName.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                projectName.setQueryMode(isState(QUERY));
            }
        });
        
        projectName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                getProjectMatches(event);
            }
        });

        /*projectButton = (AppButton)def.getWidget("projectButton");
        addScreenHandler(projectButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                showProjectLookup();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                projectButton.enable(event.getState() == State.DISPLAY ||
                                (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                     .contains(event.getState())));
            }
        });*/

        addScreenHandler(reportToName, SampleMeta.getOrgName(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                setReportToSelection();
            }

           public void onValueChange(ValueChangeEvent<Integer> event) {
               getReportToFromSelection();
           }

            public void onStateChange(StateChangeEvent event) {
                reportToName.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                reportToName.setQueryMode(isState(QUERY));                
            }
        });
        
        reportToName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                getOrganizationMatches(event.getMatch(), reportToName);
            }
        });
        
        /*reportToButton = (AppButton)def.getWidget("reportToButton");
        addScreenHandler(reportToButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                showOrganizationLookup();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                reportToButton.enable(event.getState() == State.DISPLAY ||
                                (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                     .contains(event.getState())));
            }
        });*/

        addScreenHandler(birthHospitalName, SampleMeta.getBillTo(), new ScreenHandler<Integer>() {
            public void onDataChange(DataChangeEvent event) {
                setBirthHospitalSelection();
            }

            public void onValueChange(ValueChangeEvent<Integer> event) {
                getBirthHospitalFromSelection();
            }

            public void onStateChange(StateChangeEvent event) {
                birthHospitalName.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                birthHospitalName.setQueryMode(isState(QUERY));
            }
        });
        
        birthHospitalName.addGetMatchesHandler(new GetMatchesHandler() {
            public void onGetMatches(GetMatchesEvent event) {
                getOrganizationMatches(event.getMatch(), birthHospitalName);
            }
        });
        
        /*birthHospitalButton = (AppButton)def.getWidget("birthHospitalButton");
        addScreenHandler(birthHospitalButton, new ScreenEventHandler<Object>() {
            public void onClick(ClickEvent event) {
                showOrganizationLookup();
            }

            public void onStateChange(StateChangeEvent<State> event) {
                birthHospitalButton.enable(event.getState() == State.DISPLAY ||
                                (canEdit() && EnumSet.of(State.ADD, State.UPDATE)
                                                     .contains(event.getState())));
            }
        });*/

        addScreenHandler(formNumber, SampleMeta.getNeoFormNumber(), new ScreenHandler<String>() {
            public void onDataChange(DataChangeEvent event) {
                formNumber.setValue(getNeonatalFormNumber());
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                setNeonatalFormNumber(event.getValue());
            }

            public void onStateChange(StateChangeEvent event) {
                formNumber.setEnabled(isState(QUERY) || (canEdit() && isState(ADD, UPDATE)));
                formNumber.setQueryMode(event.getState() == State.QUERY);
            }
        });


        //TODO change this code
        
        /*tabPanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {

            }
        });
        
        nav = new ScreenNavigator<IdAccessionVO>(def) {
            public void executeQuery(final Query query) {
                window.setBusy(Messages.get().querying());

                query.setRowsPerPage(5);
                /*SampleService.get().fetchByQuery(query, new AsyncCallback<ArrayList<SampleManager1>>() {
                                                     public void onSuccess() {
                                                         setQueryResult(result);
                                                     }

                                                     public void onFailure(Throwable error) {
                                                         setQueryResult(null);
                                                         if (error instanceof NotFoundException) {
                                                             window.setDone(consts.get("noRecordsFound"));
                                                             setState(State.DEFAULT);
                                                         } else if (error instanceof LastPageException) {
                                                             window.setError("No more records in this direction");
                                                         } else {
                                                             Window.alert("Error: envsample call query failed; " +
                                                                          error.getMessage());
                                                             window.setError(consts.get("queryFailed"));
                                                         }
                                                     }
                                                 });
                try {
                    setQueryResult(SampleService1.get().fetchByQuery(query));
                } catch (NotFoundException e) {
                    setQueryResult(null);
                    window.setDone(Messages.get().noRecordsFound());
                    setState(State.DEFAULT);
                } catch (LastPageException e) {
                    setQueryResult(null);
                    window.setError(Messages.get().noMoreRecordInDir());
                } catch (Exception e) {
                    setQueryResult(null);
                    Window.alert("Error: neonatalsample call query failed; " +  e.getMessage());
                    window.setError(Messages.get().queryFailed());
                }
            }

            public boolean fetch(IdAccessionVO entry) {
                return fetchById( (entry == null) ? null : entry.getId());
            }

            public ArrayList<TableDataRow> getModel() {
                ArrayList<IdAccessionVO> result;
                ArrayList<TableDataRow> model;

                result = nav.getQueryResult();
                model = new ArrayList<TableDataRow>();
                if (result != null) {
                    for (IdAccessionVO entry : result)
                        model.add(new TableDataRow(entry.getId(), entry.getAccessionNumber()));
                }
                return model;
            }
        };*/

        window.addBeforeClosedHandler(new BeforeCloseHandler<WindowInt>() {
            public void onBeforeClosed(BeforeCloseEvent<WindowInt> event) {
                if (EnumSet.of(State.ADD, State.UPDATE).contains(state)) {
                    event.cancel();
                    window.setError(Messages.get().mustCommitOrAbort());
                }
            }
        });
    }

    private void initializeDropdowns() {
        ArrayList<Item<Integer>> model;
        Item<Integer> row;

        model = new ArrayList<Item<Integer>>();
        model.add(new Item<Integer>(null, ""));
        for (DictionaryDO d : CategoryCache.getBySystemName("sample_status")) {
            row = new Item<Integer>(d.getId(), d.getEntry());
            row.setEnabled( ("Y".equals(d.getIsActive())));
            model.add(row);
        }

        statusId.setModel(model);
        
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

    protected void query() {
    }

    protected void previous() {
        nav.previous();
    }

    protected void next() {
        nav.next();
    }

    protected void add() {
        try {
            manager = SampleService1.get().getInstance(Constants.domain().NEONATAL);
            // setDataInTabs();
            setState(State.ADD);
            fireDataChange();
            accessionNumber.setFocus(true);
            window.setDone(Messages.get().enterInformationPressCommit());
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
    }

    protected void update() {

    }

    protected void commit() {
        Query query;
        ArrayList<QueryData> queryFields;

        finishEditing();
        clearErrors();
        // manager.setStatusWithError(false);

        if ( !validate()) {
            window.setError(Messages.get().correctErrors());
            return;
        }

        if (state == State.QUERY) {
            queryFields = getQueryFields();
            query = new Query();
            query.setFields(queryFields);

            nav.setQuery(query);
        } else if (state == State.ADD) {
            window.setBusy(Messages.get().adding());
            try {
                manager = SampleService1.get().add(manager, false);

                // setDataInTabs();
                setState(State.DISPLAY);
                fireDataChange();
                window.clearStatus();
            } catch (ValidationErrorsList e) {
                showErrors(e);
                //TODO change this code
                //if ( !e.hasErrors() && e.hasWarnings())
                  //  showWarningsDialog(e);
            } catch (Exception e) {
                Window.alert("commitAdd(): " + e.getMessage());
                window.clearStatus();
            }
        } else if (state == State.UPDATE) {
            window.setBusy(Messages.get().updating());
            try {
                manager = SampleService1.get().update(manager, false);

                // setDataInTabs();
                setState(State.DISPLAY);
                fireDataChange();
                window.clearStatus();
                // quickUpdate = false;
            } catch (ValidationErrorsList e) {
                showErrors(e);
                // TODO change this code
                //if ( !e.hasErrors() && e.hasWarnings())
                   // showWarningsDialog(e);
            } catch (Exception e) {
                Window.alert("commitUpdate(): " + e.getMessage());
                window.clearStatus();
            }
        }
    }

    protected void abort() {
        finishEditing();
        clearErrors();
        window.setBusy(Messages.get().cancelChanges());

        if (state == State.QUERY) {
            try {
                manager = null;

                // setDataInTabs();
                setState(State.DEFAULT);
                fireDataChange();
                window.setDone(Messages.get().queryAborted());
            } catch (Exception e) {
                Window.alert(e.getMessage());
                window.clearStatus();
            }
        } else if (state == State.ADD) {
            try {
                manager = null;

                // setDataInTabs();
                setState(State.DEFAULT);
                fireDataChange();
                window.setDone(Messages.get().addAborted());
            } catch (Exception e) {
                Window.alert(e.getMessage());
                window.clearStatus();
            }
        } else if (state == State.UPDATE) {
            /*
             * try { manager = manager.abortUpdate();
             * 
             * quickUpdate = false; if
             * (SampleManager.QUICK_ENTRY.equals(manager.
             * getSample().getDomain())) { manager =
             * SampleManager.getInstance();
             * manager.getSample().setDomain(SampleManager
             * .ENVIRONMENTAL_DOMAIN_FLAG);
             * 
             * setDataInTabs(); setState(State.DEFAULT); } else {
             * setDataInTabs(); setState(State.DISPLAY); }
             * DataChangeEvent.fire(this); window.clearStatus(); } catch
             * (Exception e) { Window.alert(e.getMessage());
             * window.clearStatus(); }
             */
        } else {
            window.clearStatus();
        }
    }    

    protected void duplicate() {

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
         * Since the analyses shown on this screen are managed by SampleItemAnalysisTreeTab,
         * this screen has no knowledge of the analysis selected by the user
         * to see the history of its results. So an ActionEvent is fired
         * so that SampleItemAnalysisTreeTab can find the desired analysis
         * and show the history.
         */
        //ActionEvent.fire(screen, ResultTabUI.Action.RESULT_HISTORY, null);
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

    protected boolean fetchById(Integer id) {
        return false;
    }

    private boolean canEdit() {
        return (manager != null && !Constants.dictionary().SAMPLE_RELEASED.equals(manager.getSample().getStatusId()));
    }

    private Integer getAccessionNumber() {
        if (manager == null)
            return null;
        return manager.getSample().getAccessionNumber();
    }

    private void setAccessionNumber(Integer accNum) {
        /*
        Integer oldNumber, orderId;
        SampleManager quickEntryMan;

        oldNumber = manager.getSample().getAccessionNumber();
        if (oldNumber != null) {
            if (quickUpdate) {
                Window.alert(consts.get("cantChangeQuickEntryAccessionNumber"));
                accessionNumber.setValue(Util.toString(oldNumber));
                setFocus(accessionNumber);
                return;
            } else if ( !Window.confirm(consts.get("accessionNumberEditConfirm"))) {
                accessionNumber.setValue(Util.toString(oldNumber));
                setFocus(accessionNumber);
                return;
            }
        }

        try {
            manager.getSample().setAccessionNumber(event.getValue());

            if (accessionNumUtil == null)
                accessionNumUtil = new AccessionNumberUtility();

            window.setBusy(consts.get("fetching"));
            quickEntryMan = accessionNumUtil.validateAccessionNumber(manager.getSample());

            if (quickEntryMan == null) {
                window.clearStatus();
                return;
            } else if (manager.getSample().getOrderId() != null) {
                Window.alert(consts.get("cantLoadQEIfOrderNumPresent"));
                quickEntryMan.abortUpdate();
                accessionNumber.setValue(Util.toString(oldNumber));
                setFocus(accessionNumber);
                window.clearStatus();
                return;
            }

            if (state == State.ADD) {
                orderId = manager.getSample().getOrderId();
                if (orderId != null) {
                    SampleMergeUtility.mergeTests(manager, quickEntryMan);
                    manager.setSample(quickEntryMan.getSample());
                    manager.getSample().setOrderId(orderId);
                } else {
                    manager = quickEntryMan;
                }

                manager.getSample().setDomain(SampleManager.ENVIRONMENTAL_DOMAIN_FLAG);
                manager.createEmptyDomainManager();
          
              /* 
               * When a sample is entered through quick entry we don't know what
               * domain the sample belongs to, thus it isn't possible for the flag
               * "isHazardous" to be set at that point because it isn't specified for
               * all samples but only for environmental ones. Also, we add the
               * standard note, if any, defined through a system variable for this
               * domain, because it isn't present in the manager fetched from the
               * back-end.
               
                setDefaults();
                DeferredCommand.addCommand(new Command() {
                    public void execute() {
                        setFocus(null);
                        setDataInTabs();
                        setState(State.UPDATE);
                        DataChangeEvent.fire(screen);
                        window.clearStatus();
                        quickUpdate = true;
                    }
                });
            } else {
                quickEntryMan.abortUpdate();
                window.clearStatus();
                throw new Exception(consts.get("quickEntryNumberExists"));
            }
        } catch (ValidationErrorsList e) {
            showErrors(e);
            accessionNumber.setValue(Util.toString(oldNumber));
            manager.getSample().setAccessionNumber(oldNumber);
            setFocus(accessionNumber);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            accessionNumber.setValue(Util.toString(oldNumber));
            manager.getSample().setAccessionNumber(oldNumber);
            setFocus(accessionNumber);
        }
        window.clearStatus();*/
         
    }

    private Integer getOrderId() {
        if (manager == null)
            return null;
        return manager.getSample().getOrderId();
    }

    private void setOrderId(Integer orderId) {
        // TODO add code for loading the order if necessary
        manager.getSample().setOrderId(orderId);
    }

    private Datetime getCollectionDate() {
        if (manager == null)
            return null;
        return manager.getSample().getCollectionDate();
    }

    private void setCollectionDate(Datetime date) {
        manager.getSample().setCollectionDate(date);
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

    public String getClientReference() {
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

    private String getNextOfKinFirstName() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKin().getFirstName();
    }

    private void setNextOfKinFirstName(String name) {
        manager.getSampleNeonatal().getNextOfKin().setFirstName(name);
    }

    private String getNextOfKinMiddleName() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKin().getMiddleName();
    }

    private void setNextOfKinMiddleName(String name) {
        manager.getSampleNeonatal().getNextOfKin().setLastName(name);
    }

    private Datetime getNextOfKinBirthDate() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKin().getBirthDate();
    }

    private void setNextOfKinBirthDate(Datetime date) {
        manager.getSampleNeonatal().getNextOfKin().setBirthDate(date);
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

    private String getNextOfKinAddressHomePhone() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKin().getAddress().getHomePhone();
    }

    private void setNextOfKinAddressHomePhone(String homePhone) {
        manager.getSampleNeonatal().getNextOfKin().getAddress().setHomePhone(homePhone);
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

    private Integer getNeonatalNextOfKinRelationId() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getNextOfKinRelationId();
    }

    private void setNeonatalNextOfKinRelationId(Integer nextOfKinRelationId) {
        manager.getSampleNeonatal().setNextOfKinRelationId(nextOfKinRelationId);
    }

    private String getNeonatalIsRepeat() {
        if (manager == null)
            return "N";
        return manager.getSampleNeonatal().getIsRepeat();
    }

    private void setNeonatalIsRepeat(String isRepeat) {
        manager.getSampleNeonatal().setIsRepeat(isRepeat);
    }

    private String getNeonatalIsNicu() {
        if (manager == null)
            return "N";
        return manager.getSampleNeonatal().getIsNicu();
    }

    private void setNeonatalIsNicu(String isNicu) {
        manager.getSampleNeonatal().setIsNicu(isNicu);
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
            return "N";
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
    }
    
    private Integer getNeonatalTransfusionAge() {
        return null;
    }

    private String getNeonatalIsCollectionValid() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getIsCollectionValid();
    }

    private void setNeonatalIsCollectionValid(String isCollectionValid) {
        manager.getSampleNeonatal().setIsCollectionValid(isCollectionValid);
    }

    private Integer getNeonatalCollectionAge() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getCollectionAge();
    }

    private void setNeonatalCollectionAge(Integer collectionAge) {
        manager.getSampleNeonatal().setCollectionAge(collectionAge);
    }

    private Integer getNeonatalProviderId() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getProviderId();
    }

    private void setNeonatalProviderId(Integer providerId) {
        manager.getSampleNeonatal().setProviderId(providerId);
    }
    
    public String getNeonatalProviderLastName() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getProviderLastName();
    }

    public void setNeonatalProviderLastName(String providerlastName) {
        manager.getSampleNeonatal().setProviderlastName(providerlastName);
    }

    public String getNeonatalProviderFirstName() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getProviderFirstName();
    }

    public void setNeonatalProviderFirstName(String providerFirstName) {
        manager.getSampleNeonatal().setProviderFirstName(providerFirstName);
    }

    private String getNeonatalFormNumber() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getFormNumber();
    }

    private void setNeonatalFormNumber(String formNumber) {
        manager.getSampleNeonatal().setFormNumber(formNumber);
    }
    
    private void setProviderSelection() {
        /*if (manager == null) {
            providerLastName.setValue(new TableDataRow(null, ""));
            return;
        }

        providerLastName.setValue(new TableDataRow(getNeonatalProviderId(),
                                                       getNeonatalProviderLastName()));*/
    }

    private void getProviderFromSelection() {
        ProviderDO sorg;

        /*row = providerLastName.getValue();
        if (row == null || row.key == null) {
            setNeonatalProviderId(null);
            setProviderFirstName(null);
            setNeonatalProviderLastName(null);
            
            providerLastName.setValue(null, "");
            providerFirstName.setValue("");
            return;
        }

        sorg = (ProviderDO)row.data;
        
        setNeonatalProviderId(sorg.getId());
        setProviderFirstName(sorg.getFirstName());
        setNeonatalProviderLastName(sorg.getLastName());
        
        providerLastName.setValue(getNeonatalProviderId(), getNeonatalProviderLastName());
        providerFirstName.setValue(getNeonatalProviderFirstName());*/
    }
    
    private void setProviderFirstName(String name) {
        manager.getSampleNeonatal().setProviderFirstName(name);
    }
    
    private String getProviderFirstName() {
        if (manager == null)
            return null;
        return manager.getSampleNeonatal().getProviderFirstName();
    }
    
    private void getProviderMatches(GetMatchesEvent event) {
        /*ProviderDO data;
        ArrayList<ProviderDO> list;
        ArrayList<TableDataRow> model;

        window.setBusy();
        try {
            list = ProviderService.get().fetchByLastName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
            model = new ArrayList<TableDataRow>();
            for (int i = 0; i < list.size(); i++ ) {
                row = new TableDataRow(3);
                data = list.get(i);

                row.key = data.getId();
                row.data = data;
                row.cells.get(0).setValue(data.getLastName());
                row.cells.get(1).setValue(data.getFirstName());
                row.cells.get(2).setValue(data.getMiddleName());

                model.add(row);
            }
            //providerLastName.showAutoMatches(model);
        } catch (Throwable e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
        window.clearStatus();*/
    }
    
    private void setProjectSelection() {
        SampleProjectViewDO p;

        p = getFirstPermanentProject();
        
        /*if (p != null)
            projectName.setValue(new TableDataRow(p.getProjectId(), p.getProjectName(),
                                                  p.getProjectDescription()));
        else
            projectName.setValue(new TableDataRow(null, "", ""));*/
       
    }
    
    private void getProjectFromSelection() {
        AutoCompleteValue row;
        SampleProjectViewDO data;

        row = projectName.getValue();
        data = null;
        /*
         * if a project was not selected and if there were permanent projects
         * present then we delete the first permanent project and set the next
         * permanent one as the first project in the list; otherwise we modify
         * the first existing permanent project or create a new one if none
         * existed
         */
        if (row == null || row.getId() == null) {
            data = getFirstPermanentProject();
            if (data != null)
                manager.project.remove(data);
            data = getFirstPermanentProject();
            
            if (data != null) {
                //manager.getProjects().setProjectAt(data, 0);
                //projectName.setValue(data.getProjectId(), data.getProjectName(), data.getProjectDescription());
            } else {
               // projectName.setValue(new TableDataRow(null, "", ""));
            }
        } else {
            data = getFirstPermanentProject();
            if (data == null) {
                data = manager.project.add();
                data.setIsPermanent("Y");
                //manager.getProjects().addProjectAt(data, 0);
            }
            /*data.setProjectId((Integer)row.key);
            data.setProjectName((String)row.cells.get(0).getValue());
            data.setProjectDescription((String)row.cells.get(1).getValue());*/
        }
    }
    
    private SampleProjectViewDO getFirstPermanentProject() {
        ArrayList<SampleProjectViewDO> p;
        
        if (manager == null) 
            return null;
        
        p = manager.project.getByType("Y");
        
        return p != null ? p.get(0): null;
    }
    
    private void getProjectMatches(GetMatchesEvent event) {
        Item<Integer> row;
        ProjectDO data;
        ArrayList<ProjectDO> list;
        ArrayList<Item<Integer>> model;

        window.setBusy();
        try {
            list = ProjectService.get().fetchActiveByName(QueryFieldUtil.parseAutocomplete(event.getMatch()));
            model = new ArrayList<Item<Integer>>();
            for (int i = 0; i < list.size(); i++ ) {
                row = new Item<Integer>(4);
                data = list.get(i);

                row.setKey(data.getId());
                row.setCell(0, data.getName());
                row.setCell(1, data.getDescription());

                model.add(row);
            }
            projectName.showAutoMatches(model);
        } catch (Throwable e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
        window.clearStatus();
    }
    
    private void showProjectLookup() {
        /*try {
            if (projectLookUp == null) {
                projectLookUp = new SampleProjectLookupScreen1();
                projectLookUp.addActionHandler(new ActionHandler<SampleProjectLookupScreen1.Action>() {
                    public void onAction(ActionEvent<SampleProjectLookupScreen1.Action> event) {
                        if (event.getAction() == SampleProjectLookupScreen1.Action.OK) {
                            DataChangeEvent.fire(screen, projectName);
                        }
                    }
                });
            }

            ScreenWindow modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
            modal.setName(Messages.get().sampleProject());
            modal.setContent(projectLookUp);
            //TODO change this code
            //projectLookUp.setState(state);

            projectLookUp.setManager(manager);
        } catch (Exception e) {
            Window.alert(e.getMessage());
            return;
        }*/
    }
    
    private void setReportToSelection() {
        ArrayList<SampleOrganizationViewDO> orgs;
                
        if (manager == null) {
            reportToName.setValue(null, "");
            return;
        }
        
        orgs = manager.organization.getByType(Constants.dictionary().ORG_REPORT_TO);

        if (orgs != null)
            reportToName.setValue(orgs.get(0).getOrganizationId(),
                                  orgs.get(0).getOrganizationName());
        else 
            reportToName.setValue(null, "");        
    }
    
    private void getReportToFromSelection() {
        AutoCompleteValue row;
        ArrayList<SampleOrganizationViewDO> sorgs;
        SampleOrganizationViewDO sorg;
        OrganizationDO org;

        row = reportToName.getValue();
        if (row == null || row.getId() == null) {
            sorgs = manager.organization.getByType(Constants.dictionary().ORG_REPORT_TO);
            if (sorgs != null)
                manager.organization.remove(sorgs.get(0));

            reportToName.setValue(null, "");
            return;
        }

        sorgs = manager.organization.getByType(Constants.dictionary().ORG_REPORT_TO);
        if (sorgs == null) {
            sorg = manager.organization.add();
            sorg.setTypeId(Constants.dictionary().ORG_REPORT_TO);
        } else {
            sorg = sorgs.get(0);
        }

        org = (OrganizationDO)row.getData();
        if (org != null)
            getSampleOrganization(org, sorg);

        reportToName.setValue(sorg.getOrganizationId(), sorg.getOrganizationName());

        try {
            showHoldRefuseWarning(sorg.getOrganizationId(), sorg.getOrganizationName());
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
    }
    
    private void setBirthHospitalSelection() {
        ArrayList<SampleOrganizationViewDO> orgs;
        
        if (manager == null) {
            birthHospitalName.setValue(null, "");
            return;
        }
        
        orgs = manager.organization.getByType(Constants.dictionary().ORG_BIRTH_HOSPITAL);

        if (orgs != null)
            birthHospitalName.setValue(orgs.get(0).getOrganizationId(),
                                  orgs.get(0).getOrganizationName());
        else 
            birthHospitalName.setValue(null, "");
    }
    
    private void getBirthHospitalFromSelection() {
        AutoCompleteValue row;
        ArrayList<SampleOrganizationViewDO> sorgs;
        SampleOrganizationViewDO sorg;
        OrganizationDO org;

        row = birthHospitalName.getValue();
        if (row == null || row.getId() == null) {
            sorgs = manager.organization.getByType(Constants.dictionary().ORG_BIRTH_HOSPITAL);
            if (sorgs != null)
                manager.organization.remove(sorgs.get(0));

            birthHospitalName.setValue(null, "");
            return;
        }

        sorgs = manager.organization.getByType(Constants.dictionary().ORG_BIRTH_HOSPITAL);
        if (sorgs == null) {
            sorg = manager.organization.add();
            sorg.setTypeId(Constants.dictionary().ORG_BIRTH_HOSPITAL);
        } else {
            sorg = sorgs.get(0);
        }

        org = (OrganizationDO)row.getData();
        if (org != null)
            getSampleOrganization(org, sorg);

        birthHospitalName.setValue(sorg.getOrganizationId(), sorg.getOrganizationName());

        try {
            showHoldRefuseWarning(sorg.getOrganizationId(), sorg.getOrganizationName());
        } catch (Exception e) {
            Window.alert(e.getMessage());
        }
    }
    
    private void getOrganizationMatches(String match, AutoComplete widget) {
        Item<Integer> row;
        OrganizationDO data;
        ArrayList<OrganizationDO> list;
        ArrayList<Item<Integer>> model;

        window.setBusy();
        try {
            list = OrganizationService.get().fetchByIdOrName(QueryFieldUtil.parseAutocomplete(match));
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
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
        window.clearStatus();
    }
    
    private void showOrganizationLookup() {
        /*
        try {
            if (organizationLookUp == null) {
                organizationLookUp = new SampleOrganizationLookupScreen1();

                organizationLookUp.addActionHandler(new ActionHandler<SampleOrganizationLookupScreen1.Action>() {
                    public void onAction(ActionEvent<SampleOrganizationLookupScreen1.Action> event) {
                        if (event.getAction() == SampleOrganizationLookupScreen1.Action.OK) {
                            DataChangeEvent.fire(screen, reportToName);
                            DataChangeEvent.fire(screen, birthHospitalName);
                        }
                    }
                });
            }

            modal = new ScreenWindow(ScreenWindow.Mode.DIALOG);
            modal.setName(Messages.get().sampleOrganization());
            modal.setContent(organizationLookUp);

            //TODO change this code
            //organizationLookUp.setScreenState(state);
            organizationLookUp.setManager(manager);

        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
            return;
        }*/
    }
    
    private void getSampleOrganization(OrganizationDO org, SampleOrganizationViewDO data) {
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
    
    private void showHoldRefuseWarning(Integer orgId, String name) throws Exception {
        if (SampleOrganizationUtility1.isHoldRefuseSampleForOrg(orgId)) 
            Window.alert(Messages.get().orgMarkedAsHoldRefuseSample(name));
    }
}